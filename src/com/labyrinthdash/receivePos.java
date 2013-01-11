package com.labyrinthdash;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

import android.util.Log;

/**
 * Receive opponent positions from opponent device over
 * UDP link
 * 
 * @author Matthew
 */
class receivePos extends Thread
{
	GamePlayer player;
	DatagramSocket socket = null;
	DatagramPacket packet;
	byte[] buf, buf2;
	ByteBuffer b;
	int tempPosX, tempPosY = 0;
	int surfaceHeight, surfaceWidth = 0;
	int surfaceHeight2, surfaceWidth2 = 0;
	int as0, as1, as2 = 0;
	int tempAs = 0;
	int asteroidSize;
	
	private static final String TAG = "receivePos";
	
	public receivePos(GamePlayer player, DatagramSocket socket, DatagramPacket packet, int surfaceHeight, 
						int surfaceWidth, int surfaceHeight2, int surfaceWidth2, byte[] buf)
	{
		this.player = player;
		this.socket = socket;
		this.packet = packet;	
		
		this.surfaceHeight = surfaceHeight;
		this.surfaceWidth = surfaceWidth;
		
		this.surfaceHeight2 = surfaceHeight2;
		this.surfaceWidth2 = surfaceWidth2;
		
		this.buf = buf;
	}
	
	public void run()
	{
		buf2 = new byte[4];
		
		while(sen.endThread == false)
		{
			//Log.d(TAG, "Awaiting Position Packet");
			
			try 
			{
				socket.receive(packet);
			} 
			catch (IOException e1) 
			{
				e1.printStackTrace();
			}
			
			// Get X
			tempPosX = 0;
				
		    if(buf[3] != 0)
		    {
		        if(buf[3] < 0)
		        {
		            tempPosX = (buf[3] + 256);
		        }
		        else
		        {
		            tempPosX = (buf[3] * 1);
		        }
		    }
		
		    if(buf[2] != 0)
		    {
		        if(buf[2] < 0)
		        {
		            tempPosX += ((buf[2] + 256) * 256);
		        }
		        else
		        {
		            tempPosX += (buf[2] * 256);
		        }
		    }
		
		    // Get Y	
		    tempPosY = 0;
		
		    if(buf[1] != 0)
		    {
		        if(buf[1] < 0)
		        {
		            tempPosY = (buf[1] + 256);
		        }
		        else
		        {
		            tempPosY = (buf[1] * 1);
		        }
		    }
		
		    if(buf[0] != 0)
		    {
		        if(buf[0] < 0)
		        {
		            tempPosY += ((buf[0] + 256) * 256);
		        }
		        else
		        {
		            tempPosY += (buf[0] * 256);
		        }
		    }
		        	
		    //Log.d(TAG, "Received (x,y): (" + tempPosX + "," + tempPosY + ")");
		        
		    tempPosX = (surfaceHeight * tempPosX) / surfaceWidth2;
		    tempPosY = (surfaceWidth * tempPosY) / surfaceHeight2;
		        
		    //Log.d(TAG, "Translated (x,y): (" + tempPosX + "," + tempPosY + ")");
		      					 
		    sen.receiveX = tempPosX;
		    sen.receiveY = tempPosY;
		    
		    // player.setX(tempPosX);
		    //player.setY(tempPosY);
		        
			try
			{
				Thread.sleep(40);
			}
			catch (Exception e)
			{
				Log.d(TAG, "Thread sleep fail receivePos");
			}
		}
		
		//sen.endThread2 = false;
	}	
}