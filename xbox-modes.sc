s.options.numBuffers = 16000;
s.options.memSize = 655360;
s.boot;
//s.freqscope;
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


(s.makeBundle(nil,{
// Set up our instruments

SynthDef(\noise, 
        { arg out = 0, amp=0,freq=800, sustain=0.001; 
                Out.ar(out, // write to output bus
					amp*Ringz.ar(PinkNoise.ar(0.1), freq, 2.6), // filtered noise
				)

        }
).add;

SynthDef(\grainer,{ 
	arg out=0,b,rho=1.0,theta=0.0,amp=0.4,pitchoff=0.3,graindur=0.1,trig=1;
	Out.ar(out,
		TGrains.ar(2, Dust.ar(40)*trig, b,
			pitchoff+rho,
			BufDur.kr(b)*theta.linlin(-3.14,3.14,0,1.0),
			graindur,
			WhiteNoise.kr(0.6), 
			amp)
	)
}).add;
SynthDef(\mouser,{
	|theta,rho|
	var x,y,rhov,thetav;
	y = MouseY.kr(-1,1,0);
	x = MouseX.kr(-1,1,0);
	rhov = hypot(x,y);
	thetav = atan2(y,x);
	Out.kr(theta,thetav);
	Out.kr(rho,rhov);
}).add;
SynthDef(\thetarho,{
	arg theta,rho,x=0,y=0,div=1;
	var rhov,thetav;
	x = x / div;
	y = y / div;
	rhov = hypot(x,y);
	thetav = atan2(y,x);
	Out.kr(theta,thetav);
	Out.kr(rho,rhov);
}).add;

~xb = Buffer.readChannel(s, "sounds-rand-tux-27975.wav", channels: [0]);
~yb = Buffer.readChannel(s, "Sabatini-Scaramouche-short.wav", channels: [0]);
~ab = Buffer.read(s, "SPO256-AL2.wav");
~bb = Buffer.readChannel(s, "idlevocals.wav", channels: [0]);

~rxb = Buffer.readChannel(s, "peergynt1.wav", channels: [0]);
~ryb = Buffer.readChannel(s, "peergynt2.wav", channels: [0]);
~rab = Buffer.readChannel(s, "peergynt3.wav", channels: [0]);
~rbb = Buffer.readChannel(s, "peergynt4.wav", channels: [0]);


s.sync;

~lnoise = Synth(\noise,[\amp,0,\out,0]);
~lnoise.set(\amp,0.0);
~lnoise.set(\freq,440);


~rnoise = Synth(\noise,[\amp,0,\out,1]);
~rnoise.set(\amp,0.0);
~rnoise.set(\freq,440);


// Buses for abstractions of sticks
~lrho = Bus.control(s).set(0);
~ltheta = Bus.control(s).set(0);
~rrho = Bus.control(s).set(0);
~rtheta = Bus.control(s).set(0);

// Buses for buttons
~xbb = Bus.control(s).set(0);
~ybb = Bus.control(s).set(0);
~abb = Bus.control(s).set(0);
~bbb = Bus.control(s).set(0);

// Make the grains and connect the maps

~xgrain = Synth(\grainer,[\b,~xb.bufnum, \trig, ~xbb.asMap, \rho, ~lrho.asMap, \theta, ~ltheta.asMap]);
~ygrain = Synth(\grainer,[\b,~yb.bufnum, \trig, ~ybb.asMap, \rho, ~lrho.asMap, \theta, ~ltheta.asMap]);
~agrain = Synth(\grainer,[\b,~ab.bufnum, \trig, ~abb.asMap, \rho, ~lrho.asMap, \theta, ~ltheta.asMap]);
~bgrain = Synth(\grainer,[\b,~bb.bufnum, \trig, ~bbb.asMap, \rho, ~lrho.asMap, \theta, ~ltheta.asMap]);

// Buses for sticks
~lthumbxb = Bus.control(s).set(0);
~lthumbyb = Bus.control(s).set(0);
~rthumbxb = Bus.control(s).set(0);
~rthumbyb = Bus.control(s).set(0);

// Right stick stuff

~rxgrain = Synth(\grainer,[\b,~rxb.bufnum, \trig, 0, \rho, ~rrho.asMap, \theta, ~rtheta.asMap]);
~rygrain = Synth(\grainer,[\b,~ryb.bufnum, \trig, 0, \rho, ~rrho.asMap, \theta, ~rtheta.asMap]);
~ragrain = Synth(\grainer,[\b,~rab.bufnum, \trig, 0, \rho, ~rrho.asMap, \theta, ~rtheta.asMap]);
~rbgrain = Synth(\grainer,[\b,~rbb.bufnum, \trig, 0, \rho, ~rrho.asMap, \theta, ~rtheta.asMap]);







~thetarhol = Synth(\thetarho,[\theta, ~ltheta, \rho, ~lrho,\div,100.0]);
~thetarhor = Synth(\thetarho,[\theta, ~rtheta, \rho, ~rrho,\div,100.0]);

~thetarhol.map(\x, ~lthumbxb);
~thetarhol.map(\y, ~lthumbyb);

~thetarhor.map(\x, ~rthumbxb);
~thetarhor.map(\y, ~rthumbyb);





// set up our looper to respond to xbox inputs continuously

~looper = {
	// use buses

	~abb.set(~xA);
	~bbb.set(~xB);
	~xbb.set(~xX);
	~ybb.set(~xY);
	~lthumbxb.set(~xLTHUMBX);
	~lthumbyb.set(~xLTHUMBY);
	~rthumbxb.set(~xRTHUMBX);
	~rthumbyb.set(~xRTHUMBY);

	~lnoise.set(\amp,0.2*(~xLTRIGGER)/100.0);
	~rnoise.set(\amp,0.2*(~xRTRIGGER)/100.0);
	~lnoise.set(\freq,~xLTRIGGER.linlin(0,100,20,240));
	~rnoise.set(\freq,~xRTRIGGER.linlin(0,100,240,1000));

	~rxgrain.set(\trig,~xX*max(1,(~xRTHUMBX.abs+~xRTHUMBY.abs)));
	~rygrain.set(\trig,~xY*max(1,(~xRTHUMBX.abs+~xRTHUMBY.abs)));
	~ragrain.set(\trig,~xA*max(1,(~xRTHUMBX.abs+~xRTHUMBY.abs)));	
	~rbgrain.set(\trig,~xB*max(1,(~xRTHUMBX.abs+~xRTHUMBY.abs)));

};

~flashkicklooper = ~soniclooper = ~whirlwindlooper = ~dragonpunchlooper = ~looper;

});




// invoke me at the end?
fork {
	loop {
		~looper.();
		(0.05).wait;
	}
};


)
// ~xA = ~xB = ~xX = ~xY = 0;
// ~xLTHUMBX = ~xLTHUMBY = 105;
//s.quit