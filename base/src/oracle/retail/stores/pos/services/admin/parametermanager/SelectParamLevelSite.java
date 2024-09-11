/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametermanager/SelectParamLevelSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:05 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:54 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:09 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:08 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:48:50  mcs
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
 *    Rev 1.0   Aug 29 2003 15:52:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:39:30   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:05:10   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:19:54   msg
 * Initial revision.
 * 
 *    Rev 1.2   06 Mar 2002 16:29:52   baa
 * Replace get/setAccessEmployee with get/setOperator
 * Resolution for POS SCR-802: Security Access override for Reprint Receipt does not journal to requirements
 *
 *    Rev 1.1   Jan 19 2002 10:28:02   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   Sep 21 2001 11:11:44   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:05:36   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.parametermanager;

// java imports
import java.util.Vector;

import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;
import oracle.retail.stores.pos.ui.beans.ParametersCommon;
//------------------------------------------------------------------------------
/**
    Displays the screen that allows the user to select the location level
    (e.g., corporation or store) to which the parameters will apply.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class SelectParamLevelSite extends PosSiteActionAdapter
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
        Sets up the UI to display the potential location levels
        (e.g., corporation or store) to which the parameters will apply.
        <p>
        @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        // We are starting afresh.
        ParameterCargo cargo = (ParameterCargo)bus.getCargo();
        cargo.reset();

        /* NOTE: In the future, we will loop through the keys to the
         * sources Hashtable, and extract the location level name (the
         * first part of the key (category.alternative pair).
         */
        Vector vChoices = new Vector();

        EmployeeIfc employee = cargo.getOperator();

        vChoices.addElement(ParametersCommon.USER_CORPORATION);
        vChoices.addElement(ParametersCommon.USER_STORE);

        // Set the access employee to the sales associate to prevent unwanted
        // access to corp parameters
        //cargo.setAccessEmployee(cargo.getSecurityOverrideRequestEmployee());

        POSUIManagerIfc ui= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        DataInputBeanModel beanModel = new DataInputBeanModel();
        beanModel.setSelectionChoices("choiceList", vChoices);
        beanModel.setSelectionValue("choiceList", (String)vChoices.firstElement());

        // display the UI screen
        ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.PARAM_SELECT_LEVEL, beanModel);

    }
}
