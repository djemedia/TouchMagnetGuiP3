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
