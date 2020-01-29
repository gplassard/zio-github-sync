package fr.gplassard.ziogithubsync.core.github

import fr.gplassard.ziogithubsync.core.program.model.{GithubBranch, GithubBranchProtection, GithubRepo, RepositorySettings}
import zio.ZIO

trait GithubApi {
  val githubApi: GithubApi.Service[Any]
}

object GithubApi {

  trait Service[T] {
    def fetchBranches(repo: GithubRepo): ZIO[T, Throwable, List[GithubBranch]]

    def getBranchProtection(repo: GithubRepo, branch: String): ZIO[T, Throwable, Option[GithubBranchProtection]]

    def updateBranchProtection(repo: GithubRepo, branch: String, settings: GithubBranchProtection): ZIO[T, Throwable, Unit]

    def deleteBranchProtection(repo: GithubRepo, branch: String): ZIO[T, Throwable, Unit]

    def fetchRepositorySettings(repo: GithubRepo): ZIO[T, Throwable, RepositorySettings]
  }
}
