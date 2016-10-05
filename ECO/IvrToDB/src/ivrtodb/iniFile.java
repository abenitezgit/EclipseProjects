/*
 * @author Juan A. Cuevas P.
 */

package ivrtodb;

// import java.io.BufferedReader;
// import java.io.FileNotFoundException;
// import java.io.FileReader;
import java.io.IOException;
// import java.util.Hashtable;
// import java.util.StringTokenizer;
import java.io.*;
import java.util.*;

public class iniFile {
	
	public static String fName = null;
    // public static Hashtable<String, String> valores = null; //para guardar lo que leamos del ini
    
    public Properties properties = null;
    
    public iniFile(String ruta) 
    {
    	this.setfName(ruta);
    	this.properties = new Properties();
    	this.readIniFile();
    }
    
    public void readIniFile()
    {
        FileInputStream fis = null;        
        System.out.println("ReadParametersFile [" + this.getfName() + "]");
        
        try 
        {
            fis = new FileInputStream(this.getfName());
            this.properties.load(fis);
            fis.close();
        }
        catch (IOException e) 
        {
            System.out.println("Excepcion al Leer Archivo de Parametros [" + e.getMessage() + "][" + e.toString());
            System.exit(0);
        }
        catch (Throwable e) 
        {
            System.out.println("Excepcion en ReadParametersFile [" + e.getMessage() + "]");
            e.printStackTrace();
            
            try
            {
                if( fis != null) fis.close();
            }
            catch (Exception e2) 
            {
            }
        }        
    }

    public String getfName() 
    {
		return fName;
	}

	public void setfName(String fName) 
	{
		iniFile.fName = fName;
	}

	public String getValor(String clave)
    {
    	return getValor(clave, null);
    }
    
    public String getValor(String clave, String defecto)
    {
    	try
    	{
	        return properties.getProperty(clave, defecto);
    	}
    	catch (Exception e)
    	{
    		return defecto;
    	}
    }
    
    public int getIntValor(String clave)
    {
    	return getIntValor(clave, 0);
    }
       
    public int getIntValor(String clave, int defecto)
    {
    	try
    	{
    		return Integer.parseInt((String) properties.getProperty(clave));
    	}
    	catch (Exception ex)
    	{
    		return defecto;
    	}
    }    
}
