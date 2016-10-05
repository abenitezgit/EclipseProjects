package testThread;

import java.util.concurrent.ThreadLocalRandom;

public class concurrent {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int r = ThreadLocalRandom.current() .nextInt(4, 77);
		
		System.out.println(r);
		
		PoolProcess pool = new PoolProcess();
		
		pool.setGrpID("001");
		pool.setProcID("prco001");
		
		
		
	}

}
