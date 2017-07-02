organization := "com.typesafe.sbt"

name := "sbt-traceur-test"

version := "0"

scalaVersion := "2.10.4"

lazy val root = (project in file(".")).enablePlugins(SbtWeb)

pipelineStages := Seq(traceur, concat, uglify)

TraceurKeys.sourceDir := (sourceDirectory in Assets).value / "js"
TraceurKeys.outputDir := (resourceManaged in Assets).value.getParentFile.getParentFile / "js"
TraceurKeys.sourceMaps := false
TraceurKeys.sourceFiles := Seq("app.js")
TraceurKeys.outputFileName := "app.js"

Concat.groups := Seq(
  "js/main.js" -> group(Seq("js/traceur-runtime.js", "js/app.js"))
)

UglifyKeys.uglifyOps := UglifyOps.singleFile