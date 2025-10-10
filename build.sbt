ThisBuild / organization := "dev.holt"
ThisBuild / homepage := Some(url("https://github.com/bpholt/cats-js-clipboard"))
ThisBuild / licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
ThisBuild / startYear := Option(2018)
ThisBuild / developers := List(
  Developer(
    "bpholt",
    "Brian Holt",
    "bholt+cats-js-clipboard@planetholt.com",
    url("https://holt.dev")
  ),
)
ThisBuild / tlBaseVersion := "0.5"
ThisBuild / tlCiReleaseBranches := Seq("main")

ThisBuild / mergifyStewardConfig ~= { _.map {
  _.withMergeMinors(true)
}}
ThisBuild / mergifySuccessConditions += MergifyCondition.Custom("#approved-reviews-by>=1")
ThisBuild / mergifyRequiredJobs ++= Seq("validate-steward")

tpolecatScalacOptions += ScalacOptions.release("8")
ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec.temurin("17"))
ThisBuild / githubWorkflowScalaVersions := Seq("3", "2.13", "2.12")
ThisBuild / githubWorkflowBuildPreamble += WorkflowStep.Run(name = Option("Install jsdom"), commands = List("npm install jsdom"))

lazy val `cats-js-clipboard` = (project in file("."))
  .settings(
    crossScalaVersions := Seq(
      "3.3.0",
      "2.12.18",
      "2.13.11",
    ),
    libraryDependencies ++= {
      Seq(
        "org.typelevel" %%% "cats-core" % "2.9.0",
        "org.typelevel" %%% "cats-effect" % "3.5.1",
        "org.scala-js" %%% "scalajs-dom" % "2.6.0",
        "org.scala-js" %%% "scala-js-macrotask-executor" % "1.1.1",
      )
    },
    description := "cats-effect wrapped utility for copying text to the clipboard from JavaScript",
    Test / jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv,
  )
  .enablePlugins(ScalaJSPlugin)
