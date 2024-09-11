/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/TransactionTax.java /main/23 2014/07/24 15:23:29 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     10/06/14 - forward port tax override not calculated correctly.
 *    sgu       07/23/14 - add tax authority name
 *    abhinavs  12/12/12 - Fixing HP Fortify missing null check issues.
 *    sgu       09/06/12 - set transaction tax override reason code to item
 *                         level
 *    sgu       09/06/12 - added useItemRulesForTaxOverride to clone, equal,
 *                         and toString
 *    sgu       09/05/12 - refactor transaction tax transformation
 *    jswan     06/29/12 - Rename NewTaxRuleIfc to TaxRulesIfc
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mdecama   11/07/08 - I18N - updated toString()
 *    mdecama   11/07/08 - I18N - Fixed Clone Method
 *    ranojha   11/04/08 - Code refreshed to tip
 *    ranojha   11/04/08 - Changes for Tax Exempt reason codes
 *    acadar    11/03/08 - transaction tax reason codes updates
 *    acadar    11/03/08 - localization of transaction tax reason codes
 *    akandru   10/31/08 - EJ Changes_I18n
 *
 * ===========================================================================
 * $Log:
 *    9    360Commerce 1.8         4/11/2008 1:46:26 PM   Charles D. Baker CR
 *         28256 - Corrected handling of recalculating tax when purchase
 *         orders deleted, removing transaction tax exempt status and
 *         transaction tax overrides. Code review by Jack Swan.
 *    8    360Commerce 1.7         4/3/2008 3:40:28 PM    Christian Greene
 *         Refactor ID_NOT_DEFINED constants into TaxConstantsIfc
 *    7    360Commerce 1.6         7/31/2007 11:43:13 AM  Alan N. Sinton  CR
 *         28001 - Fixed default tax for shipping charges.
 *    6    360Commerce 1.5         7/26/2007 7:59:53 AM   Alan N. Sinton  CR
 *         27192 Make item lookup depend on department tax group ID if item's
 *         tax group ID is invalid.
 *    5    360Commerce 1.4         4/25/2007 10:00:18 AM  Anda D. Cadar   I18N
 *         merge
 *    4    360Commerce 1.3         1/22/2006 11:41:58 AM  Ron W. Haight
 *         Removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:30:36 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:26 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:17 PM  Robert Pearse
 *
 *   Revision 1.7  2004/09/23 00:30:51  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.6  2004/08/23 16:15:46  cdb
 *   @scr 4204 Removed tab characters
 *
 *   Revision 1.5  2004/06/03 11:48:36  mkp1
 *   @scr 2775 Added all transaction tax overrides including tax exempt
 *
 *   Revision 1.4  2004/05/27 16:59:22  mkp1
 *   @scr 2775 Checking in first revision of new tax engine.
 *
 *   Revision 1.3  2004/02/12 17:14:42  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:28:51  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:34  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:41:16   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 17:06:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:11:40   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:30:30   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 16:05:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:39:54   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.transaction;

import java.math.BigDecimal;
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.manager.ifc.tax.TaxBucketIfc;
import oracle.retail.stores.domain.tax.OverrideTransactionTaxByAmountRuleIfc;
import oracle.retail.stores.domain.tax.OverrideTransactionTaxByRateRuleIfc;
import oracle.retail.stores.domain.tax.RunTimeTaxRuleIfc;
import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.tax.TaxExemptTaxRuleIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tax.TaxRuleIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.ReasonCodeList;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.factory.FoundationObjectFactoryIfc;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * Class for handling transaction tax data.
 *
 * @version $Revision: /main/23 $
 */
