organization := "rayshader"

name := "sbt-traceur-test"

version := "0"

scalaVersion := "2.10.4"

lazy val root = (project in file(".")).enablePlugins(SbtWeb)

pipelineStages := Seq(traceur, uglify, concat, filter)

TraceurKeys.sourceDir := "js"
TraceurKeys.outputDir := "js"
TraceurKeys.sourceMaps := false
TraceurKeys.sourceFiles := Seq("app.js")
TraceurKeys.outputFileName := "app.js"

Concat.groups := Seq(
  "js/main.min.js" -> group(Seq("js/traceur-runtime.min.js", "js/app.min.js"))
)

UglifyKeys.uglifyOps := UglifyOps.singleFile

includeFilter in filter := "*.js"
excludeFilter in filter := "main.min.js"