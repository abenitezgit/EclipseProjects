package ivrtodb;

import java.util.Enumeration;
import java.util.Hashtable;
import log.LogRegister;

public class ThreadManageCache extends Thread {
	
	public static Hashtable<String, Hashtable<String, Object>> htCache = new Hashtable<String, Hashtable<String, Object>>();
	public static int		maxCache = main.parametros.getIntValor("maxCache", 100);
	
	public ThreadManageCache() {
	}
	    
 	public static void addResult(String key, Object result, long cache) {
 		
 		if ( htCache.size() < maxCache ) {
	 		Hashtable<String, Object> htResult = new Hashtable<String, Object>();
			htResult.put("result", result);
			htResult.put("created", System.currentTimeMillis()/1000);	
			htResult.put("cacheTime", cache);
			htCache.put(key, htResult);
 		} else
 			LogRegister.out(LogRegister.DETAIL, "[ThreadManageCache] WARN: Resultset no se pudo ingresar a cache, ha superado el limite de memoria: " + ThreadManageCache.htCache.size());
		
 	}
 	
 	private void cleanMemory() {
 		Enumeration<String> enumKey = htCache.keys();
 		while(enumKey.hasMoreElements()) {
 		    String key = enumKey.nextElement();
 		    Hashtable<?, ?> htResult = (Hashtable<?, ?>) htCache.get(key);
 		    
 		   long currentTime = (System.currentTimeMillis() / 1000);
 		   long createdCache = (long) htResult.get("created");
 		   long cacheTime = (long) htResult.get("cacheTime");
 		   
 		   if ( currentTime > (createdCache + cacheTime) ) {
 			   htCache.remove(key);
 		   }
 		}
 	}
	 	
    public void run() 
    {   
    	while(true){
 
	    	 try {
	    		// cada un minuto revisa si debe eliminar cache obsoleto (segun tiempo definido en el servicio)
				Thread.sleep(60000);
				cleanMemory();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	     } 

    }

}
