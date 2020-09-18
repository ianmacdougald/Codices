CodexSonata : CodexComposite {
	var <proxySpace, <sectionIndex = -1;
	var <task, <timeRemaining;
	var onLoop, onLoopEnd;
	var <>taskDelta = 0.1;

	*nSections { ^nil }

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
		this.initSonata;
	}

	onLoop_{ | function |
		if(function.isFunction, { onLoop = function });
	}

	onLoopEnd_{ | function |
		if(function.isFunction, { onLoopEnd = function });
	}

	initSonata {  }

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

	next { this.sectionIndex = sectionIndex + 1 }

	previous { this.sectionIndex = sectionIndex - 1 }

	sectionIndex_{ | newIndex |
		this.stop;
		sectionIndex = newIndex;
		if(sectionIndex < this.class.nSections && sectionIndex >= 0, {
			this.engageTask(
				modules[("section"++sectionIndex).asSymbol]
				.value(modules, proxySpace)
			);
		}, {
			this.reset;
			sectionIndex = -1;
		});
	}

	engageTask { | duration |
		if(duration.isNumber, {
			task = Task({
				timeRemaining = duration;
				while({ timeRemaining > 0 }, {
					onLoop.value(timeRemaining);
					timeRemaining = (timeRemaining - taskDelta)
					.clip(0, duration);
					taskDelta.wait;
				});
				timeRemaining = nil;
				onLoopEnd.value;
				fork { this.next };
			}).play;
		});
	}

	start {
		if(sectionIndex<0, { this.next });
	}

	stop {
		if(task.notNil, {
			if(task.isPlaying, {
				task.stop;
			});
			task = nil;
		});
	}

	pause {
		if(task.notNil and: { task.isPlaying }, {
			task.pause;
		});
	}

	resume {
		if(task.notNil and: { task.isPlaying.not }, {
			task.resume;
		});
	}

	clear { proxySpace.clear }

	reset {
		this.stop;
		sectionIndex = -1;
	}

	cleanup {
		modules.cleanup.do(_.value)
	}

	moduleSet_{ | newSet, from |
		this.reset;
		super.moduleSet_(newSet, from);
	}
}
