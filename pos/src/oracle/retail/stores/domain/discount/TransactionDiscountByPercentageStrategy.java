/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/TransactionDiscountByPercentageStrategy.java /main/22 2013/12/17 16:08:05 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  12/17/13 - fix misused calls to Boolean.getBoolean()
 *    asinton   02/05/13 - Fixed an ejournal issue where precentage was not
 *                         correctly indicated
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    mkutiana  08/08/11 - Added the employeeId to the EJ for Employee Discount
 *                         based transactions
 *    cgreene   11/10/10 - update toJournalString method to print significant
 *                         digits of fractional discounts
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    deghosh   02/12/09 - Cleaning the deprecated method toJournalString()
 *    deghosh   12/23/08 - EJ i18n changes
 *    vchengeg  12/16/08 - ej defect fixes
 *    deghosh   12/08/08 - EJ i18n changes
 *    acadar    11/03/08 - localization of reason codes for discounts and
 *                         merging to tip
 *    akandru   10/31/08 - EJ Changes_I18n
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/25/2007 10:00:59 AM  Anda D. Cadar   I18N
 *         merge
 *    4    360Commerce 1.3         1/22/2006 11:41:28 AM  Ron W. Haight
 *         Removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:30:34 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:21 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:13 PM  Robert Pearse
 *
 *   Revision 1.3  2004/02/12 17:13:28  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:27  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:35:08   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:50:12   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:58:32   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:18:46   msg
 * Initial revision.
 *
 *    Rev 1.2   Feb 23 2002 10:31:22   mpm
 * Modified Util.BIG_DECIMAL to Util.I_BIG_DECIMAL, Util.ROUND_HALF to Util.I_ROUND_HALF.
 * Resolution for Domain SCR-35: Accept Foundation BigDecimal backward-compatibility changes
 *
 *    Rev 1.1   Feb 05 2002 16:34:24   mpm
 * Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 *
 *    Rev 1.0   Sep 20 2001 16:13:04   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:36:36   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.discount;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
import oracle.retail.stores.foundation.utility.xml.XMLConverterIfc;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import org.w3c.dom.Element;

/**
 * Discount by percentage strategy.
 * 
 * @see oracle.retail.stores.domain.transaction.TransactionDiscountStrategy
 * @see oracle.retail.stores.domain.transaction.TransactionDiscountByPercentageIfc
 * @version $Revision: /main/22 $
 */
public class TransactionDiscountByPercentageStrategy extends TransactionDiscountStrategy implements
        TransactionDiscountByPercentageIfc
        
