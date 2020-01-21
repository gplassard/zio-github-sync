package fr.gplassard.ziogithubsync.infra

import fr.gplassard.ziogithubsync.core.program.model.{GithubBranchProtection, GithubRepo, RepositorySettings}
import fr.gplassard.ziogithubsync.core.settings.SettingsApi
import zio.ZIO
import zio.clock.Clock
import zio.console.Console
import zio.duration.Duration

trait SettingsApiLive extends SettingsApi {
  val console: Console.Service[Any]
  val clock: Clock.Service[Any]

  final val settingsApi = new SettingsApi.Service[Any] {
    override def fetchExpectedSettings(repo: GithubRepo): ZIO[Any, Throwable, RepositorySettings] =
      clock.sleep(Duration.fromScala(scala.concurrent.duration.FiniteDuration((Math.random() * 3).toLong, "s")))
        .zipRight(console.putStrLn(s"fetch settings $repo"))
        .zipRight(ZIO.succeed(RepositorySettings(repo, Map(
          "master" -> GithubBranchProtection(true)
        ))))
  }

}
