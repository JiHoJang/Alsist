// define rpm variables
  #include <Wire.h>
  #include <Adafruit_MCP4725.h>

  Adafruit_MCP4725 dac;
  
  volatile int revs = 0;
  double rpm = 0.0;
  double err_prev = 0.0;
  unsigned long lastmillis = 0;
  double rpm_ref = 0.0;
  double dac_value = 1400;
  double dac_value_limit = 0.0;
  double dac_limit_high = 4000.0;
  double dac_limit_low = 1400.0;
  double temp = 0.0;


#define rpmPin 2

double Ki = 0.13;
double Kp = 10.5;
double Kb = 0.1; // 1/Kp
double Kd = 0.08;

// refreshrate of data logging/printing in ms (only full seconds!)
int refreshRate = 31;
double Period = 0.031;
  
void setup() {
// connect at 115200 
  Serial.begin(115200);
  
  dac.begin(0x60);
    
// define pinmodes
  pinMode(rpmPin, INPUT);
  pinMode(9, INPUT); // 16
  pinMode(8, INPUT); // 8
  pinMode(7, INPUT); // 4
  pinMode(6, INPUT); // 2
  pinMode(5, INPUT); // 1
// setup interrupt for counting engine revolutions
  attachInterrupt(digitalPinToInterrupt(rpmPin), rpm_engine, FALLING);
}

void loop() {
  
// RPM reading & logging/printing of data 
  if (millis() - lastmillis >= refreshRate) { // update every one second, this will be equal to reading frequency(Hz) 
    detachInterrupt(digitalPinToInterrupt(rpmPin)); // disable interrupt when calculating 
      
    // RPM logging/printing
    rpm = (double)revs / 32;//(refreshRate / 1000); // convert frequency to RPM, this works for one interruption per full rotation.

    // read reference rpm
    rpm_ref = (double) (digitalRead(9) * 16 + digitalRead(8) * 8 + digitalRead(7) * 4 + digitalRead(6) * 2 + digitalRead(5)) / 10;
    if (rpm_ref == 0.1)
      rpm_ref = 0.0;
    else if (rpm_ref == 3.1 || rpm_ref == 0.0)
      rpm_ref = rpm;
    
    dac_value += PI_controller(rpm_ref - rpm, Kp, Ki, dac_value_limit, Kb, Kd, Period, &err_prev, &temp);

    if (dac_value > dac_limit_high) {
      dac_value_limit = dac_value - dac_limit_high;
      dac_value = dac_limit_high;
    }
    else if (dac_value < dac_limit_low) {
      dac_value_limit = dac_value - dac_limit_low;
      dac_value = dac_limit_low;
    }
    else
      dac_value_limit = 0;

    if (rpm_ref == 0.1) {
      dac_value = dac_limit_low;
      temp = 0.0;
    }

    dac.setVoltage(dac_value, false);
    
    Serial.print("RPM: ");
    Serial.print(rpm);
    Serial.print("   ");
    Serial.print(rpm_ref);
    Serial.print("  DAC value: ");
    Serial.println(dac_value);
   
    // Finish line  
    //Serial.println(" ");     
      
    revs = 0; // restart the RPM counter 
    lastmillis = millis(); // update lastmillis 
    attachInterrupt(digitalPinToInterrupt(rpmPin), rpm_engine, FALLING); // Enable interrupt 
  }
}

void rpm_engine() { 
  revs++; }

  double PI_controller(double err, double kp, double ki, double lim_err, double kb, double kd, double period, double* err_prev, double* temp) {
    *temp += (err - lim_err * kb) * period;
    return kp * err + ki * *temp + kd * (err - *err_prev)/period;
  }
