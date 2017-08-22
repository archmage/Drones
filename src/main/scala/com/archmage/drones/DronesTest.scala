package com.archmage.drones

import com.archmage.commander.Command
import com.archmage.commander.Commander
import com.archmage.commander.Menu
import com.archmage.commander.Helpers

object DronesTest {
	def main(args: Array[String]): Unit = {
		val region = new Region
		region.locations += new ResourcePoint(region, 4, 2)

		val tempDef: () => Unit = () => { println("blah") }

		for (i <- 0 to 19) {
			region.drones += new Drone(region, 0, 0)
			region.drones(i).navigate(4, 2)
			region.drones(i).gather
			region.drones(i).navigate(0, 0)
		}

		//  val testcmd = Command.make("testing", new java.util.function.Supplier[String] {
		//    override def get = "this is a big test"
		//  })
		//
		//  val main = Menu.make(testcmd, Helpers.CMDLIST, Helpers.CMDHELP, Helpers.CMDEXIT)
		//
		//  val commander = Commander.make(main)
		//
		//  while(commander.run()){}
	}
}