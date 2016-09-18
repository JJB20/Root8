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
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

/**
 * Example autonomous program.
 * <p>
 * This example program uses elapsed time to determine how to move the robot.
 * The OpMode.java class has some class members that provide time information
 * for the current op mode.
 * The public member variable 'time' is updated before each call to the run() event.
 * The method getRunTime() returns the time that has elapsed since the op mode
 * starting running to when the method was called.
 */
public class justinFindLine2 extends PushBotTelemetrySensors {

	DcMotor motorRight;
	DcMotor motorLeft;
	OpticalDistanceSensor v_sensor_ods;
	TouchSensor v_sensor_touch;
	ColorSensor colorSensor;
	Servo servoDunk;
	Servo servoPress;

	double ODSLightValue = 1;
    int count = 0;

	public justinFindLine2() {

	}
	@Override
	public void init() {

			v_sensor_ods = hardwareMap.opticalDistanceSensor.get("sensorOds");
			v_sensor_touch = hardwareMap.touchSensor.get("sensorTouch");
			colorSensor = hardwareMap.colorSensor.get("sensorColor");
			motorRight = hardwareMap.dcMotor.get("motorR");
			motorLeft = hardwareMap.dcMotor.get("motorL");
			servoDunk = hardwareMap.servo.get("servoDunk");
			servoPress = hardwareMap.servo.get("servoPress");
			motorRight.setDirection(DcMotor.Direction.REVERSE);

			telemetry.addData("ODS: ", ODSLightValue);
			telemetry.addData("count: ", count);
			telemetry.addData("touched: ", v_sensor_touch.isPressed());

		colorSensor.enableLed(false);
		colorSensor.enableLed(true);

		servoDunk.setPosition(0.05);
			servoPress.setPosition(.45);
		}


	@Override
	public void loop() {
		
		ODSLightValue = v_sensor_ods.getLightDetected();
		telemetry.addData("ODS: ", ODSLightValue);
		telemetry.addData("count: ", count);
		telemetry.addData("touched: ", v_sensor_touch.isPressed());

		float hsvValues[] = {0,0,0};
		final float values[] = hsvValues;
		final View relativeLayout = ((Activity) hardwareMap.appContext).findViewById(R.id.RelativeLayout);

		//colorSensor.enableLed(false);

		Color.RGBToHSV(colorSensor.red() * 8, colorSensor.green() * 8, colorSensor.blue() * 8, hsvValues);
		telemetry.addData("Clear", colorSensor.alpha());
		telemetry.addData("Red  ", colorSensor.red());
		telemetry.addData("Green", colorSensor.green());
		telemetry.addData("Blue ", colorSensor.blue());
		telemetry.addData("Hue", hsvValues[0]);



		switch (count) {

			//drive until a white line -- blue will be aorund 50
			case 0:
				if(colorSensor.blue() < 20){
					motorLeft.setPower(.4);
					motorRight.setPower(.4);
				}

				else{
					//this.time = 0;
				//	while(this.time < 1.5){
				//		motorLeft.setPower(.3);
				//		motorRight.setPower(.3);
				//	}
					motorLeft.setPower(0);
					motorRight.setPower(0);
					count = 3;
				}
				break;

			//follow line until touch sensor is pressed
			case 1:
				if (v_sensor_touch.isPressed()) {
					motorLeft.setPower(.0);
					motorRight.setPower(.0);
					count++;
				} else {


					if (colorSensor.blue() > 20) {
						motorLeft.setPower(.2);
						motorRight.setPower(.2);
					} else {
						motorLeft.setPower(0.2);
						motorRight.setPower(0);
					}

				}
				break;


			case 2:
				//picks blue
				//if (colorSensor.blue() > colorSensor.red()) {
					servoPress.setPosition(.27);
				//} else
					servoPress.setPosition(.63);

				servoDunk.setPosition(.97);
				count++;
				break;

			//case 6:

			default:
				break;

		}

	}
		@Override
		public void stop() {

	}

}
