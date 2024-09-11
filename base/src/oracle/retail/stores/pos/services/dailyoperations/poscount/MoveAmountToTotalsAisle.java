/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/MoveAmountToTotalsAisle.java /main/12 2011/02/16 09:13:26 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:52:33 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    3    360Commerce 1.2         3/31/2005 4:29:05 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:37 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:42 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/06/07 18:29:38  dcobb
 *   @scr 4204 Feature Enhancement: Till Options
 *   Add foreign currency counts.
 *
 *   Revision 1.3  2004/02/12 16:49:38  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:40  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:56:56   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:30:34   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:14:40   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:27:20   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:17:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:11:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * @version $Revision: /main/12 $
 */
public class MoveAmountToTotalsAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -7359099963512732062L;
    /**
     * revision number of this class
     */
    public static String revisionNumber = "$Revision: /main/12 $";

    /**
     * Sends a Next letter.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        PosCountCargo cargo = (PosCountCargo) bus.getCargo();
        CurrencyIfc enteredAmt = cargo.getCurrentAmount();

        // Let the cargo know that the user has accepted this count
        cargo.updateAcceptedCount();
        cargo.updateCountModel(cargo.getCurrentAmount());

        // Put the entered amount in the totals objects.
        if (cargo.getCountType() == PosCountCargo.TILL)
        {
            // Defer update of financial totals until the user
            // exits the services.
        }
        // Update float.
        else if (cargo.getCountType() == PosCountCargo.START_FLOAT || cargo.getCountType() == PosCountCargo.END_FLOAT)
        {
            cargo.updateFLPSummaryInTotals(enteredAmt, 1);
        }
        // Update loan or pickup (count not incremented).
        else
        {
            cargo.updateFLPSummaryInTotals(enteredAmt, 0);
        }

        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
    }
}