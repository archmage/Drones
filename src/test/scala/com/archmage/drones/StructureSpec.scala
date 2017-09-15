package com.archmage.drones

import com.archmage.drones.Drone.{DroneState, Gather}
import com.archmage.drones.components.{Geo, State}
import org.scalatest.FlatSpec

class StructureSpec extends FlatSpec {

  "A structure with scrap that gets gathered" should "decrease its scrap by 1 each time a gather occurs" in {
    val gatherCount = 3
    val startingScrap = 10
    val structure = Structure(Geo(0, 0), startingScrap)
    var world = World(Seq(Drone(Geo(), State[DroneState](Gather()))), Seq(structure))
    for(_ <- 1 to gatherCount) world = world.process()
    assert(startingScrap - gatherCount == structure.scrap)
  }
}
