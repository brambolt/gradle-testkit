package com.brambolt.gradle.testkit.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Task
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class ProcessFixtures extends DefaultTask {

  /**
   * Specifies the fixtures to be processed.
   *
   * The value can be a string, a file or a closure, or a collection of these.
   *
   * If the value is a string or a collection with strings then each string will
   * be interpreted as a path relative to the input directory. For example, the
   * <code>['fixture1', 'fixture2'] list will result in fixtures processed
   * from <code>src/test/fixture1</code> and <code>src/test/fixture2</code>,
   * assuming the default input directory is used.
   */
  Object fixtures

  /**
   * Specifies the root directory to locate fixtures under. The default is
   * <code>src/test</code>.
   */
  @InputDirectory
  Object inputDir

  /**
   * Specifies where fixtures will be processed to. The default is
   * <code>build/resources/test</code>.
   */
  @OutputDirectory
  Object destinationDir

  /**
   * Configures the task.
   * @param closure The configuration closure
   * @return The task itself
   */
  @Override
  Task configure(Closure closure) {
    super.configure(closure)
    if (null == inputDir)
      inputDir = new File(project.projectDir, 'src/test')
    if (null == destinationDir)
      destinationDir = new File(project.buildDir, 'resources/test')
    this
  }

  /**
   * Completes the task.
   */
  @TaskAction
  void apply() {
    switch (fixtures) {
      case null:
        break // No input, no output...
      case { it instanceof List }:
        applyMultiple(fixtures as List)
        break
      default:
        throw new GradleException(
          "Unable to process fixtures: ${fixtures}"
        )
    }
  }

  protected void applyMultiple(List<?> fixtures) {
    File srcDir = asFile(inputDir)
    File destDir = asFile(destinationDir)
    fixtures.each { Object fixtureSpec ->
      String fixtureName = getFixtureName(fixtureSpec)
      ant.zip(destfile: new File(destDir, "${fixtureName}.zip")) {
        fileset(dir: new File(srcDir, fixtureName))
      }
    }
  }

  static File asFile(Object dir) {
    switch (dir) {
      case { it instanceof File }:
        return dir as File
      case { it instanceof String }:
        return new File(dir as String)
      case { it instanceof Closure }:
        return asFile((dir as Closure).call())
      default:
        throw new GradleException("Unable to convert: ${dir}")
    }
  }

  protected String getFixtureName(Object fixtureSpec) {
    fixtureSpec.toString()
  }
}
