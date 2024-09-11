/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/processordersend/CheckShippingMethodSite.java /main/1 2012/10/22 15:36:17 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     10/19/12 - Refactor, using DestinationTaxRule statue to get new
*                        tax rule from shipping/send destination postal code.
* yiqzhao     10/17/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.externalorder.processordersend;


import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 *
 * Retrieves the tax rules when there is shipping
 * done from the warehouse
 */
public class CheckShippingMethodSite extends PosSiteActionAdapter
{
    /**
     *  Serial version UID
     */
    private static final long serialVersionUID = 1L;

    /**
    * The system searches for a Geo code associated with the zip code in the shipping address.
    */
    public void arrive(BusIfc bus)
    {
        Letter letter = new Letter(CommonLetterIfc.NEXT);
        ProcessOrderSendCargo cargo = (ProcessOrderSendCargo) bus.getCargo();

        if(cargo.getShippingMethod() == null)
        {
            letter = new Letter(CommonLetterIfc.DONE);
        }

        bus.mail(letter, BusIfc.CURRENT);
    }


}
