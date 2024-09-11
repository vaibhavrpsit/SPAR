/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/ForeignTenderSelectedAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:23 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:13 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:45 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:08 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/06/07 18:29:38  dcobb
 *   @scr 4204 Feature Enhancement: Till Options
 *   Add foreign currency counts.
 *
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//--------------------------------------------------------------------------
/**
    Sets the tender selected.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ForeignTenderSelectedAisle extends PosLaneActionAdapter
{
    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Sets the tender selected. (Cash, Check, TravelCheck, GiftCert, StoreCredit)
        Mails CountSummary, CashDetail or CountDetail letter.
        <p>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        PosCountCargo cargo = (PosCountCargo)bus.getCargo();
        cargo.setCurrentActivityOrCharge(bus.getCurrentLetter().getName());

        String letterName = "CountSummary";
        if (!cargo.getSummaryFlag())
        {
            if (cargo.currentHasDenominations())
            {
                letterName = "CashDetail";
            }
            else
            {
                letterName = "CountDetail";
            }
        }
        
        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }
}
