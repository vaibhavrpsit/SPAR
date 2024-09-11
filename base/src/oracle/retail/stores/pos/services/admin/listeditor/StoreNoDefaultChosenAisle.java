/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/listeditor/StoreNoDefaultChosenAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:04 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * $ 1    360Commerce 1.0         5/4/2007 4:29:04 PM    Owen D. Horne   
 * $$$
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.listeditor;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ReasonCodeGroupBeanModel;
import oracle.retail.stores.pos.ui.beans.ReasonCodesCommon;

//------------------------------------------------------------------------------
/**
    Clear the value the user has selected for the default.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class StoreNoDefaultChosenAisle extends LaneActionAdapter
{
    //--------------------------------------------------------------------------
    /**
        Stores the default reason code as entered at the UI. <p>
        @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        ListEditorCargo cargo = (ListEditorCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ReasonCodeGroupBeanModel model = (ReasonCodeGroupBeanModel)ui.getModel(cargo.getReasonCodeScreenToDisplay());

        // Clear default in the reason code group
        ReasonCodeGroupBeanModel reasonCodeGroup = cargo.getReasonCodeGroup();
        reasonCodeGroup.setDefaultReasonCode("");

        // Set the new modifiable value
        String newModifiable = model.getModifiableValue();
        reasonCodeGroup.setModifiableValue(newModifiable);
        
        bus.mail(new Letter(ReasonCodesCommon.ACCEPT_DATA), BusIfc.CURRENT);
    }
}
