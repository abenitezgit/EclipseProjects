package log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.io.File;
import java.io.FileOutputStream;

import ivrtodb.main;

public class LogRegister {
	public static final int NONE = 0;
	public static final int DEBUG = 1;
	public static final int DETAIL = 2;
	public static final int FULL = 3;

	private static String strLevel = "";

	/** Creates a new instance of LogRegister */
	public LogRegister() 
	{
	}

	public static void out(int loglevel, String log) 
	{
		int level = main.parametros.getIntValor("LogLevel", 1);	// Default level "DEBUG"
		String strNow = getFechaHora("yyyy-MM-dd HH:mm:ss.SSSZ"); 
		String fName = null;

		if (loglevel == 0)
			strLevel = "NONE";
		if (loglevel == 1)
			strLevel = "DEBUG";
		if (loglevel == 2)
			strLevel = "DETAIL";
		if (loglevel == 3)
			strLevel = "FULL";

		if (loglevel <= level) 
		{
			try
			{
				if (main.parametros.getValor("LogToScreen", "OFF").equals("ON"))
					System.out.println("[" + strNow + "] " + strLevel + ": "+ log);

				if (main.parametros.getValor("LogToFile", "ON").equals("ON")) 
				{
					fName = main.parametros.getValor("PathLog") + "ivrtodb.log";
					
					File f = new File(fName);
					
					if (f.length() > main.parametros.getIntValor("MaxSizeLogFile", 1048576)) 
					{
						SimpleDateFormat df = new SimpleDateFormat("_yyyyMMdd_HHmmss");
						String ds = df.format(new Date());
						String oldName = fName.replaceFirst("\\.log",ds) + ".log";

						File f_old = new File(oldName);
						if (f.renameTo(f_old))
							f = new File(fName);
					}
					
					f.createNewFile();
					
					if (f.canWrite()) 
					{
						FileOutputStream osDebugFile = new FileOutputStream(f, true);
						String DebugMessage = "[" + strNow + "] (" + main.VersionApp + ") " + strLevel + ": " + log + "\n";

						osDebugFile.write(DebugMessage.getBytes());
						osDebugFile.close();
					} 
					else 
					{
						System.out.println("[" + strNow + "] LogRegister - f.canWrite() = falso en archivo [" + fName + "]");
					}
				}
			} 
			catch (Exception e) 
			{
				System.out.println("[" + strNow + "] LogRegister - EXCEPTION writing file [" + fName + "] - [" + e.getMessage() + "]");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Funcion para el registro de trx en un archivo csv
	 * 
	 * @param log
	 */
	public static void registerTRX(String log) 
	{
		String fName = null;
		
		try 
		{
			if (main.parametros.getValor("LogToFileTrx", "ON").equals("ON")) 
			{
				fName = main.parametros.getValor("PathMonitoreoTrx", ".\\registroDBTrx.csv");
				File f = new File(fName);
				
				if (f.length() > main.parametros.getIntValor("MaxSizeLogFileTrx", 1048576)) 
				{
					SimpleDateFormat df = new SimpleDateFormat("_yyyyMMdd_HHmmss");
					String ds = df.format(new Date());
					String oldName = fName.replaceFirst("\\.csv", ds) + ".csv";

					File f_old = new File(oldName);
					if (f.renameTo(f_old))
						f = new File(fName);
				}
				
				f.createNewFile();
				
				if (f.canWrite()) 
				{
					FileOutputStream osDebugFile = new FileOutputStream(f, true);
					String DebugMessage = log + "\n";
					// System.out.println(DebugMessage);
					osDebugFile.write(DebugMessage.getBytes());
					osDebugFile.close();
				} 
				else 
				{
					// System.out.println("No puede escribir linea LOG f.canWrite() = falso.");
		        	LogRegister.out(LogRegister.DEBUG, "registerTRX - No se puede escribir en archivo [" + fName + "]"  );
				}
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			LogRegister.out(LogRegister.DEBUG, "registerTRX - EXCEPTION - No se puede escribir en archivo [" + fName + "][" + e.getMessage() + "]");
		}
	}

	public static String getFechaHora(String sFormatoFecha) 
	{
		TimeZone tz = TimeZone.getTimeZone(main.parametros.getValor("TimeZone", "America/Santiago")); // ej:"Chile/Continental"
		SimpleDateFormat fmt = new SimpleDateFormat(sFormatoFecha);
		fmt.setTimeZone(tz);
		String sFechaHora = fmt.format(new java.util.Date());

		return sFechaHora;
	}

}
