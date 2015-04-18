s.options.numBuffers = 16000;
s.options.memSize = 655360;
s.boot;
s.freqscope;
s.plotTree;
s.scope;

SynthDef(\hydro4, {
	|out=0,amp=1.0,freq=440|
	var nsize,n = (2..10);
	nsize = n.size;
	Out.ar(0,
		amp * 
		(
			n.collect {arg i; 
				SinOsc.ar( (1.0 - (1/(i*i))) * 2*freq ) +
				SinOsc.ar( (1.0 - (1/(i*i))) * freq ) +
				SinOsc.ar( ((1/4) - (1/((i+1)*(i+1)))) * freq)
			}).sum / (3 * nsize)
	)
}).add;

SynthDef(\hydro2, {
	|out=0,amp=1.0,freq=440.0|
	var nsize,n = (2..10);
	nsize = n.size;
	Out.ar(0,
		amp * 
		(
			n.collect {arg i; 
				SinOsc.ar( (1.0 - (1.0/(i*i))) * freq )
			}).sum / nsize
	)
}).add;

SynthDef(\noise, 
        { arg out = 0, amp=0,freq=800, sustain=0.001; 
                Out.ar(out, // write to output bus
					amp*Ringz.ar(PinkNoise.ar(0.1), freq, 2.6), // filtered noise
				)

        }
).add;



x = Synth(\hydro4);
y = Synth(\hydro4);
y.set(\out,1);
~lnoise = Synth(\noise);
~rnoise = Synth(\noise);
~rnoise.set(\out,1);
//~noise.set(\amp,0.1)
//~noise.set(\freq,200)

~xhz = 440;
~yhz = 440;
~namp = 0.0;
~nfreq = 440;
~listener = {
	|msg|
	var xLTHUMBX ,xLTHUMBY ,xRTHUMBX ,xRTHUMBY ,xRTRIGGER ,xLTRIGGER ,xA ,xB ,xX ,xY ,xLB ,xRB ,xBACK ,xSTART ,xXBOX ,xLEFTTHUMB ,xRIGHTTHUMB ,xDPADX, xDPADY;
	xLTHUMBX = msg[ 1 ];
	xLTHUMBY = msg[ 2 ];
	xRTHUMBX = msg[ 3 ];
	xRTHUMBY = msg[ 4 ]; 
	xRTRIGGER = msg[ 5 ];
	xLTRIGGER = msg[ 6 ];
	xA = msg[ 7 ]; 
	xB = msg[ 8 ];
	xX = msg[ 9 ];
	xY = msg[ 10 ]; 
	xLB = msg[ 11 ]; 
	xRB = msg[ 12 ]; 
	xBACK = msg[ 13 ];
	xSTART = msg[ 14 ]; 
	xXBOX = msg[ 15 ];
	xLEFTTHUMB = msg[ 16 ];
	xRIGHTTHUMB = msg[ 17 ];
	xDPADX = msg[ 18 ];
	xDPADY = msg[ 19 ];
	if(xLTHUMBX != 0.0,{ ~xhz = (xLTHUMBX/101.0) + ~xhz; x.set(\freq,~xhz) });
	if(xRTHUMBX != 0.0,{ ~yhz = (xRTHUMBX/101.0) + ~yhz; y.set(\freq,~yhz) });
	~rnoise.set(\amp,xRTRIGGER/100.0);
	~lnoise.set(\amp,xLTRIGGER/100.0);
	~lnoise.set(\freq,~xhz/10);
	~rnoise.set(\freq,~yhz/10);
	msg.postln;
	[~xhz,~yhz].postln;
};

OSCFunc.newMatching(~listener, '/xbox');



