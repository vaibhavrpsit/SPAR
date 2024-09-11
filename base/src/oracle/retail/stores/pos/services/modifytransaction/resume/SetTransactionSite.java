/* ===========================================================================
* Copyright (c) 2004, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/resume/SetTransactionSite.java /main/18 2014/06/03 17:06:11 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/17/14 - limit the number of Customer Gift Lists retrieved by ICE.
 *    asinton   06/03/14 - added extended customer data locale to the
 *                         CustomerSearchCriteriaIfc.
 *    cgreene   05/14/14 - rename retrieve to resume
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    acadar    08/03/12 - moved customer search criteria
 *    acadar    06/26/12 - Cross Channel changes
 *    acadar    05/29/12 - changes for cross channel
 *    acadar    05/23/12 - CustomerManager refactoring
 *    asinton   03/15/12 - Retrieve customer from CustomerManager after
 *                         retrieving the transaction.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:57 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:15 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:12 PM  Robert Pearse
 *
 *   Revision 1.7  2004/06/03 14:47:43  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.6  2004/04/20 13:17:06  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.5  2004/04/14 15:17:10  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/02/24 16:21:29  cdb
 *   @scr 0 Remove Deprecation warnings. Cleaned code.
 *
 *   Revision 1.3  2004/02/12 16:51:12  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:45  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Jan 20 2004 16:25:08   DCobb
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.resume;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.TransactionReadDataTransaction;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

import org.apache.commons.lang3.StringUtils;

import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteria;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc.SearchType;

/**
 * Sets the selected suspended transaction in the cargo.
 */
@SuppressWarnings("serial")
public class SetTransactionSite extends PosSiteActionAdapter
{                                       // begin class SetTransactionSite
    /**
     * site name constant
     */
    public static final String SITENAME = "SetTransactionSite";

    /**
     * Sets the selected suspended transaction in the cargo.
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        String letterName = CommonLetterIfc.CONTINUE;

        // get utility manager
        UtilityManagerIfc utility =
            (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);


        // get transaction ID from cargo
        ModifyTransactionResumeCargo cargo = (ModifyTransactionResumeCargo) bus.getCargo();
        TransactionSummaryIfc selected = cargo.getSelectedSummary();
        String transactionID = selected.getTransactionID().getTransactionIDString();

        // initialize the transaction
        TransactionIfc searchTransaction =
          DomainGateway.getFactory().getTransactionInstance();
        searchTransaction.initialize(transactionID);
        searchTransaction.setBusinessDay(selected.getBusinessDate());
        searchTransaction.setTransactionStatus(TransactionIfc.STATUS_SUSPENDED);
        searchTransaction.setLocaleRequestor(utility.getRequestLocales());

        //just to make sure operator can't cancel real trans. while being in training mode.
        boolean trainingModeOn = cargo.getRegister().getWorkstation().isTrainingMode();
        searchTransaction.setTrainingMode(trainingModeOn);

        // read transaction from database and set in the cargo
        try
        {
            TransactionReadDataTransaction readTransaction = null;

            readTransaction = (TransactionReadDataTransaction) DataTransactionFactory.create(DataTransactionKeys.TRANSACTION_READ_DATA_TRANSACTION);

            RetailTransactionIfc retrieveTransaction =
                (RetailTransactionIfc) readTransaction.readTransaction
                  (searchTransaction);
            if(StringUtils.isNotBlank(retrieveTransaction.getCustomerId()))
            {
                try
                {
                    CustomerManagerIfc customerManager = (CustomerManagerIfc)bus.getManager(CustomerManagerIfc.TYPE);
                    
                    //create a customer search criteria
                    CustomerSearchCriteriaIfc searchCustomer = new CustomerSearchCriteria(SearchType.SEARCH_BY_CUSTOMER_ID, retrieveTransaction.getCustomerId(), utility.getRequestLocales());
                    Locale extendedDataRequestLocale = cargo.getOperator().getPreferredLocale();
                    if(extendedDataRequestLocale == null)
                    {
                        extendedDataRequestLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);
                    }
                    searchCustomer.setExtendedDataRequestLocale(extendedDataRequestLocale);
                    int maxCustomerItemsPerListSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxCustomerItemsPerListSize", "10"));
                    searchCustomer.setMaxCustomerItemsPerListSize(maxCustomerItemsPerListSize);
                    int maxTotalCustomerItemsSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxTotalCustomerItemsSize", "40"));
                    searchCustomer.setMaxTotalCustomerItemsSize(maxTotalCustomerItemsSize);
                    int maxNumberCustomerGiftLists = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxNumberCustomerGiftLists", "4"));
                    searchCustomer.setMaxNumberCustomerGiftLists(maxNumberCustomerGiftLists);

                    //retrieve customer
                    CustomerIfc customer = customerManager.getCustomer(searchCustomer);
                    retrieveTransaction.setCustomer(customer);
                }
                catch (DataException de)
                {
                    logger.warn("Could not retrieve customer: " + retrieveTransaction.getCustomerId(), de);
                }
            }
            cargo.setTransaction(retrieveTransaction);
        }
        catch (DataException e)
        {
            cargo.setDataExceptionErrorCode(e.getErrorCode());
            letterName = CommonLetterIfc.DB_ERROR;
        }

        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }

}
