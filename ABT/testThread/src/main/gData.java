package main;

public class gData {
	static boolean isReadParamFile;
	static boolean isSocketServerActive;
	static int socketPort;
	
	
	public gData() {
		isReadParamFile = false;
		isSocketServerActive = false;
		
	}
	
	/**
	 * Getter and Setter
	 */
	

	public static boolean isReadParamFile() {
		return isReadParamFile;
	}


	public static boolean isSocketServerActive() {
		return isSocketServerActive;
	}

	public static void setSocketServerActive(boolean isSocketServerActive) {
		gData.isSocketServerActive = isSocketServerActive;
	}

	public static void setReadParamFile(boolean isReadParamFile) {
		gData.isReadParamFile = isReadParamFile;
	}

	public static int getSocketPort() {
		return socketPort;
	}

	public static void setSocketPort(int socketPort) {
		gData.socketPort = socketPort;
	}
	

}
