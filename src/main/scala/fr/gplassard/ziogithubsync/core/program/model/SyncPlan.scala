package fr.gplassard.ziogithubsync.core.program.model

case class SyncPlan(repo: GithubRepo, branchProtectionDiffs: Map[String, Diff[GithubBranchProtection]])
