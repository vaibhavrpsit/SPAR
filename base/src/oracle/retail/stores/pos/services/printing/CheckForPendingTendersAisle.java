/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/printing/CheckForPendingTendersAisle.java /main/12 2012/04/26 14:39:34 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  04/25/12 - Fixes for Fortify redundant null check, take2
 *    mchellap  09/29/10 - BUG#10153387 Fixed giftcertificate franking after
 *                         printer timeout
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         3/10/2008 5:05:20 AM   Manas Sahu
 *         Forward ported Brett J Larsen's fix from v12x branch. - prashanm
 *    3    360Commerce 1.2         3/31/2005 4:27:25 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:08 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:55 PM  Robert Pearse
 *
 *   Revision 1.4  2004/03/03 23:15:07  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
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
 *    Rev 1.0   Aug 29 2003 16:05:26   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:07:22   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:44:22   msg
 * Initial revision.
 *
 *    Rev 1.1   01 Mar 2002 19:05:50   baa
 * fix franking multiple checks, halt /proceed options
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

// Java imports
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//------------------------------------------------------------------------------
/**
    Print endorsement on tender documents.

    @version $Revision: /main/12 $
**/
//------------------------------------------------------------------------------
public class CheckForPendingTendersAisle extends PosLaneActionAdapter
{
    /**
       Name of this  lane.
    **/
    public    static final String LANENAME = "CheckForPendingTendersAisle";


    //--------------------------------------------------------------------------
    /**
            If the transaction has tenders to endorse, do it.

            @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        String letter = CommonLetterIfc.DONE;
        PrintingCargo        cargo       = (PrintingCargo) bus.getCargo();

        // Get the tender items to endorse
        Vector tenders =  cargo.getTendersToFrank();
        Vector processed = cargo.getProcessedTenders();

         // Remove all process checks from tender list
         if ( processed != null && processed.size() > 0 && tenders != null)
         {
             Enumeration done = cargo.getProcessedTenders().elements();
             while (done.hasMoreElements())
             {
                tenders.removeElement(done.nextElement());
             }

            cargo.getProcessedTenders().removeAllElements();
         }

         ArrayList giftCertificatesForFranking = cargo.getGiftCertificatesForFranking();

         ArrayList processedGiftCertificate = cargo.getProcessedNonTenderDocuments();

         // Remove all processed Gift Certificate from giftCertificate list
        if (processedGiftCertificate != null && processedGiftCertificate.size() > 0)
        {
            for (Iterator i = processedGiftCertificate.iterator(); i.hasNext();)
            {
                SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc) i.next();
                cargo.setGiftCertificateFranked(lineItem);
            }

            cargo.getProcessedNonTenderDocuments().clear();
        }

        // Check if there are still tenders or issued gift certificates that need franking
        if ((tenders != null  && tenders.size() > 0) ||
                (giftCertificatesForFranking != null && !giftCertificatesForFranking.isEmpty()))

        {
               // continue attempting to endorse remaining tenders
               letter = "Print";
        }
        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }
}
