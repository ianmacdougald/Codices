CodexProxier : CodexComposite {
	var <proxySpace;

	*makeTemplates { | templater |
		templater.list("cleanup");
		this.sectionTemplate(templater);
		this.otherTemplates(templater);
	}

	*sectionTemplate { | templater |
		templater.codexProxier_section("section0");
	}

	*otherTemplates { | templater | }

	initComposite {
		proxySpace = ProxySpace.new(Server.default);
		this.initProxier;
	}

	initProxier { }

	server { ^proxySpace.server }

	server_{ | newServer |
		proxySpace.clear;
		this.proxySpace = ProxySpace(newServer);
	}

	proxySpace_{ | newProxySpace |
		if(newProxySpace.isKindOf(ProxySpace), {
			proxySpace = newProxySpace;
		});
	}

	clear { proxySpace.clear }
}

CodexVarProxier : CodexProxier { 
	var nSections, twoFails = false;

	initComposite { 
		proxySpace = ProxySpace.new(Server.default);
		nSections = this.countSections;
		if(nSections==0, { 
			if(twoFails.not, {  
				this.makeSection; 
				this.reloadScripts;
				twoFails = true;
			}, { warn("Something went really wrong. Make sure there aren't a silly number of modules made"); });
		
		}); 
	}
	
	makeSection { 
		this.class.sectionTemplate(
			CodexTemplater(this.moduleFolder);
		);
	}

	countSections { 
		^modules.keys.select({ | key |
			key.asString.find("section").notNil
		}).size;
	}
}

CodexVarTester : CodexVarProxier {  }

/*CodexFixedProxier : CodexProxier { 
	*nSections { ^nil }


}*/
