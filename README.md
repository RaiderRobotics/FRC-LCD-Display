# FRC-LCD-Display

:large_blue_diamond: The source code here demonstrates how to set up a 4x20 LCD panel display with the Roborio (for FRC First Robotics Competition).

-------------------
##  First the hardware:

:black_small_square: We're using a display like the one in the photo 

![photo1](https://github.com/RaiderRobotics/FRC-LCD-Display/blob/master/4x20-LCD-panel.jpg). 

:black_small_square: Note that it comes with a backplate thing mounted on it. This backplate already has all of the connections between the LCD display and the I2C protocol.

![photo2](https://github.com/RaiderRobotics/FRC-LCD-Display/blob/master/YwRobotLCD-CU-450.jpg) 

:black_small_square: The connectors on the back of the LCD display do not match the connectors on the RoboRIO. The RoboRIO has the pins in a different order. Also note that the LCD display requires 5V, not the 3.3V on the RoboRIO I2C connector. We're just using 5V from a digital IO pin.  You could also use the 5V from the Voltage Regulator Module.
<img src="https://github.com/RaiderRobotics/FRC-LCD-Display/blob/master/I2C_connector.jpg" width="300" height="340">


-------------------------------

## CODE
