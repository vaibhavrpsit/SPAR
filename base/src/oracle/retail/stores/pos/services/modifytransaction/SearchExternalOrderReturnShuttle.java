/* ===========================================================================
* Copyright (c) 2009, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/SearchExternalOrderReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:30 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       07/22/10 - pass original return transaction from external order
 *                         to the sale cargo
 *    abondala  05/28/10 - re packaging files
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    05/21/10 - additional changes for process order flow
 *    abondala  05/12/10 - There is a conflict with modifytransaction folder
 *                         and CM recommended to rename as _modifytransaction
 *                         until we have a fix.
 *    abondala  05/12/10 - Siebel search flow
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.modifytransaction;

import oracle.retail.stores.pos.services.externalorder.searchorder.SearchOrderCargo;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.modifytransaction.ModifyTransactionCargo;

/**
 * This shuttle carries the required contents from
 * Search ExternalOrder Site to Modify Transaction site. <P>
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */

public class SearchExternalOrderReturnShuttle extends FinancialCargoShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 1L;

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";


    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.modifytransaction.SearchExternalOrderReturnShuttle.class);


    /**
        Incoming SearchOrderCargo
     **/
    protected SearchOrderCargo searchCargo = null;

    //---------------------------------------------------------------------
    /**
       Get a local copy of the SearchOrderCargo cargo.
       <P>
       @param bus the bus being loaded
    **/
    //---------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        super.load(bus);
        // retrieve SearchOrder cargo
        searchCargo = (SearchOrderCargo)bus.getCargo();
    }

    //---------------------------------------------------------------------
    /**
       Copy required data from the SearchOrder cargo to the Modify Transaction Cargo.

       @param bus the bus being unloaded
    **/
    //---------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        // retrieve Modify Transaction cargo
        ModifyTransactionCargo cargo = (ModifyTransactionCargo)bus.getCargo();
        cargo.setTransaction(searchCargo.getTransaction());
        cargo.setUpdateParentCargoFlag(true);

        // add all original return transactions to the parent cargo list
        SaleReturnTransactionIfc[] originalReturnTransactions = searchCargo.getOriginalReturnTransactions();
        if (originalReturnTransactions != null)
        {
            for (int i=0; i<originalReturnTransactions.length; i++)
            {
                cargo.addOrignalReturnTransaction(originalReturnTransactions[i]);
            }
        }
    }

    //---------------------------------------------------------------------
    /**
        Retrieves the source-code-control system revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return revisionNumber;
    }
}
