package com.labyrinthdash;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

import android.util.Log;

class AwaitConnect extends Thread
{
	// TCP connection
	DataOutputStream dataOutputStream = null;
	DataInputStream dataInputStream = null;	
	int connectionNumber = 0;
	ConnectionInfo connectInfo;
	
	// UDP connection
	DatagramSocket socket = null;
	DatagramPacket packet;
	byte[] buf, buf2;
	ByteBuffer b;
	String address;
	
	boolean connectOK = false;
	int surfaceHeight, surfaceWidth = 0;
	int surfaceHeight2, surfaceWidth2 = 0;
	GamePlayer player1, player2;
	
	private static final String TAG = "AwaitConnect";
	
	public AwaitConnect(DataOutputStream dataOutputStream, DataInputStream dataInputStream, ConnectionInfo connectInfo,
							int surfaceHeight, int surfaceWidth, GamePlayer player1, GamePlayer player2)
	{
		this.dataOutputStream = dataOutputStream;
		this.dataInputStream = dataInputStream;	
		this.connectInfo = connectInfo;
		
		this.surfaceHeight = surfaceHeight;
		this.surfaceWidth = surfaceWidth;
		
		this.player1 = player1;
		this.player2 = player2;
	}
	
	public void run()
	{
		Log.d(TAG, "Begun waiting to send request");
		
		// TEST FUNCTIONALITY
		//connectionNumber = 0;
		//connectInfo.setSendRequest(true);
		
		while(connectInfo.getAcceptRequest() != true)
		{
			if(connectInfo.getSendRequest() == true)
			{
				try 
				{
					// Send request to play with specified player
					// TODO Need to get connection number
					dataOutputStream.writeInt(connectionNumber);
					
					Log.d(TAG, "Sent connection request");
					
					// Wait for other player response
					while(connectInfo.getResponse() == 0)
					{
						
					}
					
					if(connectInfo.getResponse() == 1)
					{
						connectOK = true;
					}
					else
					{
						connectOK = false;
						connectInfo.setResponse(0);
					}					
					
					if(connectOK == true)
					{
						connectInfo.setAcceptRequest(true);
						
						// You are master - instigated connection
						connectInfo.setIsMaster(true);		
					}				
				} 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				connectInfo.setAcceptRequest(true);
			}
			
		}
		
		// TODO: Get opponent address, surfaceHeight, surfaceWidth
		
		
		

		// Create UDP Connection			
		Log.d(TAG, "About to create UDP connection");
			
		/*try 
		{
			socket = new DatagramSocket(4446);
				
			Log.d(TAG, "UDP socket created");

			packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(address), 4446);		
			
			Log.d(TAG, "UDP packet created");
		} 
		catch (SocketException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (UnknownHostException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
																	
		// Start communication threads
		//new sendPos(player1, socket, packet, buf, address, connectInfo.getIsMaster()).start();
				
		//new receivePos(player2, socket, packet, surfaceHeight, surfaceWidth, surfaceHeight2, surfaceWidth2, buf).start();
	}
	
}
