package main;

import java.util.HashMap;
import java.util.Map;

public class ftp {
	String id;
	
	Map<String, Object> detail = new HashMap<String, Object>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, Object> getDetail() {
		return detail;
	}

	public void setDetail(Map<String, Object> detail) {
		this.detail = detail;
	}
	
	
}
