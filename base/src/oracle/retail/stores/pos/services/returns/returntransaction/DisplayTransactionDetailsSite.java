/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/DisplayTransactionDetailsSite.java /main/23 2013/04/24 11:41:29 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  04/24/13 - Displaying order id instead of txn id in prompt and
 *                         response for order transactions
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    mjwallac  04/25/12 - Fixes for Fortify redundant null check, take2
 *    cgreene   10/25/10 - do not call setModel right after showScreen
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/26/10 - Fixed warning messages.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    mchellap  01/11/10 - Set prompt length to imei length
 *    abondala  01/03/10 - update header date
 *    aphulamb  12/17/08 - bug fixing of PDO
 *    aphulamb  12/10/08 - returns functionality changes for greying out
 *                         buttons
 *    acadar    11/05/08 - merged to tip
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/4/2008 3:09:23 AM    Sujay Beesnalli
 *         Forward porting CR# 30354 from v12x. Added flags to determine
 *         highlighting of rows.
 *    4    360Commerce 1.3         1/25/2006 4:10:58 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:27:50 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:06 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:41 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     10/26/2005 13:36:09    Deepanshu       CR
 *         6110: Removed double quotes from the final String variable.
 *    3    360Commerce1.2         3/31/2005 15:27:50     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:21:06     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:10:41     Robert Pearse
 *
 *   Revision 1.13  2004/08/17 20:54:33  jdeleau
 *   @scr 6851 Change letter for timeouts to "Timeout" to make
 *   sure the application logsout instead of going back one site via undo.
 *
 *   Revision 1.12  2004/05/13 19:38:40  jdeleau
 *   @scr 4862 Support timeout for all screens in the return item flow.
 *
 *   Revision 1.11  2004/05/11 14:33:00  jlemieux
 *   @scr
 *   270 Fixed by adding a veto mechanism to the lifting of the GlassComponent. In particular, the GlassComponent lift is now vetoed when the scanner's scan queue contains 1 or more items and we are on a multiscan screen in POS. This effectively makes POS "prefer" to drain scan queues rather than service user input, which is what we want.
 *
 *   Revision 1.10  2004/03/25 15:07:16  baa
 *   @scr 3561 returns bug fixes
 *
 *   Revision 1.9  2004/03/10 14:16:47  baa
 *   @scr 0 fix javadoc warnings
 *
 *   Revision 1.8  2004/03/05 21:46:58  epd
 *   @scr 3561 Updates to implement select highest price item
 *
 *   Revision 1.7  2004/03/03 14:52:41  epd
 *   @scr 3561 Updated method names to more appropriately reflect what they were doing
 *
 *   Revision 1.6  2004/02/27 19:51:16  baa
 *   @scr 3561 Return enhancements
 *
 *   Revision 1.5  2004/02/27 01:43:29  baa
 *   @scr 3561 returns - selecting return items
 *
 *   Revision 1.4  2004/02/23 14:58:53  baa
 *   @scr 0 cleanup javadocs
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
 *    Rev 1.4   05 Feb 2004 23:31:38   baa
 * return multiple items
 *
 *    Rev 1.3   26 Jan 2004 00:13:58   baa
 * continue return development
 *
 *    Rev 1.2   Dec 30 2003 16:58:52   baa
 * cleanup for return feature
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 *
 *    Rev 1.1   Dec 29 2003 15:36:40   baa
 * return enhancements
 *
 *    Rev 1.0   Dec 17 2003 11:42:08   baa
 * Initial revision.
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

import java.util.ArrayList;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.transaction.OrderTransaction;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.gui.UIException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.TotalsBeanModel;
import oracle.retail.stores.pos.ui.timer.DefaultTimerModel;

