/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/utility/TenderUtility.java /main/11 2012/02/16 14:24:43 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   02/16/12 - XbranchMerge
 *                         blarsen_bug13657203-debit-cash-back-sometimes-gives-extra-change-due-options
 *                         from rgbustores_13.4x_generic_branch
 *    blarsen   02/08/12 - Reworked getEnabledChangeOptions(). Did not handle
 *                         cases where Credit or Debit is overtendered. (As of
 *                         13.4 Debit can overtender by default for PinComm
 *                         payment app.)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:52:48 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:30:27 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:05 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:58 PM  Robert Pearse
 *
 *   Revision 1.1  2004/07/21 22:55:33  bwf
 *   @scr 5963 (ServicesImpact) Moved getChangeOptions and calculateMaxCashChange out of
 *                     abstractRetailTransaction and into TenderUtility.  Also made calculateMaxCashChange
 *                     more polymorphic.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.lineitem.TenderLineItemCategoryEnum;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;

import org.apache.log4j.Logger;

//--------------------------------------------------------------------------
/**

     $Revision: /main/11 $
 **/
//--------------------------------------------------------------------------
public class TenderUtility implements TenderUtilityIfc
{
    /** Logger */
    Logger logger =
        Logger.getLogger(oracle.retail.stores.pos.ado.utility.TenderUtility.class);

    // use for change due options
    protected TenderADOIfc[] positiveTenders = null;

    /** protected to enforce Singleton */
    protected TenderUtility() {}

    protected static TenderUtilityIfc instance = null;

    /**
     * Factory method that will attempt to return an instance of this class or
     * another implementation as specified by a property file
     *
     * @return
     */
    public static TenderUtilityIfc createInstance() throws ADOException
    {
        if (instance != null)
        {
            return instance;
        }

        final String APP_PROP_GROUP = "application";
        final String UTILITY_KEY = "ado.TenderUtility";
        final String DEFAULT = TenderUtility.class.getName();

        try
        {
            String className =
                Gateway.getProperty(APP_PROP_GROUP, UTILITY_KEY, DEFAULT);
            if (className.length() == 0)
            {
                throw new ADOException(
                    "Failed to find factory class for " + UTILITY_KEY);
            }
            Class utilityClass = Class.forName(className);
            instance = (TenderUtilityIfc) utilityClass.newInstance();
            return instance;
        }
        catch (ADOException e)
        {
            throw e;
        }
        catch (ClassNotFoundException e)
        {
            throw new ADOException(
                "Factory Class not found for " + UTILITY_KEY,
                e);
        }
        catch (InstantiationException e)
        {
            throw new ADOException(
                "Failed to Instantiate factory for " + UTILITY_KEY,
                e);
        }
        catch (IllegalAccessException e)
        {
            throw new ADOException(
                "IllegalAccessException creating factory for " + UTILITY_KEY,
                e);
        }
        catch (NullPointerException e)
        {
            throw new ADOException(
                "Failed to find class for " + UTILITY_KEY,
                e);
        }
        catch (Throwable eth)
        {
            throw new ADOException(
                "Failed to create factory for " + UTILITY_KEY,
                eth);
        }

    }

    //----------------------------------------------------------------------
    /**
        This method calculates the maximum cash change amount based on the
        tender group rules.  We add up all cash equivalent tenders and then
        take the greatest of the parameter values of the non cash equivalent
        tenders and then add them.
        @param tenderGroupMap
        @return
    **/
    //----------------------------------------------------------------------
    public CurrencyIfc calculateMaxAllowableCashChange(Map tenderGroupMap)
    {
        // for each tender, find it's maximum cash back.  Use only the greatest
        // max cash back value
        CurrencyIfc maxParamAmount = DomainGateway.getBaseCurrencyInstance();

        // This sum equiv. value is used in calculation of max limit.
        // Currently, cash equiv. tenders are: Cash, Travel Check, Mall Cert, and Money Order.
        CurrencyIfc sumCashEquivalentTender = DomainGateway.getBaseCurrencyInstance();

        // iterate through all groups
        Iterator iter = tenderGroupMap.values().iterator();
        while (iter.hasNext())
        {
            CurrencyIfc currentMaxCashChange = DomainGateway.getBaseCurrencyInstance();
            TenderGroupADOIfc group = (TenderGroupADOIfc) iter.next();
            if (group.isCashEquivalentTender())
            {
                // if a cash equivalent tender then add all tender amounts to total
                sumCashEquivalentTender = sumCashEquivalentTender.add(group.getMaxCashChange());
            }
            else
            {
                // if we use a param, then make sure we get the greatest amount
                currentMaxCashChange = group.getMaxCashChange();
                // replace maxParamAmount with new value if new value is bigger
                if (currentMaxCashChange.compareTo(maxParamAmount) == CurrencyIfc.GREATER_THAN)
                {
                    maxParamAmount = currentMaxCashChange;
                }
            }
        }

        // now calculate amount.
        CurrencyIfc result = sumCashEquivalentTender.add(maxParamAmount);
        return result;
    }

