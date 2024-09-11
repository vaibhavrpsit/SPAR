/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametervaluelisteditor/StoreMoveDownChosenAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:04 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:12 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:35 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:29 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:53:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:38:52   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:06:06   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:20:18   msg
 * Initial revision.
 * 
 *    Rev 1.0   22 Jan 2002 13:52:54   KAC
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:11:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:05:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.parametervaluelisteditor;

import java.util.Vector;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ReasonCode;
import oracle.retail.stores.pos.ui.beans.ReasonCodeGroupBeanModel;
import oracle.retail.stores.pos.ui.beans.ReasonCodesCommon;

//------------------------------------------------------------------------------
/**
    Move the reason code down in the reason code list, if possible.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class StoreMoveDownChosenAisle extends PosLaneActionAdapter
{
    public static final String LANENAME = "StoreMoveDownChosenAisle";

    //--------------------------------------------------------------------------
    /**
       Move the reason code down in the reason code list, if possible.
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {

        POSUIManagerIfc ui
            = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ListEditorCargo cargo = (ListEditorCargo)bus.getCargo();
        ReasonCodeGroupBeanModel model =
            (ReasonCodeGroupBeanModel)
            ui.getModel(POSUIManagerIfc.PARAM_VALUE_LIST);

        int selectedIndex = model.getReasonCodeSelectionIndex();
        //cargo.setSelectionIndex(selectedIndex);
        cargo.setOperationRequested(bus.getCurrentLetter().getName());
        ReasonCodeGroupBeanModel reasonCodeGroup = cargo.getReasonCodeGroup();
        Letter letter = new Letter(ReasonCodesCommon.ACCEPT_DATA);

        Vector reasonCodes      = reasonCodeGroup.getReasonCodes();
        boolean nextCodeEnabled = false;
        int nextIndex = selectedIndex + 1;
        if (nextIndex < reasonCodes.size())
        {
            nextCodeEnabled = ((ReasonCode)reasonCodes.elementAt(nextIndex)).getEnabled();
        }

        // If the selected item is not the last one, move it down one
        // position.
        if (selectedIndex < (reasonCodes.size() - 1) && nextCodeEnabled)
        {
            ReasonCode reason
                = (ReasonCode)reasonCodes.elementAt(selectedIndex);
            reasonCodes.removeElementAt(selectedIndex);
            reasonCodes.insertElementAt(reason, nextIndex);
            reasonCodeGroup.setReasonCodeSelectionIndex(nextIndex);
        }
        bus.mail(letter, BusIfc.CURRENT);

    }


    //--------------------------------------------------------------------------
    /**
       Log the backing up.
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void backup(BusIfc bus)
    {
    }

}
