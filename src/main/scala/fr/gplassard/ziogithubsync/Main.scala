package fr.gplassard.ziogithubsync

import java.nio.file.{Files, Paths}

import fr.gplassard.ziogithubsync.core.github.GithubApi
import fr.gplassard.ziogithubsync.infra.{GithubApiLive, SettingsApiLive}
import fr.gplassard.ziogithubsync.core.program.GithubSync
import fr.gplassard.ziogithubsync.core.settings.SettingsApi
import zio.clock.Clock
import zio.console._
import zio.{DefaultRuntime, ZIO}

import scala.jdk.CollectionConverters._

object Main extends App {
  val runtime = new DefaultRuntime {}

  val program: ZIO[Console with GithubApi with SettingsApi, Throwable, Unit] = for {
    _ <- putStrLn("Hello World!")
    repos <- ZIO.effect(Files.readAllLines(Paths.get("src", "main", "resources", "github_repos.txt")).asScala.toList)
    results <- ZIO.collectAllPar(
      repos.map(GithubSync.sync)
    )
    _ <- ZIO.collectAll(results.map(_.toString).map(putStrLn))
    _ <- putStrLn("Done!")
  } yield ()

  runtime.unsafeRun(program.provide(new GithubApiLive with Console.Live with Clock.Live with SettingsApiLive{}))
}
