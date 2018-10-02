package com.coastal.dwds.CoastalIntegration.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.coastal.dwds.CoastalIntegration.constant.Global;
import com.coastal.dwds.CoastalIntegration.model.CasingHoleSection;
import com.coastal.dwds.CoastalIntegration.model.Costs;
import com.coastal.dwds.CoastalIntegration.model.CustomBeanFactory;
import com.coastal.dwds.CoastalIntegration.model.DepthDays;
import com.coastal.dwds.CoastalIntegration.model.ElevationData;
import com.coastal.dwds.CoastalIntegration.model.Npt;
import com.coastal.dwds.CoastalIntegration.model.OpSummary;
import com.coastal.dwds.CoastalIntegration.model.ReportBean;
import com.coastal.dwds.CoastalIntegration.model.Status;
import com.coastal.dwds.CoastalIntegration.model.WellInfo;

import net.sf.jasperreports.engine.JRException;

public class ReadExcelProcess {

	public boolean readExcelFile(Properties prop) throws JRException {

		boolean exportResult = false;
		ExportExcelProcess expExl = new ExportExcelProcess();
		WellInfo wellinfo = new WellInfo();
		DepthDays depthdays = new DepthDays();
		CasingHoleSection casinghole = new CasingHoleSection();
		Costs costinfo = new Costs();
		Npt nptinfo = new Npt();
		ElevationData elevationinfo = new ElevationData();
		Status statusinfo = new Status();
		List<OpSummary> opSummaryLst = new ArrayList<OpSummary>();

		try {
			String sourceFolder = prop.getProperty(Global.COPY_FOLDER);
			File aDirectory = new File(sourceFolder);

			// get a listing of all files in the directory
			String[] filesInDir = aDirectory.list();

			// have everything i need, just print it now
			for (int i = 0; i < filesInDir.length; i++) {

				FileInputStream excelFile = new FileInputStream(new File(sourceFolder + filesInDir[i]));
				Workbook workbook = new XSSFWorkbook(excelFile);
				Sheet datatypeSheet = workbook.getSheetAt(0);

				Row startRow = findRow(datatypeSheet, "DAILY DRILLING REPORT");
				Row depthRow = findRow(datatypeSheet, "DEPTH DAYS");
				Row statusRow = findRow(datatypeSheet, "STATUS");
				Row opsSumRow = findRow(datatypeSheet, "OPERATION SUMMARY");
				Row bitDataRow = findRow(datatypeSheet, "BIT DATA");
				Row bhaRow = findRow(datatypeSheet, "BHA");
				Row gasReadingRow = findRow(datatypeSheet, "GAS READINGS");
				Row pumpRow = findRow(datatypeSheet, "PUMP/HYDRAULICs");
				Row lotRow = findRow(datatypeSheet, "LOT/FIT");
				Row logisticRow = findRow(datatypeSheet, "LOGISTIC SUPPORT");
				Row bulkRow = findRow(datatypeSheet, "BULKS");
				Row weatherRow = findRow(datatypeSheet, "WEATHER");
				Row safetyRow = findRow(datatypeSheet, "SAFETY");
				Row surveyRow = findRow(datatypeSheet, "SURVEYS");

				System.out.println(startRow.getRowNum() + "," + depthRow.getRowNum() + "," + statusRow.getRowNum());

				if (startRow != null && depthRow != null) {
					wellinfo = setWellInfo(datatypeSheet, startRow.getRowNum(), depthRow.getRowNum());
					elevationinfo = setElevationInfo(datatypeSheet, startRow.getRowNum(), depthRow.getRowNum());
				}
				if (depthRow != null && statusRow != null) {
					depthdays = setDepthDaysInfo(datatypeSheet, depthRow.getRowNum(), statusRow.getRowNum());
					casinghole = setCasingHoleInfo(datatypeSheet, depthRow.getRowNum(), statusRow.getRowNum());
					costinfo = setCostInfo(datatypeSheet, depthRow.getRowNum(), statusRow.getRowNum());
					nptinfo = setNptInfo(datatypeSheet, depthRow.getRowNum(), statusRow.getRowNum());
				}
				if (statusRow != null && opsSumRow != null) {
					statusinfo = setStatusInfo(datatypeSheet, statusRow.getRowNum(), opsSumRow.getRowNum());
				}
				if (opsSumRow != null && bitDataRow != null) {
					opSummaryLst = setOpSummaryInfo(datatypeSheet, opsSumRow.getRowNum(), bitDataRow.getRowNum());
				}

				ReportBean reportBean = new ReportBean(wellinfo, depthdays, casinghole, costinfo, nptinfo,
						elevationinfo, statusinfo, opSummaryLst);

				CustomBeanFactory.setBeanArray(reportBean);

				exportResult = expExl.exportExcel(prop, wellinfo);

			}

		} catch (FileNotFoundException fnfe) {
			exportResult = false;
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			exportResult = false;
			ioe.printStackTrace();
		}

		return exportResult;

	}

