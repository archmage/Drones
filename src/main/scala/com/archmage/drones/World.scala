package com.archmage.drones

import com.archmage.drones.Drone.{DroneState, Move}
import com.archmage.drones.components.{Geo, State}

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
