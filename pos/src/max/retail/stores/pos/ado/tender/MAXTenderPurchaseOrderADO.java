/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
	Rev 1.3 22/7/2013    Jyoti, Bug 7247 - Hire Purchase: Application crash while doing post void of transaction completed using hire purchase
   Rev 1.2 28/06/2013     Jyoti Rawal , Pos crashing on Postvoid of HirePurchase Transaction
	Rev 1.1 01/06/2013     Jyoti Rawal , Bug 6090: Incorrect EJ of the transaction in which Hire Purchase is used as a tender type
  	Rev 1.0  08/May/2013	Jyoti Rawal, Initial Draft: Changes for Hire Purchase Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ado.tender;

import java.util.HashMap;

import max.retail.stores.domain.tender.MAXTenderPurchaseOrder;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.tender.TenderPurchaseOrderIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderPurchaseOrderADO;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
//--------------------------------------------------------------------------
/**
    This class is the purchase order ado that access the rdo.
    $Revision: 1.1 $
**/
//--------------------------------------------------------------------------
public class MAXTenderPurchaseOrderADO extends TenderPurchaseOrderADO
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8964666262499966177L;
	/**
       revision number
    **/
    public static final String revisionNumber = "$Revision: 1.1 $";
    
  
  
    
    //------------------------------------------------------------------------
    /**
        Get the tender attributes.
        @see com._360commerce.pos.ado.tender.TenderADOIfc#getTenderAttributes()
        @return map HashMap with attributes in it
    **/    
    //----------------------------------------------------------------------
    public HashMap getTenderAttributes()
    {
        HashMap map = new HashMap();
        map.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.PURCHASE_ORDER);
         map.put(TenderConstants.AMOUNT, 
         getAmount().getStringValue());
		// Rev 1.1 changes 
		//Rev 1.3 changes start
         if(((TenderPurchaseOrderIfc)tenderRDO).getPurchaseOrderNumber()!=null)
       map.put(TenderConstants.NUMBER, 
       new String (((TenderPurchaseOrderIfc)tenderRDO).getPurchaseOrderNumber()));
	//Rev 1.3 changes end
//        map.put(TenderConstants.NUMBER, 
//          new String (((TenderPurchaseOrderIfc)tenderRDO).getPurchaseOrderNumber()));
		//Rev 1.2 changes end
       //Rev 1.1 changes end
        if (((TenderPurchaseOrderIfc)tenderRDO).getFaceValueAmount() != null)
        {  
            map.put(TenderConstants.FACE_VALUE_AMOUNT, 
            ((TenderPurchaseOrderIfc)tenderRDO).getFaceValueAmount().getStringValue());
        }
        map.put(TenderConstants.AGENCY_NAME, 
          ((TenderPurchaseOrderIfc)tenderRDO).getAgencyName());
        map.put(TenderConstants.TAXABLE_STATUS, 
                ((TenderPurchaseOrderIfc)tenderRDO).getTaxableStatus());
        //Rev 1.0 changes start
          map.put(MAXTenderConstants.APPROVAL_CODE, 
                ((MAXTenderPurchaseOrder)tenderRDO).getApprovalCode());
          //Rev 1.0 changes end
        return map;
    }
    //------------------------------------------------------------------------
    /**
        Get the tender attributes.
        @see com._360commerce.pos.ado.tender.TenderADOIfc#setTenderAttributes(java.util.HashMap)
        @param tenderAttributes HashMap with attributes in it
    **/    
    //----------------------------------------------------------------------
    public void setTenderAttributes(HashMap tenderAttributes) throws TenderException
    {
        // get the amount
        CurrencyIfc amount = parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT));
        ((TenderPurchaseOrderIfc)tenderRDO).setAmountTender(amount);
        ((TenderPurchaseOrderIfc)tenderRDO).
                  setPurchaseOrderNumber((String)tenderAttributes.get(MAXTenderConstants.APPROVAL_CODE));
        if (tenderAttributes.get(TenderConstants.FACE_VALUE_AMOUNT) != null)
        {  
            ((TenderPurchaseOrderIfc)tenderRDO).setFaceValueAmount(parseAmount((String)tenderAttributes.get(TenderConstants.FACE_VALUE_AMOUNT)));
        }
        if (tenderAttributes.get(TenderConstants.AGENCY_NAME) != null)
        {
            ((TenderPurchaseOrderIfc)tenderRDO).setAgencyName((String)tenderAttributes.get(TenderConstants.AGENCY_NAME));        
        }
        if (tenderAttributes.get(TenderConstants.TAXABLE_STATUS) != null)
        {
            ((TenderPurchaseOrderIfc)tenderRDO).setTaxableStatus((String)tenderAttributes.get(TenderConstants.TAXABLE_STATUS));        
        }
        //Rev 1.0 changes start here
        if (tenderAttributes.get(MAXTenderConstants.APPROVAL_CODE) != null)
        {
            ((MAXTenderPurchaseOrder)tenderRDO).setApprovalCode((String)tenderAttributes.get(MAXTenderConstants.APPROVAL_CODE));        
        }
        //Rev 1.0 changes end here
    }
   
    
    
}
