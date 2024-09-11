/* ===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/behavior/GiftReceiptItemListAdapter.java /main/1 2014/05/22 14:49:08 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/22/14 - use list adapter to enable gift receipt button
 *    cgreene   05/22/14 - initial version
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.ui.behavior;

import javax.swing.JList;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.beans.EYSList;

/**
 * Controls whether {@link CommonActionsIfc#GIFT_RECEIPT} button is enabled when
 * an item is selected.
 * <p>
 * If any item is selected and none are from an order that is not yet picked up,
 * the GiftReceipt button will be enabled.
 * 
 * @since 14.1
 * @author cgreene
 */
public class GiftReceiptItemListAdapter extends AbstractListAdapter
{
    private static final Logger logger = Logger.getLogger(GiftReceiptItemListAdapter.class);

    /** string value to enable {@link CommonActionsIfc#GIFT_RECEIPT} button */
    public static final String ENABLED = CommonActionsIfc.GIFT_RECEIPT + "["+ true + "]";

    /** string value to disable {@link CommonActionsIfc#GIFT_RECEIPT} button */
    public static final String DISABLED = CommonActionsIfc.GIFT_RECEIPT + "["+ false + "]";

    /**
     * Builds a button state string based on the selections in the list.
     * 
     * @param list the JList that triggered the event
     * @return either {@link #ENABLED} or {@link #DISABLED}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public String determineButtonState(JList list)
    {
        EYSList multiList = (EYSList)list;

        if (multiList == null)
        {
            logger.debug("List is null. No GiftReceipt button.");
            return DISABLED;
        }

        int[] selectedIndices = multiList.getAllSelectedRows();

        if (selectedIndices.length == 0)
        {
            logger.debug("No selection. No GiftReceipt button.");
            return DISABLED;
        }

        logger.debug(selectedIndices.length + " selections.");
        try
        {
            for (int index : selectedIndices)
            {
                Object selection = multiList.getModel().getElementAt(index);
                if (selection instanceof OrderLineItemIfc)
                {
                    OrderLineItemIfc lineItem = (OrderLineItemIfc)selection;
                    OrderItemStatusIfc itemStatus = lineItem.getOrderItemStatus();
                    if (itemStatus.getStatus().getStatus() != OrderConstantsIfc.ORDER_ITEM_STATUS_PICKED_UP)
                    {
                        logger.debug("An order line is selected that is not picked up. No GiftReceipt button.");
                        return DISABLED;
                    }
                }
            }
        }
        catch (Exception ex)
        {
            logger.debug("An error occurred upon list selection. Ignoring." + ex);
        }

        logger.debug("Returning enabled by default.");
        return ENABLED;
    }
}