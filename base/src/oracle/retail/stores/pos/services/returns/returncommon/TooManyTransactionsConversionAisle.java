/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncommon/TooManyTransactionsConversionAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   09/09/10 - TooManyTransactions conversion aisle
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncommon;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * This aisle sends a TooManyTransactions Letter.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class TooManyTransactionsConversionAisle extends PosLaneActionAdapter
{
    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Sends an TooManyTransactions letter.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        bus.mail(new Letter("TooManyTransactions"), BusIfc.CURRENT);
    }
}
