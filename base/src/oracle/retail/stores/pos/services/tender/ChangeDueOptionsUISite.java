/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/ChangeDueOptionsUISite.java /main/24 2013/09/05 10:36:16 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    icole     03/06/12 - Refactor to remove CPOIPaymentUtility and attempt to
 *                         have more generic code, rather than heavily Pincomm.
 *    blarsen   09/12/11 - Moving displayChangeDueScreen() to
 *                         CPOIPaymentUtility (for consistency).
 *    cgreene   08/24/11 - create interfaces for customerinteraction objects
 *    cgreene   07/12/11 - update generics
 *    icole     07/08/11 - Remove DeviceExceptions related to Payment CPOI to
 *                         be consistent with other Payment methods.
 *    blarsen   06/28/11 - Renamed CustomerInteractionRequest.RequestType to
 *                         RequestSubType.
 *    cgreene   06/15/11 - implement gift card for servebase and training mode
 *    blarsen   06/15/11 - Adding storeID to customer interaction request.
 *    icole     06/09/11 - CPOI updates
 *    icole     06/09/11 - APF change
 *    cgreene   06/07/11 - update to first pass of removing pospal project
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    nkgautam  12/01/10 - reset refund letter in the cargo
 *    cgreene   10/25/10 - do not call setModel right after showScreen
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    asinton   04/28/09 - Enable the Cancel button on the change due options
 *                         screen only when cash is the only available option.
 *    mahising  02/18/09 - fixed amount issue at refund screen
 *    sgu       01/13/09 - specify decimal format (non locale sensitive) in
 *                         tender attraibutes
 *    cgreene   11/06/08 - add isCollected to tenders for printing just
 *                         collected tenders
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:52:46 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:27:22 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:03 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:51 PM  Robert Pearse
 *
 *   Revision 1.10.2.1  2004/11/01 22:03:55  bwf
 *   @scr 7388 Fixed quantity of traveler's check being used as change amount.
 *
 *   Revision 1.10  2004/09/17 23:00:01  rzurga
 *   @scr 7218 Move CPOI screen name constants to CIDAction to make it more generic
 *
 *   Revision 1.9  2004/07/28 22:56:37  cdb
 *   @scr 6179 Externalized some CIDScreen values.
 *
 *   Revision 1.8  2004/07/24 16:43:52  rzurga
 *   @scr 6463 Items are showing on CPOI sell item from previous transaction
 *   Remove newly introduced automatic hiding of non-active CPOI screens
 *   Enable clearing of non-visible CPOI screens
 *   Improve appearance by clearing first, then setting fields and finally showing the CPOI screen
 *
 *   Revision 1.7  2004/07/22 22:38:41  bwf
 *   @scr 3676 Add tender display to ingenico.
 *
 *   Revision 1.6  2004/07/01 20:07:00  blj
 *   @scr 5937 - strange problem, may revisit later.
 *
 *   Revision 1.5  2004/06/19 17:33:33  bwf
 *   @scr 5205 These are the overhaul changes to the Change Due Options
 *                     screen and max change calculations.
 *
 *   Revision 1.4  2004/04/15 22:03:37  epd
 *   @scr 4322 Updates for Tender Invariant work: handling Change invariant
 *
 *   Revision 1.3  2004/03/24 20:11:14  bwf
 *   @scr 3956 Code Review
 *
 *   Revision 1.2  2004/03/23 17:41:28  bwf
 *   @scr 3956 Code Review
 *
 *   Revision 1.1  2004/03/09 20:10:13  bwf
 *   @scr 3956 General Tenders work.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import java.util.HashMap;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.manager.ifc.PaymentManagerIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.tender.tdo.TenderTDOConstants;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * This class puts up the change due options screen.
 * @version $Revision: /main/24 $
 */
