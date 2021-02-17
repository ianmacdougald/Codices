+ CodexTemplater {
	morePath {
		^(Main.packages.asDict
			.at(\CodexIanMore)+/+"Templates");
	}

	proxierSection { | templateName("section") |
		this.makeTemplate(
			templateName,
			this.morePath
			+/+"proxierSection.scd"
		);
	}

	instrumentSynthDef { | templateName("synthDef") |
		this.makeTemplate(
			templateName,
			this.morePath
			+/+"instrumentSynthDef.scd"
		);
	}

	panelFunction { | templateName("function") |
		this.makeTemplate(
			templateName,
			this.morePath
			+/+"panelFunction.scd"
		);
	}

	panel { | templateName("panel") |
		this.makeTemplate(
			templateName,
			this.morePath
			+/+"panel.scd"
		);
	}

	proxyGraph { | templateName("graph") |
		this.makeTemplate(
			templateName,
			this.morePath
			+/+"proxyGraph.scd"
		);
	}

	guiPath {
		^(thisMethod.filenameString.dirname
			+/+"Templates"+/+"CodexGuiKit");
	}

	knob { | templateName("knob") |
		this.makeTemplate(templateName, this.guiPath+/+"knob.scd");
	}

	labeledKnob { | templateName("labeledKnob") |
		this.makeTemplate(templateName, this.guiPath+/+"labeledKnob.scd");
	}

	staticText { | templateName("staticText") |
		this.makeTemplate(templateName, this.guiPath+/+"staticText.scd");
	}

	numberBox { | templateName("numberBox") |
		this.makeTemplate(templateName, this.guiPath+/+"numberBox.scd");
	}

	slider { | templateName("slider") |
		this.makeTemplate(templateName, this.guiPath+/+"slider.scd");
	}

	labeledSlider { | templateName("labeledSlider") |
		this.makeTemplate(templateName, this.guiPath+/+"labeledSlider.scd");
	}

	button { | templateName("button") |
		this.makeTemplate(templateName, this.guiPath+/+"button.scd");
	}
}
