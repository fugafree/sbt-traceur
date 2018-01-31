import rayshader.traceur._

organization := "rayshader"

name := "sbt-traceur-test-amd"

version := "2.2.0-SNAPSHOT"

scalaVersion := "2.10.4"

lazy val root = (project in file(".")).enablePlugins(SbtWeb)

pipelineStages := Seq(traceur, uglify, filter)

TraceurKeys.sourceDir := "js"
TraceurKeys.outputDir := "js"
TraceurKeys.useDir := true
TraceurKeys.modules := TraceurModules.AMD

UglifyKeys.uglifyOps := UglifyOps.singleFile

includeFilter in filter := "*.js"
excludeFilter in filter := "*.min.js"