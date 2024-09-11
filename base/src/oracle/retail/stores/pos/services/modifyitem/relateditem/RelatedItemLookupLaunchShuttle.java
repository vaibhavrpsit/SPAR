/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/relateditem/RelatedItemLookupLaunchShuttle.java /main/2 2013/02/15 10:23:12 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     02/14/13 - moving DOB from relateditemstation to iteminquiry
*                        station
* yiqzhao     09/28/12 - remove getRevision method.
* yiqzhao     09/26/12 - refactor related item to add cross sell, upsell and
*                        substitute, remove pick one and pick many
* yiqzhao     09/20/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.modifyitem.relateditem;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.RelatedItemTransactionInfoIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;

/**
 * $Revision: /main/2 $
 */
public class RelatedItemLookupLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{
    private static final long serialVersionUID = -1861321890831317737L;

    /**
     * revision number
     **/
    public static final String revisionNumber = "$$";

    // Calling service's cargo
    protected RelatedItemCargo relatedItemCargo = null;

    /**
     * Loads the item cargo.
     * 
     * @param bus Service Bus to copy cargo from.
     */
    @Override
    public void load(BusIfc bus)
    {
        // load the financial cargo
        super.load(bus);

        relatedItemCargo = (RelatedItemCargo)bus.getCargo();

        // set the related item cargo info into the utility manager
        // doing this so that we do not have to pass this information through
        // a dozen or more services
        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus
                .getManager(TransactionUtilityManagerIfc.TYPE);
        RelatedItemTransactionInfoIfc riTransInfo = DomainGateway.getFactory().getRelatedItemTransactionInfoInstance();
        riTransInfo.setNextRelatedItem(relatedItemCargo.getNextRelatedItem());
        riTransInfo.setPrimaryItemSequenceNumber(relatedItemCargo.getPrimaryItemSequenceNumber());
        riTransInfo.setRelatedItem(relatedItemCargo.getRelatedItem());
        riTransInfo.setRelatedItemGroup(relatedItemCargo.getRelatedItemGroupName());
        riTransInfo.setRelatedItems(relatedItemCargo.getToBeAddRelatedItems());
        //riTransInfo.setRelatedItemGroupNumber(relatedItemCargo.getGroupNumber());
        utility.setRelatedItemTransInfo(riTransInfo);
    }

    /**
     * Transfers the item cargo to the item inquiry cargo for the item inquiry
     * service.
     * 
     * @param bus Service Bus to copy cargo to.
     */
    @Override
    public void unload(BusIfc bus)
    {
        // unload the financial cargo
        super.unload(bus);

        ItemInquiryCargo inquiryCargo = (ItemInquiryCargo)bus.getCargo();
        inquiryCargo.setRegister(relatedItemCargo.getRegister());
        inquiryCargo.setTransaction(relatedItemCargo.getTransaction());
        inquiryCargo.setModifiedFlag(true);
        inquiryCargo.setStoreStatus(relatedItemCargo.getStoreStatus());
        inquiryCargo.setRegister(relatedItemCargo.getRegister());
        inquiryCargo.setOperator(relatedItemCargo.getOperator());
        inquiryCargo.setCustomerInfo(relatedItemCargo.getCustomerInfo());
        inquiryCargo.setTenderLimits(relatedItemCargo.getTenderLimits());
        inquiryCargo.setPLUItem(relatedItemCargo.getPLUItem());
        inquiryCargo.setRestrictedDOB(relatedItemCargo.getRestrictedDOB());
        inquiryCargo.setRelatedItem(true);

        String geoCode = null;
        if (relatedItemCargo.getStoreStatus() != null && relatedItemCargo.getStoreStatus().getStore() != null)
        {
            geoCode = relatedItemCargo.getStoreStatus().getStore().getGeoCode();
        }

        inquiryCargo.setInquiry(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE),
                relatedItemCargo.getPLUItemID(), "", "", geoCode);

        inquiryCargo.setIsRequestForItemLookup(true);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.FinancialCargoShuttle#toString()
     */
    @Override
    public String toString()
    {
        return "Class:  RelatedItemLookupShuttle  " +  hashCode();
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }
}
