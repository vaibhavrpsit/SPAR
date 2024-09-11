/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/role/setaccess/SetAccessDoneAisle.java /main/11 2013/03/22 16:31:08 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  03/19/13 - Restricting access point and role access based on
 *                         operators role
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:57 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:14 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:11 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:16  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/03/03 23:15:15  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:48:59  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:37:25  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:32   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Apr 23 2003 17:00:04   baa
 * allow for modifying roles
 * Resolution for POS SCR-2194: Secruity/Role not saving changes when Setting Access
 * 
 *    Rev 1.0   Apr 29 2002 15:37:50   msg
 * Initial revision.
 * 
 *    Rev 1.2   04 Apr 2002 15:22:12   baa
 * Remove references to Rolefunction descriptor array and maximun number of role functions
 * Resolution for POS SCR-1565: Remove references to RoleFunctionIfc.Descriptor Security Service
 *
 *    Rev 1.1   Mar 18 2002 23:07:16   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:21:14   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:12:58   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:13:10   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.role.setaccess;


import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.SetAccessSelectBeanModel;
//------------------------------------------------------------------------------
/** This aisle is traversed when the user presses the Done key on the
    'SetAccess' screen.
    @version $Revision: /main/11 $
**/
//------------------------------------------------------------------------------
public class SetAccessDoneAisle extends PosLaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 3176663177913408714L;

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";

    /**
       class name constant
    **/
    public static final String LANENAME = "SetAccessDoneAisle";

    //--------------------------------------------------------------------------
    /**
       Save the access values in the cargo and mail a success letter.
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        SetAccessSelectBeanModel beanModel = null;
        String[] functionAccess = null;
       

        // change Cargo type to SetAccessCargo
        SetAccessCargo cargo = (SetAccessCargo)bus.getCargo();

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        // get the model for the bean
        beanModel = (SetAccessSelectBeanModel)ui.getModel(POSUIManagerIfc.SET_ACCESS);

        // NOTE: we do not need to store the function Titles since
        // we never edit the titles.
        // get the edited role access values

        // Update selectedRole with new access values
        functionAccess = beanModel.getRoleFunctionAccess();
        RoleFunctionIfc[] roleFunction = cargo.getFilteredRoleFunctionsForRole();

        // save the edited role function access values in the cargo
        cargo.setNewFunctionAccess(functionAccess, utility, roleFunction);

        bus.mail(new Letter(CommonLetterIfc.NEXT), BusIfc.CURRENT);

    }
}

