package fr.gplassard.ziogithubsync.core.program

import fr.gplassard.ziogithubsync.core.github.GithubApi
import fr.gplassard.ziogithubsync.core.program.model.{GithubSyncResult, SyncResult}
import fr.gplassard.ziogithubsync.core.settings.SettingsApi
import zio.ZIO

object GithubSync {

  def sync(repo: String): ZIO[GithubApi with SettingsApi, Throwable, GithubSyncResult] = {
    for {
      res <-
        ZIO.accessM[SettingsApi](_.settingsApi.fetchExpectedSettings(repo))
            .zipPar(ZIO.accessM[GithubApi](_.githubApi.fetchBranchSettings(repo)))
      expectedSettings = res._1
      currentSettings = res._2
      inSync = expectedSettings == currentSettings
      _ <-
        if (!inSync) ZIO.accessM[GithubApi](_.githubApi.updateBranchProtection(repo, expectedSettings))
        else ZIO.succeed(currentSettings)
    } yield GithubSyncResult(
      repo,
      if (inSync) SyncResult.AlreadyInSync else SyncResult.Synced
    )
  }

}
