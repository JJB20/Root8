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

public class blueCornerBeaconTimer extends PushBotTelemetrySensors {


	public void swipe (){
		int done = 0;
		/*int toggle = 0;
		int swipeCount = 0;
		double vr = 1.0;//velocity conversion for right
		double vl = 0.45;//velocity conversion for left
		int startPosSwipe;
		int lastPosSwipe;
		//DcMotor motorR;
		//DcMotor motorL;
		//DcMotor motorSwipe;
		//import com.qualcomm.robotcore.hardware.DcMotor;

		lastPosSwipe = motorSwipe.getCurrentPosition();//for swipeCount
		startPosSwipe = motorSwipe.getCurrentPosition();

		//motorL = hardwareMap.dcMotor.get("motorL");
		//motorR = hardwareMap.dcMotor.get("motorR");
		//motorSwipe = hardwareMap.dcMotor.get("motorSwipe");*/

		while(done == 0) {
			if (toggle == 0) {
				motorSwipe.setPower(-.45);
				if (motorSwipe.getCurrentPosition() < (startPosSwipe - 420)) {
					motorSwipe.setPower(0);
					motorL.setPower(0);
					motorR.setPower(0);
					lastPosSwipe = motorSwipe.getCurrentPosition();
					toggle = 1;
				} else {
					if (motorSwipe.getCurrentPosition() == lastPosSwipe) {
						swipeCount++;
						if (swipeCount > 5) {
							motorL.setPower(-vl * 0.5);
							motorR.setPower(-vr * 0.5);
						}
					} else {
						motorL.setPower(0);
						motorR.setPower(0);
						swipeCount = 0;
					}

				}
				lastPosSwipe = motorSwipe.getCurrentPosition();
			}
			if (toggle == 1) {
				motorSwipe.setPower(.45);//in
				if (motorSwipe.getCurrentPosition() > (startPosSwipe - 30)) {
					motorSwipe.setPower(0);
					motorL.setPower(0);
					motorR.setPower(0);
					lastPosSwipe = motorSwipe.getCurrentPosition();
					toggle = 2;
				}
				else{
					if(motorSwipe.getCurrentPosition() == lastPosSwipe){
						swipeCount++;
						if (swipeCount > 5) {
							toggle = 4;//block stuck when swiper swipe in
						}
						else {
							motorL.setPower(0);
							motorR.setPower(0);
							swipeCount = 0;
						}
					}
				}
				lastPosSwipe = motorSwipe.getCurrentPosition();

			}
			if (toggle == 2) {
				motorSwipe.setPower(-.45);//out
				if (motorSwipe.getCurrentPosition() < (startPosSwipe - 420)) {
					motorSwipe.setPower(0);
					toggle = 2;
				}
			}
			if (toggle == 3) {
				motorSwipe.setPower(.45);//in
				if (motorSwipe.getCurrentPosition() > (startPosSwipe - 30)) {
					motorSwipe.setPower(0);
					toggle = 3;
					done = 1;//
				}
			}
			if(toggle ==4){
				motorSwipe.setPower(-.45);
				if(motorSwipe.getCurrentPosition() < startPosSwipe - 420){
					motorL.setPower(-vl);
					motorR.setPower(-vr);
					if(motorR.getCurrentPosition() < currentPos - 200){
						motorL.setPower(0);
						motorR.setPower(0);
						currentPos = motorR.getCurrentPosition();
						toggle = 5;
					}

				}

			}
			if(toggle == 5){
				motorSwipe.setPower(.45);
				if(motorSwipe.getCurrentPosition() > startPosSwipe - 30){
					motorL.setPower(vl);
					motorR.setPower(vr);
					if(motorR.getCurrentPosition() > currentPos + 200){
						motorR.setPower(0);
						motorL.setPower(0);
						currentPos = motorR.getCurrentPosition();
						toggle = 1;
					}
				}
			}
		}
	}

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
	int count = -5;
	int swipeCount = 0;
	int moveCount = 0;
	int currentPos = 0;
	int lastPos = 0;//for moveCount
	int toggle = 0;
	int startPosSwipe;
	int lastPosSwipe;//for swipeCount
	String location = "";

