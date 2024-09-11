/********************************************************************************************
 *   
 *	Copyright (c) 2019 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev	1.0 	Aug 0, 2019		Purushotham Reddy 	Changes for POS-Amazon Pay Integration 
 *
 ********************************************************************************************/

package max.retail.stores.pos.services.tender.wallet.amazonpay;

/**
@author Purushotham Reddy Sirison
**/

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;
import oracle.retail.stores.foundation.tour.gate.Gateway;


public class MAXAmazonPayConfig {
	private static Properties properties = null;
	protected static final Logger logger = Logger
			.getLogger(MAXAmazonPayConfig.class);
	static {
		properties = new Properties();
		String PROPERTIES_FILE = null;
		try {
			PROPERTIES_FILE = Gateway.getProperty("application",
					"AmazonPayAPIFolderURL", "") + "\\config.properties";
		} catch (Exception e) {
			logger.error(e);
		}

		try {
			File configFile = new File(PROPERTIES_FILE);
			InputStream stream = new FileInputStream(configFile);
			properties.load(stream);
		} catch (Exception exp) {
			logger.error(exp);
		}
	}

	public static String get(String key) {
		return properties.getProperty(key);
	}

	public static String getURL(String key) {
		return properties.getProperty(key);
	}

}
