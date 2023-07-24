libraryDependencies += "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.1.0"

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.13.2")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.4.4")
addSbtPlugin("org.typelevel" % "sbt-typelevel-ci-release" % "0.4.21")
addSbtPlugin("org.typelevel" % "sbt-typelevel-mergify" % "0.4.21")
