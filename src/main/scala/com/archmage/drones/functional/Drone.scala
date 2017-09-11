package com.archmage.drones.functional

import com.archmage.drones.functional.Drone.{Idle, Move, State}
import com.archmage.drones.functional.components.Geo

object Drone {
  sealed trait State
  case class Idle() extends State
  case class Move(x: Int, y: Int) extends State
}

case class Drone(geo: Geo, state: State) {

  def act: Drone = {
    state match {
      case Idle() => {
        println("idle")
        this
      }
      case Move(x, y) => {
        println(s"moving to $x, $y")
        move
      }
    }
  }

  def move: Drone = {
    Drone(Geo(geo.xpos + geo.xvel, geo.ypos + geo.yvel, geo.xvel, geo.yvel), state)
  }
}

