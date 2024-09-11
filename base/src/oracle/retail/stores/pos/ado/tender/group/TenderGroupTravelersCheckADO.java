/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/group/TenderGroupTravelersCheckADO.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:44 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    masahu    03/11/09 - Refranking of Money Order, Trav Chq and Mall Cert
 *    kulu      02/22/09 - Fix the bug that foreign currency in tender info is
 *                         always CAD
 *
 * ===========================================================================
 * $Log:
 *    7    360Commerce 1.6         7/9/2007 6:04:18 PM    Alan N. Sinton  CR
 *         27494 - Removed constaint TenderLimitsIfc.TENDER_NO_LIMIT_AMOUNT in
 *          favor of a lazy init value returned by static method
 *         TenderLimits.getTenderNoLimitAmount().
 *    6    360Commerce 1.5         4/25/2007 8:52:49 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    5    360Commerce 1.4         5/12/2006 5:25:26 PM   Charles D. Baker
 *         Merging with v1_0_0_53 of Returns Managament
 *    4    360Commerce 1.3         1/22/2006 11:45:00 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:30:24 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:59 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:54 PM  Robert Pearse
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
 *    Rev 1.8   Feb 06 2004 10:19:26   rsachdeva
 * Alternate Currency
 * Resolution for 3778: Valid amounts for Canadian Traveler's Checks are not being accepted
 *
 *    Rev 1.7   Feb 05 2004 14:40:40   rsachdeva
 * Update for  New Requirements for Alternate Currency
 * Resolution for 3778: Valid amounts for Canadian Traveler's Checks are not being accepted
 *
 *    Rev 1.5   Jan 20 2004 16:28:36   bwf
 * Update for new reqs.
 *
 *    Rev 1.4   Jan 19 2004 17:20:08   epd
 * moved calls back to DomainGateway.
 *
 *    Rev 1.3   Jan 06 2004 13:12:10   epd
 * refactored away references to TenderHelper and DomainGateway
 *
 *    Rev 1.2   Dec 11 2003 11:01:30   Tim Fritz
 * Removed the ValidDollarAmountForTravelersCheckTotal parameter.
 * Resolution for 3462: Valid Traveler's Check Amount parameter needs to be removed
 *
 *    Rev 1.1   Nov 14 2003 11:10:02   epd
 * refactored some void functionality to be more general.
 *
 *    Rev 1.0   Nov 04 2003 11:14:00   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 17 2003 12:34:36   epd
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender.group;

import java.util.HashMap;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.BigDecimalExt;
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
 */
public class TenderGroupTravelersCheckADO extends AbstractTenderGroupADO
{
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.tender.group.TenderGroupADOIfc#getGroupType()
     */
    public TenderTypeEnum getGroupType()
    {
        return TenderTypeEnum.TRAVELERS_CHECK;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#getVoidType()
     */
    public TenderTypeEnum getVoidType()
    {
        return TenderTypeEnum.TRAVELERS_CHECK;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.tender.group.TenderGroupADOIfc#validateLimits(java.util.HashMap)
     */
    public void validateLimits(HashMap tenderAttributes, CurrencyIfc balanceDue) throws TenderException
    {
        // evaluate that the amount is a valid amount according to the valid parameter values
        UtilityIfc util = getUtility();

        // amount of this tender
        CurrencyIfc tenderAmount = parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT));
        String alternateAmountValue = (String)tenderAttributes.get(TenderConstants.ALTERNATE_AMOUNT);
        //If alternate currency not used, we check base currency amount
        if (alternateAmountValue == null)
        {
            // check if base amount ends with 5 or 0
            checkAmountEndFiveOrZero(tenderAmount);
        }
        else
        {
            CurrencyIfc tenderAlternateAmount = parseAlternateAmount(alternateAmountValue,tenderAttributes);
            // check if alternate amount ends with 5 or 0
            checkAmountEndFiveOrZero(tenderAlternateAmount);
        }

        // test tender limits if not overridden
        if (evaluateTenderLimits == Boolean.TRUE)
        {
            // Compare tender amount to limit value
            CurrencyIfc tenderAmountTotal = getTenderTotal().add(tenderAmount);

            // Check the max amount limit
            String maxAmountStr = util.getParameterValue("MaximumTravelersCheckAmount", "1000.00");
            CurrencyIfc maxLimitValue = DomainGateway.getBaseCurrencyInstance(maxAmountStr);
            if (!maxLimitValue.equals(TenderLimits.getTenderNoLimitAmount()) &&
                tenderAmountTotal.compareTo(maxLimitValue) == CurrencyIfc.GREATER_THAN)
            {
                throw new TenderException("Travel Check amount total exceeds maximum amount",
                                          TenderErrorCodeEnum.MAX_LIMIT_VIOLATED);
            }

            // Check the min amount limit
            String minAmountStr = util.getParameterValue("MinimumTravelersCheckAmount", "1.00");
            CurrencyIfc minLimitValue = DomainGateway.getBaseCurrencyInstance(minAmountStr);
            if (!minLimitValue.equals(TenderLimits.getTenderNoLimitAmount()) &&
                tenderAmountTotal.compareTo(minLimitValue) == CurrencyIfc.LESS_THAN)
            {
                throw new TenderException("Travel Check amount total exceeds minimum amount",
                                          TenderErrorCodeEnum.MIN_LIMIT_VIOLATED);
            }
        }
        else
        {
            evaluateTenderLimits = Boolean.TRUE;
        }
    }

    //--------------------------------------------------------------------------
    /**
       Checks whether amount ends with 5 or 0. <P>
       @param amount being checked
       @return boolean true if amount ends with 5 or 0, false otherwise
    **/
    //--------------------------------------------------------------------------
    public  void checkAmountEndFiveOrZero(CurrencyIfc amount) throws TenderException
    {
        BigDecimalExt tendAmt = new BigDecimalExt(amount.abs().getStringValue());
        if(tendAmt.remainder(new BigDecimalExt(5)).compareTo(new BigDecimalExt(0)) == CurrencyIfc.GREATER_THAN &&
           tendAmt.remainder(new BigDecimalExt(10)).compareTo(new BigDecimalExt(0)) == CurrencyIfc.GREATER_THAN)
        {
            throw new TenderException("Travel Check amount in not a valid amount",
                                      TenderErrorCodeEnum.INVALID_AMOUNT);
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

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy()
     */
    public EYSDomainIfc toLegacy()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy(java.lang.Class)
     */
    public EYSDomainIfc toLegacy(Class type)
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
     */
    public void fromLegacy(EYSDomainIfc rdo)
    {
        // TODO Auto-generated method stub
    }
}
