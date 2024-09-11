/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/TenderOptionsUISite.java /main/24 2013/09/26 15:46:55 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    abhinavs  09/04/13 - Fix to disable the instant credit button as one of
 *                         the tender options while doing layaway or order pickup
 *    cgreene   07/21/11 - remove ability to show separate Debit button
 *    cgreene   06/07/11 - update to first pass of removing pospal project
 *    cgreene   10/25/10 - do not call setModel right after calling showScreen
 *    npoola    09/22/10 - removed the mailed OnScreenKeyboard letter logic and
 *                         show OnScreenKeyboard synchronously
 *    asinton   07/01/10 - Moved Select Payment Type prompt into pospal
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    cgreene   12/16/09 - show keyboard when arriving at sell or tender screen
 *    asinton   01/28/09 - Pre code review changes.
 *    asinton   01/28/09 - Calling setModel after showScreen in order to update
 *                         the local navigation buttons correctly.
 *    asinton   01/22/09 - Updates to fix Ingenico screen problems.
 *    djenning  01/16/09 - update for screens without giftcards
 *    djenning  01/15/09 - update tender select request with whether or not
 *                         giftcards are enabled
 *
 * ===========================================================================
 * $Log:
 *  4    360Commerce 1.3         12/13/2005 4:42:35 PM  Barry A. Pape
 *       Base-lining of 7.1_LA
 *  3    360Commerce 1.2         3/31/2005 4:30:25 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:26:02 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:14:56 PM  Robert Pearse
 * $
 * Revision 1.12.2.2  2004/11/18 23:14:39  bwf
 * @scr 6552 Call correct CPOI screens during tender process for swipe anytime.
 *
 * Revision 1.12.2.1  2004/11/18 20:05:45  bwf
 * @scr 6552 Call correct CPOI screens during tender process for swipe anytime.
 *
 * Revision 1.12  2004/09/27 22:33:09  bwf
 * @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 * Revision 1.11  2004/08/27 21:23:09  bwf
 * @scr 7074 Use factory to create tdo.
 *
 * Revision 1.10  2004/08/20 21:36:30  bwf
 * @scr 6553 Make it so that credit/debit and gift card are the only buttons
 *                   enabled during swipe anytime.
 *
 * Revision 1.9  2004/07/14 18:47:09  epd
 * @scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation
 *
 * Revision 1.8  2004/07/08 20:34:57  bwf
 * @scr 6049
 *
 * Revision 1.7  2004/05/12 21:22:35  rzurga
 * @scr 5030 Re-enabling of the swipe anytime
 *
 * Revision 1.6  2004/04/09 19:31:43  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * Revision 1.5  2004/04/09 19:03:58  bwf
 * @scr 4350 Changed from gateway.getProperites to util.getparamter.
 *
 * Revision 1.4  2004/03/26 21:18:19  cdb
 * @scr 4204 Removing Tabs.
 *
 * Revision 1.3  2004/03/24 23:23:55  bjosserand
 * @scr 4093 Transaction Reentry
 * Revision 1.2 2004/02/12 16:48:22 mcs Forcing head revision
 *
 * Revision 1.1.1.1 2004/02/11 01:04:12 cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.5 Jan 06 2004 17:09:04 epd separate credit/debit buttons if credit is disabled
 *
 * Rev 1.4 Jan 06 2004 10:11:20 rsachdeva Refactoring Resolution for POS SCR-3551: Tender using Canadian Cash/Canadian
 * Travelers Check/Canadian Check
 *
 * Rev 1.3 Dec 05 2003 13:45:12 rsachdeva Screen ID Resolution for POS SCR-3551: Tender using Canadian Cash
 *
 * Rev 1.2 Nov 07 2003 15:34:42 cdb Reverted to extendyourstore version of AbstractFinancialCargo due to changes in
 * tender.xml. Resolution for 3430: Sale Service Refactoring
 *
 * Rev 1.0 Nov 04 2003 11:17:56 epd Initial revision.
 *
 * Rev 1.0 Oct 23 2003 17:29:54 epd Initial revision.
 *
 * Rev 1.2 Oct 22 2003 12:09:58 epd Updates for Credit/Debit flow
 *
 * Rev 1.1 Oct 20 2003 16:31:00 epd Updated code to determine which tender options screen to display
 *
 * Rev 1.0 Oct 17 2003 13:06:50 epd Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import java.util.HashMap;
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.ShowOnScreenKeyboardAisle;
import oracle.retail.stores.pos.services.tender.tdo.TenderOptionsTDO;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * Put up a configured UI screen
 */
