/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/TillOptionsLaunchShuttle.java /main/10 2011/02/16 09:13:33 cgreene Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:30 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:13 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:06 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/27 22:32:03  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.5  2004/09/23 00:07:17  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:49:34  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:48:14  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:56:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Jan 12 2003 16:03:56   pjf
 * Remove deprecated calls to AbstractFinancialCargo.getCodeListMap(), setCodeListMap().
 * Resolution for 1907: Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 * 
 *    Rev 1.1   Jan 12 2003 14:48:44   pjf
 * Remove new AbstractFinancialCargo().
 * Resolution for 1908: Remove "new AbstractFinancialCargo()" in TillOptionsLaunchShuttle.
 * 
 *    Rev 1.0   Apr 29 2002 15:25:08   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:26:04   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:16:10   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:16   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.dailyoperations.till.tilloptions.TillOptionsCargo;

/**
 * This shuttle carries the required contents from the Daily Operations service
 * to the Till Options service.
 * 
 * @version $Revision: /main/10 $
 */
public class TillOptionsLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 3393854184142666999L;

    /**
     * revision number supplied by source-code-control system
     */
    public static String revisionNumber = "$Revision: /main/10 $";

    /**
     * class name constant
     */
    public static final String SHUTTLENAME = "TillOptionsLaunchShuttle";

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(TillOptionsLaunchShuttle.class);

    /**
     * create new PosCargo
     */
    protected AbstractFinancialCargo afCargo;

    /**
     * Copies information from the cargo used in the Daily Operations service.
     * 
     * @param bus the bus being loaded
     */
    @Override
    public void load(BusIfc bus)
    {
        afCargo = (AbstractFinancialCargo) bus.getCargo();
    }

    /**
     * Copies information to the cargo used in the Till Options service.
     * 
     * @param bus the bus being unloaded
     */
    @Override
    public void unload(BusIfc bus)
    {
        TillOptionsCargo toCargo = (TillOptionsCargo) bus.getCargo();
        toCargo.setStoreStatus(afCargo.getStoreStatus());
        toCargo.setRegister(afCargo.getRegister());
        toCargo.setTenderLimits(afCargo.getTenderLimits());
        toCargo.setOperator(afCargo.getOperator());
        toCargo.setLastReprintableTransactionID(afCargo.getLastReprintableTransactionID());
        toCargo.setSelectionType("Options");
    }

    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return revisionNumber;
    }
}
