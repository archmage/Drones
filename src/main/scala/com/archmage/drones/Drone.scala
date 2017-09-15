package com.archmage.drones

import com.archmage.drones.Drone.{DroneState, Gather, Idle, Move}
import com.archmage.drones.components.{Geo, State}

object Drone {
  sealed trait DroneState
  case class Idle() extends DroneState
  case class Move(x: Int, y: Int) extends DroneState
  case class Gather() extends DroneState

  def newState(state: DroneState, world: World): State[DroneState] = {
    State[state.type](state, world.clock)
  }
}

case class Drone(geo: Geo = Geo(),
                 state: State[DroneState] = State[DroneState](Idle(), 0),
                 scrap: Int = 0) {

  def act(world: World): Drone = {
    state.state match {
      case Idle() => idle(world)
      case Move(x, y) => move(x, y)
      case Gather() => gather
    }
  }

  def idle(world: World): Drone = Drone(geo, Drone.newState(Gather(), world), scrap)

  def move(x: Int, y: Int): Drone = {
    Drone(Geo(geo.xpos + geo.xvel, geo.ypos + geo.yvel, geo.xvel, geo.yvel), state, scrap)
  }

  def gather: Drone = Drone(geo, state, scrap + 1)
}

