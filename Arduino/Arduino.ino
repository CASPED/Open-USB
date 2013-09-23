#include <NewPing.h>
#include <PID_v1.h>
#include <Servo.h>


/* Puertos
No. Puerto
  2 ->  Input 1
  3 ->  Input 2
  4 ->  Input 3
  5 ->  Input 4
  6 ->  Input 5
  7 ->  Input 6
  8 ->  Input 7
  9 ->  Input 8
*/




//***********************************************
//                                              *
//             Brazo y Garra                 *
//                                              *
//***********************************************

Servo garra;
Servo brazo;
int POLEA_SUBIR_PIN = 52;
int POLEA_BAJAR_PIN = 53;
int POLEA_SENSOR_ALTO_PIN = 34;
int POLEA_SENSOR_BAJO_PIN = 33;

//***********************************************
//                                              *
//             Sensores de proximidad              *
//                                              *
//***********************************************


#define MAX_DISTANCE 500 // Maximum distance we want to ping for (in centimeters). Maximum sensor distance is rated at 400-500cm.

int distancia[4];
NewPing sonar[4] = {     // Sensor object array.
  NewPing(40, 41, MAX_DISTANCE), // Each sensor's trigger pin, echo pin, and max distance to ping.
  NewPing(42, 43, MAX_DISTANCE),
  NewPing(44, 45, MAX_DISTANCE),
  NewPing(46, 47, MAX_DISTANCE),
};



//***********************************************
//                                              *
//                   SERVOS                     *
//                                              *
//***********************************************
     
          //Compuerta
     Servo compuerta1;
     Servo compuerta2;

// Pines de salida para los motores :)

    //*** Motor 1
const int out1 = 2;
const int out2 = 3;

    //*** Motor 2
const int out3 = 4;
const int out4 = 5;

    //*** Motor 3
const int out5 = 6;
const int out6 = 7;

    //*** Motor 4
const int out7 = 8;
const int out8 = 9;

//Velocidad
int vel;

//LEDs

int verde=49;
int rojo=50;
int blanco=51;
 

//Variable seleccionadora de estados
int opcion;

//Encoders

  int in1;
  int in2;
  int in3;
  int in4;

//Variables de medicion de tiempo

unsigned long high_v1;
unsigned long high_v2;
unsigned long high_v3;
unsigned long high_v4;
int c1;
int c2;
int c3;
int c4;
unsigned long pulso1;
unsigned long pulso2;
unsigned long pulso3;
unsigned long pulso4;

 //***********************************************
 //                                              *
 //                   PID                        *
 //                                              *
 //***********************************************
 
 double Setpoint, Input, Output;
 //Specify the links and initial tuning parameters
 double Kp = 0;
 double Ki = 0;
 double Kd = 0;
 PID cpid(&Input, &Output, &Setpoint,Kp,Ki,Kd,DIRECT);


