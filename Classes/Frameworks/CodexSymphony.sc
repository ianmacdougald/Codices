CodexSymphony {
	var <movements, player, <pattern, <proxySpace;

	*new { |...movements |
		^super.new
		.movements_(movements.flat)
		.proxySpace_(ProxySpace(Server.default))
		.initSymphony
	}

	movements_{ | newMovements |
		movements = newMovements
		.select(_.isKindOf(SchmooperMovement))
		.as(List);
		pattern = this.makePattern(movements);
	}

	initSymphony {  }

	makePattern { | array(movements) |
		^Pseq(array.collect(_.getPattern), 1);
	}

	reset {
		movements.do(_.cleanup);
		proxySpace.clear;
		if(this.isPlaying)
		{
			this.stop;
		};
		pattern = this.makePattern(movements);
	}

	clear { proxySpace.clear }

	addMovement { | index(movements.size), newMovement |
		newMovement.proxySpace = proxySpace;
		movements.insert(index, newMovement);
		this.reset;
	}

	replaceMovement { | index, newMovement |
		this.addMovement(index, newMovement);
		this.removeMovement(index + 1);
		this.reset;
	}

	removeMovement { | index |
		movements.removeAt(index);
		this.reset;
	}

	proxySpace_{ | newProxySpace |
		if(newProxySpace.isKindOf(ProxySpace), {
			proxySpace = newProxySpace;
			movements.do({ | movement |
				movement.proxySpace = proxySpace;
			});
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

	playMovements { | indicies(movements) |
		pattern = this.makePattern(
			indicies.collect({ | i | movements[i] })
		);
		this.play;
	}

	stop {
		if(this.isPlaying, { player.stop });
		this.reset;
	}
}

CodexMovement : CodexComposite {
	var <>proxySpace;

	*symphony { ^nil }

	*classFolder {
		var subfolder = if(this.symphony.isNil, { "" }, {
			this.symphony.asString});
		^this.directory+/+subfolder
		+/+this.name.asString;
	}

	*nSections { ^nil }

	*makeTemplates { | templater |
		templater.list("cleanup");
		this.nSections.do { | i |
			templater.codexMovement_section("section"++i);
		};
	}

	getMovements {
		^Pseq(this.class.nSections.collect({ | i |
			modules[("section"++i).asSymbol];
		}));
	}

	initComposite { }

	getPattern {
		^Pbind(
			\dur, p {
				//the movement stream should be a pseq that returns functions
				//that take in the proxy space as an argument and returns
				//the duration of the subevent.
				this.getMovements.asStream.do { | function |
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
