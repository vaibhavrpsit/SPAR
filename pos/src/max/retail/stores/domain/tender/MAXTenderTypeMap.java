/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ** * * * * ** * * * * ** * * * * *
* 
*  Copyright (c) 2013	MAX HyperMarkets.    All Rights Reserved.
*  
*  Rev 1.3		Jun 01, 2019		Purushotham Reddy   	Changes  for POS-Amazon Pay Integration
*  Rev 1.2      16/10/2017       	Bhanu Priya       		Changes  for Paytm Integration
*  Rev 1.1  	14/07/2016         	Abhishek Goyal    		Initial Draft: Changes for CR
*  Rev 1.0 		20/05/2013			Prateek					Initial Draft: Changes for TIC Customer Integration
*  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ** * * * * ** * * * * ** * * * * */

package max.retail.stores.domain.tender;

import java.util.ArrayList;
import java.util.Arrays;

import oracle.retail.stores.domain.tender.TenderTypeMap;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;

public class MAXTenderTypeMap extends TenderTypeMap {

	protected static final MAXTenderTypeMap map = new MAXTenderTypeMap();

	protected static ArrayList<String> typeCodes = new ArrayList<String>(
			Arrays.asList(new String[] { "CASH", "CRDT", "CHCK", "TRAV", "GICT", "MBCK", "DBIT", "QPON", "GCRD", "STCR",
					"MACT", "PRCH", "MNYO", "ECHK", "LYPT","EP", "ECOD","PYTM","MBWK","AMPY", "CCHK", "EWLT"}));

	protected static ArrayList<String> descriptors = new ArrayList<String>(
			Arrays.asList(new String[] { "Cash", "Credit", "Check", "TravCheck", "GiftCert", "MailCheck", "Debit",
					"Coupon", "GiftCard", "StoreCredit", "MallCert", "PurchaseOrder", "MoneyOrder", "E-Check",
					"Loyalty Points", "EComPrepaid", "EComCOD","Paytm","Mobikwik", "Amazon Pay", "Canadian Check", "EWallet"}));

	protected static ArrayList<String> IXRetailDescriptors = new ArrayList<String>(
			Arrays.asList(new String[] { "Cash", "Credit", "Check", "Trav. Check", "Gift Cert.", "Mail Check", "Debit",
					"Coupon", "Gift Card", "Store Credit", "Mall Cert.", "Purchase Order", "Money Order", "E-Check",
					"Loyalty Points", "EComPrepaid", "EComCOD","Paytm","Mobikwik" ,"Amazon Pay", "Canadian Check", "EWallet" }));

	
	public String getDescriptor(int type) {
		String desc;

		try {
			desc = (String) descriptors.get(type);
		} catch (IndexOutOfBoundsException e) {
			desc = "DescriptorUnknown";
		}
		return desc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com._360commerce.domain.tender.TenderTypeMap#getTypeFromDescriptor(java.
	 * lang.String)
	 */
	public int getTypeFromDescriptor(String desc) {
		return descriptors.indexOf(desc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com._360commerce.domain.tender.TenderTypeMap#getIXRetailDescriptor(int)
	 */
	public String getIXRetailDescriptor(int type) {
		String desc;

		try {
			desc = (String) IXRetailDescriptors.get(type);
		} catch (IndexOutOfBoundsException e) {
			desc = "DescriptorUnknown";
		}
		return desc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com._360commerce.domain.tender.TenderTypeMap#
	 * getTypeFromIXRetailDescriptor(java.lang.String)
	 */
	public int getTypeFromIXRetailDescriptor(String desc) {
		return IXRetailDescriptors.indexOf(desc);
	}

	/**
	 * @return
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see com._360commerce.domain.tender.TenderTypeMap#getCode(int)
	 */
	public String getCode(int type) {
		String code;

		try {
			code = (String) typeCodes.get(type);
		} catch (IndexOutOfBoundsException e) {
			code = "UNKN";
		}
		return code;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com._360commerce.domain.tender.TenderTypeMap#getTypeFromCode(java.lang.
	 * String)
	 */
	public int getTypeFromCode(String code) {
		return typeCodes.indexOf(code);
	}

	public static TenderTypeMapIfc getTenderTypeMap() {
		return map;
	}
}
