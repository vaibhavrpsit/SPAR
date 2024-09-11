/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/listeditor/DeletionConfirmedAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:04 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:43 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:54 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:33 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:52:22   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:40:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:03:24   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:18:42   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:11:24   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:05:28   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.listeditor;

import java.util.Vector;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.beans.ReasonCode;
import oracle.retail.stores.pos.ui.beans.ReasonCodeGroupBeanModel;
import oracle.retail.stores.pos.ui.beans.ReasonCodesCommon;

//------------------------------------------------------------------------------
/**
    Delete the reason code from the group.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class DeletionConfirmedAisle extends PosLaneActionAdapter
{

    public static final String LANENAME = "DeletionConfirmedAisle";

    //--------------------------------------------------------------------------
    /**
       Delete the reason code from the group.
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        ListEditorCargo cargo = (ListEditorCargo)bus.getCargo();
        ReasonCodeGroupBeanModel reasonCodeGroup = cargo.getReasonCodeGroup();
        Vector reasonCodes = reasonCodeGroup.getReasonCodes();
        ReasonCode reason = cargo.getReasonCode();
        if (reason.getModifyingParameter())
        {
            reasonCodes.removeElement(reason);
        }
        else
        {
            // Set the state to disabled (deleted), and move the reason code to the
            // end of the vector.  In this way the index of the items in the vector
            // will match the order of the entries visible to the user.
            reason.setEnabled(false);
            reasonCodes.removeElement(reason);
            reasonCodes.addElement(reason);
        }

        // If we're deleting the default, arbitrarily choose another
        if (reason.getReasonCodeName().equals(reasonCodeGroup.getDefaultReasonCode()))
        {
            ReasonCode newDefault = (ReasonCode)reasonCodes.elementAt(0);
            reasonCodeGroup.setDefaultReasonCode(newDefault.getReasonCodeName());
        }

        reasonCodeGroup.setReasonCodeSelectionIndex(0);

        bus.mail(new Letter(ReasonCodesCommon.ACCEPT_DATA), BusIfc.CURRENT);

    }
}
