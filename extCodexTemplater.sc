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

	guiPath {
		^(thisMethod.filenameString.dirname
			+/+"Templates"+/+"CodexGuiKit");
	}

	knob { | templateName("knob") |
		this.makeTemplate(templateName, this.guiPath+/+"codexGuiKnob.scd");
	}

	labeledKnob { | templateName("labeledKnob") |
		this.makeTemplate(templateName, this.guiPath+/+"codexGuiLabeledKnob.scd");
	}

	staticText { | templateName("staticText") |
		this.makeTemplate(templateName, this.guiPath+/+"codexGuiStaticText.scd");
	}

	numberBox { | templateName("numberBox") |
		this.makeTemplate(templateName, this.guiPath+/+"codexGuiNumberBox.scd");
	}

	slider { | templateName("slider") |
		this.makeTemplate(templateName, this.guiPath+/+"codexGuiSlider.scd");
	}

	labeledSlider { | templateName("labeledSlider") |
		this.makeTemplate(templateName, this.guiPath+/+"codexGuiLabeledSlider.scd");
	}

	button { | templateName("button") |
		this.makeTemplate(templateName, this.guiPath+/+"codexGuiButton.scd");
	}
}
