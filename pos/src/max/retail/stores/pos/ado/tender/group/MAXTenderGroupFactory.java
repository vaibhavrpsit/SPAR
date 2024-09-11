/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 * 
 *  Rev 1.3  	01/06/2019     		Purushotham Reddy    	Changes done for POS-Amazon Pay Integration
 *  Rev 1.2  	14/07/2016          Abhishek Goyal    		Initial Draft: Changes for CR
 *  Rev 1.1 	20/05/2013			Prateek					Initial Draft: Changes for TIC Customer Integration
 *  Rev 1.0 	29/April/2013       Himanshu                MAX-StoreCreditTender-FES_v1 2.doc requirement.
 *  
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.ado.tender.group;

import max.retail.stores.pos.ado.factory.MAXTenderGroupFactoryIfc;
import max.retail.stores.pos.ado.tender.MAXTenderTypeEnum;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc;
import oracle.retail.stores.pos.ado.tender.group.TenderGroupFactory;

/**
 * @author Himanshu
 *
 */
public class MAXTenderGroupFactory extends TenderGroupFactory 
	implements MAXTenderGroupFactoryIfc 
{

	public TenderGroupADOIfc createTenderGroup(TenderTypeEnum tenderType) 
	{
		TenderGroupADOIfc result = null;
		
        // MAX change for Rev 1.0 - Start
        if (tenderType == TenderTypeEnum.STORE_CREDIT)
		{
	        result = new MAXTenderGroupStoreCreditADO();
        }
        // MAX change for Rev 1.0 - End
        /**MAX Rev 1.1 Change : Start**/
        else if (tenderType == MAXTenderTypeEnum.LOYALTY_POINTS) 
		{
			result = new MAXTenderGroupLoyaltyPointsADO();
		}
                /**MAX Rev 1.1 Change : End**/
        /**MAX Rev 1.2 Change : Start**/
        else if (tenderType == MAXTenderTypeEnum.ECOM_PREPAID) 
		{
			result = new MAXTenderGroupEComPrepaidADO();
		}
        else if (tenderType == MAXTenderTypeEnum.PAYTM) 
  		{
  			result = new MAXTenderGroupPaytmADO();
  		}
        else if (tenderType == MAXTenderTypeEnum.ECOM_COD) 
		{
			result = new MAXTenderGroupEComCODADO();
		}
        
        else if (tenderType == MAXTenderTypeEnum.MOBIKWIK) 
  		{
  			result = new MAXTenderGroupMobikwikADO();
  		}
                /**MAX Rev 1.2 Change : End**/
        else if (tenderType == MAXTenderTypeEnum.AMAZON_PAY) 
  		{
  			result = new MAXTenderGroupAmazonPayADO();
  		}
		else 
		{
			result = super.createTenderGroup(tenderType);
		}
             return result;
	}	 
}
