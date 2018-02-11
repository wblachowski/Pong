#include <Wire.h>



#define    MPU9250_ADDRESS            0x68
#define    MAG_ADDRESS                0x0C

#define    GYRO_FULL_SCALE_250_DPS    0x00  
#define    GYRO_FULL_SCALE_500_DPS    0x08
#define    GYRO_FULL_SCALE_1000_DPS   0x10
#define    GYRO_FULL_SCALE_2000_DPS   0x18

#define    ACC_FULL_SCALE_2_G        0x00  
#define    ACC_FULL_SCALE_4_G        0x08
#define    ACC_FULL_SCALE_8_G        0x10
#define    ACC_FULL_SCALE_16_G       0x18



// This function read Nbytes bytes from I2C device at address Address. 
// Put read bytes starting at register Register in the Data array. 
void I2Cread(uint8_t Address, uint8_t Register, uint8_t Nbytes, uint8_t* Data)
{
  // Set register address
  Wire.beginTransmission(Address);
  Wire.write(Register);
  Wire.endTransmission();
  
  // Read Nbytes
  Wire.requestFrom(Address, Nbytes); 
  uint8_t index=0;
  while (Wire.available())
    Data[index++]=Wire.read();
}


// Write a byte (Data) in device (Address) at register (Register)
void I2CwriteByte(uint8_t Address, uint8_t Register, uint8_t Data)
{
  // Set register address
  Wire.beginTransmission(Address);
  Wire.write(Register);
  Wire.write(Data);
  Wire.endTransmission();
}

float angle;
int readAngle(){
  uint8_t Buf[14];
  I2Cread(MPU9250_ADDRESS,0x3B,14,Buf);
  
  // Accelerometer
  int16_t ay=-(Buf[2]<<8 | Buf[3]);

  // Gyroscope
  int16_t gx=-(Buf[8]<<8 | Buf[9]);

  float a = float(ay)/131.0;
  float b = float(gx)/182.0;
  float dtC = 0.010;
  float tau=0.075;
  float A=tau/(tau+dtC);
  angle=A*(angle+b*0.02)+(1-A)*a;
  return angle;
}

int buttons[]={8,7,6,5,4,3};
char buttons_chars[]={'W','S','A','D','Y','X'};
int latest[]={0,0,0,0,0,0};
void readButtons(){
  for(int i=0;i<(sizeof(buttons)/sizeof(*buttons));i++){
    int current = digitalRead(buttons[i]);
    if(latest[i]==0 && current==1){
      Serial.println(buttons_chars[i]);
    }
    latest[i]=current;
  }
}

float initAngle=0;
// Initializations
int B=9;
int G=10;
int R=11;
void setup()
{
  // Arduino initializations
  Wire.begin();
  Serial.begin(19200);
  //19200 standalone atmega328p
  //9600 arduino
  // Configure gyroscope range
  I2CwriteByte(MPU9250_ADDRESS,27,GYRO_FULL_SCALE_250_DPS);
  // Configure accelerometers range
  I2CwriteByte(MPU9250_ADDRESS,28,ACC_FULL_SCALE_2_G);
  // Set by pass mode for the magnetometers
  I2CwriteByte(MPU9250_ADDRESS,0x37,0x02);
  
  // Request first magnetometer single measurement
  I2CwriteByte(MAG_ADDRESS,0x0A,0x01);
  angle=0.0;
  //enabled?
  int sumAngle=0;
  delay(100);
  for(int i=0;i<100;i++){
    sumAngle+=readAngle();
    delay(1);
  }
  initAngle = sumAngle/100;
  for(int i=0;i<(sizeof(buttons)/sizeof(*buttons));i++){
    pinMode(buttons[i],INPUT);
  }
  pinMode(R,OUTPUT);
  pinMode(G,OUTPUT);
  pinMode(B,OUTPUT);
  analogWrite(R,255);
  analogWrite(G,255);
}


long int cpt=0;
// Main loop, read and display data
bool blocked=false;
unsigned long block_time=0;
void loop()
{
  if (Serial.available() > 0) {
    // read the incoming byte:
    int incomingByte = Serial.read();
    Serial.print("rec: ");
    Serial.println(incomingByte, DEC);
    if(incomingByte==48 || incomingByte==49){blocked=true;block_time=millis();}
    if(incomingByte==48){analogWrite(R,210);analogWrite(G,255);analogWrite(B,255);}
    if(incomingByte==49){analogWrite(R,255);analogWrite(G,210);analogWrite(B,255);}
  }
  Serial.println((int)readAngle());
  if(blocked && millis()-block_time>500){blocked=false;analogWrite(R,255);analogWrite(G,255);}
  if(!blocked)analogWrite(B,max(0,255-abs(angle)));

  readButtons();
  delay(10);    
}
