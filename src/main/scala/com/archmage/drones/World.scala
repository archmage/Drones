package com.archmage.drones

final case class World(drones: Seq[Drone] = Seq(),
                       structures: Seq[Structure] = Seq(),
                       clock: Int = 0) {

  def process(input: String = ""): World = {
    // process input
    // on "stop", stop all drones


    // drone processing
    val newDrones = drones.map((d) => d.act(this))

    World(newDrones, structures, clock).deposit.gather.explodeDrones.incrementClock
  }

  def incrementClock: World = World(drones, structures, clock + 1)

  // drones explode
  def explodeDrones: World = {
    val partitionCriteria: (Drone) => Boolean = (d) => d.isAboutToExplode(this)
    val process: ((Seq[Drone], Structure)) => ((Seq[Drone], Structure)) = (g) => {
      // calculate total of scrap held by exploding drones
      val scrapSum = g._1.foldLeft(0) { (z, i) => z + i.scrap }
      (Seq(), Structure(g._2.geo, g._2.scrap + scrapSum + g._1.length * Drone.explosionRemainder))
    }
    val structureIfNone = Structure()

    dronesInteractWithStructures(partitionCriteria, process, structureIfNone)
  }

  // this will break HARD if two structures are in the same place, so...
  // don't do that, I guess!
  def gather: World = {
    val partitionCriteria: (Drone) => Boolean = (d) => d.state.state == Drone.Gather()
    val process: ((Seq[Drone], Structure)) => ((Seq[Drone], Structure)) = (g) => {
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
    }

    dronesInteractWithStructures(partitionCriteria, process)
  }

  def deposit: World = {
    val partitionCriteria: (Drone) => Boolean = (d) => d.state.state == Drone.Deposit()
    val process: ((Seq[Drone], Structure)) => ((Seq[Drone], Structure)) = (g) => {
      val processedDrones = g._1.map((d) => Drone(d.geo, d.state, d.queue, d.scrap - 1))
      (processedDrones, Structure(g._2.geo, g._2.scrap + processedDrones.length))
    }

    dronesInteractWithStructures(partitionCriteria, process)
  }

  /** helper function to assist with drone : structure operations */
  private def dronesInteractWithStructures(partitionCriteria: (Drone) => Boolean,
    process: ((Seq[Drone], Structure)) => ((Seq[Drone], Structure)),
    structureIfNone: Structure = null): World = {
    // split drones based on partition criteria
    val (selectedDrones, otherDrones) = drones.partition(partitionCriteria)

    // if no selected drones, return this
    if(selectedDrones.isEmpty) return this

    // get the coordinates of each gathering location
    val locations = selectedDrones.map((d) => d.geo.novel).distinct
    // get all selected / unselected structures
    val (selectedStructures, otherStructures) = structures.partition((s) => locations.contains(s.geo))
    // pair each set of drones and each structure in tuples

    // if acting on no structure is okay...
    val selectedGroups = if(structureIfNone != null) {
      // map off location and provide structureIfNone if none
      locations.map((l) => {
        (selectedDrones.filter((d) => d.geo.novel == l),
          selectedStructures.find((s) => s.geo == l).getOrElse(structureIfNone.withLocation(l)))
      })
    }
    else {
      // otherwise map off selected structures!
      selectedStructures.map((s) => (selectedDrones.filter((d) => d.geo.novel == s.geo), s))
    }

    // for each pair, process appropriately!
    val processedGroups = selectedGroups.map(process)

    // finally, rejoin the selected and unselected drones
    val processedDrones = processedGroups.flatMap((g) => g._1)
    val processedStructures = processedGroups.map((g) => g._2)

    val allDrones = processedDrones ++ otherDrones
    val allStructures = processedStructures ++ otherStructures

    // and return a new world!
    World(allDrones, allStructures, clock)
  }

  // query behaviours
  def blocked(x: Int, y: Int): Boolean = {
    // TODO implement geometry
    false
  }
}
