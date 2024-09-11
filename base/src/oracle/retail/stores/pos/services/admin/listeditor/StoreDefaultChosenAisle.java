/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/listeditor/StoreDefaultChosenAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:04 mszekely Exp $
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
 *   Revision 1.4  2004/04/19 18:48:56  awilliam
 *   @scr 4374 Reason Code featrure work
 *
 *   Revision 1.3  2004/02/12 16:48:49  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:35:34  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:52:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:40:34   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:03:40   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:18:52   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:11:14   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:05:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.listeditor;

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
    Store the default reason code as entered at the UI.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class StoreDefaultChosenAisle extends LaneActionAdapter
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

        // Get the new default reason code
        ReasonCode defaultReason = model.getReasonCodeSelected();
        String defaultName = defaultReason.getReasonCodeName();

        // Set the new default in the reason code group
        ReasonCodeGroupBeanModel reasonCodeGroup = cargo.getReasonCodeGroup();
        reasonCodeGroup.setDefaultReasonCode(defaultName);

        // Set the new modifiable value
        String newModifiable = model.getModifiableValue();
        reasonCodeGroup.setModifiableValue(newModifiable);

        bus.mail(new Letter(ReasonCodesCommon.ACCEPT_DATA), BusIfc.CURRENT);
    }
}
