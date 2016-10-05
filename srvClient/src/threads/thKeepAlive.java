/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;
import java.io.* ; 
import java.net.* ;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dataClass.Grupo;
import utiles.globalAreaData;

/**
 *
 * @author andresbenitez
 */
public class thKeepAlive extends Thread {
    static globalAreaData gDatos;
    Logger logger = Logger.getLogger("thKeepAlive");
    
    //Carga constructor para inicializar los datos
    public thKeepAlive(globalAreaData m) {
        gDatos = m;
    }
    
    public String sendDataKeep()  {
        //
        
        try {
            // Se genera la salida de la lista 
            JSONObject jHeader = new JSONObject();
            JSONObject jData = new JSONObject();
            jHeader.put("data", jData);
            
            jHeader.put("auth", gDatos.getServerInfo().getAuthKey());
            jHeader.put("request", "getGroupsActive");
            
            return jHeader.toString();
        } catch (JSONException e) {
            return null;
        }
    }
    
    public void fillTableGroupActive(JSONObject jData) throws JSONException, IOException {
    	Grupo grupo;
		DefaultTableModel dtmGroup = new DefaultTableModel();
		DefaultTableModel dtmPool = new DefaultTableModel();
        Vector<Vector<Object>>  data = new  Vector<>();
        Vector<String>          cols = new Vector<>();
        cols.add("grpID");
        cols.add("Desc");
        cols.add("numsecexec");
        
        gDatos.getLstGroups().clear();
        
        JSONArray group = jData.getJSONArray("groupsActive");
        
        System.out.println("lista de groups: "+group.toString());
        
        for (int i=0; i<group.length(); i++) {
        	
        	grupo = new Grupo();
        	grupo = (Grupo) gDatos.serializeJSonStringToObject(group.getJSONObject(i).toString(), Grupo.class);
        	
        	Vector<Object> row = new Vector<>();
        	row.add(grupo.getGrpID());
        	row.add(grupo.getGrpDESC());
        	row.add(grupo.getNumSecExec());
        	
        	//row.add(group.getJSONObject(i).getString("grpID"));
        	//row.add(group.getJSONObject(i).getLong("numSecExec"));
        	data.add(row);
        	gDatos.getLstGroups().add(grupo);
        }
        dtmGroup.setDataVector(data, cols);
        gDatos.getJtableGroupActive().setModel(dtmGroup);
        
        //data.clear();
        //cols.clear();
        
//        try {
//	        JSONArray pools = jData.getJSONArray("pools");
//	        
//	        System.out.println("lista de pools: "+pools.toString());
//	        
//	        for (int i=0; i<pools.length(); i++) {
//	        	Vector<Object> row = new Vector<>();
//	        	row.add(pools.getJSONObject(i).getString("procID"));
//	        	row.add(pools.getJSONObject(i).getString("status"));
//	        	data.add(row);
//	        }
//	        dtmPool.setDataVector(data, cols);
//	        gDatos.getjTablePools().setModel(dtmPool);
//        } catch (Exception e) {
//        	System.out.println("Error: "+e.getMessage());
//        	
//        }
    	
    }
    
    @Override
    public void run() {
    	
        if (gDatos.getServerInfo().isActivePrimaryMonHost()) {
            try {
                Socket skCliente = new Socket(gDatos.getServerInfo().getSrvMonHost(), gDatos.getServerInfo().getMonPort());
                
                OutputStream aux = skCliente.getOutputStream(); 
                DataOutputStream flujo= new DataOutputStream( aux ); 
                String dataSend = sendDataKeep();
                
                logger.info("Generando (tx) hacia Server Monitor Primario: "+dataSend);
                
                flujo.writeUTF( dataSend ); 
                
                
                InputStream inpStr = skCliente.getInputStream();
                DataInputStream dataInput = new DataInputStream(inpStr);
                String response = dataInput.readUTF();
                
                logger.info("Recibiendo (rx)...: "+response);
                JSONObject jHeader = new JSONObject(response);

                try {
                    if (jHeader.getString("result").equals("OK")) {
                        JSONObject jData = jHeader.getJSONObject("data");
                        //Como es una repsuesta no se espera retorno de error del SP
                        //el mismo lo resporta internamente si hay alguno.
                        //gSub.updateAssignedProcess(jData);
                        logger.info("Enviando a actualizar lstAssignedTypeProc...");
                        //gSub.updateAssignedProcess(jData);
                        logger.info("Enviando a actualizar lstPoolProcess...");
                        //gSub.updatePoolProcess(jData);
                        //System.out.println(jData.toString());
                        fillTableGroupActive(jData);
                    } else {
                        if (jHeader.getString("result").equals("error")) {
                            JSONObject jData = jHeader.getJSONObject("data");
                            System.out.println("Error result: "+jData.getInt("errCode")+ " " +jData.getString("errMesg"));
                        }
                    }
                } catch (JSONException e) {
                    logger.error("Error en formato de respuesta");
                }
                skCliente.close();
            } catch (NumberFormatException | IOException e) {
                logger.error(" Error conexion a server de monitoreo primary...."+ e.getMessage());
            }

        } else {
            //
            //Valida conexion a server secundario Backup
            //
            try {
                Socket skCliente = new Socket(gDatos.getServerInfo().getSrvMonHostBack(), gDatos.getServerInfo().getMonPortBack());
                
                OutputStream aux = skCliente.getOutputStream(); 
                DataOutputStream flujo= new DataOutputStream( aux ); 
                String dataSend = sendDataKeep();
                
                logger.info("Generando (tx) hacia Server Monitor Secundario: "+dataSend);
                
                flujo.writeUTF( dataSend ); 
                
                InputStream inpStr = skCliente.getInputStream();
                DataInputStream dataInput = new DataInputStream(inpStr);
                String response = dataInput.readUTF();
                
                logger.info("Recibiendo (rx)...: "+response);
                JSONObject jHeader = new JSONObject(response);

                try {
                    if (jHeader.getString("result").equals("OK")) {
                        JSONObject jData = jHeader.getJSONObject("data");
                        //Como es una repsuesta no se espera retorno de error del SP
                        //el mismo lo resporta internamente si hay alguno.
                        //gSub.updateAssignedProcess(jData);
                        logger.info("Enviando a actualizar lstAssignedTypeProc...");
                        //gSub.updateAssignedProcess(jData);
                        logger.info("Enviando a actualizar lstPoolProcess...");
                        //gSub.updatePoolProcess(jData);
                        System.out.println(jData.toString());
                        fillTableGroupActive(jData);
                    } else {
                        if (jHeader.getString("result").equals("error")) {
                            JSONObject jData = jHeader.getJSONObject("data");
                            System.out.println("Error result: "+jData.getInt("errCode")+ " " +jData.getString("errMesg"));
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error en formato de respuesta");
                }
                skCliente.close();
            } catch (NumberFormatException | IOException e) {
                logger.error(" Error conexion a server de monitoreo backup...."+ e.getMessage());
            }
        }
    }
}
