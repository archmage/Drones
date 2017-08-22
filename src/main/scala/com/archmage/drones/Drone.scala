package com.archmage.drones

object Drone extends IdGenerator with ResourceHolder {
	override def prefix = "d"
	capacity = 10
}

class Drone(argRegion: Region, argX: Int, argY: Int) extends Unique with Regional with ActionQueue with ResourceHolder {
	val speed = 0.1f
	val frequency = 200

	val gatherSpeed = 2000

	override def region = argRegion

	val pos = new Position(argX, argY)

	generator = Drone
	println("Drone \"" + id + "\" created at " + pos.toString())

	capacity = Drone.capacity
	resource = 0

	def navigate(locX: Int, locY: Int) {
		enqueue(() => {
			travel(locX, locY, speed)
		})
	}

	def gather {
		val thisDrone = this
		enqueue(() => {
			if (capacity <= resource) {
				println("Already at max resource capacity.")
				return
			}
			val Location = region.getLocation(pos.x, pos.y)
			if (Location != null) {
				val resourcePoint = Location.asInstanceOf[ResourcePoint]
				while (resourcePoint.resource > 0 && resource < capacity) {
					Thread.sleep(gatherSpeed)
					val gatherAmount = Location.asInstanceOf[ResourcePoint].gather(thisDrone)
					println("Gathered " + resource + "/" + capacity + " resources from " +
						pos.toString() + " (remaining: " + resourcePoint.resource + ")")
				}
				println("Finished gathering resources.")
			} else println("Nothing to gather at " + pos.toString())
		})
	}

	private def travel(locX: Int, locY: Int, speed: Float) {
		while (pos.x != locX || pos.y != locY) {
			val xCache = pos.x
			val yCache = pos.y
			val dx = if (math.abs(locX - pos.x) < speed) locX - pos.x else speed * math.signum(locX - pos.x)
			val dy = if (math.abs(locY - pos.y) < speed) locY - pos.y else speed * math.signum(locY - pos.y)
			pos.move(dx, dy)
			if (xCache != pos.x || yCache != pos.y) {
				if (pos.x != locX || pos.y != locY) println(id + " enters " + pos.toString)
			}
			Thread.sleep(frequency)
		}
		println(id + " arrives at " + pos.toString)
	}
}
