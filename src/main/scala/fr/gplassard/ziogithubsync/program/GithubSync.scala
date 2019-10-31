package fr.gplassard.ziogithubsync.program

import fr.gplassard.ziogithubsync.github.GithubApi
import fr.gplassard.ziogithubsync.program.model.{GithubSyncResult, SyncResult}
import fr.gplassard.ziogithubsync.settings.SettingsApi
import zio.ZIO

object GithubSync {

  def sync(repo: String): ZIO[GithubApi with SettingsApi, Throwable, GithubSyncResult] = {
    for {
      res <-
        ZIO.accessM[SettingsApi](_.fetchExpectedSettings(repo))
            .zipPar(ZIO.accessM[GithubApi](_.fetchBranchSettings(repo)))
      expectedSettings = res._1
      currentSettings = res._2
      inSync = expectedSettings == currentSettings
      _ <-
        if (!inSync) ZIO.accessM[GithubApi](_.updateBranchSettings(repo, expectedSettings))
        else ZIO.succeed(currentSettings)
    } yield GithubSyncResult(
      repo,
      if (inSync) SyncResult.AlreadyInSync else SyncResult.Synced
    )
  }

}
