/*
 * ThreadClient.java
 *
 * Created on October 29, 2009, 5:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package socketServer;

import ivrtodb.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import log.LogRegister;



/**
 *
 * @author Joel Huenul
 * @author Manuel Reyes V.
 * @author Daniel Astudillo
 * @author Juan A. Cuevas P.
 */
public class ThreadClient implements Runnable 
{
    //ABT
    static Logger logger = Logger.getLogger("TreadClient");
	
	private String nombre;
	private String IP; 
	
    public String getNombreThread() 
    {
		return nombre;
	}

	public void setNombreThread(String nombreThread)
	{
		this.nombre = nombreThread;
	}

	public Socket objSocket = null;
    
    /** Creates a new instance of ThreadClient 
     * @throws InterruptedException */
    public ThreadClient(Socket socket, String name)  
    {
        this.objSocket = socket;
        
        this.IP = socket.getInetAddress().toString().replace("/","");
        
        this.setNombreThread(name + "_" + this.IP);

        LogRegister.out(LogRegister.DETAIL, "Nuevo cliente [" + this.getNombreThread() + "]");
        
        
    }
    
    public void run() 
    {     
     try 
     {

		    PrintWriter out   = new PrintWriter(this.objSocket.getOutputStream(), true);
		    BufferedReader in = new BufferedReader( new InputStreamReader(this.objSocket.getInputStream()));
		    String inputLine;
		    
		    Object outputLine = new Object();
		    
		    while ((inputLine = in.readLine()) != null) 
		    {
	    		LogRegister.out(LogRegister.DETAIL, "[" + this.IP + "] ThreadClient - Se recibió el siguiente mensaje: [" + inputLine + "]");
	    		
		    	outputLine = processInput(inputLine,this.IP);
		    	out.println(outputLine);
		    	
	                
		    	if ( outputLine != null )
		    	{
		    		LogRegister.out(LogRegister.DETAIL, "[" + this.IP + "] ThreadClient - Se envio el siguiente mensaje: [" + outputLine + "]");
	            }
		    	
			    break;
		    }
		    out.close();
		    in.close();
    		LogRegister.out(LogRegister.DETAIL, "[" + this.IP + "] ThreadClient - Cerrando conexion");
		    this.objSocket.close();
     } 
     catch (IOException e) 
     {
            LogRegister.out(LogRegister.DEBUG, "[" + this.getNombreThread() + "] ERROR! " + e.getMessage());
            e.printStackTrace();
     }
    }

    public void SendMessajeClient(String msjRequest) throws IOException
    {
       PrintWriter out = new PrintWriter(this.objSocket.getOutputStream(), true);
        out.println(msjRequest);
    }
    
    public Object processInput(String theInput, String IP)
    {
    	Object theOutput = new Object();
		String requestID = IP;
    	Map<Object, String> mapData = new HashMap<Object, String>();
    	
        long milisegundoActual;
    	long milisegundoFinal;
        
    	ClientRequest clientRequest = null;
    	Gson json = null;
    	
    	
    	try
    	{
    		if ( !theInput.contains("outParameters") )
    		{
    			theInput = theInput.substring(0, theInput.length()-1) + ", \"outParameters\":\"\"}";
    		}
    		
    		if ( !theInput.contains("dataTypes") )
    		{
    			theInput = theInput.substring(0, theInput.length()-1) + ", \"dataTypes\":\"\"}";
    		}
    		
	    	json = new Gson();
	    	clientRequest = new ClientRequest();
	    	clientRequest = json.fromJson(theInput, ClientRequest.class);
	    	
	    	logger.debug(json.toJson(clientRequest));
    	}
    	catch(Exception e)
    	{
        	LogRegister.out(LogRegister.DEBUG, "[" + requestID + "] ThreadClient.processInput - EXCEPTION converting to JSON: [" + e.getMessage() + "]"  );
        	
    		mapData.put("error", "-2");
        	mapData.put("errorMessage", e.getMessage());
        	    	
			return new Gson().toJson(mapData);       		
    	}
    	//Se modifica estructura de requestID para que log escriba campos IP y RequestID por separado, sin modificar uno por uno los logs
    	requestID = requestID.concat("] [" + clientRequest.getRequestID());
    	    	
        LogRegister.out(LogRegister.DETAIL, "[" + requestID + "] ThreadClient.processInput - Se recibe el siguiente mensaje: \"" + theInput + "\"");
        
    	try
    	{
            LogRegister.out(LogRegister.DETAIL, "[" + requestID + "] ThreadClient.processInput - Servicio [" + clientRequest.getServicio() + "][" + clientRequest.getQuery() + "]");
            
    		milisegundoActual = System.currentTimeMillis();

    		theOutput = main.catalogo.request(clientRequest, requestID);
    		
    		if(theOutput.equals(""))
    		{
    			mapData.put("error", "-3");
            	mapData.put("errorMessage", "No answer for main.catalogo.request");
    			theOutput = new Gson().toJson(mapData);
    		}
    		
    		milisegundoFinal = System.currentTimeMillis();
            LogRegister.out(LogRegister.DETAIL, "[" + requestID + "] ThreadClient.processInput - Servicio [" + clientRequest.getServicio() + "] - Duraci�n: " + (milisegundoFinal - milisegundoActual) + "ms - Resultado: [" + theOutput + "]");
    		
    	}
    	catch (InterruptedException e) 
    	{
        	LogRegister.out(LogRegister.DEBUG, "[" + requestID + "] ThreadClient.processInput - INTERRUPTED EXCEPTION: [" + e.getMessage() + "]"  );
        	
    		mapData.put("error", "-1");
        	mapData.put("errorMessage", e.getMessage());
        	    	
			return new Gson().toJson(mapData);
		}
    	catch (Exception e) 
    	{
        	LogRegister.out(LogRegister.DEBUG, "[" + requestID + "] ThreadClient.processInput - EXCEPTION: [" + e.getMessage() + "]"  );
        	
    		mapData.put("error", "-2");
        	mapData.put("errorMessage", e.getMessage());
        	    	
			return new Gson().toJson(mapData);        		
    	}        	        
        return (theOutput);
     }
}