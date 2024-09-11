/*Rev1.0 by Bipin for Capillary milestone3*/
package max.retail.stores.pos.services.capillary;
 
 import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import oracle.retail.stores.foundation.tour.gate.Gateway;
 
 public class MAXCapillaryConfig
 {
   private static Properties properties = null;
   private static Map pwdMap = new HashMap();
   
   static
   {
     properties = new Properties();
     
     String PROPERTIES_FILE = Gateway.getProperty("application", "CAPAPIFolderURL", "")+"\\config.properties";
     try
     {
       File configFile = new File(PROPERTIES_FILE);
       InputStream stream = new FileInputStream(configFile);
       properties.load(stream);
       
     }
     catch (Exception exp)
     {
       exp.printStackTrace();
     }    
   }
   
  public static String get(String key)
   {
     return properties.getProperty(key);
   }
     
 }





 

 

 