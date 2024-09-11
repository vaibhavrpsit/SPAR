/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 * 
 *  Rev 1.0 	28 Dec, 2016		Ashish Yadav			Changes for online Points Redemption FES
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.domain.utility;

import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.utility.CustomerSearchCriteria;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;

public class MAXCustomerSearchCriteria extends CustomerSearchCriteria implements  MAXCustomerSearchCriteriaIfc{

	protected String customerIDPrefix = "";
	protected String storeIDPrefix = "";
	private CustomerSearchCriteriaIfc.SearchType searchType = CustomerSearchCriteriaIfc.SearchType.UNDEFINED;
	public void setCustomerIDPrefix(String customerIDPrefix) {
		this.customerIDPrefix = customerIDPrefix;
	}

	public String getCustomerIDPrefix() {
		return this.customerIDPrefix;
	}
	public MAXCustomerSearchCriteria(CustomerSearchCriteriaIfc.SearchType type, LocaleRequestor locale) {
		setSearchType(type);
		setLocaleRequestor(locale);
	}
	public void setStoreIDPrefix(String storeIDPrefix) {
		this.storeIDPrefix = storeIDPrefix;
	}

	public String getStoreIDPrefix() {
		return this.storeIDPrefix;
	}

	@Override
	public void setSearchType(
			max.retail.stores.domain.utility.MAXCustomerSearchCriteriaIfc.SearchType paramSearchType) {
		// TODO Auto-generated method stub
		
	}
	public MAXCustomerSearchCriteria(CustomerSearchCriteriaIfc.SearchType type, String id, LocaleRequestor locale)
	  {
	    setSearchType(type);
	    setLocaleRequestor(locale);
	    switch (type)
	    {
	    case SEARCH_BY_CUSTOMER_ID:
	      setCustomerID(id);
	      break;
	    case SEARCH_BY_EMPLOYEE_ID:
	      setEmployeeID(id);
	      break;
	    case SEARCH_BY_TAX_ID:
	      setTaxID(id);
	      break;
	    default:
	      break;
	    case UNDEFINED:
	      throw new IllegalArgumentException("Unknown search type");
	    }
	  }

  }
