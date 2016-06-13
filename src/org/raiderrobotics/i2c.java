package org.raiderrobotics;

import edu.wpi.first.wpilibj.*;

/* 
 * LCD display panel code written by Michael Harwood, FRC Team 5024. November 2015.
 * Please see https://github.com/RaiderRobotics/FRC-LCD-Display for source code repository and complete documentation.
 *
 * This java code is not originally mine. 
 * It is based on my python code which in turn came from Paul Barber: https://github.com/paulbarber/raspi-gpio
 *
 * Feel free to copy it and modify it as desired
 */

public class Robot extends IterativeRobot {

	/* **********************************************************
	 *      Constants for LCD Panel
	 * ********************************************************/
	// LCD Commands
	private static final int LCD_CLEARDISPLAY = 0x01;
	private static final int LCD_RETURNHOME = 0x02;
	private static final int LCD_ENTRYMODESET = 0x04;
	private static final int LCD_DISPLAYCONTROL = 0x08;
	private static final int LCD_CURSORSHIFT = 0x10;
	private static final int LCD_FUNCTIONSET = 0x20;
	private static final int LCD_SETCGRAMADDR = 0x40;
	private static final int LCD_SETDDRAMADDR = 0x80;

	// Flags for display on/off control
	private static final int LCD_DISPLAYON = 0x04;
	private static final int LCD_DISPLAYOFF = 0x00;
	private static final int LCD_CURSORON = 0x02;
	private static final int LCD_CURSOROFF = 0x00;
	private static final int LCD_BLINKON = 0x01;
	private static final int LCD_BLINKOFF = 0x00;

	// Flags for display entry mode
	// private static final int LCD_ENTRYRIGHT = 0x00;
	private static final int LCD_ENTRYLEFT = 0x02;
	private static final int LCD_ENTRYSHIFTINCREMENT = 0x01;
	private static final int LCD_ENTRYSHIFTDECREMENT = 0x00;

	// Flags for display/cursor shift
	private static final int LCD_DISPLAYMOVE = 0x08;
	private static final int LCD_CURSORMOVE = 0x00;
	private static final int LCD_MOVERIGHT = 0x04;
	private static final int LCD_MOVELEFT = 0x00;

	// flags for function set
	private static final int LCD_8BITMODE = 0x10;
	private static final int LCD_4BITMODE = 0x00;
	private static final int LCD_2LINE = 0x08;	//for 2 or 4 lines actually
	private static final int LCD_1LINE = 0x00;
	private static final int LCD_5x10DOTS = 0x04;	//seldom used!!
	private static final int LCD_5x8DOTS = 0x00;	

	// flags for backlight control
	private static final int LCD_BACKLIGHT = 0x08;
	private static final int LCD_NOBACKLIGHT = 0x00;

	//bitmasks for register control
	private static final int En = 0b00000100; // Enable bit
	private static final int Rw = 0b00000010; // Read/Write bit
	private static final int Rs = 0b00000001; // Register select bit

	/* *********************************************************************************
	 *      End of LCD constants
	 *  ********************************************************************************/

	final static double MAXSPEED = 0.50;

	//	instance variables
	RobotDrive driveTrain;
	Joystick stick1;
	Talon talon1, talon2;
	I2C lcdDisplay;

	long startTime = 0;

	public void robotInit() {
		//first to run, only runs once
		talon1 = new Talon(1);
		talon2 = new Talon(2);
		driveTrain = new RobotDrive(talon1, talon2);
		stick1 = new Joystick(0);
		//invert motors
		driveTrain.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
		driveTrain.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
		driveTrain.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
		driveTrain.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);

