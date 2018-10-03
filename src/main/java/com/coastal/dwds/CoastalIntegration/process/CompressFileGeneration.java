/**
 * 
 */
package com.coastal.dwds.CoastalIntegration.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.coastal.dwds.CoastalIntegration.common.DateUtil;
import com.coastal.dwds.CoastalIntegration.constant.Global;
import com.coastal.dwds.CoastalIntegration.constant.GlobalCellsConstants;

/**
 * @author Syafiza
 **/
public class CompressFileGeneration {
	public static final Logger log = LogManager.getLogger(CompressFileGeneration.class);
	MailExcelFileData data = new MailExcelFileData();

	/**
	 * @param reportEngineFileBkpLoc
	 * @param reportEngineProcessFileLocation
	 * @param prop
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 *             process for Report generated files
	 * @throws MessagingException
	 */
	public boolean sendNotificationEmail(File reportEngineProcessFileLocation, Properties prop)
			throws ClassNotFoundException, SQLException, IOException, MessagingException {
		boolean success = false;
		boolean filesExist = false;
		StringBuffer mailBuffer = new StringBuffer();
		// directory files to store in arrayList
		ArrayList<String> fileNames = new ArrayList<String>();
		if (reportEngineProcessFileLocation.exists()) {
			File[] listOfFiles = reportEngineProcessFileLocation.listFiles();
			if (listOfFiles.length > 0) {
				for (int i = 0; i < listOfFiles.length; i++) {
					String fileName = listOfFiles[i].getName();
					File orgFile = new File(reportEngineProcessFileLocation + Global.FOLDER_SEPARATOR + fileName);
					if (orgFile.length() > 0) {
						fileNames.add(fileName);
					}
				}
				for (String file : fileNames) {
					File inputFile = new File(reportEngineProcessFileLocation + File.separator + file);
					// store file into output folder
					StringBuffer buffer = new StringBuffer();
					buffer.append(Global.NEXT_LINE);
					success = retrieveBlankFields(inputFile, prop, buffer, file);
					buffer.append(Global.NEXT_LINE);
					mailBuffer.append(buffer.toString());
					filesExist = true;
				}
				System.out.println("sending mail notification for mandatory fields");
				EmailSender mail = new EmailSender();
				// send mail notification for null values in mandatory fields
				if (success) {
					success = mail.sendMailNotification(mailBuffer, reportEngineProcessFileLocation, prop);
				}
			}
		}
		if (!filesExist) {
			System.out.println("DDR Reports doesn't exist in process location " + reportEngineProcessFileLocation);
			EmailSender sender = new EmailSender();
			sender.sendErrorLogEmail("Process Didn't generated the DDR Reports on " + DateUtil.getCurrentDate(),
					"As there is no updates on wells or new well information exist, System didn't generated DDR Reports on "
							+ DateUtil.getCurrentDate(),
					prop);
		}

		return success;
	}

