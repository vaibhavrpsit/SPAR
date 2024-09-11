/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/group/TenderGroupMoneyOrderADO.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:43 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    masahu    03/11/09 - Refranking of Money Order, Trav Chq and Mall Cert
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         7/9/2007 6:04:18 PM    Alan N. Sinton  CR
 *         27494 - Removed constaint TenderLimitsIfc.TENDER_NO_LIMIT_AMOUNT in
 *          favor of a lazy init value returned by static method
 *         TenderLimits.getTenderNoLimitAmount().
 *    4    360Commerce 1.3         4/25/2007 8:52:49 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:30:24 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:59 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:53 PM  Robert Pearse
 *
 *   Revision 1.4  2004/07/21 22:55:33  bwf
 *   @scr 5963 (ServicesImpact) Moved getChangeOptions and calculateMaxCashChange out of
 *                     abstractRetailTransaction and into TenderUtility.  Also made calculateMaxCashChange
 *                     more polymorphic.
 *
 *   Revision 1.3  2004/06/19 17:33:32  bwf
 *   @scr 5205 These are the overhaul changes to the Change Due Options
 *                     screen and max change calculations.
 *
 *   Revision 1.2  2004/02/12 16:47:56  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.9   Feb 05 2004 13:21:04   rhafernik
 * log4j conversion
 *
 *    Rev 1.8   Jan 19 2004 17:20:08   epd
 * moved calls back to DomainGateway.
 *
 *    Rev 1.7   Jan 13 2004 13:49:14   blj
 * money rework
 *
 *    Rev 1.6   Jan 06 2004 13:12:06   epd
 * refactored away references to TenderHelper and DomainGateway
 *
 *    Rev 1.5   Jan 06 2004 10:59:26   blj
 * refactored to use getUtility()
 *
 *    Rev 1.4   18 Dec 2003 19:47:40   Tim Fritz
 * Added getMaxChangeAmount(...) method
 *
 *    Rev 1.3   Dec 08 2003 09:16:48   blj
 * code review findings.
 *
 *    Rev 1.1   Nov 07 2003 14:43:24   blj
 * cleaned up and added javadoc
 *
 *    Rev 1.0   Nov 04 2003 11:13:58   epd
 * Initial revision.
 *
 *    Rev 1.4   Nov 02 2003 20:38:50   blj
 * added a processVoid method to return cash on postvoid.
 *
 *    Rev 1.3   Oct 26 2003 14:23:38   blj
 * updated for money order tender
 *
 *    Rev 1.2   Oct 25 2003 16:07:14   blj
 * added Money Order Tender
 *
 *    Rev 1.1   Oct 24 2003 12:48:56   blj
 * MoneyOrder Tender functionality
 *
 *    Rev 1.0   Oct 21 2003 09:25:54   blj
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
 * New Tender type for POS 7.0.  Money Order tender is very similar to cash but requires
 * franking like a check.
 *
 */
public class TenderGroupMoneyOrderADO extends AbstractTenderGroupADO
{
    /**
     * @return TenderTypeEnum for MONEY_ORDER tender type
     * @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#getGroupType()
     */
    public TenderTypeEnum getGroupType()
    {
        return TenderTypeEnum.MONEY_ORDER;
    }
    //  --------------------------------------------------------------------------
    /**
        This method validates the limits of the money order.
        @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#validateLimits(java.util.HashMap, oracle.retail.stores.domain.currency.CurrencyIfc)
        @param tenderAttributes HashMap
        @param balanceDue CurrencyIfc
        @throws TenderException
    **/
    //    --------------------------------------------------------------------------
    public void validateLimits(HashMap tenderAttributes, CurrencyIfc balanceDue) throws TenderException
    {
        // evaluate that the amount is a valid amount according to the valid parameter values
        UtilityIfc util = getUtility();

        // amount of this tender
        CurrencyIfc tenderAmount = parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT));

        // test tender limits if not overridden
        if (evaluateTenderLimits == Boolean.TRUE)
        {
            // Compare tender amount to limit value
            CurrencyIfc tenderAmountTotal = getTenderTotal().add(tenderAmount);

            // Check the max amount limit
            String maxAmountStr = util.getParameterValue("MaximumMoneyOrderAmount", "99999.99");
            CurrencyIfc maxLimitValue = DomainGateway.getBaseCurrencyInstance(maxAmountStr);
            if (!maxLimitValue.equals(TenderLimits.getTenderNoLimitAmount()) &&
                tenderAmountTotal.compareTo(maxLimitValue) == CurrencyIfc.GREATER_THAN)
            {
                throw new TenderException("Money Order amount total exceeds maximum amount",
                                          TenderErrorCodeEnum.MAX_LIMIT_VIOLATED);
            }

            // Check the min amount limit
            String minAmountStr = util.getParameterValue("MinimumMoneyOrderAmount", "99999.99");
            CurrencyIfc minLimitValue = DomainGateway.getBaseCurrencyInstance(minAmountStr);
            if (!minLimitValue.equals(TenderLimits.getTenderNoLimitAmount()) &&
                tenderAmountTotal.compareTo(minLimitValue) == CurrencyIfc.LESS_THAN)
            {
                throw new TenderException("Money Order amount total exceeds minimum amount",
                                          TenderErrorCodeEnum.MIN_LIMIT_VIOLATED);
            }
        }
        else
        {
            evaluateTenderLimits = Boolean.TRUE;
        }
    }

    //----------------------------------------------------------------------
    /**
        This method determines if the tender is a cash equivalent tender.
        This is a cash equivalent tender.
        @return
        @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#isCashEquivalentTender()
    **/
    //----------------------------------------------------------------------
    public boolean isCashEquivalentTender()
    {
        return true;
    }

    //  --------------------------------------------------------------------------
    /**
        This method is a no-op for Money Order.
        @see oracle.retail.stores.ado.ADOIfc#toLegacy()
        @return null
    **/
    //  --------------------------------------------------------------------------
    public EYSDomainIfc toLegacy()
    {
        return null;
    }

    //  --------------------------------------------------------------------------
    /**
        This method is a no-op for Money Order.
        @see oracle.retail.stores.ado.ADOIfc#toLegacy(java.lang.class)
        @param type Class
        @return null
    **/
    //  --------------------------------------------------------------------------
    public EYSDomainIfc toLegacy(Class type)
    {
        return null;
    }

    //  --------------------------------------------------------------------------
    /**
        This method is a no-op for Money Order.
        @see oracle.retail.stores.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
        @param EYSDomainIfc
    **/
    //  --------------------------------------------------------------------------
    public void fromLegacy(EYSDomainIfc rdo)
    {

    }


    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#getVoidType()
     */
    public TenderTypeEnum getVoidType()
    {
        return TenderTypeEnum.MONEY_ORDER;

    }
}
