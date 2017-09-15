package com.archmage.drones

import com.archmage.drones.components.Geo
import org.scalatest.FlatSpec

class DroneSpec extends FlatSpec {

  "A drone at (0, 0)" should "stay at (0, 0) after idling for a turn" in {
    assert(
      World(Seq(Drone()))
        .process("")
        .drones.head.geo == Geo())
  }

}
