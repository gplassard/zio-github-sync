package fr.gplassard.ziogithubsync.core.github

import fr.gplassard.ziogithubsync.core.program.model.GithubBranchProtection
import zio.ZIO

trait GithubApi {
  val githubApi: GithubApi.Service[Any]
}

object GithubApi {
  trait Service[T] {
    def fetchBranchSettings(repo: String): ZIO[T, Throwable, GithubBranchProtection]
    def updateBranchProtection(repo: String, settings: GithubBranchProtection): ZIO[T, Throwable, GithubBranchProtection]
  }
}
