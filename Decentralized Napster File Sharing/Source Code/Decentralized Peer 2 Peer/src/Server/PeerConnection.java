package Server;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class PeerConnection implements Runnable 
{
	private ConcurrentHashMap<String, Socket> MapSocket;
	private String NameOfTheServer;
	private String HostAddressOfServer;
	private int PortOfTheServer;
	private Socket socket;

	public PeerConnection(int serverPort, String serverName, String serverHostAddress,
			ConcurrentHashMap<String, Socket> socketMapping) 
	{
		// TODO Auto-generated constructor stub
		this.MapSocket = socketMapping;
		this.PortOfTheServer = serverPort;
		this.NameOfTheServer = serverName;
		this.HostAddressOfServer = serverHostAddress;
	}

	/**
	 * @param args
	 */
	public void run()
	{
		try
		{
			//System.out.println("Before Sleep");
			Thread.sleep(400000);
			socket = new Socket(HostAddressOfServer, PortOfTheServer);
			System.out.println("\nClient Connected to "+NameOfTheServer);
		}catch(IOException | InterruptedException e)
		{
			e.printStackTrace();
		}
		//System.out.println(serverName+" "+socket);
		MapSocket.put(NameOfTheServer, socket);
	}
}
