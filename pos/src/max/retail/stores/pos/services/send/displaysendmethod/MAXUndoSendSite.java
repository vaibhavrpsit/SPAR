/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *	Rev 1.0	    May 04, 2017	    Kritica Agarwal 	GST Changes
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.send.displaysendmethod;

import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.send.address.SendCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

/**
 * Undo the send item operation.
 *
 * $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class MAXUndoSendSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -7380485279882282993L;

    /**
     * Arrive at the undoSend site, anything that was done in send should
     * be undone at this point.
     *
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        SendCargo cargo = (SendCargo) bus.getCargo();
        Letter letter = new Letter(CommonLetterIfc.UNDO);

        SaleReturnLineItemIfc[] lineItems = cargo.getLineItems();

        // Remove the send tax rules, so that the calculator uses normal tax rules
        if(lineItems != null)
        {
            for(int i=0; i<lineItems.length; i++)
            {
                lineItems[i].getItemPrice().getItemTax().setSendTaxRules(null);
                lineItems[i].setTaxMode(lineItems[i].getItemPrice().getItemTax().getOriginalTaxMode());
            }
        }
        if(cargo.getTransaction().getCaptureCustomer() != null)
        {
        	//Change for Rev 1.0 : Starts
        	if(cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc && !((MAXSaleReturnTransactionIfc)cargo.getTransaction()).isGstEnable()){
        	//Change for Rev 1.0 : Ends
            cargo.getTransaction().setCaptureCustomer(null);
        	}
            // reset customer only if no customer id
            if(cargo.getTransaction().getCustomer() != null &&
                    Util.isEmpty(cargo.getTransaction().getCustomer().getCustomerID()))
            {
            	//Change for Rev 1.0 : Starts
            	if(cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc && !((MAXSaleReturnTransactionIfc)cargo.getTransaction()).isGstEnable()){
            	//Change for Rev 1.0 : Ends
                cargo.getTransaction().setCustomer(null);
            	}
                // clear customer name in the status area
                POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
                ui.customerNameChanged("");
            }
        }
        bus.mail(letter, BusIfc.CURRENT);
    }
}
