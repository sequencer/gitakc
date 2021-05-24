import scala.collection.parallel.CollectionConverters._

case class Config(ttl: BigInt, userMap: Map[String, Seq[String]], cacheFolder: String)

object Config {
  implicit val rw: upickle.default.ReadWriter[Config] = upickle.default.macroRW
}

object gitakc {
  def main(args: Array[String]): Unit = {
    implicit val c: Config = upickle.default.read[Config](
      os.read(
        sys.env
          .get("GITAKC_CONFIG")
          .map(os.Path(_))
          .getOrElse(os.root / "etc" / "gitakc.json")
      )
    )
    val cacheDir = os.Path(c.cacheFolder)
    val username = args(0)
    val userCache = cacheDir / username
    // create cache dir.
    os.makeDir.all(cacheDir)
    if (c.userMap.keys.toSet.contains(username)) {
      if (
        // no cache but user in the user map
        (!os.isFile(userCache)) ||
        // cache ttl timeout
        (System.currentTimeMillis - os.mtime(userCache)) > (c.ttl * 1000)
      )
        c.userMap.get(username) match {
          case Some(githubUsernames) => {
            System.err.println("downloading!")
            import sttp.client3.quick._
            // Bug from ScalaNative https://github.com/scala-native/scala-native/issues/2135
            // Will fix by https://github.com/scala-native/scala-native/pull/2141
            os.write.over(
              userCache,
              githubUsernames.par
                .map(u => quickRequest.get(uri"https://github.com/$u.keys").send(backend).body)
                .mkString("\n")
            )
          }
          case None =>
        }
      System.out.println(os.read(userCache))
    }
  }
}
