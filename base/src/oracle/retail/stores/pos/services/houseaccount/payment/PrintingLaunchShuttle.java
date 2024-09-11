/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/houseaccount/payment/PrintingLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/07/26 16:57:40 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/26/11 - repacked into houseaccount.payment
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
 *   Revision 1.5  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:01  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:51:29  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:05  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Jan 19 2004 15:55:04   DCobb
 * Use TenderableTransactionCargoIfc from oracle/retail/stores/pos/services/common/.
 * Resolution for 3701: Timing problem can occur in CancelTransactionSite (multiple).
 * 
 *    Rev 1.0   Aug 29 2003 16:04:04   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jan 14 2003 08:40:40   RSachdeva
 * Replaced AbstractFinancialCargo.getCodeListMap()   by UtilityManagerIfc.getCodeListMap() 
 * Resolution for POS SCR-1907: Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 * 
 *    Rev 1.0   Apr 29 2002 15:11:16   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:42:10   msg
 * Initial revision.
 * 
 *    Rev 1.1   Feb 27 2002 17:27:28   mpm
 * Restructured end-of-transaction processing.
 * Resolution for POS SCR-1440: Enhance end-of-transaction processing for performance reasons
 *
 *    Rev 1.0   Sep 21 2001 11:32:22   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:46   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.houseaccount.payment;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.common.TenderableTransactionCargoIfc;
import oracle.retail.stores.pos.services.printing.PrintingCargo;

import org.apache.log4j.Logger;

/**
 * Shuttle used to launch the printing service.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class PrintingLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -150182673915363229L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(PrintingLaunchShuttle.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Launch service cargo
     */
    protected TenderableTransactionCargoIfc ttCargo = null;

    /**
     * Loads the shuttle data from the cargo.
     * 
     * @param bus Service Bus
     */
    @Override
    public void load(BusIfc bus)
    {
        ttCargo = (TenderableTransactionCargoIfc) bus.getCargo();
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
        printingCargo.setTransaction(ttCargo.getTenderableTransaction());
        printingCargo.setTillID(ttCargo.getTillID());
    }

    /**
     * Returns a string representation of this object.
     * 
     * @return String representation of object
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
