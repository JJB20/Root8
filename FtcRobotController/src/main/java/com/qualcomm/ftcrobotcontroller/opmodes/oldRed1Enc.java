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

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.ftcrobotcontroller.R;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;


/*

In this here program, the robot does all the beacon business and climbs to the mountain low zone.

 */
public class oldRed1Enc extends PushBotTelemetrySensors {

	DcMotor motorRight;
	DcMotor motorLeft;
	TouchSensor sensorTouch;
	ColorSensor sensorColor;
	ColorSensor sensorFruity;
	Servo servoDunk;
	Servo servoPress;


	DeviceInterfaceModule dim;

    int count = 0;
	int currentPos = 0;

	long start_time = System.currentTimeMillis();
	long wait_time = 3500;
	long end_time = start_time + wait_time;


	public oldRed1Enc() {

	}
	@Override
	public void init() {

			sensorTouch = hardwareMap.touchSensor.get("sensorTouch");
			sensorColor = hardwareMap.colorSensor.get("sensorColor");
			sensorFruity = hardwareMap.colorSensor.get("sensorFruity");
			motorRight = hardwareMap.dcMotor.get("motorR");
			motorLeft = hardwareMap.dcMotor.get("motorL");
			servoDunk = hardwareMap.servo.get("servoDunk");
			servoPress = hardwareMap.servo.get("servoPress");
			motorLeft.setDirection(DcMotor.Direction.REVERSE);

			dim = hardwareMap.deviceInterfaceModule.get("Device Interface Module 2");

			telemetry.addData("count: ", count);
			telemetry.addData("touched: ", sensorTouch.isPressed());
			telemetry.addData("sensorColor:", sensorColor.blue());

		sensorColor.enableLed(false);
		sensorColor.enableLed(true);

		dim.setDigitalChannelMode(5, DigitalChannelController.Mode.OUTPUT);

		//turns LED on
		dim.setDigitalChannelState(5, true);

		//turns LED off
		dim.setDigitalChannelState(5, false);

		servoDunk.setPosition(.97);
		servoPress.setPosition(.45);


		//motorRight.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
		//motorRight.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);


		}


	@Override
	public void loop() {

		telemetry.addData("count: ", count);
		telemetry.addData("touched: ", sensorTouch.isPressed());

		float hsvValues[] = {0,0,0};
		final float values[] = hsvValues;
		final View relativeLayout = ((Activity) hardwareMap.appContext).findViewById(R.id.RelativeLayout);


		Color.RGBToHSV(sensorColor.red() * 8, sensorColor.green() * 8, sensorColor.blue() * 8, hsvValues);
		//info.addData("Clear", sensorColor.alpha());
		//info.addData("Red  ", sensorColor.red());
		//info.addData("Green", sensorColor.green());
		telemetry.addData("Blue ", sensorColor.blue());
		//info.addData("Hue", hsvValues[0]);

		Color.RGBToHSV((sensorFruity.red() * 255) / 800, (sensorFruity.green() * 255) / 800, (sensorFruity.blue() * 255) / 800, hsvValues);
		telemetry.addData("Fru Clear", sensorFruity.alpha());
		telemetry.addData("Fru Red  ", sensorFruity.red());
		telemetry.addData("Fru Green", sensorFruity.green());
		telemetry.addData("Fru Blue ", sensorFruity.blue());
		telemetry.addData("Fru Hue", hsvValues[0]);

		telemetry.addData("MotorEncoder", motorRight.getCurrentPosition());



		switch (count) {

			//turns to angle
			case 0:
				motorRight.setPower(.6);
				if(motorRight.getCurrentPosition() > 2150)
					count++;
				break;

			//slows down after 15000
			case 1:
				motorLeft.setPower(.72);
				motorRight.setPower(.6);
				if(motorRight.getCurrentPosition() > 15000)
					count++;
				break;

			//drive until a white line or more than 20000
			case 2:
				if(motorRight.getCurrentPosition() < 20000) {
					if (sensorColor.blue() < 20) {
						motorLeft.setPower(.25);
						motorRight.setPower(.4);
					} else {
						motorLeft.setPower(0);
						motorRight.setPower(0);
						currentPos = motorRight.getCurrentPosition();
						count = 5;
					}
				}
				else{
					motorLeft.setPower(0);
					motorRight.setPower(0);
					currentPos = motorRight.getCurrentPosition();
					count++;
				}

				break;

			//if missed white line- turn and go until white line
			case 3:
					if (sensorColor.blue() < 20) {
						if(motorRight.getCurrentPosition() < currentPos + 1200){
							motorLeft.setPower(-.24);
							motorRight.setPower(.4);
						}
						else {
							motorLeft.setPower(.24);
							motorRight.setPower(.4);
						}
					}
					else {
						motorLeft.setPower(0);
						motorRight.setPower(0);
						currentPos = motorRight.getCurrentPosition();
						count++;
					}

				break;

			//follow line until touch sensor is pressed if missed line originally
			case 4:
				if (sensorTouch.isPressed()) {
					motorLeft.setPower(.0);
					motorRight.setPower(.0);
					currentPos = motorRight.getCurrentPosition();
					count = 6;
				} else {
					if (sensorColor.blue() > 20) {
						motorLeft.setPower(.24);
						motorRight.setPower(.4);
					} else {
						motorLeft.setPower(0.24);
						motorRight.setPower(0);
					}

				}
				break;

			//follow line until touch sensor is pressed
			case 5:
				if (sensorTouch.isPressed()) {
					motorLeft.setPower(.0);
					motorRight.setPower(.0);
					currentPos = motorRight.getCurrentPosition();
					count++;
				} else {
					if (sensorColor.blue() > 20) {
						motorLeft.setPower(.24);
						motorRight.setPower(.4);
					} else {
						motorLeft.setPower(0);
						motorRight.setPower(.4);
					}

				}
				break;


			case 6:
				/*servoDunk.setPosition(.02); //dunks the bros

				//picks red
				if (sensorFruity.blue() > sensorFruity.red()) {
					servoPress.setPosition(.27);
				} else
					servoPress.setPosition(.63);
*/
				count++;

				break;

			//back up
			case 7:
				servoDunk.setPosition(.97);
				motorRight.setPower(-.42);
				motorLeft.setPower(-.25);
				if(motorRight.getCurrentPosition() <  currentPos - 5000) {
					currentPos = motorRight.getCurrentPosition();
					count++;
				}
				break;

			//turn
			case 8:
				motorLeft.setPower(-.25);
				motorRight.setPower(.49);
				if(motorRight.getCurrentPosition() > currentPos + 1800) {
					currentPos = motorRight.getCurrentPosition();
					count++;
				}
				break;

			//forward
			case 9:
				motorRight.setPower(.4);
				motorLeft.setPower(.2);
				if(motorRight.getCurrentPosition() > currentPos + 10000) {
					currentPos = motorRight.getCurrentPosition();
					count++;
				}
				break;

			default:
				break;

		}

	}
		@Override
		public void stop() {

	}

}
