package com.labyrinthdash;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

import android.util.Log;

/**
 * Send player positions to opponent device over
 * UDP link
 * 
 * @author Matthew
 */
class sendPos extends Thread
{
	GamePlayer player;
	DatagramSocket socket = null;
	DatagramPacket packet;
	byte[] buf, buf2;
	ByteBuffer b;
	boolean isMaster;
	int asteroidPos;
	int inX, inY = 0;
	double inX2, inY2 = 0;
	
	private static final String TAG = "sendPos";
	
	public sendPos(GamePlayer player, DatagramSocket socket, DatagramPacket packet, byte[] buf, boolean isMaster)
	{
		this.player = player;
		this.socket = socket;
		this.packet = packet;
		this.buf = buf;
		this.isMaster = isMaster;
	}
	
	public void run()
	{
		buf2 = new byte[2];
		
		while(sen.endThread == false)
		{		
			// Set Y
			buf2 = new byte[4];
			b = ByteBuffer.allocate(4);
			
			float IsaacY = 89;
			
			inY = (int)IsaacY;
			
			b.putInt(inY);
			buf2 = b.array();
			
			buf[0] = buf2[2];
			buf[1] = buf2[3];
				
			// Set X
			buf2 = new byte[4];
			b = ByteBuffer.allocate(4);	
			
			float IsaacX = 118;
			
			inX = (int)IsaacX;	
			b.putInt(inX);
			buf2 = b.array();	
				
			buf[2] = buf2[2];
			buf[3] = buf2[3];
											
			//Log.d(TAG, "Sending: (" + player.getX() + "," + player.getY() + ")");
			
			try 
			{
				socket.send(packet);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
			
		try
		{
			Thread.sleep(50);
		}
		catch (Exception e)
		{
			Log.d(TAG, "Thread sleep fail sendPos");
		}
	}
		
	//sen.endThread = false;
	
	
}