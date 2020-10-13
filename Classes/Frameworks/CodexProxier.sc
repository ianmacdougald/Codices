CodexProxier : CodexComposite {
	var <proxySpace, <sections;

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
		this.cleanup;
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
		^modules.keys.selectAs({ | key |
			modules[key].isFunction;
		}, Array).reverse;
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

	clear {
		proxySpace.clear;
		this.cleanup;
	}

	cleanup {
		if(modules.cleanup.isEmpty.not, {
			modules.cleanup.do(_.value);
			modules.cleanup.clear;
		});
	}

	moduleSet_{ | newSet, from |
		this.clear;
		super.moduleSet_(newSet, from);
	}
}