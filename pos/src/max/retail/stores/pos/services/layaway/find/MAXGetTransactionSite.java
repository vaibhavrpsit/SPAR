/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/find/GetTransactionSite.java /main/19 2014/06/03 17:06:11 asinton Exp $
 * ===========================================================================
 *Rev 1.0	Aug 29,2016		Ashish Yadav	Changes for code merging
 * ===========================================================================
 */
package max.retail.stores.pos.services.layaway.find;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
// foundation imports
import java.util.Locale;

import max.retail.stores.domain.transaction.MAXTransactionIfc;
import max.retail.stores.pos.services.common.MAXRoundingConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.StringUtils;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.LayawayDataTransaction;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.financial.LayawaySummaryEntryIfc;
import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteria;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc.SearchType;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.layaway.find.FindLayawayCargoIfc;
import oracle.retail.stores.pos.services.layaway.find.GetTransactionSite;

/**
 * Site that retrieves the LayawayTransaction corresponding to the selected
 * layaway summary entry in the layaway display list.
 */
@SuppressWarnings("serial")
public class MAXGetTransactionSite extends GetTransactionSite
{
	protected String  StrRounding;
    protected List roundingDenominations;
    public void arrive(BusIfc bus)
    {
    	// get the utility manager
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        // get the cargo for the service
        FindLayawayCargoIfc cargo = (FindLayawayCargoIfc)bus.getCargo();
        Letter result = new Letter (CommonLetterIfc.FAILURE); // default value

        // get the selected layaway summary entry from the cargo
        int selection = cargo.getSelectedLayawayIndex();
        //resetting
        cargo.setDataExceptionErrorCode(DataException.NONE);
        LayawaySummaryEntryIfc[] summaries = cargo.getLayawaySummaryEntryList();
        if (selection >= 0 && selection < summaries.length)
        {
            try
            {
                boolean trainingMode = ((AbstractFinancialCargo)cargo).getRegister().getWorkstation().isTrainingMode();

                // Read the layaway transaction using the regular sale return
                // transaction method
                LayawayDataTransaction trdt = null;

                trdt = (LayawayDataTransaction) DataTransactionFactory.create(DataTransactionKeys.LAYAWAY_DATA_TRANSACTION);

                TransactionIfc transaction
                    = instantiateTransaction(summaries[selection], trainingMode, utility.getRequestLocales());
                
				//Changes starts fro Rev 1.0
                ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
                try
                {
                	StrRounding = pm.getStringValue(MAXRoundingConstantsIfc.ROUNDING);
                	((MAXTransactionIfc) transaction).setRounding(StrRounding);
                	String[] roundingDenominationsArray = pm.getStringValues(MAXRoundingConstantsIfc.ROUNDING_DENOMINATIONS);
                   
        			
                	if(roundingDenominationsArray == null || roundingDenominationsArray.length == 0)
                		{
                			throw new ParameterException("List of parameters undefined");
                		}
                		roundingDenominations = new ArrayList();
                		roundingDenominations.add(0,new BigDecimal(0.0));
                		for(int i=0;i<roundingDenominationsArray.length;i++)
                		{
                			roundingDenominations.add(new BigDecimal(roundingDenominationsArray[i]));
                		}
                		roundingDenominations.add(roundingDenominationsArray.length,new BigDecimal(1.00));

//                		List must be sorted before setting on the cargo.
                		Collections.sort(roundingDenominations,new Comparator()	{
                			public int compare(Object o1, Object o2) {
                				BigDecimal denomination1 = (BigDecimal)o1;
                				BigDecimal denomination2 = (BigDecimal)o2;
                				return denomination1.compareTo(denomination2);
                			}
                		});
                		
                		((MAXTransactionIfc) transaction).setRoundingDenominations(roundingDenominations);
//                	}
                }
                catch(ParameterException pe)
                {
                	//if there is an error with the parameters, the price rounding logic should be disabled
                	 //cargo.setRoundingEnabledLogic(false);
                	 logger.error( "" + Util.throwableToString(pe) + "");
                }
				//Changes ends for Rev 1.0
				
				transaction
                    = trdt.readTransaction(transaction);

                // Retrieve the linked customer
                // This is somewhat hokey as the location of the correct customer ID varies depending upon
                // the path taken to get here.  
                if(transaction instanceof TenderableTransactionIfc && StringUtils.isNotBlank(((TenderableTransactionIfc)transaction).getCustomerId()))
                {
                    TenderableTransactionIfc tenderableTransaction = (TenderableTransactionIfc)transaction;
                    try
                    {
                        String customerId = null;
                        if(cargo.getLayaway() != null && cargo.getLayaway().getCustomer() != null)
                        {
                            customerId = cargo.getLayaway().getCustomer().getCustomerID();
                        }
                        else if(cargo.getCustomer() != null)
                        {
                            customerId = cargo.getCustomer().getCustomerID();
                        }
                        else
                        {
                            customerId = tenderableTransaction.getCustomerId();
                        }
                        CustomerManagerIfc customerManager = (CustomerManagerIfc)bus.getManager(CustomerManagerIfc.TYPE);
                        //create a customer search criteria
                        CustomerSearchCriteriaIfc searchCustomer = new CustomerSearchCriteria(SearchType.SEARCH_BY_CUSTOMER_ID, customerId, utility.getRequestLocales());
                        
                        Locale extendedDataRequestLocale = null;
                        if(cargo instanceof AbstractFinancialCargo && ((AbstractFinancialCargo)cargo).getOperator() != null)
                        {
                            extendedDataRequestLocale = ((AbstractFinancialCargo)cargo).getOperator().getPreferredLocale();
                        }
                        if(extendedDataRequestLocale == null)
                        {
                            extendedDataRequestLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);
                        }
                        searchCustomer.setExtendedDataRequestLocale(extendedDataRequestLocale);
                        int maxCustomerItemsPerListSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxCustomerItemsPerListSize", "10"));
                        searchCustomer.setMaxCustomerItemsPerListSize(maxCustomerItemsPerListSize);
                        int maxTotalCustomerItemsSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxTotalCustomerItemsSize", "40"));
                        searchCustomer.setMaxTotalCustomerItemsSize(maxTotalCustomerItemsSize);
                        int maxNumberCustomerGiftLists = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxNumberCustomerGiftLists", "4"));
                        searchCustomer.setMaxNumberCustomerGiftLists(maxNumberCustomerGiftLists);
                        //retrieve customer
                        CustomerIfc customer = customerManager.getCustomer(searchCustomer);
                        tenderableTransaction.setCustomer(customer);
                    }
                    catch(DataException ce)
                    {
                        logger.warn("Could not retrieve the linked customer: " + tenderableTransaction.getCustomerId());
                    }
                }

                if (logger.isDebugEnabled())
                {
                    logger.debug("Found transaction \n" + transaction);
                }

                // save the layaway transaction
                cargo.setInitialLayawayTransaction((LayawayTransactionIfc)transaction);

                // Temporary stop gap. LayawayTransaction should contain
                // the layaway object.
                if (cargo.getLayaway() == null ||
                    cargo.getLayaway().getCustomer() == null ||
                    Util.isEmpty(cargo.getLayaway().getCustomer().getLastName()))
                {
                    logger.error("GetTransactionSite.arrive: Customer information has not been populated in Layway object of LayawayTransaction");
                }
                else
                {
                   result = new Letter(CommonLetterIfc.SUCCESS);
                }

            }
            catch (DataException de)
            {
                // Save the error code if there's a data exception
                cargo.setDataExceptionErrorCode(de.getErrorCode());
            }
            catch (ClassCastException cce)
            {
                logger.error(
                    "GetTransactionSite.arrive");
            }
        }
        bus.mail(result, BusIfc.CURRENT);
    }

    /**
     * Instantiates an object implementing the TransactionIfc interface,\
     * using the initial transaction, and initial business day, of a layaway
     * summary entry. And the training mode
     * @return object implementing TransactionIfc
     */
    protected TransactionIfc instantiateTransaction(
        LayawaySummaryEntryIfc summary, boolean trainingMode, LocaleRequestor localeReq)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Building transaction using " + summary);
        }
        TransactionIfc transaction
            = DomainGateway.getFactory().getTransactionInstance();
        transaction.initialize(summary.getInitialTransactionID());
        transaction.setBusinessDay(summary.getInitialTransactionBusinessDate());
        transaction.setTransactionSequenceNumber(
                    summary.getInitialTransactionID().getSequenceNumber());
        transaction.setTrainingMode(trainingMode);
        transaction.setLocaleRequestor(localeReq);

        if (logger.isDebugEnabled())
        {
            StringBuilder debug = new StringBuilder("Built transaction had id: ");
            debug.append(transaction.getTransactionIdentifier());
            debug.append("\n  Workstation: ");
            debug.append(transaction.getWorkstation());
            debug.append("\n and business day: ");
            debug.append(transaction.getBusinessDay());
            logger.debug(debug.toString());
        }

        return(transaction);
    }

    /**
     * Instantiates an object implementing the LayawayIfc interface,
     * using the initial layaway summary entry to populate the layaway ID
     * and training mode.
     * @return object implementing LayawayIfc
     */
    protected LayawayIfc instantiateLayaway(LayawaySummaryEntryIfc summary, boolean trainingMode)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Building layaway using " + summary);
        }
        LayawayIfc layaway
            = DomainGateway.getFactory().getLayawayInstance();
        layaway.setLayawayID(summary.getLayawayID());
        layaway.setTrainingMode(trainingMode);

        if (logger.isDebugEnabled())
        {
            logger.debug("Built layway was: " + layaway.getLayawayID());
        }

        return(layaway);
    }
}
