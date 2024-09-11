/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeemain/SelectEmployeeOptionUndoRoad.java /main/12 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/30/12 - get journalmanager from bus
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *        3    360Commerce 1.2         3/31/2005 4:29:53 PM   Robert Pearse
 *        2    360Commerce 1.1         3/10/2005 10:25:08 AM  Robert Pearse
 *        1    360Commerce 1.0         2/11/2005 12:14:08 PM  Robert Pearse
 *
 *       Revision 1.3  2004/02/12 16:50:19  mcs
 *       Forcing head revision
 *
 *       Revision 1.2  2004/02/11 21:49:04  rhafernik
 *       @scr 0 Log4J conversion and code cleanup
 *
 *       Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *       updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:59:32   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:23:34   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:32:44   msg
 * Initial revision.
 *
 *    Rev 1.0   11 Mar 2002 17:07:40   sfl
 * Initial revision.
 * Resolution for POS SCR-1524: Entering and Exiting Employee is not being written to the E Journal
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeemain;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * Write "Entering Employee" and "Existing Employee" to E-Journal
 * 
 * @version $Revision: /main/12 $
 **/
@SuppressWarnings("serial")
public class SelectEmployeeOptionUndoRoad extends PosLaneActionAdapter
{
    /**
     * class name constant
     */
    public static final String SITENAME = "SelectEmployeeOptionUndoRoad";
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * Mark selected items as send items
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        StringBuffer sb = new StringBuffer();
        EmployeeCargo cargo = (EmployeeCargo)bus.getCargo();

        // sb.append(Util.EOL + "Entering Employee");
        sb.append(I18NHelper
                .getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.EXITING_EMPLOYEE_LABEL, null));
        // journal Exiting Employee here
        JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

        if (journal != null)
        {

            if (cargo.getEmployee() == null)
            {
                journal.journal(sb.toString());
            }
            else if (cargo.getEmployee().getEmployeeID().equals(""))
            {
                journal.journal(sb.toString());
            }
        }
        else
        {
            logger.warn("No journal manager found!");
        }
    }
}
