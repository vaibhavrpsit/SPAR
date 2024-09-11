/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/DoneLetterAisle.java /main/1 2012/06/21 12:42:42 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     06/14/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * This aisle sends a Cancel Letter.
 * 
 * @version $Revision: /main/1 $
 */
public class DoneLetterAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 1L;
    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/1 $";

    /**
     * Sends a Cancel letter.
     * 
     * @param bus Service Bus
     */
    public void traverse(BusIfc bus)
    {
        bus.mail(new Letter(CommonLetterIfc.DONE), BusIfc.CURRENT);
    }
}
