package queue;

import ivrtodb.main;
import log.LogRegister;
import socketServer.ClientRequest;
import catalogos.DBConnectionPool;

/**
 * @author lduhalde
 */

public class Task {
	public enum State {CREATED, DONE, ERROR;}

	private DBConnectionPool dbConnectionPool;
	private ClientRequest request;
	private String requestID;
	private String taskID;
	private State state;

	public Task(){

	}

	public Task(DBConnectionPool dbConnectionPool, ClientRequest request, String requestID)
	{
		this.dbConnectionPool=dbConnectionPool;
		this.request=request;
		this.requestID=requestID;
		main.iTaskID++;
		long init = System.currentTimeMillis();
		
		taskID=init+"."+main.iTaskID;

		state=State.CREATED;
	}

	public String getTaskID() {
		return taskID;
	}

	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	void execute() {
		long milisegundoActual = System.currentTimeMillis();
		
		Object retorno = dbConnectionPool.execute(request.getQuery(), request.getParameters(), request.getOutParameters(), request.getDataTypes(), String.valueOf(request.getSelect()), requestID);
		
		state=State.DONE;
		long milisegundoFinal = System.currentTimeMillis();
        LogRegister.out(LogRegister.DETAIL, "[" + requestID + "] Task.execute - Servicio [" + request.getServicio() + "] - Duración: " + (milisegundoFinal - milisegundoActual) + "ms - Resultado: [" + retorno + "]");
		
	}
}
