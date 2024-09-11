/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rrkohli   12/01/10 - fix to handle when same serial number is entered
 *                         more than once
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/15/09 - Launch Shuttle class for UIN Lookup tour
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;

/**
 * Launch Shuttle class for UIN Lookup tour
 * @author nkgautam
 */
public class UINLookUpLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{

    /**
     * Item Inquiry Cargo
     */
    ItemInquiryCargo inquiryCargo;

    /**
     * Loads the item inquiry cargo.
     * @param  bus
     */
    public void load(BusIfc bus)
    {
        super.load(bus);
        inquiryCargo = (ItemInquiryCargo)bus.getCargo();
    }

    /**
     * Transfers the item inquiry cargo to the UIN lookup
     * cargo for the item inquiry service.
     * @param  bus
     */
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        UINLookUpCargo cargo = (UINLookUpCargo)bus.getCargo();
        String storeID = null;

        if(inquiryCargo.getStoreStatus() != null)
        {
            storeID = inquiryCargo.getStoreStatus().getStore().getStoreID();
        }
        // Set Search Criteria
        SearchCriteriaIfc criteria = inquiryCargo.getInquiry();
        criteria.setItemID(inquiryCargo.getInquiry().getItemID());
        criteria.setItemSerialNumber(inquiryCargo.getItemSerial());
        criteria.setStoreNumber(storeID);
        cargo.setInquiry(criteria);
        cargo.setTransaction(inquiryCargo.getTransaction());
    }

}
