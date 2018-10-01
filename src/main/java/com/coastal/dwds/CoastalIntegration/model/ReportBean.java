package com.coastal.dwds.CoastalIntegration.model;

import java.util.List;

public class ReportBean {

	private WellInfo wellinfo;
	private DepthDays depthdays;
	private CasingHoleSection casinginfo;
	private Costs costinfo;
	private Npt nptinfo;
	private ElevationData elevationinfo;
	private Status statusinfo;
	private List<OpSummary> opSummaryLst;

	public ReportBean(WellInfo c_wellinfo, DepthDays c_depthdays, CasingHoleSection c_casinginfo, Costs c_costinfo,
			Npt c_nptinfo, ElevationData c_elevationinfo, Status c_statusinfo, List<OpSummary> c_opsummarylst) {
		setWellinfo(c_wellinfo);
		setDepthdays(c_depthdays);
		setCasinginfo(c_casinginfo);
		setCostinfo(c_costinfo);
		setNptinfo(c_nptinfo);
		setElevationinfo(c_elevationinfo);
		setStatusinfo(c_statusinfo);
		setOpSummaryLst(c_opsummarylst);
	}

	public ReportBean getReport() {
		return this;
	}

	public WellInfo getWellinfo() {
		return wellinfo;
	}

	public void setWellinfo(WellInfo wellinfo) {
		this.wellinfo = wellinfo;
	}

	public DepthDays getDepthdays() {
		return depthdays;
	}

	public void setDepthdays(DepthDays depthdays) {
		this.depthdays = depthdays;
	}

	public CasingHoleSection getCasinginfo() {
		return casinginfo;
	}

	public void setCasinginfo(CasingHoleSection casinginfo) {
		this.casinginfo = casinginfo;
	}

	public Costs getCostinfo() {
		return costinfo;
	}

	public void setCostinfo(Costs costinfo) {
		this.costinfo = costinfo;
	}

	public Npt getNptinfo() {
		return nptinfo;
	}

	public void setNptinfo(Npt nptinfo) {
		this.nptinfo = nptinfo;
	}

	public ElevationData getElevationinfo() {
		return elevationinfo;
	}

	public void setElevationinfo(ElevationData elevationinfo) {
		this.elevationinfo = elevationinfo;
	}

	public Status getStatusinfo() {
		return statusinfo;
	}

	public void setStatusinfo(Status statusinfo) {
		this.statusinfo = statusinfo;
	}

	public List<OpSummary> getOpSummaryLst() {
		return opSummaryLst;
	}

	public void setOpSummaryLst(List<OpSummary> opSummaryLst) {
		this.opSummaryLst = opSummaryLst;
	}

}
