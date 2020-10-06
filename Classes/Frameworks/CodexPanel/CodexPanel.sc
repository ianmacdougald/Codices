CodexPanel : CodexComposite {
	var <codexObject, <window;
	var <>inputs = 2, <>outputs = 2;

	*contribute { | versions |
		var path = Main.packages.asDict.at(\CodexIan)
		+/+"Classes/Frameworks/CodexPanel";

		versions.add(
			[\ian_sonata, path+/+"ian_sonata"]
		);
	}

	*makeTemplates { | templater |
		templater.codexPanel_function("function");
	}

	connectTo { | newObject |
		if(newObject.isKindOf(CodexComposite), {
			codexObject = newObject;
			this.build;
		}, { Error("Can only connect to object of type CodexComposite").throw });
	}

	alwaysOnTop { ^window.alwaysOnTop }

	alwaysOnTop_{ | bool(false) |
		window.alwaysOnTop = bool;
	}

	build {
		codexObject !? {
			window = modules.function(codexObject);
			window.front;
		};
	}

	close {
		window !? { window.close };
	}

	moduleSet_{ | newSet, from |
		var bool = this.alwaysOnTop;
		this.close;
		super.moduleSet_(newSet, from);
		this.build;
		this.alwaysOnTop = bool;
	}
}