	public boolean sendCompressFile(Properties prop) throws Exception {

		boolean success = false;
		boolean reportsExist = false;
		// create or update folder structure and look for input files
		File reportEnglineLocation = new File(prop.getProperty(Global.PSC) + Global.FOLDER_SEPARATOR
				+ prop.getProperty(Global.REPORT_ENGINE_LOCATION) + Global.FOLDER_SEPARATOR);
		File reportEngineProcessFileLocation = new File(reportEnglineLocation + Global.FOLDER_SEPARATOR
				+ prop.getProperty(Global.PROCESS_FOLDER) + Global.FOLDER_SEPARATOR);
		// create or update folder structure for backup files
		File reportEngineFileBkpLoc = new File(reportEnglineLocation + Global.FOLDER_SEPARATOR
				+ prop.getProperty(Global.BACKUP_FOLDER) + Global.FOLDER_SEPARATOR);
		if (!reportEngineFileBkpLoc.exists()) {
			reportEngineFileBkpLoc.mkdirs();
		}

		// directory files to store in arrayList
		ArrayList<String> fileNames = new ArrayList<String>();
		if (reportEngineProcessFileLocation.exists()) {
			File[] listOfFiles = reportEngineProcessFileLocation.listFiles();
			if (listOfFiles.length > 0) {
				for (int i = 0; i < listOfFiles.length; i++) {
					String fileName = listOfFiles[i].getName();
					File orgFile = new File(reportEngineProcessFileLocation + Global.FOLDER_SEPARATOR + fileName);
					if (orgFile.length() > 0) {
						fileNames.add(fileName);
						reportsExist = true;
						File inputFile = new File(reportEngineProcessFileLocation + Global.FOLDER_SEPARATOR + fileName);
						// backup engineer updated file
						File targetFile = new File(
								reportEngineFileBkpLoc + Global.FOLDER_SEPARATOR + FilenameUtils.getName(fileName));
						if (targetFile.exists()) {
							targetFile.delete();
						}
						FileUtils.copyFile(inputFile, targetFile);
						success = data.sendMailExcelFile(
								new File(reportEngineProcessFileLocation + File.separator + fileName),
								reportEngineProcessFileLocation, prop);
						inputFile.delete();
					}
				}
			}
		}
		if (!reportsExist) {
			System.out.println("Updated DDR Reports doesn't exist in process location " + reportEnglineLocation);
		}
		return success;

	}

