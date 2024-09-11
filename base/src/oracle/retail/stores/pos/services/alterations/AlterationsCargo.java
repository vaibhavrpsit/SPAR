/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/alterations/AlterationsCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:16 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       06/08/10 - fix tab
 *    sgu       06/08/10 - add item # & desc to the screen prompt. fix unknow
 *                         item screen to disable price and quantity for
 *                         external item
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:13 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:19:36 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:27 PM  Robert Pearse
 *
 *   Revision 1.6  2004/09/27 22:32:04  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.5  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.4  2004/04/08 22:14:55  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *   Revision 1.3  2004/02/12 16:49:04  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:53:52   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Mar 12 2003 12:27:24   DCobb
 * Code review cleanup.
 * Resolution for POS SCR-1753: POS 6.0 Alterations Package
 *
 *    Rev 1.3   Mar 05 2003 18:18:12   DCobb
 * Generalized names of alterations attributes.
 * Resolution for POS SCR-1808: Alterations instructions not saved and not printed when trans. suspended
 *
 *    Rev 1.2   Sep 25 2002 17:37:14   DCobb
 * Added price entered indicator.
 * Resolution for POS SCR-1802: Response region defaults 0.00 after alterations item is added
 *
 *    Rev 1.1   Aug 21 2002 11:21:20   DCobb
 * Added Alterations service.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.alterations;

// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.EmployeeCargoIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.pos.services.common.TransactionCargoIfc;

