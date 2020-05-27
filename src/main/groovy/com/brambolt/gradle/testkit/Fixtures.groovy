package com.brambolt.gradle.testkit

import org.apache.tools.ant.taskdefs.Expand
import org.junit.rules.TemporaryFolder

import static org.apache.commons.io.FileUtils.copyURLToFile

/**
 * Utility class for working with test fixtures.
 */
class Fixtures {

  /**
   * This function locates a test fixture zip file on the class path and expands
   * the zip file into the test project directory.
   *
   * The zip file can be prepared using the <code>processFixtures</code> task.
   *
   * @param fixtureName The name of the fixture to expand
   * @param testProjectDir The test project temporary directory
   * @return The root directory of the fixture directory hierarchy
   */
  static File createFixture(
    String fixtureName, TemporaryFolder testProjectDir) {
    File fixtureDir = testProjectDir.root // Copy the fixture into the test project
    String fixtureZipFileName = "${fixtureName}.zip"
    File fixtureZipFile = testProjectDir.newFile(fixtureZipFileName)
    copyURLToFile(Thread.getResource("/${fixtureZipFileName}"), fixtureZipFile)
    Expand expand = new Expand()
    expand.setDest(fixtureDir)
    expand.setSrc(fixtureZipFile)
    expand.execute()
    fixtureDir
  }
}
