/*
 * Copyright 2017-2020 Brambolt ehf.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.brambolt.gradle.testkit.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Task
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

import static com.brambolt.gradle.SpecObjects.asFile

/**
 * <p>Supports designating directories as fixture roots that will be converted
 * into zip files that are placed on class paths for testing.</p>
 *
 * <p>This is not very appropriate for unit testing but is useful for
 * integration testing. The usual model is to prepare a zip fixture than can
 * then be expanded into a temporary test project directory.</p>
 *
 * <p>This task will be modified to support the specification of fixtures, input
 * and output directories relative to source sets instead of the current model
 * of specification relative to the project and build directories.</p>
 */
class ProcessTestFixtures extends DefaultTask {

  /**
   * Specifies the fixtures to be processed.
   *
   * The value can be a string, a file or a closure, or a collection of these.
   *
   * If the value is a string or a collection with strings then each string will
   * be interpreted as a path relative to the input directory. For example, if
   * the input directory defaults to the project directory and the fixtures are
   * listed as <code>['src/test/fixture1', 'src/test/fixture2'] then the result
   * will be two directories named <code>fixture1</code> and <code>fixture2</code>
   * in the <code>destinationDir</code> location.
   *
   * See notes in class documentation snippet regarding a planned change.
   */
  Object fixtures

  /**
   * Specifies the root directory to locate fixtures under. The default is
   * the project directory. (See notes in class documentation snippet.)
   */
  @InputDirectory
  Object inputDir

  /**
   * Specifies where fixtures will be processed to. The default is
   * the project build directory. (See notes in class documentation snippet.
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
      inputDir = project.projectDir
    if (null == destinationDir)
      destinationDir = project.buildDir
    outputs.upToDateWhen { false }
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
          "Unable to process fixtures: ${fixtures}")
    }
  }

  protected void applyMultiple(List<?> fixtures) {
    File srcDir = asFile(inputDir)
    File destDir = asFile(destinationDir)
    fixtures.each { Object fixtureSpec ->
      String fixtureRelPath = getFixtureRelPath(fixtureSpec)
      File fixtureDir = new File(srcDir, fixtureRelPath)
      String fixtureName = fixtureDir.name
      ant.zip(destfile: new File(destDir, "${fixtureName}.zip")) {
        fileset(dir: fixtureDir)
      }
    }
  }

  protected String getFixtureRelPath(Object fixtureSpec) {
    asFile(fixtureSpec)
  }
}
