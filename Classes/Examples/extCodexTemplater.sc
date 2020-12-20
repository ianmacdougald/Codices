+ CodexTemplater {
	hybridExampleFunction { | templateName("sequence") |
		var path = Main.packages.asDict.at(\CodexIan)
		+/+"Classes/Examples/hybridExampleFunction.scd";
		this.makeTemplate(
			templateName,
			path
		);
	}
}
