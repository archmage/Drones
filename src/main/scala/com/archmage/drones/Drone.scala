package com.archmage.drones

import com.archmage.drones.Drone._
import com.archmage.drones.components.{Geo, State}

import scala.collection.immutable.Queue

final case class Drone(geo: Geo = Geo(),
                 state: State[DroneState] = State[DroneState](Idle(), 0),
                 queue: Queue[DroneState] = Queue(),
                 scrap: Int = 0) {

  def act(world: World): Drone = {
    // check queue if idle
    if(state.state == Idle()) {
      if(queue.nonEmpty) {
        val nextAction = queue.dequeue
        // act on the new action!
        Drone(geo, newState(nextAction._1, world), nextAction._2).act(world)
      }
      else this
    }

    else state.state match {
      case Idle() => this
      case Move(_, _) => move(world)
      case Gather() => validateGather(world)
      case SelfDestruct() => this
    }
  }

  def enqueue(state: DroneState): Drone = {
    if(state == Idle()) this
    else Drone(geo, this.state, queue.enqueue(state), scrap)
  }

  def move(world: World): Drone = {
    state.state match {
      case Move(x, y) =>
        val dxvel = -Integer.signum(geo.xpos - x)
        val dyvel = -Integer.signum(geo.ypos - y)
        val dxpos = geo.xpos + dxvel
        val dypos = geo.ypos + dyvel
        val stop = dxpos == x && dypos == y
        Drone(Geo(dxpos, dypos, dxvel, dyvel), if(stop) Drone.newState(Idle(), world) else state, queue, scrap)
      case _ => this
    }
  }

  def validateGather(world: World): Drone = {
    val structures = world.structures.filter(s => s.geo == Geo(geo.xpos, geo.ypos))
    if(structures.isEmpty || structures.head.scrap <= 0) {
      Drone(geo, Drone.newState(Idle(), world), queue, scrap)
    }
    else this
  }

  // used by World.gather
  def gather(world: World, done: Boolean): Drone = {
    Drone(geo, if(done) Drone.newState(Idle(), world) else state, queue, scrap + 1)
  }

  def isAboutToExplode(world: World): Boolean = {
    state.state == SelfDestruct() && state.timestamp + Drone.explosionTime <= world.clock
  }
}

object Drone {
  val cost: Int = 50
  val explosionRemainder: Int = cost / 2
  val explosionTime: Int = 3

  sealed trait DroneState
  case class Idle() extends DroneState
  case class Move(x: Int, y: Int) extends DroneState
  case class Gather() extends DroneState
  case class SelfDestruct() extends DroneState

  def newState(state: DroneState, world: World): State[DroneState] = {
    State[state.type](state, world.clock)
  }
}
