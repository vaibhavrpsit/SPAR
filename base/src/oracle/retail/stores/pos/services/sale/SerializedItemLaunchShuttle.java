/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/SerializedItemLaunchShuttle.java /main/17 2012/09/04 16:12:00 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   09/04/12 - Set PreSplitLineNumber for kit components.
 *    jswan     05/14/12 - Modified to support Ship button feature.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/14/09 - Changes to accomodate pickup and delivery in the
 *                         current tour
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:56 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:12 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:10 PM  Robert Pearse
 *
 *   Revision 1.3  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.2  2004/03/25 23:41:39  cdb
 *   @scr 4166 Removing Deprecation Warnings.
 *
 *   Revision 1.1  2004/03/15 21:43:29  baa
 *   @scr 0 continue moving out deprecated files
 *
 *   Revision 1.1  2004/03/15 16:51:06  baa
 *   @scr 0 Move deprecated pos files.
 *
 *   Revision 1.3  2004/02/12 16:51:30  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:05  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Jan 28 2004 14:12:02   rsachdeva
 * Deprecated
 *
 *    Rev 1.0   Aug 29 2003 16:04:50   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:09:40   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:43:28   msg
 * Initial revision.
 *
 *    Rev 1.1   16 Jan 2002 13:01:46   baa
 * allow for adding serial item to non serialized items
 * Resolution for POS SCR-579: Unable to manually enter a serial number to an item
 *
 *    Rev 1.0   14 Nov 2001 06:37:30   pjf
 * Initial revision.
 * Resolution for POS SCR-8: Item Kits
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

//foundation imports
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.modifyitem.serialnumber.SerializedItemCargo;

//--------------------------------------------------------------------------
/**
    This shuttle transfers data from the POS service to the serialized item service.
    @version $Revision: /main/17 $
 **/
//--------------------------------------------------------------------------
public class SerializedItemLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 2016254890906325438L;

    /**
       revision number
     **/
    public static final String revisionNumber = "$Revision: /main/17 $";
    /**
       The line item to modify
     **/
    protected SaleReturnLineItemIfc lineItem = null;

    protected SaleCargoIfc saleCargo = null;

    /**
       Copies information from the cargo used in the POS service.
       @param  bus     Service Bus
     **/
    public void load(BusIfc bus)
    {
        // retrieve cargo from the parent
        super.load(bus);
        saleCargo = (SaleCargoIfc)bus.getCargo();
    }

    /**
       Copies information to the cargo used in the Modify Item Serial Number service.
       @param  bus     Service Bus
     **/
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        SerializedItemCargo cargo = (SerializedItemCargo)bus.getCargo();
        RetailTransactionIfc transaction = saleCargo.getTransaction();
        lineItem = saleCargo.getLineItem();
        lineItem.setSelectedForItemModification(true);
        lineItem.setPreSplitLineNumber(lineItem.getLineNumber());
        
        cargo.setTransaction(transaction);
        cargo.setCustomer(transaction.getCustomer());
        cargo.setRegister(saleCargo.getRegister());

        if (!lineItem.isKitHeader())
        {
            cargo.setLineItems(new SaleReturnLineItemIfc[] {lineItem});
        }
        else
        {
            cargo.setLineItems(
                    ((KitHeaderLineItemIfc)lineItem).getKitComponentLineItemArray());
            for (SaleReturnLineItemIfc compItem: ((KitHeaderLineItemIfc)lineItem).getKitComponentLineItemArray())
            {
            	compItem.setPreSplitLineNumber(compItem.getLineNumber());
            }
            cargo.setKitHeader(true);
        }

        //Adding SearchCriteriaIfc to the SerializedItem Cargo
        String geoCode = null;
        if(saleCargo.getStoreStatus() != null &&
                saleCargo.getStoreStatus().getStore() != null)
        {
            geoCode = saleCargo.getStoreStatus().getStore().getGeoCode();
        }
        cargo.setInquiry(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE), saleCargo.getPLUItemID(), "", "", geoCode);


    }
}
