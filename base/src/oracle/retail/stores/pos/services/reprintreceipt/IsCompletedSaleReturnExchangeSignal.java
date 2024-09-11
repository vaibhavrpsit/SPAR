/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/reprintreceipt/IsCompletedSaleReturnExchangeSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:29 mszekely Exp $
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
 *3    360Commerce 1.2         3/31/2005 4:28:26 PM   Robert Pearse   
 *2    360Commerce 1.1         3/10/2005 10:22:15 AM  Robert Pearse   
 *1    360Commerce 1.0         2/11/2005 12:11:31 PM  Robert Pearse   
 *
 Revision 1.2  2004/09/23 00:07:14  kmcbride
 @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 Revision 1.1  2004/04/22 21:26:38  dcobb
 @scr 4452 Feature Enhancement: Printing
 Only completed sale, return or exchange transactions are displayed in REPRINT_SELECT.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.reprintreceipt;

import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

//--------------------------------------------------------------------------
/**
 This determines if the transaction is a completed sale, return or echange 
 transaction.
 <P>
 @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 **/
//--------------------------------------------------------------------------
public class IsCompletedSaleReturnExchangeSignal implements TrafficLightIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 3986249579423865322L;

    /**
     revision number of this class
     **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Determines if the transaction is a completed sale, return or exchange 
        transaction.
        <p>
        @param bus the bus trying to proceed
        @return true if the transaction is a completed sale, return or exchange 
        transaction.
    **/
    //----------------------------------------------------------------------
    public boolean roadClear(BusIfc bus)
    {
        ReprintReceiptCargo cargo = (ReprintReceiptCargo) bus.getCargo();

        // check the transaction type
        TenderableTransactionIfc tenderableTransaction = cargo.getTenderableTransaction();        
        return cargo.isCompletedSaleReturnExchange(tenderableTransaction);
    }

}
