package com.labyrinthdash;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import android.util.Log;


public class InitialConnectMulti extends Thread
{
	//Socket connection
	Socket socket = null;
	DataOutputStream dataOutputStream = null;
	DataInputStream dataInputStream = null;
	String s, address;
	boolean createConnection = false;
	GamePlayer player1, player2;
	ConnectionInfo connectInfo;
	
	// MultiPlayers
	ArrayList<String> playerNames;
	int numberPlayers = 0;
	String opponentPlayerName;
	
	int surfaceHeight, surfaceWidth, surfaceHeight2, surfaceWidth2 = 0;	
	int opponentNumber;
	String playerName;
	String opponentName;
	
	private static final String TAG = "InitialConnect";
	
	public InitialConnectMulti(GamePlayer player1, GamePlayer player2, int surfaceHeight, int surfaceWidth, 
									String playerName, ConnectionInfo connectInfo)
	{
		this.player1 = player1;
		this.player2 = player2;
		
		this.surfaceHeight = surfaceHeight;
		this.surfaceWidth = surfaceWidth;	
		
		this.playerName = playerName;
		this.connectInfo = connectInfo;
	}
	
	public void run()
	{
		// Connect to main server
		try 
		{
			Log.d(TAG, "About to try and connect");
			
			socket = null;
			socket = new Socket("192.168.0.102", 8889);
			
		    if(socket == null)
		    {
		    	createConnection = false;
		    	Log.d(TAG, "Socket Fail");
		    	//TODO: exit
		    }
		    else
		    {			    
			    Log.d(TAG, "Socket Created");
			    
			    // TCP connection (integer/double)
			    dataOutputStream = new DataOutputStream(socket.getOutputStream());
				dataInputStream = new DataInputStream(socket.getInputStream());
							
				Log.d(TAG, "Data streams created");
				
				// Handshake
				
				// Send screen info
				dataOutputStream.writeInt(surfaceHeight);
				dataOutputStream.writeInt(surfaceWidth);
				
				// Send location
				dataOutputStream.writeDouble(sen.longitude);
				dataOutputStream.writeDouble(sen.latitude);
				
				// Send name
				dataOutputStream.writeUTF(playerName);
				
				// Get number players
				numberPlayers = dataInputStream.readInt();
				
				playerNames = new ArrayList<String>();
				
				Log.d(TAG, "Names");
				
				for(int i = 0; i < numberPlayers; i++)
				{
					opponentPlayerName = dataInputStream.readUTF();
					
					Log.d(TAG, i + " " + opponentPlayerName);
					
					playerNames.add(opponentPlayerName);
				}				
				
								
				// Spawn request connection thread
				/*new AwaitConnect(dataOutputStream, dataInputStream, connectInfo, surfaceHeight, surfaceWidth, player1, player2).start();
				
				// Wait to receive connection
				while(connectInfo.getAcceptRequest() == false)
				{
					// Wait for request - only listening socket
					opponentName = dataInputStream.readUTF();
					
					Log.d(TAG, "Request from: " + opponentName);
					
					// Reset result
					connectInfo.setResponse(0);
					
					// Check to see if opponent accepted
					if(opponentName.contentEquals("ConnectionAccepted"))
					{
						connectInfo.setResponse(1);						
					}
					else if(opponentName.contentEquals("ConnectionRejection"))
					{
						connectInfo.setResponse(2);
					}
					// There is a request to play from someone
					else
					{
						Log.d(TAG, "Request to play from " + opponentName);
						
						//TODO: Check is okay with user
						
						
						
						// Need to tell server that is okay
						dataOutputStream.writeInt(69);
						
						Log.d(TAG, "Verified to server: device accepted request");
						
						// See if that is okay then accept
						connectInfo.setAcceptRequest(true);
						
						// You are slave - you accepted request
						connectInfo.setIsMaster(false);
					}					
				}*/

				// All control is now in the AwaitConnect Thread :)
								
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

	}
}
