

// define otput key for analog switcher(TDA 1029 default is 1 if HIGH 3 input)

#define rcVersion 1.0

#define radioOutKey 6

//define amplifier On/Off
#define amplifierOutKey 9

//define hardware tablet buttons switching(buttons activates on ground connection)
#define volumeUpKey 3
#define volumeDownKey 4
#define displayOnOffKey 5

#define wheelKeyBoardIn 0

#define powerOnOffTriger 10
#define displayOnOffTriger 3

// include radio components
#include <Wire.h>
#include <TEA5767N.h>
TEA5767N radio = TEA5767N();
float frequency;
boolean isRadioOn = false;

// assign wheel keyBoard components
int raw = 0;
int Vin = 5;
float Vout = 0;
float R1 = 1000;
float R2 = 0;
float buffer = 0;
boolean isHardwareControllEnabled = false;


// input data from serial port
String inData;
int buttonPushImmitationTime = 200;
int buttonHoldImmitationTime = 4500;
int powerOnOffEnablingCounter = 0;

void setup() {
  Serial.begin(9600);
  setupRadioModuleAndComponents();
  setupAmplifier();
  setupHardwareButtons();
}

void setupAmplifier() {
  pinMode(amplifierOutKey, OUTPUT);
  digitalWrite(amplifierOutKey, LOW);
}

void setupHardwareButtons() {
  pinMode(volumeUpKey, OUTPUT);
  pinMode(volumeDownKey, OUTPUT);
  pinMode(displayOnOffKey, OUTPUT);

  digitalWrite(volumeUpKey, LOW);
  digitalWrite(volumeDownKey, LOW);
  digitalWrite(displayOnOffKey, LOW);

}

void setupRadioModuleAndComponents() {
  frequency = 103.1;
  setFrequency(frequency);
  byte isBandLimitReached = 0;
  pinMode(radioOutKey, OUTPUT);
  radio.mute();
}


void loop()
{
  inData = "";
  while (Serial.available() > 0) {
    inData = Serial.readStringUntil('\n');
  }
  if (inData != "") {
    if (inData.indexOf(':') > 0) { // suppose request with parameters
      int dividerPosition = inData.indexOf(':');
      String action = inData.substring(0, dividerPosition);
      String params = inData.substring(dividerPosition + 1);
      if (action.equals("setRadioFreq")) {
        frequency = params.toFloat();
        setFrequency(frequency);
        callRadioResponse("frequency", String(frequency, 2));
      }
    } else {  // suppose request without parameters
      if (inData.equals("setRadioFreqUp")) {
        frequency = frequency + 0.1;
        setFrequency(frequency);
        callRadioResponse("frequency_up", String(frequency, 2));
      }
      if (inData.equals("setRadioFreqDown")) {
        frequency = frequency - 0.1;
        setFrequency(frequency);
        callRadioResponse("frequency_down", String(frequency, 2));
      }
      if (inData.equals("getRadioSigLevel")) {
        callRadioResponse("signal_level", String(getSignalLevel()));
      }
      if (inData.equals("setRadioOn")) {
        radio.turnTheSoundBackOn();
        digitalWrite(radioOutKey, HIGH);
        isRadioOn = true;
        callRadioResponse("radio_on", String(1));
      }
      if (inData.equals("setRadioOff")) {
        radio.mute();
        isRadioOn = false;
        digitalWrite(radioOutKey, LOW);
        callRadioResponse("radio_off", String(0));
      }
      if (inData.equals("setAmplifierOn")) {
        digitalWrite(amplifierOutKey, HIGH);
        callRadioResponse("amplifier_on", String(1));
      }
      if (inData.equals("setAmplifierOff")) {
        digitalWrite(amplifierOutKey, LOW);
        callRadioResponse("amplifier_off", String(0));
      }

      if (inData.equals("getFirmwareVersion")) {
        callRadioResponse("codeVersion", String(rcVersion));
      }

      /// test
      if (inData.equals("pushVolumeUpBtn")) {
        pushHardwareVolumeUp ();
        callRadioResponse("hardware_volume_up", String(1));
      }
      if (inData.equals("pushVolumeDownBtn")) {
        pushHardwareVolumeDown();
        callRadioResponse("hardware_volume_down", String(1));
      }
      if (inData.equals("pushDisplayBtn")) {
        pushHardwareOnOff();
        callRadioResponse("hardware_on_off", String(1));
      }
      if (inData.equals("switchOff")) {
        pushAndHoldOnOffBtn();
        callRadioResponse("switch_off", String(1));
      }
      if (inData.equals("setHardwareControllOn")) {
        isHardwareControllEnabled = true;
        callRadioResponse("hardware_ctrl_enabled", String(1));
      }
      if (inData.equals("setHardwareControllOff")) {
        isHardwareControllEnabled = false;
        callRadioResponse("hardware_ctrl_disables", String(0));
      }


    }
  }
  setupWheelKeyBoardListener();
}

