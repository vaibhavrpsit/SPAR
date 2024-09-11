/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/storecredit/CaptureCustomerLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:47 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:20 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:59 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:48 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/07/15 18:30:52  jdeleau
 *   @scr 6119 Removed a number of unnecessary class casts which
 *   were causing ClassCastExceptions and made the product crash.
 *
 *   Revision 1.4  2004/07/01 14:00:57  jeffp
 *   @scr 5898 Added check to see what cargo was being passed in.
 *
 *   Revision 1.3  2004/06/19 17:08:31  khassen
 *   @scr 5684 - Feature enhancements for capture customer use case.  Added setTransaction() call.
 *
 *   Revision 1.2  2004/06/19 16:09:31  khassen
 *   @scr 5684 - Feature enhancements for capture customer use case.  Added setTransaction() call.
 *
 *   Revision 1.1  2004/04/13 16:30:07  bwf
 *   @scr 4263 Decomposition of store credit.
 *
 *   Revision 1.2  2004/02/27 01:32:10  nrao
 *   Fixed value of balance due being set in cargo.
 *
 *   Revision 1.1  2004/02/27 01:11:25  nrao
 *   Added Launch Shuttle from Tender to Customer Capture.
 *
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.storecredit;

// java imports
import java.util.HashMap;

// foundation imports
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

// domain imports
import oracle.retail.stores.domain.tender.TenderLineItemConstantsIfc;

// pos imports
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.services.tender.capturecustomerinfo.CaptureCustomerInfoCargo;

//--------------------------------------------------------------------------
/**
 *  This shuttle is used to go to the Capture Customer service. 
 *  $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//--------------------------------------------------------------------------
public class CaptureCustomerLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 7852987440066899335L;

    /** revision number * */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
     * shuttle name constant
     */
    public static final String SHUTTLENAME = "CaptureCustomerLaunchShuttle";
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
        cargo.setTenderType(TenderLineItemConstantsIfc.TENDER_TYPE_STORE_CREDIT);
        cargo.setCustomer(tenderCargo.getCustomer());
        HashMap tenderAttributes = tenderCargo.getTenderAttributes();
        cargo.setBalanceDue(tenderCargo.parseAmount((String) tenderAttributes.get(TenderConstants.AMOUNT)));
        cargo.setTransaction((TransactionIfc)tenderCargo.getCurrentTransactionADO().toLegacy());
    }

    //----------------------------------------------------------------------
    /**
     * Returns a string representation of this object.
     * 
     * @return String representation of object
     */
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult =
            new String("Class:  CaptureCustomerLaunchShuttle (Revision " + getRevisionNumber() + ")" + hashCode());
        return (strResult);
    }

    //----------------------------------------------------------------------
    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
