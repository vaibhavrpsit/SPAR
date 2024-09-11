/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeeadd/EmployeeAddOptionsSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:09 mszekely Exp $
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
 *  4    360Commerce 1.3         2/27/2008 7:14:56 AM   Chengegowda Venkatesh
 *       Updated the origination point for Add Employee Audit log events
 *  3    360Commerce 1.2         3/31/2005 4:27:56 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:21:16 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:10:48 PM  Robert Pearse   
 *
 * Revision 1.7  2004/09/23 00:07:14  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.6  2004/05/07 13:59:50  tfritz
 * @scr 4457 Removed changes that were made for this SCR.
 * Requirements changed as this work was being done.
 *
 * Revision 1.4  2004/04/08 20:33:03  cdb
 * @scr 4206 Cleaned up class headers for logs and revisions.
 *
 * 
 * Rev 1.0 12 Dec 2003 14:02:26 jriggins Initial revision. 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeeadd;

// Foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.EventOriginatorInfoBean;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//------------------------------------------------------------------------------
/**
 * 
 * Displays the options for adding an employee including standard and
 * temporary.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------

public class EmployeeAddOptionsSite
    extends PosSiteActionAdapter
    implements SiteActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -4973217181095408232L;


    //--------------------------------------------------------------------------
    /**
     * Displays the Employee Add Options screen
     * 
     * @param bus the bus arriving at this site
     */
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        // Display the Employee Add Options screen
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.EMPLOYEE_ADD_OPTIONS);
        
        EventOriginatorInfoBean.setEventOriginator("EmployeeAddOptionsSite.arrive");
    }

}
