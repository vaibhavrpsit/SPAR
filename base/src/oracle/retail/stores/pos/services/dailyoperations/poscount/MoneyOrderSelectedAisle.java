/* ===========================================================================
* Copyright (c) 2002, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/MoneyOrderSelectedAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:24 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:05 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:37 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:42 PM  Robert Pearse   
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

// foundation imports
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;


//--------------------------------------------------------------------------
/**
    Sets the currency selected.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class MoneyOrderSelectedAisle extends PosLaneActionAdapter
{
    /**
       revision number
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Sets the currency selected.
       <p>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        PosCountCargo cargo = (PosCountCargo)bus.getCargo();
        cargo.setCurrentActivityOrCharge(DomainGateway.getFactory()
                                                      .getTenderTypeMapInstance()
                                                      .getDescriptor(TenderLineItemIfc.TENDER_TYPE_MONEY_ORDER));

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
