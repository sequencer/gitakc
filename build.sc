import mill._
import scalalib._
import scalafmt._
import publish._
import scalanativelib._
import mill.scalanativelib.api.{LTO, ReleaseMode}

object gitakc extends Module {
  object jvm extends Cross[GeneralJVM]("2.12.13", "2.13.6", "3.0.0")

  object native extends Cross[GeneralNative]("2.12.13", "2.13.6")

  class GeneralJVM(val crossScalaVersion: String) extends GeneralModule with ScalafmtModule {
    def scalaVersion = crossScalaVersion
    def prependShellScript = "#!/bin/sh\n" ++ super.prependShellScript()
    override def ivyDeps = super.ivyDeps() ++ Agg(
      ivy"com.lihaoyi::upickle:1.3.15",
      ivy"com.lihaoyi::os-lib:0.7.7",
      ivy"com.softwaremill.sttp.client3::core:3.3.4"
    )
  }

  class GeneralNative(val crossScalaVersion: String) extends GeneralModule with ScalaNativeModule {
    def scalaVersion = crossScalaVersion
    def scalaNativeVersion = "0.4.0"
    def releaseMode = ReleaseMode.ReleaseFull
    def nativeLTO = LTO.Full
    override def ivyDeps = super.ivyDeps() ++ Agg(
      ivy"com.lihaoyi::upickle::1.3.15",
      ivy"com.lihaoyi::os-lib::0.7.7",
      ivy"com.softwaremill.sttp.client3::core::3.3.4",
    )
  }

  trait GeneralModule extends ScalaModule with PublishModule {
    m =>
    override def millSourcePath = super.millSourcePath / os.up / os.up
    def publishVersion = "0.1"
    def pomSettings = PomSettings(
      description = artifactName(),
      organization = "me.jiuyang",
      url = "https://jiuyang.me",
      licenses = Seq(License.`Apache-2.0`),
      versionControl = VersionControl.github("sequencer", "gitakc"),
      developers = Seq(Developer("sequencer", "Jiuyang Liu", "https://jiuyang.me/"))
    )
  }

}
