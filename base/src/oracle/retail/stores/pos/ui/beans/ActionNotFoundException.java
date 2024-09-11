/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ActionNotFoundException.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:55 mszekely Exp $
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
 *   3    360Commerce 1.2         3/31/2005 4:27:08 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:19:30 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:09:23 PM  Robert Pearse   
 *
 *  Revision 1.6  2004/09/23 00:07:11  kmcbride
 *  @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *  Revision 1.5  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

//----------------------------------------------------------------------
/**
   Inner class used for exceptions when retrieving the actions associated
   with the ui.

   @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
*/
//----------------------------------------------------------------------
public class ActionNotFoundException extends Exception
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -1087770918864605148L;

    /** revision number supplied by version control */
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";
    
    //------------------------------------------------------------------
    /**
           Default constructor.
    */
    //------------------------------------------------------------------
    public ActionNotFoundException()
    {
        super();
    }

    //------------------------------------------------------------------
    /**
       Construct with a given message.
       @param str the message in the exception
    */
    //------------------------------------------------------------------
    public ActionNotFoundException(String str)
    {
        super(str);
    }
}
