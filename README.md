sbt-traceur
===========

sbt-web plugin to compile ES6 to ES5 with [traceur-compiler](https://github.com/google/traceur-compiler) by Google.

Changelog
---------

Version | Changes
--------|-----------------------------------------
 1.0.2  | Upgrade traceur-compiler to v0.0.90.<br>Refactor plugin to use pipeline stage.
 1.0.1  | Last version forked from arielscarpinelli/sbt-traceur

I decided to fork the sbt-web plugin of [arielscarpinelli](https://github.com/arielscarpinelli) because I couldn't control `traceur` within the pipeline stage.
You can now call `traceur` in the pipeline stage where ever you want.

How to use
----------

To use this plugin use the addSbtPlugin command within your project's `plugins.sbt` file:

```scala
    addSbtPlugin("com.typesafe.sbt" % "sbt-traceur" % "1.0.2")
```

Your project's build file also needs to enable sbt-web plugins. Within your `build.sbt` file:

```scala
    lazy val root = (project in file(".")).enablePlugins(SbtWeb)
```

Then, you need to include the plugin into the pipeline stage like:

```scala
    pipelineStages := Seq(traceur)
```

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
    TraceurKeys.outputFileName := "app.js"
    TraceurKeys.sourceMaps := false
    
    UglifyKeys.uglifyOps := UglifyOps.singleFile
    
    Concat.groups := Seq(
      "js/main.min.js" -> group(Seq("js/traceur-runtime.min.js", "js/app.min.js"))
    )
```

Configuration
-------------

All options are available within the object `TraceurKeys`.
For example, to disable `sourceMaps` you'll do:

```scala
    TraceurKeys.sourceMaps := false
```

in your `build.sbt` file.

Option          | Description                                                                                          | Default
----------------|------------------------------------------------------------------------------------------------------|----------
sourceDir       | The relative path where your ES6 files are located.                                                  | `javascripts`
outputDir       | The relative path where your compiled ES6 files will be located                                      | `javascripts`
sourceFiles     | Relative paths of files to compile. Should just be the 'root' module, traceur will pull the rest.    | `main.js`
outputFileName  | The name of the compiled file.                                                                       | `main.js`
sourceMaps      | Enable source maps generation.                                                                       | `true`
experimental    | Turns on all experimental features                                                                   | `false`
includeRuntime  | If traceur-runtime.js code should be included in the outputDir.                                      | `true`

More
----

Feel free to contribute for more features and bug fixes through the tickets system :).