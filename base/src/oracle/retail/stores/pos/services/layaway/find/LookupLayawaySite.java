/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/find/LookupLayawaySite.java /main/18 2014/06/03 17:06:11 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/17/14 - limit the number of Customer Gift Lists retrieved by ICE.
 *    icole		09/03/14 - added serial number collection when balance paid in full.
 *    asinton   06/03/14 - added extended customer data locale to the
 *                         CustomerSearchCriteriaIfc.
 *    icole     06/19/13 - Add customer to layaway if the original customer has
 *                         been deleted.
 *    mchellap  03/28/13 - Lookup layaway customer using customer manager
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mpbarnet  04/24/09 - Code reviewed by Brett Larsen.
 *    mpbarnet  04/24/09 - In arrive(), check customer object for null before
 *                         getting customerID.
 *    vchengeg  02/16/09 - Removed multiple occurrances of the string Link
 *                         Customer and retained only one for EJournalling
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         8/10/2006 11:17:00 AM  Brendan W. Farrell
 *         16500 -Merge fix from v7.x.  Maintain sales associate to be used in
 *          reporting.
 *    3    360Commerce 1.2         3/31/2005 4:28:58 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:21 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:28 PM  Robert Pearse
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
 *   Revision 1.6  2004/04/13 15:27:44  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.5  2004/04/08 20:33:02  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *   Revision 1.4  2004/03/03 23:15:11  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:52  mcs
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
 *    Rev 1.0   Aug 29 2003 16:00:46   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:20:54   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:35:22   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:21:32   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:32   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.find;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.LayawayDataTransaction;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.financial.LayawaySummaryEntryIfc;
import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteria;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc.SearchType;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * Retrieves a LayawaySummary based on a Layaway ID. This site sends the
 * following letters: Failure, Success, NotFound.
 * 
 * @version $Revision: /main/18 $
 */
@SuppressWarnings("serial")
public class LookupLayawaySite extends PosSiteActionAdapter
{
    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/18 $";

    /**
     * class name constant
     */
    public static final String SITENAME = "LookupLayawaySite";
    
    /**
     * 
     */
    public static final String NO_LINKED_CUSTOMER = "NoLinkedCustomer";

