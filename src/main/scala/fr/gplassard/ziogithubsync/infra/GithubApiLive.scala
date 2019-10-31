package fr.gplassard.ziogithubsync.infra

import fr.gplassard.ziogithubsync.core.github.GithubApi
import fr.gplassard.ziogithubsync.core.program.model.BranchSettings
import zio.ZIO
import zio.clock.Clock
import zio.console.Console
import zio.duration.Duration

trait GithubApiLive extends GithubApi {
  val console: Console.Service[Any]
  val clock: Clock.Service[Any]

  final val githubApi = new GithubApi.Service[Any] {
    override def fetchBranchSettings(repo: String): ZIO[Any, Throwable, BranchSettings] =
      clock.sleep(Duration.fromScala(scala.concurrent.duration.FiniteDuration((Math.random() * 10).toLong, "s")))
        .zipRight(console.putStrLn(s"fetch github $repo"))
        .zipRight(ZIO.succeed(BranchSettings()))

    override def updateBranchSettings(repo: String, settings: BranchSettings): ZIO[Any, Throwable, BranchSettings] =
      clock.sleep(Duration.fromScala(scala.concurrent.duration.FiniteDuration((Math.random() * 10).toLong, "s")))
        .zipRight(console.putStrLn(s"update $repo"))
        .zipRight(ZIO.succeed(BranchSettings()))
  }

}
