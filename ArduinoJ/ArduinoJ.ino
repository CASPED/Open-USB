#include <NewPing.h>
#include <Servo.h>
#include <Arduino.h>
#include "motor.h"

// Declaracion de los sensores
#define MAX_DISTANCE 500 // Maximum distance we want to ping for (in centimeters). Maximum sensor distance is rated at 400-500cm.
NewPing sonar[2] = {     // Sensor object array.
  NewPing(38, 39, MAX_DISTANCE), // Each sensor's trigger pin, echo pin, and max distance to ping.
  NewPing(40, 41, MAX_DISTANCE)};

int sonarIzq; // izquierda
int sonarDer; // derecha

//Declaración de los motores y sus pines (spd_pin, dirF_pin, dirB_pin)
Motor motorRF = {9, 26, 27};
Motor motorRB = {7, 28, 29};

Motor motorLF = {6, 50, 51};
Motor motorLB = {4, 48, 49};
// motores de la garra
Motor motorBrazo = {11,30,31};
Motor motorGarra = {12, 32, 33};

// posicion del brazo
int brazoArriba = 550;
int brazoAbajo = 0;
int goalBrazo = brazoArriba; // Arriba o abajo

const int feedback = A0;

int speed = 100;

void setup() {
    // initialize serial communication at 9600 bits per second:
    Serial.begin(115200);
    Serial.println("HOLA :)");
    
    motorRF.init();
    motorRB.init();
    motorLF.init();
    motorLB.init();
    motorBrazo.init();
    motorGarra.init();
    
    goalBrazo = 550;
    while(abs(potenciometro()) > 50);
}

 void forward() {
    motorRF.forward(speed);
    motorRB.forward(speed);
    motorLF.forward(speed);
    motorLB.forward(speed);
}

void backward() {
    motorRF.backward(speed);
    motorRB.backward(speed);
    motorLF.backward(speed);
    motorLB.backward(speed);
}

void turn_right() {
    motorRF.backward(speed);
    motorRB.backward(speed);
    motorLF.forward(speed);
    motorLB.forward(speed);
}

void turn_left() {
    motorRF.forward(speed);
    motorRB.forward(speed);
    motorLF.backward(speed);
    motorLB.backward(speed);
}

void stop_move() {
    motorRF.stop();
    motorRB.stop();
    motorLF.stop();
    motorLB.stop();
    motorBrazo.stop();
    motorGarra.stop();
}

void recoger_lata() {
    //BAja
    goalBrazo = 0;
    while(abs(potenciometro()) > 50);
    // Abre
    motorGarra.backward(150);
    delay(1500);
    motorGarra.stop();            
    forward();
    delay(500);
    stop_move();
    //Cierre
    motorGarra.forward(150);
    delay(1700);
    motorGarra.stop();
    // SUbe
    goalBrazo = 550;
    while(abs(potenciometro()) > 50);
    // Abre
    motorGarra.backward(150);
    delay(1000);
    motorGarra.stop();            
            
    stop_move();     
}

int potenciometro(){
  
  // Promedio de mediciones
  int i = 0, prom_pos = 0;
  for(i = 0; i < 5; i++) {
    prom_pos += analogRead(feedback);
    delayMicroseconds(10);
  }
  prom_pos = prom_pos/5;
    
  int diff = prom_pos - goalBrazo;
    
  if(goalBrazo == 550) {
    if(diff < -200) { 
      motorBrazo.backward(255);
    } else if(diff < -5) {
      motorBrazo.backward(100 + diff);
    } else {
      motorBrazo.stop();
    }
  } else {
    if(diff > 50) motorBrazo.forward(150);
    else motorBrazo.stop();
  }
  return diff;
}

void evitarObstaculos(){
  
  sonarDer = sonar[0].ping_cm();
  sonarIzq = sonar[1].ping_cm();
  
  boolean evitando = false; 
  
  if(sonarIzq<=35 || sonarDer<=35){
    Serial.write('h');
    evitando = true;
  }
  
  while(sonarIzq<=35 || sonarDer<=35){
    stop_move();
  
      
    if(sonarIzq <= 35 ){
       turn_right();
       delay(400);
       stop_move();
    }else if(sonarDer <= 35){
       turn_left();
       delay(400);
       stop_move();
    }
  }
  
  if(evitando){
    Serial.write('r'); 
  }
 
}

void loop() {
  
  /*if(abs(potenciometro()) < 50){
      evitarObstaculos();
  }*/
  
  potenciometro();
    char opcion;
    if(Serial.available()>0) {
        opcion = Serial.read();
        if(opcion == 'd') {
            turn_right();       
        }else if (opcion =='a'){
            turn_left();
        }else if (opcion == 'w'){
            forward();
        }else if (opcion == 's'){
            backward();
        }else if (opcion == 'p') {
            recoger_lata(); 
            // aviso que recogi la lata
            Serial.write('r');
        }
       
    }
}
