package activitystreamer.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import com.google.gson.Gson;

import Message.JsonMessage;
import Message.LoginMsg;
import activitystreamer.util.Settings;

public class ClientSolution extends Thread
{
	private static final Logger log = LogManager.getLogger();
	private static ClientSolution clientSolution;
	private TextFrame textFrame;

	/*
	 * additional variables
	 */

	// this is a singleton object
	public static ClientSolution getInstance()
	{
		if (clientSolution == null)
		{
			clientSolution = new ClientSolution();
		}
		
		return clientSolution;
	}

	public ClientSolution()
	{
		/*
		 * some additional initialization
		 */
		
		// Connect to a server host
		if (Settings.getRemoteHostname() != null)
		{
			try
			{
				Gson gson = new Gson();
				LoginMsg loginJson = new LoginMsg();
				
				// Set values
				loginJson.setSecret(Settings.getSecret());
				loginJson.setUsername(Settings.getUsername());
				
				String loginMessage = gson.toJson(loginJson);
				
				// Try to establish connection
				Socket socket = new Socket(Settings.getRemoteHostname(), Settings.getRemotePort());

				PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
				//PrintWriter writer = new PrintWriter(new DataOutputStream(socket.getOutputStream()), true);
				writer.println(loginMessage);
				//writer.flush();

				log.info("Message sent: " + loginMessage);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		// open the gui
		log.debug("opening the gui");
		textFrame = new TextFrame();
		
		// start the client's thread
		start();
	}

	// called by the gui when the user clicks "send"
	public void sendActivityObject(JSONObject activityObj)
	{
		
	}

	// called by the gui when the user clicks disconnect
	public void disconnect()
	{
		textFrame.setVisible(false);
		/*
		 * other things to do
		 */
	}

	// the client's run method, to receive messages
	@Override
	public void run()
	{
		
	}

	/*
	 * additional methods
	 */

}