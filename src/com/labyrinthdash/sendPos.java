package com.labyrinthdash;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

import android.util.Log;

class sendPos extends Thread
{
	GamePlayer player;
	DatagramSocket socket = null;
	DatagramPacket packet;
	byte[] buf, buf2;
	ByteBuffer b;
	String address;
	boolean isMaster;
	int asteroidPos;
	
	private static final String TAG = "sendPos";
	
	public sendPos(GamePlayer player, DatagramSocket socket, DatagramPacket packet, byte[] buf, boolean isMaster)
	{
		this.player = player;
		this.socket = socket;
		this.packet = packet;
		this.buf = buf;
		this.address = address;
		this.isMaster = isMaster;
	}
	
	public void run()
	{
		buf2 = new byte[2];
		
		while(sen.endThread == false)
		{
			// Set Y
			buf2 = new byte[4];         // MAY BE UNNECESSARY
			b = ByteBuffer.allocate(4);
			b.putDouble(player.getY());
			buf2 = b.array();
			
			buf[0] = buf2[2];
			buf[1] = buf2[3];
				
			// Set X
			buf2 = new byte[4];
			b = ByteBuffer.allocate(4);			
			b.putDouble(player.getX());			
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