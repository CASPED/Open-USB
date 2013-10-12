#include <Servo.h>
#include "motor.h"

//Declaración de los motores y sus pines (spd_pin, dirF_pin, dirB_pin)
Motor motorRF = {13, 22, 23};
Motor motorRB = {12, 24, 25};
Motor motorLF = {11, 26, 27};
Motor motorLB = {10, 28, 29};

int speed = 255;

void setup() {
    motorRF.init();
    motorRB.init();
    motorLF.init();
    motorRB.init();
}

void forward() {
    motorRF.forward(speed);
    motorRB.forward(speed);
    motorLF.forward(speed);
    motorRB.forward(speed);
}

void backward() {
    motorRF.backward(speed);
    motorRB.backward(speed);
    motorLF.backward(speed);
    motorRB.backward(speed);
}

void rigth() {
    motorRF.backward(speed);
    motorRB.backward(speed);
    motorLF.forward(speed);
    motorRB.forward(speed);
}

void left() {
    motorRF.forward(speed);
    motorRB.forward(speed);
    motorLF.backward(speed);
    motorRB.backward(speed);
}

void stop() {
    motorRF.stop();
    motorRB.stop();
    motorLF.stop();
    motorRB.stop();
}

void loop() {
    forward();
    delay(4000);
    stop();
    delay(2000);
}
