/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/SelectReturnItemsSite.java /main/20 2014/07/08 11:41:53 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/08/14 - refactor default timer model to default to 15
 *                         minutes timeout and be able to find parametermanager
 *                         from dispatcher
 *    mchellap  05/09/14 - Changes for MPOS return
 *    mkutiana  04/18/13 - displaying ordernumber if the transaction is an
 *                         OrderTransaction
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   10/25/10 - do not call setModel right after showScreen
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/26/10 - Fixed warning messages.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    mchellap  01/11/10 - Set prompt length to imei length
 *    abondala  01/03/10 - update header date
 *    mchellap  12/10/09 - Serialisation return without receipt changes
 *
 * ===========================================================================
 * $Log:
 *    9    360Commerce 1.8         5/28/2008 3:46:48 PM   Anil Rathore
 *         Updated to display ITEM_NOT_FOUND dialog. Changes reviewed by Dan.
 *    8    360Commerce 1.7         5/27/2008 7:37:28 PM   Anil Rathore
 *         Updated to display ITEM_NOT_FOUND dialog. Changes reviewed by Dan.
 *    7    360Commerce 1.6         4/4/2008 3:12:27 AM    Sujay Beesnalli
 *         Forward porting CR# 30354 from v12x. Added flags to determine
 *         highlighting of rows.
 *    6    360Commerce 1.5         3/25/2008 4:47:14 AM   Vikram Gopinath
 *    5    360Commerce 1.4         3/25/2008 4:45:17 AM   Vikram Gopinath CR
 *         #20052, ported changes from v12x. Transaction detail button is set
 *         to enable when the application is not running in training mode.
 *    5    I18N_P2    1.3.1.0     12/18/2007 3:09:14 PM  Sandy Gu        static
 *          text fix for POS
 *    4    360Commerce 1.3         1/25/2006 4:11:46 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:29:55 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:10 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:08 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     10/26/2005 13:41:09    Deepanshu       CR
 *         6111: Removed quotes from the final String constant
 *    3    360Commerce1.2         3/31/2005 15:29:55     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:25:10     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:14:08     Robert Pearse
 *
 *   Revision 1.17  2004/09/13 21:12:59  kll
 *   @scr 7186: toggle Trans. Detail button if Training Mode is On
 *
 *   Revision 1.16  2004/08/17 20:54:33  jdeleau
 *   @scr 6851 Change letter for timeouts to "Timeout" to make
 *   sure the application logsout instead of going back one site via undo.
 *
 *   Revision 1.15  2004/05/13 19:38:40  jdeleau
 *   @scr 4862 Support timeout for all screens in the return item flow.
 *
 *   Revision 1.14  2004/05/11 14:33:00  jlemieux
 *   @scr
 *   270 Fixed by adding a veto mechanism to the lifting of the GlassComponent. In particular, the GlassComponent lift is now vetoed when the scanner's scan queue contains 1 or more items and we are on a multiscan screen in POS. This effectively makes POS "prefer" to drain scan queues rather than service user input, which is what we want.
 *
 *   Revision 1.13  2004/03/15 20:17:54  epd
 *   @scr 3561 Code cleanup, bug fix
 *
 *   Revision 1.12  2004/03/05 21:46:58  epd
 *   @scr 3561 Updates to implement select highest price item
 *
 *   Revision 1.11  2004/03/04 20:52:46  epd
 *   @scr 3561 Returns.  Updates for highest price item functionality and code cleanup
 *
 *   Revision 1.10  2004/03/03 14:52:41  epd
 *   @scr 3561 Updated method names to more appropriately reflect what they were doing
 *
 *   Revision 1.9  2004/02/27 19:51:16  baa
 *   @scr 3561 Return enhancements
 *
 *   Revision 1.8  2004/02/27 01:43:29  baa
 *   @scr 3561 returns - selecting return items
 *
 *   Revision 1.7  2004/02/25 15:20:30  baa
 *   @scr 3561 Allow for selected items on blind return to be highlighted on the transaction detail screen
 *
 *   Revision 1.6  2004/02/24 22:08:14  baa
 *   @scr 3561 continue returns dev
 *
 *   Revision 1.5  2004/02/23 14:58:53  baa
 *   @scr 0 cleanup javadocs
 *
 *   Revision 1.4  2004/02/23 13:54:52  baa
 *   @scr 3561 Return Enhancements to support item size
 *
 *   Revision 1.3  2004/02/12 16:51:53  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:30  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.6   05 Feb 2004 23:31:44   baa
 * return multiple items
 *
 *    Rev 1.5   26 Jan 2004 00:14:06   baa
 * continue return development
 *
 *    Rev 1.4   Dec 30 2003 16:58:56   baa
 * cleanup for return feature
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 *
 *    Rev 1.3   Dec 29 2003 15:36:52   baa
 * return enhancements
 *
 *    Rev 1.2   Dec 17 2003 11:21:28   baa
 * return enhancements
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 *
 *    Rev 1.1   Sep 02 2003 14:23:04   sfl
 * Implement repeating algorithm during reading tax table based tax rules.
 * Resolution for POS SCR-3315: Implement Repeating Tax Table Algorithm
 *
 *    Rev 1.0   Aug 29 2003 16:06:30   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Jul 18 2003 16:20:20   sfl
 * Use standard Internationalization approach in the makeShorter method
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.2   03 Oct 2002 17:15:38   sfl
 * Expanded the CurrencyIfc precision during calculation
 * and later convert it back to 2-digit for display.
 *
 *    Rev 1.1   Aug 16 2002 09:51:00   jriggins
 * Replaced concat of customer first and last name to retrieval of CustomerAddressSpec.CustomerName from customerText bundle.
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:04:36   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:46:48   msg
 * Initial revision.
 *
 *    Rev 1.6   10 Mar 2002 11:48:14   pjf
 * Maintain kit inventory at header level.
 * Resolution for POS SCR-1444: Selling then returning a kit does not upadate the inventory count
 * Resolution for POS SCR-1503: When all kit items are returned and attempt to retrieve trans no error displays
 *
 *    Rev 1.5   Feb 05 2002 16:43:26   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.4   30 Jan 2002 18:16:38   cir
 * Made the return quantity available 0 for canceled order items
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.3   10 Dec 2001 13:56:24   jbp
 * removed unnec. imports
 * Resolution for POS SCR-418: Return Updates
 *
 *    Rev 1.1   08 Nov 2001 14:29:40   pjf
 * Modified to use SaleReturnTransaction.getLineItemsExcluding()
 * Resolution for POS SCR-8: Item Kits
 *
 *    Rev 1.0   Sep 21 2001 11:25:36   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returntransaction;

