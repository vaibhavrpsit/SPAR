/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/group/TenderGroupGiftCertificateADO.java /main/10 2011/02/03 18:23:32 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       02/03/11 - check in all
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    7    360Commerce 1.6         3/31/2008 1:48:51 PM   Mathews Kochummen
 *         forward port from v12x to trunk
 *    6    360Commerce 1.5         7/9/2007 6:04:18 PM    Alan N. Sinton  CR
 *         27494 - Removed constaint TenderLimitsIfc.TENDER_NO_LIMIT_AMOUNT in
 *          favor of a lazy init value returned by static method
 *         TenderLimits.getTenderNoLimitAmount().
 *    5    360Commerce 1.4         4/25/2007 8:52:50 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    4    360Commerce 1.3         4/7/2006 5:01:40 AM    Akhilashwar K. Gupta
 *         CR-10596: Added new method validateRefundLimits()
 *    3    360Commerce 1.2         3/31/2005 4:30:24 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:59 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:53 PM  Robert Pearse
 *
 *   Revision 1.6  2004/07/21 22:55:33  bwf
 *   @scr 5963 (ServicesImpact) Moved getChangeOptions and calculateMaxCashChange out of
 *                     abstractRetailTransaction and into TenderUtility.  Also made calculateMaxCashChange
 *                     more polymorphic.
 *
 *   Revision 1.5  2004/06/19 17:33:32  bwf
 *   @scr 5205 These are the overhaul changes to the Change Due Options
 *                     screen and max change calculations.
 *
 *   Revision 1.4  2004/03/17 16:00:16  epd
 *   @scr 3561 Bug fixing and refactoring
 *
 *   Revision 1.3  2004/03/17 15:17:15  aschenk
 *   @scr 4060 - Changed the cash change parameter for ift cards to be a value instead of a percent.
 *
 *   Revision 1.2  2004/02/12 16:47:56  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.5   Feb 05 2004 13:21:00   rhafernik
 * log4j conversion
 *
 *    Rev 1.4   Jan 19 2004 17:19:58   epd
 * moved calls back to DomainGateway.
 *
 *    Rev 1.3   Jan 06 2004 13:12:00   epd
 * refactored away references to TenderHelper and DomainGateway
 *
 *    Rev 1.2   Nov 20 2003 16:15:14   crain
 * Implemented validateLimits
 * Resolution for 3421: Tender redesign
 *
 *    Rev 1.1   Nov 14 2003 11:09:50   epd
 * refactored some void functionality to be more general.
 *
 *    Rev 1.0   Nov 04 2003 11:13:58   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 17 2003 12:34:34   epd
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
 */
public class TenderGroupGiftCertificateADO extends AbstractTenderGroupADO
{
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.tender.group.TenderGroupADOIfc#getGroupType()
     */
    public TenderTypeEnum getGroupType()
    {
        return TenderTypeEnum.GIFT_CERT;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#getVoidType()
     */
    public TenderTypeEnum getVoidType()
    {
        return getGroupType();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.tender.group.TenderGroupADOIfc#validateLimits(java.util.HashMap)
     */
    public void validateLimits(HashMap tenderAttributes, CurrencyIfc balanceDue) throws TenderException
    {
        if (evaluateTenderLimits == Boolean.TRUE)
        {
            // Compare tender amount total of this group to limit value
            CurrencyIfc tenderAmount = parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT));
            CurrencyIfc tenderAmountTotal = getTenderTotal().add(tenderAmount);

            UtilityIfc util = getUtility();
            // default value from requirements
            String maxGCStr = util.getParameterValue("MaximumGiftCertificateTenderAmount", "3000.00");
            String minGCStr = util.getParameterValue("MinimumGiftCertificateTenderAmount", "0.01");
            CurrencyIfc maxLimitValue = DomainGateway.getBaseCurrencyInstance(maxGCStr);
            CurrencyIfc minLimitValue = DomainGateway.getBaseCurrencyInstance(minGCStr);

            // compare the max amount to the tender amount total
            if (!maxLimitValue.equals(TenderLimits.getTenderNoLimitAmount()) &&
                tenderAmountTotal.compareTo(maxLimitValue) == CurrencyIfc.GREATER_THAN)
            {
                throw new TenderException("Gift certificate amount exceeds maximum amount",
                                          TenderErrorCodeEnum.MAX_LIMIT_VIOLATED);
            }

            // compare the min amount to the tender amount (only this tender amount)
            if (!minLimitValue.equals(TenderLimits.getTenderNoLimitAmount()) &&
                tenderAmount.compareTo(minLimitValue) == CurrencyIfc.LESS_THAN)
            {
                throw new TenderException("Gift certificate amount does not meet minimum required amount",
                                          TenderErrorCodeEnum.MIN_LIMIT_VIOLATED);
            }
        }
        else
        {
            evaluateTenderLimits = Boolean.TRUE;
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.tender.group.AbstractTenderGroupADO#validateRefundLimits(java.util.HashMap, boolean, boolean)
     */
    public void validateRefundLimits(HashMap tenderAttributes, boolean hasReceipt, boolean retrieved) throws TenderException
    {
        if (evaluateTenderLimits.booleanValue())
        {
            // Compare tender amount total of this group to limit value
            CurrencyIfc tenderAmount = parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT)).abs();

            UtilityIfc util = getUtility();
            // default value from requirements
            String maxGCStr = util.getParameterValue("MaximumGiftCertificateIssueAmount", "3000.00");
            String minGCStr = util.getParameterValue("MinimumGiftCertificateIssueAmount", "0.01");
            CurrencyIfc maxLimitValue = DomainGateway.getBaseCurrencyInstance(maxGCStr);
            CurrencyIfc minLimitValue = DomainGateway.getBaseCurrencyInstance(minGCStr);

            // compare the max amount to the tender amount total
            if (!maxLimitValue.equals(TenderLimits.getTenderNoLimitAmount()) &&
                    tenderAmount.compareTo(maxLimitValue) == CurrencyIfc.GREATER_THAN)
            {
                throw new TenderException("Gift certificate amount exceeds maximum amount",
                                          TenderErrorCodeEnum.MAX_CHANGE_LIMIT_VIOLATED);
            }

            // compare the min amount to the tender amount (only this tender amount)
            if (!minLimitValue.equals(TenderLimits.getTenderNoLimitAmount()) &&
                tenderAmount.compareTo(minLimitValue) == CurrencyIfc.LESS_THAN)
            {
                throw new TenderException("Gift certificate amount does not meet minimum required amount",
                                          TenderErrorCodeEnum.MIN_CHANGE_LIMIT_VIOLATED);
            }
        }
        else
        {
            evaluateTenderLimits = Boolean.TRUE;
        }
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

        // if overtenderable
        if (tenderOvertenderable())
        {
            UtilityIfc util = getUtility();
            String maxCashChangeStr = util.getParameterValue("MaximumAmountCashChangeForGiftCertificate", "10.00");
            maxCashChange = DomainGateway.getBaseCurrencyInstance(maxCashChangeStr);
        }
        return maxCashChange;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy()
     */
    public EYSDomainIfc toLegacy()
    {
        return null;
    }
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy(java.lang.Class)
     */
    public EYSDomainIfc toLegacy(Class type)
    {
        return null;
    }
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
     */
    public void fromLegacy(EYSDomainIfc rdo)
    {
    }
}
