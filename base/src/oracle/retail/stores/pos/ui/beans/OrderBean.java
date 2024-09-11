/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/OrderBean.java /main/18 2014/05/22 09:40:21 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     05/21/14 - Changes to prevent NPE due to line item list being
 *                         null.
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    sgu       05/22/12 - remove order filled status
 *    sgu       05/21/12 - remove order printed status
 *    rrkohli   05/06/11 - pos ui quick win
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:13 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:50 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:51 PM  Robert Pearse
 *
 *   Revision 1.4  2004/09/29 16:30:24  mweis
 *   @scr 7012 Special Order and Inventory integration -- canceling the entire order.
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:11:20   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Aug 14 2002 18:18:06   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:54:40   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:56:30   msg
 * Initial revision.
 *
 *    Rev 1.11   Mar 02 2002 17:58:54   mpm
 * Internationalized order UI.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.10   06 Feb 2002 18:36:36   cir
 * Removed check for returned status
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.9   05 Feb 2002 18:00:18   cir
 * Replaced totalsBean with getTotalsBean()
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.8   01 Feb 2002 14:46:16   cir
 * Added Returned status to actionPerformed()
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.7   Jan 25 2002 12:12:00   dfh
 * prevent changing voided item status
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *
 *    Rev 1.6   Jan 20 2002 18:25:24   mpm
 *
 * Cleaned up rendering (not completely) on order items.
 *
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Java awt components
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.behavior.LocalButtonListener;

//--------------------------------------------------------------------------
/**
    The OrderBean presents a list of items from an Order that
    the user can select to modify.
    @version $Revision: /main/18 $
**/
//--------------------------------------------------------------------------
public class OrderBean
extends SaleBean implements
ActionListener
{
    /** revision number **/
    public static final String revisionNumber                       = "$Revision: /main/18 $";

    /**
        Constants for button names.
    **/
    public static final String PICKUP   = "Pick Up";
    public static final String CANCELED = "Canceled";

    /** Used to determine if a particular row is highlighted **/
    protected boolean                       highlighted             = true;
    /** Listens for order events. **/
    protected LocalButtonListener           localButtonListener     = null;

    /**
        This array holds the status that each line item started with.
        If the original status was Pickup or Cancelled, it cannot be
        changed.
    **/
    protected int[]                         originalStatus          = null;

    //---------------------------------------------------------------------
    /**
        Constructor
    **/
    //---------------------------------------------------------------------
    public OrderBean()
    {
        super();
        setName("OrderBean");
    }

    //---------------------------------------------------------------------
    /**
        Updates the model if it has been changed.
    **/
    //---------------------------------------------------------------------
    public void updateBean()
    {
        if(beanModel != null && beanModel instanceof LineItemsModel)
        {
            LineItemsModel model = (LineItemsModel) beanModel;
            POSListModel listModel = model.getListModel();
            getList().setModel(listModel);

            //get size of current DefaultListModel
            int oldNumberOfItems = list.getModel().getSize();

            Object[] values = model.getLineItems();
            originalStatus = new int[values.length];

            listModel.clear();

            for (int i = 0; i < values.length; i++)
            {
                listModel.addElement(values[i]);
                originalStatus[i] =
                  ((SaleReturnLineItemIfc) values[i]).getOrderItemStatus().getStatus().getStatus();
            }

            //get multi select list and sets its model to listModel
            list.setModel(listModel);

            /*if (model.getTotalsBeanModel() != null) commented for pos ui quickwin
            {
                getTotalsBean().setModel(model.getTotalsBeanModel());
            }*/

            // make sure selected row is within bounds
            if (model.getItemModifiedIndex() == EYSList.NO_SELECTION
                || oldNumberOfItems < listModel.getSize())
            {
                int index = listModel.getSize() - 1;
                list.setSelectedIndex(index);
            }
        }
    }
    //----------------------------------------------------------
    /**
        Dynamicaly changes the status of the order items
        <p>
        @param evt an ActionEvent
    **/
    //----------------------------------------------------------
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

        if(actionName.equals(PICKUP))
        {
            status = OrderConstantsIfc.ORDER_ITEM_STATUS_PICK_UP;
        }
        else if(actionName.equals(CANCELED))
        {
            status = OrderConstantsIfc.ORDER_ITEM_STATUS_CANCEL;
        }

        Object[] values = ((LineItemsModel)beanModel).getLineItems();

        for (int i=0; i < indices.length; i++)
        {
            if (originalStatus[indices[i]] != OrderConstantsIfc.ORDER_ITEM_STATUS_PICKED_UP &&
                originalStatus[indices[i]] != OrderConstantsIfc.ORDER_ITEM_STATUS_CANCELED &&
                originalStatus[indices[i]] != OrderConstantsIfc.ORDER_ITEM_STATUS_VOIDED)
            {
               ((SaleReturnLineItemIfc) values[indices[i]]).
                 getOrderItemStatus().getStatus().changeStatus(status);
            }
            else if (firstBeep)
            {
                Toolkit.getDefaultToolkit().beep();
                firstBeep = false;
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

    //---------------------------------------------------------------------
    /**
        Adds (actually sets) the yesno listener on the Yes/No button.
        @Param listener the Browser Button Listener
    **/
    //---------------------------------------------------------------------
    public void addBrowserButtonListener(LocalButtonListener listener)
    {
        localButtonListener = listener;
    }

    //---------------------------------------------------------------------
    /**
        Removes (actually resets) the yes no listener on the Yes/No button.
        @Param listener the Browser Button Listener
    **/
    //---------------------------------------------------------------------
    public void removeBrowserButtonListener(LocalButtonListener listener)
    {
        localButtonListener = null;
    }
     //---------------------------------------------------------------------
    /**
        Sets or unsets the highlight of the selected row.
        Highlighted is an independent internal state of this Bean.
        It is set or unset via this method.
        <p>
        @param highlighted if true, then the selected row is highlighted.
                           Otherwise no selected row is highlighted.
    **/
    //---------------------------------------------------------------------
    protected void setHighlight(boolean highlighted)
    {
        this.highlighted=highlighted;
    }

    //---------------------------------------------------------------------
    /**
        Starts the part when it is run as an application
        <p>
        @param args command line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(java.lang.String[] args)
    {
        java.awt.Frame frame = new java.awt.Frame();
        OrderBean bean = new OrderBean();
        frame.add("Center", bean);
        frame.setSize(bean.getSize());
        frame.setVisible(true);
    }
}
