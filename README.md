# Setup

Add the following to your project's `build.gradle`:

	apply plugin: 'gatling'
	repositories {
		mavenCentral()
		maven {
			url 'http://repository.excilys.com/content/groups/public'
		}
	}

# Use

Put [gatling][1] scenarios in `src/test/scala` and make sure the class name ends with `Scenario`.

# Tasks

## gatling

This will run all Scenarios provided in `src/test/scala`.

## openGatlingReport

Opens the report for the most recently run gatling scenario.  If you have more than one scenario, use `openGatlingReports`.

## openGatlingReports

Opens all gatling reports in `build/gatling-reports`.

# Building

	gradle install

[1]: http://gatling-tool.org/

