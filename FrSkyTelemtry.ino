// ****************************************************************
// FrSky telemetry
// Version: 0.2.2
// Date 7/10/2012
// Changes: V0.2.2: - corrected ID_Course_ap is 0x1C not 0x24 protocol says 0x14+8
// Date 20/09/2012
// Changes: V0.2.1: - make it work with 2.1 (shared dev)
// Date: 14/08/2012
// Changes: V0.2: - Byte stuffing added
//                - vBat will be send, if "#define FAS_100" is comment out
//                V0.1: - First release
// ****************************************************************

#if defined(TELEMETRY_FRSKY)
// user defines
//#define FAS_100  //if commment out, MultiWii vBat voltage will be send instead of FrSky FAS 100 voltage


// Serial config datas
#define TELEMETRY_FRSKY_SERIAL 3 
#define TELEMETRY_FRSKY_BAUD 9600  

// Timing
#define Time_telemetry_send 125000 
static uint8_t cycleCounter = 0;
static uint32_t FrSkyTime  = 0;

// Frame protocol
#define Protocol_Header   0x5E
#define Protocol_Tail      0x5E


// Data Ids  (bp = before point; af = after point)
// Official data IDs
#define ID_GPS_altidute_bp    0x01
#define ID_GPS_altidute_ap    0x09
#define ID_Temprature1        0x02
#define ID_RPM                0x03
#define ID_Fuel_level         0x04
#define ID_Temprature2        0x05
#define ID_Volt               0x06
#define ID_Altitude_bp        0x10
#define ID_Altitude_ap        0x21
#define ID_GPS_speed_bp       0x11
#define ID_GPS_speed_ap       0x19
#define ID_Longitude_bp       0x12
#define ID_Longitude_ap       0x1A
#define ID_E_W                0x22
#define ID_Latitude_bp        0x13
#define ID_Latitude_ap        0x1B
#define ID_N_S                0x23
#define ID_Course_bp          0x14
#define ID_Course_ap          0x1C
#define ID_Date_Month         0x15
#define ID_Year               0x16
#define ID_Hour_Minute        0x17
#define ID_Second             0x18
#define ID_Acc_X              0x24
#define ID_Acc_Y              0x25
#define ID_Acc_Z              0x26
#define ID_Voltage_Amp_bp     0x3A
#define ID_Voltage_Amp_ap     0x3B
#define ID_Current            0x28
// User defined data IDs
#define ID_Gyro_X             0x40
#define ID_Gyro_Y             0x41
#define ID_Gyro_Z             0x42

//Multiwii EZ-GUI
#define ID_Ang_X             0x50
#define ID_Ang_Y             0x51
#define ID_State             0x52 


// Main function FrSky telemetry
void telemetry_frsky() 
{         
  if (currentTime > FrSkyTime ) // 
  {          
    FrSkyTime = currentTime + Time_telemetry_send;
    cycleCounter++;
    // Datas sent every 125 ms
    //send_Accel();
    sendAngles();
    sendDataTail();   

    if ((cycleCounter % 4) == 0) 
    {      
      // Datas sent every 500ms
      send_Altitude();
      send_RPM();
      send_Course();
      send_GPS_speed();
      //send_Cell_volt();       todo
      sendDataTail();   
      

    }
    if ((cycleCounter % 8) == 0) 
    {      
      // Datas sent every 1s            
      send_Time();
      send_GPS_position();
      send_GPS_altitude();
      send_Temperature2();  // Distance_to_home
      //send_Fuel_level();
      send_Voltage_ampere();
      send_Temperature1();  // num of Sats
      sendDataTail();            
    }

    if (cycleCounter == 40) 
    {
      // Datas sent every 5s
      cycleCounter = 0;       
    }
  }
}

void write_FrSky8(uint8_t Data)
{
  SerialWrite(TELEMETRY_FRSKY_SERIAL, Data);
}

void write_FrSky16(uint16_t Data)
{
  uint8_t Data_send;
  Data_send = Data;      
  check_FrSky_stuffing(Data_send);
  Data_send = Data >> 8 & 0xff;
  check_FrSky_stuffing(Data_send);
}

void check_FrSky_stuffing(uint8_t Data) //byte stuffing
{
  if (Data == 0x5E)   
  {
    write_FrSky8(0x5D);
    write_FrSky8(0x3E);
  }
  else if (Data == 0x5D)   
  {
    write_FrSky8(0x5D);
    write_FrSky8(0x3D);
  }
  else
  {
    write_FrSky8(Data);         
  }
}

static void sendDataHead(uint8_t Data_id)
{
  write_FrSky8(Protocol_Header);
  write_FrSky8(Data_id);
}

