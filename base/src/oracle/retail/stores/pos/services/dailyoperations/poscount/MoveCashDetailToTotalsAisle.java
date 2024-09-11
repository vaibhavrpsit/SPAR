/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/MoveCashDetailToTotalsAisle.java /main/12 2011/02/16 09:13:26 cgreene Exp $
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
 *    Rev 1.0   Apr 29 2002 15:30:36   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:14:42   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:27:22   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:17:00   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:11:20   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.beans.CurrencyDetailBeanModel;

/**
 * @version $Revision: /main/12 $
 */
public class MoveCashDetailToTotalsAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 5708946105199935363L;
    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * Sends a Next letter.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // Get the cargo and put the entered amounts in the totals object.
        PosCountCargo cargo = (PosCountCargo) bus.getCargo();

        // Let the cargo know that the user has accepted this count
        cargo.updateAcceptedCount();
        cargo.updateCountModel(cargo.getCurrentAmount());

        if (cargo.getCountType() == PosCountCargo.TILL)
        {
            // Defer update of financial totals until the user
            // exits the services.
        }
        else
        {
            // Put the entered amount in the totals objects.
            CurrencyIfc amt = cargo.getCurrentAmount();
            CurrencyDetailBeanModel beanModel = cargo.getCurrencyDetailBeanModel(amt.getCountryCode());
            cargo.updateCashDetailAmountInTotals(beanModel);
        }

        // Our work is done here.
        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

}