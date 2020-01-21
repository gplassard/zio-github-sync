package fr.gplassard.ziogithubsync.infra

import fr.gplassard.ziogithubsync.core.github.GithubApi
import fr.gplassard.ziogithubsync.core.program.model.{GithubBranch, GithubBranchProtection, GithubRepo, GithubSettings, GithubTeam}
import sttp.client._
import sttp.client.json4s._
import sttp.client.asynchttpclient.zio.AsyncHttpClientZioBackend
import zio.{IO, Task, ZIO}
import zio.clock.Clock
import zio.console.Console
import zio.duration.Duration

trait GithubApiLive extends GithubApi {
  val console: Console.Service[Any]
  val clock: Clock.Service[Any]
  implicit val serialization = org.json4s.native.Serialization

  final val githubApi = new GithubApi.Service[Any] {
    override def fetchBranches(repo: GithubRepo): ZIO[Any, Throwable, List[GithubBranch]] = {
      val req = basicRequest.get(uri"https://api.github.com/repos/${repo.owner}/${repo.repo}/branches").response(asJson[List[GithubBranch]])

      for {
        response <- AsyncHttpClientZioBackend().map{ implicit backend =>
          req.send()
        }
        body <- response.map(_.body)
        branches <- ZIO.fromEither(body)
        _ <- console.putStrLn(s"Fetched branches for repo $repo : $branches")
      } yield branches
    }

    override def updateBranchProtection(repo: GithubRepo, branch: String, settings: GithubBranchProtection): ZIO[Any, Throwable, GithubBranchProtection] = {
      val req = basicRequest.put(uri"https://api.github.com/repos/${repo.owner}/${repo.repo}/$branch/protection").response(asJson[GithubBranchProtection])

      for {
        response <- AsyncHttpClientZioBackend().map { implicit backend =>
          req.send()
        }
        body <- response.map(_.body)
        branches <- ZIO.fromEither(body)
        _ <- console.putStrLn(s"Fetched branches for repo $repo : $branches")
      } yield branches
    }

    override def fetchRepositorySettings(repo: GithubRepo): ZIO[Any, Throwable, GithubSettings] = {
      for {
        branches <- this.fetchBranches(repo)
      } yield GithubSettings(
        repo,
        branches
          .map(b => (b.name, GithubBranchProtection(b.`protected`)))
          .toMap
      )
    }
  }

}