	@SuppressWarnings({ "static-access", "unused" })
	public boolean retrieveBlankFields(File inputFile, Properties prop, StringBuffer sb, String sourceFile)
			throws SQLException, IOException, MessagingException, ClassNotFoundException {
		// StringBuffer sb = new StringBuffer();
		Map<String, String> blankFieldMap = new HashMap<String, String>();
		Map<String, String> dynamicBlankFieldMap = new HashMap<String, String>();
		MailExcelFileData fileData = new MailExcelFileData();
		ConstantCellData constantData = new ConstantCellData();
		boolean success = false;
		String tempName = null;
		try {
			tempName = FilenameUtils.getName(inputFile.toString());
			if (!tempName.startsWith("~$")) {
				// create source file stream
				InputStream fis = new FileInputStream(inputFile);
				if (fis.read() <= 0) {
					System.out.println(
							"The DDR Report(" + tempName + ") is not processed due to incomplete data in the file. ");
				} else {
					Workbook wb = WorkbookFactory.create(new FileInputStream(inputFile));
					Sheet tableSheet = wb.getSheetAt(0);
					// Iterate the cells for constant tables
					for (Row constantRow : tableSheet) {
						for (Cell cell : constantRow) {
							// get the excel file name from row no 2
							blankFieldMap.put(GlobalCellsConstants.PSCNAME, fileData.getNameCell(tableSheet).trim());
							// Iterate the general information from start and
							// end of
							// this rows
							Row genralStartRow = fileData.findRow(tableSheet, GlobalCellsConstants.WELL_NAME);
							Row generalEndRow = fileData.findRow(tableSheet, GlobalCellsConstants.RIG_CONTRACTOR);
							if (genralStartRow != null && generalEndRow != null) {
								blankFieldMap = constantData.addGeneralConstantCells(tableSheet,
										genralStartRow.getRowNum(), generalEndRow.getRowNum(), blankFieldMap);
								sb = getGeneralFieldAttributes(blankFieldMap, sb, sourceFile);
							}
							// depth
							Row depthDayStartRow = fileData.findRow(tableSheet, GlobalCellsConstants.DOL);
							Row depthDayEndRow = fileData.findRow(tableSheet, GlobalCellsConstants.CURRENT_STATUS);
							if (depthDayStartRow != null && depthDayEndRow != null) {
								blankFieldMap = constantData.addDepthConstantCells(tableSheet,
										depthDayStartRow.getRowNum(), depthDayEndRow.getRowNum() - 2, blankFieldMap);
								sb = getDepthFieldAttributes(blankFieldMap, sb);
							}
							// Iterate status rows
							Row statusDayEndRow = fileData.findRow(tableSheet, GlobalCellsConstants.HR_FORECAST);
							if (depthDayEndRow != null && statusDayEndRow != null) {
								blankFieldMap = constantData.addStatusConstantCells(tableSheet,
										depthDayEndRow.getRowNum(), statusDayEndRow.getRowNum(), blankFieldMap);
								sb = getStatusFieldAttributes(blankFieldMap, sb);
							}

							// Iterate OPERATION SUMMARY table cells
							Row operationStartRow = fileData.findRow(tableSheet,
									GlobalCellsConstants.OPERATION_SUMMARY);
							Row operationEndRow = fileData.findRow(tableSheet, GlobalCellsConstants.NPT_SUMMARY);
							if (operationStartRow != null && operationEndRow != null) {
								dynamicBlankFieldMap = constantData.DynamicOperationDataCells(tableSheet,
										operationStartRow.getRowNum() + 3, operationEndRow.getRowNum() - 1);
								sb = getOperationSummaryData(dynamicBlankFieldMap, sb,
										operationStartRow.getRowNum() + 3, operationEndRow.getRowNum() - 1);
							}
							// Iterate NPT table cells
							Row casingStartRow = fileData.findRow(tableSheet, GlobalCellsConstants.CASING_INFORMATION);
							if (operationEndRow != null && casingStartRow != null) {
								dynamicBlankFieldMap = constantData.DynamicNPTDataCells(tableSheet,
										operationEndRow.getRowNum() + 3, casingStartRow.getRowNum() - 1,
										dynamicBlankFieldMap);
								sb = getNPTTableData(dynamicBlankFieldMap, sb, operationEndRow.getRowNum() + 3,
										casingStartRow.getRowNum() - 1);
							}
							break;
						}
						break;
					}
					wb.close();
					fis.close();
					Thread.sleep(1000);
				}
			}
			success = true;
		} catch (NullPointerException e) {
			log.error(e.getMessage());
			sendMail("The data input is " + e.getMessage() + " For Report " + tempName, prop);
		} catch (Exception e) {
			log.error(e.getMessage());
			EmailSender sender = new EmailSender();
			sender.sendErrorLogEmail("Process is Interrupted... ",
					"Process has been stopped due to " + e.getMessage() + Global.NEXT_LINE + " For Report " + tempName,
					prop);
		}
		return success;
	}

