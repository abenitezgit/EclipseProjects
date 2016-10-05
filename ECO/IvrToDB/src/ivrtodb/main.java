/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ivrtodb;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import queue.ThreadQueue;
import socketServer.ThreadClientWaiting;
import catalogos.CatalogoDB;
import ivrtodb.iniFile;
 
import org.apache.commons.cli.*;		// PI for parsing command line options: http://commons.apache.org/proper/commons-cli/index.html
import org.apache.log4j.Logger;

import com.google.gson.Gson;


/**
 * 
 * @author Manuel Reyes V.
 * @author Daniel Astudillo
 * @author Juan A. Cuevas P.
 * @author Laura Duhalde
 * @author Juan Molina Vera
 * @author Cristian Rojas
 * @author Andres Benitez
 */

public class main 
{    
    public static CatalogoDB catalogo;        
    public static iniFile parametros = null;
    public static String VersionApp = "3.3.0";
    public static String iniFileName = "/opt/e-contact/ivrtodb/ivrtodb.ini";
           
    public static long iTaskID=0;   
    public static ThreadQueue queue  = new ThreadQueue();
    
    //Add Log4j
    static Logger logger = Logger.getLogger("ivrToDB");
    
    public static void main(String[] args) 
    {    	
    	
    	Logger.getRootLogger().setLevel(org.apache.log4j.Level.DEBUG);
    	
    	Map<String,String> map = new HashMap<>();
    	
    	map.put("servicio", "httpLocal");
    	map.put("connectionType", "REST");
    	map.put("requestID", "1234");
    	map.put("select", "1");
    	map.put("parameters", "1");
    	map.put("restType", "GET");
    	map.put("outParameters", "salida");
    	map.put("query", "API");
    	map.put("restResource", "getGrabMuestra/AA");
    	
    	Gson jo = new Gson();
    	logger.debug(jo.toJson(map));
    	
    	
    	
    	/**
    	 * Define el nombre del archivo .ini que leera.
    	 * Lo carga desde el absolutePath(), desde el directorio donde se ejecuta la aplicacion
    	 */
    	File f = new File("ivrtodb.ini");
    	iniFileName = f.getAbsolutePath();

    	
    	/**
    	 * Analiza Parametros de la linea de comando
    	 */
    	logger.info("Analizando parametros de entrada");
    	
    	CommandLineParser cmdLineParser = new DefaultParser();
    	CommandLine cmdLine;
    	Options cmdLineOptions = new Options();
    	
    	cmdLineOptions.addOption("f", "config-file", true, "Nombre y ubicacion del archivo de configuracion.");
    	cmdLineOptions.addOption("h", "help", false, "Help.");
    	
    	try 
    	{
			cmdLine = cmdLineParser.parse(cmdLineOptions, args);

			if(cmdLine.hasOption("h")) 
			{
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "ivrtodb-v2", cmdLineOptions );				
				System.exit(0);
			}
			
			if(cmdLine.hasOption("f")) 
			{
				iniFileName = cmdLine.getOptionValue("f");
			}

		} 
    	catch (ParseException e) 
    	{
    		//System.err.println ( "Parsing failed.  Reason: " + e.getMessage() );
    		logger.error("Parsing failed.  Reason: " + e.getMessage());
		}
    	
    	
    	
        // Inicio.leerParametros();
        parametros = new iniFile(iniFileName);       
        //LogRegister.out(LogRegister.DEBUG, "--- Inicio IvrToDB v" + VersionApp + " --------------------------------- ");
        logger.info("--- Inicio IvrToDB v" + VersionApp + " --------------------------------- ");
       
        try 
        {        	 
             catalogo = new CatalogoDB();
             logger.debug("Se instancia CatalogDB");
        } 
        catch (Exception ex) {
        	 //ex.printStackTrace();
        	 logger.error("Error instanciando CatalogoDB()..."+ex.getMessage());
        }

        // Construir Cola de Trabajo. ******
        ExecutorService exec=null;
        try 
        {
            exec = Executors.newFixedThreadPool(main.parametros.getIntValor("maxQueues",1));
            logger.debug("Inicia ExecutorService");
        }
        catch (Exception e) 
        {
            //LogRegister.out(LogRegister.DEBUG, " [main] Could not create Executors Threads Queues: " + e.getMessage());
            logger.error(" [main] Could not create Executors Threads Queues: " + e.getMessage());
        }
        
        
        Runnable worker = queue;
        exec.execute(worker);
        
        // Thread para cache
        logger.debug("Iniciando Cache");
        ThreadManageCache cache = new ThreadManageCache();
        cache.start();
        
        // Socket Server

			try {
				ThreadClientWaiting objThreadClientWaiting = new ThreadClientWaiting();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				logger.error("Error instanciando ThreadClientWaiting..."+e.getMessage());
			}

        
        //LogRegister.out(LogRegister.DEBUG, "Thread objThreadClientWaiting iniciado.");   
        logger.info("Thread objThreadClientWaiting iniciado.");
        
    }
       
}
