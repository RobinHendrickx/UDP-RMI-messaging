/*
 * Robin Hendrickx and Benjamin Irwin
 */
package udp;

import java.io.IOException;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.io.ByteArrayInputStream;
import common.MessageInfo;

public class UDPServer {

    private DatagramSocket recvSoc;
    private int totalMessages = -1;
    private int[] receivedMessages;
    private boolean close;
    private long start_time;
    private long end_time;
    
    private void run() {
	    
	int pacSize;
	byte[] pacData;
	DatagramPacket pac;
		
	System.out.println("Thread created");

	// Increase and log size of receive buffer
	try{
	    //recvSoc.setReceiveBufferSize(1064960);
	    System.out.println("Size of receive buffer: " + recvSoc.getReceiveBufferSize());
	}
	
	catch (SocketException e){
	    System.out.println("Could not fetch receive buffer size.");
	}

	// Run loop to receive messages
	while(true){
	    try{
		// Set timeout
		recvSoc.setSoTimeout(10*1000);

		pacData = new byte[100];
		pacSize = pacData.length;

		DatagramPacket request = new DatagramPacket(pacData, pacSize);

		recvSoc.receive(request);

		//Process input
		ByteArrayInputStream in = new ByteArrayInputStream(pacData);
		ObjectInputStream is = new ObjectInputStream(in);
		MessageInfo msg = (MessageInfo) is.readObject();
		processMessage(msg);
	
	    }

	    catch(SocketTimeoutException e) {
		System.out.println("Timed out. Quit process.");
		end_time = System.currentTimeMillis();
		printResults();
		
		break;
	    }
			
	    catch(ClassNotFoundException e){
		System.out.println("Class not found.");
	    }
	    catch(IOException e){
		System.out.println("IO exception.");
	    }
	    catch(Exception e){
		System.out.println("General exception in UDPSERVER run().");
	    }

	}

	if(totalMessages == -1) {
	    System.out.println("No Messages received.");
	}

	return;
      
    }

    private void processMessage(MessageInfo msg) {

	// If first message, log time and initialise receive array
	if(msg.messageNum == 0) {

	    totalMessages = msg.totalMessages;
	    receivedMessages = new int[totalMessages];
	    start_time =  System.currentTimeMillis();

	}

	// Log receipt
	receivedMessages[msg.messageNum] = 1;

	// Log time and print results
	if(msg.messageNum==msg.totalMessages-1){

	    end_time = System.currentTimeMillis();
	    printResults();
	    System.exit(0);
	}
    }
 
    public UDPServer(int rp) {

	// Initialise UDP socket
	try{
	    recvSoc = new DatagramSocket(rp);
	}
	catch (SocketException e){
	    System.out.println("Socket: " + e.getMessage());
	}
	
	System.out.println("UDPServer ready.");
    }


    private void printResults(){

	
	int count = 0;

	// Count missing messages
	for(int i = 0; i<totalMessages;i++) {
	    if (receivedMessages[i] == 0) {

		count++;
		System.out.println("Missing message nr: " +(i+1));
	    }
	}

	// If all messages received
	if (count == 0){
	    System.out.println("All messages received. nr: " + totalMessages);
	    System.out.println("Elapsed time in ms to receive all: " + (end_time-start_time));
	    return;
	}

	// If not all messages received
	System.out.println("Nr of missing messages: " + count);
	System.out.println("Elapsed time in ms: " + (end_time-start_time));
	return;
    }

    
    public static void main(String args[]) {
	int	recvPort;

	// Get the parameters from command line
	if (args.length < 1) {
	    System.err.println("Arguments required: recv port");
	    System.exit(-1);
	}
	recvPort = Integer.parseInt(args[0]);

	UDPServer udpServer = new UDPServer(recvPort);
	udpServer.run();

    }
}
