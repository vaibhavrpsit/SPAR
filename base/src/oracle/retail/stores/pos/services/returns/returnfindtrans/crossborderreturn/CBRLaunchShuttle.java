/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnfindtrans/crossborderreturn/CBRLaunchShuttle.java /main/2 2013/03/15 19:49:25 tksharma Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tksharma  03/15/13 - Null pointer fix for findTransCargo and
 *                         rtCustomerCargo added
 *    rsnayak   04/02/12 - Cross Border Return
 * ===========================================================================
 *  
 */
package oracle.retail.stores.pos.services.returns.returnfindtrans.crossborderreturn;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.returns.returncustomer.ReturnCustomerCargo;
import oracle.retail.stores.pos.services.returns.returnfindtrans.ReturnFindTransCargo;

public class CBRLaunchShuttle extends FinancialCargoShuttle
{

    private static final long serialVersionUID = -3015400544600020769L;
    ReturnFindTransCargo findTransCargo = null;
    ReturnCustomerCargo  rtCustomerCargo = null;
    
    public void load(BusIfc bus)
    {
        // Perform FinancialCargoShuttle load
        super.load(bus);
        if(bus.getCargo() instanceof ReturnFindTransCargo)
        {
            findTransCargo = (ReturnFindTransCargo) bus.getCargo();
        }
        
        if(bus.getCargo() instanceof ReturnCustomerCargo)
        {
            rtCustomerCargo = (ReturnCustomerCargo) bus.getCargo();
        }
     
    }

    public void unload(BusIfc bus)
    {
        // Perform FinancialCargoShuttle unload
        super.unload(bus);
        ReturnFindTransCargo rtCargo = null;
        ReturnCustomerCargo custCargo = null;
        if(bus.getCargo() instanceof ReturnFindTransCargo)
        {
            rtCargo = (ReturnFindTransCargo) bus.getCargo();
            if (findTransCargo != null)
            {
                rtCargo.setOriginalTransaction(findTransCargo.getOriginalTransaction());
            }
            else if (rtCustomerCargo != null)
            {
                rtCargo.setOriginalTransaction(rtCustomerCargo.getOriginalTransaction());
            }
        }
        
        if(bus.getCargo() instanceof ReturnCustomerCargo)
        {
            custCargo = (ReturnCustomerCargo) bus.getCargo();
            if (rtCustomerCargo != null)
            {
                custCargo.setOriginalTransaction(rtCustomerCargo.getOriginalTransaction());
            }
            else if (findTransCargo != null)
            {
                custCargo.setOriginalTransaction(findTransCargo.getOriginalTransaction());
            }
        }
       

    }

}
