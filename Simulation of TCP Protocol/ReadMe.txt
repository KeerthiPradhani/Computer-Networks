KEERTHI NAGAPPA PRADHANI - kp5955


Command Line Arguments:

First argument: Destination IP Address
Second argument: Port
Third Argument: Rover ID
Fourth Argument: Multicast IP


Compiling the program:
javac qServerUdp.java
javac qClientUdp.java

Executing the file 
Run the qServerUdp file on the sender rover
Run the qClientUdp file on the receiver rover and the intermediate rovers

Execution:
Run the qServerUdp file FIRST on the sender rover using:
	java qServerUdp
	The sender opens up a random port for the connection and displays it

Run the qClientUdp file NEXT on the receiver rover and connecting rovers using:
	java qClientUdp Destination IP Address Port Rover ID Multicast IP


	Example: javac qServerUdp.java 
		 java qServerUdp
		 Server listening on port: 62880

		java qClientUdp.java
		java qClientUdp 169.254.140.254 62880 1 224.0.0.0/24

NOTE: Please make sure you run qServerUdp file first and also, please enter the same port number while running the qClientUdp file. The 5MB file to be sent is attached in the folder. The name of the file name need not be entered as command line argument. It is pre defined in the program.
