/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/CheckIfSizeRequiredAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:53 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:25 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:09 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:56 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/03/15 15:16:52  baa
 *   @scr 3561 refactor/clean item size code, search by tender changes
 *
 *   Revision 1.4  2004/03/11 14:32:10  baa
 *   @scr 3561 Add itemScanned get/set methods to PLUItemCargoIfc and add support for changing type of quantity based on the uom
 *
 *   Revision 1.3  2004/02/20 21:00:49  epd
 *   @scr 3561 removed unused import to make Brendan happy
 *
 *   Revision 1.2  2004/02/20 19:51:59  epd
 *   @scr 3561 Updates to prompt for item size if the item requires a size
 *
 *   Revision 1.1  2004/02/20 15:51:42  baa
 *   @scr 3561  size enhancements
 *
 *   Revision 1.1  2004/02/19 15:37:31  baa
 *   @scr 3561 returns
 *
 *   Revision 1.3  2004/02/12 16:51:49  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:06:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:05:44   msg
 * Initial revision.
 * 
 *    Rev 1.1   27 Mar 2002 11:50:00   cir
 * Set the validation failed flag to true
 * Resolution for POS SCR-110: Return Item Info data cleared after Qty Notice for entering Qty > 1 and serial number
 * 
 *    Rev 1.0   Mar 18 2002 11:46:08   msg
 * Initial revision.
 * 
 *    Rev 1.1   Feb 23 2002 10:35:04   mpm
 * Modified Util.BIG_DECIMAL to Util.I_BIG_DECIMAL, Util.ROUND_HALF to Util.I_ROUND_HALF
 * Resolution for POS SCR-1398: Accept Foundation BigDecimal backward-compatibility changes
 * 
 *    Rev 1.0   Sep 21 2001 11:25:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:42   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

// foundation imports
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

import oracle.retail.stores.domain.stock.PLUItemIfc;

import oracle.retail.stores.pos.services.common.PLUItemCargoIfc;

//--------------------------------------------------------------------------
/**
    This ailse validates the returned item information.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CheckIfSizeRequiredAisle extends LaneActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

 
    //----------------------------------------------------------------------
    /**
       This ailse checks if the item size is required
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        PLUItemCargoIfc cargo = (PLUItemCargoIfc)bus.getCargo();
        PLUItemIfc pluItem = cargo.getPLUItem();
        String letterName = "Continue2";

        if (pluItem != null && pluItem.isItemSizeRequired() && !cargo.isItemScanned())
        {
             letterName ="Size";
        }
        bus.mail(new Letter(letterName), BusIfc.CURRENT);

    }
}
