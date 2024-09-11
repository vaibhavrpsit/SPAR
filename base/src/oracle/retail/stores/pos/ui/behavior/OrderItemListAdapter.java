/*===========================================================================
* Copyright (c) 2013, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/behavior/OrderItemListAdapter.java /main/2 2014/01/09 16:23:23 mjwallac Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* mjwallac    01/09/14 - fix null dereferences
* sgu         01/04/13 - add new class
* sgu         01/03/13 - add list adapter
* sgu         01/03/13 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.ui.behavior;

import javax.swing.JList;

import oracle.retail.stores.pos.ui.beans.EYSList;
import oracle.retail.stores.pos.ui.beans.EditOrderItemStatusListEntry;

public class OrderItemListAdapter extends AbstractListAdapter
{
    /** Constants for item action.  */
    private static final String INVALID_SELECTION = "Filled[false],Pending[false],Pick Up[false],Canceled[false]";
    private static final String VALID_PICKUP_SELECTION   = "Filled[false],Pending[false],Pick Up[true],Canceled[true]";
    private static final String VALID_FILL_SELECTION   = "Filled[true],Pending[true],Pick Up[false],Canceled[false]";

    /**
     *  Builds a button state string based on the selections in the list.
     *  This class is only used if xchannel is not enabled.
     *  @param list the JList that triggered the event
     *  @return String representing button states, one of NO_SELECTION, SELECTION,
     *  or DELETE_SELECTION
     */
    public String determineButtonState(JList list)
    {
        EYSList multiList = (EYSList)list;
        int[] indices = multiList.getSelectedIndices();

        // default is to disable buttons
        String result = INVALID_SELECTION;

        // If all item selected allow status editing
        if (indices.length > 0)
        {
            boolean allowEditItemStatus = true;
            EditOrderItemStatusListEntry orderItem = null;
            for (int index : indices)
            {
                orderItem = (EditOrderItemStatusListEntry)list.getModel().getElementAt(index);
                if (!orderItem.getAllowEditItemStatusFlag())
                {
                    allowEditItemStatus = false;
                    break;
                }
            }
            if (orderItem != null && allowEditItemStatus)
            {
                if (orderItem.getEditActionCode().equals(EditOrderItemStatusListEntry.EditActionCode.PICKUP))
                {
                    // allow pickup/cancel
                    result = VALID_PICKUP_SELECTION;
                }
                else
                {
                    // allow fill/pending
                    result = VALID_FILL_SELECTION;
                }

            }
        }

        return result;
    }
}

