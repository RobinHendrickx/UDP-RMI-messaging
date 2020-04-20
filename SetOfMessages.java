/*
 * Created on 01-Mar-2016
 */
package common;

import java.io.Serializable;

/**
 * Utility class that encapsulates the message information to
 * be passed from client to server.  Information can be extracted
 * or constructed as a String for use by the UDP example.
 *
 */
public class SetOfMessages implements Serializable {

	private static final long serialVersionUID = 6371270109105252647L;

	public MessageInfo msgs[];

	public SetOfMessages (int total) {

		msgs = new MessageInfo[total];

		for(int i = 0; i < total; i++)
		{
			msgs[i] = new MessageInfo(total, i);
		}
	}

}
