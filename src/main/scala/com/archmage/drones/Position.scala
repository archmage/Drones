package com.archmage.drones

final class Position(private var _x:Float, private var _y:Float) {
  def x = _x.toInt
  def y = _y.toInt
  
  def x_= (value:Int) = _x = value
  def y_= (value:Int) = _y = value
  
  def move(dx:Float, dy:Float) {
    _x += dx
    _y += dy
  }
  
  override def toString:String = {
    "[" + x + ", " + y + "]"
  }
}