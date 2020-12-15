CodexSonataViewer : CodexSonata {

	*otherTemplates { | templater |
		templater.codexPanel("panel");
	}

	initProxier {
		modules.panel.connectTo(this)
		.alwaysOnTop_(true);
	}

	clear {
		super.clear;
		this.reset;
	}

	close {
		this.stop;
		if(modules.panel.window.isClosed.not, {
			modules.panel.window.close;
			this.clear;
		});
	}

	moduleSet_{ | newSet, from |
		this.close;
		super.moduleSet_(newSet, from);
	}

}