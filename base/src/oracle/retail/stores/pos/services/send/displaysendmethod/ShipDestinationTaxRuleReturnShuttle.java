/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/displaysendmethod/ShipDestinationTaxRuleReturnShuttle.java /main/1 2012/10/22 15:36:20 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     10/19/12 - Refactor, using DestinationTaxRule station to get tax
*                        rules based on shipping/send destination postal code.
* yiqzhao     10/17/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.send.displaysendmethod;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.destinationtaxrule.DestinationTaxRuleCargo;
import oracle.retail.stores.pos.services.send.address.SendCargo;

/**
 * @author epd
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ShipDestinationTaxRuleReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    static final long serialVersionUID = 1L;

    /**
     customer main cargo
     **/
    protected DestinationTaxRuleCargo destinationTaxRuleCargo = null;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void load(BusIfc bus)
    {
        // retrieve cargo from the customer service
    	destinationTaxRuleCargo = (DestinationTaxRuleCargo)bus.getCargo();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void unload(BusIfc bus)
    {
        // retrieve cargo from the parent
    	SendCargo sendCargo = (SendCargo)bus.getCargo();

    	sendCargo.setDestinationTaxRule(destinationTaxRuleCargo.getTaxRulesVO());

    }
}