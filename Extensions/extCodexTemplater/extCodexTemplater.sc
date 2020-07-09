+ CodexTemplater {

	patternFunction { | templateName("patternFunction") |

		this.makeExtTemplate(
			templateName,
			"patternFunction",
			Main.packages.asDict.at('CodexIan')
			+/+"Extensions/extCodexTemplater",
		);

	}

}
