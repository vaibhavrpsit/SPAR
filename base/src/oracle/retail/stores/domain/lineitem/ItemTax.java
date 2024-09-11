/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/lineitem/ItemTax.java /main/28 2014/07/24 15:23:29 sgu Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *   icole   10/06/14 - forward port tax override not calculated correctly.
 *   sgu     07/23/14 - add tax authority name
 *   abondal 09/04/13 - initialize collections
 *   rabhaws 11/09/12 - tax should be zero if pincode having no tax rules. send
 *                      functionality.
 *   cgreene 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *   sgu     09/22/11 - add function to set sign for tax amounts
 *   sgu     09/22/11 - negate return tax in post void case
 *   jkoppol 10/13/10 - Modified toJournalString() method to use 'reason
 *                      (LocalizedCode)' instead of the obsolete 'reasonCode'
 *                      and 'reasonCodeText'
 *   acadar  06/10/10 - use default locale for currency display
 *   acadar  06/09/10 - XbranchMerge acadar_tech30 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *   cgreene 05/26/10 - convert to oracle packaging
 *   acadar  04/05/10 - use default locale for currency and date/time display
 *   nganesh 02/02/10 - Tax Override Amount information added to Ejournal
 *   abondal 01/03/10 - update header date
 *   cgreene 03/06/09 - added method isTaxOff and getCalculated tax to help
 *                      print on receipts
 *   deghosh 02/12/09 - Cleaning the deprecated method toJournalString()
 *   mdecama 11/07/08 - I18N - updated toString()
 *   mdecama 11/07/08 - I18N - Fixed Clone Method
 *   acadar  11/03/08 - transaction tax reason codes updates
 *   akandru 10/31/08 - EJ Changes_I18n
 *   akandru 10/30/08 - EJ changes
 *   acadar  10/28/08 - localization for item tax reason codes
 *   acadar  10/28/08 - changes for localized item tax reason codes
 *   mdecama 10/23/08 - ReasonCode - Added new methods to the interfaces and
 *                      method stubs to the respective classes.
 *   cgreene 09/19/08 - updated with changes per FindBugs findings
 *   cgreene 09/11/08 - update header
 *
 * ===========================================================================
     $Log:
      23   360Commerce 1.22        4/18/2008 4:39:26 PM   Sandy Gu        back
           out v12x port to write VAT discount amount in EJ.
      22   360Commerce 1.21        4/8/2008 6:04:47 PM    Sharma Yanamandra
           made NON_TAXABILITY take higher precedence over SEND
      21   360Commerce 1.20        4/3/2008 3:37:45 PM    Christian Greene
           Refactor ID_NOT_DEFINED constants into TaxConstantsIfc
      20   360Commerce 1.19        3/25/2008 1:36:39 PM   Mathews Kochummen
           forward port changes from v12x to trunk
      19   360Commerce 1.18        8/16/2007 2:47:59 PM   Charles D. Baker CR
           27803 - Removed remaining handling of deprecated property.
      18   360Commerce 1.17        8/13/2007 6:22:34 PM   Charles D. Baker CR
           27803 - Removed unused code formally implied by deprecated domain
           property.
      17   360Commerce 1.16        8/7/2007 2:35:51 PM    Alan N. Sinton  CR
           27384: Merge code fix back in for EJournal formatting.
      16   360Commerce 1.15        7/31/2007 11:43:13 AM  Alan N. Sinton  CR
           28001 - Fixed default tax for shipping charges.
      15   360Commerce 1.14        7/26/2007 7:59:53 AM   Alan N. Sinton  CR
           27192 Make item lookup depend on department tax group ID if item's
           tax group ID is invalid.
      14   360Commerce 1.13        7/17/2007 10:03:06 AM  Ashok.Mondal    CR
           27384 :Correcting Tax Override Amount format on eJournal.
      13   360Commerce 1.12        6/12/2007 8:48:32 PM   Anda D. Cadar   SCR
           27207: Receipt changes -  proper alignment for amounts
      12   360Commerce 1.11        6/6/2007 12:54:40 AM   Sandy Gu
           Incrrease the scale of default tax to 4
      11   360Commerce 1.10        5/31/2007 5:57:46 PM   Sandy Gu        added
            test cases and fixed problems resulting from that
      10   360Commerce 1.9         5/21/2007 9:17:02 AM   Anda D. Cadar   Ej
           changes and cleanup
      9    360Commerce 1.8         5/17/2007 2:50:20 PM   Owen D. Horne
           CR#23450 - Merged fix from v8.0.1
           6    .v8x       1.3.1.1     5/10/2007 11:36:41 AM  Sujay
           Purkayastha Fix
           for CR 23,450
           5    .v8x       1.3.1.0     1/12/2007 4:20:35 PM   Brett J. Larsen
           CR
           23450 - reason code for tax on/off not included in ejournal
      8    360Commerce 1.7         4/30/2007 5:38:35 PM   Sandy Gu        added
            api to handle inclusive tax
      7    360Commerce 1.6         4/25/2007 10:00:41 AM  Anda D. Cadar   I18N
           merge
      6    360Commerce 1.5         4/2/2007 5:57:23 PM    Snowber Khan    Merge
            from v8x 1.3.1.0 previous merge was incomplete - CR 23450 - reason
            code for tax on/off not included in ejournal, Merge from
           ItemTax.java, Revision 1.3.1.0

      5    360Commerce 1.4         1/12/2007 4:46:07 PM   Brett J. Larsen Merge
            from ItemTax.java, Revision 1.3.1.0
      4    360Commerce 1.3         1/22/2006 11:41:40 AM  Ron W. Haight
           Removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:28:34 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:22:32 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:11:42 PM  Robert Pearse
     $
     Revision 1.17.2.1  2005/01/21 21:10:05  csuehs
     @scr 7813  Added setting the taxToggle flag when going between non taxable
     to taxable.  This flag is used later on to determine if the tax needs to be reset and recalculated later.  Duplicates mwisbauer's change on the trunk 1.19

     Revision 1.17  2004/09/23 00:30:54  kmcbride
     @scr 7211: Inserting serialVersionUIDs in these Serializable classes

     Revision 1.16  2004/08/23 16:15:45  cdb
     @scr 4204 Removed tab characters

     Revision 1.15  2004/08/18 20:20:29  jdeleau
     @scr 6510 Make sure sign is negative for tax on a post void.

     Revision 1.14  2004/07/31 16:32:13  jdeleau
     @scr 6632 Make sure send tax always overrides other tax ruels

     Revision 1.13  2004/07/27 00:07:46  jdeleau
     @scr 6251 Make sure when tax is toggled off, that the original tax is charged when its toggled back on

     Revision 1.12  2004/07/22 20:28:27  jdeleau
     @scr 2775 Fix the way certain tax things were done, to comply
     with arch guidelines.

     Revision 1.11  2004/07/22 18:37:33  jdeleau
     @scr 6410 GiftCerts (nontaxable) were being taxed on a transaction tax
     override because the line items tax mode was set to override instead
     of non-taxable.

     Revision 1.10  2004/07/21 15:02:05  jdeleau
     @scr 5946 Make sure the default tax rule has the name "LocTx"

     Revision 1.9  2004/06/25 21:00:18  jdeleau
     @scr 5849 Tax for send item was not propogating through the various
     cargos correctly.  Now it is.

     Revision 1.8  2004/06/21 22:29:16  jdeleau
     @scr 3767 Make sure the default tax rate is used if no rules can be found.

     Revision 1.7  2004/06/07 18:19:31  jdeleau
     @scr 2775 Add tax Service, Multiple Geo Codes screens

     Revision 1.6  2004/06/03 16:22:41  jdeleau
     @scr 2775 Initial Drop of send item tax support.

     Revision 1.5  2004/06/02 13:33:47  mkp1
     @scr 2775 Implemented item tax overrides using new tax engine

     Revision 1.4  2004/05/27 16:59:23  mkp1
     @scr 2775 Checking in first revision of new tax engine.

     Revision 1.3  2004/02/12 17:13:57  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:26:31  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:32  cschellenger
     updating to pvcs 360store-current
 *
 *    Rev 1.0   Aug 29 2003 15:37:58   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Jun 16 2003 13:54:22   sfl
 * Simplified the cloneAttributes
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.3   Jun 12 2003 13:19:08   sfl
 * Added more tax data in the taxByTaxJurisdiction hashtable.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.2   Apr 24 2003 17:14:32   sfl
 * Added new data structure to hold tax amount by different tax jurisdictions.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.1   Jan 16 2003 10:41:18   sfl
 * Make sure the item tax override amount is displayed with
 * two digits after decimal point.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.0   Jun 03 2002 16:58:16   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:04:12   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:24:04   msg
 * Initial revision.
 *
 *    Rev 1.3   Mar 11 2002 20:38:38   dfh
 * updates to keep item tax mode set to exempt when transaction was set to exempt
 * Resolution for POS SCR-1529: Able to modify item level tax on an Tax Exempt transaction
 *
 *    Rev 1.2   Feb 23 2002 10:31:24   mpm
 * Modified Util.BIG_DECIMAL to Util.I_BIG_DECIMAL, Util.ROUND_HALF to Util.I_ROUND_HALF.
 * Resolution for Domain SCR-35: Accept Foundation BigDecimal backward-compatibility changes
 *
 *    Rev 1.1   Feb 05 2002 16:35:54   mpm
 * Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 *
 *    Rev 1.0   Sep 20 2001 16:16:16   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:38:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.lineitem;

