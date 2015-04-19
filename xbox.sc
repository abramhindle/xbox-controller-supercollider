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
	Out.ar(out,
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
	Out.ar(out,
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



x = Synth(\hydro4,[\out,0]);
y = Synth(\hydro4,[\out,1]);
~x2 = Synth(\hydro4,[\out,0]);
~y2 = Synth(\hydro4,[\out,1]);

~h21 = Synth(\hydro2,[\amp,0,\freq,440,\out,0]);
~h22 = Synth(\hydro2,[\amp,0,\freq,440*2,\out,1]);
~h23 = Synth(\hydro2,[\amp,0,\freq,440*3,\out,0]);
~h24 = Synth(\hydro2,[\amp,0,\freq,440*4,\out,1]);
y.set(\out,1);
~lnoise = Synth(\noise,[\amp,0,\out,0]);
~rnoise = Synth(\noise,[\amp,0,\out,1]);
~anoise = Synth(\noise,[\amp,0,\freq,100,\out,0]);
~bnoise = Synth(\noise,[\amp,0,\freq,200,\out,1]);
~xnoise = Synth(\noise,[\amp,0,\freq,300,\out,0]);
~ynoise = Synth(\noise,[\amp,0,\freq,400,\out,1]);
~rbnoise = Synth(\noise,[\amp,0,\freq,500,\out,1]);
~lbnoise = Synth(\noise,[\amp,0,\freq,600,\out,0]);
~backnoise = Synth(\noise,[\amp,0,\freq,700,\out,1]);
~startnoise = Synth(\noise,[\amp,0,\freq,800,\out,0]);
~ltnoise = Synth(\noise,[\amp,0,\freq,1600,\out,1]);
~rtnoise = Synth(\noise,[\amp,0,\freq,3200,\out,0]);


//~noise.set(\amp,0.1)
//~noise.set(\freq,200)
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





~xhz = 440;
~yhz = 440;
~x2hz = 440;
~y2hz = 440;

~namp = 0.0;
~nfreq = 440;
~listener = {
	|msg|
	//var xLTHUMBX ,xLTHUMBY ,xRTHUMBX ,xRTHUMBY ,xRTRIGGER ,xLTRIGGER ,xA ,xB ,xX ,xY ,xLB ,xRB ,xBACK ,xSTART ,xXBOX ,xLEFTTHUMB ,xRIGHTTHUMB ,xDPADX, xDPADY;
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
	msg.postln;
};



~looper = {
	if(~xLTHUMBX != 0.0,{ ~xhz = (~xLTHUMBX/101.0) + ~xhz; x.set(\freq,~xhz) });
	if(~xRTHUMBX != 0.0,{ ~yhz = (~xRTHUMBX/101.0) + ~yhz; y.set(\freq,~yhz) });
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

fork {
	loop {
		~looper.();
		(0.01).wait;
	}
};


OSCFunc.newMatching(~listener, '/xbox');



