CodexSonata : CodexProxier {
	var <sectionIndex = -1;
	var <task, <timeRemaining;
	var <onLoop, <onLoopEnd;
	var <>loopDelta = 0.1;

	onLoop_{ | function |
		if(function.isFunction, { onLoop = function });
	}

	onLoopEnd_{ | function |
		if(function.isFunction, { onLoopEnd = function });
	}

	next { this.sectionIndex = sectionIndex + 1 }

	previous { this.sectionIndex = sectionIndex - 1 }

	sectionIndex_{ | newIndex |
		this.stop;
		if(newIndex < sections.size && newIndex >= 0, {
			sectionIndex = newIndex;
			this.engageTask(
				modules[sections[sectionIndex]]
				.value(
					modules,
					proxySpace
				);
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
					timeRemaining = (timeRemaining - loopDelta)
					.clip(0, duration);
					loopDelta.wait;
				});
				timeRemaining = nil;
				onLoopEnd.value;
				fork { this.next };
			}).play;
		}, {
			timeRemaining = nil;
			onLoop.value(timeRemaining);
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

	isPlaying { ^task.isPlaying }

	reset {
		this.stop;
		sectionIndex = -1;
	}

	clear {
		this.reset;
		super.clear;
	}
}
