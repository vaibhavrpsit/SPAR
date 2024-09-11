/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EnterTillPayOutBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:44 mszekely Exp $
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
 *    4    .v8x      1.2.1.0     3/11/2007 7:24:11 PM   Brett J. Larsen CR 4530
 *          - default code list values not being displayed
 *
 *         integrated a method rename (was done to reduce confustion and
 *         increase consistency)
 *
 *         added storage for default approval code
 *    3    360Commerce1.2         3/31/2005 4:28:05 PM   Robert Pearse   
 *    2    360Commerce1.1         3/10/2005 10:21:28 AM  Robert Pearse   
 *    1    360Commerce1.0         2/11/2005 12:10:55 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 18:30:41  cdb
 *   @scr 0 Removed tabs from all java source code.
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

import java.util.Vector;

import oracle.retail.stores.domain.utility.CodeConstantsIfc;


//----------------------------------------------------------------------------
/**
    This is model for the editing till payin pay out information.
 *  @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class EnterTillPayOutBeanModel extends ReasonBeanModel
{
    /**
        Revision number
    */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    
    public static final int NUM_ADDRESS_LINES = 3;
    /**
     *     Approval Code
     */
    int selectedApprovalCodeIndex = CodeConstantsIfc.CODE_INTEGER_UNDEFINED;
    int defaultApprovalCodeIndex = CodeConstantsIfc.CODE_INTEGER_UNDEFINED;

    Vector approvalCodes;
    /**
        Till PayIn/Pay Amount
    **/
    String amount = "";
    /**
     Till PayIn/Pay paid to
     **/
    String paidTo = "";
    /**
     * Till pay out employee ID entry.
     */
    String employeeID = "";
    /**
     Till PayIn/Pay address line 1
     **/
    String[] addressLine = new String[NUM_ADDRESS_LINES];
    protected int numAddressLines;
    /**
     Till PayIn/Pay comment
     **/
    String comment = "";
    
    
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
     Get the value of the Amount field
     @return the value of Amount
     **/
    //--------------------------------------------------------------------------
    public String getPaidTo()
    {
        return paidTo;
    }

    public void setEmployeeID(String i)
    {
        employeeID = i;
    }
    public String getEmployeeID()
    {
        return employeeID;
    }
    //--------------------------------------------------------------------------
    /**
     Sets the Amount field
     @param Amount the value to be set for Amount
     **/
    //--------------------------------------------------------------------------
    public void setPaidTo(String pdTo)
    {
        paidTo = pdTo;
    }

    public int getNumAddressLines()
    {
        return numAddressLines;
    }
    public void setNumAddressLines(int lines)
    {
        numAddressLines = lines;
    }
    public void setAddressLine(int i, String address)
    {
        if ((i > -1) || (i < NUM_ADDRESS_LINES))
        {
            addressLine[i] = address;
        }
    }
    public String getAddressLine(int i)
    {
        if ((i > -1) || (i < NUM_ADDRESS_LINES))
        {
            return addressLine[i];
        }
        else
        {
            return null;
        }
    }
    //--------------------------------------------------------------------------
    /**
     Get the value of the Amount field
     @return the value of Amount
     **/
    //--------------------------------------------------------------------------
    public String getComment()
    {
        return comment;
    }

    //--------------------------------------------------------------------------
    /**
     Sets the Amount field
     @param Amount the value to be set for Amount
     **/
    //--------------------------------------------------------------------------
    public void setComment(String com)
    {
        comment = com;
    }
    
    


    //--------------------------------------------------------------------------
    /**
     *  Gets the reasonCodes property (Vector) value.
     *  @return Vector
     *  @see #setReasonCodes
     */
    //--------------------------------------------------------------------------
    public int getSelectedApprovalCodeIndex()
    {
        return selectedApprovalCodeIndex;
    }
    
    public void setSelectedApprovalCodeIndex(int index)
    {
        selectedApprovalCodeIndex = index;
    }
    
    public void setApprovalCodes(Vector v)
    {
        approvalCodes = v;
    }

    public Vector getApprovalCodes()
    {
        return approvalCodes;
    }

    //----------------------------------------------------------------------
    /**
     @return Returns the defaultApprovalCodeIndex.
     **/
    //----------------------------------------------------------------------
    public int getDefaultApprovalCodeIndex()
    {
        return defaultApprovalCodeIndex;
    }

    //----------------------------------------------------------------------
    /**
     
     @param defaultApprovalCodeIndex The defaultApprovalCodeIndex to set.
     **/
    //----------------------------------------------------------------------
    public void setDefaultApprovalCodeIndex(int defaultApprovalCodeIndex)
    {
        this.defaultApprovalCodeIndex = defaultApprovalCodeIndex;
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
        buff.append("Class: EnterTillPayOutBeanModel Revision: " + revisionNumber + "\n");
        buff.append("Amount [" + amount + "]\n");
        return(buff.toString());
    }
}
