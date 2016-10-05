package catalogos;

import sqllite.*;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ivrtodb.ThreadManageCache;
import ivrtodb.main;
import oracle.jdbc.OracleTypes;

// import org.apache.commons.BasicDataSource;		// http://commons.apache.org/proper/commons-dbcp/api-1.2.2/org/apache/commons/dbcp/BasicDataSource.html
import org.apache.commons.dbcp2.BasicDataSource;	// http://commons.apache.org/proper/commons-dbcp/
													// dbcp depende de http://commons.apache.org/proper/commons-pool/
													// y pool depende de http://commons.apache.org/proper/commons-collections/
													// y de https://commons.apache.org/proper/commons-logging/
import org.omg.CORBA.Request;

import log.LogRegister;


@SuppressWarnings("unused")
public class DBConnectionPool 
{
	private BasicDataSource basicDataSource;
	private String servicio;		
	private int timeout;
	private int timeCache;
		
	public DBConnectionPool(
							String dsServicio, String dsClassName, String dsHost, String dsPort, String dsDBName, String dsUser, String dsPassword,
							boolean dsValidateObjets, String dsValidationQuery, String dsURL, int dsMaxConn, int dsTimeout, int dsCache
							) 
	{
		servicio  = dsServicio;
		timeout   = dsTimeout;
		timeCache = dsCache;
		
	    LogRegister.out(LogRegister.DEBUG, "Creando DBConnectionPool [" + dsServicio + "][" +  dsClassName + "][" + dsHost + ":" + dsPort + "][" + dsDBName + "][" + dsUser + "][" + dsURL + "]" );
	    
		basicDataSource = setupDataSource(dsClassName, dsHost, dsPort, dsDBName, dsUser, dsPassword, dsValidateObjets, dsValidationQuery, dsURL, dsMaxConn);
		
	    LogRegister.out(LogRegister.DEBUG, "DBConnectionPool Creado [" + dsServicio +"] creado con " + basicDataSource.getInitialSize() + " conexiones.");	      
	}
		
	/**	 
	 * Inicializacion de DataSource
	 */
    private BasicDataSource setupDataSource(
											String dsClassName, String dsHost, String dsPort, String dsDBName, 
											String dsUser, String dsPassword, boolean dsValidateObjects, String dsValidationQuery, String dsURL, int dsMaxConn) 
	{
	    LogRegister.out(LogRegister.DEBUG, "setupDataSource [" + dsClassName + "][" + dsHost + ":" + dsPort + "][" + dsDBName + "][" + dsUser + "][" + dsURL + "]" );
	    BasicDataSource bDS = null;
	    
	    try
	    {
	    	bDS = new BasicDataSource();
	    	
	    	bDS.setDriverClassName(dsClassName);
	    	bDS.setUrl(dsURL);
	    	bDS.setUsername(dsUser);
	    	bDS.setPassword(dsPassword);	    	
	    	bDS.setMaxWaitMillis(1000);
	    				    
	    	bDS.setInitialSize(dsMaxConn);		    
		    bDS.setMaxIdle(dsMaxConn);
		    
	    	if( dsValidateObjects && !dsValidationQuery.equals("") )
	    	{
	    		bDS.setValidationQuery(dsValidationQuery);
	    		bDS.setValidationQueryTimeout(60);
	    		
	    		bDS.setTestOnBorrow(true);
	    		bDS.setTestWhileIdle(true);
	    		bDS.setTestOnReturn(true);
	    		bDS.setRemoveAbandonedOnBorrow(true);
	    		bDS.setRemoveAbandonedOnMaintenance(true);
	    		
	    		bDS.setRemoveAbandonedTimeout(300);				//cada 5 minutos
	    		bDS.setMinEvictableIdleTimeMillis(300000);		
	    		bDS.setTimeBetweenEvictionRunsMillis(300000);
	    	}
	    	else
	    	{
	    		bDS.setTestOnBorrow(false);
	    		bDS.setTestWhileIdle(false);
	    		bDS.setTestOnReturn(false);
	    		bDS.setRemoveAbandonedOnBorrow(false);
	    		bDS.setRemoveAbandonedOnMaintenance(false);
	    	}
	    	
	    	LogRegister.out(LogRegister.DETAIL, "setupDataSource : setDriverClassName [" + dsClassName + "][");
	    	LogRegister.out(LogRegister.DETAIL, "setupDataSource : setUsername +  setPassword [" + dsUser + "]" );
	    	LogRegister.out(LogRegister.DETAIL, "setupDataSource : setUrl + setInitialSize [" + dsURL + "]" );
	    	LogRegister.out(LogRegister.DETAIL, "setupDataSource : Num. Idle Connections [" + String.valueOf(bDS.getNumIdle()) +"]" );
	    	LogRegister.out(LogRegister.DETAIL, "setupDataSource : Num. Active Connections [" + String.valueOf(bDS.getNumActive()) +"]" );
	    }
        catch(Exception ex)
        {
        	LogRegister.out(LogRegister.DEBUG, "CRITICAL: "+ "setupDataSource - CRITICAL ERROR: [" + ex.getMessage() + "]"  );
        }		
	    return bDS; 		
	}
	