	// To get start row and end row based on the input cell field
	public static Row findRow(Sheet sheet, String textToFind) {
		Row resultRow = null;
		for (Row row : sheet) {
			for (Cell cell : row) {
				if (cell.toString().equalsIgnoreCase(textToFind.trim())) {
					resultRow = row;
					return resultRow;
				}
			}
		}
		return resultRow;
	}

	private static WellInfo setWellInfo(Sheet sheet, int start, int end) {

		WellInfo excelwellinfo = new WellInfo();

		// retrieve data for 1st row only
		Row row = sheet.getRow(start + 1);
		if (row.getCell(3) != null) {
			excelwellinfo.setWellname(row.getCell(3).getStringCellValue());
		}
		if (row.getCell(8) != null) {
			excelwellinfo.setWellboreno(row.getCell(8).getStringCellValue());
		}
		if (row.getCell(12) != null) {
			excelwellinfo.setReportno((int) row.getCell(12).getNumericCellValue());
		}
		if (row.getCell(15) != null) {
			excelwellinfo.setReportdate(row.getCell(15).getDateCellValue());
		}

		// retrieve data for the rest of well info
		for (int rowNum = start + 3; rowNum <= end; rowNum++) {
			row = sheet.getRow(rowNum);

			if (row.getRowNum() == 3) {
				if (row.getCell(3) != null) {
					excelwellinfo.setEvent(row.getCell(3).getStringCellValue());
				}
				if (row.getCell(12) != null) {
					excelwellinfo.setRigname(row.getCell(12).getStringCellValue());
				}
				if (row.getCell(17) != null) {
					excelwellinfo.setNorth(row.getCell(17).getNumericCellValue());
				}
			}
			if (row.getRowNum() == 4) {
				if (row.getCell(3) != null) {
					excelwellinfo.setPurpose(row.getCell(3).getStringCellValue());
				}
				if (row.getCell(12) != null) {
					excelwellinfo.setSpuddate(row.getCell(12).getDateCellValue());
				}
				if (row.getCell(14) != null) {
					excelwellinfo.setCountry(row.getCell(14).getStringCellValue());
				}
				if (row.getCell(17) != null) {
					excelwellinfo.setEast(row.getCell(17).getNumericCellValue());
				}
			}
			if (row.getRowNum() == 5) {
				if (row.getCell(3) != null) {
					excelwellinfo.setSite(row.getCell(3).getStringCellValue());
				}
				if (row.getCell(12) != null) {
					excelwellinfo.setEnddate(row.getCell(12).getDateCellValue());
				}
				if (row.getCell(16) != null) {
					excelwellinfo.setGeologist(row.getCell(16).getStringCellValue());
				}

			}
			if (row.getRowNum() == 6) {
				if (row.getCell(3) != null) {
					excelwellinfo.setBlock(row.getCell(3).getStringCellValue());
				}
				if (row.getCell(9) != null) {
					excelwellinfo.setSupervisor(row.getCell(9).getStringCellValue());
				}
				if (row.getCell(13) != null) {
					excelwellinfo.setSuperintendant(row.getCell(13).getStringCellValue());
				}
				if (row.getCell(16) != null) {
					excelwellinfo.setEngineer(row.getCell(16).getStringCellValue());
				}

			}
		}

		return excelwellinfo;

	}

