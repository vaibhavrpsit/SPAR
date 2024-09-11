/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/TaxTotals.java /main/11 2012/12/14 09:46:21 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  12/11/12 - Fixing HP Fortify missing null check issues
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    7    360Commerce 1.6         6/4/2007 5:25:55 PM    Brett J. Larsen CR
 *         26478 - Financial Totals Enhancements for VAT
 *         code review rework - formatting changes only
 *         
 *    6    360Commerce 1.5         5/31/2007 6:00:20 PM   Sandy Gu        added
 *          test case 
 *    5    360Commerce 1.4         5/14/2007 6:08:34 PM   Sandy Gu
 *         update inclusive information in financial totals and history tables
 *    4    360Commerce 1.3         4/25/2007 10:00:53 AM  Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:30:20 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:50 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:45 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:30:53  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.5  2004/07/08 20:13:59  jdeleau
 *   @scr 6054 - For the tax history write opreration, TaxMode was being
 *   saved instead of tax type.  For the read operation,  a join condition on
 *   taxType was missing.
 *
 *   Revision 1.4  2004/07/02 19:11:27  jdeleau
 *   @scr 5982 Support Tax Holiday
 *
 *   Revision 1.3  2004/06/18 20:18:22  jdeleau
 *   @scr 2775 Add clone/equals methods to domain objects for tax
 *
 *   Revision 1.2  2004/06/17 15:35:32  jdeleau
 *   @scr 4616 Add PrintItemTax parameter, and make receipts print tax 
 *   on a line item basis as necessary.
 *
 *   Revision 1.1  2004/06/15 00:44:31  jdeleau
 *   @scr 2775 Support register reports and financial totals with the new
 *   tax engine.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.tax.TaxInformationIfc;
import oracle.retail.stores.foundation.utility.Util;
/**
 * This object contains all the information needed to see total
 * taxes populated per jurisdiction, on an individual tax rule basis.
 * 
 * $Revision: /main/11 $
 */
