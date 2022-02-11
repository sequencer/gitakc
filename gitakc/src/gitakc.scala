case class Config(ttl: BigInt, userMapUrl: String = "", userMap: Map[String, Seq[String]], cacheFolder: String)
case class UserMap(userMap: Map[String, Seq[String]])

object Config {
  implicit val rw: upickle.default.ReadWriter[Config] = upickle.default.macroRW
}

object UserMap {
  implicit val rw: upickle.default.ReadWriter[UserMap] = upickle.default.macroRW
}
object gitakc {
  import sttp.client3.quick._
  def main(args: Array[String]): Unit = {
    val c: Config = upickle.default.read[Config](
      os.read(
        sys.env
          .get("GITAKC_CONFIG")
          .map(os.Path(_))
          .getOrElse(os.root / "etc" / "gitakc.json")
      )
    )
    val userMap =
      if (c.userMapUrl.nonEmpty)
        c.userMap ++ upickle.default.read[UserMap](quickRequest.get(uri"${c.userMapUrl}").send(backend).body).userMap
      else
        c.userMap
    println(userMap)
    val cacheDir = os.Path(c.cacheFolder)
    val username = args(0)
    val userCache = cacheDir / username
    // create cache dir.
    os.makeDir.all(cacheDir)
    userMap.get(username) match {
      case Some(githubUsernames) => {
        // update user cache.
        if (
          // no cache but user in the user map
          (!os.isFile(userCache)) ||
          // cache ttl timeout
          (System.currentTimeMillis - os.mtime(userCache)) > (c.ttl * 1000)
        ) {
          System.err.println("downloading!")
          os.write.over(
            userCache,
            githubUsernames
              .map(u => quickRequest.get(uri"https://github.com/$u.keys").send(backend).body)
              .mkString("\n")
          )
        }
        // print to stdout, ssh will read this.
        System.out.println(os.read(userCache))
      }
      case None =>
      // Do nothing.
    }
  }
}
