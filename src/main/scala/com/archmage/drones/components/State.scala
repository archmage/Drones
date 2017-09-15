package com.archmage.drones.components

case class State[+S](state: S, timestamp: Int = 0)
