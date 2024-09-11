/* ===========================================================================
* Copyright (c) 2001, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/CheckForExternalOrderSite.java /main/4 2014/03/11 17:13:56 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/11/14 - add support for returning ASA ordered items
 *    mjwallac  04/24/12 - Fixes for Fortify redundant null check
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     06/01/10 - Checked in for refresh to latest lable.
 *    jswan     05/27/10 - Added for external order feature.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returntransaction;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.returns.returncommon.AbstractFindTransactionCargo;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnUtilities;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Checks for external order processing.
 */
public class CheckForExternalOrderSite extends PosSiteActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = 8631045206289033939L;

    public final static String RETURN_UNAVAILABLE_EXT_ORDER = "ReturnUnavailableExtOrder";
    public final static String LETTER_WEB_MANAGED_ORDER = "WebManagedOrder";

    /**
     * Check for external order processing and mail letter.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ReturnTransactionCargo cargo = (ReturnTransactionCargo) bus.getCargo();
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        SaleReturnTransactionIfc saleTransaction = cargo.getOriginalTransaction();

        // if there is not a customer linked to the current transaction
        // and there is a customer linked to the original transaction
        // link the customer of the original transaction.
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        if ((cargo.getTransaction() != null && cargo.getTransaction().getCustomer() == null)
            && (saleTransaction != null && saleTransaction.getCustomer() != null))
        {
            CustomerIfc customer = saleTransaction.getCustomer();
            ReturnUtilities.displayLinkedCustomer(ui, utility, customer);
        }

        //get the non-kit header items from the original sale
        SaleReturnLineItemIfc[] itemsArray = ReturnUtilities.processNonKitCodeHeaderItems(utility, saleTransaction);
        cargo.setOriginalSaleLineItems(itemsArray);
        cargo.setTransactionDetailsDisplayed(false);

        //save the header items for later comparison
        //if all component items for a kit are returned,
        //their header item must be added to the transaction to increment the
        //header inventory count
        if (saleTransaction != null)
        {        
            cargo.setKitHeaderItems(saleTransaction.getKitHeaderLineItems());
    
            // Before display taxTotals, need to convert the longer precision
            // calculated total tax amount back to shorter precision tax total
            // amount for UI display.
            saleTransaction.getTransactionTotals().setTaxTotal(saleTransaction.getTransactionTotals().getTaxTotalUI());
        }

        // Determine whether to process items as external order items.
        String letter = null;
        if (cargo.isExternalOrder())
        {
            letter = "ExternalOrder";
        }
        else
        {
            letter = handleNotExternalOrder(bus, cargo);
        }
        if (letter != null)
        {
            bus.mail(letter);
        }
    }

    /**
     * In this case, the cargo was not flagged by
     * {@link AbstractFindTransactionCargo#isExternalOrder()}. Returns {@link #LETTER_WEB_MANAGED_ORDER}
     * if the original transaction is web managed. Returns "Next" if there is no
     * order. Displays a dialog if there is an external order id. 
     * 
     * @param bus
     * @param cargo
     * @param ui
     */
    protected String handleNotExternalOrder(BusIfc bus, ReturnTransactionCargo cargo)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        if (cargo.getOriginalTransaction().isWebManagedOrder())
        {
            return LETTER_WEB_MANAGED_ORDER;
        }
        else if (!Util.isEmpty(cargo.getOriginalTransaction().getExternalOrderID()))
        {
            // If the transaction has an external order (from Siebel), the user
            // must begin the return process in the order entry system.
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID(RETURN_UNAVAILABLE_EXT_ORDER);
            model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            model.setButtonLetter(0, CommonLetterIfc.FAILURE);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
            return null;
        }
        else
        {
            return CommonLetterIfc.NEXT;
        }
    }

}
