/*
 * Robin Hendrickx and Benjamin Irwin
 */
package udp;

import java.io.IOException;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import common.MessageInfo;

public class UDPClient {

    private DatagramSocket sendSoc;
    
    public static void main(String[] args) {

	InetAddress	serverAddr = null;
	int			recvPort;
	int 		countTo;
	String 		message;

	// Get the parameters
	if (args.length < 3) {
	    System.err.println("Arguments required: server name/IP, recv port, message count");
	    System.exit(-1);
	}

	try {
	    serverAddr = InetAddress.getByName(args[0]);
	}
	catch (UnknownHostException e) {
	    System.out.println("Bad server address in UDPClient, " + args[0] +
			       " caused an unknown host exception " + e);
	    System.exit(-1);
	}

	recvPort = Integer.parseInt(args[1]);
	countTo = Integer.parseInt(args[2]);

	// Create UPD client
	UDPClient udpClient = new UDPClient();

	// Sendmessages
	udpClient.sendLoop(serverAddr,recvPort,countTo);

    }

    public UDPClient() {

	try {
	    sendSoc = new DatagramSocket();
	}
	catch(SocketException e){
	    System.out.println("Socket exception");
	}
    }

    private void sendLoop(InetAddress serverAddr, int recvPort, int countTo) {

	int tries = 0;

	
	try{
	    // Set and log size of send buffer
	    // sendSoc.setSendBufferSize(106496);		    
	    System.out.println("Size of send buffer: " + sendSoc.getSendBufferSize());

	    // Loop to send messages
	    while(tries<countTo) {
		
		send( countTo + ";" + tries, serverAddr, recvPort);
		tries++;
	    }

	    System.out.println("All messages sent.");

	}
	
	catch (SocketException e){
	    System.out.println("Could not fetch send buffer size.");
	}
	
	       
	return;
    }

    private void send(String payload, InetAddress destAddr, int destPort) {

	int payloadSize;
	byte[] pktData;
	DatagramPacket pkt;

	// Sleep to ensure there are no receive buffer exhaustions
	/*
	  try{
	  Thread.sleep((long)1);
	  }
	  catch(InterruptedException e){
	  System.out.println("Sleep interrupted.");
	  }
	*/
	
	try {

	    // Serialize object 
	    MessageInfo messageInfo = new MessageInfo(payload);
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    ObjectOutputStream os = new ObjectOutputStream(outputStream);
	    os.writeObject(messageInfo);
	    pktData = outputStream.toByteArray();
	    payloadSize = pktData.length;

	    // Build datagram packet and send
	    pkt = new DatagramPacket(pktData, payloadSize, destAddr, destPort);
	    sendSoc.send(pkt);
	}

	catch(IOException e){
	    System.out.println("IO exception in send UDP client");
	}
	catch(Exception e){
	    System.out.println("Exception in send UDP client");
	}

	return;
        
    }
}
