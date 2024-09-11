/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/RefundDueUISite.java /main/17 2013/09/05 10:36:16 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   09/11/14 - remove unused argument for call to
 *                         updateCustomerForRedeemedStoreCredit
 *    abananan  09/09/14 - fix for class cast exception
 *    abondala  09/04/13 - initialize collections
 *    jswan     05/20/13 - Refactored the location of cash change adjustment
 *                         calculation in the tender tour to handle cancel
 *                         order refunds.
 *    cgreene   07/12/11 - update generics
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    arathore  02/14/09 - Updated to pass Personal Id information to printing
 *                         tour.
 *    abondala  11/06/08 - updated files related to reason codes
 *    abondala  11/05/08 - updated files related to reason codes
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         12/13/2005 4:42:35 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:29:37 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:37 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:37 PM  Robert Pearse   
 *
 *   Revision 1.12.2.1  2004/11/12 14:28:53  kll
 *   @scr 7337: JournalFactory extensibility initiative
 *
 *   Revision 1.12  2004/09/27 22:33:09  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.11  2004/07/14 18:47:09  epd
 *   @scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation
 *
 *   Revision 1.10  2004/07/12 20:58:26  jriggins
 *   @scr 4401 Incorporated logic in EvaluateBalanceSite to determine when to capture customer information based on the NegativeAmtDue parameter. Removed this check from RefundOptionsDueUISite because it is now performed at a more generic level.
 *
 *   Revision 1.9  2004/06/23 00:46:11  blj
 *   @scr 5113 - added capture customer capability for redeem store credit.
 *
 *   Revision 1.8  2004/06/18 12:13:14  khassen
 *   @scr 5684 - Feature enhancements for capture customer use case: had to change how customer was being accessed.
 *
 *   Revision 1.7  2004/06/15 22:57:18  bwf
 *   @scr 5000 Check to see if customer was captured before asking again.
 *
 *   Revision 1.6  2004/04/15 14:39:11  bwf
 *   @scr 3956 fix check of customer.
 *
 *   Revision 1.5  2004/04/13 21:43:09  bwf
 *   @scr 4263 Fix problem with decomposition.
 *
 *   Revision 1.4  2004/03/17 19:26:23  bwf
 *   @scr 3956 Update to check customer in return trans and update
 *                     refund options buttons.
 *
 *   Revision 1.3  2004/02/12 16:48:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.4   Jan 30 2004 14:19:24   epd
 * added check for refund limits and invokes auth on refunds when needed
 *
 *    Rev 1.3   Nov 19 2003 14:10:50   epd
 * TDO refactoring to use factory
 *
 *    Rev 1.2   Nov 07 2003 14:50:22   cdb
 * Corrected incorrect fix.
 * Resolution for 3430: Sale Service Refactoring
 *
 *    Rev 1.0   Nov 04 2003 11:17:52   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 23 2003 17:29:50   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 17 2003 13:06:46   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import java.util.HashMap;

import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.JournalableADOIfc;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.tender.tdo.RefundDueTDO;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

/**
 *  Show UI showing that we will issue refund
 */
@SuppressWarnings("serial")
public class RefundDueUISite extends PosSiteActionAdapter
{
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();

        // First see if we need to update captured customer
        // info for a redeemed store credit.
        if (cargo.getCustomer() != null)
        {
            cargo.getCurrentTransactionADO().updateCustomerForRedeemedStoreCredit(cargo.getCustomer(),
                    cargo.getLocalizedPersonalIDCode());
        }

        // set up the TDO attributes
        HashMap<String,Object> attributes = new HashMap<String,Object>(2);
        attributes.put(RefundDueTDO.BUS, bus);
        attributes.put(RefundDueTDO.TRANSACTION, cargo.getCurrentTransactionADO());

        // build bean model helper
        TDOUIIfc tdo = null;
        try
        {
            tdo = (TDOUIIfc)TDOFactory.create("tdo.tender.RefundDue");
        }
        catch (TDOException tdoe)
        {
            // TODO Auto-generated catch block
            tdoe.printStackTrace();
        }
        
        // Calculate the cash change/refund adjustment amount (if any).
        RetailTransactionADOIfc trans = cargo.getCurrentTransactionADO();

        // Calculate the cash change/refund adjustment amount (if any).
        trans.adjustCashAmountReturnedToCustomer();

        // Display
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.ISSUE_REFUND, tdo.buildBeanModel(attributes));

        displayLineDisplayText(bus, tdo);

        // Journal the tender totals (takes the transaction)
        JournalFactoryIfc jrnlFact = null;
        try
        {
            jrnlFact = JournalFactory.getInstance();
        }
        catch (ADOException e)
        {
            logger.error(JournalFactoryIfc.INSTANTIATION_ERROR, e);
            throw new RuntimeException(JournalFactoryIfc.INSTANTIATION_ERROR, e);
        }
        RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();
        registerJournal.journal((JournalableADOIfc)cargo.getCurrentTransactionADO(),
                        JournalFamilyEnum.TRANSACTION,
                        JournalActionEnum.TENDER_TOTAL);

        if (cargo.getCurrentTransactionADO().getIRSCustomer() != null)
        {
            registerJournal.journal((JournalableADOIfc)cargo.getCurrentTransactionADO(),
                JournalFamilyEnum.TRANSACTION,
                JournalActionEnum.IRS_CUSTOMER);
        }

        if (cargo.getCurrentTransactionADO().openDrawer())
        {
            bus.mail(new Letter("Open"), BusIfc.CURRENT);
        }
        else
        {
            bus.mail(new Letter("ExitTender"), BusIfc.CURRENT);
        }
    }

    /**
     * Display information on the Line display
     * @param bus
     */
    protected void displayLineDisplayText(BusIfc bus, TDOUIIfc tdo)
    {
        try
        {
            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);

            pda.clearText();

            pda.displayTextAt(1,0,tdo.formatPoleDisplayLine2(((AbstractFinancialCargo)bus.getCargo()).getCurrentTransactionADO()));
        }
        catch (DeviceException e)
        {
            logger.warn(
                        "Unable to use Line Display: " + e.getMessage() + "");
        }

    }
}
