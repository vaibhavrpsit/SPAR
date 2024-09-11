/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.   
*    
*    Rev 1.8  	01/06/2019     	Purushotham Reddy       Changes done for POS-Amazon Pay Integration
*    Rev 1.7  	14/07/2016     	Abhishek Goyal    		Initial Draft: Changes for CR
*	 Rev 1.6  	02/Jul/2013		Jyoti Rawal, 			Fix for Bug Bug 6804 - POS crashes when spit tendered with Hire Purchase and deleted the tender line item 
*	 Rev 1.5  	22/May/2013		Jyoti Rawal, 			Changes for Credit Card FES
*    Rev 1.4  	20/May/2013		Prateek					Changes done for TIC Customer Integration.
*    Rev 1.3  	07/May/2013		Jyoti Rawal, 			Changes for Hire Purchase Functionality
*    Rev 1.2  	29/Apr/2013		Himanshu, 				Store Credit Functionality 
*    Rev 1.1  	24/Apr/2013		Jyoti Rawal, 			Initial Draft: Changes for Gift Card Functionality 
*    Rev 1.0  	23/Apr/2013		Tanmaya, 				Initial Draft: Changes for Coupon Functionality 
*   
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ado.tender;

import java.util.HashMap;

import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.pos.ado.tender.AbstractTenderADO;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderFactory;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;

public class MAXTenderFactory extends TenderFactory {
	 
	protected TenderADOIfc getTenderADO(TenderTypeEnum tenderType) 
	    {
	        TenderADOIfc tender = null;
	         if(tenderType == TenderTypeEnum.COUPON)
	        	 tender = new MAXTenderCouponADO();
	         //Rev 1.1 changes start here
			else if (tenderType == TenderTypeEnum.GIFT_CARD)
    		
            {
                tender = new MAXTenderGiftCardADO();
            }
	         //Rev 1.1 changes end here

         // Rev 1.2 starts here
	     else if (tenderType == TenderTypeEnum.STORE_CREDIT || tenderType == MAXTenderTypeEnum.STORE_CREDIT)
		
        {
            tender = new MAXTenderStoreCreditADO();
        }
		// Rev 1.2 ends here
		      //Rev 1.3 changes start here
			else if (tenderType == MAXTenderTypeEnum.PURCHASE_ORDER) // rev 1.6 change
		    {
		            tender = new MAXTenderPurchaseOrderADO();
		    }
	         //Rev 1.3 changes end here
                 /**MAX Rev 1.4 Change : Start **/
			else if (tenderType == MAXTenderTypeEnum.LOYALTY_POINTS)
			{
				tender = new MAXTenderLoyaltyPointsADO();
			}
	         /**MAX Rev 1.4 Change : End**/
			    /**
	          * Rev 1.5
	          * Credit Card Changes start
	          */
			else if (tenderType == TenderTypeEnum.CREDIT || tenderType == MAXTenderTypeEnum.CREDIT)
	    		
            {
                tender = new MAXTenderCreditADO();
            }
			else if (tenderType == MAXTenderTypeEnum.AMAZON_PAY)
			{
				tender = new MAXTenderAmazonPayADO();
			}
			else if (tenderType == MAXTenderTypeEnum.MOBIKWIK)
			{
				tender = new MAXTenderMobikwikADO();
			}
			else if (tenderType == MAXTenderTypeEnum.PAYTM)
			{
				tender = new MAXTenderPaytmADO();
			}
	         
	         
	         /**
	          * Rev 1.5
	          * Credit Card Changes end
	          */
	         //Rev 1.1 changes end here
	         /**MAX Rev 1.7 Change : Start **/
			else if (tenderType == MAXTenderTypeEnum.ECOM_PREPAID)
			{
				tender = new MAXTenderEComPrepaidADO();
			}
			else if (tenderType == MAXTenderTypeEnum.ECOM_COD)
			{
				tender = new MAXTenderEComCODADO();
			}
	         /**MAX Rev 1.7 Change : End**/
	         else
	        	 tender = super.getTenderADO(tenderType);
	        return tender;
	    }

       // Rev 1.2 starts here

		public TenderADOIfc createTender(HashMap tenderAttributes)
        throws TenderException
       {
       // assert(tenderAttributes.get(TenderConstants.TENDER_TYPE) != null) : "Must provide tender type";
        
        TenderTypeEnum tenderType = (TenderTypeEnum)tenderAttributes.get(TenderConstants.TENDER_TYPE);
                
        TenderADOIfc tender = getTenderADO(tenderType);
        ((AbstractTenderADO)tender).setTenderAttributes(tenderAttributes);
        return tender;
    }
	// Rev 1.2 ends here

      // Rev 1.2 starts here
	 public TenderADOIfc createTender(TenderLineItemIfc rdoObject)
    {
        TenderTypeEnum tenderType = MAXTenderTypeEnum.makeTenderTypeEnumFromRDO(rdoObject);
        
        // If using this method, one should _always_ have a tender type.
       // assert(tenderType != null);   
        return createTender(tenderType);
    }
    
    // Rev 1.2 starts here
    public TenderADOIfc createTender(TenderTypeEnum tenderType)
    {        
        TenderADOIfc tender = getTenderADO(tenderType);
        return tender;        
    }
	// Rev 1.2 ends here
}
