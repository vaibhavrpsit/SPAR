/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/manager/registerreports/QueTransReportSelectedAilse.java /main/10 2011/02/16 09:13:27 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:33 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:29 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:32 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/04/09 16:56:02  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:50:59  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:46  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Nov 04 2003 13:42:58   rrn
 * Changed code to setFunctionAccessID, setReportType and to send "Continue" letter.
 * 
 *    Rev 1.0   Aug 29 2003 16:01:22   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:19:02   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:36:36   msg
 * Initial revision.
 * 
 *    Rev 1.1   19 Jan 2002 13:48:28   pdd
 * Added header information to the report.
 * Resolution for POS SCR-370: 5.0 Summary report tender summary updates
 * 
 *    Rev 1.0   Sep 21 2001 11:24:16   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:12   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.manager.registerreports;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * User has chosen to print the Queued Transactions report.
 * 
 * @version $Revision: /main/10 $
 */
public class QueTransReportSelectedAilse extends LaneActionAdapter
{
    private static final long serialVersionUID = 6727038333054804966L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(QueTransReportSelectedAilse.class);

    public static final String LANENAME = "QueTransReportSelectedAilse";

    /**
     * Create the report and put it in the cargo.
     * 
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
        RegisterReportsCargo cargo = (RegisterReportsCargo) bus.getCargo();
        cargo.setAccessFunctionID(RoleFunctionIfc.REPORT_QUEUE_TRANSACTION);
        cargo.setReportType(RegisterReportsCargo.REPORT_QUETRANS);
        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }
}