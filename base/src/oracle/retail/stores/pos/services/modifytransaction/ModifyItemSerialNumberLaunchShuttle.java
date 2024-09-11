/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/ModifyItemSerialNumberLaunchShuttle.java /main/10 2012/05/18 14:20:08 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     05/14/12 - Modified to support Ship button feature.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abhayg    03/26/10 - adding operator in serialised cargo class
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/29/09 - changes for serialization
 *    aariyer   02/02/09 - Added the Shuttle for SerialNumber check in Item
 *                         Basket.
 *    aariyer   01/28/09 - Adding elements for Item Basket
 *    vikini    01/21/09 - Creating Shuttle for launching Serialized item
 *                         Station
 *    vikini    01/21/09 - Creating Shuttle for launching Serialized item
 *                         Station
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction;

//foundation imports
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.modifyitem.serialnumber.SerializedItemCargo;


//--------------------------------------------------------------------------
/**
    This shuttle transfers data from the POS service to the Serialized Item service.
    @version $Revision: /main/10 $
 **/
//--------------------------------------------------------------------------
public class ModifyItemSerialNumberLaunchShuttle extends FinancialCargoShuttle
{

    /**
     * Parent tour cargo class
     */
    protected ModifyTransactionCargo transactionCargo = null;

    /**
     * store financial status
     */
    protected StoreStatusIfc storeStatus;

    /**
     * The line item to modify
     */
    protected SaleReturnLineItemIfc lineItem = null;

    /**
       Copies information from the cargo used in the POS service.
       <P>
       @param  bus     Service Bus
     **/
    public void load(BusIfc bus)
    {
        // retrieve cargo from the parent
        transactionCargo = (ModifyTransactionCargo)bus.getCargo();
        lineItem = transactionCargo.getBasketDTO().getsaleReturnSpecifiedItem();
        storeStatus = transactionCargo.getStoreStatus();
    }

    /**
       Copies information from the cargo used in the POS service.
       <P>
       @param  bus     Service Bus
     **/
    public void unload(BusIfc bus)
    {
        SerializedItemCargo serializedCargo = (SerializedItemCargo)bus.getCargo();
        serializedCargo.setTransaction(transactionCargo.getTransaction());
        serializedCargo.setCustomer(transactionCargo.getTransaction().getCustomer());
        serializedCargo.setRegister(transactionCargo.getRegister());
        serializedCargo.setStoreStatus(storeStatus);
        serializedCargo.setOperator(transactionCargo.getOperator());
        lineItem.setSelectedForItemModification(true);

        if (!lineItem.isKitHeader())
        {
            serializedCargo.setLineItems(new SaleReturnLineItemIfc[] {lineItem});
        }
        else
        {
            serializedCargo.setLineItems(
                    ((KitHeaderLineItemIfc)lineItem).getKitComponentLineItemArray());
            serializedCargo.setKitHeader(true);
        }

        //Adding SearchCriteriaIfc to the SerializedItem Cargo
        String geoCode = null;
        if(transactionCargo.getStoreStatus() != null &&
                transactionCargo.getStoreStatus().getStore() != null)
        {
            geoCode = transactionCargo.getStoreStatus().getStore().getGeoCode();
        }
        serializedCargo.setInquiry(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE), lineItem.getItemID(), "", "", geoCode);

    }
}
