/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *
 *	Rev	1.0 	Dec 20, 2016		Mansi Goel		Changes for Gift Card FES	
 *
 ********************************************************************************/

package max.retail.stores.pos.qc.terminal;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import com.qwikcilver.clientapi.interfaces.iTerminalId;

public class MAXTerminalIDImpl implements iTerminalId{

	public String getTerminalId() {

		ClassLoader loader = ClassLoader.getSystemClassLoader();
		final ResourceBundle rb = ResourceBundle.getBundle("application", Locale.getDefault(), loader);
		Properties result = new Properties();
		for (Enumeration keys = rb.getKeys(); keys.hasMoreElements();) {
			final String key = (String) keys.nextElement();
			final String value = rb.getString(key);

			result.put(key, value);
		}

		String URL = result.getProperty("StoreID") + result.getProperty("WorkstationID");
		//String URL = "20001201";
		return URL;
	}
}
