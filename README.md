sbt-traceur
===========
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://github.com/LuigiPeace/sbt-traceur/blob/master/LICENSE)
![Version: 2.1.0](https://img.shields.io/badge/version-2.1.0-green.svg)

sbt-web plugin to compile ES6 to ES5 with [traceur-compiler](https://github.com/google/traceur-compiler).

Changelog
---------
Version | Changes
--------|-----------------------------------------
 2.1.0  | Upgrade traceur-compiler to v0.0.111.<br>Improve plugin by supporting all traceur-compiler options.
 2.0.0  | Upgrade traceur-compiler to v0.0.90.<br>Refactor plugin to use pipeline stage.
 1.0.2  | First try with Scala, sbt and Bintray. Experimental, don't use it!
 1.0.1  | Last version forked from arielscarpinelli/sbt-traceur

I decided to fork the sbt-web plugin of [arielscarpinelli](https://github.com/arielscarpinelli) because I couldn't control `traceur` within the pipeline stage.
You can now call `traceur` in the pipeline stage where ever you want.


How to use
----------
You first need to add this resolver in your project's `plugins.sbt` file:

```scala
    resolvers += Resolver.bintrayRepo("rayshader", "sbt")
```

Then, add the dependency with the `addSbtPlugin` command:

```scala
    addSbtPlugin("rayshader" % "sbt-traceur" % "2.2.0-SNAPSHOT")
```

Your project's `build.sbt` file also needs to enable sbt-web plugins:

```scala
    lazy val root = (project in file(".")).enablePlugins(SbtWeb)
```

Finally, you need to include the plugin into the pipeline stage like:

```scala
    pipelineStages := Seq(traceur)
```

NB: the plugin will remove .js files from the `sourceDir` path. As only the output file seems necessary.
This is due to an issue encountered with sbt-uglify. If you want to keep these files in your pipeline
you can turn off the option `removeJs`.

Example
-------
You may want to compile ES6 to ES5, then minify javascript and concat everything.
Let's say your main entry point of ES6 code is `app.js`.
Then, you can minify `app.js` and `traceur-runtime.js`.
Finally you can concat the whole to output a single `main.min.js` file:

```scala
    pipelineStages := Seq(traceur, uglify, concat)
    
    TraceurKeys.sourceDir := "js"
    TraceurKeys.outputDir := "js"
    TraceurKeys.sourceFiles := Seq("app.js")
    TraceurKeys.outputFile := "app.js"
    
    UglifyKeys.uglifyOps := UglifyOps.singleFile
    
    Concat.groups := Seq(
      "js/main.min.js" -> group(Seq("js/traceur-runtime.min.js", "js/app.min.js"))
    )
```


Configuration
-------------
All options are available within the object `TraceurKeys`.
For example, to enable `sourceMaps` you'll do:

```scala
    import rayshader.traceur._
    
    TraceurKeys.sourceMaps := true
```

For option `modules`, you will found constants in the object `TraceurModules`.

For option `moduleName`, you will found constants in the object `TraceurModuleName`.

For example:

```scala
    import rayshader.traceur._
    
    TraceurKeys.modules := TraceurModules.AMD
    TraceurKeys.moduleName := TraceurModuleName.Named
```


Option          | Description                                                                                          | Default
----------------|------------------------------------------------------------------------------------------------------|----------
sourceDir       | The relative path where your ES6 files are located. | `javascripts`
outputDir       | The relative path where your compiled ES6 files will be located. | `javascripts`
useDir          | If true will use the option `--dir in out` where `in` and `out` are `sourceDir` and `outputDir`. | `false`
sourceFiles     | Relative paths of the files to compile. Should just be the 'root' module, traceur will pull the rest. | `Seq("main.js")`
outputFile      | The name of the compiled file. | `main.js`
sourceMaps      | Enable source maps generation. | `false`
experimental    | Turns on all experimental features. | `false`
modules         | Select the output format for modules between AMD, CommonJS, Closure, Instantiate, Inline, Bootstrap. | `Bootstrap`
moduleName      | `Named` for named, `Anonymous` for anonymous modules; `Default` depends on `modules`. | `Default`
options         | Provide extra options to traceur-compiler. | 
includeRuntime  | If traceur-runtime.js code should be copied in the outputDir. | `true`
removeJs        | Remove .js files from sourceDir within the pipeline. | `true`


How to test
-----------
 1. First clone the project.
 2. Use the command `clean compile package publishLocal` with sbt to compile and publish locally the plugin.
 3. Use commands `clean webStage` within `test/[NAME_OF_THE_TEST]` directory to try the plugin.
 3. Open `index.html` from `src/main/assets/` directory.
 4. Output section shall show some texts and display a green background.


More
----
Feel free to contribute for more features and bug fixes through issues :).