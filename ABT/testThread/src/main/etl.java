package main;

import java.util.ArrayList;
import java.util.List;

public class etl {
	String id;
	
	List<etlMatch> lstEtlMatch = new ArrayList<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<etlMatch> getLstEtlMatch() {
		return lstEtlMatch;
	}

	public void setLstEtlMatch(List<etlMatch> lstEtlMatch) {
		this.lstEtlMatch = lstEtlMatch;
	}
	
	
}
