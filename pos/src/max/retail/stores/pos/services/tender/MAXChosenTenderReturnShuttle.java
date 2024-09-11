/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* 
*  Copyright (c) 2019 M SPAR HyperMarkets, Inc.    All Rights Reserved.
*  
*  Rev 	1.0       Tanmaya				24/05/2013			Initial Draft: Changes for Store Credit
*  Rev	1.1 	  Purushotham Reddy 	June 20, 2019		Changes for POS_Amazon Pay Integration 
*  
*  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender;

import max.retail.stores.pos.ado.tender.MAXTenderAmazonPayADO;
import max.retail.stores.pos.ado.tender.MAXTenderConstantsIfc;
import max.retail.stores.pos.ado.tender.MAXTenderPaytmADO;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.context.ContextFactory;
import oracle.retail.stores.pos.ado.context.TourADOContext;
import oracle.retail.stores.pos.services.tender.ChosenTenderReturnShuttle;


/**
 * Copies the transaction back, regardless of whether we
 * added a tender to it or not
 */
public class MAXChosenTenderReturnShuttle extends ChosenTenderReturnShuttle
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 1073198897365895172L;


    protected MAXTenderCargo childCargo;

    /* (non-Javadoc)
     * @see com.extendyourstore.foundation.tour.ifc.ShuttleIfc#load(com.extendyourstore.foundation.tour.ifc.BusIfc)
     */
    public void load(BusIfc bus)
    {
        childCargo = (MAXTenderCargo)bus.getCargo();
    }

    /* (non-Javadoc)
     * @see com.extendyourstore.foundation.tour.ifc.ShuttleIfc#unload(com.extendyourstore.foundation.tour.ifc.BusIfc)
     */
    public void unload(BusIfc bus)
    {
        MAXTenderCargo callingCargo = (MAXTenderCargo)bus.getCargo();
        callingCargo.setCurrentTransactionADO(childCargo.getCurrentTransactionADO());
        // this is to copy the customer over if it was gotten in the subtour.
        if(childCargo.getCustomer() != null)
        {
            callingCargo.setCustomer(childCargo.getCustomer());
        }

        callingCargo.setTenderADO(childCargo.getTenderADO());
        callingCargo.setLineDisplayTender(childCargo.getLineDisplayTender());

        callingCargo.setItemScanned(childCargo.isItemScanned());
        callingCargo.setItemQuantity(childCargo.getItemQuantity());
        
        //changes by Himanshu
        ((MAXTenderCargo)callingCargo).setStoreCreditExpirtDate((EYSDate)callingCargo.getTenderAttributes().get(MAXTenderConstantsIfc.STORE_CREDIT_EXPIRED));
        // Changes endded by Himanshu 
        
        callingCargo.setPreTenderMSRModel(childCargo.getPreTenderMSRModel());
        callingCargo.setRegister(childCargo.getRegister());
        callingCargo.setStoreStatus(childCargo.getStoreStatus());
        ((MAXTenderCargo) callingCargo).setGiftCardApproved(((MAXTenderCargo) childCargo).isGiftCardApproved());
        // Reset ADO context for calling service
        TourADOContext context = new TourADOContext(bus);
        context.setApplicationID(callingCargo.getAppID());
        ContextFactory.getInstance().setContext(context);
        
        
        // Changes start by Bhanu Priya   
        if(callingCargo.getLineDisplayTender() instanceof MAXTenderPaytmADO)
        {
        	callingCargo.setPaytmResp(childCargo.getPaytmResp());
        	callingCargo.setTransaction(childCargo.getTransaction());
        }
        
        // Changes for POS-Amazon PAy Integration @Purushotham
        
        if(callingCargo.getLineDisplayTender() instanceof MAXTenderAmazonPayADO)
        {
			callingCargo.setAmazonPayResp(childCargo.getAmazonPayResp());
			callingCargo.setTransaction(childCargo.getTransaction());
		}
        

    }

}
