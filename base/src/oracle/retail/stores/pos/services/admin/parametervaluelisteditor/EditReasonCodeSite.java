/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametervaluelisteditor/EditReasonCodeSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:04 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:53 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:12 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:44 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:53:04   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:38:38   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:05:48   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:20:06   msg
 * Initial revision.
 * 
 *    Rev 1.2   Mar 09 2002 14:16:48   mpm
 * Text externalization.
 *
 *    Rev 1.1   04 Feb 2002 13:29:50   KAC
 * Changed screen name to "Edit Parameter"
 * Resolution for POS SCR-1011: Edit Parameter screen has wrong screen name
 *
 *    Rev 1.0   22 Jan 2002 13:52:56   KAC
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:11:12   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:05:28   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.parametervaluelisteditor;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ReasonCode;
import oracle.retail.stores.pos.ui.beans.ReasonCodesCommon;

//------------------------------------------------------------------------------
/**
    Edits a reason code whose value is a string value.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class EditReasonCodeSite extends PosSiteActionAdapter
{

    //--------------------------------------------------------------------------
    /**
        Edits a reason code whose value is a string. <p>
        @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        ListEditorCargo cargo = (ListEditorCargo)bus.getCargo();
        cargo.setErrorMessage(null);
        ReasonCode beanModel = cargo.getReasonCode();

        // display the UI screen
        // This single site is used for both adds and edits.
        if (cargo.getOperationRequested() == ReasonCodesCommon.SHOW_ADD_SCREEN)
        {
            beanModel.setEditBean(false);
        }
        beanModel.setModifyingParameter(true);

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.PARAMETER_VALUE_EDIT, beanModel);
    }
}