void setup() 
{
  
 //***********************************************
 //                                              *
 //                   PID                        *
 //                                              *
 //***********************************************
 /*Setpoint = 100;
 
 //turn the PID on
  cpid.SetMode(AUTOMATIC);
 
*/
  //Inicio de puerto Serial
  Serial.begin(115200);
  
  //Velocidad maxima
   vel = 255;
   
   //Configuracion de pines
   pinMode(22,OUTPUT);
   pinMode(23,OUTPUT);
   pinMode(24,OUTPUT);
   pinMode(25,OUTPUT);
   pinMode(26,OUTPUT);
   pinMode(27,OUTPUT);
   pinMode(28,OUTPUT);
   pinMode(29,OUTPUT);
   pinMode(30,OUTPUT);
   pinMode(31,OUTPUT); 
   pinMode(32,OUTPUT);
   pinMode(33,OUTPUT); 
   pinMode(34,OUTPUT);
   pinMode(35,OUTPUT);
   
   // Pines de Efecto Hall
   
   pinMode(36,INPUT_PULLUP);
   pinMode(37,INPUT_PULLUP); 
   pinMode(38,INPUT_PULLUP);
   pinMode(39,INPUT_PULLUP);
   
   // Pines de sensores de Garra
   pinMode(POLEA_SENSOR_BAJO_PIN, INPUT_PULLUP);
   pinMode(POLEA_SENSOR_ALTO_PIN, INPUT_PULLUP); 
   
   //LEDS
  pinMode(verde,OUTPUT); //Verde
  pinMode(rojo,OUTPUT); //Rojo
  pinMode(blanco,OUTPUT); //Amarillo
   
   //Pines de alimentación
   
   digitalWrite(22,HIGH);
   digitalWrite(23,LOW);
   digitalWrite(24,HIGH);
   digitalWrite(25,LOW);
   digitalWrite(26,HIGH);
   digitalWrite(27,LOW);
   
   pararPolea();
   
        
    //Pines de los encoder
      //Motor No. 1
    pinMode(36,INPUT_PULLUP);
      //Motor No. 2
    pinMode(37,INPUT_PULLUP);
      //Motor No. 3
    pinMode(38,INPUT_PULLUP);
      //Motor No. 4
    pinMode(39,INPUT_PULLUP);
       
     //***********************************************
     //                                              *
     //                   SERVOS                     *
     //                                              *
     //***********************************************
     
          //Compuerta
     compuerta1.attach(10);
     compuerta2.attach(11);
     compuerta2.write(170);
     delay(100);
     compuerta1.write(5);
     
     
     
     
       
       /***********************************************
       Constantes para control de ruedas
       ***********************************************/
       float diametro1_3 = 0.102;
       float diametro2_4 = 0.127;
       
       //Sonido de entrada
       
     sonido(out1,out2,out3,out4,out5,out6,out7,out8,10);
     delay(500);
     sonido(out1,out2,out3,out4,out5,out6,out7,out8,0);
     delay(500);
     sonido(out1,out2,out3,out4,out5,out6,out7,out8,20);
     delay(500);
     sonido(out1,out2,out3,out4,out5,out6,out7,out8,0);
     
     // Luces de inicio
     
     digitalWrite(blanco,HIGH);
     sonido(out1,out2,out3,out4,out5,out6,out7,out8,10);
     delay(100);
     digitalWrite(verde,HIGH);
     sonido(out1,out2,out3,out4,out5,out6,out7,out8,0);
     delay(100);
     digitalWrite(rojo,HIGH);
     sonido(out1,out2,out3,out4,out5,out6,out7,out8,20); 
     delay(100);
     digitalWrite(blanco,LOW);
     sonido(out1,out2,out3,out4,out5,out6,out7,out8,0);
     delay(100);
     digitalWrite(verde,LOW);
     delay(100);
     digitalWrite(rojo,LOW); 
     delay(100);
     digitalWrite(rojo,HIGH);
     delay(100);
     digitalWrite(verde,HIGH);
     delay(100);
     digitalWrite(blanco,HIGH); 
     delay(100);
     digitalWrite(rojo,LOW);
     delay(100);
     digitalWrite(verde,LOW);
     delay(100);
     digitalWrite(blanco,LOW); 
     delay(100);
     
     garra.attach(12);
     brazo.attach(13);
     regresarBrazo();
}


/*

Comienzo del loop


*/








