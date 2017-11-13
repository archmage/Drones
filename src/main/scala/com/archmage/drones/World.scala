package com.archmage.drones

final case class World(drones: Map[Int, Drone] = Map(),
                       structures: Set[Structure] = Set(),
                       clock: Int = 0) {

  type DronePredicate = (Drone) => Boolean
  case class DronesStructure(drones: Map[Int, Drone], structure: Structure)
  type DronesStructureTransform = DronesStructure => DronesStructure

  def process(input: String = ""): World = {
    // process input
    // on "stop", stop all drones

    // drone processing
    val newDrones = drones.map((d) => d._1 -> d._2.act(this))

    World(newDrones, structures, clock).deposit.gather.explodeDrones.incrementClock
  }

  private def incrementClock: World = World(drones, structures, clock + 1)

  // double definition nonsense
  /*
  private def add(newDrones: Drone*): World = {
    if(newDrones.isEmpty) return this
    def appendDrone(newDrones: Seq[Drone], existingDrones: Map[Int, Drone]): Map[Int, Drone] = {
      if(newDrones.isEmpty) return existingDrones
      val id = (0 until existingDrones.size).find(existingDrones.contains).getOrElse(drones.size)
      appendDrone(newDrones.tail, existingDrones + (id -> newDrones.head))
    }
    World(appendDrone(newDrones, drones), structures, clock)
  }

  private def add(newStructures: Structure*): World = {
    if(newStructures.isEmpty) return this
    World(drones, structures ++ newStructures, clock)
  }
  */

  // drones explode
  private def explodeDrones: World = {
    val partitionCriteria: DronePredicate = d => d.isAboutToExplode(this)
    val process: DronesStructureTransform = g => {
      // calculate total of scrap held by exploding drones
      val scrapSum = g.drones.foldLeft(0) { (z, i) => z + i._2.scrap }
      DronesStructure(Map(), Structure(g.structure.geo, g.structure.structureType,
        g.structure.scrap + scrapSum + g.drones.size * Drone.explosionRemainder))
    }
    val structureIfNone = Structure()

    dronesInteractWithStructures(partitionCriteria, process, structureIfNone)
  }

  // this will break HARD if two structures are in the same place, so...
  // don't do that, I guess!
  private def gather: World = {
    val partitionCriteria: DronePredicate = (d) => d.state.state == Drone.Gather()
    val process: DronesStructureTransform = g => {
      val remainder = g.structure.scrap - g.drones.size
      // every drone gets to gather if there's enough scrap
      if(remainder >= 0) {
        DronesStructure(g.drones.keySet.map((id) => id -> g.drones(id).gather(this, remainder == 0)).toMap,
          Structure(g.structure.geo, g.structure.structureType, remainder))
      }
      // otherwise, only a suitable number of drones get to gather!
      else {
        // this is the number of lucky drones
        val luckyDroneCount = Integer.max(0, g.drones.size + remainder)
        val (luckyDrones, unluckyDrones) = g.drones.splitAt(luckyDroneCount)

        DronesStructure(
          luckyDrones.map((g) => g._1 -> g._2.gather(this, true)) ++ unluckyDrones,
          Structure(g.structure.geo))
      }
    }

    dronesInteractWithStructures(partitionCriteria, process)
  }

  def deposit: World = {
    val partitionCriteria: DronePredicate = (d) => d.state.state == Drone.Deposit()
    val process: DronesStructureTransform = g => {
      val processedDrones = g.drones.keySet.map((id) => {
        id -> Drone(g.drones(id).geo, g.drones(id).state, g.drones(id).queue, g.drones(id).scrap - 1)
      }).toMap
      DronesStructure(processedDrones, Structure(g.structure.geo, g.structure.structureType,
        g.structure.scrap + processedDrones.size))
    }

    dronesInteractWithStructures(partitionCriteria, process)
  }

  /** helper function to assist with drone : structure operations */
  private def dronesInteractWithStructures(partitionCriteria: DronePredicate,
                                           process: DronesStructureTransform,
                                           structureIfNone: Structure = null): World = {
    // split drones based on partition criteria
    val (selectedDrones, otherDrones) = drones.partition((d) => partitionCriteria(d._2))

    // if no selected drones, return this
    if(selectedDrones.isEmpty) return this

    // get the coordinates of each gathering location
    val locations = selectedDrones.keySet.map((d) => selectedDrones(d).geo.novel)
    // get all selected / unselected structures
    val (selectedStructures, otherStructures) = structures.partition((s) => locations.contains(s.geo))

    // pair each set of drones and each structure in tuples

    // if acting on no structure is okay...
    val selectedGroups: Set[DronesStructure] = if(structureIfNone != null) {
      // map off location and provide structureIfNone if none
      locations.map((l) => {
        DronesStructure(selectedDrones.filter((d) => d._2.geo.novel == l),
          selectedStructures.find((s) => s.geo == l).getOrElse(structureIfNone.withLocation(l)))
      })
    }
    else {
      // otherwise map off selected structures!
      selectedStructures.map((s) => {
        DronesStructure(selectedDrones.filter((d) => d._2.geo.novel == s.geo), s)
      })
    }

    // for each pair, process appropriately!
    val processedGroups = selectedGroups.map(process)

    // finally, rejoin the selected and unselected drones
    val processedDrones = processedGroups.flatMap((g) => g.drones).toMap
    val processedStructures = processedGroups.map((g) => g.structure)

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