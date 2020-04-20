/*
 * Created on 01-Mar-2016
 */
package rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.*;

import common.*;

//User defined RMI error exit codes:
//1 - Insufficient command line arguments
//2 - Malformed URL
//3 - Bind failure
//4 - Communication failure (remote exception)
//5 - Server creation failure
//6 - Invalid message number
//7 - Empty set of messages received

public class RMIServer extends UnicastRemoteObject
    implements RMIServerI {

  private int totalMessages = -1;
  private int[] receivedMessages;
  private long start_time;
  private long end_time;

  public RMIServer() throws RemoteException {
    super();
  }

  public void receiveMessage(MessageInfo msg) throws RemoteException {

    if (receivedMessages == null) {
      receivedMessages = new int[msg.totalMessages];
      start_time = System.currentTimeMillis();

      totalMessages = msg.totalMessages;
      if (totalMessages <= 0) {
        System.out.println("Error (server): cannot receive less than "
            + "one message.");
        System.exit(6);
      }

    }

    receivedMessages[msg.messageNum] = 1;
    //System.out.println("Received Message " + (msg.messageNum + 1) +
    //    " of " + totalMessages);

  }

  public void receiveSetOfMessages(SetOfMessages set)
      throws RemoteException {

      if(set == null) {
        System.out.println("Error (server): empty set of messages " +
            "received.");
        System.exit(7);
      }

      receivedMessages = new int[set.msgs[0].totalMessages];
      totalMessages = set.msgs[0].totalMessages;

    for(int i = 0; i < set.msgs[0].totalMessages; i++) {
      if(set.msgs[i] != null) {
        receivedMessages[i] = 1;
      }

      System.out.println("Received Message " +
          (i + 1) + " of " + totalMessages);
    }

  }

  public void getMessageInfo() throws RemoteException {
    int lost_count = 0;
    for (int i = 0; i < totalMessages; i++) {
      if (receivedMessages[i] == 0) {
        lost_count++;
      }
    }

    if (lost_count > 0) {
      System.out.println("Messages lost: " + lost_count);
    } else {
      System.out.println("All messages received");
    }
  }

  public static void main(String[] args) {

    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new SecurityManager());
    }

    RMIServer rmis = null;

    try {
      rmis = new RMIServer();
    } catch (Exception e) {
      System.err.println("Error (server): could not create new " +
          "RMIServer.");
      System.exit(5);
    }

    rebindServer("RMIServer", rmis);

  }

  protected static void rebindServer(String serverURL,
                                     RMIServer server) {

    Registry r = null;

    try {
      r = LocateRegistry.createRegistry(1099);
    } catch (RemoteException e) {
      try {
        r = LocateRegistry.getRegistry();
      } catch (RemoteException e1) {
        System.err.println("Error (server): could not create registry "
            + "for rebinding.");
        System.exit(4);
      }
    }

    try {
      Naming.rebind(serverURL, server);
    } catch (RemoteException e) {
      System.err.println("Error (server): could not rebind.");
      System.exit(4);
    } catch (MalformedURLException e) {
      System.err.println("Error (server): could not rebind with " +
          "malformed URL.");
      System.exit(2);
    }
  }
}