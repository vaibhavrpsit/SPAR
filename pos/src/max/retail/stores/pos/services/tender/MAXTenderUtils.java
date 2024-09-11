package max.retail.stores.pos.services.tender;

import java.util.HashMap;
import java.util.Iterator;

public class MAXTenderUtils {	

	public static String getBankCodeFromCardType(HashMap responseMap, String cardType) {
		String bankCode = null;
		String value = null;
		Object key = null;

		Iterator it = responseMap.keySet().iterator();
			while (it.hasNext()) {
				key = it.next();
				value = (String) responseMap.get(key);

				value= value.replaceAll("\\s+","");
				cardType= cardType.replaceAll("\\s+","");		

				if (cardType.equalsIgnoreCase(value))
				{
					return (String) key;
					//break;
				}
			}

			return null;
		}		
}
