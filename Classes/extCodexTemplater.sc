+ CodexTemplater {
	codexFrameworksPath { 
		^Main.packages.asDict.at(\CodexIan)+/+"Classes/Frameworks";
	}

	codexProxierSection { | templateName("section") |
		this.makeTemplate(
			templateName, 
			this.codexFrameworksPath
			+/+"codexProxierSection.scd"
		);
	}

	codexInstrumentSynthDef { | templateName("synthDef") |
		this.makeTemplate(
			templateName, 
			this.codexFrameworksPath
			+/+"codexInstrumentSynthDef.scd"
		);
	}

	codexPanelFunction { | templateName("function") |
		this.makeTemplate(
			templateName, 
			this.codexFrameworksPath
			+/+"codexPanelFunction.scd"
		);
	}

	codexPanel { | templateName("panel") |
		this.makeTemplate(
			templateName, 
			this.codexFrameworksPath
			+/+"codexPanel.scd"
		);
	}

	codexProxyGraph { | templateName("graph") |
		this.makeTemplate(
			templateName, 
			this.codexFrameworksPath
			+/+"codexProxyGraph.scd"
		);
	}
}
