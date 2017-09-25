package com.archmage.drones

import com.archmage.drones.components.Geo

final case class World(drones: Seq[Drone] = Seq(),
                       structures: Seq[Structure] = Seq(),
                       clock: Int = 0) {

  def process(input: String = ""): World = {
    // process input

    // drone processing
    val newDrones = drones.map((d) => d.act(this))

    World(newDrones, structures, clock).gather.explodeDrones.incrementClock
  }

  // drones explode
  def explodeDrones: World = {
    // separate drones into two groups!
    val (explodingDrones, otherDrones) = drones.partition((d) => d.isAboutToExplode(this))

    if(explodingDrones.isEmpty) return this

    // get the coordinates of each exploding location
    val locations = explodingDrones.map((d) => d.geo.novel).distinct
    // get relevant / irrelevant structures
    val (nearbyStructures, otherStructures) = structures.partition((s) => locations.contains(s.geo))

    // group drones / structures by location
    val explodeGroups = locations.map((l) => {
      (explodingDrones.filter((d) => d.geo.novel == l),
        nearbyStructures.find((s) => s.geo == l).getOrElse(Structure(l)))
    })

    // explode!
    val resultingStructures = explodeGroups.map((g) => {
      // calculate total of scrap held by exploding drones
      val scrapSum = g._1.foldLeft(0) { (z, i) => z + i.scrap }
      Structure(g._2.geo, g._2.scrap + scrapSum + g._1.length * Drone.explosionRemainder)
    })

    // and the resulting world...
    World(otherDrones, resultingStructures ++ otherStructures, clock)
  }

  // this will break HARD if two structures are in the same place, so...
  // don't do that, I guess!
  def gather: World = {
    // partition gathering / nongathering drones
    val (gatherDrones, otherDrones) = drones.partition((d) => d.state.state == Drone.Gather())

    if(gatherDrones.isEmpty) return this

    // get the coordinates of each gathering location
    val locations = gatherDrones.map((d) => Geo(d.geo.xpos, d.geo.ypos)).distinct
    // get all gathered / nongathered structures
    val (gatherStructures, otherStructures) = structures.partition((s) => locations.contains(s.geo))
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
          Structure(g._2.geo))
      }
    })

    // finally, rejoin the gathering and non-gathering drones
    val processedDrones = processedGroups.flatMap((g) => g._1)
    val processedStructures = processedGroups.map((g) => g._2)

    val allDrones = processedDrones ++ otherDrones
    val allStructures = processedStructures ++ otherStructures

    // and return a new world!
    World(allDrones, allStructures, clock)
  }

  def incrementClock: World = World(drones, structures, clock + 1)
}
