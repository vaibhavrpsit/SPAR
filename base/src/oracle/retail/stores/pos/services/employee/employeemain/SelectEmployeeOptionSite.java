/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeemain/SelectEmployeeOptionSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:09 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:53 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:08 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:08 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/14 21:24:26  tfritz
 *   @scr 3884 - New Training Mode Functionality
 *
 *   Revision 1.3  2004/02/12 16:50:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:49:04  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:59:32   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:23:32   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:32:44   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:23:46   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeemain;

import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//------------------------------------------------------------------------------
/**

    This site presents the menu containing the Add and Find options.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class SelectEmployeeOptionSite extends PosSiteActionAdapter
{

    public static final String SITENAME = "SelectEmployeeOptionSite";

    //--------------------------------------------------------------------------
    /**

       This site presents the menu containing the Add and Find options.

       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {

        // Need to change Cargo type to <ServiceName>Cargo
        EmployeeCargo cargo = (EmployeeCargo) bus.getCargo();
        POSBaseBeanModel          pModel = new POSBaseBeanModel();
        RegisterIfc register = cargo.getRegister();
        boolean trainingModeOn = false;
        
        if (register != null)
        {
            trainingModeOn = register.getWorkstation().isTrainingMode();
        }
        
        pModel.setInTraining(trainingModeOn);
        // Need to change screen ID and bean type
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.EMPLOYEE_OPTIONS, pModel);

    }
} // end class SelectEmployeeOptionSite