	private static DepthDays setDepthDaysInfo(Sheet sheet, int start, int end) {
		DepthDays depthdaysinfo = new DepthDays();
		Row row = null;

		for (int rowNum = start + 1; rowNum <= end; rowNum++) {
			row = sheet.getRow(rowNum);

			if (row.getRowNum() == 8) {
				if (row.getCell(3) != null) {
					depthdaysinfo.setDol(row.getCell(3).getNumericCellValue());
				}
				if (row.getCell(7) != null) {
					depthdaysinfo.setMd(row.getCell(7).getNumericCellValue());
				}
				if (row.getCell(11) != null) {
					depthdaysinfo.setDrlghrs(row.getCell(11).getNumericCellValue());
				}

			}
			if (row.getRowNum() == 9) {
				if (row.getCell(3) != null) {
					depthdaysinfo.setDfs(row.getCell(3).getNumericCellValue());
				}
				if (row.getCell(7) != null) {
					depthdaysinfo.setTvd(row.getCell(7).getNumericCellValue());
				}
				if (row.getCell(11) != null) {
					depthdaysinfo.setCumrothrs(row.getCell(11).getNumericCellValue());
				}

			}
			if (row.getRowNum() == 10) {
				if (row.getCell(3) != null) {
					depthdaysinfo.setTotaldays(row.getCell(3).getNumericCellValue());
				}
				if (row.getCell(7) != null) {
					depthdaysinfo.setProgress(row.getCell(7).getNumericCellValue());
				}
				if (row.getCell(11) != null) {
					depthdaysinfo.setAvgrop(row.getCell(11).getNumericCellValue());
				}
			}
			if (row.getRowNum() == 11) {
				if (row.getCell(3) != null) {
					depthdaysinfo.setEstdays(row.getCell(3).getNumericCellValue());
				}
				if (row.getCell(7) != null) {
					depthdaysinfo.setMdplan(row.getCell(7).getNumericCellValue());
				}
			}

		}

		return depthdaysinfo;
	}

	private static CasingHoleSection setCasingHoleInfo(Sheet sheet, int start, int end) {
		CasingHoleSection casingholeinfo = new CasingHoleSection();
		Row row = null;

		for (int rowNum = start + 1; rowNum <= end; rowNum++) {
			row = sheet.getRow(rowNum);

			if (row.getRowNum() == 8) {
				if (row.getCell(14) != null) {
					casingholeinfo.setLastcasing(row.getCell(14).getStringCellValue());
				}

			}
			if (row.getRowNum() == 9) {
				if (row.getCell(14) != null) {
					casingholeinfo.setLastholesize(row.getCell(14).getStringCellValue());
				}

			}
			if (row.getRowNum() == 10) {
				if (row.getCell(14) != null) {
					casingholeinfo.setLastshoemd(row.getCell(14).getNumericCellValue());
				}

			}
			if (row.getRowNum() == 11) {
				if (row.getCell(14) != null) {
					casingholeinfo.setLastshoetvd(row.getCell(14).getNumericCellValue());
				}

			}
			if (row.getRowNum() == 12) {
				if (row.getCell(14) != null) {
					casingholeinfo.setCurrentholesize(row.getCell(14).getStringCellValue());
				}

			}

		}

		return casingholeinfo;
	}

	private static Costs setCostInfo(Sheet sheet, int start, int end) {
		Costs costinfo = new Costs();
		Row row = null;
		DataFormatter formatter = new DataFormatter();

		for (int rowNum = start + 1; rowNum <= end; rowNum++) {
			row = sheet.getRow(rowNum);
			if (row.getRowNum() == 8) {
				if (row.getCell(17) != null) {
					costinfo.setDailycost(row.getCell(17).getNumericCellValue());
				}

			}
			if (row.getRowNum() == 9) {
				if (row.getCell(17) != null) {
					costinfo.setCummcost(row.getCell(17).getNumericCellValue());
				}

			}
			if (row.getRowNum() == 10) {
				if (row.getCell(17) != null) {
					costinfo.setAfecost(row.getCell(17).getNumericCellValue());
				}

			}
			if (row.getRowNum() == 11) {
				if (row.getCell(17) != null) {
					costinfo.setAfeno(row.getCell(17).getStringCellValue());
				}

			}
			if (row.getRowNum() == 12) {
				if (row.getCell(17) != null) {
					costinfo.setExpenditure(formatter.formatCellValue(row.getCell(17)));
				}

			}

		}
		return costinfo;
	}

