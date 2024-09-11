/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/OkToUndoConversionAisle.java /main/11 2011/02/16 09:13:25 cgreene Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:11 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:45 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:49 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:08  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:54:36   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:34:52   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:09:54   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:23:02   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:13:50   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:06:12   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * Mails an Undo letter. This class is used to traverse from another letter
 * (such as Ok or another UI-based letter) to an Undo letter.
 * 
 * @version $Revision: /main/11 $
 */
public class OkToUndoConversionAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -7829860936086577226L;

    public static final String LANENAME = "OkToUndoConversionAisle";

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/11 $";

    /**
     * Mails an Undo letter.
     * 
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
        bus.mail(new Letter(CommonLetterIfc.UNDO), BusIfc.CURRENT);
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