/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnkit/ProcessSelectedKitComponentsSite.java /rgbustores_13.4x_generic_branch/1 2011/07/07 12:20:07 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    jswan     01/11/10 - Deprecated method due to code review.
 *    jswan     01/11/10 - Modified to fix issue with return for lowest price
 *                         in X days with kit items.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:31 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:25 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:27 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/03/22 22:39:48  epd
 *   @scr 3561 Refactored cargo to get rid of itemQuantities attribute.  Added it to ReturnItemIfc instead.  Refactored to reduce code complexity and confusion.
 *
 *   Revision 1.1  2004/03/12 19:36:48  epd
 *   @scr 3561 Updates for handling kit items in non-retrieved no receipt returns
 *
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnkit;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.KitComponentLineItemIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;


/**
 * Processes the selected kit components
 */
public class ProcessSelectedKitComponentsSite extends PosSiteActionAdapter
{
    
    /**  */
    private static final long serialVersionUID = 3036423617549545618L;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        LineItemsModel model = (LineItemsModel)ui.getModel(POSUIManagerIfc.KIT_COMPONENTS);
        
        // get the component items and selected rows from the UI
        ReturnKitCargo cargo = (ReturnKitCargo)bus.getCargo();
        KitComponentLineItemIfc[] components = (KitComponentLineItemIfc[])cargo.getKitComponents();
        int[] selectedRows = model.getSelectedRows();
        
        // add each selected item as a return item
        for (int i = 0; i < selectedRows.length; i++)
        {
            int selectedRow = selectedRows[i];
            KitComponentLineItemIfc component = components[selectedRow];
            
            ReturnItemIfc returnItem = setupReturnItem(component, cargo);
            // add return item
            cargo.setCurrentItem(cargo.getCurrentItem()+1);
            cargo.addReturnItem(returnItem);
            // add line item
            cargo.addReturnSaleLineItem(component);             
        }
        
        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
    }
    
    /**
     * Modifications to this method supports changes for non-receipted returns -- lowest price in N days
     * @param component 
     * @deprecated in version 13.2
     */
    protected ReturnItemIfc setupReturnItem(KitComponentLineItemIfc component)
    {
        return setupReturnItem(component, null);
    }
    
    /**
     * Modifications to this method supports changes for non-receipted returns -- lowest price in N days
     * @param component 
     * @param cargo
     */
    protected ReturnItemIfc setupReturnItem(KitComponentLineItemIfc component, ReturnKitCargo cargo)
    {
        
        ReturnItemIfc returnItem = DomainGateway.getFactory().getReturnItemInstance();
        
        int pricingGroupID = -1;
        if (cargo != null &&
            cargo.getTransaction() != null && 
            cargo.getTransaction().getCustomer() != null &&
            cargo.getTransaction().getCustomer().getPricingGroupID() != null)
        {
            pricingGroupID = cargo.getTransaction().getCustomer().getPricingGroupID();
        }
        returnItem.setPrice(component.getPLUItem().getReturnPrice(pricingGroupID));
        returnItem.setEntryMethod(EntryMethod.Manual);
        returnItem.setItemQuantity(component.getQuantityReturnable());
        
        return returnItem;
    }
}
