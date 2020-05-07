+ Array{

	*minMax{|arr|
		var output, envir;
		var n = arr.size;
		var makeMinMax = {|min, max|
			(min: min, max: max);
		};

		if(n==1){
			envir = makeMinMax.value(arr[0], arr[0]);
		}/*ELSE*/{
			var i = 2;
			if(arr[0] > arr[1]){
				envir = makeMinMax.value(arr[1], arr[0]);
			}/*ELSE*/{
				envir = makeMinMax.value(arr[0], arr[1]);
			};


			while({i < n}, {
				if(arr[i] > envir.max){
					envir.max = arr[i];
				};

				if(arr[i] < envir.min){
					envir.min = arr[i];
				};

				i = i + 1;
			})
		};

		output = envir;
		^output;
	}

	min{
		var minMax = this.class.minMax(this);
		^minMax.min;
	}

	max{
		var minMax = this.class.minMax(this);
		^minMax.max;
	}

	minMax{
		var return = this.class.minMax(this);
		^return;
	}

}