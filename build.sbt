import sbt.Keys.libraryDependencies

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.1.0"

resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/maven-releases/"

val AkkaVersion = "2.8.0"
val AkkaHttpVersion = "10.5.0"
val loggerVersion = "1.4.6"
val slickVersion = "3.5.0-M2"
val ioJsonVersion = "1.3.6"
val redisVersion = "4.3.1"
val configVersion = "1.4.2"

lazy val root = (project in file("."))
  .settings(
    name := "ScalaTest",
    organization := "com.innowise-group",
  )

libraryDependencies ++= Seq( //Akka dependencies
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion cross CrossVersion.for3Use2_13,
  "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion cross CrossVersion.for3Use2_13,
  "io.spray" %% "spray-json" % ioJsonVersion cross CrossVersion.for3Use2_13)

libraryDependencies += "ch.qos.logback" % "logback-classic" % loggerVersion

libraryDependencies += "redis.clients" % "jedis" % redisVersion

libraryDependencies += "com.typesafe" % "config" % configVersion

libraryDependencies += "ch.megard" %% "akka-http-cors" % "1.2.0" cross CrossVersion.for3Use2_13

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % Test

libraryDependencies += "org.mockito" % "mockito-core" % "5.3.0" % Test
