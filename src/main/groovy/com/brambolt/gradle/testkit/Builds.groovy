package com.brambolt.gradle.testkit

import org.gradle.testkit.runner.GradleRunner
import org.junit.rules.TemporaryFolder

class Builds {

  /**
   * Creates a build file for testing.
   *
   * @param basename The build file basename
   * @param content The content of the build file
   * @param testProjectDir The temporary directory for the test project
   * @return The build file
   */
  static File createBuildFile(String basename, String content, TemporaryFolder testProjectDir) {
    File file = testProjectDir.newFile(basename)
    file << content
    file
  }

  static def runTask(File dir, String... args) {
    GradleRunner.create()
      .withProjectDir(dir)
      .withArguments(args)
      .withPluginClasspath()
      .build()
  }
}
