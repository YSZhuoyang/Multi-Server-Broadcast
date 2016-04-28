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

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Message.*;
import activitystreamer.util.Settings;


public class ClientSolution extends Thread
{
	private static final Logger log = LogManager.getLogger();
	private static ClientSolution clientSolution;
	private TextFrame textFrame;
	
	/*
	 * additional variables
	 */
	private Socket socket;
	private PrintWriter writer;
	private DataInputStream in;
	private DataOutputStream out;
	private BufferedReader inreader;
	private boolean term;
	
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
		
		// Test: register only
		if (Settings.getRemoteHostname() != null)
		{
			// Connect to a server host
			establishConnection();
			
			// Testing
			sendRegisterMsg();
			
			// open the gui
			log.debug("opening the gui");
			
			textFrame = new TextFrame();
			
			// start the client's thread
			start();
		}
		else
		{
			log.debug("Host name is empty");
		}
	}

	// called by the gui when the user clicks "send"
	public void sendActivityObject(JsonObject receivedJsonObj)
	{
		log.info("Activity message sent");
		
		String command = receivedJsonObj.get("command").getAsString();
		
		if (command.equals("ACTIVITY_MESSAGE"))
		{
			String activityMessage = new Gson().toJson(receivedJsonObj);
			writer.println(activityMessage);
		}
	}

	// called by the gui when the user clicks disconnect
	public void disconnect()
	{
		textFrame.setVisible(false);

		/*
		 * other things to do
		 */
		closeConnection();
	}

	// the client's run method, to receive messages
	@Override
	public void run()
	{
		log.debug("Client started");
		
		try
		{
			while (!term)
			{
				String receivedMsg = inreader.readLine();
				
				log.debug("Client received: " + receivedMsg);
				
				JsonObject receivedJson = new Gson().fromJson(receivedMsg, JsonObject.class);
				String command = receivedJson.get("command").getAsString();
				
				switch (command)
				{
					case JsonMessage.ACTIVITY_BROADCAST:
						log.info("Activity broadcast received");
						
						textFrame.displayActivityMessageText(receivedJson);
						
						break;
					
					case JsonMessage.REGISTER_FAILED:
						log.info("Register failed");

						processRegisterFailedMsg(receivedJson);
						
						System.exit(0);
						
					case JsonMessage.REDIRECT:
						processRedirectMsg(receivedJson);
						
						break;
					
					case JsonMessage.AUTHENTICATION_FAIL:
						log.info("Client failed to send activity message to server.");
						
						// Close the current connection
						closeConnection();
						
						break;
					
					case JsonMessage.INVALID_MESSAGE:
						log.info("Client failed to send activity message to server.");
						
						// Close the current connection
						closeConnection();
						
						break;
						
					case JsonMessage.LOGIN_FAILED:
						log.info("Login failed");
						
						closeConnection();
						
						System.exit(0);
					
					default:
						break;
				}
				
			}
		}
		catch (IOException e)
		{
			System.err.println("Client failed: " + e.getMessage());
		}
	}
	
	/*
	 * additional methods
	 */
	private void processRedirectMsg(JsonObject receivedJsonObj)
	{
		log.info("Redirect");
		
		// Close the current connection
		closeConnection();
		
		// Setup with new host and port number
		String newHost = receivedJsonObj.get("host").getAsString();
		int newPort = receivedJsonObj.get("port").getAsInt();
		
		Settings.setRemoteHostname(newHost);
		Settings.setRemotePort(newPort);
		
		// Reconnect to another server
		log.info("Connect to another server");
		
		establishConnection();
		sendLoginMsg();
	}
	
	private void processRegisterFailedMsg(JsonObject receivedJsonObj)
	{
		String username = receivedJsonObj.get("username").getAsString();
		
		RegisterFailedMsg registerFailedMsg = new RegisterFailedMsg();
		registerFailedMsg.setInfo("The attempt of" + username + "registering the system is failed.");
		writer.println(registerFailedMsg.toJsonString());
		
		closeConnection();
	}

	private void establishConnection()
	{
		try
		{
			socket = new Socket(Settings.getRemoteHostname(), Settings.getRemotePort());
			
			in = new DataInputStream(socket.getInputStream());
			inreader = new BufferedReader(new InputStreamReader(in));
			
			out = new DataOutputStream(socket.getOutputStream());
			writer = new PrintWriter(out, true);
			
		}
		catch (IOException e)
		{
			log.debug("Client establish connection failed: " + e.getMessage());
		}
	}
	
	private void closeConnection()
	{
		try
		{
			in.close();
			inreader.close();
			
			out.close();
			writer.close();
		}
		catch (IOException e)
		{
			log.debug("Client close connection failed: " + e.getMessage());
		}
	}

	private void sendRegisterMsg()
	{
		RegisterMsg registerMsg = new RegisterMsg();
		registerMsg.setUsername(Settings.getUsername());
		registerMsg.setSecret(Settings.getSecret());
		String registerMessage = registerMsg.toJsonString();

		writer.println(registerMessage);
	}
	
	private void sendLoginMsg()
	{
		LoginMsg loginMsg = new LoginMsg();
		loginMsg.setUsername(Settings.getUsername());
		loginMsg.setSecret(Settings.getSecret());
		String loginMessage = loginMsg.toJsonString();

		writer.println(loginMessage);
	}
	
	public void sendLogoutMsg()
	{
		LogoutMsg logoutMsg = new LogoutMsg();
		logoutMsg.setUsername(Settings.getUsername());
		logoutMsg.setSecret(Settings.getSecret());
		
		writer.println(logoutMsg.toJsonString());
		
		term = true;
	}
}
