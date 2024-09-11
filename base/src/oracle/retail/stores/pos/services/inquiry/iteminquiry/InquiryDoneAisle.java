/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/InquiryDoneAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:45 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:22 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:07 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:24 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/03 23:15:10  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:30  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:11  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:00:08   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:22:14   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:33:44   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:29:56   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:08:14   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;
// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------------
/**
    This aisle is traversed when the item is not
    added to the current transaction.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class InquiryDoneAisle extends LaneActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Sends a Next letter to the Final system site.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // Set the modifiable flag to cargo
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        cargo.setModifiedFlag(false);

        bus.mail(new Letter(CommonLetterIfc.NEXT), BusIfc.CURRENT);

    }
}
