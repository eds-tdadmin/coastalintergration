package com.coastal.dwds.CoastalIntegration.model;

import java.util.Date;

public class OpSummary {
	private Date fromdt;
	private Date todt;
	private Double hrs;
	private String phase;
	private String operation;
	private String nptcode;
	private Double mdfrom;
	private String desc;

	public Date getFromdt() {
		return fromdt;
	}

	public void setFromdt(Date fromdt) {
		this.fromdt = fromdt;
	}

	public Date getTodt() {
		return todt;
	}

	public void setTodt(Date todt) {
		this.todt = todt;
	}

	public Double getHrs() {
		return hrs;
	}

	public void setHrs(Double hrs) {
		this.hrs = hrs;
	}

	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getNptcode() {
		return nptcode;
	}

	public void setNptcode(String nptcode) {
		this.nptcode = nptcode;
	}

	public Double getMdfrom() {
		return mdfrom;
	}

	public void setMdfrom(Double mdfrom) {
		this.mdfrom = mdfrom;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
