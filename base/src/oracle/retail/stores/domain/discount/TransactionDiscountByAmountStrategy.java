/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/TransactionDiscountByAmountStrategy.java /main/18 2013/12/17 16:08:05 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  12/17/13 - fix misused calls to Boolean.getBoolean()
 *    cgreene   05/26/10 - convert to oracle packaging
 *    npoola    03/08/10 - changed the DiscountLabel Key to print the correct
 *                         data
 *    abondala  01/03/10 - update header date
 *    deghosh   02/12/09 - Cleaning the deprecated method toJournalString()
 *    deghosh   12/23/08 - EJ i18n changes
 *    vchengeg  12/16/08 - ej defect fixes
 *    deghosh   12/08/08 - EJ i18n changes
 *    acadar    11/03/08 - localization of reason codes for discounts and
 *                         merging to tip
 *    acadar    11/02/08 - cleanup
 *    acadar    11/02/08 - updates to unit tests
 *    acadar    10/30/08 - use localized reason codes for item and transaction
 *                         discounts
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 10:00:59 AM  Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:30:34 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:20 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:12 PM  Robert Pearse
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
 *    Rev 1.1   Dec 10 2003 10:11:28   rrn
 * In string method, changed "Removed" to "Deleted".
 * Resolution for 3506: Journal format changes
 *
 *    Rev 1.0   Aug 29 2003 15:35:06   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:50:10   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:58:28   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:18:44   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 16:13:00   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:36:36   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.discount;
// java imports
import java.util.Locale;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import org.w3c.dom.Element;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
import oracle.retail.stores.foundation.utility.xml.XMLConverterIfc;

