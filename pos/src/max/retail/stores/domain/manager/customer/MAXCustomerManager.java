/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
*	
*	Rev 1.3     Feb 18, 2017		Ashish yadav			Changes for bug fix business customer
*   Rev 1.2     Feb 09,2017         Nitika Arora         Changes for Id 197
*	Rev 1.1     Dec 28, 2016		Ashish yadav			Changes for Online points redemption FES
*	Rev 1.0     Oct 19, 2016		Mansi Goel			Changes for Customer FES
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package max.retail.stores.domain.manager.customer;

import java.util.List;

import max.retail.stores.domain.utility.MAXCustomerSearchCriteriaIfc;
import oracle.retail.stores.common.utility.ResultList;
import oracle.retail.stores.domain.arts.BusinessReadDataTransaction;
import oracle.retail.stores.domain.arts.CustomerReadDataTransaction;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.manager.customer.CustomerManager;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;


public class MAXCustomerManager extends CustomerManager implements MAXCustomerManagerIfc{

	public ResultList getCustomers(CustomerSearchCriteriaIfc criteria)
			throws DataException {
		List<CustomerIfc> customers = null;

		ResultList resultList = null;

		DataTransaction dataTransaction = createDataTransaction(criteria);
		// Changes starts for Rev 1.3 (ashish)
		int caseType = criteria.getSearchType().ordinal();
		if(dataTransaction.getTransactionName().toString().equalsIgnoreCase("BusinessReadDataTransaction")){
			caseType = 3;
		}
		switch (caseType) {
		// Changes ends for Rev 1.3 (ashish)
		//Changes for Rev 1.0 : Starts
		case 1:
		case 2:
		case 4:
			resultList = ((CustomerReadDataTransaction) dataTransaction)
					.selectCustomers(criteria);
			break;
		case 3:
			resultList = ((BusinessReadDataTransaction) dataTransaction)
					.lookupBusiness(criteria);
			break;
		//Changes for Rev 1.0 : Ends
		/*
		 * case 4: resultList =
		 * ((CustomerReadDataTransaction)dataTransaction).readCustomerbyTaxID
		 * (criteria); break;
		 */
		case 5:
			resultList = ((CustomerReadDataTransaction) dataTransaction)
					.readCustomerbyEmployee(criteria);
			break;
		case 6:
		default:
			throw new DataException("Unknown search type");
		}

		customers = resultList.getList();

		for (CustomerIfc customer : customers) {
			retrieveCustomerDiscountRules(customer,
					criteria.getLocaleRequestor());
		}

		return resultList;
	}
	//Changes starts for Rev 1.1 (Ashish : online points redemption)
	public CustomerIfc getCustomer(MAXCustomerSearchCriteriaIfc criteria) throws DataException {
		DataTransaction customerReadDataTransaction = createDataTransaction(criteria);
		CustomerIfc foundCustomer = ((CustomerReadDataTransaction) customerReadDataTransaction).readCustomer(criteria);

		retrieveCustomerDiscountRules(foundCustomer, criteria.getLocaleRequestor());

		return foundCustomer;
	}
	//Changes starts for Rev 1.1 (Ashish : online points redemption)

	//Changes for Id 197 starts
	  protected DataTransaction createDataTransaction(CustomerSearchCriteriaIfc criteria)
	  {
	    DataTransaction dataTransaction = null;
	    switch (criteria.getSearchType().ordinal())
	    {
	    case 1:
	    case 2:
	    case 4:
	    case 5:
	    case 7:
	      dataTransaction = (CustomerReadDataTransaction)DataTransactionFactory.create("persistence_CustomerReadDataTransaction");

	      break;
	    case 3:
	      dataTransaction = (BusinessReadDataTransaction)DataTransactionFactory.create("persistence_BusinessReadDataTransaction");
	      break;
	    case 6:
	      throw new IllegalArgumentException("Unknown search type");
	    }

	    return dataTransaction;
	  }
	//Changes for Id 197 ends
}
