package fr.gplassard.ziogithubsync.settings

import fr.gplassard.ziogithubsync.github.model.BranchSettings
import zio.ZIO

trait SettingsApi {
  def fetchExpectedSettings(repo: String): ZIO[Any, Throwable, BranchSettings]
}
