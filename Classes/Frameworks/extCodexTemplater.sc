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
}
