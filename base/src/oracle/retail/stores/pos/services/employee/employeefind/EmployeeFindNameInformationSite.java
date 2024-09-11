/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeefind/EmployeeFindNameInformationSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:08 mszekely Exp $
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
 *    4    360Commerce 1.3         1/10/2008 7:58:22 AM   Manas Sahu      Event
 *          Originator Changes
 *    3    360Commerce 1.2         3/31/2005 4:27:57 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:18 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:49 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:59:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Apr 11 2003 13:17:06   baa
 * remove usage of  deprecated EployeeIfc methods get/setName 
 * Resolution for POS SCR-2155: Deprecation warnings - EmployeeIfc
 * 
 *    Rev 1.0   Apr 29 2002 15:23:44   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:32:24   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:23:38   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:52   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeefind;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.EventOriginatorInfoBean;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.EmployeeLookupNameBeanModel;
//------------------------------------------------------------------------------
/**

       This site is used to enter employee name information
       which will be used to find the employee.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class EmployeeFindNameInformationSite extends PosSiteActionAdapter
{

    public static final String SITENAME = "EmployeeFindNameInformationSite";

    //--------------------------------------------------------------------------
    /**

       This site is used to enter employee name information
       which will be used to find the employee.


       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {

        // Need to change Cargo type to <ServiceName>Cargo
        EmployeeCargo cargo = (EmployeeCargo) bus.getCargo();

        // Need to change screen ID and bean type
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        EmployeeLookupNameBeanModel model = new EmployeeLookupNameBeanModel();
        if (cargo.getEmployee() != null )
        {
            model.setFirstName(cargo.getEmployee().getPersonName().getFirstName());
            model.setLastName(cargo.getEmployee().getPersonName().getLastName());
        }

        // Set the event originator class as this class
        EventOriginatorInfoBean.setEventOriginator("EmployeeFindNameInformationSite.arrive");
        ui.showScreen(POSUIManagerIfc.EMPLOYEE_FIND_NAME, model);

    }
}// end EmployeeFindNameInformationSite
