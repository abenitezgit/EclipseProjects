/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author andresbenitez
 */
public class Process {
    String procID;
    int nOrder;
    int critical;
    
    Object param = new Object();
    
    public Object getParam() {
		return param;
	}

	public void setParam(Object param) {
		this.param = param;
	}

	Map<String, etl> header = new HashMap<String, etl>();
    
    Map<String, etlMatch> details = new HashMap<String, etlMatch>();

	public Map<String, etl> getHeader() {
		return header;
	}

	public void setHeader(Map<String, etl> header) {
		this.header = header;
	}

	public Map<String, etlMatch> getDetails() {
		return details;
	}

	public void setDetails(Map<String, etlMatch> details) {
		this.details = details;
	}

	public int getCritical() {
        return critical;
    }

    public void setCritical(int critical) {
        this.critical = critical;
    }

    public int getnOrder() {
        return nOrder;
    }

    public void setnOrder(int nOrder) {
        this.nOrder = nOrder;
    }

    public String getProcID() {
        return procID;
    }

    public void setProcID(String procID) {
        this.procID = procID;
    }
}
