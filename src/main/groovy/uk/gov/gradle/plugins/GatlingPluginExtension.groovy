package uk.gov.gradle.plugins

class GatlingPluginExtension {
	String scenario
	List scenarios
	boolean verbose

	protected List get_scenarios() {
		scenarios?: scenario? [scenario]: null
	}

	protected void verifySettings() {
		if(scenario && scenarios) {
			throw new GatlingPluginConfigurationException('Should not define both gatling.scenario and gatling.scenarios')
		}
	}
}

