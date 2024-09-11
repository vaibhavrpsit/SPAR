/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/salesassociate/ModifyTransactionSalesAssociateCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:32 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:05 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:36 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:41 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/27 22:32:05  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.4  2004/07/28 15:18:55  rsachdeva
 *   @scr 4865 Transaction Sales Associate
 *
 *   Revision 1.3  2004/02/12 16:51:14  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:02:42   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:15:34   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:39:24   msg
 * Initial revision.
 * 
 *    Rev 1.1   18 Jan 2002 19:03:32   baa
 * convert to new security model
 * Resolution for POS SCR-309: Convert to new Security Override design.
 *
 *    Rev 1.0   Sep 21 2001 11:31:18   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.salesassociate;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Vector;

import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.EmployeeCargoIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.common.TransactionCargoIfc;

/**
 * Cargo class for ModifyTransactionSalesAssociate service.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class ModifyTransactionSalesAssociateCargo extends AbstractFinancialCargo implements EmployeeCargoIfc,
        TransactionCargoIfc, Serializable
{
    private static final long serialVersionUID = 8938901709090657208L;

    /**
     * revision number of this class
     */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * flag indicating all items should be updated with new value
     */
    protected boolean updateAllItemsFlag = false;

    /**
     * flag indicating some of the items have already been updated
     **/
    protected boolean itemsModifiedFlag = false;

    /**
     * flag indicating this record has been modified
     **/
    protected boolean dirtyFlag = false;

    /**
     * default sales associate (from transaction)
     **/
    protected EmployeeIfc salesAssociate;

    /**
     * new sales associate
     **/
    protected EmployeeIfc employee;

    /**
     * Employee ID
     **/
    protected String employeeID;

    /**
     * Error code
     **/
    protected int errorCode = 0;

    /**
     * the transaction
     **/
    protected RetailTransactionIfc transaction;

    /**
     * flag to indicate whether the service created a transaction
     **/
    protected boolean transactionCreated = false;

    /**
     * flag to indicate whether the service needs to create a transaction
     **/
    protected boolean createTransaction = false;

    /**
     * false if no override is requested, true is override is needed
     * 
     * @deprecated as of release 5.0.0
     **/
    protected boolean securityOverrideFlag = false;

    /**
     * employee granting Security override
     * 
     * @deprecated as of release 5.0.0
     **/
    protected EmployeeIfc securityOverrideEmployee;

    /**
     * employee attempting Security override
     * 
     * @deprecated as of release 5.0.0
     **/
    protected EmployeeIfc securityOverrideRequestEmployee;

    /**
     * employee attempting Access
     * 
     * @deprecated as of release 5.0.0
     **/
    protected EmployeeIfc accessEmployee;

    /**
     * Security override Return Letter
     * 
     * @deprecated as of release 5.0.0
     **/

    protected String securityOverrideReturnLetter;

    /**
     * sales associate set using modify transaction sales associate
     **/
    protected boolean salesAssociateAlreadySet = false;

    // ----------------------------------------------------------------------
    /**
     * Constructs ModifyTransactionSalesAssociateCargo object.
     **/
    // ----------------------------------------------------------------------
    public ModifyTransactionSalesAssociateCargo()
    {
    }

    // ----------------------------------------------------------------------
    /**
     * Initialize cargo, setting sales associate value and checking for modified
     * item sales associates.
     * <P>
     * 
     * @param lineItems Vector of transaction line items
     * @param defaultSales default sales associate
     **/
    // ----------------------------------------------------------------------
    public void initialize(Vector lineItems, EmployeeIfc defaultSales)
    {
        // set default-sales-associate attribute
        salesAssociate = defaultSales;
        employee = defaultSales;
        itemsModifiedFlag = checkModifiedFlags(lineItems);
    }

    // ----------------------------------------------------------------------
    /**
     * Checks modified-flags on items.
     * <P>
     * 
     * @param items Vector of items to be checked
     * @return True if items have been modified
     **/
    // ---------------------------------------------------------------------
    public boolean checkModifiedFlags(Vector items)
    {
        // line item object, modified flag
        SaleReturnLineItemIfc srli = null;
        boolean modifiedFlag = false;
        // set up enumeration
        Enumeration e = items.elements();
        // set up loop
        boolean loopContinue = false;
        if (e.hasMoreElements())
        {
            loopContinue = true;
        }
        while (loopContinue)
        { // begin loop through line items
            // get element
            srli = (SaleReturnLineItemIfc) e.nextElement();
            // check item modified flag
            if (srli.getSalesAssociateModifiedFlag())
            {
                // if item has been modified, set flag and get out
                modifiedFlag = true;
                loopContinue = false;
            }
            // if no more elements, exit loop
            else if (!e.hasMoreElements())
            {
                loopContinue = false;
            }
        } // end loop through line items

        return (modifiedFlag);
    }

    // ----------------------------------------------------------------------
    /**
     * Returns the employee ID.
     * <P>
     * 
     * @return the employee ID
     **/
    // ----------------------------------------------------------------------
    public String getEmployeeID()
    {
        return (employeeID);
    }

    // ----------------------------------------------------------------------
    /**
     * Sets the employee ID.
     * <P>
     * 
     * @param employeeID new employee ID
     **/
    // ----------------------------------------------------------------------
    public void setEmployeeID(String employeeID)
    {
        this.employeeID = employeeID;
    }

    // ----------------------------------------------------------------------
    /**
     * Sets update-all-items flag.
     * <P>
     * 
     * @param value new update-all-items-flag setting
     **/
    // ----------------------------------------------------------------------
    public void setUpdateAllItemsFlag(boolean value)
    {
        updateAllItemsFlag = value;
    }

    // ----------------------------------------------------------------------
    /**
     * Returns update-all-items flag.
     * <P>
     * 
     * @return update-all-items flag
     **/
    // ----------------------------------------------------------------------
    public boolean getUpdateAllItemsFlag()
    {
        return (updateAllItemsFlag);
    }

    // ----------------------------------------------------------------------
    /**
     * Sets items-modified flag.
     * <P>
     * 
     * @param value new items-modified-flag setting
     **/
    // ----------------------------------------------------------------------
    public void setItemsModifiedFlag(boolean value)
    {
        itemsModifiedFlag = value;
    }

    // ----------------------------------------------------------------------
    /**
     * Returns items-modified flag.
     * <P>
     * 
     * @return items-modified flag
     **/
    // ----------------------------------------------------------------------
    public boolean getItemsModifiedFlag()
    {
        return (itemsModifiedFlag);
    }

    // ----------------------------------------------------------------------
    /**
     * Sets dirty flag.
     * <P>
     * 
     * @param value new dirty-flag setting
     **/
    // ----------------------------------------------------------------------
    public void setDirtyFlag(boolean value)
    {
        dirtyFlag = value;
    }

    // ----------------------------------------------------------------------
    /**
     * Returns dirty flag.
     * <P>
     * 
     * @return dirty flag
     **/
    // ----------------------------------------------------------------------
    public boolean getDirtyFlag()
    {
        return (dirtyFlag);
    }

    // ----------------------------------------------------------------------
    /**
     * Sets new sales associate.
     * <P>
     * 
     * @param value new sales associate
     **/
    // ----------------------------------------------------------------------
    public void setEmployee(EmployeeIfc value)
    {
        if (value != null)
        {
            // set value
            employee = value;
            // set dirty flag
            dirtyFlag = true;
        }
    }

    // ----------------------------------------------------------------------
    /**
     * Returns new sales associate.
     * <P>
     * 
     * @return new sales associate
     **/
    // ----------------------------------------------------------------------
    public EmployeeIfc getEmployee()
    {
        return (employee);
    }

    // ---------------------------------------------------------------------
    /**
     * Sets the sales associate.
     * <P>
     * 
     * @param value the sales associate
     **/
    // ---------------------------------------------------------------------
    public void setSalesAssociate(EmployeeIfc value)
    {
        salesAssociate = value;
    }

    // ---------------------------------------------------------------------
    /**
     * Returns the sales associate.
     * <P>
     * 
     * @return the sales associate
     **/
    // ---------------------------------------------------------------------
    public EmployeeIfc getSalesAssociate()
    {
        return (salesAssociate);
    }

    // ---------------------------------------------------------------------
    /**
     * Sets the retail transaction.
     * <P>
     * 
     * @param trans the retail transaction
     **/
    // ---------------------------------------------------------------------
    public void setTransaction(RetailTransactionIfc trans)
    {
        transaction = trans;
    }

    // ---------------------------------------------------------------------
    /**
     * Returns the retail transaction.
     * <P>
     * 
     * @return the retail transaction
     **/
    // ---------------------------------------------------------------------
    public RetailTransactionIfc getTransaction()
    {
        return (transaction);
    }

    // ---------------------------------------------------------------------
    /**
     * Sets whether the retail transaction was created in this service.
     * <P>
     * 
     * @param value true if the transaction was created, false otherwise
     **/
    // ---------------------------------------------------------------------
    public void setTransactionCreated(boolean value)
    {
        transactionCreated = value;
    }

    // ---------------------------------------------------------------------
    /**
     * Returns whether the retail transaction was created in this service.
     * <P>
     * 
     * @return true if the transaction was created, false otherwise
     **/
    // ---------------------------------------------------------------------
    public boolean getTransactionCreated()
    {
        return (transactionCreated);
    }

    // ---------------------------------------------------------------------
    /**
     * Sets whether the retail transaction can be created in this service.
     * <P>
     * 
     * @param value true if the transaction can be created, false otherwise
     **/
    // ---------------------------------------------------------------------
    public void setCreateTransaction(boolean value)
    {
        createTransaction = value;
    }

    // ---------------------------------------------------------------------
    /**
     * Returns whether the retail transaction can be created in this service.
     * <P>
     * 
     * @return true if the transaction can be created, false otherwise
     **/
    // ---------------------------------------------------------------------
    public boolean createTransaction()
    {
        return (createTransaction);
    }

    // --------------------------------------------------------------------------
    /**
     * Returns the securityOverrideFlag boolean.
     * <P>
     * 
     * @return The securityOverrideFlag boolean.
     * @deprecated as of release 5.0.0
     **/
    // ----------------------------------------------------------------------
    public boolean getSecurityOverrideFlag()
    { // begin getSecurityOverrideFlag()
        return securityOverrideFlag;
    } // end getSecurityOverrideFlag()

    // ----------------------------------------------------------------------
    /**
     * Sets the securityOverrideFlag boolean.
     * <P>
     * 
     * @param value The ssecurityOverrideFlag boolean.
     * @deprecated as of release 5.0.0
     **/
    // ----------------------------------------------------------------------
    public void setSecurityOverrideFlag(boolean value)
    { // begin setSecurityOverrideFlag()
        securityOverrideFlag = value;
        // end setSecurityOverrideFlag()
    }

    // ----------------------------------------------------------------------
    /**
     * Returns the securityOverrideEmployee object.
     * <P>
     * 
     * @return The securityOverrideEmployee object.
     * @deprecated as of release 5.0.0
     **/
    // ----------------------------------------------------------------------
    public EmployeeIfc getSecurityOverrideEmployee()
    { // begin getSecurityOverrideEmployee()
        return securityOverrideEmployee;
    } // end getSecurityOverrideEmployee()

    // ----------------------------------------------------------------------
    /**
     * Sets the security override employee object.
     * <P>
     * 
     * @param value The security override employee object.
     * @deprecated as of release 5.0.0
     **/
    // ----------------------------------------------------------------------
    public void setSecurityOverrideEmployee(EmployeeIfc value)
    { // begin setSecurityOverrideEmployee()
        securityOverrideEmployee = value;
    } // end setSecurityOverrideEmployee()

    // ----------------------------------------------------------------------
    /**
     * Returns the securityOverrideRequestEmployee object.
     * <P>
     * 
     * @return The securityOverrideRequestEmployee object.
     * @deprecated as of release 5.0.0
     **/
    // ----------------------------------------------------------------------
    public EmployeeIfc getSecurityOverrideRequestEmployee()
    { // begin getSecurityOverrideRequestEmployee()
        return securityOverrideRequestEmployee;
    } // end getSecurityOverrideRequestEmployee()

    // ----------------------------------------------------------------------
    /**
     * Sets the securityOverrideRequestEmployee object.
     * <P>
     * 
     * @param value securityOverrideRequestEmployee object.
     * @deprecated as of release 5.0.0
     **/
    // ----------------------------------------------------------------------
    public void setSecurityOverrideRequestEmployee(EmployeeIfc value)
    { // begin setSecurityOverrideRequestEmployee()
        securityOverrideRequestEmployee = value;
    } // end setSecurityOverrideRequestEmployee()

    // ----------------------------------------------------------------------
    /**
     * The access employee returned by this cargo is the currently logged on
     * cashier or an Override Security Employee
     * <P>
     * 
     * @return the void
     * @deprecated as of release 5.0.0
     **/
    // ----------------------------------------------------------------------
    public void setAccessEmployee(EmployeeIfc value)
    {
        accessEmployee = value;
    }

    // ----------------------------------------------------------------------
    /**
     * The access employee returned by this cargo is the currently logged on
     * cashier or an Override Security Employee
     * <P>
     * 
     * @return the EmployeeIfc value
     * @deprecated as of release 5.0.0
     **/
    // ----------------------------------------------------------------------
    public EmployeeIfc getAccessEmployee()
    {
        return accessEmployee;
    }

    // ----------------------------------------------------------------------
    /**
     * The securityOverrideReturnLetter returned by this cargo is to indecated
     * where the security override will return
     * <P>
     * 
     * @return the void
     * @deprecated as of release 5.0.0
     **/
    // ----------------------------------------------------------------------
    public void setSecurityOverrideReturnLetter(String value)
    {
        securityOverrideReturnLetter = value;
    }

    // ----------------------------------------------------------------------
    /**
     * The securityOverrideReturnLetter returned by this cargo is to indecated
     * where the security override will return
     * <P>
     * 
     * @return the String value
     * @deprecated as of release 5.0.0
     **/
    // ----------------------------------------------------------------------
    public String getSecurityOverrideReturnLetter()
    {
        return securityOverrideReturnLetter;

    }

    // ----------------------------------------------------------------------
    /**
     * Returns the function ID whose access is to be checked.
     * 
     * @return int Role Function ID
     **/
    // ----------------------------------------------------------------------
    public int getAccessFunctionID()
    {
        return RoleFunctionIfc.SET_SALES_ASSOCIATE;
    }

    // ----------------------------------------------------------------------
    /**
     * This is to keep track if sales associate set using transaction options
     * 
     * @param value true if being set first time
     **/
    // ----------------------------------------------------------------------
    public void setAlreadySetTransactionSalesAssociate(boolean value)
    {
        salesAssociateAlreadySet = value;
    }

    // ----------------------------------------------------------------------
    /**
     * Already set sales associate using transaction options return boolean true
     * is sales associate is already set
     **/
    // ----------------------------------------------------------------------
    public boolean isAlreadySetTransactionSalesAssociate()
    {
        return salesAssociateAlreadySet;
    }

    // ----------------------------------------------------------------------
    /**
     * Method to default display string function.
     * <P>
     * 
     * @return String representation of object
     **/
    // ----------------------------------------------------------------------
    public String toString()
    { // begin toString()
        // verbose flag
        boolean bVerbose = false;
        // result string
        String strResult = new String("Class:  ModifyTransactionSalesAssociateCargo (Revision " + getRevisionNumber()
                + ")" + hashCode());
        // if verbose mode, do inspection gig
        if (bVerbose)
        { // begin verbose mode
            // theClass will ascend through the inheritance hierarchy
            Class theClass = getClass();
            // fieldType contains the type of the field currently being examined
            Class fieldType = null;
            // fieldName contains the name of the field currently being examined
            String fieldName = "";
            // fieldValue contains the value of the field currently being
            // examined
            Object fieldValue = null;

            // Ascend through the class hierarchy, capturing field information
            while (theClass != null)
            { // begin loop through fields
                // fields contains all noninherited field information
                Field[] fields = theClass.getDeclaredFields();

                // Go through each field, capturing information
                for (int i = 0; i < fields.length; i++)
                {
                    fieldType = fields[i].getType();
                    fieldName = fields[i].getName();

                    // get the field's value, if possible
                    try
                    {
                        fieldValue = fields[i].get(this);
                    }
                    // if the value can't be gotten, say so
                    catch (IllegalAccessException ex)
                    {
                        fieldValue = "*no access*";
                    }
                    // If it is a "simple" field, use the value
                    if (Util.isSimpleClass(fieldType))
                    {
                        strResult += "\n\t" + fieldName + ":\t" + fieldValue;
                    } // if simple
                    // If it is a null value, say so
                    else if (fieldValue == null)
                    {
                        strResult += "\n\t" + fieldName + ":\t(null)";
                    }
                    // Otherwise, use <type<hashCode>
                    else
                    {
                        strResult += "\n\t" + fieldName + ":\t" + fieldType.getName() + "@" + fieldValue.hashCode();
                    }
                } // for each field
                theClass = theClass.getSuperclass();
            } // end loop through fields
        } // end verbose mode

        return (strResult);
    }

    // ----------------------------------------------------------------------
    /**
     * Returns the revision number of this class
     * <P>
     * 
     * @return String representation of revision number
     **/
    // ----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }

    // ----------------------------------------------------------------------
    /**
     * Returns the error code returned with a DataException.
     * <P>
     * 
     * @return the integer value
     **/
    // ----------------------------------------------------------------------
    public int getDataExceptionErrorCode()
    {
        return (errorCode);
    }

    // ----------------------------------------------------------------------
    /**
     * Sets the error code returned with a DataException.
     * <P>
     * 
     * @param the integer value
     **/
    // ----------------------------------------------------------------------
    public void setDataExceptionErrorCode(int value)
    {
        errorCode = value;
    }
} // end class ModifyTransactionSalesAssociateCargo
