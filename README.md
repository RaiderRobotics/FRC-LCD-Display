# FRC-LCD-Display

The source code here demonstrates how to set up a 4x20 LCD panel display with the Roborio (for FRC First Robotics Competition).

-------------------
:large_blue_diamond: First the hardware:

We're using a display like the one in the photo ![photo1](https://github.com/RaiderRobotics/FRC-LCD-Display/blob/master/4x20-LCD-panel.jpg). 

Note that it comes with a backplate thing mounted on it. This backplate ![photo2](https://github.com/RaiderRobotics/FRC-LCD-Display/blob/master/YwRobotLCD-CU-450.jpg) already has all of the connections between the LCD display and the I2C protocol

The connectors on the back of the LCD display do not match the connectors on the RoboRIO. The RoboRIO has the pins in a different order. Also note that the LCD display requires 5V, not the 3.3V on the RoboRIO I2C connector. We're just using 5V from a digital IO pin. ![photo3](https://github.com/RaiderRobotics/FRC-LCD-Display/blob/master/I2C_connector.jpg)  You could also use the 5V from the Voltage Regulator Module.
