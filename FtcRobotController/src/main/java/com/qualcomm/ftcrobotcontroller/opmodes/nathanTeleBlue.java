/* Copyright (c) 2014 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

public class nathanTeleBlue extends OpMode {

    //Create motor variables

    //Left Tread
    DcMotor motorL;
    //Right Tread
    DcMotor motorR;
    //Lower Motor for climbing
    DcMotor motorClimb;
    //Motor for climbing
    DcMotor motorSwipe;
    //Motor for hangin closer to Hook
    DcMotor motorHang;
    //Motor for hanign closer to eyes
    DcMotor motorHang2;

    //Servo for Zipline Red
    Servo servoZipRed;
    //Servo for Zipline Blue
    Servo servoZipBlue;
    //Servo for Holding swiper in place
    Servo servoHold;
    //Servo for dunking the brose
    Servo servoDunk;
    //Servo for pressing the buttons
    Servo servoPress;

    int startPosSwipe;
    int toggle = 2;//initialize to something other than 0 or 1
    int direction = -1;
    int timeCount = 0;//for motorSwipe intitialization
    Boolean holdUp = false;
    double vr = 1.0;//velocity conversion for right
    double vl = 0.45;//velocity conversion for left
    int dunk = 0;//0 if down, 1 if middle, 2 if dunked
    boolean redthingy = true;
    String location = "";

    //double climbCount = .9;

    public nathanTeleBlue() {

    }

    @Override
    public void init() {

        //retrieve DC motors from configuration
        motorR = hardwareMap.dcMotor.get("motorR");
        motorL = hardwareMap.dcMotor.get("motorL");
        motorClimb = hardwareMap.dcMotor.get("motorClimb");
        motorSwipe = hardwareMap.dcMotor.get("motorSwipe");
        motorHang = hardwareMap.dcMotor.get("motorHang");
        motorHang2 = hardwareMap.dcMotor.get("motorHang2");

        //reverse direction of right motor
        motorL.setDirection(DcMotor.Direction.REVERSE);//at beginning L is reversed and direction = 1
        motorHang2.setDirection(DcMotor.Direction.REVERSE);

        //retrieve servo motors from configuration
        servoZipRed = hardwareMap.servo.get("servoZipRed");
        servoZipBlue = hardwareMap.servo.get("servoZipBlue");
        servoHold = hardwareMap.servo.get("servoHold");
        servoDunk = hardwareMap.servo.get("servoDunk");
        servoPress = hardwareMap.servo.get("servoPress");

        //initialize servos
        servoZipBlue.setPosition(.99);//in
        servoZipRed.setPosition(.1);//in
        servoHold.setPosition(.3);//down
        holdUp = false;
        servoDunk.setPosition(.97);//middle
        servoPress.setPosition(.4);
        startPosSwipe = motorSwipe.getCurrentPosition();



    }

    @Override
    public void loop() {

        //telemetry.addData("climbCount: ", climbCount);
        telemetry.addData("startPosSwipe: ", startPosSwipe);
        telemetry.addData("holdUp: ", holdUp);
        telemetry.addData("location: ", location);
        telemetry.addData("motorSwipe.getCurrentPosition() - startPosSwipe ", motorSwipe.getCurrentPosition() - startPosSwipe );
        if(direction == -1) {
            telemetry.addData("direction: dunk ", direction);
        }
        else{
            telemetry.addData("direction: climb ", direction);
        }

        if(gamepad1.right_stick_button){
            direction = 1;//forward to drive up ramp
        }

        if(gamepad1.left_stick_button){
            direction = -1;//forward to dunk brose
        }

        //Drive treads
        float left = gamepad1.left_stick_y;
        float right = gamepad1.right_stick_y;
        right = Range.clip(right, -1, 1);
        left = Range.clip(left, -1, 1);
        right = (float) scaleInput(right);
        left = (float) scaleInput(left);

        if(right > 0.3 || right < -0.3) {
            if(direction == -1) {
                motorR.setPower(right * vr * direction);//account for differences in power of motors
            }
            else if(direction == 1){
                motorL.setPower(right * vl * direction);
            }
        }
        else{
            if(direction == -1) {
                motorR.setPower(0);//account for differences in power of motors
            }
            else if(direction == 1){
                motorL.setPower(0);
            }
        }

        if(left > 0.3 || left < -0.3) {
            if (direction == -1) {
                motorL.setPower(left * vl * direction);
            }
            else if (direction == 1){
                motorR.setPower(left * vr * direction);//account for differences in power of motors
            }
        }
        else {
            if (direction == -1) {
                motorL.setPower(0);
            }
            else if (direction == 1){
                motorR.setPower(0);//account for differences in power of motors
            }
        }


        //Lower Tongue DC motor
        if (gamepad1.right_bumper) {
            motorClimb.setPower(-.6);//out,.9

        } else if (gamepad1.right_trigger > 0.05) {
            motorClimb.setPower(.6);//in
        } else {
            motorClimb.setPower(0);
        }

        //Upper Tongue DC motor
        if (gamepad1.left_bumper ) {//only moves if dunker is out of way
            servoDunk.setPosition(.02);//dunked
            motorHang.setPower(-0.9);
            motorHang2.setPower(-.99);//out, 23:21
        } else if (gamepad1.left_trigger > 0.05) {//only moves if dunker is out of way
            servoDunk.setPosition(.02);//dunked
            motorHang.setPower(0.9);
            motorHang2.setPower(.99);//in
        } else {
            motorHang.setPower(0);
            motorHang2.setPower(0);
        }

        if(gamepad1.dpad_up){
            servoHold.setPosition(.01);//up
            holdUp = true;
        }

        if(gamepad1.dpad_down){
            servoHold.setPosition(.3);//down
            holdUp = false;
        }

        if (gamepad1.a && holdUp) {//swing in
            toggle = 0;
            motorSwipe.setPower(.3);
        }




        if(toggle == 0 && (motorSwipe.getCurrentPosition() > (startPosSwipe - 30))) {//cushion
            motorSwipe.setPower(0);

        }

        if (gamepad1.b && holdUp) {//swing out
            toggle = 1;
            motorSwipe.setPower(-.3);
        }

        if(toggle == 1 && (motorSwipe.getCurrentPosition() < (startPosSwipe - 300))) {//encoder for outer
            motorSwipe.setPower(0);

        }
        /*if (gamepad1.back){
            redthingy = !redthingy;
        }*/
        //Zip-line for blue
        if (gamepad1.dpad_left) {
            servoZipBlue.setPosition(.43);//out
        }

        if (gamepad1.start) {//make harder to put back in
            servoZipBlue.setPosition(.99);//in
        }

        if (gamepad1.dpad_right) {
            servoZipBlue.setPosition(.1);//far out, man
        }



        //Zip-line servo for red (intuitive only for right side servo... Ah well, what are you gonna do?)
        /*if (redthingy && gamepad1.dpad_right) {
            servoZipRed.setPosition(.67);//out
        }

        if (redthingy && gamepad1.start) {//make harder to put back in
            servoZipRed.setPosition(.1);//in
        }

        if (redthingy && gamepad1.dpad_left) {
            servoZipRed.setPosition(.99);//far out, man
        }
*/
        //Dunk servo
        /*if (gamepad1.x) {
            servoDunk.setPosition(.02);//dunked
            dunk = 2;
        }

        if (gamepad1.y) {
            servoDunk.setPosition(.7);//middle
            dunk = 1;
        }

        if (gamepad1.b) {
            servoDunk.setPosition(.97);//not dunked
            dunk = 0;
        }*/

    }


    @Override
    public void stop() {

    }

    //I'd be amazed if anyone knew what the below method does

    /*
     * This method scales the joystick input so for low joystick values, the
     * scaled value is less than linear.  This is to make it easier to drive
     * the robot more precisely at slower speeds.
     */
    double scaleInput(double dVal)  {
        double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
                0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };

        // get the corresponding index for the scaleInput array.
        int index = (int) (dVal * 16.0);

        // index should be positive.
        if (index < 0) {
            index = -index;
        }

        // index cannot exceed size of array minus 1.
        if (index > 16) {
            index = 16;
        }

        // get value from the array.
        double dScale = 0.0;
        if (dVal < 0) {
            dScale = -scaleArray[index];
        } else {
            dScale = scaleArray[index];
        }

        // return scaled value.
        return dScale;
    }

}
