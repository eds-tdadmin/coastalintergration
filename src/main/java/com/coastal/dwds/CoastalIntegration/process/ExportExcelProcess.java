package com.coastal.dwds.CoastalIntegration.process;

import java.io.File;
import java.util.HashMap;
import java.util.Properties;

import com.coastal.dwds.CoastalIntegration.constant.Global;
import com.coastal.dwds.CoastalIntegration.model.CustomBeanFactory;

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

	public boolean exportExcel(Properties prop) {
		boolean expResult = true;
		try {

			HashMap<String, Object> parameter = new HashMap<String, Object>();
			parameter.put("company", "Company Name - Coastal Energy");
			// parameter.put("OpSummaryList",ReportBean.getOpSummaryLst());

			JasperReport jasperReport = JasperCompileManager.compileReport(prop.getProperty(Global.JRXML_FILE));
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameter,
					new JRBeanCollectionDataSource(CustomBeanFactory.getBeanCollection()));

			/*
			 * JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
			 * parameter, new JREmptyDataSource());
			 */
			// JasperExportManager.exportReportToPdfFile(jasperPrint, "sample.pdf");

			JRXlsxExporter exporter = new JRXlsxExporter();
			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			File outputFile = new File(prop.getProperty(Global.PSC) + Global.FOLDER_SEPARATOR
					+ prop.getProperty(Global.REPORT_ENGINE_LOCATION) + Global.FOLDER_SEPARATOR
					+ Global.FOLDER_SEPARATOR + prop.getProperty(Global.PROCESS_FOLDER) + Global.FOLDER_SEPARATOR
					+ "\\excelTest.xlsx");
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputFile));
			SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
			configuration.setDetectCellType(true);// Set configuration as you like it!!
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
}