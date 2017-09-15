package com.archmage.drones.components

final case class State[+S](state: S, timestamp: Int = 0)