	protected String creaQuery(String strQuery, Object[] parameters) 
	{		
		String strquery = "{CALL " + strQuery + "(";
		
		for (int i = 0; i < parameters.length; i++)
		{
			strquery += (i == 0 ? "?" : ",?");
		}
		strquery += ")}";
		
		return strquery;
	}
	
	protected String creaFunctionQuery(String strQuery, Object[] parameters) 
	{		
		String strquery = "{? = call " + strQuery + "(";
		
		for (int i = 0; i < parameters.length; i++) 
		{
			strquery += (i == 0 ? "?" : ",?");
		}
		
		strquery += ")}";
		
		return strquery;
	}	
	
	public synchronized Object execute(String query, String parameters, String outParameters, String dataTypes, String QueryType, String requestID)
	{
		int 	auxFunction = 0;
		String 	result 		= "";		
		String 	fechaF 		= "";
		String 	fechaI 		= LogRegister.getFechaHora("yyyy-MM-dd HH:mm:ss:SSS");
		boolean tieneOutParameters = false;
		
		Connection 			conn 	= null;
	    CallableStatement   cs   	= null;
	    ResultSet 			rs	 	= null;
	    String 				strSQL 	= null;

		Map<Object, String> mapError = new HashMap<Object, String>();
		Map<String, Object> mapResultado = null;		
		List<Map<String, Object>> lista = null;
		
        try 
        {
        	/*DETERMINANDO SI TIENE PARAMETROS DE ENTRADAS Y SALIDAS*/
        	String auxAllParameters = parameters;
        	if (!outParameters.equals(null) && !outParameters.equals("")) 
        	{
        		if (parameters.length() == (parameters.lastIndexOf("|")+1) ) 
        		{
        			auxAllParameters += outParameters;
        		}
        		else
        		{
        			auxAllParameters += "|"+outParameters;
        		} 
        		
        		tieneOutParameters = true;
        	}
        	
        	Object[] allParamList = auxAllParameters.equals("") ? null : auxAllParameters.split("\\|",-1); // doesn't discard empty tokens at the end.
        	Object[] paramList = parameters.equals("") ? null : parameters.split("\\|", -1);     
    		Object[] outParamList = outParameters.equals("") ? null : outParameters.split("\\|", -1);   
                		
    		// valida si en el query viene definido si es funcion y/o si el resultado se almacena en cache..
    		// Ejemplo: query = "function|cache|sp_query";
    		String arrayQuery[] = query.split("\\|");
    		boolean	isFunction	= false;
    		boolean	toCache	= false;
    		
    		for (int i = 0; i < arrayQuery.length; i++) 
    		{
    			if (arrayQuery[i].equals("function")) 
    			{
    				isFunction	= true;
    			}
    			
    			if (arrayQuery[i].equals("cache")) 
    			{
    				toCache	= true;
    			}
    			
    			if (!arrayQuery[i].equals("function")&&!arrayQuery[i].equals("cache")) 
    			{
    				query = arrayQuery[i];
    			}
    		}
    		////////////////////////////////////////////////////
    		
    		/*APLICA SOLO PARA ORACLE*/
            /*SI ES UNA FUNCION DE UN SOLO RETORNO*/
    		
    		if (isFunction)
    		{
    			if (allParamList != null)
    			{
            		strSQL = creaFunctionQuery(query, allParamList);
            		auxFunction = 1;
    			}
    			else
        			strSQL = query; 
    		}
    		else
    		{
    			if (allParamList != null)
            		strSQL = creaQuery(query, allParamList);
        		else
        			strSQL = query;   
    		}
    		
    		LogRegister.out(LogRegister.FULL, "[" + requestID + "] QUERY " + strSQL);
            conn = this.basicDataSource.getConnection();           
            
            cs = conn.prepareCall(strSQL);
            cs.setQueryTimeout(timeout);
            
            /*APLICA SOLO PARA ORACLE*/
            /*SI ES UNA FUNCION DE UN SOLO RETORNO*/
            if (auxFunction == 1) 
            {
            	cs.registerOutParameter(1, OracleTypes.VARCHAR); 
            }

            //VARIABLE PARA DETERMINAR DESDE QUE POSICION ESTAN DEFINIDOS LOS TIPOS DE DATOS DE SALIDAS
            int tiposOut = 0;
            
            /*REGISTRANDO PARAMETROS DE ENTRADA*/
            if (paramList != null)
            {            	
            	if (dataTypes.equals(""))
            	{
            		for (int i = 0; i < paramList.length; i++) 
            		{
                		cs.setString(i+1, paramList[i].toString());
                		LogRegister.out(LogRegister.FULL, "[" + requestID + "] Parametros " + String.valueOf(i+1) + " = \"" + paramList[i].toString() + "\"");
                	}
            	}
            	else
            	{
            		boolean valorNulo = false;
            		String[] tipos = dataTypes.split("\\|");
            		tiposOut = paramList.length;
            	
            		for (int i = 0; i < paramList.length; i++) 
            		{ 
            			int posIn = i+1+auxFunction;
            			LogRegister.out(LogRegister.FULL, "[" + requestID + "] Parametros ("+tipos[i]+") " + String.valueOf(posIn) + " = \"" + paramList[i].toString() + "\"");
            		
            			if (paramList[i].toString().equalsIgnoreCase("null"))
            			{
            				valorNulo = true;
            			}
            			            			
            			if(tipos[i].equalsIgnoreCase("int"))
            			{
            				if (valorNulo)
            					cs.setNull(posIn, OracleTypes.INTEGER);            					
            				else
            					cs.setInt(posIn, Integer.valueOf(paramList[i].toString()));            				
            			}
            			else if(tipos[i].equalsIgnoreCase("float"))
            			{
            				if (valorNulo)
            					cs.setNull(posIn, OracleTypes.FLOAT);            					
            				else
            					cs.setFloat(posIn, Float.valueOf(paramList[i].toString()));
            			}
            			else if(tipos[i].equalsIgnoreCase("number"))
            			{
            				if (valorNulo)
            					cs.setNull(posIn, OracleTypes.NUMBER);            					
            				else
            					cs.setInt(posIn, Integer.valueOf(paramList[i].toString()));            				
            			}
            			else if(tipos[i].equalsIgnoreCase("string"))
            			{
            				if (valorNulo)
            					cs.setNull(posIn, OracleTypes.VARCHAR);            					
            				else
            					cs.setString(posIn, paramList[i].toString());            				
            			}
            			else if(tipos[i].equalsIgnoreCase("date"))
            			{
            				if (valorNulo)
            					cs.setNull(posIn, OracleTypes.DATE);            					
            				else
            				{
            					long dateLong = 0;
            					String dateStr = paramList[i].toString();
            					SimpleDateFormat formato = new SimpleDateFormat("yyyyMMdd");
            					dateLong = formato.parse(dateStr).getTime();
            					cs.setDate(posIn, new java.sql.Date(dateLong));
            				}
            			}
            			else
            			{
            				if (valorNulo)
            					cs.setNull(posIn, OracleTypes.VARCHAR);            					
            				else
            					cs.setString(posIn, paramList[i].toString());            				
            			}            			
            			valorNulo = false;
                	}
            	}
            }

            /*REGISTRANDO PARAMETROS DE SALIDA*/            
            if (tieneOutParameters)
            {
            	if (dataTypes.equals(""))
            	{
            		for (int i = 0; i < outParamList.length; i++) 
            		{
                		cs.setString(i+1, outParamList[i].toString());
                		LogRegister.out(LogRegister.FULL, "[" + requestID + "] Parametros OUT " + String.valueOf(i+1) + " = \"" + outParamList[i].toString() + "\"");
                	}
            	}
            	else
            	{
            		/*SOLO DEFINIDOS PARA ORACLE*/
            		String[] tipos = dataTypes.split("\\|");
            		int posDataType = tiposOut;
            	
            		for (int i = 0; i < outParamList.length; i++) 
            		{       
            			LogRegister.out(LogRegister.FULL, "[" + requestID + "] Parametros OUT ("+tipos[posDataType]+")  " + String.valueOf(posDataType+1) + " = \"" + outParamList[i].toString() + "\"");
            			
            			if(tipos[posDataType].equalsIgnoreCase("int")){
            				cs.registerOutParameter(posDataType+1, OracleTypes.INTEGER);            				
            			}else if(tipos[posDataType].equalsIgnoreCase("float")){            				
            				cs.registerOutParameter(posDataType+1, OracleTypes.FLOAT);            				
            			}else if(tipos[posDataType].equalsIgnoreCase("number")){            				
            				cs.registerOutParameter(posDataType+1, OracleTypes.NUMBER);            				
            			}else if(tipos[posDataType].equalsIgnoreCase("string")){            				
            				cs.registerOutParameter(posDataType+1, OracleTypes.VARCHAR);            				
            			}else if(tipos[posDataType].equalsIgnoreCase("clob")){            				
            				cs.registerOutParameter(posDataType+1, OracleTypes.CLOB);            				
            			}else if(tipos[posDataType].equalsIgnoreCase("cursor")){            				
            				cs.registerOutParameter(posDataType+1, OracleTypes.CURSOR);            				
            			}else if(tipos[posDataType].equalsIgnoreCase("date")){            				
            				cs.registerOutParameter(posDataType+1, OracleTypes.DATE);            				
            			}else{
            				cs.registerOutParameter(posDataType+1, OracleTypes.VARCHAR); 
            			}
            			posDataType++;                		
                	}
            	}
            }
            
			/*EJECUCION DEL SQL*/
            if ( toCache && ThreadManageCache.htCache.containsKey(servicio+query+parameters) ) 
            {
            	Hashtable htCache = (Hashtable) ThreadManageCache.htCache.get(servicio+query+parameters);
            	long currentTime = (System.currentTimeMillis() / 1000);
            	long createdCache = (long) htCache.get("created");
            	LogRegister.out(LogRegister.DETAIL, "[" + requestID + "] Cache currentTime: " + currentTime + ", createdCache: " + createdCache + ", timeCache: " + timeCache);
            	
            	if ( currentTime < (createdCache + timeCache) ) 
            	{
            		LogRegister.out(LogRegister.DETAIL, "[" + requestID + "] Se obtiene resultado desde Cache.");
            		return (htCache.get("result"));
            	}
            }
            
			if (tieneOutParameters || auxFunction == 1) 
			{							
				cs.execute();			
				
				lista = new ArrayList<Map<String, Object>>();
				
				/*Obteniendo Resultados (Parametros de Salida) dado los tipos de datos registrados del PLSQL, */
				/*NOTA: Esto esta para Oracle.*/
				
				if ( auxFunction == 1 ) 
				{
					mapResultado = new LinkedHashMap<String, Object>();
					mapResultado.put("RESULTADO_FUNCTION", cs.getString(1));
					lista.add(mapResultado);
					
					LogRegister.out(LogRegister.DETAIL, "[" + requestID + "] RESULTADO_FUNCTION: " + cs.getString(1));
				}
				else
				{
					String[] tipos = dataTypes.split("\\|");
	        		int posDataType = tiposOut;
	        		
	        		for (int i = 0; i < outParamList.length; i++) 
	        		{  
	        			mapResultado = new LinkedHashMap<String, Object>();
	        			
	        			if(tipos[posDataType].equalsIgnoreCase("string"))
	        			{            				
	        				mapResultado.put(outParamList[i].toString(), cs.getString(posDataType+1));
	        				lista.add(mapResultado);	        			
	        			}
	        			else if(tipos[posDataType].equalsIgnoreCase("int"))
	        			{            				
	        				mapResultado.put(outParamList[i].toString(), cs.getInt(posDataType+1));
	        				lista.add(mapResultado);
	        			}
	        			else if(tipos[posDataType].equalsIgnoreCase("float"))
	        			{            				
	        				mapResultado.put(outParamList[i].toString(), cs.getFloat(posDataType+1));
	        				lista.add(mapResultado);
	        			}
	        			else if(tipos[posDataType].equalsIgnoreCase("cursor"))
	        			{
	        				rs = (ResultSet) cs.getObject(posDataType+1);    

	        				int _totalRecords = 0;	        				
	        				
	        				while( rs.next() )
	        				{
	        	        		ResultSetMetaData rsmt = rs.getMetaData();
	        	        		LinkedHashMap<String, Object> MapRecordSet = new LinkedHashMap<String, Object>();
	        	        			        	        		
	        	        		_totalRecords = _totalRecords + 1;	        	        		
	        	        		
	        	        		for( int j=1; j<=rsmt.getColumnCount(); j++ )
	        	        		{
	        	        			String oKeyName  = "";
	        	        			String oKeyValue ="";
	        	        				        	        			
	        	        			if ( (!rsmt.getColumnName(j).equalsIgnoreCase("null")) && (!rsmt.getColumnName(j).equals("")) )
	        	        			{
	        	        				if ( mapResultado.containsKey( rsmt.getColumnName(j) ) )
	        	        					oKeyName = rsmt.getColumnName(j) + "_" + String.valueOf(_totalRecords);
	        	        				else
	        	        					oKeyName = rsmt.getColumnName(j);
	        	        				
	        	        				if ( rs.getObject(j) != null )
	        	        					oKeyValue = rs.getObject(j).toString();	        	        					
	        	        				else
	        	        					oKeyValue = "";
	        	        			}
	        	        			else
	        	        			{
	        	        				if ( rs.getObject(j) != null )
	        	        				{	oKeyName  = "DATO_" + j;
	        	        					oKeyValue = rs.getObject(j).toString();
	        	        				}
	        	        				else
	        	        				{
	        	        					oKeyName  = "DATO_" + j;
	        	        					oKeyValue = "";
	        	        				}
	        	        			}
	        	        			
	        	        			
	        	        			MapRecordSet.put(oKeyName, oKeyValue);	        	        				        	        			
	        	        		}	        	        		
	        	        		mapResultado.put("RecordItem_" + String.valueOf(_totalRecords), MapRecordSet);
	        	        		lista.add(mapResultado);
	        				}	        				
	        			}
	        			else
	        			{
	        				mapResultado.put(outParamList[i].toString(), cs.getString(posDataType+1));
	        				lista.add(mapResultado);
	        			}	
	        			
	        			posDataType++;	        			
	            	}	        		
	        		LogRegister.out(LogRegister.DETAIL, "[" + requestID + "] Numero de filas devueltas: " + lista.size());
				}
			} 
			else 
			{					
				if( QueryType.equals("2") ) // La opcion 2 corresponde a sentencias que no retornan un recordset
				{
					int rowAffected = cs.executeUpdate();					
					mapResultado = new LinkedHashMap<String, Object>();					
					mapResultado.put("RowsAffected", rowAffected);
					
					lista = new ArrayList<Map<String, Object>>();					
					lista.add(mapResultado);
					
					LogRegister.out(LogRegister.DETAIL, "[" + requestID + "] Numero de registros actualizados: " + rowAffected);
				}
				else
				{
					rs = cs.executeQuery();					
					ResultSetMetaData rsmt = rs.getMetaData();
									
					int j;
					if (rs!=null) 
					{				
						lista = new ArrayList<Map<String, Object>>();
						
						while (rs.next()) 
						{
							mapResultado = new LinkedHashMap<String, Object>();
							
							for (j = 1; j <= rsmt.getColumnCount(); j++) 
								mapResultado.put(rsmt.getColumnName(j), rs.getObject(j));										
													
							lista.add(mapResultado);
						}					
			        	LogRegister.out(LogRegister.DETAIL, "[" + requestID + "] Numero de filas devueltas: " + lista.size());
					}					
				}		
			}
					
			result = new Gson().toJson(lista);			
			fechaF = LogRegister.getFechaHora("yyyy-MM-dd HH:mm:ss:SSS");
			
			// valida si se almacena en cache
			if ( toCache && lista.size() > 0) 
			{
				LogRegister.out(LogRegister.DETAIL, "[" + requestID + "] Se almacena resultado en cache: " + ThreadManageCache.htCache.size());			
				ThreadManageCache.addResult(servicio+query+parameters, result, timeCache);
			}			
        }
    	catch (SQLException ex) 
    	{
    		// ex.printStackTrace();
        	LogRegister.out(LogRegister.DEBUG, "[" + requestID + "] - CRITICAL SQL ERROR: SQLException: " + ex.getMessage());
            LogRegister.out(LogRegister.DEBUG, "[" + requestID + "] - CRITICAL SQL ERROR: SQLState    : " + ex.getSQLState());
            LogRegister.out(LogRegister.DEBUG, "[" + requestID + "] - CRITICAL SQL ERROR: VendorError : " + ex.getErrorCode());
            
        	mapError.put("MSG", ex.getMessage());
        	mapError.put("RC", Integer.toString(ex.getErrorCode()));
        	mapError.put("SQLSTATE", ex.getSQLState());
        	
        	// Mantenemos errorMessage y errorCode por compatibilidad con versiones anteriores.
        	mapError.put("errorMessage", ex.getMessage());			
        	mapError.put("errorCode", Integer.toString(ex.getErrorCode()));
        	
        	// Para que vuelva a "abrir" la conexion.
        	this.basicDataSource.invalidateConnection(conn);        	
        	result = new Gson().toJson(mapError);
    	}
        catch(Exception ex)
        {
        	LogRegister.out(LogRegister.DEBUG, "CRITICAL: "+ "[" + requestID +" ] - CRITICAL ERROR: [" + ex.getMessage() + "]"  );       
        	mapError.put("RC", "-99");
        	mapError.put("MSG", ex.getMessage());
        	mapError.put("errorMessage", ex.getMessage());
        	
        	result = new Gson().toJson(mapError);
        }        
        finally 
        {        
        	try
        	{	
        		//Se establece variables secundaria requestIDCSV para que registro de TRX contenga el campo IP y requestID por separado
        		String requestIDCSV=requestID.replace("] [", ";");
        		LogRegister.registerTRX(this.servicio + ";" + query + ";" + requestIDCSV + ";" + fechaI + ";" + fechaF + ";" + strSQL + ";" + result);	
            } 
        	catch (Exception ex) 
        	{
            	LogRegister.out(LogRegister.DEBUG, "[" + requestID + "] Error registro sp [" + strSQL + "] : " + ex.getMessage());
            }
            
        	try 
        	{ 
        		if( cs != null ) 
        			cs.close();        		
		
				liberaConexion(conn);
        	} 
        	catch (SQLException ex) 
        	{
            	LogRegister.out(LogRegister.DEBUG, "[" + requestID + "] SQLException: " + ex.getMessage());
                LogRegister.out(LogRegister.DEBUG, "[" + requestID + "] SQLState    : " + ex.getSQLState());
                LogRegister.out(LogRegister.DEBUG, "[" + requestID + "] VendorError : " + ex.getErrorCode());                
        	}
        	catch (Exception ex) 
        	{
            	LogRegister.out(LogRegister.DEBUG, "[" + requestID + "] Exception: " + ex.getMessage());
        	}        	
        }	        
        return result;
	}

