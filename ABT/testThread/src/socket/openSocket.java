package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class openSocket {
	
	
	public static void main(String args[]) throws UnknownHostException, IOException {
		Socket socket = new Socket("localhost", 50090);
		
	    PrintWriter out =
	            new PrintWriter(socket.getOutputStream(), true);
	    
        BufferedReader in =
            new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        BufferedReader stdIn =
	            new BufferedReader(
	                new InputStreamReader(System.in));
        
        out.print("fola");
		
        stdIn.close();
        in.close();
        out.close();
        socket.close();
	}

}
