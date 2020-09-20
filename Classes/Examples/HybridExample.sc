HybridExample : CodexHybrid {
	var player;

	*contribute { | versions |
		var toQuark = Main.packages.asDict.at(\CodexIan);
		var toExample = toQuark+/+"Classes/Examples/example_modules";

		versions.add(
			[\example, toExample]
		);
	}

	initHybrid {}

	*makeTemplates { | templater |
		templater.hybridExample_function( "sequence" );
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
