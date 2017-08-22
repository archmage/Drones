package com.archmage.drones

/**
 * generates incrementing ids
 * companion trait to Unique.scala
 * this goes on the companion object
 */
trait IdGenerator { 
  def prefix:String
  private var nextValue = 0
  def nextId = {nextValue += 1; prefix + nextValue}
}