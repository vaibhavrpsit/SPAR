/*===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EditOrderItemStatusBean.java /main/5 2014/07/10 17:48:30 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     07/10/14 - Fix the index out of boundary.
* vtemker     04/16/13 - Moved constants in OrderLineItemIfc to
*                        OrderConstantsIfc in common project
* sgu         01/03/13 - add fill and pending
* sgu         10/29/12 - disable pickup and cancel buttons when not applicable
* sgu         10/04/12 - split order item for pickup
* sgu         10/04/12 - add suport to split order line for partial
*                        pickup/cancel
* sgu         10/02/12 - add new file
* sgu         10/02/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.ui.beans;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.pos.ui.POSListModel;

public class EditOrderItemStatusBean extends ListBean
implements ActionListener
{
    /**
     *
     */
    private static final long serialVersionUID = -3426469535646371049L;

    /**
     * Constants for button names.
     **/
    public static final String FILLED   = "Filled";
    public static final String PENDING  = "Pending";
    public static final String PICKUP   = "Pick Up";
    public static final String CANCELED = "Canceled";

    //---------------------------------------------------------------------
    /**
        Constructor
    **/
    //---------------------------------------------------------------------
    public EditOrderItemStatusBean()
    {
        super();
        setName("EditOrderItemStatusBean");
    }

    //---------------------------------------------------------------------
    /**
        Updates the model if it has been changed.
    **/
    //---------------------------------------------------------------------
    public void updateBean()
    {
        if(beanModel != null && beanModel instanceof ListBeanModel)
        {
            ListBeanModel model = (ListBeanModel) beanModel;
            POSListModel listModel = model.getListModel();
            getList().setModel(listModel);

            Object[] values = model.getListArray();
            if (values == null)
            {
                values =new Object[0];
            }

            listModel.clear();

            for (int i = 0; i < values.length; i++)
            {
                listModel.addElement(values[i]);
            }

            //get multi select list and sets its model to listModel
            list.setModel(listModel);
        }
    }

//  ----------------------------------------------------------
    /**
        Dynamicaly changes the status of the order items
        <p>
        @param evt an ActionEvent
    **/
    //----------------------------------------------------------
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent evt)
    {
        /*
        * Get the indices of all selected items
        */
        int[] allSelected = list.getAllSelectedRows();
        boolean firstBeep  = true;

        int[] indices = new int[allSelected.length];
        for (int j=0; j < allSelected.length; j++)
        {
            indices[j] = allSelected[j];
        }

        // get the action command corresponding to
        // relavent uicfg.xml
        String actionName = evt.getActionCommand();
        int    status     = OrderConstantsIfc.ORDER_ITEM_STATUS_UNDEFINED;

        if(actionName.equals(FILLED))
        {
            status = OrderConstantsIfc.ORDER_ITEM_STATUS_FILLED;
        }
        else if(actionName.equals(PENDING))
        {
            status = OrderConstantsIfc.ORDER_ITEM_STATUS_PENDING;
        }
        else if(actionName.equals(PICKUP))
        {
            status = OrderConstantsIfc.ORDER_ITEM_STATUS_PICK_UP;
        }
        else if(actionName.equals(CANCELED))
        {
            status = OrderConstantsIfc.ORDER_ITEM_STATUS_CANCEL;
        }

        Object[] values = ((ListBeanModel)beanModel).getListArray();

        for (int i=0; i < indices.length; i++)
        {
            if (indices[i]<values.length)
            {
                EditOrderItemStatusListEntry entry = (EditOrderItemStatusListEntry)values[indices[i]];
                if (entry.getAllowEditItemStatusFlag())
                {
                    entry.getStatus().changeStatus(status);
                }
                else if (firstBeep)
                {
                    Toolkit.getDefaultToolkit().beep();
                    firstBeep = false;
                }
            }
        }

        // remove all the selected rows and their highlighting
        // from the user interface
        list.getSelectionModel().clearSelection();

        // select and move the cursor to the first row
        list.setSelectedIndex(0);

        // must call this method to clear the selections from
        // the list model

        if (list.getSelectedValue() != null)
        {
            ((Component)list.getSelectedValue()).repaint();
        }
        else
        {
            list.repaint();
        }
    }

}


