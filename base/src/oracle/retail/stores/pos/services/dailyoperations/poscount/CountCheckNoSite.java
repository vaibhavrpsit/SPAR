/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/CountCheckNoSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:23 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    npoola    03/29/10 - Changed the logic to pickup the checks from Till
 *                         instead of Tender line items table
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:52:33 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    3    360Commerce 1.2         3/31/2005 4:27:31 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:24 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:13 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/06/03 14:47:42  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.6  2004/04/20 13:13:09  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.5  2004/04/14 15:17:09  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/03/03 23:15:06  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:38  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:40  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.3   Jan 20 2004 17:08:06   kll
 * Exclusion of ECheck tenders from till counts - gap-specific functionality
 * Resolution for 3604: Pick-up Till Report does not distinguish between Checks and e-Checks and Total is incorrect
 * 
 *    Rev 1.2   Jan 16 2004 15:12:40   blj
 * add money order totals to check totals for till close.
 * 
 *    Rev 1.1   Jan 13 2004 13:49:16   blj
 * money rework
 * 
 *    Rev 1.0   Aug 29 2003 15:56:40   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jun 23 2003 13:44:06   DCobb
 * Canadian Check Till Pickup
 * Resolution for POS SCR-2484: Canadian Check Till Pickup
 * 
 *    Rev 1.0   Apr 29 2002 15:30:18   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:14:16   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:27:02   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 10 2002 18:00:08   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.0   Sep 21 2001 11:17:18   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:11:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

// foundation imports
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.tender.TenderTypeMap;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.TillChecksDataTransaction;
import oracle.retail.stores.domain.tender.TenderCheckIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Retrieve and count the pickup checks for this Till
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class CountCheckNoSite extends PosSiteActionAdapter
{
    static final long serialVersionUID = 2046771535148688773L;
    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * site name constant
     */
    public static final String SITENAME = "CountCheckNosite";

    /**
     * database offline message tag
     */
    protected static String DATABASE_OFFLINE_TAG = "DatabaseError.Offline";

    /**
     * database offline message
     */
    protected static String DATABASE_OFFLINE = "The database is offline.";

    /**
     * boolean hold value in case parameter approach engaged - this exists in
     * order to EXCLUDE ECHECK from till counts
     */
    protected static boolean checkFilter = true;

    /**
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // get transaction objects
        PosCountCargo cargo = (PosCountCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        Letter letter = new Letter(CommonLetterIfc.SUCCESS);

        TenderCheckIfc[] checks = null;

        TillChecksDataTransaction dataTrans = null;

        dataTrans = (TillChecksDataTransaction) DataTransactionFactory
                .create(DataTransactionKeys.TILL_CHECKS_DATA_TRANSACTION);
        try
        {
            String tillId = cargo.getTillID();
            String storeId = cargo.getRegister().getWorkstation().getStoreID();
            EYSDate businessDate = cargo.getRegister().getBusinessDate();
            String tenderName = TenderTypeMap.getTenderTypeMap().getDescriptor(TenderLineItemIfc.TENDER_TYPE_CHECK);

            // pass in a new parameter here - cargo.getTenderNationality()
            String tenderNationality = cargo.getTenderNationality();
            checks = dataTrans.getChecksForTill(tillId, storeId, businessDate, tenderName, tenderNationality,
                    checkFilter);

            // calculate the total amount of the retrieved checks
            // account for USD vs Alt Curr
            CurrencyIfc checkTotal = null;
            checkTotal = DomainGateway.getBaseCurrencyInstance();
            for (int i = 0; i < checks.length; i++)
            {
                checkTotal = checkTotal.add(checks[i].getAmountTender());
            }

            cargo.updateAcceptedCount();
            cargo.updateCountModel(checkTotal);
            cargo.updateCheckAmountsInTotals(checks);
        }
        catch (DataException e)
        {
            // revert to detail check pickup
            letter = null;
            showDataBaseOfflineDialog(ui, utility);
        }

        if (letter != null)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
    }

    /**
     * Displays the Database Offline Dialog.
     */
    protected void showDataBaseOfflineDialog(POSUIManagerIfc ui, UtilityManagerIfc utility)
    {
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("DATABASE_ERROR");
        String msg[] = new String[1];
        msg[0] = utility.retrieveDialogText(DATABASE_OFFLINE_TAG, DATABASE_OFFLINE);
        model.setArgs(msg);
        model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
}