//--------------------------------------------------------------------------
/**
    Get the items selected on the UI.
    <p>
    @version $Revision: /main/23 $
**/
//--------------------------------------------------------------------------
public class DisplayTransactionDetailsSite extends PosSiteActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = -7304709692319512104L;

    /**
     * Prompt respose spec
     */
    protected static final String PROMPT_SPEC = "SalePromptAndResponsePanelSpec";

    /**
     * Prompt message tag
     */
    protected static final String PROMPT_MESSAGE_TAG = "ReturnTransactionDetailsPrompt";

    /**
     *  Prompt message default text
     */
    protected static final String PROMPT_MESSAGE = "Enter or select item(s) to be returned from receipt {0} and press Next.";

    //----------------------------------------------------------------------
    /**
       Get the items selected on the UI.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // get uiManager and Cargo from bus
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ReturnTransactionCargo cargo = (ReturnTransactionCargo) bus.getCargo();
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        boolean IMEIEnabled = utility.getIMEIProperty();
        boolean serializationEnabled = utility.getSerialisationProperty();
        String maxIMEILength = utility.getIMEIFieldLengthProperty();
        boolean onHandFlag = false;
        SaleReturnLineItemIfc[] saleItemArray = null;

        // get original sale transaction
        SaleReturnTransactionIfc saleTransaction = cargo.getOriginalTransaction();

        // if there is not a customer linked to the current transaction
        // and there is a customer linked to the original transaction
        // link the customer of the original transaction.
        if ((cargo.getTransaction() != null && cargo.getTransaction().getCustomer() == null)
             && (saleTransaction != null && saleTransaction.getCustomer() != null))
        {
           CustomerIfc customer =  saleTransaction.getCustomer();
           ReturnUtilities.displayLinkedCustomer(ui, utility, customer);
        }

        //get the non-kit header items from the original sale
        SaleReturnLineItemIfc[] itemsArray = ReturnUtilities.processNonKitCodeHeaderItems(utility, saleTransaction);
        OrderTransaction orderTransaction = null;
        int orderType = 0;
        int transactionType = 0;
        if (cargo.getOriginalTransaction() instanceof OrderTransaction)
        {
            orderTransaction = (OrderTransaction)cargo.getOriginalTransaction();
            orderType = orderTransaction.getOrderType();
            transactionType = orderTransaction.getTransactionType();
            if (orderType == OrderConstantsIfc.ORDER_TYPE_ON_HAND
                    && transactionType == TransactionIfc.TYPE_ORDER_INITIATE)
            {
                onHandFlag = true;
                ArrayList<SaleReturnLineItemIfc> listOfItems = new ArrayList<SaleReturnLineItemIfc>();
                for (int i = 0; i < itemsArray.length; i++)
                {
                    if (itemsArray[i].getOrderItemStatus().getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_SALE)
                    {
                        listOfItems.add(itemsArray[i]);
                    }
                }
                saleItemArray = new SaleReturnLineItemIfc[listOfItems.size()];
                if (listOfItems.contains(null))
                {
                    listOfItems.remove(null);
                }
                listOfItems.remove(null);
                listOfItems.toArray(saleItemArray);
            }

        }
        if (onHandFlag)
        {
            cargo.setOriginalSaleLineItems(saleItemArray);
        }
        else
        {
            cargo.setOriginalSaleLineItems(itemsArray);
        }

        cargo.setTransactionDetailsDisplayed(true);
        //save the header items for later comparison
        //if all component items for a kit are returned,
        //their header item must be added to the transaction to increment the
        //header inventory count
        if (saleTransaction != null)
        {
            cargo.setKitHeaderItems(saleTransaction.getKitHeaderLineItems());
        }
        
        //initialize bean model
        LineItemsModel beanModel = new LineItemsModel();
        TotalsBeanModel tbm = new TotalsBeanModel();

        // Before display taxTotals, need to convert the longer precision
        // calculated total tax amount back to shorter precision tax total
        // amount for UI display.
        saleTransaction.getTransactionTotals().setTaxTotal(saleTransaction.getTransactionTotals().getTaxTotalUI());

        // Now, display on the UI.
        tbm.setTotals(saleTransaction.getTransactionTotals());

        beanModel.setTotalsBeanModel(tbm);
        if (onHandFlag)
        {
            beanModel.setLineItems(saleItemArray);
        }
        else
        {
            beanModel.setLineItems(itemsArray);
        }
        beanModel.setSelectedRows(cargo.getSelectedIndexes());
        beanModel.setMoveHighlightToTop(false);

        beanModel.setHighlightItem(cargo.isHighlightItem());
        beanModel.setDisplayTransDetailScreen(true);

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

        if(IMEIEnabled && serializationEnabled)
        {
            promptModel.setMaxLength(maxIMEILength);
        }

        beanModel.setPromptAndResponseModel(promptModel);

        //display the screen
        String screenName = POSUIManagerIfc.RETURN_TRANSACTION_DETAILS;
        try
        {
            // show the screen differently depending on whether we're
            // updating the current screen or installing a new one
            if (ui.getActiveScreenID() == screenName)
            {
                // Set the timeout details
                DefaultTimerModel timerModel = new DefaultTimerModel(bus, false);
                timerModel.setActionName(CommonLetterIfc.TIMEOUT);
                beanModel.setTimerModel(timerModel);
                ui.setModel(screenName, beanModel);
            }
            else
            {
                // Set the timeout details
                beanModel.setTimerModel(new DefaultTimerModel(bus, true));
                ui.showScreen(screenName, beanModel);
            }
        }
        catch (UIException e)
        {
            logger.warn("DisplayTransactionDetailsSite.arrive() unable to get the active screen ID");
        }
    }



    /**
     * Reset selected indexes if leaving site with undo letter
     * @param bus
     *
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#depart(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void depart(BusIfc bus)
    {
    	ReturnTransactionCargo cargo = (ReturnTransactionCargo)bus.getCargo();

    	// if escaping out of details screen, clear list of selected detail items
        if (bus.getCurrentLetter().getName().equals(CommonLetterIfc.UNDO))
        {
            cargo.setSelectedIndexes(null);
            cargo.setHighlightItem(false);
            cargo.setTransDetailFreshVisit(true);
        }
        else
        {
        	//used for clearing/refreshing the selections
        	cargo.setHighlightItem(true);
        	cargo.setTransDetailFreshVisit(false);
        }
    }
}
