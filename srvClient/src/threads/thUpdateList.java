package threads;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import utiles.MetaData;
import utiles.globalAreaData;

public class thUpdateList extends Thread{
	globalAreaData gDatos;
	Logger logger = Logger.getLogger("mysqlDB");

	public thUpdateList(globalAreaData g) {
		gDatos = g;
		
	}
	
	public void run() {
		try {
			MetaData conn = new MetaData(gDatos);
			if (conn.isConnected()) {
				logger.debug("Connected Metadata!!");
				String vSQL= "select * from tb_grupos";
				ResultSet rs = (ResultSet) conn.getQuery(vSQL);
				DefaultTableModel dtmGroup = new DefaultTableModel();
	            Vector<Vector<Object>>  data = new  Vector<>();
	            Vector<String>          cols = new Vector<>();
	            cols.add("GRPID");
	            cols.add("GRPDESC");
	            cols.add("ENABLE");
	            cols.add("HORID");
	            cols.add("UFECHAEXEC");
	            cols.add("USTATUS");
	            cols.add("CLIID");
	            cols.add("STATUS");
	            cols.add("LASTNUMSECEXEC");
				if (rs!=null) {
					try {
						while (rs.next()) {
							Vector<Object> row = new Vector<>();
							row.add(rs.getString("GRPID"));
							row.add(rs.getString("GRPDESC"));
							row.add(rs.getInt("ENABLE"));
							row.add(rs.getString("HORID"));
							row.add(rs.getDate("UFECHAEXEC"));
							row.add(rs.getString("USTATUS"));
							row.add(rs.getString("CLIID"));
							row.add(rs.getString("STATUS"));
							row.add(rs.getString("LASTNUMSECEXEC"));
							data.add(row);
						}
						dtmGroup.setDataVector(data, cols);
						//gDatos.jTableGroup.setModel(dtmGroup);
						gDatos.getlTableGroup().setModel(dtmGroup);
						rs.close();
					} catch (SQLException e) {
						System.out.println("Error recorriendo resultado de query: "+e.getMessage());
						logger.error("Error recorriendo resultado de query: "+e.getMessage());
					}
				} else {
					logger.error("No se pudo ejecutar query de consulta");
				}
				conn.closeConnection();
				
			} else {
				logger.info("No Connected..");
			}
		} catch (Exception e) {
			logger.error("Error thUpdateList: "+e.getMessage());
		}	
	}
}
