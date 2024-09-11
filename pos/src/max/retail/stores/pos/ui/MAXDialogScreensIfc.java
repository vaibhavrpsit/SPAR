/*
 * package max.retail.stores.pos.ui; /*
 * 
 * 
 * 
 * public abstract interface MAXDialogScreensIfc { //Changes starts for Rev 1.1
 * (Ashish : Dual) public static final int BUTTON_EXCELLENT = 0; public static
 * final int BUTTON_GOOD = 1; public static final int BUTTON_NOTOK = 2; public
 * static final int CUSTOMER_FEEDBACK = 20; // public static final String[]
 * DIALOG_BUTTON_LABELS = { "E","G","P" };
 * 
 * 
 * public static final String[] DIALOG_BUTTON_LABELS = { "E","G","P","Ok",
 * "Yes", "No", "Continue", "Retry", "Cancel", "Now", "Later", "Add", "Update",
 * "Pickup", "Ship", "Deliver", "SearchWebStore", "SearchSIM", "One",
 * "Multiple","CustInfo","Enter","Partial","Converted","Total",
 * "EdgeCustomerInformation","SbiPointInformation" }; public static final int
 * CUSTOMER_INFORMATION = 21; public static final int CUSTOMER_ENTER = 22;
 * public static final int BUTTON_PARTIAL = 23; public static final int
 * BUTTON_CONVERTED =24; public static final int BUTTON_TOTAL = 25; public
 * static final int EDGE_CUSTOMER_INFORMATION = 26; public static final int
 * SBI_POINT_INFORMATION = 27; public static final int ACKNOWLEDGEMENT = 28;
 * 
 * 
 * 
 * 
 * //Changes ends for Rev 1.1 (Ashish : Dual) }
 * 
 * Location: C:\Setup\LifestylePOSCode\baselibraries\lib\oracle.stores.pos.jar
 * Qualified Name: oracle.retail.stores.pos.ui.DialogScreensIfc Java Class
 * Version: 7 (51.0) JD-Core Version: 0.7.1
 */

package max.retail.stores.pos.ui;

import oracle.retail.stores.pos.ui.DialogScreensIfc;

public interface MAXDialogScreensIfc extends DialogScreensIfc {
  public static final int CONFIRMATION = 0;
  
  public static final int ERROR = 1;
  
  public static final int RETRY_CONTINUE_CANCEL = 2;
  
  public static final int RETRY_CONTINUE = 3;
  
  public static final int RETRY_CANCEL = 4;
  
  public static final int CONTINUE_CANCEL = 5;
  
  public static final int RETRY = 6;
  
  public static final int ACKNOWLEDGEMENT = 7;
  
  public static final int INVALID_FIELD = 8;
  
  public static final int SIGNATURE = 9;
  
  public static final int NOW_LATER = 10;
  
  public static final int UPDATE_CANCEL = 11;
  
  public static final int YES_NO = 12;
  
  public static final int NO_RESPONSE = 13;
  
  public static final int PICKUP_SHIP = 14;
  
  public static final int PICKUP_DELIVER = 15;
  
  public static final int SEARCHWEBSTORE_CANCEL = 16;
  
  public static final int ONE_OR_MULTIPLE = 17;
  
  public static final int ERROR_NO_BUTTONS = 18;
  
  public static final int SEARCHWEB_SIM_CANCEL = 19;
  
  public static final int MASTER = -1;
  
  public static final int BUTTON_OK = 0;
  
  public static final int BUTTON_YES = 1;
  
  public static final int BUTTON_NO = 2;
  
  public static final int BUTTON_CONTINUE = 3;
  
  public static final int BUTTON_RETRY = 4;
  
  public static final int BUTTON_CANCEL = 5;
  
  public static final int BUTTON_NOW = 6;
  
  public static final int BUTTON_LATER = 7;
  
  public static final int BUTTON_ADD = 8;
  
  public static final int BUTTON_UPDATE = 9;
  
  public static final int BUTTON_PICKUP = 10;
  
  public static final int BUTTON_SHIP = 11;
  
  public static final int BUTTON_DELIVER = 12;
  
  public static final int BUTTON_SEARCH_WEBSTORE = 13;
  
  public static final int BUTTON_SEARCH_SIM = 14;
  
  public static final int BUTTON_ONE = 15;
  
  public static final int BUTTON_MULTIPLE = 16;
  
	/*
	 * public static final String[] DIALOG_BUTTON_LABELS = new String[] { "Ok",
	 * "Yes", "No", "Continue", "Retry", "Cancel", "Now", "Later", "Add", "Update",
	 * "Pickup", "Ship", "Deliver", "SearchWebStore", "SearchSIM", "One", "Multiple"
	 * };
	 */
  public static final String[] DIALOG_BUTTON_LABELS = new String[]{"Ok", "Yes", "No",
			"Continue", "Retry", "Cancel", "Now", "Later", "Add", "Update",
			"Pickup", "Ship", "Deliver", "SearchWebStore", "SearchSIM", "One",
			"Multiple","CustInfo","Enter","Partial","Converted","Total","EDGE_CUSTOMER_INFORMATION","SBI_POINT_INFORMATION"};
  public static final int CUSTOMER_INFORMATION = 17;
  public static final int CUSTOMER_ENTER = 18;
  public static final int BUTTON_PARTIAL = 19;
  public static final int BUTTON_CONVERTED = 20;
  public static final int BUTTON_TOTAL = 21;
  public static final int EDGE_CUSTOMER_INFORMATION = 22;
  public static final int SBI_POINT_INFORMATION = 23;
}
