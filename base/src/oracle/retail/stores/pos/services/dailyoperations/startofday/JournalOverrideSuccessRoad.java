/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/startofday/JournalOverrideSuccessRoad.java /main/12 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:48 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:58 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:11 PM  Robert Pearse
 *
 *   Revision 1.3  2004/02/12 16:49:53  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:57:20   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:29:12   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:15:26   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:28:06   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:16:34   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.startofday;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This site writes the store status to the journal.
 * 
 * @version $Revision: /main/12 $
 */
public class JournalOverrideSuccessRoad extends LaneActionAdapter
{
    private static final long serialVersionUID = -134853845214206528L;
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * Journals the store status.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // Journal String
        // String text = null;
        StringBuffer text = new StringBuffer();

        // get the Journal manager

        JournalManagerIfc jmi;
        jmi = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

        StartOfDayCargo cargo = (StartOfDayCargo)bus.getCargo();

        // journal the store status
		Object[] dataArgs = new Object[2];
		text.append(I18NHelper.getString(
								I18NConstantsIfc.EJOURNAL_TYPE,
								JournalConstantsIfc.START_OF_DAY_SECURITY_OVERRIDE_LABEL,
								null));
		text.append(Util.EOL);
		dataArgs[0] = cargo.getOperator().getEmployeeID();
		text.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
				JournalConstantsIfc.CASHIER_CORP_LABEL, dataArgs));
		dataArgs[0] = cargo.getSalesAssociate().getEmployeeID();
		text.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
				JournalConstantsIfc.ACCESS_GIVEN_BY_EMPLOYEE_LABEL, dataArgs));

            jmi.journal(text.toString());
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
