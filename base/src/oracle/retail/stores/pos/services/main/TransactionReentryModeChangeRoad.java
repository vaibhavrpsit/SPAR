/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/main/TransactionReentryModeChangeRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:12 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:30:35 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:26:24 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:15:16 PM  Robert Pearse   
 * $
 * Revision 1.2  2004/03/26 21:18:20  cdb
 * @scr 4204 Removing Tabs.
 *
 * Revision 1.1  2004/03/24 23:23:55  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.main;

// foundation imports
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//--------------------------------------------------------------------------
/**
 * This road sets the function to override and the training request in the cargo.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//--------------------------------------------------------------------------
public class TransactionReentryModeChangeRoad extends PosLaneActionAdapter
{
    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
     * Set the function ID for Transaction Reentry.
     * 
     * @param bus
     *            Service Bus
     */
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        MainCargo cargo = (MainCargo) bus.getCargo();
        cargo.setAccessFunctionID(RoleFunctionIfc.REENTRY_ON_OFF);
    }

}
