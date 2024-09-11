/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/security/EmployeeLaunchShuttle.java /main/12 2012/09/12 11:57:09 blarsen Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:57 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:19 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:49 PM  Robert Pearse
 *
 *   Revision 1.5  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:01  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:49:00  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:37:32  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:53:34   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:36:50   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:07:32   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:21:18   msg
 * Initial revision.
 *
 *    Rev 1.2   11 Mar 2002 17:18:54   sfl
 * Moved the "Entering Employee" e-journaling over here
 * so that the pair of message "Entering Employee/Exiting
 * Employee" will be e-journaled EVERY time the operation
 * enters into Employee service even if no Employee Add or
 * Employee Find is processed.
 * Resolution for POS SCR-1524: Entering and Exiting Employee is not being written to the E Journal
 *
 *    Rev 1.1   22 Jan 2002 17:23:20   baa
 * set operator
 * Resolution for POS SCR-309: Convert to new Security Override design.
 *
 *    Rev 1.0   Sep 21 2001 11:10:52   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:13:18   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.security;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeCargo;

/**
 * The SecurityLaunchShuttle moves data from the Admin service to the Security
 * service.
 * 
 * @version $Revision: /main/12 $
 */
public class EmployeeLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -8830110006249024658L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(EmployeeLaunchShuttle.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * class name constant
     */
    public static final String SHUTTLENAME = "EmployeeLaunchShuttle";

    /**
     * Source cargo
     */
    protected SecurityCargo sCargo = null;

    /**
     * @param bus the bus being loaded
     */
    @Override
    public void load(BusIfc bus)
    {
        sCargo = (SecurityCargo)bus.getCargo();
    }

    /**
     * @param bus the bus being unloaded
     */
    @Override
    public void unload(BusIfc bus)
    {
        StringBuilder sb = new StringBuilder();
        EmployeeCargo cargo = (EmployeeCargo)bus.getCargo();
        sb.append(Util.EOL);
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ENTERING_EMPLOYEE_LABEL, null));
        cargo.setRegister(sCargo.getRegister());
        cargo.setStoreStatus(sCargo.getStoreStatus());
        cargo.setOperator(sCargo.getOperator());

        // journal Entering Employee
        JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

        if (journal != null)
        {
            journal.journal(sb.toString());
        }
        else
        {
            logger.warn("No journal manager found!");
        }
    }

    /**
     * Returns a string representation of this object.
     * 
     * @param none
     * @return String representation of object
     */
    public String toString()
    {
        // result string
        String strResult = new String("Class: " + SHUTTLENAME + " (Revision " + getRevisionNumber() + ")" + hashCode());

        // pass back result
        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @param none
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}