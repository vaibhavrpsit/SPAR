/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/group/TenderGroupGiftCardADO.java /rgbustores_13.4x_generic_branch/3 2011/09/20 12:30:29 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     09/19/11 - Remove the MaximumCashChangeForGiftCard parameter
 *                         and references.
 *    blarsen   07/28/11 - Removed code that determined tender limit
 *                         violations. These limits were removed as part of
 *                         13.4 Advanced Payment Foundation.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    npoola    12/16/09 - Fixed the exception code for the Gift Card amount
 *                         exceeds maximum issue amount
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         6/6/2008 3:51:17 PM    Maisa De Camargo CR
 *         31948 - Updated the validateRefundLimits method to validate the
 *         GCIssue Minimum and Max amount by Gift Card, not by the
 *         totalAmount.
 *         Code reviewed by Alan Sinton.
 *    7    360Commerce 1.6         3/31/2008 1:48:11 PM   Mathews Kochummen
 *         forward port from v12x to trunk
 *    6    360Commerce 1.5         7/9/2007 6:04:18 PM    Alan N. Sinton  CR
 *         27494 - Removed constaint TenderLimitsIfc.TENDER_NO_LIMIT_AMOUNT in
 *          favor of a lazy init value returned by static method
 *         TenderLimits.getTenderNoLimitAmount().
 *    5    360Commerce 1.4         4/25/2007 8:52:50 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    4    360Commerce 1.3         1/25/2006 4:11:51 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:30:24 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:59 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:53 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     12/7/2005 23:00:51     Deepanshu       CR
 *         3868: Get the absolute value
 *    3    360Commerce1.2         3/31/2005 15:30:24     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:25:59     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:14:53     Robert Pearse
 *
 *   Revision 1.8  2004/09/28 22:55:53  blj
 *   @scr 6650 - removed change from original 6650 scr
 *
 *   Revision 1.7  2004/09/28 20:12:25  bwf
 *   @scr 7255 Made sure not voided when getting pending void auth tenders.
 *
 *   Revision 1.6  2004/08/31 19:12:35  blj
 *   @scr 6855 - cleanup gift card credit code and fix defects found by PBY
 *
 *   Revision 1.5  2004/07/21 22:55:33  bwf
 *   @scr 5963 (ServicesImpact) Moved getChangeOptions and calculateMaxCashChange out of
 *                     abstractRetailTransaction and into TenderUtility.  Also made calculateMaxCashChange
 *                     more polymorphic.
 *
 *   Revision 1.4  2004/07/16 22:12:05  epd
 *   @scr 4268 Changing flows to add gift card credit
 *
 *   Revision 1.3  2004/04/14 22:37:53  epd
 *   @scr 4322 Tender Invariant work.  Specifically for change invariant
 *
 *   Revision 1.2  2004/02/12 16:47:56  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.11   Feb 05 2004 13:20:58   rhafernik
 * log4j conversion
 *
 *    Rev 1.10   Jan 19 2004 17:19:58   epd
 * moved calls back to DomainGateway.
 *
 *    Rev 1.9   Jan 06 2004 13:11:58   epd
 * refactored away references to TenderHelper and DomainGateway
 *
 *    Rev 1.8   Jan 06 2004 10:58:50   blj
 * removed Utility import
 *
 *    Rev 1.7   Jan 06 2004 10:57:10   blj
 * refactored to use getUtility()
 *
 *    Rev 1.6   Dec 12 2003 07:47:42   blj
 * Code review updates
 *
 *    Rev 1.5   Nov 24 2003 16:27:40   blj
 * implemented recalculateTransactionTotal method, added reversal and voidauth capability to gift card tender.
 *
 *    Rev 1.4   Nov 19 2003 22:11:52   blj
 * added code for gift card tender using ado design
 *
 *    Rev 1.1   Nov 11 2003 13:44:38   blj
 * updated with max gift card tender limits.
 *
 *    Rev 1.2   Oct 30 2003 20:39:56   epd
 * removed authorize() method
 *
 *    Rev 1.1   Oct 27 2003 20:49:08   epd
 * renamed interface
 *
 *    Rev 1.0   Oct 17 2003 12:34:34   epd
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderLimits;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.pos.ado.tender.ReversibleTenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderGiftCardADO;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;

/**
 *
 */
public class TenderGroupGiftCardADO extends AbstractAuthorizableTenderGroupADO
{
    /*
     * @see oracle.retail.stores.ado.tender.group.TenderGroupADOIfc#getGroupType()
     */
    public TenderTypeEnum getGroupType()
    {
        return TenderTypeEnum.GIFT_CARD;
    }

    /*
     * @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#getVoidType()
     */
    public TenderTypeEnum getVoidType()
    {
        return getGroupType();
    }