	private static Npt setNptInfo(Sheet sheet, int start, int end) {
		Npt nptinfo = new Npt();
		Row row = null;

		for (int rowNum = start + 1; rowNum <= end; rowNum++) {
			row = sheet.getRow(rowNum);
			if (row.getRowNum() == 12) {
				if (row.getCell(3) != null) {
					nptinfo.setDailynpt(row.getCell(3).getNumericCellValue());
				}
				if (row.getCell(7) != null) {
					nptinfo.setCummnpt(row.getCell(7).getNumericCellValue());
				}
			}

		}
		return nptinfo;
	}

	private static ElevationData setElevationInfo(Sheet sheet, int start, int end) {
		ElevationData elevationinfo = new ElevationData();
		Row row = null;

		for (int rowNum = start + 1; rowNum <= end; rowNum++) {
			row = sheet.getRow(rowNum);
			if (row.getRowNum() == 3) {
				if (row.getCell(9) != null) {
					elevationinfo.setRtmsl(row.getCell(9).getNumericCellValue());
				}
			}
			if (row.getRowNum() == 4) {
				if (row.getCell(9) != null) {
					elevationinfo.setMslml(row.getCell(9).getNumericCellValue());
				}
			}
			if (row.getRowNum() == 5) {
				if (row.getCell(9) != null) {
					elevationinfo.setWhmsl(row.getCell(9).getNumericCellValue());
				}
			}

		}
		return elevationinfo;

	}

	private static Status setStatusInfo(Sheet sheet, int start, int end) {
		Status statusinfo = new Status();
		Row row = null;

		for (int rowNum = start + 1; rowNum <= end; rowNum++) {
			row = sheet.getRow(rowNum);

			if (row.getRowNum() == 14) {
				if (row.getCell(3) != null) {
					statusinfo.setCurrentstatus(row.getCell(3).getStringCellValue());
				}

			}
			if (row.getRowNum() == 15) {
				if (row.getCell(3) != null) {
					statusinfo.setSummary(row.getCell(3).getStringCellValue());
				}

			}
			if (row.getRowNum() == 16) {
				if (row.getCell(3) != null) {
					statusinfo.setForecast(row.getCell(3).getStringCellValue());
				}

			}
		}

		return statusinfo;
	}

	private static List<OpSummary> setOpSummaryInfo(Sheet sheet, int start, int end) {
		List<OpSummary> opSummaryLst = new ArrayList<OpSummary>();
		OpSummary opsSummary = null;

		Row row = null;

		for (int rowNum = start + 3; rowNum < end; rowNum++) {
			opsSummary = new OpSummary();
			row = sheet.getRow(rowNum);

			if (row.getCell(1).getDateCellValue() != null && row.getCell(2).getDateCellValue() != null) {
				opsSummary.setFromdt(row.getCell(1).getDateCellValue());
				System.out.println("--- opsummary check 1 ----" + opsSummary.getFromdt());
				if (row.getCell(2) != null) {
					opsSummary.setTodt(row.getCell(2).getDateCellValue());
				}
				if (row.getCell(3) != null) {
					opsSummary.setHrs(row.getCell(3).getNumericCellValue());
				}
				if (row.getCell(4) != null) {
					opsSummary.setPhase(row.getCell(4).getStringCellValue());
				}
				if (row.getCell(5) != null) {
					opsSummary.setOperation(row.getCell(5).getStringCellValue());
				}
				if (row.getCell(6) != null) {
					opsSummary.setNptcode(row.getCell(6).getStringCellValue());
				}
				if (row.getCell(7) != null) {
					opsSummary.setMdfrom(row.getCell(7).getNumericCellValue());
				}
				if (row.getCell(10) != null) {
					opsSummary.setDesc(row.getCell(10).getStringCellValue());
				}
				opSummaryLst.add(opsSummary);
			}
		}

		return opSummaryLst;

	}

}