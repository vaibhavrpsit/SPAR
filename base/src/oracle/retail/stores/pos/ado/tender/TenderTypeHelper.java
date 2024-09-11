package oracle.retail.stores.pos.ado.tender;
/* ===========================================================================
* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Log:$
 * ===========================================================================
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc.TenderType;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.utility.CardTypeCodesIfc;


/**
 * Base product implementation of the TenderTypeHelperIfc.
 * 
 */
public class TenderTypeHelper implements TenderTypeHelperIfc {
 
	 /* serial version id  */
	private static final long serialVersionUID = -6906349167196065371L;

	/* Logger */
	protected Logger logger = Logger.getLogger(TenderTypeHelper.class);
	
	/** Parameters loaded via Spring to create TenderTypeEnum instances */
	protected List<List<String>> tenderEnumParms = new ArrayList<List<String>>();
	
	/** Map of TenderType keyed by the name of the tender type enum */
	protected Map<String, TenderType> tdrTypeMap = new HashMap<String, TenderType>();
	
	
	/**
	 * Constructor for TenderTypeHelper
	 */
	public TenderTypeHelper()
	{
		setupTenderTypeMappings();
	}
	
	/**
	 * Create the mapping between the domain TenderType enumeration and the 
	 * TenderTypeEnum instances
	 */
	protected void setupTenderTypeMappings()
	{
		tdrTypeMap.put("Cash", TenderType.CASH);
		tdrTypeMap.put("Check",TenderType.CHECK);
		tdrTypeMap.put("Coupon", TenderType.COUPON);
		tdrTypeMap.put("Credit",TenderType.CREDIT);
		tdrTypeMap.put("Debit",TenderType.DEBIT);
		tdrTypeMap.put("GiftCard", TenderType.GIFT_CARD);
		tdrTypeMap.put("GiftCert", TenderType.GIFT_CERT);
		tdrTypeMap.put("MailCheck", TenderType.MAIL_CHECK);
		tdrTypeMap.put("PurchaseOrder",TenderType.PURCHASE_ORDER);
		tdrTypeMap.put("StoreCredit", TenderType.STORE_CREDIT);
		tdrTypeMap.put("TravCheck", TenderType.TRAVELERS_CHECK);
		tdrTypeMap.put("MallCert", TenderType.MALL_CERT);
		tdrTypeMap.put("MoneyOrder",TenderType.MONEY_ORDER);
		tdrTypeMap.put("HouseAccount", TenderType.HOUSE_ACCOUNT);
		tdrTypeMap.put("Alternate", TenderType.ALTERNATE);
	//	tdrTypeMap.put("Paytm", TenderType.PAYTM);
	//	tdrTypeMap.put("Mobikwik", TenderType.MOBIKWIK);
	}
	
	/**
	 * Determine the domain TenderType to use for a specific TenderTypeEnum declaration
	 * @param name of the application leve tender type enum
	 * @return domain TenderType reference assoicated with the applicaiton level TenderTypeEnum
	 */
	protected TenderType determineTenderType(String name)
	{
		return tdrTypeMap.get(name);
	}
	
	/**
	 * Return the domain TenderType to use for unmapped TenderTypeEnums
	 * @param name of TenderTypeEnum
	 * @return TenderType.ALTERNATE in default implementation
	 */
	protected TenderType determineUnmappedTenderType(String name)
	{
		return TenderType.ALTERNATE;
	}
	
	/**
	 * Sets the parameters for creating the TenderTypeEnum instances
	 * @param parms 2 dimensional array of string. [i][0] is the TenderTypeEnumeration name,
	 *  [i][1] is the name of the domain rdo class 
	 */
	public void setTenderEnumParameters(List<List<String>> parms)
	{
		tenderEnumParms = parms;
	}
	
	public List<List<String>> getTenderEnumParameters()
	{
		return tenderEnumParms;
	}
	
