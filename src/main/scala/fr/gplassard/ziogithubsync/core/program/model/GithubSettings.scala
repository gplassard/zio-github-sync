package fr.gplassard.ziogithubsync.core.program.model

case class GithubSettings(repo: GithubRepo, branchProtections: Map[String, GithubBranchProtection])
