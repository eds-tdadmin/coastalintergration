package com.coastal.dwds.CoastalIntegration.model;

public class CasingHoleSection {
	
	private String lastcasing;
	private String lastholesize;
	private Double lastshoemd;
	private Double lastshoetvd;
	private String currentholesize;
	
	public String getLastcasing() {
		return lastcasing;
	}
	public void setLastcasing(String lastcasing) {
		this.lastcasing = lastcasing;
	}
	public String getLastholesize() {
		return lastholesize;
	}
	public void setLastholesize(String lastholesize) {
		this.lastholesize = lastholesize;
	}
	public Double getLastshoemd() {
		return lastshoemd;
	}
	public void setLastshoemd(Double d) {
		this.lastshoemd = d;
	}
	public Double getLastshoetvd() {
		return lastshoetvd;
	}
	public void setLastshoetvd(Double lastshoetvd) {
		this.lastshoetvd = lastshoetvd;
	}
	public String getCurrentholesize() {
		return currentholesize;
	}
	public void setCurrentholesize(String currentholesize) {
		this.currentholesize = currentholesize;
	}

}
