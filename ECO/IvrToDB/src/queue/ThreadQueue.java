package queue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.Gson;

import log.LogRegister;
import socketServer.ClientRequest;
import catalogos.DBConnectionPool;


/**
 * @author lduhalde
 */

public class ThreadQueue implements Runnable{
	//private static ArrayBlockingQueue<Task> taskQueue = null;
	private static LinkedBlockingQueue<Task> taskQueue = null;
	Object retorno = null;

	public ThreadQueue() {

		//taskQueue = new ArrayBlockingQueue<Task>(main.parametros.getIntValor("MaxSizeQueue", 10));
		taskQueue = new LinkedBlockingQueue<Task>();
	}

	public Object addTask(DBConnectionPool dbConnectionPool, ClientRequest request, String requestID) {
		
		Task task = new Task(dbConnectionPool, request, requestID);
		Map<String, String> mapResultado = new HashMap<String, String>();
		boolean added=taskQueue.add(task);
		if(added){
			mapResultado.put("taskID", task.getTaskID());
			mapResultado.put("MSG", "Transacción añadida con TaskID "+task.getTaskID());
			mapResultado.put("RC", "0");
		}
		else{
			mapResultado.put("errorMessage", "Transacción no pudo ser agregada a la cola");
			mapResultado.put("MSG", "Transacción no pudo ser agregada a la cola");
			mapResultado.put("RC", "-1");
		}
		return new Gson().toJson(mapResultado);
	}
	
	public void run() {

	
		while (true) {

			try {
				Task task = taskQueue.take();
				
				task.execute();
				

			} catch (InterruptedException e) {
				LogRegister.out(LogRegister.DEBUG, "Hilo interrumpido [" + e.getMessage() + "]");
			}
		}
	}


}