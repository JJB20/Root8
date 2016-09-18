package com.qualcomm.ftcrobotcontroller.opmodes;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.ftcrobotcontroller.R;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;


/*

In this here program, the red team robot does all
the beacon business and parks in a specified location

*/

public class blueCornerPark extends PushBotTelemetrySensors {

	//create variables
	DcMotor motorRight;
	DcMotor motorLeft;
	TouchSensor sensorTouch;
	ColorSensor sensorColor;
	ColorSensor sensorFruity;
	Servo servoDunk;
	Servo servoPress;
	Servo servoRaiseR;
	Servo servoRaiseL;
	Servo servoZip;
	Servo servoClimb;
	DeviceInterfaceModule dim;
	int count = 0;
	int currentPos = 0;

	//master driving DC motor power variables
	double vr = .88;//velocity conversion for right
	double vl = .54;//velocity conversion for left

	//wait variables
	long start_time = System.currentTimeMillis();
	long wait_time = 3500;
	long end_time = start_time + wait_time;


	public blueCornerPark() {

	}

	@Override
	public void init() {

		//retrieve motors and sensors from configuration
		sensorTouch = hardwareMap.touchSensor.get("sensorTouch");
		sensorColor = hardwareMap.colorSensor.get("sensorColor");
		sensorFruity = hardwareMap.colorSensor.get("sensorFruity");
		motorLeft = hardwareMap.dcMotor.get("motorR");
		motorRight = hardwareMap.dcMotor.get("motorL");
		servoDunk = hardwareMap.servo.get("servoDunk");
		servoPress = hardwareMap.servo.get("servoPress");
		servoZip = hardwareMap.servo.get("servoZip");
		servoClimb = hardwareMap.servo.get("servoClimb");
		servoRaiseR = hardwareMap.servo.get("servoRaiseR");
		servoRaiseL = hardwareMap.servo.get("servoRaiseL");
		dim = hardwareMap.deviceInterfaceModule.get("Device Interface Module 2");

		//reverse left motor
		motorLeft.setDirection(DcMotor.Direction.REVERSE);

		//info settings
		telemetry.addData("count: ", count);
		telemetry.addData("touched: ", sensorTouch.isPressed());
		telemetry.addData("sensorColor:", sensorColor.blue());

		//enable LED on the color sensor
		sensorColor.enableLed(true);

		//IDK...
		dim.setDigitalChannelMode(5, DigitalChannelController.Mode.OUTPUT);

		//turns LED on
		dim.setDigitalChannelState(5, true);

		//turns LED off
		dim.setDigitalChannelState(5, false);

		//initialize servos
		servoDunk.setPosition(.97);
		servoPress.setPosition(.12);
		servoRaiseR.setPosition(.95);
		servoRaiseL.setPosition(.05);
		servoZip.setPosition(.01);//in
		servoClimb.setPosition(.9);

		currentPos = motorRight.getCurrentPosition();

	}


	@Override
	public void loop() {

		//constantly update info settings
		telemetry.addData("count: ", count);
		telemetry.addData("touched: ", sensorTouch.isPressed());

		//IDK...
		float hsvValues[] = {0,0,0};
		final float values[] = hsvValues;
		final View relativeLayout = ((Activity) hardwareMap.appContext).findViewById(R.id.RelativeLayout);

		Color.RGBToHSV(sensorColor.red() * 8, sensorColor.green() * 8, sensorColor.blue() * 8, hsvValues);
		telemetry.addData("Blue ", sensorColor.blue());
		Color.RGBToHSV((sensorFruity.red() * 255) / 800, (sensorFruity.green() * 255) / 800, (sensorFruity.blue() * 255) / 800, hsvValues);
		telemetry.addData("Fru Clear", sensorFruity.alpha());
		telemetry.addData("Fru Red  ", sensorFruity.red());
		telemetry.addData("Fru Green", sensorFruity.green());
		telemetry.addData("Fru Blue ", sensorFruity.blue());
		telemetry.addData("Fru Hue", hsvValues[0]);

		telemetry.addData("MotorEncoder", motorRight.getCurrentPosition());
		


			//Starts robot actions
			switch (count) {

				//forward
				case 0:
					motorLeft.setPower(vl);
					motorRight.setPower(vr);
					if (motorRight.getCurrentPosition() > currentPos + 1200) {
						currentPos = motorRight.getCurrentPosition();
						count++;
					}
					break;

				//turns to angle towards the general vicinity of mountain opening
				case 1:
					motorLeft.setPower(vl);
					motorRight.setPower(-vr);
					if (motorRight.getCurrentPosition() < currentPos - 900) {
						currentPos = motorRight.getCurrentPosition();
						count++;
					}
					break;

				//drive until spot in front of mountain opening
				case 2:
					motorLeft.setPower(vl);
					motorRight.setPower(vr);
					if (motorRight.getCurrentPosition() > currentPos + 4100)
						count++;
					break;

				//turn to orient robot in front of mountain
				case 3:
					motorLeft.setPower(vl);
					motorRight.setPower(-vr);
					if (motorRight.getCurrentPosition() < currentPos - 800) {
						currentPos = motorRight.getCurrentPosition();
						count++;
					}
					break;

				//drive onto mountain
				case 4:
					motorLeft.setPower(vl);
					motorRight.setPower(vr);
					if (motorRight.getCurrentPosition() > currentPos + 2000) {
						currentPos = motorRight.getCurrentPosition();
						count++;//go to last case
					}
					break;

				//Robot stops if finished before 30 seconds
				case 5:

					motorRight.setPower(0);
					motorLeft.setPower(0);

					break;

				default:

					motorLeft.setPower(0);
					motorRight.setPower(0);

					break;

			}

		}


	@Override
	public void stop() {

	}

}
