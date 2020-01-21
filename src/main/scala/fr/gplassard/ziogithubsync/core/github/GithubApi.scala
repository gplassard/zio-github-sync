package fr.gplassard.ziogithubsync.core.github

import fr.gplassard.ziogithubsync.core.program.model.{GithubBranch, GithubBranchProtection, GithubRepo, RepositorySettings}
import zio.ZIO

trait GithubApi {
  val githubApi: GithubApi.Service[Any]
}

object GithubApi {
  trait Service[T] {
    def fetchBranches(repo: GithubRepo): ZIO[T, Throwable, List[GithubBranch]]
    def updateBranchProtection(repo: GithubRepo, branch: String, settings: GithubBranchProtection): ZIO[T, Throwable, GithubBranchProtection]
    def fetchRepositorySettings(repo: GithubRepo): ZIO[T, Throwable, RepositorySettings]
  }
}
