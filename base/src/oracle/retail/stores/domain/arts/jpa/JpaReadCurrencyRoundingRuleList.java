/*===========================================================================
* Copyright (c) 2013, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/jpa/JpaReadCurrencyRoundingRuleList.java /main/3 2014/07/07 10:43:49 cgreene Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* cgreene     07/01/14 - refactor throwing and catching exceptions so that
*                        SQLExceptions get mapped
* jswan       12/13/13 - Upated JAVADOC.
* mkutiana    02/18/13 - Creating Data operation for
*                        retrieving Currency Rounding rules
* mkutiana    02/18/13 - Creation
* ===========================================================================
*/

package oracle.retail.stores.domain.arts.jpa;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.domain.utility.CurrencyRoundingRuleSearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JpaDataOperation;
import oracle.retail.stores.storeservices.entities.currency.CurrencyRoundingRule;
import oracle.retail.stores.storeservices.services.common.ServiceException;
import oracle.retail.stores.storeservices.services.common.StoresDataServiceIfc;
import oracle.retail.stores.storeservices.services.currency.CurrencyRoundingRuleServiceIfc;

/**
 * This Data operation retrieves the currency rounding rule list from the DB
 * @since 14.0
 */
public class JpaReadCurrencyRoundingRuleList extends JpaDataOperation
{

    private static final long serialVersionUID = -8631334328034499997L;

    /**
     * Execute the service to get the Currency Rounding Rule Lists.
     *
     * @see oracle.retail.stores.foundation.manager.data.JpaDataOperation#execute(oracle.retail.stores.storeservices.services.common.StoresDataServiceIfc, java.io.Serializable)
     */
    @Override
    protected Serializable execute(StoresDataServiceIfc service, Serializable dataObject) throws ServiceException, DataException
    {
        //Get the service and criteria
        CurrencyRoundingRuleSearchCriteriaIfc currencyRoundingRuleSearchCriteria =  (CurrencyRoundingRuleSearchCriteriaIfc)dataObject;
        String currencyRoundingType = currencyRoundingRuleSearchCriteria.getCurrencyRoundingType();       
        BigDecimal currencyRoundingDenomination =  currencyRoundingRuleSearchCriteria.getCurrencyRoundingDenomination();
        String isoCurrencyCode = currencyRoundingRuleSearchCriteria.getIsoCurrencyCode();
        CurrencyRoundingRuleServiceIfc currencyRoundingRuleService = (CurrencyRoundingRuleServiceIfc)service;

        List<CurrencyRoundingRule> results = null;
        ArrayList<CurrencyRoundingRule> currencyRoundingRuleList = null;
        try
        {
            results = (List<CurrencyRoundingRule>) currencyRoundingRuleService.findCurrencyRoundingRules(currencyRoundingType, currencyRoundingDenomination, isoCurrencyCode );
        }
        catch (ServiceException exception)
        {
            if (exception.getCause() instanceof DataException)
            {
                throw (DataException)exception.getCause();
            }
            mapAndThrowDataException(exception);
        }

        if (results == null || results.size() == 0)
        {
            throw new DataException(DataException.NO_DATA, "Currency Rounding Rule Data Not found for search criteria currencyRoundingType :"
                                + currencyRoundingType + " currencyRoundingDenomination " + currencyRoundingDenomination + " isoCurrencyCode "+ isoCurrencyCode );
                                
        }
        else
        {
            currencyRoundingRuleList = new ArrayList<CurrencyRoundingRule>(results.size());            
            for(CurrencyRoundingRule entity : results)
            {
                currencyRoundingRuleList.add(entity);
            }
        }

        return currencyRoundingRuleList;        
    }    
}