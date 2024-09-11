/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/behavior/XChannelOrderItemListAdapter.java /main/3 2014/07/08 12:17:06 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  07/08/14 - CAE order pickup cleanup
 *    sgu       10/29/12 - disable pickup and cancel buttons when not
 *                         applicable
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.behavior;

import javax.swing.JList;
import javax.swing.ListModel;

import oracle.retail.stores.pos.ui.beans.EYSList;
import oracle.retail.stores.pos.ui.beans.EditOrderItemStatusListEntry;

public class XChannelOrderItemListAdapter extends AbstractListAdapter
{
    /** Constants for item action.  */
    private static final String INVALID_SELECTION = "Pick Up[false],Canceled[false]";
    private static final String VALID_SELECTION   = "Pick Up[true],Canceled[true]";

    /**
     *  Builds a button state string based on the selections in the list.
     *  @param list the JList that triggered the event
     *  @return String representing button states, one of NO_SELECTION, SELECTION,
     *  or DELETE_SELECTION
     */
    @SuppressWarnings("rawtypes")
    public String determineButtonState(JList list)
    {
        EYSList multiList = (EYSList)list;
        int[] indices = multiList.getSelectedIndices();
        ListModel posListmodels  =  list.getModel();
        int itemListSize = posListmodels.getSize();
        // default is to disable buttons
        String result = INVALID_SELECTION;
        
        // If all item selected allow status editing
        if (indices.length > 0)
        {
            boolean allowEditItemStatus = true;
            for (int index : indices)
            {
                if (!(index >= itemListSize))
                {
                    EditOrderItemStatusListEntry orderItem = (EditOrderItemStatusListEntry)list.getModel()
                            .getElementAt(index);
                    if (!orderItem.getAllowEditItemStatusFlag())
                    {
                        allowEditItemStatus = false;
                        break;
                    }
                }
            }
            if (allowEditItemStatus)
            {
                result = VALID_SELECTION;
            }
        }

        return result;
    }
}