import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tax.TaxRuleIfc;
import oracle.retail.stores.domain.tax.OverrideItemTaxByAmountRuleIfc;
import oracle.retail.stores.domain.tax.OverrideItemTaxByRateRuleIfc;
import oracle.retail.stores.domain.tax.OverrideItemTaxRuleIfc;
import oracle.retail.stores.domain.tax.RunTimeTaxRuleIfc;
import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tax.TaxInformationContainerIfc;
import oracle.retail.stores.domain.tax.TaxRateCalculatorIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * Class for handling item tax data.
 */
public class ItemTax implements ItemTaxIfc, EYSDomainIfc, TaxIfc
{
    // This id is used to tell the compiler not to generate a new
    // serialVersionUID.
    static final long serialVersionUID = -3049485652757787021L;

    /**
     * taxable amount
     */
    protected CurrencyIfc itemTaxableAmount = null;

    /**
     * tax rate
     */
    protected double overrideRate = 0;

    /**
     * default tax rate
     */
    protected double defaultRate = 0;

    /**
     * default rule name
     */
    protected String defaultRuleName = "";

    /**
     * line item tax amount
     */
    protected CurrencyIfc itemTaxAmount;

    /**
     * line item inclusive tax amount
     */
    protected CurrencyIfc itemInclusiveTaxAmount;

    /**
     * tax override Amount
     */
    protected CurrencyIfc overrideAmount = null;

    /**
     * override/exempt reason code
     */
    protected int reasonCode = 0;

    /**
     * override/exempt reason code string
     */
    protected String reasonCodeText = null;

    /**
     * taxToggle (true = tax on)
     */
    protected boolean taxToggle = true;

    /**
     * item tax mode
     */
    protected int taxMode = TaxIfc.TAX_MODE_STANDARD;

    /**
     * If any item's tax mode is changed from the default, then we must store
     * its original tax mode so it can be restored
     */
    protected int originalTaxMode = -1;

    /**
     * item tax scope
     */
    protected int taxScope = TaxIfc.TAX_SCOPE_TRANSACTION;

    /**
     * item tax status
     */
    protected boolean taxable = true;

    /**
     * toggled item tax mode
     * 
     * @deprecated
     */
    protected int toggledTaxMode = TaxIfc.TAX_MODE_STANDARD;

    /**
     * toggled item tax scope
     * 
     * @deprecated
     */
    protected int toggledTaxScope = TaxIfc.TAX_SCOPE_TRANSACTION;

    /**
     * toggled tax rate
     * 
     * @deprecated
     */
    protected double toggledOverrideRate = 0;

    /**
     * toggled default tax rate
     * 
     * @deprecated
     */
    protected double toggledDefaultRate = 0;

    /**
     * toggled tax override Amount
     * 
     * @deprecated
     */
    protected CurrencyIfc toggledOverrideAmount = null;

    /**
     * toggled override/exempt reason code
     * 
     * @deprecated
     */
    protected int toggledReasonCode = 0;

    /**
     * external tax calculation flag
     */
    protected boolean externalTaxEnabled = false;

    /**
     * Tax group identitier
     */
    protected int taxGroupId = TaxConstantsIfc.TAX_GROUP_ID_NOT_DEFINED;

    /**
     * Tax group name
     */
    protected String taxGroupName;

    /**
     * standard amount tax
     */
    protected CurrencyIfc standardAmountTax = null;

    /**
     * standard quantity tax
     */
    protected CurrencyIfc standardQuantityTax = null;

    /**
     * override amount tax
     */
    protected CurrencyIfc overrideAmountTax = null;

    /**
     * override quantity tax
     */
    protected CurrencyIfc overrideQuantityTax = null;

    /**
     * ItemPriceIfc that references this object
     */
    protected ItemPriceIfc itemPrice = null;

    /**
     * hashtable with key combined with tax authority id and tax group id, the
     * value is the distributed tax amount for that tax authority id and tax
     * group id.
     * 
     * @deprecated as of 7.0.0
     */
    protected Hashtable taxByTaxJurisdiction = new Hashtable(1);

    protected TaxInformationContainerIfc taxInformationContainer = null;

    /**
     * If an item is beign sent to another jurisdiction, the tax rules for that
     * new jurisdiction are stored here.
     */
    protected TaxRuleIfc[] sendTaxRules;

    /**
     * Default Tax Rule
     */
    protected TaxRuleIfc[] defaultTaxRules;

    /**
     * Localized reson
     */
    protected LocalizedCodeIfc reason = DomainGateway.getFactory().getLocalizedCode();

    /**
     * Constructs ItemTax object.
     */
    public ItemTax()
    {
        itemTaxAmount = DomainGateway.getBaseCurrencyInstance();
        itemInclusiveTaxAmount = DomainGateway.getBaseCurrencyInstance();
        itemTaxableAmount = DomainGateway.getBaseCurrencyInstance();
        overrideAmount = DomainGateway.getBaseCurrencyInstance();
        toggledOverrideAmount = DomainGateway.getBaseCurrencyInstance();
        standardAmountTax = DomainGateway.getBaseCurrencyInstance();
        standardQuantityTax = DomainGateway.getBaseCurrencyInstance();
        overrideAmountTax = DomainGateway.getBaseCurrencyInstance();
        overrideQuantityTax = DomainGateway.getBaseCurrencyInstance();
        taxInformationContainer = DomainGateway.getFactory().getTaxInformationContainerInstance();
        // taxByTaxJurisdiction.clear();
    }

    /**
     * Constructs ItemTax object and sets rate based on parameter.
     * 
     * @param pRate tax rate
     */
    public ItemTax(double pRate)
    { // Begin ItemTax()
        this();
        defaultRate = pRate;
    } // End ItemTax()

    /**
     * Constructs ItemTax object and sets attributes based on parameters.
     * 
     * @param pRate tax rate
     * @param pAmount tax overrideAmount
     * @param pCode reason code
     * @param pToggle tax toggle
     * @param pMode tax mode
     * @param pScope tax scope
     */
    public ItemTax(double pRate, CurrencyIfc pAmount, LocalizedCodeIfc pCode, boolean pToggle, int pMode, int pScope)
    {
        // set attributes
        defaultRate = pRate;
        overrideAmount = pAmount;
        reason = pCode;
        taxToggle = pToggle;
        setTaxMode(pMode);
        taxScope = pScope;
        // if no currency dollar object here, make one
        if (overrideAmount == null)
        {
            overrideAmount = DomainGateway.getBaseCurrencyInstance();
        }
        taxInformationContainer = DomainGateway.getFactory().getTaxInformationContainerInstance();
    }

    /**
     * Clones this object.
     * 
     * @return t cloned object
     */
    public Object clone()
    {
        // create new object
        ItemTax t = new ItemTax();

        setCloneAttributes(t);

        return t;
    }

