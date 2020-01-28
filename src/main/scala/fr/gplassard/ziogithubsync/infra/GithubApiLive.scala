package fr.gplassard.ziogithubsync.infra


import fr.gplassard.ziogithubsync.core.github.GithubApi
import fr.gplassard.ziogithubsync.core.program.model.{GithubBranch, GithubBranchProtection, GithubRepo, RepositorySettings}
import fr.gplassard.ziogithubsync.core.settings.SettingsApi
import org.json4s.DefaultFormats
import org.json4s.native.Serialization
import sttp.client._
import sttp.client.asynchttpclient.zio.AsyncHttpClientZioBackend
import sttp.client.json4s._
import zio.ZIO
import zio.clock.Clock
import zio.console.Console

trait GithubApiLive extends GithubApi {
  val console: Console.Service[Any]
  val clock: Clock.Service[Any]
  val settingsApi: SettingsApi.Service[Any]
  implicit val serialization = org.json4s.native.Serialization
  implicit val serializer = DefaultFormats.preservingEmptyValues

  final val githubApi = new GithubApi.Service[Any] {
    override def fetchBranches(repo: GithubRepo): ZIO[Any, Throwable, List[GithubBranch]] = {
      val req = (token:String) => basicRequest.get(uri"https://api.github.com/repos/${repo.owner}/${repo.repo}/branches")
        .headers(Map("Authorization" -> s"token $token"))
        .response(asJson[List[GithubBranch]])

      for {
        authentication <- settingsApi.githubAuthentication()
        response <- AsyncHttpClientZioBackend().map{ implicit backend =>
          req(authentication.oauthToken).send()
        }
        body <- response.map(_.body)
        branches <- ZIO.fromEither(body)
        _ <- console.putStrLn(s"Fetched branches for repo $repo : $branches")
      } yield branches
    }

    override def getBranchProtection(repo: GithubRepo, branch: String): ZIO[Any, Throwable, GithubBranchProtection] = {
      val req = (token:String) => basicRequest.get(uri"https://api.github.com/repos/${repo.owner}/${repo.repo}/branches/$branch/protection")
        .headers(Map("Authorization" -> s"token $token"))
        .response(asJson[GithubBranchProtection])

      for {
        authentication <- settingsApi.githubAuthentication()
        response <- AsyncHttpClientZioBackend().map{ implicit backend =>
          req(authentication.oauthToken).send()
        }
        body <- response.map(_.body)
        protection <- ZIO.fromEither(body)
        _ <- console.putStrLn(s"Get protection settings $repo $branch : $protection")
      } yield protection
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
