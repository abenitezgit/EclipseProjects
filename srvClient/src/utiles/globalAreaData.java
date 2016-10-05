package utiles;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.swing.JList;
import javax.swing.JTable;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import dataClass.Grupo;
import dataClass.ServerInfo;

public class globalAreaData {
	/**
	 * Log Class
	 */
	static Logger logger = Logger.getLogger("globalAreaData");
	
	/**
	 * Data Class
	 */
	ServerInfo serverInfo = new ServerInfo();
	
	/**
	 * Declara lista de Objetos que serán referenciados por los thread de procesos
	 * sin getter and Setter
	 */	
	public JList<?> jlistGroup;
	public JTable jTableGroup;
	public JTable jtableGroupActive;
	public JTable jTablePools;
	
	private List<Grupo> lstGroups = new ArrayList<Grupo>();
	
	
	/**
	 * Getter and Setter
	 */
	
	
	public JTable getlTableGroup() {
		return jTableGroup;
	}

	public List<Grupo> getLstGroups() {
		return lstGroups;
	}

	public void setLstGroups(List<Grupo> lstGroups) {
		this.lstGroups = lstGroups;
	}

	public JTable getJtableGroupActive() {
		return jtableGroupActive;
	}

	public void setJtableGroupActive(JTable jtableGroupActive) {
		this.jtableGroupActive = jtableGroupActive;
	}

	public JTable getjTablePools() {
		return jTablePools;
	}

	public void setjTablePools(JTable jTablePools) {
		this.jTablePools = jTablePools;
	}

	public void setlTableGroup(JTable jTableGroup) {
		this.jTableGroup = jTableGroup;
	}	
	
	public ServerInfo getServerInfo() {
		return serverInfo;
	}

	public void setServerInfo(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
	}

	public JList<?> getJlistGroup() {
		return jlistGroup;
	}

	public void setJlistGroup(JList<?> jlistGroup) {
		this.jlistGroup = jlistGroup;
	}
	
    public String serializeObjectToJSon (Object object, boolean formated) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();

            mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, formated);

            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            logger.error("Error serializeObjectToJson: "+e.getMessage());
            return null;
        }
    }
    
    public Object serializeJSonStringToObject (String parseJson, Class<?> className) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(parseJson, className);
        } catch (Exception e) {
            logger.error("Error serializeJSonStringToObject: "+e.getMessage());
            return null;
        }
    }      	
	
	public globalAreaData() {
        Properties fileConf = new Properties();

        try {

            //Parametros del File Properties
            //
            fileConf.load(new FileInputStream("srvClient.properties"));

            serverInfo.setSrvID(fileConf.getProperty("srvID"));
            serverInfo.setTxpMain(Integer.valueOf(fileConf.getProperty("txpMain")));
            serverInfo.setTxpAgendas(Integer.valueOf(fileConf.getProperty("txpAgendas")));
            serverInfo.setTxpETL(Integer.valueOf(fileConf.getProperty("txpETL")));
            serverInfo.setTxpKeep(Integer.valueOf(fileConf.getProperty("txpKeep")));
            serverInfo.setTxpSocket(Integer.valueOf(fileConf.getProperty("txpSocket")));
            serverInfo.setSrvPort(Integer.valueOf(fileConf.getProperty("srvPort")));
            serverInfo.setAgeShowHour(Integer.valueOf(fileConf.getProperty("ageShowHour")));
            serverInfo.setAgeGapMinute(Integer.valueOf(fileConf.getProperty("ageGapMinute")));
            serverInfo.setAuthKey(fileConf.getProperty("authKey"));
            serverInfo.setDriver(fileConf.getProperty("driver"));
            serverInfo.setConnString(fileConf.getProperty("ConnString"));
            serverInfo.setDbType(fileConf.getProperty("dbType"));
            serverInfo.setActivePrimaryMonHost(true);
            serverInfo.setSrvMonHost(fileConf.getProperty("srvMonHost"));
            serverInfo.setSrvMonHostBack(fileConf.getProperty("srvMonHostBack"));
            serverInfo.setMonPort(Integer.valueOf(fileConf.getProperty("monPort")));
            serverInfo.setMonPortBack(Integer.valueOf(fileConf.getProperty("monPortBack")));
                        
            if (serverInfo.getDbType().equals("ORA")) {
                //Recupera Paramteros ORA
                serverInfo.setDbOraHost(fileConf.getProperty("dbORAHost"));
                serverInfo.setDbOraPort(Integer.valueOf(fileConf.getProperty("dbORAPort")));
                serverInfo.setDbOraUser(fileConf.getProperty("dbORAUser"));
                serverInfo.setDbOraPass(fileConf.getProperty("dbORAPass"));
                serverInfo.setDbOraDBNAme(fileConf.getProperty("dbORADbName"));
            }
            
            if (serverInfo.getDbType().equals("SQL")) {
                //Recupera Paramteros ORA
                serverInfo.setDbSqlHost(fileConf.getProperty("dbSQLHost"));
                serverInfo.setDbSqlPort(Integer.valueOf(fileConf.getProperty("dbSQLPort")));
                serverInfo.setDbSqlUser(fileConf.getProperty("dbSQLUser"));
                serverInfo.setDbSqlPass(fileConf.getProperty("dbSQLPass"));
                serverInfo.setDbSqlDBName(fileConf.getProperty("dbSQLDbName"));
                serverInfo.setDbSqlDBInstance(fileConf.getProperty("dbSQLInstance"));
            }

            if (serverInfo.getDbType().equals("mySQL")) {
                //Recupera Paramteros ORA
                serverInfo.setDbmyHost(fileConf.getProperty("dbmyHost"));
                serverInfo.setDbmyPort(Integer.valueOf(fileConf.getProperty("dbmyPort")));
                serverInfo.setDbmyUser(fileConf.getProperty("dbmyUser"));
                serverInfo.setDbmyPass(fileConf.getProperty("dbmyPass"));
                serverInfo.setDbmyDbName(fileConf.getProperty("dbmyDbName"));
            }

            //Extrae Fecha de Hoy
            //
            Date today;
            SimpleDateFormat formatter;
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //System.out.println(formatter.getTimeZone());
            today = new Date();
                        
            
        } catch (IOException | NumberFormatException e) {
            logger.error("Error en constructor: "+e.getMessage());
        }
    }
}
