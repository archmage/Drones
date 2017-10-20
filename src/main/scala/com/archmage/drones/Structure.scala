package com.archmage.drones

import com.archmage.drones.components.Geo

case class Structure(geo: Geo = Geo(), scrap: Int = 0)

object Structure {
  sealed trait Type
  case class Scrapheap() extends Type
  case class Base() extends Type
}