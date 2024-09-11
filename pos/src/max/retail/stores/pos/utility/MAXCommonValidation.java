package max.retail.stores.pos.utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.ValidatingBean;

/*this class conatains the common error validation messages 
 * that will be used when we
 want the custom validation instead of 
 validation provided by the POS validation classes

 */

public class MAXCommonValidation extends ValidatingBean {

	/*
	 * for MINIMUM length validation name is the fieldname value is the value
	 * contained in the field minLength is the mininmum length allowed
	 * 
	 * return error message if error exist
	 */

	public String minLengthValidate(String name, String value, Integer minLength) {
		String errorMsg = "";
		if (value.length() < minLength.intValue()) {
						
			errorMsg = UIUtilities.retrieveText("DialogSpec",
                    BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                    "InvalidData.minCheck",
                    "{0} must be atleast  {1} character long");
			Object[] data = null;
			data = new Object[2];
	        data[0] =name;
	        data[1] =minLength;
	        
	      
	           errorMsg = LocaleUtilities.formatComplexMessage(errorMsg,data,getLocale());
			
		}

		return errorMsg;
	}

	/*
	 * for MAXIMUM length validation name is the fieldname value is the value
	 * contained in the field maxLength is the Maximum length allowed return
	 * error message if error exist
	 */

	private String maxLengthValidate(String name, String value,
			Integer maxLength) {
		String errorMsg = "";
		if (value.length() < maxLength.intValue()) {
	
			
			errorMsg = UIUtilities.retrieveText("DialogSpec",
                    BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                    "InvalidData.maxCheck",
                    "{0} must be less than  {1} character long");
			Object[] data = null;
			data = new Object[2];
	        data[0] =name;
	        data[1] =maxLength;
	        
	      
	           errorMsg = LocaleUtilities.formatComplexMessage(errorMsg,data,getLocale());
			
		}
		return errorMsg;
	}

	/*
	 * for BLANK and NULL value validation name is the fieldname value is the
	 * value contained in the field return error message if error exist
	 */

	public String blankCheck(String name, String value) {
		Object[] data = null;
		String errorMsg = "";
		if (value == null || (value != null && value.trim().length() == 0)) {
			
			errorMsg = UIUtilities.retrieveText("DialogSpec",
                    BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                    "InvalidData.blankData",
                    "{0} must not be left blank.");
			 data = new Object[1];
	           data[0] =name;
	      
	           errorMsg = LocaleUtilities.formatComplexMessage(errorMsg,data,getLocale());
		
			
			
		}
		
		return errorMsg;
	}

	/*
	 * for EXACT length validation name is the fieldname value is the value
	 * contained in the field exactLength is the length to validate return error
	 * message if error exist
	 */

	public String exactCheck(String name, String value, Integer exactLength) {
		String errorMsg = "";
		String pincodePattern = "^[6-9][0-9]{5}$";
		String mobileno = "^[6-9]\\d{9}$";

		Pattern pattern = Pattern.compile(pincodePattern);
		 pattern = Pattern.compile(mobileno);

		Matcher matcher = pattern.matcher(value);

		if (!matcher.matches()) {
	
		if (value.trim().length() != exactLength.intValue()) {
			
			errorMsg = UIUtilities.retrieveText("DialogSpec",
                    BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                    "InvalidData.exactLength",
                    "{0} must be {1} character long.");
			Object[] data=null;
			 data = new Object[2];
	           data[0] =name;
	           data[1] =exactLength;
	           
	      
	           errorMsg = LocaleUtilities.formatComplexMessage(errorMsg,data,getLocale());
			
		
		}}
		return errorMsg;
	}
	
	/*
	 * for value check of the  field if it start with zero
	 * message if error exist
	 */
	public String minValueCheck(String name, String value, String minValue) {
		String errorMsg = "";
		
		double valueIn=Double.parseDouble(value);
		double minValueIn=Double.parseDouble(minValue);
		
		if (valueIn < minValueIn) {
			
			errorMsg = UIUtilities.retrieveText("DialogSpec",
                    BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                    "InvalidData.minValueCheck",
                    "{0} must Not start with {0,1,2,3,4,5}.");
			Object[] data=null;
			 data = new Object[1];
	           data[0] =name;
	           
	      
	           errorMsg = LocaleUtilities.formatComplexMessage(errorMsg,data,getLocale());
			
		
		}
		return errorMsg;
	}
	
	

