/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/ShippingMethod.java /main/14 2012/07/02 10:12:50 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     06/29/12 - Rename NewTaxRuleIfc to TaxRulesIfc
 *    yiqzhao   04/03/12 - refactor store send for cross channel
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         5/8/2007 2:30:39 PM    Brett J. Larsen CR
 *         26477 - adding support for new columns/fields added for the VAT
 *         project
 *
 *         table: tax shipping records (shp_rds_sls_rtn)
 *         columns: tax group id, tax amount & inclusive tax amount
 *    5    360Commerce 1.4         4/30/2007 5:38:35 PM   Sandy Gu        added
 *          api to handle inclusive tax
 *    4    360Commerce 1.3         4/25/2007 10:00:53 AM  Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:29:58 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:17 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:13 PM  Robert Pearse
 *
 *   Revision 1.8  2004/09/23 00:30:53  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.7  2004/08/31 19:58:44  rsachdeva
 *   @scr 6791 Transaction Level Send Javadoc
 *
 *   Revision 1.6  2004/08/09 13:58:44  rsachdeva
 *   @scr 6791 Transaction Level Send
 *
 *   Revision 1.5  2004/06/11 18:59:53  lzhao
 *   @scr 4670: Change the way to getCalculatedShippingCharge
 *
 *   Revision 1.4  2004/02/17 16:18:52  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:34  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:27  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:30  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:35:50   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:52:40   msg
 * Initial revision.
 *
 *    Rev 1.2   20 Mar 2002 12:47:12   vxs
 * Put in null pointer checks in toString()
 * Resolution for POS SCR-954: Domain - Arts Translation
 *
 *    Rev 1.1   Mar 18 2002 23:01:34   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:21:46   msg
 * Initial revision.
 *
 *    Rev 1.5   11 Jan 2002 16:29:48   sfl
 * Code cleanup based on good suggestions collected during
 * code review.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 *    Rev 1.4   04 Jan 2002 16:24:16   sfl
 * Comments clean up.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 *    Rev 1.3   07 Dec 2001 09:44:22   sfl
 * Modified the setCloneAttributes, equal and toString
 * method to include the newly added calculatedShippingCharge
 * and instruction attributes information.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 *    Rev 1.2   06 Dec 2001 18:44:32   baa
 * add methods to support send feature
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.1   04 Dec 2001 13:33:30   sfl
 * Added new attributes and the associated set/get
 * methods for weight based shipping charge rate
 * information and flat rate information.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 *    Rev 1.0   03 Dec 2001 18:19:00   sfl
 * Initial revision.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;

// foundation imports
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tax.TaxRuleIfc;
import oracle.retail.stores.foundation.utility.Util;


//------------------------------------------------------------------------------
/**
    This class stores the shipping type and shipping base charge information
    of the ShippingMethod.
    $Revision: /main/14 $
**/
/**
 * @deprecated since version 14.
 * @author yiqzhao
 *
 */
//------------------------------------------------------------------------------