//--------------------------------------------------------------------------
/**
    The cargo needed by the Alterations service. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class AlterationsCargo extends AbstractFinancialCargo
                              implements TransactionCargoIfc,
                                         EmployeeCargoIfc
{                                        // begin class AlterationsCargo
    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.alterations.AlterationsCargo.class);
    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        Constant for the Pants button action.
    **/
    public static final String ACTION_PANTS         = "Pants";
    /**
        Constant for the Shirt button action.
    **/
    public static final String ACTION_SHIRT         = "Shirt";
    /**
        Constant for the Coat button action.
    **/
    public static final String ACTION_COAT          = "Coat";
    /**
        Constant for the Skirt button action.
    **/
    public static final String ACTION_SKIRT         = "Skirt";
    /**
        Constant for the Dress button action.
    **/
    public static final String ACTION_DRESS         = "Dress";
    /**
        Constant for the Repairs button action.
    **/
    public static final String ACTION_REPAIRS       = "Repairs";

    /**
       line item
    **/
    protected PLUItemIfc item;

    /**
       letter
    **/
    protected String letter;

    /**
        employee ID
    **/
    protected String employeeID;

    /**
       alterationsCustomer
    **/
    protected CustomerIfc customer;

    /**
       The sales associate
    **/
    protected EmployeeIfc employee;

    /**
       transaction type - sale or return
    **/
    protected RetailTransactionIfc transaction;

    /**
       line item index
    **/
    protected int index;

    /**
       price entered indicator
    **/
    protected boolean priceEntered = false;

    /**
        Indicator if UI needs to be refreshed with the recently linked customer to the transaction.
    **/
    protected boolean customerLinkRefreshUI = false;

    /**
        new item flag - indicates when an new item is being added
        vs. when an existing item is being modified
    **/
    protected boolean newPLUItem = true;

    /**
        add PLU item flag - needed for modify item service when a
        new item has successfully been created.
    **/
    protected boolean addPLUItem = false;

    /**
        modify item service flag
    **/
    protected boolean modifyItemService = false;

    /**
     * create transaction flag
     */
    protected boolean createTransaction = false;

    /**
     * transaction created flag
     */
    protected boolean transactionCreated = false;

    /**
     * This flag indicates whether to skip enter price even if the price entry
     * required flag of a PLU item is set to true.
     */
    protected boolean skipPriceEntryFlag = false;

    //---------------------------------------------------------------------
    /**
       Constructor
    **/
    //---------------------------------------------------------------------
    public AlterationsCargo()
    {
    }

    //---------------------------------------------------------------------
    /**
       Retrieves line item. <P>
       @return line item
    **/
    //---------------------------------------------------------------------
    public PLUItemIfc getPLUItem()
    {
        return(item);
    }

    //---------------------------------------------------------------------
    /**
       Sets line item. <P>
       @param value line item
    **/
    //---------------------------------------------------------------------
    public void setPLUItem(PLUItemIfc value)
    {
        item = value;
    }

    //---------------------------------------------------------------------
    /**
       Retrieves a letterName. <P>
       @return line item
    **/
    //---------------------------------------------------------------------
    public String getSelectedLetter()
    {
        return(letter);
    }

    //---------------------------------------------------------------------
    /**
       Sets a letterName. <P>
       @param value line item
    **/
    //---------------------------------------------------------------------
    public void setSelectedLetter(String letterName)
    {
        letter = letterName;
    }

    //---------------------------------------------------------------------
    /**
        Returns customer. <P>
        @return customer
    **/
    //---------------------------------------------------------------------
    public CustomerIfc getCustomer()
    {
        return(customer);
    }

    //---------------------------------------------------------------------
    /**
        Sets customer. <P>
        @param value    the customer
    **/
    //---------------------------------------------------------------------
    public void setCustomer(CustomerIfc value)
    {
        customer = value;
    }

    //----------------------------------------------------------------------
    /**
       Returns the sales associate. <P>
       @return The sales associate
    **/
    //----------------------------------------------------------------------
    public EmployeeIfc getEmployee()
    {
        return employee;
    }

    //----------------------------------------------------------------------
    /**
       Sets the sales associate. <P>
       @param employee The sales associate
    **/
    //----------------------------------------------------------------------
    public void setEmployee(EmployeeIfc employee)
    {
        this.employee = employee;
    }

    //----------------------------------------------------------------------
    /**
       Returns the employee ID
       @return String representing employee ID
    **/
    //----------------------------------------------------------------------
    public String getEmployeeID()
    {
        return employeeID;
    }

    //----------------------------------------------------------------------
    /**
      Sets the employee ID
      @param employeeID String representing employee ID
    **/
    //----------------------------------------------------------------------
    public void setEmployeeID(String employeeID)
    {
        this.employeeID = employeeID;
    }

    //---------------------------------------------------------------------
    /**
       Sets transaction.
       <P>
       @param  transaction the retail transaction
    **/
    //---------------------------------------------------------------------
    public void setTransaction(RetailTransactionIfc transaction)
    {
        this.transaction = transaction;
    }

    //---------------------------------------------------------------------
    /**
       Gets the  transaction.
       <P>
       @param  transaction the retail transaction
    **/
    //---------------------------------------------------------------------
    public RetailTransactionIfc getTransaction()
    {
        return(transaction);
    }


    //---------------------------------------------------------------------
    /**
       Retrieves line-item index. <P>
       @return line-item index
    **/
    //---------------------------------------------------------------------
    public int getIndex()
    {
        return(index);
    }

    //---------------------------------------------------------------------
    /**
       Sets line-item index. <P>
       @param value line-item index
    **/
    //---------------------------------------------------------------------
    public void setIndex(int value)
    {
        index = value;
    }

    //----------------------------------------------------------------------
    /**
       Returns the priceEntered flag
       @return boolean flag representing if price has been entered.
    **/
    //----------------------------------------------------------------------
    public boolean isPriceEntered()
    {
        return priceEntered;
    }

    //----------------------------------------------------------------------
    /**
      Sets the priceEntered flag
      @param boolean value for the flag
    **/
    //----------------------------------------------------------------------
    public void setPriceEntered(boolean value)
    {
        priceEntered = value;
    }

    //----------------------------------------------------------------------
    /**
       Returns the customerLinkRefreshUI flag
       @return boolean flag representing if UI needs to be refreshed.
       Specifically, the status bar needs to be updated with linked customer name.
    **/
    //----------------------------------------------------------------------
    public boolean isCustomerLinkRefreshUI()
    {
        return customerLinkRefreshUI;
    }

    //----------------------------------------------------------------------
    /**
      Sets the customerLinkRefreshUI flag
      @param boolean value for the flag
    **/
    //----------------------------------------------------------------------
    public void setCustomerLinkRefreshUI(boolean value)
    {
        customerLinkRefreshUI = value;
    }

    //----------------------------------------------------------------------
    /**
       Returns the newPLUItem flag
       @return boolean flag indicating if the item is added or moddified.
           Returns true when the item is new and false if the item is being modified.
    **/
    //----------------------------------------------------------------------
    public boolean getNewPLUItem()
    {
        return newPLUItem;
    }

    //----------------------------------------------------------------------
    /**
       Returns the new PLU item flag
       @return boolean flag indicating if the item is added or moddified.
           Returns true when the item is new and false if the item is being modified.
   **/
   //----------------------------------------------------------------------
    public boolean isNewPLUItem()
    {
        return newPLUItem;
    }

    //----------------------------------------------------------------------
    /**
        Sets the newPLUItem flag
        @param boolean value for the flag. A value of true indicates that the
            PLUItem is new rather than modified.
    **/
    //----------------------------------------------------------------------
    public void setNewPLUItem(boolean value)
    {
        newPLUItem = value;
    }

    //----------------------------------------------------------------------
    /**
       Returns the addPLUItem flag
       @return boolean flag telling modify item if the item needs to be
           added to the transaction. Returns true when the item is new
           and false if the item is being modified.
    **/
    //----------------------------------------------------------------------
    public boolean getAddPLUItem()
    {
        return addPLUItem;
    }

    //----------------------------------------------------------------------
    /**
       Returns the addPLUItem flag
       @return boolean flag telling modify item if the item needs to be
           added to the transaction. Returns true when the item is new
           and false if the item is being modified.
   **/
   //----------------------------------------------------------------------
    public boolean isAddPLUItem()
    {
        return addPLUItem;
    }

    //----------------------------------------------------------------------
    /**
        Sets the addPLUItem flag
        @param boolean value for the flag. A value of true indicates that a
            new PLUItem has successfully been created.
    **/
    //----------------------------------------------------------------------
    public void setAddPLUItem(boolean value)
    {
        addPLUItem = value;
    }

    //----------------------------------------------------------------------
    /**
       Returns the modifyItemService flag
       @return boolean flag indicating whether the alteration item is for the
           modify item service.
    **/
    //----------------------------------------------------------------------
    public boolean getModifyItemService()
    {
        return modifyItemService;
    }

    //----------------------------------------------------------------------
    /**
       Returns the modifyItemService flag
       @return boolean flag indicating whether the alteration item is for the
           modify item service.
    **/
    //----------------------------------------------------------------------
    public boolean isModifyItemService()
    {
        return modifyItemService;
    }

    //----------------------------------------------------------------------
    /**
        Sets the modifyItemService flag
        @param boolean value for the flag. A value of true indicates that
            this alteration item is for the modify item service
    **/
    //----------------------------------------------------------------------
    public void setModifyItemService(boolean value)
    {
        modifyItemService = value;
    }

    ///////////////  TransactionCargoIfc methods  //////////////////////////
    //----------------------------------------------------------------------
    /**
        Returns whether the transaction was created.
        <p>
        @return whether the transaction was created.
    **/
    //----------------------------------------------------------------------
    public boolean getTransactionCreated()
    {
        return transactionCreated;
    }

    //----------------------------------------------------------------------
    /**
        Sets whether the transaction was created.
        <p>
        @param  value   true if the transaction was created, false otherwise
    **/
    //----------------------------------------------------------------------
    public void setTransactionCreated(boolean value)
    {
        transactionCreated = value;
    }

    //----------------------------------------------------------------------
    /**
       Sets whether the transaction was created.
       <p>
       @param  value   true if the transaction was created, false otherwise
    **/
    //----------------------------------------------------------------------
    public void setCreateTransaction(boolean value)
    {
        createTransaction = value;
    }

    //----------------------------------------------------------------------
    /**
        Returns whether the transaction should be created, if needed.
        <p>
        @return whether the transaction should be created.
    **/
    //----------------------------------------------------------------------
    public boolean createTransaction()
    {
        return createTransaction;
    }

    //---------------------------------------------------------------------
    /**
        Sets the sales associate. <P>
        @param value  the sales associate
    **/
    //---------------------------------------------------------------------
    public void setSalesAssociate(EmployeeIfc value)
    {
        if (getTransaction() != null)
        {
            transaction.setSalesAssociate(value);
        }
    }

    //---------------------------------------------------------------------
    /**
        Returns the sales associate. <P>
        @return the sales associate
    **/
    //---------------------------------------------------------------------
    public EmployeeIfc getSalesAssociate()
    {
        if (getTransaction() != null)
        {
            return(getTransaction().getSalesAssociate());

        }
        return null;
    }

    //----------------------------------------------------------------------
    /**
     * @return the skip entre price flag
     */
    //----------------------------------------------------------------------
    public boolean getSkipPriceEntryFlag()
    {
        return skipPriceEntryFlag;
    }

    //----------------------------------------------------------------------
    /**
     * Sets the skip enter price flag
     * @param skipEnterPriceFlag the skip enter price flag
     */
    //----------------------------------------------------------------------
    public void setSkipPriceEntryFlag(boolean skipPriceEntryFlag)
    {
        this.skipPriceEntryFlag = skipPriceEntryFlag;
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object. <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  AlterationsCargo (Revision " +
                                      getRevisionNumber() +
                                      ") @" + hashCode());
        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}                                       // end class AlterationsCargo
