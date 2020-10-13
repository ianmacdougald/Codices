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
	var <sections;

	initComposite {
		proxySpace = ProxySpace.new(Server.default);
		sections = this.collectSections;
		this.initProxier;
	}

	makeSection {
		this.class.sectionTemplate(
			CodexTemplater(this.moduleFolder);
		);
	}

	collectSections {
		^modules.keys.select({ | key |
			modules[key].isFunction;
		}).asArray.reverse;
	}
}

CodexFixedProxier : CodexProxier {
	*nSections { ^nil }

	*makeTemplates { | templater |
		super.makeTemplates(templater);
		if(this.nSections.notNil, {
			(this.nSections - 1).do {
				this.sectionTemplate(templater);
			};
		});
	}
}

Codex2Proxier : CodexFixedProxier {
	*nSections { ^2 }
}

Codex4Proxier : CodexFixedProxier {
	*nSections { ^4 }
}

Codex8Proxier : CodexFixedProxier {
	*nSections { ^8 }
}
