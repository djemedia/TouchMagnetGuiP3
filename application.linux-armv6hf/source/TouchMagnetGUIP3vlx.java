import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 
import ddf.minim.analysis.*; 
import controlP5.*; 
import javax.swing.JColorChooser; 
import java.awt.Color; 
import processing.net.*; 
import processing.core.*; 
import processing.io.*; 
import java.util.*; 
import oscP5.*; 
import netP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class TouchMagnetGUIP3vlx extends PApplet {

 /////////////////////////////////////////////////////////
////////////  TouchMagnet  ///////////////////////////
////////////////////////////////////////////////////////
//////////   OSC controller ///////////////////////
///////////////////////////////////////////////////
//written by dustin edwards and dan cote 2014
///////////////////////////////////////////////////////





ControlP5 cp5;


 









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

public void setup() {
  
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
          oscMessageOutFloat = 1.0f;
        } else {
          oscMessageOutFloat = 0.0f;
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
          oscMessageOutFloat = 1.0f;
        } else {
          oscMessageOutFloat = 0.0f;
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
          oscMessageOutFloat = 1.0f;
        } else {
          oscMessageOutFloat = 0.0f;
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

public void draw() {

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

public void osc2d() {
  if (toggle2d) {
    oscMessageOut = new OscMessage("/luminous/xy");
    oscMessageOutFloat = s.getArrayValue()[0];
    oscMessageOut.add(oscMessageOutFloat);
    oscMessageOutFloat = s.getArrayValue()[1];
    oscMessageOut.add(oscMessageOutFloat);
    oscP5.send(oscMessageOut, myRemoteLocation);
  }
}
public void osc2dRandom() {
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
public void audioTrigger() {
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
public void motionTrigger() {
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
public void oscSend() {

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

public void oscEffect2(float iA) {
  //println("recieved " + iA);
  heatToggle.setValue(iA); //set toggle to value from main app
}


public void oscEffect4(float iA) {
  //println("recieved " + iA);
  randomTouch.setValue(iA); //set toggle to value from main app
}

public void oscEffect5(float iA) {
  //println("recieved " + iA);
  audioResponse.setValue(iA); //set toggle to value from main app
}


public void controlEvent(ControlEvent theEvent) {

  if (theEvent.isController()) {

    if (theEvent.isFrom(cp5.getController("WaterColor"))) {

      oscMessageOut = new OscMessage("/luminous/sketch1");
      oscMessageOutFloat = (1.0f);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    
    if (theEvent.isFrom(cp5.getController("Clouds"))) {

      oscMessageOut = new OscMessage("/luminous/sketch2");
      oscMessageOutFloat = (1.0f);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    
    if (theEvent.isFrom(cp5.getController("Heat"))) {

      oscMessageOut = new OscMessage("/luminous/sketch3");
      oscMessageOutFloat = (1.0f);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Oil Paint"))) {

      oscMessageOut = new OscMessage("/luminous/sketch4");
      oscMessageOutFloat = (1.0f);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Flash and Trails"))) {

      oscMessageOut = new OscMessage("/luminous/sketch5");
      oscMessageOutFloat = (1.0f);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Lava Stripes"))) {

      oscMessageOut = new OscMessage("/luminous/sketch6");
      oscMessageOutFloat = (1.0f);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Candle"))) {

      oscMessageOut = new OscMessage("/luminous/sketch7");
      oscMessageOutFloat = (1.0f);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Flame"))) {

      oscMessageOut = new OscMessage("/luminous/sketch8");
      oscMessageOutFloat = (1.0f);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Natural"))) {

      oscMessageOut = new OscMessage("/luminous/sketch9");
      oscMessageOutFloat = (1.0f);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Simple Colors"))) {

      oscMessageOut = new OscMessage("/luminous/sketch10");
      oscMessageOutFloat = (1.0f);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("FlickerFlame"))) {

      oscMessageOut = new OscMessage("/luminous/sketch11");
      oscMessageOutFloat = (1.0f);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("FlickerCandle"))) {

      oscMessageOut = new OscMessage("/luminous/sketch12");
      oscMessageOutFloat = (1.0f);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Heat 2"))) {

      oscMessageOut = new OscMessage("/luminous/sketch20");
      oscMessageOutFloat = (1.0f);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Watercolor2"))) {

      oscMessageOut = new OscMessage("/luminous/sketch14");
      oscMessageOutFloat = (1.0f);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Lava2"))) {

      oscMessageOut = new OscMessage("/luminous/sketch16");
      oscMessageOutFloat = (1.0f);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
        if (theEvent.isFrom(cp5.getController("Oil Paint 2"))) {

      oscMessageOut = new OscMessage("/luminous/sketch13");
      oscMessageOutFloat = (1.0f);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Flash and Trails 2"))) {

      oscMessageOut = new OscMessage("/luminous/sketch15");
      oscMessageOutFloat = (1.0f);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Flash and Trails 3"))) {

      oscMessageOut = new OscMessage("/luminous/sketch17");
      oscMessageOutFloat = (1.0f);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Clouds 2"))) {

      oscMessageOut = new OscMessage("/luminous/sketch18");
      oscMessageOutFloat = (1.0f);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Clouds 3"))) {

      oscMessageOut = new OscMessage("/luminous/sketch19");
      oscMessageOutFloat = (1.0f);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    if (theEvent.isFrom(cp5.getController("Save"))) {

      oscMessageOut = new OscMessage("/luminous/save");
      oscMessageOutFloat = (1.0f);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);
    }
    
  }
}

public void stop()
{
  // always close Minim audio classes when you are finished with them
  in.close();
  // always stop Minim before exiting
  minim.stop();
  // this closes the sketch
  super.stop();
}
class BeatListener implements AudioListener
{
  private BeatDetect beat;
  private AudioInput source;
  
  BeatListener(BeatDetect beat, AudioInput source)
  {
    this.source = source;
    this.source.addListener(this);
    this.beat = beat;
  }
  
  public void samples(float[] samps)
  {
    beat.detect(source.mix);
  }
  
  public void samples(float[] sampsL, float[] sampsR)
  {
    beat.detect(source.mix);
  }
}
public void oscFader1(float faderIn) {
  Hue.setValue((int)map(faderIn, 0, 1, 0, 255));
}
public void oscFader2(float faderIn) {
  Saturation.setValue((int)map(faderIn, 0, 1, 0, 255));
}
public void oscFader3(float faderIn) {
  Brightness.setValue((int)map(faderIn, 0, 1, 0, 255));
}
public void oscFader4(float faderIn) {
  Contrast.setValue((int)map(faderIn, 0, 1, 0, 255));
}  
public void oscFader5(float faderIn) {
  Speed.setValue((int)map(faderIn, 0, 1, 0, 255));
}  
public void oscFader6(float faderIn) {
  Detail.setValue((int)map(faderIn, 0, 1, 0, 255));
}
public void oscFader7(float faderIn) {
  autoSpeed.setValue((int)map(faderIn, 0, 1, 0, 255));
}

/*
public void oscFader8(float faderIn) {
  vFader8.setValue((int)map(faderIn, 0, 1, 0, 255));
}    
public void oscFaderB1(float faderIn) {
  vFaderB1.setValue((int)map(faderIn, 0, 1, 0, 255));
}
public void oscFaderB2(float faderIn) {
  vFaderB2.setValue((int)map(faderIn, 0, 1, 0, 255));
}
public void oscFaderB3(float faderIn) {
  vFaderB3.setValue((int)map(faderIn, 0, 1, 0, 255));
}
public void oscFaderB4(float faderIn) {
  vFaderB4.setValue((int)map(faderIn, 0, 1, 0, 255));
}
public void oscFaderB5(float faderIn) {
  vFaderB5.setValue((int)map(faderIn, 0, 1, 0, 255));
}
public void oscFaderB6(float faderIn) {
  vFaderB6.setValue((int)map(faderIn, 0, 1, 0, 255));
}
*/
public void oscDimmer1(float faderIn) {
  Master.setValue((int)map(faderIn, 0, 1, 0, 255));
} 
/*
public void oscDimmer2(float faderIn) {
  dimmer2.setValue((int)map(faderIn, 0, 1, 0, 255));
}
public void oscDimmer3(float faderIn) {
  dimmer3.setValue((int)map(faderIn, 0, 1, 0, 255));
}
public void oscDimmer4(float faderIn) {
  dimmer4.setValue((int)map(faderIn, 0, 1, 0, 255));
}
public void oscDimmer5(float faderIn) {
  dimmer5.setValue((int)map(faderIn, 0, 1, 0, 255));
}
public void oscDimmer6(float faderIn) {
  dimmer6.setValue((int)map(faderIn, 0, 1, 0, 255));
}
public void oscDimmer7(float faderIn) {
  dimmer7.setValue((int)map(faderIn, 0, 1, 0, 255));
}
public void oscDimmer8(float faderIn) {
  dimmer8.setValue((int)map(faderIn, 0, 1, 0, 255));
}
public void oscDimmer9(float faderIn) {
  dimmer9.setValue((int)map(faderIn, 0, 1, 0, 255));
}
public void oscDimmer10(float faderIn) {
  dimmer10.setValue((int)map(faderIn, 0, 1, 0, 255));
}
*/


// MCP4725 is a Digital-to-Analog converter using I2C
// datasheet: http://ww1.microchip.com/downloads/en/DeviceDoc/22039d.pdf

// also see DigitalAnalog_I2C_MCP4725 for how to write the
// same sketch in an object-oriented way
private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
public static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for ( int j = 0; j < bytes.length; j++ ) {
        int v = bytes[j] & 0xFF;
        hexChars[j * 2] = hexArray[v >>> 4];
        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
}
byte address=0x29;

public void writeReg(byte reg, byte val)
{
   i2c.beginTransmission(0x29);
  i2c.write(reg);
  i2c.write(val);
  i2c.endTransmission();
}
public byte [] readRegs(byte reg, int cnt)
{
   i2c.beginTransmission(0x29);
   i2c.write(reg);
   byte[] in = i2c.read(cnt);
   i2c.endTransmission();
   return in;
}
public byte [] readRegs(int reg, int cnt)
{
  return readRegs((byte)reg, cnt);
}
public byte readReg(byte reg)
{
  return readRegs(reg,1)[0];
}
public short readReg16Bit(byte reg)
{
   int value;
   int msb;
   int lsb;
   byte[] res = readRegs(reg, 2);
   msb = res[0] & 0xFF;
   lsb = res[1] & 0xFF;
   value = msb;
   value = (value << 8);
   value |= lsb;	
   return (short)value;
}
public short readReg16BitPrint(byte reg)
{
   int value;
   int msb;
   int lsb;
   byte[] res = readRegs(reg, 2);
   msb = res[0] & 0xFF;
   lsb = res[1] & 0xFF;
   value = msb;
   //print(msb); print(" "); print(lsb); print(" ");
   value = (value << 8);
   value |= lsb;  
   return (short)value;
}
public byte readReg(int reg)
{
  return readReg((byte)reg);
}
public void writeReg(int reg, int val)
{
  writeReg((byte)reg, (byte)val);
}

public void writeMulti(byte reg, byte [] src, int count)
{
  i2c.beginTransmission(address);
  i2c.write(reg);

  for (int i = 0; i < count; i++)
  {
    i2c.write(src[i]);
  }

  i2c.endTransmission();
}

public void writeReg16Bit(byte reg, short value)
{
  i2c.beginTransmission(address);
  i2c.write(reg);
  i2c.write((value >> 8) & 0xFF); // value high byte
  i2c.write( value       & 0xFF); // value low byte
  i2c.endTransmission();
}
public void writeReg32Bit(byte reg, int value)
{
  i2c.beginTransmission(address);
  i2c.write(reg);
  i2c.write((value >> 24) & 0xFF); // value highest byte
  i2c.write((value >> 16) & 0xFF);
  i2c.write((value >>  8) & 0xFF);
  i2c.write( value        & 0xFF); // value lowest byte
  i2c.endTransmission();
}
byte stop_variable;
byte MSRC_CONFIG_CONTROL = (byte)0x60;

public short encodeTimeout(short timeout_mclks)
{
  // format: "(LSByte * 2^MSByte) + 1"

  int ls_byte = 0;
  short ms_byte = 0;

  if (timeout_mclks > 0)
  {
    ls_byte = timeout_mclks - 1;

    while ((ls_byte & 0xFFFFFF00) > 0)
    {
      ls_byte >>= 1;
      ms_byte++;
    }

    return (short)((ms_byte << 8) | (ls_byte & 0xFF));
  }
  else { return 0; }
}


int measurement_timing_budget_us;
public int  calcMacroPeriod(short vcsel_period_pclks)
{
 return ((((int)2304 * vcsel_period_pclks  * 1655) + 500) / 1000);
}
public int timeoutMclksToMicroseconds(short timeout_period_mclks, short vcsel_period_pclks)
{
 int macro_period_ns = calcMacroPeriod(vcsel_period_pclks);

  return (((int)timeout_period_mclks * macro_period_ns) + (macro_period_ns / 2)) / 1000;
}

public int timeoutMicrosecondsToMclks(int timeout_period_us, short vcsel_period_pclks)
{
  int macro_period_ns = calcMacroPeriod(vcsel_period_pclks);

  return (((timeout_period_us * 1000) + (macro_period_ns / 2)) / macro_period_ns);
}


public int decodeTimeout(byte [] reg_val)
{
  // format: "(LSByte * 2^MSByte) + 1"
return ((reg_val[0] & 0xFF)) + ((reg_val[1] & 0xFF) << 8) + 1;
/*  return (uint16_t)((reg_val & 0x00FF) <<
         (uint16_t)((reg_val & 0xFF00) >> 8)) + 1;*/
}


public short decodeVcselPeriod(byte reg_val)
{
 return  (short) (((reg_val & 0xFF) + 1) << 1);
}
public int getMeasurementTimingBudget()
{
	//SequenceStepEnables enables;
 boolean tcc, msrc, dss, pre_range, final_range;


//  SequenceStepTimeouts timeouts;
 short pre_range_vcsel_period_pclks, final_range_vcsel_period_pclks;

      short msrc_dss_tcc_mclks, pre_range_mclks, final_range_mclks;
      int msrc_dss_tcc_us,    pre_range_us,    final_range_us;
// end SequenceStepTimeouts

  short StartOverhead     = 1910; // note that this is different than the value in set_
  short  EndOverhead        = 960;
  short  MsrcOverhead       = 660;
  short  TccOverhead        = 590;
  short  DssOverhead        = 690;
  short  PreRangeOverhead   = 660;
  short  FinalRangeOverhead = 550;

  // "Start and end overhead times always present"
  int budget_us = StartOverhead + EndOverhead;

  //getSequenceStepEnables(&enables);
  byte sequence_config = readReg((byte)0x01); //SYSTEM_SEQUENCE_CONFIG

	tcc          = ((sequence_config >> 4) & (byte) 0x1) == (byte) 0x01;
	dss          = ((sequence_config >> 3) & (byte) 0x1) == (byte) 0x01;
	msrc         = ((sequence_config >> 2) & (byte) 0x1) == (byte) 0x01;
	pre_range    = ((sequence_config >> 6) & (byte) 0x1) == (byte) 0x01;
	final_range  = ((sequence_config >> 7) & (byte) 0x1) == (byte) 0x01;
  //end getSequenceStepEnables(&enables);

//  getSequenceStepTimeouts(&enables, &timeouts);
 pre_range_vcsel_period_pclks = decodeVcselPeriod(readReg(0x50));// PRE_RANGE_CONFIG_VCSEL_PERIOD getVcselPulsePeriod(VcselPeriodPreRange);

  msrc_dss_tcc_mclks = (short) (readReg((byte)0x46) + 1); //MSRC_CONFIG_TIMEOUT_MACROP
  msrc_dss_tcc_us =
    (short)timeoutMclksToMicroseconds(msrc_dss_tcc_mclks,
                               pre_range_vcsel_period_pclks);

  pre_range_mclks =
    (short) decodeTimeout(readRegs((byte)0x51, 2));//PRE_RANGE_CONFIG_TIMEOUT_MACROP_HI
  pre_range_us =
    timeoutMclksToMicroseconds(pre_range_mclks,
                              pre_range_vcsel_period_pclks);

  final_range_vcsel_period_pclks = decodeVcselPeriod(readReg(0x70));//FINAL_RANGE_CONFIG_VCSEL_PERIOD getVcselPulsePeriod(VcselPeriodFinalRange);

  final_range_mclks =
    (short) decodeTimeout(readRegs(0x71, 2));//FINAL_RANGE_CONFIG_TIMEOUT_MACROP_HI

  if (pre_range)
  {
    final_range_mclks -= pre_range_mclks;
  }

  final_range_us =
     timeoutMclksToMicroseconds(final_range_mclks,
                                final_range_vcsel_period_pclks);
//end getSequenceStepTimeouts



  if (tcc)
  {
    budget_us += (msrc_dss_tcc_us + TccOverhead);
  }

  if (dss)
  {
    budget_us += 2 * (msrc_dss_tcc_us + DssOverhead);
  }
  else if (msrc)
  {
    budget_us += (msrc_dss_tcc_us + MsrcOverhead);
  }

  if (pre_range)
  {
    budget_us += (pre_range_us + PreRangeOverhead);
  }

  if (final_range)
  {
    budget_us += (final_range_us + FinalRangeOverhead);
  }

  measurement_timing_budget_us = budget_us; // store for internal reuse
  return budget_us;
}

public boolean setMeasurementTimingBudget(int budget_us)
{
	//SequenceStepEnables enables;
 boolean tcc, msrc, dss, pre_range, final_range;


//  SequenceStepTimeouts timeouts;
 short pre_range_vcsel_period_pclks, final_range_vcsel_period_pclks;

      short msrc_dss_tcc_mclks, pre_range_mclks, final_range_mclks;
      int msrc_dss_tcc_us,    pre_range_us,    final_range_us;
// end SequenceStepTimeouts

  short StartOverhead     = 1320; // note that this is different than the value in get_
  short  EndOverhead        = 960;
  short  MsrcOverhead       = 660;
  short  TccOverhead        = 590;
  short  DssOverhead        = 690;
  short  PreRangeOverhead   = 660;
  short  FinalRangeOverhead = 550;

  int MinTimingBudget = 20000;

  if (budget_us < MinTimingBudget) { return false; }

  int used_budget_us = StartOverhead + EndOverhead;


  //getSequenceStepEnables(&enables);
  byte sequence_config = readReg((byte)0x01); //SYSTEM_SEQUENCE_CONFIG

	tcc          = ((sequence_config >> 4) & (byte) 0x1) == (byte) 0x01;
	dss          = ((sequence_config >> 3) & (byte) 0x1) == (byte) 0x01;
	msrc         = ((sequence_config >> 2) & (byte) 0x1) == (byte) 0x01;
	pre_range    = ((sequence_config >> 6) & (byte) 0x1) == (byte) 0x01;
	final_range  = ((sequence_config >> 7) & (byte) 0x1) == (byte) 0x01;
  //end getSequenceStepEnables(&enables);

//  getSequenceStepTimeouts(&enables, &timeouts);
// pre_range_vcsel_period_pclks = getVcselPulsePeriod(VcselPeriodPreRange);

 pre_range_vcsel_period_pclks = decodeVcselPeriod(readReg(0x50));// PRE_RANGE_CONFIG_VCSEL_PERIOD getVcselPulsePeriod(VcselPeriodPreRange);

  msrc_dss_tcc_mclks = (short) ((int)readReg((byte)0x46) + 1); //MSRC_CONFIG_TIMEOUT_MACROP
  msrc_dss_tcc_us =
    timeoutMclksToMicroseconds(msrc_dss_tcc_mclks,
                               pre_range_vcsel_period_pclks);

  pre_range_mclks =
    (short) decodeTimeout(readRegs((byte)0x51, 2));//PRE_RANGE_CONFIG_TIMEOUT_MACROP_HI
  pre_range_us =
    timeoutMclksToMicroseconds(pre_range_mclks,
                               pre_range_vcsel_period_pclks);

//  final_range_vcsel_period_pclks = getVcselPulsePeriod(VcselPeriodFinalRange);
  final_range_vcsel_period_pclks = decodeVcselPeriod(readReg(0x70));//FINAL_RANGE_CONFIG_VCSEL_PERIOD getVcselPulsePeriod(VcselPeriodFinalRange);

  final_range_mclks =
   (short) decodeTimeout(readRegs(0x71, 2));//FINAL_RANGE_CONFIG_TIMEOUT_MACROP_HI

  if (pre_range)
  {
    final_range_mclks -= pre_range_mclks;
  }

  final_range_us =
    timeoutMclksToMicroseconds(final_range_mclks,
                               final_range_vcsel_period_pclks);
//end getSequenceStepTimeouts

  if (tcc)
  {
    used_budget_us += (msrc_dss_tcc_us + TccOverhead);
  }

  if (dss)
  {
    used_budget_us += 2 * (msrc_dss_tcc_us + DssOverhead);
  }
  else if (msrc)
  {
    used_budget_us += (msrc_dss_tcc_us + MsrcOverhead);
  }

  if (pre_range)
  {
    used_budget_us += (pre_range_us + PreRangeOverhead);
  }

  if (final_range)
  {
    used_budget_us += FinalRangeOverhead;

    // "Note that the final range timeout is determined by the timing
    // budget and the sum of all other timeouts within the sequence.
    // If there is no room for the final range timeout, then an error
    // will be set. Otherwise the remaining time will be applied to
    // the final range."

    if (used_budget_us > budget_us)
    {
      // "Requested timeout too big."
      return false;
    }

    int final_range_timeout_us = budget_us - used_budget_us;

    // set_sequence_step_timeout() begin
    // (SequenceStepId == VL53L0X_SEQUENCESTEP_FINAL_RANGE)

    // "For the final range timeout, the pre-range timeout
    //  must be added. To do this both final and pre-range
    //  timeouts must be expressed in macro periods MClks
    //  because they have different vcsel periods."

	short final_range_timeout_mclks =
     (short) timeoutMicrosecondsToMclks(final_range_timeout_us,
                                 final_range_vcsel_period_pclks);

    if (pre_range)
    {
      final_range_timeout_mclks += pre_range_mclks;
    }

    writeReg16Bit((byte)0x71,
      encodeTimeout(final_range_timeout_mclks)); //FINAL_RANGE_CONFIG_TIMEOUT_MACROP_HI

    // set_sequence_step_timeout() end

    measurement_timing_budget_us = budget_us; // store for internal reuse
  }
  return true;
}
public boolean setSignalRateLimit(float limit_Mcps)
{
  if (limit_Mcps < 0 || limit_Mcps > 511.99f) { return false; }

  // Q9.7 fixed point format (9 integer bits, 7 fractional bits)
  writeReg16Bit((byte)0x44, (short) (limit_Mcps * (1 << 7))); //FINAL_RANGE_CONFIG_MIN_COUNT_RATE_RTN_LIMIT
  return true;
}
int count;
boolean type_is_aperture;
int timeout_start_ms;
int io_timeout = 0;
public void startTimeout()
{
  timeout_start_ms = millis();
}
public boolean checkTimeoutExpired()
{
  return (io_timeout > 0) && ((millis() - timeout_start_ms) > io_timeout);
}
public boolean getSpadInfo()
{
  byte tmp;

  writeReg(0x80, 0x01);
  writeReg(0xFF, 0x01);
  writeReg(0x00, 0x00);

  writeReg(0xFF, 0x06);
  writeReg(0x83, readReg(0x83) | 0x04);
  writeReg(0xFF, 0x07);
  writeReg(0x81, 0x01);

  writeReg(0x80, 0x01);

  writeReg(0x94, 0x6b);
  writeReg(0x83, 0x00);
  startTimeout();
  while (readReg(0x83) == 0x00)
  {
    if (checkTimeoutExpired()) { return false; }
  }
  writeReg(0x83, 0x01);
  tmp = readReg(0x92);

  count = tmp & (byte)0x7f;
  type_is_aperture = ((tmp >> (byte)7) & (byte)0x01) == (byte)0x1;

  writeReg(0x81, 0x00);
  writeReg(0xFF, 0x06);
  writeReg(0x83, readReg(0x83)  & ~0x04);
  writeReg(0xFF, 0x01);
  writeReg(0x00, 0x01);

  writeReg(0xFF, 0x00);
  writeReg(0x80, 0x00);

  return true;
}

// based on VL53L0X_perform_single_ref_calibration()
public boolean performSingleRefCalibration(byte vhv_init_byte)
{
//SYSRANGE_START 0x00
  writeReg(0x00, 0x01 | vhv_init_byte); // VL53L0X_REG_SYSRANGE_MODE_START_STOP

  startTimeout();
  while (((int)readReg(0x13) & 0x07) == 0) //RESULT_INTERRUPT_STATUS
  {
    if (checkTimeoutExpired()) { return false; }
  }

  writeReg(0x0B, 0x01); //SYSTEM_INTERRUPT_CLEAR

  writeReg(0x00, 0x00); //SYSRANGE_START

  return true;
}


public boolean initVL53LOX()
{
   writeReg(0x88, 0x00);

  writeReg(0x80, 0x01);
  writeReg(0xFF, 0x01);
  writeReg(0x00, 0x00);
  stop_variable = readReg(0x91);
  writeReg(0x00, 0x01);
  writeReg(0xFF, 0x00);
  writeReg(0x80, 0x00);
  
  
  

  // disable SIGNAL_RATE_MSRC (bit 1) and SIGNAL_RATE_PRE_RANGE (bit 4) limit checks
  writeReg(MSRC_CONFIG_CONTROL, readReg(MSRC_CONFIG_CONTROL) | 0x12);
  
  // set final range signal rate limit to 0.25 MCPS (million counts per second)
  setSignalRateLimit(0.25f);
  
  writeReg(0x01, 0xFF); //SYSTEM_SEQUENCE_CONFIG
    // VL53L0X_DataInit() end

  // VL53L0X_StaticInit() begin
  
   if (!getSpadInfo()) { return false; }
  byte spad_count = (byte)count;
  boolean spad_type_is_aperture = type_is_aperture;
  // The SPAD map (RefGoodSpadMap) is read by VL53L0X_get_info_from_device() in
  // the API, but the same data seems to be more easily readable from
  // GLOBAL_CONFIG_SPAD_ENABLES_REF_0 through _6, so read it from there
  byte [] ref_spad_map = readRegs((byte)0xB0, 6); //GLOBAL_CONFIG_SPAD_ENABLES_REF_0

  writeReg(0xFF, 0x01);
  writeReg(0x4F, 0x00); //DYNAMIC_SPAD_REF_EN_START_OFFSET
  writeReg(0x4E, 0x2C); //DYNAMIC_SPAD_NUM_REQUESTED_REF_SPAD
  writeReg(0xFF, 0x00);
  writeReg(0xB6, 0xB4); //GLOBAL_CONFIG_REF_EN_START_SELECT

  byte first_spad_to_enable = (byte)(spad_type_is_aperture ? 12 : 0); // 12 is the first aperture spad
  byte spads_enabled = 0;
  
  for (int i = 0; i < 48; i++)
  {
    if (i < first_spad_to_enable || spads_enabled == spad_count)
    {
      // This bit is lower than the first one that should be enabled, or
      // (reference_spad_count) bits have already been enabled, so zero this bit
      ref_spad_map[i / 8] &= ~(1 << (i % 8));
    }
    else if (((ref_spad_map[i / 8] >> (byte)(i % 8)) & (byte)0x1) == (byte)0x01)
    {
      spads_enabled++;
    }
  }
  writeMulti((byte)0xB0, ref_spad_map, 6);//GLOBAL_CONFIG_SPAD_ENABLES_REF_0

// -- VL53L0X_set_reference_spads() end

  // -- VL53L0X_load_tuning_settings() begin
  // DefaultTuningSettings from vl53l0x_tuning.h

  writeReg(0xFF, 0x01);
  writeReg(0x00, 0x00);

  writeReg(0xFF, 0x00);
  writeReg(0x09, 0x00);
  writeReg(0x10, 0x00);
  writeReg(0x11, 0x00);

  writeReg(0x24, 0x01);
  writeReg(0x25, 0xFF);
  writeReg(0x75, 0x00);

  writeReg(0xFF, 0x01);
  writeReg(0x4E, 0x2C);
  writeReg(0x48, 0x00);
  writeReg(0x30, 0x20);

  writeReg(0xFF, 0x00);
  writeReg(0x30, 0x09);
  writeReg(0x54, 0x00);
  writeReg(0x31, 0x04);
  writeReg(0x32, 0x03);
  writeReg(0x40, 0x83);
  writeReg(0x46, 0x25);
  writeReg(0x60, 0x00);
  writeReg(0x27, 0x00);
  writeReg(0x50, 0x06);
  writeReg(0x51, 0x00);
  writeReg(0x52, 0x96);
  writeReg(0x56, 0x08);
  writeReg(0x57, 0x30);
  writeReg(0x61, 0x00);
  writeReg(0x62, 0x00);
  writeReg(0x64, 0x00);
  writeReg(0x65, 0x00);
  writeReg(0x66, 0xA0);

  writeReg(0xFF, 0x01);
  writeReg(0x22, 0x32);
  writeReg(0x47, 0x14);
  writeReg(0x49, 0xFF);
  writeReg(0x4A, 0x00);

  writeReg(0xFF, 0x00);
  writeReg(0x7A, 0x0A);
  writeReg(0x7B, 0x00);
  writeReg(0x78, 0x21);

  writeReg(0xFF, 0x01);
  writeReg(0x23, 0x34);
  writeReg(0x42, 0x00);
  writeReg(0x44, 0xFF);
  writeReg(0x45, 0x26);
  writeReg(0x46, 0x05);
  writeReg(0x40, 0x40);
  writeReg(0x0E, 0x06);
  writeReg(0x20, 0x1A);
  writeReg(0x43, 0x40);

  writeReg(0xFF, 0x00);
  writeReg(0x34, 0x03);
  writeReg(0x35, 0x44);

  writeReg(0xFF, 0x01);
  writeReg(0x31, 0x04);
  writeReg(0x4B, 0x09);
  writeReg(0x4C, 0x05);
  writeReg(0x4D, 0x04);

  writeReg(0xFF, 0x00);
  writeReg(0x44, 0x00);
  writeReg(0x45, 0x20);
  writeReg(0x47, 0x08);
  writeReg(0x48, 0x28);
  writeReg(0x67, 0x00);
  writeReg(0x70, 0x04);
  writeReg(0x71, 0x01);
  writeReg(0x72, 0xFE);
  writeReg(0x76, 0x00);
  writeReg(0x77, 0x00);

  writeReg(0xFF, 0x01);
  writeReg(0x0D, 0x01);

  writeReg(0xFF, 0x00);
  writeReg(0x80, 0x01);
  writeReg(0x01, 0xF8);

  writeReg(0xFF, 0x01);
  writeReg(0x8E, 0x01);
  writeReg(0x00, 0x01);
  writeReg(0xFF, 0x00);
  writeReg(0x80, 0x00);

  // -- VL53L0X_load_tuning_settings() end

  // "Set interrupt config to new sample ready"
  // -- VL53L0X_SetGpioConfig() begin

  writeReg(0x0A, 0x04);//SYSTEM_INTERRUPT_CONFIG_GPIO
  writeReg(0x84, readReg(0x84) & ~0x10); // active low GPIO_HV_MUX_ACTIVE_HIGH
  writeReg(0x0B, 0x01); //SYSTEM_INTERRUPT_CLEAR

  // -- VL53L0X_SetGpioConfig() end
  
  
  measurement_timing_budget_us = getMeasurementTimingBudget();
  // "Disable MSRC and TCC by default"
  // MSRC = Minimum Signal Rate Check
  // TCC = Target CentreCheck
  // -- VL53L0X_SetSequenceStepEnable() begin

  writeReg(0x01, 0xE8); //SYSTEM_SEQUENCE_CONFIG

  // -- VL53L0X_SetSequenceStepEnable() end

  // "Recalculate timing budget"
  setMeasurementTimingBudget(measurement_timing_budget_us);

  // VL53L0X_StaticInit() end

  // VL53L0X_PerformRefCalibration() begin (VL53L0X_perform_ref_calibration())

  // -- VL53L0X_perform_vhv_calibration() begin

  writeReg(0x01, 0x01); //SYSTEM_SEQUENCE_CONFIG
  if (!performSingleRefCalibration((byte)0x40)) { return false; }

  // -- VL53L0X_perform_vhv_calibration() end

  // -- VL53L0X_perform_phase_calibration() begin

  writeReg(0x01, 0x02); //SYSTEM_SEQUENCE_CONFIG
  if (!performSingleRefCalibration((byte)0x00)) { return false; }

  // -- VL53L0X_perform_phase_calibration() end

  // "restore the previous Sequence Config"
  writeReg(0x01, 0xE8); //SYSTEM_SEQUENCE_CONFIG

  // VL53L0X_PerformRefCalibration() end
  return true;

}

int period_ms = 0;
public void VL53LOX_startContinuous()
{
  writeReg(0x80, 0x01);
  writeReg(0xFF, 0x01);
  writeReg(0x00, 0x00);
  writeReg(0x91, stop_variable);
  writeReg(0x00, 0x01);
  writeReg(0xFF, 0x00);
  writeReg(0x80, 0x00);

  if (period_ms != 0)
  {
    // continuous timed mode

    // VL53L0X_SetInterMeasurementPeriodMilliSeconds() begin

    short osc_calibrate_val = readReg16Bit((byte)0xF8); //OSC_CALIBRATE_VAL

    if (osc_calibrate_val != 0)
    {
      period_ms *= osc_calibrate_val;
    }

    writeReg32Bit((byte)0x04, period_ms); //SYSTEM_INTERMEASUREMENT_PERIOD

    // VL53L0X_SetInterMeasurementPeriodMilliSeconds() end

    writeReg(0x00, 0x04); // VL53L0X_REG_SYSRANGE_MODE_TIMED //SYSRANGE_START
  }
  else
  {
    // continuous back-to-back mode
    writeReg(0x00, 0x02); // VL53L0X_REG_SYSRANGE_MODE_BACKTOBACK //SYSRANGE_START
  }
}

// Stop continuous measurements
// based on VL53L0X_StopMeasurement()
public void stopContinuous()
{
  writeReg(0x00, 0x01); // VL53L0X_REG_SYSRANGE_MODE_SINGLESHOT //SYSRANGE_START

  writeReg(0xFF, 0x01);
  writeReg(0x00, 0x00);
  writeReg(0x91, 0x00);
  writeReg(0x00, 0x01);
  writeReg(0xFF, 0x00);
}

boolean did_timeout = false;
// Returns a range reading in millimeters when continuous mode is active
// (readRangeSingleMillimeters() also calls this function after starting a
// single-shot range measurement)


public int readRangeContinuousMillimeters()
{
  startTimeout();
  while (((int)readReg(0x13) & 0x07) == 0) //RESULT_INTERRUPT_STATUS
  {
    if (checkTimeoutExpired())
    {
      did_timeout = true;
      return 65535;
    }
  }

  // assumptions: Linearity Corrective Gain is 1000 (default);
  // fractional ranging is not enabled
  short range = readReg16BitPrint((byte)0x1E);//0x14 + (byte)10);//RESULT_RANGE_STATUS 

  writeReg(0x0B, 0x01); //SYSTEM_INTERRUPT_CLEAR

  return (int)range;
}


public void setupVlx() {
  //printArray(I2C.list());
  print("Hello World");
  i2c = new I2C(I2C.list()[0]);
  printArray(I2C.list());
  
  println("scanning");
  
  try
  {
  i2c.beginTransmission(0x29);
  i2c.write(0x00);
  byte[] in = i2c.read(256);
   i2c.endTransmission();

    println("found device: ");
    int i =0;
    print("   : ");
    for (i=0;i<16;i++)
    {
      
      print(bytesToHex(new byte[] {(byte)i}));
      print(" ");
    }
    for (i=0;i<=255;i++)
    {
      if (i % 16 == 0)
      {
        println(": ");
        print(bytesToHex(new byte[] {(byte)i}));
        print(" : "); 
      }
   
     print( bytesToHex(new byte[] { in[i] }));
      print(" ");

    }

    println("!");
    
}
  catch (RuntimeException e)
  {
    print("exception ");
    
    println(e);
  }

boolean initresult = initVL53LOX();


if (initresult)
{
println("initVL53LOX return true");
}
else
{
println("initVL53LOX return false");
}

io_timeout  = 500; //setTimeout

VL53LOX_startContinuous();
  
  print("Goodbye world"); 
}

public void drawVlx() {
 //print(readRangeContinuousMillimeters());
  //background(map(mouseX, 0, width, 0, 255));
 boolean tmp = did_timeout;
  did_timeout = false;
	if (tmp) { print(" TIMEOUT"); }
	//println(" ");
if (vlxEnable == true){
    //int vlxIn;
    int vlxDist;
    
    //if (initresult == true) {
    vlxDist = readRangeContinuousMillimeters();
    //println(vlxDist);
    if (vlxDist < 1250) {
    float vlxDistF = Float.valueOf(vlxDist);
    //float distF = Float.valueOf(dist).floatValue();
    float vlxIn = map(vlxDistF, 0, 1250, 0, 1);
    float[] vlxTouch = {vlxIn, .5f};
    s.setArrayValue(vlxTouch);
    oscMessageOut = new OscMessage("/luminous/xy");
    oscMessageOutFloat = vlxIn;
    oscMessageOut.add(oscMessageOutFloat);
    oscMessageOutFloat = .5f;
    oscMessageOut.add(oscMessageOutFloat);
    oscP5.send(oscMessageOut, myRemoteLocation);
    }
  }
}
  public void settings() {  size(300, 640); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "TouchMagnetGUIP3vlx" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
