s.boot;
//b = Buffer.read(s, Platform.resourceDir +/+ "sounds/a11wlk01.wav");
// ~b = Buffer.read(s, "wavs/1.harmonica-06.wav");
// ~b = Buffer.read(s, "/home/hindle1/Music/SPO256-AL2.wav");
~b = Buffer.read(s, "SPO256-AL2.wav");
~b = Buffer.readChannel(s, "idlevocals.wav", channels: [0]);
~b = Buffer.readChannel(s, "Sabatini-Scaramouche-short.wav", channels: [0]);
~b = Buffer.readChannel(s, "sounds-rand-tux-27975.wav", channels: [0]);
//s.sendMsg(\b_allocRead, 10, "wavs/1.harmonica-08.wav");

// Now define the granular synth
(
{
var b = ~b.bufnum, trate, dur,tin;
trate = MouseY.kr(1,12000,1);
	//trate = Phasor.kr(1,2,0,1200);
	//trate = (tin - Delay1.kr(tin)).abs;
dur = 2*1.2 / trate;
TGrains.ar(2, Dust.ar(trate), b,
	(1.2 ** WhiteNoise.kr(3).round(1)),
	MouseX.kr(0,BufDur.kr(b)),
	dur,
	WhiteNoise.kr(0.6), 0.4);
}.play;
)

~b = Buffer.readChannel(s, "sounds-rand-tux-27975.wav", channels: [0]);
(
{
var b = ~b.bufnum, x,y,rho,theta,trate, dur,tin;
	y = MouseY.kr(-1,1,0);
	x = MouseX.kr(-1,1,0);
	rho = hypot(x,y);
	theta = atan2(y,x);
	dur = 2*1.2 / rho;
	TGrains.ar(2, Dust.ar(40), b,
		0.3+rho,
		BufDur.kr(b)*theta.linlin(-3.14,3.14,0,1.0),
		0.2,
		WhiteNoise.kr(0.6), 
		0.4);
}.play;
)

SynthDef(\grainer,{ 
	arg out=0,b,rho=1.0,theta=0.0,amp=0.4,pitchoff=0.3,graindur=0.2,trig=1;
	Out.ar(out,
		TGrains.ar(2, Dust.ar(40)*trig, b,
			pitchoff+rho,
			BufDur.kr(b)*theta.linlin(-3.14,3.14,0,1.0),
			graindur,
			WhiteNoise.kr(0.6), 
			amp)
	)
}).add;
~thetabus = Bus.control(s);
~rhobus = Bus.control(s);

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
y = Synth(\mouser,[\theta, ~thetabus, \rho, ~rhobus]);
x = Synth(\grainer,[\b,~b.bufnum]);
x.map(\theta, ~thetabus);
x.map(\rho, ~rhobus);
x.set(\theta,3.14)