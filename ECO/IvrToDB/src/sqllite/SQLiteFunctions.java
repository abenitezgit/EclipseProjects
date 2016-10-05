package sqllite;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class SQLiteFunctions {

	private Connection c = null;
	
	public SQLiteFunctions(Connection conn) {
		c = conn;
	}
	
	public List<Map<String, Object>> execute(String query, String parameters) {
		List<Map<String, Object>> lista = null;
		
		if (query.equals("ACD_GET_CONTINGENCIA"))
			lista = ACD_GET_CONTINGENCIA(parameters);
		if (query.equals("ACD_LOGIN"))
			lista = ACD_LOGIN(parameters);
		if (query.equals("ACD_ACTIVACION"))
			lista = ACD_ACTIVACION(parameters);
		
		return lista;
	}

	public List<Map<String, Object>> ACD_LOGIN(String parameters){
			System.out.println( "Se ha ejecutado el metodo login" ); 	
		    //Declaramos variables
		    int sTENANT_DBID=1;
		    String sUSER_ID="";
		    String sPASSWORD="";
		    String sFECHAHORA="";
		    
		    String[] param = parameters.split("\\|",4);
			sTENANT_DBID 	= Integer.parseInt(param[0]);
			sUSER_ID		= param[1];
			sPASSWORD		= param[2];
			sFECHAHORA		= param[3];
			
		    //variabes de retorno
		    List<Map<String, Object>> lista 	= null;
			Map<String, Object> mapResultado 	= null;
			
		    int sRC = -1;
		    String sMSG= "ERROR";
		    String sDATA="";
		    
		    //variabes intermedias
		    int sUSER_DBID=0;
		    String sID_GATEWAY="";
		    String sDNIS="";
		    String sSERVICIO="";
		    String sAUDIO="";
		    String sESTADO="0"; //int
		    String sLAST_LOGIN = "";
		    String sCREATION_DATE="";
		    
		    //funcion para obtener la fecha
		    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Date date = new Date();
		    //String fechaActual = dateFormat.format(date);
		    sFECHAHORA = dateFormat.format(date);
		    
	        Statement stmt = null;
	        Statement stmt2 = null;
	        Statement stmt3 = null;
	        
	        try {
	            stmt = c.createStatement();

	            sRC = 0;

	            ResultSet rs = stmt.executeQuery( "SELECT USER_DBID, "+
	            								  "ID_GATEWAY, "+
	            								  "DNIS, "+
	            								  "SERVICIO, "+
	            								  "AUDIO, "+
	            								  "ESTADO, "+
	            								  "LAST_LOGIN, "+
	            								  "CREATION_DATE"+
	            		 						  " FROM ACD_USUARIOS NOLOCK"+
	            		 						  " WHERE TENANT_DBID="+sTENANT_DBID+" AND USER_ID="+sUSER_ID+ " AND PASSWORD='"+sPASSWORD+"'"+
	            		 						  " ORDER BY USER_DBID ASC LIMIT 1" );
	            
	            /*ResultSet rs = stmt.executeQuery( "SELECT USER_DBID"+
						  " FROM ACD_USUARIOS NOLOCK "+
						  "WHERE TENANT_DBID="+sTENANT_DBID+" AND USER_ID="+sUSER_ID+
						  " ORDER BY USER_DBID ASC LIMIT 1" );*/
	            								  
	            System.out.println( "Se ha ejecutado la sentencia SQL\n" ); 	
							
	            
	            while ( rs.next() ) {
	            	 
	            	 sUSER_DBID = rs.getInt("USER_DBID");
	            	 sID_GATEWAY = rs.getString("ID_GATEWAY");
	            	 sDNIS = rs.getString("DNIS");
	            	 sSERVICIO = rs.getString("SERVICIO");
	            	 sAUDIO = rs.getString("AUDIO");
	            	 sESTADO = rs.getString("ESTADO");
	            	 sLAST_LOGIN = rs.getString("LAST_LOGIN");
	            	 sCREATION_DATE = rs.getString("CREATION_DATE");
	            	 
	                 System.out.println( "TENANT_DBID = " + sTENANT_DBID );
	                 System.out.println( "USER_DBID = " + sUSER_DBID );
	                 System.out.println( "PASSWORD = " + sPASSWORD );
	                 System.out.println( "ID_GATEWAY = " + sID_GATEWAY );
	                 System.out.println( "DNIS = " + sDNIS );
	                 System.out.println( "SERVICIO = " + sSERVICIO );
	                 System.out.println( "AUDIO = " + sAUDIO );
	                 System.out.println( "ESTADO = " + sESTADO );
	                 System.out.println( "LAST_LOGIN = " + sLAST_LOGIN );
	                 System.out.println( "CREATION_DATE = " + sCREATION_DATE );
	            }
	            
	            if(sID_GATEWAY == null){
	            	sID_GATEWAY = "";
	            }
	            if(sDNIS == null){
	            	sDNIS = "";
	            }
	            if(sSERVICIO == null){
	            	sSERVICIO = "";
	            }
	            if(sAUDIO == null){
	            	sAUDIO = "";
	            }
	            if(sESTADO == null){
	            	sESTADO = "1";
	            }
	            //if(sLAST_LOGIN == null){
	            //	sLAST_LOGIN = "";
	            //}
	            
	            //Verificar existencia y clave del Usuario
				  if(sRC != 0){
					  sRC = 1001;
					  sMSG = "Usuario No Existe o Clave Invalida";
					  System.out.println("");
					  System.out.println(sRC);
					  System.out.println(sMSG);
				  }
				  
				  //Verificar si usuario est� activo
				  else if(sESTADO != "1"){//l�nea actualizada
					  sRC = 1002;
					  sMSG = "Usuario Inactivo - ESTADO: "+sESTADO;
				  }
				  // Usuario Existe y esta Activo.
				  sRC = 0;
				  if(sLAST_LOGIN == null){
					  sLAST_LOGIN = "";
					  sMSG = "Primera Conexion";
				  }else{
					  sMSG = "lastLogin: "+sLAST_LOGIN;
					  sDATA = "audio:"+sAUDIO+";"+"servicio:"+sSERVICIO+";"+"DNIS:"+sDNIS;
					  
					  //Actualizar Fecha y hora de ultimo Login
					  stmt2 = c.createStatement();
					  String sql = "UPDATE ACD_USUARIOS SET LAST_LOGIN = '"+sFECHAHORA+"' WHERE USER_DBID="+sUSER_DBID+";";
					  stmt2.executeUpdate(sql);
					  
					  System.out.println("Se ha actualizado la tabla ACD_USUARIOS USER_DBID: "+sUSER_DBID);
				  }
				  
				  //numero de fias antes de insetar en ACD_LOG
				  stmt2 = c.createStatement();
				  ResultSet rs2 = stmt2.executeQuery("SELECT COUNT(*) FROM ACD_LOG");
				  int count = 0;
		          while ( rs2.next() ) {
		        	     count = rs2.getInt("COUNT(*)");
		            	 System.out.println("Numero de filas: "+count);
		            }
		          
				  if (count != 0){
					  stmt3 = c.createStatement();
			          String sql2 = "INSERT INTO ACD_LOG (DBID,TENANT_DBID, FECHAHORA, USER_DBID, USER_ID, NIVEL, ACCION, DATA, MSG) " +
			                   "VALUES ((SELECT MAX(DBID) FROM ACD_LOG)+1,"+sTENANT_DBID+",'"+sFECHAHORA+"',"+sUSER_DBID+",'"+sUSER_ID+"',1,'LOGIN','"+sDATA+"','"+sMSG+"');"; 
			          
			          stmt3.executeUpdate(sql2);              
			          
			          System.out.println("Se han insertado datos en la tabla ACD_LOG");
				  }else{
					  stmt3 = c.createStatement();
			          String sql2 = "INSERT INTO ACD_LOG (DBID,TENANT_DBID, FECHAHORA, USER_DBID, USER_ID, NIVEL, ACCION, DATA, MSG) " +
			                   "VALUES (1,"+sTENANT_DBID+",'"+sFECHAHORA+"',"+sUSER_DBID+",'"+sUSER_ID+"',1,'LOGIN','"+sDATA+"','"+sMSG+"');"; 
			          
			          stmt3.executeUpdate(sql2);              
			          
			          System.out.println("Se han insertado datos en la tabla ACD_LOG");
				  }
				  
	            rs.close();
	            rs2.close();
	            stmt.close();
	            stmt2.close();
	            stmt3.close();
	            //super.closeConnection();
	            
	            lista = new ArrayList<Map<String, Object>>();
	            mapResultado = new LinkedHashMap<String, Object>();
	            mapResultado.put("RC", sRC);
	            mapResultado.put("MSG", sMSG);
	            mapResultado.put("DATA", sDATA);
	            lista.add(mapResultado);

	        } catch(SQLException ex) {
	            System.err.println("SQLException: " + ex.getMessage());
	        } catch (Exception e) {
				e.printStackTrace();
			}
	        
	        return lista;
	}

	public List<Map<String, Object>> ACD_GET_CONTINGENCIA(String parameters){
		
		int sTENANT_DBID	= 0;
		String sHOST		= ""; 
		String sDNIS		= "";
		String sFECHAHORA	= "";
		
		String[] param = parameters.split("\\|",4);
		sTENANT_DBID 	= Integer.parseInt(param[0]);
		sHOST			= param[1];
		sDNIS			= param[2];
		sFECHAHORA		= param[3];
		
	    //variabes de retorno
		List<Map<String, Object>> lista 	= null;
		Map<String, Object> mapResultado 	= null;
		
	    int sRC = -1;
	    String sMSG = "ERROR DESCONOCIDO";
	    String sDATA = "";
	    
	    //variabes intermedias
	    String sID_GATEWAY;
	    String sCONTINGENCIA="";
	    String slTENANT_DBID="";
	    String slDNIS="";
	    String sSERVICIO = "";
	    String sDESTINO="";
	    String sAUDIO="";
	    String sPROXY="";
	    String sFECHA_ACTIVACION;
	    String sUSER_ID;
	    
	    Statement stmt = null;
        
	    try {
	    	stmt = c.createStatement();

	        sRC = 0;
	        
	        ResultSet rs = stmt.executeQuery( "SELECT ID_GATEWAY, " +
					  "TENANT_DBID, " +
					  "DNIS, " +
					  "CONTINGENCIA, " +
					  "SERVICIO, " +
					  "AUDIO, " +
					  "DESTINO, " +
					  "PROXY, " +
					  "FECHA_ACTIVACION, " +
					  "UID_ACTIVACION" +
					  " FROM ACD_CONTINGENCIA NOLOCK" +
					  " WHERE TENANT_DBID=" + sTENANT_DBID + " AND DNIS='"+sDNIS+"'" +
					  " ORDER BY FECHA_ACTIVACION DESC LIMIT 10" );
	        
	        while ( rs.next() ) {
           	 
           	 sID_GATEWAY 	= rs.getString("ID_GATEWAY");
           	 sCONTINGENCIA 	= rs.getString("CONTINGENCIA");
           	 slTENANT_DBID 	= rs.getString("TENANT_DBID");
           	 slDNIS 		= rs.getString("DNIS");
           	 sSERVICIO 		= rs.getString("SERVICIO");
           	 sAUDIO 		= rs.getString("AUDIO");
           	 sDESTINO 		= rs.getString("DESTINO");
           	 sPROXY 		= rs.getString("PROXY");
           	 sFECHA_ACTIVACION = rs.getString("FECHA_ACTIVACION");
           	 sUSER_ID 		= rs.getString("UID_ACTIVACION");
           	 
             System.out.println( "ID_GATEWAY = " + sID_GATEWAY );
             System.out.println( "CONTINGENCIA = " + sCONTINGENCIA );
             System.out.println( "TENANT_DBID = " + slTENANT_DBID );
             System.out.println( "DNIS = " + slDNIS );
             System.out.println( "SERVICIO = " + sSERVICIO );
             System.out.println( "AUDIO = " + sAUDIO );
             System.out.println( "DESTINO = " + sDESTINO );
             System.out.println( "PROXY = " + sPROXY );
             System.out.println( "FECHA_ACTIVACION = " + sFECHA_ACTIVACION );
             System.out.println( "UID_ACTIVACION = " + sUSER_ID );
           }
           
           if(sCONTINGENCIA == null){
        	   sCONTINGENCIA = "INACTIVA";
           }
           if(sAUDIO == null){
           	   sAUDIO = "";
           }
           if(sDESTINO == null){
        	   sDESTINO = "";
           }
           if(sPROXY == null){
        	   sPROXY = "";
           }
	        
	       //Verificar resultado de la busqueda
           if(sRC != 0){
				  sRC = 0;
				  sMSG = "Contingencia para Servicio "+sDNIS+"@"+sHOST+" INACTIVA - Valor por Defecto";
				  sDATA = "status:INACTIVA;";
				  
			  }else{
				  sRC = 0;
				  sMSG = "Contingencia para Servicio "+sDNIS+"@"+sHOST+" "+sCONTINGENCIA;
				  sDATA = "status:"+sCONTINGENCIA+";"+"destino:"+sDESTINO+";"+"audio:"+sAUDIO+";"+"proxy:"+sPROXY+";"+"servicio:"+sSERVICIO;
			  }
           
           lista = new ArrayList<Map<String, Object>>();
           mapResultado = new LinkedHashMap<String, Object>();
           mapResultado.put("RC", sRC);
           mapResultado.put("MSG", sMSG);
           mapResultado.put("DATA", sDATA);
           lista.add(mapResultado);
           
           System.out.println("");
           System.out.println(sRC);
           System.out.println(sMSG);
           System.out.println(sDATA);
           
           rs.close();
           stmt.close();
           //super.closeConnection();
           
           
		} catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
        } catch (Exception e) {
			e.printStackTrace();
		}
        
        return lista;
	}
	
	public List<Map<String, Object>> ACD_GET_ANEXOS_QUEUE(String parameters){
		//Declaramos variables
		int sTENANT_DBID=1;
		String sSERVICIO = "";
		String sFECHAHORA;
		
		String[] param = parameters.split("\\|",3);
		sTENANT_DBID 	= Integer.parseInt(param[0]);
		sSERVICIO		= param[1];
		sFECHAHORA		= param[2];
		
	    //variabes de retorno
		List<Map<String, Object>> lista 	= null;
		Map<String, Object> mapResultado 	= null;
		
		//Variables para ejecucion del metodo
		int sID_ANEXO=0;//variable para rescatar ID_ANEXO del rs
		int sRC = 0;
		String sMSG="";
		String sID_QUEUE = "";
		String sID_GATEWAY = "";
		String sHORA_FIN = "";
		String sHORA_INI = "";
		
		Statement stmt = null;
		Statement stmt2 = null;
		
		try {
			stmt = c.createStatement();
			stmt2 = c.createStatement();

	        ResultSet rs = stmt.executeQuery( "SELECT ID_ANEXO"+
	        		" FROM ACD_QUEUE AQ"+
	        		  " LEFT JOIN ACD_QUEUE_ANEXO AQA"+
					  " ON AQA.TENANT_DBID = AQ.TENANT_DBID AND AQA.ID_GATEWAY = AQ.ID_GATEWAY AND AQA.ID_QUEUE = AQ.ID_QUEUE"+
					  " WHERE AQ.TENANT_DBID="+sTENANT_DBID+" AND (AQ.SERVICIO='"+sSERVICIO+"' OR '"+sSERVICIO+"' = 'ALL')");
	        
	        while ( rs.next() ) {
	           	 
	           	 sID_ANEXO = rs.getInt("ID_ANEXO");
	           	 
	             System.out.println( "ID_ANEXO = " + sID_ANEXO );
	           }
	        
	        if(sID_ANEXO != 0){
	        	sRC = 0;
	        	sMSG = "OK";
	        	
	        	stmt2 = c.createStatement();

		        ResultSet rs2 = stmt2.executeQuery( "SELECT AQA.ID_ANEXO"+
		        		  " AQA.ID_QUEUE"+
		        		  " AQA.ID_GATEWAY"+
		        		  " AQA.HORA_FIN"+
		        		  " AQA.HORA_INI"+
		        		  " FROM ACD_QUEUE AQ"+
		        		  " LEFT JOIN ACD_QUEUE_ANEXO AQA"+
						  " ON AQA.TENANT_DBID = AQ.TENANT_DBID AND AQA.ID_GATEWAY = AQ.ID_GATEWAY AND AQA.ID_QUEUE = AQ.ID_QUEUE"+
						  " WHERE AQ.TENANT_DBID="+sTENANT_DBID+" AND (AQ.SERVICIO='"+sSERVICIO+"' OR '"+sSERVICIO+"' = 'ALL')"+
						  " ORDER BY ID_ANEXO DESC");
		        
		        while ( rs2.next() ) {
		           	 
		           	 sID_ANEXO = rs2.getInt("ID_ANEXO");
		           	 
		             System.out.println( "ID_ANEXO = " + sID_ANEXO );
		           }
	        	
	        }else{
	        	sRC = 1010;
	        	sMSG = "No records";
	        	sID_ANEXO = 0;
	        	sID_QUEUE = "";
	        	sID_GATEWAY = "";
	        	sHORA_FIN = "";
	        	sHORA_INI = "";
	        }
	        
	       lista = new ArrayList<Map<String, Object>>();
           mapResultado = new LinkedHashMap<String, Object>();
           mapResultado.put("RC", sRC);
           mapResultado.put("MSG", sMSG);
           lista.add(mapResultado);
	       
	        
		} catch(SQLException ex) {
			sMSG = ex.getMessage();
            System.err.println("SQLException: " + sMSG);
            	sID_ANEXO = 0;
            	sID_QUEUE = "";
            	sID_GATEWAY = "";
            	sHORA_FIN = "";
            	sHORA_INI = "";
        } catch (Exception e) {
			e.printStackTrace();
		}
        
        return lista;
	}
	
	public List<Map<String, Object>> ACD_ACTIVACION(String parameters){
		//Declaramos variables
		int sTENANT_DBID	= 1;
		String sSERVICIO 	= "";
		String sFECHAHORA	= "";
		String sACCION		= "";
		String sUSER_ID		= "";
		
		String[] param = parameters.split("\\|",5);
		sTENANT_DBID 	= Integer.parseInt(param[0]);
		sUSER_ID		= param[1];
		sFECHAHORA		= param[2];
		sSERVICIO		= param[3];
		sACCION			= param[4];
		
	    //variabes de retorno
		List<Map<String, Object>> lista 	= null;
		Map<String, Object> mapResultado 	= null;
		
		//variabes de retorno
	    int sRC = -1;
	    String sMSG = "ERROR";
	    
	    //variabes intermedias
	    int sUSER_DBID = 0;
	    String sID_GATEWAY="";
	    String sDNIS = "";
	    String sAUDIO="";
	    int sESTADO=0;
	    String sLAST_LOGIN;
	    String sNUEVO_ESTADO = null;
	    int countRows = 0;
	    
	    Statement stmt = null;
	    Statement stmt2 = null;
	    
	    try {
	    	stmt = c.createStatement();
	    	stmt2 = c.createStatement();

	        sRC = 0;
	        
	        ResultSet rs = stmt.executeQuery( "SELECT USER_DBID, "+
					  "ID_GATEWAY, "+
					  "DNIS, "+
					  "AUDIO, "+
					  "ESTADO, "+
					  "LAST_LOGIN"+
					  " FROM ACD_USUARIOS NOLOCK"+
					  " WHERE TENANT_DBID="+sTENANT_DBID+" AND USER_ID='"+sUSER_ID+"' AND SERVICIO='"+sSERVICIO+"'"+
					  " ORDER BY USER_DBID ASC LIMIT 1" );
	        
	        while ( rs.next() ) {
           	 
           	 sUSER_DBID = rs.getInt("USER_DBID");
           	 sID_GATEWAY = rs.getString("ID_GATEWAY");
           	 sDNIS = rs.getString("DNIS");
           	 sAUDIO = rs.getString("AUDIO");
           	 sESTADO = rs.getInt("ESTADO");
           	 sLAST_LOGIN = rs.getString("LAST_LOGIN");
           	 
           	 System.out.println( "USER_DBID = " + sUSER_DBID );
             System.out.println( "ID_GATEWAY = " + sID_GATEWAY );
             System.out.println( "DNIS = " + sDNIS );
             System.out.println( "AUDIO = " + sAUDIO );
             System.out.println( "ESTADO = " + sESTADO );
             System.out.println( "LAST_LOGIN = " + sLAST_LOGIN );
             //System.out.println( "rs.getRow() = " + rs.getRow() );
           }
	        
	        // Verificar existencia del Usuario y asignaci�n al servicio
			if (sRC != 0){			// Usuario no Existe o no tiene el servicio asignado
				sRC = 1003;
				sMSG = "Usuario "+sUSER_ID+" No Existe o no tiene el servicio "+sSERVICIO+" asignado";
			}
			//Verificar si usuario est� activo
			 
			if (sESTADO != 1){			// Usuario no esta activo: 1 = Activo.
				sRC = 1002;
				sMSG = "Usuario inactivo - ESTADO = "+sESTADO;
			}
			
			if (sACCION.equals("ACTIVAR")){			// Usuario Existe y esta Activo => aplicar "ACCION" al Servicio
				sNUEVO_ESTADO = "ACTIVA";
				sMSG = "Contingencia Activa en "+sSERVICIO;
			}else if(sACCION.equals("DESACTIVAR")){
				sNUEVO_ESTADO = "INACTIVA";
				sMSG = "Contingencia Inactiva en "+sSERVICIO;
			}else{//Accion Invalida
				sRC = 1004;
				sMSG = "Accion "+sACCION+" desconocida";
			}
			
			//Actualizar Fecha y hora de ultimo Login
			  stmt2 = c.createStatement();
			  int sql = stmt2.executeUpdate("UPDATE ACD_CONTINGENCIA " +
					  		"SET CONTINGENCIA = '"+sNUEVO_ESTADO+"',"+ //declaramos sql INT para devolver filas afectadas
					  	   "FECHA_ACTIVACION = date('now'),"+
					  	   "USER_DBID = "+sUSER_DBID+","+
					  	   "UID_ACTIVACION = "+sUSER_ID+""+
			  			   " WHERE SERVICIO="+sSERVICIO+" OR "+sSERVICIO+" = 'ALL';");
			  
			  
			  System.out.println("Se ha actualizado la tabla ACD_USUARIOS USER_DBID: "+sUSER_DBID);
			  System.out.println("Filas afectadas: "+sql);
			  
			  if (sql > 0){
				  sRC = 0;
				  sMSG = sMSG+" "+sql+" Rows Affected";
			  }
			  else{
				  sRC = 1005;
				  sMSG = "No hay servicio configurados para "+sSERVICIO;
			  }
			
	       lista = new ArrayList<Map<String, Object>>();
           mapResultado = new LinkedHashMap<String, Object>();
           mapResultado.put("RC", sRC);
           mapResultado.put("MSG", sMSG);
           lista.add(mapResultado);
	           
			rs.close();
            stmt.close();
            stmt2.close();
            //super.closeConnection();
			  
		} catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
        } catch (Exception e) {
			e.printStackTrace();
		}
        
        return lista;
        
	}
	
/*public static void main(String[] args) {
	SQLiteACD metodos = new SQLiteACD(dsClassName, dsHost, dsPort, dsDBName, dsUser, dsPassword, dsURL, dsServicio, dsMaxConn);
	metodos.login();
	
}*/
	
}
