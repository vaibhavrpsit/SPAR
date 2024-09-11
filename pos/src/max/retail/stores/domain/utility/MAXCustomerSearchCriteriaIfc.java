/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 * 
 *  Rev 1.0 	28 Dec, 2016		Ashish Yadav			Changes for online Points Redemption FES
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.domain.utility;

import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc.SearchType;

public interface MAXCustomerSearchCriteriaIfc extends CustomerSearchCriteriaIfc{

	public abstract void setCustomerIDPrefix(String paramString);

	public abstract String getCustomerIDPrefix();
	public abstract void setStoreIDPrefix(String paramString);

	public abstract String getStoreIDPrefix();
	public abstract void setSearchType(SearchType paramSearchType);

	public abstract oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc.SearchType getSearchType();
	public static enum SearchType {
		UNDEFINED, SEARCH_BY_CUSTOMER_ID, SEARCH_BY_EMPLOYEE_ID, SEARCH_BY_TAX_ID, SEARCH_BY_CUSTOMER_INFO, SEARCH_BY_BUSINESS_INFO, SEARCH_BY_PHONE_NUMBER;
	}
	
}
