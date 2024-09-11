/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/CaptureCustomerInfoReturnShuttle.java /main/12 2012/03/26 13:08:27 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   03/21/12 - update CustomerIfc to use collections generics (i.e.
 *                         List<AddressIfc>) and remove old deprecated methods
 *                         and references to them
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:27:20 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:19:59 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:09:48 PM  Robert Pearse   
 * $
 * Revision 1.4  2004/09/23 00:07:10  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.3  2004/06/21 13:13:55  lzhao
 * @scr 4670: cleanup
 *
 * Revision 1.2  2004/06/19 14:06:40  lzhao
 * @scr 4670: Integrate with capture customer
 *
 * Revision 1.1  2004/06/02 19:06:51  lzhao
 * @scr 4670: add ability to delete send items, modify shipping and display shipping method.
 *
 * Revision 1.1  2004/05/26 16:37:47  lzhao
 * @scr 4670: add capture customer and bill addr. same as shipping for send
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.send;

import oracle.retail.stores.pos.services.tender.capturecustomerinfo.CaptureCustomerInfoCargo;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;
//--------------------------------------------------------------------------
/**
 * Returns from CaptureCustomerInfo to Send.
 * 
 * @version $Revision: /main/12 $
 */
//--------------------------------------------------------------------------
public class CaptureCustomerInfoReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 4470071825930004222L;

    /**
     * The logger to which log messages will be sent.
     */
    public static final String SHUTTLENAME = "CaptureCustomerInfoReturnShuttle";

    // the customer cargo
    protected CaptureCustomerInfoCargo customerCargo = null;
    //----------------------------------------------------------------------
    /**
     * Gets a copy of CustomerCargo for use in unload().
     * <p>
     * 
     * @param bus
     *            the bus being loaded
     */
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        customerCargo = (CaptureCustomerInfoCargo) bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
     * Links the Customer to the Transaction and copies Customer info.
     * <p>
     * 
     * @param bus
     *            the bus being unloaded
     */
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        CustomerIfc customer = customerCargo.getCustomer();

        ItemCargo itemCargo = (ItemCargo) bus.getCargo();
        if (customer != null)
        {
            if ( customer.getAddressList().size() > 0 )
            {    
                //set address type is home address, It will be used at
                //CustomerUtilities.getCustomerInfo() DisplayShippingAddressSite
                ((AddressIfc)customer.getAddressList().get(0)).setAddressType(0);
            }
            itemCargo.setCustomer(customer);
            
            itemCargo.getTransaction().setCaptureCustomer(customerCargo.getTransaction().getCaptureCustomer());
        }
    }
}
