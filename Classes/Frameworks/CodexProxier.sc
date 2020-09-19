CodexProxier : CodexComposite {
	var <proxySpace;

	*nSection { ^nil }

	*makeTemplates { | templater |
		templater.list("cleanup");
		this.nSections.do{ this.sectionTemplate(templater) };
		this.otherTemplates(templater);
	}

	*sectionTemplate { | templater |
		templater.codexSonata_section("section0");
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

	*getModules { | set, from |
		var nSections = this.nSections;
		if(nSections.notNil and: { nSections!=this }, {
			super.getModules(set, from);
		}, {
			Error("No sections specified. Modules can't be loaded").throw;
		});
	}

}
