package com.archmage.drones
 
class ResourcePoint(region:Region, x:Int, y:Int) extends Location(region, x, y) with ResourceHolder {
  capacity = 100
  resource = 100
}