public class TaxTotals implements TaxTotalsIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 1496638220948015593L;

    private CurrencyIfc taxAmount;
    private String uniqueId;
    private int taxCount;
    private int taxType;
    private int taxGroupId;
    private int taxAuthorityId;
    private String taxRuleName;
    private String taxAuthorityName;
    private boolean taxHoliday;
    private boolean inclusiveTaxFlag = false;   // must initialize to false

    /**
     * Default constructor
     */
    public TaxTotals()
    {
        
    }
    
    /**
     * Constructor that creates data tax totals cares about
     * based on the information in the TaxInformationIfc.
     * TaxRuleName and taxAuthorityName are not set when a tax
     * is being saved to totals, they are retrieved from the DB.
     *  
     * @param taxInfo
     */
    public TaxTotals(TaxInformationIfc taxInfo)
    {
        setTaxAmount(taxInfo.getTaxAmount());
        setUniqueId(taxInfo.getUniqueID());
        setTaxCount(1);
        setTaxType(taxInfo.getTaxTypeCode());
        setTaxGroupId(taxInfo.getTaxGroupID());
        setTaxAuthorityId(taxInfo.getTaxAuthorityID());
        setTaxRuleName(taxInfo.getTaxRuleName());
        setTaxHoliday(taxInfo.getTaxHoliday());
        setInclusiveTaxFlag(taxInfo.getInclusiveTaxFlag());
        
    }
    /**
     * Get the total amount of tax charged for this rule 
     *  
     * @return taxAmount
     * @see oracle.retail.stores.domain.financial.TaxTotalsIfc#getTaxAmount()
     */
    public CurrencyIfc getTaxAmount()
    {
        return this.taxAmount;
    }

    /**
     * Set the total amount of tax charged for this rule 
     * @param value tax amount
     * @see oracle.retail.stores.domain.financial.TaxTotalsIfc#setTaxAmount(oracle.retail.stores.domain.currency.CurrencyIfc)
     */
    public void setTaxAmount(CurrencyIfc value)
    {
        this.taxAmount = value;
    }

    /**
     * Get the total count of taxes charged
     *  
     * @return taxCount
     * @see oracle.retail.stores.domain.financial.TaxTotalsIfc#getTaxCount()
     */
    public int getTaxCount()
    {
        return this.taxCount;
    }

    /**
     * Set the total count of taxes charged
     * @param value number of taxes charged
     * @see oracle.retail.stores.domain.financial.TaxTotalsIfc#setTaxCount(int)
     */
    public void setTaxCount(int value)
    {
        this.taxCount = value;
    }

    /**
     * Set the tax type 
     * @param value tax type
     * @see oracle.retail.stores.domain.financial.TaxTotalsIfc#setTaxType(int)
     */
    public void setTaxType(int value)
    {
        this.taxType = value;
    }

    /**
     * Get the tax type 
     * @return tax type
     * @see oracle.retail.stores.domain.financial.TaxTotalsIfc#getTaxType()
     */
    public int getTaxType()
    {
        return this.taxType;
    }

    /**
     * Set the name of the tax rule
     * @param value tax rule name
     * @see oracle.retail.stores.domain.financial.TaxTotalsIfc#setTaxRuleName(java.lang.String)
     */
    public void setTaxRuleName(String value)
    {
        this.taxRuleName = value;
    }

    /**
     * Get the tax rule name 
     * @return The tax rule name
     * @see oracle.retail.stores.domain.financial.TaxTotalsIfc#getTaxRuleName()
     */
    public String getTaxRuleName()
    {
        return this.taxRuleName;
    }

    /**
     * Set the tax authority id
     * @param value tax authority id
     * @see oracle.retail.stores.domain.financial.TaxTotalsIfc#setTaxAuthorityId(int)
     */
    public void setTaxAuthorityId(int value)
    {
        this.taxAuthorityId = value;
    }

    /**
     * Get the tax authority id
     * @return tax authority id
     * @see oracle.retail.stores.domain.financial.TaxTotalsIfc#getTaxAuthorityId()
     */
    public int getTaxAuthorityId()
    {
        return this.taxAuthorityId;
    }

    /**
     * Get the tax authority name 
     * @return tax authority name
     * @see oracle.retail.stores.domain.financial.TaxTotalsIfc#getTaxAuthorityName()
     */
    public String getTaxAuthorityName()
    {
        return this.taxAuthorityName;
    }

    /**
     * Set the tax authority name 
     * @param value Tax authority name
     * @see oracle.retail.stores.domain.financial.TaxTotalsIfc#setTaxAuthorityName(java.lang.String)
     */
    public void setTaxAuthorityName(String value)
    {
        this.taxAuthorityName = value;
    }

    /**
     * Set the tax group id 
     * @param value tax group id
     * @see oracle.retail.stores.domain.financial.TaxTotalsIfc#setTaxGroupId(int)
     */
    public void setTaxGroupId(int value)
    {
        this.taxGroupId = value;
    }

    /**
     * Get the tax group Id
     * @return taxGroupId
     * @see oracle.retail.stores.domain.financial.TaxTotalsIfc#getTaxGroupId()
     */
    public int getTaxGroupId()
    {
        return this.taxGroupId;
    }
    
    /**
     * Get the uniqueId for this tax rule
     * 
     * @return uniqueId
     * @see oracle.retail.stores.domain.financial.TaxTotalsIfc#getUniqueId()
     */
    public String getUniqueId()
    {
        if(this.uniqueId == null)
        {
            this.uniqueId = "";
        }
        return this.uniqueId;
    }
    
    /**
     * Set the uniqueId
     *  
     * @param uniqueId
     * @see oracle.retail.stores.domain.financial.TaxTotalsIfc#setUniqueId(java.lang.String)
     */
    public void setUniqueId(String uniqueId)
    {
        this.uniqueId = uniqueId;
    }
    
    /**
     * Get whether or not this totals object represents a rule that is a tax holiday
     *  
     * @return true or false
     * @see oracle.retail.stores.domain.financial.TaxTotalsIfc#isTaxHoliday()
     */
    public boolean isTaxHoliday()
    {
        return this.taxHoliday;
    }
    
    /**
     * Set whether or not this tax totals object represents a rule thats a tax holiday
     *  
     * @param value
     * @see oracle.retail.stores.domain.financial.TaxTotalsIfc#setTaxHoliday(boolean)
     */
    public void setTaxHoliday(boolean value)
    {
        this.taxHoliday = value;
    }

    /**
     * @return Returns the inclusiveTaxFlag.
     */
    public boolean getInclusiveTaxFlag()
    {
        return inclusiveTaxFlag;
    }

    /**
     * @param inclusiveTaxFlag The inclusiveTaxFlag to set.
     */
    public void setInclusiveTaxFlag(boolean inclusiveTaxFlag)
    {
        this.inclusiveTaxFlag = inclusiveTaxFlag;
    }

    /**
     * Add this object's currency and count information to existing totals
     *  
     * @param addTaxInformation
     * @see oracle.retail.stores.domain.financial.TaxTotalsIfc#add(oracle.retail.stores.domain.financial.TaxTotalsIfc)
     */
    public void add(TaxTotalsIfc addTaxInformation)
    {
        if(addTaxInformation.getUniqueId().equals(this.getUniqueId()))
        {
            this.setTaxCount(this.getTaxCount() + addTaxInformation.getTaxCount());
            this.setTaxAmount(this.getTaxAmount().add(addTaxInformation.getTaxAmount()));
        }
    }

    /**
     * subtract this items currency and count information from what already exists in this object
     *  
     * @param subtractTaxInformation
     * @see oracle.retail.stores.domain.financial.TaxTotalsIfc#subtract(oracle.retail.stores.domain.financial.TaxTotalsIfc)
     */
    public void subtract(TaxTotalsIfc subtractTaxInformation)
    {
        if(subtractTaxInformation.getUniqueId().equals(this.getUniqueId()))
        {
            this.setTaxCount(this.getTaxCount() - subtractTaxInformation.getTaxCount());
            this.setTaxAmount(this.getTaxAmount().subtract(subtractTaxInformation.getTaxAmount()));
        }
        
    }

    /**
     * Transform this object so that count and currency values are negative.
     *  
     * @return
     * @see oracle.retail.stores.domain.financial.TaxTotalsIfc#negate()
     */
    public TaxTotalsIfc negate()
    {
        this.setTaxCount(-Math.abs(this.getTaxCount()));
        this.setTaxAmount(this.getTaxAmount().negate());
        return this;
    }

    /**
     * Clone an identical copy of this object 
     * @return cloned object
     * @see java.lang.Object#clone()
     */
    public Object clone()
    {
        TaxTotals newClass = new TaxTotals();
        
        newClass.setTaxAmount(this.getTaxAmount());
        newClass.setUniqueId(this.getUniqueId());
        newClass.setTaxCount(this.getTaxCount());
        newClass.setTaxType(this.getTaxType());
        newClass.setTaxGroupId(this.getTaxGroupId());
        newClass.setTaxAuthorityId(this.getTaxAuthorityId());
        newClass.setTaxRuleName(this.getTaxRuleName());
        newClass.setTaxAuthorityName(this.getTaxAuthorityName());
        newClass.setTaxHoliday(this.isTaxHoliday());
        newClass.setInclusiveTaxFlag(this.getInclusiveTaxFlag());
        
        return newClass;
    }

    /**
     * Comparisons of taxTotal objects are based on taxAuthorityId.
     * This lets a list of these objects be sorted by tax Jurisdiction.
     *  
     * @param arg0
     * @return
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object arg0)
    {
        TaxTotalsIfc taxTotals = (TaxTotalsIfc) arg0;
        
        return this.getTaxAuthorityId() - taxTotals.getTaxAuthorityId();
    }
    
    /**
     * Tell if 2 objects are equal.  An object is equal
     * if all the attributes have identical values.
     *  
     * @param obj object to compare for equality
     * @return true or false
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
    	if(obj == this)
    	{
    		return true;
    	}
    	boolean isEqual=false;
    	if(obj instanceof TaxTotals)
    	{
    		TaxTotals totals = (TaxTotals) obj;

    		if (Util.isObjectEqual(totals.getTaxAmount(), this.getTaxAmount()) &&
    				Util.isObjectEqual(totals.getUniqueId(), this.getUniqueId()) &&
    				totals.getTaxCount() == this.getTaxCount() &&
    				totals.getTaxType() == this.getTaxType() &&
    				totals.getTaxGroupId() == this.getTaxGroupId() && 
    				totals.getTaxAuthorityId() == this.getTaxAuthorityId() &&
    				totals.isTaxHoliday() == this.isTaxHoliday() &&
    				Util.isObjectEqual(totals.getTaxRuleName(), this.getTaxRuleName()) &&
    				Util.isObjectEqual(totals.getTaxAuthorityName(), this.getTaxAuthorityName()) &&
    				totals.getInclusiveTaxFlag() == this.getInclusiveTaxFlag())
    		{
    			isEqual=true;
    		}
    	}

    	return isEqual;
    }
    /**
     * Return a string representation of this classes contents
     *  
     * @return This class as a string
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer strResult = new StringBuffer(super.toString()).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("taxAmount", this.getTaxAmount())).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("uniqueId", this.getUniqueId())).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("taxCount", this.getTaxCount())).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("taxType", this.getTaxType())).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("taxGroupID", this.getTaxGroupId())).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("taxAuthorityID", this.getTaxAuthorityId())).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("taxRuleName", this.getTaxRuleName())).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("taxAuthorityName", this.getTaxAuthorityName())).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("taxHoliday", this.isTaxHoliday())).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("inclusiveTaxFlag", this.getInclusiveTaxFlag())).append(Util.EOL);
        return strResult.toString();
    }
}
