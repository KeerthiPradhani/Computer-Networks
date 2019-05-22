import java.net.*;
import java.nio.charset.*;
import java.util.*;
import java.io.*;
import java.security.*;


public class qServerUdp extends Thread
{ 
	DataOutputStream out = null;
	DatagramSocket connection = null;
	

	public void runServer() throws IOException  {

		connection = new DatagramSocket(0);

		// set a timeout
		connection.setSoTimeout(50000);
		byte[] range = null;
		String checksumsentstr = "";
		// Displaying the port that the server is listening
		System.out.println("Server listening on port: "+ connection.getLocalPort());
		
		// buffer to read the input
		byte[] client_packet = new byte[1024];
		// receive the packet
		DatagramPacket receive_packet =  new DatagramPacket(client_packet , 1024);
		while(true) {
			// Waiting for a client to connect to server
			connection.receive(receive_packet);
			System.out.println("Connection established!");

			// reading the quote from the quotes.txt file
			BufferedReader br = null;
			String strLine = "";
			try {
				//reading the contents of the file
				br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/quotes.txt"));
				while ((strLine = br.readLine()) != null) {
					byte[] out_data = strLine.getBytes();

					int blockSize = 1024;
					int blockCount = (out_data.length+blockSize - 1) / blockSize;

					// creating chunks of size of 1024 to send them to client
					for (int i = 1; i <= blockCount; i++) {
						int idx = (i - 1) * blockSize;
						range = Arrays.copyOfRange(out_data, idx, idx + blockSize);

						// sending every chunk of data
						connection.send(new DatagramPacket(range, range.length, receive_packet.getAddress(), receive_packet.getPort()));
						String quotefromserverstring = new String(range);
						System.out.println("Data is: "+quotefromserverstring);
						System.out.println("Packet sent!");

						// create checksum of the chunk of data using SHA - 256
						MessageDigest md = MessageDigest.getInstance("SHA-256");
						byte[] hashInBytes = md.digest(quotefromserverstring.getBytes(StandardCharsets.UTF_8));

						StringBuilder result = new StringBuilder();
						for (byte b :hashInBytes ) {
							result.append(String.format("%02x", b));
						}

						// generate the checksum string
						checksumsentstr = result.toString();

						// send the checksum value to the client
						connection.send(new DatagramPacket(checksumsentstr.getBytes(), checksumsentstr.length(), receive_packet.getAddress(), receive_packet.getPort()));
						System.out.println("Checksum is: "+ checksumsentstr);
						System.out.println("Checksum sent!");

						byte[] ackvalue = new byte[1024];
						DatagramPacket receiveddata = new DatagramPacket(ackvalue, 1024);

						// receive the ACK from the client
						connection.receive(receiveddata);
						String ackvaluestr = new String(receiveddata.getData(),receiveddata.getOffset(),receiveddata.getLength(),"UTF-8");
						if("Sending ACK".equals(ackvaluestr)) {
							System.out.println("ACK received");
						}
						else{
							// resend the packet and the checksum if the ACK is not received
							connection.send(new DatagramPacket(range, range.length, receive_packet.getAddress(), receive_packet.getPort()));
							System.out.println("Packet resent");
							connection.send(new DatagramPacket(checksumsentstr.getBytes(), checksumsentstr.length(), receive_packet.getAddress(), receive_packet.getPort()));
							System.out.println("Checksum resent");
						}
						Thread.sleep(5000);
						}
				}

			} catch (FileNotFoundException e) {
				System.err.println("Unable to find the file");
			} catch (NoSuchAlgorithmException e){
				System.err.println("No such Algorithm found");
			} catch (InterruptedException e){
				System.err.println("Interrupted exception occurred");
			} catch(SocketTimeoutException  e){
				// if there is a timeout, assume that the packet is list and resend the packet
				System.err.println("Time out,ACK not received, resending the packet");
				connection.send(new DatagramPacket(range, range.length, receive_packet.getAddress(), receive_packet.getPort()));
				System.out.println("Packet resent");
				connection.send(new DatagramPacket(checksumsentstr.getBytes(), checksumsentstr.length(), receive_packet.getAddress(), receive_packet.getPort()));
				System.out.println("Checksum resent");
			}
		}
	}

	public static void main(String args[]) throws UnknownHostException, IOException 
	{ 
		// Starting and running the server
		new qServerUdp().runServer();
	} 
} 
