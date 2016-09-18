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

public class straightTest extends PushBotTelemetrySensors {

	//create variables
	DcMotor motorR;
	DcMotor motorL;
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
	int count = 0;
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
	double vr = 0.8;//velocity conversion for right
	double vl = 0.45;//velocity conversion for left

	public straightTest(){

	}


	@Override
	public void init() {

		//retrieve motors and sensors from configuration
		sensorTouch = hardwareMap.touchSensor.get("sensorTouch");
		sensorColor = hardwareMap.colorSensor.get("sensorColor");
		sensorFruity = hardwareMap.colorSensor.get("sensorFruity");
		motorR = hardwareMap.dcMotor.get("motorR");
		motorL = hardwareMap.dcMotor.get("motorL");
		motorSwipe = hardwareMap.dcMotor.get("motorSwipe");
		servoDunk = hardwareMap.servo.get("servoDunk");
		servoPress = hardwareMap.servo.get("servoPress");
		servoHold = hardwareMap.servo.get("servoHold");
		servoZipRed = hardwareMap.servo.get("servoZipRed");
		servoZipBlue = hardwareMap.servo.get("servoZipBlue");
		dim = hardwareMap.deviceInterfaceModule.get("Device Interface Module 2");

		//reverse left motor
		motorL.setDirection(DcMotor.Direction.REVERSE);

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
		servoDunk.setPosition(.9);
		servoHold.setPosition(.3);//down
	

		currentPos = motorR.getCurrentPosition();
		lastPos = motorR.getCurrentPosition();
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
			telemetry.addData("MotorEncoder", motorR.getCurrentPosition());
			telemetry.addData("vl", vl);
			telemetry.addData("vr", vr);



			motorL.setPower(vl);
			motorR.setPower(vr);


			}
	@Override
	public void stop(){

		}



	}

