package com.archmage.drones

import com.archmage.drones.Drone.{DroneState, Gather, Idle, Move}
import com.archmage.drones.components.{Geo, State}
import org.scalatest.FlatSpec

class DroneSpec extends FlatSpec {

  "An idle drone" should "stay at its location after idling for a turn" in {
    val world = World(Seq(Drone())).process()
    assert(world.drones.head.geo == Geo())
  }

  "A drone with a move target" should "move to that target at a rate of up to 1 horizontal" +
    " and one vertical space per turn" in {
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
    val drone1 = world.drones(0)
    val drone2 = world.drones(1)
    assert((drone1.scrap, drone2.scrap) == (startingScrap / 2 + 1, startingScrap / 2))
  }

  "A drone that fails to gather" should "become idle" in {
    val drone = Drone(Geo(), State[DroneState](Gather()))
    var world = World(Seq(drone)) // drone will fail to gather due to no structure present
    world = world.process()
    assert(world.drones.head.state.state == Idle())
  }
}
