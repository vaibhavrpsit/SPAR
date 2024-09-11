/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/history/DisplayCustomerHistoryDetailSite.java /main/21 2014/06/03 17:06:11 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/17/14 - limit the number of Customer Gift Lists retrieved by ICE.
 *    asinton   06/03/14 - added extended customer data locale to the
 *                         CustomerSearchCriteriaIfc.
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    acadar    08/03/12 - moved customer search criteria
 *    acadar    06/26/12 - Cross Channel changes
 *    acadar    05/29/12 - changes for cross channel
 *    acadar    05/23/12 - CustomerManager refactoring
 *    asinton   03/19/12 - Retrieve customer data for retrieved transaction
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    vtemker   07/14/11 - Removed commented out old code
 *    vtemker   07/14/11 - Display only linked customer details (12686871)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    mchellap  04/17/09 - Changes to retrieve transaction using enterprise
 *                         technician for CTR
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:47 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:02 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:38 PM  Robert Pearse
 *
 *   Revision 1.9  2004/07/22 00:06:33  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.8  2004/06/03 14:47:43  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.7  2004/04/20 13:11:00  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.6  2004/04/14 20:50:01  tfritz
 *   @scr 4367 - Renamed moveTransactionToOrigninal() method to moveTransactionToOriginal() method and added a call to setOriginalTransactionId() in this method.
 *
 *   Revision 1.5  2004/04/14 15:17:10  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/03/03 23:15:11  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:31  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:44:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:55:46   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Apr 09 2003 14:03:16   baa
 * data base conversion / plaf cleanup
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.3   Apr 08 2003 10:07:06   bwf
 * Deprecation Fixes
 * Resolution for 2103: Remove uses of deprecated items in POS.
 *
 *    Rev 1.2   Mar 03 2003 16:27:38   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Sep 25 2002 10:15:06   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:32:40   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:12:32   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:25:16   msg
 * Initial revision.
 *
 *    Rev 1.4   08 Feb 2002 13:49:58   pjf
 * Don't display kit headers on customer history detail.
 * Resolution for POS SCR-1201: Kit info displayed incorrectly on History Detail screen
 *
 *    Rev 1.3   19 Nov 2001 16:16:30   baa
 * customer & inquiry options cleanup
 * Resolution for POS SCR-293: F11enabled on Cust History/History Detail, choosing F11 hangs app
 *
 *    Rev 1.2   05 Nov 2001 17:36:52   baa
 * Code Review changes. Customer, Customer history Inquiry Options
 * Resolution for POS SCR-244: Code Review  changes
 *
 *    Rev 1.1   30 Oct 2001 16:10:44   baa
 * customer history. Enable training mode
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.0   19 Oct 2001 15:50:20   msg
 * Initial revision.
 * Resolution for 209: Customer History
 *
 *    Rev 1.0   19 Oct 2001 15:27:34   baa
 * Initial revision.
 * Resolution for POS SCR-209: Customer History
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.history;



import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.ReadTransactionsForReturn;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
import oracle.retail.stores.domain.stock.ItemKitConstantsIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteria;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc.SearchType;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.SiteActionAdapter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.TagConstantsIfc;
import oracle.retail.stores.pos.services.returns.returncustomer.ReturnCustomerCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;
import oracle.retail.stores.pos.ui.beans.TotalsBeanModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

//------------------------------------------------------------------------------
/**

    $Revision: /main/21 $
**/
//------------------------------------------------------------------------------

