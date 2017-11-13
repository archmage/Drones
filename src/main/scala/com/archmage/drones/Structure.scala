package com.archmage.drones

import com.archmage.drones.Structure.{Scrapheap, Type}
import com.archmage.drones.components.Geo

case class Structure(geo: Geo = Geo(), structureType: Type = Scrapheap(), scrap: Int = 0) {
  def withLocation(geo: Geo): Structure = Structure(geo, structureType, scrap)
}

object Structure {
  sealed trait Type
  case class Scrapheap() extends Type
  case class Base() extends Type
}