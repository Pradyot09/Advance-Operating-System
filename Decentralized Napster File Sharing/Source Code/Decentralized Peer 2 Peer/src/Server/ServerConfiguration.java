package Server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ServerConfiguration 
{
	//Attributes for Config.xml file
	private static String NameOftheServer;		/*Contains the Server Name*/
	private static int PortOfTheServer;			/*Contains the PORT Number for the Server*/
	private static String HostAddressOfTheServer;/*Contains the IP Address*/

	//Variables for fetching data from XML file
	private static Element element;
	private static Node nNode;

	//condition variables
	private static boolean asAServer;
	private static boolean asAClient;

	//Socket constructs
	private static ConcurrentHashMap<String, Socket> socketMapping 
	= new ConcurrentHashMap<String, Socket>();		/*Mapping the ServerName and its Socket*/

	private static ServerSocket serverSocket;
	private static Socket clientSocket;

	public static String serverArgs;
	
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		
		try
		{
			//Change the path here for config file
			//Load the configuration file CONFIG.XML has information about all the servers
			File configFile = new File("C:\\Users\\prady\\OneDrive\\Documents\\NetBeansProjects\\Decentralized Peer 2 Peer\\src\\config.xml");
			System.out.println("config File "+ configFile);
                        serverArgs = args[0];
			
			System.out.println("------------------");
			System.out.println("\t"+serverArgs);
			System.out.println("------------------");

			//File configFile = new File("C:\\Users\\USER\\Desktop\\PROG2_CHOPRA_SHALIN\\SimpleDistributedHashTable\\src\\config.xml");	
			//Parsing the DOM tree for XML file
			DocumentBuilderFactory docbldFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			docBuilder = docbldFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(configFile);

			//normalize the DOM tree
			doc.getDocumentElement().normalize();

			//get the list of servers and their information from XML <Servers> tag
			NodeList nodeList = doc.getElementsByTagName("Servers");

			//Repeat for all the servers in the config.xml file (In our case 8 Servers)
			for (int i = 0; i < nodeList.getLength(); i++) 
			{
				nNode = nodeList.item(i);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) 
				{
					element = (Element) nNode;

					//get all the attributes for each of the servers
					NameOftheServer = element.getElementsByTagName("ServerName").item(0).getTextContent();  
					PortOfTheServer = Integer.parseInt(element.getElementsByTagName("ServerPort").item(0).getTextContent());
					HostAddressOfTheServer = element.getElementsByTagName("ServerIP").item(0).getTextContent();

					//check whether the Command Line Args is same as ServerName,
					//if YES, make it as a server
					if(NameOftheServer.equalsIgnoreCase(serverArgs))
					{
						asAServer = true;
						asAClient = false;
						//System.out.println("ServerName: "+serverName+" ServerPort: "+serverPort+" ServerIP: "+serverHostAddress+"\n");

						if(asAServer)
						{
							try 
							{
								//create a server socket
								serverSocket = new ServerSocket(PortOfTheServer);

								
								//create a thread for Client and start the client
								Thread clientThread = new Thread(new DHTClientImplementation(socketMapping));
								clientThread.start();
								
								/*For Performance Evaluation thread to run Uncomment this and Comment above thread*/
								/*Thread PerfEvlThread = new Thread(new DHTPerformanceEvaluation(socketMapping));
								PerfEvlThread.start();*/
								

							} catch (IOException /*| InterruptedException*/ e) 
							{
								e.printStackTrace();
							}
						}
					}
					else 
					{
						asAServer = false;
						asAClient = true;

						if(asAClient)
						{
							Thread connectServerThread = new Thread(new PeerConnection(PortOfTheServer,NameOftheServer,HostAddressOfTheServer,socketMapping));
							connectServerThread.start();
						}	
					}
				}
			}//end for()

			while(!serverSocket.isClosed())
			{
				//System.out.println("Server waiting for client to accept\n");
				clientSocket = serverSocket.accept();

				//start the server side thread
				Thread serverSide = new Thread(new ImplementationServerSide(clientSocket));
				serverSide.start();
				//System.out.println(serverName +" accepted Connection\n");
			}

			//call ClientSide Thread DHTClient.java
			Thread clientMenu = new Thread(new DHTClientImplementation(socketMapping));
			clientMenu.start();
			

		}catch(IOException | ParserConfigurationException | SAXException e)
		{
			e.printStackTrace();
		} 
	}
}
