CodexProxyGraph : CodexComposite {
	var <nodes, <cleanup_list, <server;

	*makeTemplates { | templater |
		templater.codexProxyGraph("graph");
	}

	initComposite {
		cleanup_list ?? {
			cleanup_list = List.new;
		} !? { this.free };
		nodes = CodexModules.new;
		modules.keysValuesDo({ | key, value |
			if(key!=\graph){
				case
				{ value.isFunction }
				{
					var node = NodeProxy.new.source_(value);
					nodes.add(key -> node);
					cleanup_list.add({ node.release });
				}
				{ value.isKindOf(NodeProxy) }
				{
					nodes.add(key -> value);
					cleanup_list.add({ value.release });
				};
			};
		});
		this.makeGraph;
	}

	makeGraph {
		modules[\graph].value(nodes, cleanup_list);
	}

	free {
		cleanup_list.do(_.value);
		cleanup_list.clear;
	}

	storeNode { | key, node |
		var source;
		case
		{ node.isFunction }{
			source = node.def.sourceCode;
		}
		{ node.isKindOf(NodeProxy) }
		{
			source = node.asCode;
		};

		if(source.notNil and: { source.find("open Function").isNil })
		{
			var path = this.moduleFolder+/+key++".scd";
			var file = File.open(path, "w");
			file.write(source);
			file.close;
		}
		//else
		{
			"Could not store new module. Make sure all functions are closed".warn;
		};
		this.reloadScripts;
	}
}
