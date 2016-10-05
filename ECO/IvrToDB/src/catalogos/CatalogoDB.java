package catalogos;

import ivrtodb.main;

import java.util.Map;
import java.util.HashMap;
import socketServer.ClientRequest;
import com.google.gson.Gson;
//import com.sun.media.jfxmedia.logging.Logger;
import org.apache.log4j.Logger;

import log.LogRegister;

public class CatalogoDB
{	
    //ABT
    static Logger logger = Logger.getLogger("Catalog");
    
    private static Map<String, Object> connectionsPools = new HashMap<String, Object>();
	
	public CatalogoDB()
	{
	}

	public synchronized Object request(ClientRequest request, String requestID) throws Exception 
	{
		Map<String, String> mapRC = new HashMap<String, String>();
		String dsServicio = request.getServicio();
		Object retorno = null;

		try
		{					

			//Permite que el parametro no venga en el request y lo asume como DB
			if (request.getConnectionType()==null || request.getConnectionType().equals("")) {
				request.setConnectionType("DB");
			}
		
			switch (request.getConnectionType()) {
				case "REST":
		        	logger.info("[" + requestID + "] CatalogoDB.request - Creando RestConnection : [" + request.getServicio() + "]");
		        	 
		        	String dsResource          	= request.getRestResource();
		        	String dsRestType 			= request.getRestType();
		        	String dsParams 			= request.getParameters();
		        	String dsURLBase 			= main.parametros.getValor(dsServicio + "_urlBase");
		        	String dsTimeOut 			= main.parametros.getValor(dsServicio + "_timeout");

		        	logger.info("[" + requestID + "] CatalogoDB.request - Ejecutando REST API: [" 
		        					+ dsURLBase + "][" 
		        					+ dsResource + "][" 
		        					+ dsParams + "]"); 
		        	
		        	
		        	RestConnectionPool restConnection = new RestConnectionPool(dsURLBase, dsResource, dsRestType, dsParams, dsTimeOut);
					
		        	retorno = restConnection.execute();
		        	
					return retorno;
				case "DB":
					DBConnectionPool dbConnectionPool = null;
			    
					dbConnectionPool = (DBConnectionPool) connectionsPools.get(request.getServicio());
					
					logger.debug("dbConnectionPool: "+request.getServicio());
					
					if (dbConnectionPool == null)
					{
			        	LogRegister.out(LogRegister.DEBUG, "[" + requestID + "] CatalogoDB.request - Creando DBConnectionPool : [" + request.getServicio() + "][" + request.getQuery() + "]");
			        	 
			        	String dsClassName 			= main.parametros.getValor(dsServicio + "_classname");
			        	String dsHost 				= main.parametros.getValor(dsServicio + "_host");
			        	String dsPort 				= main.parametros.getValor(dsServicio + "_port");
			        	String dsDBName 			= main.parametros.getValor(dsServicio + "_dbname");
			        	String dsUser 				= main.parametros.getValor(dsServicio + "_user");
			        	String dsPassword 			= main.parametros.getValor(dsServicio + "_pwd");
			        	String dsURL 				= main.parametros.getValor(dsServicio + "_url");	        	
			        	String dsValidationQuery 	= main.parametros.getValor(dsServicio + "_validationQuery","");
			        	boolean dsValidateObjetcs 	= Boolean.parseBoolean(main.parametros.getValor(dsServicio + "_validateConnections", "false"));	        	
			        	int dsMaxConn 				= main.parametros.getIntValor(dsServicio + "_maxconnections", 5);
			        	int dsTimeout 				= main.parametros.getIntValor(dsServicio + "_timeout", 1);
			        	int dsTimeCache 			= main.parametros.getIntValor(dsServicio + "_timecache", 60);
			        	
			        	dbConnectionPool = new DBConnectionPool(
			        			                                dsServicio, dsClassName, dsHost, dsPort, dsDBName, dsUser, dsPassword, 
			        											dsValidateObjetcs, dsValidationQuery, dsURL, dsMaxConn, dsTimeout, dsTimeCache
			        										   );
			        	
			        	connectionsPools.put(dsServicio, dbConnectionPool);
					}
					
		        	LogRegister.out(LogRegister.DETAIL, "[" + requestID + "] CatalogoDB.request - sentencia SQL: [" + request.getServicio() + "][" + request.getQuery() + "][" + request.getParameters() + "][" + request.getOutParameters() + "][" + request.getDataTypes() + "]");
		        			        	
		        	if (main.parametros.getValor(dsServicio+"_async", "NO").equalsIgnoreCase("YES")) 
		        	{
		        		retorno=main.queue.addTask(dbConnectionPool, request, requestID);        		        	
		        		return retorno;
		        	}
		        	
		        	// JCuevas - 20160105 - Esta distincion de un servicio SQLite NO deberia hacerse aqui. Deberia hacerse en DBConnection.
		        	if (main.parametros.getValor(dsServicio + "_classname").contains("sqlite"))
		        		retorno = dbConnectionPool.executeSQLite(request.getQuery(), request.getParameters(), request.getOutParameters(), request.getDataTypes(), requestID);        	
		        	else
		        		retorno = dbConnectionPool.execute(request.getQuery(), request.getParameters(), request.getOutParameters(), request.getDataTypes(), String.valueOf(request.getSelect()), requestID);        	
		        	
		            return retorno;
				default:
					String message = "ConnectionType no definido en initFile";
		        	LogRegister.out(LogRegister.DEBUG, "[" + requestID + "] CatalogoDB.request - CRITICAL EXCEPTION: ["+message+"]");
		        	
		        	mapRC.put("errorMessage", message);
		        	mapRC.put("MSG", message);
		        	mapRC.put("RC", "-97");
		        	    	
					return new Gson().toJson(mapRC);
				}
		}
        catch(Exception e)
        {
        	LogRegister.out(LogRegister.DEBUG, "[" + requestID + "] CatalogoDB.request - CRITICAL EXCEPTION: [" + e.toString() + "]"  );
        	LogRegister.out(LogRegister.DEBUG, "[" + requestID + "] CatalogoDB.request - CRITICAL EXCEPTION: [" + e.getMessage() + "]"  );
        	
        	mapRC.put("errorMessage", e.getMessage());
        	mapRC.put("MSG", e.getMessage());
        	mapRC.put("RC", "-98");
        	    	
			return new Gson().toJson(mapRC);
        }
	}
}