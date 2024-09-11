/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/group/TenderGroupCashADO.java /rgbustores_13.4x_generic_branch/2 2011/08/19 12:16:05 rrkohli Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rrkohli   08/19/11 - fix for 'POS - Return - Able to complete cash refund
 *                         for amount greater than the Maximum Cash Refund
 *                         value'
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  02/21/09 - Fixed maximum cash override dialog box for split
 *                         redeem tender
 *
 * ===========================================================================
 * $Log:
 *    9    360Commerce 1.8         3/31/2008 1:47:35 PM   Mathews Kochummen
 *         forward port from v12x to trunk
 *    8    360Commerce 1.7         7/9/2007 6:04:18 PM    Alan N. Sinton  CR
 *         27494 - Removed constaint TenderLimitsIfc.TENDER_NO_LIMIT_AMOUNT in
 *          favor of a lazy init value returned by static method
 *         TenderLimits.getTenderNoLimitAmount().
 *    7    360Commerce 1.6         4/25/2007 8:52:52 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    6    360Commerce 1.5         2/20/2006 1:23:43 PM   Brett J. Larsen CR
 *         8450 - adding back original validateChangeLimits method and making
 *         it deprecated (note the original method still contains the bug
 *         which caused CR 8450)
 *    5    360Commerce 1.4         2/16/2006 11:01:14 AM  Brett J. Larsen CR
 *         8450 - max over change dialog not being presented at appropriate
 *         times - fix logic from previous CR which did not consider
 *         cashOnlyForChangeDue value
 *    4    360Commerce 1.3         1/25/2006 4:11:51 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:30:24 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:59 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:53 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/28/2005 21:52:49    Deepanshu       CR
 *         3872: Check the Tender amount for both - ?Maximum Calculated Cash
 *         Change? or ?Maximum Cash Change? for maximum limit
 *    3    360Commerce1.2         3/31/2005 15:30:24     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:25:59     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:14:53     Robert Pearse
 *
 *   Revision 1.9  2004/07/21 22:55:33  bwf
 *   @scr 5963 (ServicesImpact) Moved getChangeOptions and calculateMaxCashChange out of
 *                     abstractRetailTransaction and into TenderUtility.  Also made calculateMaxCashChange
 *                     more polymorphic.
 *
 *   Revision 1.8  2004/06/22 16:24:23  bwf
 *   @scr 5764
 *   @scr 5763 Calculate max cash change based on correct amount.
 *
 *   Revision 1.7  2004/06/19 17:33:32  bwf
 *   @scr 5205 These are the overhaul changes to the Change Due Options
 *                     screen and max change calculations.
 *
 *   Revision 1.6  2004/06/15 21:54:45  bwf
 *   @scr 5476 Fixed validateRefundLimits so that redeems dont crash when
 *                     using mbc or credit.  Use correct error message for cash.
 *
 *   Revision 1.5  2004/03/16 18:30:46  cdb
 *   @scr 0 Removed tabs from all java source code.
 *
 *   Revision 1.4  2004/02/23 20:02:18  aschenk
 *   @scr 2938.  If statements in ValidateLimits were switched to throw correct exception when both conditions are met.
 *
 *   Revision 1.3  2004/02/19 15:22:31  tfritz
 *   @scr 2938 - Fixed defect
 *
 *   Revision 1.2  2004/02/12 16:47:56  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.5   Feb 06 2004 12:18:18   Tim Fritz
 * Changed the location of this check.
 * Resolution for 2938: Wrong error msg appears when cash change > Maximum Cash Change
 *
 *    Rev 1.4   Feb 05 2004 13:20:50   rhafernik
 * log4j conversion
 *
 *    Rev 1.3   Jan 19 2004 17:19:54   epd
 * moved calls back to DomainGateway.
 *
 *    Rev 1.2   Jan 06 2004 13:11:48   epd
 * refactored away references to TenderHelper and DomainGateway
 *
 *    Rev 1.1   Nov 14 2003 11:09:40   epd
 * refactored some void functionality to be more general.
 *
 *    Rev 1.0   Nov 04 2003 11:13:54   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 17 2003 12:34:30   epd
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender.group;

import java.util.HashMap;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderLimits;
import oracle.retail.stores.domain.utility.EYSDomainIfc;

/**
 *
 *
 */