void loop()
{
 // avanzar(out1,out2,out3,out4,out5,out6,out7,out8,vel);
  /*
  ancho_pulso(36,&c1,&high_v1,&pulso1);
  
  
  Input = (unsigned long)pulso1;
  
  Input = map(Input,129,8,0,255);
  if(pulso1 == 0) Input = 0; 
  
  Serial.print("Input: ");
  Serial.println(Input);
  cpid.Compute();
  vel = Output;
  Serial.print("Output: ");
  Serial.println(vel);
  */
  
  
  //Obtencion de los estados
  if(Serial.available()>0)
    {
      opcion = Serial.read();
      //Serial.print("r");
      switch(opcion)
        {
          
     //***********************************************
     //                                              *
     //         Seleccion de estados                 *
     //                                              *
     //***********************************************
          
          //Inicializar estado mecanico:
          case 'i':
            regresarBrazo();
            break;
     
          case 'd': 
          //do{
          digitalWrite(rojo,LOW);  
          digitalWrite(blanco,HIGH); 
          digitalWrite(verde,HIGH);   
          girarDerecha(out1,out2,out3,out4,out5,out6,out7,out8,vel);
          //} while(Serial.available() <= 0); 
          
          break;
          
          
          case 'a': 
          //do{
          digitalWrite(blanco,LOW);   
          digitalWrite(verde,HIGH); 
          digitalWrite(rojo,HIGH);   
          girarIzquierda(out1,out2,out3,out4,out5,out6,out7,out8,vel);
          //} while(Serial.available() <= 0); 
           
          break;
          
          
          case 'w': 
          
          
             //do{
           digitalWrite(rojo,LOW); 
           digitalWrite(blanco,LOW);     
           digitalWrite(verde,HIGH); //Verde
          avanzar(out1,out2,out3,out4,out5,out6,out7,out8,vel);
           
          
          //} while(Serial.available() <= 0); 
              break;
          
          
          case 's': 
          
           //do{   
          digitalWrite(verde,LOW);    
          digitalWrite(rojo,LOW); 
          digitalWrite(blanco,HIGH);    
          retroceder(out1,out2,out3,out4,out5,out6,out7,out8,vel);
          //} while(Serial.available() <= 0); 
          break;
          
          
          case 'p': 
          
           //do{
          digitalWrite(blanco,LOW); 
          digitalWrite(verde,LOW);    
          digitalWrite(rojo,HIGH);   
          detener(out1,out2,out3,out4,out5,out6,out7,out8,vel);
          
          agarrarLata();
         //} while(Serial.available() <= 0); 
          break;
          
          
          
          
          
          //****************************************************
          
          //Seleccion de la velocidad
          
              //cuando llega v, se realiza el aumento de la velocidad hasta 255 (max -> 11.1 v)
              //y se devuelve a 0
              
          //*****************************************************
            
          case 'v':
          if(vel < 255)
          {
            vel = vel + 5;
            Serial.print("Velocidad: ");
            Serial.println(vel);
          }
          else 
            {  
              vel = 0;
              Serial.print("Velocidad: ");
              Serial.println(vel);
              
            }
          
          break;
          
          
          
          
     //***********************************************
     //                                              *
     //                 Sensor ultrasonico                    *
     //                                              *
     //***********************************************
     
     
          case '3': 
          
          Serial.print("Distancia: ");
          //Se guarda las distancias en el arreglo
          for(int k = 0; k < 4; k++)
            {
              distancia[k] = sonar[k].ping_cm(); 
              Serial.println(distancia[k]);
              
            }
          Serial.println(" cm");
          
          
      
          break;
     
     
     
     
     
     //***********************************************
     //                                              *
     //                  Servos                    *
     //                                              *
     //***********************************************
          
          
          case '1': 
          
          digitalWrite(blanco,HIGH); 
          digitalWrite(verde,LOW);    
          digitalWrite(rojo,HIGH); 
           compuerta2.write(170);
           delay(100);
           compuerta1.write(5);
           
          break;
          
          case '2': 
         
          digitalWrite(verde,HIGH);    
           
           compuerta2.write(50);
           compuerta1.write(120);
          break;    
          
          case 'b': 
           agarrarLata();
          break; 
          
        
      
       //***********************************************
     //                                              *
     //                  PID Tuning                    *
     //                                              *
     //***********************************************
      /*
        case '4': 
         
         if(Kp >= 2) Kp = 0;
         else
             {
               Kp = Kp + 0.1;
               //SetTunings(Kp , Ki, Kd);  
             }
          
          break;  
          
          case '5': 
         
         if(Ki >= 2) Ki = 0;
         else
             {
               Ki = Ki + 0.1;
               SetTunings(Kp , Ki, Kd);  
             }
           
          
          break;  
          
          case '6': 
         
         if(Kd >= 2) Kd = 0;
         else
             {
               Kd = Kd + 0.1;
               SetTunings(Kp , Ki, Kd);  
             }
           
          
          break;  */
          
          default:
            return;
        }
        //Solo lo llama si no cae en default;
        Serial.write('r');
    }
    /*
 girarDerecha(out1,out2,out3,out4,out5,out6,out7,out8,vel);
 delay(4000);

 girarIzquierda(out1,out2,out3,out4,out5,out6,out7,out8,vel);
 delay(4000);
 avanzar(out1,out2,out3,out4,out5,out6,out7,out8,vel);
 delay(4000);
 retroceder(out1,out2,out3,out4,out5,out6,out7,out8,vel);
 delay(4000);
 detener(out1,out2,out3,out4,out5,out6,out7,ut8,vel);
 delay(4000);
 
 */
  
     //***********************************************
     //                                              *
     //                  Control de Ruedas           *
     //                                              *
     //***********************************************
  /*
    unsigned long tiempo[4];
    ancho_pulso(36,&c1,&high_v1,&tiempo[0]);
    ancho_pulso(37,&c2,&high_v2,&tiempo[1]);
    ancho_pulso(38,&c3,&high_v3,&tiempo[2]);
    ancho_pulso(39,&c4,&high_v4,&tiempo[3]);
   
    for(int k = 0 ; k < 4 ; k++) 
    {
      if( tiempo[k] == 0 ) Serial.write(k);
    }
  
*/      


           
        
  
}



