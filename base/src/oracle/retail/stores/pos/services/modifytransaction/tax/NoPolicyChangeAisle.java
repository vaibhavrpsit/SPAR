/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/tax/NoPolicyChangeAisle.java /main/10 2011/02/16 09:13:28 cgreene Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:09 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:45 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/04/29 01:45:34  tfritz
 *   @scr 4354 - Look at the TaxUpdateFlag
 *
 *   Revision 1.3  2004/02/12 16:51:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:37  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:02:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:14:52   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:39:52   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:31:36   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:10:04   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.tax;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * @version $Revision: /main/10 $
 */
public class NoPolicyChangeAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -2521138760034471706L;
    /**
     * revision number supplied by Team Connection
     **/
    public static final String revisionNumber = "$Revision: /main/10 $";

    /**
     * This aisle will be traversed when the user chooses not to update all
     * items with the new registry number to be entered. A next letter is sent.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // retrieve cargo
        ModifyTransactionTaxCargo cargo = (ModifyTransactionTaxCargo) bus.getCargo();

        // set update flag in cargo
        cargo.setUpdateAllItemsFlag(false);

        // the user has clicked "No", so return to the previous screen
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
