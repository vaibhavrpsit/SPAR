/* ===========================================================================
* Copyright (c) 2009, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/behavior/DeleteItemListAdapter.java /main/4 2014/05/09 13:15:49 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   05/02/14 - Disable Delete button for pickup items while doing
 *                         order pickup.
 *    yiqzhao   04/13/12 - delete shipping charge line item and update send
 *                         item
 *    abondala  06/22/10 - Disable Clear Button If ExternalOrder item exists in
 *                         the Transaction
 *    abondala  06/21/10 - handle exception during deleting the items from the
 *                         sell item screen
 *    abondala  06/21/10 - updated
 *    abondala  06/21/10 - New Class to disable Clear button if the Item is
 *                         from ExternalOrder
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.ui.behavior;

import javax.swing.JList;

import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.pos.ui.beans.EYSList;

/**
 * Controls whether Delete Button is enabled when Item is selected.
 * 
 * If the Item is from ExternalOrder, disable the Clear button.
 * 
 * If multiple items are selected and atleast one of them is from 
 * ExternalOrder, disable the Clear button.
 */
public class DeleteItemListAdapter extends AbstractListAdapter
{

    /** string value to disable Delete button */
    public static String DISABLED = "Clear[false]";

    /** string value to enable Delete button */
    public static String ENABLED = "Clear[true]";

    /**
     * Builds a button state string based on the selections in the list.
     * 
     * @param list the JList that triggered the event
     */
    @Override
    public String determineButtonState(JList list)
    {
        EYSList multiList = (EYSList)list;
        
        // default is to disable delete button
        String result = ENABLED;

        // make sure that we have a valid object
        if (list == null)
        {
            return result;
        }
         
        // get the selection
        int listLength = multiList.getSelectedIndices().length;
        int[] selectedIndices = multiList.getSelectedIndices();
        int currentItem = multiList.getSelectedRow();
        int selection = list.getSelectedIndex();

        try
        {
            // check for disabling selection
            if (selection != -1)
            {
                if (listLength <= 1)
                {
                    //single item selected
                    if (multiList.getModel().getElementAt(currentItem) instanceof SaleReturnLineItemIfc)
                    {
                        SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)multiList.getModel().getElementAt(currentItem);
                        if(srli.isFromExternalOrder())
                        {
                            return DISABLED;
                        }
                        if (srli.isShippingCharge() )
                        {
                        	return DISABLED;
                        } 
                        if (srli instanceof OrderLineItemIfc)
                        {
                            if (((OrderLineItemIfc) srli).isPickupCancelLineItem())
                            {
                                return DISABLED;
                            }
                        }
                    }    
                }

                //does list contain any External Orders
                // Same item can exist as an external order as well as local item with different prices
                for (int i = 0; i < selectedIndices.length; i++) 
                {
                    if (multiList.getModel().getElementAt(selectedIndices[i]) instanceof SaleReturnLineItemIfc)
                    {
                        SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)multiList.getModel().getElementAt(selectedIndices[i]);
                        if(srli.isFromExternalOrder())
                        {
                            return DISABLED;
                        }
                        if (srli.isShippingCharge() )
                        {
                        	return DISABLED;
                        }   
                        if (srli instanceof OrderLineItemIfc)
                        {
                            if (((OrderLineItemIfc) srli).isPickupCancelLineItem())
                            {
                                return DISABLED;
                            }
                        }                        
                    } 
                }

                if(currentItem != -1 )
                {
                    if (multiList.getModel().getElementAt(currentItem) instanceof SaleReturnLineItemIfc)
                    {
                        SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)multiList.getModel().getElementAt(currentItem);
                        if(srli.isFromExternalOrder())
                        {
                            return DISABLED;
                        }
                        if (srli.isShippingCharge() )
                        {
                        	return DISABLED;
                        }
                        if (srli instanceof OrderLineItemIfc)
                        {
                            if (((OrderLineItemIfc) srli).isPickupCancelLineItem())
                            {
                                return DISABLED;
                            }
                        }                        
                    } 
                }
                
            }
            else if (currentItem != -1 && listLength != -1)
            {
                //no selection but is current item external order?
                Object obj = (Object)multiList.getModel().getElementAt(currentItem);
                if (obj instanceof SaleReturnLineItemIfc)
                {
                    SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)multiList.getModel().getElementAt(currentItem);
                    if(srli.isFromExternalOrder())
                    {
                        return DISABLED;
                    }
                    if (srli.isShippingCharge() )
                    {
                    	return DISABLED;
                    }
                    if (srli instanceof OrderLineItemIfc)
                    {
                        if (((OrderLineItemIfc) srli).isPickupCancelLineItem())
                        {
                            return DISABLED;
                        }
                    }                    
                }
            }
        }
        catch(Exception ex)
        {
            //when an item is deleted from the list, this may throw exception which can be ignored.
        }
            
        return result;
    }

}
