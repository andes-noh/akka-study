ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

val AkkaVersion = "2.7.0"

lazy val root = (project in file("."))
  .settings(
    name := "akkaStudy"
  )

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-persistence-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-persistence-cassandra" % "1.1.0",
  "com.datastax.oss" % "java-driver-core" % "4.15.0",
  "com.typesafe.akka" %% "akka-serialization-jackson" % AkkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.4.5"
)