    /**
     * Sets attributes used for clone.
     * 
     * @param newClass new instance of class
     */
    public void setCloneAttributes(ItemTax newClass)
    {
        newClass.setDefaultRate(defaultRate);
        newClass.setDefaultTaxRules(defaultTaxRules);
        if (reason != null)
        {
            newClass.setReason((LocalizedCodeIfc) reason.clone());
        }
        newClass.setTaxToggle(taxToggle);
        newClass.setTaxMode(taxMode);
        newClass.setOriginalTaxMode(originalTaxMode);
        newClass.setTaxScope(taxScope);
        // if no currency dollar object here, make one
        if (overrideAmount == null)
        {
            newClass.setOverrideAmount(DomainGateway.getBaseCurrencyInstance());
        }
        else
        {
            newClass.setOverrideAmount((CurrencyIfc) overrideAmount.clone());
        }
        if (itemTaxAmount == null)
        {
            newClass.setItemTaxAmount(DomainGateway.getBaseCurrencyInstance());
        }
        else
        {
            newClass.setItemTaxAmount((CurrencyIfc) itemTaxAmount.clone());
        }
        if (itemInclusiveTaxAmount == null)
        {
            newClass.setItemInclusiveTaxAmount(DomainGateway.getBaseCurrencyInstance());
        }
        else
        {
            newClass.setItemInclusiveTaxAmount((CurrencyIfc) itemInclusiveTaxAmount.clone());
        }
        if (itemTaxableAmount != null)
        {
            newClass.setItemTaxableAmount((CurrencyIfc) itemTaxableAmount.clone());
        }
        // set override rate
        // The setOverrideRate() method has side-effects, so we won't use it
        // here.
        newClass.overrideRate = overrideRate;
        newClass.setTaxable(taxable);

        // set toggled values
        newClass.toggledOverrideRate = toggledOverrideRate;
        if (toggledOverrideAmount != null)
        {
            newClass.toggledOverrideAmount = (CurrencyIfc) toggledOverrideAmount.clone();
        }
        newClass.toggledReasonCode = toggledReasonCode;
        newClass.toggledTaxMode = toggledTaxMode;
        newClass.toggledTaxScope = toggledTaxScope;
        newClass.externalTaxEnabled = externalTaxEnabled;
        // clone new attributes for external tax
        newClass.taxGroupId = taxGroupId;
        if (taxGroupName != null)
        {
            newClass.taxGroupName = taxGroupName;
        }
        newClass.standardAmountTax = (CurrencyIfc) standardAmountTax.clone();
        newClass.standardQuantityTax = (CurrencyIfc) standardQuantityTax.clone();
        newClass.overrideAmountTax = (CurrencyIfc) overrideAmountTax.clone();
        newClass.overrideQuantityTax = (CurrencyIfc) overrideQuantityTax.clone();

        newClass.setItemPrice(itemPrice);
        if (taxByTaxJurisdiction != null)
        {
            if (taxByTaxJurisdiction.size() > 0)
            {
                Hashtable ht = new Hashtable(1);
                ht = (Hashtable) taxByTaxJurisdiction.clone();
                newClass.setTaxByTaxJurisdiction(ht);
            }
        }
        else
        {
            Hashtable ht = new Hashtable(1);
            newClass.setTaxByTaxJurisdiction(ht);
        }
        if (taxInformationContainer != null)
        {
            newClass.taxInformationContainer = (TaxInformationContainerIfc) taxInformationContainer.clone();
        }
        newClass.setSendTaxRules(sendTaxRules);

    }

    /**
     * Override line item tax rate.
     * 
     * @param newRate new tax rate
     * @param reason reason code
     * @deprecated as of 13.1. Use {@link overrideTaxRate(double newRate,
     *             LocalizedCodeIfc reason)}
     */
    public void overrideTaxRate(double newRate, int reason)
    {
        // reset values (only if taxable)
        if (taxToggle)
        {
            // set attributes accordingly
            overrideRate = newRate;
            overrideAmount.setZero();
            reasonCode = reason;
            setTaxMode(TaxIfc.TAX_MODE_OVERRIDE_RATE);
            taxScope = TaxIfc.TAX_SCOPE_ITEM;
        }
        itemTaxAmount.setZero();
        itemInclusiveTaxAmount.setZero();
    }

    /**
     * Overrides the tax rate
     * 
     * @param double newRate
     * @param LocalizedCodeIfc reason
     */
    public void overrideTaxRate(double newRate, LocalizedCodeIfc reason)
    {
        // reset values (only if taxable)
        if (taxToggle)
        {
            // set attributes accordingly
            overrideRate = newRate;
            overrideAmount.setZero();
            this.reason = reason;
            setTaxMode(TaxIfc.TAX_MODE_OVERRIDE_RATE);
            taxScope = TaxIfc.TAX_SCOPE_ITEM;
        }
        itemTaxAmount.setZero();
        itemInclusiveTaxAmount.setZero();
    }

    /**
     * Override line item tax amount.
     * 
     * @param pAmount new amount
     * @param reason reason code
     * @deprecated as of 13.1. Use {@link overrideTaxAmount(CurrencyIfc,
     *             LocalizedCodeIfc reason)
     */
    public void overrideTaxAmount(CurrencyIfc pAmount, int reason)
    {
        // reset values (only if taxable)
        if (taxToggle)
        {
            // set relevant attributes
            overrideRate = 0;
            overrideAmount.setStringValue(pAmount.getStringValue());
            reasonCode = reason;
            setTaxMode(TaxIfc.TAX_MODE_OVERRIDE_AMOUNT);
            taxScope = TaxIfc.TAX_SCOPE_ITEM;
            itemTaxAmount.setZero();
            itemInclusiveTaxAmount.setZero();
        }
    }

    /**
     * Overrides the tax amount
     * 
     * @param CurrencyIfc
     * @param LocalizedCodeIfc
     */
    public void overrideTaxAmount(CurrencyIfc pAmount, LocalizedCodeIfc reason)
    {
        // reset values (only if taxable)
        if (taxToggle)
        {
            // set relevant attributes
            overrideRate = 0;
            overrideAmount.setStringValue(pAmount.getStringValue());
            this.reason = reason;
            setTaxMode(TaxIfc.TAX_MODE_OVERRIDE_AMOUNT);
            taxScope = TaxIfc.TAX_SCOPE_ITEM;
            itemTaxAmount.setZero();
            itemInclusiveTaxAmount.setZero();
        }
    }

