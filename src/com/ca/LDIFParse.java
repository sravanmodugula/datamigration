/**
 * 
 */
package com.ca;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldif.LDIFException;
import com.unboundid.ldif.LDIFReader;

/**
 * @author modsr01
 *
 */
public class LDIFParse {
	
	private static Logger logger = Logger.getLogger(LDIFParse.class.getName());
	
	public List<CADirModel> getLDIFData() {
		
		LDIFReader ldifReader;
		CADirModel caDirModel;
		
		List<CADirModel> cadirModelList = new ArrayList<CADirModel>();
		
		int entriesRead = 0;
		int errorsEncountered = 0;
		
		try {
			ldifReader = new LDIFReader(PropertiesUtil.getDirLDIF());
			
			while (true) {
				Entry entry;
				try {
					entry = ldifReader.readEntry();
					if (entry == null) {
						// All entries have been read.
						break;
					}
					if (entry.getAttribute("userPassword") != null && entry.getAttribute("userPassword").getValue().contains("{SHA}") && entry.getAttribute("uid") != null && entry.getAttribute("orgId") != null) {
						caDirModel = new CADirModel();
						String userPassword = entry.getAttribute("userPassword").getValue();
						byte[] decoded = Base64.decodeBase64(userPassword.substring(5).getBytes());
						caDirModel.setUser_password(new String(Hex.encodeHex(new String(decoded, "UTF-8").getBytes())));
						caDirModel.setUid(entry.getAttribute("uid").getValue());
						caDirModel.setOrg_id(entry.getAttribute("orgId").getValue());
						cadirModelList.add(caDirModel);
					}
					entriesRead++;
				} catch (LDIFException ldifException) {
					errorsEncountered++;
					if (ldifException.mayContinueReading()) {
						// A recoverable error occurred while attempting to read a change record, at or near line number le.getLineNumber()
						// The entry will be skipped, but we'll try to keep reading from the LDIF file.
						logger.error(ldifException);
						continue;
					} else {
						// An unrecoverable error occurred while attempting to read an entry at or near line number le.getLineNumber()
						// No further LDIF processing will be performed.
						logger.error(ldifException);
						break;
					}
				} catch (IOException ioException) {
					// An I/O error occurred while attempting to read from the LDIF file.
					// No further LDIF processing will be performed.
					logger.error(ioException);
					errorsEncountered++;
					break;
				}
			}
			ldifReader.close();
		} catch (IOException ioException) {
			logger.error(ioException);
		} finally {
			logger.info("ldif entriesRead " + entriesRead + "===== errorsEncountered " + errorsEncountered);
		}
		return cadirModelList;
	}

}
