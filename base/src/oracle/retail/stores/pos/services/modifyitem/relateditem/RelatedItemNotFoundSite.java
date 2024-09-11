/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/relateditem/RelatedItemNotFoundSite.java /main/1 2012/09/28 17:32:43 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     09/27/12 - move from sale package.
* yiqzhao     09/26/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.modifyitem.relateditem;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

//--------------------------------------------------------------------------
/**
     This site displays the item not found message.
     $Revision: /main/1 $
 **/
//--------------------------------------------------------------------------
public class RelatedItemNotFoundSite extends PosSiteActionAdapter
{

    public static String RELATED_ITEM_NOT_FOUND = "RelatedItemNotFound";
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UIUtilities.setDialogModel(ui, 
                        DialogScreensIfc.ERROR, 
                        RELATED_ITEM_NOT_FOUND, 
                        null, 
                        CommonLetterIfc.CONTINUE);
    }
}