    /**
     * Toggle tax off (false) or on (true).
     * 
     * @param toggle switch indicating tax is on (true) or off (false)
     * @param reason reason code (of tax being switched off)
     * @deprecated as of 13.1. Use toggleTax(boolean, LocalizedCodeIfc
     */
    public void toggleTax(boolean toggle, int reason)
    {
        itemTaxAmount.setZero();
        itemInclusiveTaxAmount.setZero();
        // if reverting from toggled values, load previously toggled values into
        // attributes
        if (taxMode == TaxIfc.TAX_MODE_TOGGLE_ON)
        {
            // overrideRate = toggledOverrideRate;
            // overrideAmount.setStringValue(toggledOverrideAmount.getStringValue
            // ());
            // reasonCode = toggledReasonCode;
            setTaxMode(TaxIfc.TAX_MODE_TOGGLE_OFF);
            // taxScope = toggledTaxScope;
            taxToggle = toggle;
        }
        else if (taxMode == TaxIfc.TAX_MODE_TOGGLE_OFF)
        {
            setTaxMode(originalTaxMode);
            // still have to set the taxtoggle to true so later the tax will be
            // modified correctly.
            taxToggle = toggle;

        }
        // This item was originally not a togglable tax state
        else
        // save current values in toggled attributes and then toggle tax
        {
            // DEFECT 6251 - Dont save old values. Commenting out because
            // I believe this may change in the future.
            // toggledOverrideRate = overrideRate;
            // toggledOverrideAmount.setStringValue(overrideAmount.getStringValue
            // ());
            // toggledReasonCode = reasonCode;
            // toggledTaxMode = taxMode;
            // toggledTaxScope = taxScope;
            // set relevant attributes

            // A tax originally off or non-taxable is now taxable
            if (toggle == true)
            {
                overrideRate = 0;
                overrideAmount.setZero();
                // If turning tax on an item which by default is taxable, then
                // reset reason code.
                if (taxable == true)
                {
                    reasonCode = CodeConstantsIfc.CODE_INTEGER_UNDEFINED;
                }
                else
                {
                    reasonCode = reason;
                }
                setOriginalTaxMode(getTaxMode());
                setTaxMode(TaxIfc.TAX_MODE_TOGGLE_ON);
                // get tax data from transaction
                taxScope = TaxIfc.TAX_SCOPE_TRANSACTION;
                taxToggle = true;
            }
            // A tax that was originally in place is now non-taxable.
            else
            {
                // if item non-taxable, reset tax mode accordingly
                if (taxable == false)
                {
                    setOriginalTaxMode(getTaxMode());
                    setTaxMode(TaxIfc.TAX_MODE_NON_TAXABLE);
                    // get tax data from transaction
                    taxScope = TaxIfc.TAX_SCOPE_TRANSACTION;
                    reasonCode = CodeConstantsIfc.CODE_INTEGER_UNDEFINED;
                }
                else
                {
                    setOriginalTaxMode(getTaxMode());
                    setTaxMode(TaxIfc.TAX_MODE_TOGGLE_OFF);
                    taxScope = TaxIfc.TAX_SCOPE_ITEM;
                }
                reasonCode = reason;
                overrideRate = 0;
                overrideAmount.setZero();
                taxToggle = false;
            }
        }
    }

    /**
     * Toggle tax off (false) or on (true).
     * 
     * @param toggle switch indicating tax is on (true) or off (false)
     * @param reason reason code (of tax being switched off)
     */
    public void toggleTax(boolean toggle, LocalizedCodeIfc reason)
    {
        itemTaxAmount.setZero();
        itemInclusiveTaxAmount.setZero();
        // if reverting from toggled values, load previously toggled values into
        // attributes
        if (taxMode == TaxIfc.TAX_MODE_TOGGLE_ON)
        {
            // overrideRate = toggledOverrideRate;
            // overrideAmount.setStringValue(toggledOverrideAmount.getStringValue
            // ());
            // reasonCode = toggledReasonCode;
            setTaxMode(TaxIfc.TAX_MODE_TOGGLE_OFF);
            // taxScope = toggledTaxScope;
            taxToggle = toggle;
        }
        else if (taxMode == TaxIfc.TAX_MODE_TOGGLE_OFF)
        {
            setTaxMode(originalTaxMode);
            // still have to set the taxtoggle to true so later the tax will be
            // modified correctly.
            taxToggle = toggle;

        }
        // This item was originally not a togglable tax state
        else
        // save current values in toggled attributes and then toggle tax
        {
            // DEFECT 6251 - Dont save old values. Commenting out because
            // I believe this may change in the future.
            // toggledOverrideRate = overrideRate;
            // toggledOverrideAmount.setStringValue(overrideAmount.getStringValue
            // ());
            // toggledReasonCode = reasonCode;
            // toggledTaxMode = taxMode;
            // toggledTaxScope = taxScope;
            // set relevant attributes

            // A tax originally off or non-taxable is now taxable
            if (toggle == true)
            {
                overrideRate = 0;
                overrideAmount.setZero();
                // If turning tax on an item which by default is taxable, then
                // reset reason code.
                if (taxable == true)
                {
                    this.reason.setCode(CodeConstantsIfc.CODE_UNDEFINED);
                }
                else
                {
                    this.reason = reason;
                }
                setOriginalTaxMode(getTaxMode());
                setTaxMode(TaxIfc.TAX_MODE_TOGGLE_ON);
                // get tax data from transaction
                taxScope = TaxIfc.TAX_SCOPE_TRANSACTION;
                taxToggle = true;
            }
            // A tax that was originally in place is now non-taxable.
            else
            {
                // if item non-taxable, reset tax mode accordingly
                if (taxable == false)
                {
                    setOriginalTaxMode(getTaxMode());
                    setTaxMode(TaxIfc.TAX_MODE_NON_TAXABLE);
                    // get tax data from transaction
                    taxScope = TaxIfc.TAX_SCOPE_TRANSACTION;
                    this.reason.setCode(CodeConstantsIfc.CODE_UNDEFINED);
                }
                else
                {
                    setOriginalTaxMode(getTaxMode());
                    setTaxMode(TaxIfc.TAX_MODE_TOGGLE_OFF);
                    taxScope = TaxIfc.TAX_SCOPE_ITEM;
                }
                this.reason = reason;
                overrideRate = 0;
                overrideAmount.setZero();
                taxToggle = false;
            }
        }
    }

    /**
     * Resets values for standard, transaction-default tax.
     * 
     * @deprecated as of 13.1. No callers
     */
    public void resetStandardTax()
    {
        // toggle tax on reverts to transaction tax rules
        overrideRate = 0;
        overrideAmount.setZero();
        reason.setCode(CodeConstantsIfc.CODE_UNDEFINED);
        setTaxMode(TaxIfc.TAX_MODE_USE_TRANSACTION_STANDARD);
        // get tax data from transaction
        taxScope = TaxIfc.TAX_SCOPE_TRANSACTION;
        taxToggle = true;
    }

    /**
     * Sets override rate.
     * 
     * @param value new override rate setting
     */
    public void setOverrideRate(double value)
    {
        clearTaxAmounts();
        overrideRate = value;
    }

    /**
     * Returns override rate.
     * 
     * @return overrideRate tax override rate
     */
    public double getOverrideRate()
    {
        return (overrideRate);
    }

    /**
     * Sets default rate.
     * 
     * @param value new default rate setting
     */
    public void setDefaultRate(double value)
    {
        defaultRate = value;
    }

    /**
     * Returns default rate.
     * 
     * @return defaultRate tax default rate
     */
    public double getDefaultRate()
    {
        return (defaultRate);
    }

    /**
     * Sets overrideAmount.
     * 
     * @param value new overrideAmount setting
     */
    public void setOverrideAmount(CurrencyIfc value)
    {
        if (getItemPrice() != null)
        {
            clearTaxAmounts();
        }
        overrideAmount = value;
    }

    /**
     * Returns overrideAmount.
     * 
     * @return overrideAmount tax overrideAmount
     */
    public CurrencyIfc getOverrideAmount()
    {
        return (overrideAmount);
    }

    /**
     * Returns the {@link #getOverrideAmount()} unless that is zero, in which
     * case the {@link #getOverrideRate()} is multiplied against the
     * {@link #getItemTaxableAmount()} and returned.
     * 
     * @return overrideRate tax override rate
     */
    public CurrencyIfc getCalculatedOverrideAmount()
    {
        CurrencyIfc amount = getOverrideAmount();
        if (amount == null || amount.getDoubleValue() == 0)
        {
            amount = getItemTaxableAmount().multiply(BigDecimal.valueOf(getOverrideRate()));
        }
        return amount;
    }

    /**
     * Sets reason code.
     * 
     * @param value new reason code setting
     * @deprecated as of 13.1 Use {@link #setReason(LocalizedCodeIfc)}
     **/
    public void setReasonCode(int value)
    {

        reason.setCode(Integer.toString(value));
    }

    /**
     * Returns reason code.
     * 
     * @return reasonCode reason code
     * @deprecated as of 13.1 Use {@link #setReason(LocalizedCodeIfc)}
     **/
    public int getReasonCode()
    {
        return Integer.parseInt(reason.getCode());
    }

    /**
     * Sets reason code text.
     * 
     * @param value new reason code text
     * @deprecated as of 13.1 Use {@link #getReason()}
     */
    public void setReasonCodeText(String value)
    {

        LocalizedTextIfc text = DomainGateway.getFactory().getLocalizedText();
        text.putText(LocaleMap.getLocale(LocaleMap.DEFAULT), value);
        reason.setText(text);
    }

