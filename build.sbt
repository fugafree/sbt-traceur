sbtPlugin := true

name := "sbt-traceur"
organization := "rayshader"
version := "1.0.2"
description := "sbt-web plugin to compile ES6 to ES5 with traceur-compiler by Google."
licenses += ("MIT", url("https://github.com/LuigiPeace/sbt-traceur/blob/master/LICENSE"))

publishMavenStyle := false
bintrayRepository := "sbt"
bintrayOrganization in bintray := None

scalaVersion := "2.10.4"

resolvers += Resolver.typesafeRepo("releases")

addSbtPlugin("com.typesafe.sbt" % "sbt-js-engine" % "1.2.1")

libraryDependencies ++= Seq(
  "org.webjars" % "traceur" % "0.0.90"
)

publishTo := Some("Bintray API Realm" at "https://api.bintray.com/content/rayshader/sbt/sbt-traceur/1.0.2")