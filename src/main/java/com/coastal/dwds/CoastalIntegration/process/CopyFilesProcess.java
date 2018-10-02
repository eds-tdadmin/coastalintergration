package com.coastal.dwds.CoastalIntegration.process;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import com.coastal.dwds.CoastalIntegration.constant.Global;

public class CopyFilesProcess {
	public boolean copyFiles(Properties prop) throws IOException, ParseException {

		// create a file that is really a directory
		String sourceDir = prop.getProperty(Global.SHARED_FOLDER);
		String destDir = prop.getProperty(Global.COPY_FOLDER);
		File aDirectory = new File(sourceDir);
		Date updatedDate = null;
		boolean result = false;

		// get a listing of all files in the directory
		String[] filesInDir = aDirectory.list();

		// have everything i need, just print it now
		for (int i = 0; i < filesInDir.length; i++) {

			File source = new File(sourceDir + filesInDir[i]);
			File dest = new File(destDir + filesInDir[i]);
			if (dest.exists()) {
				dest.delete();
			}
			long lastModified = source.lastModified();
			Date time = format(lastModified);
			if (i == 0) {
				updatedDate = time;
			}
			if (time.compareTo(updatedDate) > 0) {
				System.out.printf("file %s was last modified at %s %n", source.getName(), time);
				copyFile(source, dest);
				updatedDate = time;
				result = true;
			}

		}
		return result;
	}

	public static Date format(long time) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String newDate = sdf.format(new Date(time));
		return sdf.parse(newDate);
	}

	private static void copyFile(File source, File dest) throws IOException {
		Files.copy(source.toPath(), dest.toPath());
	}
}
