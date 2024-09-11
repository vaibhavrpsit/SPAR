/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/TenderLimitOverrideReturnShuttle.java /main/12 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:25 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:00 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:54 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.1  2004/04/02 20:56:24  epd
 *   @scr 4263 Updates to accommodate new tender limit override station
 *
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

/**
 * @author epd
 */
public class TenderLimitOverrideReturnShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -9167740005410137748L;


    protected TenderCargo childCargo;
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void load(BusIfc bus)
    {
        childCargo = (TenderCargo)bus.getCargo();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void unload(BusIfc bus)
    {
        TenderCargo callingCargo = (TenderCargo)bus.getCargo();
        callingCargo.setCurrentTransactionADO(childCargo.getCurrentTransactionADO());
    }

}