	/*
	 * for valid email format validation name is the fieldname value is the
	 * value contained in the field return error message if error exist
	 */

	public String isValidEmail(String name, String value) {
		String errorMsg = "";
		String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

		Pattern pattern = Pattern.compile(EMAIL_PATTERN);

		Matcher matcher = pattern.matcher(value);

		if (!matcher.matches()) {
	
			
			errorMsg = UIUtilities.retrieveText("DialogSpec",
                    BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                    "InvalidData.emailFormat",
                    "{0} must be of proper format i.e abc@gmail.com ");
			Object[] data=null;
			 data = new Object[1];
	           data[0] =name;
	           errorMsg = LocaleUtilities.formatComplexMessage(errorMsg,data,getLocale());
	           
			
		}
		return errorMsg;
	}

	/*
	 * for date format and future date check name is the fieldname value is the
	 * value contained in the field return error message if error exist
	 */

	public String validateDateFuture(String name, String value) {
		// / for format dd/MM/yyyy
		String errorMsg = "";
		Object[] data=null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		if (value.split("\\/").length == 3
				&& value.split("\\/")[2].length() == 4) {

			try {
				Date d = dateFormat.parse(value);
				if (!d.before(new Date())) {

			
					errorMsg = UIUtilities.retrieveText("DialogSpec",
		                    BundleConstantsIfc.DIALOG_BUNDLE_NAME,
		                    "InvalidData.datecurrent",
		                    "{0} must be before current date ");
					
					 data = new Object[1];
			           data[0] =name;
			           errorMsg = LocaleUtilities.formatComplexMessage(errorMsg,data,getLocale());
					

				}
			} catch (Exception e) {

				e.printStackTrace();
			}

		} else if (value.split("\\/").length < 3
				|| value.split("\\/")[2].length() == 0) {

	
			
			errorMsg = UIUtilities.retrieveText("DialogSpec",
                    BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                    "InvalidData.dateFormat",
                    "{0} must be in the dd/MM/YYYY Format ");
			
			 data = new Object[1];
	           data[0] =name;
	           errorMsg = LocaleUtilities.formatComplexMessage(errorMsg,data,getLocale());
			
		}

		return errorMsg;
	}
	//Changes done by kajal nautiyal start
	public String NonZero(String name, String value) {
		//System.out.print("going inside maxcommonvalidate 247");
		String errorMsg = "";
		String pincodePattern = "^[1-9][0-9]{5}$";

		Pattern pattern = Pattern.compile(pincodePattern);

		Matcher matcher = pattern.matcher(value);

		if (!matcher.matches()) {
	
			//System.out.print("going inside maxcommonvalidate 257");
			
			errorMsg = UIUtilities.retrieveText("DialogSpec",
                    BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                    "InvalidData.pinCode",
                    "{0} must be of proper format i.e it should not start from zero ");
			Object[] data=null;
			 data = new Object[1];
	           data[0] =name;
	           errorMsg = LocaleUtilities.formatComplexMessage(errorMsg,data,getLocale());
	         //  System.out.print("going inside maxcommonvalidate 267");
		}
		return errorMsg;
	}
	//Changes done by kajal nautiyal ends
	
	//Changes done by kamlesh pant start
	public String validatemobileno(String name, String value, Integer exactLength)
	{
		String errorMsg = "";
		String mobileno = "^[6-9]\\d{9}$";

		Pattern pattern = Pattern.compile(mobileno);

		Matcher matcher = pattern.matcher(value);
		
		if (!matcher.matches()) {
			
			//System.out.print("going inside maxcommonvalidate 257");
			
			errorMsg = UIUtilities.retrieveText("DialogSpec",
                    BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                    "InvalidData.validatemobileno",
                    "{0} must be of proper format i.e it should not start from Zero to Five ");
			Object[] data=null;
			 data = new Object[1];
	           data[0] =name;
	           errorMsg = LocaleUtilities.formatComplexMessage(errorMsg,data,getLocale());
	         //  System.out.print("going inside maxcommonvalidate 267");
		}
		return errorMsg;
	}

	//Changes done by kamlesh pant ends
}
