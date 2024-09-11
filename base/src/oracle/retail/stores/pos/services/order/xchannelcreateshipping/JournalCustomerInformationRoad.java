/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreateshipping/JournalCustomerInformationRoad.java /main/1 2012/06/21 12:42:40 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     06/04/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.order.xchannelcreateshipping;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;

/**
 * This road journals the linked customer's information.
 *
 */
public class JournalCustomerInformationRoad extends LaneActionAdapter
{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1218735473060030847L;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
    	XChannelShippingCargo cargo = (XChannelShippingCargo)bus.getCargo();
        RetailTransactionIfc transaction = cargo.getTransaction();
        CustomerIfc customer = transaction.getCustomer();
        if ( customer != null )
        {
        	JournalManagerIfc journalManager = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        	CustomerUtilities.journalCustomerInformation(customer, journalManager, transaction);
        }
    }

}