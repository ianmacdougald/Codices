+ CodexTemplater {
	morePath {
		^(Main.packages.asDict
			.at(\CodexIanMore)+/+"Templates");
	}

	proxierSection { | templateName("section") |
		this.makeTemplate(
			templateName,
			this.morePath
			+/+"codexProxierSection.scd"
		);
	}

	instrumentSynthDef { | templateName("synthDef") |
		this.makeTemplate(
			templateName,
			this.morePath
			+/+"codexInstrumentSynthDef.scd"
		);
	}

	panelFunction { | templateName("function") |
		this.makeTemplate(
			templateName,
			this.morePath
			+/+"codexPanelFunction.scd"
		);
	}

	panel { | templateName("panel") |
		this.makeTemplate(
			templateName,
			this.morePath
			+/+"codexPanel.scd"
		);
	}

	proxyGraph { | templateName("graph") |
		this.makeTemplate(
			templateName,
			this.morePath
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
