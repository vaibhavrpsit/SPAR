/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/InvalidConversionAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:51 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/22/10 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * Mails an "Invalid" letter. This class is used to traverse from another letter
 * (such as Yes or No or Ok or another UI-based letter) to an "Invalid" letter.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class InvalidConversionAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -7205727184316985752L;

    /**
     * lane name constant
     */
    public static final String LANENAME = "InvalidConversionAisle";

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Mails a Invalid letter.
     * 
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
        Letter letter = new Letter(CommonLetterIfc.INVALID);
        bus.mail(letter, BusIfc.CURRENT);
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
