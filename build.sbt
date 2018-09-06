lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "wolfendale",
      scalaVersion := "2.12.6",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "scraper",
    libraryDependencies ++= Seq(
      "org.jsoup" % "jsoup" % "1.11.3",
      "org.scalactic" %% "scalactic" % "3.0.5",
      "org.scalatest" %% "scalatest" % "3.0.5" % Test,
      "com.github.tomakehurst" % "wiremock" % "2.15.0" % Test
    )
  )
