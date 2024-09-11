/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/TenderingInfoUISite.java /main/21 2014/01/07 18:00:13 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  01/07/14 - fix dereferencing of null objects.
 *    icole     08/06/13 - Remove change that added balance due label adding
 *                         tenders. Now handled in the scrolling receipt
 *                         formatters.
 *    icole     07/11/13 - Add text to request to show balance due.
 *    icole     03/06/12 - Refactor to remove CPOIPaymentUtility and attempt to
 *                         have more generic code, rather than heavily Pincomm.
 *    jswan     06/29/11 - Modified to use ADO transaction object.
 *    icole     06/17/11 - Remove redunant non working CPOI tenders code
 *    icole     06/16/11 - Removed commented out lines
 *    icole     06/16/11 - Changes for CurrencyIfc, Sardine refresh items list,
 *                         other simulted changes.
 *    cgreene   06/15/11 - implement gift card for servebase and training mode
 *    blarsen   06/15/11 - Adding storeID to customer interaction request.
 *    icole     06/14/11 - Restore CurrencyIfc
 *    blarsen   06/14/11 - Adding storeID to scrolling receipt request.
 *    icole     06/09/11 - CPOI code changes
 *    cgreene   06/07/11 - update to first pass of removing pospal project
 *    icole     04/28/11 - Payment updates
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    04/09/10 - optimize calls to LocaleMAp
 *    acadar    04/05/10 - use default locale for currency and date/time
 *                         display
 *    abondala  01/03/10 - update header date
 *    asinton   04/03/09 - Localizing the amounts that appear in the CPOI
 *                         device.
 *    asinton   02/26/09 - changes per review comments from Christian Greene.
 *    asinton   02/24/09 - Added utility manager.
 *    asinton   02/24/09 - Honor thy customer's preferred language.
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:30:25 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:25:59 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:14:54 PM  Robert Pearse
 *
 *  Revision 1.12  2004/09/17 23:00:01  rzurga
 *  @scr 7218 Move CPOI screen name constants to CIDAction to make it more generic
 *
 *  Revision 1.11  2004/07/28 22:56:37  cdb
 *  @scr 6179 Externalized some CIDScreen values.
 *
 *  Revision 1.10  2004/07/24 16:43:52  rzurga
 *  @scr 6463 Items are showing on CPOI sell item from previous transaction
 *  Remove newly introduced automatic hiding of non-active CPOI screens
 *  Enable clearing of non-visible CPOI screens
 *  Improve appearance by clearing first, then setting fields and finally showing the CPOI screen
 *
 *  Revision 1.9  2004/07/22 23:33:07  dcobb
 *  @scr 3676 Add tender display to ingenico.
 *
 *  Revision 1.8  2004/07/22 22:38:41  bwf
 *  @scr 3676 Add tender display to ingenico.
 *
 *  Revision 1.6  2004/07/12 21:49:06  bwf
 *  @scr 6170 The check tender was being remove twice.  Removed the
 *                    unnecessary one.
 *
 *  Revision 1.5  2004/04/05 15:47:54  jdeleau
 *  @scr 4090 Code review comments incorporated into the codebase
 *
 *  Revision 1.4  2004/03/25 20:25:15  jdeleau
 *  @scr 4090 Deleted items appearing on Ingenico, I18N, perf improvements.
 *  See the scr for more info.
 *
 *  Revision 1.3  2004/02/12 16:48:22  mcs
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 21:22:51  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Nov 19 2003 14:15:36   epd
 * updated to use TDO factory
 *
 *    Rev 1.0   Nov 04 2003 11:17:54   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 23 2003 17:29:52   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 17 2003 13:06:48   epd
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import oracle.retail.stores.domain.manager.ifc.PaymentManagerIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;

/**
 * User has selected Tender
 * 
 * @version $Revision: /main/21 $
 */
public class TenderingInfoUISite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -2867106822797919316L;

    /**
     * revision number supplied by source-code-control system
     */
    public static String revisionNumber = "$Revision: /main/21 $";

    /**
     * Determine transaction type - Display appropriate UI - If the balance is
     * paid, proceed
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        displayPoleDisplayInfo(bus);
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        PaymentManagerIfc paymentManager = (PaymentManagerIfc)bus.getManager(PaymentManagerIfc.TYPE);
        WorkstationIfc workstation = cargo.getRegister().getWorkstation();
        paymentManager.addTenders(workstation, (TenderableTransactionIfc)cargo.getCurrentTransactionADO().toLegacy());
        bus.mail(CommonLetterIfc.NEXT, BusIfc.CURRENT);
    }

 
    /**
     * Displays transaction info on the pole device
     * @param bus
     */
    protected void displayPoleDisplayInfo(BusIfc bus)
    {
        try
        {
            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
            pda.clearText();

            TenderCargo cargo = (TenderCargo)bus.getCargo();

            // build bean model helper
            TDOUIIfc tdo = null;
            try
            {
                tdo = (TDOUIIfc)TDOFactory.create("tdo.tender.TenderLineDisplay");
            }
            catch (TDOException tdoe)
            {
                logger.error(tdoe);
            }
            if (tdo != null)
            {
                pda.displayTextAt(0, 0, tdo.formatPoleDisplayLine1(cargo.getCurrentTransactionADO()));
                pda.displayTextAt(1, 0, tdo.formatPoleDisplayLine2(cargo.getCurrentTransactionADO()));
            }
        }
        catch (DeviceException e)
        {
            logger.error(e);
        }

    }
}
