lazy val drones = (project in file("."))
  .settings(
    name := "drones",
    organization := "com.archmage",
    version := "0.1",
    scalaVersion := "2.12.3",
    resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases",
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
  )

