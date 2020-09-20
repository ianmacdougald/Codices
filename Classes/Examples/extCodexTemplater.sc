+ CodexTemplater {
	hybridExample_function { | templateName("sequence") |
		var path = Main.packages.asDict.at(\CodexIan)
		+/+"Classes/Examples/hybridExample_function.scd";
		this.makeTemplate(
			templateName,
			path
		);
	}
}
