/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametervaluelisteditor/StoreEditReasonCodeChosenAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:04 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:11 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:32 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:27 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:48:51  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:36:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:08   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:38:50   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:06:04   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:20:18   msg
 * Initial revision.
 * 
 *    Rev 1.0   22 Jan 2002 13:52:52   KAC
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:11:14   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:05:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.parametervaluelisteditor;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ReasonCode;
import oracle.retail.stores.pos.ui.beans.ReasonCodeGroupBeanModel;
import oracle.retail.stores.pos.ui.beans.ReasonCodesCommon;

//------------------------------------------------------------------------------
/**
    Stores the reason code to be edited.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class StoreEditReasonCodeChosenAisle extends LaneActionAdapter
{
    //--------------------------------------------------------------------------
    /**
        Stores the reason code to be edited.
        @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ReasonCodeGroupBeanModel model = (ReasonCodeGroupBeanModel)
            ui.getModel(POSUIManagerIfc.PARAM_VALUE_LIST);

        // Get the data from the model
        ReasonCode reason = model.getReasonCodeSelected();
        ListEditorCargo cargo = (ListEditorCargo)bus.getCargo();
        ReasonCodeGroupBeanModel reasonCodeGroup = cargo.getReasonCodeGroup();
        reason.setReasonCodeGroup(reasonCodeGroup.getGroupName());
        cargo.setReasonCode(reason);

        // This will let us distinguish between adds and edits
        cargo.setOperationRequested(ReasonCodesCommon.SHOW_EDIT_SCREEN);

        bus.mail(new Letter(ReasonCodesCommon.SHOW_EDIT_SCREEN), BusIfc.CURRENT);
    }
}
