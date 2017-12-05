package com.archmage.drones

import org.scalatest.FlatSpec

class WorldSpec extends FlatSpec {

  "A world that processes once" should "increment its clock once" in {
    val world = World()
    val processedWorld = world.process()
    assert(processedWorld.clock - world.clock == 1)
  }

  "Adding two drones to the world" should "add those drones to the world" in {
    val world = World()
    val worldWithDrones = world.addDrones(Drone(), Drone())
    assert(worldWithDrones.drones.size == 2)
  }

  "Trying to add an empty array of drones to the world" should "not modify the world's drones" in {
    val world = World()
    val unmodifiedWorld = world.addDrones(Seq(): _*)
    assert(world.drones.isEmpty)
  }

  "Adding a structure to the world" should "add that structure to the world" in {
    val world = World()
    val worldWithStructure = world.addStructures(Structure())
    assert(worldWithStructure.structures.size == 1)
  }
}