static void sendDataTail(void)
{
  write_FrSky8(Protocol_Tail);      
}


//*********************************************************************************
//-----------------   Telemetrie Datas   ------------------------------------------   
//*********************************************************************************

// GPS altitude
void send_GPS_altitude(void)
{          
  if (f.GPS_FIX && GPS_numSat >= 4) 
  {
    int16_t Datas_GPS_altidute_bp;
    uint16_t Datas_GPS_altidute_ap;

    Datas_GPS_altidute_bp = GPS_altitude;
    Datas_GPS_altidute_ap = 0;

    sendDataHead(ID_GPS_altidute_bp);
    write_FrSky16(Datas_GPS_altidute_bp);
    sendDataHead(ID_GPS_altidute_ap);
    write_FrSky16(Datas_GPS_altidute_ap);
  }
}

// Temperature
void send_Temperature1(void)
{
  int16_t Datas_Temprature1;

  Datas_Temprature1 = GPS_numSat;  // Number of Satalits alias Temp1

  sendDataHead(ID_Temprature1);
  write_FrSky16(Datas_Temprature1); 
}

// RPM
void send_RPM(void)
{
  uint16_t Datas_RPM = 0;      
  for (uint8_t i=0;i<NUMBER_MOTOR;i++)
  {
    Datas_RPM += motor[i];
  } 
  Datas_RPM = (Datas_RPM / NUMBER_MOTOR) / 30;   // RPM 

  sendDataHead(ID_RPM);
  write_FrSky16(Datas_RPM);
}

// Fuel level
void send_Fuel_level(void)
{
  uint16_t Datas_Fuel_level;

  Datas_Fuel_level = 0; 

  sendDataHead(ID_Fuel_level);
  write_FrSky16(Datas_Fuel_level); 
}

// Temperature 2
void send_Temperature2(void)
{
  if (f.GPS_FIX_HOME)
  {
    int16_t Datas_Temprature2;

    Datas_Temprature2 = GPS_distanceToHome; // Distance to home alias Temp2

    sendDataHead(ID_Temprature2);
    write_FrSky16(Datas_Temprature2);  
  }      
}

// Cell voltage  todo !!!!!!!!!!!!!!!!!!
void send_Cell_volt(void) // Datas FrSky FLVS-01 voltage sensor
{
  uint16_t Datas_Volt;
  uint8_t number_of_cells = 0;   // LiPo 3S = 3; LiPo 4S = 4 ...
  static uint8_t cell = 0;
  if (cell >= number_of_cells); 
  cell = 0;

  Datas_Volt = 0; // 0.01v / 0 ~ 4.2v

  sendDataHead(ID_Volt);
  write_FrSky16(Datas_Volt); 
}

// Altitude
void send_Altitude(void)
{
  uint16_t Datas_altitude_bp;
  uint16_t Datas_altitude_ap;
  static uint16_t Start_altitude;

  if (!f.ARMED)
  {
    Start_altitude = EstAlt / 100;
  }

  Datas_altitude_bp = (EstAlt / 100) - Start_altitude;
  Datas_altitude_ap = (EstAlt % 100);

  sendDataHead(ID_Altitude_bp);
  write_FrSky16(Datas_altitude_bp);
  sendDataHead(ID_Altitude_ap);
  write_FrSky16(Datas_altitude_ap);
}

// GPS speed
void send_GPS_speed(void)
{
  if (f.GPS_FIX && GPS_numSat >= 4) 
  {            
    uint16_t Datas_GPS_speed_bp;
    uint16_t Datas_GPS_speed_ap;

    Datas_GPS_speed_bp = GPS_speed * 0.036;
    Datas_GPS_speed_ap = 0;

    sendDataHead(ID_GPS_speed_bp);
    write_FrSky16(Datas_GPS_speed_bp);
    sendDataHead(ID_GPS_speed_ap);
    write_FrSky16(Datas_GPS_speed_ap);
  }
}

// GPS position
void send_GPS_position(void)
{
  uint16_t Datas_Longitude_bp;
  uint16_t Datas_Longitude_ap;
  uint16_t Datas_E_W;
  uint16_t Datas_Latitude_bp;
  uint16_t Datas_Latitude_ap;
  uint16_t Datas_N_S;
  Datas_Longitude_bp = abs(GPS_coord[LON]) / 100000;
  Datas_Longitude_ap = abs((GPS_coord[LON])/10)  % 10000;
  Datas_E_W = GPS_coord[LON] < 0 ? 'W' : 'E';
  Datas_Latitude_bp = abs(GPS_coord[LAT]) / 100000;
  Datas_Latitude_ap = abs((GPS_coord[LAT])/10) % 10000;
  Datas_N_S = GPS_coord[LAT] < 0 ? 'S' : 'N';

  sendDataHead(ID_Longitude_bp);
  write_FrSky16(Datas_Longitude_bp);
  sendDataHead(ID_Longitude_ap);
  write_FrSky16(Datas_Longitude_ap);
  sendDataHead(ID_E_W);
  write_FrSky16(Datas_E_W);

  sendDataHead(ID_Latitude_bp);
  write_FrSky16(Datas_Latitude_bp);
  sendDataHead(ID_Latitude_ap);
  write_FrSky16(Datas_Latitude_ap);
  sendDataHead(ID_N_S);
  write_FrSky16(Datas_N_S);

}