    /*
     * @see oracle.retail.stores.ado.tender.group.TenderGroupADOIfc#validateLimits(java.util.HashMap, oracle.retail.stores.domain.currency.CurrencyIfc)
     */
    public void validateLimits(HashMap tenderAttributes, CurrencyIfc balanceDue) throws TenderException
    {
        // limits removed in 13.4 - do nothing
    }


    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.tender.group.AbstractTenderGroupADO#validateRefundLimits(java.util.HashMap, boolean)
     */
    public void validateRefundLimits(HashMap tenderAttributes, boolean hasReceipt, boolean retrieved) throws TenderException
    {
        // evaluate that the amount is a valid amount according to the valid parameter values
        UtilityIfc util = getUtility();

        // amount of this tender
        CurrencyIfc tenderAmount = parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT)).abs();

        // test tender limits if not overridden
        if (evaluateTenderLimits == Boolean.TRUE)
        {
            // Check the min issue amount limit
            String minIssueAmountStr = util.getParameterValue("MinimumGiftCardIssueAmount", "5.00");
            CurrencyIfc minIssueAmount = DomainGateway.getBaseCurrencyInstance(minIssueAmountStr);
            if (!minIssueAmount.equals(TenderLimits.getTenderNoLimitAmount()) &&
                    tenderAmount.compareTo(minIssueAmount) == CurrencyIfc.LESS_THAN)
            {
                throw new TenderException("Gift Card amount less than minimum issue amount",
                                          TenderErrorCodeEnum.MIN_LIMIT_VIOLATED);
            }
            // Check the max issue amount limit
            String maxIssueAmountStr = util.getParameterValue("MaximumGiftCardIssueAmount", "500.00");
            CurrencyIfc maxIssueAmount = DomainGateway.getBaseCurrencyInstance(maxIssueAmountStr);
            if (!maxIssueAmount.equals(TenderLimits.getTenderNoLimitAmount()) &&
                    tenderAmount.compareTo(maxIssueAmount) == CurrencyIfc.GREATER_THAN)
            {
                throw new TenderException("Gift Card amount exceeds maximum issue amount",
                                          TenderErrorCodeEnum.MAX_LIMIT_VIOLATED);
            }
        }
    }

    /**
     * This method sums up all the cash change components for each gift card and
     * returns the total
     * @return
     */
    public CurrencyIfc getCashChangeTotal()
    {
        CurrencyIfc result = DomainGateway.getBaseCurrencyInstance();
        Iterator iter = tenderList.iterator();
        while (iter.hasNext())
        {
            TenderGiftCardADO tender = (TenderGiftCardADO) iter.next();
            String cashChangeStr = (String)tender.getTenderAttributes().get(TenderConstants.CASH_CHANGE_AMOUNT);
            result = result.add(DomainGateway.getBaseCurrencyInstance(cashChangeStr));
        }
        return result;
    }

    //----------------------------------------------------------------------
    /**
        This method gets the amount of cash change which is the parameter
        amount for this tender, unless it is not overtenderable.
        @return
        @see oracle.retail.stores.pos.ado.tender.group.AbstractTenderGroupADO#getMaxCashChange()
    **/
    //----------------------------------------------------------------------
    public CurrencyIfc getMaxCashChange()
    {
        CurrencyIfc maxCashChange = DomainGateway.getBaseCurrencyInstance();
        return maxCashChange;
    }

    /**
     * Pulls all the tenders from a named from and adds them to a supplied list.
     *
     * @param tenderList
     * @param group
     */
    public List pullAuthPendingTendersFromGroup()
    {
        // iterate through tenders in group and only get tenders
        // needing authorization
        // Note: GiftCard tenders are not authorized during a refund(giftcardcredit)
        // transaction.
        TenderADOIfc[] tenders = getTenders();
        List authTenders = new ArrayList();
        for (int i = 0; i < tenders.length; i++)
        {
            boolean isGiftCardCredit = false;

            isGiftCardCredit = ((TenderGiftCardADO)tenders[i]).isGiftCardCredit();

            if (!isGiftCardCredit && !((TenderGiftCardADO)tenders[i]).isAuthorized())
            {
                authTenders.add(tenders[i]);
            }
        }
        return authTenders;
    }

    /**
     * Returns an array of all tenders still requiring authorization.
     *
     * @return
     */
    public List getVoidAuthPendingTenderLineItems()
    {
        // iterate through tenders in group and only get tenders
        // needing authorization
        TenderADOIfc[] tenders = getTenders();
        List voidtenders = new ArrayList();
        for (int i = 0; i < tenders.length; i++)
        {
            boolean isGiftCardCredit = false;

            isGiftCardCredit = ((TenderGiftCardADO)tenders[i]).isGiftCardCredit();

            if (!isGiftCardCredit && !((ReversibleTenderADOIfc) tenders[i]).isVoided())
            {
                voidtenders.add(tenders[i]);
            }
        }
        return voidtenders;
    }

    /*
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy()
     */
    public EYSDomainIfc toLegacy()
    {
        return null;
    }
    /*
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy(java.lang.Class)
     */
    public EYSDomainIfc toLegacy(Class type)
    {
        return null;
    }
    /*
     * @see oracle.retail.stores.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
     */
    public void fromLegacy(EYSDomainIfc rdo)
    {

    }
    /**
     * Indicates whether this group aggregates a tender type that
     * requires a reversal when removed from the transaction.
     * @return
     */
    public boolean isReversible()
    {
        return true;
    }
}
