import java.net.*;
import java.io.*;
import java.security.*;
import java.nio.charset.StandardCharsets;
import java.lang.*;
import java.util.ArrayList;
import java.util.List;

public class qClientUdp 
{ 
	// Declaring and initialising the variables
	DatagramSocket cl_connection = null;
	String host = null;
	int port = 0;
	public static int selfRover;
	public static String INET_ADDR;
	public static String SelfRoverID;
	public static String[] SelfRoverParts;
	static byte[] buf = new byte[256];
	

	public qClientUdp(String hostName, int port_num) throws SocketException {

		// create the Datagram packet
		cl_connection = new DatagramSocket();
		host = hostName;
		port = port_num;
	}

	// Extract the Rover ID
	public static int getRouterID(int kk) {
		String roverid = "";
		roverid = roverid + String.valueOf(BinaryToDecimal(1, 4 + (kk*20)));
		return Integer.valueOf(roverid);
	}

	//Extract the Destination Multicast address
	public static String getDestinationID(int kk) {
		String srcAdress = "";

		for (int i =8 + (kk*20);i<=11 + (kk*20);i++) {
			srcAdress = srcAdress + String.valueOf(BinaryToDecimal(1, i));
			if (i!=11)
				srcAdress = srcAdress + ".";
		}
		return srcAdress;
	}

	// Extract the subnet Mask
	public static String getSubnetMask(int kk) {
		String subnetMask = "";
		for (int i =12 + (kk*20);i<=15 + (kk*20);i++) {
			subnetMask = subnetMask + String.valueOf(BinaryToDecimal(1, i));
			if (i!=15)
				subnetMask = subnetMask + ".";
		}
		return subnetMask;
	}

	// Extract the Next Hop Rover ID
	public static String getNextHop(int kk) {
		String nextHop = "1";
		return nextHop;
	}

	// Extract the cost to reach the Destination
	public static int getMetric(int kk) {
		String Metric = "";
		Metric = Metric + String.valueOf(BinaryToDecimal(1, 23 + (kk*20)));
		return Integer.valueOf(Metric);
	}

	// Method to convert Binary value to Decimal
	public static long BinaryToDecimal(int noOfBytes, int startByte) {
		int byteIndex = startByte;
		long finalnumber = 0;
		int highestpower = (noOfBytes * (int) Math.pow(2, 3)) - 1;

		for (int jj = noOfBytes; jj >= 1; jj--) {
			for (int i = highestpower,j=7; i >= highestpower-7; i--,j--) {
				if ((buf[byteIndex] &  (1<<j)) != 0) {
					finalnumber = finalnumber + (int) Math.pow(2, i);
				}
			}
			byteIndex = byteIndex +1;
			highestpower = highestpower - 8;
		}
		return finalnumber;
	}
	

	public void runClient() throws IOException {
		try{
		// Datagram packet that has to be sent to the server
		DatagramPacket client_data = new DatagramPacket(new byte[1024],1024,InetAddress.getByName(host), port);

		// Sending the packet
		cl_connection.send(client_data);

		while(true){
		// Buffer to store the input from server
		byte[] quote_from_server = new byte[1024];
		// Receiving data from server
			cl_connection.receive(new DatagramPacket(quote_from_server, 1024));
			String received_data = new String(quote_from_server);
			System.out.println("Data received");
			System.out.println("Data received is: "+received_data);

			// A string to save data received from server
			String quotefromserverstring = new String(quote_from_server);

			// Creating checksum for the chunk of data sent
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashInBytes = md.digest(quotefromserverstring.getBytes(StandardCharsets.UTF_8));

			StringBuilder result = new StringBuilder();
			for (byte b : hashInBytes) {
				result.append(String.format("%02x", b));
			}

			// reading the checksum received from the sender
			byte[] checksumreceived = new byte[1024];
			DatagramPacket receivedata = new DatagramPacket(checksumreceived, 1024);
			cl_connection.receive(receivedata);
			String checksumreceivedstr = new String(receivedata.getData(),receivedata.getOffset(),receivedata.getLength(),"UTF-8");
			System.out.println("Received checksum is: " +checksumreceivedstr);
			String resultstr = new String(result);
			System.out.println("Calculated checksum is: "+resultstr);
			String ack = new String("Sending ACK");

			// comparing the calculated checksum and the received checksum
			if(checksumreceivedstr.equals(resultstr)){
				System.out.println("Checksum matched! Data received correct. Sending Acknowledgement");
				cl_connection.send(new DatagramPacket(ack.getBytes(), ack.length(), InetAddress.getByName(host), port));
			}
			else{
				// if the checksum doesn't match, packet is resent
				System.out.println("Checksum not matched! Data received incorrect. Dropping packet");
			}
		}
	}catch (NoSuchAlgorithmException e){
	System.err.println("No such Algorithm found");
		}
	}

	public static void main(String[] args) throws IOException {
		// Declaring and Initialising the variables
		int port =0;
		String host_name ="";

		// Reading the values from the command lines
		host_name = args[0];
		port = Integer.parseInt(args[1]);
		selfRover = Integer.parseInt(args[2]);
		INET_ADDR = args[3].substring(0,9);
		SelfRoverParts = INET_ADDR.split("\\.");
		SelfRoverID  = "10"+"."+args[1]+"."+SelfRoverParts[3]+"."+SelfRoverParts[3];
		InetAddress addr = InetAddress.getByName(INET_ADDR);

		// creating the multicast IP for the rovers to communicate
		MulticastSocket clientSocket = new MulticastSocket(520);
			InetAddress address = InetAddress.getByName(INET_ADDR);
			clientSocket.joinGroup(address);

		// if the destination address is same as the local host address, print the file
		if(host_name.contains(InetAddress.getLocalHost().getHostAddress())){
			BufferedReader br = new BufferedReader(new FileReader("quotes.txt"));
				String line = null;
				while ((line = br.readLine()) != null) {
					//System.out.println(line);
				}
		}

		// create the list to compute the message
		List<Byte> message = new ArrayList<Byte>();

		message.add((byte)2);
		message.add((byte)2);
		message.add((byte)0);
		message.add((byte)0);

		message.add((byte)1);
		message.add((byte)0);
		message.add((byte)0);
		message.add((byte)0);

		message.add((byte)10);
		message.add((byte)selfRover);
		message.add((byte)Integer.parseInt(SelfRoverParts[2]));
		message.add((byte)Integer.parseInt(SelfRoverParts[3]));

		message.add((byte)255);
		message.add((byte)0);
		message.add((byte)0);
		message.add((byte)0);

		message.add((byte)Integer.parseInt(SelfRoverParts[0]));
		message.add((byte)selfRover);
		message.add((byte)Integer.parseInt(SelfRoverParts[2]));
		message.add((byte)Integer.parseInt(SelfRoverParts[3]));

		message.add((byte)0);
		message.add((byte)0);
		message.add((byte)0);
		message.add((byte)0);

		// Running the client
	      new qClientUdp(host_name,port).runClient();     
	}
} 
