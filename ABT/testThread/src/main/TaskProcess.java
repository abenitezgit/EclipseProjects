/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;


/**
 *
 * @author andresbenitez
 */
public class TaskProcess {
    /**
     * Declaraciones de Variables
     */
        String srvID;               //Para asignación de Servico correspondiente
        String grpID;               //Grupo del Proceso
        String typeProc;            //Tipo de Proceso: ETL,OSP,LOR,etc
        String procID;              //ProcID Unico del Proceso: ETL00001, OSP00002,etc
        String intervalID;          //Creado para los Procesos ETL
        String status;              //Status de Operacion del Proceso: Assigned, Ready, Running, Finished, Released
        String uStatus;             //Status de Termino del Proceso en Ejecucion: Success, Error, Warning 
        String insTime;             //Fecha-Hora Inscripcion en Pool
        String updateTime;          //Fecha-Hora cualquier actualización
        String startTime;           //Fecha-Hora que paso a estado Running
        String endTime;             //Fecha-Hora de termino del proceso
        String errMesg;             //Mensaje de Error capturado
        int errNum;                 //Numero de error capturado
        String numSecExec;          //Numero Secuencia de Ejecución del Grupo incorporado en cada proceso
        Object params; 				//Objecto para ingresar parametros del proceso

    /**
     * Getter and Setter
     * @return 
     */
    
    

    public String getGrpID() {
        return grpID;
    }

	public Object getParams() {
		return params;
	}

	public void setParams(Object params) {
		this.params = params;
	}

	public void setGrpID(String grpID) {
        this.grpID = grpID;
    }

    public String getNumSecExec() {
        return numSecExec;
    }

    public void setNumSecExec(String numSecExec) {
        this.numSecExec = numSecExec;
    }

    public String getSrvID() {
        return srvID;
    }

    public void setSrvID(String srvID) {
        this.srvID = srvID;
    }

    public String getIntervalID() {
        return intervalID;
    }

    public synchronized void setIntervalID(String intervalID) {
        this.intervalID = intervalID;
    }

    public String getuStatus() {
        return uStatus;
    }

    public synchronized void setuStatus(String uStatus) {
        this.uStatus = uStatus;
    }

    public String getInsTime() {
        return insTime;
    }

    public synchronized void setInsTime(String insTime) {
        this.insTime = insTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public synchronized void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public synchronized void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getErrMesg() {
        return errMesg;
    }

    public synchronized void setErrMesg(String errMesg) {
        this.errMesg = errMesg;
    }

    public int getErrNum() {
        return errNum;
    }

    public synchronized void setErrNum(int errNum) {
        this.errNum = errNum;
    }

    public String getTypeProc() {
        return typeProc;
    }

    public synchronized void setTypeProc(String typeProc) {
        this.typeProc = typeProc;
    }

    public String getProcID() {
        return procID;
    }

    public synchronized void setProcID(String procID) {
        this.procID = procID;
    }

    public String getStatus() {
        return status;
    }

    public synchronized void setStatus(String status) {
        this.status = status;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public synchronized void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
