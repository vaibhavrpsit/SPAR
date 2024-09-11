/* ===========================================================================
* Copyright (c) 2004, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/AbstractTaxRule.java /main/16 2014/07/24 15:23:28 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       07/22/14 - set tax authority name
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
 *    cgreene   03/25/09 - move determination of tax receipt code to
 *                         TaxInformation.getReceiptCode since the code should
 *                         be determined at request time, not at set time
 *    cgreene   10/17/08 - setReceiptCode on new tax infos
 *
 * ===========================================================================
 * $Log: 
 * 5    360Commerce 1.4         4/30/2007 5:38:35 PM   Sandy Gu        added api
 *      to handle inclusive tax
 * 4    360Commerce 1.3         4/25/2007 10:00:28 AM  Anda D. Cadar   I18N
 *      merge
 * 3    360Commerce 1.2         3/31/2005 4:27:07 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:19:28 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:09:21 PM  Robert Pearse   
 * $
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;

import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.log4j.Logger;

/**
 * Base class for all tax rules
 */
public abstract class AbstractTaxRule implements RunTimeTaxRuleIfc 
{    
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -7665735348709563949L;
    /* the logger */
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.domain.tax.AbstractTaxRule.class);

    private TaxCalculatorIfc taxCalculator;
    private String uniqueID;
    private int order;
    private boolean useBasePrice = false;
    private String taxRuleName;
    private int taxAuthorityID;
    private String taxAuthorityName;
    private int taxGroupID;
    private int taxTypeCode;
    private boolean taxHoliday = false;
    private boolean inclusiveTaxFlag = false;	// by default, it must be set to false
    private List<String> overrideUniqueIDs = new ArrayList<String>();

    /**
     * 
     * Create a TaxInformationIfc object.  This data is saved to the database
     * with the transaction
     *  
     *  @param mode Tax mode (standard, override, etc)
     *  @return TaxInformationIfc object
     */
    public TaxInformationIfc createTaxInformation(int mode)
    {
        TaxInformationIfc taxInformation = DomainGateway.getFactory().getTaxInformationInstance();
        taxInformation.setTaxAuthorityID(taxAuthorityID);
        taxInformation.setTaxAuthorityName(taxAuthorityName);
        taxInformation.setTaxGroupID(taxGroupID);
        taxInformation.setTaxMode(mode);
        taxInformation.setTaxRuleName(taxRuleName);
        taxInformation.setTaxTypeCode(taxTypeCode);
        taxInformation.setUniqueID(uniqueID);
        taxInformation.setUsesTaxRate(taxCalculator instanceof TaxRateCalculatorIfc);
        taxInformation.setTaxHoliday(taxHoliday);
        taxInformation.setInclusiveTaxFlag(inclusiveTaxFlag);
        return taxInformation;        
    }
    
    /**
     * Create a TaxInformationIfc object.  This data is saved to the database
     * with the transaction
     *  
     *  @param taxableAmount Taxable amount 
     *  @param taxAmount Tax charged
     *  @param mode Tax mode (standard, override, etc)
     *  @return TaxInformationIfc object
     */
    public TaxInformationIfc createTaxInformation(CurrencyIfc taxableAmount,
            CurrencyIfc taxAmount, int mode)
    {
        TaxInformationIfc taxInformation = createTaxInformation(mode);
        taxInformation.setTaxableAmount(taxableAmount);
        taxInformation.setTaxAmount(taxAmount);
        if(taxCalculator instanceof TaxRateCalculatorIfc)
        {
            TaxRateCalculatorIfc calc = (TaxRateCalculatorIfc) taxCalculator;
            taxInformation.setTaxPercentage(calc.getTaxRate().movePointRight(2));
        }
        return taxInformation;
    }

    /**
     * Tell whether or not this is a valid tax rule.  The calculator
     * must be valid for the rule to be valid.
     *  
     * @return true or false
     * @see oracle.retail.stores.domain.tax.RunTimeTaxRuleIfc#isValid()
     */
    public boolean isValid()
    {
        boolean valid = false;
        
        if(taxCalculator != null)
        {
            valid = taxCalculator.isValid();
            if (!valid)
            {
                String message = getValidationErrorMessage("Tax Rule is not valid because its tax calculator is not valid.");
                logger.error(message);
            }
        }
        
        return valid;
    }

    /**
     * Set attributes for clone.
     * 
     * @param newClass new instance of AbstractTaxRule
     */
    public void setCloneAttributes(AbstractTaxRule newClass)
    {
        if(taxCalculator != null)
        {
            newClass.taxCalculator = (TaxCalculatorIfc) taxCalculator.clone();
        }
        newClass.uniqueID = uniqueID;
        newClass.order = order;
        newClass.useBasePrice = useBasePrice;
        newClass.taxRuleName = taxRuleName;
        newClass.taxAuthorityID = taxAuthorityID;
        newClass.taxGroupID = taxGroupID;
        newClass.taxTypeCode = taxTypeCode;
        newClass.taxHoliday = taxHoliday;
        newClass.inclusiveTaxFlag = inclusiveTaxFlag;
    }

    /**
     * Get the tax calculator used to calculate this rule's tax
     *  
     * @return tax calculator
     * @see oracle.retail.stores.domain.tax.RunTimeTaxRuleIfc#getTaxCalculator()
     */
    public TaxCalculatorIfc getTaxCalculator() 
    {
        return taxCalculator;
    }

    /**
     * @param value
     */
    public void setTaxCalculator(TaxCalculatorIfc value) 
    {
        taxCalculator = value;
    }

    /**
     * Return the unique ID
     * @return the unique id.
     */
    public String getUniqueID() 
    {
        if(this.uniqueID == null)
        {
            this.uniqueID = "";
        }
        return this.uniqueID;
    }

    /**
     * @param value
     */
    public void setUniqueID(String value) 
    {
        uniqueID = value;
    }

    /**
     * @param item the Item to add
     * @param taxRules the tax rules that could potentially be 
     *      affiliated with this item. 
     */  
    public boolean shouldAddItem(TaxLineItemInformationIfc item, RunTimeTaxRuleIfc[] taxRules) 
    {
        boolean returnValue = true;
        for(String id : overrideUniqueIDs)
        {
            for(int i = 0; i < taxRules.length; i++)
            {
                if( taxRules[i].getUniqueID().equals(id))
                {
                    //if override rule is active then we do
                    //not want to add this item to this rule
                    //because the active rule takes precedence
                    returnValue = taxRules[i].isRuleActiveForItem(item) == false;
                }
                    
            }
        }
        
        if(returnValue)
        {
            returnValue = isRuleActiveForItem(item);
        }

        return returnValue;
    }
    
    /**
     * @param item Item to tax
     * @return whether or not the given item should be used to calculate tax
     */
    public boolean isRuleActiveForItem(TaxLineItemInformationIfc item) 
    {
        return true;
    }
    
    /**
     * @return Returns the order.
     */
    public int getOrder()
    {
        return order;
    }

    /**
     * @param order The order to set.
     */
    public void setOrder(int value)
    {
        order = value;
    }

    /**
     * @return Returns the useBasePrice.
     */
    public boolean getUseBasePrice()
    {
        return useBasePrice;
    }

    /**
     * @param value The useBasePrice to set.
     */
    public void setUseBasePrice(boolean value)
    {
        useBasePrice = value;
    }
    
    /**
     * Get the taxable amount on this rule
     *  
     *  @param item
     *  @return taxable amount
     */
    public CurrencyIfc getItemTaxableAmount(TaxLineItemInformationIfc item)
    {
        CurrencyIfc retValue = null;
        if(useBasePrice)
        {
            retValue = item.getExtendedSellingPrice();
        }
        else
        {
            retValue = item.getExtendedDiscountedSellingPrice();
        }
        return retValue;
    }

    /**
     * @return Returns the taxAuthorityID.
     */
    public int getTaxAuthorityID() 
    {
        return taxAuthorityID;
    }

    /**
     * @param value The taxAuthorityID to set.
     */
    public void setTaxAuthorityID(int value) 
    {
        taxAuthorityID = value;
    }
    
    /**
     * @return the taxAuthorityName
     */
    public String getTaxAuthorityName() 
    {
        return taxAuthorityName;
    }

    /**
     * @param taxAuthorityName the taxAuthorityName to set.
     */
    public void setTaxAuthorityName(String taxAuthorityName) 
    {
        this.taxAuthorityName = taxAuthorityName;
    }

    /**
     * @return Returns the taxGroupID.
     */
    public int getTaxGroupID() 
    {
        return taxGroupID;
    }

    /**
     * @param value The taxGroupID to set.
     */
    public void setTaxGroupID(int value) 
    {
        taxGroupID = value;
    }

    /**
     * @return Returns the taxRuleName.
     */
    public String getTaxRuleName() 
    {
        return taxRuleName;
    }

    /**
     * @param value The taxRuleName to set.
     */
    public void setTaxRuleName(String value) 
    {
        taxRuleName = value;
    }

    /**
     * Gets the tax type 
     * @return tax type
     */
    public int getTaxTypeCode()
    {
        return taxTypeCode;
    }
    
    /**
     * Sets the tax type 
     * @param value
     */
    public void setTaxTypeCode(int value)
    {
        taxTypeCode = value;
    }
    
    /**
     * Set whether or not this is a tax holiday rule
     *  
     * @param value true or false
     * @see oracle.retail.stores.domain.tax.RunTimeTaxRuleIfc#setTaxHoliday(boolean)
     */
    public void setTaxHoliday(boolean value)
    {
        this.taxHoliday = value;
    }
    
    /**
     * Return whether or not this is a tax holiday rule
     *  
     * @return true or false
     * @see oracle.retail.stores.domain.tax.RunTimeTaxRuleIfc#isTaxHoliday()
     */
    public boolean isTaxHoliday()
    {
        return this.taxHoliday;
    }
    
	/**
	 * @return Returns the inclusiveTaxFlag.
	 */
	public boolean getInclusiveTaxFlag() {
		return inclusiveTaxFlag;
	}
	
	/**
	 * @param inclusiveTaxFlag The inclusiveTaxFlag to set.
	 */
	public void setInclusiveTaxFlag(boolean inclusiveTaxFlag) {
		this.inclusiveTaxFlag = inclusiveTaxFlag;
	}
	
    /**
     * Add rule unique ID to determine if another rule supersedes 
     * this one.<P>  
     * 
     * For example if you have a threshold tax which if it applies
     * then this rule should not apply to the item.  
     * 
     * @param value uniqueID of the rule that can override this rule
     */
    public void addOverrideRuleUniqueID(String value)
    {
        overrideUniqueIDs.add(value);
    }

    /**
     *  This method provides an additional message which will help locate 
     *  the offending tax rule.
     *  @param The error text associated with the specific error text.
     *  @return The completed error text
     */
    public String getValidationErrorMessage(String message)
    {
        String completeMessage = message + 
            "  Tax Rule Name: " + getTaxRuleName() +
            "; Tax Authority ID: " + getTaxAuthorityID() +
            "; Tax Group ID: " + getTaxGroupID() +
            "\n\t  The tax for items using this rule will be calculated at default rate.";
        
        return completeMessage;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        else if (obj == null || !obj.getClass().equals(this.getClass()))
        {
            return false;
        }
        else
        {
            AbstractTaxRule that = (AbstractTaxRule) obj;
            EqualsBuilder builder = new EqualsBuilder();
            builder.append(uniqueID, that.uniqueID);
            builder.append(taxRuleName, that.taxRuleName);
            builder.append(taxAuthorityName, that.taxAuthorityName);
            builder.append(taxGroupID, that.taxGroupID);
            builder.append(taxTypeCode, that.taxTypeCode);
            builder.append(order, that.order);
            builder.append(useBasePrice, that.useBasePrice);
            builder.append(taxHoliday, that.taxHoliday);
            builder.append(inclusiveTaxFlag, that.inclusiveTaxFlag);
            return builder.isEquals();
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("uniqueID", uniqueID);
        builder.append("taxRuleName", taxRuleName);
        builder.append("taxAuthorityName", taxAuthorityName);
        builder.append("taxGroupID", taxGroupID);
        builder.append("taxTypeCode", taxTypeCode);
        builder.append("order", order);
        builder.append("useBasePrice", useBasePrice);
        builder.append("taxHoliday", taxHoliday);
        builder.append("inclusiveTaxFlag", inclusiveTaxFlag);
        return builder.toString();
    }
}
