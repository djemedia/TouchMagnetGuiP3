 /////////////////////////////////////////////////////////
////////////  TouchMagnet  ///////////////////////////
////////////////////////////////////////////////////////
//////////   OSC controller ///////////////////////
///////////////////////////////////////////////////
//written by dustin edwards and dan cote 2014
///////////////////////////////////////////////////////

import ddf.minim.*;
import ddf.minim.analysis.*;

import controlP5.*;
ControlP5 cp5;

import javax.swing.JColorChooser;
import java.awt.Color; 

import processing.net.*;
import processing.core.*;
import processing.io.*;
import java.util.*;

import oscP5.*;
import netP5.*;

I2C i2c;

OscP5 oscP5;
NetAddress myRemoteLocation;
OscMessage oscMessageOut;
float oscMessageOutFloat;


Minim minim;

BeatDetect beat;
BeatListener bl;
float kickSize, snareSize, hatSize;

AudioInput in;

Client motionClient;
String dist;

int canvasW = 360;
int canvasH = 640;

Slider Hue;
Slider Saturation;
Slider Brightness;
Slider Contrast;
Slider Speed;
Slider Detail;
Slider autoSpeed;
Slider Master;

boolean toggleHue = false;
boolean toggleSaturation = false;
boolean toggleBrightness = false;
boolean toggleContrast = false;
boolean toggleSpeed = false;
boolean toggleDetail = false;
boolean toggleMaster = false;
boolean toggleAutoSpeed = false;

int faderWait = millis();
int randomWait;
int autoSpeedF;
int vFader7 = 255;
float randomX;
float randomY;
boolean randomState;


Slider vFader8;
Slider vFaderB1;
Slider vFaderB2;
Slider vFaderB3;
Slider vFaderB4;
Slider vFaderB5;
Slider vFaderB6;

Slider dimmer1;
Slider dimmer2;
Slider dimmer3;
Slider dimmer4;
Slider dimmer5;
Slider dimmer6;
Slider dimmer7;
Slider dimmer8;
Slider dimmer9;
Slider dimmer10;

Slider2D s;
DropdownList d1, d2;

Toggle randomTouch;
Toggle audioResponse;
Toggle heatToggle;

boolean audioEnable = false;
boolean motionEnable = false;
boolean vlxEnable = true;
boolean toggleValue = false;
boolean toggle2d = false;
boolean audioResponseLastState = false;
boolean toggleHeat = false;

