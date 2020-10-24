CodexProxier : CodexComposite {
	var <proxySpace, <sections, <cleanup_list;

	*makeTemplates { | templater |
		this.sectionTemplate(templater);
		this.otherTemplates(templater);
	}

	*sectionTemplate { | templater |
		templater.codexProxier_section("section0");
	}

	*otherTemplates { | templater | }

	initComposite {
		proxySpace = ProxySpace.new(Server.default);
		sections = this.collectSections;
		cleanup_list !? { this.cleanup } ?? { cleanup_list = List.new };
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
		if(cleanup_list.isEmpty.not, {
			cleanup_list.do(_.value);
			cleanup_list.clear;
		});
	}

	moduleSet_{ | newSet, from |
		this.clear;
		super.moduleSet_(newSet, from);
	}

	engage { | ... sections |
		sections.do{ | item |
			var module = modules[item];
			if(module.notNil, {
				module.value(
					modules,
					proxySpace,
					cleanup_list
				);
			});
		};
	}
}