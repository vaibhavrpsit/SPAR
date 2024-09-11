/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/TriListBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:45 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Properties;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.UIUtilities;

//---------------------------------------------------------------------
/**
    This class displays the item inquiry for multiple store look ups
    @version
 **/
//---------------------------------------------------------------------
public class TriListBean extends DualListBean {

	/** header for bottom list */
    protected BaseHeaderBean bottomHeader;

    /** list to display in bottom section */
    protected EYSList bottomList;

    /** list to display in bottom section */
    protected ListCellRenderer bottomRenderer;

    /** list to display in bottom section */
    protected JScrollPane bottomScrollPane;
	
    //--------------------------------------------------------------------------
    /**
     *    Default constructor.
     */
    //--------------------------------------------------------------------------
    public TriListBean()
    {
        super();
        beanModel = new TriListBeanModel();

    }

    //--------------------------------------------------------------------------
    /**
     *     Activates this class.
     */
    //--------------------------------------------------------------------------    
    public void activate()
    {
        super.activate();

        if(bottomScrollPane == null)
        {
            bottomScrollPane = getBottomScrollPane();
        }
        bottomHeader.activate();
        getList().addFocusListener(this);
    }
	
    //--------------------------------------------------------------------------
    /**
     *     Deactivates this class.
     */
    //--------------------------------------------------------------------------    
    public void deactivate()
    {
        super.deactivate();
        getList().removeFocusListener(this);
    }

    //--------------------------------------------------------------------------
    /**
     *  Sets the header bean.
     *  @param propValue the class name of the header bean
     */
    //-------------------------------------------------------------------------- 
    public void setBottomHeader(String propValue)
    {
        if(propValue != null)
        {
            bottomHeader =
                (BaseHeaderBean)UIUtilities.getNamedClass(propValue);
        }
        if(bottomHeader == null)
        {
            bottomHeader = new BaseHeaderBean();
        }
        if(bottomScrollPane != null)
        {
            bottomScrollPane.setColumnHeaderView(bottomHeader);
        }
    }
    
    //--------------------------------------------------------------------------
    /**
     *  Convenience method for setting alignments of the header labels.
     *  @param aligns the alignments for the labels
     */
    //-------------------------------------------------------------------------- 
    public void setBottomLabelAlignments(String aligns)
    {
        getBottomHeader().setAlignments(aligns);
    }

    //--------------------------------------------------------------------------
    /**
     *  Convenience method for setting property tags of the header labels.
     *  @param tags the property tags for the labels
     */
    //-------------------------------------------------------------------------- 
    public void setBottomLabelTags(String tags)
    {
        getBottomHeader().setLabelTags(tags);
    }

    //--------------------------------------------------------------------------
    /**
     *  Convenience method for setting text of the header labels.
     *  @param text the text of the labels
     */
    //--------------------------------------------------------------------------
    public void setBottomLabelText(String text)
    {
        getBottomHeader().setLabelText(text);
    }

    //--------------------------------------------------------------------------
    /**
     *  Convenience method for setting the weights of the header labels.
     *  @param text the weights of the labels
     */
    //--------------------------------------------------------------------------
    public void setBottomLabelWeights(String text)
    {
        getBottomHeader().setLabelWeights(text);
    }

    //--------------------------------------------------------------------------
    /**
     *  Sets the list.
     *  @param propValue the class name of the list
     */
    //--------------------------------------------------------------------------    
    public void setBottomList(String propValue)
    {
        if(propValue != null)
        {
            bottomList =
                (EYSList)UIUtilities.getNamedClass(propValue);
        }
        if(bottomList == null)
        {
            bottomList = new EYSList();
        }
        if(bottomScrollPane != null)
        {
            bottomScrollPane.getViewport().setView(list);
        }
        configureBottomList();
    }

    //--------------------------------------------------------------------------
    /**
     *  Sets the scroll pane.
     *  @param propValue the class name of the scroll pane
     */
    //--------------------------------------------------------------------------
    public void setBottomScrollPane(String propValue)
    {
        // if there is an existing scrollPane, remove it
        if(bottomScrollPane != null)
        {
            remove(bottomScrollPane);
        }
        // try to create the new scrollPane using reflection
        if(propValue != null)
        {
            bottomScrollPane =
                (JScrollPane)UIUtilities.getNamedClass(propValue);
        }
        // if the creation failed, use the default scrollPane
        if(bottomScrollPane == null)
        {
            bottomScrollPane = new JScrollPane();
        }

        configureBottomScrollPane();
    }

    //--------------------------------------------------------------------------
    /**
     *  Sets the list renderer.
     *  @param propValue the class name of the renderer
     */
    //--------------------------------------------------------------------------
    public void setBottomRenderer(String propValue)
    {
        if(propValue != null)
        {
            bottomRenderer = (AbstractListRenderer)UIUtilities.getNamedClass(propValue);
            ((AbstractListRenderer)bottomRenderer).initialize();
        }
        if(bottomRenderer == null)
        {
            bottomRenderer = new DefaultListCellRenderer();
        }
        if(bottomList != null)
        {
            bottomList.setCellRenderer(bottomRenderer);
        }
    }

