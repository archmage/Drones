package com.archmage.drones

import com.archmage.drones.Drone._
import com.archmage.drones.Structure.Scrapheap
import com.archmage.drones.components.{Geo, State}
import org.scalatest.FlatSpec

import scala.collection.immutable.Queue

class DroneSpec extends FlatSpec {

  // -- move --

  "An idle drone" should "stay at its location after idling for a turn" in {
    val drone = Drone()
    val world = World(Map(1 -> drone))
    val processedWorld = world.process()
    assert(processedWorld.drones(1).geo == drone.geo)
  }

  "A drone with a move target" should "move to that target at a rate of up to 1 per axis per turn" in {
    val target = Geo(10, 10)
    val drone = Drone(Geo(), State[DroneState](Move(target.xpos, target.ypos)))
    var world = World(Map(1 -> drone))
    for(_ <- 1 to Integer.max(target.xpos, target.ypos)) world = world.process()
    val geo = world.drones(1).geo
    assert(Geo(geo.xpos, geo.ypos) == target)
  }

  "A drone with a move target" should "not move further than its move target" in {
    val target = Geo(10, 10)
    val drone = Drone(Geo(), State[DroneState](Move(target.xpos, target.ypos)))
    var world = World(Map(1 -> drone))
    for(_ <- 1 to Integer.max(target.xpos, target.ypos)) world = world.process()
    for(_ <- 1 to 5) world = world.process()
    val geo = world.drones(1).geo
    assert(Geo(geo.xpos, geo.ypos) == target)
  }

  // TODO write pathfinding test

  // -- gather --

  "A drone that is gathering" should "accumulate scrap at a rate of 1 scrap per turn" in {
    val scrapTarget = 10
    val drone = Drone(Geo(), State[DroneState](Gather()))
    val structure = Structure(Geo(), Scrapheap(), scrapTarget)
    var world = World(Map(1 -> drone), Set(structure))
    for(_ <- 1 to scrapTarget) world = world.process()
    assert(scrapTarget == world.drones(1).scrap)
  }

  "Two drones that are gathering the same structure" should "salvage the correct amount of scrap" in {
    val startingScrap = 7
    val drone = Drone(Geo(), State[DroneState](Gather()))
    val structure = Structure(Geo(), Scrapheap(), startingScrap)
    var world = World(Map(1 -> drone, 2 -> drone), Set(structure))
    for(_ <- 0 to startingScrap / 2) world = world.process()
    val drone1 = world.drones(1)
    val drone2 = world.drones(2)
    assert((drone1.scrap, drone2.scrap) == (startingScrap / 2 + 1, startingScrap / 2))
  }

  "A large number of drones gathering the same structure" should "salvage the correct amount of scrap" in {
    val startingScrap = 7
    val drones = List.fill(20)(Drone(Geo(), State[DroneState](Gather())))
    val structure = Structure(Geo(), Scrapheap(), startingScrap)
    var world = World(drones.zipWithIndex.map(g => (g._2, g._1)).toMap, Set(structure))
    world = world.process()
    val dronesWithScrap = world.drones.values.count((d) => d.scrap > 0)
    val dronesWithoutScrap = world.drones.values.count((d) => d.scrap <= 0)
    assert((dronesWithScrap, dronesWithoutScrap) == (startingScrap, drones.length - startingScrap))
  }

  "A drone that fails to gather" should "become idle" in {
    val drone = Drone(Geo(), State[DroneState](Gather()))
    var world = World(Map(1 -> drone)) // drone will fail to gather due to no structure present
    world = world.process()
    assert(world.drones(1).state.state == Idle())
  }

  // -- deposit --

  "A drone with scrap that tries to deposit" should "deposit one scrap per turn" in {
    val startingScrap = 5
    val drone = Drone(Geo(), State[DroneState](Deposit()), Queue(), startingScrap)
    val world = World(Map(1 -> drone), Set(Structure()))
    val processedWorld = world.process()
    assert(startingScrap - processedWorld.drones(1).scrap == 1)
  }

  "A drone without scrap that tries to deposit" should "not deposit anything" in {
    val drone = Drone(Geo(), State[DroneState](Deposit()), Queue())
    val world = World(Map(1 -> drone), Set(Structure()))
    val processedWorld = world.process()
    assert(processedWorld.drones(1).scrap == 0)
  }

  // -- self-destruct --

  "An imminently self-destructing drone" should "properly report its imminint destruction" in {
    val drone = Drone(Geo(), State[DroneState](SelfDestruct()))
    var world = World(Map(1 -> drone))
    for(_ <- 1 to Drone.explosionTime) world = world.process()
    assert(world.drones.nonEmpty && world.drones(1).isAboutToExplode(world))
  }

  "A self-destructed drone" should "be removed from the world's drone list" in {
    val drone = Drone(Geo(), State[DroneState](SelfDestruct()))
    var world = World(Map(1 -> drone))
    for(_ <- 1 to Drone.explosionTime + 1) world = world.process()
    assert(world.drones.isEmpty)
  }

  "A self-destructing drone" should "increase its local structure's scrap count" in {
    val drone = Drone(Geo(), State[DroneState](SelfDestruct()))
    val structure = Structure(Geo())
    var world = World(Map(1 -> drone), Set(structure))
    for(_ <- 1 to Drone.explosionTime + 1) world = world.process()
    assert(world.structures.head.scrap == Drone.explosionRemainder)
  }

  "A self-destructed drone without a structure" should "create a structure with scrap at its location" in {
    val drone = Drone(Geo(), State[DroneState](SelfDestruct()))
    var world = World(Map(1 -> drone))
    for(_ <- 1 to Drone.explosionTime + 1) world = world.process()
    assert(world.structures.head == Structure(drone.geo.novel, Scrapheap(), Drone.explosionRemainder))
  }

  "A self-destructing drone carrying scrap" should "add its scrap to its structure" in {
    val drone = Drone(Geo(), State[DroneState](SelfDestruct()), Queue(), 20)
    val structure = Structure(Geo(), Scrapheap(), 10)
    var world = World(Map(1 -> drone), Set(structure))
    for(_ <- 1 to Drone.explosionTime + 1) world = world.process()
    assert(world.structures.head.scrap == drone.scrap + structure.scrap + Drone.explosionRemainder)
  }

  "Many self-destructing drones" should "each contribute to a structure's scrap" in {
    val drones = List.fill(20)(Drone(Geo(), State[DroneState](SelfDestruct())))
    val structure = Structure(Geo())
    var world = World(drones.zipWithIndex.map(g => (g._2, g._1)).toMap, Set(structure))
    for(_ <- 1 to Drone.explosionTime + 1) world = world.process()
    assert(world.structures.head.scrap == Drone.explosionRemainder * drones.length)
  }

  // -- queue --

  "A drone that enqueues a non-idle action" should "add that action to its action queue" in {
    val drone = Drone()
    val droneAfterEnqueue = drone.enqueue(Move(10, 10))
    assert(droneAfterEnqueue.queue.length - drone.queue.length == 1)
  }

  "A drone that enqueues an idle action" should "not add that action to its queue" in {
    val drone = Drone()
    val droneAfterEnqueue = drone.enqueue(Idle())
    assert(droneAfterEnqueue.queue.length - drone.queue.length == 0)
  }

  "An idle drone that enqueues a non-idle action" should "perform that action" in {
    val stateToEnqueue = Move(10, 10)
    val drone = Drone(Geo(), State(Idle()), Queue(stateToEnqueue))
    val world = World(Map(1 -> drone)).process()
    assert(world.drones(1).state.state == stateToEnqueue)
  }
}