//------------------------------------------------------------------------------
/**
    Discount by amount strategy. <P>
        @see oracle.retail.stores.domain.transaction.TransactionDiscountStrategy
        @see oracle.retail.stores.domain.transaction.TransactionDiscountByAmountIfc
        @version $Revision: /main/18 $
**/
//------------------------------------------------------------------------------
public class TransactionDiscountByAmountStrategy
extends TransactionDiscountStrategy
implements TransactionDiscountByAmountIfc
{
    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /main/18 $";

    //---------------------------------------------------------------------
    /**
        Constructs TransactionDiscountByAmountStrategy object. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
    **/
    //---------------------------------------------------------------------
    public TransactionDiscountByAmountStrategy()
    {

    }

    //---------------------------------------------------------------------
    /**
        Constructs TransactionDiscountByAmountStrategy object,
        setting amount and reason code attributes. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param amount discount amount
        @param reason code
        @deprecated as of 13.1. Use {@link TransactionDiscountByAmountStrategy(CurrencyIfc amount, LocalizedCodeIfc reason)}
    **/
    //---------------------------------------------------------------------
    public TransactionDiscountByAmountStrategy(CurrencyIfc amount, int reason)
    {
        discountAmount  = amount;
        reasonCode      = reason;
        this.reason.setCode(Integer.toString(reason));
    }

    /**
     * Constructs TransactionDiscountByAmountStrategy object,
     * @param amount discount amount
     * @param reason code
     */
    public TransactionDiscountByAmountStrategy(CurrencyIfc amount, LocalizedCodeIfc reason)
    {
        discountAmount  = amount;
        this.reason      = reason;
    }

    //---------------------------------------------------------------------
    /**
        Calculate, return discount amount for transaction. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>Discount amount is calculated and returned.
        </UL>
        @param remainderTotal remainder of total (used for pro-ration)
        @return discount amount
    **/
    //---------------------------------------------------------------------
    public CurrencyIfc calculateTransactionDiscountTotal(CurrencyIfc remainderTotal)
    {
        return(discountAmount);
    }

    //---------------------------------------------------------------------
    /**
        Clone this object. <P>
        @return generic object copy of this object
    **/
    //---------------------------------------------------------------------
    public Object clone()
    {
        TransactionDiscountByAmountIfc
            newClass = new TransactionDiscountByAmountStrategy();
        setCloneAttributes(newClass);
        return newClass;
    }

    //---------------------------------------------------------------------
    /**
        Determine if two objects are identical. <P>
        @param obj object to compare with
        @return true if the objects are identical, false otherwise
    **/
    //---------------------------------------------------------------------
    public boolean equals(Object obj)
    {
        return (obj instanceof TransactionDiscountByAmountStrategy)
                                  ?
                    super.equals(obj) : false;
    }

    //---------------------------------------------------------------------
    /**
        Retrieves discount method. <P>
        @return method discount method
    **/
    //---------------------------------------------------------------------
    public int getDiscountMethod()
    {
        return DISCOUNT_METHOD_AMOUNT;
    }

    //---------------------------------------------------------------------
    /**
        Retrieves discount scope. <P>
        @return scope discount scope
    **/
    //---------------------------------------------------------------------
    public int getDiscountScope()
    {
        return DISCOUNT_SCOPE_TRANSACTION;
    }

    //---------------------------------------------------------------------
    /**
        Method to default display string function. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        StringBuffer strResult =
          new StringBuffer("Class:  TransactionDiscountByAmountStrategyStrategy (Revision ");
        strResult.append(getRevisionNumber()).append(") @").append(hashCode());
        if (discountAmount == null)
        {
            strResult.append("discountAmount:                     [null]\n");
        }
        else
        {
            strResult.append("discountAmount:                     [").
              append(discountAmount).append("]\n");
        }

        // pass back result
        return(strResult.toString());
    }                                  // end toString()

    //---------------------------------------------------------------------
    /**
        Returns journal string for this object. <P>
        @return journal string
        @deprecated new method added to take the client's journal locale
    **/
    //---------------------------------------------------------------------
    public String toJournalString()
    {

        return(toJournalString(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)));
    }

    /**
        Returns journal string for this object. <P>
        @return journal string
    **/
    public String toJournalString(Locale journalLocale)
    {
        CurrencyIfc discAmt = getDiscountAmount();
        String discAmtString = discAmt.toFormattedString();
        StringBuffer strResult = new StringBuffer();
        strResult.append(Util.EOL).append(Util.EOL);
        Object[] dataArgs = new Object[]{""};

        strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.TRANS_DISCOUNT, new Object[]{discAmtString},
                journalLocale))
         .append(Util.EOL)
         .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.DISCOUNT_AMT, null,
                journalLocale));
        
        if(discAmt.getDoubleValue() < 0.0)
        {
            strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
     				JournalConstantsIfc.DELETED_LABEL, null,
    				journalLocale));
        }

        String reasonCode = (getReason().getText(journalLocale) == null)? getReason().getCode() : getReason().getText(journalLocale);
        dataArgs[0] = reasonCode;
        strResult.append(Util.EOL)
                 .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
          				JournalConstantsIfc.DISCOUNT_RSN_TAG_LABEL, dataArgs,
        				journalLocale));
        return(strResult.toString());
    }

    //---------------------------------------------------------------------
    /**
        Returns the revision number of this class. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                  // end getRevisionNumber()

    //---------------------------------------------------------------------
    /**
         Restores the object from the contents of the xml tree based on the
         current node property of the converter.
         @param converter is the conversion utility
         @exception XMLConversionException if error occurs translating XML
         @deprecated as of 13.1. No callers.
    **/
    //---------------------------------------------------------------------
    public void translateFromElement(XMLConverterIfc converter) throws XMLConversionException
    {
        Element top = converter.getCurrentElement();
        Element[] properties = converter.getChildElements(top,XMLConverterIfc.TAG_PROPERTY);

        // Retrieve and store the values for each property
        for (int i = 0; i < properties.length; i++)
        {
            Element element = properties[i];
            String name = element.getAttribute("name");

            if ("discountAmount".equals(name))
            {
                discountAmount = (CurrencyIfc) converter.getPropertyObject(element);
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

    //---------------------------------------------------------------------
    /**
        TransactionDiscountByAmountStrategyStrategy main method. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>toString() output
        </UL>
        @param String args[]  command-line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        TransactionDiscountByAmountStrategy obj = new TransactionDiscountByAmountStrategy();
        // output toString()
        System.out.println(obj.toString());
    }                                  // end main()
}                          // end class TransactionDiscountByAmountStrategy
