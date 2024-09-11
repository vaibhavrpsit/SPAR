/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/KitComponentBean.java /main/13 2014/05/22 09:40:21 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     05/21/14 - Changes to prevent NPE due to line item list being
 *                         null.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:48 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:59 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:12 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
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
 *    Rev 1.0   Aug 29 2003 16:11:02   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:17:54   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:52:58   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:55:52   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:30:48   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   30 Oct 2001 11:47:08   pjf
 * Initial revision.
 * Resolution for POS SCR-8: Item Kits
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports

// swing imports
import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionListener;

import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.foundation.manager.gui.BeanChangeEvent;
import oracle.retail.stores.foundation.manager.gui.BeanChangeListener;
import oracle.retail.stores.pos.ui.behavior.EnableButtonListener;
import oracle.retail.stores.pos.ui.behavior.GlobalButtonListener;
import oracle.retail.stores.pos.ui.behavior.LocalButtonListener;

//--------------------------------------------------------------------------
/**
 * Bean for displaying a list of kit components and allowing single selection of
 * component items in the list.
    @version $Revision: /main/13 $
**/
//--------------------------------------------------------------------------
public class KitComponentBean extends ListBean
{
    /**
     *  revision number
     *  **/
    public static final String revisionNumber            = "$Revision: /main/13 $";
    /**
     * The bean model from the business logic
     * **/
    protected LineItemsModel beanModel                   = null;
    /**
     * The local enable button listener
     * **/
    protected EnableButtonListener localButtonListener   = null;
    /**
     * The global enable button listener
     * **/
    protected EnableButtonListener globalButtonListener  = null;
    /**
     * Handle for a BeanChangeListener
     * **/
    protected BeanChangeListener beanChangeListener      = null;

    //---------------------------------------------------------------------
    /**
        Constructor
    **/
    //---------------------------------------------------------------------
    public KitComponentBean()
    {
        super();
    }

    //---------------------------------------------------------------------
    /**
        Updates the model if it's a LineItemsModel.
    **/
    //---------------------------------------------------------------------
    protected void updateBean()
    {
        if(beanModel instanceof LineItemsModel)
        {
            LineItemsModel model = (LineItemsModel)beanModel;
            
            DefaultListModel listModel = (DefaultListModel)list.getModel();

            listModel.clear();
            
            AbstractTransactionLineItemIfc[] items = model.getLineItems();
            // if there is a line item...
            if (items.length > 0)
            {
                // add the items to the list
                for (int i = 0; i < items.length; i++)
                {
                    listModel.addElement(items[i]);
                }

                // select the first element in the list
                list.setSelectedIndex(0);
            }
            else
            {
                list.setSelectedIndex(EYSList.NO_SELECTION);
            }
            // Do not use this bean to update the screen again.
            model.setBeanHasBeenUpdated(true);
        }
    }

    //---------------------------------------------------------------------
    /**
        Sets the index of the highlighted row (the one with the border) in the model.
    **/
    //---------------------------------------------------------------------
    public void updateModel()
    {
        ((LineItemsModel)beanModel).setHighlightedRow(((EYSList)list).getSelectedRow());
    }

    //---------------------------------------------------------------------
    /**
        Adds (actually sets) the enable button listener on the bean.
        @param listener
    **/
    //---------------------------------------------------------------------
    public void addGlobalButtonListener(GlobalButtonListener listener)
    {
        globalButtonListener = listener;
    }

    //---------------------------------------------------------------------
    /**
        Removes the enable button listener from the bean.
        @param listener
    **/
    //---------------------------------------------------------------------
    public void removeGlobalButtonListener(GlobalButtonListener listener)
    {
        globalButtonListener = null;
    }

    //---------------------------------------------------------------------
    /**
        Gets the enable button listener from the bean.
        @return listener
    **/
    //---------------------------------------------------------------------
    public EnableButtonListener getGlobalButtonListener()
    {
        return globalButtonListener;
    }

    //---------------------------------------------------------------------
    /**
        Adds (actually sets) the enable button listener on the bean.
        @param listener
    **/
    //---------------------------------------------------------------------
    public void addLocalButtonListener(LocalButtonListener listener)
    {
        localButtonListener = listener;
    }

    //---------------------------------------------------------------------
    /**
        Removes the enable button listener from the bean.
        @param listener
    **/
    //---------------------------------------------------------------------
    public void removeLocalButtonListener(LocalButtonListener listener)
    {
        localButtonListener = null;
    }

    //---------------------------------------------------------------------
    /**
        Gets the enable button listener from the bean.
        @return listener
    **/
    //---------------------------------------------------------------------
    public EnableButtonListener getLocalButtonListener()
    {
        return localButtonListener;
    }

    //---------------------------------------------------------------------
    /**
        Adds (actually sets) a BeanChangeListener.
        @param listener BeanChangeListener
    **/
    //---------------------------------------------------------------------
    public void addBeanChangeListener(BeanChangeListener listener)
    {
        beanChangeListener = listener;
    }

    //---------------------------------------------------------------------
    /**
        Removess (actually unsets) a BeanChangeListener.
        @param listener BeanChangeListener
    **/
    //---------------------------------------------------------------------
    public void removeBeanChangeListener(BeanChangeListener listener)
    {
        if (listener != null && listener == beanChangeListener)
        {
            beanChangeListener = null;
        }
    }

//------------------------------------------------------------------------------
/**
 *      Adds a list selection listener to the internal list.
 *      @param l the listener
 */
    public void addListSelectionListener(ListSelectionListener l)
    {
        list.addListSelectionListener(l);
    }

//------------------------------------------------------------------------------
/**
 *      Removes a list selection listener to the internal list.
 *      @param l the listener
 */
    public void removeListSelectionListener(ListSelectionListener l)
    {
        list.removeListSelectionListener(l);
    }
    //---------------------------------------------------------------------
    /**
        Notify bean change listener that bean has changed... and we're
        ready for data.
    **/
    //---------------------------------------------------------------------
    protected void notifyReadyForData()
    {
        if (beanChangeListener != null)
        {
            beanChangeListener.beanChanged(new BeanChangeEvent(this));
        }
    }

}
