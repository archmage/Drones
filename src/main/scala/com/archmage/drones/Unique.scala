package com.archmage.drones

/**
 * stores generated id (prefix + value)
 * example ids: d1, dog42, titanfall2
 * companion trait to IdGenerator.scala
 * this goes on the class
 */
trait Unique {
  var generator:IdGenerator = null
  lazy val id = generator.nextId
}