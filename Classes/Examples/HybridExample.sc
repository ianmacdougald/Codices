HybridExample : CodexHybrid {
	var player;

	*contribute { | versions |
		var modulesPath = Main.packages.asDict.at(\CodexIan)
		+/+"Classes/Examples/example_modules";
		versions.add(
			[\example, modulesPath]
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
