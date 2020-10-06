{ | modules, object |
	var font = Font.default.copy.size = 24;
	var drawingList = List.new;
	var window = Window.new(
		name: object.class.name.asString++": "
		++object.moduleSet.asString,
		bounds: Rect(
			0,
			Window.screenBounds.height,
			Window.screenBounds.width * 0.5 * 0.985,
			Window.screenBounds.height * 1 / 3
		),
		resizable: false
	);
	var top = window.view.bounds.height * 1 / 22;
	var height = window.view.bounds.height * 5 / 6;
	var initialPrompt = "Press next to start the piece";
	var clockPrompt = "Press next to advance.";

	var updateSection = {
		var i = object.sectionIndex;
		if(i < 0, {
			i = "\%";
			this.announce(initialPrompt);
			clockText.stringColor = Color.black;
			clockText.string = clockPrompt;
		});
		sectionLabel.string = "Section: "++i;
		if(pause.value==1, {
			pause.value = 0;
		})
	};

	var nextSection = {
		object.next;
		updateSection.value;
	};

	var previousSection = {
		object.previous;
		updateSection.value;
	};

	var left = Button(window,
		Rect(
			0,
			top,
			window.view.bounds.width/16,
			height + 24;
		)
	).string_("<<").action_({previousSection.value}).font_(font);

	var right = Button(window,
		Rect(
			left: window.view.bounds.width - left.bounds.width,
			top: top,
			width: left.bounds.width,
			height: left.bounds.height
		)
	).string_(">>").action_({nextSection.value}).font_(font).resize_(3);

	var sectionLabel = StaticText.new(window,
		Rect(
			left.bounds.width * 2 + (left.bounds.width * 4 - 8 / 2),
			top,
			left.bounds.width * 4 - 8 * 2,
			height * 0.1
		)
	).string_("Section: %").align_(\center).font_(font);

	var prompter = StaticText.new(window,
		Rect(
			sectionLabel.bounds.left,
			sectionLabel.bounds.top+sectionLabel.bounds.height+8,
			sectionLabel.bounds.width,
			height * 0.6
		)
	).string_(initialPrompt).align_(\center).font_(font);

	var clockLabel = StaticText.new(window,
		Rect(
			prompter.bounds.left,
			prompter.bounds.top+prompter.bounds.height + 8,
			prompter.bounds.width * 1/2 - 4,
			height * 0.2
		)
	).string_("Time to next event: ").align_(\center).font_(font);

	var clockText = StaticText.new(window,
		Rect(
			prompter.bounds.left + clockLabel.bounds.width + 8,
			prompter.bounds.top+prompter.bounds.height + 8,
			prompter.bounds.width * 1 / 2 - 4,
			height * 0.2
		)
	).string_("0.00").align_(\center).font_(font);

	var pause = Button(window,
		Rect(
			prompter.bounds.left,
			clockText.bounds.top+clockText.bounds.height + 8,
			prompter.bounds.width / 2 - 4,
			height * 0.1
		)
	).states_([
		["Pause clock", Color.black, Color(0.5, 1.0, 0.75)],
		["Resume clock", Color.black, Color(1.0, 0.4, 0.6)]
	])
	.action_({
		| obj |
		case { obj.value==1 }{
			object.pause;
		} { obj.value==0 }{
			object.resume;
		};
	}).font_(font);

	var clearReset = Button(window,
		Rect(
			prompter.bounds.left+pause.bounds.left,
			clockText.bounds.top+clockText.bounds.height + 8,
			prompter.bounds.width / 2 - 4,
			height * 0.1
		)
	).states_([
		["Clear", Color.black, Color(0.5, 1.0, 0.75)],
		["Reset", Color.black, Color(1.0, 0.4, 0.6)]
	]).font_(font)
	.action_({ | obj |
		if(obj.value==1, {
			object.clear;
		}, {
			object.reset;
			updateSection.value;
		});
	});

	var server = try { object.server }{Server.default };

	var inputs = server.options.numInputBusChannels;
	var serverInputWidth = ServerMeterView.getWidth(inputs, 0);

	var serverInput = CompositeView.new(window,
		Rect(
			(sectionLabel.bounds.left - left.bounds.width) * 0.5
			+ left.bounds.width - (serverInputWidth * 0.5),
			top+(left.bounds.height / 2) - 115,
			serverInputWidth,
			230
		);
	);

	var outputs = server.options.numOutputBusChannels;
	var serverOutputWidth = ServerMeterView.getWidth(0, outputs);

	var serverOutput = CompositeView.new(window,
		Rect(
			(right.bounds.left - (sectionLabel.bounds.left + sectionLabel.bounds.width)) * 0.5
			+ (sectionLabel.bounds.left + sectionLabel.bounds.width) - (serverOutputWidth * 0.5),
			top+(right.bounds.height / 2) - 115,
			serverOutputWidth,
			230
		)
	);

	var currentColor = Color.black;

	ServerMeterView(
		try({ object.server }, { Server.default }),
		serverOutput,
		0@0,
		0,
		outputs
	);

	ServerMeterView(
		try({ object.server }, { Server.default }),
		serverInput,
		0@0,
		inputs,
		0
	);

	drawingList.add({
		Pen.use({
			Pen.color = Color.white;
			Pen.fillRect(sectionLabel.bounds);
		})
	});

	drawingList.add({
		Pen.use({
			Pen.color = Color.white;
			Pen.fillRect(clockText.bounds);
		})
	});

	drawingList.add({
		Pen.use({
			Pen.width = 2;
			window.view.children.do({ | item |
				Pen.strokeRect(item.bounds)
			});
			Pen.width_(8);
			Pen.strokeRect(window.view.bounds);
		});
	});

	drawingList.add({
		Pen.use({
			Pen.color = Color.white;
			Pen.fillRect(prompter.bounds)
		})
	});

	window.drawFunc = { drawingList.do(_.value) };

	window.onClose = { object.stop };

	object.onLoop = { | time |
		defer {
			clockText.string = time.round(object.loopDelta).asString;
			clockText.stringColor = Color.black;
			if(time < 5, { clockText.stringColor = Color.red });
		};
	};

	object.onLoopEnd = {
		Routine({
			updateSection.value(object.sectionIndex);
			clockText.stringColor = Color.black;
			0.1.wait;
			if(object.timeRemaining.isNil, {
				clockText.string = clockPrompt;
			})
		}).play(AppClock);
	};

	window.refresh;
	window.background = Color(0.4125, 0.8, 1.0);
	modules.add(\prompter -> prompter);
	window;
};