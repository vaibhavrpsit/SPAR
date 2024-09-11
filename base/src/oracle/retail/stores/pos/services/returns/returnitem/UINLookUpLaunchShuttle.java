/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     11/03/14 - added UIN (SIM) lookup to the non-receipted returns flow.
 *    rrkohli   05/19/11 - UIN scan in return without receipt fix
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    nkgautam  01/28/10 - fix for item not being retrieved by UIN
 *    abondala  01/03/10 - update header date
 *    mchellap  12/16/09 - Changes for code review findings
 *    mchellap  12/15/09 - Serialisation changes
 *    mchellap  12/15/09 - Launch shuttle for UINLooup service
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

import oracle.retail.stores.domain.transaction.SearchCriteria;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.common.UINLookUpCargo;

@SuppressWarnings("serial")
public class UINLookUpLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{
    /**
     * Return Item Cargo
     */
    ReturnItemCargo returnItemCargo;

    /**
     * Loads the return item cargo.
     *
     * @param bus
     */
    public void load(BusIfc bus)
    {
        super.load(bus);
        returnItemCargo = (ReturnItemCargo) bus.getCargo();
    }

    /**
     * Transfers the return item cargo to the UIN lookup cargo for the return
     * item service.
     *
     * @param bus
     */
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        UINLookUpCargo cargo = (UINLookUpCargo) bus.getCargo();
        cargo.setCheckForNotSellable(true);
        // Set Search Criteria
        SearchCriteriaIfc criteria = new SearchCriteria();
        criteria.setItemID(returnItemCargo.getPLUItemID());
        criteria.setItemSerialNumber(returnItemCargo.getPLUItemID());
        criteria.setItemNumber(returnItemCargo.getPLUItemID());
        criteria.setStoreNumber(returnItemCargo.getStoreID());
        cargo.setInquiry(criteria);
        cargo.setReturnWhenItemNotFound(true);
    }
}
