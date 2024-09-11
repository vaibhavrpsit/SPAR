/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/security/override/UndoSelectedAisle.java /main/10 2011/02/16 09:13:25 cgreene Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:38 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:31 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:22 PM  Robert Pearse   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.security.override;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Mails a Failure letter. This class is used to traverse from another letter
 * (such as Yes or No or Ok or another UI-based letter) to a Failure letter.
 * 
 * @version $Revision: /main/10 $
 */
public class UndoSelectedAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 8276631862578023090L;

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/10 $";

    /**
     * lane name constant
     */
    public static final String LANENAME = "UndoSelectedAisle";

    /**
     * Mails a Failure letter.
     * 
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
        SecurityOverrideCargo cargo = (SecurityOverrideCargo) bus.getCargo();

        cargo.setUndoSelected(true);

        // Any error is fatal at this point
        Letter letter = new Letter(CommonLetterIfc.FAILURE);
        bus.mail(letter, BusIfc.CURRENT);

    }

}