/**
 * 
 */
package com.ca;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * @author modsr01
 * 
 */
public class DB {

	private static Logger logger = Logger.getLogger(DB.class.getName());

	String db_connect_string = "";
	String db_userid = "";
	String db_password = "";
	
	FileWriter fstream = null;
	BufferedWriter buffer = null;
	String updateSQL = "";

	public DB() {
		try {
			db_connect_string = PropertiesUtil.getDBURL();
			db_userid = PropertiesUtil.getDBUser();
			db_password = PropertiesUtil.getDBPassword();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	NSDModel nsdModel;

	List<NSDModel> nsdModelList = new ArrayList<NSDModel>();

	public Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			conn = DriverManager.getConnection(db_connect_string, db_userid, db_password);
		} catch (ClassNotFoundException classNotFoundException) {
			logger.error(classNotFoundException);
		} catch (SQLException sqlException) {
			logger.error(sqlException);
		}
		return conn;
	}

	public List<NSDModel> getDBData(Connection conn) {
		Statement statement;
		int records = 0;
		try {
			statement = conn.createStatement();
			String queryString = "select r.USER_LOGIN, r.EXTERNAL_USER_ID, d.DB_NAME from platform.dbo.IU_REL_APP_USERS_SLICES r, platform.dbo.IU_SLICES s, platform.dbo.IU_DATABASES d where r.SLICE = s.SLICE and s.DB_ID = d.ID";
			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				nsdModel = new NSDModel();
				nsdModel.setUser_login((rs.getString(1)) == null ? "" : rs.getString(1));
				nsdModel.setExternal_user_id((rs.getString(2)) == null ? "" : rs.getString(2));
				nsdModel.setDb_name((rs.getString(3)) == null ? "" : rs.getString(3));
				nsdModelList.add(nsdModel);
				records++;
			}
		} catch (SQLException sqlException) {
			logger.error(sqlException);
		} finally {
			logger.info("NSD DB records " + records);
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqlException) {
				logger.error(sqlException);
			}
		}
		return nsdModelList;
	}

	public void exportDBData(Connection conn, String filename) {
		Statement statement;
		try {
			fstream = new FileWriter("./extracted_data/extract.txt",true);
			buffer = new BufferedWriter(fstream);

			statement = conn.createStatement();
			String queryString = "select r.USER_LOGIN, r.EXTERNAL_USER_ID, d.DB_NAME from platform.dbo.IU_REL_APP_USERS_SLICES r, platform.dbo.IU_SLICES s, platform.dbo.IU_DATABASES d where r.SLICE = s.SLICE and s.DB_ID = d.ID";
			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				try {
					buffer.write(rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3));
					buffer.newLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			logger.info("Exported DB Data to " + filename);
		} catch (SQLException sqlException) {
			logger.error(sqlException);
		} catch (FileNotFoundException fileNotFoundException) {
			logger.error(fileNotFoundException);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
				if (buffer != null) {
					buffer.close();
				}
			} catch (SQLException sqlException) {
				logger.error(sqlException);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void generateUpdateScript(DB db) {

		LDIFParse ldifParse = new LDIFParse();
		Date date = new Date();

		Connection conn = db.getConnection();

		List<CADirModel> cadirModelList = ldifParse.getLDIFData();
		List<NSDModel> nsdModelList = db.getDBData(conn);
		List<PwdModel> pwdModelList = new ArrayList<PwdModel>();

		PwdModel pwdModel;

		int matching_records = 0;

		Set<String> uniques = new HashSet<String>();
		for (Iterator<CADirModel> caiter = cadirModelList.iterator(); caiter.hasNext();) {
			CADirModel dirModel = caiter.next();
			for (Iterator<NSDModel> nsditer = nsdModelList.iterator(); nsditer.hasNext();) {
				NSDModel nsdModel = nsditer.next();
				if (nsdModel.getExternal_user_id().equals(dirModel.getUid())) {
					pwdModel = new PwdModel();
					pwdModel.setDb_name(nsdModel.getDb_name());
					pwdModel.setUser_password(dirModel.getUser_password());
					pwdModel.setExternal_user_id(nsdModel.getExternal_user_id());
					uniques.add(nsdModel.getDb_name());
					pwdModelList.add(pwdModel);
					matching_records++;
				}
			}
		}
		for (Iterator<String> uniiter = uniques.iterator(); uniiter.hasNext();) {
			String db_name = uniiter.next();
			try {
				fstream = new FileWriter("./tenatslist_mod_passwd/"+db_name + "_" + date.getTime() + ".sql",true);
				buffer = new BufferedWriter(fstream);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (Iterator<PwdModel> pwditer = pwdModelList.iterator(); pwditer
					.hasNext();) {
				PwdModel pwdModels = pwditer.next();
				if (db_name.equals(pwdModels.getDb_name())) {
					updateSQL = "update " + pwdModels.getDb_name().substring(4) + ".dbo.IU_USERS set USER_PASSWORD = 0x" + pwdModels.getUser_password() + " where EXTERNAL_USER_ID = '" + pwdModels.getExternal_user_id() + "'";
					try {
						buffer.write(updateSQL);
						buffer.newLine();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
			try {
				buffer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		logger.info("matching records " + matching_records);
	}
	
	public void updateDB(DB db) {
		Statement statement = null;
		File file;
		File newfile;
		Connection conn = db.getConnection();
		try {
			File[] listOfFiles = loadFiles();
			if (listOfFiles.length <= 0) {
				logger.info("No SQL files found ... ");
			}
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains(".sql")  && !listOfFiles[i].getName().contains("completed")) {
					try {
						logger.info("Processing file name: "+listOfFiles[i].getName());
						statement = conn.createStatement();
						List<String> lines = Files.readAllLines(Paths.get("./tenatslist_mod_passwd/"+listOfFiles[i].getName()), Charset.defaultCharset());
						logger.info("Processing records: "+lines.size());
						for (String updateSQL : lines) {
							statement.executeUpdate(updateSQL);
						}
						file = new File("./tenatslist_mod_passwd/"+listOfFiles[i].getName());
						newfile = new File("./tenatslist_mod_passwd/"+listOfFiles[i].getName()+".completed");
						file.renameTo(newfile);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						if (statement != null) {
							statement.close();
						}
					}
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
		}
	}
	
	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}
	
	static File[] loadFiles() {
		File folder = new File("./tenatslist_mod_passwd");
		return folder.listFiles();
	}

}
