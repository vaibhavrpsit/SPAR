/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/group/TenderGroupMallCertificateADO.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:44 mszekely Exp $
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
 *    4    360Commerce 1.3         4/25/2007 8:52:50 AM   Anda D. Cadar   I18N
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
 *    Rev 1.4   Feb 05 2004 13:21:02   rhafernik
 * log4j conversion
 *
 *    Rev 1.3   Jan 19 2004 17:20:06   epd
 * moved calls back to DomainGateway.
 *
 *    Rev 1.2   Jan 06 2004 13:12:04   epd
 * refactored away references to TenderHelper and DomainGateway
 *
 *    Rev 1.1   Dec 29 2003 14:29:38   bwf
 * Updated for unit test.
 *
 *    Rev 1.0   Dec 11 2003 13:08:50   bwf
 * Initial revision.
 * Resolution for 3538: Mall Certificate Tender
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

//--------------------------------------------------------------------------
/**
    This class is the group class for mall certificates.
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class TenderGroupMallCertificateADO extends AbstractTenderGroupADO
{
    //----------------------------------------------------------------------
    /**
        Gets the group type.
        @return
        @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#getGroupType()
    **/
    //----------------------------------------------------------------------
    public TenderTypeEnum getGroupType()
    {
        return TenderTypeEnum.MALL_CERT;
    }

    //----------------------------------------------------------------------
    /**
        Gets the void type.
        @return
        @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#getVoidType()
    **/
    //----------------------------------------------------------------------
    public TenderTypeEnum getVoidType()
    {
        return TenderTypeEnum.MALL_CERT;
    }

    //----------------------------------------------------------------------
    /**
        This method validates the limits of the mall gift certificate.
        @param tenderAttributes
        @param balanceDue
        @throws TenderException
        @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#validateLimits(java.util.HashMap, oracle.retail.stores.domain.currency.CurrencyIfc)
    **/
    //----------------------------------------------------------------------
    public void validateLimits(HashMap tenderAttributes, CurrencyIfc balanceDue) throws TenderException
    {
        // evaluate that the amount is a valid amount according to the valid parameter values
        UtilityIfc util = getUtility();
        // amount of this tender
        CurrencyIfc tenderAmount = parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT));

        // test tender limits if not overridden
        if(evaluateTenderLimits == Boolean.TRUE)
        {
            // Compare tender amount to limit value
            CurrencyIfc tenderAmountTotal = getTenderTotal().add(tenderAmount);

            // Check the max amount limit
            String maxAmountStr = util.getParameterValue("MaximumMallCertificateTenderAmount", "1000.00");
            CurrencyIfc maxLimitValue = DomainGateway.getBaseCurrencyInstance(maxAmountStr);
            if(!maxLimitValue.equals(TenderLimits.getTenderNoLimitAmount()) &&
               tenderAmountTotal.compareTo(maxLimitValue) == CurrencyIfc.GREATER_THAN)
            {
                throw new TenderException("Check amount total exceeds maximum amount",
                                          TenderErrorCodeEnum.MAX_LIMIT_VIOLATED);
            }
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

    //----------------------------------------------------------------------
    /**
        This method converts to legacy.
        @return
        @see oracle.retail.stores.pos.ado.ADOIfc#toLegacy()
    **/
    //----------------------------------------------------------------------
    public EYSDomainIfc toLegacy()
    {
        return null;
    }
    //----------------------------------------------------------------------
    /**
        This method converts to a specific legacy type.
        @param type
        @return
        @see oracle.retail.stores.pos.ado.ADOIfc#toLegacy(java.lang.Class)
    **/
    //----------------------------------------------------------------------
    public EYSDomainIfc toLegacy(Class type)
    {
        return null;
    }

    //----------------------------------------------------------------------
    /**

        @param rdo
        @see oracle.retail.stores.pos.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
    **/
    //----------------------------------------------------------------------
    public void fromLegacy(EYSDomainIfc rdo)
    {
    }
}
