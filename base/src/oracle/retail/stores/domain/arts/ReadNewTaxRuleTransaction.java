/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/ReadNewTaxRuleTransaction.java /main/11 2011/01/27 19:03:04 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   01/27/11 - refactor creation of data transactions to use spring
 *                         context
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *5    360Commerce 1.4         6/22/2006 11:30:38 AM  Charles D. Baker
 *     serialVersionUID change
 *4    360Commerce 1.3         6/13/2006 4:12:03 PM   Brett J. Larsen CR 18490
 *     - UDM - removal of TaxAuthorityPostalCode & TaxAuthorityProvince
 *3    360Commerce 1.2         3/31/2005 4:29:33 PM   Robert Pearse   
 *2    360Commerce 1.1         3/10/2005 10:24:31 AM  Robert Pearse   
 *1    360Commerce 1.0         2/11/2005 12:13:33 PM  Robert Pearse   
 *
 Revision 1.4  2004/06/15 20:41:36  cschellenger
 @scr 2775 Tax rules for unknown item
 *
 Revision 1.3  2004/06/10 14:21:29  jdeleau
 @scr 2775 Use the new tax data for the tax flat files
 *
 Revision 1.2  2004/06/07 18:19:31  jdeleau
 @scr 2775 Add tax Service, Multiple Geo Codes screens
 *
 Revision 1.1  2004/06/03 16:22:41  jdeleau
 @scr 2775 Initial Drop of send item tax support.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.util.Collection;

import oracle.retail.stores.domain.tax.TaxRulesVO;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;

import org.apache.log4j.Logger;

/**
 * $Revision: /main/11 $
 */
public class ReadNewTaxRuleTransaction extends DataTransaction
{
    /**
     * This id is used to tell the compiler not to generate a new serialVersionUID.
     */
    static final long serialVersionUID = -1L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(ReadNewTaxRuleTransaction.class);
    
    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/11 $";
    
    /**
     * The name that links this transaction to a command within the DataScript.
     */
    protected static String dataCommandName="ReadNewTaxRuleTransaction";
    
    /**
     * Default Constructor
     */
    public ReadNewTaxRuleTransaction()
    {
        super(dataCommandName);
    }
    
    /**
     * Given a geoCode and a Collection of taxGroupId, return the applicable tax rules.
     *  
     * @param geoCode GeoCode to use when retrieving tax rules
     * @param taxGroupIds TaxGroup IDs to get tax info for
     * @return Array of tax rules
     * @throws DataException If there is a problem reading the data
     */
    public TaxRulesVO getTaxRulesByGeoCode(String geoCode, Collection<Integer> taxGroupIds) throws DataException
    {
        if (logger.isDebugEnabled()) 
        {
            logger.debug("ReadNewTaxRuleTransaction.ReadTaxRulesByGeoCode (multiple)");
        }
        
        // build search criteria for tax rule query
        NewTaxRuleSearchCriteria searchCriteria =
            new NewTaxRuleSearchCriteria(geoCode, taxGroupIds, NewTaxRuleSearchCriteria.SEARCH_BY_GEO_CODE);
        
        return getTaxRules(searchCriteria);
        
    }
    /**
     * Given a geoCode and a taxGroupId, return the applicable tax rules.
     *  
     * @param geoCode GeoCode to use when retrieving tax rules
     * @param taxGroupId TaxGroup to get tax info for
     * @return Array of tax rules
     * @throws DataException If there is a problem reading the data
     */
    public TaxRulesVO getTaxRulesByGeoCode(String geoCode, int taxGroupId) throws DataException
    {
        if (logger.isDebugEnabled()) 
        {
            logger.debug("ReadNewTaxRuleTransaction.ReadTaxRulesByGeoCode");
        }
        
        // build search criteria for tax rule query
        NewTaxRuleSearchCriteria searchCriteria =
            new NewTaxRuleSearchCriteria(geoCode, taxGroupId, NewTaxRuleSearchCriteria.SEARCH_BY_GEO_CODE);
        
        return getTaxRules(searchCriteria);
    }
    
    /**
     * Given a postalCode and a taxGroupId, return the applicable tax rules.
     *  
     * @param postalCode PostalCode to use when retrieving tax rules
     * @param taxGroupId TaxGroup to get tax info for
     * @return Array of tax rules
     * @throws DataException If there is a problem reading the data
     */
    public TaxRulesVO getTaxRulesByPostalCode(String postalCode, int taxGroupId) throws DataException
    {
        if (logger.isDebugEnabled()) 
        {
            logger.debug("ReadNewTaxRuleTransaction.ReadTaxRulesByPostalCode");
        }
        
        // build search criteria for tax rule query
        NewTaxRuleSearchCriteria searchCriteria =
            new NewTaxRuleSearchCriteria(postalCode, taxGroupId, NewTaxRuleSearchCriteria.SEARCH_BY_POSTAL_CODE);
        
        return getTaxRules(searchCriteria);
    }
    
