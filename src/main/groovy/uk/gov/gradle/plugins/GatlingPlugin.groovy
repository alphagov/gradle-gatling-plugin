package uk.gov.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class GatlingPlugin implements Plugin<Project> {
	final String GATLING_VERSION = '2.0.0-RC2'

	private String gatlingReportsDirectory
	private Project project

	void apply(Project project) {
		this.project = project
		project.plugins.apply 'scala'
		project.extensions.create('gatling', GatlingPluginExtension)
		project.dependencies {
			testCompile "io.gatling.highcharts:gatling-charts-highcharts:${GATLING_VERSION}",
					'com.nimbusds:nimbus-jose-jwt:2.22.1'
		}
		project.repositories {
			maven {
				url 'http://repository.excilys.com/content/groups/public'
				url 'https://oss.sonatype.org/content/repositories/snapshots'
			}
		}
		gatlingReportsDirectory = "$project.buildDir.absolutePath/gatling-reports"
		project.task('gatlingTest',
				dependsOn:'build') << {
			project.gatling.verifySettings()
			final def sourceSet = project.sourceSets.test
			final def gatlingRequestBodiesDirectory = firstPath(sourceSet.resources.srcDirs) + "/request-bodies"
			final def gatlingClasspath = sourceSet.output + sourceSet.runtimeClasspath
			final def scenarios = project.gatling._scenarios ?: getGatlingScenarios(sourceSet)
			logger.lifecycle "Executing gatling scenarios: $scenarios"
			scenarios?.each { scenario ->
				project.javaexec {
					main = 'io.gatling.app.Gatling'
					classpath = gatlingClasspath
					if(project.gatling.verbose) jvmArgs '-verbose'
					// If a user has the GATLING_HOME env var set, gradle will try to compile
					// simulations which are saved in GATLING_HOME.  This can break the build.
					environment GATLING_HOME:''
					args '-rf', gatlingReportsDirectory,
							'-s', scenario,
							'-bf', gatlingRequestBodiesDirectory
				}
			}
			logger.lifecycle "Gatling scenarios completed."
		}
		project.task('openGatlingReport') << {
			def mostRecent
			withGatlingReportsDirs { projectDir ->
				if(projectDir > mostRecent) {
					mostRecent = projectDir
				}
			}
			openReport mostRecent
		}
		project.task('openGatlingReports') << {
			withGatlingReportsDirs openReport
		}
	}

	private getGatlingScenarios(sourceSet) {
		final String scenarioSrcDir = "$project.projectDir.absolutePath/src/$sourceSet.name/scala"
		final int scenarioPathPrefix = "$scenarioSrcDir/".size()
		final int scenarioPathSuffix = - ('.scala'.size() + 1)
		sourceSet.allScala.files*.toString().
				findAll { it.endsWith 'Scenario.scala' }.
				collect { it[scenarioPathPrefix..scenarioPathSuffix] }*.
				replace('/', '.')
	}

	private firstPath(Set<File> files) {
		return files.toList().first().toString()
	}

	private openReport = { reportDir ->
		project.exec { commandLine 'open', "$reportDir/index.html" }
	}

	private withGatlingReportsDirs(Closure c) {
		new File(gatlingReportsDirectory).eachDirMatch(~/.*-\d+/, c)
	}
}

