/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeefind/NameInformationEnteredAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:08 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:07 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:39 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:44 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:59:22   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Apr 11 2003 13:17:08   baa
 * remove usage of  deprecated EployeeIfc methods get/setName 
 * Resolution for POS SCR-2155: Deprecation warnings - EmployeeIfc
 * 
 *    Rev 1.0   Apr 29 2002 15:23:52   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:32:30   msg
 * Initial revision.
 * 
 *    Rev 1.1   12 Mar 2002 16:52:46   pdd
 * Modified to use the factory.
 * Resolution for POS SCR-1332: Ensure domain objects are created through factory
 * 
 *    Rev 1.0   Sep 21 2001 11:23:40   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeefind;

// Foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.utility.PersonNameIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.EmployeeLookupNameBeanModel;

//------------------------------------------------------------------------------
/**

      The NameInformationEnteredAisle is traversed
      when the UI returns an Accept letter. The data is checked
      to see if one criterion is entered. If so, the data
      is saved and a Continue letter is mailed to continue.
      If not , an error message is presented to tell the user
      to enter a single search.

      @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class NameInformationEnteredAisle extends PosLaneActionAdapter 
{

    public static final String LANENAME = "NameInformationEnteredAisle";

    //--------------------------------------------------------------------------
    /**
       The NameInformationEnteredAisle is traversed
       when the UI returns an Accept letter. The data is checked
       to see if one criterion is entered. If so, the data
       is saved and a Continue letter is mailed to continue.
       If not , an error message is presented to tell the user
       to enter a single search.

       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        Letter result;
        EmployeeIfc newEmployee = DomainGateway.getFactory().getEmployeeInstance();
        PersonNameIfc name = DomainGateway.getFactory().getPersonNameInstance();
        EmployeeCargo cargo = (EmployeeCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        // check the search criteria
        // send the letter based on the result
        // get the model for the bean
        EmployeeLookupNameBeanModel model = (EmployeeLookupNameBeanModel)
            ui.getModel(POSUIManagerIfc.EMPLOYEE_FIND_NAME);
        name.setFirstName(model.getFirstName());
        name.setLastName(model.getLastName());
        newEmployee.setPersonName(name);
        cargo.setEmployee(newEmployee);
        result = new Letter(CommonLetterIfc.CONTINUE);
        // Mail the appropriate letter to continue
        bus.mail(result, BusIfc.CURRENT);
    }

}  // end class NameInformationEnteredAisle
