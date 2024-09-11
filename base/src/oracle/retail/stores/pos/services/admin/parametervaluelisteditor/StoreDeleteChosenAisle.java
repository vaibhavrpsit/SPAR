/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametervaluelisteditor/StoreDeleteChosenAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:04 mszekely Exp $
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
 *    Rev 1.0   Aug 29 2003 15:53:08   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:38:48   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:06:02   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:20:16   msg
 * Initial revision.
 * 
 *    Rev 1.2   10 Feb 2002 14:39:00   KAC
 * Now handles situations with no values.
 * Resolution for POS SCR-1226: Update list parameter value editor per new requirements
 * 
 *    Rev 1.1   04 Feb 2002 11:38:08   KAC
 * Avoids ArrayIndexOutOfBoundsException when nothing is
 * selected.
 * Resolution for POS SCR-1010: Deleting every value on Create List Parameter Editor screens hangs application
 * 
 *    Rev 1.0   22 Jan 2002 13:52:52   KAC
 * Initial revision.
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
    Delete the reason code indicated by the user, if it is not the only one.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class StoreDeleteChosenAisle
extends PosLaneActionAdapter
implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -8192200119980300275L;


    public static final String LANENAME = "StoreDeleteChosenAisle";

    //--------------------------------------------------------------------------
    /**
       Delete the reason code indicated by the user, if it is not the only one.
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
        Letter letter = new Letter(ReasonCodesCommon.CONFIRM_DELETE);

        POSUIManagerIfc ui
            = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ListEditorCargo cargo = (ListEditorCargo)bus.getCargo();
        cargo.setOperationRequested(bus.getCurrentLetter().getName());

        ReasonCodeGroupBeanModel model =
            (ReasonCodeGroupBeanModel)
            ui.getModel(POSUIManagerIfc.PARAM_VALUE_LIST);

        ReasonCodeGroupBeanModel reasonCodeGroup = cargo.getReasonCodeGroup();
        Vector reasonCodes = reasonCodeGroup.getReasonCodes();

        // Select something from the nondeleted items
        if (reasonCodes.size() > 0)
        {
            int selectedIndex = model.getReasonCodeSelectionIndex();

            // If nothing is selected, presume the first one
            if (selectedIndex < 0)
            {
                selectedIndex = 0;
            }

            // Prepare to delete the reason code indicated by the user
            ReasonCode reason
                = (ReasonCode)reasonCodes.elementAt(selectedIndex);
            cargo.setReasonCode(reason);
        }
        else
        {
            cargo.setReasonCode(null);
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
