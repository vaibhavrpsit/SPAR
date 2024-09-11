/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/group/TenderGroupCouponADO.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:43 mszekely Exp $
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
 *    4    360Commerce 1.3         4/25/2007 8:52:50 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    3    360Commerce 1.2         3/31/2005 4:30:24 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:59 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:53 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/02/12 16:47:56  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Feb 05 2004 13:20:54   rhafernik
 * No change.
 * 
 *    Rev 1.1   Nov 14 2003 11:09:44   epd
 * refactored some void functionality to be more general.
 * 
 *    Rev 1.0   Nov 04 2003 11:13:54   epd
 * Initial revision.
 * 
 *    Rev 1.1   Oct 30 2003 12:57:52   crain
 * Removed processVoid
 * Resolution for 3421: Tender redesign
 * 
 *    Rev 1.0   Oct 17 2003 12:34:30   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender.group;

import java.util.HashMap;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.domain.utility.EYSDomainIfc;

/**
 *  
 */
public class TenderGroupCouponADO extends AbstractTenderGroupADO
{
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.tender.group.TenderGroupADOIfc#getGroupType()
     */
    public TenderTypeEnum getGroupType()
    {
        return TenderTypeEnum.COUPON;
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
        // coupon does not have limits
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
