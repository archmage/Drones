package com.archmage.drones

import com.archmage.drones.Drone.Gather
import com.archmage.drones.components.Geo

case class Structure(geo: Geo, scrap: Int = 0) {

  def act(world: World): Structure = {
    val drones = world.drones.filter(d => {
      d.geo.xpos == geo.xpos && d.geo.ypos == geo.ypos &&
        d.state.state == Gather()
    })
    if(drones.isEmpty) this
    else Structure(geo, Integer.max(0, scrap - drones.length))
  }
}
