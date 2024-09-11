/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/ValidateItemSizeAisle.java /main/12 2014/06/03 13:25:37 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  05/22/14 - UsePOSUIManager.getInput method for getting response
 *                         text.
 *    yiqzhao   01/29/13 - Remove item size check to allow alpha numeric
 *                         string.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:42 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:41 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:29 PM  Robert Pearse   
 *
 *   Revision 1.13  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.12  2004/03/18 15:54:59  baa
 *   @scr 3561 Add changes to support giftcard returns
 *
 *   Revision 1.11  2004/03/10 14:16:46  baa
 *   @scr 0 fix javadoc warnings
 *
 *   Revision 1.10  2004/02/27 19:51:16  baa
 *   @scr 3561 Return enhancements
 *
 *   Revision 1.9  2004/02/23 13:54:52  baa
 *   @scr 3561 Return Enhancements to support item size
 *
 *   Revision 1.8  2004/02/19 23:31:22  aarvesen
 *   @scr 3561  Do a null pointer check
 *
 *   Revision 1.7  2004/02/19 21:58:43  epd
 *   @scr 3561 updated to reflect updated API
 *
 *   Revision 1.6  2004/02/19 21:52:06  epd
 *   @scr 3561 added item size to search criteria
 *
 *   Revision 1.5  2004/02/19 15:37:31  baa
 *   @scr 3561 returns
 *
 *   Revision 1.4  2004/02/18 20:36:20  baa
 *   @scr 3561 Returns changes to support size
 *
 *   Revision 1.3  2004/02/12 16:51:52  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:25  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Dec 19 2003 13:23:06   baa
 * more return enhancements
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 * 
 *    Rev 1.0   Dec 17 2003 11:37:44   baa
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;

import oracle.retail.stores.domain.transaction.SearchCriteria;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;

import oracle.retail.stores.pos.services.returns.returnitem.ReturnItemCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;


//------------------------------------------------------------------------------
/**
     
    @version $Revision: /main/12 $
**/
//------------------------------------------------------------------------------

public class ValidateItemSizeAisle extends LaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 3372460626413832102L;

    /**
     * size format
     */
    public static final String SIZE_NUMBER_FORMAT = "0000";
    //--------------------------------------------------------------------------
    /**
            @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        
        // get the response text
        String input = ui.getInput();
        ReturnItemCargo cargo = (ReturnItemCargo) bus.getCargo();

        // set the item size in the criteria
        SearchCriteriaIfc criteria = cargo.getSearchCriteria();
        
        if (criteria == null) {
            criteria = new SearchCriteria();
        }
        
        criteria.setItemSizeCode(input);
        cargo.setSearchCriteria(criteria);
        
        bus.mail(new Letter(CommonLetterIfc.SEARCH), BusIfc.CURRENT);
    }


}
