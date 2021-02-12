CodexEnvir : CodexComposite {
	var <envir, cleanup;

	initCodex { | from |
		cleanup ?? { cleanup = List.new };
		envir !? { this.cleanupEnvir }  ?? {
			envir = Environment.new
		};
		envir.use({
			this.loadModules(from).initComposite;
		});
	}

	cleanupEnvir {
		cleanup.do(_.value);
		envir.keys.asArray.do { | key |
			try { envir.removeAt(key).free };
		};
	}
}