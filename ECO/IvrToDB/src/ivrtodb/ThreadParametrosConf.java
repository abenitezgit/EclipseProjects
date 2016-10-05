package ivrtodb;

import log.LogRegister;

/**
 * 
 * @author Mareyes
 * Clase que permite leer el archivo de configuracion para poder refrescar los parametros.
 */
public class ThreadParametrosConf extends Thread {

	 public ThreadParametrosConf() 
	    {
	       
	    }
	    
	    public void run() 
	    {   
	    	while(true)
	    	{	    	
				 try 
				 {	    	 
					 Thread.sleep(1000 * main.parametros.getIntValor("recargaArchivoConfiguracion", 300));
					 main.parametros.readIniFile();
				 } 
				 catch (Exception e) 
				 {
				        LogRegister.out(LogRegister.DEBUG, " ERROR! Leer Archivo de Configuracion" + e.getMessage());
				        e.printStackTrace();
				 }
	    	}
	    }

}
