/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/printing/PrintingLaunchShuttle.java /main/10 2011/02/16 09:13:32 cgreene Exp $
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
 *    5    360Commerce 1.4         2/21/2006 4:43:03 AM   Akhilashwar K. Gupta
 *         CR-6092: Previous changes were crashing the POS. Corrected the
 *         problem now.
 *    4    360Commerce 1.3         2/10/2006 11:06:45 AM  Deepanshu       CR
 *         6092: Sales Assoc sould be last 4 digits of Sales Assoc ID and not
 *         of Cashier ID on the recipt
 *    3    360Commerce 1.2         3/31/2005 4:29:29 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:23 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:26 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:17  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:01  cdb
 *   @scr 4302 Removed double semicolon warnings.
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
 *
 * 
 *    Rev 1.1   08 Nov 2003 01:39:26   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.0   Aug 29 2003 16:05:32   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jan 13 2003 15:27:38   RSachdeva
 * Using UtilityManagerIfc.getCodeListMap() 
 * Resolution for POS SCR-1907: Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 * 
 *    Rev 1.0   Apr 29 2002 15:07:38   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:44:34   msg
 * Initial revision.
 * 
 *    Rev 1.1   Feb 27 2002 17:27:30   mpm
 * Restructured end-of-transaction processing.
 * Resolution for POS SCR-1440: Enhance end-of-transaction processing for performance reasons
 *
 *    Rev 1.0   Sep 21 2001 11:22:48   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:11:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.printing;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.TenderableTransactionCargoIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

/**
 * Shuttle used to launch the printing service.
 * 
 * @version $Revision: /main/10 $
 */
public class PrintingLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 9218330229065219843L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(PrintingLaunchShuttle.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/10 $";

    /**
     * Calling service's cargo
     */
    protected TenderableTransactionCargoIfc cargo = null;

    /**
     * Calling service's saleCargo
     */
    protected SaleCargoIfc saleCargo = null;

    /**
     * Loads the shuttle data from the cargo.
     * 
     * @param bus Service Bus
     */
    @Override
    public void load(BusIfc bus)
    {
        cargo = (TenderableTransactionCargoIfc) bus.getCargo();
        if (bus.getCargo() instanceof SaleCargoIfc)
        {
            saleCargo = (SaleCargoIfc) bus.getCargo();
        }
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
        TenderableTransactionIfc transaction = cargo.getTenderableTransaction();
        if (saleCargo != null)
        {
            transaction.setSalesAssociate(saleCargo.getSalesAssociate());
        }
        printingCargo.setTransaction(transaction);
        printingCargo.setTillID(cargo.getTillID());
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
