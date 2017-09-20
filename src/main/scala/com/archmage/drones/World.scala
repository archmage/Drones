package com.archmage.drones

final case class World(drones: Seq[Drone] = Seq(), structures: Seq[Structure] = Seq(), clock: Int = 0) {

  def process(input: String = ""): World = {
    // process input

    // drone processing
    val newDrones = drones.map((d) => d.act(this))

    // structure processing
    val newStructures = structures.map((s) => s.act(this))

    World(newDrones, newStructures, clock + 1)
  }
}
