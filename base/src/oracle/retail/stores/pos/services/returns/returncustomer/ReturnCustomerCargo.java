/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncustomer/ReturnCustomerCargo.java /main/15 2011/12/05 12:16:21 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   08/18/11 - Refactor code to not unlock screen when setting
 *                         model to avoid unwanted letters.
 *    vtemker   07/14/11 - initialized String using String literals
 *    vtemker   07/14/11 - Added linked customer details, so can display them
 *                         in the status bar (Bug 12686871)
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/14/10 - ExternalOrder mods checkin for refresh to tip.
 *    jswan     05/12/10 - Modify cargos for external order items return.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:44 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:50 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:52 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/10 14:16:46  baa
 *   @scr 0 fix javadoc warnings
 *
 *   Revision 1.3  2004/02/12 16:51:47  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:05:56   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 16:32:34   jriggins
 * Replaced concat of customer first and last name to retrieval of CustomerAddressSpec.CustomerName from customerText bundle.
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:06:20   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:45:34   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:24:48   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:34   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncustomer;

import java.util.Locale;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.common.DBErrorCargoIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.returns.returncommon.AbstractFindTransactionCargo;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnData;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnExternalOrderItemsCargoIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnTransactionsCargoIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnableItemCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
 * Cargo for the Customer Return service.
 * 
 * @version $Revision: /main/15 $
 */
