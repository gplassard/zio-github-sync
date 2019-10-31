import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8"
  lazy val zio  = "dev.zio" %% "zio" % "1.0.0-RC16"
  lazy val sttp  = "com.softwaremill.sttp.client" %% "core" % "2.0.0-M9"
}
