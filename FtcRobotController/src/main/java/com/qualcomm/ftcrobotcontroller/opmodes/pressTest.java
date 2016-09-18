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

public class pressTest extends PushBotTelemetrySensors {

	//create variables
	DcMotor motorRight;
	DcMotor motorLeft;
	DcMotor motorSwipe;
	TouchSensor sensorTouch;
	ColorSensor sensorColor;
	ColorSensor sensorFruity;
	Servo servoDunk;
	Servo servoPress;
	Servo servoZipRed;
	Servo servoZipBlue;
	Servo servoHold;

	DeviceInterfaceModule dim;
	int count = -2;
	int swipeCount = 0;
	int moveCount = 0;
	int currentPos = 0;
	int lastPos = 0;//for moveCount
	int toggle = 0;
	int startPosSwipe;
	int lastPosSwipe;//for swipeCount
	String location = "";
	int timerCheck = 0;
	int timerCount = 0;
	Boolean eightbackup = false;

	//master driving DC motor power variables
	double vr = 1.0;//velocity conversion for right
	double vl = 0.45;//velocity conversion for left


	@Override
	public void init() {

		//retrieve motors and sensors from configuration
		sensorTouch = hardwareMap.touchSensor.get("sensorTouch");
		sensorColor = hardwareMap.colorSensor.get("sensorColor");
		sensorFruity = hardwareMap.colorSensor.get("sensorFruity");
		motorLeft = hardwareMap.dcMotor.get("motorR");
		motorRight = hardwareMap.dcMotor.get("motorL");
		motorSwipe = hardwareMap.dcMotor.get("motorSwipe");
		servoDunk = hardwareMap.servo.get("servoDunk");
		servoPress = hardwareMap.servo.get("servoPress");
		servoHold = hardwareMap.servo.get("servoHold");
		servoZipRed = hardwareMap.servo.get("servoZipRed");
		servoZipBlue = hardwareMap.servo.get("servoZipBlue");
		dim = hardwareMap.deviceInterfaceModule.get("Device Interface Module 2");

		//reverse left motor
		motorLeft.setDirection(DcMotor.Direction.REVERSE);

		//info settings
		telemetry.addData("count: ", count);
		telemetry.addData("touched: ", sensorTouch.isPressed());
		telemetry.addData("sensorColor:", sensorColor.blue());
		telemetry.addData("motorSwipe.getCurrentPosition():", motorSwipe.getCurrentPosition());

		//enable LED on the color sensor
		sensorColor.enableLed(true);

		//IDK...
		dim.setDigitalChannelMode(5, DigitalChannelController.Mode.OUTPUT);

		//turns LED on
		dim.setDigitalChannelState(5, true);

		//turns LED off
		dim.setDigitalChannelState(5, false);

		servoPress.setPosition(.4);
		servoZipBlue.setPosition(.99);//in
		servoZipRed.setPosition(.1);//in
		servoDunk.setPosition(.97);
		servoHold.setPosition(.3);//down
	

		currentPos = motorRight.getCurrentPosition();
		lastPos = motorRight.getCurrentPosition();
		lastPosSwipe = motorSwipe.getCurrentPosition();//for swipeCount
		startPosSwipe = motorSwipe.getCurrentPosition();
	}



		@Override
		public void loop() {

			//constantly update info settings
			telemetry.addData("touched: ", sensorTouch.isPressed());
			telemetry.addData("location: ", location);

			//IDK...
			float hsvValues[] = {0, 0, 0};
			final float values[] = hsvValues;
			final View relativeLayout = ((Activity) hardwareMap.appContext).findViewById(R.id.RelativeLayout);

			Color.RGBToHSV(sensorColor.red() * 8, sensorColor.green() * 8, sensorColor.blue() * 8, hsvValues);
			telemetry.addData("Blue ", sensorColor.blue());
			Color.RGBToHSV((sensorFruity.red() * 255) / 800, (sensorFruity.green() * 255) / 800, (sensorFruity.blue() * 255) / 800, hsvValues);
			telemetry.addData("Fru Red  ", sensorFruity.red());
			telemetry.addData("Fru Blue ", sensorFruity.blue());
			telemetry.addData("MotorEncoder", motorRight.getCurrentPosition());
			telemetry.addData("MotorEncoder difference", motorRight.getCurrentPosition() - currentPos);
			telemetry.addData("motorSwipe", motorSwipe.getCurrentPosition());
			telemetry.addData("motorSwipe difference", motorSwipe.getCurrentPosition() - startPosSwipe);
			telemetry.addData("vl", vl);
			telemetry.addData("vr", vr);


switch (count) {
	case -2:
		motorSwipe.setPower(-.25);
		if (motorSwipe.getCurrentPosition() < (startPosSwipe - 420)) {
			motorSwipe.setPower(0);
			motorLeft.setPower(0);
			motorRight.setPower(0);
			lastPosSwipe = motorSwipe.getCurrentPosition();
			count = -1;//swipe successful
		} else {
			if (motorSwipe.getCurrentPosition() == lastPosSwipe) {
				swipeCount++;
				if (swipeCount > 5) {
					motorLeft.setPower(-vl * 0.5);
					motorRight.setPower(-vr * 0.5);
				}
			} else {
				motorLeft.setPower(0);
				motorRight.setPower(0);
				swipeCount = 0;
			}

		}
		lastPosSwipe = motorSwipe.getCurrentPosition();
		break;

	case -1:
		if (toggle == 0) {
			motorSwipe.setPower(.25);//in
			if (motorSwipe.getCurrentPosition() > (startPosSwipe - 30)) {
				motorSwipe.setPower(0);
				toggle = 1;
			}
		}
		if (toggle == 1) {
			motorSwipe.setPower(-.35);//out
			if (motorSwipe.getCurrentPosition() < (startPosSwipe - 420)) {
				motorSwipe.setPower(0);
				toggle = 2;
			}
		}
		if (toggle == 2) {
			motorSwipe.setPower(.35);//in
			if (motorSwipe.getCurrentPosition() > (startPosSwipe - 30)) {
				motorSwipe.setPower(0);
				toggle = 0;
				count = 0;//Swipes all executed
			}
		}
		break;

	default:
		motorLeft.setPower(0);
		motorRight.setPower(0);
		break;
}


			}
	@Override
	public void stop(){

		}



	}

