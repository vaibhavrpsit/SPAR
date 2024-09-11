/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/tax/ModifyTransactionTaxCargo.java /main/12 2011/02/16 09:13:32 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 02/15/11 - move constants into interfaces and refactor
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    ranojh 11/04/08 - Code refreshed to tip
 *    acadar 11/03/08 - localization of transaction tax reason codes
 * ===========================================================================
     $Log:
      3    360Commerce 1.2         3/31/2005 4:29:05 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:23:36 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:41 PM  Robert Pearse
     $
     Revision 1.6  2004/09/27 22:32:04  bwf
     @scr 7244 Merged 2 versions of abstractfinancialcargo.

     Revision 1.5  2004/07/27 00:07:45  jdeleau
     @scr 6251 Make sure when tax is toggled off, that the original tax is charged when its toggled back on

     Revision 1.4  2004/07/13 00:23:13  jdeleau
     @scr 6186 Make sure tax scope is correctly saved in the ItemTaxIfc.
     Correct some code in TransactionOverride that was incorrectly using
     the scope variable to check for modifications to tax.

     Revision 1.3  2004/02/12 16:51:17  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:51:37  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.1   Jan 13 2004 16:55:58   lzhao
 * add requireCertificateInfo parameter to show/skip TaxExempt screen.
 * Resolution for 3655: Feature Enhancement:  Tax Exempt Enhancement
 *
 *    Rev 1.0   Aug 29 2003 16:02:52   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jan 14 2003 09:12:34   RSachdeva
 * Replaced AbstractFinancialCargo.getCodeListMap()   by UtilityManagerIfc.getCodeListMap()
 * Resolution for POS SCR-1907: Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 *
 *    Rev 1.0   Apr 29 2002 15:14:50   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:39:50   msg
 * Initial revision.
 *
 *    Rev 1.1   17 Jan 2002 13:01:56   pjf
 * Modified to use new security override service and correct SCR 403.
 * Resolution for POS SCR-403: Security Override continually loops in Trans Tax
 *
 *    Rev 1.0   Sep 21 2001 11:31:42   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:04   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.modifytransaction.tax;

// java imports
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Vector;

import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.TransactionCargoIfc;

