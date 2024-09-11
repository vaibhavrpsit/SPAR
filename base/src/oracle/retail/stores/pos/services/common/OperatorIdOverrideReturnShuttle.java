/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/OperatorIdOverrideReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:52 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:12 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:49 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:50 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/27 22:32:03  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.5  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
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
 *    Rev 1.0   Apr 29 2002 15:35:36   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:10:04   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:23:10   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:13:32   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:06:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.operatorid.OperatorIdCargo;

/**
 * This shuttle carries the required contents from the OperatorId service to the
 * calling service.
 * <P>
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 **/
public class OperatorIdOverrideReturnShuttle implements ShuttleIfc
{
    static final long serialVersionUID = 1980548221949579326L;

    /**
     * The logger to which log messages will be sent.
     **/
    protected static final Logger logger = Logger.getLogger(OperatorIdOverrideReturnShuttle.class);

    /**
     * revision number supplied by source-code-control system
     **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * class name constant
     **/
    public static final String SHUTTLENAME = "OperatorIdOverrideReturnShuttle";

    // Child Service cargo
    protected OperatorIdCargo opIDCargo = null;

    /**
     * Copies information from the Operator ID cargo into the shuttle.
     * 
     * @param bus the bus being loaded
     **/
    public void load(BusIfc bus)
    {
        opIDCargo = (OperatorIdCargo) bus.getCargo();
    }

    /**
     * Copies information to the calling service's cargo
     * 
     * @param bus the bus being unloaded
     **/
    public void unload(BusIfc bus)
    {
        AbstractFinancialCargo cargo = (AbstractFinancialCargo) bus.getCargo();
        cargo.setDataExceptionErrorCode(opIDCargo.getErrorType());
    }

    /**
     * Retrieves the source-code-control system revision number.
     * <P>
     * 
     * @return String representation of revision number
     **/
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
