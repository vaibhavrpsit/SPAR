/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeefind/DisplayPossibleMatchesAcceptAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:08 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:05 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:40 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/03 23:15:07  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:39:47  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:59:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:23:36   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:32:18   msg
 * Initial revision.
 * 
 *    Rev 1.2   04 Feb 2002 10:10:00   baa
 * uncomment cloning of employee object
 * Resolution for POS SCR-1026: Employee - search for employee causes system to freeze
 *
 *    Rev 1.1   31 Jan 2002 15:37:44   baa
 * fix select employe screens
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   Sep 21 2001 11:23:40   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:54   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeefind;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DualListBeanModel;

//------------------------------------------------------------------------------
/**
    The DisplayPossibleMatchesAccept  aisle is traversed when an accept letter
    is received indicating an employee was selected.  This employee will be saved
     in cargo and a continue letter will be mailed.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class DisplayPossibleMatchesAcceptAisle extends PosLaneActionAdapter
{


    public static final String LANENAME = "DisplayPossibleMatchesAcceptAisle";

    //--------------------------------------------------------------------------
    /**
       The DisplayPossibleMatchesAccept  aisle is traversed when an accept
       letter is received indicating an employee was selected.  This
       employee will be saved in cargo and a continue letter will be mailed.

       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {

        // default the letter to continue
        Letter letter= new Letter (CommonLetterIfc.CONTINUE);

        EmployeeIfc employee;              // employee object in the cargo
        EmployeeCargo cargo;

        // get the POS UI manager
        POSUIManagerIfc uiManager =
            (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        cargo = (EmployeeCargo) bus.getCargo();

        // Store the search criteria and mail a Continue letter.

        DualListBeanModel model = (DualListBeanModel) uiManager.getModel(POSUIManagerIfc.EMPLOYEE_SELECT_MODIFY);
        int selection = model.getSelectedRow();

        letter = new Letter(CommonLetterIfc.CONTINUE);

        // set the Employee in the cargo to the one selected by the operator

        cargo.setEmployee((EmployeeIfc)cargo.getEmployeeList().elementAt(selection));
        cargo.setOriginalEmployee((EmployeeIfc)cargo.getEmployee().clone());
        // mail the letter to move to next appropriate site
        bus.mail(letter, BusIfc.CURRENT);


    }

}  // end class DisplayPossibleMatchesAcceptAisle