void setup() {
  size(300, 640);
  background(0);
  noStroke();

   ////////////////  MINIM /////////////

  minim = new Minim(this);
 if (audioEnable == true){
  // get a line in from Minim, default bit depth is 16
  in = minim.getLineIn(Minim.STEREO, 512);
  beat = new BeatDetect(in.bufferSize(), in.sampleRate());
    beat.setSensitivity(40);  
  kickSize = snareSize = hatSize = 16;
  // make a new beat listener, so that we won't miss any buffers for the analysis
  bl = new BeatListener(beat, in); 
  in.close();
  }
  
  ////////////////////////motion sensor client/////////////////
  if (motionEnable == true){
    motionClient = new Client(this, "127.0.0.1", 5207);
  }
  
  if (vlxEnable == true){
    setupVlx();
  }
  ////////////////////////////////////////////
  //////////////   osc  ///////////////////////
  ///////////////////////////////////////////////
  
  oscP5 = new OscP5(this, 9000);
  myRemoteLocation = new NetAddress("192.168.3.1", 12000);

  oscP5.plug(this, "oscEffect2", "/luminous/effect2");
  oscP5.plug(this, "oscEffect4", "/luminous/effect4");
  oscP5.plug(this, "oscEffect5", "/luminous/effect5");

  //faders
  oscP5.plug(this, "oscFader1", "/luminous/fader1");
  oscP5.plug(this, "oscFader2", "/luminous/fader2");
  oscP5.plug(this, "oscFader3", "/luminous/fader3");
  oscP5.plug(this, "oscFader4", "/luminous/fader4");
  oscP5.plug(this, "oscFader5", "/luminous/fader5");
  oscP5.plug(this, "oscFader6", "/luminous/fader6");
  oscP5.plug(this, "oscFader7", "/luminous/fader7");
  oscP5.plug(this, "oscFader8", "/luminous/fader8");

  oscP5.plug(this, "oscFaderB1", "/luminous/faderB1");
  oscP5.plug(this, "oscFaderB2", "/luminous/faderB2");
  oscP5.plug(this, "oscFaderB3", "/luminous/faderB3");
  oscP5.plug(this, "oscFaderB4", "/luminous/faderB4");
  oscP5.plug(this, "oscFaderB5", "/luminous/faderB5");
  oscP5.plug(this, "oscFaderB6", "/luminous/faderB6");

  //dimmers
  oscP5.plug(this, "oscDimmer1", "/luminous/dimmer1");
  oscP5.plug(this, "oscDimmer2", "/luminous/dimmer2");
  oscP5.plug(this, "oscDimmer3", "/luminous/dimmer3");
  oscP5.plug(this, "oscDimmer4", "/luminous/dimmer4");
  oscP5.plug(this, "oscDimmer5", "/luminous/dimmer5");
  oscP5.plug(this, "oscDimmer6", "/luminous/dimmer6");
  oscP5.plug(this, "oscDimmer7", "/luminous/dimmer7");
  oscP5.plug(this, "oscDimmer8", "/luminous/dimmer8");
  oscP5.plug(this, "oscDimmer9", "/luminous/dimmer9");
  oscP5.plug(this, "oscDimmer10", "/luminous/dimmer10");

/////////////////   sLIDERS /////////////////////

  cp5 = new ControlP5(this);

  //2d slider
  s = cp5.addSlider2D("touch")
    .setPosition(0, 20)
      .setSize(300, 120)
      .setArrayValue(new float[] {
        50, 50
      }
  )
    //.disableCrosshair()
    ;

  //sliders
  Hue = cp5.addSlider("Hue")
    .setPosition(10, 160)
      .setSize(260, 30)
        .setRange(0, 255)
          ;

  Saturation = cp5.addSlider("Saturation")
    .setPosition(10, 220)
      .setSize(200, 20)
        .setRange(0, 255)
          .setValue(255)
            ;

  //cp5.getController("slider").getValueLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingX(0);
  //cp5.getController("slider").getCaptionLabel().align(ControlP5.RIGHT, ControlP5.BOTTOM_OUTSIDE).setPaddingX(0);

  Brightness = cp5.addSlider("Brightness")
    .setPosition(10, 240)
      .setSize(200, 20)
        .setRange(0, 255)
          .setValue(120)
            ;

  Contrast = cp5.addSlider("Contrast")
    .setPosition(10, 270)
      .setSize(200, 20)
        .setRange(0, 255)
          .setValue(128)
            ;

  Speed = cp5.addSlider("Speed")
    .setPosition(10, 290)
      .setSize(200, 20)
        .setRange(0, 255)
          .setValue(28)
            ;

  Detail = cp5.addSlider("Detail")
    .setPosition(10, 310)
      .setSize(200, 20)
        .setRange(0, 255)
          .setValue(28)
            ;
  Master = cp5.addSlider("Master")
    .setPosition(260, 360)
      .setSize(30, 260)
        .setRange(255, 0)
          .setValue(0)
            ;
  autoSpeed = cp5.addSlider("autoSpeed")
    .setPosition(200, 400)
      .setSize(20, 220)
        .setRange(255, 0)
          .setValue(205)
            ;


  s.setMaxX(1);
  s.setMaxY(1);

  //////////////////////////////////////////////////////////CALLBACKS
  s.addCallback(new CallbackListener() {
    public void controlEvent(CallbackEvent theEvent) {
      switch(theEvent.getAction()) {
        case(ControlP5.ACTION_PRESS): 
        toggle2d = true; 
        break;
        case(ControlP5.ACTION_RELEASE): 
        toggle2d = false; 
        break;
      }
    }
  }
  );

  Hue.addCallback(new CallbackListener() {
    public void controlEvent(CallbackEvent theEvent) {
      switch(theEvent.getAction()) {
        case(ControlP5.ACTION_PRESS): 
        toggleHue = true; 
        break;
        case(ControlP5.ACTION_RELEASE): 
        toggleHue = false; 
        break;
      }
    }
  }
  );

  Saturation.addCallback(new CallbackListener() {
    public void controlEvent(CallbackEvent theEvent) {
      switch(theEvent.getAction()) {
        case(ControlP5.ACTION_PRESS): 
        toggleSaturation = true; 
        break;
        case(ControlP5.ACTION_RELEASE): 
        toggleSaturation = false; 
        break;
      }
    }
  }
  );

  Brightness.addCallback(new CallbackListener() {
    public void controlEvent(CallbackEvent theEvent) {
      switch(theEvent.getAction()) {
        case(ControlP5.ACTION_PRESS): 
        toggleBrightness = true; 
        break;
        case(ControlP5.ACTION_RELEASE): 
        toggleBrightness = false; 
        break;
      }
    }
  }
  );

  Contrast.addCallback(new CallbackListener() {
    public void controlEvent(CallbackEvent theEvent) {
      switch(theEvent.getAction()) {
        case(ControlP5.ACTION_PRESS): 
        toggleContrast = true; 
        break;
        case(ControlP5.ACTION_RELEASE): 
        toggleContrast = false; 
        break;
      }
    }
  }
  );

  Speed.addCallback(new CallbackListener() {
    public void controlEvent(CallbackEvent theEvent) {
      switch(theEvent.getAction()) {
        case(ControlP5.ACTION_PRESS): 
        toggleSpeed = true; 
        break;
        case(ControlP5.ACTION_RELEASE): 
        toggleSpeed = false; 
        break;
      }
    }
  }
  );

  Detail.addCallback(new CallbackListener() {
    public void controlEvent(CallbackEvent theEvent) {
      switch(theEvent.getAction()) {
        case(ControlP5.ACTION_PRESS): 
        toggleDetail = true; 
        break;
        case(ControlP5.ACTION_RELEASE): 
        toggleDetail = false; 
        break;
      }
    }
  }
  );

  Master.addCallback(new CallbackListener() {
    public void controlEvent(CallbackEvent theEvent) {
      switch(theEvent.getAction()) {
        case(ControlP5.ACTION_PRESS): 
        toggleMaster = true; 
        break;
        case(ControlP5.ACTION_RELEASE): 
        toggleMaster = false; 
        break;
      }
    }
  }
  );

  autoSpeed.addCallback(new CallbackListener() {
    public void controlEvent(CallbackEvent theEvent) {
      switch(theEvent.getAction()) {
        case(ControlP5.ACTION_PRESS): 
        toggleAutoSpeed = true; 
        break;
        case(ControlP5.ACTION_RELEASE): 
        toggleAutoSpeed = false; 
        break;
      }
    }
  }
  );

  ///////////////////////////////   buttons
  cp5.addButton("WaterColor")
    .setValue(0)
      .setPosition(10, 400)
        .setSize(100, 19)
          ;

  // and add another 2 buttons
  cp5.addButton("Clouds")
    .setValue(100)
      .setPosition(10, 420)
        .setSize(100, 19)
          ;

  cp5.addButton("Heat")
    .setPosition(10, 440)
      .setSize(100, 19)
        .setValue(0)
          ;

  cp5.addButton("Oil Paint")
    .setPosition(10, 460)
      .setSize(100, 19)
        .setValue(0)
          ;
  cp5.addButton("Flash and Trails")
    .setPosition(10, 480)
      .setSize(100, 19)
        .setValue(0)
          ;
  cp5.addButton("Lava Stripes")
    .setPosition(10, 500)
      .setSize(100, 19)
        .setValue(0)
          ;
  cp5.addButton("Candle")
    .setPosition(10, 520)
      .setSize(100, 19)
        .setValue(0)
          ;
  cp5.addButton("Flame")
    .setPosition(10, 540)
      .setSize(100, 19)
        .setValue(0)
          ;
  cp5.addButton("Natural")
    .setPosition(10, 540)
      .setSize(100, 19)
        .setValue(0)
          ;
  cp5.addButton("Simple Colors")
    .setPosition(10, 560)
      .setSize(100, 19)
        .setValue(0)
          ;
  cp5.addButton("FlickerFlame")
    .setPosition(10, 580)
      .setSize(100, 19)
        .setValue(0)
          ;
  cp5.addButton("FlickerCandle")
    .setPosition(10, 600)
      .setSize(100, 19)
        .setValue(0)
          ;
          cp5.addButton("Heat 2")
    .setPosition(120, 440)
      .setSize(70, 19)
        .setValue(0)
          ;
          cp5.addButton("Watercolor2")
    .setPosition(120, 400)
      .setSize(70, 19)
        .setValue(0)
          ;
          cp5.addButton("Lava2")
    .setPosition(120, 500)
      .setSize(70, 19)
        .setValue(0)
          ;
            cp5.addButton("Oil Paint 2")
    .setPosition(120, 600)
      .setSize(70, 19)
        .setValue(0)
          ;
          cp5.addButton("Flash and Trails 2")
    .setPosition(120, 480)
      .setSize(70, 19)
        .setValue(0)
          ;
          cp5.addButton("Flash and Trails 3")
    .setPosition(120, 560)
      .setSize(70, 19)
        .setValue(0)
          ;
          cp5.addButton("Clouds 2")
    .setPosition(120, 420)
      .setSize(70, 19)
        .setValue(0)
          ;
          cp5.addButton("Clouds 3")
    .setPosition(120, 520)
      .setSize(70, 19)
        .setValue(0)
          ;
          cp5.addButton("Save")
    .setPosition(10, 350)
      .setSize(80, 19)
        .setValue(0)
          ;
// create a toggle button
  heatToggle = cp5.addToggle("-Heat")
    .setPosition(100, 350)
      .setSize(50, 20)
        ;
  heatToggle.addCallback(new CallbackListener() {
    public void controlEvent(CallbackEvent theEvent) {
      switch(theEvent.getAction()) {
        case(ControlP5.ACTION_PRESS): 
        oscMessageOut = new OscMessage("/luminous/effect2");
        if (heatToggle.getState()) {
          oscMessageOutFloat = 1.0;
        } else {
          oscMessageOutFloat = 0.0;
        }
        oscMessageOut.add(oscMessageOutFloat);
        oscP5.send(oscMessageOut, myRemoteLocation);
        println("local heat toggle pushed " + randomTouch.getState());         
        break;
        case(ControlP5.ACTION_RELEASE): 
        break;
      }
    }
  }
  );
/////////////////////////////////////////////////////////////RANDOM TOUCH
  randomTouch = cp5.addToggle("Random Touch")
    .setPosition(180, 370)
      .setSize(50, 15)
        ;
  randomTouch.addCallback(new CallbackListener() {
    public void controlEvent(CallbackEvent theEvent) {
      switch(theEvent.getAction()) {
        case(ControlP5.ACTION_PRESS): 
           oscMessageOut = new OscMessage("/luminous/effect4");
        if (randomTouch.getState()) {
          oscMessageOutFloat = 1.0;
        } else {
          oscMessageOutFloat = 0.0;
        }
        oscMessageOut.add(oscMessageOutFloat);
        oscP5.send(oscMessageOut, myRemoteLocation);
        println("local random touch pushed " + randomTouch.getState());         
        break;
        case(ControlP5.ACTION_RELEASE): 
        break;
      }
    }
  }
  );
/////////////////////////////////////////////////////////////AUDIO RESPONSE TOGGLE
  audioResponse = cp5.addToggle("Audio Response")
    .setPosition(180, 340)
      .setSize(50, 15)
        ;
  audioResponse.addCallback(new CallbackListener() {
    public void controlEvent(CallbackEvent theEvent) {
      switch(theEvent.getAction()) {
        case(ControlP5.ACTION_PRESS): 
        oscMessageOut = new OscMessage("/luminous/effect5");
        if (audioResponse.getState()) {
          oscMessageOutFloat = 1.0;
        } else {
          oscMessageOutFloat = 0.0;
        }
        oscMessageOut.add(oscMessageOutFloat);
        oscP5.send(oscMessageOut, myRemoteLocation);
        println("local audio response pushed " + audioResponse.getState());         
        break;
        case(ControlP5.ACTION_RELEASE): 
        break;
      }
    }
  }
  );
/*
 //////////////////////////////////////////////////////////// // create a DropdownList
  d1 = cp5.addDropdownList("myList-d1")
    .setPosition(20, 20)
      ;

  customize(d1); // customize the first list
}

void customize(DropdownList ddl) {
  // a convenience function to customize a DropdownList
  ddl.setBackgroundColor(color(190));
  ddl.setItemHeight(20);
  ddl.setBarHeight(15);
  ddl.captionLabel().set("dropdown");
  ddl.captionLabel().style().marginTop = 3;
  ddl.captionLabel().style().marginLeft = 3;
  ddl.valueLabel().style().marginTop = 3;
  for (int i=0; i<40; i++) {
    ddl.addItem("item "+i, i);
  }
  //ddl.scroll(0);
  ddl.setColorBackground(color(60));
  ddl.setColorActive(color(255, 128));
*/
}

