package fr.gplassard.ziogithubsync.core.settings

import fr.gplassard.ziogithubsync.core.program.model.GithubBranchProtection
import zio.ZIO

trait SettingsApi {
  val settingsApi: SettingsApi.Service[Any]
}
object SettingsApi {
  trait Service[T] {
    def fetchExpectedSettings(repo: String): ZIO[T, Throwable, GithubBranchProtection]
  }
}