    //----------------------------------------------------------------------
    /**
     * This method determines if we have a certain tender type in the transaction.
     *
     * @param type
     * @return
     *
     * @deprecated deprecated as of 13.4.1 - dead code - repalced with hasOnlyTenders() method
     */
    //----------------------------------------------------------------------
    protected boolean hasTenderType(TenderTypeEnum type)
    {
        boolean hasTender = false;
        TenderADOIfc[] usedTenders = positiveTenders;

        for (int i = 0; i < usedTenders.length; i++)
        {
            if (usedTenders[i].getTenderType() == type)
            {
                hasTender = true;
                break;
            }
        }
        return hasTender;
    }
    //----------------------------------------------------------------------
    /**
     * This method determines if we have on tender type and not another in the transaction.
     *
     * @param hasType
     * @param notType
     * @return
     * @deprecated deprecated as of 13.4.1 - dead code - repalced with hasOnlyTenders() method
     */
    //----------------------------------------------------------------------
    protected boolean hasTenderButNotTender(TenderTypeEnum hasType, TenderTypeEnum notType)
    {
        boolean result = false;

        TenderADOIfc[] usedTenders = positiveTenders;
        boolean hasTender = false;
        boolean hasNotTender = false;
        // loop through tenders to see if we have what we want
        for (int i = 0; i < usedTenders.length; i++)
        {
            if (usedTenders[i].getTenderType() == hasType)
            {
                hasTender = true;
            }
            else if (usedTenders[i].getTenderType() == notType)
            {
                hasNotTender = true;
            }
        }

        // if has the wanted tender but doesnt have the unwanted tender
        // set result to true
        if (hasTender && !hasNotTender)
        {
            result = true;
        }

        return result;
    }

    //----------------------------------------------------------------------
    /**
     * This method determines if we only have a certain tender type in the transaction.
     *
     * @param type
     * @return
     *
     */
    //----------------------------------------------------------------------
    protected boolean hasOnlyTender(TenderTypeEnum type)
    {
        boolean hasOnlyTender = true;
        TenderADOIfc[] usedTenders = positiveTenders;

        for (int i = 0; i < usedTenders.length; i++)
        {
            if (usedTenders[i].getTenderType() != type)
            {
                hasOnlyTender = false;
                break;
            }
        }
        return hasOnlyTender;
    }

    //----------------------------------------------------------------------
    /**
     * This method determines if we only have certain tender types in the transaction.
     *
     * @param type
     * @return
     *
     */
    //----------------------------------------------------------------------
    protected boolean hasOnlyTenders(TenderTypeEnum type1, TenderTypeEnum type2)
    {
        boolean hasOnlyTenders = true;
        TenderADOIfc[] usedTenders = positiveTenders;

        for (int i = 0; i < usedTenders.length; i++)
        {
            if (usedTenders[i].getTenderType() != type1 && usedTenders[i].getTenderType() != type2)
            {
                hasOnlyTenders = false;
                break;
            }
        }
        return hasOnlyTenders;
    }

    //----------------------------------------------------------------------
    /**
        Given the internal state of the current transaction, return an array of
        tenders which are valid for change.
        @param trans
        @return
    **/
    //----------------------------------------------------------------------
    public TenderTypeEnum[] getEnabledChangeOptions(RetailTransactionADOIfc trans)
    {
        positiveTenders = trans.getTenderLineItems(TenderLineItemCategoryEnum.POSITIVE_TENDERS);

        // temporary list. initialize to a size that can hold
        // all tender types
        ArrayList<TenderTypeEnum> tenderList = new ArrayList<TenderTypeEnum>(5);

        // always allow cash
        tenderList.add(TenderTypeEnum.CASH);
        boolean cashOnlyForChangeDue = true;

        // Requirements Note: This if/else statement derives from the the POS Tender requirements doc.
        // See change due options table in the special requirements section.
        if (hasOnlyTender(TenderTypeEnum.STORE_CREDIT))
        {
            tenderList.add(TenderTypeEnum.STORE_CREDIT);
            tenderList.add(TenderTypeEnum.MAIL_CHECK);
            cashOnlyForChangeDue = false;
        }
        else if (hasOnlyTender(TenderTypeEnum.GIFT_CERT))
        {
            tenderList.add(TenderTypeEnum.GIFT_CARD);
            tenderList.add(TenderTypeEnum.GIFT_CERT);
            cashOnlyForChangeDue = false;
        }
        else if (hasOnlyTender(TenderTypeEnum.GIFT_CARD))
        {
            tenderList.add(TenderTypeEnum.GIFT_CARD);
            tenderList.add(TenderTypeEnum.STORE_CREDIT);
            tenderList.add(TenderTypeEnum.MAIL_CHECK);
            cashOnlyForChangeDue = false;
        }
        else if (hasOnlyTenders(TenderTypeEnum.GIFT_CERT, TenderTypeEnum.GIFT_CARD))
        {
            tenderList.add(TenderTypeEnum.GIFT_CARD);
            tenderList.add(TenderTypeEnum.GIFT_CERT);
            tenderList.add(TenderTypeEnum.STORE_CREDIT);
            tenderList.add(TenderTypeEnum.MAIL_CHECK);
            cashOnlyForChangeDue = false;
        }
        else if (hasOnlyTenders(TenderTypeEnum.STORE_CREDIT, TenderTypeEnum.GIFT_CARD))
        {
            tenderList.add(TenderTypeEnum.GIFT_CARD);
            tenderList.add(TenderTypeEnum.STORE_CREDIT);
            tenderList.add(TenderTypeEnum.MAIL_CHECK);
            cashOnlyForChangeDue = false;
        }
        else if (hasOnlyTenders(TenderTypeEnum.STORE_CREDIT, TenderTypeEnum.GIFT_CERT))
        {
            tenderList.add(TenderTypeEnum.GIFT_CARD);
            tenderList.add(TenderTypeEnum.GIFT_CERT);
            tenderList.add(TenderTypeEnum.STORE_CREDIT);
            tenderList.add(TenderTypeEnum.MAIL_CHECK);
            cashOnlyForChangeDue = false;
        }


        // set cash only option flag in transaction
        trans.setCashOnlyOptionForChangeDue(cashOnlyForChangeDue);

        // convert list to array
        TenderTypeEnum[] tenderTypeArray = new TenderTypeEnum[tenderList.size()];
        tenderTypeArray = (TenderTypeEnum[]) tenderList.toArray(tenderTypeArray);
        return tenderTypeArray;
    }
}
