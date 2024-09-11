/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/GiftCardAddLetterConversionAisle.java /main/10 2011/02/16 09:13:27 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:16 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:52 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:12 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:50:30  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:10  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Dec 19 2003 16:11:10   lzhao
 * Initial revision.
 * 
 *    Rev 1.0   Dec 12 2003 14:28:52   lzhao
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Mails an Add letter. This class is used to traverse from another letter (such
 * as Yes or No or Ok or another UI-based letter) to a Continue letter.
 * 
 * @version $Revision: /main/10 $
 */
public class GiftCardAddLetterConversionAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 222658487744133007L;

    /**
     * lane name constant
     */
    public static final String LANENAME = "GiftCardAddLetterConversionAisle";

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/10 $";

    /**
     * Mails an Add letter.
     * 
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // Any error is fatal at this point
        Letter letter = new Letter(CommonLetterIfc.ADD);
        bus.mail(letter, BusIfc.CURRENT);
    }
}