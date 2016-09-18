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
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

public class MaxAutonomousColor extends OpMode {


	Servo servo1;
	Servo servo2;

	DcMotor motorR;
	DcMotor motorL;

	public enum ColorSensorDevice {MODERN_ROBOTICS_I2C};

	public ColorSensorDevice device = ColorSensorDevice.MODERN_ROBOTICS_I2C;

	ColorSensor colorSensor;
	OpticalDistanceSensor v_sensor_ods;

	double ODSLightValue = 1;
	int ODS_Stop_Signal = 0;

	public MaxAutonomousColor() {

	}

	@Override
	public void init() {

		hardwareMap.logDevices();
		colorSensor = hardwareMap.colorSensor.get("color");
		v_sensor_ods = hardwareMap.opticalDistanceSensor.get ("sensor_ods");

		motorL = hardwareMap.dcMotor.get("motorL");
		motorR = hardwareMap.dcMotor.get("motorR");

		servo1 = hardwareMap.servo.get("servo1");
		servo2 = hardwareMap.servo.get("servo2");

		servo1.setPosition(0.05);
		servo2.setPosition(.45);

	}

	@Override
	public void loop() {


		ODSLightValue = v_sensor_ods.getLightDetected();

		if(ODS_Stop_Signal < 1) {
			if (ODSLightValue > 0.9) {
				motorR.setPower(0);
				motorL.setPower(0);
				ODS_Stop_Signal = 1;
			} else {
				motorR.setPower(-.1);
				motorL.setPower(-.1);
			}


		}
		//picks blue
		if(colorSensor.blue() > colorSensor.red()){
			servo2.setPosition(.27);
		}
		else
			servo2.setPosition(.63);

		servo1.setPosition(.97);

		float hsvValues[] = {0,0,0};
		final float values[] = hsvValues;
		final View relativeLayout = ((Activity) hardwareMap.appContext).findViewById(R.id.RelativeLayout);

		Color.RGBToHSV(colorSensor.red() * 8, colorSensor.green() * 8, colorSensor.blue() * 8, hsvValues);
		telemetry.addData("Clear", colorSensor.alpha());
		telemetry.addData("Red  ", colorSensor.red());
		telemetry.addData("Green", colorSensor.green());
		telemetry.addData("Blue ", colorSensor.blue());
		telemetry.addData("Hue", hsvValues[0]);
	}


	@Override
	public void stop() {

	}

}
