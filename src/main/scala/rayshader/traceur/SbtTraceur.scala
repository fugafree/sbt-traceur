package rayshader.traceur

import sbt._
import sbt.Keys._
import com.typesafe.sbt.jse.SbtJsTask
import com.typesafe.sbt.web.{CompileProblems, LineBasedProblem, PathMapping}
import com.typesafe.sbt.web.pipeline.Pipeline
import java.io.File

import xsbti.Severity

object Import {

  val traceur = TaskKey[Pipeline.Stage]("traceur", "sbt-web plugin to compile ES6 to ES5 with traceur-compiler.")

  object TraceurModules {

    sealed trait Modules

    case object AMD extends Modules
    case object CommonJS extends Modules
    case object Closure extends Modules
    case object Instantiate extends Modules
    case object Inline extends Modules
    case object Bootstrap extends Modules
  }

  object TraceurModuleName {

    sealed trait ModuleName

    case object Named extends ModuleName
    case object Anonymous extends ModuleName
    case object Default extends ModuleName
  }

  object TraceurKeys {

    val sourceDir = SettingKey[String]("traceur-source-dir", "The relative path where your ES6 files are located. Default: javascripts/")
    val outputDir = SettingKey[String]("traceur-output-dir", "The relative path where your compiled ES6 files will be located. Default: javascripts/")
    val useDir = SettingKey[Boolean]("traceur-use-dir", "If true will use the option '--dir in out' where 'in' and 'out' are 'sourceDir' and 'outputDir'. Default: false")
    val sourceFiles = SettingKey[Seq[String]]("traceur-source-files", "Relative paths of the files to compile. Should just be the 'root' module, traceur will pull the rest. Default: Seq(\"main.js\")")
    val outputFile = SettingKey[String]("traceur-output-file", "The name of the compiled file. Default: main.js")
    val sourceMaps = SettingKey[Boolean]("traceur-source-maps", "Enable source maps generation. Default: false")
    val experimental = SettingKey[Boolean]("traceur-experimental", "Turns on all experimental features. Default: false")
    val modules = SettingKey[TraceurModules.Modules]("traceur-modules", "Select the output format for modules between AMD, CommonJS, Closure, Instantiate, Inline, Bootstrap. Default: Bootstrap.")
    val moduleName = SettingKey[TraceurModuleName.ModuleName]("traceur-module-name", "Named for named, Anonymous for anonymous modules; Default depends on 'modules'. Default: Default")
    val options = SettingKey[Seq[String]]("traceur-options", "Provide extra options to traceur-compiler.")
    val includeRuntime = SettingKey[Boolean]("traceur-include-runtime", "If traceur-runtime.js code should be copied in the outputDir. Default: true")
    val removeJs = SettingKey[Boolean]("traceur-remove-js", "Remove .js files from sourceDir within the pipeline. Default: true")

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
    useDir := false,
    sourceFiles := Seq("main.js"),
    outputFile := "main.js",
    sourceMaps := false,
    experimental := false,
    modules := TraceurModules.Bootstrap,
    moduleName := TraceurModuleName.Default,
    options := Seq(),
    includeRuntime := true,
    removeJs := true,
    resourceManaged in traceur := webTarget.value / traceur.key.label,
    traceur := runCompiler.dependsOn(webJarsNodeModules in Plugin).value
  )

  private def boolToParam(value: Boolean, param: String): Seq[String] = {
    if (value) Seq(param) else Seq()
  }

  private def runCompiler: Def.Initialize[Task[Pipeline.Stage]] = Def.task {
    mappings: Seq[PathMapping] =>

      val source = (sourceDirectory in Assets).value / sourceDir.value
      val output = (resourceManaged in traceur).value / "build"
      val outputFull = output / outputDir.value
      val outputFileFull = outputFull / outputFile.value
      val sourceFilesAbsolutePath = sourceFiles.value.map(f => source + "/" + f.toString)

      val hasModuleName = if (moduleName.value != TraceurModuleName.Default) true else false
      val moduleNameValue = if (hasModuleName == true && moduleName.value == TraceurModuleName.Named) "true" else "false"

      val parameters = (
        boolToParam(experimental.value, "--experimental")
          ++ boolToParam(sourceMaps.value, "--source-maps=file")
          ++ Seq("--modules=" + modules.value.toString.toLowerCase)
          ++ boolToParam(hasModuleName, "--moduleName=" + moduleNameValue)
          ++ options.value
          ++ (if (useDir.value == false) Seq("--out", outputFileFull.toString) else Seq())
          ++ (if (useDir.value == false) sourceFilesAbsolutePath else Seq())
          ++ (if (useDir.value == true) Seq("--dir", source.toString, outputFull.toString) else Seq())
        )

      if (useDir.value == false) {
        streams.value.log.info(s"[Traceur]\tCompiling ${sourceFiles.value.size} file(s) to ${outputFile.value}")
      } else {
        streams.value.log.info(s"[Traceur]\tCompiling ${source.listFiles.toSeq.size} file(s) to ${outputDir.value}")
      }

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
      var outputMappings = mappings.seq

      if (removeJs.value)
        outputMappings = mappings.filter(f => !(f._2.startsWith(sourceDir.value + File.separator) && f._2.endsWith(".js")))

      (outputMappings.toSet ++ (outputFiles pair relativeTo(output))).toSeq
  }

}