	@Override
	public TenderTypeEnum getTenderTypeEnum(
			Map<String, TenderTypeEnum> typeMap, TenderLineItemIfc rdoObject) {
		
        TenderTypeEnum result = null;
        
        switch (rdoObject.getTypeCode())
        {
	        case TenderLineItemIfc.TENDER_TYPE_MALL_GIFT_CERTIFICATE:
	        {
	        	result = getMallGiftCertificateEnum(typeMap, rdoObject);
	        	break;
	        }
	        case TenderLineItemIfc.TENDER_TYPE_GIFT_CERTIFICATE:
	        {
	        	result = getGiftCertificateEnum(typeMap, rdoObject);
	        	break;
	        }
	        case TenderLineItemIfc.TENDER_TYPE_CHARGE:
	        {
	        	result = getChargeEnum(typeMap, rdoObject);
	        	break;
	        }
	        default:
	        {
	        	result = getRDOTypeEnum(typeMap, rdoObject); 
	        }
        }
        
        /* OLD IMPLEMENTATION - REFACTORED INTO MULTIPLE PROTECTED METHODS TO ALLOW EXTENSION
        // Since there is not a 1:1 relationship between TenderTypEnum and Domain Objects, must make this check.
        if (rdoObject.getTypeCode() == TenderLineItemIfc.TENDER_TYPE_MALL_GIFT_CERTIFICATE)
        {
            result = MALL_CERT;
        }
        else if (rdoObject.getTypeCode() == TenderLineItemIfc.TENDER_TYPE_GIFT_CERTIFICATE)
        {
            result = GIFT_CERT;
        }
        else if (rdoObject.getTypeCode() == TenderLineItemIfc.TENDER_TYPE_CHARGE)
        {
            TenderChargeIfc tenderCharge = (TenderChargeIfc)rdoObject;
            if (CardTypeCodesIfc.HOUSE_CARD.equals(tenderCharge.getCardType()))
            {
                result = HOUSE_ACCOUNT;
            }
            else
            {
                result = CREDIT;
            }
        }
        else
        {
            loop:
                while (iter.hasNext())
                {
                    TenderTypeEnum typeEnum = (TenderTypeEnum)iter.next();

                    // if the test type is equal to or is a subclass of
                    // the current RDO type, return true;
                    // first check the type itself
                    if (rdoObject.getClass().equals(typeEnum.rdoType))
                    {
                        result = typeEnum;
                        break loop;
                    }

                    // next check the interfaces
                    Class[] interfaces = rdoObject.getClass().getInterfaces();
                    for (int i=0; i<interfaces.length; i++)
                    {
                        if (interfaces[i].equals(typeEnum.rdoType))
                        {
                            result = typeEnum;
                            break loop;
                        }
                    }
                }
        }
 
           	 * 
    	 */
		
		return result;
	}

	protected TenderTypeEnum getMallGiftCertificateEnum(Map<String, TenderTypeEnum> typeMap, TenderLineItemIfc rdoObject)
	{
		return TenderTypeEnum.MALL_CERT;
	}
	
	protected TenderTypeEnum getGiftCertificateEnum(Map<String, TenderTypeEnum> typeMap, TenderLineItemIfc rdoObject)
	{
		return TenderTypeEnum.GIFT_CERT;
	}
	
	protected TenderTypeEnum getChargeEnum(Map<String, TenderTypeEnum> typeMap, TenderLineItemIfc rdoObject)
	{
		TenderTypeEnum result = null;
		
		TenderChargeIfc tenderCharge = (TenderChargeIfc)rdoObject;
        if (CardTypeCodesIfc.HOUSE_CARD.equals(tenderCharge.getCardType()))
        {
            result = TenderTypeEnum.HOUSE_ACCOUNT;
        }
        else
        {
            result = TenderTypeEnum.CREDIT;
        }
		return result;
	}
	
	protected TenderTypeEnum getRDOTypeEnum(Map<String, TenderTypeEnum> typeMap, TenderLineItemIfc rdoObject)
	{
        Iterator<TenderTypeEnum> iter = typeMap.values().iterator();
        TenderTypeEnum result = null;
        
        loop:
            while (iter.hasNext())
            {
                TenderTypeEnum typeEnum = (TenderTypeEnum)iter.next();

                // if the test type is equal to or is a subclass of
                // the current RDO type, return true;
                // first check the type itself
                if (rdoObject.getClass().equals(typeEnum.rdoType))
                {
                    result = typeEnum;
                    break loop;
                }

                // next check the interfaces
                Class<?>[] interfaces = rdoObject.getClass().getInterfaces();
                for (int i=0; i<interfaces.length; i++)
                {
                    if (interfaces[i].equals(typeEnum.rdoType))
                    {
                        result = typeEnum;
                        break loop;
                    }
                }
            }
		return result;
	}

	
	
	@Override
	public List<TenderTypeEnumParameters> getTenderTypeEnumParameters() {
		List<TenderTypeEnumParameters> parameters = new ArrayList<TenderTypeEnumParameters>();
		TenderTypeEnumParameters tdrEnumParms;
    	
		String enumName;
    	String desc = null;
		String rdoClassName;
    	Class<?> rdoClass;
    	TenderType enumTdrType;
    	
    	// Extract the parameters to create the TenderTypeEnum instances
    	for (List<String> enumParms : tenderEnumParms)
    	{
    		enumName = enumParms.get(0);
    		rdoClassName = enumParms.get(1);
    		if (enumParms.size() >2)
    		{
    			desc = enumParms.get(2);
    		}
    		
    		if ((rdoClassName != null)&&(rdoClassName.length() > 0))
    		{
	    		try {
					rdoClass = Class.forName(rdoClassName);
				} catch (ClassNotFoundException e) {
					logger.error("Unable to create class for extended TenderTypeEnum " + rdoClassName);
					throw new RuntimeException("Unable to create class for " + rdoClassName, e);
				}
    		}
    		else
    		{
    			rdoClass = null;
    		}
    		
    		enumTdrType = this.determineTenderType(enumName);
    		if (enumTdrType == null)
    		{
    			determineUnmappedTenderType(enumName);
    		}

    		tdrEnumParms = new TenderTypeEnumParameters(enumName, rdoClass, enumTdrType, desc);
    		parameters.add(tdrEnumParms);
    	}
    	return parameters;
	}
	
}