public class DisplayCustomerHistoryDetailSite extends SiteActionAdapter
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -4646914783378069931L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.customer.history.DisplayCustomerHistoryDetailSite.class);

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/21 $";

    //--------------------------------------------------------------------------
    /**


            @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
    	// get utility manager
    	UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        // Create the model and set the data
        ReturnCustomerCargo cargo = (ReturnCustomerCargo) bus.getCargo();


        /*
         * Lookup the transaction from the ID stored in the cargo.
         */
        TransactionSummaryIfc[] summaries = cargo.getTransactionSummary();
        TransactionSummaryIfc summary = summaries[cargo.getSelectedIndex()];
        EYSDate startEndDate = summary.getBusinessDate();

        try
        {
            String transactionID = summary.getTransactionID().getTransactionIDString();
            boolean trainingMode = cargo.getRegister().getWorkstation().isTrainingMode();

            ReadTransactionsForReturn dt = (ReadTransactionsForReturn) DataTransactionFactory.create(DataTransactionKeys.READ_TRANSACTIONS_FOR_RETURN);

            TransactionIfc[] transactions = dt.readTransactionsByID(transactionID,
                                                                    startEndDate,
                                                                    trainingMode,
                                                                    utility.getRequestLocales());
            if(transactions[0] instanceof TenderableTransactionIfc &&
                    StringUtils.isNotBlank(((TenderableTransactionIfc)transactions[0]).getCustomerId()))
            {
                TenderableTransactionIfc tenderableTransaction = (TenderableTransactionIfc)transactions[0];
                try
                {
                    CustomerManagerIfc customerManager = (CustomerManagerIfc)bus.getManager(CustomerManagerIfc.TYPE);
                    
                    CustomerSearchCriteriaIfc criteria = new CustomerSearchCriteria(SearchType.SEARCH_BY_CUSTOMER_ID,tenderableTransaction.getCustomerId(), utility.getRequestLocales());
                    Locale extendedDataRequestLocale = cargo.getOperator().getPreferredLocale();
                    if(extendedDataRequestLocale == null)
                    {
                        extendedDataRequestLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);
                    }
                    criteria.setExtendedDataRequestLocale(extendedDataRequestLocale);
                    int maxCustomerItemsPerListSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxCustomerItemsPerListSize", "10"));
                    criteria.setMaxCustomerItemsPerListSize(maxCustomerItemsPerListSize);
                    int maxTotalCustomerItemsSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxTotalCustomerItemsSize", "40"));
                    criteria.setMaxTotalCustomerItemsSize(maxTotalCustomerItemsSize);
                    int maxNumberCustomerGiftLists = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxNumberCustomerGiftLists", "4"));
                    criteria.setMaxNumberCustomerGiftLists(maxNumberCustomerGiftLists);

                    //search for Customer
                    CustomerIfc customer = customerManager.getCustomer(criteria);
                    tenderableTransaction.setCustomer(customer);
                }
                catch (DataException de)
                {
                    logger.warn("Could not retrieve customer: " + tenderableTransaction.getCustomerId(), de);
                }
            }

            cargo.moveTransactionToOriginal((SaleReturnTransactionIfc)transactions[0]);
            
            // Display the customer name and the screen
            String customerName = new String("");
            if (cargo.isLinkCustomer())
            {
                String[] vars = { cargo.getPreviousCustomer().getFirstName(), cargo.getPreviousCustomer().getLastName() };
                String pattern = utility.retrieveText("CustomerAddressSpec", BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                        TagConstantsIfc.CUSTOMER_NAME_TAG, TagConstantsIfc.CUSTOMER_NAME_PATTERN_TAG);
                customerName = LocaleUtilities.formatComplexMessage(pattern, vars);
            }
           
            StatusBeanModel statusModel = new StatusBeanModel();
            statusModel.setCustomerName(customerName);
            
            LineItemsModel model = new  LineItemsModel();
            SaleReturnTransactionIfc trans = cargo.getOriginalTransaction();
            
            //Set transaction id on response area
            PromptAndResponseModel responseModel = new PromptAndResponseModel();
            responseModel.setArguments(trans.getTransactionID());

            //Retrieve totals
            TotalsBeanModel  totalsModel = new TotalsBeanModel();
            totalsModel.setTotals(trans.getTransactionTotals());

            // Update model
            model.setTotalsBeanModel(totalsModel);
            model.setPromptAndResponseModel(responseModel);
            model.setLineItems(trans.getLineItemsExcluding(ItemKitConstantsIfc.ITEM_KIT_CODE_HEADER));
            
            //Set the StatusBeanModel
            model.setStatusBeanModel(statusModel);

            // Display the screen
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(POSUIManagerIfc.HISTORY_DETAIL, model);
        }
        catch (DataException e)
        {
           cargo.setDataExceptionErrorCode(e.getErrorCode());
           String args[] = new String[1];
           args[0] =
             utility.getErrorCodeString(e.getErrorCode());
           POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
           UIUtilities.setDialogModel(ui,DialogScreensIfc.ERROR,"DATABASE_ERROR", args,CommonLetterIfc.DONE);
        }
    }
}
