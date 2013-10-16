#include <Servo.h>
#ifndef Motor_h
#define Motor_h
#include <Arduino.h>

typedef struct Motor{
public:
    int spd_pin;
    int dirF_pin;
    int dirB_pin;

    void init() {
        pinMode(spd_pin, OUTPUT); 
        pinMode(dirF_pin, OUTPUT); 
        pinMode(dirB_pin, OUTPUT); 
    }

    //El motor gira hacia "adelante"
    void forward(int speed) {
        analogWrite(spd_pin, speed);
        digitalWrite(dirF_pin, HIGH); 
        digitalWrite(dirB_pin, LOW); 
    }

    //El motor gira hacia "atras"
    void backward(int speed) {
        analogWrite(spd_pin, speed);
        digitalWrite(dirF_pin, LOW); 
        digitalWrite(dirB_pin, HIGH); 
    }

    //Frena con fuerza (No dejar activado por más de 5 segundos)
    void brake(int strength) {
        analogWrite(spd_pin, strength);
        digitalWrite(dirF_pin, HIGH); 
        digitalWrite(dirB_pin, HIGH); 
    }

    //Apaga el motor
    void stop() {
        analogWrite(spd_pin, 0);
        digitalWrite(dirF_pin, LOW); 
        digitalWrite(dirB_pin, LOW); 
    }
} Motor;

#endif
//Declaración de los motores y sus pines (spd_pin, dirF_pin, dirB_pin)
//no sirven 2 y 3, 9 o 10
Motor motorRF = {9, 26, 27};
Motor motorRB = {7, 28, 29};

Motor motorLF = {6, 50, 51};
Motor motorLB = {4, 48, 49};
// motores de la garra
Motor motorBrazo = {11,30,31};
Motor motorGarra = {12, 32, 33};

int speed = 150;

void setup() {
    Serial.begin(115200);
    motorRF.init();
    motorRB.init();
    motorLF.init();
    motorLB.init();
    motorBrazo.init();
    motorGarra.init();
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

void loop() {
  
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
        }else if (opcion == 'q') {
            stop_move();     
        }else if (opcion == 'g'){
            motorGarra.backward(150);
            delay(1000);
            motorGarra.stop();            
        }else if (opcion == 'b' ){
            motorBrazo.backward(255);
            delay(1500);
            motorBrazo.stop();
        }else if (opcion == 'n'){
            motorGarra.forward(150);
            delay(1000);
            motorGarra.stop();
        }else if (opcion == 'v'){
            motorBrazo.forward(200);
            delay(1000);
            motorBrazo.stop();
        }
       
    }
}
