/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/ValidateSerialNumberLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:51 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    nkgautam  08/25/10 - fixed NPE issue
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/17/09 - code review updates
 *    nkgautam  12/15/09 - Launch Shuttle class for serial validation tour
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.modifyitem.serialnumber.SerializedItemCargo;

/**
 * Launsh Shuttle class of serial validation tour
 * @author nkgautam
 *
 */
public class ValidateSerialNumberLaunchShuttle implements ShuttleIfc
{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
   Serialized Item Cargo
     */
    protected SerializedItemCargo serializedCargo;

    /**
  The line item to modify
     */
    protected SaleReturnLineItemIfc lineItem;

    /**
  register financial status
     */
    protected RegisterIfc register;

    /**
  Copies information from the cargo used in the POS service.
  @param  bus     Service Bus
     */
    public void load(BusIfc bus)
    {
        serializedCargo = (SerializedItemCargo)bus.getCargo();
        lineItem = serializedCargo.getItem();
        register = serializedCargo.getRegister();
    }

    /**
  Copies information from the cargo used in the POS service.
  @param  bus     Service Bus
     */
    public void unload(BusIfc bus)
    {
        SerialValidationCargo cargo = (SerialValidationCargo)bus.getCargo();
        cargo.setProcessValidationResult(true);
        String storeID = null;
        String geoCode = null;
        cargo.setRegister(register);
        cargo.setLineItem(lineItem);
        if(serializedCargo.getStoreStatus() != null)
        {
            storeID = serializedCargo.getStoreStatus().getStore().getStoreID();
            geoCode = serializedCargo.getStoreStatus().getStore().getGeoCode();
        }
        SearchCriteriaIfc criteria = serializedCargo.getInquiry();
        if(criteria == null)
        {
            criteria = DomainGateway.getFactory().getSearchCriteriaInstance();
            criteria.setSearchLocale(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
            criteria.setGeoCode(geoCode);
        }
        criteria.setItemSerialNumber(lineItem.getItemSerial());
        criteria.setStoreNumber(storeID);
        criteria.setItemID(lineItem.getItemID());
        cargo.setCriteria(criteria);
    }
}
