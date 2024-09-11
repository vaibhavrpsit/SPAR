/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/postvoid/PrintingLaunchShuttle.java /main/13 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:30 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:23 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:26 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/09/27 22:32:04  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.6  2004/09/23 00:07:15  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/07/23 22:17:26  epd
 *   @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 *   Revision 1.4  2004/04/09 16:56:01  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:48:15  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:28:20  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Nov 04 2003 11:16:06   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 23 2003 17:28:36   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 17 2003 13:03:24   epd
 * Initial revision.
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.postvoid;

import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.printing.PrintingCargo;

import org.apache.log4j.Logger;

/**
 * Shuttle used to launch the printing service.
 * 
 * @version $Revision: /main/13 $
 **/
public class PrintingLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -3504205411468997746L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(PrintingLaunchShuttle.class);
    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/13 $";
    /**
     * Calling service's cargo
     */
    protected AbstractFinancialCargo cargo = null;

    /**
     * Loads the shuttle data from the cargo.
     * 
     * @param bus Service Bus
     */
    @Override
    public void load(BusIfc bus)
    {
        cargo = (AbstractFinancialCargo)bus.getCargo();
    }

    /**
     * Unloads the shuttle data into the Printing cargo.
     * 
     * @param bus Service Bus
     */
    @Override
    public void unload(BusIfc bus)
    {
        PrintingCargo printingCargo = (PrintingCargo)bus.getCargo();
        RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
        TenderableTransactionIfc txnRDO = (TenderableTransactionIfc)((ADO)txnADO).toLegacy();
        printingCargo.setTransaction(txnRDO);
        printingCargo.setTillID(cargo.getRegister().getCurrentTillID());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = new String("Class:  PrintingLaunchShuttle (Revision " + getRevisionNumber() + ") @"
                + hashCode());
        // pass back result
        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

}