/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/group/TenderGroupCheckADO.java /rgbustores_13.4x_generic_branch/3 2011/08/12 17:01:45 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  08/12/11 - property to hold value for if max overtender limit
 *                         has been violated
 *    mkutiana  08/10/11 - Undid incorrect changes for bug 11838309,
 *                         MAX_CHANGE_LIMIT_VIOLATED is previously checked in
 *                         site CheckLimitActionSite
 *    mchellap  03/24/11 - XbranchMerge mchellap_bug-11838309 from main
 *    mchellap  03/24/11 - BUG#11838309 Over tendering allowed for check
 *    kelesika  12/10/10 - check tender limits
 *    cgreene   05/26/10 - convert to oracle packaging
 *    npoola    01/04/10 - updated with the latest build
 *    npoola    01/04/10 - Ask for the manager override if the over tender
 *                         amount is more than parameter
 *                         MaximumOvertenderCheckAmount
 *    npoola    01/04/10 - Check the over tender limit based on the parameter
 *                         MaximumOvertenderCheckAmount
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         7/9/2007 6:04:18 PM    Alan N. Sinton  CR
 *         27494 - Removed constaint TenderLimitsIfc.TENDER_NO_LIMIT_AMOUNT in
 *          favor of a lazy init value returned by static method
 *         TenderLimits.getTenderNoLimitAmount().
 *    4    360Commerce 1.3         4/25/2007 8:52:51 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:30:24 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:59 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:53 PM  Robert Pearse
 *
 *   Revision 1.5  2004/08/31 19:12:35  blj
 *   @scr 6855 - cleanup gift card credit code and fix defects found by PBY
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
 *    Rev 1.9   Feb 05 2004 13:20:52   rhafernik
 * log4j conversion
 *
 *    Rev 1.8   Jan 19 2004 17:19:56   epd
 * moved calls back to DomainGateway.
 *
 *    Rev 1.7   Jan 09 2004 11:28:24   bwf
 * Remove todos.
 *
 *    Rev 1.6   Jan 06 2004 13:14:54   epd
 * refactored away references to TenderHelper and DomainGateway
 *
 *    Rev 1.5   Nov 19 2003 14:00:04   bwf
 * Check to see if there was an echeck authorization.
 * Resolution for 3429: Check/ECheck Tender
 *
 *    Rev 1.4   Nov 14 2003 11:09:40   epd
 * refactored some void functionality to be more general.
 *
 *    Rev 1.3   Nov 11 2003 16:18:20   epd
 * Updates made to accommodate tender deletion/reversal
 *
 *    Rev 1.2   Nov 09 2003 16:32:08   bwf
 * Remove authorization.
 * Resolution for 3429: Check/ECheck Tender
 *
 *    Rev 1.1   Nov 07 2003 16:27:40   bwf
 * Added check functionality.
 * Resolution for 3429: Check/ECheck Tender
 *
 *    Rev 1.0   Nov 04 2003 11:13:54   epd
 * Initial revision.
 *
 *    Rev 1.1   Oct 27 2003 20:48:56   epd
 * renamed interface
 *
 *    Rev 1.0   Oct 17 2003 12:34:30   epd
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender.group;

import java.util.HashMap;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderLimits;
import oracle.retail.stores.domain.utility.EYSDomainIfc;

