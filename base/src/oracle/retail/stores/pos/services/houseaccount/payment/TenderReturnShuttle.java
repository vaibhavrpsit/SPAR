/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/houseaccount/payment/TenderReturnShuttle.java /rgbustores_13.4x_generic_branch/2 2011/07/26 16:57:40 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/26/11 - repacked into houseaccount.payment
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:26 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:02 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:56 PM  Robert Pearse   
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
 *    Rev 1.3   Nov 04 2003 11:22:06   epd
 * Updates for repackaging
 * 
 *    Rev 1.2   Oct 23 2003 17:24:48   epd
 * Updated to use renamed ADO packages
 * 
 *    Rev 1.1   Oct 17 2003 13:01:28   epd
 * Updated for new ADO tender service
 * 
 *    Rev 1.0   Aug 29 2003 16:04:04   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:11:18   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:42:14   msg
 * Initial revision.
 * 
 *    Rev 1.1   Feb 05 2002 16:43:00   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 * 
 *    Rev 1.0   Sep 21 2001 11:32:24   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:10:46   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.houseaccount.payment;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.transaction.PaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

/**
 * This shuttle copies information from the cargo used in the Tender service to
 * the cargo used in the POS service.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
public class TenderReturnShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -439006443929432128L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(TenderReturnShuttle.class);

    /**
     * revision number supplied by Team Connection
     **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";
    /**
     * transaction
     */
    private TenderableTransactionIfc transaction;
    /**
     * store financial status
     */
    private StoreStatusIfc storeStatus;
    /**
     * register financial status
     */
    private RegisterIfc register;
    /**
     * Not opening the cash drawer, use this to make sure the user has time to
     * press Enter before leaving.
     */
    private boolean waitForNext = false;

    /**
     * Loads cargo from tender service. Cargo will contain the selected item.
     * 
     * @param bus Service Bus
     */
    @Override
    public void load(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        transaction = (TenderableTransactionIfc)((ADO)cargo.getCurrentTransactionADO()).toLegacy();
        storeStatus = cargo.getStoreStatus();
        register = cargo.getRegister();
    }

    /**
     * Loads cargo for POS service. The cargo will contain the selected item.
     * 
     * @param bus Service Bus
     */
    @Override
    public void unload(BusIfc bus)
    {
        PayHouseAccountCargo cargo = (PayHouseAccountCargo)bus.getCargo();
        cargo.setTransaction((PaymentTransactionIfc)transaction);
        cargo.setStoreStatus(storeStatus);
        cargo.setRegister(register);
        cargo.setWaitForNext(waitForNext);
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