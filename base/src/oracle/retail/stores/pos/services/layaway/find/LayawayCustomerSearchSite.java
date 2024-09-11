/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/find/LayawayCustomerSearchSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:13 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    sbeesnal  02/02/10 - If no Layaway found with the entered customer, set
 *                         seedLayawayTransaction to NULL.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:49 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:02 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:16 PM  Robert Pearse
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
 *    Rev 1.0   Aug 29 2003 16:00:42   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:20:44   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:35:14   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:21:20   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:34   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.find;

// foundation imports
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.LayawayDataTransaction;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.financial.LayawaySummaryEntryIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**
     Retrieves LayawaySummaries(s) from database by Customer. Stores these
     LayawaySummaries in the LayawayCargo.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class LayawayCustomerSearchSite extends PosSiteActionAdapter
{
    /**
        revision number supplied by source-code-control system
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    private static final boolean debug        = false;

    /**
        class name constant
    **/
    public static final String SITENAME = "LayawayCustomerSearchSite";

    //--------------------------------------------------------------------------
    /**
            Retrieves LayawaySummaries(s) from database by Customer.
            Stores these LayawaySummaries in the LayawayCargo.
            Displays the info not found screen when no layaways are returned.
            <P>
            @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        Letter result = new Letter (CommonLetterIfc.FAILURE); // default value
        boolean mailLetter = true;

        // Grab the cargo and find the customer
    	UtilityManagerIfc   utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        FindLayawayCargoIfc cargo = (FindLayawayCargoIfc)bus.getCargo();
        CustomerIfc customer = cargo.getCustomer();

        // If the customer is null, the NOT_FOUND letter will be sent, to
        // distinguish it from an error retrieving the layaway
        if (customer == null)
        {
            result = new Letter (CommonLetterIfc.NOT_FOUND);
        }
        else
        {
            try
            {
                cargo.setLayawaySearch(FindLayawayCargoIfc.LAYAWAY_SEARCH_BY_CUSTOMER);

                boolean trainingMode = ((AbstractFinancialCargo)cargo).getRegister().getWorkstation().isTrainingMode();
                LayawayIfc layaway = instantiateLayaway(customer, trainingMode, utility.getRequestLocales());

                // Create the data transaction and get the layaways
                LayawayDataTransaction layawayTransaction = null;

                layawayTransaction = (LayawayDataTransaction) DataTransactionFactory.create(DataTransactionKeys.LAYAWAY_DATA_TRANSACTION);

                LayawaySummaryEntryIfc[] layaways
                    = layawayTransaction.readLayawaysByCustomerID(
                          layaway);

                if (debug)
                {
                    for (int x = 0; x < layaways.length; x++)
                    {
                        System.out.println("############### " + layaways[x]);
                    }
                }

                // Store the retrieved layaway summaries in the cargo
                // and send the Success letter
                cargo.setLayawaySummaryEntryList(layaways);
                result = new Letter(CommonLetterIfc.SUCCESS);
            }
            catch (DataException de)
            {
                if (debug)
                {
                    de.printStackTrace();
                }
                if (de.getErrorCode() == DataException.NO_DATA) // layaway not found
                {
                    mailLetter = false;
                    cargo.setSeedLayawayTransaction(null);
                    displayNoMatch(bus);
                }
                else
                {
                    // Save the error code if there's a data exception
                    cargo.setDataExceptionErrorCode(de.getErrorCode());
                }
            }
        }

        // Send the resulting letter
        if (mailLetter)
        {
            bus.mail(result, BusIfc.CURRENT);
        }
    }

    //---------------------------------------------------------------------
    /**
       Instantiates an object implementing the LayawayIfc interface. <P>
       @return object implementing LayawayIfc
    **/
    //---------------------------------------------------------------------
    static protected LayawayIfc instantiateLayaway(CustomerIfc customer, boolean trainingMode, LocaleRequestor localeRequestor)
    {
        LayawayIfc layaway = DomainGateway.getFactory().getLayawayInstance();
        layaway.setCustomer(customer);
        layaway.setTrainingMode(trainingMode);
        layaway.setLocaleRequestor(localeRequestor);
        return(layaway);
    }
    //---------------------------------------------------------------------
    /**
       Displays the information not found error dialog screen. <P>
       @param   the bus
    **/
    //---------------------------------------------------------------------
    protected void displayNoMatch(BusIfc bus)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("INFO_NOT_FOUND_ERROR");
        dialogModel.setType(DialogScreensIfc.ERROR);
        // show the screen
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

}