    private void liberaConexion(Connection conexion)
    {
        try 
        {
        	if (null != conexion)
        	{
        		conexion.close();
            }
        }
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }
	
    public synchronized Object executeSQLite(String query, String parameters, String outParameters, String dataTypes, String requestID) throws SQLException{
    	Connection 			conn = null;
    	conn = this.basicDataSource.getConnection() ;
    	
        Map<String, Object> mapResultado = null;
        List<Map<String, Object>> lista = null;
        ResultSet rs = null;
        Statement stmt = null;
        
        if (!query.toUpperCase().contains("SELECT") && !query.toUpperCase().contains("UPDATE") && !query.toUpperCase().contains("INSERT") && !query.toUpperCase().contains("DELETE")) {
        	SQLiteFunctions sqlite = new SQLiteFunctions(conn);
        	lista = sqlite.execute(query, parameters);
        }
        else {
	        stmt = conn.createStatement();
	        rs = stmt.executeQuery(query);
	        // Obteniendo MetaData desde el Resultset y pasandolo a un List<Map<String,Object>>
	        ResultSetMetaData rsmt = rs.getMetaData();
	        ///fechaF = LogRegister.getFechaHora("yyyy-MM-dd HH:mm:ss:SSS");
	        int j;
	        if(rs!=null)
	        {
	            lista = new ArrayList<Map<String, Object>>();
	            while (rs.next()) 
	            {
	                mapResultado = new LinkedHashMap<String, Object>();
	                for (j = 1; j <= rsmt.getColumnCount(); j++)
	                {
	                	mapResultado.put(rsmt.getColumnName(j), rs.getObject(j));
	                	LogRegister.out(LogRegister.DETAIL, "[" + requestID + "] Field: "+ rsmt.getColumnName(j) + "-> Value: "+ rs.getObject(j));
	                }
	                lista.add(mapResultado);
	            }
	        }
	        rs.close();
	        stmt.close();
        }
        
        conn.close();
        String result = new Gson().toJson(lista);

		return result;	
	}
    
}
