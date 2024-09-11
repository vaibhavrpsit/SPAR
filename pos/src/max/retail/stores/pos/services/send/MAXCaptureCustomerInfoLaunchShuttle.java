/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *  Rev 1.0		May 04, 2017		Kritica Agarwal 	GST Changes
 *
 ********************************************************************************/
package max.retail.stores.pos.services.send;


import max.retail.stores.pos.services.modifyitem.MAXItemCargo;
import max.retail.stores.pos.services.tender.capturecustomerinfo.MAXCaptureCustomerInfoCargo;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.send.CaptureCustomerInfoLaunchShuttle;

//--------------------------------------------------------------------------
/**
 * This shuttle is used to go to the customer service. $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//--------------------------------------------------------------------------
public class MAXCaptureCustomerInfoLaunchShuttle extends CaptureCustomerInfoLaunchShuttle
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 7574298528577519673L;

    /** revision number * */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
     * shuttle name constant
     */
    public static final String SHUTTLENAME = "CaptureCustomerInfoLaunchShuttle";
 
    protected MAXItemCargo itemCargo;
    
    //----------------------------------------------------------------------
    /**
     * Load a copy of ItemCargo into the shuttle for use in unload().
     * <p>
     * 
     * @param bus
     *            the bus being loaded
     */
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
    	 itemCargo = (MAXItemCargo) bus.getCargo();
    	
    }
    public void unload(BusIfc bus)
    {
        MAXCaptureCustomerInfoCargo cargo = (MAXCaptureCustomerInfoCargo) bus.getCargo();
        //Change for Rev 1.0 : Starts
        cargo.setSend(itemCargo.isSend());
      //Change for Rev 1.0 : Ends
        cargo.setTenderType(TransactionConstantsIfc.TYPE_SEND);
        if (itemCargo.getTransaction() != null)
        {
            cargo.setTransaction(itemCargo.getTransaction());
            cargo.setCustomer(itemCargo.getTransaction().getCustomer());
        }
    }

    
}