/*
void controlEvent(ControlEvent theEvent) {
 // DropdownList is of type ControlGroup.
 // A controlEvent will be triggered from inside the ControlGroup class.
 // therefore you need to check the originator of the Event with
 // if (theEvent.isGroup())
 // to avoid an error message thrown by controlP5.
 
 if (theEvent.isGroup()) {
 // check if the Event was triggered from a ControlGroup
 println("event from group : "+theEvent.getGroup().getValue()+" from "+theEvent.getGroup());
 } 
 else if (theEvent.isController()) {
 println("event from controller : "+theEvent.getController().getValue()+" from "+theEvent.getController());
 }
 }
 */
////////////////////////draw///////////////
//////////////////////////////////////////

void draw() {

  osc2d();
  osc2dRandom();
  audioTrigger();
  motionTrigger();
  
  if (vlxEnable == true){
    drawVlx();
  }

  if ((millis() - faderWait) > 50) {
    faderWait = millis();  
    oscSend();
  }

  if (toggleValue==true) {
    fill(255, 255, 220);
  } else {
    fill(128, 128, 110);
  }
}

void osc2d() {
  if (toggle2d) {
    oscMessageOut = new OscMessage("/luminous/xy");
    oscMessageOutFloat = s.getArrayValue()[0];
    oscMessageOut.add(oscMessageOutFloat);
    oscMessageOutFloat = s.getArrayValue()[1];
    oscMessageOut.add(oscMessageOutFloat);
    oscP5.send(oscMessageOut, myRemoteLocation);
  }
}
void osc2dRandom() {
  if (randomTouch.getState()) {
    if ((millis() - randomWait) > 0) {
      if (randomState == false) {
        randomWait = millis() + (int)random(125, 2000);//duration of touches range
        randomState = true;
        randomX = random(0, 1);
        randomY = random(0, 1);
        //println("touch " + randomX + ", " + randomY);
      } else {
        autoSpeedF = (int)random(5, 100) * (int)autoSpeed.getValue();
        randomWait = millis() + autoSpeedF;
        //randomWait = millis() + (int)random(1600, 2400);//wait in between touches range
        randomState = false;
        //println("lift");
      }
    }
  }

  if (randomState == true) {  
    oscMessageOut = new OscMessage("/luminous/xy");
    float[] showTouch = {randomX, randomY};
    s.setArrayValue(showTouch);
    oscMessageOutFloat = randomX;
    oscMessageOut.add(oscMessageOutFloat);
    oscMessageOutFloat = randomY;
    oscMessageOut.add(oscMessageOutFloat);
    oscP5.send(oscMessageOut, myRemoteLocation);

    oscMessageOut = new OscMessage("/luminous/xyB");
    oscMessageOutFloat = randomX;
    oscMessageOut.add(oscMessageOutFloat);
    oscMessageOutFloat = randomY;
    oscMessageOut.add(oscMessageOutFloat);
    oscP5.send(oscMessageOut, myRemoteLocation);
  }
}
//audio trigger
void audioTrigger() {
  if (audioResponse.getState()) {
    //beat.detect(minim.in);
    /* if (beat.isSnare()) {
      oscMessageOut = new OscMessage("/luminous/effect1");
      oscMessageOutFloat = (1.0);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);

      oscMessageOut = new OscMessage("/luminous/effectB1");
      oscMessageOutFloat = (1.0);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);

      //fill(255, 0, 0);
      //noStroke();
      //rect(160, 520, 25, 10);
    }
    */
 
    //if (beat.isKick()) {
      
    oscMessageOut = new OscMessage("/luminous/xy");
    oscMessageOutFloat = s.getArrayValue()[0];
    oscMessageOut.add(oscMessageOutFloat);
    oscMessageOutFloat = s.getArrayValue()[1];
    oscMessageOut.add(oscMessageOutFloat);
    oscP5.send(oscMessageOut, myRemoteLocation);

    oscMessageOut = new OscMessage("/luminous/xyB");
    oscMessageOutFloat = s.getArrayValue()[0];
    oscMessageOut.add(oscMessageOutFloat);
    oscMessageOutFloat = s.getArrayValue()[1];
    oscMessageOut.add(oscMessageOutFloat);
    oscP5.send(oscMessageOut, myRemoteLocation);



      //fill(255, 0, 0);
      //noStroke();
      //rect(160, 520, 25, 10);
  //  }
  
  
  
    if (!audioResponseLastState) {
      in = minim.getLineIn(Minim.STEREO, 512);
      beat = new BeatDetect(in.bufferSize(), in.sampleRate());
      beat.setSensitivity(30);  
      bl = new BeatListener(beat, in);
      println("activate beat listener");
      audioResponseLastState = true;
    }
  } else {
    if (audioResponseLastState) {
      in.close();
      println("disable beat listener");
      audioResponseLastState = false;
    }
  }
}
////////////////////////////////motion trigger//////////////////////////////////
void motionTrigger() {
  if (motionEnable == true){
    //int sensorIn;
    byte data = 10;
    //String dist;
    if (motionClient.available()>0) {
    dist = motionClient.readStringUntil(data);
    //println(dist);
    float distF = Float.valueOf(dist);
    //float distF = Float.valueOf(dist).floatValue();
    float sensorIn = map(distF, 0, 100, 0, 1);
    float[] motionTouch = {sensorIn, sensorIn};
    s.setArrayValue(motionTouch);
    oscMessageOut = new OscMessage("/luminous/xy");
    oscMessageOutFloat = sensorIn;
    oscMessageOut.add(oscMessageOutFloat);
    oscMessageOutFloat = sensorIn;
    oscMessageOut.add(oscMessageOutFloat);
    oscP5.send(oscMessageOut, myRemoteLocation);
    }
  }
    
}
////////////////////////////////////////////////////////////////////OSC send
void oscSend() {

  if (toggleHue) {
    oscMessageOut = new OscMessage("/luminous/fader1");
    oscMessageOutFloat = (float)map(Hue.getValue(), 0, 255, 0, 1);
    oscMessageOut.add(oscMessageOutFloat);
    oscP5.send(oscMessageOut, myRemoteLocation);
    //println(oscMessageOutFloat);
  }

  if (toggleSaturation) {
    oscMessageOut = new OscMessage("/luminous/fader2");
    oscMessageOutFloat = (float)map(Saturation.getValue(), 0, 255, 0, 1);
    oscMessageOut.add(oscMessageOutFloat);
    oscP5.send(oscMessageOut, myRemoteLocation);
  }

  if (toggleBrightness) {
    oscMessageOut = new OscMessage("/luminous/fader3");
    oscMessageOutFloat = (float)map(Brightness.getValue(), 0, 255, 0, 1);
    oscMessageOut.add(oscMessageOutFloat);
    oscP5.send(oscMessageOut, myRemoteLocation);
  }

  if (toggleContrast) {
    oscMessageOut = new OscMessage("/luminous/fader4");
    oscMessageOutFloat = (float)map(Contrast.getValue(), 0, 255, 0, 1);
    oscMessageOut.add(oscMessageOutFloat);
    oscP5.send(oscMessageOut, myRemoteLocation);
  }

  if (toggleSpeed) {
    oscMessageOut = new OscMessage("/luminous/fader5");
    oscMessageOutFloat = (float)map(Speed.getValue(), 0, 255, 0, 1);
    oscMessageOut.add(oscMessageOutFloat);
    oscP5.send(oscMessageOut, myRemoteLocation);
  }

  if (toggleDetail) {
    oscMessageOut = new OscMessage("/luminous/fader6");
    oscMessageOutFloat = (float)map(Detail.getValue(), 0, 255, 0, 1);
    oscMessageOut.add(oscMessageOutFloat);
    oscP5.send(oscMessageOut, myRemoteLocation);
  }

  if (toggleMaster) {
    oscMessageOut = new OscMessage("/luminous/dimmer1");
    oscMessageOutFloat = (float)map(Master.getValue(), 0, 255, 0, 1);
    oscMessageOut.add(oscMessageOutFloat);
    oscP5.send(oscMessageOut, myRemoteLocation);
  }

  if (toggleAutoSpeed) {
    oscMessageOut = new OscMessage("/luminous/fader7");
    oscMessageOutFloat = (float)map(autoSpeed.getValue(), 0, 255, 0, 1);
    oscMessageOut.add(oscMessageOutFloat);
    oscP5.send(oscMessageOut, myRemoteLocation);
  }
}

