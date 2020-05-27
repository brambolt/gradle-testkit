

node {

  stage('checkout') {
    checkout scm
  }

  stage('build') {

    withCredentials(
      [usernamePassword(
        // The Jenkins server also has a gradle.properties in ~/.gradle
        credentialsId: 'artifactory.brambolt.io',
        usernameVariable: 'ORG_GRADLE_PROJECT_mavenUser',
        passwordVariable: 'ORG_GRADLE_PROJECT_mavenToken'),
       usernamePassword(
        credentialsId: 'gitlab.brambolt.io',
        usernameVariable: 'ORG_GRADLE_PROJECT_vcsUser',
        passwordVariable: 'ORG_GRADLE_PROJECT_vcsToken')]) {

      withEnv([
        "GRADLE_OPTS=" +
          "-Dgradle.user.home=${env.HOME}/.gradle " 
      ]) {
        sh 'echo ${GRADLE_OPTS}'

        sh './gradlew clean artifactoryPublish -PbuildNumber=${BUILD_NUMBER} --info --stacktrace --no-daemon --refresh-dependencies'
      }
    }
  }
}