    //--------------------------------------------------------------------------
    /**
     *  Updates the bean model before sending it to the ui.
     */
    public void updateModel()
    {
        super.updateModel();

        if(beanModel != null && beanModel instanceof TriListBeanModel)
        {
            TriListBeanModel model = (TriListBeanModel)beanModel;

            if(selectionMode == NO_SELECTION_MODE)
            {
                model.setSelectedRow(-1);
                model.setSelectedValue(null);
            }
            else
            {
                int row = list.getSelectedRow();
                model.setSelectedRow(row);

                if(row != -1)
                {
                    model.setSelectedValue(list.getModel().getElementAt(row));
                }
                else
                {
                    model.setSelectedValue(null);
                }
                if(selectionMode == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
                {
                    model.setSelectedRows(list.getAllSelectedRows());
                }
            }
            //model.setListModel(new Object[0]);
        }
    }

    //--------------------------------------------------------------------------
    /**
     *  Configures the list.
     */
    //--------------------------------------------------------------------------
    protected void configureBottomList()
    {
         uiFactory.configureUIComponent(bottomList, "List");

         if(bottomRenderer != null)
         {
            bottomList.setCellRenderer(bottomRenderer);
         }
         if(selectionMode == NO_SELECTION_MODE)
         {
            bottomList.setEnabled(false);
            bottomList.setFocusable(false);
            bottomList.setRequestFocusEnabled(false);
         }
         else
         {
            bottomList.setSelectionMode(selectionMode);
            bottomList.setEnabled(true);
         }
    }

    //--------------------------------------------------------------------------
    /**
     *  Configures the scrollPane.
     */
    //--------------------------------------------------------------------------
    protected void configureBottomScrollPane()
    {
        uiFactory.configureUIComponent(bottomScrollPane, "ScrollPane");

        bottomScrollPane.setVerticalScrollBar(new EYSScrollBar());
        bottomScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        bottomScrollPane.setColumnHeaderView(getBottomHeader());
        bottomScrollPane.getColumnHeader().setOpaque(false);

        bottomScrollPane.getViewport().setView(getBottomList());
        bottomScrollPane.getViewport().setOpaque(false);

        bottomScrollPane.setPreferredSize(new Dimension(160, 220));
        bottomScrollPane.setMinimumSize(new Dimension(160, 220));

        add(bottomScrollPane, BorderLayout.SOUTH);
    }

    //--------------------------------------------------------------------------
    /**
     *  Gets the header bean.
     *  @return a header bean
     */
    //--------------------------------------------------------------------------
    protected BaseHeaderBean getBottomHeader()
    {
        // lazy instantiation
        if(bottomHeader == null)
        {
            setBottomHeader(null);
        }
        return bottomHeader;
    }

    //--------------------------------------------------------------------------
    /**
     *  Gets the list that will be displayed. Subclasses should override
     *    for different list objects.
     *  @return a JList object
     */
    //--------------------------------------------------------------------------
    protected EYSList getBottomList()
    {
        if(bottomList == null)
        {
            setBottomList(null);
        }
        return bottomList;
    }
    //--------------------------------------------------------------------------
    /**
     *  Gets the scroll pane that wraps this list.
     *  @return a renderer object
     */
    //--------------------------------------------------------------------------
    protected JScrollPane getBottomScrollPane()
    {
        // lazy instantiation
        if(bottomScrollPane == null)
        {
            setBottomScrollPane(null);
        }
        return bottomScrollPane;
    }

    //--------------------------------------------------------------------------
    /**
     *  Enables or disables the Query list and panel.
     *    @param aValue true to enable, false to disable
     */
    //--------------------------------------------------------------------------
    protected void enableQuery(boolean aValue)
    {
        getBottomList().setEnabled(aValue);
        getBottomScrollPane().setEnabled(aValue);
        getBottomScrollPane().setRequestFocusEnabled(aValue);

        getTopList().setEnabled(false);
        getTopScrollPane().setEnabled(false);
        getTopScrollPane().setRequestFocusEnabled(false);
        
        getScrollPane().setEnabled(aValue);
        getList().setEnabled(aValue);
    }

    //--------------------------------------------------------------------------
    /**
     *     Updates the bean if It's been changed
     */
    //--------------------------------------------------------------------------
    protected void updateBean()
    {
    	// make center list scrollable if needed
       	getScrollPane().setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
       	
        if (beanModel instanceof TriListBeanModel)
        {    
            TriListBeanModel model = (TriListBeanModel)beanModel;
   
            POSListModel bottomListModel = (POSListModel) getBottomList().getModel();
            bottomListModel.removeAllElements();
            
            for (int i=0; i < model.getBottomListVector().size(); i++){
            	bottomListModel.addElement(model.getBottomListVector().get(i));
            }
               
            POSListModel topListModel = (POSListModel) getTopList().getModel();
            topListModel.removeAllElements();
            topListModel.addElement(model.getTopListVector().firstElement());
            
            Vector matches = model.getListVector();
            POSListModel posListModel = new POSListModel(matches);
    
            // set the possible matches
            getList().setModel(posListModel);
            
            // request focus
            if (selectionMode != NO_SELECTION_MODE)
            {
                enableQuery(true);
            }
            else
            {
                enableQuery(false);
            }
            getList().setFocusTraversalKeysEnabled(false);
            setCurrentFocus(getList());
        }
    }


    //--------------------------------------------------------------------------
    /**
     *  Set the properties to be used by this bean. If the header and
     *    renderer accept properties, this method will pass the properties
     *    on to those objects.
     *
     *    @param props the propeties object
     */
    //--------------------------------------------------------------------------   	
    public void setProps(Properties props)
    {
        super.setProps(props);
        getBottomHeader().setProps(props);

        if (bottomRenderer != null && bottomRenderer instanceof AbstractListRenderer)
        {
           ((AbstractListRenderer) bottomRenderer).setProps(props);
        }
    }
	
}
