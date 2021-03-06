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

public class blueCornerBeacon3 extends PushBotTelemetrySensors {//keeps cowcatch arm out while turning

	//create variables
	DcMotor motorRight;
	DcMotor motorLeft;
	DcMotor motorSwipe;
	TouchSensor sensorTouch;
	ColorSensor sensorColor;
	ColorSensor sensorFruity;
	Servo servoDunk;
	Servo servoPress;
	Servo servoZip;
	Servo servoHangL;
	//Servo servoClimb;
	DeviceInterfaceModule dim;
	int count = 2;
	int currentPos = 0;
	int startPosSwipe;

	//master driving DC motor power variables
	double vr = .9;//velocity conversion for right
	double vl = .54;//velocity conversion for left


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
		servoZip = hardwareMap.servo.get("servoZip");
		servoHangL = hardwareMap.servo.get("servoHangL");
		//	servoClimb = hardwareMap.servo.get("servoClimb");
		dim = hardwareMap.deviceInterfaceModule.get("Device Interface Module 2");

		//reverse left motor
		motorLeft.setDirection(DcMotor.Direction.REVERSE);

		//info settings
		telemetry.addData("count: ", count);
		telemetry.addData("touched: ", sensorTouch.isPressed());
		telemetry.addData("sensorColor:", sensorColor.red());

		//enable LED on the color sensor
		sensorColor.enableLed(true);

		//IDK...
		dim.setDigitalChannelMode(5, DigitalChannelController.Mode.OUTPUT);

		//turns LED on
		dim.setDigitalChannelState(5, true);

		//turns LED off
		dim.setDigitalChannelState(5, false);

		servoPress.setPosition(.4);
		servoZip.setPosition(.99);//in
		servoDunk.setPosition(.97);
		servoHangL.setPosition(.8);
		//servoClimb.setPosition(.9);
		

