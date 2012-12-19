package com.labyrinthdash;

class ConnectionInfo
{
	private boolean acceptRequest;
	private boolean sendRequest;
	private boolean isMaster;
	private int awaitResponse = 0;
		
	public ConnectionInfo(boolean acceptRequest, boolean sendRequest, boolean isMaster)
	{
		this.acceptRequest = acceptRequest;
		this.sendRequest = sendRequest;
		this.isMaster = isMaster;
	}	
	
	public boolean getAcceptRequest()
	{
		return acceptRequest;
	}
	
	public void setAcceptRequest(boolean state)
	{
		acceptRequest = state;
	}
	
	public boolean getSendRequest()
	{
		return sendRequest;
	}
	
	public void setSendRequest(boolean state)
	{
		sendRequest = state;
	}
	
	public boolean getIsMaster()
	{
		return isMaster;
	}
	
	public void setIsMaster(boolean state)
	{
		isMaster = state;
	}
	
	public void setResponse(int state)
	{
		awaitResponse = state;
	}
	
	public int getResponse()
	{
		return awaitResponse;
	}
	
}