		initLCD();
	}

	/* ***************************************************************************
	 *      Methods for using LCD Display
	 * **************************************************************************/

	void initLCD() {
		lcdDisplay = new I2C(I2C.Port.kOnboard, 0x27);

		LCDwriteCMD(0x03);
		LCDwriteCMD(0x03); 
		LCDwriteCMD(0x03); 
		LCDwriteCMD(0x02);
		//4 bit mode??? -- yes. Always. It's the default way of doing this for LCD displays
		LCDwriteCMD( LCD_FUNCTIONSET | LCD_2LINE | LCD_5x8DOTS | LCD_4BITMODE );
		LCDwriteCMD( LCD_DISPLAYCONTROL | LCD_DISPLAYON );    
		LCDwriteCMD( LCD_CLEARDISPLAY );
		LCDwriteCMD( LCD_ENTRYMODESET | LCD_ENTRYLEFT );
		zsleep(10);
	}

	//write a sleep method to get rid of the try-catch stuff
	void zsleep(int t) {
		try { Thread.sleep(t);
		} catch(InterruptedException e) {}
	}

	//This is for writing commands, 4 bits at a time 
	void LCDwriteCMD (int data) {
		LCD_rawWrite(data & 0xF0);
		LCD_rawWrite((data <<4 ) & 0xF0);
	}

	//This is for writing a character, 4 bits at a time
	void LCDwriteChar ( int data) {
		LCD_rawWrite( Rs |  (data & 0xF0));
		LCD_rawWrite( Rs | ((data <<4 ) & 0xF0));
	}

	void LCD_rawWrite( int data) {
		lcdDisplay.write(0, data | LCD_BACKLIGHT );
		strobe(data);
	}

	void strobe(int data){
		//    	Syntax: lcdDisplay.write(reg,data);    	
		lcdDisplay.write(0, data | En | LCD_BACKLIGHT );
		zsleep(1);
		lcdDisplay.write(0, (data & ~En) | LCD_BACKLIGHT );
		zsleep(1);
	}

	//This is the "public" method. The one that is actually used by other code to write to the display.
	void LCDwriteString(String s, int line) {
		switch (line) {
		case 1: LCDwriteCMD(0x80); break;
		case 2: LCDwriteCMD(0xC0); break;
		case 3: LCDwriteCMD(0x94); break;
		case 4: LCDwriteCMD(0xD4); break;
		default: return;	//invalid line number does nothing.
		}

		//limit to 20 chars/line so we don't have to worry about overflow messing up the display
		if (s.length() > 20) s = s.substring(0, 20); 

		for (int i=0; i<s.length(); i++){
			LCDwriteChar(s.charAt(i));
		}
	}
	/* ****************************************************************************************
	 *     End of LCD methods
	 * ****************************************************************************************/



	/****************************************** VERY SIMPLE ROBOT DRIVE CODE *****************/
	
	@Override
	public void autonomousInit(){
		startTime = System.currentTimeMillis();   	
	}

	@Override
	public void autonomousPeriodic() {
		if(System.currentTimeMillis() - startTime < 2000)	//for 2 seconds
			driveTrain.drive(0.1,0.0);
		else
			driveTrain.stopMotor();    
	}

	@Override
	public void teleopPeriodic() {
		double xAxis = stick1.getX();
		double yAxis = stick1.getY();

		if(stick1.getRawButton(2)) {
			LCDwriteCMD(LCD_CLEARDISPLAY);
		}

		if(stick1.getRawButton(3)) {        	
			LCDwriteCMD(LCD_CLEARDISPLAY);
			LCDwriteString("FRC Team 5024",2);
			LCDwriteString("Raider Robotics",4);
		}
		if(stick1.getRawButton(4)) {
			LCDwriteCMD(LCD_CLEARDISPLAY);
			LCDwriteString("Stop pushing my",1);
			LCDwriteString("buttons!!!",2);
			LCDwriteString("Self destruct",3);
			LCDwriteString("activated...",4);
		}

		driveTrain.arcadeDrive(yAxis*MAXSPEED, xAxis*MAXSPEED);

		//because of refresh problems, this works if you just quickly press button1 instead of holding it down,
		if(stick1.getRawButton(1)) {
			LCDwriteCMD(LCD_CLEARDISPLAY);
			LCDwriteString("Joystick values:",1);
			LCDwriteString("x="+xAxis,2);
			LCDwriteString("y="+yAxis,3);
		}
	}

}
