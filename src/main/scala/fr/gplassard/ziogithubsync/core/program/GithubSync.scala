package fr.gplassard.ziogithubsync.core.program

import fr.gplassard.ziogithubsync.core.github.GithubApi
import fr.gplassard.ziogithubsync.core.program.model.{GithubRepo, GithubSyncResult, SyncResult}
import fr.gplassard.ziogithubsync.core.settings.SettingsApi
import zio.ZIO

object GithubSync {

  def sync(repo: GithubRepo): ZIO[GithubApi with SettingsApi, Throwable, GithubSyncResult] = {
    for {
      res <-
        ZIO.accessM[SettingsApi](_.settingsApi.fetchExpectedSettings(repo.repo))
            .zipPar(ZIO.accessM[GithubApi](_.githubApi.fetchRepositorySettings(repo)))
      expectedSettings = res._1
      currentSettings = res._2
     /* inSync = expectedSettings == currentSettings
      _ <-
        if (!inSync) ZIO.accessM[GithubApi](_.githubApi.updateBranchProtection(repo, expectedSettings))
        else ZIO.succeed(currentSettings)*/
    } yield GithubSyncResult(
      repo,
      SyncResult.Synced
    )
  }

}