public class TenderGroupCashADO extends AbstractTenderGroupADO
{
    /**
     * @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#getGroupType()
     */
    public TenderTypeEnum getGroupType()
    {
        return TenderTypeEnum.CASH;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#getVoidType()
     */
    public TenderTypeEnum getVoidType()
    {
        return getGroupType();
    }

    /**
     * Validate the tender limits for the test cash tender
     * @see oracle.retail.stores.pos.ado.tender.group.AbstractTenderGroupADO#validateLimits(oracle.retail.stores.pos.ado.tender.TenderADOIfc)
     */
    public void validateLimits(HashMap tenderAttributes, CurrencyIfc balanceDue) throws TenderException
    {
        if (evaluateTenderLimits == Boolean.TRUE && balanceDue.signum()!= CurrencyIfc.NEGATIVE)
        {
            // TODO: uncomment when moved to JDK1.4
            //assert(tenderAttributes.get(TenderConstants.TENDER_TYPE) != null);

            // Compare tender amount total of this group to limit value
            CurrencyIfc tenderAmount = parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT));
            CurrencyIfc tenderAmountTotal = getTenderTotal().add(tenderAmount);

            UtilityIfc util = getUtility();
            // default value from requirements
            String maxCashStr = util.getParameterValue("MaximumCashAccepted", "1000.00");
            CurrencyIfc limitValue = DomainGateway.getBaseCurrencyInstance(maxCashStr);

            //compare the max amount to the tender amount
            if (!limitValue.equals(TenderLimits.getTenderNoLimitAmount()) &&
                tenderAmountTotal.compareTo(limitValue) == CurrencyIfc.GREATER_THAN)
            {
                throw new TenderException("Cash amount exceeds maximum amount",
                        TenderErrorCodeEnum.MAX_LIMIT_VIOLATED);
            }
        }
        else
        {
            // reset boolean flag
            evaluateTenderLimits = Boolean.TRUE;
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.tender.group.TenderGroupADOIfc#validateRefundLimits(java.util.HashMap, boolean, boolean)
     */
    public void validateRefundLimits(HashMap tenderAttributes, boolean hasReceipt, boolean retrieved) throws TenderException
    {
        if (evaluateTenderLimits == Boolean.TRUE)
        {
            UtilityIfc util = getUtility();
            String maxCashRefundStr = "";
            // get maximum amount depending on whether a receipt is present or not
            // and also if the original transaction is successfully retrieved
            if (hasReceipt && retrieved)
            {
                maxCashRefundStr = util.getParameterValue("MaximumCashRefund", "100.00");
            }
            else
            {
                maxCashRefundStr = util.getParameterValue("MaximumCashRefundWithoutReceipt", "20.00");
            }
            // compare tender amount to limit value (take absolute value of tender amount)
            CurrencyIfc tenderAmount = parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT));
            CurrencyIfc tenderAmountTotal = getTenderTotal().add(tenderAmount).abs();
            CurrencyIfc limitValue = DomainGateway.getBaseCurrencyInstance(maxCashRefundStr);
            if (!limitValue.equals(TenderLimits.getTenderNoLimitAmount()) &&
                tenderAmountTotal.compareTo(limitValue) == CurrencyIfc.GREATER_THAN)
            {
                throw new TenderException("Cash refund amount exceeds maximum amount",
                                          TenderErrorCodeEnum.MAX_LIMIT_VIOLATED);
            }
        }
        else
        {
            // reset tender limit check to true
            evaluateTenderLimits = Boolean.TRUE;
        }
    }

    //----------------------------------------------------------------------
    /**
        This method validates the redeem limits for cash.
        @param tenderAttributes
        @param balanceDue
        @throws TenderException
    **/
    //----------------------------------------------------------------------
    public void validateRedeemLimits(HashMap tenderAttributes, CurrencyIfc balanceDue) throws TenderException
    {
        if (evaluateTenderLimits == Boolean.TRUE)
        {
            // overtendering in a return is not allowed
            CurrencyIfc tenderAmount =
                DomainGateway.getBaseCurrencyInstance((String) tenderAttributes.get(TenderConstants.AMOUNT)).abs();
            if (tenderAmount.compareTo(balanceDue.abs()) == CurrencyIfc.GREATER_THAN)
            {
                throw new TenderException("Overtender not allowed in a redeem", TenderErrorCodeEnum.OVERTENDER_ILLEGAL);
            }

            UtilityIfc util = getUtility();
            CurrencyIfc tenderAmountTotal = getTenderTotal().abs();
            tenderAmountTotal = tenderAmountTotal.add(tenderAmount);
            CurrencyIfc maxParamAmount = DomainGateway.getBaseCurrencyInstance();
            String maxParamAmountStr = util.getParameterValue("MaximumCashBackforRedeem", "0.00");
            maxParamAmount = DomainGateway.getBaseCurrencyInstance(maxParamAmountStr);
            CurrencyIfc tenderAmt = parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT)).abs();
            if(tenderAmountTotal.compareTo(maxParamAmount) == CurrencyIfc.GREATER_THAN)
            {
                throw new TenderException("More than maximum cash redemption", TenderErrorCodeEnum.MAX_CHANGE_LIMIT_VIOLATED);
            }
        }
        else
        {
            // reset tender limit check to true
            evaluateTenderLimits = Boolean.TRUE;
        }
    }

    //----------------------------------------------------------------------
    /**
        This method validates the change limits for cash.
        @param tenderAttributes
        @param maxCalculatedCashChange
        @deprecated as of release 1.0.0.35 - this method's calculations are incorrect - use validateChangeLimits() which accepts the cashOnlyForChangeDue flag
        @throws TenderException
    **/
    //----------------------------------------------------------------------
    public void validateChangeLimits(CurrencyIfc tenderAmount, CurrencyIfc maxCalculatedCashChange) throws TenderException
    {
        if (evaluateTenderLimits == Boolean.TRUE)
        {
            UtilityIfc util = getUtility();
            String maxParamAmountStr = util.getParameterValue("MaximumCashChange", "0.00");
            CurrencyIfc maxParamAmount = DomainGateway.getBaseCurrencyInstance(maxParamAmountStr);
            if(tenderAmount.compareTo(maxCalculatedCashChange) == CurrencyIfc.GREATER_THAN &&
               tenderAmount.compareTo(maxParamAmount) == CurrencyIfc.GREATER_THAN)
            {
                throw new TenderException("More than maximum cash allowed for change", TenderErrorCodeEnum.MAX_CHANGE_LIMIT_VIOLATED);
            }
        }
        else
        {
            // reset tender limit check to true
            evaluateTenderLimits = Boolean.TRUE;
        }
    }

    //----------------------------------------------------------------------
    /**
        This method validates the change limits for cash.
        @param tenderAttributes
        @param maxCalculatedCashChange
        @throws TenderException
    **/
    //----------------------------------------------------------------------
    public void validateChangeLimits(CurrencyIfc tenderAmount, CurrencyIfc maxCalculatedCashChange, boolean cashOnlyForChangeDue) throws TenderException
    {
        if (evaluateTenderLimits == Boolean.TRUE)
        {
            UtilityIfc util = getUtility();
            String maxParamAmountStr = util.getParameterValue("MaximumCashChange", "0.00");
            CurrencyIfc maxParamAmount = DomainGateway.getBaseCurrencyInstance(maxParamAmountStr);
            if ((!cashOnlyForChangeDue &&
                    tenderAmount.compareTo(maxCalculatedCashChange) == CurrencyIfc.GREATER_THAN) ||
                (cashOnlyForChangeDue &&
                    tenderAmount.compareTo(maxParamAmount) == CurrencyIfc.GREATER_THAN))
            {
                throw new TenderException("More than maximum cash allowed for change", TenderErrorCodeEnum.MAX_CHANGE_LIMIT_VIOLATED);
            }
        }
        else
        {
            // reset tender limit check to true
            evaluateTenderLimits = Boolean.TRUE;
        }
    }

    //----------------------------------------------------------------------
    /**
        Determines if tender is a cash equivalent tender.
        Cash IS a cash equivalent tender.
        @return
        @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#isCashEquivalentTender()
    **/
    //----------------------------------------------------------------------
    public boolean isCashEquivalentTender()
    {
        return true;
    }
    /**
     * @see oracle.retail.stores.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
     */
    public void fromLegacy(EYSDomainIfc rdo)
    {
        // TODO Auto-generated method stub
    }

    /**
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy()
     */
    public EYSDomainIfc toLegacy()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy(java.lang.Class)
     */
    public EYSDomainIfc toLegacy(Class type)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
