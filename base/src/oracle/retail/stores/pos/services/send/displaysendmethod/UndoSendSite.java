/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/displaysendmethod/UndoSendSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:03 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   05/11/10 - convert Base64 from axis
 *    abondala  01/03/10 - update header date
 *    acadar    09/28/09 - merge from 13.1.x
 *    acadar    09/28/09 - XbranchMerge acadar_bug-8913867 from
 *                         rgbustores_13.1x_branch
 *    acadar    09/17/09 - clear the customer in the transaction if no customer
 *                         id
 *    acadar    09/17/09 - clear up customer information
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:38 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:31 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:22 PM  Robert Pearse
 *
 *   Revision 1.2  2004/07/31 16:32:14  jdeleau
 *   @scr 6632 Make sure send tax always overrides other tax ruels
 *
 *   Revision 1.1  2004/07/30 20:01:57  jdeleau
 *   @scr 6630 When hitting escape on a send item transaction, reset the
 *   tax back to its original value.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.send.displaysendmethod;

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
public class UndoSendSite extends PosSiteActionAdapter
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
            cargo.getTransaction().setCaptureCustomer(null);

            // reset customer only if no customer id
            if(cargo.getTransaction().getCustomer() != null &&
                    Util.isEmpty(cargo.getTransaction().getCustomer().getCustomerID()))
            {

                cargo.getTransaction().setCustomer(null);
                // clear customer name in the status area
                POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
                ui.customerNameChanged("");
            }
        }
        bus.mail(letter, BusIfc.CURRENT);
    }
}
