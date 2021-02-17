+ CodexTemplater {
	codexMorePath {
		^(Main.packages.asDict
			.at(\CodexIanMore)+/+"Templates");
	}

	codexProxierSection { | templateName("section") |
		this.makeTemplate(
			templateName,
			this.codexMorePath
			+/+"codexProxierSection.scd"
		);
	}

	codexInstrumentSynthDef { | templateName("synthDef") |
		this.makeTemplate(
			templateName,
			this.codexMorePath
			+/+"codexInstrumentSynthDef.scd"
		);
	}

	codexPanelFunction { | templateName("function") |
		this.makeTemplate(
			templateName,
			this.codexMorePath
			+/+"codexPanelFunction.scd"
		);
	}

	codexPanel { | templateName("panel") |
		this.makeTemplate(
			templateName,
			this.codexMorePath
			+/+"codexPanel.scd"
		);
	}

	codexProxyGraph { | templateName("graph") |
		this.makeTemplate(
			templateName,
			this.codexMorePath
			+/+"codexProxyGraph.scd"
		);
	}
}