	private void sendMail(String message, Properties prop) {
		EmailSender sender = new EmailSender();
		try {
			sender.sendErrorLogEmail("Process is Interrupted... ",
					"Process has been stopped due to " + Global.NEXT_LINE + message, prop);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public StringBuffer getOperationSummaryData(Map<String, String> dynamicBlankFieldMap, StringBuffer sb, int startRow,
			int endRow) {
		for (int rowOper = startRow; rowOper <= startRow; rowOper++) {
			if (StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.TIME_FROM + rowOper))
					&& StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.TIME_TO + rowOper))
					&& StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.TIME_HRS + rowOper))
					&& StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.PHASE + rowOper))
					&& StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.CODE_OPRN + rowOper))
					&& StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.SUBCODE_GROUP + rowOper))
					&& StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.MD_FROM + rowOper))
					&& StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.OPERATION_DESC + rowOper))) {
				sb.append(Global.NEXT_LINE);
				sb.append(GlobalCellsConstants.OPERATION_SUMMARY + ":- Is Empty");
				sb.append(Global.NEXT_LINE);
			} else if (StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.TIME_FROM + rowOper))
					|| StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.TIME_TO + rowOper))
					|| StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.TIME_HRS + rowOper))
					|| StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.PHASE + rowOper))
					|| StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.CODE_OPRN + rowOper))
					|| StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.SUBCODE_GROUP + rowOper))
					|| StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.MD_FROM + rowOper))
					|| StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.OPERATION_DESC + rowOper))) {
				sb.append(Global.NEXT_LINE);
				sb.append(GlobalCellsConstants.OPERATION_SUMMARY + ":-");
				sb.append(Global.NEXT_LINE);
			}
		}
		for (int rowNum = startRow; rowNum <= endRow; rowNum++) {
			if (StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.TIME_FROM + rowNum))) {
				sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.TIME_FROM + Global.BREAK_LINE);
			}
			if (StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.TIME_TO + rowNum))) {
				sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.TIME_TO + Global.BREAK_LINE);
			}
			if (StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.TIME_HRS + rowNum))) {
				sb.append(GlobalCellsConstants.NULL_DATA_WITH
						+ dynamicBlankFieldMap.get(GlobalCellsConstants.TIME_FROM + rowNum) + " - "
						+ dynamicBlankFieldMap.get(GlobalCellsConstants.TIME_TO + rowNum) + GlobalCellsConstants.FOR
						+ GlobalCellsConstants.TIME_HRS + Global.BREAK_LINE);
			}
			if (StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.PHASE + rowNum))) {
				sb.append(GlobalCellsConstants.NULL_DATA_WITH
						+ dynamicBlankFieldMap.get(GlobalCellsConstants.TIME_FROM + rowNum) + " - "
						+ dynamicBlankFieldMap.get(GlobalCellsConstants.TIME_TO + rowNum) + GlobalCellsConstants.FOR
						+ GlobalCellsConstants.PHASE + Global.BREAK_LINE);
			}
			if (StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.CODE_OPRN + rowNum))) {
				sb.append(GlobalCellsConstants.NULL_DATA_WITH
						+ dynamicBlankFieldMap.get(GlobalCellsConstants.TIME_FROM + rowNum) + " - "
						+ dynamicBlankFieldMap.get(GlobalCellsConstants.TIME_TO + rowNum) + GlobalCellsConstants.FOR
						+ GlobalCellsConstants.CODE_OPRN + Global.BREAK_LINE);
			}
			if (StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.SUBCODE_GROUP + rowNum))) {
				sb.append(GlobalCellsConstants.NULL_DATA_WITH
						+ dynamicBlankFieldMap.get(GlobalCellsConstants.TIME_FROM + rowNum) + " - "
						+ dynamicBlankFieldMap.get(GlobalCellsConstants.TIME_TO + rowNum) + GlobalCellsConstants.FOR
						+ GlobalCellsConstants.SUBCODE_GROUP + Global.BREAK_LINE);
			}
			if (StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.MD_FROM + rowNum))) {
				sb.append(GlobalCellsConstants.NULL_DATA_WITH
						+ dynamicBlankFieldMap.get(GlobalCellsConstants.TIME_FROM + rowNum) + " - "
						+ dynamicBlankFieldMap.get(GlobalCellsConstants.TIME_TO + rowNum) + GlobalCellsConstants.FOR
						+ GlobalCellsConstants.MD_FROM + Global.BREAK_LINE);
			}
			if (StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.OPERATION_DESC + rowNum))) {
				sb.append(GlobalCellsConstants.NULL_DATA_WITH
						+ dynamicBlankFieldMap.get(GlobalCellsConstants.TIME_FROM + rowNum) + " - "
						+ dynamicBlankFieldMap.get(GlobalCellsConstants.TIME_TO + rowNum) + GlobalCellsConstants.FOR
						+ GlobalCellsConstants.OPERATION_DESC + Global.BREAK_LINE);
			}
		}
		return sb;

	}

	public StringBuffer getNPTTableData(Map<String, String> dynamicBlankFieldMap, StringBuffer sb, int startRow,
			int endRow) {
		for (int rowNums = startRow; rowNums <= startRow; rowNums++) {
			if (StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_TIME_FROM + rowNums))
					&& StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_TIME_TO + rowNums))
					&& StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_TIME_HRS + rowNums))
					&& StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_CATEGORY + rowNums))
					&& StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_TYPE + rowNums))
					&& StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.MAIN_VENDOR + rowNums))
					&& StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.SUB_VENDOR + rowNums))
					&& StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_DESC + rowNums))
					&& StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.NET_COST_USD + rowNums))) {
				sb.append(Global.NEXT_LINE);
				sb.append(GlobalCellsConstants.NPT_SUMMARY + " Is Empty");
				sb.append(Global.NEXT_LINE);
			} else if (StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_TIME_FROM + rowNums))
					|| StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_TIME_TO + rowNums))
					|| StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_TIME_HRS + rowNums))
					|| StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_CATEGORY + rowNums))
					|| StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_TYPE + rowNums))
					|| StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.MAIN_VENDOR + rowNums))
					|| StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.SUB_VENDOR + rowNums))
					|| StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_DESC + rowNums))
					|| StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.NET_COST_USD + rowNums))) {
				sb.append(Global.NEXT_LINE);
				sb.append(GlobalCellsConstants.NPT_SUMMARY);
				sb.append(Global.NEXT_LINE);
			}
		}
		for (int rowNum = startRow; rowNum <= endRow; rowNum++) {
			if (StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_TIME_FROM + rowNum))) {
				sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.NPT_TIME_FROM + Global.BREAK_LINE);
			}
			if (StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_TIME_TO + rowNum))) {
				sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.NPT_TIME_TO + Global.BREAK_LINE);
			}
			if (StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_TIME_HRS + rowNum))) {
				sb.append(GlobalCellsConstants.NULL_DATA_WITH
						+ dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_TIME_FROM + rowNum) + " - "
						+ dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_TIME_TO + rowNum) + GlobalCellsConstants.FOR
						+ GlobalCellsConstants.NPT_TIME_HRS + Global.BREAK_LINE);
			}
			if (StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_CATEGORY + rowNum))) {
				sb.append(GlobalCellsConstants.NULL_DATA_WITH
						+ dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_TIME_FROM + rowNum) + " - "
						+ dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_TIME_TO + rowNum) + GlobalCellsConstants.FOR
						+ GlobalCellsConstants.NPT_CATEGORY + Global.BREAK_LINE);
			}
			if (StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_TYPE + rowNum))) {
				sb.append(GlobalCellsConstants.NULL_DATA_WITH
						+ dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_TIME_FROM + rowNum) + " - "
						+ dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_TIME_TO + rowNum) + GlobalCellsConstants.FOR
						+ GlobalCellsConstants.NPT_TYPE + Global.BREAK_LINE);
			}
			if (StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.MAIN_VENDOR + rowNum))) {
				sb.append(GlobalCellsConstants.NULL_DATA_WITH
						+ dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_TIME_FROM + rowNum) + " - "
						+ dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_TIME_TO + rowNum) + GlobalCellsConstants.FOR
						+ GlobalCellsConstants.MAIN_VENDOR + Global.BREAK_LINE);
			}
			if (StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.SUB_VENDOR + rowNum))) {
				sb.append(GlobalCellsConstants.NULL_DATA_WITH
						+ dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_TIME_FROM + rowNum) + " - "
						+ dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_TIME_TO + rowNum) + GlobalCellsConstants.FOR
						+ GlobalCellsConstants.SUB_VENDOR + Global.BREAK_LINE);
			}
			if (StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_DESC + rowNum))) {
				sb.append(GlobalCellsConstants.NULL_DATA_WITH
						+ dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_TIME_FROM + rowNum) + " - "
						+ dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_TIME_TO + rowNum) + GlobalCellsConstants.FOR
						+ GlobalCellsConstants.NPT_DESC + Global.BREAK_LINE);
			}
			if (StringUtils.isBlank(dynamicBlankFieldMap.get(GlobalCellsConstants.NET_COST_USD + rowNum))) {
				sb.append(GlobalCellsConstants.NULL_DATA_WITH
						+ dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_TIME_FROM + rowNum) + " - "
						+ dynamicBlankFieldMap.get(GlobalCellsConstants.NPT_TIME_TO + rowNum) + GlobalCellsConstants.FOR
						+ GlobalCellsConstants.NET_COST_USD + Global.BREAK_LINE);
			}
		}
		return sb;
	}

	public StringBuffer getGeneralFieldAttributes(Map<String, String> blankFieldMap, StringBuffer sb,
			String sourceFile) {
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.WELL_NAME))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.WELL_UWI))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.WELL_PURPOSE))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.WELLBORE_NAME))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.WELLBORE_NO))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.LATITUDE))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.RIG_CONTRACTOR))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.SITE_NAME))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.REGION))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.BLOCK_NO))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.LOCATION))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.WELL_SEVERITY))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.LONGITUDE))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.RIG_NAME))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.START_DATE))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.END_DATE))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.SPUD_DATE))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.EVENT_DESC))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.TYPE_OF_WELL))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.RIG_TYPE))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.REPORT_DATE))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.FINAL_DDR))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.CARBONATE))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.MAX_ANGLE))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.RIG_OPERATOR))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.DAY_SUPERVISOR))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.NIGHT_SUPERVISOR))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.ENGINEER))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.GEOLOGIST))) {
			sb.append(Global.NEXT_LINE);
			sb.append(GlobalCellsConstants.REPORT + sourceFile);
			sb.append(Global.NEXT_LINE);
			sb.append(GlobalCellsConstants.FOR_WELL_NAME + blankFieldMap.get(GlobalCellsConstants.WELL_NAME));
			sb.append(Global.NEXT_LINE);
			sb.append(GlobalCellsConstants.FOR_REPORT_NO + blankFieldMap.get(GlobalCellsConstants.REPORT_NO));
			sb.append(Global.NEXT_LINE);
			sb.append(GlobalCellsConstants.GENERAL);
			sb.append(Global.NEXT_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.WELL_NAME))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.WELL_NAME + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.WELL_UWI))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.WELL_UWI + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.WELL_PURPOSE))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.WELL_PURPOSE + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.WELLBORE_NAME))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.WELLBORE_NAME + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.WELLBORE_NO))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.WELLBORE_NO + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.LATITUDE))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.LATITUDE + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.RIG_CONTRACTOR))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.RIG_CONTRACTOR + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.SITE_NAME))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.SITE_NAME + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.REGION))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.REGION + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.BLOCK_NO))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.BLOCK_NO + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.LOCATION))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.LOCATION + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.WELL_SEVERITY))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.WELL_SEVERITY + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.LONGITUDE))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.LONGITUDE + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.RIG_NAME))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.RIG_NAME + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.START_DATE))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.START_DATE + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.END_DATE))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.END_DATE + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.SPUD_DATE))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.SPUD_DATE + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.EVENT_DESC))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.EVENT_DESC + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.TYPE_OF_WELL))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.TYPE_OF_WELL + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.RIG_TYPE))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.RIG_TYPE + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.REPORT_DATE))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.REPORT_DATE + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.FINAL_DDR))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.FINAL_DDR + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.CARBONATE))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.CARBONATE + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.MAX_ANGLE))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.MAX_ANGLE + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.RIG_OPERATOR))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.RIG_OPERATOR + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.DAY_SUPERVISOR))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.DAY_SUPERVISOR + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.NIGHT_SUPERVISOR))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.NIGHT_SUPERVISOR + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.ENGINEER))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.ENGINEER + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.GEOLOGIST))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.GEOLOGIST + Global.BREAK_LINE);
		}
		return sb;
	}

	public StringBuffer getDepthFieldAttributes(Map<String, String> blankFieldMap, StringBuffer sb) {

		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.DOL))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.DFS))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.TOTAL_DAYS))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.EST_DAYS))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.PROGRESS))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.AVG_ROP))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.MD))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.TVD))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.MD_AUTH))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.ROTATING_HOURS))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.CUMM_ROT_HRS))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.CURRENT_HOLE_SIZE))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.LAST_HOLE_SIZE))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.NO_OF_HOLE_SECTION))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.LAST_LOT_EMW))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.MAASP))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.MAX_MUD_WEIGHT))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.DATE_SECT_START))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.DATE_SECT_END))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.WATER_DEPTH))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.RT_MSL))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.RT_ML))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.DAILY_NPT))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.CUMM_NPT))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.DAILY_NPT_COST))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.CUMM_DAILY_NPT_COST))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.DAILY_COST))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.CUMM_COST))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.AFE_COST))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.SUPP_COST_DAYS))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.EXPENDITURE))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.AFE_NO))) {
			sb.append(Global.NEXT_LINE);
			sb.append(GlobalCellsConstants.DEPTH_CASING_HOLE_ELEVATION_COST);
			sb.append(Global.NEXT_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.DOL))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.DOL + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.DFS))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.DFS + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.TOTAL_DAYS))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.TOTAL_DAYS + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.EST_DAYS))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.EST_DAYS + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.PROGRESS))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.PROGRESS + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.AVG_ROP))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.AVG_ROP + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.MD))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.MD + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.TVD))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.TVD + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.MD_AUTH))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.MD_AUTH + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.ROTATING_HOURS))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.ROTATING_HOURS + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.CUMM_ROT_HRS))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.CUMM_ROT_HRS + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.CURRENT_HOLE_SIZE))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.CURRENT_HOLE_SIZE + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.LAST_HOLE_SIZE))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.LAST_HOLE_SIZE + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.NO_OF_HOLE_SECTION))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.NO_OF_HOLE_SECTION + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.LAST_LOT_EMW))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.LAST_LOT_EMW + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.MAASP))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.MAASP + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.MAX_MUD_WEIGHT))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.MAX_MUD_WEIGHT + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.DATE_SECT_START))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.DATE_SECT_START + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.DATE_SECT_END))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.DATE_SECT_END + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.WATER_DEPTH))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.WATER_DEPTH + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.RT_MSL))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.RT_MSL + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.RT_ML))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.RT_ML + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.CUMM_DAILY_NPT_COST))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.CUMM_DAILY_NPT_COST + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.DAILY_NPT))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.DAILY_NPT + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.CUMM_NPT))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.CUMM_NPT + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.DAILY_NPT_COST))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.DAILY_NPT_COST + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.DAILY_COST))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.DAILY_COST + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.CUMM_COST))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.CUMM_COST + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.AFE_COST))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.AFE_COST + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.SUPP_COST_DAYS))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.SUPP_COST_DAYS + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.EXPENDITURE))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.EXPENDITURE + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.AFE_NO))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.AFE_NO + Global.BREAK_LINE);
		}
		return sb;
	}

	public StringBuffer getStatusFieldAttributes(Map<String, String> blankFieldMap, StringBuffer sb) {

		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.CURRENT_STATUS))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.HR_FORECAST))
				|| StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.HR_SUMMARY))) {
			sb.append(Global.NEXT_LINE);
			sb.append(GlobalCellsConstants.STATUS);
			sb.append(Global.NEXT_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.CURRENT_STATUS))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.CURRENT_STATUS + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.HR_FORECAST))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.HR_FORECAST + Global.BREAK_LINE);
		}
		if (StringUtils.isBlank(blankFieldMap.get(GlobalCellsConstants.HR_SUMMARY))) {
			sb.append(GlobalCellsConstants.NULL_DATA + GlobalCellsConstants.HR_SUMMARY + Global.BREAK_LINE);
		}
		return sb;
	}
}
