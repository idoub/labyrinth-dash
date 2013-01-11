import java.net.*;
import java.io.*;
 
public class gameServer
{
    static String masterIP = null;
    static String slaveIP = null;
	
    public static void main(String[] args) throws IOException 
    {
        ServerSocket serverSocket = null;
        boolean listening = true;

        gameServer serve = new gameServer();
        
        System.out.println("\nMultiple connection handling server started\n");

        try 
        {
            serverSocket = new ServerSocket(22);
            System.out.println(Inet4Address.getLocalHost().getHostAddress());
        } 
        catch (IOException e) 
        {
            System.err.println("Could not listen on port: 22.");
            System.exit(-1);
        }
 
        System.out.println("Listening for connections");

        while(listening)
        {
        	// Get master device
        	serve.getIpAddress(serverSocket.accept(), true);    	
        	
        	// Get slave device
        	serve.getIpAddress(serverSocket.accept(), false);    
        	
        	// Spawn thread to connect devices
        	new spawnConnection(masterIP, slaveIP).start();
        	
        }
        serverSocket.close();
    }
    
    public void getIpAddress(Socket socket, boolean isMaster)
    {
   		InetAddress address = socket.getInetAddress();
        String addressString = address.toString();
        
        if(isMaster)
        {
        	masterIP = addressString.replace("/", "");
        	
         	System.out.println("Master Address: " + masterIP);
        }
        else
        {
        	slaveIP = addressString.replace("/", "");
        	
         	System.out.println("Slave Address: " + slaveIP);
        }  	
    }    
}
