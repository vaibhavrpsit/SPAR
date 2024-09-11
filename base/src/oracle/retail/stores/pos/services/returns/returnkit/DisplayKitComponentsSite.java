/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnkit/DisplayKitComponentsSite.java /main/13 2012/09/12 11:57:20 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   08/28/12 - Merge project Echo (MPOS) into trunk.
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    cgreene   05/26/10 - convert to oracle packaging
 *    jswan     01/11/10 - Modified to fix issue with return for lowest price
 *                         in X days with kit items.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:48 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:03 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:39 PM  Robert Pearse
 *
 *   Revision 1.4  2004/05/13 19:38:40  jdeleau
 *   @scr 4862 Support timeout for all screens in the return item flow.
 *
 *   Revision 1.3  2004/03/19 23:04:11  epd
 *   @scr 3561 Updated comments and strategy for initializing SaleReturnLineItems
 *
 *   Revision 1.2  2004/03/12 19:36:48  epd
 *   @scr 3561 Updates for handling kit items in non-retrieved no receipt returns
 *
 *   Revision 1.1  2004/03/11 23:39:48  epd
 *   @scr 3561 New work to accommodate returning kit items
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnkit;

import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.pos.ui.timer.DefaultTimerModel;

/**
 * @author epd
 */
@SuppressWarnings("serial")
public class DisplayKitComponentsSite extends PosSiteActionAdapter
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // create and initialize a transaction
        // IMPORTANT: this transaction is temporary.  DO NOT INITIALIZE with a new
        // transaction ID.  We intentionally do NOT do that.
        // We only are using this temp txn to access business logic that initializes
        // a SaleReturnLineItem
        SaleReturnTransactionIfc tempTxn = DomainGateway.getFactory().getSaleReturnTransactionInstance();
        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc) bus.getManager(TransactionUtilityManagerIfc.TYPE);
        tempTxn.setTransactionTax(utility.getInitialTransactionTax());

        // before we can display items, we need sale return line items, soooo, add
        // add the kit plu to the transaction.  That's the easiest way.  Then we can
        // remove the whole kit and add each component item as a return item later.
        ReturnKitCargo cargo = (ReturnKitCargo)bus.getCargo();
        tempTxn.addPLUItem(cargo.getPLUItem(), BigDecimalConstants.ONE_AMOUNT);

        // get the first (and only) line item (the kit header) and get the line items from it
        AbstractTransactionLineItemIfc[] lineItems = tempTxn.getLineItems();
        KitHeaderLineItemIfc header = (KitHeaderLineItemIfc)lineItems[0];
        SaleReturnLineItemIfc[] components = header.getKitComponentLineItemArray();
        for(SaleReturnLineItemIfc kitItem: components)
        {
            setupReturnPrice(kitItem, cargo);
        }
        cargo.setKitComponents(components);

        // set up UI
        LineItemsModel model = new LineItemsModel();
        model.setLineItems(components);
        model.setTimerModel(new DefaultTimerModel(bus, true));
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.KIT_COMPONENTS, model);
    }

    /*
     * This method supports changes for non-receipted returns -- lowest price in N days
     */
    private void setupReturnPrice(SaleReturnLineItemIfc component, ReturnKitCargo cargo)
    {
        int pricingGroupID = -1;
        if (cargo != null &&
            cargo.getTransaction() != null &&
            cargo.getTransaction().getCustomer() != null &&
            cargo.getTransaction().getCustomer().getPricingGroupID() != null)
        {
            pricingGroupID = cargo.getTransaction().getCustomer().getPricingGroupID();
        }
        component.getItemPrice().setSellingPrice(component.getPLUItem().getReturnPrice(pricingGroupID));
        component.getItemPrice().calculateItemTotal();
    }
}