//------------------------------------------------------------------------------
/**
    Cargo class for ModifyTransactionTaxIfc service. <P>
    @version $Revision: /main/12 $
**/
//------------------------------------------------------------------------------
public class ModifyTransactionTaxCargo extends AbstractFinancialCargo
                                       implements CargoIfc,
                                                  TransactionCargoIfc
{                                       // begin class ModifyTransactionTaxCargo
    /**
        revision number
    **/
    public static String revisionNumber = "$Revision: /main/12 $";

    /**
        constant for update rate
    **/
    public static final int TAX_UPDATE_RATE = 0;
    /**
        constant for update amount
    **/
    public static final int TAX_UPDATE_AMOUNT = 1;

    /**
        flag indicating all items should be updated with new value
    **/
    protected boolean updateAllItemsFlag = false;
    /**
        flag indicating some of the items have already been updated
    **/
    protected boolean itemsModifiedFlag = false;
    /**
        flag indicating this record has been modified
    **/
    protected boolean dirtyFlag = false;
    /**
        flag indicating that the next letter should be sent
    **/
    protected boolean nextFlag = false;
    /**
        flag indicating type of tax update -- rate or amount
    **/
    protected int taxUpdateFlag = 0;
    /**
        transaction tax object
    **/
    protected TransactionTaxIfc transactionTax = null;

    /**
        the transaction
    **/
    protected RetailTransactionIfc transaction;

    /**
        the transaction
    **/
    protected CustomerIfc customer;
    /**
        the sales associate
    **/
    protected EmployeeIfc salesAssociate;

    /**
        flag to indicate whether the service created a transaction
    **/
    protected boolean transactionCreated = false;

    /**
        flag to indicate whether the service needs to create a transaction
    **/
    protected boolean createTransaction = false;
    /**
        flag to indicate whether the service linked a customer
    **/
    protected boolean customerLinked = false;
    /**
        flag to indicate whether the a customer was previously linked
    **/
    protected boolean customerPreviouslyLinked = false;
    /**
     false if no override is requested, true is override is needed
    **/
    protected boolean securityOverrideFlag = false;
    /**
       true if require reason code and certificate number
    **/
    protected boolean requireCertificateInfo = true;

    /**
     * Localized tax amount reason codes
     */
    protected CodeListIfc localizedOverrideAmountReasonCodes = null;

    /**
     * Localized override transaction tax rate reason codes
     */
    protected CodeListIfc localizedOverrideRateReasonCodes = null;

    /**
        employee granting Security override
        @deprecated as of release 5.0.
    **/
    protected EmployeeIfc securityOverrideEmployee;

    /**
        employee attempting Security override
        @deprecated as of release 5.0.
    **/
    protected EmployeeIfc securityOverrideRequestEmployee;

    /**
        employee attempting Access
        @deprecated as of release 5.0.
    **/
    protected EmployeeIfc accessEmployee;

    /**
        Security override Return Letter
        @deprecated as of release 5.0.
    **/
    protected String securityOverrideReturnLetter;
    
    /**
     * Localized Tax Exempt reason codes
     */
    protected CodeListIfc localizedTaxExemptReasonCodes = null;

    //---------------------------------------------------------------------
    /**
        Constructs ModifyTransactionTaxCargo object. <P>
        <B>Pre-Condition</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition</B>
        <UL>
        <LI>none
        </UL>
    **/
    //---------------------------------------------------------------------
    public ModifyTransactionTaxCargo()
    {                                   // begin ModifyTransactionTaxCargo()
    }                                   // end ModifyTransactionTaxCargo()

    //---------------------------------------------------------------------
    /**
        Initialize cargo, setting transaction tax attribute and checking for
        modified item tax objects. <P>
        <B>Pre-Condition</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition</B>
        <UL>
        <LI>none
        </UL>
        @param lineItems  Vector of transaction line items
        @param tax TransactionTaxIfc object
    **/
    //---------------------------------------------------------------------
    public void initialize(Vector lineItems,
                           TransactionTaxIfc tax)
    {                                   // begin initialize()
        itemsModifiedFlag = checkModifiedFlags(lineItems);
        transactionTax = tax;
    }                                   // end initialize()

    //---------------------------------------------------------------------
    /**
        Checks modified-flags on items. <P>
        <B>Pre-Condition</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition</B>
        <UL>
        <LI>none
        </UL>
        @param items  Vector of items to be checked
        @return boolean  indicator set to true if any single item has been tax modified
    **/
    //---------------------------------------------------------------------
    public boolean checkModifiedFlags(Vector items)
    {                                   // begin checkModifiedFlags()
        // line item object, modified flag
        SaleReturnLineItemIfc srli = null;
        boolean modifiedFlag = false;
        // set up enumeration
        Enumeration e = items.elements();
        while (!modifiedFlag && e.hasMoreElements())
        {                               // begin loop through line items
            // get element
            srli = (SaleReturnLineItemIfc) e.nextElement();
            ItemTaxIfc itemTax = srli.getItemPrice().getItemTax();
            modifiedFlag = (itemTax.getTaxMode() != itemTax.getOriginalTaxMode());
        }                               // end loop through line items
        // pass back modified flag
        return(modifiedFlag);
    }                                   // end checkModifiedFlags()

    //----------------------------------------------------------------------
    /**
        Returns the function ID whose access is to be checked.
        @return int Role Function ID
    **/
    //----------------------------------------------------------------------
    public int getAccessFunctionID()
    {
        return RoleFunctionIfc.TAX_MODIFICATION;
    }

    //---------------------------------------------------------------------
    /**
        Sets update-all-items flag. <P>
        @param value  new update-all-items-flag setting
    **/
    //---------------------------------------------------------------------
    public void setUpdateAllItemsFlag(boolean value)
    {
        updateAllItemsFlag = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns update-all-items flag. <P>
        @return update-all-items flag
    **/
    //---------------------------------------------------------------------
    public boolean getUpdateAllItemsFlag()
    {
        return(updateAllItemsFlag);
    }

    //---------------------------------------------------------------------
    /**
        Sets items-modified flag. <P>
        @param value  new items-modified-flag setting
    **/
    //---------------------------------------------------------------------
    public void setItemsModifiedFlag(boolean value)
    {
        itemsModifiedFlag = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns items-modified flag. <P>
        @return items-modified flag
    **/
    //---------------------------------------------------------------------
    public boolean getItemsModifiedFlag()
    {
        return(itemsModifiedFlag);
    }

    //---------------------------------------------------------------------
    /**
        Sets dirty flag. <P>
        @param value  new dirty-flag setting
    **/
    //---------------------------------------------------------------------
    public void setDirtyFlag(boolean value)
    {
        dirtyFlag = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns dirty flag. <P>
        @return dirty flag
    **/
    //---------------------------------------------------------------------
    public boolean getDirtyFlag()
    {
        return(dirtyFlag);
    }

    //---------------------------------------------------------------------
    /**
        Sets next flag. <P>
        @param value  new next-flag setting
    **/
    //---------------------------------------------------------------------
    public void setNextFlag(boolean value)
    {
        nextFlag = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns next flag. <P>
        @return next flag
    **/
    //---------------------------------------------------------------------
    public boolean getNextFlag()
    {
        return(nextFlag);
    }

    //---------------------------------------------------------------------
    /**
        Resets Cargo. <P>
    **/
    //---------------------------------------------------------------------
    public void resetCargo()
    {
        // reset members
        dirtyFlag = false;
        customerLinked = false;
    }

    /**
     * Returns exempt reason-code list. <P>
     * @return exempt reason-code list
     */
    public CodeListIfc getLocalizedExemptReasonCodes()
    {
    	return localizedTaxExemptReasonCodes;
    }
    
    /**
     * Method sets the localizedTaxExemptReasonCodes
     * @param localizedTaxExemptReasonCodes the localizedTaxExemptReasonCodes to set
     */
    public void setLocalizedTaxExemptReasonCodes(CodeListIfc localizedTaxExemptReasonCodes)
    {
        this.localizedTaxExemptReasonCodes = localizedTaxExemptReasonCodes;
    }

    //---------------------------------------------------------------------
    /**
        Returns tax-update flag. <P>
        @return tax-update flag
    **/
    //---------------------------------------------------------------------
    public int getTaxUpdateFlag()
    {
        return(taxUpdateFlag);
    }

    //---------------------------------------------------------------------
    /**
        Sets tax-update flag. <P>
        @param value  new tax-update-flag setting
    **/
    //---------------------------------------------------------------------
    public void setTaxUpdateFlag(int value)
    {
        taxUpdateFlag = value;
    }

    //---------------------------------------------------------------------
    /**
        Sets transaction tax object. <P>
        @param value  new transaction tax object
    **/
    //---------------------------------------------------------------------
    public void setTransactionTax(TransactionTaxIfc value)
    {
        transactionTax = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns transaction tax object. <P>
        @return transaction tax object
    **/
    //---------------------------------------------------------------------
    public TransactionTaxIfc getTransactionTax()
    {
        return(transactionTax);
    }

    //---------------------------------------------------------------------
    /**
        Sets the sales associate. <P>
        @param value  the sales associate
    **/
    //---------------------------------------------------------------------
    public void setSalesAssociate(EmployeeIfc value)
    {
        salesAssociate = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns the sales associate. <P>
        @return the sales associate
    **/
    //---------------------------------------------------------------------
    public EmployeeIfc getSalesAssociate()
    {
        return(salesAssociate);
    }

    //---------------------------------------------------------------------
    /**
        Sets the retail transaction. <P>
        @param  trans   the retail transaction
    **/
    //---------------------------------------------------------------------
    public void setTransaction(RetailTransactionIfc trans)
    {
        transaction = trans;
    }

    //---------------------------------------------------------------------
    /**
        Returns the retail transaction. <P>
        @return the retail transaction
    **/
    //---------------------------------------------------------------------
    public RetailTransactionIfc getTransaction()
    {
        return(transaction);
    }

    //---------------------------------------------------------------------
    /**
        Sets the customer. <P>
        @param  cust   the customer
    **/
    //---------------------------------------------------------------------
    public void setCustomer(CustomerIfc cust)
    {
        customer = cust;
    }

    //---------------------------------------------------------------------
    /**
        Returns the customer. <P>
        @return the customer
    **/
    //---------------------------------------------------------------------
    public CustomerIfc getCustomer()
    {
        return(customer);
    }

    //---------------------------------------------------------------------
    /**
        Sets whether the retail transaction was created in this service.
        <P>
        @param  value   true if the transaction was created, false otherwise
    **/
    //---------------------------------------------------------------------
    public void setTransactionCreated(boolean value)
    {
        transactionCreated = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns whether the retail transaction was created in this service.
        <P>
        @return true if the transaction was created, false otherwise
    **/
    //---------------------------------------------------------------------
    public boolean getTransactionCreated()
    {
        return(transactionCreated);
    }

    //---------------------------------------------------------------------
    /**
        Sets whether the customer link  to the tax transaction  in this service.
        <P>
        @param  value   true if the customer was link to the transaction, false otherwise
    **/
    //---------------------------------------------------------------------
    public void setCustomerLinked(boolean value)
    {
        customerLinked = value;
    }
    //---------------------------------------------------------------------
    /**
        Returns whether a customer has been linked  to the tax transaction  in this service.
        <P>
        @return true  if the customer was link to the transaction, false otherwise
    **/
    //---------------------------------------------------------------------
    public boolean getCustomerLinked()
    {
        return (customerLinked);
    }

    //---------------------------------------------------------------------
    /**
        Sets whether the customer was link  prior to the tax transaction .
        <P>
        @param  value   true if a customer was link to the transaction, false otherwise
    **/
    //---------------------------------------------------------------------
    public void setCustomerPreviouslyLinked(boolean value)
    {
        customerPreviouslyLinked = value;
    }
    //---------------------------------------------------------------------
    /**
        Returns whether a customer has been linked  prior to the tax transaction.
        <P>
        @return true  if the customer was link to the transaction, false otherwise
    **/
    //---------------------------------------------------------------------
    public boolean getCustomerPreviouslyLinked()
    {
        return (customerPreviouslyLinked);
    }
    //---------------------------------------------------------------------
    /**
        Sets whether the retail transaction can be created in this service.
        <P>
        @param  value   true if the transaction can be created, false otherwise
    **/
    //---------------------------------------------------------------------
    public void setCreateTransaction(boolean value)
    {
        createTransaction = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns whether the retail transaction can be created in this service.
        <P>
        @return true if the transaction can be created, false otherwise
    **/
    //---------------------------------------------------------------------
    public boolean createTransaction()
    {
        return(createTransaction);
    }
   //--------------------------------------------------------------------------
    /**
        Returns the securityOverrideFlag boolean. <P>
        @return The securityOverrideFlag boolean.
    **/
    //----------------------------------------------------------------------
    public boolean getSecurityOverrideFlag()
    {                                   // begin getSecurityOverrideFlag()
        return securityOverrideFlag;
    }                                   // end getSecurityOverrideFlag()

    //--------------------------------------------------------------------------
    /**
        <P> get require certificate number and reason code
        @return ture if capture reason code.
    **/
    //----------------------------------------------------------------------
    public boolean requireCertificateInfo()
    {
        return requireCertificateInfo;
    }

    //--------------------------------------------------------------------------
    /**
       <P> set require certificate number or reason code
       @param boolean
    **/
    //----------------------------------------------------------------------
    public void setRequireCertificateInfo(boolean require)
    {
        requireCertificateInfo = require;
    }


    //----------------------------------------------------------------------
    /**
        Sets the securityOverrideFlag boolean. <P>
        @param  value  The ssecurityOverrideFlag boolean.
        @deprecated as of release 5.0.
    **/
    //----------------------------------------------------------------------
    public void setSecurityOverrideFlag(boolean value)
    {                                   // begin setSecurityOverrideFlag()
        securityOverrideFlag = value;
                                        // end setSecurityOverrideFlag()
    }
    //----------------------------------------------------------------------
    /**
        Returns the securityOverrideEmployee object. <P>
        @return The securityOverrideEmployee object.
        @deprecated as of release 5.0.
    **/
    //----------------------------------------------------------------------
    public EmployeeIfc getSecurityOverrideEmployee()
    {                                   // begin getSecurityOverrideEmployee()
        return securityOverrideEmployee;
    }                                   // end getSecurityOverrideEmployee()

    //----------------------------------------------------------------------
    /**
        Sets the security override employee object. <P>
        @param  value  The security override employee object.
        @deprecated as of release 5.0.
    **/
    //----------------------------------------------------------------------
    public void setSecurityOverrideEmployee(EmployeeIfc value)
    {                                   // begin setSecurityOverrideEmployee()
        securityOverrideEmployee = value;
    }                                   // end setSecurityOverrideEmployee()
    //----------------------------------------------------------------------
    /**
        Returns the securityOverrideRequestEmployee object. <P>
        @return The securityOverrideRequestEmployee object.
        @deprecated as of release 5.0.
    **/
    //----------------------------------------------------------------------
    public EmployeeIfc getSecurityOverrideRequestEmployee()
    {                                   // begin getSecurityOverrideRequestEmployee()
        return securityOverrideRequestEmployee;
    }                                   // end getSecurityOverrideRequestEmployee()

    //----------------------------------------------------------------------
    /**
        Sets the securityOverrideRequestEmployee object. <P>
        @param  value  securityOverrideRequestEmployee object.
        @deprecated as of release 5.0.
    **/
    //----------------------------------------------------------------------
    public void setSecurityOverrideRequestEmployee(EmployeeIfc value)
    {                                   // begin setSecurityOverrideRequestEmployee()
        securityOverrideRequestEmployee = value;
    }                                   // end setSecurityOverrideRequestEmployee()
    //----------------------------------------------------------------------
    /**
        The access employee returned by this cargo is the currently
        logged on cashier or an Override Security Employee
        <P>
        @deprecated as of release 5.0.
        @return the void
    **/
    //----------------------------------------------------------------------
    public void setAccessEmployee(EmployeeIfc value)
    {
        accessEmployee = value;
    }
    //----------------------------------------------------------------------
    /**
        The access employee returned by this cargo is the currently
        logged on cashier or an Override Security Employee
        <P>
        @deprecated as of release 5.0.
        @return the EmployeeIfc value
    **/
    //----------------------------------------------------------------------
    public EmployeeIfc getAccessEmployee()
    {
        return accessEmployee;
    }

    //----------------------------------------------------------------------
    /**
        The securityOverrideReturnLetter returned by this cargo is to indecated
        where the security override will return
        <P>
        @return the void
        @deprecated as of release 5.0.
    **/
    //----------------------------------------------------------------------
    public void setSecurityOverrideReturnLetter(String value)
    {
        securityOverrideReturnLetter = value;
    }
    //----------------------------------------------------------------------
    /**
        The securityOverrideReturnLetter returned by this cargo is to indecated
        where the security override will return
        <P>
        @deprecated as of release 5.0.
        @return the String value
    **/
    //----------------------------------------------------------------------
    public String getSecurityOverrideReturnLetter()
    {
        return securityOverrideReturnLetter;
    }

    //---------------------------------------------------------------------
    /**
        Returns the string representation of the object. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        // verbose flag
        boolean bVerbose = false;
        // result string
        String strResult = new String("Class:  ModifyTransactionTaxCargo (Revision " +
                                      getRevisionNumber() +
                                      ")" +
                                      hashCode());
        // if verbose mode, do inspection gig
        if (bVerbose)
        {                               // begin verbose mode
            // theClass will ascend through the inheritance hierarchy
            Class theClass = getClass();
            // fieldType contains the type of the field currently being examined
            Class fieldType = null;
            // fieldName contains the name of the field currently being examined
            String fieldName = "";
            // fieldValue contains the value of the field currently being examined
            Object fieldValue = null;

            // Ascend through the class hierarchy, capturing field information
            while (theClass != null)
            {                           // begin loop through fields
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
                        strResult += "\n\t" +
                                     fieldName +
                                     ":\t" +
                                     fieldValue;
                    }       // if simple
                    // If it is a null value, say so
                    else if (fieldValue == null)
                    {
                        strResult += "\n\t" +
                                     fieldName +
                                     ":\t(null)";
                    }
                    // Otherwise, use <type<hashCode>
                    else
                    {
                        strResult += "\n\t" +
                                     fieldName +
                                     ":\t" +
                                     fieldType.getName() +
                                     "@" +
                                     fieldValue.hashCode();
                    }
                }   // for each field
                theClass = theClass.getSuperclass();
            }                           // end loop through fields
        }                               // end verbose mode

        return(strResult);
    }                                   // end toString()

    //---------------------------------------------------------------------
    /**
        Returns the revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }

    /**
     * @return the localizedOverrideAmountReasonCodes
     */
    public CodeListIfc getLocalizedOverrideAmountReasonCodes()
    {
        return localizedOverrideAmountReasonCodes;
    }

    /**
     * @param localizedOverrideAmountReasonCodes the localizedOverrideAmountReasonCodes to set
     */
    public void setLocalizedOverrideAmountReasonCodes(CodeListIfc localizedOverrideAmountReasonCodes)
    {
        this.localizedOverrideAmountReasonCodes = localizedOverrideAmountReasonCodes;
    }

    /**
     * @return the localizedOverrideRateReasonCodes
     */
    public CodeListIfc getLocalizedOverrideRateReasonCodes()
    {
        return localizedOverrideRateReasonCodes;
    }

    /**
     * @param localizedOverrideRateReasonCodes the localizedOverrideRateReasonCodes to set
     */
    public void setLocalizedOverrideRateReasonCodes(CodeListIfc localizedOverrideRateReasonCodes)
    {
        this.localizedOverrideRateReasonCodes = localizedOverrideRateReasonCodes;
    }
}                                       // end class ModifyTransactionTaxCargo

