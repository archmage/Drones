package com.archmage.drones

trait Regional {
  lazy val _region = region
  def region:Region
}