import java.util.List;

import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.gui.UIException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.timer.DefaultTimerModel;

/**
 * Get the items selected on the UI.
 */
public class SelectReturnItemsSite extends DisplayTransactionDetailsSite
{
    /** serialVersionUID */
    private static final long serialVersionUID = -6131032280779409798L;

    /**
     *  prompt spec
     */
    protected static final String PROMPT_SPEC = "SalePromptAndResponsePanelSpec";

    /**
     * prompt message tag
     */
    protected static final String PROMPT_MESSAGE_TAG = "ReturnSelectItemPrompt";

    /**
     *  Default prompt message
     */
    protected static final String PROMPT_MESSAGE = "Enter item(s) to be returned from receipt {0} and press Next.";


    /**
     * Get the items selected on the UI.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // get uiManager and Cargo from bus
        ReturnTransactionCargo cargo = (ReturnTransactionCargo) bus.getCargo();
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        boolean IMEIEnabled = utility.getIMEIProperty();
        boolean serializationEnabled = utility.getSerialisationProperty();
        String maxIMEILength = utility.getIMEIFieldLengthProperty();
        NavigationButtonBeanModel localModel = new NavigationButtonBeanModel();
        SaleReturnTransactionIfc saleTransaction = cargo.getOriginalTransaction();

        //get the non-kit header items from the original sale
        LineItemsModel beanModel = new LineItemsModel();
        if (cargo.getLineItemsToDisplayList() != null)
        {
            beanModel.setLineItems(cargo.getLineItemsToDisplay());
            beanModel.setSelectedRows(cargo.getSelectedIndexes());
            beanModel.setTimerModel(new DefaultTimerModel(bus, true));
        }
        else
        {
            // show blind return screen
            cargo.setLineItemsToDisplay(null);
            DefaultTimerModel timerModel = new DefaultTimerModel(bus, false);
            timerModel.setActionName(CommonLetterIfc.TIMEOUT);
            beanModel.setTimerModel(timerModel);

            // Set the training buttons enabled state based on
            // flags from the cargo
            boolean trainingModeOn =
                cargo.getRegister().getWorkstation().isTrainingMode();
            if(trainingModeOn)
            {
                localModel.setButtonEnabled(CommonActionsIfc.DETAIL, false);
                beanModel.setLocalButtonBeanModel(localModel);
            }
            else
            {
                localModel.setButtonEnabled(CommonActionsIfc.DETAIL, true);
                beanModel.setLocalButtonBeanModel(localModel);
            }
        }

        // Append transaction id to prompt response
        String pattern =
            utility.retrieveText(
                PROMPT_SPEC,
                BundleConstantsIfc.RETURNS_BUNDLE_NAME,
                PROMPT_MESSAGE_TAG,
                PROMPT_MESSAGE);

        String receiptNumber = "";
        if(saleTransaction instanceof OrderTransactionIfc)
        {
            receiptNumber = saleTransaction.getOrderID();
        }
        else
        {
            receiptNumber = saleTransaction.getTransactionID();
        }
        
        String message = LocaleUtilities.formatComplexMessage(pattern, receiptNumber);
        PromptAndResponseModel promptModel = new PromptAndResponseModel();
        promptModel.setPromptText(message);

        // If IMEI scan is enabled then set the prompt max length to imei length
        if(IMEIEnabled && serializationEnabled)
        {
            promptModel.setMaxLength(maxIMEILength);           
        }

        if (promptModel.getMaxLength() != null)
        {
            try
            {
                cargo.setMaxPLUItemIDLength(Integer.valueOf(promptModel.getMaxLength()));
            }
            catch (NumberFormatException ex)
            {
               // Do nothing, default length will be used.
            }
        }
        beanModel.setPromptAndResponseModel(promptModel);

        beanModel.setDisplayTransDetailScreen(false);

        // setup the function access point for transaction details in case they are
        // requested
        cargo.setAccessFunctionID(RoleFunctionIfc.DISPLAY_TRANSACTION_DETAILS);

        //display the screen
        String screenName = POSUIManagerIfc.RETURN_SELECT_ITEM;
        try
        {
            // show the screen differently depending on whether we're
            // updating the current screen or installing a new one
            if (ui.getActiveScreenID() == screenName)
            {
                ui.setModel(screenName, beanModel);
            }
            else
            {
                ui.showScreen(screenName, beanModel);
            }
        }
        catch (UIException e)
        {
            logger.warn("SelectReturnItemsSite.arrive() unable to get the active screen ID");
        }
    }
    //----------------------------------------------------------------------
    /**
     This method is executed before leaving this site.
     <P>
     @param  bus     Service Bus
     **/
    //----------------------------------------------------------------------
    public void depart(BusIfc bus)
    {
        ReturnTransactionCargo cargo = (ReturnTransactionCargo) bus.getCargo();
        if (bus.getCurrentLetter().getName().equals(CommonLetterIfc.DETAIL))
        {
            //Get list of any previously selected return items.
            List<SaleReturnLineItemIfc> returnItems = cargo.getLineItemsToDisplayList();

            if (returnItems != null)
            {
                for (SaleReturnLineItemIfc item: returnItems)
                {
                    cargo.markItemSelected(item);
                    cargo.setCurrentItem(cargo.getCurrentItem()+1);
                }
            }

        }
    }
}
