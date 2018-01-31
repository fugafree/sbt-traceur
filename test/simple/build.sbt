import org.irundaia.sbt.sass._

organization := "rayshader"

name := "sbt-traceur-test-simple"

version := "2.2.0-SNAPSHOT"

scalaVersion := "2.10.4"

lazy val root = (project in file(".")).enablePlugins(SbtWeb)

pipelineStages := Seq(traceur, uglify, concat, filter)

SassKeys.cssStyle := Minified
SassKeys.generateSourceMaps := false
SassKeys.syntaxDetection := ForceScss

TraceurKeys.sourceDir := "js"
TraceurKeys.outputDir := "js"
TraceurKeys.sourceFiles := Seq("app.js")
TraceurKeys.outputFile := "app.js"

Concat.groups := Seq(
  "js/main.min.js" -> group(Seq("js/traceur-runtime.min.js", "js/app.min.js"))
)

UglifyKeys.uglifyOps := UglifyOps.singleFile

includeFilter in filter := "*.js" | "*.scss"
excludeFilter in filter := "main.min.js" | "main.css"