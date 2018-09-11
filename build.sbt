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
      "com.typesafe.akka" %% "akka-stream" % "2.5.16",
      "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.16",
      "com.typesafe.akka" %% "akka-http"   % "10.1.5",
      "com.softwaremill.sttp" %% "core" % "1.3.2",
      "com.softwaremill.sttp" %% "akka-http-backend" % "1.3.2",
      "org.scala-graph" %% "graph-core" % "1.12.5",
      "org.scala-graph" %% "graph-dot" % "1.12.1",
      "org.scalatest" %% "scalatest" % "3.0.5" % Test,
      "com.github.tomakehurst" % "wiremock" % "2.15.0" % Test
    )
  )
