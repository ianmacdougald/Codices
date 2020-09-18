+ CodexTemplater {
	codexFrameworks_path { 
		^Main.packages.asDict.at(\CodexIan)+/+"Classes/Frameworks";
	}

	codexSonata_section{ | templateName("section") |
		this.makeTemplate(
			templateName,
			this.codexFrameworks_path
			+/+"codex_sonata_section.scd"
		);
	}

	codexSymphony_movement { | templateName("movement") |
		this.makeTemplate(
			templateName, 
			this.codexFrameworks_path
			+/+"codexSymphony_movement.scd"
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
