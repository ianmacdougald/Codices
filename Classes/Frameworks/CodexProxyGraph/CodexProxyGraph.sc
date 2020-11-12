CodexProxyGraph : CodexComposite {
	var <nodes, <cleanup_list, <server;

	*makeTemplates { | templater |
		templater.codex_proxyGraph("graph");
	}

	initComposite {
		nodes = ();
		modules.keysValuesDo({ | key, value |
			if(key!=\graph){
				case
				{ value.isFunction }
				{
					var node = NodeProxy.new.source_(value);
					nodes.add(key -> node);
					cleanup_list.add({ node.clear });
				}
				{ value.isKindOf(NodeProxy) }
				{
					nodes.add(key -> value);
					cleanup_list.add({ value.clear });
				};
			};
		});
		this.makeGraph;
	}

	makeGraph {
		modules.graph(nodes, cleanup_list);
	}

	free {
		cleanup_list.do(_.value);
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
		}

	}
}
