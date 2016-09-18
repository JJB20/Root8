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
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

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
public class info extends PushBotTelemetrySensors {

	DcMotor motorRight;
	DcMotor motorLeft;
	OpticalDistanceSensor v_sensor_ods;
	TouchSensor sensorTouch;
	ColorSensor sensorColor;
	ColorSensor sensorFruity;
	Servo servoDunk;
	Servo servoPress;

	double ODSLightValue = 1;
    int count = 0;

	public info() {

	}

	@Override
	public void init() {

			sensorTouch = hardwareMap.touchSensor.get("sensorTouch");
			sensorColor = hardwareMap.colorSensor.get("sensorColor");
			motorRight = hardwareMap.dcMotor.get("motorR");
			motorLeft = hardwareMap.dcMotor.get("motorL");
			servoDunk = hardwareMap.servo.get("servoDunk");
			servoPress = hardwareMap.servo.get("servoPress");
			motorRight.setDirection(DcMotor.Direction.REVERSE);
			sensorFruity = hardwareMap.colorSensor.get("sensorFruity");

			telemetry.addData("count: ", count);
			telemetry.addData("touched: ", sensorTouch.isPressed());

		sensorColor.enableLed(false);
		sensorColor.enableLed(true);

		}


	@Override
	public void loop() {

		telemetry.addData("count: ", count);
		telemetry.addData("touched: ", sensorTouch.isPressed());

		float hsvValues[] = {0,0,0};
		final float values[] = hsvValues;
		final View relativeLayout = ((Activity) hardwareMap.appContext).findViewById(R.id.RelativeLayout);

		Color.RGBToHSV(sensorColor.red() * 8, sensorColor.green() * 8, sensorColor.blue() * 8, hsvValues);
		telemetry.addData("col Clear", sensorColor.alpha());
		telemetry.addData("col Red  ", sensorColor.red());
		telemetry.addData("col Green", sensorColor.green());
		telemetry.addData("col Blue ", sensorColor.blue());
		telemetry.addData("col Hue", hsvValues[0]);

		Color.RGBToHSV((sensorFruity.red() * 255) / 800, (sensorFruity.green() * 255) / 800, (sensorFruity.blue() * 255) / 800, hsvValues);
		telemetry.addData("Fru Red  ", sensorFruity.red());
		telemetry.addData("Fru Blue ", sensorFruity.blue());
		telemetry.addData("Fru Clear", sensorFruity.alpha());
		telemetry.addData("Fru Green", sensorFruity.green());
		telemetry.addData("Fru Hue", hsvValues[0]);



	}
		@Override
		public void stop() {

	}

}
