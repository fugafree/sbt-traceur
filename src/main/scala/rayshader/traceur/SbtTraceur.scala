package rayshader.traceur

import sbt._
import sbt.Keys._
import com.typesafe.sbt.jse.SbtJsTask
import com.typesafe.sbt.web.{CompileProblems, LineBasedProblem, PathMapping}
import com.typesafe.sbt.web.pipeline.Pipeline
import xsbti.Severity

object Import {

  val traceur = TaskKey[Pipeline.Stage]("traceur", "sbt-web plugin to compile ES6 to ES5 with traceur-compiler from Google.")

  object TraceurKeys {

    val sourceDir = SettingKey[String]("traceur-source-dir", "Source directory containing ES6 javascript files. Default: javascripts/")
    val outputDir = SettingKey[String]("traceur-output-dir", "Output directory containing compiled ES6 javascript files. Default: javascripts/")
    val sourceFiles = SettingKey[Seq[String]]("traceur-source-files", "Source ES6 files to compile. Should just be the 'root' modules, traceur will pull the rest.")
    val outputFileName = SettingKey[String]("traceur-output-file-name", "Name of the output file containing compiled ES6 javascript files. Default: main.js")
    val sourceMaps = SettingKey[Boolean]("traceur-source-maps", "Enable source maps generation. Default: true")
    val experimental = SettingKey[Boolean]("traceur-experimental", "Turns on all experimental features. Default: false")
    val includeRuntime = SettingKey[Boolean]("traceur-include-runtime", "If traceur-runtime.js code should be included in Assets. Default: true")

  }
}

object SbtTraceur extends AutoPlugin {

  override def requires = SbtJsTask

  override def trigger = AllRequirements

  val autoImport = Import

  import com.typesafe.sbt.jse.SbtJsEngine.autoImport.JsEngineKeys._
  import com.typesafe.sbt.jse.SbtJsTask.autoImport.JsTaskKeys._
  import com.typesafe.sbt.web.Import.WebKeys._
  import com.typesafe.sbt.web.SbtWeb.autoImport._
  import rayshader.traceur.Import.TraceurKeys._
  import rayshader.traceur.Import._

  override def projectSettings = Seq(
    sourceDir := "javascripts",
    outputDir := "javascripts",
    sourceFiles := Seq("main.js"),
    outputFileName := "main.js",
    sourceMaps := true,
    experimental := false,
    includeRuntime := true,
    resourceManaged in traceur := webTarget.value / traceur.key.label,
    traceur := runCompiler.dependsOn(webJarsNodeModules in Plugin).value
  )

  private def boolToParam(value: Boolean, param: String): Seq[String] = {
    if (value) Seq(param) else Seq()
  }

  private def runCompiler: Def.Initialize[Task[Pipeline.Stage]] = Def.task {
    mappings: Seq[PathMapping] =>

      val source = (sourceDirectory in Assets).value / sourceDir.value
      val sourceFilesAbsolutePath = sourceFiles.value.map(f => source + "/" + f.toString)
      val output = (resourceManaged in traceur).value / "build"
      val outputFull = output / outputDir.value
      val outputFile = outputFull / outputFileName.value

      streams.value.log.info("[Traceur]\tCompiling with traceur-compiler v0.0.90")

      val parameters = (
        boolToParam(experimental.value, "--experimental")
        ++ boolToParam(sourceMaps.value, "--source-maps=file")
        ++ sourceFilesAbsolutePath
        ++ Seq("--out", outputFile.toString)
      )

      try {
        SbtJsTask.executeJs(
          state.value,
          EngineType.Node,
          None,
          Nil,
          (webJarsNodeModulesDirectory in Plugin).value / "traceur" / "src" / "node" / "command.js",
          parameters,
          (timeoutPerSource in traceur).value
        )
      } catch {
        case failure: SbtJsTask.JsTaskFailure => {
          val Pattern = "^\\[? *'?(.+?):(\\d+):(\\d+):(.+?)'?,? ?\\]?".r
          val problems = failure.getMessage.split("\n").map {
            case Pattern(path, line, column, message) => {
              new LineBasedProblem(message, Severity.Error, line.toInt, column.toInt - 1, "", new File(path))
            }
            case _ => throw new RuntimeException(failure)
          }

          CompileProblems.report(reporter.value, problems)
        }
      }

      if (includeRuntime.value) {
        val traceurRuntime = IO.read((webJarsNodeModulesDirectory in Plugin).value / "traceur" / "bin" / "traceur-runtime.js")
        val traceurRuntimeOutput = outputFull / "traceur-runtime.js"

        IO.write(traceurRuntimeOutput, traceurRuntime)
      }

      val outputFiles = outputFull.listFiles.toSeq

      mappings.filter(f => f._2.startsWith(sourceDir.value.toString + "/")) ++ (outputFiles pair relativeTo(output)).toSeq
  }

}