package com.archmage.drones

import com.archmage.drones.Drone.{DroneState, Gather, Move}
import com.archmage.drones.components.{Geo, State}
import org.scalatest.FlatSpec

class DroneSpec extends FlatSpec {

  "An idle drone at (0, 0)" should "stay at (0, 0) after idling for a turn" in {
    assert(
      World(Seq(Drone()))
        .process()
        .drones.head.geo == Geo())
  }

  "A drone with a move target" should "move to that target at a rate of up to 1 horizontal" +
    " and one vertical space per turn" in {
    val target = Geo(10, 10, 0, 0)
    val drone = Drone(Geo(), State[DroneState](Move(target.xpos, target.ypos)))
    var world = World(Seq(drone))
    for(_ <- 1 to Integer.max(target.xpos, target.ypos)) world = world.process()
    val geo = world.drones.head.geo
    assert(geo.xpos == target.xpos && geo.ypos == target.ypos)
  }

  "A drone with a move target" should "not move further than its move target" in {
    val target = Geo(10, 10, 0, 0)
    val drone = Drone(Geo(), State[DroneState](Move(target.xpos, target.ypos)))
    var world = World(Seq(drone))
    for(_ <- 1 to Integer.max(target.xpos, target.ypos)) world = world.process()
    for(_ <- 1 to 5) world = world.process()
    val geo = world.drones.head.geo
    assert(geo.xpos == target.xpos && geo.ypos == target.ypos)
  }

  "A drone that is gathering" should "accumulate scrap at a rate of 1 scrap per turn" in {
    val scrapTarget = 10
    val drone = Drone(Geo(), State[DroneState](Gather()))
    var world = World(Seq(drone))
    for(_ <- 1 to scrapTarget) world = world.process()
    assert(world.drones.head.scrap == scrapTarget)
  }
}
