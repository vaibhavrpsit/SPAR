/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/servicealert/OperatorIdReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:11 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:12 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:49 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:51 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/09/23 00:07:15  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.1  2004/05/13 14:10:00  awilliam
 *   @scr 4314 selcting Service Alert crashes Pos
 *
 *   Revision 1.4  2004/04/09 16:55:59  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:49:08  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:54:42   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Dec 16 2002 16:58:40   pdd
 * Added null check in unload().
 * Resolution for 1780: NullPointerException when backing out of Logoff and back to Sell Item
 * 
 *    Rev 1.0   Apr 29 2002 15:35:38   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:10:06   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:23:10   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:13:56   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:06:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.servicealert;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.operatorid.OperatorIdCargo;

/**
 * This shuttle carries the required contents from the OperatorId service to the
 * calling service.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 * @deprecated As of 14.1, this class is not used.  Using {@link oracle.retail.stores.pos.services.common.OperatorIdReturnShuttle} instead.
 */
@Deprecated
public class OperatorIdReturnShuttle implements ShuttleIfc
{
    static final long serialVersionUID = -2804939973718864166L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(OperatorIdReturnShuttle.class);

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * class name constant
     */
    public static final String SHUTTLENAME = "OperatorIdReturnShuttle";

    /**
     * Child service cargo
     */
    protected OperatorIdCargo opIDCargo = null;

    /**
     * Gets the cargo from the Operator ID service into the shuttle.
     * 
     * @param bus the bus being loaded
     */
    public void load(BusIfc bus)
    {
        opIDCargo = (OperatorIdCargo) bus.getCargo();
    }

    /**
     * Unloads the data from the shuttle to the calling service's cargo.
     * 
     * @param bus the bus being unloaded
     */
    public void unload(BusIfc bus)
    {
        ServiceAlertCargo cargo = (ServiceAlertCargo) bus.getCargo();
        cargo.setDataExceptionErrorCode(opIDCargo.getErrorType());
        EmployeeIfc employee = opIDCargo.getSelectedEmployee();

        if (employee != null)
        {
            cargo.setOperator(employee);
        }
    }

    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
