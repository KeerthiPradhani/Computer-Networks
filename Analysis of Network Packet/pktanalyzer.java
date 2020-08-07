import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
public class pktanalyzer {

	public static void main(String[] args) throws IOException {
		
		/*If Packet type is not given, the program pops a warning to the user*/
		if(args.length==0) {
			System.out.println("Please enter the filename of the packet you would like to analyze\n");
			System.exit(0);
			}
		
		try {	
			
			FileInputStream fr1 = new FileInputStream(args[0]); 
			BufferedInputStream reader = new BufferedInputStream(fr1);
			  
		    int i=0;
		    int length = 0;
			String filename = args[0];
			String PacketName = "";
		    reader.mark(0);
		   
		
		    /*Calculates the length of the packet*/
		    while ((i = reader.read()) != -1) {
		    	String hex = Integer.toHexString(i);
		    	length++; 
		    	
				/*checks if the packet is of the following types*/
		    	
		    	 if(length == 24 && (i = reader.read()) !=-1)  {
					int value = Integer.parseInt(hex, 16 );
					if(value == 17)
						PacketName = "UDP";
					else if(value == 6) 
					PacketName = "TCP";
					else if(value == 1)
					PacketName = "ICMP";
				}  
		    }
		    length++;
		    /*Ethernet Header starts here*/
		    System.out.println("\n" +"---Ethernet Header---"+"\n"+"Packet Size: "+length+" bytes");
		    reader.reset();
		    i = 0;
		    length = 0;
		    
		    /*Destination address is computed*/
		    System.out.print("Destination = ");
		    while (length != 6 && (i = reader.read()) !=-1)  {
		    	String hex = Integer.toHexString(i);
		    	if(hex.length()==1)
		    		System.out.append("0");
		    	if(length == 5)
		    		System.out.print(hex+",");
		    	else
		    		System.out.print(hex+":");
		    		length++;
		    }  
		    
		    /*Source address is computed*/
		    System.out.print("\nSource = ");
		    while (length != 12 && (i = reader.read()) !=-1)  {
		    	String hex = Integer.toHexString(i);
		    	if(hex.length()==1)
		    		System.out.append("0");
		    	if(length == 11)
		    		System.out.print(hex+",");
		    	else
		    		System.out.print(hex+":");
		    		length++;	
		    } 
		    
		    /*Ethertype is computed*/
		    System.out.print("\nEthertype = ");
		    while (length != 14 && (i = reader.read()) !=-1)  {
		    	String hex = Integer.toString(i);
		    	if(hex.length()==1)
		    		System.out.append("0");
		    		System.out.print(hex);
		    		length++;	
		    }  
		    System.out.print("(IP)\n");
		    
		    /*IP Header starts here*/
		    System.out.print("\n" +"---IP Header---");
		    
		    /*Version and Header Length is computed*/
		    System.out.print("\nVersion = ");
		    if (length != 15 && (i = reader.read()) !=-1)  {
		    	int k =0;
		        	String hex = Integer.toHexString(i);
		        	int decvalue1 = Integer.parseInt(hex, 16 );
		        	int decvalue2 = Integer.parseInt(hex, 16 );
		        	while(k!=4) {
		    		      decvalue1 =  decvalue1 >> 1;              
		    		      k++;
		    		      }
		        	System.out.print(decvalue1);
		        	int last4bit = (((1 << 4) - 1) & (decvalue2 >> (1 - 1)));
		        	System.out.print("\nHeader Length = "+decvalue1*last4bit+" bytes"); 
		    		length++;
		    } 
		    
		    /*Type of Service is computed*/
		    System.out.print("\nType of Service = 0x");
		    if (length != 16 && (i = reader.read()) !=-1)  {
		    	String hex = Integer.toHexString(i);
		    	if(hex.length()==1)
		    		System.out.append("0");
		    		System.out.print(hex);
		    		length++;
		    
		    System.out.print("\n\txxx. .... = "+ hex.charAt(0)+ "(precedence)\n\t ..."+hex.charAt(0)+" .... = normal delay \n\t .... "+hex.charAt(0)+"... = normal throughput\n\t .... ."+hex.charAt(0)+".. = normal reliability");
}
		    /*Total Length is computed*/
		    System.out.print("\nTotal Length = ");
		    if(length != 17 && (i = reader.read()) !=-1)  {
		    	String hex1 = Integer.toHexString(i);
		    	length++;
		    	if(length != 18 && (i = reader.read()) !=-1)  {
			    	String hex2 = Integer.toHexString(i);
			    	String hex = hex1+hex2;
		    	int value = Integer.parseInt(hex, 16 );
		    		System.out.print(value);
		    		length++;	
		    	}}
		    System.out.print(" bytes");
		    
		    /*Identification is computed*/
		    System.out.print("\nIdentification = ");
		    if(length != 19 && (i = reader.read()) !=-1)  {
		    	String hex1 = Integer.toHexString(i);
		    	String hex3 = Integer.toHexString(i);
		    	length++;	
		    if(length != 20 && (i = reader.read()) !=-1)  {
				    	String hex2 = Integer.toHexString(i);
				    	String hex4 = Integer.toHexString(i);
				    	String hex = hex1+hex2;
		    	int value = Integer.parseInt(hex, 16 );
		    		System.out.print("0x"+hex3+hex4+"("+value+")");
		    		length++;		
		    }
		  }
		    
		    /*Flag is computed*/
		    System.out.print("\nFlags = 0x");
		    if (length != 21 && (i = reader.read()) !=-1)  {
		    	int k =0;
	        	String hex = Integer.toHexString(i);
	        	int decvalue1 = Integer.parseInt(hex, 16 );
	        	while(k!=4) {
	    		      decvalue1 =  decvalue1 >> 1;              
	    		      k++;
	    		      }
	        	System.out.print(decvalue1);
	    		length++;
		    }
		    
		    /*Fragment Offset is computed*/
		    System.out.print("\nfragment offset = ");
		    if (length != 22 && (i = reader.read()) !=-1)  {
		    	int k =0;
	        	String hex = Integer.toHexString(i);
	        	int decvalue1 = Integer.parseInt(hex, 16 );
	        	while(k!=4) {
	    		      decvalue1 =  decvalue1 >> 1;              
	    		      k++;
	    		      }
	        	System.out.print(decvalue1+" bytes");
	    		length++;
		    }

		    /*Time to Live is computed*/
		    System.out.print("\nTime to live = ");
		    if(length != 23 && (i = reader.read()) !=-1)  {
		    	String hex = Integer.toHexString(i);
		    	int value = Integer.parseInt(hex, 16 );
		    		System.out.print(value);
		    		length++;	
		    }
		    System.out.print(" seconds/hops");
		    
		    /*Protocol is computed*/
		    System.out.print("\nProtocol = ");
		    if(length != 24 && (i = reader.read()) !=-1)  {
		    	String hex = Integer.toHexString(i);
		    	int value = Integer.parseInt(hex, 16 );
		    		System.out.print(value);
		    		length++;
		    	 	if(value == 17)
		    	 		 System.out.print(" (UDP)");
					else if(value == 6) 
						 System.out.print(" (TCP)");
					else if(value == 1)
						 System.out.print(" (ICMP)"); 
		    }
		
		    /*Checksum is computed*/
		    System.out.print("\nHeader checksum = 0x");
		    while(length != 26 && (i = reader.read()) !=-1)  {
		    	String hex = Integer.toHexString(i);
		    	System.out.print(hex);
		        		length++;	
		    }
		
		    /*Source Address is computed*/
		    System.out.print("\nSource Address = ");
		    while(length != 30 && (i = reader.read()) !=-1)  {
		    	String hex = Integer.toHexString(i);
		    	int value = Integer.parseInt(hex, 16 );
		    	if(length == 29)
		    		System.out.print(value);
		    	else
		    		System.out.print(value+":");
		    		length++;
		    }  
		    	
		    /*Destination Address is computed*/
		    System.out.print("\nDestination Address = ");
		    while(length != 34 && (i = reader.read()) !=-1)  {
		    	String hex = Integer.toHexString(i);
		    	int value = Integer.parseInt(hex, 16 );
		    	
		    	if(length == 33)
		    		System.out.print(value);
		    	else
		    		System.out.print(value+":");
		    		length++;
		    }  
		    System.out.print("\nNo options\n");
		    
		    
		switch(PacketName) {
		case "UDP":
				/*FOR UDP PACKET*/
 
			    /*UDP Header starts here*/
				System.out.print("\n" +"---UDP Header---");
				
				   /*Source Port is computed*/
				System.out.print("\nSource Port = ");
				if (length != 35 && (i = reader.read()) !=-1)  {
					String hex1 = Integer.toHexString(i);
					length++;
					if (length != 36 && (i = reader.read()) !=-1)  {
						String hex2 = Integer.toHexString(i);
						String hex = hex1+hex2;
					int value = Integer.parseInt(hex, 16 );
						System.out.print(value);
						length++;
				} }
			    
				   /*Destination Port is computed*/
				System.out.print("\nDestination Port = ");
				if (length != 37 && (i = reader.read()) !=-1)  {
					String hex1 = Integer.toHexString(i);
					length++;
					if (length != 38 && (i = reader.read()) !=-1)  {
						String hex2 = Integer.toHexString(i);
						String hex = hex1+hex2;
					int value = Integer.parseInt(hex, 16 );
						System.out.print(value);
						length++;
				} }
				
				   /*Length is computed*/
				 System.out.print("\nLength =  ");
				    if(length != 39 && (i = reader.read()) !=-1)  {
				    	String hex1 = Integer.toHexString(i);
				    	length++;
				    	if(length != 40 && (i = reader.read()) !=-1)  {
					    	String hex2 = Integer.toHexString(i);
					    	length++;
					    	String hex = hex1+hex2;
				    	int value = Integer.parseInt(hex, 16 );
				    		System.out.print(value);
				    }}
				    
				    /*Checksum is computed*/
				    System.out.print("\nChecksum = 0x");
				    while(length != 42 && (i = reader.read()) !=-1)  {
				    	String hex = Integer.toHexString(i);
				    		System.out.print(hex);
				    		length++;	
				    }
				    
				    /*Data is displayed*/
				    System.out.print("\n\nData: ");
				    while((i = reader.read()) !=-1)  {
				    	
				    	String hex = Integer.toHexString(i);
				    	if(hex.length()==1)
				    		System.out.append("0");
				    	System.out.print(hex+" ");
				        		length++;	
				    }
				    System.out.print("\n");
				    break;
		case "TCP":
			
			   		/*FOR TCP PACKET*/				    
				    /*TCP Header starts here*/
					System.out.print("\n" +"---TCP Header---");
					
					   /*Source Port is computed*/
					System.out.print("\nSource Port = ");
					if (length != 35 && (i = reader.read()) !=-1)  {
						String hex1 = Integer.toHexString(i);
						length++;
						if (length != 36 && (i = reader.read()) !=-1)  {
							String hex2 = Integer.toHexString(i);
							String hex = hex1+hex2;
						int value = Integer.parseInt(hex, 16 );
							System.out.print(value);
							length++;
					} }
				    
					   /*Destination Port is computed*/
					System.out.print("\nDestination Port = ");
					if (length != 37 && (i = reader.read()) !=-1)  {
						String hex1 = Integer.toHexString(i);
						length++;
						if (length != 38 && (i = reader.read()) !=-1)  {
							String hex2 = Integer.toHexString(i);
							String hex = hex1+hex2;
						int value = Integer.parseInt(hex, 16 );
							System.out.print(value);
							length++;
					} }
					
					   /*Sequence Number is computed*/
						System.out.print("\nSequence Number = ");
						if (length != 39 && (i = reader.read()) !=-1)  {
							String hex1 = Integer.toHexString(i);
							length++;
							if (length != 40 && (i = reader.read()) !=-1)  {
								String hex2 = Integer.toHexString(i);
								length++;
								if (length != 41 && (i = reader.read()) !=-1)  {
									String hex3 = Integer.toHexString(i);
									length++;
									if (length != 42 && (i = reader.read()) !=-1)  {
										String hex4 = Integer.toHexString(i);
										length++;
								String hex = hex1+hex2+hex3+hex4;
							int value = Integer.parseInt(hex, 16 );
								System.out.print(value);
								
						} }}}
						
						   /*Acknowledgement Number is computed*/
						System.out.print("\nAcknowledgement Number = ");
						if (length != 43 && (i = reader.read()) !=-1)  {
							String hex1 = Integer.toHexString(i);
							length++;
							if (length != 44 && (i = reader.read()) !=-1)  {
								String hex2 = Integer.toHexString(i);
								length++;
								if (length != 45 && (i = reader.read()) !=-1)  {
									String hex3 = Integer.toHexString(i);
									length++;
									if (length != 46 && (i = reader.read()) !=-1)  {
										String hex4 = Integer.toHexString(i);
										length++;
										String hex = hex1+hex2+hex3+hex4;
										Long value = Long.parseLong(hex, 16);
										System.out.print(value);
										
						} }}}
						
						   /*Data Offset and flags is computed*/
					    System.out.print("\nHeader Length = ");
					    while(length != 47 && (i = reader.read()) !=-1)  {
					    	int k =0;
				        	String hex = Integer.toHexString(i);
				        	int decvalue1 = Integer.parseInt(hex, 16 );
				        	int decvalue2 = Integer.parseInt(hex, 16 );
				        	while(k!=4) {
				    		      decvalue1 =  decvalue1 >> 1;              
				    		      k++;
				    		      }
				        	System.out.print(decvalue1*4+" bytes ("+decvalue1+")");
				        	System.out.print("\nFlags = 0x"+(((1 << 4) - 1) & (decvalue2 >> (1 - 1)))); 
				    		length++;		
					        }
					    
					    /*Flags is computed*/
					    while(length != 48 && (i = reader.read()) !=-1)  {
					    	String hex = Integer.toHexString(i);
					    	System.out.print(hex);
					        		length++;	
					       }
					    System.out.print("\n ..0. .... = No Urgent pointer\n ...1 .... = Acknowledgement\n .... 1... = Push\n .... .0.. = No reset\n .... ..0. = No Syn\n .... ...0 = No Fin");
					    
					    /*Window is computed*/
					    System.out.print("\nWindow = ");
					    if (length != 49 && (i = reader.read()) !=-1)  {
							String hex1 = Integer.toHexString(i);
							length++;
							if (length != 50 && (i = reader.read()) !=-1)  {
								String hex2 = Integer.toHexString(i);
								length++;
								String hex = hex1+hex2;
								int value = Integer.parseInt(hex,16);
								System.out.print(value);
							}}
					    
					    /*Checksum is computed*/
					    System.out.print("\nChecksum = 0x");
					    while(length != 52 && (i = reader.read()) !=-1)  {
					    	String hex = Integer.toHexString(i);
					    	System.out.print(hex);
					        		length++;	
					    }
					    
					    /*Urgent Pointer is computed*/
					    System.out.print("\nUrgent Pointer = ");
					    while(length != 53 && (i = reader.read()) !=-1)  {
					    	String hex = Integer.toHexString(i);
					    	System.out.print(hex);
					        		length++;	
		               }
					    
					    System.out.print("\nNo options\n");
					    
					    /*Data is Displayed*/
					    System.out.print("\nData: ");
					    while((i = reader.read()) !=-1)  {
					    	
					    	String hex = Integer.toHexString(i);
					    	if(hex.length()==1)
					    		System.out.append("0");
					    	System.out.print(hex+" ");
					        		length++;	
					    	}
					    System.out.print("\n");
					    break;
		case "ICMP":
			   		/*FOR ICMP PACKET*/
				    
				    /*ICMP Header starts here*/
					System.out.print("\n" +"---ICMP Header---");
					
					   /*Type is computed*/
				    System.out.print("\nType = ");
				    if(length != 35 && (i = reader.read()) !=-1)  {
				    	String hex = Integer.toHexString(i);
				    	int value = Integer.parseInt(hex, 16 );
				    		System.out.print(value);
				    		length++;	
				    }
				    
				    /*Code is computed*/
				    System.out.print("\nCode = ");
				    if(length != 36 && (i = reader.read()) !=-1)  {
				    	String hex = Integer.toHexString(i);
				    	int value = Integer.parseInt(hex, 16 );
				    		System.out.print(value);
				    		length++;	
				    }
					 
				    /*Checksum is computed*/
				    System.out.print("\nChecksum = 0x");
				    while(length != 38 && (i = reader.read()) !=-1)  {
				    	String hex = Integer.toHexString(i);
				    	System.out.print(hex);
				        		length++;	
				    }
					   
				    /* Data is displayed*/
				    System.out.print("\n\nData: ");
				    while((i = reader.read()) !=-1)  {
				    	String hex = Integer.toHexString(i);
				    	if(hex.length()==1)
				    		System.out.append("0");
				    	System.out.print(hex+" ");
				        		length++;	
	}
				    System.out.print("\n");
				    break;
				    default:
				    	System.out.println("The Packet you are trying to analyze is neither UDP nor TCP nor ICMP");
			  
		}		
	}
		catch (IOException e) {
	      	  e.printStackTrace();
		    }}
}

