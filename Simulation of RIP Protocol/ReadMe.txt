KEERTHI NAGAPPA PRADHANI - kp5955


Command Line Arguments:

First argument: PORT
Second argument: Rover ID
Third Argument: Subnet Mask

There are 10 Rover files. RouterA, RouterB..... RouterJ. 
Compiling the program: 
javac RouterA.java

Executing the file 
java RouterA 520 1 224.0.0.0/24


Execution:
The metric cost to itself is always 0.
The metric cost to its neighbours is always 1.
The updates are sent every 5 seconds.
After every 10 seconds it checks if the Rover receives the update from a particular rover. If it does not receive,
 the rover is considered to be offline, the link is broken and the metric cost is changed to "999" 