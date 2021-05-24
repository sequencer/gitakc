case class Config(ttl: BigInt, userMap: Map[String, String], cacheFolder: String)

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
          case Some(githubUsername) => {
            System.err.println("downloading!")
            val keys = requests.get(s"https://github.com/$githubUsername.keys", check = false).text()
            os.write.over(userCache, keys)
          }
          case None =>
        }
      System.out.println(os.read(userCache))
    }
  }
}
