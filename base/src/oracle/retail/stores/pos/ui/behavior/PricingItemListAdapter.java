/*===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/behavior/PricingItemListAdapter.java /main/3 2014/07/07 16:57:30 vineesin Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vineesin  07/03/14 - Removing disabling of pricing button for Order Line 
 *                         item during Order Pickup
 *    yiqzhao   06/25/14 - make it consistent with the code in SaleBean.
 *    yiqzhao   06/25/14 - Handle item delete.
 *    yiqzhao   06/24/14 - enable disable pricing button based on the selected
 *                         items.
 *    yiqzhao   06/13/14 - Disable pricing button after retriving order for
 *                         pickup or cancel.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.behavior;



import javax.swing.JList;


import oracle.retail.stores.pos.ui.beans.EYSList;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;




public class PricingItemListAdapter extends AbstractListAdapter 
{

    /** Constants for pricing action.  */
    public static String  DISABLED = "Pricing[false]";
    public static String  ENABLED  = "Pricing[true]";
    

    
    /**
     * Builds a button state string based on the selections in the list.
     * 
     * @param list the JList that triggered the event
     */
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
