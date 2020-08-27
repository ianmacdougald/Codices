HybridExample : CodexHybrid {
	var player;

	*contribute { | versions |
		versions.add(
			[\example, this.filenameString.dirname+/+"Modules"]
		);
	}

	initHybrid {}

	*makeTemplates { | templater |
		templater.patternFunction( "sequence" );
		templater.synthDef( "synthDef" );
	}

	play {
		if(player.isPlaying.not, {
			player = modules.sequence.play;
		});
	}

	stop {
		if(player.isPlaying, {
			player.stop;
		});
	}
}