    /**
     * Given a postalCode and a Collection of taxGroupIds, return the applicable tax rules.
     *  
     * @param postalCode PostalCode to use when retrieving tax rules
     * @param taxGroupIds TaxGroup IDs to get tax info for
     * @return Array of tax rules
     * @throws DataException If there is a problem reading the data
     */
    public TaxRulesVO getTaxRulesByPostalCode(String postalCode, Collection<Integer> taxGroupIds) throws DataException
    {
        if (logger.isDebugEnabled()) 
        {
            logger.debug("ReadNewTaxRuleTransaction.ReadTaxRulesByPostalCode");
        }
        
        // build search criteria for tax rule query
        NewTaxRuleSearchCriteria searchCriteria =
            new NewTaxRuleSearchCriteria(postalCode, taxGroupIds, NewTaxRuleSearchCriteria.SEARCH_BY_POSTAL_CODE);
        
        return getTaxRules(searchCriteria);
    }
    
    /**
     * Given a storeID, and a list of taxGroupIds get the taxRules.
     *  
     * @param storeId StoreId to get geoCode from.
     * @param taxGroupIds List of tax GroupIDs
     * @return Tax rules Value Object
     * @throws DataException On database error.
     */
    public TaxRulesVO getTaxRulesByStore(String storeId, Collection<Integer> taxGroupIds) throws DataException
    {
        if (logger.isDebugEnabled()) 
        {
            logger.debug("ReadNewTaxRuleTransaction.ReadTaxRulesByStore");
        }
        
        // build search criteria for tax rule query
        NewTaxRuleSearchCriteria searchCriteria =
            new NewTaxRuleSearchCriteria(storeId, taxGroupIds, NewTaxRuleSearchCriteria.SEARCH_BY_STORE);
        
        return getTaxRules(searchCriteria);
    }
    
    /**
     * Given a storeID, and a list of taxGroupIds get the taxRules.
     *  
     * @param storeId StoreId to get geoCode from.
     * @return Tax rules Value Object
     * @throws DataException On database error.
     */
    public TaxRulesVO getTaxRulesByStore(String storeId) throws DataException
    {
        if (logger.isDebugEnabled()) 
        {
            logger.debug("ReadNewTaxRuleTransaction.ReadTaxRulesByStore");
        }
        
        // build search criteria for tax rule query
        NewTaxRuleSearchCriteria searchCriteria =
            new NewTaxRuleSearchCriteria(storeId, null, NewTaxRuleSearchCriteria.SEARCH_BY_STORE);
        
        return getTaxRules(searchCriteria);
    }
    
    /**
     * Given a storeId and a taxGroupId get the taxRules.
     *  
     * @param storeId StoreId to get geoCode from.
     * @param taxGroupId taxGroupId to use
     * @return Tax rules Value Object
     * @throws DataException On database error.
     */
    public TaxRulesVO getTaxRulesByStore(String storeId, int taxGroupId) throws DataException
    {
        if (logger.isDebugEnabled()) 
        {
            logger.debug("ReadNewTaxRuleTransaction.ReadTaxRulesByStore");
        }
        
        // build search criteria for tax rule query
        NewTaxRuleSearchCriteria searchCriteria =
            new NewTaxRuleSearchCriteria(storeId, taxGroupId, NewTaxRuleSearchCriteria.SEARCH_BY_STORE);
        
        return getTaxRules(searchCriteria);
    }
    
    /**
     * Given a geoCode and departmentId get the default tax rules for that department
     * 
     * 
     * @param geoCode
     * @param departmentId
     * @return Tax rules Value Object
     * @throws DataException
     */
    public TaxRulesVO getDepartmentDefaultTaxRules(String geoCode, String departmentId) throws DataException
    {
        if (logger.isDebugEnabled()) 
        {
            logger.debug("ReadNewTaxRuleTransaction.ReadDefaultTaxRulesForDepartment");
        }
        
        // build search criteria for tax rule query
        NewTaxRuleSearchCriteria searchCriteria =
            new NewTaxRuleSearchCriteria(geoCode, departmentId);
        
        return getTaxRules(searchCriteria);
        
    }
    
    
    /**
     * Given a set of search criteria, return the tax rules that meet the criteria.
     *  
     * @param searchCriteria Criteria to search on.
     * @return TaxRules that meet the criteria.
     * @throws DataException If there is a problem reading the data from the database.
     */
    protected TaxRulesVO getTaxRules(NewTaxRuleSearchCriteria searchCriteria) throws DataException
    {
        if (logger.isDebugEnabled()) 
        {
            logger.debug("ReadNewTaxRuleTransaction.ReadTaxRules");
        }
        
        // set data actions and execute
        applyDataObject(searchCriteria);
        
        TaxRulesVO taxRulesVO = (TaxRulesVO) getDataManager().execute(this);
        
        return taxRulesVO;
        
    }
}
