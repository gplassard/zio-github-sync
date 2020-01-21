package fr.gplassard.ziogithubsync.core.program.model

case class GithubSyncResult(repo: GithubRepo, result: SyncResult)

sealed trait SyncResult
object SyncResult {
  case object AlreadyInSync extends SyncResult
  case object Synced extends SyncResult
}
