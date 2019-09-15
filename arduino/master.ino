#include <Stepper.h>

const int stepsPerRevolution = 200;

Stepper myStepper(stepsPerRevolution, 22, 23, 24, 25);
char state = 'H';
char control = 0;

int rpm_left_ref = 1;
int rpm_right_ref = 1;
int rpm_left = 1;
int rpm_right = 1;
int throttle = 0;
int steering = 0;
unsigned long lastmillis = 0;
int refreshRate = 400;

int i;

int distance;
int triggerPin = 30;
int echoPin = 31;

void setup() {
  Serial.begin(9600);
  myStepper.setSpeed(60);
  
  pinMode(A0, INPUT); //throttle
  pinMode(A1, INPUT); //steering
  
  // right
  pinMode(11, OUTPUT); //16
  pinMode(10, OUTPUT); //8
  pinMode(9, OUTPUT); //4
  pinMode(8, OUTPUT); //2
  pinMode(7, OUTPUT); //1

  // left
  pinMode(6, OUTPUT); //16
  pinMode(5, OUTPUT); //8
  pinMode(4, OUTPUT); //4
  pinMode(3, OUTPUT); //2
  pinMode(2, OUTPUT); //1
  
  pinMode(triggerPin, OUTPUT);
  pinMode(echoPin, INPUT);
}

void loop() {
  if(Serial.available())
  {
    state=Serial.read();
    Serial.print("Serial: ");
    Serial.println(state);
  }
    if(state ==  'H') {
      //throttle = analogRead(A0) / 50;
      //steering = analogRead(A1) / 50;
    
      switch(throttle) {
        case 4 : rpm_left = 5;
                 rpm_right = 5;
                 break;
        case 5 : rpm_left = 9;
                 rpm_right = 9;
                 break;
        case 6 : rpm_left = 13;
                 rpm_right = 13;
                 break;
        case 7 : rpm_left = 17;
                 rpm_right = 17;
                 break;
        case 8 : rpm_left = 21;
                 rpm_right = 21;
                 break;
        case 9 : rpm_left = 26;
                 rpm_right = 26;
                 break;
        case 10 : rpm_left = 30;
                  rpm_right = 30;
                  break;
        default : rpm_left = 1;
                  rpm_right = 1;
      }

      //Serial.println(steering);
    
      switch(steering) {
        case 7 : rpm_left = rpm_left * 1.6;
                 rpm_right = rpm_right * 0.4;
                 break;
        case 8 : rpm_left = rpm_left * 1.4;
                 rpm_right = rpm_right * 0.6;
                 break;
        case 9 : rpm_left = rpm_left * 1.2;
                 rpm_right = rpm_right * 0.8;
                 break;
        case 12 : rpm_left = rpm_left * 0.8;
                  rpm_right = rpm_right * 1.2;
                  break;
        case 13 : rpm_left = rpm_left * 0.6;
                  rpm_right = rpm_right * 1.4;
                  break;
        case 14 : rpm_left = rpm_left * 0.4;
                  rpm_right = rpm_right * 1.6;
        default : break; 
      }
    }
    else if(state == 'F') {
      /*
      for(i = 0; i<400;i++)
      {
        myStepper.step(1);
        delay(10);
      }*/
      state = 'H';
    }
    else if(state == 'T') {
      while(1) {
        if(Serial.available())
        {
          state=Serial.read();
          Serial.print("Tracking: ");
          Serial.println(state);
          break;
        }
        digitalWrite(triggerPin, HIGH);
        delayMicroseconds(10);
        digitalWrite(triggerPin, LOW);
        
        distance = pulseIn(echoPin, HIGH) / 58;
        Serial.print("Distance(cm) = ");
        Serial.println(distance);
        if(distance < 80)
        {
          rpm_left = 1;
          rpm_right = 1;
        }else {
          rpm_left = 10;
          rpm_right = 10;
        }
      }
    }
    else if(state == 'C') {
      if(Serial.available()) {
        control=Serial.read();
        Serial.print("Control: ");
        Serial.println(control);
      }
      switch(control) {
        case 0 : rpm_left = 1; rpm_right = 1; break;
        case 1 : rpm_left = 1; rpm_right = 10; break;
        case 2 : rpm_left = 5; rpm_right = 10; break;
        case 3 : rpm_left = 10; rpm_right = 10;break;
        case 4 : rpm_left = 10; rpm_right = 5; break;
        case 5 : rpm_left = 10; rpm_right = 1; break;
        default : rpm_left = 1; rpm_right =1;
      }
    }
    else {
      rpm_left = 1;
      rpm_right = 1;
    }
    
    rpm_left = 20;
    rpm_right = 30;
    
    if (rpm_left > rpm_left_ref)
      rpm_left_ref++;
    else if (rpm_left < rpm_left_ref)
      rpm_left_ref = rpm_left;

    if (rpm_right > rpm_right_ref)
      rpm_right_ref++;
    else if (rpm_right < rpm_right_ref)
      rpm_right_ref = rpm_right;
    
    digitalWrite(6, rpm_left_ref & 16);
    digitalWrite(5, rpm_left_ref & 8);
    digitalWrite(4, rpm_left_ref & 4);
    digitalWrite(3, rpm_left_ref & 2);
    digitalWrite(2, rpm_left_ref & 1);

    digitalWrite(11, rpm_right_ref & 16);
    digitalWrite(10, rpm_right_ref & 8);
    digitalWrite(9, rpm_right_ref & 4);
    digitalWrite(8, rpm_right_ref & 2);
    digitalWrite(7, rpm_right_ref & 1);

    delay(refreshRate);
  
}