		currentPos = motorRight.getCurrentPosition();
		startPosSwipe = motorSwipe.getCurrentPosition();
	}






	@Override
	public void loop() {


			//constantly update info settings
			telemetry.addData("count: ", count);
			telemetry.addData("touched: ", sensorTouch.isPressed());

			//IDK...
			float hsvValues[] = {0, 0, 0};
			final float values[] = hsvValues;
			final View relativeLayout = ((Activity) hardwareMap.appContext).findViewById(R.id.RelativeLayout);

			Color.RGBToHSV(sensorColor.red() * 8, sensorColor.green() * 8, sensorColor.red() * 8, hsvValues);
			telemetry.addData("Blue ", sensorColor.red());
			Color.RGBToHSV((sensorFruity.red() * 255) / 800, (sensorFruity.green() * 255) / 800, (sensorFruity.blue() * 255) / 800, hsvValues);
			telemetry.addData("Fru Red  ", sensorFruity.red());
			telemetry.addData("Fru Blue ", sensorFruity.blue());
			telemetry.addData("MotorEncoder", motorRight.getCurrentPosition());
			telemetry.addData("MotorEncoder difference", motorRight.getCurrentPosition() - currentPos);


			//Starts robot actions
			switch (count) {
/*
				//forward
				case 0:
					motorLeft.setPower(vl);
					motorRight.setPower(vr);
					if (motorRight.getCurrentPosition() > currentPos + 700) {
						currentPos = motorRight.getCurrentPosition();
						count++;
					}
					break;

				//turns to angle
				case 1:
					motorLeft.setPower(vl);
					motorRight.setPower(-vr);
					if (motorRight.getCurrentPosition() < currentPos - 475) {
						currentPos = motorRight.getCurrentPosition();
						count++;
					}
					break;*/

				//slows down after 15000
				case 2:
					motorLeft.setPower(vl);
					motorRight.setPower(vr);
					if (motorRight.getCurrentPosition() > currentPos + 13500)
						count++;
					break;

				//drive until a white line or more than 24000
				case 3:
					if (motorRight.getCurrentPosition() < currentPos + 19000) {
						if (sensorColor.red() < 35) {
							motorLeft.setPower(vl * 0.8);
							motorRight.setPower(vr * 0.8);
						} else {//if it hits the line
							motorLeft.setPower(0);
							motorRight.setPower(0);
							currentPos = motorRight.getCurrentPosition();
							count = 8;//hit line
						}
					} else {
						motorLeft.setPower(0);
						motorRight.setPower(0);
						currentPos = motorRight.getCurrentPosition();
						count = 4;
					}
					break;

				case 4://if missed swipe
					motorSwipe.setPower(-.3);
					if (motorSwipe.getCurrentPosition() < (startPosSwipe - 300)) {
						motorSwipe.setPower(0);
						count++;
					}
					break;

				case 5:
					motorSwipe.setPower(.3);
					if (motorSwipe.getCurrentPosition() > (startPosSwipe - 30)) {
						motorSwipe.setPower(0);
						count++;
					}
					break;

				//if missed white line, turn and go until white line
				case 6:
					if (sensorColor.red() < 20) {
						if (motorRight.getCurrentPosition() > currentPos - 2800) {//Turning after miss
							motorLeft.setPower(vl);
							motorRight.setPower(-vr);
						} else {//Driving straight after miss
							motorLeft.setPower(vl * 0.8);
							motorRight.setPower(vr * 0.8);
						}
					} else {
						motorLeft.setPower(0);
						motorRight.setPower(0);
						currentPos = motorRight.getCurrentPosition();
						count++;
					}

					break;

				//follow line until touch sensor is pressed if missed line originally
				case 7:
					if (sensorTouch.isPressed()) {
						motorLeft.setPower(.0);
						motorRight.setPower(.0);
						currentPos = motorRight.getCurrentPosition();
						count = 12;//go to beacon business
					} else {
						if (sensorColor.red() > 35) {
							motorLeft.setPower(vl * 0.8);
							motorRight.setPower(vr * 0.8);
						} else {
							motorLeft.setPower(0);
							motorRight.setPower(vr * 0.8);
						}

					}
					break;

				case 8://HIT LINE swipe
					motorSwipe.setPower(-.3);
					if (motorSwipe.getCurrentPosition() < (startPosSwipe - 400)) {

						motorSwipe.setPower(0);
						count++;
					}
					break;

				case 9:
					motorLeft.setPower(vl);
					motorRight.setPower(vr);
					if(motorRight.getCurrentPosition() > currentPos + 300){
						currentPos = motorRight.getCurrentPosition();
						count++;
					}
					break;

				case 10:
					if (sensorColor.red() > 30) {//found line
						motorLeft.setPower(0);
						motorRight.setPower(0);
						count++;
					} else {
						motorLeft.setPower(vl * 0.8);
						motorRight.setPower(0);
					}
					break;

				case 11:
					motorSwipe.setPower(.3);
					if (motorSwipe.getCurrentPosition() > (startPosSwipe - 30)) {
						motorSwipe.setPower(0);
						count++;
					}
					break;

				//follow line until touch sensor is pressed if hit line on original path
				case 12:
					if (sensorTouch.isPressed()) {
						motorLeft.setPower(.0);
						motorRight.setPower(.0);
						currentPos = motorRight.getCurrentPosition();
						count++;//go to beacon business
					} else {
						if (sensorColor.red() > 20) {
							motorLeft.setPower(vl * 0.8);
							motorRight.setPower(vr * 0.8);
						} else {
							motorLeft.setPower(vl * 0.8);
							motorRight.setPower(0);
						}

					}
					break;


				case 13:
					servoDunk.setPosition(.02); //dunks the bros

					if (sensorFruity.blue() > sensorFruity.red()) {  //picks blue
						servoPress.setPosition(.10);//right
							count++;

					}
					else {
						servoPress.setPosition(.75);//left
							count++;
					}

					break;

/*
				//start of mountain parking: back up
				case 12:
					servoDunk.setPosition(.85);
					servoPress.setPosition(.12);
					motorRight.setPower(-vr);
					motorLeft.setPower(-vl);
					if (motorRight.getCurrentPosition() < currentPos - 5000) {
						currentPos = motorRight.getCurrentPosition();
						count++;
					}
					break;

				//mountain parking: turn
				case 13:
					motorLeft.setPower(-vl);
					motorRight.setPower(vr);
					if (motorRight.getCurrentPosition() > currentPos + 1400) {
						currentPos = motorRight.getCurrentPosition();
						count++;
					}
					break;

				//mountain parking: forward
				case 14:
					motorRight.setPower(vr);
					motorLeft.setPower(vl);
					if (motorRight.getCurrentPosition() > currentPos + 2000) {
						currentPos = motorRight.getCurrentPosition();
						count++;
					}
					break;

				//mountain parking: turn to orient robot in front of mountain opening
				case 15:
					motorRight.setPower(-vr);
					motorLeft.setPower(vl);
					if (motorRight.getCurrentPosition() < currentPos - 200) {
						currentPos = motorRight.getCurrentPosition();
						count++;
					}
					break;

				//mountain parking: forward onto mountain ftw!
				case 16:
					motorRight.setPower(vr);
					motorLeft.setPower(vl);
					if (motorRight.getCurrentPosition() > currentPos + 5000) {
						currentPos = motorRight.getCurrentPosition();
						count = 16;//go to last case
					}
					break;

				//start of floor zone parking: back up a teeny weeny bit
				case 17:
					motorRight.setPower(-vr * 0.5);
					motorLeft.setPower(-vl * 0.5);
					if (motorRight.getCurrentPosition() < currentPos - 350) {
						currentPos = motorRight.getCurrentPosition();
						count++;
					}
					break;

				//floor zone parking: turn to orient robot in front of floor zone
				case 18:
					motorRight.setPower(vr);
					motorLeft.setPower(-vl);
					if (motorRight.getCurrentPosition() > currentPos + 2200) {
						currentPos = motorRight.getCurrentPosition();
						count++;
					}
					break;

				//floor zone parking: drive into floor zone!
				case 19:
					motorRight.setPower(vr * 0.8);
					motorLeft.setPower(vl * 0.8);
					if (motorRight.getCurrentPosition() > currentPos + 2000) {
						currentPos = motorRight.getCurrentPosition();
						count++;//go to last case
					}
					break;

				case 20:
					motorLeft.setPower(0);
					motorRight.setPower(0);
					break;*/

				default:
					break;

			}

		}

	@Override
	public void stop(){

	}

	public static void sleep(long sleepTime){

		long wakeupTime = System.currentTimeMillis() + sleepTime;

		while(sleepTime < 0){

			try{
				Thread.sleep(sleepTime);
			}
			catch(InterruptedException e){

			}

			sleepTime = wakeupTime - System.currentTimeMillis();

		}

	}


}


