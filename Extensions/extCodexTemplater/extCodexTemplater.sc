+ CodexTemplater {

	patternFunction { | templateName("patternFunction") |
		var path = Main.packages.asDict.at('CodexIan')
		+/+"Extensions/extCodexTemplater";
		this.makeExtTemplate(
			path,
			templateName,
			"patternFunction",
		);
	}

}