public class ChangeDueOptionsUISite extends PosSiteActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -4981605264349923136L;


    /**
     * This is the arrive method that puts up the screen.
     *
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();

        //resetting the refund letter to null to handle conditions when max/min change limits is not overridden
        cargo.setRefundTenderLetter(null);

        // only display this screen if balance due is greater than zero.
        // This might happen if we have forced cash change due to depletion of gift card
        // remaining balance.  In lieu of showing this screen, move on directly to handle cash.
        if (cargo.getCurrentTransactionADO().getBalanceDue().signum() == CurrencyIfc.ZERO)
        {
            bus.mail(new Letter("ForcedCash"), BusIfc.CURRENT);
            return;
        }

        // Create map for TDO
        HashMap<String,Object> attributeMap = new HashMap<String,Object>(2);
        attributeMap.put(TenderTDOConstants.BUS, bus);
        attributeMap.put(TenderTDOConstants.TRANSACTION, cargo.getCurrentTransactionADO());

        // build bean model helper
        TDOUIIfc tdo = null;
        try
        {
            tdo = (TDOUIIfc)TDOFactory.create("tdo.tender.ChangeDueOptions");
        }
        catch (TDOException tdoe)
        {
            logger.error("Unable to get change due options", tdoe);
        }
        PaymentManagerIfc paymentManager = (PaymentManagerIfc)bus.getManager(PaymentManagerIfc.TYPE);

        paymentManager.displayChangeDueScreen(cargo.getRegister().getWorkstation(), cargo.getCurrentTransactionADO().getBalanceDue());

        // display the configured change due options screen
        // get the UI manager
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel model = tdo.buildBeanModel(attributeMap);

        // if cash is the only option and this is the first time.  force cash
        if (cargo.getCurrentTransactionADO().isCashOnlyOptionForChangeDue() &&
            cargo.isFirstTimeChangeDue())
        {
            cargo.setCashOnlyOption(true);
            cargo.setFirstTimeChangeDue(false);
            bus.mail(new Letter("Cash"), BusIfc.CURRENT);
        }
        else
        {
            if(cargo.getCurrentTransactionADO().isCashOnlyOptionForChangeDue())
            {
                NavigationButtonBeanModel globalNav = new NavigationButtonBeanModel();
                globalNav.setButtonEnabled(CommonActionsIfc.CANCEL, true);
                model.setGlobalButtonBeanModel(globalNav);
            }
            ui.showScreen(POSUIManagerIfc.CHANGE_DUE_OPTIONS, model);
        }
    }

    /**
     * This is the depart method that captures the amount.
     *
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#depart(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void depart(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();

        // reset the tender attributes Map.  At this point
        // it is either no longer needed, or we have a new tender.
        cargo.resetTenderAttributes();
        cargo.setTenderADO(null);

        String letterName = bus.getCurrentLetter().getName();

        // save the entered amount in the tender attributes
        if (!letterName.equals("Undo") &&
            !letterName.equals("Clear") &&
            !letterName.equals("Cancel") &&
            !letterName.equals("ForcedCash"))
        {
            HashMap<String,Object> attributes = cargo.getTenderAttributes();
            attributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.makeEnumFromString(bus.getCurrentLetter().getName()));
            attributes.put(TenderConstants.COLLECTED, Boolean.FALSE); //given to customer

            // save the entered amount in the tender attributes
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            String inputStr = ui.getInput();
            String amountStr = null;

            // if cash was only option there wont be any input
            // or if the input was not a valid number.
            // travelers check keeps the amount of travelers check as input here.
            // that is why we are checking cashOnlyOption
            if (Util.isEmpty(inputStr) || !Util.isStringANumber(inputStr) || cargo.isCashOnlyOption())
            {
                amountStr = cargo.getCurrentTransactionADO().getBalanceDue().getStringValue();
                cargo.setCashOnlyOption(false);
            }
            else
            {
                amountStr = LocaleUtilities.formatCurrency(inputStr, LocaleMap.getLocale(LocaleMap.DEFAULT));
                // reverse the sign of the amount because this is a refund (or change) tender
                amountStr = "-" + amountStr;
            }

            attributes.put(TenderConstants.AMOUNT, amountStr);
        }
    }

}
