s.options.numBuffers = 16000;
s.options.memSize = 655360;
s.boot;
s.freqscope;
s.plotTree;
s.scope;


// xbox globals

~xLTHUMBX = 0.0;
~xLTHUMBY = 0.0;
~xRTHUMBX = 0.0;
~xRTHUMBY = 0.0;
~xRTRIGGER = 0.0;
~xLTRIGGER = 0.0;
~xA = 0.0;
~xB = 0.0;
~xX = 0.0;
~xY = 0.0;
~xLB = 0.0;
~xRB = 0.0;
~xBACK = 0.0;
~xSTART = 0.0;
~xXBOX = 0.0;
~xLEFTTHUMB = 0.0;
~xRIGHTTHUMB = 0.0;
~xDPADX = 0.0;
~xDPADY = 0.0;

// convert an OSC message to our globals

~msgsetter = { |msg|
	~xLTHUMBX = msg[ 1 ];
	~xLTHUMBY = msg[ 2 ];
	~xRTHUMBX = msg[ 3 ];
	~xRTHUMBY = msg[ 4 ]; 
	~xRTRIGGER = msg[ 5 ];
	~xLTRIGGER = msg[ 6 ];
	~xA = msg[ 7 ]; 
	~xB = msg[ 8 ];
	~xX = msg[ 9 ];
	~xY = msg[ 10 ]; 
	~xLB = msg[ 11 ]; 
	~xRB = msg[ 12 ]; 
	~xBACK = msg[ 13 ];
	~xSTART = msg[ 14 ]; 
	~xXBOX = msg[ 15 ];
	~xLEFTTHUMB = msg[ 16 ];
	~xRIGHTTHUMB = msg[ 17 ];
	~xDPADX = msg[ 18 ];
	~xDPADY = msg[ 19 ];
};

// osc listener

~listener = {
	|msg|
	var move,nmove,xbs;
	//var xLTHUMBX ,xLTHUMBY ,xRTHUMBX ,xRTHUMBY ,xRTRIGGER ,xLTRIGGER ,xA ,xB ,xX ,xY ,xLB ,xRB ,xBACK ,xSTART ,xXBOX ,xLEFTTHUMB ,xRIGHTTHUMB ,xDPADX, xDPADY;
	//msg.postln;
	~msgsetter.(msg);
	xbs = ~xbsyms.(msg);
	//[\xbs,xbs].postln;
	nmove = ~moveemitter.(xbs);
	//if(nmove!="",{nmove.postln},{});
	~moveresponder.( nmove  );

};

// move patterns

~fireballpat = ["_D____","FD____","F_A___" ];
~fireballpat2 = ["_D____","FD____","F_____","F_A___" ];
~dragonpunchpat = ["F_____","_D____","FDA___" ];
~dragonpunchpat2 = ["F_____","_D____","FD____","FDA___" ];
~dragonpunchpat3 = ["F_____","FD____","_D____","FDA___" ];
~dragonpunchpat4 = ["F_____","FD____","_D____","FD____","FDA___" ];
~whirlwindpat = ["_D____","BD____","B__B__" ];
~whirlwindpat2 = ["_D____","BD____","B_____","B__B__" ];
~sonic1 = ["B_____","F.____","F.A___"];
~sonic11 =    ["B_____","BU____","F_____","F_A___"];
~sonic12 =    ["B_____","BU____","F_A___"];
~sonic13 =    ["B_____","BU____","FU____","F_A___"];
~sonic14 =    ["B_____","BU____","FU____","F_____","F_A___"];
~sonic2 =     ["B_____","F_A___"];
~flashkick1 = ["_D____","_U____","_U_B__"];
~flashkick2 = ["_D____","_U_B__"];


~patterns = [
	['fireball',~fireballpat],
	['fireball',~fireballpat2],
	['dragonpunch',["F_____",".D____","FDA___" ]],
	['dragonpunch',~dragonpunchpat],
	['dragonpunch',~dragonpunchpat2],
	['dragonpunch',~dragonpunchpat3],
	['dragonpunch',~dragonpunchpat4],
	['whirlwind',~whirlwindpat],
	['whirlwind',~whirlwindpat2],
	['sonic',~sonic1],
	['sonic',~sonic11],
	['sonic',~sonic12],
	['sonic',~sonic13],
	['sonic',~sonic14],
	['sonic',~sonic2],
	['flashkick',~flashkick1],
	['flashkick',[".D____",".U____","_U_B__"]],
	['flashkick',~flashkick2]
];


