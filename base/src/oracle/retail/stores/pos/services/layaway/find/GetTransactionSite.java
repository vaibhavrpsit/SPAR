/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/find/GetTransactionSite.java /main/19 2014/06/03 17:06:11 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/17/14 - limit the number of Customer Gift Lists retrieved by ICE.
 *    asinton   06/03/14 - added extended customer data locale to the
 *                         CustomerSearchCriteriaIfc.
 *    icole     06/19/13 - Add customer to layaway if the original customer has
 *                         been deleted.
 *    acadar    08/03/12 - moved customer search criteria
 *    acadar    06/26/12 - Cross Channel changes
 *    acadar    05/29/12 - changes for cross channel
 *    acadar    05/23/12 - CustomerManager refactoring
 *    asinton   03/19/12 - Retrieve customer data for retrieved transaction
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         11/9/2006 6:53:42 PM   Jack G. Swan
 *         Initial XMl Replication check-in.
 *    5    360Commerce 1.4         1/25/2006 4:11:01 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:42:42 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:28:15 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:51 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:12 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     9/21/2005 17:53:05     Rohit Sachdeva
 *         Layaway: Offline Payment screen is appearing after the database goes
 *         back online
 *    3    360Commerce1.2         3/31/2005 15:28:15     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:21:51     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:12     Robert Pearse
 *
 *Log:
 *    6    360Commerce 1.5         11/9/2006 6:53:42 PM   Jack G. Swan
 *         Initial XMl Replication check-in.
 *    5    360Commerce 1.4         1/25/2006 4:11:01 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:42:42 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:28:15 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:51 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:12 PM  Robert Pearse
 *: GetTransactionSite.java,v $
 *Log:
 *    6    360Commerce 1.5         11/9/2006 6:53:42 PM   Jack G. Swan
 *         Initial XMl Replication check-in.
 *    5    360Commerce 1.4         1/25/2006 4:11:01 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:42:42 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:28:15 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:51 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:12 PM  Robert Pearse
 *:
 *    5    .v710     1.2.2.1     10/25/2005 18:06:30    Charles Suehs   merged
 *         from v700
 *    4    .v710     1.2.2.0     10/25/2005 18:01:55    Charles Suehs   Merge
 *         from GetTransactionSite.java, Revision 1.2.1.0
 *    3    360Commerce1.2         3/31/2005 15:28:15     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:21:51     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:12     Robert Pearse
 *
 *   Revision 1.10  2004/09/30 18:08:46  cdb
 *   @scr 7248 Cleaned up inventory location and state in LayawayTransaction object.
 *
 *   Revision 1.9  2004/09/27 22:32:03  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.8  2004/06/03 14:47:44  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.7  2004/04/20 13:17:06  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.6  2004/04/14 15:17:11  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.5  2004/04/13 15:27:44  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/03/03 23:15:11  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:49  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:00:40   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:20:38   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:35:10   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:21:30   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:36   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.find;

// foundation imports
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.StringUtils;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.LayawayDataTransaction;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.financial.LayawaySummaryEntryIfc;
import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteria;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc.SearchType;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Site that retrieves the LayawayTransaction corresponding to the selected
 * layaway summary entry in the layaway display list.
 */
