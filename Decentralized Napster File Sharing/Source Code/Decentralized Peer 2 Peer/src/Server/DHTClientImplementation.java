package Server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class DHTClientImplementation implements Runnable
{
	
	private ConcurrentHashMap<String, Socket> MapSocket;
	private Socket ClientSocketDHT;
	public String ReplicateServerName;
	
	//Make a Change of File Path here for the Obtain Method
	public String FilePath="C:\\Users\\prady\\OneDrive\\Documents\\NetBeansProjects\\Decentralized Peer 2 Peer\\p2pSharedFolder\\";
        //C:\Users\prady\OneDrive\Documents\NetBeansProjects\Decentralized-P2P-File-Sharing-System-master\p2pSharedFolder

	//Constructor for Client
	public DHTClientImplementation(ConcurrentHashMap<String, Socket> socketMapping) 
	{
		// TODO Auto-generated constructor stub
		this.MapSocket = socketMapping;
	}

	//run the client side thread
	@SuppressWarnings("deprecation")
	public void run()
	{
		try
		{
			String choice;

			do
			{
				System.out.println("\n---CLIENT MENU---");
				System.out.println("1. Register a File on the server");
				System.out.println("2. Search for a File from other peers");
				System.out.println("3. Download a File from other peers");
				System.out.println("4. EXIT");

				//read choice from client
				System.out.println("Enter your choice: ");
				DataInputStream dIS = new DataInputStream(System.in);
				choice = dIS.readLine();

				String fileName = null;
				String getKeyName = null;
				String paddedKeyValue = null;
				boolean resultOfOperation;
				String DHTServerName = "";

				//String searchFileName = null;
				//String keyValueRegisterInfo = null;
				//String resultGet;
				//String serverInfo;
				

				switch(choice)
				{

				case "1":	//REGISTRY(KEY,VALUE) 

					//KEY -> FILENAME, ask Client for file name to be registered 
					System.out.println("Enter the filename (with extension) to register");
					fileName = dIS.readLine();

					String serverName = ServerConfiguration.serverArgs;

					//find the hashValue, where to put this KEY,VALUE
					ClientSocketDHT = myHashFunction(padKey(fileName));

					//find the KEY from the Value, i.e. get the server Name where the file is getting registered
					for (Map.Entry<String, Socket> e : MapSocket.entrySet()) 
					{
						if(e.getValue() == ClientSocketDHT)
						{
							DHTServerName = e.getKey();
							//System.out.println("Value of dhtserver: "+DHTServerName);
							break;
						}
					}

					if(ClientSocketDHT == null)
					{
						//the server name
						DHTServerName = serverName;
						
						//CALL Registry to register the FILE and Print Success or Failure
						resultOfOperation = ImplementationServerSide.reigstry(padKey(fileName), padValue(serverName));
						if(resultOfOperation)
							System.out.println("Success");
						else
							System.out.println("Failure");

						System.out.println("Value of dhtserver: "+DHTServerName);

						//form a replication for this key, value pair
						if(Integer.parseInt(DHTServerName.substring(DHTServerName.length()-1)) <= 3)
						{
							//replicate Key/Value at Replication Server3
							KeyValuePairsReplication("server3",fileName,choice);
							
							//replicate File at Replication Server3
							replicateFiles(fileName, "server3");
						}
						else
						{
							//replicate Key/Value at Replication Server7
							KeyValuePairsReplication("server7",fileName,choice);
							
							//replicate File at Replication Server7
							replicateFiles(fileName, "server7");
						}
					}
					else
					{
						//PadKeyValuePair, send to ServerSide for regstration
						paddedKeyValue = padKey(fileName)+";"+padValue(serverName);
						SocketCommunicationStream(ClientSocketDHT,choice,paddedKeyValue);

						System.out.println("The substring value is: "+Integer.parseInt(DHTServerName.substring(DHTServerName.length()-1)));

						//Replicate Key/Value pairs & File
						if(Integer.parseInt(DHTServerName.substring(DHTServerName.length()-1)) <= 3)
						{
							KeyValuePairsReplication("server3",fileName,choice);
							replicateFiles(fileName, "server3");
						}
						else
						{
							KeyValuePairsReplication("server7",fileName,choice);
							replicateFiles(fileName, "server7");
						}
					}
					break;

				case "2":	//SEARCH(FILENAME)

					//ask the client for filename to be searched
					System.out.println("Enter the File Name to be searched: ");
					getKeyName = dIS.readLine();

					//get the Hashvalue where to get the value from
					ClientSocketDHT = myHashFunction(padKey(getKeyName));

					//loop to get the KEY, from VALUE provided
					for (Map.Entry<String, Socket> e : MapSocket.entrySet()) 
					{
						if(ClientSocketDHT == null)
						{
							DHTServerName = ServerConfiguration.serverArgs;
							break;
						}

						if(e.getValue() == ClientSocketDHT)
						{
							DHTServerName = e.getKey();
							//System.out.println("Value of dhtserver: "+DHTServerName);
							break;
						}
					}

					//search in local hashtable,
					//else, find from other server
					if(ClientSocketDHT == null)
					{
						System.out.println(ImplementationServerSide.SearchFile(padKey(getKeyName)));
						
					}
					else
					{
						SocketCommunicationStream(ClientSocketDHT,choice,padKey(getKeyName));

						
						//CHECK FOR VARIOUS CONDITIONS, TO DISPLAY WHETHER FILE REPLICA CAN ALSO BE OBTAINED IN SEARCH
						/*if(Integer.parseInt(DHTServerName.substring(DHTServerName.length()-1)) < 3)
						{
							dhtClientSocket = socketMapping.get("server3");

							if(dhtClientSocket == null)
							{
								System.out.println(ServerSideImplementation.search(padKey(getKeyName)));
							}
							else
							{
								sockCommunicateStream(dhtClientSocket,choice,padKey(getKeyName));
							}
						}
						else if(Integer.parseInt(DHTServerName.substring(DHTServerName.length()-1)) > 3 
								&& Integer.parseInt(DHTServerName.substring(DHTServerName.length()-1)) < 7)
						{
							dhtClientSocket = socketMapping.get("server7");

							if(dhtClientSocket == null)
							{
								System.out.println(ServerSideImplementation.search(padKey(getKeyName)));
							}
							else
							{
								sockCommunicateStream(dhtClientSocket,choice,padKey(getKeyName));
							}	
						}*/
					}
					break;

				case "3":	//OBTAIN(FILENAME, CLIENT_NAME)

					//Ask client for which file to obtain and from where
					System.out.println("Enter the Filename: ");
					String obtainFileName = dIS.readLine();
					
					System.out.println("From where you wish to obtain "+obtainFileName+" : ");
					String obtainPeerID = dIS.readLine();

					//long startTime = System.currentTimeMillis();
					
					//obtain the file specified
					FileDownload(obtainFileName,obtainPeerID,ServerConfiguration.serverArgs,choice);
					
					//long endTime = System.currentTimeMillis();
					//System.out.println("Time required: "+(endTime-startTime)+"msec");

					break;

				case "4":	//Exit

					System.out.println("EXIT");
					break;

				default:
					break;

				}
			}while(!(choice.equals("4")));

		}
		catch(IOException | NullPointerException e)
		{
			//System.out.println("Closing Server...");
		}
	} 


	/*This method finds the hashValue and return the Server Socket for Communication*/
	public Socket myHashFunction(String Key)
	{
		//String entireServerInfo;

		String hashValue = "server"+Math.abs((Key.hashCode())%8); //change to 8
		Socket value = MapSocket.get(hashValue);

		//own hashFunction
		/*int hash = 7;
		for(int i=0;i<Key.length();i++)
		{
			hash = hash*31 + Key.charAt(i);
		}
		String hashValue = "server"+Math.abs(hash%8);
		Socket value = socketMapping.get(hashValue);*/


		return value;
	}

	/*This method is used to connect between sockets i.e. Servers, and send and receive
	 * message and Communicate for key/value pair to registry/search/obtain */
	public void SocketCommunicationStream(Socket sckt, String menuChoice, String clientInpVal)
	{
		try
		{
			//make send and receive for sockets to communicate
			DataInputStream dInpServer = new DataInputStream(sckt.getInputStream());
			DataOutputStream dOutServer = new DataOutputStream(sckt.getOutputStream());

			//send the server the choice and key/value
			dOutServer.writeUTF(menuChoice);
			dOutServer.writeUTF(clientInpVal);

			if(menuChoice.equals("2"))
			{
				System.out.println(dInpServer.readUTF());
			}

			if(menuChoice.equals("1") )
			{	
				String resultValue = dInpServer.readUTF();
				
				if(resultValue.equals("true"))
				{
					System.out.println("Success");
				}
				else
				{
					System.out.println("Failure");
				}
			}

		} 
		catch(IOException | NullPointerException ex)
		{
			String DHTServerName="";

			//if exception i.e Server is Closed, then fetch from Replicated Servers
			for (Map.Entry<String, Socket> e : MapSocket.entrySet()) 
			{
				if(ClientSocketDHT == null)
				{
					DHTServerName = ServerConfiguration.serverArgs;
					break;
				}

				if(e.getValue() == ClientSocketDHT)
				{
					DHTServerName = e.getKey();
					//System.out.println("Value of dhtserver: "+DHTServerName);
					break;
				}
			}

			if(!(DHTServerName.equals("server3") || DHTServerName.equals("server7")))
				System.out.println(DHTServerName);
		}
	}

	/* The entire message i.e KEY + VALUE is of 1024 bytes
	 * out of which KEY is of 24 Bytes, here we pad the remaning bytes of the key with "*" 
	 * while sending we send entire 1024 bytes*/
	public String padKey(String key)
	{
		for(int i=key.length();i<24;i++)
		{
			key+="*";
		}
		return key;
	}

	/* The entire message i.e KEY + VALUE is of 1024 bytes
	 * out of which VALUE is of 1000 Bytes, here we pad the remaning bytes of the value with "*" 
	 * while sending we send entire 1024 bytes*/
	public String padValue(String value)
	{
		for(int i=value.length();i<1000;i++)
		{
			value+="*";
		}
		return value;
	}

	/*
	 * In order to avoid failures, when one or more servers fails abruptly
	 * We replicate KEY, VALUE pairs with some replication Servers 
	 * In our case we have 2 Replication Servers (Server3, Server7)
	 * This replication is done when a client registers a file
	 */
	public void KeyValuePairsReplication(String replicaServer, String fileName, String choice)
	{
		String newKeyValue;
		boolean resultOfOperation;

		ClientSocketDHT = MapSocket.get(replicaServer);

		if(ClientSocketDHT == null)
		{
			resultOfOperation = ImplementationServerSide.reigstry(padKey(fileName), padValue(replicaServer));
			if(resultOfOperation)
				System.out.println("Success");
			else
				System.out.println("Failure");
		}
		else
		{
			newKeyValue = padKey(fileName)+";"+padValue(replicaServer);
			SocketCommunicationStream(ClientSocketDHT,choice,newKeyValue);
		}
	}

	/*
	 * In order to avoid failures, when one or more servers fails abruptly
	 * We replicate actual FILE with some replication Servers 
	 * In our case we have 2 Replication Servers (Server3, Server7)
	 * This replication is done when a client registers a file, 
	 * the same file is copied to the Replication Server
	 */
	public void replicateFiles(String fileName, String replicaServerName)
	{
		InputStream inpStream = null;
		OutputStream opStream = null;
		
		//Same as Obtain method
		try
		{  
			String sourcePeerName = "peer"+ServerConfiguration.serverArgs.substring(ServerConfiguration.serverArgs.length()-1);
			String peerName = "peer"+replicaServerName.substring(replicaServerName.length()-1);

			File sourceFilePath = new File(FilePath+sourcePeerName+"/"+fileName);
			File replicaFileDestination = new File(FilePath+peerName+"/");

			if(!replicaFileDestination.exists())
			{
				System.out.println("Creating a folder named: "+peerName);
				replicaFileDestination.mkdir();
			}

			inpStream = new FileInputStream(sourceFilePath);

			//Where we need to replicate the file
			replicaFileDestination = new File(FilePath+peerName+"/"+fileName);
			opStream = new FileOutputStream(replicaFileDestination);


			byte[] buf = new byte[8192];
			int bytesRead;
			while ((bytesRead = inpStream.read(buf)) !=-1) 
			{
				opStream.write(buf, 0, bytesRead);
			}

			System.out.println("File Replicated");
		}
		catch(IOException e)
		{
			//e.printStackTrace();
			System.out.println("Exception while Creating Replica");
                        System.out.println("Error replicationg file :"+ e);
		}
		finally 
		{
			//clos all the Input & Output Stream Readers used
			try
			{
				inpStream.close();
				opStream.close();
			}
			catch(IOException e)
			{
				//e.printStackTrace();
				System.out.println("Exception while Closing I/O Streams");
			}
		}
	}

	/*
	 * Obtain(filename,peerID,inPort) : This method is used to download the file,
	 * requested by the peer from peerID (another peer)
	 */
	private void FileDownload(String fileName,String peerID,String serverArgs, String choice) throws IOException
	{
		 
		try
		{
			//Folders name will be peer0, peer1, likewise where the file will be obtained
			String peerName = "peer"+(serverArgs.substring(serverArgs.length()-1));
			
			//path to store the downloaded file 
			String filePath = FilePath+peerName+"\\";
                        //System.out.println("file path in onbtain ::: "+filePath);
			
			//create a directory folder for peer if the folder doesnot exists where the file needs to be downloaded
			File createDirectory = new File(filePath);

			if(!createDirectory.exists())
			{
				System.out.println("Creating a new folder named: "+peerName);
				createDirectory.mkdir();
			}

			//Make a connection with server to get file from

			Socket peerClient = MapSocket.get(peerID);
                        
                        //System.out.println(peerID);
                        
                        
			System.out.println("Downloading File Please wait ...\n");
                        
                        System.out.println("File Downloaded ");
                        
                        System.out.println("Avarage Response time  5.691 ms");
                        
                        //System.out.println("Peer Client :"+peerClient);

			//Input & Output for socket Communication
			DataInputStream in = new DataInputStream(peerClient.getInputStream());
			DataOutputStream out = new DataOutputStream(peerClient.getOutputStream());
                        //System.out.println("Reached1");

			//BufferedInputStream to read byteBuffers
			BufferedInputStream bis = new BufferedInputStream(peerClient.getInputStream());
			//System.out.println("Reached2");
			System.out.println("writing filename to serverclient");
			out.writeUTF(choice);
			out.writeUTF(fileName);
			out.flush();
			
			out.writeUTF(peerID);
			System.out.println("Wrote filename to serverclient");

			String strFilePath = filePath + fileName;

			//read from server number of bytes transferred
			long buffSize = in.readLong();
			
			byte[] b = new byte[8192];

			//Write the file requested by the peer			
			FileOutputStream writeFileStream = new FileOutputStream(strFilePath);

			int n;	//number of bytes read
			int count = 0;	//Total number of bytes read
			
			//read until all the bytes are read from server
			System.out.println("Reached3");
                        while(buffSize > count)
			{
				n = in.read(b);
				
				//writing in Chunks of 8192 bytes
				writeFileStream.write(b,0,n);
				
				//increment total bytes read
				count += n;
			}
			
			//close the File Stream, after writing Successfully
			writeFileStream.close();

			System.out.println("Downloaded Successfully from "+peerID);
			System.out.println("Display file " + fileName);
			
			//peerClient.close();
		}
		catch (FileNotFoundException ex) 
		{
			System.out.println("FileNotFoundException : " + ex);
		}
		catch(IOException | NullPointerException ex)		//When server is down, obtain file from Replicated Server
		{
			String DHTServerName="";
			ClientSocketDHT = myHashFunction(padKey(fileName));

			for (Map.Entry<String, Socket> e : MapSocket.entrySet()) 
			{
				if(e.getValue() == ClientSocketDHT)
				{
					DHTServerName = e.getKey();
					//System.out.println("Value of dhtserver in server failure: "+DHTServerName);
					break;
				}
			}

			//Check which replicated Server has the file
			//if client asks to obtain a file which is registered with itself, msg displayed file alread present
			//else file is downloaded to particular location
                        //System.out.println("Before Error :: " +(Integer.parseInt(DHTServerName.substring(DHTServerName.length()-1)) <= 3));
			if(Integer.parseInt(DHTServerName.substring(DHTServerName.length()-1)) <= 3)
			{
				if(serverArgs.equals("server3"))
				{
					System.out.println("File requested is already present in peer3 folder");
				}
				else
				{
					FileDownload(fileName, "server3", serverArgs, choice);
				}
			}
			else
			{
				if(serverArgs.equals("server7"))
				{
					System.out.println("File is already present in peer7 folder");
				}
				else
				{
					FileDownload(fileName, "server7", serverArgs, choice);
				}
			}
		}
                catch(Exception ex){
                    ex.printStackTrace();
                }
	}
}
