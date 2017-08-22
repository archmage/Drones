package com.archmage.drones

trait ResourceHolder {
	var capacity: Int = _
	@volatile var _resource = 0

	def resource = _resource
	def resource_=(value: Int) = {
		_resource = Integer.max(0, Integer.min(capacity, value))
	}

	def gather(gatherer: ResourceHolder) = {
		this.synchronized {
			if (resource > 0 && gatherer.resource < gatherer.capacity) {
				resource -= 1
				gatherer.resource += 1
				1
			} else 0
		}
	}
}