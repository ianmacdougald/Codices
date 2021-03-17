+ CodexTemplater {
	morePath {
		^(Main.packages.asDict
			.at(\CodicesMore)+/+"Templates");
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

	nodeGraph { | templateName("graph") |
		this.makeTemplate(
			templateName,
			this.morePath
			+/+"nodeGraph.scd"
		);
	}

	knob { | templateName("knob") |
		this.makeTemplate(templateName, this.morePath+/+"knob.scd");
	}

	labeledKnob { | templateName("labeledKnob") |
		this.makeTemplate(templateName, this.morePath+/+"labeledKnob.scd");
	}

	staticText { | templateName("staticText") |
		this.makeTemplate(templateName, this.morePath+/+"staticText.scd");
	}

	numberBox { | templateName("numberBox") |
		this.makeTemplate(templateName, this.morePath+/+"numberBox.scd");
	}

	slider { | templateName("slider") |
		this.makeTemplate(templateName, this.morePath+/+"slider.scd");
	}

	labeledSlider { | templateName("labeledSlider") |
		this.makeTemplate(templateName, this.morePath+/+"labeledSlider.scd");
	}

	button { | templateName("button") |
		this.makeTemplate(templateName, this.morePath+/+"button.scd");
	}
}
