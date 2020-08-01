+ CodexTemplater {
	patternFunction { | templateName("patternFunction") |
		this.makeTemplate(templateName,
			thisMethod.filenameString.dirname+/+"patternFunction.scd")
	}
}
