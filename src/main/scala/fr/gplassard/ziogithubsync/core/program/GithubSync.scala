package fr.gplassard.ziogithubsync.core.program

import fr.gplassard.ziogithubsync.core.github.GithubApi
import fr.gplassard.ziogithubsync.core.program.model.{Diff, GithubRepo, GithubSyncResult, SyncPlan, SyncResult}
import fr.gplassard.ziogithubsync.core.settings.SettingsApi
import zio.ZIO

object GithubSync {

  def plan(repo: GithubRepo): ZIO[GithubApi with SettingsApi, Throwable, SyncPlan] = {
    for {
      res <-
        ZIO.accessM[SettingsApi](_.settingsApi.fetchExpectedSettings(repo))
            .zipPar(ZIO.accessM[GithubApi](_.githubApi.fetchRepositorySettings(repo)))
      expectedSettings = res._1
      currentSettings = res._2
      outOfSyncBranches = expectedSettings
        .branchProtections
        .filterNot{case (branch, branchSetting) => currentSettings.branchProtections.get(branch).contains(branchSetting)}
        .map{case (branch, branchSetting) => branch -> Diff(currentSettings.branchProtections.get(branch).flatten, branchSetting)}
    } yield SyncPlan(
      repo,
      outOfSyncBranches
    )
  }

  def executePlan(syncPlan: SyncPlan): ZIO[GithubApi, Throwable, Boolean] = {
    for {
      _ <- ZIO.collectAllPar(
        syncPlan.branchProtectionDiffs
          .map{ _ match {
            case (branch, Diff(_, Some(protection))) => ZIO.accessM[GithubApi](_.githubApi.updateBranchProtection(syncPlan.repo, branch, protection))
            case (branch, Diff(_, None)) => ZIO.accessM[GithubApi](_.githubApi.deleteBranchProtection(syncPlan.repo, branch))
          }}
        )
    } yield true
  }

}
