/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 1998-2002 360Commerce, Inc.    All Rights Reserved.
    
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.utility;

//--------------------------------------------------------------------------
/**
 * This is a list of constants for method of entry. These methods adhere to the
 * IXRetail standard for RetailTransactionEntry.
 * <P>
 * 
 * @version $Revision: 3$
 **/
// --------------------------------------------------------------------------
public interface MAXEntryMethodConstantsIfc {
	/**
	 * revision number supplied by source-code-control system
	 **/
	public static String revisionNumber = "$Revision: 3$";
	/**
	 * indicates entry undefined
	 **/
	public static final int ENTRY_METHOD_UNDEFINED = -1;
	/**
	 * indicates entered from the keyboard
	 **/
	public static final int ENTRY_METHOD_KEYED = 0;
	/**
	 * indicates entered by scanner
	 **/
	public static final int ENTRY_METHOD_SCANNED = 1;
	/**
	 * indicates entered by MICR
	 **/
	public static final int ENTRY_METHOD_MICR = 2;
	/**
	 * indicates entered by MSR
	 **/
	public static final int ENTRY_METHOD_MSR = 3;
	/**
	 * indicates entered by SmartCard
	 **/
	public static final int ENTRY_METHOD_SMARTCARD = 4;
	/**
	 * entry method descriptors
	 **/
	public static final String[] ENTRY_METHOD_DESCRIPTORS = { "Keyed", "Scanned", "MICR", "MSR", "SmartCard" };
	/**
	 * entry method codes
	 **/
	public static final String[] ENTRY_METHOD_CODES = { "KEY", "SCAN", "MICR", "MSR", "SMCD" };

}