void oscEffect2(float iA) {
  //println("recieved " + iA);
  heatToggle.setValue(iA); //set toggle to value from main app
}


void oscEffect4(float iA) {
  //println("recieved " + iA);
  randomTouch.setValue(iA); //set toggle to value from main app
}

void oscEffect5(float iA) {
  //println("recieved " + iA);
  audioResponse.setValue(iA); //set toggle to value from main app
}


void controlEvent(ControlEvent theEvent) {

  if (theEvent.isController()) {

    if (theEvent.isFrom(cp5.getController("WaterColor"))) {

      oscMessageOut = new OscMessage("/luminous/sketch1");
      oscMessageOutFloat = (1.0);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    
    if (theEvent.isFrom(cp5.getController("Clouds"))) {

      oscMessageOut = new OscMessage("/luminous/sketch2");
      oscMessageOutFloat = (1.0);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    
    if (theEvent.isFrom(cp5.getController("Heat"))) {

      oscMessageOut = new OscMessage("/luminous/sketch3");
      oscMessageOutFloat = (1.0);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Oil Paint"))) {

      oscMessageOut = new OscMessage("/luminous/sketch4");
      oscMessageOutFloat = (1.0);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Flash and Trails"))) {

      oscMessageOut = new OscMessage("/luminous/sketch5");
      oscMessageOutFloat = (1.0);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Lava Stripes"))) {

      oscMessageOut = new OscMessage("/luminous/sketch6");
      oscMessageOutFloat = (1.0);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Candle"))) {

      oscMessageOut = new OscMessage("/luminous/sketch7");
      oscMessageOutFloat = (1.0);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Flame"))) {

      oscMessageOut = new OscMessage("/luminous/sketch8");
      oscMessageOutFloat = (1.0);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Natural"))) {

      oscMessageOut = new OscMessage("/luminous/sketch9");
      oscMessageOutFloat = (1.0);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Simple Colors"))) {

      oscMessageOut = new OscMessage("/luminous/sketch10");
      oscMessageOutFloat = (1.0);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("FlickerFlame"))) {

      oscMessageOut = new OscMessage("/luminous/sketch11");
      oscMessageOutFloat = (1.0);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("FlickerCandle"))) {

      oscMessageOut = new OscMessage("/luminous/sketch12");
      oscMessageOutFloat = (1.0);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Heat 2"))) {

      oscMessageOut = new OscMessage("/luminous/sketch20");
      oscMessageOutFloat = (1.0);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Watercolor2"))) {

      oscMessageOut = new OscMessage("/luminous/sketch14");
      oscMessageOutFloat = (1.0);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Lava2"))) {

      oscMessageOut = new OscMessage("/luminous/sketch16");
      oscMessageOutFloat = (1.0);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
        if (theEvent.isFrom(cp5.getController("Oil Paint 2"))) {

      oscMessageOut = new OscMessage("/luminous/sketch13");
      oscMessageOutFloat = (1.0);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Flash and Trails 2"))) {

      oscMessageOut = new OscMessage("/luminous/sketch15");
      oscMessageOutFloat = (1.0);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Flash and Trails 3"))) {

      oscMessageOut = new OscMessage("/luminous/sketch17");
      oscMessageOutFloat = (1.0);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Clouds 2"))) {

      oscMessageOut = new OscMessage("/luminous/sketch18");
      oscMessageOutFloat = (1.0);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Clouds 3"))) {

      oscMessageOut = new OscMessage("/luminous/sketch19");
      oscMessageOutFloat = (1.0);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Save"))) {

      oscMessageOut = new OscMessage("/luminous/save");
      oscMessageOutFloat = (1.0);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    
  }
}

void stop()
{
  // always close Minim audio classes when you are finished with them
  in.close();
  // always stop Minim before exiting
  minim.stop();
  // this closes the sketch
  super.stop();
}
