import java.net.*;
import java.io.*;
 
public class spawnConnection extends Thread 
{
    private Socket socket, socket2 = null;

    String masterIP, slaveIP;
 
    public spawnConnection(String masterIP, String slaveIP) 
    {
        super("serverThreadConnect");

        this.masterIP = masterIP;
        this.slaveIP = slaveIP;
        
    }
 
    public void run() 
    { 
        try 
        {
            System.out.println("Connection thread spawned");

            // Connect to Master
            socket = new Socket(masterIP, 9999);
            
            System.out.println("Connection made to master");
            
            InputStream in = socket.getInputStream();
            DataInputStream input = new DataInputStream(in);

            OutputStream out = socket.getOutputStream();
            DataOutputStream output = new DataOutputStream(out);
            
            // Tell device they are master
            output.writeUTF("Master");
            
            // Tell device slave's IP
            output.writeUTF(slaveIP);
            
            // Close sockets
            socket.close();
            output.close();
            input.close();
            
            // Connect to Slave
            socket2 = new Socket(slaveIP, 9999);
            
            System.out.println("Connection made to slave");
            
            InputStream in2 = socket2.getInputStream();
            DataInputStream input2 = new DataInputStream(in2);

            OutputStream out2 = socket2.getOutputStream();
            DataOutputStream output2 = new DataOutputStream(out2);
            
            // Tell device they are master
            output2.writeUTF("Slave");
            
            // Tell device slave's IP
            output2.writeUTF(masterIP);
            
            // Close sockets
            socket2.close();
            output2.close();
            input2.close();
            

            //System.out.println("Connection closed from client " + connectionNumber); 
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }

        System.out.println("\nConnection closed from clients"); 
    }
}