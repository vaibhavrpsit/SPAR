/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EnterTillPayrollPayOutBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:44 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    .v8x      1.2.1.0     3/11/2007 7:58:43 PM   Brett J. Larsen CR 4530
 *          - default codes not being used
 *
 *         added storage for default approval code
 *
 *         renamed a method to avoid confusion
 *    3    360Commerce1.2         3/31/2005 4:28:05 PM   Robert Pearse   
 *    2    360Commerce1.1         3/10/2005 10:21:29 AM  Robert Pearse   
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

/**
 * 
 * @author khassen
 *
 * This class implements the bean model for the payroll
 * pay out use case.
 */
public class EnterTillPayrollPayOutBeanModel extends ReasonBeanModel
{
    /**
        Revision number
    */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    
    public static final int NUM_ADDRESS_LINES = 3;
    protected int      selectedApprovalCodeIndex;
    protected int      defaultApprovalCodeIndex;
    protected Vector   approvalCodes   = null;
    protected String   amount          = "";
    protected String   paidTo          = "";
    protected String   employeeID      = "";
    protected String[] addressLine     = new String[NUM_ADDRESS_LINES];
    protected int      numAddressLines = 1;
    protected String   comment         = "";
    
    /**
     * Returns the amount for the payroll pay out.
     * @return the string representation of the amount.
     */
    public String getAmount()
    {
            return amount;
    }

    /**
     * Sets the amount for the payroll pay out.
     * @param amt
     */
    public void setAmount(String amt)
    {
            amount = amt;
    }

    /**
     * Returns the paid to String entered by the user.
     * @return the string representation of the paidTo field.
     */
    public String getPaidTo()
    {
        return paidTo;
    }

    /**
     * Sets the paid to field.
     * @param pdTo
     */
    public void setPaidTo(String pdTo)
    {
        paidTo = pdTo;
    }

    /**
     * Sets the identification string of the employee
     * to whom we are paying out cash. 
     * @param i the string representation of the id.
     */
    public void setEmployeeID(String i)
    {
        employeeID = i;
    }

    /**
     * Returns the employee ID string value.
     * @return the employee ID
     */
    public String getEmployeeID()
    {
        return employeeID;
    }

    /**
     * Gets the number of address lines used
     * in this payroll pay out use case.
     * @return the number of address lines.
     */
    public int getNumAddressLines()
    {
        return numAddressLines;
    }

    /**
     * Sets the number of address lines used
     * in the payroll pay out use case.
     * @param lines
     */
    public void setNumAddressLines(int lines)
    {
        numAddressLines = lines;
    }

    /**
     * Sets the string representation of the address line
     * on a particular line.
     * @param i the line which we would like to set.
     * @param address the string representation of the address line.
     */
    public void setAddressLine(int i, String address)
    {
        if ((i > -1) || (i < NUM_ADDRESS_LINES))
        {
            addressLine[i] = address;
        }
    }

    /**
     * Gets the address line string at a particular line.
     * @param i the line we would like to return.
     * @return the string representation of the line.
     */
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

    /**
     * Gets the comment string.
     * @return string representing the comment.
     */
    public String getComment()
    {
        return comment;
    }

    /**
     * Sets the comment string.
     * @param com
     */
    public void setComment(String com)
    {
        comment = com;
    }
    
    /**
     * Gets the selected approval code index value as an integer.
     * @return the integer value of the approval code index.
     */
    public int getSelectedApprovalCodeIndex()
    {
        return selectedApprovalCodeIndex;
    }

    /**
     * Sets the selected approval code index value.
     * @param index
     */
    public void setSelectedApprovalCodeIndex(int index)
    {
        selectedApprovalCodeIndex = index;
    }

   /**
     * Gets the default approval code index value as an integer.
     * @return the integer value of the approval code index.
     */
    public int getDefaultApprovalCodeIndex()
    {
        return defaultApprovalCodeIndex;
    }

    /**
     * Sets the default approval code index value.
     * @param index
     */
    public void setDefaultApprovalCodeIndex(int index)
    {
        defaultApprovalCodeIndex = index;
    }
     
    
    /**
     * Sets the approval codes as a vector.
     * @param v
     */
    public void setApprovalCodes(Vector v)
    {
        approvalCodes = v;
    }

    /**
     * Gets the approval codes as a vector.
     * @return a vector of approval code strings.
     */
    public Vector getApprovalCodes()
    {
        return approvalCodes;
    }

    /**
     * Converts this object to a string.
     */
    public String toString()
    {
        StringBuffer buff = new StringBuffer();
        buff.append("Class: EnterTillPayrollPayOutBeanModel Revision: " + revisionNumber + "\n");
        buff.append("Amount [" + amount + "]\n");
        return(buff.toString());
    }
}
