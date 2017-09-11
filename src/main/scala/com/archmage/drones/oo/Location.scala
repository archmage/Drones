package com.archmage.drones.oo

class Location(argRegion:Region, val x:Int, val y:Int) extends Regional {
  override def region = argRegion
  
  override def toString:String = {
    "[" + x + ", " + y + "]"
  }
}