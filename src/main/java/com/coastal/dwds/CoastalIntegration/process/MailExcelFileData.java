package com.coastal.dwds.CoastalIntegration.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.coastal.dwds.CoastalIntegration.constant.Global;
import com.coastal.dwds.CoastalIntegration.constant.GlobalCellsConstants;
import com.coastal.dwds.CoastalIntegration.constant.GlobalEmail;
import com.coastal.dwds.CoastalIntegration.constant.GlobalErrors;

/**
 * @author Syafiza
 */
public class MailExcelFileData {
	private static final Logger log = LogManager.getLogger(MailExcelFileData.class);
	static final int BUFFER = 2048;
	static String reportNo = "";
	static String wellName = "";
	static String wellBoreName = "";
	static String reportDate = "";
	static String pscName = "";
	ConstantCellData constantData = new ConstantCellData();

	/**
	 * @param sheet
	 * @param colName
	 * @param textToFind
	 * @return
	 */
	// To get start row and end row based on the input cell field
	public static Row findRow(Sheet sheet, String textToFind) {
		Row resultRow = null;
		for (Row row : sheet) {
			for (Cell cell : row) {
				if (cell.toString().equalsIgnoreCase(textToFind)) {
					resultRow = row;
					return resultRow;
				}
			}
		}
		return resultRow;
	}

	/**
	 * @param errMessage
	 * @param dateFolder
	 * @param prop
	 * @throws IOException
	 */
	public boolean errorLogMail(Object errMessage, Properties prop) throws IOException {
		boolean success = false;
		EmailSender sender = new EmailSender();

		// message info
		String subject = GlobalEmail.ERROR_LOG_SUBJECT + Global.DRILLING_DATA;
		String bodyMessage = GlobalEmail.ERROR_LOG_BODY_MESSAGE;
		try {
			success = sender.sendErrorLogEmail(subject, bodyMessage + errMessage, prop);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(GlobalErrors.MAIL_NETWORK_ERROR + GlobalEmail.MAIL_SMTP_PORT + Global.NEXT_LINE);
			log.error(ex.getMessage() + Global.NEXT_LINE + GlobalErrors.NETWORK_ADMIN);
		}
		return success;
	}

	/**
	 * @param mySheet
	 * @return name
	 */
	// method for get the file name
	@SuppressWarnings("rawtypes")
	public String getNameCell(Sheet mySheet) {
		String name = "";
		Row nameRow = mySheet.getRow(0);
		Iterator cells = nameRow.cellIterator();
		while (cells.hasNext()) {
			Cell c1 = (Cell) cells.next();
			if (c1.toString().startsWith(GlobalCellsConstants.COMPANY_NAME)) {
				name = c1.toString().substring(15, c1.toString().length());
				break;
			}
		}
		return name;
	}

