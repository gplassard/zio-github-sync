import Dependencies._

ThisBuild / scalaVersion     := "2.13.1"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "fr.gplassard"

lazy val root = (project in file("."))
  .settings(
    name := "zio-github-sync",
    libraryDependencies ++= Seq(
      zio,
      sttp,
      sttpZio,
      sttpJson4s,
      json4s,
      github4s,
      scalaTest % Test
    )
  )
