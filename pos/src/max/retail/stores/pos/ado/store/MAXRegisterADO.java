/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev 1.0  	Jan 22,2017  		Ashish Yadav  	Changes Loyalty OTP FES (issue during post void loyalty transaction)
 ********************************************************************************/

package max.retail.stores.pos.ado.store;



import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import max.retail.stores.domain.utility.MAXCustomerSearchCriteria;
import max.retail.stores.domain.utility.MAXCustomerSearchCriteriaIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc.SearchType;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.TransactionPrototypeEnum;

public class MAXRegisterADO extends RegisterADO{
	
	private static Logger logger = Logger.getLogger(max.retail.stores.pos.ado.store.MAXRegisterADO.class);
	public RetailTransactionADOIfc loadTransaction(LocaleRequestor localeReq, String transactionID)
            throws DataException
    {
        if (logger.isInfoEnabled())
        {
            logger.debug("Loading transaction...");
        }

        // attempt to retrieve the original RDO transaction
        TransactionIfc txnRDO = loadRDOTransaction(localeReq, transactionID);

        // check if customer exists for this transaction
        if(txnRDO instanceof TenderableTransactionIfc)
        {
            TenderableTransactionIfc tenderableTransaction = (TenderableTransactionIfc)txnRDO;
            if(StringUtils.isNotBlank(tenderableTransaction.getCustomerId()))
            {
                CustomerManagerIfc customerManager = (CustomerManagerIfc)Gateway.getDispatcher().getManager(CustomerManagerIfc.TYPE);
                // Changes starts for Rev 1.0 (Ashish : Loyalty OTP)
                MAXCustomerSearchCriteriaIfc criteria = new MAXCustomerSearchCriteria(SearchType.SEARCH_BY_CUSTOMER_ID, tenderableTransaction.getCustomerId(), localeReq);
             // Changes ends for Rev 1.0 (Ashish : Loyalty OTP)
                Locale extendedDataRequestLocale = null;
                if(operator != null && operator.toLegacy() instanceof EmployeeIfc)
                {
                    extendedDataRequestLocale = ((EmployeeIfc)operator.toLegacy()).getPreferredLocale();
                }
                if(extendedDataRequestLocale == null)
                {
                    extendedDataRequestLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);
                }
                criteria.setExtendedDataRequestLocale(extendedDataRequestLocale);
                int maxCustomerItemsPerListSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxCustomerItemsPerListSize", "10"));
                criteria.setMaxCustomerItemsPerListSize(maxCustomerItemsPerListSize);
                int maxTotalCustomerItemsSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxTotalCustomerItemsSize", "40"));
                criteria.setMaxTotalCustomerItemsSize(maxTotalCustomerItemsSize);
                int maxNumberCustomerGiftLists = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxNumberCustomerGiftLists", "4"));
                criteria.setMaxNumberCustomerGiftLists(maxNumberCustomerGiftLists);
                //search for the customer
                CustomerIfc customer = customerManager.getCustomer(criteria);
                
                tenderableTransaction.setCustomer(customer);
               
            }
        }

        RetailTransactionADOIfc txnADO = null;
        if (txnRDO != null)
        {
            TransactionPrototypeEnum enumer = TransactionPrototypeEnum
                    .makeEnumFromTransactionType(txnRDO.getTransactionType());
            try
            {
                txnADO = enumer.getTransactionADOInstance();
            }
            catch (ADOException e)
            {
                // KLM: More thought probably needs to put into this and a more
                // suitable exception thrown, given that a
                // DataException seems to typically represents a database
                // related error. in any case, it should be incrementally
                // better in that caller will at least know something went
                // wrong.
                //
                throw new DataException(DataException.UNKNOWN, e.getMessage(), e);
            }
            ((ADO)txnADO).fromLegacy(txnRDO);
        }
        return txnADO;
    }

}