/*
Permite girar sobre su propio eje en sentido antihorario
*/
void girarIzquierda(const int mot1_pin1, const int mot1_pin2,const int mot2_pin1, const int mot2_pin2,const int mot3_pin1, const int mot3_pin2,const int mot4_pin1, const int mot4_pin2,const int vel)
  {
    //Motor 1
    analogWrite(mot1_pin1,0);
    analogWrite(mot1_pin2,vel);
    
     //Motor 2
    analogWrite(mot2_pin1,0);
    analogWrite(mot2_pin2,vel);
    
     //Motor 3
    analogWrite(mot3_pin1,vel);
    analogWrite(mot3_pin2,0);
    
     //Motor 4
    analogWrite(mot4_pin1,vel);
    analogWrite(mot4_pin2,0);
    
  }
  
  
  
 /*
Permite girar sobre su propio eje en sentido horario
*/ 
void girarDerecha(const int mot1_pin1, const int mot1_pin2,const int mot2_pin1, const int mot2_pin2,const int mot3_pin1, const int mot3_pin2,const int mot4_pin1, const int mot4_pin2,const int vel)
  {
    //Motor 1
    analogWrite(mot1_pin1,vel);
    analogWrite(mot1_pin2,0);
    
     //Motor 2
    analogWrite(mot2_pin1,vel);
    analogWrite(mot2_pin2,0);
    
     //Motor 3
    analogWrite(mot3_pin1,0);
    analogWrite(mot3_pin2,vel);
    
     //Motor 4
    analogWrite(mot4_pin1,0);
    analogWrite(mot4_pin2,vel);
    
    
    
  }
  
  /*
Permite avanzar
*/ 
void avanzar(const int mot1_pin1, const int mot1_pin2,const int mot2_pin1, const int mot2_pin2,const int mot3_pin1, const int mot3_pin2,const int mot4_pin1, const int mot4_pin2,const int vel)
  {
    //Motor 1
    analogWrite(mot1_pin1,vel);
    analogWrite(mot1_pin2,0);
    
     //Motor 2
    analogWrite(mot2_pin1,vel);
    analogWrite(mot2_pin2,0);
    
     //Motor 3
    analogWrite(mot3_pin1,vel);
    analogWrite(mot3_pin2,0);
    
     //Motor 4
    analogWrite(mot4_pin1,vel);
    analogWrite(mot4_pin2,0);
    
    
    
  }
  

/*
Permite retroceder
*/ 
void retroceder(const int mot1_pin1, const int mot1_pin2,const int mot2_pin1, const int mot2_pin2,const int mot3_pin1, const int mot3_pin2,const int mot4_pin1, const int mot4_pin2,const int vel)
  {
    //Motor 1
    analogWrite(mot1_pin1,0);
    analogWrite(mot1_pin2,vel);
    
     //Motor 2
    analogWrite(mot2_pin1,0);
    analogWrite(mot2_pin2,vel);
    
     //Motor 3
    analogWrite(mot3_pin1,0);
    analogWrite(mot3_pin2,vel);
    
     //Motor 4
    analogWrite(mot4_pin1,0);
    analogWrite(mot4_pin2,vel);
    
  }
  
  void detener(const int mot1_pin1, const int mot1_pin2,const int mot2_pin1, const int mot2_pin2,const int mot3_pin1, const int mot3_pin2,const int mot4_pin1, const int mot4_pin2,const int vel)
  {
    //Motor 1
    analogWrite(mot1_pin1,0);
    analogWrite(mot1_pin2,0);
    
     //Motor 2
    analogWrite(mot2_pin1,0);
    analogWrite(mot2_pin2,0);
    
     //Motor 3
    analogWrite(mot3_pin1,0);
    analogWrite(mot3_pin2,0);
    
     //Motor 4
    analogWrite(mot4_pin1,0);
    analogWrite(mot4_pin2,0);
    
  }
  
  
  void sonido(const int mot1_pin1, const int mot1_pin2,const int mot2_pin1, const int mot2_pin2,const int mot3_pin1, const int mot3_pin2,const int mot4_pin1, const int mot4_pin2,const int vel)
  {
    //Motor 1
    analogWrite(mot1_pin1,vel);
    analogWrite(mot1_pin2,0);
    
     //Motor 2
    analogWrite(mot2_pin1,vel);
    analogWrite(mot2_pin2,0);
    
     //Motor 3
    analogWrite(mot3_pin1,vel);
    analogWrite(mot3_pin2,0);
    
     //Motor 4
    analogWrite(mot4_pin1,vel);
    analogWrite(mot4_pin2,0);
    
    
    
  }
  
  
  
     //***********************************************
     //                                              *
     //               Medicion de tiempo           *
     //                                              *
     //***********************************************
  
   // Imprime la duracion del pulso (cuando el sensor detecta el iman) en milisegundos
  // Imprime la duracion del pulso (cuando el sensor detecta el iman) en milisegundos
