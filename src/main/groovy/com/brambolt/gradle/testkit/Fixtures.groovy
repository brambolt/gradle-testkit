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

package com.brambolt.gradle.testkit

import java.nio.file.FileSystem
import org.apache.tools.ant.taskdefs.Expand
import org.junit.rules.TemporaryFolder

import static com.brambolt.nio.file.ZipFileSystems.unzip
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
   * @param resourceName The name of the fixture resource to expand
   * @param testProjectDir The test project temporary directory
   * @return The root directory of the fixture directory hierarchy
   */
  static File createDirectoryFixture(
    String resourceName, TemporaryFolder testProjectDir) {
    // Copy the fixture into the test project:
    createDirectoryFixture(
      resourceName,
      createFileFixture(resourceName, testProjectDir),
      testProjectDir.root)
  }

  static File createDirectoryFixture(
    String resourceName, File fixtureFile, File fixtureDir) {
    Expand expand = new Expand()
    expand.setSrc(fixtureFile)
    expand.setDest(fixtureDir)
    expand.execute()
    fixtureDir
  }

  static File createFileFixture(
    String resourceName, TemporaryFolder testProjectDir) {
    createFileFixture(resourceName, testProjectDir, null)
  }

  static File createFileFixture(
    String resourceName, TemporaryFolder testProjectDir, String suffix) {
    String fileName = resourceName + (null != suffix ? suffix : '')
    File zipFile = testProjectDir.newFile(fileName)
    String resourcePath = "/${resourceName}"
    URL url = Fixtures.class.getResource(resourcePath)
    if (null == url)
      throw new IllegalStateException(
        "Unable to locate class path resource: ${resourcePath}")
    copyURLToFile(url, zipFile)
    zipFile
  }

  static FileSystem createFileSystemFixture(
    String resourceName, TemporaryFolder testProjectDir) {
    unzip(createFileFixture(resourceName, testProjectDir))
  }
}