public class ReturnCustomerCargo extends AbstractFindTransactionCargo implements DBErrorCargoIfc,
        ReturnTransactionsCargoIfc, ReturnableItemCargoIfc, ReturnExternalOrderItemsCargoIfc
{
    /** serialVersionUID */
    private static final long serialVersionUID = 25681891024664566L;

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * CustomerIfc object
     */
    protected CustomerIfc customer = null;

    /**
     * Transaction Summary objects
     */
    protected TransactionSummaryIfc[] transactionSummary = null;

    /**
     * The error code generated by the attempt to access the database
     */
    protected int dataExceptionErrorCode = DataException.NONE;

    /**
     * this flag is set when the child should transfer its Cargo to the parent's
     * Cargo
     */
    protected boolean transferCargo = false;

    /**
     * The service was stared with a customer already in the cargo.
     */
    protected boolean startedWithCustomer = true;

    /**
     * Contains all data for return items. This data is collected in another
     * service; it is just being passed back to the calling service.
     */
    protected ReturnData returnData = null;

    /**
     * Selected transaction index
     */
    protected int selectedIndex = -1;

    /**
     * Search criteria
     */
    protected SearchCriteriaIfc searchCriteria = null;

    /**
     * Customer name bundle tag
     */
    protected static final String CUSTOMER_NAME_TAG = "CustomerName";

    /**
     * Customer name default text
     */
    protected static final String CUSTOMER_NAME_TEXT = "{0} {1}";

    /**
     * Flag to show if this customer has been linked
     */
    protected boolean linkCustomer = false;

    /**
     * previously linked customer
     */
    protected CustomerIfc previousCustomer = null;
    
    /**
     * Class Constructor. A new instance of <code>ReturnCustomerCargo</code>
     */
    public ReturnCustomerCargo()
    {
    }

    /**
     * Sets the transfer cargo flag.
     * 
     * @param value true if the cargo should be transfered
     */
    public void setTransferCargo(boolean value)
    {
        transferCargo = value;
    }

    /**
     * Returns the transfer cargo flag.
     * 
     * @return true if the cargo should be transfered
     */
    public boolean getTransferCargo()
    {
        return transferCargo;
    }

    /**
     * Sets the started with customer flag.
     * 
     * @param value true if the service began with a valid customer
     */
    public void setStartedWithCustomer(boolean value)
    {
        startedWithCustomer = value;
    }

    /**
     * Returns the started with customer flag.
     * 
     * @return true if the service began with a valid customer
     */
    public boolean getStartedWithCustomer()
    {
        return startedWithCustomer;
    }

    /**
     * Returns the customer.
     * 
     * @return customer
     */
    public CustomerIfc getCustomer()
    {
        return customer;
    }

    /**
     * Sets the customer.
     * 
     * @param value the customer
     */
    public void setCustomer(CustomerIfc value)
    {
        customer = value;
    }

    /**
     * Displays customer name on status bar.
     * 
     * @param bus the current bus
     */
    public void displayCustomer(BusIfc bus)
    {
        // Display linked customer if there is one
        String customerName = "";

        // set the customer's name in the status area
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        StatusBeanModel statusModel = new StatusBeanModel();
        // Create the string from the bundle.
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        // If link customer, fetch previously linked customer name
        if (linkCustomer)
        {
            Object parms[] = { previousCustomer.getFirstName(), previousCustomer.getLastName() };
            String pattern = utility.retrieveText("CustomerAddressSpec", BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                    CUSTOMER_NAME_TAG, CUSTOMER_NAME_TEXT);
            customerName = LocaleUtilities.formatComplexMessage(pattern, parms, locale);
        }
        // Else, fetch the current customers details
        else
        {
            Object parms[] = { customer.getFirstName(), customer.getLastName() };
            String pattern = utility.retrieveText("CustomerAddressSpec", BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                    CUSTOMER_NAME_TAG, CUSTOMER_NAME_TEXT);
            customerName = LocaleUtilities.formatComplexMessage(pattern, parms, locale);
        }

        statusModel.setCustomerName(customerName);
        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        baseModel.setStatusBeanModel(statusModel);
        ui.setModel(POSUIManagerIfc.SHOW_STATUS_ONLY, baseModel, false);

    }

    /**
     * Returns the transaction summary array.
     * 
     * @return The transaction summary array
     */
    public TransactionSummaryIfc[] getTransactionSummary()
    {
        return transactionSummary;
    }

    /**
     * Sets the transaction summary array.
     * 
     * @param transactions The transaction
     */
    public void setTransactionSummary(TransactionSummaryIfc[] transactions)
    {
        transactionSummary = transactions;
    }

    /**
     * Returns the error code returned with a DataException.
     * 
     * @return the error code
     */
    public int getDataExceptionErrorCode()
    {
        return dataExceptionErrorCode;
    }

    /**
     * Sets the error code returned with a DataException.
     * 
     * @param value the error code
     */
    public void setDataExceptionErrorCode(int value)
    {
        dataExceptionErrorCode = value;
    }

    /**
     * Returns the Returned Data object.
     * 
     * @return ReturnData
     */
    public ReturnData getReturnData()
    {
        return returnData;
    }

    /**
     * Sets the Returned Data object.
     * 
     * @param value
     */
    public void setReturnData(ReturnData value)
    {
        returnData = value;
    }

    /**
     * Returns the selected transaction index.
     * 
     * @return the integer value
     */
    public int getSelectedIndex()
    {
        return selectedIndex;
    }

    /**
     * Sets the selected transaction index.
     * 
     * @param value the integer value
     */
    public void setSelectedIndex(int value)
    {
        selectedIndex = value;
    }

    /**
     * Returns the search criteria.
     * 
     * @return searchCriteria as SearchCriteriaIfc
     */
    public SearchCriteriaIfc getSearchCriteria()
    {
        return searchCriteria;
    }

    /**
     * Sets the search criteria.
     * 
     * @param criteria as SearchCriteriaIfc
     */
    public void setSearchCriteria(SearchCriteriaIfc criteria)
    {
        searchCriteria = criteria;
    }
    

    public boolean isLinkCustomer()
    {
        return linkCustomer;
    }

    public void setLinkCustomer(boolean linkCustomer)
    {
        this.linkCustomer = linkCustomer;
    }
    
    public CustomerIfc getPreviousCustomer()
    {
        return previousCustomer;
    }

    public void setPreviousCustomer(CustomerIfc previousCustomer)
    {
        this.previousCustomer = previousCustomer;
    }

    /**
     * Returns a string representation of this object.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        String strResult = new String("Class:  getClass().getName() (Revision " +
                getRevisionNumber() + ")" + hashCode());
        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }


}
