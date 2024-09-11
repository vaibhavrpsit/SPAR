/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/CaptureRefundCustomerLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:46 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    asinton   06/11/09 - Changed getTransacstionTotals to
 *                         getTenderTransacstionTotals for obtaining the grand
 *                         total.
 *    asinton   06/11/09 - Improved robustness of setting the balance due on
 *                         the CaptureCustomerInfoCargo. If the transaction
 *                         from the tender cargo is null, then attempt to
 *                         retrieve the transaction from the transaction ADO.
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:21 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:59 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:49 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/06/23 00:46:11  blj
 *   @scr 5113 - added capture customer capability for redeem store credit.
 *
 *   Revision 1.4  2004/06/18 14:22:37  khassen
 *   @scr 5684 - Feature enhancements for capture customer use case.
 *
 *   Revision 1.3  2004/04/06 16:38:59  khassen
 *   @scr 4275 - fixed unload() so that customer info gets copied correctly.
 *
 *   Revision 1.2  2004/03/24 20:11:14  bwf
 *   @scr 3956 Code Review
 *
 *   Revision 1.1  2004/03/17 19:26:23  bwf
 *   @scr 3956 Update to check customer in return trans and update
 *                     refund options buttons.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.tender.capturecustomerinfo.CaptureCustomerInfoCargo;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

//--------------------------------------------------------------------------
/**
     This class is a shuttle to go to the capture customer service.
     $Revision: /rgbustores_13.4x_generic_branch/1 $
 **/
//--------------------------------------------------------------------------
public class CaptureRefundCustomerLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -3256947399865376884L;

    /** handle to the logger */
    protected static final Logger logger = Logger.getLogger(CaptureRefundCustomerLaunchShuttle.class);

    /** revision number * */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
     * shuttle name constant
     */
    public static final String SHUTTLENAME = "CaptureRefundCustomerLaunchShuttle";
    /**
     * tender cargo reference
     */
    protected TenderCargo tenderCargo;

    //----------------------------------------------------------------------
    /**
     * Load a copy of TenderCargo into the Shuttle
     *  
     * @param bus the bus being loaded
     */
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        tenderCargo = (TenderCargo) bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
     * Make a CaptureCustomerInfoCargo and populate it.
     * 
     * @param bus the bus being unloaded
     */
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        CaptureCustomerInfoCargo cargo = (CaptureCustomerInfoCargo) bus.getCargo();
        cargo.setTransaction(tenderCargo.getTransaction());
        cargo.setTenderType(cargo.getTenderType());
        // Make transaction generic to handle redeem transactions.
        cargo.setCustomer(tenderCargo.getCurrentTransactionADO().getCustomer());

        if(tenderCargo.getTransaction() != null)
        {
            cargo.setBalanceDue(tenderCargo.getTransaction().getTenderTransactionTotals().getGrandTotal());
        }
        else if(tenderCargo.getCurrentTransactionADO().toLegacy() instanceof TenderableTransactionIfc)
        {
            cargo.setBalanceDue(((TenderableTransactionIfc)tenderCargo.getCurrentTransactionADO().toLegacy()).getTenderTransactionTotals().getGrandTotal());
        }
        else
        {
            logger.info("Could not set the balance due on the CaptureCustomerInfoCargo");
        }
    }
}
