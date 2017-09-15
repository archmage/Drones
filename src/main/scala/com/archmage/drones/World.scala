package com.archmage.drones

import com.archmage.drones.Drone.{DroneState, Move}
import com.archmage.drones.components.{Geo, State}

final case class World(drones: Seq[Drone], structures: Seq[Structure] = Seq(), clock: Int = 0) {

  def process(input: String = ""): World = {
//    println(input)

    // drone movement
    val newDrones = drones.map((d) => d.act(this))
    World(newDrones, structures, clock + 1)
  }
}

object World {
  def make: World = {
    val drone1 = Drone(Geo(0, 0, 1, 0), State[DroneState](Move(600, 0), 0), 0)
    World(Seq(drone1), Seq(Structure(Geo(10, 10))))
  }
}
