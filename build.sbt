lazy val drones = (project in file("."))
  .settings(
    name := "drones",
    organization := "com.archmage",
    version := "0.1",
    scalaVersion := "2.12.3",
    logBuffered in Test := false,
    resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases",
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
  )

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.2.2")