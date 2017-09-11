package com.archmage.drones.functional

import com.archmage.drones.functional.Drone.{Idle, Move}
import com.archmage.drones.functional.components.Geo

case class WorldState(drones: Seq[Drone], clock: Int = 0) {

  def process(input: String): WorldState = {
//    println(input)

    // drone movement
    val newDrones = drones.map((d) => d.act)
    WorldState(newDrones, clock + 1)
  }
}

object WorldState {
  def make: WorldState = {
    val drone1 = Drone(Geo(0, 0, 1, 0), Move(600, 0))
    WorldState(Seq(drone1))
  }
}
