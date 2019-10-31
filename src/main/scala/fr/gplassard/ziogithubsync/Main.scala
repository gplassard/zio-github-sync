package fr.gplassard.ziogithubsync

import java.nio.file.{Files, Paths}

import fr.gplassard.ziogithubsync.github.GithubApi
import fr.gplassard.ziogithubsync.github.model.BranchSettings
import fr.gplassard.ziogithubsync.program.GithubSync
import fr.gplassard.ziogithubsync.settings.SettingsApi
import zio.{DefaultRuntime, ZIO}
import zio.console._
import zio.duration.Duration

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

  runtime.unsafeRun(program.provide(Env(runtime)))
}

case class Env(runtime: DefaultRuntime) extends Console with GithubApi with SettingsApi {
  override val console: Console.Service[Any] = runtime.Environment.console

  override def fetchBranchSettings(repo: String): ZIO[Any, Throwable, BranchSettings] =
    ZIO.sleep(Duration.fromScala(scala.concurrent.duration.FiniteDuration((Math.random() * 10).toLong, "s")))
    .zipRight(putStrLn(s"fetch github $repo"))
    .zipRight(ZIO.succeed(BranchSettings()))
    .provide(runtime.Environment)


  override def updateBranchSettings(repo: String, settings: BranchSettings): ZIO[Any, Throwable, BranchSettings] =
    ZIO.sleep(Duration.fromScala(scala.concurrent.duration.FiniteDuration((Math.random() * 10).toLong, "s")))
      .zipRight(putStrLn(s"update $repo"))
      .zipRight(ZIO.succeed(BranchSettings()))
      .provide(runtime.Environment)

  override def fetchExpectedSettings(repo: String): ZIO[Any, Throwable, BranchSettings] =
    ZIO.sleep(Duration.fromScala(scala.concurrent.duration.FiniteDuration((Math.random() * 10).toLong, "s")))
      .zipRight(putStrLn(s"fetch settings $repo"))
      .zipRight(ZIO.succeed(BranchSettings()))
      .provide(runtime.Environment)
}
