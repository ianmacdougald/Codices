+ Templater { 

	patternFunction { | templateName("patternFunction") | 
		var path = Main.packages.asDict.at('CodexIan')
		+/+"Extensions/extTemplater";
		this.makeExtTemplate(
			path,
			templateName, 
			"patternFunction", 
		);
	}

}
