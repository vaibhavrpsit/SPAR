  /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  05/07/2013               Izhar              Changes done for BUG 6807
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.storecredit;

// java imports
import java.util.HashMap;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.tender.TenderLineItemConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.services.tender.capturecustomerinfo.CaptureCustomerInfoCargo;

//--------------------------------------------------------------------------
/**
 *  This shuttle is used to go to the Capture Customer service. 
 *  $Revision: 3$
 */
//--------------------------------------------------------------------------
public class MAXCaptureCustomerLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 7852987440066899335L;

    /** revision number * */
    public static final String revisionNumber = "$Revision: 3$";
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
        /**Setting up Customer Details**/
        if(tenderCargo.getCustomer() != null)
              cargo.setCustomer(tenderCargo.getCustomer());
        else
        {
        	CustomerIfc customer = DomainGateway.getFactory().getCustomerInstance();
        	customer.setFirstName((String) tenderCargo.getTenderAttributes().get(TenderConstants.FIRST_NAME));
        	customer.setLastName((String) tenderCargo.getTenderAttributes().get(TenderConstants.LAST_NAME));
        	customer.setCustomerID((String) tenderCargo.getTenderAttributes().get(TenderConstants.ID_TYPE));
        	cargo.setCustomer(customer);
        }
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
