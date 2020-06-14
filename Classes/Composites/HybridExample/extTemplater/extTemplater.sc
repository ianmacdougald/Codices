+ Templater { 

	patternFunction { | templateName("patternFunction") | 
		this.setTemplateDir = PathName(this.filenameString).pathOnly+/+"templates";
		this.makeTemplate(templateName, "patternFunction"); 
		this.resetTemplateDir;
	}

}