public class TransactionTax implements TransactionTaxIfc, TaxIfc, TaxBucketIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 1579305400816762934L;

    /**
     * revision number supplied by source-code control system
     */
    public static String revisionNumber = "$Revision: /main/23 $";
    /**
     * tax rate
     */
    protected double overrideRate = 0;
    /**
     * default tax rate
     */
    protected double defaultRate = 0;
    /**
     * default tax rules
     */
    protected TaxRuleIfc[] defaultTaxRules = null;
    /**
     * tax override amount
     */
    protected CurrencyIfc overrideAmount = null;
    /**
     * override/exempt reason code
     */
    protected int reasonCode = ReasonCodeList.DEFAULT_CODE_UNDEFINED;
    /**
     * tax-exempt certificate identifier
     */
    protected String taxExemptCertificateID;

    /**
     * masked tax-exempt certificate identifier
     */
    protected String maskedTaxExemptCertificateID;

    /**
     * transaction tax method
     */
    protected int taxMode = TaxIfc.TAX_MODE_STANDARD;

    /**
     * tax group id
     */
    protected int taxGroupId = TaxConstantsIfc.TAX_GROUP_ID_NOT_DEFINED;

    /**
     * tax group name
     */
    protected String taxGroupName = "";

    /**
     * Reason code
     */
    protected LocalizedCodeIfc reason = DomainGateway.getFactory().getLocalizedCode();

    protected EncipheredDataIfc taxExemptCertificate = null;

    /**
     * A flag indicating if line item tax rules should be used to calculate
     * the transaction tax override. This is only set to true if we are retrieving
     * a tendered transaction from store database or another external source such
     * as OMS if cross channel is enabled.
     */
    protected boolean useItemRulesForTaxOverride = false;

    /**
     * Constructs TransactionTax object.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>amount attribute instantiated
     * </UL>
     */
    public TransactionTax()
    {
        overrideAmount = DomainGateway.getBaseCurrencyInstance();
    }

    /**
     * Constructs TransactionTax object with default rate attribute.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>amount attribute instantiated
     * </UL>
     *
     * @param pRate tax rate
     */
    public TransactionTax(double pRate)
    {
        overrideAmount = DomainGateway.getBaseCurrencyInstance();
        defaultRate = pRate;
    }

    /**
     * Constructs TransactionTax object and sets attributes based on parameters.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>amount attribute instantiated
     * </UL>
     *
     * @param pRate default tax rate
     * @param pAmount tax amount
     * @param pCode reason code
     * @param pCert tax exempt certificate identifier
     * @param pMode tax mode
     * @deprecated as of 13.1. No replacement provided
     */
    public TransactionTax(double pRate, CurrencyIfc pAmount, int pCode, String pCert, int pMode)
    {
        // set attributes
        defaultRate = pRate;
        overrideAmount = pAmount;
        reasonCode = pCode;
        taxExemptCertificateID = pCert;
        taxMode = pMode;
    }

    /**
     * Constructs TransactionTax object and sets attributes based on parameters.
     *
     * @param taxGroupName tax group name
     * @param taxGroupId tax group identifier
     */
    public TransactionTax(String taxGroupName, int taxGroupId)
    {
        this.taxGroupName = taxGroupName;
        this.taxGroupId = taxGroupId;
    }

    /**
     * Clones this object.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>clone object created
     * </UL>
     *
     * @return t cloned object
     */
    public Object clone()
    {
        // create new object
        TransactionTaxIfc t = new TransactionTax();

        // set clone attributes
        setCloneAttributes(t);

        // pass back object
        return t;
    }

    /**
     * Sets attributes in clone.
     *
     * @param newClass new instance of class
     */
    protected void setCloneAttributes(TransactionTaxIfc newClass)
    {
        newClass.setDefaultRate(defaultRate);
        newClass.setDefaultTaxRules(defaultTaxRules);
        newClass.setOverrideAmount((CurrencyIfc)getOverrideAmount().clone());
        if (reason != null)
        {
            newClass.setReason((LocalizedCodeIfc)getReason().clone());
        }
        if (getTaxExemptCertificate().getEncryptedNumber() != null)
        {
            newClass.setTaxExemptCertificate((EncipheredDataIfc)getTaxExemptCertificate()
                    .clone());
        }
        newClass.setTaxMode(getTaxMode());
        // reset default rate
        newClass.setOverrideRate(overrideRate);
        newClass.setUseItemRulesForTaxOverride(useItemRulesForTaxOverride);
    }

    /**
     * Override transaction tax rate.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>transaction tax rate set
     * <LI>override reason code set
     * </UL>
     *
     * @param newRate new tax rate
     * @param reason reason code
     * @deprecated as of 13.1. Use {@link overrideTaxRate(double newRate,
     *             LocalizedCodeIfc reason)
     */
    public void overrideTaxRate(double newRate, int reason)
    {
        // set attributes accordingly
        overrideRate = newRate;
        overrideAmount.setZero();
        reasonCode = reason;
        taxMode = TaxIfc.TAX_MODE_OVERRIDE_RATE;
    }

    /**
     * Override transaction tax rate.
     *
     * @param newRate new tax rate
     * @param reason reason code
     */
    public void overrideTaxRate(double newRate, LocalizedCodeIfc reason)
    {
        // set attributes accordingly
        overrideRate = newRate;
        overrideAmount.setZero();
        this.reason = reason;
        taxMode = TaxIfc.TAX_MODE_OVERRIDE_RATE;
    }

    /**
     * Override transaction tax rate.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>transaction tax rate set
     * <LI>override reason code set
     * </UL>
     *
     * @param pAmount new amount
     * @param reason reason code
     * @deprecated as of 13.1. Use {@link overrideTaxAmount(CurrencyIfc pAmount,
     *             LocalizedCodeIfc reason)}
     */
    public void overrideTaxAmount(CurrencyIfc pAmount, int reason)
    {
        // set relevant attributes
        overrideRate = 0;
        overrideAmount.setStringValue(pAmount.getStringValue());
        reasonCode = reason;
        taxMode = TaxIfc.TAX_MODE_OVERRIDE_AMOUNT;
    }

    /**
     * Override transaction tax rate.
     *
     * @param pAmount new amount
     * @param reason reason code
     */
    public void overrideTaxAmount(CurrencyIfc pAmount, LocalizedCodeIfc reason)
    {
        // set relevant attributes
        overrideRate = 0;
        overrideAmount.setStringValue(pAmount.getStringValue());
        this.reason = reason;
        taxMode = TaxIfc.TAX_MODE_OVERRIDE_AMOUNT;
    }

    /**
     * Set tax exempt.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     *
     * @param cert tax exempt certificate identifier
     * @param reason reason code
     * @deprecated As of release 13.1 Use
     *             {@link TransactionTax#setTaxExempt(String, LocalizedCodeIfc)}
     */
    public void setTaxExempt(String cert, int reason)
    {
        // set attributes accordingly
        overrideRate = 0;
        overrideAmount.setZero();
        reasonCode = reason;
        taxExemptCertificateID = cert;
        taxMode = TaxIfc.TAX_MODE_EXEMPT;
    }

    /**
     * Set tax exempt.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     *
     * @param cert tax exempt certificate identifier
     * @param reasonCode The LocalizedCode object
     */
    public void setTaxExempt(String cert, LocalizedCodeIfc reasonCode)
    {
        // set attributes accordingly
        overrideRate = 0;
        overrideAmount.setZero();
        this.reason = reasonCode;
        taxExemptCertificateID = cert;
        taxMode = TaxIfc.TAX_MODE_EXEMPT;
    }

    /**
     * Resets values for standard, transaction-default tax.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     */
    public void resetStandardTax()
    {
        // toggle tax on reverts to transaction tax rules
        overrideRate = 0;
        overrideAmount.setZero();
        reasonCode = ReasonCodeList.DEFAULT_CODE_UNDEFINED;
        taxMode = TaxIfc.TAX_MODE_STANDARD;
    }

    /**
     * Resets values to standard, transaction-default tax if the current tax
     * mode is tax exempt.
     * <P>
     */
    public void clearTaxExempt()
    {
        if (taxMode == TaxIfc.TAX_MODE_EXEMPT)
        {
            resetStandardTax();
        }
    }

    /**
     * Resets values to standard, transaction-default tax if the current tax
     * mode is tax override.
     * <P>
     */
    public void clearOverrideTax()
    {
        if (taxMode == TaxIfc.TAX_MODE_OVERRIDE_RATE || taxMode == TaxIfc.TAX_MODE_OVERRIDE_AMOUNT)
        {
            resetStandardTax();
        }
    }

    /**
     * Retrieves override rate.
     *
     * @return override tax rate
     */
    public double getOverrideRate()
    {
        return (overrideRate);
    }

    /**
     * Sets override rate.
     *
     * @param value new override rate setting
     */
    public void setOverrideRate(double value)
    {
        overrideRate = value;
    }

    /**
     * Retrieves default rate.
     *
     * @return default tax rate
     */
    public double getDefaultRate()
    {
        return (defaultRate);
    }

    /**
     * Sets defau;t rate.
     *
     * @param value new default rate setting
     */
    public void setDefaultRate(double value)
    {
        defaultRate = value;
    }

    /**
     * Sets override amount.
     *
     * @param value new override amount setting
     */
    public void setOverrideAmount(CurrencyIfc value)
    {
        overrideAmount = value;
    }

    /**
     * Retrieves override amount.
     *
     * @return amount tax amount
     */
    public CurrencyIfc getOverrideAmount()
    {
        return (overrideAmount);
    }

    /**
     * Sets reason code.
     *
     * @param value new reason code setting
     * @deprecated as of 13.1 Use {@link #setReason(LocalizedCodeIfc)}
     */
    public void setReasonCode(int value)
    {
        reason.setCode(Integer.toString(value));
    }

    /**
     * Retrieves reason code.
     *
     * @return reasonCode reason code
     * @deprecated as of 13.1 Use {@link #getReason()}
     */
    public int getReasonCode()
    {
        return (Integer.parseInt(reason.getCode()));
    }

    /*
     * (non-Javadoc)
     * @see
     * oracle.retail.stores.domain.transaction.TransactionTaxIfc#getReason()
     */
    public LocalizedCodeIfc getReason()
    {
        return reason;
    }

    /*
     * (non-Javadoc)
     * @see
     * oracle.retail.stores.domain.transaction.TransactionTaxIfc#setReason(oracle
     * .retail.stores.common.utility.LocalizedCodeIfc)
     */
    public void setReason(LocalizedCodeIfc reason)
    {
        this.reason = reason;

    }

    /**
     * Sets tax exempt certificate identifier.
     *
     * @param value new tax exempt certificate identifier
     */
    public void setTaxExemptCertificateID(String value)
    {
        taxExemptCertificateID = value;
    }

    /**
     * Retrieves tax exempt certificate identifier.
     *
     * @return taxExemptCertificateID tax exempt certificate identifier
     */
    public String getTaxExemptCertificateID()
    {
        return (taxExemptCertificateID);
    }

    /**
     * Retrieves tax exempt certificate enciphered object.
     *
     * @return EncipheredDataIfc
     */
    @Override
    public EncipheredDataIfc getTaxExemptCertificate()
    {
        if (taxExemptCertificate == null)
        {
            FoundationObjectFactoryIfc factory = FoundationObjectFactory.getFactory();
            taxExemptCertificate = factory.createEncipheredDataInstance();
        }
        return taxExemptCertificate;

    }

    /**
     * Sets tax exempt certificate enciphered object.
     *
     * @param EncipheredDataIfc
     */
    @Override
    public void setTaxExemptCertificate(EncipheredDataIfc taxExemptCertificate)
    {
        this.taxExemptCertificate = taxExemptCertificate;
    }

    /**
     * Sets tax mode.
     *
     * @param value new tax mode setting
     */
    public void setTaxMode(int value)
    {
        taxMode = value;
    }

    /**
     * Retrieves tax mode.
     *
     * @return taxMode tax mode
     */
    public int getTaxMode()
    {
        return (taxMode);
    }

    /**
     * Set the tax group identifier
     *
     * @param taxGroupId - unique identifier for the tax group
     */
    public void setTaxGroupId(int taxGroupId)
    {
        this.taxGroupId = taxGroupId;
    }

    /**
     * Set the tax group name
     *
     * @param taxGroupName - unique name for the tax group
     */
    public void setTaxGroupName(String taxGroupName)
    {
        this.taxGroupName = taxGroupName;
    }

    /**
     * Returns true if the tax manager reference for this object is not null.
     *
     * @return boolean value
     * @deprecated as of 13.4. Does not have any meaning since TaxManagerIfc is deleted.
     */
    public boolean getExternalTaxEnabled()
    {
        return false;
    }

    // =====================================================================
    // TaxBucketIfc Methods
    // =====================================================================
    /**
     * Numeric id of the tax group.
     *
     * @return int id of the associated tax group
     */
    public int getTaxGroupId()
    {
        return this.taxGroupId;
    }

    /**
     * String name of the tax group.
     *
     * @return name of the associated tax group
     */
    public String getTaxGroupName()
    {
        return this.taxGroupName;
    }

    /**
     * Return the quantity of items associated with the tax bucket.
     *
     * @return quantity of items
     */
    public long getTaxableQuantity()
    {
        return 0;
    }

    /**
     * Return the net extended taxable amount.
     *
     * @return net extended mount
     */
    public double getTaxableAmount()
    {
        return 0.0;
    }

    /**
     * Returns the amount of total tax applied to the bucket. Total tax is
     * comprised of standard amount tax, standard quantity tax, override amount
     * tax, and override quantity tax applied by the tax calculations.
     *
     * @return amount of total tax applied to this bucket.
     */
    public double getTotalTax()
    {
        return 0.0;
    }

    /**
     * Set the amount of tax applied using standard tax calculations on the
     * amount of the item(s) associated with the bucket.
     *
     * @param amountTax - standard tax applied to the bucket
     */
    public void setStandardAmountTax(double amountTax)
    {
        System.out.println("TransactionTax.setStandardAmountTax()");
    }

    /**
     * Get the amount of tax applied using standard tax calculations on the
     * amount of the item(s) associated with the bucket.
     *
     * @return standard tax applied to the bucket based on item(s) amount
     */
    public double getStandardAmountTax()
    {
        return 0.0;
    }

    /**
     * Increase/Decrease the amount of tax applied using standard tax
     * calculations on the amount of the item(s) associated with the bucket.
     *
     * @param amountTax - standard tax applied to the bucket
     */
    public void modifyStandardAmountTax(double amountTax)
    {
        System.out.println("TransactionTax.modifyStandardAmountTax()");
    }

    /**
     * Set the amount of tax applied using standard tax calculations on the
     * quantity of the item(s) associated with the bucket.
     *
     * @param quantityTax - standard tax applied to the bucket
     */
    public void setStandardQuantityTax(double quantityTax)
    {
        System.out.println("TransactionTax.setStandardQuantityTax()");
    }

    /**
     * Get the amount of tax applied using standard tax calculations on the
     * quantity of the item(s) associated with the bucket.
     *
     * @return standard tax applied to the bucket based on quantity of item(s)
     */
    public double getStandardQuantityTax()
    {
        return 0.0;
    }

    /**
     * Increase/Decrease the amount of tax applied using standard tax
     * calculations on the quantity of the item(s) associated with the bucket.
     *
     * @param taxAmount - standard tax applied to the bucket
     */
    public void modifyStandardQuantityTax(double taxAmount)
    {
        System.out.println("TransactionTax.modifyStandardQuantityTax()");
    }

    /**
     * Sets the amount of override tax on the extended amount of the items.
     *
     * @param overrideAmount - amount of tax
     */
    public void setOverrideAmountTax(double overrideAmount)
    {
        System.out.println("TransactionTax.setOverrideAmountTax()");
    }

    /**
     * Returns the amount of override Tax calculated on the net extended amount
     * of the items.
     *
     * @return amount of tax
     */
    public double getOverrideAmountTax()
    {
        return 0.0;
    }

    /**
     * Increase/Decrease the amount of tax applied override standard tax
     * calculations on the amount of the item(s) associated with the bucket.
     *
     * @param overrideAmount - override tax applied to the bucket
     */
    public void modifyOverrideAmountTax(double overrideAmount)
    {
        System.out.println("TransactionTax.modifyOverrideAmountTax()");
    }

    /**
     * Sets the amount of override tax on the quantity of items.
     *
     * @param overrideQuantity - amount of tax
     */
    public void setOverrideQuantityTax(double overrideQuantity)
    {
        System.out.println("TransactionTax.setOverrideQuantityTax()");
    }

    /**
     * Returns the amount of override Tax calculated on the quantity of items.
     *
     * @return amount of tax
     */
    public double getOverrideQuantityTax()
    {
        System.out.println("TransactionTax.getOverrideQuantityTax()");
        return 0.0;
    }

    /**
     * Increase/Decrease the amount of tax applied using override tax
     * calculations on the quantityt of the item(s) associated with the bucket.
     *
     * @param quantityTax - override tax applied to the bucket
     */
    public void modifyOverrideQuantityTax(double quantityTax)
    {
        System.out.println("TransactionTax.modifyOverrideQuantityTax()");
    }

    /**
     * Called if an override flag is set. The bucket needs to determine the
     * appropriate override taxes.
     */
    public void calculateOverrideTax()
    {
        System.out.println("TransactionTax.calculateOverrideTax()");
    }

    /**
     * Clear the tax amounts within the bucket in preparation for new tax
     * calculations
     */
    public void clearTaxAmounts()
    {
        System.out.println("TransactionTax.clearTaxAmounts()");
    }

    // =====================================================================

    // =====================================================================

    /**
     * Returns string data for journal entry.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>journal string created
     * </UL>
     *
     * @return String representation of object for journal
     * @deprecated as of 13.1 use {@link #toJournalString(Locale)} instead
     */
    public String toJournalString()
    {
        return (toJournalString(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)));
    }

    /**
     * Returns string data for journal entry.
     * <P>
     * journal string created
     *
     * @param journalLocale locale received from the client
     * @return String representation of object for journal
     */
    public String toJournalString(Locale journalLocale)
    {
        // build string
        StringBuffer strResult = new StringBuffer();
        Object[] dataArgs = new Object[] { defaultRate };
        strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.TRANSACTION_TAX_DEFAULT_RATE_LABEL, dataArgs, journalLocale));
        dataArgs[0] = taxMode;
        strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.TRANSACTION_TAX_DEFAULT_RATE_LABEL, dataArgs, journalLocale));
        // tailor output to mode
        switch (taxMode)
        {
        case TaxIfc.TAX_MODE_OVERRIDE_RATE:
            dataArgs[0] = overrideRate;
            strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.REASON_CODE_LABEL, dataArgs, journalLocale));

            dataArgs[0] = reasonCode;
            strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.REASON_CODE_LABEL, dataArgs, journalLocale));
            break;
        case TaxIfc.TAX_MODE_OVERRIDE_AMOUNT:
            dataArgs[0] = overrideAmount.toString();
            strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.OVERRIDE_AMOUNT_LABEL, dataArgs, journalLocale));
            strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.REASON_CODE_LABEL, dataArgs, journalLocale));
            break;
        case TaxIfc.TAX_MODE_EXEMPT:
            dataArgs[0] = taxExemptCertificateID;
            strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.TAX_EXEMPTION_CERTIFICATE_LABEL, dataArgs, journalLocale));
            strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.REASON_CODE_LABEL, dataArgs, journalLocale));
            break;
        default:
            // do nothing
            break;
        }
        return (strResult.toString());
    }

    /**
     * Determine if two objects are identical.
     *
     * @param obj object to compare with
     * @return true if the objects are identical, false otherwise
     */
    public boolean equals(Object obj)
    {
    	if(obj == this)
    	{
    		return true;
    	}

    	boolean isEqual = false;
    	if(obj instanceof TransactionTax)
    	{
    		TransactionTax c = (TransactionTax)obj; // downcast the input object

    		// compare all the attributes of TransactionTax
    		if (overrideRate == c.getOverrideRate() && defaultRate == c.getDefaultRate()
    				&& Util.isArrayEqual(defaultTaxRules, c.getDefaultTaxRules())
    				&& Util.isObjectEqual(overrideAmount, c.getOverrideAmount())
    				&& reasonCode == c.getReasonCode()
    				&& taxMode == c.getTaxMode() && taxGroupId == c.getTaxGroupId()
    				&& Util.isObjectEqual(taxGroupName, c.getTaxGroupName())
    				&& useItemRulesForTaxOverride == c.useItemRulesForTaxOverride)
    		{
    			isEqual = true; // set the return code to true
    		}
    	}
    	return (isEqual);
    }

    /**
     * Method to default display string function.
     *
     * @return String representation of object
     */
    public String toString()
    {
        // build result string
        String strResult = new String("Class:  TransactionTax (Revision " + getRevisionNumber() + ") @" + hashCode());
        strResult += "\n";

        // add attributes to string
        strResult += "overrideRate:                       [" + overrideRate + "]\n";
        strResult += "defaultRate:                        [" + defaultRate + "]\n";
        strResult += "defaultTaxRules:                    [" + defaultTaxRules + "]\n";
        if (overrideAmount == null)
        {
            strResult += "overrideAmount:                     [null]\n";
        }
        else
        {
            strResult += "overrideAmount:                     [" + overrideAmount + "]\n";
        }
        strResult += "reason:                             [" + reason + "]\n";
        strResult += "taxExemptCertificateID:             [" + taxExemptCertificateID + "]\n";
        strResult += "taxMode:                            [" + taxMode + "]\n";
        strResult += "taxGroupId:                         [" + taxGroupId + "]\n";
        strResult += "taxGroupName:                       [" + taxGroupName + "]\n";
        strResult += "externalTaxEnabled:                 [" + getExternalTaxEnabled() + "]\n";
        strResult += "useItemRulesForTaxOverride:         [" + useItemRulesForTaxOverride + "]\n";
        // pass back result
        return (strResult);
    }

    /**
     * Retrieves the Team Connection revision number.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

    /**
     * Retrieve the active tax rules on the transaction
     *
     * @return array of active tax rules should never be null but can return
     *         array with 0 elements
     */

    public RunTimeTaxRuleIfc[] getActiveTaxRules()
    {
        RunTimeTaxRuleIfc[] taxRules = new RunTimeTaxRuleIfc[0];

        switch (taxMode)
        {
        case TAX_MODE_OVERRIDE_AMOUNT:
            if (!useItemRulesForTaxOverride())
            {
                OverrideTransactionTaxByAmountRuleIfc overrideTransactionTaxByAmountRule = DomainGateway.getFactory()
                .getOverrideTransactionTaxByAmountRuleInstance();
                overrideTransactionTaxByAmountRule.setFixedTaxAmount(overrideAmount);
                overrideTransactionTaxByAmountRule.setUniqueID(TAX_SCOPE_DESCRIPTOR[0] + " " + TAX_OVERRIDE_BY_AMOUNT);
                overrideTransactionTaxByAmountRule.setTaxRuleName(overrideTransactionTaxByAmountRule.getUniqueID());
                overrideTransactionTaxByAmountRule.setTaxAuthorityName(overrideTransactionTaxByAmountRule.getUniqueID());
                overrideTransactionTaxByAmountRule.setReasonCode(getReason());

                taxRules = new RunTimeTaxRuleIfc[1];
                taxRules[0] = overrideTransactionTaxByAmountRule;
            }
            break;
        case TAX_MODE_OVERRIDE_RATE:
            if (!useItemRulesForTaxOverride())
            {
                OverrideTransactionTaxByRateRuleIfc overrideTransactionTaxByRateRule = DomainGateway.getFactory()
                .getOverrideTransactionTaxByRateRuleInstance();
                overrideTransactionTaxByRateRule.setTaxRate(BigDecimal.valueOf(overrideRate));
                overrideTransactionTaxByRateRule.setUniqueID(TAX_SCOPE_DESCRIPTOR[1] + " " + TAX_OVERRIDE_BY_PERCENT);
                overrideTransactionTaxByRateRule.setTaxRuleName(overrideTransactionTaxByRateRule.getUniqueID());
                overrideTransactionTaxByRateRule.setTaxAuthorityName(overrideTransactionTaxByRateRule.getUniqueID());
                overrideTransactionTaxByRateRule.setReasonCode(getReason());

                taxRules = new RunTimeTaxRuleIfc[1];
                taxRules[0] = overrideTransactionTaxByRateRule;
            }
            break;
        case TaxIfc.TAX_MODE_EXEMPT:
            TaxExemptTaxRuleIfc taxExemptTaxRule = DomainGateway.getFactory().getTaxExemptTaxRuleInstance();
            taxExemptTaxRule.setUniqueID(TAX_EXEMPT_TAX_TOGGLE_OFF);
            taxExemptTaxRule.setTaxRuleName(TAX_EXEMPT_TAX_TOGGLE_OFF);
            taxExemptTaxRule.setTaxAuthorityName(TAX_EXEMPT_TAX_TOGGLE_OFF);

            taxRules = new RunTimeTaxRuleIfc[1];
            taxRules[0] = taxExemptTaxRule;

            break;
        }

        return taxRules;
    }

    /**
     * Gets the default tax rules.
     *
     * @return The default tax rules.
     */
    public TaxRuleIfc[] getDefaultTaxRules()
    {
        return defaultTaxRules;
    }

    /**
     * Sets the default tax rules.
     *
     * @param taxRules
     */
    public void setDefaultTaxRules(TaxRuleIfc[] taxRules)
    {
        this.defaultTaxRules = taxRules;
    }

    /**
     * Sets masked tax exempt certificate identifier.
     *
     * @param value new masked tax exempt certificate identifier
     */
    public void setMaskedTaxExemptCertificateID(String value)
    {
        maskedTaxExemptCertificateID = value;
    }

    /**
     * Retrieves masked tax exempt certificate identifier.
     *
     * @return Masked Tax ExemptCertificateID
     */
    public String getMaskedTaxExemptCertificateID()
    {
        EncipheredDataIfc taxCertificate = FoundationObjectFactory.getFactory()
                .createEncipheredDataInstance(getTaxExemptCertificateID());
        return (taxCertificate.getMaskedNumber());
    }

    /**
     * Return the flag indicating if line item tax rules should be used to
     * calculate tax override.
     *
     * @return the flag
     */
    public boolean useItemRulesForTaxOverride()
    {
        return useItemRulesForTaxOverride;
    }

    /**
     * Set the flag indicating if line item tax rules should be used to calculate
     * tax override
     *
     * @param useLineItemTaxRules the flag to set
     */
    public void setUseItemRulesForTaxOverride(boolean useItemRulesForTaxOverride)
    {
        this.useItemRulesForTaxOverride = useItemRulesForTaxOverride;
    }
}