public class ShippingMethod implements ShippingMethodIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 2404405133136003910L;

    /**
        revision number
    **/
    public static String revisionNumber = "$Revision: /main/14 $";
    
	public static int SHIPPING_CHARGE_BY_WEIGHT = 0;
	public static int SHIPPING_CHARGE_BY_FLAT_RATE = 1;
	public static int SHIPPING_CHARGE_BY_AMOUNT = 2;
	public static int SHIPPING_CHARGE_NONE = 3;
	
    /**
	shipping charge calculation type
	**/
	protected int shippingChargeCalculationType = SHIPPING_CHARGE_BY_WEIGHT;	

    /**
        shippingmethod id
    **/
    protected int shippingMethodID = 0;

    /**
    	shippingCarriers
    **/
    protected LocalizedTextIfc shippingCarriers = null;

    /**
    	shippingTypes
     **/
    protected LocalizedTextIfc shippingTypes = null;  

    /**
        shippingBaseCharge
    **/
    protected CurrencyIfc baseShippingCharge = null;

    /**
        shippingChargeByWeight
    **/
    protected CurrencyIfc shippingChargeRateByWeight = null;

    /**
        flatRate
    **/
    protected CurrencyIfc flatRate = null;

    /**
     *  shipping instructions
     */
    protected String  instructions = "";

    /**
     * taxable
     */
    protected boolean taxable = false;

    /**
     * tax group id
     */
    protected int taxGroupID;

    /**
     Tax Rules
     **/
    protected ArrayList taxRules = null;

    /**
        calculated shipping charge
    **/
    protected CurrencyIfc calculatedShippingCharge = null;

    //----------------------------------------------------------------------------
    /**
        Constructs ShippingMethod object.
    **/
    //----------------------------------------------------------------------------
    public ShippingMethod()
    {  // begin
        initialize();
    }

    //---------------------------------------------------------------------
    /**
        Initializes object.
    **/
    //---------------------------------------------------------------------
    public void initialize()
    {
        baseShippingCharge = DomainGateway.getBaseCurrencyInstance();
        shippingChargeRateByWeight = DomainGateway.getBaseCurrencyInstance();
        flatRate = DomainGateway.getBaseCurrencyInstance();
        calculatedShippingCharge = DomainGateway.getBaseCurrencyInstance();
        taxRules = new ArrayList();
        shippingCarriers = DomainGateway.getFactory().getLocalizedText();
        shippingTypes = DomainGateway.getFactory().getLocalizedText();
    }


    //----------------------------------------------------------------------------
    /**
        Creates clone of this object.
        @return Object clone of this object
    **/
    //----------------------------------------------------------------------------
    public Object clone()
    {                                   // begin clone()
        // instantiate new object
        ShippingMethod sm = new ShippingMethod();

        // set values
        setCloneAttributes(sm);

        // pass back Object
        return(sm);
    }                                   // end clone()

    //----------------------------------------------------------------------------
    /**
        Sets attributes in clone of this object.
        @param newClass new instance of object
    **/
    //----------------------------------------------------------------------------
    public void setCloneAttributes(ShippingMethod newClass)
    {                                   // begin setCloneAttributes()

        newClass.setShippingMethodID(getShippingMethodID());

        if (shippingCarriers != null)
        {
            newClass.setLocalizedShippingCarriers((LocalizedTextIfc)shippingCarriers.clone());
        }

        if (shippingTypes != null)
        {
            newClass.setLocalizedShippingTypes((LocalizedTextIfc)shippingTypes.clone());
        }

        if (baseShippingCharge != null)
        {
            newClass.setBaseShippingCharge((CurrencyIfc) getBaseShippingCharge().clone());
        }

        if (shippingChargeRateByWeight != null)
        {
            newClass.setShippingChargeRateByWeight((CurrencyIfc) getShippingChargeRateByWeight().clone());
        }

        if (flatRate != null)
        {
            newClass.setFlatRate((CurrencyIfc) getFlatRate().clone());
        }

        if (instructions != "")
        {
            newClass.setShippingInstructions(new String(instructions));
        }

        if (calculatedShippingCharge != null)
        {
            newClass.setCalculatedShippingCharge((CurrencyIfc) getCalculatedShippingCharge().clone());
        }

        newClass.setTaxable(getTaxable());
        newClass.setTaxGroupID(getTaxGroupID());

        if (taxRules != null)
        {
        	for (Iterator i = taxRules.iterator(); i.hasNext(); )
        	{
        		TaxRuleIfc txRule = (TaxRuleIfc)i.next();
        		newClass.addTaxRule(
        				txRule == null ? null : (TaxRuleIfc)txRule.clone());
        	}
        }
    }                                   // end setCloneAttributes()

    //----------------------------------------------------------------------------
    /**
        Determine if two objects are identical.
        @param obj object to compare with
        @return true if the objects are identical, false otherwise
    **/
    //----------------------------------------------------------------------------
    public boolean equals(Object obj)
    {                                   // begin equals()
        boolean isEqual = true;
        // confirm object instanceof this object
        if (obj instanceof ShippingMethod)
        {                                   // begin compare objects
            // downcast the input object
            ShippingMethod sm = (ShippingMethod) obj;

            // compare all the attributes of ShippingMethod

            if  ((getShippingMethodID() == sm.getShippingMethodID()) &&
                Util.isObjectEqual(getLocalizedShippingCarriers(), sm.getLocalizedShippingCarriers()) &&
                Util.isObjectEqual(getLocalizedShippingTypes(), sm.getLocalizedShippingTypes()) &&
                Util.isObjectEqual(getBaseShippingCharge(), sm.getBaseShippingCharge()) &&
                Util.isObjectEqual(getShippingChargeRateByWeight(), sm.getShippingChargeRateByWeight()) &&
                Util.isObjectEqual(getFlatRate(), sm.getFlatRate()) &&
                Util.isObjectEqual(getShippingInstructions(), sm.getShippingInstructions()) &&
                Util.isObjectEqual(getCalculatedShippingCharge(), sm.getCalculatedShippingCharge()) &&
				(getTaxable() == sm.getTaxable()) &&
				(getTaxGroupID() == sm.getTaxGroupID()))

            {
                isEqual = true;             // set the return code to true
            }
            else
            {
                isEqual = false;            // set the return code to false
            }
        }                                   // end compare objects
        else
        {
             isEqual = false;
        }
        return(isEqual);
    }                                   // end equals()

    //----------------------------------------------------------------------------
    /**
        Retrieves calculated shipping charge.
        @return calculated shipping charge
    **/
    //----------------------------------------------------------------------------
    public CurrencyIfc getCalculatedShippingCharge()
    {                                   // begin getCalculatedShippingCharge()
        return(calculatedShippingCharge);
    }                                   // end getCalculatedShippingCharge()

    //----------------------------------------------------------------------------
    /**
        Sets calculated shipping charge
        @param value shipping charge
    **/
    //----------------------------------------------------------------------------
    public void setCalculatedShippingCharge(CurrencyIfc value)
    {                                   // begin setCalculatedShippingCharge()
        calculatedShippingCharge = value;
    }                                   // end setCalculatedShippingCharge()

    //----------------------------------------------------------------------------
    /**
        Retrieves shipping instructions.
        @return String instructions
    **/
    //----------------------------------------------------------------------------
    public String getShippingInstructions()
    {                                   // begin getShippingInstructions()
        return(instructions);
    }                                   // end getShippingInstructions()

    //----------------------------------------------------------------------------
    /**
        Sets the ShippingInstructions.
        @param value  String identifier
    **/
    //----------------------------------------------------------------------------
    public void setShippingInstructions(String value)
    {                                   // begin setShippingInstructions()
        instructions = value;
    }                                   // end setShippingInstructions()
    //----------------------------------------------------------------------------
    /**
        Retrieves shippingmethod identifier.
        @return shippingmethod identifier
    **/
    //----------------------------------------------------------------------------
    public int getShippingMethodID()
    {                                   // begin getShippingMethodID()
        return(shippingMethodID);
    }                                   // end getShippingMethodID()

    //----------------------------------------------------------------------------
    /**
        Sets the shippingmethod identifier.
        @param value  shippingmethod identifier
    **/
    //----------------------------------------------------------------------------
    public void setShippingMethodID(int value)
    {                                   // begin setShippingMethodID()
        shippingMethodID = value;
    }                                   // end setShippingMethodID()

    //----------------------------------------------------------------------------
    /**
        Retrieves shipping carrier.
        @return shipping carrier
        @deprecated As of 13.1 Use {@link ShippingMethodIfc#getShippingCarrier(Locale)}
    **/
    //----------------------------------------------------------------------------
    public String getShippingCarrier()
    {                                   // begin getShippingCarrier()
        return getShippingCarrier(LocaleMap.getLocale(LocaleMap.DEFAULT));
    }                                   // end getShippingCarrier()

    //----------------------------------------------------------------------------
    /**
        Retrieves shipping carrier of the locale
        @param locale the locale
        @return shipping carrier of the locale
    **/
    //----------------------------------------------------------------------------
    public String getShippingCarrier(Locale locale)
    {
    	return shippingCarriers.getText(LocaleMap.getBestMatch(locale));
    }

    //----------------------------------------------------------------------------
    /**
        Retrieves shipping carriers of all locales
        @return shipping carriers of all locales
    **/
    //----------------------------------------------------------------------------
    public LocalizedTextIfc getLocalizedShippingCarriers()
    {
    	return shippingCarriers;
    }

    //----------------------------------------------------------------------------
    /**
        Sets the shipping carrier.
        @param value  shipping carrier
        @deprecated As of 13.1 Use {@link ShippingMethodIfc#setShippingCarrier(Locale, String)}
    **/
    //----------------------------------------------------------------------------
    public void setShippingCarrier(String value)
    {                                   // begin setShippingCarrier()
    	setShippingCarrier(LocaleMap.getLocale(LocaleMap.DEFAULT), value);
    }                                   // end setShippingCarrier()

    //----------------------------------------------------------------------------
    /**
        Sets the shipping carrier of the locale.
        @param locale the locale
        @param value shipping carrier
    **/
    //----------------------------------------------------------------------------
    public void setShippingCarrier(Locale locale, String value)
    {
    	shippingCarriers.putText(LocaleMap.getBestMatch(locale), value);
    }

    //----------------------------------------------------------------------------
    /**
        Sets the shipping carriers of all locales
        @param values shipping carriers of all locales
    **/
    //----------------------------------------------------------------------------
    public void setLocalizedShippingCarriers(LocalizedTextIfc values)
    {
    	shippingCarriers = values;
    }

    //----------------------------------------------------------------------------
    /**
        Retrieves shipping type.
        @return shipping type
        @deprecated As of 13.1 Use {@link ShippingMethodIfc#getShippingType(Locale)}
    **/
    //----------------------------------------------------------------------------
    public String getShippingType()
    {                                   // begin getShippingType()
        return getShippingType(LocaleMap.getLocale(LocaleMap.DEFAULT));
    }                                   // end getShippingType()

    //----------------------------------------------------------------------------
    /**
        Retrieves shipping type of the locale
        @param locale the locale
        @return shipping type of the locale
    **/
    //----------------------------------------------------------------------------
    public String getShippingType(Locale locale)
    {
    	return shippingTypes.getText(LocaleMap.getBestMatch(locale));
    }

    //----------------------------------------------------------------------------
    /**
        Retrieves shipping types of all locales
        @return shipping types of all locales
    **/
    //----------------------------------------------------------------------------
    public LocalizedTextIfc getLocalizedShippingTypes()
    {
    	return shippingTypes;
    }

    //----------------------------------------------------------------------------
    /**
        Sets the shipping type.
        @param value  shipping type
        @deprecated As of 13.1 Use {@link ShippingMethodIfc#setShippingType(Locale, String)}
    **/
    //----------------------------------------------------------------------------
    public void setShippingType(String value)
    {                                   // begin setShippingType()
    	setShippingType(LocaleMap.getLocale(LocaleMap.DEFAULT), value);
    }                                   // end setShippingType()

    //----------------------------------------------------------------------------
    /**
        Sets the shipping type of the locale.
        @param locale the locale
        @param value shipping type
    **/
    //----------------------------------------------------------------------------
    public void setShippingType(Locale locale, String value)
    {
    	shippingTypes.putText(LocaleMap.getBestMatch(locale), value);
    }

    //----------------------------------------------------------------------------
    /**
        Sets the shipping types of all locales
        @param values shipping types of all locales
    **/
    //----------------------------------------------------------------------------
    public void setLocalizedShippingTypes(LocalizedTextIfc values)
    {
    	shippingTypes = values;
    }

    //----------------------------------------------------------------------------
    /**
        Retrieves base shipping charge.
        @return base shipping charge
    **/
    //----------------------------------------------------------------------------
    public CurrencyIfc getBaseShippingCharge()
    {                                   // begin getBaseShippingCharge()
        return(baseShippingCharge);
    }                                   // end getBaseShippingCharge()

    //----------------------------------------------------------------------------
    /**
        Sets the base shipping charge.
        @param value base shipping charge
    **/
    //----------------------------------------------------------------------------
    public void setBaseShippingCharge(CurrencyIfc value)
    {                                   // begin setBaseShippingCharge()
        baseShippingCharge = value;
    }                                   // end setBaseShippingCharge()

    //----------------------------------------------------------------------------
    /**
        Retrieves weight based shipping charge rate.
        @return weight based shipping charge rate
    **/
    //----------------------------------------------------------------------------
    public CurrencyIfc getShippingChargeRateByWeight()
    {                                   // begin getShippingChargeRateByWeight()
        return(shippingChargeRateByWeight);
    }                                   // end getShippingChargeRateByWeight()

    //----------------------------------------------------------------------------
    /**
        Sets the weight based shipping charge rate.
        @param value weight based shipping charge rate
    **/
    //----------------------------------------------------------------------------
    public void setShippingChargeRateByWeight(CurrencyIfc value)
    {                                   // begin setShippingChargeRateByWeight()
        shippingChargeRateByWeight = value;
    }                                   // end setShippingChargeRateByWeight()

    //----------------------------------------------------------------------------
    /**
        Retrieves flat rate.
        @return flat rate
    **/
    //----------------------------------------------------------------------------
    public CurrencyIfc getFlatRate()
    {                                   // begin getFlatRate()
        return(flatRate);
    }                                   // end getFlatRate()

    //----------------------------------------------------------------------------
    /**
        Sets the flat rate.
        @param value flat rate
    **/
    //----------------------------------------------------------------------------
    public void setFlatRate(CurrencyIfc value)
    {                                   // begin setFlatRate()
        flatRate = value;
    } // end setFlatRate()

    //----------------------------------------------------------------------------
    /**
     * Retrieves taxable flag
     * @return taxable flag
     */
    //----------------------------------------------------------------------------
    public boolean getTaxable()
    {
    	return taxable;
    }
    //----------------------------------------------------------------------------
	/**
	 * Sets the taxable flag
	 * @param taxable the taxable flag
	 */
    //----------------------------------------------------------------------------
	public void setTaxable(boolean taxable)
	{
		this.taxable = taxable;
	}
    //----------------------------------------------------------------------------
	/**
	 * Retrieves tax group id
	 * @return tax group id
	 */
    //----------------------------------------------------------------------------
	public int getTaxGroupID()
	{
		return taxGroupID;
	}
    //----------------------------------------------------------------------------
	/**
	 * Sets the tax group id
	 * @param taxGroupID the tax group id
	 */
    //----------------------------------------------------------------------------
	public void setTaxGroupID(int taxGroupID)
	{
		this.taxGroupID = taxGroupID;
	}
    //----------------------------------------------------------------------------
	/**
	 * Retrieves tax rules
	 * @return tax rules
	 */
    //----------------------------------------------------------------------------
	public TaxRuleIfc[] getTaxRules()
	{
        TaxRuleIfc[] tr = null;
        if (taxRules != null && taxRules.size() > 0)
        {
            tr = (TaxRuleIfc[]) taxRules.toArray(new TaxRuleIfc[0]);
        }
        return tr;
	}

    //----------------------------------------------------------------------------
	/**
	 * Sets the tax rules
	 * @param rules the tax rules
	 */
    //----------------------------------------------------------------------------
	public void setTaxRules(TaxRuleIfc[] rules)
	{
		if (rules != null)
		{
			if (taxRules != null)
			{
				taxRules.clear();
			}
			else
			{
				taxRules = new ArrayList();
			}
			taxRules.addAll(Arrays.asList(rules));

		}
		else
		{
			if( taxRules == null )
			{
				taxRules = new ArrayList();
			}
			else
			{
				taxRules.clear();
			}
		}
	}

    //----------------------------------------------------------------------------
    /**
        Adds a rule to the collection of tax rules.
        @param the tax rule
    **/
    //----------------------------------------------------------------------------
    public void addTaxRule(TaxRuleIfc rule)
    {
        if(taxRules == null )
        {
            taxRules = new ArrayList();
        }
        taxRules.add(rule);
    }

	public int getShippingChargeCalculationType() {
		return shippingChargeCalculationType;
	}

	public void setShippingChargeCalculationType(int shippingChargeCalculationType) {
		this.shippingChargeCalculationType = shippingChargeCalculationType;
	}     
    //----------------------------------------------------------------------------
    /**
        Returns default display string.
        @return String representation of object
    **/
    //----------------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // build result string
        StringBuilder strResult = Util.classToStringHeader("ShippingMethod",
                                    revisionNumber,
                                    hashCode());
        String baseShippingChargeString =
            getBaseShippingCharge() != null ? getBaseShippingCharge().toString() : null;
        String shippingChargeRateByWeightString =
            getShippingChargeRateByWeight() != null ? getShippingChargeRateByWeight().toString() : null;
        String flatRateString = getFlatRate() != null ? getFlatRate().toString() : null;
        String calculatedShippingChargeString =
            getCalculatedShippingCharge() != null ? getCalculatedShippingCharge().toString() : null;

        strResult.append(Util.formatToStringEntry("shipping method ID",
                                                  getShippingMethodID()))
                 .append(Util.formatToStringEntry("shipping carrier",
                                                  getLocalizedShippingCarriers()))
                 .append(Util.formatToStringEntry("shipping type",
                                                  getLocalizedShippingTypes()))
                 .append(Util.formatToStringEntry("base shipping charge",
                                                  baseShippingChargeString))
                 .append(Util.formatToStringEntry("weight based shipping charge rate",
                                                  shippingChargeRateByWeightString))
                 .append(Util.formatToStringEntry("flat rate",
                                                  flatRateString))
                 .append(Util.formatToStringEntry("shipping instructions",
                                                  getShippingInstructions()))
                 .append(Util.formatToStringEntry("calculated shipping charge",
                                                  calculatedShippingChargeString))
				 .append(Util.formatToStringEntry("taxable flag", getTaxable()))
                 .append(Util.formatToStringEntry("tax group ID", getTaxGroupID()));

        // add tax rule strings
        TaxRuleIfc[] taxRules = getTaxRules();
        if (taxRules != null)
        {
        	for (int i=0; i<taxRules.length; i++)
        	{
        		strResult.append(Util.formatToStringEntry("tax rule", taxRules[i].toString()));
        	}
        }

        // pass back result
        return(strResult.toString());
    }                                   // end toString()

    //----------------------------------------------------------------------------
    /**
        Districtnmain method.
        @param args[]  command-line parameters
    **/
    //----------------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        ShippingMethod sm = new ShippingMethod();
        // output toString()
        System.out.println(sm.toString());
    }
}
