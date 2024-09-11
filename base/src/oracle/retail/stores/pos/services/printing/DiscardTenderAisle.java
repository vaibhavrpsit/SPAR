/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/printing/DiscardTenderAisle.java /main/11 2011/02/16 09:13:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    mchellap  09/29/10 - BUG#10153387 Fixed giftcertificate franking after
 *                         printer timeout
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:45 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:58 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:35 PM  Robert Pearse
 *
 *   Revision 1.3  2004/02/12 16:51:40  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:05:28   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Jul 09 2003 16:58:10   bwf
 * Check if tenders null before using it.
 * Resolution for 2915: System crashes if user selects to frank a store coupon manually
 *
 *    Rev 1.1   Jul 09 2003 16:12:36   vxs
 * removing tenders from list by calling addProcessedTender()
 *
 *    Rev 1.0   Apr 29 2002 15:07:24   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:44:24   msg
 * Initial revision.
 *
 *    Rev 1.0   02 Mar 2002 11:57:52   pdd
 * Initial revision.
 * Resolution for POS SCR-1362: Selecting No on Slip Printer Timeout retries franking, should not
 *
 *    Rev 1.0   06 Feb 2002 20:41:56   baa
 * Initial revision.
 * Resolution for POS SCR-641: Any franking problem with multi Check tenders do not frank the 2nd check
 *
 *    Rev 1.0   Sep 21 2001 11:22:44   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:11:54   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.printing;

import java.util.ArrayList;
import java.util.Vector;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderTravelersCheck;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Print endorsement on tender documents.
 * 
 * @version $Revision: /main/11 $
 */
public class DiscardTenderAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -1416656316004091520L;
    /**
     * Name of this lane.
     **/
    public static final String LANENAME = "CheckForPendingTendersAisle";

    /**
     * If the transaction has tenders to endorse, do it.
     * 
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
        PrintingCargo cargo = (PrintingCargo) bus.getCargo();

        Vector tenders =  cargo.getTendersToFrank();
        int currentTender =  cargo.getCurrentTender();

        if(tenders != null) // if only store coupon then tenders is null
        {
            // add current tender to list of process tenders
            Object tenderItem = tenders.elementAt(currentTender);

            if ( tenderItem instanceof TenderTravelersCheck)
            {
                int pendingCnt = cargo.getPendingCheckCount()-1;

                cargo.setPendingCheckCount(pendingCnt);
                // this was the last of the travel checks, remove it from tender list
                if (pendingCnt <= 0)
                {
                    cargo.addProcessedTender((TenderLineItemIfc)tenderItem);
                }
            }
            //remove tender from list
            else if(tenderItem instanceof TenderLineItemIfc)
            {
                cargo.addProcessedTender((TenderLineItemIfc)tenderItem);
            }
        }

        ArrayList giftCertificatesForFranking = cargo.getGiftCertificatesForFranking();
        int currentGiftCertificate = cargo.getCurrentNonTenderDocument();

        if (!giftCertificatesForFranking.isEmpty())
        {
            // add current GiftCertificate to the list of processed
            // GiftCertificate
            Object lineItem = giftCertificatesForFranking.get(currentGiftCertificate);
            cargo.addProcessedNonTenderDocuments((SaleReturnLineItemIfc) lineItem);
        }
        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }
}
