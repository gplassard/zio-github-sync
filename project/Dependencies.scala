import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.1.0"
  lazy val zio  = "dev.zio" %% "zio" % "1.0.0-RC17"
  lazy val sttp  = "com.softwaremill.sttp.client" %% "core" % "2.0.0-RC1"
  lazy val sttpZio  = "com.softwaremill.sttp.client" %% "async-http-client-backend-zio" % "2.0.0-RC6"
  lazy val sttpJson4s  = "com.softwaremill.sttp.client" %% "json4s" % "2.0.0-RC6"
  lazy val json4s = "org.json4s" %% "json4s-native" % "3.7.0-M2"
  lazy val github4s = "com.47deg" %% "github4s" % "0.21.1-SNAPSHOT"
}
