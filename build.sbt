sbtPlugin := true

name := "sbt-traceur"
organization := "rayshader"
version := "2.1.0"
description := "sbt-web plugin to compile ES6 to ES5 with traceur-compiler."
licenses += ("MIT", url("https://github.com/LuigiPeace/sbt-traceur/blob/master/LICENSE"))

publishMavenStyle := true
bintrayRepository := "sbt"
bintrayOrganization in bintray := None

scalaVersion := "2.10.4"

resolvers += Resolver.typesafeRepo("releases")

addSbtPlugin("com.typesafe.sbt" % "sbt-js-engine" % "1.2.1")

libraryDependencies ++= Seq(
  "org.webjars.npm" % "minimatch" % "3.0.4",
  "org.webjars.npm" % "traceur" % "0.0.111"
)

publishTo := Some("Bintray API Realm" at s"https://api.bintray.com/content/rayshader/sbt/sbt-traceur/2.0.1")