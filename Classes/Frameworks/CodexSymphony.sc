CodexSymphony {
	var <movements, player, <pattern, <proxySpace;

	*new { |...movements | ^super.new.movements_(movements.flat).initSchmooper }

	movements_{ | newSections |
		movements = newSections.select(_.isKindOf(SchmooperSection)).as(List);
		pattern = this.makePattern(movements);
	}

	initSchmooper {
		this.proxySpace = ProxySpace.new(Server.default);
	}

	makePattern { | array(movements) | ^Pseq(array.collect(_.getPattern), 1) }

	reset {
		movements.do(_.cleanup);
		proxySpace.clear;
		pattern = this.makePattern(movements);
	}

	addSection { | index(movements.size), newSection |
		newSection.proxySpace = proxySpace;
		movements.insert(index, newSection);
		this.reset;
	}

	replaceSection { | index, newSection |
		this.addSection(index, newSection);
		this.removeSection(index + 1);
		this.reset;
	}

	removeSection { | index |
		movements.removeAt(index);
		this.reset;
	}

	proxySpace_{ | newProxySpace |
		if(newProxySpace.isKindOf(ProxySpace), {
			proxySpace = newProxySpace;
			movements.do({ | movement | movement.proxySpace = proxySpace });
		});
	}

	server { ^proxySpace.server }

	server_{ | newServer |
		proxySpace !? { proxySpace.clear };
		this.proxySpace = ProxySpace(newServer);
	}

	isPlaying { ^player.isPlaying }

	play {
		if(this.isPlaying, { this.stop });
		player = pattern.play(proxySpace.clock ? TempoClock.default, ());
	}

	playSections { | indicies(movements) |
		pattern = this.makePattern(indicies.collect({ | i | movements[i] }));
		this.play;
	}

	stop {
		if(this.isPlaying, { player.stop });
		this.reset;
	}
}

CodexMovement : CodexComposite {
	var <>proxySpace;

	*symphony { CodexSymphony }

	*classFolder { ^(
		this.directory
		+/+this.symphony.asString
		+/+this.name.asString
	) }

	*n_movements { ^nil }

	*makeTemplates { | templater |
		templater.list("cleanup");
		this.n_movements.do { | i |
			templater.codex_movement("movement"++i);
		};
	}

	getSections { 
		^Pseq(this.class.n_movements.collect({ | i |
			modules[("movement"++i).asSymbol];
		}));
	}

	initComposite { }

	getPattern {
		^Pbind(
			\dur, p {
				//the movement stream should be a pseq that returns functions
				//that take in the proxy space as an argument and returns
				//the duration of the subevent.
				this.getSections.asStream.do { | function |
					function.value(modules, proxySpace).yield;
				};
				this.cleanup;
			}
		)
	}

	cleanup {
		//proxySpace.do(_.release);
		modules.cleanup.do(_.value).clear;
	}
}
