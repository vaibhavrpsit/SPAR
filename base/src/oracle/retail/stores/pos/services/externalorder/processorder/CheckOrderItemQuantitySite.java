/* ===========================================================================
* Copyright (c) 2010, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/processorder/CheckOrderItemQuantitySite.java /main/10 2012/09/28 17:32:43 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   09/27/12 - modify the way to check the item has related items.
 *    yiqzhao   09/26/12 - refactor related item to add cross sell, upsell and
 *                         substitute, remove pick one and pick many
 *    yiqzhao   09/20/12 - Remove DisplayPriority and
 *                         RelatedItemGroupAssociation columns from
 *                         RelationItemAssociation table. Remove
 *                         RelatedItemGroupContainer java object.
 *    acadar    08/31/10 - add check for unit of measure
 *    acadar    06/08/10 - changes for signature capture, disable txn send, and
 *                         discounts
 *    acadar    06/02/10 - refactoring
 *    acadar    06/02/10 - signature capture changes
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    05/21/10 - renamed from _externalorder to externalorder
 *    sgu       05/20/10 - refactor site code due to api changes
 *    acadar    05/17/10 - temporarily rename the package
 *    acadar    05/17/10 - incorporated feedback from code review
 *    acadar    05/17/10 - additional logic added for processing orders
 *    acadar    05/14/10 - initial version for external order processing
 *    acadar    05/14/10 - initial version
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.externalorder.processorder;

import java.math.BigDecimal;

import oracle.retail.stores.domain.externalorder.ExternalOrderSaleItemIfc;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.RelatedItemGroupIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;




/**
 * This site checks the quantity of the order items.
 * If a line item came from an external order and requires operator
 * interaction (Enter Serial Number, Enter Age, etc.) and has a
 * quantity greater than 1, an error dialog is displayed for the operator.
 * The operator acknowledges the error and the use case ends and returns
 * to the calling use case. No line items are retrieved into the transaction.
 *
 * @author acadar
 *
 */
public class CheckOrderItemQuantitySite extends PosSiteActionAdapter
{


    /**
        Constant for unit of measure UNITS.
     **/
    public static final String UNITS = "UN";

    /**
     *  Serial Version UID
     */
    private static final long serialVersionUID = -2420684126186584116L;

    /**
     * For each order item check the quantity
     * @param BusIfc
     */
    public void arrive(BusIfc bus)
    {
        ProcessOrderCargo cargo = (ProcessOrderCargo)bus.getCargo();
        Letter letter = new Letter(CommonLetterIfc.NEXT);
        ExternalOrderSaleItemIfc orderItem = cargo.getCurrentExternalOrderItem();
        BigDecimal quantity = orderItem.getQuantity();
        //If a line item came from an external order and requires operator
        //interaction (Enter Serial Number, Enter Age, etc.) and has a
        //quantity greater than 1, an error dialog is displayed for the operator.
        //The operator acknowledges the error and the use case ends and returns
        //to the calling use case. No line items are retrieved into the transaction.
        boolean isExpectedQuantity = true;
        if (quantity.compareTo(BigDecimal.ONE) > 0)
        {
            isExpectedQuantity = checkExpectedQuantity(orderItem);
        }

        if(!isExpectedQuantity)
        {
            //go to Cancel order site
            letter = new Letter(CommonLetterIfc.FAILURE);
        }

        // go to item validation station
        bus.mail(letter, BusIfc.CURRENT);

    }

    /**
     * If a line item came from an external order and requires operator
     * interaction (Enter Serial Number, Enter Age, etc.) and has a
     * quantity greater than 1, an error dialog is displayed for the operator.
     * The operator acknowledges the error and the use case ends and returns
     * to the calling use case. No line items are retrieved into the transaction.
     */
    private boolean checkExpectedQuantity(ExternalOrderSaleItemIfc orderItem)
    {
        boolean isExpectedQuantity = true;
//      get the pluitem
        PLUItemIfc plu = orderItem.getPLUItem();
        if (plu != null)
        {
            isExpectedQuantity = isQuantityValid(plu);

        }
        else
        {
            PLUItemIfc[] items = orderItem.getPLUItems();
            if(items != null && items.length > 0)
            {

                for (int i = 0; i < items.length; i++)
                {
                    isExpectedQuantity = isQuantityValid(plu);
                    if(!isExpectedQuantity)
                    {
                        break;
                    }

                }
            }
        }

        return isExpectedQuantity;
    }

    /**
     *
     * @param plu
     * @return
     */
    private boolean isQuantityValid(PLUItemIfc plu)
    {
        boolean isExpectedQuantity = true;
        if ( plu.getUnitOfMeasure().getUnitID().equals(UNITS) ||
             plu.isAlterationItem() || plu.isItemSizeRequired() ||plu.isSerializedItem() || plu instanceof GiftCardPLUItemIfc ||
             !plu.getRelatedItemContainer().isEmpty())
       {
                isExpectedQuantity = false;
       }
       return isExpectedQuantity;
    }




}
