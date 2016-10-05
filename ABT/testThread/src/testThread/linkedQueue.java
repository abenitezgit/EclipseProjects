package testThread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class linkedQueue {

	public static void main(String[] args) {
		BlockingQueue<String> bounded   = new LinkedBlockingQueue<String>(1024);
		
		try {
			bounded.put("hola");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			System.out.println(bounded.take());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}

}
