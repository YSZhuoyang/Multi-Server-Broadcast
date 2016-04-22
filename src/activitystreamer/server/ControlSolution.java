package activitystreamer.server;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import activitystreamer.util.Settings;

public class ControlSolution extends Control
{
	private static final Logger log = LogManager.getLogger();
	private static String sec = null;
	private static ArrayList<Connection> serverList;
	private static ArrayList<Connection> clientList;
	/*
	 * additional variables as needed
	 */

	// since control and its subclasses are singleton, we get the singleton this
	// way
	public static ControlSolution getInstance()
	{
		if (control == null)
		{
			control = new ControlSolution();
		}
		return (ControlSolution) control;
	}

	public ControlSolution()
	{
		super();
		
		/*
		 * Do some further initialization here if necessary
		 */

		//This is the root server
		if(Settings.getRemoteHostname()==null){
			//generate secrete keys here
			sec = Settings.nextSecret();
			Settings.setSecret(sec);
			log.info("The secret key for all server is:" + sec.toString());
		}// else is not the root server;check if the secret is correct or the command is valid
		else {
			// check if we should initiate a connection and do so if necessary
			
			initiateConnection();

			
		
		}
		// start the server's activity loop
		// it will call doActivity every few seconds
		start();
	}

	/*
	 * a new incoming connection
	 */
	@Override
	public Connection incomingConnection(Socket s) throws IOException
	{
		Connection con = super.incomingConnection(s);
		
		/*
		 * do additional things here
		 */
		
		

		return con;
	}

	/*
	 * a new outgoing connection
	 */
	@Override
	public Connection outgoingConnection(Socket s) throws IOException
	{
		Connection con = super.outgoingConnection(s);
		con.writeMsg("hi, this is a new server yelling at you!!!");
		/*
		 * do additional things here
		 */

		return con;
	}

	/*
	 * the connection has been closed
	 */
	@Override
	public void connectionClosed(Connection con)
	{
		super.connectionClosed(con);
		/*
		 * do additional things here
		 */
	}

	/*
	 * process incoming msg, from connection con return true if the connection
	 * should be closed, false otherwise
	 */
	@Override
	public synchronized boolean process(Connection con, String msg)
	{
		/*
		 * do additional work here return true/false as appropriate
		 */
		log.debug(msg);
		return false;
	}

	/*
	 * Called once every few seconds Return true if server should shut down,
	 * false otherwise
	 */
	@Override
	public boolean doActivity()
	{
		/*
		 * do additional work here return true/false as appropriate
		 */

		return false;
	}

	/*
	 * Other methods as needed
	 */

}
