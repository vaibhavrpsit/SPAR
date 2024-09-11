/* ===========================================================================
* Copyright (c) 2004, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/FFTaxVO.java /main/15 2014/07/24 15:23:28 sgu Exp $
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
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         7/26/2007 7:59:53 AM   Alan N. Sinton  CR
 *         27192 Make item lookup depend on department tax group ID if item's
 *         tax group ID is invalid.
 *    7    360Commerce 1.6         7/3/2007 7:10:13 AM    Manikandan Chellapan
 *         CR#27329 BuildFF cleanup bulk check in
 *    6    360Commerce 1.5         4/30/2007 5:38:35 PM   Sandy Gu        added
 *          api to handle inclusive tax
 *    5    360Commerce 1.4         4/25/2007 10:01:06 AM  Anda D. Cadar   I18N
 *         merge
 *    4    360Commerce 1.3         1/22/2006 11:41:14 AM  Ron W. Haight
 *         Removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:10 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:37 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:02 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:30:50  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.5  2004/07/02 19:11:27  jdeleau
 *   @scr 5982 Support Tax Holiday
 *
 *   Revision 1.4  2004/06/22 22:33:43  jdeleau
 *   @scr 5775 Take care of null date ranges causing item not found errors.
 *
 *   Revision 1.3  2004/06/18 13:59:09  jdeleau
 *   @scr 2775 Unify the way rules are generated, so that flat files and 
 *   the database use the same business logic
 *
 *   Revision 1.2  2004/06/10 15:34:53  jdeleau
 *   @scr 2775 Make Flat File PLU lookups contain tax rules
 *
 *   Revision 1.1  2004/06/10 14:21:29  jdeleau
 *   @scr 2775 Use the new tax data for the tax flat files
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import java.math.BigDecimal;
import java.io.Serializable;

/**
 * This tax contains one tax rule as defined in the flat file.  This object
 * knows how to sort itself based on compoundSequenceNumber, which is necessary to
 * guarantee that the tax rules are formed correctly.
 * 
 * $Revision: /main/15 $
 */
