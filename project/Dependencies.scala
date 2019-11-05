import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8"
  lazy val zio  = "dev.zio" %% "zio" % "1.0.0-RC16"
  lazy val sttp  = "com.softwaremill.sttp.client" %% "core" % "2.0.0-M9"
  lazy val sttpZio  = "com.softwaremill.sttp.client" %% "async-http-client-backend-zio" % "2.0.0-M9"
  lazy val sttpJson4s  = "com.softwaremill.sttp.client" %% "json4s" % "2.0.0-M9"
  lazy val json4s = "org.json4s" %% "json4s-native" % "3.7.0-M1"
}
