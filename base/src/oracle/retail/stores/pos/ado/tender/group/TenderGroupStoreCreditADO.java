/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/group/TenderGroupStoreCreditADO.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:43 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         3/31/2008 1:49:26 PM   Mathews Kochummen
 *         forward port from v12x to trunk
 *    5    360Commerce 1.4         7/9/2007 6:04:18 PM    Alan N. Sinton  CR
 *         27494 - Removed constaint TenderLimitsIfc.TENDER_NO_LIMIT_AMOUNT in
 *          favor of a lazy init value returned by static method
 *         TenderLimits.getTenderNoLimitAmount().
 *    4    360Commerce 1.3         4/25/2007 8:52:49 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    3    360Commerce 1.2         3/31/2005 4:30:24 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:59 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:54 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/07/21 22:55:33  bwf
 *   @scr 5963 (ServicesImpact) Moved getChangeOptions and calculateMaxCashChange out of
 *                     abstractRetailTransaction and into TenderUtility.  Also made calculateMaxCashChange
 *                     more polymorphic.
 *
 *   Revision 1.6  2004/06/19 17:38:18  blj
 *   @scr 5720 - Removed minimum store credit issue param check for store credit tender.  There are no tenderlimits for tender store credit just refund limits for issue store credit.
 *
 *   Revision 1.5  2004/05/16 20:54:18  blj
 *   @scr 4476 rework,postvoid and cleanup
 *
 *   Revision 1.4  2004/04/22 22:35:55  blj
 *   @scr 3872 - more cleanup
 *
 *   Revision 1.3  2004/02/17 17:52:55  nrao
 *   Added methods for Issue Store Credit
 *
 *   Revision 1.2  2004/02/12 16:47:56  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.6   Feb 05 2004 13:21:06   rhafernik
 * log4j conversion
 * 
 *    Rev 1.5   Jan 19 2004 17:20:10   epd
 * moved calls back to DomainGateway.
 * 
 *    Rev 1.4   Jan 06 2004 13:12:08   epd
 * refactored away references to TenderHelper and DomainGateway
 * 
 *    Rev 1.3   Dec 12 2003 13:30:48   blj
 * initial "happy path" for storecredit
 * 
 *    Rev 1.2   Dec 04 2003 10:56:12   blj
 * added check for minimum store credit issuance amount parameter.
 * 
 *    Rev 1.1   Nov 14 2003 11:10:00   epd
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
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderStoreCreditADO;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderLimits;
import oracle.retail.stores.domain.utility.EYSDomainIfc;

/**
 *  
 */
public class TenderGroupStoreCreditADO extends AbstractTenderGroupADO
{
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.tender.group.TenderGroupADOIfc#getGroupType()
     */
    public TenderTypeEnum getGroupType()
    {
        return TenderTypeEnum.STORE_CREDIT;
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
     Creates a new tender group idential to the current one with the
     exception that the added tenders have negated amounts        
     @return
     @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#processVoid()
     **/
    //----------------------------------------------------------------------
    public TenderGroupADOIfc processVoid()
    {
        TenderGroupADOIfc voidGroup = super.processVoid();
        
        TenderADOIfc[] tenders = (TenderADOIfc[])voidGroup.getTenders();
        
        for (int i=0; i<tenders.length; i++)
        {
            if (tenders[i] instanceof TenderStoreCreditADO)
            {    
                ((TenderStoreCreditADO)tenders[i]).updateStateForVoid();
            }
        }
        
        return voidGroup;
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.tender.group.TenderGroupADOIfc#validateLimits(java.util.HashMap, oracle.retail.stores.domain.currency.CurrencyIfc)
     */
    public void validateLimits(HashMap tenderAttributes, CurrencyIfc balanceDue) throws TenderException
    {
        /** TODO: scr 5720 there are no tenderlimits for
                for store credit just refund limits.
        // evaluate that the amount is a valid amount according to the valid parameter values
        UtilityIfc util = getUtility();

        // amount of this tender
        CurrencyIfc tenderAmount = parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT));
    
        // test tender limits if not overridden        
        if (evaluateTenderLimits == Boolean.TRUE)
        {      
             // Compare tender amount to limit value
             CurrencyIfc tenderAmountTotal = getTenderTotal().add(tenderAmount);
             // Check the min amount limit
             String minAmountStr = util.getParameterValue("MinimumStoreCreditIssuanceAmount", "5.00");
             CurrencyIfc minLimitValue = DomainGateway.getBaseCurrencyInstance(minAmountStr);
             if (!minLimitValue.equals(TenderLimitsIfc.TENDER_NO_LIMIT_AMOUNT) &&
                tenderAmountTotal.compareTo(minLimitValue) == CurrencyIfc.LESS_THAN)
             {
                throw new TenderException("Store credit amount total exceeds minimum amount",
                          TenderErrorCodeEnum.MIN_LIMIT_VIOLATED);
             }
             
        }
        else
        {
            evaluateTenderLimits = Boolean.TRUE;
        }
        **/
    }
    
    //---------------------------------------------------------------------------------------------------   
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.tender.group.TenderGroupADOIfc#validateRefundLimits(java.util.HashMap, boolean, boolean)
     */
    //---------------------------------------------------------------------------------------------------
    public void validateRefundLimits(HashMap tenderAttributes, boolean hasReceipt, boolean retrieved) throws TenderException
    {
        if (evaluateTenderLimits == Boolean.TRUE)
        {
            UtilityIfc util = getUtility();
            String minStoreCreditIssueStr = "";
            minStoreCreditIssueStr = util.getParameterValue("MinimumStoreCreditIssuanceAmount", "5.00");
            
            // compare tender amount to limit value (take absolute value of tender amount)
            CurrencyIfc tenderAmount = parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT)).abs();
            CurrencyIfc limitValue = DomainGateway.getBaseCurrencyInstance(minStoreCreditIssueStr);
            if (!limitValue.equals(TenderLimits.getTenderNoLimitAmount()) &&
                 tenderAmount.compareTo(limitValue) == CurrencyIfc.LESS_THAN)
            {
                throw new TenderException("Store Credit refund amount is less than minimum allowed",
                          TenderErrorCodeEnum.MIN_LIMIT_VIOLATED);
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
            String maxCashChangeStr = util.getParameterValue("MaximumCashChangeForStoreCredit", "5.00");
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
