CompositeExample : CodexComposite {
	var player;
	
	//initComposite is called immediately after modules are loaded into the class. 
	//Initialize instance variables here if you don't want to rewrite the constructor.
	initComposite {}

	//The only actual requirement for developing CodexComposite-typed classes is to define the modules that make up the composite.
	//This is done in the method makeTemplater. CodexComposite holds an instance of Templater called templater for this purpose.
	//If a specific template is not available, extend Templater to make it.
	*makeTemplates { | templater | 
		templater.pattern( "sequence" );
	}

	//Here is an example of how to interact with the modules. 
	//The class expects that the collection of modules will have an item \sequence because it was specified by the templater. 
	//Because it expects that that item is a pattern, it can have .play called on it. 
	//It can, therefore, also be stopped.
	play { 
		player = modules.sequence.play;
	}

	stop { 
		player !? { 
			player.stop; 
			player = nil;
		}
	}

}
