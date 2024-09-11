/* =============================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * =============================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ReturnResponseBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:53 mszekely Exp $
 * =============================================================================
 * NOTES
 * Created by Lucy Zhao (Oracle Consulting) for POS-RM integration.
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    rkar      11/07/08 - Additions/changes for POS-RM integration
 *
 * =============================================================================
 */
package oracle.retail.stores.pos.ui.beans;


import java.util.Hashtable;
import java.util.Properties;


import javax.swing.DefaultListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;

import oracle.retail.stores.foundation.manager.gui.BeanChangeEvent;
import oracle.retail.stores.foundation.manager.gui.BeanChangeListener;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.OnlineStatusContainer;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.behavior.EnableButtonListener;
import oracle.retail.stores.pos.ui.behavior.GlobalButtonListener;
import oracle.retail.stores.pos.ui.behavior.LocalButtonListener;

//---------------------------------------------------------------------
/**
 * This bean uses the ListBeanModel
 * @see oracle.retail.stores.pos.ui.beans.ReturnResponserBeanModelTest
 */
//---------------------------------------------------------------------
public class ReturnResponseBean extends ListBean
                                implements DocumentListener
{
    private static final long serialVersionUID = -5463358104093346445L;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        The action to manager override when clearing.
    **/
    public    static final String CLEAR_ACTION             = "Clear";

    /** Constant for the next action name **/
    protected static final String NEXT_ACTION           = "Next";
    
    /** The local enable button listener **/
    protected EnableButtonListener localButtonListener  = null;

    /**
        The global enable button listener
    **/
    protected EnableButtonListener globalButtonListener = null;

    /**
        array of rows to delete
        Object array of ReturnResponseLineItemIfc objects
    **/
    protected int[] rowsToDelete                         = null;


    /** Handle for a BeanChangeListener **/
    protected BeanChangeListener beanChangeListener     = null;

    //---------------------------------------------------------------------
    /**
     * Constructor
     */
    //---------------------------------------------------------------------
    public ReturnResponseBean()
    {
        super();
        setName("ReturnResponseBean");
    }

    //---------------------------------------------------------------------
    /**
        Activate this screen.
    **/
    //---------------------------------------------------------------------
    public void activate()
    {
        super.activate();
        //getTotalsBean().activate();
        list.setEnabled(true);
    }

    //--------------------------------------------------------------------------
    /**
     *  Configures the list.
     */
    protected void configureList()
    {
        super.configureList();
        list.setAutoscrolls(true);
    }

    //---------------------------------------------------------------------
    /**
     *  Set the properties to be used by this bean
        @param props the propeties object
     */
    //---------------------------------------------------------------------
    public void setProps(Properties props)
    {
        super.setProps(props);

        if (renderer != null && renderer instanceof AbstractListRenderer)
        {
           ((AbstractListRenderer)renderer).setProps(props);
        }
    }


    //---------------------------------------------------------------------
    /**
     * Updates the model
     * @see oracle.retail.stores.pos.ui.beans.ReturnResponserBeanModelTest
     */
    //---------------------------------------------------------------------
    public void updateModel()
    {
        // verify that the beanModel is of the expected type
        if (LineItemsModel.class.isInstance(beanModel))
        {                               // Begin bean model is expected type

            LineItemsModel model = (LineItemsModel)beanModel;

            if (rowsToDelete != null)
            {
                model.setRowsToDelete(rowsToDelete);
                rowsToDelete = null;
            }
            else
            {
                model.setSelectedRows(list.getAllSelectedRows());
            }

        }                               // End bean model is expected types
    }

    //---------------------------------------------------------------------
    /**
     * Update the bean if the model has changed
     */
    //---------------------------------------------------------------------
    protected void updateBean()
    {
        // verify that the beanModel is of the expected type
        if (LineItemsModel.class.isInstance(beanModel))
        {                               // Begin bean model is expected type
            LineItemsModel model = (LineItemsModel)beanModel;
            StatusBeanModel statusBeanModel = model.getStatusBeanModel();

            if ( statusBeanModel != null )
            {
                OnlineStatusContainer onlineStatusContainer = statusBeanModel.getStatusContainer();

                if ( onlineStatusContainer != null )
                {
                    Hashtable hashtable = onlineStatusContainer.getStatusHash();
                    Object object = hashtable.get(new Integer(POSUIManagerIfc.TRAINING_MODE_STATUS));
                    if ( object != null )
                    {
                        Boolean trainingMode = (Boolean) object;
                        setApplicationBackground(trainingMode.booleanValue());
                    }
                }
            }


            POSListModel listModel = model.getListModel();
            getList().setModel(listModel);

            // If there is a line item ...
            if (listModel != null && listModel.getSize() > 0)
            {
                list.clearSelection();

                // Check for highlight position
                if (model.getMoveHighlightToTop())
                {
                    list.setSelectedIndex(0);
                }
                else
                {
                    if (model.getSelectedRows() != null && (model.getSelectedRows().length > 0))
                    {
                        list.setSelectedIndices(model.getSelectedRows());
                    }
                    else
                    {
                        if ( model.getSelectedRow()>0 )
                        {
                            list.setSelectedIndex(model.getSelectedRow());
                        }
                    }
                }
            }
            else
            {
                getList().setSelectedIndex(EYSList.NO_SELECTION);
            }
            // Do not use this bean to update the screen again.
            model.setBeanHasBeenUpdated(true);

            // Tell the Scanner Session that we're ready to handle scanner data
            notifyReadyForData();
        }                               // End bean model is expected type
    }

    //---------------------------------------------------------------------
    /**
     * Deactivates the bean
     */
    //---------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
       //deactivateListeners();
        list.setEnabled(false);
    }


    //---------------------------------------------------------------------
    /**
     *
     * @param index int
     * @param item java.lang.Object
     */
    //---------------------------------------------------------------------
    public void modifyItem(int index, Object item)
    {
        ((DefaultListModel)list.getModel()).setElementAt(item, index);
    }

    /* (non-Javadoc)
     * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
     */
    public void removeUpdate(DocumentEvent e)
    {
        checkAndEnableButtons(e);
    }

    /* (non-Javadoc)
     * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
     */
    public void changedUpdate(DocumentEvent e)
    {
        checkAndEnableButtons(e);
    }

    /* (non-Javadoc)
     * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
     */
    public void insertUpdate(DocumentEvent e)
    {
        checkAndEnableButtons(e);
    }

    //--------------------------------------------------------------------------
    /**
        Adds (actually sets) the enable button listener on the bean.
        @param listener the global listener button
    **/
    //--------------------------------------------------------------------------
    public void addGlobalButtonListener(GlobalButtonListener listener)
    {
        globalButtonListener = listener;
    }

    //--------------------------------------------------------------------------
    /**
        Removes the enable button listener from the bean. <P>
        @param listener the global button listener
    **/
    //--------------------------------------------------------------------------
    public void removeGlobalButtonListener(GlobalButtonListener listener)
    {
        globalButtonListener = null;
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

  //------------------------------------------------------------------------------
    /**
     *    Adds a list selection listener to the internal list.
     *    @param l the listener
     */
        public void addListSelectionListener(ListSelectionListener l)
        {
            list.addListSelectionListener(l);
        }

    //------------------------------------------------------------------------------
    /**
     *    Removes a list selection listener to the internal list.
     *    @param l the listener
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
    
    //--------------------------------------------------------------------------
    /**
        Determines if the response field has text and sets the "Clear"
        button appropriately. <P>
        @Param evt the document event
    **/
    //--------------------------------------------------------------------------
    public void checkAndEnableButtons(DocumentEvent evt)
    {
        if (evt.getDocument().getLength() > 0)
        {
            globalButtonListener.enableButton(NEXT_ACTION, true);
            //globalButtonListener.enableButton(CLEAR_ACTION, true);
        }
        else
        {
            globalButtonListener.enableButton(NEXT_ACTION, false);
        }
        globalButtonListener.enableButton(CLEAR_ACTION, false);
    }


    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: ReturnResponseBean (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

}
