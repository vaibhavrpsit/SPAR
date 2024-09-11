/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/ApprovedCheckDetailAisle.java /main/12 2011/02/16 09:13:26 cgreene Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:39 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:31 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:56:38   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:30:10   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:14:08   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:26:54   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:17:18   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:11:28   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.OtherTenderDetailBeanModel;

/**
 * @version $Revision: /main/12 $
 */
public class ApprovedCheckDetailAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 5730612947247058757L;
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
        // Get the tender amount from the UI
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        PosCountCargo cargo = (PosCountCargo) bus.getCargo();

        OtherTenderDetailBeanModel beanModel = cargo.getOtherTenderDetailBeanModel();

        CurrencyIfc enteredAmt = beanModel.getTotal();
        cargo.updateAcceptedCount();
        cargo.updateCountModel(enteredAmt);

        // Put the entered amount in the totals objects.
        if (cargo.getCountType() != PosCountCargo.TILL)
        {
            cargo.updateCheckAmountsInTotals(beanModel);
        }
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