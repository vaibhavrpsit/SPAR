/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/RedeemRefundOptionsUISite.java /main/15 2013/09/05 10:36:16 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    rrkohli   05/06/11 - Added Code For Pos UI update Quick win
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:36 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:35 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:36 PM  Robert Pearse
 *
 *   Revision 1.6  2004/06/04 21:03:18  crain
 *   @scr 5338 Client crashes if F11 Delete, then Undo selected on Redeem Refund Options dialog
 *
 *   Revision 1.5  2004/04/22 22:35:55  blj
 *   @scr 3872 - more cleanup
 *
 *   Revision 1.4  2004/04/07 22:49:41  blj
 *   @scr 3872 - fixed problems with foreign currency, fixed ui labels, redesigned to do validation and adding tender to transaction in separate sites.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import java.util.HashMap;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.services.tender.tdo.RefundOptionsTDO;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

/**
 * This site displays the redeem refund options screen.
 * This site will display the refund due for local and foreign amounts.
 */
public class RedeemRefundOptionsUISite extends PosSiteActionAdapter
{
    /** Display redeem refund options screen.
     * @param bus
     */
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        // Create map for TDO
        HashMap attributeMap = new HashMap(2);
        attributeMap.put(RefundOptionsTDO.BUS, bus);

        attributeMap.put(RefundOptionsTDO.TRANSACTION, cargo.getCurrentTransactionADO());

        // build bean model helper
        TDOUIIfc tdo = null;
        try
        {
            tdo = (TDOUIIfc)TDOFactory.create("tdo.tender.RedeemRefundOptions");
        }
        catch (TDOException tdoe)
        {
            tdoe.printStackTrace();
        }

        // display the configured tender options screen
        // get the UI manager
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.REDEEM_REFUND_OPTIONS, tdo.buildBeanModel(attributeMap));
    }

    /**
     * At this point, we know the amount entered.  Save it in the tender
     * attributes.
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#depart(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void depart(BusIfc bus)
    {
        String letterName = bus.getCurrentLetter().getName();

        if (!letterName.equals("Undo") &&
            !letterName.equals("Clear") &&
            !letterName.equals("Cancel"))
        {
            TenderCargo cargo = (TenderCargo)bus.getCargo();

            // reset the tender attributes Map.  At this point
            // it is either no longer needed, or we have a new tender.
            cargo.resetTenderAttributes();
            cargo.setTenderADO(null);

            HashMap attributes = cargo.getTenderAttributes();
            attributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.makeEnumFromString(bus.getCurrentLetter().getName()));

            // save the entered amount in the tender attributes
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            String amountStr = LocaleUtilities.parseCurrency(ui.getInput(),LocaleMap.getLocale(LocaleMap.DEFAULT)).toString();
            // reverse the sign of the amount because this is a refund (or change) tender
            amountStr = "-" + amountStr;
            cargo.getTenderAttributes().put(TenderConstants.AMOUNT, amountStr);
        }
    }

}
