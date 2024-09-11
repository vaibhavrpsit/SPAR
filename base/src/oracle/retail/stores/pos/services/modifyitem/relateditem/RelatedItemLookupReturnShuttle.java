/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/relateditem/RelatedItemLookupReturnShuttle.java /main/2 2013/03/05 14:03:17 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     02/27/13 - set setPluDataFromCrossChannelSource flag from
*                        inquiryCargo to SaleReturnLineItem.
* yiqzhao     09/28/12 - remove getRevision method.
* yiqzhao     09/26/12 - refactor related item to add cross sell, upsell and
*                        substitute, remove pick one and pick many
* yiqzhao     09/20/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.modifyitem.relateditem;

import java.math.BigDecimal;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
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
public class RelatedItemLookupReturnShuttle extends FinancialCargoShuttle implements ShuttleIfc
{
    private static final long serialVersionUID = -1650087188722860781L;
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/2 $";
    /**
     * Child service's cargo
     */
    protected ItemInquiryCargo inquiryCargo = null;

    /**
     * This shuttle copies information from the Item Inquiry service back to the
     * Modify Item service.
     * 
     * @param bus Service Bus to copy cargo from.
     */
    @Override
    public void load(BusIfc bus)
    {
        inquiryCargo = (ItemInquiryCargo)bus.getCargo();
    }

    /**
     * Copies the new item to the cargo for the Modify Item service.
     * 
     * @param bus Service Bus to copy cargo to.
     */
    @Override
    public void unload(BusIfc bus)
    {
        RelatedItemCargo cargo = (RelatedItemCargo)bus.getCargo();
        if (inquiryCargo.getModifiedFlag())
        {
            PLUItemIfc pluItem = inquiryCargo.getPLUItem();
            BigDecimal itemQuantity = inquiryCargo.getItemQuantity();

            if (pluItem != null)
            {
                cargo.setTransaction((SaleReturnTransactionIfc)inquiryCargo.getTransaction());

                if (cargo.getTransaction() == null)
                {
                    cargo.initializeTransaction(bus);
                }
                TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc) bus.getManager(TransactionUtilityManagerIfc.TYPE);
                RelatedItemTransactionInfoIfc relatedItemTransInfo = utility.getRelatedItemTransInfo();

                cargo.setPLUItem(pluItem);
                cargo.setItemQuantity(itemQuantity);
                cargo.setItemSerial(inquiryCargo.getItemSerial());
                SaleReturnLineItemIfc srli = cargo.getTransaction().addPLUItem(cargo.getPLUItem(),
                        cargo.getItemQuantity());
                srli.setRelatedItemReturnable(relatedItemTransInfo.getRelatedItem().isReturnable());
                srli.setRelatedItemDeleteable(relatedItemTransInfo.getRelatedItem().isDeleteable());
                srli.setRelatedItemSequenceNumber(relatedItemTransInfo.getPrimaryItemSequenceNumber());
                if ( inquiryCargo.isItemFromWebStore() )
                {
                    srli.setPluDataFromCrossChannelSource(true);
                }

                // set the related item cargo
                cargo.setRelatedItemGroupName(relatedItemTransInfo.getRelatedItemGroup());
                cargo.setNextRelatedItem(relatedItemTransInfo.getNextRelatedItem());
                cargo.setPrimaryItemSequenceNumber(relatedItemTransInfo.getPrimaryItemSequenceNumber());
                cargo.setToBeAddRelatedItems(relatedItemTransInfo.getRelatedItems());
                //cargo.setGroupNumber(relatedItemTransInfo.getRelatedItemGroupNumber());

                // set old sale return line item to have new related item sale
                // return line item in it
                // for use with deletes
                SaleReturnLineItemIfc[] items = (SaleReturnLineItemIfc[])cargo.getTransaction().getLineItems();
                items[cargo.getPrimaryItemSequenceNumber()].addRelatedItemLineItem(srli);
                cargo.getTransaction().setLineItems(items);

                if (pluItem.isAlterationItem())
                {
                    srli.setAlterationItemFlag(true);
                }
                cargo.setLineItem(srli);
            }
        }

        // get the transaction if it was created for the date.
        if (inquiryCargo.getTransaction() != null
                && ((SaleReturnTransactionIfc)inquiryCargo.getTransaction()).getAgeRestrictedDOB() != null)
        {
            cargo.setTransaction((SaleReturnTransactionIfc)inquiryCargo.getTransaction());
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.FinancialCargoShuttle#toString()
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = new String("Class:  RelatedItemLookupReturnShuttle"  + hashCode());

        // pass back result
        return (strResult);
    }

}