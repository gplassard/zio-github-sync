package fr.gplassard.ziogithubsync.infra


import fr.gplassard.ziogithubsync.core.github.GithubApi
import fr.gplassard.ziogithubsync.core.program.model.{GithubBranch, GithubBranchProtection, GithubRepo, RepositorySettings}
import fr.gplassard.ziogithubsync.core.settings.SettingsApi
import github4s.Github._
import github4s.free.domain.{Branch, BranchProtection}
import github4s.{Github, HttpRequestBuilderExtensionJVM}
import github4s.free.interpreters.{Capture, Interpreters}
import org.json4s.DefaultFormats
import org.json4s.native.Serialization
import sttp.client._
import sttp.client.asynchttpclient.zio.AsyncHttpClientZioBackend
import sttp.client.json4s._
import zio.clock.Clock
import zio.console.Console
import zio.{Task, ZIO}

trait GithubApiLive extends GithubApi {
  val console: Console.Service[Any]
  val clock: Clock.Service[Any]
  val settingsApi: SettingsApi.Service[Any]

  import scala.concurrent.ExecutionContext.Implicits.global
  import github4s.implicits._

  final val githubApi = new GithubApi.Service[Any] {
    override def fetchBranches(repo: GithubRepo): ZIO[Any, Throwable, List[Branch]] = {
      for {
        authentication <- settingsApi.githubAuthentication()
        ghReponse <- ZIO.fromFuture(_ => Github(Some(authentication.oauthToken)).repos.listBranches(repo.owner, repo.repo).execFuture(Map.empty))
        branches <- ZIO.fromEither(ghReponse).map(_.result)
        _ <- console.putStrLn(s"Fetched branches for repo $repo : $branches")
      } yield branches
    }

    override def getBranchProtection(repo: GithubRepo, branch: String): ZIO[Any, Throwable, Option[BranchProtection]] = {
     for {
        authentication <- settingsApi.githubAuthentication()
        ghReponse <- ZIO.fromFuture(_ => Github(Some(authentication.oauthToken)).branches.getBranchProtection(repo.owner, repo.repo, branch).execFuture(Map.empty))
        result <- ZIO.fromEither(ghReponse)
      } yield if (result.statusCode == 200) Some(result.result) else None
    }

    override def updateBranchProtection(repo: GithubRepo, branch: String, settings: GithubBranchProtection): ZIO[Any, Throwable, Unit] = {
      val req = (token:String) => basicRequest.put(uri"https://api.github.com/repos/${repo.owner}/${repo.repo}/branches/$branch/protection")
        .body(Serialization.write(settings))
        .headers(Map("Authorization" -> s"token $token"))

      for {
        authentication <- settingsApi.githubAuthentication()
        response <- AsyncHttpClientZioBackend().map { implicit backend =>
          req(authentication.oauthToken).send()
        }
        code <- response.map(_.code)
        _ <- console.putStrLn(s"Updating branch settings $code $repo $branch $settings")
      } yield ()
    }

    override def fetchRepositorySettings(repo: GithubRepo): ZIO[Any, Throwable, RepositorySettings] = {
      for {
        branches <- this.fetchBranches(repo)
        protections <- ZIO.collectAllPar(branches.map(branch => this.getBranchProtection(repo, branch.name)))
      } yield RepositorySettings(
        repo,
        branches.zip(protections)
          .map{case (b, protection) => (b.name, protection)}
          .toMap
      )
    }

    override def deleteBranchProtection(repo: GithubRepo, branch: String): ZIO[Any, Throwable, Unit] = {
      val req = (token:String) => basicRequest.delete(uri"https://api.github.com/repos/${repo.owner}/${repo.repo}/branches/$branch/protection")
        .headers(Map("Authorization" -> s"token $token"))

      for {
        authentication <- settingsApi.githubAuthentication()
        response <- AsyncHttpClientZioBackend().map { implicit backend =>
          req(authentication.oauthToken).send()
        }
        _ <- response.flatMap {
          case r if r.code.isSuccess => ZIO.succeed(())
          case r => ZIO.fail(new RuntimeException(s"Unexpected response while deleting branch protection for $repo, $branch : ${r.code}"))
        }
        _ <- console.putStrLn(s"Delete branch protection $repo $branch")
      } yield ()
    }
  }

}
