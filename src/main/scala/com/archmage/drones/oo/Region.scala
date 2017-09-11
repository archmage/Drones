package com.archmage.drones.oo

import scala.collection.mutable.ListBuffer

class Region {
  val locations = new ListBuffer[Location]
  val drones = new ListBuffer[Drone]
  
  def getLocation(x:Int, y:Int) = {
    val location = locations.clone().filter(loc => loc.x == x && loc.y == y)
    if(location.isEmpty) null else location(0)
  }
}