public class FFTaxVO implements Comparable, Serializable
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 298342698422340722L;

    private int taxAuthorityId;
    private String taxAuthorityName;
    private int taxGroupId;
    private int taxType;
    private boolean taxHoliday;
    private String taxRuleName;
    private int compoundSequenceNumber;
    private boolean taxOnGrossAmountFlag;
    private int calculationMethodCode;
    private int taxRateRuleUsageCode;
    private int taxTypeCode;
    private BigDecimal taxPercentage;
    private CurrencyIfc taxAmount;
    private boolean taxAboveThresholdAmountFlag;
    private CurrencyIfc taxThresholdAmount;
    private CurrencyIfc minimumTaxableAmount;
    private CurrencyIfc maximumTaxableAmount;
    private EYSDate taxRateExpirationTimestamp;
    private EYSDate taxRateEffectiveTimestamp;
    private int roundingCode;
    private int roundingDigits;
    private boolean inclusiveTaxFlag = false;	// by default, it is always false
    
    /**
     *  Default constructor
     */
    public FFTaxVO()
    {
        super();
    }
    
    /**
     * Compare an object to this one.  This is the 
     * implementation of the comparable interface.
     *  
     * @param obj
     * @return
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object obj)
    {
        FFTaxVO otherObj = (FFTaxVO) obj;
        return getCompoundSequenceNumber() - otherObj.getCompoundSequenceNumber();
    }

    /**
     * @return Returns the calculationMethodCode.
     */
    public int getCalculationMethodCode()
    {
        return calculationMethodCode;
    }
    /**
     * @param calculationMethodCode The calculationMethodCode to set.
     */
    public void setCalculationMethodCode(int calculationMethodCode)
    {
        this.calculationMethodCode = calculationMethodCode;
    }
    /**
     * @return Returns the compoundSequenceNumber.
     */
    public int getCompoundSequenceNumber()
    {
        return compoundSequenceNumber;
    }
    /**
     * @param compoundSequenceNumber The compoundSequenceNumber to set.
     */
    public void setCompoundSequenceNumber(int compoundSequenceNumber)
    {
        this.compoundSequenceNumber = compoundSequenceNumber;
    }
    /**
     * @return Returns the maximumTaxableAmount.
     */
    public CurrencyIfc getMaximumTaxableAmount()
    {
        return maximumTaxableAmount;
    }
    /**
     * @param maximumTaxableAmount The maximumTaxableAmount to set.
     */
    public void setMaximumTaxableAmount(CurrencyIfc maximumTaxableAmount)
    {
        this.maximumTaxableAmount = maximumTaxableAmount;
    }
    /**
     * @return Returns the minimumTaxableAmount.
     */
    public CurrencyIfc getMinimumTaxableAmount()
    {
        return minimumTaxableAmount;
    }
    /**
     * @param minimumTaxableAmount The minimumTaxableAmount to set.
     */
    public void setMinimumTaxableAmount(CurrencyIfc minimumTaxableAmount)
    {
        this.minimumTaxableAmount = minimumTaxableAmount;
    }
    /**
     * @return Returns the roundingCode.
     */
    public int getRoundingCode()
    {
        return roundingCode;
    }
    /**
     * @param roundingCode The roundingCode to set.
     */
    public void setRoundingCode(int roundingCode)
    {
        this.roundingCode = roundingCode;
    }
    /**
     * @return Returns the roundingDigits.
     */
    public int getRoundingDigits()
    {
        return roundingDigits;
    }
    /**
     * @param roundingDigits The roundingDigits to set.
     */
    public void setRoundingDigits(int roundingDigits)
    {
        this.roundingDigits = roundingDigits;
    }
    /**
     * @return Returns the taxAboveThresholdAmountFlag.
     */
    public boolean isTaxAboveThresholdAmountFlag()
    {
        return taxAboveThresholdAmountFlag;
    }
    /**
     * @param taxAboveThresholdAmountFlag The taxAboveThresholdAmountFlag to set.
     */
    public void setTaxAboveThresholdAmountFlag(boolean taxAboveThresholdAmountFlag)
    {
        this.taxAboveThresholdAmountFlag = taxAboveThresholdAmountFlag;
    }
    /**
     * @return Returns the taxAmount.
     */
    public CurrencyIfc getTaxAmount()
    {
        return taxAmount;
    }
    /**
     * @param taxAmount The taxAmount to set.
     */
    public void setTaxAmount(CurrencyIfc taxAmount)
    {
        this.taxAmount = taxAmount;
    }
    /**
     * @return Returns the taxAuthorityId.
     */
    public int getTaxAuthorityId()
    {
        return taxAuthorityId;
    }
    /**
     * @param taxAuthorityId The taxAuthorityId to set.
     */
    public void setTaxAuthorityId(int taxAuthorityId)
    {
        this.taxAuthorityId = taxAuthorityId;
    }
    
    /**
     * @return tax authority name
     */
    public String getTaxAuthorityName()
    {
        return taxAuthorityName;
    }

    /**
     * @param taxAuthorityName tax authority name
     */
    public void setTaxAuthorityName(String taxAuthorityName)
    {
        this.taxAuthorityName = taxAuthorityName;
    }

    /**
     * @return Returns the taxGroupId.
     */
    public int getTaxGroupId()
    {
        return taxGroupId;
    }
    /**
     * @param taxGroupId The taxGroupId to set.
     */
    public void setTaxGroupId(int taxGroupId)
    {
        this.taxGroupId = taxGroupId;
    }
    /**
     * @return Returns the taxOnGrossAmountFlag.
     */
    public boolean isTaxOnGrossAmountFlag()
    {
        return taxOnGrossAmountFlag;
    }
    /**
     * @param taxOnGrossAmountFlag The taxOnGrossAmountFlag to set.
     */
    public void setTaxOnGrossAmountFlag(boolean taxOnGrossAmountFlag)
    {
        this.taxOnGrossAmountFlag = taxOnGrossAmountFlag;
    }
    /**
     * @return Returns the taxPercentage.
     */
    public BigDecimal getTaxPercentage()
    {
        return taxPercentage;
    }
    /**
     * @param taxPercentage The taxPercentage to set.
     */
    public void setTaxPercentage(BigDecimal taxPercentage)
    {
        this.taxPercentage = taxPercentage;
    }
    /**
     * @return Returns the taxRateEffectiveTimestamp.
     */
    public EYSDate getTaxRateEffectiveTimestamp()
    {
        return taxRateEffectiveTimestamp;
    }
    /**
     * @param taxRateEffectiveTimestamp The taxRateEffectiveTimestamp to set.
     */
    public void setTaxRateEffectiveTimestamp(EYSDate taxRateEffectiveTimestamp)
    {
        this.taxRateEffectiveTimestamp = taxRateEffectiveTimestamp;
    }
    /**
     * @return Returns the taxRateExpirationTimestamp.
     */
    public EYSDate getTaxRateExpirationTimestamp()
    {
        return taxRateExpirationTimestamp;
    }
    /**
     * @param taxRateExpirationTimestamp The taxRateExpirationTimestamp to set.
     */
    public void setTaxRateExpirationTimestamp(EYSDate taxRateExpirationTimestamp)
    {
        this.taxRateExpirationTimestamp = taxRateExpirationTimestamp;
    }
    /**
     * @return Returns the taxRateRuleUsageCode.
     */
    public int getTaxRateRuleUsageCode()
    {
        return taxRateRuleUsageCode;
    }
    /**
     * @param taxRateRuleUsageCode The taxRateRuleUsageCode to set.
     */
    public void setTaxRateRuleUsageCode(int taxRateRuleUsageCode)
    {
        this.taxRateRuleUsageCode = taxRateRuleUsageCode;
    }
    /**
     * @return Returns the taxRuleName.
     */
    public String getTaxRuleName()
    {
        return taxRuleName;
    }
    /**
     * @param taxRuleName The taxRuleName to set.
     */
    public void setTaxRuleName(String taxRuleName)
    {
        this.taxRuleName = taxRuleName;
    }
    /**
     * @return Returns the taxThresholdAmount.
     */
    public CurrencyIfc getTaxThresholdAmount()
    {
        return taxThresholdAmount;
    }
    /**
     * @param taxThresholdAmount The taxThresholdAmount to set.
     */
    public void setTaxThresholdAmount(CurrencyIfc taxThresholdAmount)
    {
        this.taxThresholdAmount = taxThresholdAmount;
    }
    /**
     * @return Returns the taxType.
     */
    public int getTaxType()
    {
        return taxType;
    }
    /**
     * @param taxType The taxType to set.
     */
    public void setTaxType(int taxType)
    {
        this.taxType = taxType;
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
    public void setTaxTypeCode(int taxTypeCode)
    {
        this.taxTypeCode = taxTypeCode;
    }
    
    /**
     * Set whether or not this rule is a tax holiday
     *  
     * @param value
     */
    public void setTaxHoliday(boolean value)
    {
        this.taxHoliday = value;
    }
    
    /**
     * Get whether or not this rule is a tax holiday
     *  
     * @return
     */
    public boolean getTaxHoliday()
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
     *  This method provides an additional message which will help locate 
     *  the offending tax rule.
     *  @param The error text associated with the specific error text.
     *  @return The completed error text
     */
    public String getValidationErrorMessage(String message)
    {
        String completeMessage = message + 
            "  Tax Rule Name: " + getTaxRuleName() +
            "; Tax Authority ID: " + taxAuthorityId +
            "; Tax Group ID: " + taxGroupId +
            "\n\t  The tax for items using this rule will be calculated at default rate.";
        
        return completeMessage;
    }

    /**
     * Clone this object
     *  
     * @return
     * @see java.lang.Object#clone()
     */
    public Object clone()
    {
        FFTaxVO ffTaxVO = new FFTaxVO();
        ffTaxVO.setTaxAuthorityId(this.getTaxAuthorityId());
        ffTaxVO.setTaxAuthorityName(this.getTaxAuthorityName());
        ffTaxVO.setTaxGroupId(this.getTaxGroupId());
        ffTaxVO.setTaxType(this.getTaxType());
        ffTaxVO.setTaxRuleName(this.getTaxRuleName());
        ffTaxVO.setCompoundSequenceNumber(this.getCompoundSequenceNumber());
        ffTaxVO.setTaxOnGrossAmountFlag(this.isTaxOnGrossAmountFlag());
        ffTaxVO.setCalculationMethodCode(this.getCalculationMethodCode());
        ffTaxVO.setTaxRateRuleUsageCode(this.getTaxRateRuleUsageCode());
        ffTaxVO.setTaxTypeCode(this.getTaxTypeCode());
        ffTaxVO.setTaxHoliday(this.getTaxHoliday());
        if(this.getTaxPercentage() != null)
        {
            ffTaxVO.setTaxPercentage(new BigDecimal(this.getTaxPercentage().doubleValue()));
        }
        if(this.getTaxAmount() != null)
        {
            ffTaxVO.setTaxAmount((CurrencyIfc)this.getTaxAmount().clone());
        }
        ffTaxVO.setTaxAboveThresholdAmountFlag(this.isTaxAboveThresholdAmountFlag());
        if(this.getTaxThresholdAmount() != null)
        {
            ffTaxVO.setTaxThresholdAmount((CurrencyIfc)this.getTaxThresholdAmount().clone());
        }
        if(this.getMinimumTaxableAmount() != null)
        {
            ffTaxVO.setMinimumTaxableAmount((CurrencyIfc)this.getMinimumTaxableAmount().clone());
        }
        if(this.getMaximumTaxableAmount() != null)
        {
            ffTaxVO.setMaximumTaxableAmount((CurrencyIfc)this.getMaximumTaxableAmount().clone());
        }
        if(this.getTaxRateExpirationTimestamp() != null)
        {
            ffTaxVO.setTaxRateExpirationTimestamp((EYSDate)this.getTaxRateExpirationTimestamp().clone());
        }
        if(this.getTaxRateEffectiveTimestamp() != null)
        {
            ffTaxVO.setTaxRateEffectiveTimestamp((EYSDate)this.getTaxRateEffectiveTimestamp().clone());
        }
        ffTaxVO.setRoundingCode(this.getRoundingCode());
        ffTaxVO.setRoundingDigits(this.getRoundingDigits());
        ffTaxVO.setInclusiveTaxFlag(this.getInclusiveTaxFlag());
        
        return ffTaxVO;
    }
}
