lazy val drones = (project in file("."))
  .settings(
    name := "drones",
    organization := "com.archmage",
    version := "0.1",
    scalaVersion := "2.12.3",
    libraryDependencies += "com.archmage" % "commander" % "1.2" from
      "https://github.com/archmage/commander/releases/download/v1.2/commander.jar"
  )