	int timerCount = 0;
	Boolean eightbackup = false;
	int paddle = 0;

	//master driving DC motor power variables
	double vr = 0.8;//velocity conversion for right
	double vl = 0.45;//velocity conversion for left


	@Override
	public void init() {

		//retrieve motors and sensors from configuration
		sensorTouch = hardwareMap.touchSensor.get("sensorTouch");
		sensorColor = hardwareMap.colorSensor.get("sensorColor");
		sensorFruity = hardwareMap.colorSensor.get("sensorFruity");
		motorL = hardwareMap.dcMotor.get("motorL");
		motorR = hardwareMap.dcMotor.get("motorR");
		motorSwipe = hardwareMap.dcMotor.get("motorSwipe");
		servoDunk = hardwareMap.servo.get("servoDunk");
		servoPress = hardwareMap.servo.get("servoPress");
		servoHold = hardwareMap.servo.get("servoHold");
		servoZipRed = hardwareMap.servo.get("servoZipRed");
		servoZipBlue = hardwareMap.servo.get("servoZipBlue");

		dim = hardwareMap.deviceInterfaceModule.get("Device Interface Module 2");

		//reverse right motor
		motorL.setDirection(DcMotor.Direction.REVERSE);

		//info settings
		telemetry.addData("count: ", count);
		telemetry.addData("touched: ", sensorTouch.isPressed());
		telemetry.addData("sensorColor:", sensorColor.green());
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
		servoDunk.setPosition(.97);//back
		servoHold.setPosition(.01);//up
		

		currentPos = motorR.getCurrentPosition();
		lastPos = motorR.getCurrentPosition();
		lastPosSwipe = motorSwipe.getCurrentPosition();//for swipeCount
		startPosSwipe = motorSwipe.getCurrentPosition();
	}



