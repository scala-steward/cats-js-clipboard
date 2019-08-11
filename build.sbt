lazy val commonSettings = Seq(
  organization := "com.planetholt",
  homepage := Some(url("https://github.com/bpholt/cats-js-clipboard")),
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  startYear := Option(2018),
  libraryDependencies ++= {
    Seq(
      "org.typelevel" %%% "cats-core" % "2.0.0",
      "org.typelevel" %%% "cats-effect" % "2.0.0",
      "org.scala-js" %%% "scalajs-dom" % "0.9.7",
    )
  },
)

lazy val bintraySettings = Seq(
  bintrayVcsUrl := homepage.value.map(_.toString),
  bintrayRepository := "maven",
  bintrayOrganization := None,
  pomIncludeRepository := { _ => false }
)

lazy val releaseSettings = {
  import sbtrelease.ReleaseStateTransformations._

  Seq(
    releaseVersionBump := sbtrelease.Version.Bump.Minor,
    releaseCrossBuild := true,
    releaseProcess -= runTest,
  )
}

lazy val `cats-js-clipboard` = (project in file("."))
  .settings(Seq(
    description := "cats-effect wrapped utility for copying text to the clipboard from JavaScript",
  ) ++ commonSettings ++ bintraySettings ++ releaseSettings: _*)
  .enablePlugins(ScalaJSPlugin)
