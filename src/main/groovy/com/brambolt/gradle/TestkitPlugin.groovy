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

package com.brambolt.gradle

import com.brambolt.gradle.testkit.tasks.ProcessTestFixtures
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.language.jvm.tasks.ProcessResources

/**
 * A Gradle plug-in with opinionated test utilities.
 *
 * The plugin creates a <code>processFixtures</code> task when applied.
 *
 * If a <code>test</code> task exists then the process-fixtures task is made a
 * dependency of test, so fixtures are processed before testing.
 */
class TestkitPlugin implements Plugin<Project> {

  /**
   * Applies the plug-in to the parameter project.
   * @param project The project to apply the plug-in to
   */
  void apply(Project project) {
    project.logger.debug("Applying ${getClass().getCanonicalName()}.")
    Task processTestFixtures = createProcessTestFixturesTask(project)
    configureDefaultTaskDependencies(project, processTestFixtures)
  }

  protected Task createProcessTestFixturesTask(Project project) {
    project.task(
      // Apply an empty configuration closure to set defaults, if any:
      [type: ProcessTestFixtures], 'processTestFixtures') {}

  }

  protected void configureDefaultTaskDependencies(Project project, Task processTestFixtures) {
    project.afterEvaluate { Project p ->
      try {
        p.tasks.withType(ProcessResources).named('processTestResources') { Task t ->
          t.dependsOn(processTestFixtures)
        }
      } catch (Exception x) {
        project.logger.debug('Unable to add task dependency', x)
      }
    }
  }
}