void ancho_pulso(const int encoder, int *c, unsigned long *high_value, unsigned long *duracion)
  {
  
  unsigned long low_value=0;
  unsigned long espera=0;
  //unsigned long duracion=7;
  unsigned long contador=0;
 
   if(digitalRead(encoder)==0){
    

       low_value=millis(); 
      
      // Serial.print("var= ");
       //Serial.println(low);
      
       while(digitalRead(encoder)==0 && espera!=500){
      
          espera=millis()-low_value;
          
        }
        
        if(digitalRead(encoder)==1){
 
 
           *high_value=millis();           
           
           *duracion = millis()-low_value;

        }
        
        
        
        else   *duracion=0;
     
      // Serial.flush(); 
    
       
       *c=1;
       
       //return duracion;
       return;
       
     }
     
     
     
     if(*c){
     
     contador=millis() - *high_value;
     
     if(contador>500){
     
         *duracion=0;
         
         //return duracion;
         
         return;
       
     } 
     
     }
     
     
    
    }
  
  
  
  
  
  
    
/*
*
*
*            GARRA Y BRAZO
*
*
*
*/

void regresarBrazo() {
    subirPolea();
    while(true) if(digitalRead(POLEA_SENSOR_ALTO_PIN) == LOW) break;
    pararPolea();
    
    brazo.write(0);
    delay(1500);
    garra.write(120);
    delay(600);
    garra.write(100);
}

void agarrarLata() {
    brazo.write(100);
    delay(700);
    
    bajarPolea();
    while(true) if(digitalRead(POLEA_SENSOR_BAJO_PIN) == LOW) break;
    pararPolea();
    
    garra.write(120);
    brazo.write(179);
    delay(1000);
    garra.write(0);
    delay(2500);
    brazo.write(40);

    regresarBrazo();
}

void bajarPolea() {
  digitalWrite(POLEA_BAJAR_PIN, HIGH);
  digitalWrite(POLEA_SUBIR_PIN, LOW);
}

void pararPolea() {
  digitalWrite(POLEA_BAJAR_PIN, LOW);
  digitalWrite(POLEA_SUBIR_PIN, LOW);
}

void subirPolea() {
  digitalWrite(POLEA_BAJAR_PIN, LOW);
  digitalWrite(POLEA_SUBIR_PIN, HIGH);
}


 /*
       MOVVERTICAL --> posicion del servo: 512   (90 grados)
       HORIZONTALF --> posicion del servo: 0     (0 grados)
       HORIZONTALB --> posicion del servo: 1023  (180 grados)
       DIAGONALF   --> posicion del servo: 256   (45 grados)
       DIAGONALB   --> posicion del servo: 767   (135 grados)
     
 */
 /*
 void MovVertical(int PinAttach){
  
   Medio.attach(PinAttach);
   int Val = 512;
   Val = map(Val,0,1023,0,179);
   Medio.write(Val);
   delay(2000);
 }
 
  void HorizontalF(int PinAttach){
  
   Medio.attach(PinAttach); 
   int Val = 0;
   Val = map(Val,0,1023,0,179);
   Medio.write(Val);
   delay(2000);
 }
 
  void HorizontalB(int PinAttach){
  
   Medio.attach(PinAttach); 
   int Val = 1023;
   Val = map(Val,0,1023,0,179);
   Medio.write(Val);
   delay(2000);
 }
 
 
  void DiagonalF(int PinAttach){
  
   Medio.attach(PinAttach); 
   int Val = 256;
   Val = map(Val,0,1023,0,179);
   Medio.write(Val);
   delay(2000);
 }
 
  void DiagonalB(int PinAttach){
  
   Medio.attach(PinAttach); 
   int Val = 767;
   Val = map(Val,0,1023,0,179);
   Medio.write(Val);
   delay(2000);
 }
 
*/




