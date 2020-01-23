package fr.gplassard.ziogithubsync.core.settings

import fr.gplassard.ziogithubsync.core.program.model.{GithubAuthentication, GithubRepo, RepositorySettings}
import zio.ZIO

trait SettingsApi {
  val settingsApi: SettingsApi.Service[Any]
}
object SettingsApi {
  trait Service[T] {
    def fetchExpectedSettings(repo: GithubRepo): ZIO[T, Throwable, RepositorySettings]
    def githubAuthentication(): ZIO[T, Throwable, GithubAuthentication]
  }
}
