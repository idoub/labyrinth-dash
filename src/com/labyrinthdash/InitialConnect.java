package com.labyrinthdash;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.InetAddress;
import android.util.Log;

/**
 * Create a connection to opponent device via main server
 * 
 * Connection process:
 * 
 * 1. Initiate TCP connection with main server
 * 2. Await assignment of opponent
 * 3. Break connection with main server when received opponent IP
 * 3. If master, initiate TCP connection with opponent device
 * 4. Complete handshake transfer of information over TCP link
 * 5. Break TCP link with opponent
 * 6. If master, initiate UDP connection with opponent
 * 7. Begin send and receive communication threads
 * 
 * @author Matthew Woodacre
 */
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
	DatagramPacket packet;
	
	boolean noError = true;

	byte[] buf;

	GamePlayer player1, player2;

	
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
		buf = new byte[4];
		
		// Initial connection to main server
		try 
		{
			Log.d(TAG, "About to try and connect");
			
			socket1 = null;
			socket1 = new Socket("192.168.0.102", 7777);
			
		    if(socket1 == null)
		    {
		    	//createConnection = false;
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
			sen.connectionError = true;
			noError = false;
			
			e.printStackTrace();
			
			Log.d("ERROR", "Error in connection 1");
			
			//TODO: exit
		} 
		catch (IOException e) 
		{
			sen.connectionError = true;
			noError = false;
			
			e.printStackTrace();
			
			Log.d("ERROR", "Error in connection 2");
			
			//TODO: exit
		}  		
		
		if(noError == true)
		{		
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
				sen.connectionError = true;
				noError = false;
				
				e.printStackTrace();
				
				Log.d("ERROR", "Error in connection 3");
				
				//TODO: exit
			} 
			catch (IOException e) 
			{
				sen.connectionError = true;
				noError = false;
				
				e.printStackTrace();
				
				Log.d("ERROR", "Error in connection 4");
				
				//TODO: exit
			}
		}
		
		if(noError == true)
		{		
			// Connect devices with TCP
			try
			{
				if(isMaster == true)
				{
					Log.d(TAG, "Connect to slave IP: " + slaveIP);
					
					// Receive TCP connection from Master
					socket2 = null;
					socket2 = new ServerSocket(4444);
					
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
					socket1 = new Socket(masterIP, 4444);
					
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
				sen.connectionError = true;
				noError = false;
				
				e.printStackTrace();
				
				Log.d("ERROR", "Error in connection 5");
				
				//TODO: exit
			} 
			catch (IOException e) 
			{
				sen.connectionError = true;
				noError = false;
				
				e.printStackTrace();
				
				Log.d("ERROR", "Error in connection 6");
				
				//TODO: exit
			}
		}
		
		if(noError == true)
		{		
			// Connect devices with UDP
			try
			{
				if(isMaster == true)
				{
					Log.d(TAG, "About to set up Master UDP");
					
					socket4 = new DatagramSocket(4446);
												
					Log.d(TAG, "Master UDP socket created");
												
					packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(slaveIP), 4446);
					
					Log.d(TAG, "Master UDP packet created");
					
					// Start communication threads
									
					new sendPos(player1, socket4, packet, buf, true).start();
					
					new receivePos(player2, socket4, packet, surfaceHeight, surfaceWidth, surfaceHeight2, surfaceWidth2, buf).start();
					
					Log.d(TAG, "Master send/receive threads started");
				}
				else
				{
					Log.d(TAG, "About to set up Slave UDP");
					
					socket4 = new DatagramSocket(4446);
												
					Log.d(TAG, "Slave UDP socket created");
												
					packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(masterIP), 4446);
					
					Log.d(TAG, "Slave UDP packet created");
					
					// Start communication threads
					new sendPos(player1, socket4, packet, buf, false).start();
					
					new receivePos(player2, socket4, packet, surfaceHeight, surfaceWidth, surfaceHeight2, surfaceWidth2, buf).start();
					
					Log.d(TAG, "Slave send/receive threads started");
				}			
			}
			catch (UnknownHostException e) 
			{
				sen.connectionError = true;
				noError = false;
				
				e.printStackTrace();
				
				Log.d("ERROR", "Error in connection 5");
				
				//TODO: exit
			} 
			catch (IOException e) 
			{
				sen.connectionError = true;
				noError = false;
				
				e.printStackTrace();
				
				Log.d("ERROR", "Error in connection 6");
				
				//TODO: exit
			}
		}
		
		if(noError == true)
		{		
			while(sen.endThread == false)
			{
				
			}
		
			socket4.close();
		}
	}
}
