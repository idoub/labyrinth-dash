package com.labyrinthdash;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import android.util.Log;


public class InitialConnect extends Thread
{
	//Socket connection
	Socket socket1 = null;
	ServerSocket socket2 = null;
	
	DataOutputStream dataOutputStream = null;
	DataInputStream dataInputStream = null;
	
	boolean isMaster = false;
	String masterIP, slaveIP = null;
	
	DatagramSocket socket4 = null;
	
	String s, address;
	boolean createConnection, readyConnect, initialConnect, screenInfo = false;
	boolean createUDP = true;
	DatagramPacket packet, packet2;
	byte[] buf, buf2;
	ByteBuffer b;

	GamePlayer player1, player2;

	PrintWriter printOut;
	BufferedReader printIn;	
	
	int surfaceHeight, surfaceWidth = 0;	
	int surfaceHeight2, surfaceWidth2 = 0;
	String playerName;
	
	private static final String TAG = "InitialConnect";
	
	public InitialConnect(GamePlayer player1, GamePlayer player2, int surfaceHeight, int surfaceWidth, String playerName)
	{
		this.player1 = player1;
		this.player2 = player2;
		
		this.surfaceHeight = surfaceHeight;
		this.surfaceWidth = surfaceWidth;	
		
		this.playerName = playerName;
	}
	
	public void run()
	{
		// Initial connection to main server
		try 
		{
			Log.d(TAG, "About to try and connect");
			
			socket1 = null;
			socket1 = new Socket("192.168.0.102", 2222);
			
		    if(socket1 == null)
		    {
		    	createConnection = false;
		    	Log.d(TAG, "Socket Fail");
		    }
		    else
		    {			    
			    Log.d(TAG, "TCP Socket Created");
			    Log.d(TAG, "Server knows device's IP :)");
			    
			    // Server is aware of device ipAddress :)
			    socket1.close();
		    }
	    } 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
			
			Log.d("ERROR", "Error in connection 1");
			
			//TODO: exit
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			
			Log.d("ERROR", "Error in connection 2");
			
			//TODO: exit
		}  		
		
		// Receive other players info from main server
		try
		{
			socket2 = new ServerSocket(9999);
			
			socket1 = socket2.accept();
			
			Log.d(TAG, "Connection made");
			
			dataOutputStream = new DataOutputStream(socket1.getOutputStream());
            dataInputStream = new DataInputStream(socket1.getInputStream());
            
            String message = dataInputStream.readUTF();
            
            Log.d(TAG, message + " device");
            
            if(message.equals("Master"))
            {
            	isMaster = true;
            	
            	slaveIP = dataInputStream.readUTF();
            }
            else
            {
            	isMaster = false;
            	
            	masterIP = dataInputStream.readUTF();
            }           
            
            // Close sockets
            socket2.close();
            socket1.close();
            dataInputStream.close();
            dataOutputStream.close();
            
		}
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
			
			Log.d("ERROR", "Error in connection 3");
			
			//TODO: exit
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			
			Log.d("ERROR", "Error in connection 4");
			
			//TODO: exit
		}
		
		// Connect devices with TCP
		try
		{
			if(isMaster == true)
			{
				Log.d(TAG, "Connect to slave IP: " + slaveIP);
				
				// Receive TCP connection from Master
				socket2 = null;
				socket2 = new ServerSocket(7777);
				
				socket1 = socket2.accept();
				
				if(socket1 == null)
				{
					Log.d(TAG, "Error with TCP connection to slave");
				}
				else
				{
					Log.d(TAG, "TCP connection to slave created");
				}
				
				// Create stream
				dataOutputStream = new DataOutputStream(socket1.getOutputStream());
	            dataInputStream = new DataInputStream(socket1.getInputStream());
				
				// Send Screen info
				dataOutputStream.writeInt(surfaceWidth);
				dataOutputStream.writeInt(surfaceHeight);
				
				// Receive screen info
				surfaceWidth2 = dataInputStream.readInt();
				surfaceHeight2 = dataInputStream.readInt();
				
				Log.d(TAG, "Enemy slave: (width, height): (" + surfaceWidth2 + ", " + surfaceHeight2 + ")");
				
				// Close streams and socket
				socket2.close();
				socket1.close();
				dataInputStream.close();
				dataOutputStream.close();
			}
			else
			{
				Log.d(TAG, "Connect to master IP: " + masterIP);
								
				// Make TCP connection to slave
				socket1 = null;
				socket1 = new Socket(masterIP, 7777);
				
				if(socket1 == null)
				{
					Log.d(TAG, "Error receiving TCP connection from master");
				}
				else
				{
					Log.d(TAG, "TCP connection to slave received");
				}
				// Create streams
				dataOutputStream = new DataOutputStream(socket1.getOutputStream());
	            dataInputStream = new DataInputStream(socket1.getInputStream());
	            
	            // Receive screen info
				surfaceWidth2 = dataInputStream.readInt();
				surfaceHeight2 = dataInputStream.readInt();
	            
				// Send Screen info
				dataOutputStream.writeInt(surfaceWidth);
				dataOutputStream.writeInt(surfaceHeight);
				
				Log.d(TAG, "Enemy master: (width, height): (" + surfaceWidth2 + ", " + surfaceHeight2 + ")");	
				
				// Close streams and socket
				socket2.close();
				socket1.close();
				dataInputStream.close();
				dataOutputStream.close();
			}   
		}
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
			
			Log.d("ERROR", "Error in connection 5");
			
			//TODO: exit
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			
			Log.d("ERROR", "Error in connection 6");
			
			//TODO: exit
		}
		
		// Connect devices with UDP
		
		
	}
}
