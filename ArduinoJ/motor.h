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
        digitalWrite(spd_pin, LOW); 
        digitalWrite(dirF_pin, LOW); 
        digitalWrite(dirB_pin, LOW); 
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
