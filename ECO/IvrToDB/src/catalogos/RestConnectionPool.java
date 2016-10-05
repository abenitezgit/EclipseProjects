package catalogos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;

public class RestConnectionPool {
	static Logger logger = Logger.getLogger("RestConnectionPool");
	HttpClient client = new DefaultHttpClient();
	
	String urlBase;
	String resource;
	String restType;
	String params;
	String timeOut;
	
    
    
    public RestConnectionPool(String dsURLBase, String dsResource, String dsRestType, String dsParams, String dsTimeOut) {
	    urlBase = dsURLBase;
	    resource = dsResource;
	    restType = dsRestType;
	    params = dsParams;
	    timeOut = dsTimeOut;
    }
    
    
    public synchronized Object execute() throws ClientProtocolException, IOException {
    	Object resultado=null;
		String inputLine;
		StringBuffer responseBuffer = new StringBuffer();
		BufferedReader br;
		int responseCode;
    	
    	switch (restType) {
    		case "GET":
    			HttpGet requestGet = new HttpGet(urlBase+resource);
    			requestGet.addHeader("Content-Type", "text/plain");
    			//requestGet.setHeader("Content-Type", "application/json");
    			requestGet.addHeader("accept", "application/json");
    			//requestGet.addHeader("accept-encoding", "gzip, deflate");
    			//requestGet.addHeader("accept-language", "en-US,en;q=0.8");
    			//requestGet.setHeader("Accept-Encoding", "text/plain");
    			HttpResponse responseGet = client.execute(requestGet);
    	        responseCode = responseGet.getStatusLine().getStatusCode();
    			
    	        if (responseGet.getStatusLine().getStatusCode() == 200 || responseGet.getStatusLine().getStatusCode() == 204) {

    	            br = new BufferedReader(
    	                    new InputStreamReader((responseGet.getEntity().getContent())));


        	        responseBuffer.setLength(0);
        	        while ((inputLine = br.readLine()) != null) {
        	        	logger.debug("line: "+inputLine);
        	        	responseBuffer.append(inputLine);
        	        }
        	        
        	        resultado = new Gson().toJson(responseBuffer.toString());
        	        //resultado = responseBuffer;
    	        }
    	        else{
    	            logger.error(responseGet.getStatusLine().getStatusCode());

    	            throw new RuntimeException("Failed : HTTP error code : "
    	                    + responseGet.getStatusLine().getStatusCode());
    	        }
    	        
    	        resultado = new Gson().toJson(responseBuffer.toString());
    	        //resultado = responseBuffer.toString();
    			//return resultado;
    	        break;
    		case "PUT":
    			responseCode = -1;
    			ArrayList<NameValuePair> postParameters;
    			HttpPut sendPut = new HttpPut(urlBase+resource);
    			StringEntity httpParams =new StringEntity(params,"UTF-8");
    			httpParams.setContentType("application/json");
    	        sendPut.addHeader("content-type", "application/json");
    	        sendPut.addHeader("Accept", "*/*");
    	        sendPut.addHeader("Accept-Encoding", "gzip,deflate,sdch");
    	        sendPut.addHeader("Accept-Language", "en-US,en;q=0.8");
    	        sendPut.setEntity(httpParams);
    	        postParameters = new ArrayList<NameValuePair>();
    	        postParameters.add(new BasicNameValuePair("param1", "param1_value"));
    	        sendPut.setEntity(new UrlEncodedFormEntity(postParameters));
    	        
    	        HttpResponse responsePut = client.execute(sendPut);
    	        responseCode = responsePut.getStatusLine().getStatusCode();
    			
    	        if (responsePut.getStatusLine().getStatusCode() == 200 || responsePut.getStatusLine().getStatusCode() == 204) {

    	            br = new BufferedReader(
    	                    new InputStreamReader((responsePut.getEntity().getContent())));


        	        responseBuffer.setLength(0);
        	        while ((inputLine = br.readLine()) != null) {
        	        	logger.debug("line: "+inputLine);
        	        	responseBuffer.append(inputLine);
        	        }
        	        
        	        resultado = new Gson().toJson(responseBuffer.toString());
    	        }
    	        else{
    	            logger.error(responsePut.getStatusLine().getStatusCode());

    	            throw new RuntimeException("Failed : HTTP error code : "
    	                    + responsePut.getStatusLine().getStatusCode());
    	        }
    			
    	        return resultado;
    		case "POST":
    			responseCode = -1;
    			HttpPost sendPost = new HttpPost(urlBase+resource);
    			StringEntity httpPostParams =new StringEntity(params,"UTF-8");
    			httpPostParams.setContentType("application/json");
    			sendPost.addHeader("content-type", "application/json");
    			sendPost.addHeader("Accept", "*/*");
    			sendPost.addHeader("Accept-Encoding", "gzip,deflate,sdch");
    			sendPost.addHeader("Accept-Language", "en-US,en;q=0.8");
    			sendPost.setEntity(httpPostParams);
    			
    	        HttpResponse responsePost = client.execute(sendPost);
    	        responseCode = responsePost.getStatusLine().getStatusCode();
    			
    	        if (responsePost.getStatusLine().getStatusCode() == 200 || responsePost.getStatusLine().getStatusCode() == 204) {

    	            br = new BufferedReader(
    	                    new InputStreamReader((responsePost.getEntity().getContent())));

        	        responseBuffer.setLength(0);
        	        while ((inputLine = br.readLine()) != null) {
        	        	logger.debug("line: "+inputLine);
        	        	responseBuffer.append(inputLine);
        	        }
        	        
        	        resultado = new Gson().toJson(responseBuffer.toString());
    	        }
    	        else{
    	            logger.error(responsePost.getStatusLine().getStatusCode());

    	            throw new RuntimeException("Failed : HTTP error code : "
    	                    + responsePost.getStatusLine().getStatusCode());
    	        }
    			
    			
    			return resultado;
			default:
				break;
    	}
    	client.getConnectionManager().shutdown();
    	return  resultado;
    }
    
}
