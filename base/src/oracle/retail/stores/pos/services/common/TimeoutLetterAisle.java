/* =============================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * =============================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/TimeoutLetterAisle.java /main/6 2011/02/16 09:13:31 cgreene Exp $
 * =============================================================================
 * NOTES
 * Created by Lucy Zhao (Oracle Consulting) for POS-RM integration.
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    rkar      11/07/08 - Additions/changes for POS-RM integration
 *
 * =============================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * This aisle sends a Timeout Letter.
 * 
 * @version $Revision: /main/6 $
 */
public class TimeoutLetterAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -8641258420488338031L;

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/6 $";

    /**
     * Sends an Timeout letter.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        bus.mail(CommonLetterIfc.TIMEOUT, BusIfc.CURRENT);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
