lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "wolfendale",
      scalaVersion := "2.12.6",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "scraper",
    libraryDependencies ++= Seq(
      "org.scalactic" %% "scalactic" % "3.0.5",
      "org.scalatest" %% "scalatest" % "3.0.5" % Test
    )
  )