// Course
void send_Course(void)
{
  uint16_t Datas_Course_bp;
  uint16_t Datas_Course_ap;

  Datas_Course_bp = heading;
  Datas_Course_ap = 0;

  sendDataHead(ID_Course_bp);
  write_FrSky16(Datas_Course_bp);
  sendDataHead(ID_Course_ap);
  write_FrSky16(Datas_Course_ap);
}

// Time
void send_Time(void)
{
  uint32_t seconds_since_start = millis() / 1000;

  uint16_t Datas_Date_month;
  uint16_t Datas_Year;
  uint16_t Datas_Minutes_hours;
  uint16_t Datas_seconds;

  Datas_Date_month = 0;
  Datas_Year = 12;
  Datas_Minutes_hours = (seconds_since_start / 60) % 60;
  Datas_seconds = seconds_since_start % 60;      

  sendDataHead(ID_Hour_Minute);
  write_FrSky16(Datas_Minutes_hours);
  sendDataHead(ID_Second);
  write_FrSky16(Datas_seconds);
}

// ACC
void send_Accel(void)
{
  int16_t Datas_Acc_X;
  int16_t Datas_Acc_Y;
  int16_t Datas_Acc_Z;

  Datas_Acc_X = ((float)accSmooth[0] / acc_1G) * 1000;
  Datas_Acc_Y = ((float)accSmooth[1] / acc_1G) * 1000;
  Datas_Acc_Z = ((float)accSmooth[2] / acc_1G) * 1000;

  sendDataHead(ID_Acc_X);
  write_FrSky16(Datas_Acc_X);
  sendDataHead(ID_Acc_Y);
  write_FrSky16(Datas_Acc_Y);
  sendDataHead(ID_Acc_Z);
  write_FrSky16(Datas_Acc_Z);      
}


// angles EZ-GUI
void sendAngles(void)
{
  int16_t Datas_Ang_X;
  int16_t Datas_Ang_Y;
  //  int16_t Datas_Acc_Z;

  Datas_Ang_X = angle[0];//((float)accSmooth[0] / acc_1G) * 1000;
  Datas_Ang_Y = angle[1];//((float)accSmooth[1] / acc_1G) * 1000;
  // Datas_Acc_Z = angle[2];//((float)accSmooth[2] / acc_1G) * 1000;

  sendDataHead(ID_Ang_X);
  write_FrSky16(Datas_Ang_X);
  sendDataHead(ID_Ang_Y);
  write_FrSky16(Datas_Ang_Y);
  // sendDataHead(ID_Acc_Z);
  // write_FrSky16(Datas_Acc_Z);      
}

// Voltage (Ampere Sensor)  
void send_Voltage_ampere(void)
{

#if defined (FAS_100)   // todo   !!!!!!!!!!!!!!!!!
  {
    uint16_t Datas_Voltage_Amp_bp;
    uint16_t Datas_Voltage_Amp_ap;
    uint16_t Datas_Current;   

    Datas_Voltage_Amp_bp = 0;
    Datas_Voltage_Amp_ap = 0;   
    Datas_Current = 0;

    sendDataHead(ID_Voltage_Amp_bp);
    write_FrSky16(Datas_Voltage_Amp_bp);
    sendDataHead(ID_Voltage_Amp_ap);
    write_FrSky16(Datas_Voltage_Amp_ap);   
    sendDataHead(ID_Current);
    write_FrSky16(Datas_Current);
  }
#else   // use vBat
  {
    uint16_t Datas_Voltage_vBat_bp;
    uint16_t Datas_Voltage_vBat_ap;   
    uint16_t voltage;
    voltage = (vbat * 110) / 21;          
    Datas_Voltage_vBat_bp = voltage / 100;
    Datas_Voltage_vBat_ap = ((voltage % 100) + 5) / 10;         

    sendDataHead(ID_Voltage_Amp_bp);
    write_FrSky16(Datas_Voltage_vBat_bp);
    sendDataHead(ID_Voltage_Amp_ap);
    write_FrSky16(Datas_Voltage_vBat_ap);   
  }
#endif
}

#endif