	@Override
	public void loop() {

		//constantly update info settings
		telemetry.addData("count: ", count);
		telemetry.addData("touched: ", sensorTouch.isPressed());
		telemetry.addData("location: ", location);
		telemetry.addData("timerCount: ", timerCount);

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
		telemetry.addData("MotorEncoder difference", motorR.getCurrentPosition() - currentPos);
		telemetry.addData("motorSwipe", motorSwipe.getCurrentPosition());
		telemetry.addData("motorSwipe difference", motorSwipe.getCurrentPosition() - startPosSwipe);
		telemetry.addData("eightbackup", eightbackup);
		telemetry.addData("paddle: ", paddle);
		telemetry.addData("toggle: ", toggle);


		//Starts robot actions
		switch (count) {

			case -5:
				swipe();
				count = -4;
				break;

			case -4:
				count = -3;
				break;

			case -3:
				motorL.setPower(vl * 0.7);
				motorR.setPower(vr * 0.7);
				if (motorR.getCurrentPosition() > currentPos + 6000) {
					motorR.setPower(0);
					motorL.setPower(0);
					currentPos = motorR.getCurrentPosition();
					count = -2;
				}
				break;

			case -2:
				motorSwipe.setPower(-.45);
				if (motorSwipe.getCurrentPosition() < (startPosSwipe - 300)) {//was 420 1/18
					motorSwipe.setPower(0);
					motorL.setPower(0);
					motorR.setPower(0);
					lastPosSwipe = motorSwipe.getCurrentPosition();
					count = -1;//swipe successful
				}
				else{
					if(motorSwipe.getCurrentPosition() == lastPosSwipe){
						swipeCount++;
						if(swipeCount > 5){
							motorL.setPower(-vl * 0.5);
							motorR.setPower(-vr * 0.5);
						}
					}
					else{
						motorL.setPower(0);
						motorR.setPower(0);
						swipeCount = 0;
					}

				}
				lastPosSwipe = motorSwipe.getCurrentPosition();
				break;

			case -1:
				if (toggle == 0) {
					motorSwipe.setPower(.45);//in
					if (motorSwipe.getCurrentPosition() > (startPosSwipe - 30)) {
						motorSwipe.setPower(0);
						toggle = 1;
					}
				}
				if (toggle == 1) {
					motorSwipe.setPower(-.45);//out
					if (motorSwipe.getCurrentPosition()  < (startPosSwipe - 420)) {
						motorSwipe.setPower(0);
						toggle = 2;
					}
				}
				if (toggle == 2) {
					motorSwipe.setPower(.45);//in
					if (motorSwipe.getCurrentPosition() > (startPosSwipe - 30)) {
						motorSwipe.setPower(0);
						toggle = 0;
						count = 0;//Swipes all executed
					}
				}
				break;

			case 0:
				motorL.setPower(vl * 0.7);
				motorR.setPower(vr * 0.7);
				if (motorR.getCurrentPosition() > currentPos + 6000) {
					currentPos = motorR.getCurrentPosition();
					motorR.setPower(0);
					motorL.setPower(0);
					count = 1;
				}
				break;

			case 1:
				motorSwipe.setPower(-.45);
				if (motorSwipe.getCurrentPosition() < (startPosSwipe - 420)) {
					motorSwipe.setPower(0);
					motorL.setPower(0);
					motorR.setPower(0);
					lastPosSwipe = motorSwipe.getCurrentPosition();
					count = 2;//swipe successful
				}
				else{
					if(motorSwipe.getCurrentPosition() == lastPosSwipe){
						swipeCount++;
						if(swipeCount > 5){
							motorL.setPower(-vl * 0.5);
							motorR.setPower(-vr * 0.5);
						}
					}
					else{
						motorL.setPower(0);
						motorR.setPower(0);
						swipeCount = 0;
					}

				}
				lastPosSwipe = motorSwipe.getCurrentPosition();
				break;

			case 2:
				if (toggle == 0) {
					motorSwipe.setPower(.45);//in
					if (motorSwipe.getCurrentPosition() > (startPosSwipe - 30)) {
						motorSwipe.setPower(0);
						toggle = 1;
					}
				}
				if (toggle == 1) {
					motorSwipe.setPower(-.45);//out
					if (motorSwipe.getCurrentPosition()  < (startPosSwipe - 420)) {
						motorSwipe.setPower(0);
						toggle = 2;
					}
				}
				if (toggle == 2) {
					motorSwipe.setPower(.45);//in
					if (motorSwipe.getCurrentPosition() > (startPosSwipe - 30)) {
						motorSwipe.setPower(0);
						toggle = 0;
						count = 3;//Swipes all executed
					}
				}
				break;

			//drive until a white line or more than 19000
			case 3:
				if (motorR.getCurrentPosition() < currentPos + 5900) {
					if (sensorColor.green() < 20) {
						motorL.setPower(vl * 0.55);//was .8 1/18
						motorR.setPower(vr * 0.55);
					} else {//if it hits the line
						motorL.setPower(0);
						motorR.setPower(0);
						currentPos = motorR.getCurrentPosition();
						count = 8;//hit line on first try
					}
				} else {
					motorL.setPower(0);
					motorR.setPower(0);
					currentPos = motorR.getCurrentPosition();
					count = 4;//missed line on first try
				}

				break;


			case 4://if missed line-- swipe
				motorSwipe.setPower(-.45);
				if (motorSwipe.getCurrentPosition() < (startPosSwipe - 420)) {
					motorSwipe.setPower(0);
					motorL.setPower(0);
					motorR.setPower(0);
					lastPosSwipe = motorSwipe.getCurrentPosition();
					count = 5;//swipe successful
				}
				else{
					if(motorSwipe.getCurrentPosition() == lastPosSwipe){
						swipeCount++;
						if(swipeCount > 5){
							eightbackup = true;
							motorL.setPower(-vl * 0.5);
							motorR.setPower(-vr * 0.5);
						}
					}
					else{
						motorL.setPower(0);
						motorR.setPower(0);
						swipeCount = 0;
					}

				}
				lastPosSwipe = motorSwipe.getCurrentPosition();
				break;

			case 5: //swipe in out in
				if (toggle == 0) {
					motorSwipe.setPower(.45);//in
					if (motorSwipe.getCurrentPosition() > (startPosSwipe - 30)) {
						motorSwipe.setPower(0);
						toggle = 1;
					}
				}
				if (toggle == 1) {
					motorSwipe.setPower(-.45);//out
					if (motorSwipe.getCurrentPosition()  < (startPosSwipe - 420)) {
						motorSwipe.setPower(0);
						toggle = 2;
					}
				}
				if (toggle == 2) {
					motorSwipe.setPower(.45);//in
					if (motorSwipe.getCurrentPosition() > (startPosSwipe - 30)) {
						motorSwipe.setPower(0);
						toggle = 0;
						count = 6;//Swipes all executed
					}
				}
				break;

			//if missed white line, turn and go until white line
			case 6:
				if (sensorColor.green() < 20) {
					if (motorR.getCurrentPosition() > currentPos - 2600) {//Turning after miss
						motorL.setPower(vl * 0.55);
						motorR.setPower(-vr * 0.55);
					} else {//Driving straight after miss
						motorL.setPower(vl * 0.55);
						motorR.setPower(vr * 0.55);
					}
				} else {
					motorL.setPower(0);
					motorR.setPower(0);
					currentPos = motorR.getCurrentPosition();
					count = 8;//loops back into program as if line was hit originally(goes into swipe)
				}
				break;

			//follow line until touch sensor is pressed if missed line originally
			case 7:

				break;

			//hit line first time-Swipe out with backup
			case 8:
				motorSwipe.setPower(-.45);
				if (motorSwipe.getCurrentPosition() < (startPosSwipe - 420)) {
					motorSwipe.setPower(0);
					motorL.setPower(0);
					motorR.setPower(0);
					lastPosSwipe = motorSwipe.getCurrentPosition();
					count = 9;//swipe successful
				}
				else{
					if(motorSwipe.getCurrentPosition() == lastPosSwipe){
						swipeCount++;
						if(swipeCount > 5){
							eightbackup = true;
							motorL.setPower(-vl * 0.5);
							motorR.setPower(-vr * 0.5);
						}
					}
					else{
						motorL.setPower(0);
						motorR.setPower(0);
						swipeCount = 0;
					}

				}
				lastPosSwipe = motorSwipe.getCurrentPosition();
				break;

			case 9: //swipe in out in
				if (toggle == 0) {
					motorSwipe.setPower(.45);//in
					if (motorSwipe.getCurrentPosition() > (startPosSwipe - 30)) {
						motorSwipe.setPower(0);
						toggle = 1;
					}
				}
				if (toggle == 1) {
					motorSwipe.setPower(-.45);//out
					if (motorSwipe.getCurrentPosition()  < (startPosSwipe - 420)) {
						motorSwipe.setPower(0);
						toggle = 2;
					}
				}
				if (toggle == 2) {
					motorSwipe.setPower(.45);//in
					if (motorSwipe.getCurrentPosition() > (startPosSwipe - 30)) {
						motorSwipe.setPower(0);
						toggle = 0;
						count = 10;//Swipes all executed
					}
				}
				break;

			case 10://drive until white line if drove backwards to swipe
				if(eightbackup) {
					if (sensorColor.green() < 20) {//gray
						motorR.setPower(vr * .5);
						motorL.setPower(vl * 0.5);
					} else {//white
						motorR.setPower(0);
						motorL.setPower(0);
						count = 11;
					}
				}
				else if(!eightbackup){
					count = 11;
				}
				break;

			//follow line until touch sensor is pressed if hit line on original path
			case 11:
				if (sensorTouch.isPressed()) {
					motorL.setPower(.0);
					motorR.setPower(.0);
					currentPos = motorR.getCurrentPosition();
					timerCount = 0;
					count = 16;//go to dunking bros
				}
				else {
					if (sensorColor.green() > 20) {//white
						motorL.setPower(0);
						motorR.setPower(vr * 0.55);
					} else {//gray
						motorL.setPower(vr * 0.55);
						motorR.setPower(0);
					}
				}
				timerCount++;

				if(timerCount > 400){//If crunched against the wall, do case 12
					motorL.setPower(0);
					motorR.setPower(0);
					timerCount = 0;
					count = 12;
				}
				break;

			case 12:
				motorSwipe.setPower(-.45);
				if (motorSwipe.getCurrentPosition() < (startPosSwipe - 420)) {
					motorSwipe.setPower(0);
					motorL.setPower(0);
					motorR.setPower(0);
					lastPosSwipe = motorSwipe.getCurrentPosition();
					count = 13;
				}
				else{
					if(motorSwipe.getCurrentPosition() == lastPosSwipe){
						swipeCount++;
						if(swipeCount > 5){
							motorL.setPower(-vl * 0.5);
							motorR.setPower(-vr * 0.5);
						}
					}
					else{
						motorL.setPower(0);
						motorR.setPower(0);
						swipeCount = 0;
					}

				}
				lastPosSwipe = motorSwipe.getCurrentPosition();
				break;

			case 13:
				if (toggle == 0) {
					motorSwipe.setPower(.45);//in
					if (motorSwipe.getCurrentPosition() > (startPosSwipe - 30)) {
						motorSwipe.setPower(0);
						toggle = 1;
					}
				}
				if (toggle == 1) {
					motorSwipe.setPower(-.45);//out
					if (motorSwipe.getCurrentPosition()  < (startPosSwipe - 420)) {
						motorSwipe.setPower(0);
						toggle = 2;
					}
				}
				if (toggle == 2) {
					motorSwipe.setPower(.45);//in
					if (motorSwipe.getCurrentPosition() > (startPosSwipe - 30)) {
						motorSwipe.setPower(0);
						toggle = 0;
						count = 14;//
					}
				}
				break;

			case 14: //drive until white line
				if (sensorColor.green() < 20) {//gray
					motorR.setPower(vr * .5);
					motorL.setPower(vl * 0.5);
				} else {//white
					motorR.setPower(0);
					motorL.setPower(0);
					count = 15;
				}
				break;

			case 15://second attempt to get touchSensor
				if (sensorTouch.isPressed()) {
					motorL.setPower(.0);
					motorR.setPower(.0);
					currentPos = motorR.getCurrentPosition();
					timerCount = 0;
					count = 16;//go to dunking bros
				}
				else {
					if (sensorColor.green() > 20) {//white
						motorL.setPower(0);
						motorR.setPower(vr * 0.55);
					} else {//gray
						motorL.setPower(vl * 0.55);
						motorR.setPower(0);
					}
				}
				timerCount++;

				if(timerCount > 400){//If crunched against the wall, park
					motorL.setPower(0);
					motorR.setPower(0);
					timerCount = 0;
					count = 20;
				}
				break;

			case 16:
				servoDunk.setPosition(.02); //dunks the bros

				timerCount ++;
				if(timerCount > 200) {
					timerCount = 0;
					if (sensorFruity.blue() < sensorFruity.red()) {  //picks blue
						paddle = -1;//right
					}
					else {
						paddle = 1;//left
					}
					count = 17;
				}
				break;

			case 17:
				motorL.setPower(vl * -0.55);
				motorR.setPower(vr * -0.55);
				servoDunk.setPosition(.97);
				if(motorR.getCurrentPosition() < currentPos - 500){
					motorL.setPower(0);
					motorR.setPower(0);
					if(paddle == 1) {
						servoPress.setPosition(.10);//right
					}
					else if(paddle == -1){
						servoPress.setPosition(.75);//left
					}
					timerCount++;
					if(timerCount > 70) {
						timerCount = 0;
						count = 18;
					}
				}
				break;

			case 18:
				motorL.setPower(vl * 0.55);
				motorR.setPower(vr * 0.55);
				timerCount ++;
				if(timerCount > 70){
					timerCount = 0;
					count = 19;
				}
				break;

			case 19:
				motorL.setPower(0);
				motorR.setPower(0);
				break;



			default:
				motorL.setPower(0);
				motorR.setPower(0);
				break;

		}

	}
	@Override
	public void stop(){

	}






}

