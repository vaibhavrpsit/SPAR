/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametervaluelisteditor/StoreMoveUpChosenAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:04 mszekely Exp $
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
 *   Revision 1.4  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
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
 *    Rev 1.0   Apr 29 2002 15:38:54   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:06:08   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:20:20   msg
 * Initial revision.
 * 
 *    Rev 1.0   22 Jan 2002 13:52:54   KAC
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:11:20   msg
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
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ReasonCode;
import oracle.retail.stores.pos.ui.beans.ReasonCodeGroupBeanModel;
import oracle.retail.stores.pos.ui.beans.ReasonCodesCommon;

//------------------------------------------------------------------------------
/**
    Move the reason code up in the reason code list, if possible.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class StoreMoveUpChosenAisle
extends PosLaneActionAdapter
implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 7271621635831120265L;


    public static final String LANENAME = "StoreMoveUpChosenAisle";

    //--------------------------------------------------------------------------
    /**
       Move the reason code up in the reason code list, if possible.
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {

        POSUIManagerIfc ui
            = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ListEditorCargo cargo = (ListEditorCargo)bus.getCargo();
        ReasonCodeGroupBeanModel model = (ReasonCodeGroupBeanModel)
            ui.getModel(POSUIManagerIfc.PARAM_VALUE_LIST);

        Vector choices = model.getReasonCodes();
        int selectedIndex = model.getReasonCodeSelectionIndex();
        //cargo.setSelectionIndex(selectedIndex);
        cargo.setOperationRequested(bus.getCurrentLetter().getName());
        ReasonCodeGroupBeanModel reasonCodeGroup = cargo.getReasonCodeGroup();
        Letter letter = new Letter(ReasonCodesCommon.ACCEPT_DATA);

        // If the selected item is not the first one, move it up one position.
        if (selectedIndex > 0)
        {
            Vector reasonCodes = reasonCodeGroup.getReasonCodes();
            ReasonCode reason
                = (ReasonCode)reasonCodes.elementAt(selectedIndex);
            reasonCodes.removeElementAt(selectedIndex);
            reasonCodes.insertElementAt(reason, selectedIndex - 1);
            reasonCodeGroup.setReasonCodeSelectionIndex(selectedIndex - 1);
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
