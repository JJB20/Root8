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

public class blueTele extends OpMode {

	//Create motor variables

	DcMotor motorL;
	DcMotor motorR;
	DcMotor motorPullR;
	DcMotor motorPullL;
	DcMotor motorClimb;
	DcMotor motorSwipe;

	Servo servoRaiseR;
	Servo servoRaiseL;
	Servo servoZip;
	Servo servoClimb;
	Servo servoDunk;

	double climbCount = .9;

	public blueTele() {

	}

	@Override
	public void init() {

		//retrieve DC motors from configuration
		motorR = hardwareMap.dcMotor.get("motorR");
		motorL = hardwareMap.dcMotor.get("motorL");
		motorPullR = hardwareMap.dcMotor.get("motorPullR");
		motorPullL = hardwareMap.dcMotor.get("motorPullL");
		motorClimb = hardwareMap.dcMotor.get("motorClimb");
		//motorSwipe = hardwareMap.dcMotor.get("motorSwipe");

		//reverse direction of right motor
		motorR.setDirection(DcMotor.Direction.REVERSE);

		//retrieve servo motors from configuration
		servoRaiseR = hardwareMap.servo.get("servoRaiseR");
		servoRaiseL = hardwareMap.servo.get("servoRaiseL");
		servoZip = hardwareMap.servo.get("servoZip");
		servoClimb = hardwareMap.servo.get("servoClimb");
		servoDunk = hardwareMap.servo.get("servoDunk");

		//initialize servos
		servoRaiseR.setPosition(.95);//up
		servoRaiseL.setPosition(.05);//up
		servoZip.setPosition(.4);//in
		servoClimb.setPosition(climbCount);//middle
		servoDunk.setPosition(.97);//middle

	}

	@Override
	public void loop() {



			telemetry.addData("climbCount: ", climbCount);

			//Drive treads
			float left = gamepad1.left_stick_y;
			float right = gamepad1.right_stick_y;
			right = Range.clip(right, -1, 1);
			left = Range.clip(left, -1, 1);
			right = (float) scaleInput(right);
			left = (float) scaleInput(left);
			motorR.setPower(right);
			motorL.setPower(left);

			//Tongue DC motor
			if (gamepad2.y) {
				motorClimb.setPower(-.9);//out
			} else if (gamepad2.a) {
				motorClimb.setPower(.9);//in
			} else {
				motorClimb.setPower(0);
			}


			//Tongue servo motor
			if (gamepad2.dpad_up) {
				climbCount -= .002;
			}

			if (gamepad2.dpad_down) {
				climbCount += .002;
			}

			if (climbCount > 0 && climbCount < 1)
				servoClimb.setPosition(climbCount);

			//Linear slides DC motor
			float leftGrapple = gamepad2.left_stick_y;
			float rightGrapple = gamepad2.right_stick_y;
			motorPullL.setPower(leftGrapple / -2);
			motorPullR.setPower(rightGrapple / 2);

			//Linear slides servo motors
			if (gamepad2.right_bumper) {
				servoRaiseR.setPosition(.95);
			}

			if (gamepad2.right_trigger > 0.05) {
				servoRaiseR.setPosition(.5);
			}

			if (gamepad2.left_bumper) {
				servoRaiseL.setPosition(.05);
			}

			if (gamepad2.left_trigger > 0.05) {
				servoRaiseL.setPosition(.5);
			}

			//Zip-line servo (intuitive only for right side servo... Ah well, what are you gonna do?)
			if (gamepad1.right_bumper) {
				servoZip.setPosition(.5);//out
			}

			if (gamepad1.left_bumper) {
				servoZip.setPosition(.99);//in
			}

			if (gamepad1.right_trigger > .05) {
				servoZip.setPosition(.2);//far out, man
			}

			//Dunk servo
			if (gamepad1.x) {
				servoDunk.setPosition(.02);//dunked
			}

			if (gamepad1.y) {
				servoDunk.setPosition(.7);//middle
			}

			if (gamepad1.b) {
				servoDunk.setPosition(.97);//not dunked
			}

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
