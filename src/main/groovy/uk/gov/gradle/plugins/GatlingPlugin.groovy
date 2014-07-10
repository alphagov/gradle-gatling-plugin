package uk.gov.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class GatlingPlugin implements Plugin<Project> {
	private String gatlingReportsDirectory
	private Project project

	void apply(Project project) {
		this.project = project
		project.plugins.apply 'scala'
		project.dependencies {
			testCompile 'io.gatling.highcharts:gatling-charts-highcharts:2.0.0-M3a',
					'com.nimbusds:nimbus-jose-jwt:2.22.1'
		}
		project.repositories {
			maven {
				url 'http://repository.excilys.com/content/groups/public'
			}
		}
		gatlingReportsDirectory = "$project.buildDir.absolutePath/gatling-reports"
		project.task('gatling',
				dependsOn:'build') << {
			final def sourceSet = project.sourceSets.test
			final String scenarioSrcDir = "$project.projectDir.absolutePath/src/$sourceSet.name/scala"
			final int scenarioPathPrefix = "$scenarioSrcDir/".size()
			final int scenarioPathSuffix = - ('.scala'.size() + 1)
			final List scenarios = sourceSet.allScala.files*.toString().
					findAll { it.endsWith 'Scenario.scala' }.
					collect { it[scenarioPathPrefix..scenarioPathSuffix] }*.
					replace('/', '.')
			final def gatlingClasspath = sourceSet.output + sourceSet.runtimeClasspath
			logger.lifecycle "Executing gatling scenarios: $scenarios"
			scenarios.each { scenario ->
				project.javaexec {
					main = 'io.gatling.app.Gatling'
					classpath = gatlingClasspath
					args '-rf', gatlingReportsDirectory,
							'-s', scenario
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

	private openReport = { reportDir ->
		project.exec { commandLine 'open', "$reportDir/index.html" }
	}

	private withGatlingReportsDirs(Closure c) {
		new File(gatlingReportsDirectory).eachDirMatch(~/^perftest-.*/, c)
	}
}

