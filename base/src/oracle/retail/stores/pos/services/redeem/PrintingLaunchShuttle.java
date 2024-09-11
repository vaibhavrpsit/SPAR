/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/PrintingLaunchShuttle.java /main/11 2011/02/16 09:13:32 cgreene Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:30 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:23 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:26 PM  Robert Pearse
 *
 *   Revision 1.4  2004/09/23 00:07:16  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/04/21 15:08:58  blj
 *   @scr 3872 - cleanup from code review
 *
 *   Revision 1.2  2004/04/09 16:56:02  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.1  2004/03/22 17:26:42  blj
 *   @scr 3872 - added redeem security, receipt printing and saving redeem transactions.
 *
 *   Revision 1.3  2004/02/12 16:51:40  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.redeem;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.printing.PrintingCargo;

/**
 * Shuttle used to launch the printing service for Redeem transactions.
 *
 * @version $Revision: /main/11 $
 */
public class PrintingLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 7185192217801539858L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(PrintingLaunchShuttle.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/11 $";

    /**
     * Calling service's cargo
     */
    protected RedeemCargo cargo = null;

    /**
     * Loads the shuttle data from the cargo.
     *
     * @param bus Service Bus
     */
    @Override
    public void load(BusIfc bus)
    {
        cargo = (RedeemCargo) bus.getCargo();
    }

    /**
     * Unloads the shuttle data into the Printing cargo.
     *
     * @param bus Service Bus
     */
    @Override
    public void unload(BusIfc bus)
    {
        PrintingCargo printingCargo = (PrintingCargo) bus.getCargo();
        TenderableTransactionIfc trans = (TenderableTransactionIfc) ((ADO) cargo.getCurrentTransactionADO()).toLegacy();
        printingCargo.setTransaction(trans);
        printingCargo.setTillID(cargo.getTillID());
    }

    /**
     * Returns a string representation of this object.
     *
     * @return String representation of object
     */
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
