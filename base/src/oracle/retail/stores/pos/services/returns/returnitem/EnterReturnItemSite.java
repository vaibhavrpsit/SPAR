/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/EnterReturnItemSite.java /main/18 2014/07/08 11:41:53 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   11/14/14 - handle the case in mpos, message does not exist.
 *    yiqzhao   11/13/14 - set pluItemId to null to make flow finish loop.pluItemId
 *                         is used in GetReturnItemEnteredAisle.
 *    jswan     11/03/14 - added UIN (SIM) lookup to the non-receipted returns flow.
 *    cgreene   07/08/14 - refactor default timer model to default to 15
 *                         minutes timeout and be able to find parametermanager
 *                         from dispatcher
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    asinton   10/28/10 - Set Next button enabled if there are
 *                         ReturnSaleLineItems in the cargo.
 *    cgreene   10/25/10 - do not call setModel right after showScreen
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    mchellap  01/11/10 - Set prompt length to imei length
 *    mchellap  12/16/09 - Changes for code review findings
 *    mchellap  12/10/09 - Serialisation return without receipt changes
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         5/28/2008 3:46:48 PM   Anil Rathore
 *         Updated to display ITEM_NOT_FOUND dialog. Changes reviewed by Dan.
 *    4    360Commerce 1.3         5/27/2008 7:37:28 PM   Anil Rathore
 *         Updated to display ITEM_NOT_FOUND dialog. Changes reviewed by Dan.
 *    4    I18N_P2    1.2.1.0     12/18/2007 3:09:14 PM  Sandy Gu        static
 *          text fix for POS
 *    3    360Commerce 1.2         3/31/2005 4:28:04 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:27 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:55 PM  Robert Pearse
 *
 *   Revision 1.16  2004/09/16 20:09:20  jdeleau
 *   @scr 6526 Prevent the user from entering "Enter" when nothing is selected
 *
 *   Revision 1.15  2004/08/17 20:54:33  jdeleau
 *   @scr 6851 Change letter for timeouts to "Timeout" to make
 *   sure the application logsout instead of going back one site via undo.
 *
 *   Revision 1.14  2004/07/29 19:22:37  rsachdeva
 *   @scr 6274 Item Not Found Cancel for Returns
 *
 *   Revision 1.13  2004/07/22 23:08:57  blj
 *   @scr 6258 - changed the flow so that if UNDO is pressed, we dont lookup the item again we use the information previously entered.
 *
 *   Revision 1.12  2004/07/13 21:23:05  jdeleau
 *   @scr 6226 Disable the escape/undo button for the ReturnEnterItem screen.
 *   Also enable the cancel button, as in the mockup.
 *
 *   Revision 1.11  2004/07/12 23:01:55  mweis
 *   @scr 6150 Screen prompt wrong when returning items for a transaction that wasn't found.
 *
 *   Revision 1.10  2004/05/13 19:38:40  jdeleau
 *   @scr 4862 Support timeout for all screens in the return item flow.
 *
 *   Revision 1.9  2004/05/11 14:32:59  jlemieux
 *   @scr
 *   270 Fixed by adding a veto mechanism to the lifting of the GlassComponent. In particular, the GlassComponent lift is now vetoed when the scanner's scan queue contains 1 or more items and we are on a multiscan screen in POS. This effectively makes POS "prefer" to drain scan queues rather than service user input, which is what we want.
 *
 *   Revision 1.8  2004/03/26 05:39:05  baa
 *   @scr 3561 Returns - modify flow to support entering price code for not found gift receipt
 *
 *   Revision 1.7  2004/03/24 21:32:38  epd
 *   @scr 3561 updated to defensively clear the search criteria
 *
 *   Revision 1.6  2004/03/09 17:05:52  epd
 *   @scr 3561 broke the coupling to another site class
 *
 *   Revision 1.5  2004/02/27 22:43:50  baa
 *   @scr 3561 returns add trans not found flow
 *
 *   Revision 1.4  2004/02/27 01:43:29  baa
 *   @scr 3561 returns - selecting return items
 *
 *   Revision 1.3  2004/02/12 16:51:49  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   05 Feb 2004 23:22:06   baa
 * returns multi items
 *
 *    Rev 1.1   Dec 19 2003 13:22:44   baa
 * more return enhancements
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 *
 *    Rev 1.0   Aug 29 2003 16:06:02   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:05:20   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:45:50   msg
 * Initial revision.
 *
 *    Rev 1.2   Dec 10 2001 17:23:38   blj
 * updated per codereview findings.
 * Resolution for POS SCR-237: Gift Receipt Feature
 *
 *    Rev 1.1   Nov 27 2001 18:07:48   blj
 * Updated to allow return by gift receipt
 * Resolution for POS SCR-237: Gift Receipt Feature
 *
 *    Rev 1.0   Sep 21 2001 11:25:10   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:46   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnItemCargoIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.gui.UIException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.timer.DefaultTimerModel;

/**
 * This site will show the return item screen which allows the user to enter an
 * item to be returned.
 * 
 * @version $Revision: /main/18 $
 */