~xbstr = {|msg|
	~msgsetter.(msg);
	~xbsyms.();
};
~xbsyms = {
	[
		if((~xLTHUMBX < 30)&&(~xLTHUMBX > -30),"_",
			if(~xLTHUMBX > 0,"F","B")),
		if((~xLTHUMBY < 30)&&(~xLTHUMBY > -30),"_",
			if(~xLTHUMBY > 0,"U","D")),
		if(~xA == 0,"_","A"),
		if(~xB == 0,"_","B"),
		if(~xX == 0,"_","X"),
		if(~xY == 0,"_","Y")
	].join;
};

~compilepat = {
	|pat|
	pat.collect{|str| ["(",str,")"].join }.join("+")
};

~mkemitter = {|assoc|	
	var func,state,pats;
	pats = assoc.collect {|v| [v[0],~compilepat.(v[1])] };
	state = List.new();
	func = {
		|sym|
		var str,emit;
		if(sym=="______",{""},{
			state.add(sym);
			str = state.join;
			emit = "";
			//str.postln;
			//[\sym, sym].postln;
			pats.collect { |v|
				if(v[1].matchRegexp(str),{
					emit = v[0];
					state = List.new();
				})
			};			
			emit
		})
	};
	func
};

~moveemitter = ~mkemitter.(  ~patterns );


// please implement a dictionary ~moveresponses of string -> (move -> void)

~moveresponder = {
	|move|
	var f = ~moveresponses.atFail(move,{});
	f.(move);
};


fork {
	loop {
		~looper.();
		(0.01).wait;
	}
};

OSCFunc.newMatching(~listener, '/xbox');

// Instrument the moveresponder

~fireballresponse = {
	var synth = Synth(\hydro4);
	synth.release(10);
};


~moveresponses = ('fireball':{|move| move.postln; ~fireballresponse.();  },
	'dragonpunch':{	
		|move|
		var synth = Synth(\hydro4,[\freq,1024]);
		move.postln;
		synth.release(10);
		~looper = ~dragonpunchlooper;
	},
	'whirlwind':{	
		|move|
		var synth = Synth(\hydro4,[\freq,10000]);
		move.postln;
		synth.release(10);
		~looper = ~whirlwindlooper;
	},
	'sonic':{	
		|move|
		var synth = Synth(\hydro4,[\freq,120]);
		move.postln;
		synth.release(2);
		~looper = ~soniclooper;
	},
	'flashkick':{	
		|move|
		var synth = Synth(\hydro4,[\freq,40]);
		move.postln;
		synth.release(5);
		~looper = ~flashkicklooper;
	}

);

// set up our looper to respond to xbox inputs continuously

~looper = {
	if(~xLTHUMBX != 0.0,{ ~xhz = (~xLTHUMBX/101.0) + ~xhz; ~x1.set(\freq,~xhz) });
	if(~xRTHUMBX != 0.0,{ ~yhz = (~xRTHUMBX/101.0) + ~yhz; ~y1.set(\freq,~yhz) });
	if(~xLTHUMBY != 0.0,{ ~x2hz = (~xLTHUMBY/101.0) + ~x2hz; ~x2.set(\freq,~x2hz) });
	if(~xRTHUMBY != 0.0,{ ~y2hz = (~xRTHUMBY/101.0) + ~y2hz; ~y2.set(\freq,~y2hz) });

	~rnoise.set(\amp,~xRTRIGGER/100.0);
	~lnoise.set(\amp,~xLTRIGGER/100.0);
	~lnoise.set(\freq,~xhz/10);
	~rnoise.set(\freq,~yhz/10);
	~anoise.set(\amp,~xA);
	~bnoise.set(\amp,~xB);
	~xnoise.set(\amp,~xX);
	~ynoise.set(\amp,~xY);
	~rbnoise.set(\amp,~xRB);
	~lbnoise.set(\amp,~xLB);
	~backnoise.set(\amp,~xBACK);
	~startnoise.set(\amp,~xSTART);
	~ltnoise.set(\amp,~xLEFTTHUMB);
	~rtnoise.set(\amp,~xRIGHTTHUMB);

	~h21.set(\amp,if(~xDPADX==1,1,0));
	~h22.set(\amp,if(~xDPADX== -1,1,0));
	~h23.set(\amp,if(~xDPADY==1,1,0));
	~h24.set(\amp,if(~xDPADY== -1,1,0));

};

~flashkicklooper = ~soniclooper = ~whirlwindlooper = ~dragonpunchlooper = ~looper;


// Set up our instruments

SynthDef(\noise, 
        { arg out = 0, amp=0,freq=800, sustain=0.001; 
                Out.ar(out, // write to output bus
					amp*Ringz.ar(PinkNoise.ar(0.1), freq, 2.6), // filtered noise
				)

        }
).add;



~lnoise = Synth(\noise,[\amp,0,\out,0]);
~lnoise.set(\amp,0.1)
~lnoise.set(\freq,440)





