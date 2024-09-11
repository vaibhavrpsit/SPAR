/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SimpleListBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:05 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:21 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:15 PM  Robert Pearse   
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
 *    Rev 1.1   Sep 16 2003 17:53:18   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * 
 *    Rev 1.0   Aug 29 2003 16:12:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:18:42   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:49:54   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:53:50   msg
 * Initial revision.
 * 
 *    Rev 1.2   Jan 19 2002 10:31:56   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.1   Nov 28 2001 16:09:34   cir
 * Check for null model in setVisible()
 * Resolution for POS SCR-333: Check for null model in setVisible()
 * 
 *    Rev 1.0   Sep 21 2001 11:34:50   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:16:28   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import java.awt.BorderLayout;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;

//------------------------------------------------------------------------------
/**
 *      Presents a list of objects that can be selected.
 *      NOTE: we should enhance this bean to be configurable from
 *                properties. Then it can be reused for most lists in
 *        the app.
 *      @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------
public class SimpleListBean extends CycleRootPanel
{
        /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
   
    /** list to display */
        protected JList list = null;
        
        /** scroll pane that contains the list */
        protected JScrollPane scrollPane = null;
        
        /** renderer for the list */
        protected ListCellRenderer renderer = null;
        
        /** header object */
        protected JPanel headerBean = null;
        
        /** data model for this bean */
        protected UIModelIfc model = null;
        
    //------------------------------------------------------------------------------
    /**
     *  Default Constructor.
     */
        public SimpleListBean()
        {
                super();
                // NOTE: why doesn't the ui call configure() automatically?
                configure();
        }

    //------------------------------------------------------------------------------
    /**
     *  Configures this bean.
     */
    public void configure()
    {   
        setLayout(new BorderLayout());        
        
        headerBean = getHeaderBean();
        renderer   = getRenderer();
        list       = getList();
        scrollPane = getScrollPane();
        
        scrollPane.setColumnHeaderView(headerBean);
        scrollPane.getViewport().setView(list);
        add(scrollPane, BorderLayout.CENTER);
    }

    //------------------------------------------------------------------------------
    /**
     *  Gets the header bean. Subclasses should override for custom
     *      headers
     *  @return a JPanel used for a header
     */
    protected JPanel getHeaderBean()
    {
        // lazy instantiation
        if(headerBean == null)
        {
            headerBean = new JPanel();
        }
        return headerBean;
    }
    
    //------------------------------------------------------------------------------
    /**
     *  Gets the list that will be displayed. Subclasses should override
     *      for different list objects.
     *  @return a JList object
     */
    protected JList getList()
    {
        if(list == null)
        {
            list = new JList();
            list.setCellRenderer(getRenderer());
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.setEnabled(true);
        }
        return list;
    }

    //------------------------------------------------------------------------------
    /**
     *  Gets the renderer for this list.
     *  @return a renderer object
     */
    protected ListCellRenderer getRenderer()
    {
        // lazy instantiation
        if(renderer == null)
        {
            renderer = new DefaultListCellRenderer();
        }
        return renderer;
    }

    //------------------------------------------------------------------------------
    /**
     *  Gets the scroll pane that wraps this list.
     *  @return a renderer object
     */
    protected JScrollPane getScrollPane()
    {
        // lazy instantiation
        if(scrollPane == null)
        {
            scrollPane = new JScrollPane();
        }
        return scrollPane;
    }
    
    //------------------------------------------------------------------------------
    /**
     *  Sets the visibility of this bean.
     *  @param aFlag true if visible, false otherwise
     */
    public void setVisible(boolean aFlag)
    {  
        super.setVisible(aFlag);
        
        // if visible is true, set the selection and request focus
        if (aFlag)
        {
            if (list.getModel() != null)
            {
                if(list.getSelectedIndex() == -1 && list.getModel().getSize() > 0)
                {
                    list.setSelectedIndex(0);
                }
            }
            list.requestFocusInWindow();
            list.setFocusTraversalKeysEnabled(false);
        }
        
    }

    //--------------------------------------------------------------------------
    /**
       Activates this class.
    **/
    //--------------------------------------------------------------------------    
    public void activate()
    {
        super.activate();
        if (list != null)
        {
            list.addFocusListener(this);
        }
    }

    //--------------------------------------------------------------------------
    /**
       Deactivates this class.
    **/
    //--------------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
        if (list != null)
        {
            list.removeFocusListener(this);
        }
    }

    //------------------------------------------------------------------------------
    /**
 *  Sets the bean model.
 *  @param model a new data model for this bean
 */
    public void setModel(UIModelIfc beanModel)
    {
        if(beanModel != null)
        {
                model = beanModel;
                updateBean();
        }
    }

    //------------------------------------------------------------------------------
    /**
     *  Updates the bean with new data from the model.
     */
    public void updateBean()
    {
        }
        
    //------------------------------------------------------------------------------
    /**
     *  Updates the bean model before sending it to the ui.
     */
    public void updateModel()
    {
    }
    
    //------------------------------------------------------------------------------
    /**
     *  Converts a array contents into a DefaultListModel
     *  @return DefaultListModel
     */
    public DefaultListModel array2Model(Object[] aList) 
    {
        DefaultListModel listModel = new DefaultListModel();
        
        for(int i=0; i<aList.length; i++)
        {
            listModel.addElement(aList[i]);
        }
        return listModel;
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
        
    //------------------------------------------------------------------------------
    /**
     *  Returns a string representation of this object.
     *  @return String representation of object
     */
    public String toString()
    {                                   
        // result string
        String strResult = new String("Class:  SimpleListBean (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }                                   

    //------------------------------------------------------------------------------
    /**
     *  Returns the revision number of the class.
     *  @return String representation of revision number
     */
    public String getRevisionNumber()
    {                                   
        // return string
        return(revisionNumber);
    }                                   

    //------------------------------------------------------------------------------
    /**
     *  Entry point for testing.
     *  @param args command line parameters
     */
    public static void main(String[] args)
    {
                
    }       
}
