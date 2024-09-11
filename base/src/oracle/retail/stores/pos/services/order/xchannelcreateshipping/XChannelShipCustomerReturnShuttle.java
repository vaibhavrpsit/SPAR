/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreateshipping/XChannelShipCustomerReturnShuttle.java /main/3 2013/03/05 14:03:17 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     03/05/13 - if a customer is linked in Shipping, set the linked
*                        customer for the transaction.
* blarsen     08/28/12 - Merge project Echo (MPOS) into trunk.
* yiqzhao     06/05/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.order.xchannelcreateshipping;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;

/**
 * @author epd
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class XChannelShipCustomerReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 1L;

    /**
     customer main cargo
     **/
    protected CustomerMainCargo customerMainCargo = null;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void load(BusIfc bus)
    {
        // retrieve cargo from the customer service
        customerMainCargo = (CustomerMainCargo)bus.getCargo();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void unload(BusIfc bus)
    {
        CustomerIfc customer = customerMainCargo.getCustomer();

        // retrieve cargo from the parent
        XChannelShippingCargo shipCargo = (XChannelShippingCargo)bus.getCargo();
        shipCargo.setCustomer(customer);
        
        // get the transaction from cargo
        SaleReturnTransactionIfc txn = (SaleReturnTransactionIfc)shipCargo.getTransaction();

        // journal customer tour exit
        if(txn != null)
        {
            if ( customerMainCargo.isLink() && customer != null )
            {
                txn.linkCustomer(customer);
                
                // journal the customer pricing after customer has been linked
                // to the transaction in order to pickup the correct pricing.
                CustomerUtilities.journalCustomerPricing(bus, txn, customer.getPricingGroupID());
            }

            CustomerUtilities.journalCustomerExit(bus, txn.getCashier().getEmployeeID(),
                                                 txn.getTransactionID());
        }

    }
}
