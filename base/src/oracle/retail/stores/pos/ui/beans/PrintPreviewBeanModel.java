/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/PrintPreviewBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/11 16:05:18 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.ui.beans;

public class PrintPreviewBeanModel extends POSBaseBeanModel {
	/** 
    Revision number for this class 
	 **/ 
	public static final String revisionNumber = ""; 

	/**
    Preview text to display
	 **/   
	String printPreviewText = new String("testin testing");    

	//--------------------------------------------------------------------------
	/**
   PrintPreviewBeanModel default constructor <P>
   @version
	 **/
	//--------------------------------------------------------------------------
	public PrintPreviewBeanModel() 
	{
		super();
	}

	//---------------------------------------------------------------------
	/**
   Retrieves print preview display text. <P>
   @return the string.
	 **/
	//---------------------------------------------------------------------
	public String getPrintPreviewText()
	{                                   // begin getPrintPreviewText()
		return(printPreviewText);
	}                                   // end getPrintPreviewText()

	//---------------------------------------------------------------------
	/**
   Sets print preview display text. <P>
   @param String  transaction display text
	 **/
	//---------------------------------------------------------------------
	public void setPrintPreviewText(String printPreviewText)
	{                                   // begin setDisplayText()
		this.printPreviewText = printPreviewText;
	}                                  // end getDisplayText()

	//--------------------------------------------------------------------------
	/**
   Converts to a string representing the data in this object
   @returns a string representing the data in this object
	 **/
	//--------------------------------------------------------------------------
	public String toString() 
	{
		StringBuffer buf = new StringBuffer();

		buf.append("Class: PrintPreviewBeanModel Revision: " + revisionNumber + "\n");
		buf.append("Print Preview [" + printPreviewText + "]\n");

		return buf.toString();
	}


}
