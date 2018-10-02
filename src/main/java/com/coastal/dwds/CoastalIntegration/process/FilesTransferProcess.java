package com.coastal.dwds.CoastalIntegration.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.Date;
import java.util.Properties;

import com.coastal.dwds.CoastalIntegration.common.DateUtil;
import com.coastal.dwds.CoastalIntegration.constant.Global;

public class FilesTransferProcess {

	/**
	 * Copy the latest files from shared into local environment in order to start
	 * the process
	 * 
	 * @param prop
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public boolean copyFiles(Properties prop) throws IOException, ParseException {

		String sourceDir = prop.getProperty(Global.SHARED_FOLDER);
		String destDir = prop.getProperty(Global.COPY_FOLDER);
		File aDirectory = new File(sourceDir);
		Date prevModDate = getLastCopiedDate();
		Date newModDate = null;

		boolean result = false;

		// get a listing of all files in the directory
		String[] filesInDir = aDirectory.list();

		for (int i = 0; i < filesInDir.length; i++) {

			File source = new File(sourceDir + filesInDir[i]);
			File dest = new File(destDir + filesInDir[i]);
			if (dest.exists()) {
				dest.delete();
			}
			long lastModified = source.lastModified();
			Date time = DateUtil.convertTimetoDateFormat(lastModified);

			if (newModDate == null) {
				newModDate = time;
			}

			if (prevModDate == null) {
				prevModDate = time;
			}

			if (time.compareTo(prevModDate) > 0) {
				System.out.printf("file %s was last modified at %s %n", source.getName(), time);
				copyFile(source, dest);
				result = true;
			}

			if (time.compareTo(newModDate) > 0 || time.compareTo(newModDate) == 0) {
				System.out.println("date ===== " + newModDate);
				newModDate = time;
			}

			if (i == filesInDir.length - 1) {
				updateLastModDate(newModDate);
			}

		}
		return result;
	}

	/**
	 * Delete all files in copied folder after process completed
	 * 
	 * @param prop
	 */
	public void delete(Properties prop) {
		String dirPath = prop.getProperty(Global.COPY_FOLDER);
		File folder = new File(dirPath);

		// get a listing of all files in the directory
		String[] filesInDir = folder.list();

		for (int i = 0; i < filesInDir.length; i++) {
			File empFile = new File(dirPath + filesInDir[i]);
			empFile.delete();
		}

	}

	/**
	 * copyFile from feature from source to destination
	 * 
	 * @param source
	 * @param dest
	 * @throws IOException
	 */
	private static void copyFile(File source, File dest) throws IOException {
		Files.copy(source.toPath(), dest.toPath());
	}

	/**
	 * To get the last modified date to be compared with the files from shared
	 * folder
	 * 
	 * @return
	 */
	public Date getLastCopiedDate() {
		File currFolder = new File(System.getProperty("user.dir"));
		File[] listOfFiles = currFolder.listFiles();
		FileReader input = null;
		BufferedReader bufferedReader = null;
		String line = null;
		Date lastModified = null;

		try {
			for (File file : listOfFiles) {
				String ext = file.getName();
				if (ext.equals(Global.LAST_COPIED_FILE)) {
					input = new FileReader(file);
					bufferedReader = new BufferedReader(input);
					while ((line = bufferedReader.readLine()) != null) {
						lastModified = DateUtil.convertStringToDate(line, DateUtil.DATE_DDMMYYYYHHMMSS_FORMAT);
					}

					// Always close files.
					bufferedReader.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lastModified;
	}

	/**
	 * Write the last modified date
	 * 
	 * @param newModDate
	 */
	public void updateLastModDate(Date newModDate) {
		File currFolder = new File(System.getProperty("user.dir"));
		BufferedWriter bw = null;
		FileWriter fw = null;

		try {

			fw = new FileWriter(currFolder + "\\" + Global.LAST_COPIED_FILE);
			System.out.println(currFolder + "\\" + Global.LAST_COPIED_FILE);
			bw = new BufferedWriter(fw);
			bw.write(DateUtil.convertDateToyyyymmddFormat(newModDate, DateUtil.DATE_DDMMYYYYHHMMSS_FORMAT));

			System.out.println("Done");

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}
	}
}
