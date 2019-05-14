package Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomFilesGeneration 
{

	public static void main(String args[]) throws IOException
	{
		
		
                for(int i=100000;i<110000;i++)
		{
			File file = new File("C:\\Users\\prady\\OneDrive\\Documents\\NetBeansProjects\\Decentralized Peer 2 Peer\\p2pSharedFolder\\peer0\\"+i);
			RandomAccessFile RAF = new RandomAccessFile(file, "rw");
			RAF.setLength(10000);
			RAF.close();
		}
                for(int i=200000;i<210000;i++)
		{
			File file = new File("C:\\Users\\prady\\OneDrive\\Documents\\NetBeansProjects\\Decentralized Peer 2 Peer\\p2pSharedFolder\\peer1\\"+i);
			RandomAccessFile RAF = new RandomAccessFile(file, "rw");
			RAF.setLength(10000);
			RAF.close();
		}
		
		for(int i=300000;i<310000;i++)
		{
			File file = new File("C:\\Users\\prady\\OneDrive\\Documents\\NetBeansProjects\\Decentralized Peer 2 Peer\\p2pSharedFolder\\peer2\\"+i);
			RandomAccessFile RAF = new RandomAccessFile(file, "rw");
			RAF.setLength(10000);
			RAF.close();
		}
		
		for(int i=400000;i<410000;i++)
		{
			File file = new File("C:\\Users\\prady\\OneDrive\\Documents\\NetBeansProjects\\Decentralized Peer 2 Peer\\p2pSharedFolder\\peer3\\"+i);
			RandomAccessFile RAF = new RandomAccessFile(file, "rw");
			RAF.setLength(10000);
			RAF.close();
		}
		
		for(int i=500000;i<510000;i++)
		{
			File file = new File("C:\\Users\\prady\\OneDrive\\Documents\\NetBeansProjects\\Decentralized Peer 2 Peer\\p2pSharedFolder\\peer4\\"+i);
			RandomAccessFile RAF = new RandomAccessFile(file, "rw");
			RAF.setLength(10000);
			RAF.close();
		}
		
		for(int i=600000;i<610000;i++)
		{
			File file = new File("C:\\Users\\prady\\OneDrive\\Documents\\NetBeansProjects\\Decentralized Peer 2 Peer\\p2pSharedFolder\\peer5\\"+i);
			RandomAccessFile RAF = new RandomAccessFile(file, "rw");
			RAF.setLength(10000);
			RAF.close();
		}
		
		for(int i=700000;i<710000;i++)
		{
			File file = new File("C:\\Users\\prady\\OneDrive\\Documents\\NetBeansProjects\\Decentralized Peer 2 Peer\\p2pSharedFolder\\peer6\\"+i);
			RandomAccessFile RAF = new RandomAccessFile(file, "rw");
			RAF.setLength(10000);
			RAF.close();
		}
		for(int i=800000;i<810000;i++)
		{
			File file = new File("C:\\Users\\prady\\OneDrive\\Documents\\NetBeansProjects\\Decentralized Peer 2 Peer\\p2pSharedFolder\\peer7\\"+i);
			RandomAccessFile RAF = new RandomAccessFile(file, "rw");
			RAF.setLength(10000);
			RAF.close();
		}
	}
}
