package com.archmage.drones.functional

object Main extends App {

  def main(state: WorldState): WorldState = {
    // get user input
//    val input = StdIn.readLine("> ")
    val input = ""

    // process the next frame
    val newState = state.process(input)
    // render
    newState.drones.foreach((d) => println(s"a drone moved to ${(d.geo.xpos, d.geo.ypos)}"))
    // wait a unit of time (optional)
    Thread.sleep(1000)

    main(newState)
  }

  main(WorldState.make)
}
