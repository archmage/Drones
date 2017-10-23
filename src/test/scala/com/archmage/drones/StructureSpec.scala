package com.archmage.drones

import com.archmage.drones.Drone.{Deposit, DroneState, Gather}
import com.archmage.drones.components.{Geo, State}
import org.scalatest.FlatSpec

import scala.collection.immutable.Queue

class StructureSpec extends FlatSpec {

  // -- gather --

  "A structure with scrap that gets gathered" should "decrease its scrap by 1 each time a gather occurs" in {
    val gatherCount = 3
    val startingScrap = 10
    val structure = Structure(Geo(0, 0), startingScrap)
    var world = World(Seq(Drone(Geo(), State[DroneState](Gather()))), Seq(structure))
    for(_ <- 1 to gatherCount) world = world.process()
    assert(world.structures.head.scrap == startingScrap - gatherCount)
  }

  "A scrapless structure being gathered" should "not drop below zero scrap" in {
    var world = World(Seq(Drone(Geo(), State[DroneState](Gather()))), Seq(Structure()))
    world = world.process()
    assert(world.structures.head.scrap == 0)
  }

  // -- deposit --

  "A structure that has scrap deposited in it" should "increase its scrap count accordingly" in {
    val startingScrap = 5
    val world = World(Seq(Drone(Geo(), State[DroneState](Deposit()), Queue(), startingScrap)),
      Seq(Structure()))
    val processedWorld = world.process()
    assert(processedWorld.structures.head.scrap - world.structures.head.scrap == 1)
  }
}
