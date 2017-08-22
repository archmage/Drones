package com.archmage.drones

import scala.collection.mutable.Queue

trait ActionQueue {
	private val queue: Queue[() => Unit] = new Queue

	def actionsInQueue(): Int = {
		queue.size
	}

	def enqueue(action: () => Unit) = {
		val actionsBefore = actionsInQueue()
		queue += action
		if (actionsBefore != actionsInQueue() && actionsBefore == 0) run
	}

	def run = new Thread(new Runnable() {
		def run = {
			while (actionsInQueue() > 0) {
				queue.head.apply()
				queue.dequeue()
			}
		}
	}).start()
}