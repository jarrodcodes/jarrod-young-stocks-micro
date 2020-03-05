import play.core.PlayVersion.akkaVersion

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "jarrod-young-stocks-micro",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      guice,
      ws,
      "com.yahoofinance-api" % "YahooFinanceAPI" % "3.15.0",
      "org.webjars" % "flot" % "0.8.3-1",
      "org.webjars" % "bootstrap" % "3.3.7",
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
      "org.awaitility" % "awaitility" % "4.0.1" % Test,
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  )
