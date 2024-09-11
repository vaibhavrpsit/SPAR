/* ===========================================================================
* Copyright (c) 2004, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/utility/tdo/TaxTDO.java /main/16 2013/09/05 10:36:15 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    abonda 09/04/13 - initialize collections
 *    cgreen 03/30/12 - convert Hashtables and Vectors to Maps and Lists
 *    jswan  07/02/12 - Tax cleanup in preparation for JPA conversion.
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    cgreen 05/15/09 - optimize performance of
 *                      createTaxGroupTaxRuleMappingForStore
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.utility.tdo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.ReadNewTaxRuleTransaction;
import oracle.retail.stores.domain.tax.TaxRuleIfc;
import oracle.retail.stores.domain.tax.TaxRulesVO;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.utility.BaseException;
import oracle.retail.stores.pos.tdo.TDOAdapter;

/**
 * @author epd
 */
public class TaxTDO extends TDOAdapter
{
    /*
     * Creates the tax group rule mappings for the supplied store and store
     * state
     *
     * @param storeID The store ID of the current store
     * @param storeState The state in which the store resides
     * @return a Hashtable of tag group tag rule mappings
     * @throws BaseException
     */
    public Map<Integer,List<TaxRuleIfc>> createTaxGroupTaxRuleMappingForStore(String storeID,
            String storeState) throws BaseException
    {
        ReadNewTaxRuleTransaction taxRuleTransaction = null;

        taxRuleTransaction = (ReadNewTaxRuleTransaction) DataTransactionFactory
                .create(DataTransactionKeys.READ_NEW_TAX_RULE_TRANSACTION);

        TaxRulesVO taxRulesVO = null;
        try
        {
            taxRulesVO = taxRuleTransaction.getTaxRulesByStore(storeID);
        }
        catch (DataException e)
        {
            throw new BaseException("Could not read tax rules", e);
        }

        Map<Integer,List<TaxRuleIfc>> taxGroupTaxRulesMapping = new HashMap<Integer,List<TaxRuleIfc>>(0);

        if (taxRulesVO != null && taxRulesVO.hasTaxRules())
        {
            TaxRuleIfc[] taxRules = taxRulesVO.getAllTaxRules();
            // Collect the unique tax groups from the result read from database.
            // Use the hashtable to hold these unique tax groups as keys.
            for (int i = taxRules.length - 1; i >= 0; i--)
            {
                int taxGroupKey = taxRules[i].getTaxGroupID();
                List<TaxRuleIfc> taxRulesByGroup = taxGroupTaxRulesMapping.get(taxGroupKey);
                if (taxRulesByGroup == null)
                {
                    taxRulesByGroup = new ArrayList<TaxRuleIfc>();
                    taxGroupTaxRulesMapping.put(taxGroupKey, taxRulesByGroup);
                }
                taxRulesByGroup.add(taxRules[i]);
            }
        }

        return taxGroupTaxRulesMapping;
    }
}