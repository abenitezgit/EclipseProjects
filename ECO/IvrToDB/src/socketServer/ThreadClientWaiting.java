/*
 * ThreadClientWaiting.java
 *
 * Created on October 29, 2009, 5:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package socketServer;

import ivrtodb.main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import log.LogRegister;


/**
 *
 * @author Juan Molina
 */


 public class ThreadClientWaiting {
	 
    private ServerSocket serverSocket = null;
    // public static ThreadClient objThreadClient = null;
    private boolean listening = true;
    // public static String MQHost;
    // public static String MQChannel;
    // public static String MQPort;
    public static String SocketPort;
    public ExecutorService exec;
    
    /** Creates a new instance of ThreadClientWaiting */
    public ThreadClientWaiting() throws InterruptedException 
    {

    	Socket socket = null;
        SocketPort = "50090"; //main.parametros.getValor("SocketPort", "58000");
        
        try 
        {
            exec = Executors.newFixedThreadPool(main.parametros.getIntValor("maxThreads",300));
        }
        catch (Exception e) 
        {
            LogRegister.out(LogRegister.DEBUG, " Could not create Executors Threads Pool: " + e.getMessage());
        }
        
        while (serverSocket == null) 
        {
        	try 
        	{
        		LogRegister.out(LogRegister.DEBUG, " Abriendo port " + SocketPort);
				serverSocket = new ServerSocket(Integer.parseInt(SocketPort));
			} 
        	catch (Exception e) 
        	{
				LogRegister.out(LogRegister.DEBUG, "  Could not listen on port " + SocketPort + " : " + e.getMessage());
			}
        	
        	if (serverSocket == null)
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					LogRegister.out(LogRegister.DEBUG, "  Espera Interrumpida: " + e.getMessage());
					e.printStackTrace();
				}
        }

        while (listening)
        {
            try 
            {
                LogRegister.out(LogRegister.DETAIL, "Esperando por un cliente TCP.");
                
                socket = serverSocket.accept();
                
                LogRegister.out(LogRegister.DETAIL, "Cliente conectado [" + socket.getInetAddress().toString() + "]");
                
                // objThreadClient = new ThreadClient(socket, "TCPClient");                
                // Runnable worker= objThreadClient;
                Runnable worker=null;
				worker = new ThreadClient(socket, "TCPClient");
                exec.execute(worker); 
                
            } 
            catch (IOException ex) 
            {
            	LogRegister.out(LogRegister.DEBUG, "I/O Exception esperando cliente TCP [" + ex.toString() + "]");
            	exec.shutdown(); // ????
            }
        }
        
        try 
        {
            LogRegister.out(LogRegister.FULL, "Se cerrar� el Server Socket, puerto: " + SocketPort);
            //Inicio.objConectionDB.CloseConection();
            exec.shutdown();
            serverSocket.close();
        }
        catch (IOException ex) 
        {
            LogRegister.out(LogRegister.DEBUG, "ERROR! " + ex.getMessage());
        }
    }

    
}