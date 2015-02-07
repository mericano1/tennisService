val appName ="tennisService" 

val appVersion = "0.0.1"

val akkaVersion = "2.3.5"

val sprayVersion = "1.3.1"

organization := "asalvadore"

name := appName

version := appVersion


libraryDependencies ++= {
  Seq(
    "io.spray" % "spray-can" % sprayVersion,
    "io.spray" % "spray-routing" % sprayVersion,
    "io.spray" % "spray-caching" % sprayVersion,
    "io.spray" % "spray-client" % sprayVersion ,
    "io.spray" % "spray-testkit" % sprayVersion % "test",
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.play" %% "play-json" % "2.3.2",
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
  )
}

