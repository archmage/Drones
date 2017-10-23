package com.archmage.drones

import org.scalatest.FlatSpec

class WorldSpec extends FlatSpec {

  "A world that processes once" should "increment its clock once" in {
    val world = World()
    val processedWorld = world.process()
    assert(processedWorld.clock - world.clock == 1)
  }
}