void setupWheelKeyBoardListener() {
  raw = analogRead(wheelKeyBoardIn);
  if (raw)
  {
    buffer = raw * Vin;
    Vout = (buffer) / 1024.0;
    buffer = (Vin / Vout) - 1;
    R2 = R1 * buffer;
    if (R2 > 75) {
      obtaineButtonValue(R2);
    }
    delay(200);
  }
}

void setFrequency(float newF) {
  radio.mute();
  radio.selectFrequency(newF);
  if (isRadioOn) {
    radio.turnTheSoundBackOn();
  }
}

int getSignalLevel() {
  return radio.getSignalLevel();
}

void obtaineButtonValue(float resistVal) {
  if (resistVal > 200 && resistVal < 300) {
    //btn vol down
    callKeyOutEvent("volumeDown");
    powerOnOffEnablingCounter = 0;
    if (isHardwareControllEnabled) {
      pushHardwareVolumeDown();

    }
  }
  if (resistVal > 400 && resistVal < 450) {
    //btn vol up
    callKeyOutEvent("volumeUp");
    powerOnOffEnablingCounter = 0;
    if (isHardwareControllEnabled) {
      pushHardwareVolumeUp();
    }
  }
  if (resistVal > 630 && resistVal < 750) {
    //btn mode
    callKeyOutEvent("buttonMode");
    powerOnOffEnablingCounter = powerOnOffEnablingCounter + 1;
    if (powerOnOffEnablingCounter >= displayOnOffTriger) {
      pushHardwareOnOff();
      powerOnOffEnablingCounter = 0;
    }
  }

  if (resistVal > 1000 && resistVal < 1100) {
    //btn mute
    callKeyOutEvent("buttonMute");
    powerOnOffEnablingCounter = powerOnOffEnablingCounter + 1;
    if (powerOnOffEnablingCounter >= powerOnOffTriger) {
      powerOnOffEnablingCounter = 0;
      pushAndHoldOnOffBtn();
    }
  }

  if (resistVal > 1700 && resistVal < 2000) {
    //btn down
    callKeyOutEvent("buttonDown");
    powerOnOffEnablingCounter = 0;
  }

  if (resistVal > 2600 && resistVal < 2900) {
    //btn up
    callKeyOutEvent("buttonUp");
    powerOnOffEnablingCounter = 0;
  }

  if (resistVal > 5000 && resistVal < 6000) {
    //btn left
    callKeyOutEvent("buttonLeft");
    powerOnOffEnablingCounter = 0;
  }

  if (resistVal > 511000 && resistVal < 1033000) {
    // btn right
    callKeyOutEvent("buttonRight");
    powerOnOffEnablingCounter = 0;
  }
}

void callRadioResponse (String action, String value) {
  String response = "{\"radio\":[{\"action\":" + action + "\"}, {\"value\":" + value + "}]}";
  Serial.println(response);
}

void callKeyOutEvent(String event) {
  String response = "{\"keyAction\":\"" + event + "\"}";
  Serial.println(response);
}


void pushHardwareVolumeUp () {
  digitalWrite(volumeUpKey, HIGH);
  delay(buttonPushImmitationTime);
  digitalWrite(volumeUpKey, LOW);
}

void pushHardwareVolumeDown() {
  digitalWrite(volumeDownKey, HIGH);
  delay(buttonPushImmitationTime);
  digitalWrite(volumeDownKey, LOW);
}

void pushHardwareOnOff() {
  digitalWrite(displayOnOffKey, HIGH);
  delay(buttonPushImmitationTime);
  digitalWrite(displayOnOffKey, LOW);
}

void pushAndHoldOnOffBtn () {
  digitalWrite(displayOnOffKey, HIGH);
  delay(buttonHoldImmitationTime);
  digitalWrite(displayOnOffKey, LOW);
}

