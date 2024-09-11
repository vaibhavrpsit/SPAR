/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeeadd/EmployeeMatchesFoundSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:09 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    cgreene   02/04/09 - updated UIManager call to showScreen
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:58 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:20 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:50 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/06/23 15:09:04  awilliam
 *   @scr 5269, 5727, Pressing add on the add temp screen does not end use case, and Employee duplicate name confirmation is not displayed when adding temp employee with duplicate name
 *
 *   Revision 1.3  2004/02/12 16:50:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:49:24  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:59:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:24:08   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:32:12   msg
 * Initial revision.
 * 
 *    Rev 1.1   31 Jan 2002 15:37:42   baa
 * fix select employe screens
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   Sep 21 2001 11:23:22   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:46   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeeadd;

import java.util.Vector;

import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.EmployeeTypeEnum;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.DualListBeanModel;

/**
 * The EmployeeMatchesFound site allows the user to view the employees who match
 * the name entered for the new employee. If the user decides that the employee
 * has already been added the user can press Accept to continue to add the new
 * name, or Undo to return to the Employee Options.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class EmployeeMatchesFoundSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 3997942187688899232L;

    public static final String SITENAME = "EmployeeMatchesFoundSite";

    /**
     * The EmployeeMatchesFound site allows the user to view the employees who
     * match the name entered for the new employee. If the user decides that the
     * employee has already been added the user can press Accept to continue to
     * add the new name, or Undo to return to the Employee Options.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // Retrieve the data from cargo
        EmployeeCargo cargo = (EmployeeCargo)bus.getCargo();

        // Display the possible matches
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        if (cargo.getAddEmployeeType() == EmployeeTypeEnum.TEMPORARY)
        {
            DialogBeanModel model = new DialogBeanModel();

            model.setResourceID("DuplicateEmpName");
            model.setType(DialogScreensIfc.YES_NO);

            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        else
        {
            // setup model to display data using new UI format
            DualListBeanModel model = new DualListBeanModel();
            Vector<EmployeeIfc> topList = new Vector<EmployeeIfc>();
            topList.addElement(cargo.getEmployee());
            model.setTopListModel(topList);
            model.setListModel(cargo.getEmployeeList());

            ui.setModel(POSUIManagerIfc.EMPLOYEE_SELECT_ADD, model);
            ui.showScreen(POSUIManagerIfc.EMPLOYEE_SELECT_ADD);
        }
    }
} // end class EmployeeMatchesFoundSite
