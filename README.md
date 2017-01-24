# Setup

Build the plugin with `./gradlew`, this by default places an artifact in mavenLocal.

Add the following to your project's `build.gradle`:

	apply plugin: 'gatling'
	buildscript {
		dependencies {
			classpath group:'uk.gov', name:'gradle-gatling-plugin', version:'1.2-SNAPSHOT'
		}
		repositories {
			mavenLocal()
		}
	}

# Use

Put [gatling][1] scenarios in `src/test/scala` and make sure the class name ends with `Scenario`.

# Tasks

## gatlingTest

This will run all Scenarios provided in `src/test/scala`.

Alternatively you can specify the scenarios to run in your gradle file:

````
gatling {
  scenarios = [ "package.MySimulation", ... ]
}
````

## openGatlingReport

Opens the report for the most recently run gatling scenario.  If you have more than one scenario, use `openGatlingReports`.

## openGatlingReports

Opens all gatling reports in `build/gatling-reports`.

# Building

	gradle install

[1]: http://gatling-tool.org/

