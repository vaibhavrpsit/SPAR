/* ===========================================================================
* Copyright (c) 2004, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/TaxInformation.java /main/22 2014/07/24 15:23:28 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       07/22/14 - add tax authority name
 *    yiqzhao   08/08/13 - Return empty string when the tax rate is zero.
 *    sgu       11/14/12 - added discount and tax pickup view
 *    arabalas  08/17/12 - Forward Port: added a new function for printing tax
 *                         percentage in locale specific format
 *    arabalas  08/16/12 - Forward Port: added a new function for printing tax
 *                         percentage in locale specific format
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    sgu       10/21/11 - negate return tax correctly
 *    sgu       09/22/11 - add function to set sign for tax amounts
 *    sgu       09/22/11 - negate return tax in post void case
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    cgreene   03/25/09 - move determination of tax receipt code to
 *                         TaxInformation.getReceiptCode since the code should
 *                         be determined at request time, not at set time
 *    asinton   01/29/09 - Updated VAT Summary formatting for Type 2 VAT
 *                         receipts.
 *    cgreene   10/17/08 - added inclusiveTaxableAmount and receiptCode
 *
 * ===========================================================================
 * $Log:
 * 11   360Commerce 1.10        5/31/2007 5:57:46 PM   Sandy Gu        added
 *      test cases and fixed problems resulting from that
 * 10   360Commerce 1.9         5/14/2007 6:08:34 PM   Sandy Gu        update
 *      inclusive information in financial totals and history tables
 * 9    360Commerce 1.8         4/30/2007 5:38:35 PM   Sandy Gu        added api
 *       to handle inclusive tax
 * 8    360Commerce 1.7         4/25/2007 10:00:25 AM  Anda D. Cadar   I18N
 *      merge
 * 7    360Commerce 1.6         2/13/2006 11:19:00 AM  Brett J. Larsen Merge
 *      from TaxInformation.java, Revision 1.5.1.0
 * 7    .v720     1.5.1.0     2/6/2006 8:45:03 PM    Brett J. Larsen CR 10385 -
 *      incorrect line item tax on receipts - this backs out the faulty changes
 *      from the 7.0.3 -> 7.1.1 merge (which were then merged into 7.2)
 * 7    .v710     1.2.3.3     11/11/2005 4:20:41 PM  Brett J. Larsen CR 7299 -
 *      changed toString to print out all info (noticed during CR 7299 code
 *      review)
 * 6    .v710     1.2.3.2     11/7/2005 11:23:10 AM  Charles Suehs   change
 *      add(TaxInformationIfc addTaxInformation): use temporary variables to
 *      store the results in as we calculate them.  Once they are all
 *      calculated, we can then set them.
 * 5    .v710     1.2.3.1     10/25/2005 5:22:09 PM  Charles Suehs   Merge from
 *      v700
 * 4    .v710     1.2.3.0     10/24/2005 3:48:24 PM  Charles Suehs   Merge from
 *      TaxInformation.java, Revision 1.2.2.0
 * 6    .v710     1.2.3.2     11/7/2005 11:23:10 AM  Charles Suehs   change
 *      add(TaxInformationIfc addTaxInformation): use temporary variables to
 *      store the results in as we calculate them.  Once they are all
 *      calculated, we can then set them.
 * 4    .v700     1.2.2.0     9/28/2005 12:28:02 PM  Jason L. DeLeau 4067: Fix
 *      the way a receipt prints for a price adjusted threshold item, where the
 *      price adjustment is below the threshold,
 *      and the original price is above it.
 * 3    360Commerce1.2         3/31/2005 3:30:19 PM   Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:25:47 AM  Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:14:42 PM  Robert Pearse
 * $
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;

import java.math.BigDecimal;
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceLocator;
import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;

/**
 * Reference implementation of the TaxInformationIfc.  This is the
 * data structure for storing tax information from a transaction.
 * This handles transaction level and line item level tax data.
 *
 * @author mkp1
 * $Revision: /main/22 $
 */
