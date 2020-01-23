package fr.gplassard.ziogithubsync.core.program.model

case class GithubBranchProtection(allow_deletions: Boolean,
                                  allow_force_pushes: Boolean,
                                  required_status_checks: Option[String],
                                  required_pull_request_reviews: Option[String],
                                  enforce_admins: Option[Boolean],
                                  restrictions: Option[String],
                                  required_linear_history: Boolean)
