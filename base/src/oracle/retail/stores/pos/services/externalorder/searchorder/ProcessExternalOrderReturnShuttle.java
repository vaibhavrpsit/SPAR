/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/searchorder/ProcessExternalOrderReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:58 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       07/22/10 - pass original return transaction from external order
 *                         to the sale cargo
 *    acadar    07/09/10 - remove reference to salereturn transaction
 *    acadar    07/09/10 - extend FinancialShuttle
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    05/14/10 - initial version for process external order tour
 *    acadar    05/14/10 - initial version
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.externalorder.searchorder;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.externalorder.processorder.ProcessOrderCargo;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;


/**
 * This shuttle carries the required contents from
 * the search external order service to process external order service <P>
 * @author acadar
 */

public class ProcessExternalOrderReturnShuttle extends FinancialCargoShuttle implements ShuttleIfc
{


    /**
     *  Serial version UID
     */
    private static final long serialVersionUID = -7234861008827417215L;

    /**
        class name constant
    **/
    public static final String SHUTTLENAME = "ProcessExternalOrderReturnShuttle";


    /**
     * Search External Order cargo
     */
    protected ProcessOrderCargo cargo = null;




    /**
     * Gets the external order from the cargo
     * @param bus the bus being unloaded
     */
    public void load(BusIfc bus)
    {
    	super.load(bus);
        cargo = (ProcessOrderCargo)bus.getCargo();

    }


    /**
     * Copies information to the cargo used by the process order service. <P>
     * @param bus the bus being unloaded
    */
    public void unload(BusIfc bus)
    {
    	super.unload(bus);
        SearchOrderCargo searchCargo = (SearchOrderCargo)bus.getCargo();
        //if a transaction is in progress set it in the cargo
        searchCargo.setTransaction(cargo.getTransaction());
        searchCargo.setOriginalReturnTransactions(cargo.getOriginalReturnTransactions());
    }
}
