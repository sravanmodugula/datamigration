/**
 * 
 */
package com.ca;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author modsr01
 *
 */
public class PropertiesUtil {
	
	/**
	 * Gets the ca.db_connect property value from
	 * the ./migration.properties file of the base folder
	 *
	 * @return ca.db_connect string
	 * @throws IOException
	 */
	public static String getDBURL() throws IOException{

	    String dbConnect = null;

	    //to load application's properties, we use this class
	    Properties mainProperties = new Properties();

	    FileInputStream file;

	    //the base folder is ./, the root of the main.properties file  
	    String path = "./migration.properties";

	    //load the file handle for main.properties
	    file = new FileInputStream(path);

	    //load all the properties from this file
	    mainProperties.load(file);

	    //we have loaded the properties, so close the file handle
	    file.close();

	    //retrieve the property we are intrested, the app.version
	    dbConnect = mainProperties.getProperty("ca.db_connect");

	    return dbConnect;
	}
	
	/**
	 * Gets the ca.db_username property value from
	 * the ./migration.properties file of the base folder
	 *
	 * @return ca.db_username string
	 * @throws IOException
	 */
	public static String getDBUser() throws IOException{

	    String dbUser = null;

	    //to load application's properties, we use this class
	    Properties mainProperties = new Properties();

	    FileInputStream file;

	    //the base folder is ./, the root of the main.properties file  
	    String path = "./migration.properties";

	    //load the file handle for main.properties
	    file = new FileInputStream(path);

	    //load all the properties from this file
	    mainProperties.load(file);

	    //we have loaded the properties, so close the file handle
	    file.close();

	    //retrieve the property we are intrested, the app.version
	    dbUser = mainProperties.getProperty("ca.db_username");

	    return dbUser;
	}
	
	/**
	 * Gets the ca.db_password property value from
	 * the ./migration.properties file of the base folder
	 *
	 * @return ca.db_password string
	 * @throws IOException
	 */
	public static String getDBPassword() throws IOException{

	    String dbPassword = null;

	    //to load application's properties, we use this class
	    Properties mainProperties = new Properties();

	    FileInputStream file;

	    //the base folder is ./, the root of the main.properties file  
	    String path = "./migration.properties";

	    //load the file handle for main.properties
	    file = new FileInputStream(path);

	    //load all the properties from this file
	    mainProperties.load(file);

	    //we have loaded the properties, so close the file handle
	    file.close();

	    //retrieve the property we are intrested, the app.version
	    dbPassword = mainProperties.getProperty("ca.db_password");

	    return dbPassword;
	}
	
	/**
	 * Gets the ca.dir_ldif property value from
	 * the ./migration.properties file of the base folder
	 *
	 * @return ca.dir_ldif string
	 * @throws IOException
	 */
	public static String getDirLDIF() throws IOException{

	    String dirLDIF = null;

	    //to load application's properties, we use this class
	    Properties mainProperties = new Properties();

	    FileInputStream file;

	    //the base folder is ./, the root of the main.properties file  
	    String path = "./migration.properties";

	    //load the file handle for main.properties
	    file = new FileInputStream(path);

	    //load all the properties from this file
	    mainProperties.load(file);

	    //we have loaded the properties, so close the file handle
	    file.close();

	    //retrieve the property we are intrested, the app.version
	    dirLDIF = mainProperties.getProperty("ca.dir_ldif");

	    return dirLDIF;
	}

}
