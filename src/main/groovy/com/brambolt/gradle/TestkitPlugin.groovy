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
