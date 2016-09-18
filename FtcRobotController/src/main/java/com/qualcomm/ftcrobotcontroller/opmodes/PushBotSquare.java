package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

/*
 * An example linear op mode where the pushbot
 * will drive in a square pattern using sleep() 
 * and a for loop.
 */
public class PushBotSquare extends LinearOpMode {
    DcMotor motorRight;
    DcMotor motorLeft;
    Servo servoDunk;
    TouchSensor sensorTouch;

    int count = 0;

    @Override
    public void runOpMode() throws InterruptedException {

        servoDunk = hardwareMap.servo.get("servoDunk");
        motorLeft = hardwareMap.dcMotor.get("motorR");
        motorRight = hardwareMap.dcMotor.get("motorL");
        sensorTouch = hardwareMap.touchSensor.get("sensorTouch");
        motorLeft.setDirection(DcMotor.Direction.REVERSE);
        servoDunk.setPosition(.97);


        waitForStart();

        while(count < 16) {

            switch(count) {

                case 0:

                    motorLeft.setPower(.6);
                    motorRight.setPower(.8);

                    if (sensorTouch.isPressed()) {
                        motorLeft.setPower(0);
                        motorRight.setPower(0);
                        count++;
                    }

                    break;

                case 1:

                    sleep(500);

                    servoDunk.setPosition(.02);

                    sleep(3000);

                    servoDunk.setPosition(.97);

                    break;

                default:
                    break;
            }
        }




    }

    //@Override
    //public void stop() {

    //}
}