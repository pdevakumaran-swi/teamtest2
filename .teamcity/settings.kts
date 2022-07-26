import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.kubernetesCloudProfile
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2022.04"

project {

    vcsRoot(HttpsGithubComMarcobehlerjetbrainsBuildpipelinesRefsHeadsMain)

    buildType(Build)

    features {
        kubernetesCloudProfile {
            id = "kube-1"
            name = "profile1"
            description = "profile1"
            terminateIdleMinutes = 30
            apiServerURL = "localhost-kubernetes.com"
            authStrategy = defaultServiceAccount()
            param("kubeconfigContext", "")
        }
    }
}

object Build : BuildType({
    name = "Build"

    vcs {
        root(HttpsGithubComMarcobehlerjetbrainsBuildpipelinesRefsHeadsMain)
    }

    steps {
        maven {
            name = "maven-step-1"
            goals = "clean"
            pomLocation = "calculator-service/pom.xml"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            dockerImagePlatform = MavenBuildStep.ImagePlatform.Windows
            dockerImage = "maven:latest"
        }
        maven {
            name = "maven-step-2"
            enabled = false
            goals = "clean"
            pomLocation = "authorization-service/pom.xml"
            dockerImagePlatform = MavenBuildStep.ImagePlatform.Linux
            dockerPull = true
        }
    }

    triggers {
        vcs {
        }
    }
})

object HttpsGithubComMarcobehlerjetbrainsBuildpipelinesRefsHeadsMain : GitVcsRoot({
    name = "https://github.com/marcobehlerjetbrains/buildpipelines#refs/heads/main"
    url = "https://github.com/marcobehlerjetbrains/buildpipelines"
    branch = "refs/heads/main"
    branchSpec = "refs/heads/*"
})
