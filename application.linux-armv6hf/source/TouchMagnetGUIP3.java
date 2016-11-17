import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 
import ddf.minim.analysis.*; 
import controlP5.*; 
import javax.swing.JColorChooser; 
import java.awt.Color; 
import processing.core.*; 
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

public class TouchMagnetGUIP3 extends PApplet {





ControlP5 cp5;


 








OscP5 oscP5;
NetAddress myRemoteLocation;
OscMessage oscMessageOut;
float oscMessageOutFloat;


Minim minim;

BeatDetect beat;
BeatListener bl;
float kickSize, snareSize, hatSize;

AudioInput in;

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

boolean toggleValue = false;
boolean toggle2d = false;
boolean audioResponseLastState = false;
boolean toggleHeat = false;

public void setup() {
  
  noStroke();

  minim = new Minim(this);

  // get a line in from Minim, default bit depth is 16
  in = minim.getLineIn(Minim.STEREO, 512);

  // a beat detection object that is FREQ_ENERGY mode that 
  // expects buffers the length of song's buffer size
  // and samples captured at songs's sample rate
  beat = new BeatDetect(in.bufferSize(), in.sampleRate());
  // set the sensitivity to 300 milliseconds
  // After a beat has been detected, the algorithm will wait for 300 milliseconds 
  // before allowing another beat to be reported. You can use this to dampen the 
  // algorithm if it is giving too many false-positives. The default value is 10, 
  // which is essentially no damping. If you try to set the sensitivity to a negative value, 
  // an error will be reported and it will be set to 10 instead. 
  beat.setSensitivity(40);  
  //kickSize = snareSize = hatSize = 16;
  // make a new beat listener, so that we won't miss any buffers for the analysis
  bl = new BeatListener(beat, in);  
  //textFont(createFont("Helvetica", 16));
  //textAlign(CENTER);
  in.close();
  oscP5 = new OscP5(this, 9000);
  myRemoteLocation = new NetAddress("255.255.255.255", 12000);

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

  //buttons
  cp5.addButton("Oil Paint")
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

  cp5.addButton("Watercolor")
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
  cp5.addButton("Blue Flame")
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
  // create a toggle
  heatToggle = cp5.addToggle("-Heat")
    .setPosition(120, 440)
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

  randomTouch = cp5.addToggle("Random Touch")
    .setPosition(170, 360)
      .setSize(50, 20)
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

  audioResponse = cp5.addToggle("Audio Response")
    .setPosition(100, 360)
      .setSize(50, 20)
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
  // create a DropdownList
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


public void draw() {

  osc2d();
  osc2dRandom();
  audioTrigger();

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
        randomWait = millis() + (int)random(125, 600);//duration of touches range
        randomState = true;
        randomX = random(0, 1);
        randomY = random(0, 1);
        //println("touch " + randomX + ", " + randomY);
      } else {
        autoSpeedF = 20 * (int)autoSpeed.getValue();
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

public void audioTrigger() {
  if (audioResponse.getState()) {
    if (beat.isSnare()) {
      oscMessageOut = new OscMessage("/luminous/effect1");
      oscMessageOutFloat = (1.0f);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);

      oscMessageOut = new OscMessage("/luminous/effectB1");
      oscMessageOutFloat = (1.0f);
      oscMessageOut.add(oscMessageOutFloat);
      oscP5.send(oscMessageOut, myRemoteLocation);



      //fill(255, 0, 0);
      //noStroke();
      //rect(160, 520, 25, 10);
    }
    if (beat.isKick()) {
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
    }
  
  
  
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

    if (theEvent.isFrom(cp5.getController("Oil Paint"))) {

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
    if (theEvent.isFrom(cp5.getController("Watercolor"))) {

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
    if (theEvent.isFrom(cp5.getController("Blue Flame"))) {

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
  public void settings() {  size(300, 640); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "TouchMagnetGUIP3" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
