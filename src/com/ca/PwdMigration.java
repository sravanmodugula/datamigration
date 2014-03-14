/**
 * 
 */
package com.ca;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * @author modsr01
 *
 */
public class PwdMigration {
	
	private static Logger logger = Logger.getLogger(PwdMigration.class.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		DB db = new DB();
		Date date = new Date();
		Path logs = FileSystems.getDefault().getPath(System.getProperty("user.dir"), "logs");
		Path extracted_data = FileSystems.getDefault().getPath(System.getProperty("user.dir"), "extracted_data");
		Path tenatslist_mod_passwd = FileSystems.getDefault().getPath(System.getProperty("user.dir"), "tenatslist_mod_passwd");
		if (Files.notExists(logs, LinkOption.NOFOLLOW_LINKS)) {
			File dir = new File("logs");
			dir.mkdir();
		}
		if (Files.notExists(extracted_data, LinkOption.NOFOLLOW_LINKS)) {
			File dir = new File("extracted_data");
			dir.mkdir();
		}
		if (Files.notExists(tenatslist_mod_passwd, LinkOption.NOFOLLOW_LINKS)) {
			File dir = new File("tenatslist_mod_passwd");
			dir.mkdir();
		}
		
		for (String command: args) {
			if (command.equalsIgnoreCase("extract")) {
				logger.info("extract started ... " + date.getTime());
				Connection conn = db.getConnection();
				db.exportDBData(conn, "./extracted_data/extract.txt");
				db.generateUpdateScript(db);
				logger.info("extract finished ... " + date.getTime());
			} else if (command.equalsIgnoreCase("update")) {
				logger.info("update started ... " + date.getTime());
				db.updateDB(db);
				logger.info("update finished ... " + date.getTime());
			} else if (command.equalsIgnoreCase("comprehensive")) {
				logger.info("comprehensive started ... " + date.getTime());
				// extract
				Connection conn = db.getConnection();
				db.exportDBData(conn, "extract.txt");
				db.generateUpdateScript(db);
				// update
				db.updateDB(db);
				logger.info("comprehensive finished ... " + date.getTime());
			} else {
				logger.error("Command does not exists, please refer to wiki or contact administrator.");
			}
        }
		
	}
}
