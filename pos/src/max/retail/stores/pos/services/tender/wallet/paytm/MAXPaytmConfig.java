/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *     Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
 *
 * Rev 1.0  march 29, 2022    Kamlesh Pant		Requirement Paytm
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package max.retail.stores.pos.services.tender.wallet.paytm;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import oracle.retail.stores.foundation.tour.gate.Gateway;

	public class MAXPaytmConfig {
		private static Properties properties = null;
	
		static {
			properties = new Properties();
			String PROPERTIES_FILE = null;
			try {
				// String
				PROPERTIES_FILE = Gateway.getProperty("application",
						"PaytmAPIFolderURL", "") + "\\config.properties";
			} catch (Exception e) {
				e.printStackTrace();
			}
	
			try {
				File configFile = new File(PROPERTIES_FILE);
				InputStream stream = new FileInputStream(configFile);
				properties.load(stream);
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}
	
		public static String get(String key) {
			return properties.getProperty(key);
		}
	
		public static String getURL(String key) {
			return properties.getProperty(key);
		}
	
}

