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

final case class Drone(geo: Geo = Geo(),
                 state: State[DroneState] = State[DroneState](Idle(), 0),
                 scrap: Int = 0) {

  def act(world: World): Drone = {
    state.state match {
      case Idle() => idle()
      case Move(x, y) => move(x, y, world)
      case Gather() => validateGather(world)
    }
  }

  def idle(): Drone = this

  def move(x: Int, y: Int, world: World): Drone = {
    state.state match {
      case Move(x, y) => {
        val dxvel = -Integer.signum(geo.xpos - x)
        val dyvel = -Integer.signum(geo.ypos - y)
        val dxpos = geo.xpos + dxvel
        val dypos = geo.ypos + dyvel
        val stop = dxpos == x && dypos == y
        Drone(Geo(dxpos, dypos, dxvel, dyvel), if(stop) Drone.newState(Idle(), world) else state, scrap)
      }
      case _ => this
    }
  }

  def validateGather(world: World): Drone = {
    val structures = world.structures.filter(s => s.geo == Geo(geo.xpos, geo.ypos))
    if(structures.isEmpty || structures.head.scrap <= 0) {
      Drone(geo, Drone.newState(Idle(), world), scrap)
    }
    else this
  }

  // used by World.gather
  def gather(world: World, done: Boolean): Drone = {
    Drone(geo, if(done) Drone.newState(Idle(), world) else state, scrap + 1)
  }
}

