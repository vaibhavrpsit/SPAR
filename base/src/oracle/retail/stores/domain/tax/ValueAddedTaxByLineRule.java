/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/ValueAddedTaxByLineRule.java /main/14 2012/07/02 10:12:52 jswan Exp $
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
 * 4    360Commerce 1.3         4/25/2007 10:00:24 AM  Anda D. Cadar   I18N
 *      merge
 * 3    360Commerce 1.2         3/31/2005 4:30:44 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:26:44 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:15:31 PM  Robert Pearse
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
 * @created 13-Apr-2004 6:42:25 PM
 */
public class ValueAddedTaxByLineRule extends TaxByLineRule implements ValueAddedTaxRuleIfc 
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -2625606403982185341L;

    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.tax.ValueAddedTaxByLineRule.class);
    private ArrayList<String> valueAddedTaxUniqueIDs = new ArrayList<String>();

    /**
     * Creates an instance of ValueAddedTaxByLineRule
     *
     */
    public ValueAddedTaxByLineRule()
    {
    }
    
    /**
     * Determines whether this ValueAddedTaxByLineRule is valid
     * @return true if valid, otherwise false
     */
    public boolean isValid()
    {
        boolean returnValue = super.isValid();
        
        if(returnValue)
        {
            if(valueAddedTaxUniqueIDs.isEmpty())
            {
                returnValue = false;
                String message = getValidationErrorMessage("ValueAddedTaxByLineRule is invalid since valueAddedTaxUniqueIDs is null.");
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
    
    /**
     * Figure out the taxable amount for the line item
     * @param item the item to get the taxable amount from
     * @return the taxable amount for the item passed in 
     */
    public CurrencyIfc getItemTaxableAmount(TaxLineItemInformationIfc item)
    {
        CurrencyIfc taxableAmount = super.getItemTaxableAmount(item);
        
        for(Iterator<String> i = valueAddedTaxUniqueIDs.iterator();i.hasNext();)
        {
            String uniqueID = i.next();
            TaxInformationIfc taxInformation = item.getTaxInformationContainer().getTaxInformation(uniqueID);
            if(taxInformation != null)
            {
                taxableAmount = taxableAmount.add(taxInformation.getTaxAmount());
            }
            else
            {
                logger.error("ValueAddedTaxByLineRule -- Item has no tax information for unique id " + uniqueID);
            }
        }
        
        return taxableAmount;
    }
    

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.tax.AbstractTaxRule#clone()
     */
    public Object clone()
    {
        ValueAddedTaxByLineRule newClass = new ValueAddedTaxByLineRule();
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
    public void setCloneAttributes(ValueAddedTaxByLineRule newClass)
    {
        super.setCloneAttributes(newClass);
        newClass.valueAddedTaxUniqueIDs = (ArrayList) valueAddedTaxUniqueIDs.clone();
    }


}
