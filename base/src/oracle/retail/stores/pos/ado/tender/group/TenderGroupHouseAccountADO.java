/*===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/group/TenderGroupHouseAccountADO.java /rgbustores_13.4x_generic_branch/1 2011/09/09 17:41:56 sgu Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* sgu         09/08/11 - add house account as a refund tender
* sgu         09/02/11 - add house account tender group
* sgu         09/02/11 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.ado.tender.group;

import java.util.HashMap;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;

public class TenderGroupHouseAccountADO extends AbstractAuthorizableTenderGroupADO
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 6750144703121995071L;

    /**
     * @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#getGroupType()
     */
    public TenderTypeEnum getGroupType()
    {
        return TenderTypeEnum.HOUSE_ACCOUNT;
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
