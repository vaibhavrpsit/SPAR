/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/PaidUpActionSite.java /main/16 2014/02/10 10:19:22 bhsuthar Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    bhsuthar  02/07/14 - do not unlock container to avoid unexpected button
 *                         pressed by user.
 *    abondala  09/04/13 - initialize collections
 *    jswan     05/20/13 - Refactored the location of cash change adjustment
 *                         calculation in the tender tour to handle cancel
 *                         order refunds.
 *    jswan     08/04/11 - Removed check for authorizable tenders which are in
 *                         PENDING state; changes to the order of tender
 *                         processing make this check unnecessary.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         6/9/2008 12:12:01 PM   Alan N. Sinton  CR
 *         31886: Reverting this file because it broke tender authorizations.
 *          Code Reviewed by Dwight Jennings and Brett Larsen.
 *    7    360Commerce 1.6         6/7/2008 7:45:12 AM    Naveen Ganesh   Added
 *          cash transaction if the layaway delete with $0 refund for CR 31886
 *    6    360Commerce 1.5         3/21/2008 12:21:12 PM  Mathews Kochummen
 *         forward port from v12x to trunk
 *    5    360Commerce 1.4         4/25/2007 8:52:45 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    4    360Commerce 1.3         12/13/2005 4:42:35 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:29:18 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:58 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:58 PM  Robert Pearse   
 *
 *   Revision 1.7.2.1  2004/11/12 14:28:53  kll
 *   @scr 7337: JournalFactory extensibility initiative
 *
 *   Revision 1.7  2004/09/27 22:33:09  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.6  2004/09/22 18:24:38  bwf
 *   @scr 3511 Create cash change tender and make sure refund options
 *   	       button calculations work correctly.
 *
 *   Revision 1.5  2004/06/23 00:46:11  blj
 *   @scr 5113 - added capture customer capability for redeem store credit.
 *
 *   Revision 1.4  2004/04/27 15:50:29  epd
 *   @scr 4513 Fixing tender change options functionality
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
 *    Rev 1.4   Feb 04 2004 10:43:32   Tim Fritz
 * Check to see if the tender is allowed to open the drawer.
 * Resolution for 3488: Prompted to close drawer even though tender is not in Open Drawer for Tender Parameter
 * Resolution for 3590: When Gift Card is remove from the Open Drawer for Tender Parameter List the drawer still opens.
 * Resolution for 3643: Open Drawer for Tender- Remove  Money Order and Cash Drawer still opens
 * Resolution for 3745: Open Drawer for Tender - Remove Mall Certificate, drawer still opens
 * Resolution for 3756: Open Drawer for Tender - Remove Check, Cash Drawer still opens
 * Resolution for 3757: Open Drawer for Tender - Remove Purchase Order and Cash Drawer still opens
 * 
 *    Rev 1.3   Nov 19 2003 14:10:48   epd
 * TDO refactoring to use factory
 * 
 *    Rev 1.2   Nov 07 2003 14:50:18   cdb
 * Corrected incorrect fix.
 * Resolution for 3430: Sale Service Refactoring
 * 
 *    Rev 1.0   Nov 04 2003 11:17:48   epd
 * Initial revision.
 * 
 *    Rev 1.2   Oct 31 2003 09:01:42   epd
 * refactoring
 * 
 *    Rev 1.1   Oct 30 2003 20:43:52   epd
 * new routing to authorization service
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

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
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
import oracle.retail.stores.pos.services.tender.tdo.PaidUpTDO;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

/**
 *  Determine whether tender authorization is required and if so,
 *  route to authorization, otherwise finish up
 */
public class PaidUpActionSite extends PosSiteActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -7340249219349184415L;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {        
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        
        // set up the TDO attributes
        HashMap attributes = new HashMap(2);
        attributes.put(PaidUpTDO.BUS, bus);
        attributes.put(PaidUpTDO.TRANSACTION, cargo.getCurrentTransactionADO());
        
        // build bean model helper
        TDOUIIfc tdo = null;
        try
        {
            tdo = (TDOUIIfc)TDOFactory.create("tdo.tender.PaidUp");
        }
        catch (TDOException tdoe)
        {
            tdoe.printStackTrace();
        }

        RetailTransactionADOIfc trans = cargo.getCurrentTransactionADO();

        // Calculate the cash change/refund adjustment amount (if any).
        trans.adjustCashAmountReturnedToCustomer();

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        if (trans.openDrawer())
        {
        	boolean isReentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();
            // we know we have negative cash and we will never get here in return
            if (trans.getTotalCashChangeAmount().compareTo(DomainGateway.getBaseCurrencyInstance()) == CurrencyIfc.GREATER_THAN)
            {
                ui.showScreen(POSUIManagerIfc.ISSUE_CHANGE, tdo.buildBeanModel(attributes));
            } 
            else if(isReentryMode == false)
            {
                ui.showScreen(POSUIManagerIfc.CLOSE_DRAWER, tdo.buildBeanModel(attributes));
            }
        }

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
        	ui.statusChanged(2, true, false);
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
            logger.warn("Unable to use Line Display: " + e.getMessage());
        }

    }
}
