/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/ValueAddedTaxProrateRule.java /main/14 2012/07/02 10:12:52 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/02/12 - Tax cleanup in preparation for JPA conversion.
 *    jswan     02/04/12 - Fix issues with loading tax rules when some rules
 *                         have logical errors.
 *    jswan     02/03/12 - XbranchMerge jswan_bug-13599093 from
 *                         rgbustores_13.4x_generic_branch
 *    jswan     01/30/12 - Modified to: 1) provide a more detailed log message
 *                         when a tax rule is invalid, and 2) allow valid tax
 *                         rules to load even if one or more other rules are
 *                         not valid.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         4/25/2007 10:00:23 AM  Anda D. Cadar   I18N
 *      merge
 * 3    360Commerce 1.2         3/31/2005 4:30:44 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:26:44 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:15:32 PM  Robert Pearse
 *$$
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @version 1.0
 * @created 13-Apr-2004 6:42:40 PM
 */
public class ValueAddedTaxProrateRule extends TaxProrateRule implements ValueAddedTaxRuleIfc 
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 5199778039912253502L;

    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.tax.ValueAddedTaxByLineRule.class);
    private ArrayList<String> valueAddedTaxUniqueIDs = new ArrayList<String>();
    
    public ValueAddedTaxProrateRule()
    {
    }

    public boolean isValid()
    {
        boolean returnValue = super.isValid();
        
        if(returnValue)
        {
            returnValue = valueAddedTaxUniqueIDs.isEmpty() == false;
            if (!returnValue)
            {
                String message = getValidationErrorMessage("ValueAddedTaxByLineRule is invalid since valueAddedTaxUniqueIDs is empty.");
                logger.error(message);
            }
        }
        
        return returnValue;
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.tax.ValueAddedTaxRuleIfc#addValueAddedTaxUniqueId(java.lang.String)
     */
    public void addValueAddedTaxUniqueId(String value)
    {
        valueAddedTaxUniqueIDs.add(value);
    }
    
    
    public CurrencyIfc getItemTaxableAmount(TaxLineItemInformationIfc item)
    {
        CurrencyIfc retValue = super.getItemTaxableAmount(item);
        
        TaxInformationContainerIfc container = item.getTaxInformationContainer();
        
        for(Iterator<String> iter = valueAddedTaxUniqueIDs.iterator(); iter.hasNext();)
        {    
            String uniqueID = iter.next();
            
            TaxInformationIfc taxInformation = container.getTaxInformation(uniqueID);
            if(taxInformation != null)
            {
                retValue = retValue.add(taxInformation.getTaxAmount());
            }
            else
            {
                //log error
            }
        }
        return retValue;
    }
    
    
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.tax.AbstractTaxRule#clone()
     */
    public Object clone()
    {
        ValueAddedTaxProrateRule newClass = new ValueAddedTaxProrateRule();
        setCloneAttributes(newClass);
        return newClass;
    }
    
    //---------------------------------------------------------------------
    /**
     Set attributes for clone. <P>
     @param newClass new instance of ValueAddedTaxByLineRule
     **/
    //---------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void setCloneAttributes(ValueAddedTaxProrateRule newClass)
    {
        super.setCloneAttributes(newClass);
        newClass.valueAddedTaxUniqueIDs = (ArrayList) valueAddedTaxUniqueIDs.clone();
    }
    

}
