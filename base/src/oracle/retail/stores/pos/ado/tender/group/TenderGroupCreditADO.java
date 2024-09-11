/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/group/TenderGroupCreditADO.java /rgbustores_13.4x_generic_branch/2 2011/07/28 17:17:16 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   07/28/11 - Removed code that determined tender limit
 *                         violations. These limits were removed as part of
 *                         13.4 Advanced Payment Foundation.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    asinton   12/03/09 - Changes to support credit card authorizations on
 *                         returns and voids.
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
 *   Revision 1.4  2004/08/31 19:12:35  blj
 *   @scr 6855 - cleanup gift card credit code and fix defects found by PBY
 *
 *   Revision 1.3  2004/04/07 20:19:10  epd
 *   @scr 4322 Updates for tender invariant work
 *
 *   Revision 1.2  2004/02/12 16:47:56  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.5   Feb 05 2004 13:20:56   rhafernik
 * log4j conversion
 *
 *    Rev 1.4   Jan 19 2004 17:19:56   epd
 * moved calls back to DomainGateway.
 *
 *    Rev 1.3   Jan 06 2004 13:11:54   epd
 * refactored away references to TenderHelper and DomainGateway
 *
 *    Rev 1.2   Nov 14 2003 11:09:46   epd
 * refactored some void functionality to be more general.
 *
 *    Rev 1.1   Nov 11 2003 16:18:22   epd
 * Updates made to accommodate tender deletion/reversal
 *
 *    Rev 1.0   Nov 04 2003 11:13:56   epd
 * Initial revision.
 *
 *    Rev 1.4   Oct 30 2003 20:39:52   epd
 * removed authorize() method
 *
 *    Rev 1.3   Oct 27 2003 20:49:06   epd
 * renamed interface
 *
 *    Rev 1.2   Oct 27 2003 20:36:14   epd
 * fixed min limit logic
 *
 *    Rev 1.1   Oct 20 2003 15:20:34   epd
 * added limit validation
 *
 *    Rev 1.0   Oct 17 2003 12:34:32   epd
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender.group;

import java.util.HashMap;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;

/**
 *
 *
 */
public class TenderGroupCreditADO extends AbstractAuthorizableTenderGroupADO
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -893448292708820468L;

    /**
     * @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#getGroupType()
     */
    public TenderTypeEnum getGroupType()
    {
        return TenderTypeEnum.CREDIT;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#getVoidType()
     */
    public TenderTypeEnum getVoidType()
    {
        return getGroupType();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.tender.group.TenderGroupADOIfc#validateLimits(java.util.HashMap, oracle.retail.stores.domain.currency.CurrencyIfc)
     */
    public void validateLimits(HashMap tenderAttributes, CurrencyIfc balanceDue) throws TenderException
    {
        // min/max credit limits were removed in 13.4 - simply set to true
        evaluateTenderLimits = Boolean.TRUE;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.tender.group.AuthorizableTenderGroupADOIfc#isReversible()
     */
    public boolean isReversible()
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
