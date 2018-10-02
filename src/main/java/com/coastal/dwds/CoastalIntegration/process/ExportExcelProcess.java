package com.coastal.dwds.CoastalIntegration.process;

import java.io.File;
import java.util.HashMap;
import java.util.Properties;

import com.coastal.dwds.CoastalIntegration.common.DateUtil;
import com.coastal.dwds.CoastalIntegration.constant.Global;
import com.coastal.dwds.CoastalIntegration.model.CustomBeanFactory;
import com.coastal.dwds.CoastalIntegration.model.WellInfo;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

// Java Program To Call Jasper Report

public class ExportExcelProcess {

	public boolean exportExcel(Properties prop, WellInfo wellinfo) {
		boolean expResult = true;
		try {

			String reportName = createReportName(wellinfo);

			HashMap<String, Object> parameter = new HashMap<String, Object>();
			parameter.put("company", "Company Name - Coastal Energy");
			// parameter.put("OpSummaryList",ReportBean.getOpSummaryLst());

			JasperReport jasperReport = JasperCompileManager.compileReport(prop.getProperty(Global.JRXML_FILE));
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameter,
					new JRBeanCollectionDataSource(CustomBeanFactory.getBeanCollection()));

			JRXlsxExporter exporter = new JRXlsxExporter();
			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			File outputFile = new File(prop.getProperty(Global.PSC) + Global.FOLDER_SEPARATOR
					+ prop.getProperty(Global.REPORT_ENGINE_LOCATION) + Global.FOLDER_SEPARATOR
					+ Global.FOLDER_SEPARATOR + prop.getProperty(Global.PROCESS_FOLDER) + Global.FOLDER_SEPARATOR
					+ reportName + ".xlsx");
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputFile));
			SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
			configuration.setDetectCellType(true);
			configuration.setCollapseRowSpan(false);
			exporter.setConfiguration(configuration);
			exporter.exportReport();
		} catch (JRException jre) {
			expResult = false;
			jre.printStackTrace();
		} catch (Exception e) {
			expResult = false;
			e.printStackTrace();
		}
		return expResult;

	}

	private String createReportName(WellInfo wellinfo) {

		StringBuilder strBuild = new StringBuilder();
		String reportName = "";

		String year = String.valueOf(DateUtil.getYearFromDate(wellinfo.getReportdate()));
		String reportdate = DateUtil.convertDateToyyyymmddFormat(wellinfo.getReportdate(),
				DateUtil.DATE_YYYYMMDDHHMMSS_FORMAT);

		reportName = strBuild.append(year).append("_").append("Coastal Energy").append("_")
				.append(wellinfo.getWellname()).append("_").append(reportdate).append("_").append("DDR_Report No - ")
				.append(String.valueOf(wellinfo.getReportno())).toString();

		return reportName;
	}
}