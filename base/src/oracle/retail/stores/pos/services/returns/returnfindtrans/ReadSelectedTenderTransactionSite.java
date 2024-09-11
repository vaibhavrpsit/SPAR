/* ===========================================================================
* Copyright (c) 2003, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnfindtrans/ReadSelectedTenderTransactionSite.java /main/18 2014/06/03 17:06:11 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     11/10/14 - limit the number of Customer Gift Lists retrieved by ICE.
 *    asinton   06/03/14 - added extended customer data locale to the
 *                         CustomerSearchCriteriaIfc.
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    acadar    08/03/12 - moved customer search criteria
 *    acadar    06/26/12 - Cross Channel changes
 *    acadar    05/29/12 - changes for cross channel
 *    acadar    05/23/12 - CustomerManager refactoring
 *    asinton   03/19/12 - Retrieve customer data for retrieved transaction
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         11/9/2006 6:55:26 PM   Jack G. Swan
 *         Initial XMl Replication check-in.
 *    3    360Commerce 1.2         3/31/2005 4:29:33 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:31 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:33 PM  Robert Pearse
 *
 *   Revision 1.12  2004/08/23 21:04:12  jriggins
 *   @scr 6652 Added a business date reference to the readTransactionsByID() call to avoid collisions when all other transaction ID elements match
 *
 *   Revision 1.11  2004/06/03 14:47:42  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.10  2004/04/20 13:17:05  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.9  2004/04/14 20:50:01  tfritz
 *   @scr 4367 - Renamed moveTransactionToOrigninal() method to moveTransactionToOriginal() method and added a call to setOriginalTransactionId() in this method.
 *
 *   Revision 1.8  2004/04/14 15:17:09  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.7  2004/03/03 23:15:09  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.6  2004/02/23 14:58:52  baa
 *   @scr 0 cleanup javadocs
 *
 *   Revision 1.5  2004/02/17 20:40:28  baa
 *   @scr 3561 returns
 *
 *   Revision 1.4  2004/02/13 22:46:22  baa
 *   @scr 3561 Returns - capture tender options on original trans.
 *
 *   Revision 1.3  2004/02/12 16:51:48  mcs
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
 *    Rev 1.1   Dec 30 2003 16:58:06   baa
 * cleanup for return feature
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnfindtrans;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.TransactionReadDataTransaction;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnUtilities;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteria;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc.SearchType;

/**
 * Reads the select return transaction from the database repository.
 */
@SuppressWarnings("serial")
public class ReadSelectedTenderTransactionSite extends PosSiteActionAdapter
{
    /**
     * Reads transactions from the database based on the specified/selected transaction.
     * @param bus  provides the cargo & managers to lookup the selected return transaction
     */
    public void arrive(BusIfc bus)
    {
    	//get utility manager
    	UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        ReturnFindTransCargo cargo = (ReturnFindTransCargo) bus.getCargo();

        // Lookup the transaction from the ID stored in the cargo.
        TransactionSummaryIfc summary = cargo.getSelectedTransactionSummary();
        String letterName = CommonLetterIfc.SUCCESS;

        try
        {
            boolean trainingMode = cargo.getRegister().getWorkstation().isTrainingMode();
            String transactionID = summary.getTransactionID().getTransactionIDString();
            EYSDate businessDate = summary.getBusinessDate();

            TransactionReadDataTransaction dt = null;

            dt = (TransactionReadDataTransaction) DataTransactionFactory.create(DataTransactionKeys.READ_TRANSACTIONS_FOR_RETURN);

            TransactionIfc[] transactions = dt.readTransactionsByID(transactionID, businessDate, trainingMode, utility.getRequestLocales());
            if(transactions[0] instanceof TenderableTransactionIfc &&
                    StringUtils.isNotBlank(((TenderableTransactionIfc)transactions[0]).getCustomerId()))
            {
                TenderableTransactionIfc tenderableTransaction = (TenderableTransactionIfc)transactions[0];
                try
                {
                    CustomerManagerIfc customerManager = (CustomerManagerIfc)bus.getManager(CustomerManagerIfc.TYPE);

                    CustomerSearchCriteriaIfc criteria = new CustomerSearchCriteria(SearchType.SEARCH_BY_CUSTOMER_ID, tenderableTransaction.getCustomerId(), utility.getRequestLocales());
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

                    //search for customer
                    CustomerIfc customer = customerManager.getCustomer(criteria);
                    tenderableTransaction.setCustomer(customer);
                }
                catch (DataException de)
                {
                    logger.warn("Could not retrieve customer: " + tenderableTransaction.getCustomerId(), de);
                }
            }
            cargo.moveTransactionToOriginal((SaleReturnTransactionIfc) transactions[0]);
            
            // If the transaction is an order, save the OrderID in the cargo and mail the
            // "TransactionHasOrder" letter.  The causes the order to be read.
            if (transactions[0].getTransactionType() == TransactionIfc.TYPE_ORDER_CANCEL ||
                transactions[0].getTransactionType() == TransactionIfc.TYPE_ORDER_COMPLETE || 
                transactions[0].getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE ||
                transactions[0].getTransactionType() == TransactionIfc.TYPE_ORDER_PARTIAL)
            {
                cargo.setSelectedTransactionOrderID(((OrderTransactionIfc)transactions[0]).getOrderID());
                letterName = ReturnUtilities.TRANSACTION_HAS_ORDER;
            }

        }
        catch (DataException e)
        {
            logger.error( "Can't find Transaction for ID=" + summary.getTransactionID() + "");

            logger.error( "" + e + "");
            cargo.setDataExceptionErrorCode(e.getErrorCode());

            letterName = CommonLetterIfc.DB_ERROR;
        }

        bus.mail(letterName);
    }


}
