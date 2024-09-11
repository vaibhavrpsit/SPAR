/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/IsItemNotToBeAddedSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:44 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    11/16/09 - deprecated signals
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:27 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:17 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:32 PM  Robert Pearse
 *
 *   Revision 1.4  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
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
 *    Rev 1.0   Aug 29 2003 16:00:12   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:22:18   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:33:48   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:30:02   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:12   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

// foundation imports
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

//--------------------------------------------------------------------------
/**
    This signal checks to make sure that the item is not going to be added
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
    @deprecated as of 13.2. No replacement provided
**/
//--------------------------------------------------------------------------
public class IsItemNotToBeAddedSignal implements TrafficLightIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -9114643632915932964L;

    /**
        revision number supplied by Team Connection
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Checks to make sure that the item is not to be added
        @return boolean true if the  item list is empty false otherwise.
    **/
    //----------------------------------------------------------------------
    public boolean roadClear(BusIfc bus)
    {
        boolean result = false;

        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        if (!cargo.getModifiedFlag())
        {
           result = true;
        }
        return(result);
    }
}
