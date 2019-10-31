package fr.gplassard.ziogithubsync.core.program.model

case class GithubSyncResult(repo: String, result: SyncResult)

sealed trait SyncResult
object SyncResult {
  case object AlreadyInSync extends SyncResult
  case object Synced extends SyncResult
}