public class TenderOptionsUISite extends PosSiteActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1359946699548818136L;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo) bus.getCargo();
        boolean transReentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();

        // build bean model helper
        TDOUIIfc tdo = null;
        try
        {
            tdo = (TDOUIIfc)TDOFactory.create("tdo.tender.TenderOptions");
        }
        catch (TDOException tdoe)
        {
            logger.error("Problem creating Tender Options screen: " + tdoe.getMessage());
        }

        // Create map for TDO
        HashMap<String,Object> attributeMap = new HashMap<String,Object>(4);
        attributeMap.put(TenderOptionsTDO.BUS, bus);
        attributeMap.put(TenderOptionsTDO.TRANSACTION, ((AbstractFinancialCargo) bus.getCargo()).getCurrentTransactionADO());
        attributeMap.put(TenderOptionsTDO.TRANSACTION_REENTRY_MODE, new Boolean(transReentryMode));
        attributeMap.put(TenderOptionsTDO.SWIPE_ANYTIME, new Boolean(cargo.getPreTenderMSRModel() != null));
        

        displayCorrectScreen(bus, POSUIManagerIfc.TENDER_OPTIONS, POSUIManagerIfc.TENDER_OPTIONS_CPOI, attributeMap, tdo);

        // display the on screen keyboard
        showOnScreenKeyboard(bus);

    }

    /**
     * Call the ShowOnScreenKeyboard Aisle to display the On Screen Keyboard
     * after displaying the Tender Options and POI screen 
     * @param bus
     */
    protected void showOnScreenKeyboard(BusIfc bus)
    {
        new ShowOnScreenKeyboardAisle().traverse(bus);        
    }

    /**
     * At this point, we know the amount entered. Save it in the tender attributes.
     *
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#depart(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void depart(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo) bus.getCargo();

        // reset the tender attributes Map. At this point
        // it is either no longer needed, or we have a new tender.
        cargo.resetTenderAttributes();
        cargo.setTenderADO(null);
        cargo.setOverrideOperator(null);

        // save the entered amount in the tender attributes
        String letterName = bus.getCurrentLetter().getName();
        if (!CommonLetterIfc.UNDO.equals(letterName)
            && !CommonLetterIfc.CLEAR.equals(letterName)
            && !CommonLetterIfc.CANCEL.equals(letterName))
        {
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            String input = ui.getInput();
            Locale defaultLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);
            if (input == null ||
                input.equals(""))
            {
                input = cargo.getCurrentTransactionADO().getBalanceDue().toFormattedString();
            }
            String amount = LocaleUtilities.parseCurrency(input, defaultLocale).toString();
            cargo.getTenderAttributes().put(TenderConstants.AMOUNT, amount);
        }
        else if (bus.getCurrentLetter().getName().equals("Cancel"))
        {
            // nullify msr model.  user canceled.
            cargo.setPreTenderMSRModel(null);
        }
    }

    /**
     * This method displays the 3 button screen on the CPOI.
     *
     * @param bus
     * @deprecated as of 13.4. No replacement
     */
    public void showCPOIThreeButton(BusIfc bus)
    {
    }

    /**
     * This method displays the 4 button screen on the CPOI.
     *
     * @param bus
     * @deprecated as of 13.4. No replacement
     */
    public void showCPOIFourButton(BusIfc bus)
    {
    }

    /**
     * Displays screen or swipeScreen depending on the result of
     * POSDeviceActions.isFormOnline().equals(Boolean.TRUE).
     *
     * @param bus
     * @param screen
     * @param swipeScreen
     * @param attributeMap
     * @param tdo
     */
    protected void displayCorrectScreen(BusIfc bus, String screen, String swipeScreen,
            HashMap<String,Object> attributeMap, TDOUIIfc tdo)
    {

        TenderCargo cargo = (TenderCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel model = tdo.buildBeanModel(attributeMap);
        if((cargo.getTransType() == TransactionIfc.TYPE_LAYAWAY_COMPLETE) || (cargo.getTransType() == TransactionIfc.TYPE_LAYAWAY_PAYMENT)
                || (cargo.getTransType() == TransactionIfc.TYPE_ORDER_COMPLETE) ||  (cargo.getTransType() == TransactionIfc.TYPE_ORDER_PARTIAL))
        {
            NavigationButtonBeanModel nModel = model.getLocalButtonBeanModel();
            nModel.setButtonEnabled(CommonActionsIfc.INSTANT_CREDIT, false);
            model.setLocalButtonBeanModel(nModel);
        }

        /*
         * setting the model after showing the screen causes the screen to be updated
         * which is necessary for the enabled/disabled buttons to display correctly
         */
        ui.showScreen(screen, model);
    }
}
