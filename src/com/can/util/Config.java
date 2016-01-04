package com.can.util;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Config {

	public static int SERVER_PORT=4550;
	public static String SERVER_ID="CAN_SERVER";
	public static String POST_IP_URL=":"+Config.SERVER_PORT+"/"+Config.SERVER_ID;
	
	public Config(){
		
	}
	
	public static String getIPV4Address() {
		 
		Enumeration<NetworkInterface> intf;
		
		try {
				intf = NetworkInterface.getNetworkInterfaces();
				
				while(intf.hasMoreElements()) {

					for ( InterfaceAddress addr : intf.nextElement().getInterfaceAddresses()) {
						
						if(addr instanceof InterfaceAddress) {
								
							if ( addr.getAddress().isSiteLocalAddress())
							{
								//System.out.println("Address is "+addr.getAddress());
								return addr.getAddress().toString().split("/")[1];
							}
						}
					} 
	         }  
		} catch (SocketException ex) {
			
			ex.printStackTrace();
		}
       return null;
	}
	
	
	
	public void displayNoValueMessage(){
		
		System.out.println("Please enter some value ");
	}
	
	public void displayErrorMessage(){
		
		System.out.println("Error! You have entered wrong value ");
	}
}