	/**
	 * @param fileEntry
	 * @param dateFolder
	 * @param finalProcessOutputFileLocation
	 * @param prop
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	public boolean sendMailExcelFile(File fileEntry, File finalProcessOutputFileLocation, Properties prop) throws Exception {
		boolean success = false;
		// if file not found send mail mail notification
		if (!fileEntry.exists()) {
			System.out.println(GlobalErrors.PROCESS_INPUT_ERROR + GlobalErrors.INPUT_EXCEL_FILE_NOT_FOUND + ")" + Global.NEXT_LINE);
			success = errorLogMail(GlobalErrors.INPUT_EXCEL_FILE_NOT_FOUND, prop);
		}
		try {
			String tempName = FilenameUtils.getName(fileEntry.toString());
			if (!tempName.startsWith("~$")) {
				// create source file stream
				InputStream fis = new FileInputStream(fileEntry);
				Workbook wb = WorkbookFactory.create(fis);
				Sheet tableSheet = wb.getSheetAt(0);

				// Iterate the cells for constant tables
				for (Row constantRow : tableSheet) {
					Map<String, String> generalMap = new HashMap<String, String>();
					// get the excel file name from row no 2
					pscName = getNameCell(tableSheet).trim();
					// Iterate the general information from start and end of
					// this rows
					Row generalStartRow = findRow(tableSheet, GlobalCellsConstants.WELL_NAME);
					Row generalEndRow = findRow(tableSheet, GlobalCellsConstants.RIG_CONTRACTOR);
					generalMap = constantData.addGeneralConstantCells(tableSheet, generalStartRow.getRowNum(),
							generalEndRow.getRowNum(), generalMap);

					if (generalMap.containsKey(GlobalCellsConstants.REPORT_NO)) {
						String no = generalMap.get(GlobalCellsConstants.REPORT_NO);
						String[] parts = no.split("\\.");
						reportNo = parts[0];
					}
					if (generalMap.containsKey(GlobalCellsConstants.WELL_NAME)) {
						wellName = generalMap.get(GlobalCellsConstants.WELL_NAME);
					}
					if (generalMap.containsKey(GlobalCellsConstants.WELLBORE_NAME)) {
						wellBoreName = generalMap.get(GlobalCellsConstants.WELLBORE_NAME);
					}
					if (generalMap.containsKey(GlobalCellsConstants.REPORT_DATE)) {
						reportDate = generalMap.get(GlobalCellsConstants.REPORT_DATE);
					}
					break;
				}
				wb.close();
				fis.close();
				Thread.sleep(1000);
				success = true;
				File zipFile = doCompressFile(fileEntry);
				System.out.println(zipFile + Global.IS_GENERATED + Global.NEXT_LINE);
				// generate mailing for all pcs's based on input psc value.
				EmailSender mail = new EmailSender();
				if (success) {
					success = mail.sendPSCMail(zipFile, pscName, reportNo, reportDate, wellName, prop);
				}
				if (success) {
					zipFile.delete();
				}
			}
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
			log.error(GlobalErrors.PROCESS_INPUT_ERROR + GlobalErrors.FILE_NOT_FOUND + Global.NEXT_LINE);
			errorLogMail(e.getMessage(), prop);
		} catch (NullPointerException ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		} catch (StringIndexOutOfBoundsException exp) {
			exp.printStackTrace();
			log.error(exp.getMessage());
		} catch (IllegalStateException exp) {
			exp.printStackTrace();
			log.error(exp.getMessage());
		} catch (Exception exp) {
			exp.printStackTrace();
			log.error(exp.getMessage());
		}
		return success;
	}

	public File doCompressFile(File fileEntry) {
		File targetZip = new File(FilenameUtils.removeExtension(fileEntry.toString()) + Global.ZIP_FILE_EXTENSION);
		try {
			// create byte buffer
			byte[] buffer = new byte[1024];
			FileOutputStream fos = new FileOutputStream(targetZip);
			ZipOutputStream zos = new ZipOutputStream(fos);
			File srcFile = new File(fileEntry.toString());
			FileInputStream fis = new FileInputStream(srcFile);
			// begin writing a new ZIP entry, positions the stream to the start
			// of the entry data
			zos.putNextEntry(new ZipEntry(srcFile.getName()));
			int length;
			while ((length = fis.read(buffer)) > 0) {
				zos.write(buffer, 0, length);
			}
			zos.closeEntry();
			// close the InputStream
			fis.close();
			// close the ZipOutputStream
			zos.close();
			System.out.println("Compress the output file " + targetZip);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return targetZip;
	}

	public Properties loadPropertiesFile() {
		File currFolder = new File(System.getProperty("user.dir"));
		File[] listOfFiles = currFolder.listFiles();
		Properties prop = new Properties();
		InputStream input = null;
		try {
			for (File file : listOfFiles) {
				String ext = file.getName();
				if (ext.equals(Global.PROPERTY_EXTENTSION)) {
					input = new FileInputStream(file);
					prop.load(input);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return prop;
	}
}