    /**
     * Returns reason code text.
     * 
     * @return reasonCode reason code text
     * @deprecated as of 13.1 Use {@link #getReason()}
     */
    public String getReasonCodeText()
    {
        return reason.getText(LocaleMap.getLocale(LocaleMap.DEFAULT));
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.ItemTaxIfc#getReason()
     */
    public LocalizedCodeIfc getReason()
    {
        return reason;
    }

    /*
     * (non-Javadoc)
     * @see
     * oracle.retail.stores.domain.lineitem.ItemTaxIfc#setReason(oracle.retail
     * .stores.common.utility.LocalizedCodeIfc)
     */
    public void setReason(LocalizedCodeIfc reason)
    {
        this.reason = reason;

    }

    /**
     * Sets tax toggle (true indicates tax is toggled on).
     * 
     * @param value new tax toggle setting
     */
    public void setTaxToggle(boolean value)
    {
        taxToggle = value;
    }

    /**
     * Returns tax toggle.
     * 
     * @return taxToggle tax toggle
     */
    public boolean getTaxToggle()
    {
        return (taxToggle);
    }

    /**
     * Sets tax mode.
     * 
     * @param value new tax mode setting
     */
    public void setTaxMode(int value)
    {
        // If an item send, nothing can override its tax mode.
        if (this.getSendTaxRules() == null)
        {
            taxMode = value;
        }
    }

    /**
     * Returns tax mode.
     * 
     * @return taxMode tax mode
     */
    public int getTaxMode()
    {
        return (taxMode);
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.ItemTaxIfc#isTaxOff()
     */
    public boolean isTaxOff()
    {
        return (TaxConstantsIfc.TAX_MODE_TOGGLE_OFF == getTaxMode());
    }

    /**
     * Sets original tax mode.
     * 
     * @param value original tax mode setting
     * @since 7.0
     */
    public void setOriginalTaxMode(int value)
    {
        if (originalTaxMode == -1)
        {
            originalTaxMode = value;
        }
    }

    /**
     * Returns original tax mode. The original tax mode is set on an item or
     * transaction tax override. This is in case the operator changes his mind
     * later and wants to go back to the original tax information after
     * performing this sequence of steps:<BR>
     * 1) Override tax<BR>
     * 2) Toggle tax off<BR>
     * 3) Toggle Tax on<BR>
     * At this point we want the tax to be the same as it was before tax was
     * overridden, and that is why this variable exists.
     * 
     * @return taxMode original tax mode
     * @since 7.0
     */
    public int getOriginalTaxMode()
    {
        int result = originalTaxMode;
        // It's possible the original tax mode was not set, then return whatever
        // the current tax mode is. The originalTaxMode only gets set when a
        // transaction or item tax override is performed.
        if (result == -1)
        {
            result = getTaxMode();
        }
        return result;
    }

    /**
     * Sets tax scope.
     * 
     * @param value new tax scope setting
     */
    public void setTaxScope(int value)
    {
        taxScope = value;
    }

    /**
     * Returns tax scope.
     * 
     * @return taxScope tax scope
     */
    public int getTaxScope()
    {
        return (taxScope);
    }

    /**
     * Returns taxable.
     * 
     * @return taxable flag
     */
    public boolean getTaxable()
    {
        return (taxable);
    }

    /**
     * Sets taxable flag.
     * 
     * @param value taxable flag
     */
    public void setTaxable(boolean value)
    {
        taxable = value;
        // transactionTax.getTaxMode() inside SaleReturnTransaction does not
        // return TAX_MODE_NON_TAXABLE
        // so at time of initialization of tax mode, we figure out if it needs
        // to be TAX_MODE_NON_TAXABLE
        // set tax mode to match
        // However TAX_MODE_OVERRIDE_RATE and TAX_MODE_OVERRIDE_AMOUNT also
        // needs to be in the condition statement because
        // combination of taxToggle = true, taxable=false and
        // taxMode=TAX_MODE_OVERRIDE_RATE is valid
        // combination of taxToggle = true, taxable=false and
        // taxMode=TAX_MODE_OVERRIDE_AMOUNT is also valid
        // and setCloneAttributes method can overwrite TAX_MODE_OVERRIDE_RATE
        // with TAX_MODE_NON_TAXABLE unintentionally.
        boolean setTaxMode = true;
        if (taxToggle && !taxable && (taxMode == TAX_MODE_OVERRIDE_RATE || taxMode == TAX_MODE_OVERRIDE_AMOUNT))
            setTaxMode = false;
        if (value == false && taxMode != TAX_MODE_TOGGLE_ON && taxMode != TAX_MODE_EXEMPT && setTaxMode)
        {
            setTaxMode(TAX_MODE_NON_TAXABLE);
        }

    }

    /**
     * Retrieves line item extended tax amount.
     * 
     * @return item tax amount
     */
    public CurrencyIfc getItemTaxAmount()
    {
        return (itemTaxAmount);
    }

    /**
     * Sets line item tax amount.
     * 
     * @param value new item tax amount
     */
    public void setItemTaxAmount(CurrencyIfc value)
    {
        itemTaxAmount = value;
    }

    /**
     * Retrieves line item extended inclusive tax amount.
     * 
     * @return item inclusive tax amount
     */
    public CurrencyIfc getItemInclusiveTaxAmount()
    {
        return itemInclusiveTaxAmount;
    }

    /**
     * Sets line item inclusive tax amount.
     * 
     * @param value new item inclusive tax amount
     */
    public void setItemInclusiveTaxAmount(CurrencyIfc value)
    {
        itemInclusiveTaxAmount = value;
    }

    /**
     * Retrieves line item extended taxable amount.
     * 
     * @return item taxable amount
     */
    public CurrencyIfc getItemTaxableAmount()
    {
        return (itemTaxableAmount);
    }

    /**
     * Sets line item taxable amount.
     * 
     * @param value new item taxable amount
     */
    public void setItemTaxableAmount(CurrencyIfc value)
    {
        itemTaxableAmount = value;
    }

    /**
     * Retrieves line item quantity.
     * 
     * @return item quantity
     * @deprecated As of release 4.0.0, replaced by
     *             {@link ItemPrice#getItemQuantity()}
     */
    public BigDecimal getItemQuantity()
    {
        BigDecimal returnQuantity = BigDecimal.ZERO;
        if (getItemPrice() != null)
        {
            returnQuantity = getItemPrice().getItemQuantityDecimal();
        }
        return (returnQuantity);
    }

    /**
     * Sets line item quantity.
     * 
     * @param value new quantity
     * @deprecated As of release 4.0.0, replaced by
     *             {@link ItemPrice#setItemQuantity(BigDecimal)}
     */
    public void setItemQuantity(BigDecimal value)
    {
        if (getItemPrice() != null)
        {
            getItemPrice().setItemQuantity(value);
        }
    }

    /**
     * Retrieves the item price associated with this object.
     * 
     * @return ItemPriceIfc
     */
    public ItemPriceIfc getItemPrice()
    {
        return itemPrice;
    }

    /**
     * Sets the item price associated with this object.
     * 
     * @param ItemPrice
     */
    public void setItemPrice(ItemPriceIfc value)
    {
        itemPrice = value;
    }

    /**
     * Sets enableExternalTax attribute. This indicates to the domain that the
     * foundation's Tax Manager will be used to retrieve rate information and
     * calculate taxes rather than the default rate from the TaxRate parameter.
     * 
     * @param enable new external tax mode setting
     */
    public void setExternalTaxEnabled(boolean enable)
    {
        externalTaxEnabled = enable;
    }

    /**
     * Returns enableExternalTax attribute.
     * 
     * @return externalTaxEnabled value
     */
    public boolean getExternalTaxEnabled()
    {
        return (externalTaxEnabled);
    }

    /**
     * Numeric id of the tax group.
     * 
     * @return int id of the associated tax group
     */
    public int getTaxGroupId()
    {
        return taxGroupId;
    }

    /**
     * Numeric id of the tax group.
     * 
     * @param taxGroupId - tax group identifier
     */
    public void setTaxGroupId(int taxGroupId)
    {
        this.taxGroupId = taxGroupId;
    }

    /**
     * Retrieves string name of the tax group.
     * 
     * @return name of the associated tax group
     */
    public String getTaxGroupName()
    {
        return taxGroupName;
    }

    /**
     * Sets string name of the tax group.
     * 
     * @param name of the associated tax group
     */
    public void setTaxGroupName(String value)
    {
        taxGroupName = value;
    }

    /**
     * Return the quantity of items associated with the tax bucket.
     * 
     * @return quantity of items
     */
    public long getTaxableQuantity()
    {
        BigDecimal roundQty = getItemQuantity().add(BigDecimalConstants.POINT_FIVE);
        return (roundQty.longValue());
    }

    /**
     * Return the net extended taxable amount.
     * 
     * @return net extended mount
     */
    public double getTaxableAmount()
    {
        return (getItemTaxableAmount().getDoubleValue());
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
        return this.standardAmountTax.getDoubleValue() + this.standardQuantityTax.getDoubleValue()
                + this.overrideAmountTax.getDoubleValue() + this.overrideQuantityTax.getDoubleValue();
    }

    /**
     * Set the amount of tax applied using standard tax calculations on the
     * amount of the item(s) associated with the bucket.
     * 
     * @param amountTax - standard tax applied to the bucket
     */
    public void setStandardAmountTax(double amountTax)
    {
        double oldTax = standardAmountTax.getDoubleValue();
        this.standardAmountTax.setDoubleValue(amountTax);
        CurrencyIfc delta = DomainGateway.getBaseCurrencyInstance();
        double d = amountTax - oldTax;
        delta.setDoubleValue(d);
        updateItemTaxAmount(delta);
    }

    /**
     * Get the amount of tax applied using standard tax calculations on the
     * amount of the item(s) associated with the bucket.
     * 
     * @return standard tax applied to the bucket based on item(s) amount
     */
    public double getStandardAmountTax()
    {
        return this.standardAmountTax.getDoubleValue();
    }

    /**
     * Increase/Decrease the amount of tax applied using standard tax
     * calculations on the amount of the item(s) associated with the bucket.
     * 
     * @param amountTax - standard tax applied to the bucket
     */
    public void modifyStandardAmountTax(double amountTax)
    {
        CurrencyIfc delta = DomainGateway.getBaseCurrencyInstance();
        delta.setDoubleValue(amountTax);
        standardAmountTax = standardAmountTax.add(delta);
        updateItemTaxAmount(delta);
    }

    /**
     * Set the amount of tax applied using standard tax calculations on the
     * quantity of the item(s) associated with the bucket.
     * 
     * @param quantityTax - standard tax applied to the bucket
     */
    public void setStandardQuantityTax(double quantityTax)
    {
        double oldTax = standardQuantityTax.getDoubleValue();
        this.standardQuantityTax.setDoubleValue(quantityTax);
        CurrencyIfc delta = DomainGateway.getBaseCurrencyInstance();
        double d = quantityTax - oldTax;
        delta.setDoubleValue(d);
        updateItemTaxAmount(delta);
    }

    /**
     * Get the amount of tax applied using standard tax calculations on the
     * quantity of the item(s) associated with the bucket.
     * 
     * @return standard tax applied to the bucket based on quantity of item(s)
     */
    public double getStandardQuantityTax()
    {
        return this.standardQuantityTax.getDoubleValue();
    }

    /**
     * Increase/Decrease the amount of tax applied using standard tax
     * calculations on the quantity of the item(s) associated with the bucket.
     * 
     * @param amountTax - standard tax applied to the bucket
     */
    public void modifyStandardQuantityTax(double taxAmount)
    {
        CurrencyIfc delta = DomainGateway.getBaseCurrencyInstance();
        delta.setDoubleValue(taxAmount);
        standardQuantityTax = standardQuantityTax.add(delta);
        updateItemTaxAmount(delta);
    }

    /**
     * Sets the amount of override tax on the extended amount of the items.
     * 
     * @param overrideAmount - amount of tax
     * @deprecated. Replaced by void setOverrideAmountTax(CurrencyIfc
     *              overrideAmount)
     */
    public void setOverrideAmountTax(double overrideAmount)
    {
        double oldTax = overrideAmountTax.getDoubleValue();
        this.overrideAmountTax.setDoubleValue(overrideAmount);
        CurrencyIfc delta = DomainGateway.getBaseCurrencyInstance();
        double d = overrideAmount - oldTax;
        delta.setDoubleValue(d);
        updateItemTaxAmount(delta);
    }

    /**
     * Sets the amount of override tax on the extended amount of the items.
     * 
     * @param overrideAmount - amount of tax
     */
    public void setOverrideAmountTax(CurrencyIfc overrideAmount)
    {
        CurrencyIfc oldTax = overrideAmountTax;
        this.overrideAmountTax = overrideAmount;
        CurrencyIfc delta = DomainGateway.getBaseCurrencyInstance();
        delta = overrideAmount.add(oldTax.negate());
        updateItemTaxAmount(delta);
    }

    /**
     * Returns the amount of override Tax calculated on the net extended amount
     * of the items.
     * 
     * @return amount of tax
     */
    public double getOverrideAmountTax()
    {
        return this.overrideAmountTax.getDoubleValue();
    }

    /**
     * Increase/Decrease the amount of tax applied override standard tax
     * calculations on the amount of the item(s) associated with the bucket.
     * 
     * @param amountTax - override tax applied to the bucket
     */
    public void modifyOverrideAmountTax(double overrideAmount)
    {
        CurrencyIfc delta = DomainGateway.getBaseCurrencyInstance();
        delta.setDoubleValue(overrideAmount);
        overrideAmountTax = overrideAmountTax.add(delta);
        updateItemTaxAmount(delta);
    }

    /**
     * Sets the amount of override tax on the quantity of items.
     * 
     * @param overrideQuantity - amount of tax
     */
    public void setOverrideQuantityTax(double overrideQuantity)
    {
        double oldTax = overrideQuantityTax.getDoubleValue();
        this.overrideQuantityTax.setDoubleValue(overrideQuantity);
        CurrencyIfc delta = DomainGateway.getBaseCurrencyInstance();
        double d = overrideQuantity - oldTax;
        delta.setDoubleValue(d);
        updateItemTaxAmount(delta);
    }

    /**
     * Returns the amount of override Tax calculated on the quantity of items.
     * 
     * @return amount of tax
     */
    public double getOverrideQuantityTax()
    {
        return this.overrideQuantityTax.getDoubleValue();
    }

    /**
     * Increase/Decrease the amount of tax applied using override tax
     * calculations on the quantityt of the item(s) associated with the bucket.
     * 
     * @param amountTax - override tax applied to the bucket
     */
    public void modifyOverrideQuantityTax(double quantityTax)
    {
        CurrencyIfc delta = DomainGateway.getBaseCurrencyInstance();
        delta.setDoubleValue(quantityTax);
        overrideQuantityTax = this.overrideQuantityTax.add(delta);
        updateItemTaxAmount(delta);
    }

    /**
     * Called if an override flag is set. The bucket needs to determine the
     * appropriate override taxes.
     * 
     * @deprecated As of release 7.0.0
     */
    public void calculateOverrideTax()
    {
        switch (getTaxMode())
        {
        case TaxIfc.TAX_MODE_OVERRIDE_AMOUNT: {
            this.setOverrideAmountTax(getOverrideAmount().getDoubleValue());
            break;
        }
        case TaxIfc.TAX_MODE_OVERRIDE_RATE: {
            CurrencyIfc temp = getItemTaxableAmount().multiply(new BigDecimal(getOverrideRate()));
            setOverrideAmountTax(temp.getDoubleValue());
            break;
        }
        default: {

        }
        }
    }

    /**
     * Clear the tax amounts within the bucket in preparation for new tax
     * calculations
     */
    public void clearTaxAmounts()
    {
        standardAmountTax.setZero();
        standardQuantityTax.setZero();
        overrideAmountTax.setZero();
        overrideQuantityTax.setZero();
        itemTaxAmount.setZero();
        itemInclusiveTaxAmount.setZero();
        taxInformationContainer.reset();
    }

    /**
     * Negate the tax amounts
     */
    public void negateTaxAmounts()
    {
        standardAmountTax = standardAmountTax.negate();
        standardQuantityTax = standardQuantityTax.negate();
        overrideAmountTax = overrideAmountTax.negate();
        overrideQuantityTax = overrideQuantityTax.negate();
        itemTaxAmount = itemTaxAmount.negate();
        itemInclusiveTaxAmount = itemInclusiveTaxAmount.negate();
        taxInformationContainer.negate();
    }

    /**
     * Set sign for tax amounts
     * 
     * @param sign CurrencyIfc.POSITIVE or CurrencyIfc.Negative
     */
    public void setSignforTaxAmounts(int sign)
    {
        if (sign == CurrencyIfc.POSITIVE || sign == CurrencyIfc.NEGATIVE)
        {
            int negatedSign = sign * -1;
            if (standardAmountTax.signum() == negatedSign)
            {
                standardAmountTax = standardAmountTax.negate();
            }
            if (standardQuantityTax.signum() == negatedSign)
            {
                standardQuantityTax = standardQuantityTax.negate();
            }
            if (overrideAmountTax.signum() == negatedSign)
            {
                overrideAmountTax = overrideAmountTax.negate();
            }
            if (overrideQuantityTax.signum() == negatedSign)
            {
                overrideQuantityTax = overrideQuantityTax.negate();
            }
            if (itemTaxAmount.signum() == negatedSign)
            {
                itemTaxAmount = itemTaxAmount.negate();
            }
            if (itemInclusiveTaxAmount.signum() == negatedSign)
            {
                itemInclusiveTaxAmount = itemInclusiveTaxAmount.negate();
            }
            taxInformationContainer.setSign(sign);
        }
    }

    /**
     * Sets override rate to external tax manager rate. Does not clear the tax
     * amounts.
     * 
     * @deprecated As of release 7.0.0
     * @param value new override rate setting
     */
    public void setExternalOverrideRate(double value)
    {
        overrideRate = value; // external tax mgr
    }

    // =====================================================================
    // End TaxBucketIfc Members
    // =====================================================================
    /**
     * Updates item tax amount, based on a specified change in the taxes
     * 
     * @deprecated As of release 7.0.0
     * @param delta amount of change
     */
    protected void updateItemTaxAmount(CurrencyIfc delta)
    {
        itemTaxAmount = itemTaxAmount.add(delta);
    }

    /**
     * Determine if two objects have equal attribute values and that the
     * associated objects are equivalent.
     * 
     * @param obj object to compare with
     * @return boolean true if the objects pass the test
     */
    public boolean equals(Object obj)
    {
        boolean isEqual = false;
        try
        {
            ItemTax other = (ItemTax) obj;

            // Note: taxGroupID and taxGroupName are not included here.
            // They are temporary values and are not persisted.
            if (this.overrideRate != other.overrideRate)
            {
                isEqual = false;
            }
            else if (this.defaultRate != other.defaultRate)
            {
                isEqual = false;
            }
            else if (!Util.isArrayEqual(this.defaultTaxRules, other.defaultTaxRules))
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(overrideAmount, other.overrideAmount))
            {
                isEqual = false;
            }
            else if (this.reason.getCode() != other.reason.getCode())
            {
                isEqual = false;
            }
            else if (this.taxToggle != other.taxToggle)
            {
                isEqual = false;
            }
            else if (this.taxMode != other.taxMode)
            {
                isEqual = false;
            }
            else if (this.taxScope != other.taxScope)
            {
                isEqual = false;
            }
            else if (this.taxable != other.taxable)
            {
                isEqual = false;
            }
            else if (externalTaxEnabled != other.externalTaxEnabled)
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(taxByTaxJurisdiction, other.taxByTaxJurisdiction))
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(itemTaxableAmount, other.itemTaxableAmount))
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(itemTaxAmount, other.itemTaxAmount))
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(itemInclusiveTaxAmount, other.itemInclusiveTaxAmount))
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(taxInformationContainer, other.taxInformationContainer))
            {
                isEqual = false;
            }
            else
            {
                isEqual = true;
            }
        }
        catch (Exception e)
        {
            isEqual = false;
        }
        return isEqual;
    }

    /**
     * Returns string data for journal entry.
     * 
     * @deprecated Instead use toJournalString(Locale)
     * @return String representation of object for journal
     */
    public String toJournalString()
    {
        return toJournalString(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
    }

    /**
     * Returns string data for journal entry.
     * 
     * @param journalLocale client's journal locale.
     * @return String representation of object for journal
     */
    public String toJournalString(Locale journalLocale)
    {
        // build string
        StringBuilder sb = new StringBuilder();
        Object[] dataArgs = new Object[3];
        // tailor output to mode
        if (taxScope == TaxIfc.TAX_SCOPE_ITEM)
        {
            switch (taxMode)
            {
            case TaxIfc.TAX_MODE_OVERRIDE_RATE:
                dataArgs[0] = formatTaxRate(overrideRate);
                sb.append(Util.EOL)
                        .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                JournalConstantsIfc.TAX_OVERRIDE_LABEL, dataArgs, journalLocale))
                        .append(JournalConstantsIfc.PERCENTILE_SYMBOL)
                        .append(Util.EOL)
                        .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TAX_RSN_LABEL,
                                null, journalLocale));
                sb.append(" ");
                appendReasonJournalString(sb);
                break;
            case TaxIfc.TAX_MODE_OVERRIDE_AMOUNT:
                String overrideAmountString = overrideAmount.toGroupFormattedString();
                dataArgs[0] = overrideAmountString;
                sb.append(Util.EOL).append(
                        I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                JournalConstantsIfc.TAX_OVERRIDE_AMOUNT_LABEL, dataArgs, journalLocale)); // CR

                sb.append(Util.EOL).append(
                        I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TAX_RSN_LABEL, null,
                                journalLocale));
                sb.append(" ");
                appendReasonJournalString(sb);
                break;
            case TaxIfc.TAX_MODE_TOGGLE_ON:
            case TaxIfc.TAX_MODE_TOGGLE_OFF:
                dataArgs[0] = TaxIfc.TAX_MODE_DESCRIPTOR[taxMode];
                sb.append(Util.EOL).append(
                        I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TAX_LABEL, dataArgs,
                                journalLocale));
                sb.append(Util.EOL).append(
                        I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TAX_RSN_LABEL, null,
                                journalLocale));
                sb.append(" ");
                appendReasonJournalString(sb);
                break;
            default:
                // do nothing
                break;
            }
        }
        return (sb.toString());
    }

    private void appendReasonJournalString(StringBuilder journalString)
    {
        if (reason != null)
        {
            journalString.append(reason.getText(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)) + " (")
                    .append(reason.getCode()).append(")");
        }

    }

    /**
     * Formats tax rate (corrects doubles problem).
     * 
     * @deprecated As of release 4.0.0
     * @param value rate
     * @return formatted tax rate
     */
    public String formatTaxRate(double taxRate)
    {
        BigDecimal bRate = new BigDecimal(taxRate).movePointRight(2);
        if (bRate.scale() > 4)
        {
            bRate = bRate.setScale(4, BigDecimal.ROUND_HALF_UP);
        }
        return (bRate.toString());
    }

    /**
     * Set the hashtable which contains the key of tax authority id and tax
     * group id, and the value of tax amount for that tax authority.
     * 
     * @deprecated As of release 7.0.0
     * @param ht Hashtable
     */
    public void setTaxByTaxJurisdiction(Hashtable ht)
    {
        this.taxByTaxJurisdiction = ht;
    }

    /**
     * Get the hashtable which contains the key of tax authority id and tax
     * group id, and the value of tax amount for that tax authority.
     * 
     * @deprecated As of release 7.0.0
     * @return Hashtable object
     */
    public Hashtable getTaxByTaxJurisdiction()
    {
        return this.taxByTaxJurisdiction;
    }

    /**
     * Method to default display string function.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        StringBuilder strResult = new StringBuilder("Class:  ItemTax  " + hashCode());
        strResult.append("\n\tDefault Rate:       [").append(defaultRate).append("]");
        strResult.append("\n\tDefault Tax Rules:  [").append(defaultTaxRules).append("]");
        strResult.append("\n\tOverride Rate:      [").append(overrideRate).append("]");
        strResult.append("\n\tOverride Amount:    [").append(overrideAmount.toString()).append("]");
        strResult.append("\n\tReason:             [").append(reason).append("]");
        strResult.append("\n\tToggle:             [").append(taxToggle).append("]");
        strResult.append("\n\tTax Mode:           [").append(TaxIfc.TAX_MODE_DESCRIPTOR[taxMode]).append("]");
        strResult.append("\n\tTax Scope:          [").append(TaxIfc.TAX_SCOPE_DESCRIPTOR[taxScope]).append("]");
        strResult.append("\n\tTaxable:            [").append(taxable).append("]");
        strResult.append("\t\nExternalTaxEnabled: [").append(externalTaxEnabled).append("]");
        strResult.append("\t\nitemTaxAmount:      [").append(itemTaxAmount).append("]");
        strResult.append("\t\nitemInclusiveTaxAmount:      [").append(itemInclusiveTaxAmount).append("]");
        strResult.append("\t\nTaxByTaxJurisdiction: [").append(taxByTaxJurisdiction).append("]");
        // pass back result
        return strResult.toString();
    }

    /**
     * Retrieve the tax information container
     * 
     * @return TaxInformationContainerIfc
     */
    public TaxInformationContainerIfc getTaxInformationContainer()
    {
        return taxInformationContainer;
    }

    /**
     * Set the tax information container
     * 
     * @param value the tax information container to set
     */
    public void setTaxInformationContainer(TaxInformationContainerIfc value)
    {
        taxInformationContainer = value;
    }

    /**
     * This line item is being shipped to another tax jurisdiction, save the tax
     * rules for that jurisdiction here. This is then used in the
     * getActiveTaxRules method to return taxRules for this line item.
     * 
     * @param taxRules TaxRules to set
     */
    public void setSendTaxRules(TaxRuleIfc[] taxRules)
    {
        this.sendTaxRules = taxRules;
    }

    /**
     * Get the tax rules to be used for this line item, if it is being sent to
     * another location.
     * 
     * @return taxRules to use
     */
    public TaxRuleIfc[] getSendTaxRules()
    {
        return this.sendTaxRules;
    }

    /**
     * Retrieve the active tax rules on this object Return NULL if theres no
     * rules, return 0 rules if Tax mode is taxexempt or nontaxable.
     * 
     * @return array of the active tax rules
     */
    public RunTimeTaxRuleIfc[] getActiveTaxRules()
    {
        RunTimeTaxRuleIfc[] taxRules = null;
        int identity = getLineItemTaxIdentifier();

        if (getTaxMode() == TaxConstantsIfc.TAX_MODE_TOGGLE_OFF)
        {
            OverrideItemTaxRuleIfc toggleOffTaxRule = DomainGateway.getFactory()
                    .getOverrideItemTaxToggleOffRuleInstance();
            toggleOffTaxRule.setTaxRuleName(TAX_SCOPE_DESCRIPTOR[1] + " " + TAX_TOGGLE_OFF);
            toggleOffTaxRule.setTaxAuthorityName(TAX_SCOPE_DESCRIPTOR[1] + " " + TAX_TOGGLE_OFF);
            toggleOffTaxRule.setUniqueID(toggleOffTaxRule.getTaxRuleName() + " - " + String.valueOf(identity));
            toggleOffTaxRule.setItemTaxIdentityHashCode(identity);
            taxRules = new RunTimeTaxRuleIfc[1];
            taxRules[0] = toggleOffTaxRule;

        }
        else if (getSendTaxRules() != null && getTaxMode() != TaxConstantsIfc.TAX_MODE_NON_TAXABLE)
        {
            taxRules = this.sendTaxRules;
        }
        else
        {

            switch (getTaxMode())
            {
            case TaxConstantsIfc.TAX_MODE_OVERRIDE_AMOUNT: {
                OverrideItemTaxByAmountRuleIfc overrideItemTaxByAmountRule = DomainGateway.getFactory()
                        .getOverrideItemTaxByAmountRuleInstance();

                overrideItemTaxByAmountRule.setTaxRuleName(TAX_SCOPE_DESCRIPTOR[1] + " " + TAX_OVERRIDE_BY_AMOUNT);
                overrideItemTaxByAmountRule.setTaxAuthorityName(TAX_SCOPE_DESCRIPTOR[1] + " " + TAX_OVERRIDE_BY_AMOUNT);
                overrideItemTaxByAmountRule.setUniqueID(overrideItemTaxByAmountRule.getTaxRuleName() + " - "
                        + String.valueOf(identity));
                overrideItemTaxByAmountRule.setFixedTaxAmount(overrideAmount);
                overrideItemTaxByAmountRule.setItemTaxIdentityHashCode(identity);

                taxRules = new RunTimeTaxRuleIfc[1];
                taxRules[0] = overrideItemTaxByAmountRule;

                break;
            }
            case TaxConstantsIfc.TAX_MODE_OVERRIDE_RATE: {
                OverrideItemTaxByRateRuleIfc overrideItemTaxByRateRule = DomainGateway.getFactory()
                        .getOverrideItemTaxByRateRuleInstance();

                overrideItemTaxByRateRule.setTaxRuleName(TAX_SCOPE_DESCRIPTOR[1] + " " + TAX_OVERRIDE_BY_PERCENT);
                overrideItemTaxByRateRule.setTaxAuthorityName(TAX_SCOPE_DESCRIPTOR[1] + " " + TAX_OVERRIDE_BY_PERCENT);
                overrideItemTaxByRateRule.setUniqueID(overrideItemTaxByRateRule.getTaxRuleName() + " - "
                        + String.valueOf(identity));
                overrideItemTaxByRateRule.setTaxRate(BigDecimal.valueOf(overrideRate));
                overrideItemTaxByRateRule.setItemTaxIdentityHashCode(identity);

                taxRules = new RunTimeTaxRuleIfc[1];
                taxRules[0] = overrideItemTaxByRateRule;

                break;
            }
            case TaxConstantsIfc.TAX_MODE_EXEMPT:
            case TaxConstantsIfc.TAX_MODE_NON_TAXABLE: {
                taxRules = new RunTimeTaxRuleIfc[0];
                break;
            }
            }
        }
        return taxRules;
    }

    public void setDefaultTaxRules(TaxRuleIfc[] newDefaultTaxRules)
    {
        this.defaultTaxRules = newDefaultTaxRules;
    }

    /**
     * This is the array of default rules to use when no other rules can be
     * found. Default implementation is a TaxByLineItem rule with a fixed rate
     * as defined in the application.xml file.
     * 
     * @return Array of tax rules to use as default
     */
    public TaxRuleIfc[] getDefaultTaxRules()
    {
        if (defaultTaxRules == null || defaultTaxRules.length == 0)
        {
            defaultTaxRules = new TaxRuleIfc[1];
            TaxRuleIfc taxRule = DomainGateway.getFactory().getTaxByLineRuleInstance();
            taxRule.setTaxRuleName("LocTx");
            taxRule.setTaxAuthorityName("LocTx");
            defaultTaxRules[0] = taxRule;

        }
        for (int i = 0; i < defaultTaxRules.length; i++)
        {
            TaxRuleIfc taxRule = defaultTaxRules[i];

            boolean inclusiveTaxFlag = Boolean.valueOf(DomainGateway.getProperty("InclusiveTaxEnabled", "false"));
            taxRule.setInclusiveTaxFlag(inclusiveTaxFlag);

            // create tax rate calculator
            TaxRateCalculatorIfc calc = DomainGateway.getFactory().getTaxRateCalculatorInstance(inclusiveTaxFlag);
            calc.setTaxRate(new BigDecimal(getDefaultRate()));
            calc.setScale(4);
            taxRule.setTaxCalculator(calc);
        }
        return defaultTaxRules;
    }

    /**
     * Get the identifier the uniquely identifies this item
     * 
     * @return unique identifier for this tax line item
     */
    public int getLineItemTaxIdentifier()
    {
        return System.identityHashCode(this);
    }

}