@SuppressWarnings("serial")
public class GetTransactionSite extends PosSiteActionAdapter
{
    /**
     * Retrieves the LayawayTransaction corresponding to the selected
     * layaway summary entry in the layaway display list.
     * @param  bus     Service Bus
     */
    public void arrive(BusIfc bus)
    {
    	// get the utility manager
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        // get the cargo for the service
        FindLayawayCargoIfc cargo = (FindLayawayCargoIfc)bus.getCargo();
        Letter result = new Letter (CommonLetterIfc.FAILURE); // default value

        // get the selected layaway summary entry from the cargo
        int selection = cargo.getSelectedLayawayIndex();
        //resetting
        cargo.setDataExceptionErrorCode(DataException.NONE);
        LayawaySummaryEntryIfc[] summaries = cargo.getLayawaySummaryEntryList();
        if (selection >= 0 && selection < summaries.length)
        {
            try
            {
                boolean trainingMode = ((AbstractFinancialCargo)cargo).getRegister().getWorkstation().isTrainingMode();

                // Read the layaway transaction using the regular sale return
                // transaction method
                LayawayDataTransaction trdt = null;

                trdt = (LayawayDataTransaction) DataTransactionFactory.create(DataTransactionKeys.LAYAWAY_DATA_TRANSACTION);

                TransactionIfc transaction
                    = instantiateTransaction(summaries[selection], trainingMode, utility.getRequestLocales());
                transaction
                    = trdt.readTransaction(transaction);

                // Retrieve the linked customer
                // This is somewhat hokey as the location of the correct customer ID varies depending upon
                // the path taken to get here.  
                if(transaction instanceof TenderableTransactionIfc && StringUtils.isNotBlank(((TenderableTransactionIfc)transaction).getCustomerId()))
                {
                    TenderableTransactionIfc tenderableTransaction = (TenderableTransactionIfc)transaction;
                    try
                    {
                        String customerId = null;
                        if(cargo.getLayaway() != null && cargo.getLayaway().getCustomer() != null)
                        {
                            customerId = cargo.getLayaway().getCustomer().getCustomerID();
                        }
                        else if(cargo.getCustomer() != null)
                        {
                            customerId = cargo.getCustomer().getCustomerID();
                        }
                        else
                        {
                            customerId = tenderableTransaction.getCustomerId();
                        }
                        CustomerManagerIfc customerManager = (CustomerManagerIfc)bus.getManager(CustomerManagerIfc.TYPE);
                        //create a customer search criteria
                        CustomerSearchCriteriaIfc searchCustomer = new CustomerSearchCriteria(SearchType.SEARCH_BY_CUSTOMER_ID, customerId, utility.getRequestLocales());
                        
                        Locale extendedDataRequestLocale = null;
                        if(cargo instanceof AbstractFinancialCargo && ((AbstractFinancialCargo)cargo).getOperator() != null)
                        {
                            extendedDataRequestLocale = ((AbstractFinancialCargo)cargo).getOperator().getPreferredLocale();
                        }
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
                        tenderableTransaction.setCustomer(customer);
                    }
                    catch(DataException ce)
                    {
                        logger.warn("Could not retrieve the linked customer: " + tenderableTransaction.getCustomerId());
                    }
                }

                if (logger.isDebugEnabled())
                {
                    logger.debug("Found transaction \n" + transaction);
                }

                // save the layaway transaction
                cargo.setInitialLayawayTransaction((LayawayTransactionIfc)transaction);

                // Temporary stop gap. LayawayTransaction should contain
                // the layaway object.
                if (cargo.getLayaway() == null ||
                    cargo.getLayaway().getCustomer() == null ||
                    Util.isEmpty(cargo.getLayaway().getCustomer().getLastName()))
                {
                    logger.error("GetTransactionSite.arrive: Customer information has not been populated in Layway object of LayawayTransaction");
                }
                else
                {
                   result = new Letter(CommonLetterIfc.SUCCESS);
                }

            }
            catch (DataException de)
            {
                // Save the error code if there's a data exception
                cargo.setDataExceptionErrorCode(de.getErrorCode());
            }
            catch (ClassCastException cce)
            {
                logger.error(
                    "GetTransactionSite.arrive");
            }
        }
        bus.mail(result, BusIfc.CURRENT);
    }

    /**
     * Instantiates an object implementing the TransactionIfc interface,\
     * using the initial transaction, and initial business day, of a layaway
     * summary entry. And the training mode
     * @return object implementing TransactionIfc
     */
    protected TransactionIfc instantiateTransaction(
        LayawaySummaryEntryIfc summary, boolean trainingMode, LocaleRequestor localeReq)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Building transaction using " + summary);
        }
        TransactionIfc transaction
            = DomainGateway.getFactory().getTransactionInstance();
        transaction.initialize(summary.getInitialTransactionID());
        transaction.setBusinessDay(summary.getInitialTransactionBusinessDate());
        transaction.setTransactionSequenceNumber(
                    summary.getInitialTransactionID().getSequenceNumber());
        transaction.setTrainingMode(trainingMode);
        transaction.setLocaleRequestor(localeReq);

        if (logger.isDebugEnabled())
        {
            StringBuilder debug = new StringBuilder("Built transaction had id: ");
            debug.append(transaction.getTransactionIdentifier());
            debug.append("\n  Workstation: ");
            debug.append(transaction.getWorkstation());
            debug.append("\n and business day: ");
            debug.append(transaction.getBusinessDay());
            logger.debug(debug.toString());
        }

        return(transaction);
    }

    /**
     * Instantiates an object implementing the LayawayIfc interface,
     * using the initial layaway summary entry to populate the layaway ID
     * and training mode.
     * @return object implementing LayawayIfc
     */
    protected LayawayIfc instantiateLayaway(LayawaySummaryEntryIfc summary, boolean trainingMode)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Building layaway using " + summary);
        }
        LayawayIfc layaway
            = DomainGateway.getFactory().getLayawayInstance();
        layaway.setLayawayID(summary.getLayawayID());
        layaway.setTrainingMode(trainingMode);

        if (logger.isDebugEnabled())
        {
            logger.debug("Built layway was: " + layaway.getLayawayID());
        }

        return(layaway);
    }
}
