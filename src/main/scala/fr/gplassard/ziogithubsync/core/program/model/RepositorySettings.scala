package fr.gplassard.ziogithubsync.core.program.model

case class RepositorySettings(repo: GithubRepo, branchProtections: Map[String, GithubBranchProtection])
