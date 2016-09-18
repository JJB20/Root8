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

public class mountainTeleOp extends OpMode {

	DcMotor motorL;
	DcMotor motorR;
	DcMotor motorPullR;
	DcMotor motorPullL;
	DcMotor motorClimb;

	Servo servoRaiseR;
	Servo servoRaiseL;
	Servo servoZip;
	Servo servoClimb;

	int countR;
	int countL;

	public mountainTeleOp() {

	}

	@Override
	public void init() {

		motorR = hardwareMap.dcMotor.get("motorR");
		motorL = hardwareMap.dcMotor.get("motorL");
		motorR.setDirection(DcMotor.Direction.REVERSE);

		motorPullR = hardwareMap.dcMotor.get("motorPullR");
		motorPullL = hardwareMap.dcMotor.get("motorPullL");

		servoRaiseR = hardwareMap.servo.get("servoRaiseR");
		servoRaiseL = hardwareMap.servo.get("servoRaiseL");
		servoZip = hardwareMap.servo.get("servoZip");

		servoRaiseR.setPosition(.45);
		servoRaiseL.setPosition(.45);
		servoZip.setPosition(.45);

	}

	@Override
	public void loop() {


		float left = gamepad1.left_stick_y;
		float right = gamepad1.right_stick_y;

		boolean pressed = false;
		boolean pressed2 = false;

		right = Range.clip(right, -1, 1);
		left = Range.clip(left, -1, 1);

		// scale the joystick value to make it easier to control
		// the robot more precisely at slower speeds.
		right = (float)scaleInput(right);
		left =  (float)scaleInput(left);
		
		// write the values to the motors
		motorR.setPower(right);
		motorL.setPower(left);

		//extend out
		if (gamepad2.a){
			motorPullR.setPower(-.2);
		}

		//back in
		else if (gamepad2.b){
			motorPullR.setPower(.2);
		}

		else
			motorPullR.setPower(0);

		//extend out
		if (gamepad2.x){
			motorPullL.setPower(-.2);
		}

		//back in
		else if (gamepad2.y){
			motorPullL.setPower(.2);
		}

		else
			motorPullL.setPower(0);



		if(gamepad2.right_bumper) {
			if (countR == 0) {
				servoRaiseR.setPosition(.95);
				countR++;
			}
			else{
				servoRaiseR.setPosition(.65);
				countR--;
			}
		}

		if(gamepad2.right_trigger > 0.05){
				servoRaiseR.setPosition(.5);
			}



		if(gamepad2.left_bumper) {
			if (countL == 0) {
				servoRaiseL.setPosition(.05);
				countL++;
			}
			else{
				servoRaiseL.setPosition(.35);
				countL--;
			}
		}

		if(gamepad2.left_trigger > 0.05){
			servoRaiseL.setPosition(.5);
		}

		if(gamepad2.dpad_down){
			servoZip.setPosition(.97);
		}

		if(gamepad2.dpad_up){
			servoZip.setPosition(.5);
		}

        telemetry.addData("Text", "*** Robot Data***");
        telemetry.addData("left tgt pwr",  "left  pwr: " + String.format("%.2f", left));
        telemetry.addData("right tgt pwr", "right pwr: " + String.format("%.2f", right));

	}


	@Override
	public void stop() {

	}

    	
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