@SuppressWarnings("deprecation")
public class TaxInformation implements TaxInformationIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 5619664826707519668L;

    private String uniqueID;
    private int taxTypeCode;
    boolean taxHoliday;
    private int taxAuthorityID;
    private String taxAuthorityName;
    private int taxGroupID;
    private String taxRuleName;
    private int taxMode;
    private String geoCode;
    private BigDecimal taxPercentage;
    private String taxPercentageString;
    private boolean usesTaxRate;
    private boolean inclusiveTaxFlag = false;
    private int orderItemTaxLineReference = 0;

    /**
     * Internal variables.  Not tied to a database, used for
     * receipt printing purposes.
     */
    private CurrencyIfc negativeTaxableAmount;
    private CurrencyIfc negativeTaxAmount;
    private CurrencyIfc positiveTaxableAmount;
    private CurrencyIfc positiveTaxAmount;
    private CurrencyIfc effectiveTaxableAmount;
    private CurrencyIfc inclusiveTaxableAmount;

    /**
     * Get the taxable amount. This maps to MO_TXBL_RTN_SLS
     * the table TR_LTM_SLS_RTN_TX.
     *
     * @return taxable amount
     *
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#getTaxableAmount()
     */
    public CurrencyIfc getTaxableAmount()
    {
    	return getPositiveTaxableAmount().add(getNegativeTaxableAmount());
    }

    /**
     * Set the taxable amount. This maps to MO_TXBL_RTN_SLS
     * the table TR_LTM_SLS_RTN_TX.
     *
     * @param value taxable amount
     *
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#setTaxableAmount(oracle.retail.stores.domain.currency.CurrencyIfc)
     */
    public void setTaxableAmount(CurrencyIfc value)
    {
        if(value.signum() == CurrencyIfc.POSITIVE)
        {
            setPositiveTaxableAmount(value);
            setNegativeTaxableAmount(null);
        }
        else
        {
            setPositiveTaxableAmount(null);
            setNegativeTaxableAmount(value);
        }
    }

    /**
     * Get the tax amount charged. This is the MO_TAX
     * field in the TR_LTM_TX table.  For the TR_LTM_SLS_RTN_TX
     * table, this maps to the field MO_TX_RTN_SLS which is
     * taxAmountPerAuthority.  If a line item goes across multiple
     * jurisdictions, this will be the tax amount for the jurisdiction
     * defined by the TaxAuthorityID.
     *
     * @return taxAmount
     *
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#getTaxAmount()
     */
    public CurrencyIfc getTaxAmount()
    {
    	return getPositiveTaxAmount().add(getNegativeTaxAmount());
    }

    /**
     * Set the tax amount charged. This is the MO_TAX
     * field in the TR_LTM_TX table.  For the TR_LTM_SLS_RTN_TX
     * table, this maps to the field MO_TX_RTN_SLS which is
     * taxAmountPerAuthority.  If a line item goes across multiple
     * jurisdictions, this will be the tax amount for the jurisdiction
     * defined by the TaxAuthorityID.
     *
     * @param value taxAmount
     *
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#setTaxAmount(oracle.retail.stores.domain.currency.CurrencyIfc)
     */
    public void setTaxAmount(CurrencyIfc value)
    {
        if(value.signum() != CurrencyIfc.NEGATIVE)
        {
            setPositiveTaxAmount(value);
            setNegativeTaxAmount(null);
        }
        else
        {
            setPositiveTaxAmount(null);
            setNegativeTaxAmount(value);
        }
    }

    /**
     * Clear the tax amount charged. This is the MO_TAX field in the TR_LTM_TX
     * table. For the TR_LTM_SLS_RTN_TX table, this maps to the field
     * MO_TX_RTN_SLS which is taxAmountPerAuthority. If a line item goes across
     * multiple jurisdictions, this will be the tax amount for the jurisdiction
     * defined by the TaxAuthorityID.
     */
    public void clearTaxAmount()
    {
        setPositiveTaxAmount(null);
        setNegativeTaxAmount(null);
    }

    /**
     * Retrieve the tax mode.  Valid values are
     * the constants defined in TaxConstantsIfc.java.
     * This maps to values like TAX_EXEMPT, etc.
     *
     * @param value TaxMode
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#setTaxMode(int)
     */
    public void setTaxMode(int value)
    {
        taxMode = value;
    }

    /**
     * Retrieve the tax mode.  Valid values are
     * the constants defined in TaxConstantsIfc.java.
     * This maps to TY_TX in the table TR_LTM_TX.
     *
     * @return tax typeMode
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#getTaxMode()
     */
    public int getTaxMode()
    {
        return taxMode;
    }

    /**
     * Set the name of the tax rule. This
     * maps to the field NM_RU_TX in the
     * table TR_LTM_SLS_RTN_TX.
     *
     * @param value
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#setTaxRuleName(java.lang.String)
     */
    public void setTaxRuleName(String value)
    {
        taxRuleName = value;
    }

    /**
     * Get the name of the tax rule. This
     * maps to the field NM_RU_TX in the
     * table TR_LTM_SLS_RTN_TX.
     *
     * @return name of the tax rule
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#getTaxRuleName()
     */
    public String getTaxRuleName()
    {
        String returnValue = taxRuleName;
        if(taxRuleName == null)
        {
            returnValue = "";
        }
        return returnValue;
    }

    /**
     * Set the ID of the tax Authority.  This maps
     * to the field ID_ATHY_TX in both the table
     * TR_LTM_TX and the table TR_LTM_SLS_RTN_TX.
     *
     * @param value The tax authority
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#setTaxAuthorityID(int)
     */
    public void setTaxAuthorityID(int value)
    {
        taxAuthorityID = value;
    }

    /**
     * Get the ID of the tax Authority.  This maps
     * to the field ID_ATHY_TX in both the table
     * TR_LTM_TX and the table TR_LTM_SLS_RTN_TX.
     *
     * @return The tax authority
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#getTaxAuthorityID()
     */
    public int getTaxAuthorityID()
    {
        return taxAuthorityID;
    }
        
    /**
     * Get the name of the tax authority. This maps 
     * to the field NM_ATHY_TX of table PA_ATHY_TX.
     * 
     * @return the tax authority name
     */
    public String getTaxAuthorityName() 
    {
        return taxAuthorityName;
    }

    /**
     * Set the name of the tax authority. This maps 
     * to the field NM_ATHY_TX of table PA_ATHY_TX.
     * 
     * @param taxAuthorityName the tax authority name
     */
    public void setTaxAuthorityName(String taxAuthorityName) 
    {
        this.taxAuthorityName = taxAuthorityName;
    }

    /**
     * Get the Tax Group ID.  This maps to the
     * field ID_GP_TX in the tables TR_LTM_TX and
     * TR_LTM_SLS_RTN_TX. A tax Group ID is a logical
     * construct to group items that use the same
     * tax rules. It is primarily used as a foreign
     * key for tax lookup.
     *
     * @param value Tax group ID
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#setTaxGroupID(int)
     */
    public void setTaxGroupID(int value)
    {
        taxGroupID = value;
    }

    /**
     * Get the Tax Group ID.  This maps to the
     * field ID_GP_TX in the tables TR_LTM_TX and
     * TR_LTM_SLS_RTN_TX.
     *
     * @return Tax Group ID
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#getTaxGroupID()
     */
    public int getTaxGroupID()
    {
        return taxGroupID;
    }

    /**
     * @return Returns the geoCode.
     */
    public String getGeoCode()
    {
        String returnValue = geoCode;
        if(returnValue == null)
        {
            returnValue = geoCode;
        }
        return geoCode;
    }

    /**
     * @param geoCode The geoCode to set.
     */
    public void setGeoCode(String value)
    {
        geoCode = value;
    }

    /**
     * @return Returns the taxTypeCode.
     */
    public int getTaxTypeCode()
    {
        return taxTypeCode;
    }

    /**
     * @param taxTypeCode The taxTypeCode to set.
     */
    public void setTaxTypeCode(int value)
    {
        taxTypeCode = value;
    }

    /**
     * Get the tax holiday
     *
     * @return value set
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#getTaxHoliday()
     */
    public boolean getTaxHoliday()
    {
        return this.taxHoliday;
    }

    /**
     * Set the tax holiday
     *
     * @param value value to set
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#setTaxHoliday(boolean)
     */
    public void setTaxHoliday(boolean value)
    {
        this.taxHoliday = value;
    }

    /**
     * @return Returns the taxPercent.
     */
    public BigDecimal getTaxPercentage()
    {
        BigDecimal returnValue = taxPercentage;
        if(taxPercentage == null)
        {
            returnValue = BigDecimal.ZERO;
        }
        return returnValue;
    }

    /**
     * @param taxPercent The taxPercent to set.
     */
    public void setTaxPercentage(BigDecimal value)
    {
        taxPercentage = value;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#getTaxPercentage()
     */
    public String getTaxPercentageAsString()
    {
        String returnValue = "";
        if(taxPercentageString != null)
        {
            returnValue = taxPercentageString;
        }
        return returnValue;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#setTaxPercentageAsString(java.lang.String)
     */
    public void setTaxPercentageAsString(String value)
    {
        taxPercentageString = value;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#getTaxPercentageAsLocaleString()
     */
    public String getTaxPercentageAsLocaleString()
    {
        String returnValue = taxPercentageString;
        if ( taxPercentage.equals(BigDecimal.ZERO) )
        {
            returnValue = "";
        }
        else if(taxPercentageString != null)
        {
            CurrencyServiceIfc currencyService = CurrencyServiceLocator.getCurrencyService();
            //The tax percentage is formatted based on Locale also the scale is set to 4.
            returnValue = currencyService.formatCurrency(taxPercentageString, LocaleMap.getLocale(LocaleMap.DEFAULT),4);
        }
        return returnValue;
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#getUniqueID()
     */
    public String getUniqueID()
    {
        String result = uniqueID;
        if(result == null)
        {
            result = "";
        }
        return result;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#setUniqueID(java.lang.String)
     */
    public void setUniqueID(String value)
    {
        uniqueID = value;
    }

    /**
     * Add one taxInformationIfc item to another
     *
     * @param addTaxInformation
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#add(oracle.retail.stores.domain.tax.TaxInformationIfc)
     */
    public void add(TaxInformationIfc addTaxInformation)
    {
        //setTaxableAmount(getTaxableAmount().add(addTaxInformation.getTaxableAmount()));
        //setTaxAmount(getTaxAmount().add(addTaxInformation.getTaxAmount()));
        CurrencyIfc positiveTaxableAmount =
            getPositiveTaxableAmount().add(addTaxInformation.getPositiveTaxableAmount());
        CurrencyIfc positiveTaxAmount =
            getPositiveTaxAmount().add(addTaxInformation.getPositiveTaxAmount());
        CurrencyIfc negTaxableAmount =
            getNegativeTaxableAmount().add(addTaxInformation.getNegativeTaxableAmount());
    	CurrencyIfc negTaxAmount =
            getNegativeTaxAmount().add(addTaxInformation.getNegativeTaxAmount());
    	CurrencyIfc effTaxableAmount =
            getEffectiveTaxableAmount().add(addTaxInformation.getEffectiveTaxableAmount());

        setPositiveTaxableAmount(positiveTaxableAmount);
        setPositiveTaxAmount(positiveTaxAmount);
        setNegativeTaxableAmount(negTaxableAmount);
        setNegativeTaxAmount(negTaxAmount);
        setEffectiveTaxableAmount(effTaxableAmount);
    }

    /**
     * Set if this rule uses tax rates to calculate tax
     *
     * @param value
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#setUsesTaxRate(boolean)
     */
    public void setUsesTaxRate(boolean value)
    {
        this.usesTaxRate = value;
    }

    /**
     * Tell if this rule uses tax rates to calculate tax
     *
     * @return true or false
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#getUsesTaxRate()
     */
    public boolean getUsesTaxRate()
    {
        return this.usesTaxRate;
    }

    /**
     * Set the negative taxable amount
     * @return negativeTaxableAmount
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#getNegativeTaxableAmount()
     * @since 7.0.2
     */
    public void setNegativeTaxableAmount(CurrencyIfc negativeTaxableAmount)
    {
    	this.negativeTaxableAmount = negativeTaxableAmount;
    }

    /**
     * Set the negative taxable amount
     * @param negativeTaxableAmount
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#setNegativeTaxableAmount()
     * @since 7.0.2
     */
    public CurrencyIfc getNegativeTaxableAmount()
    {
        if(this.negativeTaxableAmount == null)
        {
            this.negativeTaxableAmount = DomainGateway.getBaseCurrencyInstance();
        }
    	return this.negativeTaxableAmount;
    }

    /**
     * Set the negative tax amount
     * @param negativeTaxAmount
     * @since 7.0.2
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#setNegativeTaxAmount()
     */
    public void setNegativeTaxAmount(CurrencyIfc negativeTaxAmount)
    {
    	this.negativeTaxAmount = negativeTaxAmount;
    }

    /**
     * Get the negative tax amount
     * @return negativeTaxAmount
     * @since 7.0.2
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#getNegativeTaxAmount()
     */
    public CurrencyIfc getNegativeTaxAmount()
    {
        if(this.negativeTaxAmount == null)
        {
            this.negativeTaxAmount = DomainGateway.getBaseCurrencyInstance();
        }
    	return this.negativeTaxAmount;
    }

    /**
     * Set the positive taxable amount
     * @param positiveTaxableAmount
     * @since 7.0.2
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#setPositiveTaxableAmount()
     */
    public void setPositiveTaxableAmount(CurrencyIfc positiveTaxableAmount)
    {
    	this.positiveTaxableAmount = positiveTaxableAmount;
    }

    /**
     * Get the positive taxable amount
     * @return positiveTaxableAmount
     * @since 7.0.2
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#getPositiveTaxableAmount()
     */
    public CurrencyIfc getPositiveTaxableAmount()
    {
        if(this.positiveTaxableAmount == null)
        {
            this.positiveTaxableAmount = DomainGateway.getBaseCurrencyInstance();
        }
    	return this.positiveTaxableAmount;
    }

    /**
     * Set the positiveTaxAmount
     * @param positiveTaxableAmount
     * @since 7.0.2
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#setPositiveTaxAmount()
     */
    public void setPositiveTaxAmount(CurrencyIfc positiveTaxAmount)
    {
    	this.positiveTaxAmount = positiveTaxAmount;
    }

    /**
     * Get the positive tax amount
     * @return positiveTaxAmount
     * @since 7.0.2
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#getPositiveTaxAmount()
     */
    public CurrencyIfc getPositiveTaxAmount()
    {
        if(this.positiveTaxAmount == null)
        {
            this.positiveTaxAmount = DomainGateway.getBaseCurrencyInstance();
        }
    	return this.positiveTaxAmount;
    }

    /**
     * Get the effective taxable amount. This is the taxable amount that contributed
     * to a non-zero tax.  In other words, items below a threshold are not included
     * in this total.  This is the same as getTaxableAmount for all tax types except
     * threshold tax.
     * @return effectiveTaxableAmount
     * @since 7.0.2
     */
    public CurrencyIfc getEffectiveTaxableAmount()
    {
    	if(this.effectiveTaxableAmount == null)
    	{
    		this.effectiveTaxableAmount = getTaxableAmount();
    	}
    	return this.effectiveTaxableAmount;
    }

    /**
     * Set the effective taxable amount for this item
     * @param effectiveTaxableAmount
     * @since 7.0.2
     */
    public void setEffectiveTaxableAmount(CurrencyIfc effectiveTaxableAmount)
    {
    	this.effectiveTaxableAmount = effectiveTaxableAmount;
    }
    /**
     * Clone a TaxInformation object
     *
     * @return A clone of this object
     * @see java.lang.Object#clone()
     */
    public Object clone()
    {
        TaxInformation newClass = new TaxInformation();
        setCloneAttributes(newClass);
        return newClass;
    }

    /**
     * Set the attributes for a clone <P>
     *
     *  @param newClass  Class to put this information into
     */
    public void setCloneAttributes(TaxInformation newClass)
    {
        newClass.setPositiveTaxableAmount((CurrencyIfc) getPositiveTaxableAmount().clone());
        newClass.setPositiveTaxAmount((CurrencyIfc) getPositiveTaxAmount().clone());
        newClass.setNegativeTaxableAmount((CurrencyIfc)getNegativeTaxableAmount().clone());
        newClass.setNegativeTaxAmount((CurrencyIfc)getNegativeTaxAmount().clone());
        newClass.setEffectiveTaxableAmount((CurrencyIfc)getEffectiveTaxableAmount().clone());
        newClass.setTaxTypeCode(getTaxTypeCode());
        newClass.setTaxHoliday(getTaxHoliday());
        newClass.setTaxAuthorityID(getTaxAuthorityID());
        newClass.setTaxAuthorityName(getTaxAuthorityName());
        newClass.setTaxGroupID(getTaxGroupID());
        newClass.setTaxRuleName(getTaxRuleName());
        newClass.setTaxMode(getTaxMode());
        newClass.setUniqueID(getUniqueID());
        newClass.setGeoCode(getGeoCode());
        newClass.setTaxPercentage(new BigDecimal(getTaxPercentage().doubleValue()));
        newClass.setTaxPercentageAsString(getTaxPercentage().toString());
        newClass.setUsesTaxRate(getUsesTaxRate());
        newClass.setInclusiveTaxFlag(getInclusiveTaxFlag());
        newClass.setOrderItemTaxLineReference(getOrderItemTaxLineReference());
    }

    /**
     * Determine if two TaxInformation objects are equal.
     * Equality is defined as all attributes being equal,
     * through the == operator for primatives, or through
     * the Util.isObjectEqual(attribute) method for objects.
     *
     * @param arg
     * @return true if equal, false if not.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object arg)
    {
        boolean returnValue = false;
        if(arg != null && arg instanceof TaxInformation)
        {
            TaxInformation taxInformation = (TaxInformation) arg;
            returnValue = Util.isObjectEqual(positiveTaxableAmount, taxInformation.positiveTaxableAmount) &&
                Util.isObjectEqual(positiveTaxAmount, taxInformation.positiveTaxAmount) &&
                Util.isObjectEqual(negativeTaxableAmount, taxInformation.negativeTaxableAmount) &&
                Util.isObjectEqual(negativeTaxAmount, taxInformation.negativeTaxAmount) &&
                Util.isObjectEqual(effectiveTaxableAmount, taxInformation.effectiveTaxableAmount) &&
                Util.isObjectEqual(getTaxRuleName(), taxInformation.getTaxRuleName()) &&
                Util.isObjectEqual(getGeoCode(), taxInformation.getGeoCode()) &&
                Util.isObjectEqual(getUniqueID(), taxInformation.getUniqueID()) &&
                getTaxAuthorityID() == taxInformation.getTaxAuthorityID() &&
                Util.isObjectEqual(getTaxAuthorityName(), taxInformation.getTaxAuthorityName()) &&
                getTaxGroupID() == taxInformation.getTaxGroupID() &&
                getTaxMode() == taxInformation.getTaxMode() &&
                getTaxHoliday() == taxInformation.getTaxHoliday() &&
                getUsesTaxRate() == taxInformation.getUsesTaxRate() &&
                getTaxTypeCode() == taxInformation.getTaxTypeCode() &&
				getInclusiveTaxFlag() == taxInformation.getInclusiveTaxFlag() &&
				getOrderItemTaxLineReference() == taxInformation.getOrderItemTaxLineReference();
        }
        return returnValue;
    }

    /**
     * String representation of this object
     *
     * @return String describing this object
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer strResult = new StringBuffer(super.toString()).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("geoCode", getGeoCode())).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("positiveTaxableAmount", positiveTaxableAmount)).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("negativeTaxableAmount", negativeTaxableAmount)).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("positiveTaxAmount", positiveTaxAmount)).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("negativeTaxAmount", negativeTaxAmount)).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("effectiveTaxableAmount", effectiveTaxableAmount)).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("taxAuthorityID", getTaxAuthorityID())).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("taxAuthorityName", getTaxAuthorityName())).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("taxGroupID", getTaxGroupID())).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("taxHoliday", getTaxHoliday())).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("taxMode", getTaxMode())).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("taxRuleName", getTaxRuleName())).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("taxTypeCode", taxTypeCode)).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("usesTaxRate", usesTaxRate)).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("inclusiveTaxFlag", inclusiveTaxFlag)).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("orderItemTaxLineReference", orderItemTaxLineReference)).append(Util.EOL);
        return strResult.toString();
    }

    /**
     * Negate all the currency values for returns
     */
    public void negate()
    {
        negateTaxableAmount();
        negateTaxAmount();
    }

    /**
     * Negate taxable amount
     */
    protected void negateTaxableAmount()
    {
        CurrencyIfc oldPositiveTaxableAmount = this.getPositiveTaxableAmount();
        this.setPositiveTaxableAmount(getNegativeTaxableAmount().abs());
        this.setNegativeTaxableAmount(oldPositiveTaxableAmount.negate());
        this.setEffectiveTaxableAmount(getEffectiveTaxableAmount().negate());
    }

    /**
     * Negate tax amount
     */
    protected void negateTaxAmount()
    {
        CurrencyIfc oldPositiveTaxAmount = this.getPositiveTaxAmount();
        this.setPositiveTaxAmount(getNegativeTaxAmount().abs());
        this.setNegativeTaxAmount(oldPositiveTaxAmount.negate());
    }

    /**
     * Set all currency values to be the specified sign
     *
     * @param sign CurrencyIfc.POSITIVE or CurrencyIfc.Negative
     */
    public void setSign(int sign)
    {
        if (sign == CurrencyIfc.POSITIVE || sign == CurrencyIfc.NEGATIVE)
        {
            int negatedSign = sign * -1;
            if (getTaxableAmount().signum() == negatedSign)
            {
                negateTaxableAmount();
            }
            if (getTaxAmount().signum() == negatedSign)
            {
                negateTaxAmount();
            }
        }
    }

    /**
     * Compare two objects of this type
     *
     * @param obj Object to compare against this one
     * @return whether its greater, less than, or equal to this one
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(TaxInformationIfc other)
    {
        Locale  locale = LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT);
        return LocaleUtilities.compareValues(getTaxRuleName(), other.getTaxRuleName(), locale);
    }

    /**
     * @return boolean inclusive tax flag
     */
    public boolean getInclusiveTaxFlag()
    {
    	return inclusiveTaxFlag;
    }

    /**
     * Set the inclusive tax flag
     * @param inclusiveTaxFlag the inclusive tax flag
     */
    public void setInclusiveTaxFlag(boolean inclusiveTaxFlag)
    {
    	this.inclusiveTaxFlag = inclusiveTaxFlag;
    }


    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#getInclusiveTaxableAmount()
     */
    public CurrencyIfc getInclusiveTaxableAmount()
    {
        return inclusiveTaxableAmount;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#setInclusiveTaxableAmount(oracle.retail.stores.commerceservices.common.currency.CurrencyIfc)
     */
    public void setInclusiveTaxableAmount(CurrencyIfc inclusiveTaxableAmount)
    {
        this.inclusiveTaxableAmount = inclusiveTaxableAmount;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#getReceiptCode()
     */
    public String getReceiptCode()
    {
        String receiptCode = TaxConstantsIfc.TAX_MODE_CHAR[getTaxMode()];
        if (getInclusiveTaxFlag())
        {
            receiptCode = getTaxRuleName();
        }
        return receiptCode;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#getReceiptTaxPercentage()
     */
    public BigDecimal getReceiptTaxPercentage()
    {
        BigDecimal percent = getTaxPercentage();
        if (percent != null && percent.signum() == 0 &&
                getTaxAmount() != null && getTaxAmount().signum() == 1)
        {
            // if there is a tax amount, but the percent is zero, return null
            percent = null;
        }
        return percent;
    }

	/**
	 * Get order item tax line reference
	 * 
	 * @return the reference id
	 */
	public int getOrderItemTaxLineReference() 
	{
		return orderItemTaxLineReference;
	}

	/**
	 * Set order item tax line reference
	 * 
	 * @param orderItemTaxLineReference the reference id
	 */
	public void setOrderItemTaxLineReference(int orderItemTaxLineReference) 
	{
		this.orderItemTaxLineReference = orderItemTaxLineReference;
	}

	@Override
	public void addTaxAmount(CurrencyIfc arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CurrencyIfc getRawTaxAmount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRawTaxAmount(CurrencyIfc arg0) {
		// TODO Auto-generated method stub
		
	}
}
