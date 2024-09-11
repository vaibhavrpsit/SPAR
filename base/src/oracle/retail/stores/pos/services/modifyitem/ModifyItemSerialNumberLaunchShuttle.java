/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/ModifyItemSerialNumberLaunchShuttle.java /main/17 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/08/12 - implement force use of entered serial number instead
 *                         of checking with SIM
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    nkgautam  03/08/10 - fix for serialised components item in a kit
 *    nkgautam  01/22/10 - fix for imei lookup flow
 *    nkgautam  01/13/10 - implementing the unload method in this shuttle
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:04 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:34 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:40 PM  Robert Pearse
 *
 *   Revision 1.5  2004/04/09 16:55:59  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.4  2004/03/15 21:43:30  baa
 *   @scr 0 continue moving out deprecated files
 *
 *   Revision 1.3  2004/02/12 16:51:03  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:39:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:01:44   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:17:40   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:37:30   msg
 * Initial revision.
 *
 *    Rev 1.0   14 Nov 2001 06:50:44   pjf
 * Initial revision.
 * Resolution for POS SCR-8: Item Kits
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.modifyitem.serialnumber.SerializedItemCargo;

/**
 * This shuttle transfers data from the POS service to the Serialized Item
 * service.
 * 
 * @version $Revision: /main/17 $
 */
public class ModifyItemSerialNumberLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{
    private static final long serialVersionUID = 6676038073843118504L;

    /**
     * Parent cargo class
     */
    protected ItemCargo itemCargo;

    /**
     * Current Line Item
     */
    protected SaleReturnLineItemIfc lineItem = null;

    /**
     * Copies information from the cargo used in the POS service.
     * 
     * @param bus Service Bus
     */
    @Override
    public void load(BusIfc bus)
    {
        itemCargo = (ItemCargo)bus.getCargo();
    }

    /**
     * Copies information to the cargo used in the Modify Item Serial Number
     * service.
     * 
     * @param bus Service Bus
     */
    @Override
    public void unload(BusIfc bus)
    {
        SerializedItemCargo cargo = (SerializedItemCargo)bus.getCargo();
        RetailTransactionIfc transaction = itemCargo.getTransaction();
        lineItem = itemCargo.getItem();
        cargo.setTransaction(transaction);
        if(transaction != null)
        {
            cargo.setCustomer(transaction.getCustomer());
        }
        cargo.setRegister(itemCargo.getRegister());

        if (!lineItem.isKitHeader())
        {
            cargo.setLineItems(new SaleReturnLineItemIfc[] {lineItem});
        }
        else
        {
            cargo.setLineItems(
                    ((KitHeaderLineItemIfc)lineItem).getKitComponentLineItemArray());
            cargo.setKitHeader(true);
        }

        //Adding SearchCriteriaIfc to the SerializedItem Cargo
        String geoCode = null;
        if(itemCargo.getStoreStatus() != null &&
                itemCargo.getStoreStatus().getStore() != null)
        {
            cargo.setStoreStatus(itemCargo.getStoreStatus());
            geoCode = itemCargo.getStoreStatus().getStore().getGeoCode();
        }

        cargo.setInquiry(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE), lineItem.getItemID(), "", "", geoCode);
    }
}