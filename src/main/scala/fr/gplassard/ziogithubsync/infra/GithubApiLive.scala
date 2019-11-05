package fr.gplassard.ziogithubsync.infra

import fr.gplassard.ziogithubsync.core.github.GithubApi
import fr.gplassard.ziogithubsync.core.program.model.{GithubBranch, GithubBranchProtection, GithubRepo, GithubTeam}
import sttp.client._
import sttp.client.json4s._
import sttp.client.asynchttpclient.zio.AsyncHttpClientZioBackend
import zio.ZIO
import zio.clock.Clock
import zio.console.Console
import zio.duration.Duration

trait GithubApiLive extends GithubApi {
  val console: Console.Service[Any]
  val clock: Clock.Service[Any]
  implicit val sttpBackend = AsyncHttpClientZioBackend()

  final val githubApi = new GithubApi.Service[Any] {
    override def fetchBranches(repo: GithubRepo): ZIO[Any, Throwable, List[GithubBranch]] = {
      val req = basicRequest.get(uri"https://api.github.com/repos/${repo.owner}/${repo.repo}/teams").response(asJson[List[GithubBranch]])
      req.send()(sttpBackend)
    }
      clock.sleep(Duration.fromScala(scala.concurrent.duration.FiniteDuration((Math.random() * 10).toLong, "s")))
        .zipRight(console.putStrLn(s"fetch github $repo"))
        .zipRight(ZIO.succeed(GithubBranchProtection()))

    override def updateBranchProtection(repo: String, settings: GithubBranchProtection): ZIO[Any, Throwable, GithubBranchProtection] =
      clock.sleep(Duration.fromScala(scala.concurrent.duration.FiniteDuration((Math.random() * 10).toLong, "s")))
        .zipRight(console.putStrLn(s"update $repo"))
        .zipRight(ZIO.succeed(GithubBranchProtection()))
  }

}
