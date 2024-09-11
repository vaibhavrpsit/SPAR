/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeefind/RoleInformationEnteredAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:08 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:47 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:56 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:58 PM  Robert Pearse   
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
 *    Rev 1.0   Jan 26 2004 15:57:48   jriggins
 * Initial revision.
 * Resolution for 3597: Employee 7.0 Updates
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
import oracle.retail.stores.pos.ui.beans.EmployeeLookupRoleBeanModel;

//------------------------------------------------------------------------------
/**
    The RoleInformationEneteredAisle is traversed when an role is
    entered. The role is saved to cargo and a Continue letter is mailed.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class RoleInformationEnteredAisle extends PosLaneActionAdapter
{

    public static final String LANENAME = "RoleInformationEnteredAisle";

    //--------------------------------------------------------------------------
    /**
       The RoleInformationEneteredAisle is traversed when
       an role is entered. The role is saved to cargo and a Continue
       letter is mailed.

       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        Letter result = new Letter(CommonLetterIfc.CONTINUE);
        EmployeeCargo cargo = (EmployeeCargo) bus.getCargo();       
        
        /* setup the search criteria */          
        EmployeeIfc employeeCriteria = DomainGateway.getFactory().getEmployeeInstance();
        // blank name since we are search by role but needed to avoid 
        // NullPointerException
        PersonNameIfc name = DomainGateway.getFactory().getPersonNameInstance();
        name.setFullName("");
        employeeCriteria.setPersonName(name);
        // the user-selected role
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        EmployeeLookupRoleBeanModel beanModel = (EmployeeLookupRoleBeanModel)ui.getModel();
        employeeCriteria.setRole(beanModel.getSelectedRole());
        
        // Place in cargo. This also becomes the default for the top list 
        // when there is more than one possible match.
        cargo.setEmployee(employeeCriteria);
        
        // Mail the  letter to continue
        bus.mail(result, BusIfc.CURRENT);
    }
} // end class RoleInformationEnteredAisle
