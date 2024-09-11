/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/postvoid/ConvertToFailureAisle.java /main/11 2011/02/16 09:13:28 cgreene Exp $
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
 *    1    360Commerce 1.0         12/13/2005 4:47:03 PM  Barry A. Pape   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.postvoid;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * @version $Revision: /main/11 $
 */
public class ConvertToFailureAisle extends PosLaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell  the compiler not to generate a ew serialVersionUID.
    static final long serialVersionUID = -6643412629509039716L;

    public static final String LANENAME = "ConvertToFailureAisle";

    /**
     * @param bus the bus traversing this lane
     **/
    @Override
    public void traverse(BusIfc bus)
    {
        Letter letter = new Letter(CommonLetterIfc.FAILURE);
        bus.mail(letter, BusIfc.CURRENT);
    }

    /**
     * @param bus the bus traversing this lane
     */
    public void backup(BusIfc bus)
    {
    }

}
