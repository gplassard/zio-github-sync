package fr.gplassard.ziogithubsync.github

import fr.gplassard.ziogithubsync.github.model.BranchSettings
import zio.ZIO

trait GithubApi {
  val githubApi: GithubApi.Service[Any]
}

object GithubApi {
  trait Service[T] {
    def fetchBranchSettings(repo: String): ZIO[T, Throwable, BranchSettings]
    def updateBranchSettings(repo: String, settings: BranchSettings): ZIO[T, Throwable, BranchSettings]
  }
}
