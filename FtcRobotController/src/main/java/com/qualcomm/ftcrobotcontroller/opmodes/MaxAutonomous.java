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
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.Servo;


public class MaxAutonomous extends OpMode {

	final static double MOTOR_POWER = 0.15; // Higher values will cause the robot to move faster

	DcMotor motorRight;
	DcMotor motorLeft;

	double servorPosition;
	double servolPosition;


	Servo servor;
	Servo servol;

	public MaxAutonomous() {

	}

	@Override
	public void init() {

		servor = hardwareMap.servo.get("servor");
		servol = hardwareMap.servo.get("servol");


		servor.setPosition(0.05);
		servol.setPosition(.9);

		motorRight = hardwareMap.dcMotor.get("motorr");
		motorLeft = hardwareMap.dcMotor.get("motorl");
		motorLeft.setDirection(DcMotor.Direction.REVERSE);

	}

	@Override
	public void loop() {

		double left, right = 0.0;

		servor.setPosition(.05 );
		servol.setPosition(.9);

/*
		while(this.time <= 5)
		{
			motorRight.setPower(.3);
			motorLeft.setPower(.3);

		}

		servor.setPosition(.05);
		servol.setPosition(.05);
*/

        if (this.time <= 4)
		{
            left = 0.3;
            right = 0.3;
        }
		else if (this.time > 5 && this.time <= 6.5)
		{
            left = 0.3;
            right = -0.3;
        }
		else if (this.time > 7 && this.time <= 7.5)
		{
			left = 0.2;
			right = 0.2;
		}
		else if (this.time > 9 && this.time <= 13)
		{
			left = -0.2;
			right = 0.2;
		}
		else
		{
            left = 0.0;
            right = 0.0;
        }

		motorRight.setPower(right);
		motorLeft.setPower(left);

		telemetry.addData("Text", "*** Robot Data***");
        telemetry.addData("time", "elapsed time: " + Double.toString(this.time));
		telemetry.addData("left tgt pwr",  "left  pwr: " + Double.toString(left));
		telemetry.addData("right tgt pwr", "right pwr: " + Double.toString(right));

		servor.setPosition(0.9);
		servol.setPosition(.05);
	}

	@Override
	public void stop() {



	}

}
