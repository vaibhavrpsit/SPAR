/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnfindtrans/crossborderreturn/CBRReturnShuttle.java /main/1 2012/04/05 10:22:50 rsnayak Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rsnayak   04/02/12 - Cross Border Return
 * ===========================================================================
 *  
 */
package oracle.retail.stores.pos.services.returns.returnfindtrans.crossborderreturn;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;

public class CBRReturnShuttle extends FinancialCargoShuttle
{

    private static final long serialVersionUID = -3015400544600020769L;

    public void load(BusIfc bus)
    {
        // Perform FinancialCargoShuttle load
        super.load(bus);
     
    }

    public void unload(BusIfc bus)
    {
        // Perform FinancialCargoShuttle unload
        super.unload(bus);

    }
}
