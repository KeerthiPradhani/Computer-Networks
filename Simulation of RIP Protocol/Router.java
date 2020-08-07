import java.net.*;
import java.lang.*;
import java.util.*;

public class RouterA implements Runnable {

    public static int PORT;
    public static int selfRover;
    static byte[] buf = new byte[256];
    private static int routerid;
    public static ArrayList ReceivedRoutingInfo = new ArrayList();
    public static ArrayList RoutingTableData = new ArrayList();
    public static String NextHopRouterId;
    public static String destaddr;
    public static String INET_ADDR;
    public static String subnetMaskID;
    public static int hopcount;
    public static RouterA obj = new RouterA();
    public static String SelfRoverID;
    public static String[] SelfRoverParts;
    public static Map<String, List<String>> hashmap = new HashMap<String, List<String>>();
    public static List<String> hashvalues = new ArrayList<String>();
    public static int length = 0;

    // Method to retrieve Destination Address, Next Hop and the Metric
    public static ArrayList RoutingTable(int id, String destRouterId, String subnetMask, String nextHopRouterId, int metric ,DatagramPacket msgPacket) throws UnknownHostException {

        routerid = id;
        destaddr = destRouterId;
        subnetMaskID = subnetMask;
        NextHopRouterId = nextHopRouterId;
        hopcount = metric;

        if (hopcount == 0 && !(RoutingTableData.contains(destaddr)))
        {
            RoutingTableData.add(routerid); // destination router id
            RoutingTableData.add(destaddr);
            RoutingTableData.add(String.valueOf(msgPacket.getAddress()).substring(1));
            RoutingTableData.add(hopcount + 1);

        }




        // If the metric value is greater, compute the shortest path and update the Routing Table
        if(RoutingTableData.contains(destRouterId)) {
            if (Integer.parseInt(RoutingTableData.get(RoutingTableData.indexOf(destRouterId)+2).toString()) > metric+1) {
                RoutingTableData.set(RoutingTableData.indexOf(destRouterId)+2, metric+1);
                RoutingTableData.set(RoutingTableData.indexOf(destRouterId)+1,msgPacket.getAddress().toString().substring(1));
            }
        }
        else{
            // If the metric value is not greater, no changes are made to the Routing Table
            RoutingTableData.add(routerid);
            RoutingTableData.add(destaddr);
            RoutingTableData.add(msgPacket.getAddress().toString().substring(1));
            RoutingTableData.add(hopcount);
        }
        // Add the received table from other Rovers to an Array List
        ReceivedRoutingInfo.add(routerid);
        ReceivedRoutingInfo.add(destaddr);
        ReceivedRoutingInfo.add(msgPacket.getAddress().toString().substring(1));
        ReceivedRoutingInfo.add(hopcount);

        // Poison reverse implementation
        int index2 = ReceivedRoutingInfo.indexOf(InetAddress.getLocalHost().getHostAddress());
        if(ReceivedRoutingInfo.contains(InetAddress.getLocalHost().getHostAddress()) && ReceivedRoutingInfo.get(index2+1).toString() == "1"){
            if(RoutingTableData.contains(InetAddress.getLocalHost().getHostAddress())){
                int Routingindex = RoutingTableData.indexOf(InetAddress.getLocalHost().getHostAddress());
                RoutingTableData.remove(Routingindex);
                RoutingTableData.remove(Routingindex);
                RoutingTableData.remove(Routingindex-1);
                RoutingTableData.remove(Routingindex);

            }
        }

        return ReceivedRoutingInfo;
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

    public static void main(String[] args) throws UnknownHostException {

        PORT = Integer.parseInt(args[0]);
        selfRover = Integer.parseInt(args[1]);
        INET_ADDR = args[2].substring(0,9);
        SelfRoverParts = INET_ADDR.split("\\.");
        SelfRoverID  = "10"+"."+args[1]+"."+SelfRoverParts[2]+"."+SelfRoverParts[3];
        InetAddress addr = InetAddress.getByName(INET_ADDR);


        try (DatagramSocket serverSocket = new DatagramSocket()) {

            // Start the new thread
            (new Thread(new RouterA())).start();

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


            for (int i = 1; i < RoutingTableData.size()/4 ; i++) {

                int count = 5;
                if(count+1 <RoutingTableData.size()) {
                    message.set(3,(byte) ((RoutingTableData.size()/4) - 1));

                    message.add((byte) 0);
                    message.add((byte) 2);
                    message.add((byte) 0);
                    message.add((byte) 0);


                    String MulticastParts = RoutingTableData.get(count).toString();
                    String[] parts = MulticastParts.split("\\.");
                    message.add((byte) Integer.parseInt(parts[0]));
                    message.add((byte) Integer.parseInt(parts[1]));
                    message.add((byte) Integer.parseInt(parts[2]));
                    message.add((byte) Integer.parseInt(parts[3]));

                    message.add((byte) 255);
                    message.add((byte) 0);
                    message.add((byte) 0);
                    message.add((byte) 0);

                    String NextHopParts = RoutingTableData.get(count+1).toString();
                    String[] hopparts = NextHopParts.split("\\.");
                    message.add((byte) Integer.parseInt(hopparts[0]));
                    message.add((byte) Integer.parseInt(hopparts[1]));
                    message.add((byte) Integer.parseInt(hopparts[2]));
                    message.add((byte) Integer.parseInt(hopparts[3]));

                    int MetricParts = Integer.parseInt(RoutingTableData.get(count + 2).toString());
                    message.add((byte) 0);
                    message.add((byte) 0);
                    message.add((byte) 0);
                    message.add((byte) MetricParts);
                }
            }

            byte[] messagearray;
            messagearray = new byte[message.size()];

            // add the message to byte array
            for (int i =0 ; i< message.size();i++) {
                messagearray[i] = message.get(i);
            }

            while(true) {
                ReceivedRoutingInfo.clear();
                // wait for 10 seconds to determine if the Rover is offline
                Thread.sleep(10000);

                // if the message is not received in 10 seconds, then make the cost to infinity indicating the Rover is unreachable
                int m = 1;
                while(m>0 && m<11){
                    String str1 = "10.";
                    String str2 = Integer.toString(m);
                    String str3 = ".0.0";

                    if(!(ReceivedRoutingInfo.contains(str1+str2+str3))){
                        if(RoutingTableData.contains(str1+str2+str3)){
                            int index = RoutingTableData.indexOf(str1+str2+str3);
                            if(Integer.parseInt(RoutingTableData.get(index+2).toString())== 1){
                                RoutingTableData.set(index+2, 999);

                            }}}
                    m++;
                }

                // send the new updated table to the neighbours
                DatagramPacket msgPacket = new DatagramPacket(messagearray, messagearray.length, addr, PORT);
                serverSocket.send(msgPacket);
                Thread.sleep(5000);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void run(){

        // Rover can receive the message on the Port 520
        try (MulticastSocket clientSocket = new MulticastSocket(PORT)){
            InetAddress address = InetAddress.getByName(INET_ADDR);
            clientSocket.joinGroup(address);

            while (true) {
                synchronized (this) {
                    // Receive the updates from the neighbours
                    DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
                    clientSocket.receive(msgPacket);

                    System.out.println("RECEIVED "+ReceivedRoutingInfo);

                    this.wait(500);

                    System.out.println("Routing table for this rover");
                    int noofpackets   = (int) BinaryToDecimal(1,3);

                    for (int kk = 0 ; kk<= noofpackets; kk++)
                    {

                        obj.RoutingTable(getRouterID(kk), getDestinationID(kk), getSubnetMask(kk), getNextHop(kk), getMetric(kk),msgPacket);

                        // Metric cost to itself is always zero
                        if(RoutingTableData.contains(SelfRoverID)) {
                            int selfDestinationindex = RoutingTableData.indexOf(SelfRoverID);
                            RoutingTableData.set(selfDestinationindex + 2, 0);
                            RoutingTableData.set(selfDestinationindex + 1, String.valueOf(InetAddress.getLocalHost().getHostAddress()));
                        }

                        //After making the cost to infinity, compute the alternate path
                        while(RoutingTableData.contains("999")){
                            int DisconnectedNeighbourIndex = RoutingTableData.indexOf("999");
                            String DisconnectedNeighbour = RoutingTableData.get(DisconnectedNeighbourIndex-2).toString();
                            if(ReceivedRoutingInfo.contains(DisconnectedNeighbour)){
                                String newMetric = ReceivedRoutingInfo.get(ReceivedRoutingInfo.indexOf(DisconnectedNeighbour)+2).toString();
                                if(newMetric != "999"){
                                    RoutingTableData.set(DisconnectedNeighbourIndex, newMetric+1);
                                    RoutingTableData.set(DisconnectedNeighbourIndex-1,msgPacket.getAddress().toString().substring(1));
                                }
                            }
                        }

                        // Display the table
                        System.out.print("Rover ID            ");
                        System.out.print("Destination       ");
                        System.out.print("Next Hop ID        ");
                        System.out.print("Metric cost       ");
                        System.out.println();
                        int k = 0;
                        while(k<RoutingTableData.size()){
                            System.out.print(selfRover+"                   ");
                            System.out.print(RoutingTableData.get(k+1)+"       ");
                            System.out.print(RoutingTableData.get(k+2)+"          ");
                            System.out.print(RoutingTableData.get(k+3)+"                ");
                            System.out.println();
                            k = k+4;
                        }
                    }


                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
