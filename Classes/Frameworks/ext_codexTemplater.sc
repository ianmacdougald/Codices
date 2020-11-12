+ CodexTemplater {
	codexFrameworks_path { 
		^Main.packages.asDict.at(\CodexIan)+/+"Classes/Frameworks";
	}

	codexProxier_section { | templateName("section") |
		this.makeTemplate(
			templateName, 
			this.codexFrameworks_path
			+/+"codexProxier_section.scd"
		);
	}

	codexInstrument_synthDef { | templateName("synthDef") |
		this.makeTemplate(
			templateName, 
			this.codexFrameworks_path
			+/+"codexInstrument_synthDef.scd"
		);
	}

	codexPanel_function { | templateName("function") |
		this.makeTemplate(
			templateName, 
			this.codexFrameworks_path
			+/+"codexPanel_function.scd"
		);
	}

	codexPanel { | templateName("panel") |
		this.makeTemplate(
			templateName, 
			this.codexFrameworks_path
			+/+"codexPanel.scd"
		);
	}

	codexProxyGraph { | templateName("graph") |
		this.makeTemplate(
			templateName, 
			this.codexFrameworks_path
			+/+"codexProxyGraph.scd"
		);
	}
}
