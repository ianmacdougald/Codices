+ CodexTemplater {
	codexFrameworks_path { 
		^Main.packages.asDict.at(\CodexIan)+/+"Classes/Frameworks";
	}

	codexSonata_section{ | templateName("section") |
		this.makeTemplate(
			templateName,
			this.codexFrameworks_path
			+/+"codexSonata_section.scd"
		);
	}

	codexMovement_section { | templateName("movement") |
		this.makeTemplate(
			templateName, 
			this.codexFrameworks_path
			+/+"codexMovement_section.scd"
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
