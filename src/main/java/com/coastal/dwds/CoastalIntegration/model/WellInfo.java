package com.coastal.dwds.CoastalIntegration.model;

import java.util.Date;

/**
 * @author Syafiza
 * @version initial
 * Development for Coastal Energy DDR
 *
 */

public class WellInfo {
	private String wellname;
	private String wellboreno;
	private Integer reportno;
	private Date reportdate;
	private String event;
	private String purpose;
	private String site;
	private String block;
	private String supervisor;
	private String rigname;
	private Date spuddate;
	private Date enddate;
	private String country;
	private String superintendant;
	private String geologist;
	private String engineer;
	private Double north;
	private Double east;

	public String getWellname() {
		return wellname;
	}

	public void setWellname(String wellname) {
		this.wellname = wellname;
	}

	public String getWellboreno() {
		return wellboreno;
	}

	public void setWellboreno(String wellboreno) {
		this.wellboreno = wellboreno;
	}

	public Integer getReportno() {
		return reportno;
	}

	public void setReportno(Integer reportno) {
		this.reportno = reportno;
	}

	public Date getReportdate() {
		return reportdate;
	}

	public void setReportdate(Date reportdate) {
		this.reportdate = reportdate;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getBlock() {
		return block;
	}

	public void setBlock(String block) {
		this.block = block;
	}

	public String getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}

	public String getRigname() {
		return rigname;
	}

	public void setRigname(String rigname) {
		this.rigname = rigname;
	}

	public Date getSpuddate() {
		return spuddate;
	}

	public void setSpuddate(Date spuddate) {
		this.spuddate = spuddate;
	}

	public Date getEnddate() {
		return enddate;
	}

	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getSuperintendant() {
		return superintendant;
	}

	public void setSuperintendant(String superintendant) {
		this.superintendant = superintendant;
	}

	public String getGeologist() {
		return geologist;
	}

	public void setGeologist(String geologist) {
		this.geologist = geologist;
	}

	public String getEngineer() {
		return engineer;
	}

	public void setEngineer(String engineer) {
		this.engineer = engineer;
	}

	public Double getNorth() {
		return north;
	}

	public void setNorth(Double north) {
		this.north = north;
	}

	public Double getEast() {
		return east;
	}

	public void setEast(Double east) {
		this.east = east;
	}

}
