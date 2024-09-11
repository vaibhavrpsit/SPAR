 package max.retail.stores.pos.ui;

import oracle.retail.stores.pos.ui.DialogScreensIfc;

 public abstract interface MAXEdgeDialogScreensIfc extends DialogScreensIfc
 {
	    
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