//--------------------------------------------------------------------------
/**
    This class defines the tender group check ado.
    $Revision: /rgbustores_13.4x_generic_branch/3 $
**/
//--------------------------------------------------------------------------
public class TenderGroupCheckADO extends AbstractAuthorizableTenderGroupADO
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/3 $";
    
    protected boolean isMaxOvertenderLimitViolated = false;

    //----------------------------------------------------------------------
    /**
        This method returns the tender group type
        @return tender type check
        @see oracle.retail.stores.ado.tender.group.TenderGroupADOIfc#getGroupType()
    **/
    //----------------------------------------------------------------------
    public TenderTypeEnum getGroupType()
    {
        return TenderTypeEnum.CHECK;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#getVoidType()
     */
    public TenderTypeEnum getVoidType()
    {
        return getGroupType();
    }

    //----------------------------------------------------------------------
    /**
        This method validates the check limits.
        @param tenderAttributes
        @param balanceDue
        @throws TenderException
        @see oracle.retail.stores.ado.tender.group.TenderGroupADOIfc#validateLimits(java.util.HashMap, oracle.retail.stores.domain.currency.CurrencyIfc)
    **/
    //----------------------------------------------------------------------
    public void validateLimits(HashMap tenderAttributes, CurrencyIfc balanceDue) throws TenderException
    {
        // evaluate that the amount is a valid amount according to the valid parameter values
        UtilityIfc util = getUtility();

        // amount of this tender
        CurrencyIfc tenderAmount =
               parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT));

        // test tender limits if not overridden
        if (evaluateTenderLimits == Boolean.TRUE)
        {
            // Compare tender amount to limit value
            CurrencyIfc tenderAmountTotal = getTenderTotal().add(tenderAmount);

            // Check the max amount limit
            String maxAmountStr = util.getParameterValue("MaximumCheckAmount", "1000.00");
            CurrencyIfc maxLimitValue = DomainGateway.getBaseCurrencyInstance(maxAmountStr);
            if (!maxLimitValue.equals(TenderLimits.getTenderNoLimitAmount()) &&
                tenderAmountTotal.compareTo(maxLimitValue) == CurrencyIfc.GREATER_THAN)
            {
                throw new TenderException("Check amount total exceeds maximum amount",
                                          TenderErrorCodeEnum.MAX_LIMIT_VIOLATED);
            }

            // Check the min amount limit
            String minAmountStr = util.getParameterValue("MinimumCheckAmount", "1.00");
            CurrencyIfc minLimitValue = DomainGateway.getBaseCurrencyInstance(minAmountStr);
            if (!minLimitValue.equals(TenderLimits.getTenderNoLimitAmount()) &&
                tenderAmountTotal.compareTo(minLimitValue) == CurrencyIfc.LESS_THAN)
            {
                throw new TenderException("Check amount total exceeds minimum amount",
                                          TenderErrorCodeEnum.MIN_LIMIT_VIOLATED);
            }

            // Check the max over tender amount limit
            // calculate the over tender amount from tender total and balance due
            CurrencyIfc overTenderAmount = tenderAmount.subtract(balanceDue);
            if (overTenderAmount.compareTo(getMaxChangeForTender()) == CurrencyIfc.GREATER_THAN)
            {
                setMaxOvertenderLimitViolated(true);
                throw new TenderException("Maximum overtender for check amount has been violated", TenderErrorCodeEnum.CHECK_MAX_OVERTENDER_LIMIT_VIOLATED);
            }
        }
        else
        {
            evaluateTenderLimits = Boolean.TRUE;
        }
    }

    //----------------------------------------------------------------------
    /**
        Return whether or not check is reversible.
        @return
        @see oracle.retail.stores.pos.ado.tender.group.AuthorizableTenderGroupADOIfc#isReversible()
    **/
    //----------------------------------------------------------------------
    public boolean isReversible()
    {
        // Only E-checks require reversal, deposited checks do not, but return true here so that
        // processing of checks is invoked
        return true;
    }

    //----------------------------------------------------------------------
    /**
        This method steps through all check tenders to determine if there
        is a check that has been authorized as an echeck.
        @return
    **/
    //----------------------------------------------------------------------
    public boolean isECheckPresent()
    {
        boolean eCheckPresent = false;
        TenderADOIfc[] tenders = getTenders();
        for(int i = 0;i < tenders.length;i++)
        {
            String checkAuthType =
                 (String)tenders[i].getTenderAttributes().get(TenderConstants.CHECK_AUTH_TYPE);
            if(checkAuthType != null)
            {
                if(checkAuthType.equals("ECheck"))
                {
                    eCheckPresent = true;
                }
            }
        }
        return eCheckPresent;
    }

    //----------------------------------------------------------------------
    /**
        This method gets the amount of cash change.  Check either uses
        the real amount of all checks or the overtender check amount, which
        ever one is less.
        @return
        @see oracle.retail.stores.pos.ado.tender.group.AbstractTenderGroupADO#getMaxCashChange()
    **/
    //----------------------------------------------------------------------
    public CurrencyIfc getMaxCashChange()
    {
        CurrencyIfc maxCashChange = DomainGateway.getBaseCurrencyInstance();

        // if the check is overtenderable
        if (tenderOvertenderable())
        {
            maxCashChange = super.getMaxCashChange();
            CurrencyIfc maxCheckChange = getMaxChangeForTender();

            if (maxCashChange.compareTo(maxCheckChange) == CurrencyIfc.GREATER_THAN)
            {
                maxCashChange = maxCheckChange;
            }
        }
        return maxCashChange;
    }

    //----------------------------------------------------------------------
    /**
        This method gets the max cash change currency.
        @return
        @see oracle.retail.stores.pos.ado.tender.AbstractTenderADO#getMaxChangeForTender()
    **/
    //----------------------------------------------------------------------
    public CurrencyIfc getMaxChangeForTender()
    {
        UtilityIfc util = getUtility();
        String maxCheckOverTenderStr = util.getParameterValue("MaximumOvertenderCheckAmount", "5.00");
        CurrencyIfc maxCheckOverTender = DomainGateway.getBaseCurrencyInstance(maxCheckOverTenderStr);
        return maxCheckOverTender;
    }

    //----------------------------------------------------------------------
    /**
        This method determines if the tender is overtenderable.
        @return
        @see oracle.retail.stores.pos.ado.tender.TenderADOIfc#tenderOvertenderable()
    **/
    //----------------------------------------------------------------------
    protected boolean tenderOvertenderable()
    {
        boolean overTenderable = super.tenderOvertenderable();
        if (overTenderable)
        {
            CurrencyIfc maxCheckOverTender = getMaxChangeForTender();
            if (maxCheckOverTender.compareTo(DomainGateway.getBaseCurrencyInstance()) != CurrencyIfc.GREATER_THAN)
            {
                overTenderable = false;
            }
        }
        return overTenderable;
    }

    //----------------------------------------------------------------------
    /**
        This method determines if you should use the full amount for cash
        change.  Check does use the full amount.
        @return
        @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#isCashEquivalentTender()
    **/
    //----------------------------------------------------------------------
    public boolean isCashEquivalentTender()
    {
        return true;
    }

    //----------------------------------------------------------------------
    /**
        This method converts to the legacy.
        @return
        @see oracle.retail.stores.ado.ADOIfc#toLegacy()
    **/
    //----------------------------------------------------------------------
    public EYSDomainIfc toLegacy()
    {
        return null;
    }

    //----------------------------------------------------------------------
    /**
        This method converts to the legacy.
        @param type
        @return
        @see oracle.retail.stores.ado.ADOIfc#toLegacy(java.lang.Class)
    **/
    //----------------------------------------------------------------------
    public EYSDomainIfc toLegacy(Class type)
    {
        return null;
    }

    //----------------------------------------------------------------------
    /**
        This method converts from the legacy.
        @param rdo
        @see oracle.retail.stores.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
    **/
    //----------------------------------------------------------------------
    public void fromLegacy(EYSDomainIfc rdo)
    {
    }
    
    /**
     * Getter for isTenderMaxOvertenderLimitViolated
     * @return boolean isTenderMaxOvertenderLimitViolated
     */
    public boolean isMaxOvertenderLimitViolated()
    {
        return isMaxOvertenderLimitViolated;
    }

    /**
     * Setter for isTenderMaxOvertenderLimitViolated
     * @param isTenderMaxOvertenderLimitViolated
     */
    public void setMaxOvertenderLimitViolated(boolean isMaxOvertenderLimitViolated)
    {
        this.isMaxOvertenderLimitViolated = isMaxOvertenderLimitViolated;
    }
    
}
