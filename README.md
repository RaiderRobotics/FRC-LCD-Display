# FRC-LCD-Display

:large_blue_diamond: The source code here demonstrates how to set up a 4x20 LCD panel display with the Roborio (for FRC First Robotics Competition).

All you have to do to use it is add the code that I've written, and then run the command `LCDwriteString(String s, int line);` where String s is the string to write to the display, and int line is line number (1-4).

![p1](https://raw.githubusercontent.com/RaiderRobotics/FRC-LCD-Display/master/LCD1.jpg)
![p2](https://raw.githubusercontent.com/RaiderRobotics/FRC-LCD-Display/master/joystick.jpg)
![p3](https://raw.githubusercontent.com/RaiderRobotics/FRC-LCD-Display/master/LCD2.jpg)

-------------------
##  First the hardware:

:black_small_square: We're using a display like the one in the photo 

![photo1](https://github.com/RaiderRobotics/FRC-LCD-Display/blob/master/4x20-LCD-panel.jpg). 

![p5](https://raw.githubusercontent.com/RaiderRobotics/FRC-LCD-Display/master/LCDback.jpg)

:black_small_square: Note that it comes with a backplate thing mounted on it. This backplate already has all of the connections between the LCD display and the I2C protocol.

![photo2](https://github.com/RaiderRobotics/FRC-LCD-Display/blob/master/YwRobotLCD-CU-450.jpg) 

:black_small_square: The connectors on the back of the LCD display do not match the connectors on the RoboRIO. The RoboRIO has the pins in a different order. Also note that the LCD display requires 5V, not the 3.3V on the RoboRIO I2C connector. We're just using 5V from a digital IO pin.  You could also use the 5V from the Voltage Regulator Module.

<img src="https://github.com/RaiderRobotics/FRC-LCD-Display/blob/master/I2C_connector.jpg" width="300" height="340">


-------------------------------

## CODE

### Background information

:boom: **WPI Documentation** The [WPI documenation](http://first.wpi.edu/FRC/roborio/stable/docs/java/classedu_1_1wpi_1_1first_1_1wpilibj_1_1I2C.html) is completely useless.  The [source code](https://usfirst.collab.net/gerrit/gitweb?p=allwpilib.git;f=wpilibj/wpilibjava/src/main/java/edu/wpi/first/wpilibj/I2C.java;h=8476) is no help either.

It's easy to make an I2C object: `lcdDisplay = new I2C(I2C.Port.kOnboard, 0x27);`  The address of most LCD panels is 0x27. It's a lot harder to write to the display since the only thing that the documentation tells you is `boolean 	write (int registerAddress, int data)`. There is no explanation of what the register address should be, no examples of this being used anywhere. *(I finally figured it out, see below.)*
 
:boom: LCD Panels are all driven by the [Hitachi HD44780 driver](http://www.waveshare.com/datasheet/LCD_en_PDF/HD44780.pdf). This page provides details, explains the display addressing, and lists the commands that are build in.

:boom: There are some very useful code examples here:

* LCD panel display documentation:  http://www.microcontrollerboard.com/lcd.html
* Using Java to access I2C devices with examples of LCD display:  https://docs.oracle.com/javame/8.1/me-dev-guide/i2c.htm
* This helped understand the hardware a bit. http://letsmakerobots.com/content/drive-standard-hd44780-lcd-using-pcf8574-and-i2c
 
:boom: Finally, I realized that [my python code](https://github.com/salamander2/RaspberryPi/tree/master/programs/LCD) :snake: (which I got from someone else's Raspberry Pi repository) could be directly ported to Java.  **It worked!!!**

-----------------

### How the code works

* It turns out that the register to write the I2C data is ALWAYS 0 (I'm talking about the WPI `I2C.write()` command).  Why? This is either the I2C controller or the register on the I2C device.  So it seems that the LCD display only has one (external facing) register.
* However ... internally, there are TWO registers: a command register and a data/display register. The registers are INTERNAL and are selected by using the Rs bit or not.
* Commands are always written 4 bits at a time. Why? This is because it's the way that everyone does it. (Originally it was so that you need fewer data lines connected to your display.)
* Commands have to be "strobed" using the En bit for them to take effect.
* Commands do not execute instantaneously, so you need a short delay between them.
* There is a sequence of initialization commands that must be done to set up the display after it's been powered on before it can be used.
* Many of the other commands (cursor movement, etc.) I have not tried.
	 