public class EnterReturnItemSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 1629447688819501239L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/18 $";

    /**
     * Prompt respose spec
     */
    protected static final String PROMPT_SPEC = "SalePromptAndResponsePanelSpec";

    /**
     * Prompt message tag
     */
    protected static final String PROMPT_MESSAGE_TAG = "ReturnNonRetrievedItemPrompt";

    /**
     *  Prompt message default text
     */
    protected static final String PROMPT_MESSAGE = "Enter item(s) to be returned from receipt {0} and press Next.";

    /**
     * Show the UI screen for the enter item.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // get the UI manager
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc util = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ReturnItemCargo cargo = (ReturnItemCargo) bus.getCargo();

        // defensive clearing of search criteria
        cargo.setSearchCriteria(null);
        cargo.setEnableCancelItemNotFoundFromReturns(true);

        String screenName = POSUIManagerIfc.RETURN_ITEM_NO_RECEIPT;

        //get the non-kit header items from the original sale
        LineItemsModel beanModel = new LineItemsModel();

        boolean enableNextButton = false;
        if (cargo.getReturnSaleLineItems() != null)
        {
            if(cargo.getReturnSaleLineItems().length > 0)
            {
                enableNextButton = true;
            }
            beanModel.setLineItems(cargo.getReturnSaleLineItems());
            beanModel.getMoveHighlightToTop();
            beanModel.setTimerModel(new DefaultTimerModel(bus, true));
        }
        else
        {
            DefaultTimerModel model = new DefaultTimerModel(bus, false);
            model.setActionName(CommonLetterIfc.TIMEOUT);
            beanModel.setTimerModel(model);
        }

        boolean IMEIEnabled = util.getIMEIProperty();
        boolean serializationEnabled = util.getSerialisationProperty();
        String maxIMEILength = util.getIMEIFieldLengthProperty();

        PromptAndResponseModel promptModel = new PromptAndResponseModel();
        if(IMEIEnabled && serializationEnabled)
        {
            promptModel.setMaxLength(maxIMEILength);
            beanModel.setPromptAndResponseModel(promptModel);
        }

        // No Receipt case
        if (!cargo.haveReceipt())
        {
            screenName = POSUIManagerIfc.RETURN_ITEM_NO_RECEIPT;
            if (!cargo.getItemLookupLocaction().equals(ReturnItemCargoIfc.ItemLookupType.STORE))
            {

                if (cargo.getReturnData() != null)
                {
                    if (cargo.getReturnData().getSaleReturnLineItems().length > 0 && cargo.areReturnItemsAdded())
                    {
                        // Maybe user hit undo so we already have items in the ReturnData object.
                        cargo.setPLUItemID(null);
                        ui.showScreen(screenName, beanModel);
                        try
                        {
                            LineItemsModel tempBeanModel = (LineItemsModel) ui.getModel(screenName);
                            cargo.setMaxPLUItemIDLength(Integer.valueOf(tempBeanModel.getPromptAndResponseModel()
                                    .getMaxLength()));
                        }
                        catch (Exception e)
                        {
                            logger.warn(
                            "ShowSaleScreenSite.arrive() unable to get the maximum PLU item ID length", e);
                        }
                    }
                    else
                    {
                        // item came from return options screen
                        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
                    }
                }
                else
                {

                    // item came from return options screen
                    bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
                }
            }
            else
            {
                try
                {
                    // show the screen differently depending on whether we're
                    // updating the current screen or installing a new one
                    if (ui.getActiveScreenID() == screenName)
                    {
                        cargo.setPLUItemID(null);
                        ui.setModel(screenName, beanModel);
                    }
                    else
                    {
                        cargo.setPLUItemID(null);
                        ui.showScreen(screenName, beanModel);
                        try
                        {
                            LineItemsModel tempBeanModel = (LineItemsModel) ui.getModel(screenName);
                            cargo.setMaxPLUItemIDLength(Integer.valueOf(tempBeanModel.getPromptAndResponseModel()
                                    .getMaxLength()));
                        }
                        catch (Exception e)
                        {
                            logger.warn(
                            "ShowSaleScreenSite.arrive() unable to get the maximum PLU item ID length", e);
                        }
                    }
                }
                catch (UIException e)
                {
                    logger.warn("EnterReturmItemSite.arrive() unable to get the active screen ID");
                }
            }
         }
        else
        {
            screenName = POSUIManagerIfc.RETURN_ITEM_NON_RETRIEVED;
            // Append transaction id to prompt response
            UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
            String pattern =
                utility.retrieveText(
                    PROMPT_SPEC,
                    BundleConstantsIfc.RETURNS_BUNDLE_NAME,
                    PROMPT_MESSAGE_TAG,
                    PROMPT_MESSAGE);
            
            
            String message = "";
            if (cargo.getOriginalTransactionId()!=null)
            {
                message = LocaleUtilities.formatComplexMessage(pattern, cargo.getOriginalTransactionId().getTransactionIDString());
            }
            promptModel.setPromptText(message);

            NavigationButtonBeanModel globalNavigationModel = new NavigationButtonBeanModel();
            globalNavigationModel.setButtonEnabled(CommonLetterIfc.UNDO, false);
            globalNavigationModel.setButtonEnabled(CommonLetterIfc.CANCEL, true);            
            globalNavigationModel.setButtonEnabled(CommonLetterIfc.NEXT, enableNextButton);
            beanModel.setGlobalButtonBeanModel(globalNavigationModel);

            beanModel.setPromptAndResponseModel(promptModel);
            try
            {
                // show the screen differently depending on whether we're
                // updating the current screen or installing a new one
                if (ui.getActiveScreenID() == screenName)
                {
                    cargo.setPLUItemID(null);
                    ui.setModel(screenName, beanModel);
                }
                else
                {
                    cargo.setPLUItemID(null);
                    ui.showScreen(screenName, beanModel);
                    try
                    {
                        LineItemsModel tempBeanModel = (LineItemsModel) ui.getModel(screenName);
                        cargo.setMaxPLUItemIDLength(Integer.valueOf(tempBeanModel.getPromptAndResponseModel()
                                .getMaxLength()));
                    }
                    catch (Exception e)
                    {
                        logger.warn(
                        "ShowSaleScreenSite.arrive() unable to get the maximum PLU item ID length", e);
                    }
                }
            }
            catch (UIException e)
            {
                logger.warn("EnterReturnItemSite.arrive() unable to get the active screen ID");
            }
        }
    }
}
