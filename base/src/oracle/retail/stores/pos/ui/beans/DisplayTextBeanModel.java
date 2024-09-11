/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DisplayTextBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:44 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:27:50 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:21:06 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:41 PM  Robert Pearse   
 *
 *  Revision 1.5  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

//--------------------------------------------------------------------------
/**
    This is the bean model used by the DisplayTextBean. Contains the transaction
    text to display. <P>
    @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW; 
 **/
//--------------------------------------------------------------------------
public class DisplayTextBeanModel extends POSBaseBeanModel 
{
    /** 
        Revision number for this class 
    **/ 
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW:"; 
    
    /**
        Transaction text to display
    **/   
    String fieldDisplayText = new String("testin testing");    
    
    //--------------------------------------------------------------------------
    /**
       DisplayTextBeanModel default constructor <P>
       @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW; 
     **/
    //--------------------------------------------------------------------------
    public DisplayTextBeanModel() 
    {
        super();
    }
    
    //---------------------------------------------------------------------
    /**
       Retrieves transaction display text. <P>
       @return the string.
    **/
    //---------------------------------------------------------------------
    public String getDisplayText()
    {                                   // begin getDisplayText()
        return(fieldDisplayText);
    }                                   // end getDisplayText()
    
    //---------------------------------------------------------------------
    /**
       Sets transaction display text. <P>
       @param String  transaction display text
    **/
    //---------------------------------------------------------------------
    public void setDisplayText(String displayText)
    {                                   // begin setDisplayText()
        fieldDisplayText = displayText;
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
        
        buf.append("Class: DisplayTextBeanModel Revision: " + revisionNumber + "\n");
        buf.append("Transaction [" + fieldDisplayText + "]\n");

        return buf.toString();
    }
}
