package com.archmage.drones

import com.archmage.drones.components.Geo

final case class World(drones: Seq[Drone] = Seq(),
                       structures: Seq[Structure] = Seq(),
                       clock: Int = 0) {

  def process(input: String = ""): World = {
    // process input

    // drone processing
    val newDrones = drones.map((d) => d.act(this))

    // gather
    val postGatherWorld = World(newDrones, structures, clock).gather


    postGatherWorld.incrementClock
  }

  // this will break HARD if two structures are in the same place, so...
  // don't do that, I guess!
  def gather: World = {
    // get all drones that are gathering
    val gatherDrones = drones.filter((d) => d.state.state == Drone.Gather())
    // get the coordinates of each location they are gathering from
    val locations = gatherDrones.map((d) => Geo(d.geo.xpos, d.geo.ypos)).distinct
    // get all relevant structures
    val gatherStructures = structures.filter((s) => locations.contains(s.geo))
    // pair each set of drones and each structure in tuples
    val gatherGroups = gatherStructures.map((s) => (gatherDrones.filter((d) => d.geo == s.geo), s))
    // for each pair, gather appropriately!
    val processedGroups = gatherGroups.map((g) => {
      val remainder = g._2.scrap - g._1.length
      // every drone gets to gather if there's enough scrap
      if(remainder >= 0) {
        (g._1.map((d) => d.gather(this, remainder == 0)), Structure(g._2.geo, remainder))
      }
      // otherwise, only a suitable number of drones get to gather!
      else {
        // this is the number of lucky drones
        val luckyDroneCount = Integer.max(0, g._1.length + remainder)
        val luckyDrones = g._1.slice(0, luckyDroneCount)

        (luckyDrones.map((d) => d.gather(this, true)) ++ g._1.slice(luckyDroneCount, g._1.length),
          Structure(g._2.geo, 0))
      }
    })

    // finally, rejoin the gathering and non-gathering drones
    val processedDrones = processedGroups.flatMap((g) => g._1)
    val processedStructures = processedGroups.map((g) => g._2)

    val allDrones = processedDrones ++ drones.filterNot((d) => d.state.state == Drone.Gather())
    val allStructures = processedStructures ++ structures.filterNot((s) => locations.contains(s.geo))

    // and return a new world!
    World(allDrones, allStructures, clock)
  }

  def incrementClock: World = World(drones, structures, clock + 1)
}