    /**
     * Retrieves a layaway summary based on a layaway ID. The layaway ID is
     * retrieved from the LayawayCargo using getLayawaySearchID(). The resulting
     * summary (LayawaySummaryEntryIfc) is then set in the cargo as an array of
     * 1 using setLayawaySummaryEntryList(LayawaySummaryEntryIfc[]). Save the
     * Layaway object generated by the data call as well.
     * 
     * @param bus the bus arriving at this site
     */
    public void arrive(BusIfc bus)
    {
        CustomerIfc customer = null;             // customer associated with the layaway
        String customerID;                // customer ID associated with the layaway
      
        FindLayawayCargoIfc cargo = (FindLayawayCargoIfc) bus.getCargo();

        Letter result = new Letter (CommonLetterIfc.FAILURE); // default value

        boolean mailLetter = true;

        try
        {
            cargo.setLayawaySearch(FindLayawayCargoIfc.LAYAWAY_SEARCH_BY_ID);

            boolean trainingMode =
              ((AbstractFinancialCargo) cargo).getRegister().
                getWorkstation().isTrainingMode();
            // Get the layaway id to search for from the cargo
            UtilityManagerIfc util = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            LayawayIfc layaway = instantiateLayaway(cargo.getLayawaySearchID(),
                                 trainingMode, util.getRequestLocales());

            // Search for the layaway
            LayawayDataTransaction ldt = null;

            ldt = (LayawayDataTransaction) DataTransactionFactory.create(DataTransactionKeys.LAYAWAY_DATA_TRANSACTION);

            layaway = ldt.readLayaway(layaway);
            if (layaway.getStatus() != LayawayConstantsIfc.STATUS_COMPLETED && layaway.getCustomer() != null)
            {
                try
                {
                    CustomerManagerIfc customerManager = (CustomerManagerIfc)bus.getManager(CustomerManagerIfc.TYPE);
                    CustomerSearchCriteriaIfc criteria = new CustomerSearchCriteria(SearchType.SEARCH_BY_CUSTOMER_ID,
                            layaway.getCustomer().getLocaleRequestor());
                    Locale extendedDataRequestLocale = null;
                    if(cargo instanceof AbstractFinancialCargo && ((AbstractFinancialCargo)cargo).getOperator() != null)
                    {
                        extendedDataRequestLocale = ((AbstractFinancialCargo)cargo).getOperator().getPreferredLocale();
                    }
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
                    criteria.setCustomerID(layaway.getCustomer().getCustomerID());
                    customer = customerManager.getCustomer(criteria);
                }
                catch(DataException de)
                {
                    mailLetter = false;
                    displayNoCustomer(bus);
                }
                if (customer == null)
                {
                    logger.warn("Customer " + layaway.getCustomer().getCustomerID() + " associated with layaway "
                            + layaway.getLayawayID() + " could not be found");
                }
                else
                {
                    layaway.setCustomer(customer);
                    result = new Letter(CommonLetterIfc.SUCCESS);
                }
            }
            else
            {
                // layaway was found but it's status is complete thus can't be processed
                mailLetter = false;
                displayNoMatch(bus);
            }

            // Convert the single result into an array of layaway summary entries,
            // with one entry
            LayawaySummaryEntryIfc[] summaries = createSummaryList(layaway);

            // Save the layaway summary entries and the layaway
            cargo.setLayawaySummaryEntryList(summaries);
            cargo.setLayaway(layaway);
            cargo.setCustomer(layaway.getCustomer());

            if (cargo.getSeedLayawayTransaction() == null)
            {
                // Create transaction; the initializeTransaction() method is called
                // on UtilityManager
                TransactionIfc transaction = DomainGateway.getFactory().getTransactionInstance();
                transaction.setCashier(((AbstractFinancialCargo)cargo).getOperator());
                if(((AbstractFinancialCargo)cargo).getSalesAssociate() != null)
                {
                    transaction.setSalesAssociate(((AbstractFinancialCargo)cargo).getSalesAssociate());
                }
                TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
                utility.initializeTransaction(transaction);
                cargo.setSeedLayawayTransaction(transaction);

                JournalManagerIfc mgr = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

                StringBuffer strResult = new StringBuffer();

                // Get the customer ID of the layaway customer
                customer = layaway.getCustomer();
                if (customer == null)
                {
                  customerID = "null";
                }
                else
                {
                  customerID = customer.getCustomerID();
                }
                
                Object[] dataArgs = new Object[2];
                dataArgs[0] = customerID;
                strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.LINK_CUSTOMER_LABEL, dataArgs));
                strResult.append(Util.EOL);

                mgr.journal(transaction.getCashier().getEmployeeID(),
                            transaction.getTransactionID(),
                            strResult.toString());
            }
            cargo.setSelectedLayawayIndex(0);
        }
        catch (DataException de)
        {
            if (de.getErrorCode() == DataException.NO_DATA) // layaway not found
            {
                mailLetter = false;
                displayNoMatch(bus);
            }
            else
            {
                // Save the error code if there's a data exception
                cargo.setDataExceptionErrorCode(de.getErrorCode());
            }
        }
        // Send the resulting letter
        if (mailLetter)
        {
            bus.mail(result, BusIfc.CURRENT);
        }
    }

    /**
     * Instantiates an object implementing the LayawayIfc interface.
     * 
     * @return object implementing LayawayIfc
     */
    protected LayawayIfc instantiateLayaway(String layawayID, boolean trainingMode, LocaleRequestor localeRequestor)
    {
        LayawayIfc layaway = DomainGateway.getFactory().getLayawayInstance();
        layaway.setLayawayID(layawayID);
        layaway.setTrainingMode(trainingMode);
        layaway.setLocaleRequestor(localeRequestor);
        return(layaway);
    }

    /**
     * Populates the layaway summary entry array with the corresponding layaway
     * data.
     * 
     * @return array of layaway summary entries
     */
    protected LayawaySummaryEntryIfc[] createSummaryList(LayawayIfc layaway)
    {
        // Convert the layawy into a layaway summary entry and add it
        // to an array to be displayed in the next site.
        LayawaySummaryEntryIfc[] summaries = new LayawaySummaryEntryIfc[1];
        summaries[0] = DomainGateway.getFactory().getLayawaySummaryEntryInstance();
        summaries[0].setLayawayID(layaway.getLayawayID());
        summaries[0].setLocalizedDescriptions(layaway.getLocalizedDescriptions());
        summaries[0].setStatus(layaway.getStatus());
        summaries[0].setExpirationDate(layaway.getExpirationDate());
        summaries[0].setBalanceDue(layaway.getBalanceDue());
        summaries[0].setInitialTransactionBusinessDate(layaway.getInitialTransactionBusinessDate());
        summaries[0].setInitialTransactionID(layaway.getInitialTransactionID());

        return summaries;
    }

    /**
     * Displays the information not found error dialog screen.
     * 
     * @param the bus
     */
    protected void displayNoMatch(BusIfc bus)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("INFO_NOT_FOUND_ERROR");
        dialogModel.setType(DialogScreensIfc.ERROR);
        // show the screen
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    /**
     * Displays the information customer not found dialog screen.
     * 
     * @param the bus
     */
    protected void displayNoCustomer(BusIfc bus)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(NO_LINKED_CUSTOMER);
        dialogModel.setType(DialogScreensIfc.YES_NO);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, CommonLetterIfc.CUSTOMER);
        // show the screen
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

}
