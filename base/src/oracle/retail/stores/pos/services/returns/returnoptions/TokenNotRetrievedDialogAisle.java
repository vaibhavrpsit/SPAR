/* ===========================================================================
* Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/TokenNotRetrievedDialogAisle.java /main/2 2013/04/19 13:35:38 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     04/18/13 - Modified to make lookup by credit card work the same
 *                         way for orders and transactions.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**
    Display dialog to for card number token not retrieved.

    @version $Revision: /main/2 $
**/
//------------------------------------------------------------------------------
public class TokenNotRetrievedDialogAisle extends PosLaneActionAdapter
{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 2783225559145284256L;

    /**
        Display dialog to for card number token not retrieved.
       <p>
       @param bus the bus arriving at this site
    **/
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        //display the dialog
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("RET_TRANS_SEARCH_BY_TOKEN_ERROR");
        model.setType(DialogScreensIfc.CONFIRMATION);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
}

