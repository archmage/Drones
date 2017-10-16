package com.archmage.drones

import com.archmage.drones.Drone._
import com.archmage.drones.components.{Geo, State}
import org.scalatest.FlatSpec

import scala.collection.immutable.Queue

class DroneSpec extends FlatSpec {

  "An idle drone" should "stay at its location after idling for a turn" in {
    val world = World(Seq(Drone())).process()
    assert(world.drones.head.geo == Geo())
  }

  "A idle drone that has move() called on it" should "do nothing" in {
    val world = World(Seq(Drone()))
    val droneAfterMove = world.drones.head.move(world)
    assert(world.drones.head == droneAfterMove)
  }

  "A drone with a move target" should "move to that target at a rate of up to 1 per axis per turn" in {
    val target = Geo(10, 10)
    val drone = Drone(Geo(), State[DroneState](Move(target.xpos, target.ypos)))
    var world = World(Seq(drone))
    for(_ <- 1 to Integer.max(target.xpos, target.ypos)) world = world.process()
    val geo = world.drones.head.geo
    assert(Geo(geo.xpos, geo.ypos) == target)
  }

  "A drone with a move target" should "not move further than its move target" in {
    val target = Geo(10, 10)
    val drone = Drone(Geo(), State[DroneState](Move(target.xpos, target.ypos)))
    var world = World(Seq(drone))
    for(_ <- 1 to Integer.max(target.xpos, target.ypos)) world = world.process()
    for(_ <- 1 to 5) world = world.process()
    val geo = world.drones.head.geo
    assert(Geo(geo.xpos, geo.ypos) == target)
  }

  // TODO write pathfinding test

  "A drone that is gathering" should "accumulate scrap at a rate of 1 scrap per turn" in {
    val scrapTarget = 10
    val drone = Drone(Geo(), State[DroneState](Gather()))
    val structure = Structure(Geo(), scrapTarget)
    var world = World(Seq(drone), Seq(structure))
    for(_ <- 1 to scrapTarget) world = world.process()
    assert(scrapTarget == world.drones.head.scrap)
  }

  "Two drones that are gathering the same structure" should "salvage the correct amount of scrap" in {
    val startingScrap = 7
    val drone = Drone(Geo(), State[DroneState](Gather()))
    val structure = Structure(Geo(), startingScrap)
    var world = World(Seq(drone, drone), Seq(structure))
    for(_ <- 0 to startingScrap / 2) world = world.process()
    val drone1 = world.drones.head
    val drone2 = world.drones(1)
    assert((drone1.scrap, drone2.scrap) == (startingScrap / 2 + 1, startingScrap / 2))
  }

  "A large number of drones gathering the same structure" should "salvage the correct amount of scrap" in {
    val startingScrap = 7
    val drones = List.fill(20)(Drone(Geo(), State[DroneState](Gather())))
    val structure = Structure(Geo(), startingScrap)
    var world = World(drones, Seq(structure))
    world = world.process()
    val dronesWithScrap = world.drones.count((d) => d.scrap > 0)
    val dronesWithoutScrap = world.drones.count((d) => d.scrap <= 0)
    assert((dronesWithScrap, dronesWithoutScrap) == (startingScrap, drones.length - startingScrap))
  }

  "A drone that fails to gather" should "become idle" in {
    val drone = Drone(Geo(), State[DroneState](Gather()))
    var world = World(Seq(drone)) // drone will fail to gather due to no structure present
    world = world.process()
    assert(world.drones.head.state.state == Idle())
  }

  "An imminently self-destructing drone" should "properly report its imminint destruction" in {
    val drone = Drone(Geo(), State[DroneState](SelfDestruct()))
    var world = World(Seq(drone))
    for(_ <- 1 to Drone.explosionTime) world = world.process()
    assert(world.drones.nonEmpty && world.drones.head.isAboutToExplode(world))
  }

  "A self-destructed drone" should "be removed from the world's drone list" in {
    val drone = Drone(Geo(), State[DroneState](SelfDestruct()))
    var world = World(Seq(drone))
    for(_ <- 1 to Drone.explosionTime + 1) world = world.process()
    assert(world.drones.isEmpty)
  }

  "A self-destructing drone" should "increase its local structure's scrap count" in {
    val drone = Drone(Geo(), State[DroneState](SelfDestruct()))
    val structure = Structure(Geo())
    var world = World(Seq(drone), Seq(structure))
    for(_ <- 1 to Drone.explosionTime + 1) world = world.process()
    assert(world.structures.head.scrap == Drone.explosionRemainder)
  }

  "A self-destructed drone without a structure" should "create a structure with scrap at its location" in {
    val drone = Drone(Geo(), State[DroneState](SelfDestruct()))
    var world = World(Seq(drone))
    for(_ <- 1 to Drone.explosionTime + 1) world = world.process()
    assert(world.structures.head == Structure(drone.geo.novel, Drone.explosionRemainder))
  }

  "A self-destructing drone carrying scrap" should "add its scrap to its structure" in {
    val drone = Drone(Geo(), State[DroneState](SelfDestruct()), Queue(), 20)
    val structure = Structure(Geo(), 10)
    var world = World(Seq(drone), Seq(structure))
    for(_ <- 1 to Drone.explosionTime + 1) world = world.process()
    assert(world.structures.head.scrap == drone.scrap + structure.scrap + Drone.explosionRemainder)
  }

  "Many self-destructing drones" should "each contribute to a structure's scrap" in {
    val drones = List.fill(20)(Drone(Geo(), State[DroneState](SelfDestruct())))
    val structure = Structure(Geo())
    var world = World(drones, Seq(structure))
    for(_ <- 1 to Drone.explosionTime + 1) world = world.process()
    assert(world.structures.head.scrap == Drone.explosionRemainder * drones.length)
  }

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
    val world = World(Seq(drone)).process()
    assert(world.drones.head.state.state == stateToEnqueue)
  }
}
