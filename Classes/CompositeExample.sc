CompositeExample : CodexComposite {
	var player;
	
	//initComposite is called immediately after modules are loaded into the class. 
	//Initialize instance variables here if you don't want to rewrite the constructor.
	initComposite {}

	*makeTemplates { | templater | 
		templater.pattern( "sequence" );
	}

	play { 
		player ?? {
			player = modules.sequence.play;
		};
	}

	stop { 
		player !? { 
			player.stop; 
			player = nil;
		}
	}
}
