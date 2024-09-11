/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EnterTillPayInBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:42 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:05 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:28 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:55 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.1  2004/03/12 18:48:29  khassen
 *   @scr 0 Till Pay In/Out use case
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:10:32   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Feb 13 2003 10:30:26   HDyer
 * Modified class to extend from ReasonBeanModel. Removed variables and methods now handled by ReasonBeanModel. Modified headers.
 * Resolution for POS SCR-2035: I18n Reason Code support
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;



//----------------------------------------------------------------------------
/**
    This is model for the editing till payin pay out information.
 *  @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class EnterTillPayInBeanModel extends ReasonBeanModel
{
    /**
        Revision number
    */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        Till PayIn/Pay Amount
    **/
    String amount = "";

    //--------------------------------------------------------------------------
    /**
            Get the value of the Amount field
            @return the value of Amount
    **/
    //--------------------------------------------------------------------------
    public String getAmount()
    {
            return amount;
    }

    //--------------------------------------------------------------------------
    /**
            Sets the Amount field
            @param Amount the value to be set for Amount
    **/
    //--------------------------------------------------------------------------
    public void setAmount(String amt)
    {
            amount = amt;
    }

    //--------------------------------------------------------------------------
    /**
            Converts to a string representing the data in this Object
            @returns string representing the data in this Object
    **/
    //--------------------------------------------------------------------------
    public String toString()
    {
        StringBuffer buff = new StringBuffer();
        buff.append("Class: EnterTillPayInPayOutBeanModel Revision: " + revisionNumber + "\n");
        buff.append("Amount [" + amount + "]\n");
        return(buff.toString());
    }
}