{
    private static final long serialVersionUID = 58586694737977040L;

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/22 $";

    /**
     * Constructs TransactionDiscountByPercentageStrategy object.
     */
    protected String employeeCompanyName="";
    public TransactionDiscountByPercentageStrategy()
    {
    }

    /**
     * Constructs TransactionDiscountByPercentageStrategy object, setting rate
     * and reason code attributes.
     * 
     * @param rate discount rate
     * @param reason code
     * @deprecated as of 13.1. Use {@link
     *             TransactionDiscountByPercentageStrategy(BigDecimal rate,
     *             LocalizedCodeIfc reason)}
     */
    public TransactionDiscountByPercentageStrategy(BigDecimal rate, int reason)
    {
        discountRate = rate;
        reasonCode = reason;
        this.reason.setCode(Integer.toString(reason));
    }

    /**
     * Constructs TransactionDiscountByPercentageStrategy object, setting rate
     * and reason code attributes.
     * 
     * @param rate discount rate
     * @param reason code
     */
    public TransactionDiscountByPercentageStrategy(BigDecimal rate, LocalizedCodeIfc reason)
    {
        discountRate = rate;
        this.reason = reason;
    }

    /**
     * Calculate, return discount amount for transaction.
     * 
     * @param remainderTotal remainder of total (used for pro-ration)
     * @return discount amount
     */
    public CurrencyIfc calculateTransactionDiscountTotal(CurrencyIfc remainderTotal)
    {
        return remainderTotal.multiply(getDiscountRate());
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.TransactionDiscountStrategy#clone()
     */
    @Override
    public Object clone()
    {
        TransactionDiscountByPercentageIfc newClass = new TransactionDiscountByPercentageStrategy();
        setCloneAttributes(newClass);
        return newClass;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.TransactionDiscountStrategy#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof TransactionDiscountByPercentageStrategy)?
                super.equals(obj) : false;
    }

    /**
     * Retrieves discount method.
     * 
     * @return method discount method
     */
    public int getDiscountMethod()
    {
        return DISCOUNT_METHOD_PERCENTAGE;
    }

    /**
     * Retrieves discount scope.
     * 
     * @return scope discount scope
     */
    public int getDiscountScope()
    {
        return DISCOUNT_SCOPE_TRANSACTION;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.DiscountRule#toString()
     */
    @Override
    public String toString()
    {
        // result string
        StringBuilder strResult = new StringBuilder("Class:  ");
        strResult.append("TransactionDiscountByPercentageStrategy (Revision ")
            .append(getRevisionNumber()).append(") @").append(hashCode()).append(Util.EOL);
        if (getDiscountRate() == null)
        {
            strResult.append("discountRate:                       [null]").append(Util.EOL);
        }
        else
        {
            strResult.append("discountRate:                       [").append(getDiscountRate()).append("]").append(Util.EOL);
        }

        // pass back result
        return strResult.toString();
    }

    /**
     * Returns journal string for this object.
     * 
     * @return journal string
     * @deprecated as of 13.1. New method added which takes the client's journal
     *             locale
     */
    public String toJournalString()
    {

        return toJournalString(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
    }

    /**
     * Returns journal string for this object.
     * 
     * @param journalLocale client's journal locale
     * @return journal string
     */
    public String toJournalString(Locale journalLocale)
    {
        Object[] dataArgs = new Object[] { "" };
        StringBuilder strResult = new StringBuilder(Util.EOL);
        strResult.append(Util.EOL).append(
                I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.TRANS_DISCOUNT_TAG_LABEL,
                        null, journalLocale)).append(Util.EOL);

        try
        {
            // convert e.g. 0.25 to 25%
            BigDecimal percentageAmount = getDiscountRate().multiply(BigDecimal.valueOf(100));
            // try to get number as an integer, not a decimal. Throws ArithmeticException
            BigInteger discountInteger = percentageAmount.toBigIntegerExact();
            dataArgs[0] = discountInteger.toString();
        }
        catch (ArithmeticException e)
        {
            // for discounts with fractional amounts, include the significant digits.
            BigDecimal discountDecimal = getDiscountRate();
            discountDecimal = discountDecimal.movePointRight(2);
            discountDecimal = discountDecimal.stripTrailingZeros();
            dataArgs[0] = discountDecimal.toString();
        }

        strResult.append(
                I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.DISCOUNT_TAG_LABEL, dataArgs,
                        journalLocale)).append(Util.EOL);

        dataArgs[0] = (getReason().getText(journalLocale) == null) ? getReason().getCode()
                : getReason().getText(journalLocale);
        strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.DISCOUNT_RSN_TAG_LABEL, dataArgs, journalLocale));
        
        if (getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE)
        {
            strResult.append(Util.EOL);
            dataArgs[0]=discountEmployee.getEmployeeID();
            strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.EMPLOYEE_ID, dataArgs,journalLocale));
        }

        return strResult.toString();
    }

    /**
     * Restores the object from the contents of the xml tree based on the
     * current node property of the converter.
     * 
     * @param converter is the conversion utility
     * @exception XMLConversionException if error occurs translating XML
     * @deprecated as of 13.1. No callers.
     */
    public void translateFromElement(XMLConverterIfc converter) throws XMLConversionException
    {
        Element top = converter.getCurrentElement();
        Element[] properties = converter.getChildElements(top,XMLConverterIfc.TAG_PROPERTY);

        // Retrieve and store the values for each property
        for (int i = 0; i < properties.length; i++)
        {
            Element element = properties[i];
            String name = element.getAttribute("name");

            if ("discountRate".equals(name))
            {
                discountRate = (BigDecimal) converter.getPropertyObject(element);
            }
            else if ("enabled".equals(name))
            {
                enabled = Boolean.valueOf(converter.getElementText(element));
            }
            else if ("reasonCode".equals(name))
            {
                reasonCode = Integer.parseInt(converter.getElementText(element));
            }
        }
    }

	// below code is added bya tul shukla
	 public  void setEmployeeCompanyName(String employeeCompanyName)
	 {
		 this.employeeCompanyName=employeeCompanyName;
	 }
	  public String getEmployeeCompanyName() {
		return employeeCompanyName;
	}
    
    /**
     * Returns the revision number of this class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return revisionNumber;
    }
}
