package fr.gplassard.ziogithubsync.github

import fr.gplassard.ziogithubsync.github.model.BranchSettings
import zio.ZIO

trait GithubApi {
  def fetchBranchSettings(repo: String): ZIO[Any, Throwable, BranchSettings]
  def updateBranchSettings(repo: String, settings: BranchSettings): ZIO[Any, Throwable, BranchSettings]
}
