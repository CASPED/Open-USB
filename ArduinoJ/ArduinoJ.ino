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
int brazoArriba = 1022;
int brazoAbajo = 600;
int goalBrazo = brazoArriba; // Arriba o abajo

//servos compuertas
Servo compuerta1;
Servo compuerta2; 

const int feedback = A0;

int speed = 70;

void setup() {
    // flag para verificar arduino 
    Serial.begin(115200);
    Serial.println("HOLA :)");
    
    // motores ruedas
    motorRF.init();
    motorRB.init();
    motorLF.init();
    motorLB.init();
    motorBrazo.init();
    motorGarra.init();
    
    // servos compuertas
    compuerta1.attach(46);
    compuerta2.attach(47);
    
    // brazo
    goalBrazo = brazoArriba;
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
      // bajar la velocidad de las cuatro ruedas
    motorRF.decrease(speed);
    motorRB.decrease(speed);
    motorLF.decrease(speed);
    motorLB.decrease(speed)
    //girar
    motorRF.backward(speed);
    motorRB.backward(speed);
    motorLF.forward(speed);
    motorLB.forward(speed);
}

void turn_left() { 
    // bajar la velocidad de las cuatro ruedas
    motorRF.decrease(speed);
    motorRB.decrease(speed);
    motorLF.decrease(speed);
    motorLB.decrease(speed);
    // girar
    motorRF.forward(speed);
    motorRB.forward(speed);
    motorLF.backward(speed);
    motorLB.backward(speed);
}

void stop_move() {
    // parar movimiento ruedas
    motorRF.stop();
    motorRB.stop();
    motorLF.stop();
    motorLB.stop();
    // parar movimiento brazo
    motorBrazo.stop();
    motorGarra.stop();
}

void recoger_lata() {
    //BAja
    goalBrazo = brazoAbajo;
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
    goalBrazo = brazoArriba;
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
    
    if(goalBrazo == brazoArriba) {
        // si esta muy abajo, subir con fuerza
        if(diff < -200) { 
            motorBrazo.backward(255);
        // si esta un poco abajo, subir despacio
        } else if(diff < -5) {
            motorBrazo.backward(100 + diff);
        } else {
            motorBrazo.stop();
        }
    // queremos tener el brazo abajo
    } else {
        if(diff > 50) motorBrazo.forward(150);
        else motorBrazo.stop();
    }
    return diff;
}

void evitarObstaculos(){
  
    // tomar medidas sonares
    sonarDer = sonar[0].ping_cm();
    sonarIzq = sonar[1].ping_cm();
    
    boolean evitando = false; 
    
    //Serial.println(sonarDer);
    //Serial.println(sonarIzq); 
    
    // aviso que evitare obstaculos
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
      
        // actualizar medidas sonares
        sonarDer = sonar[0].ping_cm();
        sonarIzq = sonar[1].ping_cm();
    }
    
    // aviso que termine de evitar obstaculos 
    if(evitando){
        Serial.write('r'); 
    }   
}

void abrir_compuertas(){
    compuerta2.write(50);
    compuerta1.write(120);
}

void meneito(){
    turn_right();
    delay(500);
    turn_left();
    delay(500);
    turn_right();
    delay(500);
    turn_left();
    delay(500);
    stop_move();
}

void loop() {
  
    /*if(abs(potenciometro()) < 50){
        evitarObstaculos();
    }*/
  
    potenciometro();
    
    evitarObstaculos(); 
  
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
        } else if (opcion == 'q') {
            stop_move(); 
        }else if (opcion == '2') {
            abrir_compuertas(); 
            meneito(); 
        }
       
    }
}
