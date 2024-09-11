/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SelectGroupBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:58 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:54 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:08 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:08 PM  Robert Pearse   
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
 *    Rev 1.1   Sep 16 2003 17:53:06   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * 
 *    Rev 1.0   Aug 29 2003 16:12:02   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:18:34   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:49:42   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:57:30   msg
 * Initial revision.
 * 
 *    Rev 1.2   04 Feb 2002 14:56:54   KAC
 * Deprecated this in favor of the ListSelectionBean
 * Resolution for POS SCR-997: Wrong Data Field Label on Parameter Groups screen
 * 
 *    Rev 1.1   Jan 19 2002 10:31:44   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 21 2001 11:35:30   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:16:36   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;

//-------------------------------------------------------------------------
/** 
    This class presents the reason code groups to the user, so he can
    select one. 
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
    @deprecated As of POS 5.0, use (@link SelectionListBean) instead
**/
//-------------------------------------------------------------------------
public class SelectGroupBean extends CycleRootPanel
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    protected SelectGroupBeanModel beanModel = new SelectGroupBeanModel();
    protected JList groupList = null;

    //---------------------------------------------------------------------
    /**
        Class Constructor
    **/
    //---------------------------------------------------------------------
    public SelectGroupBean() 
    {
        super();
        initialize();
    }

    //---------------------------------------------------------------------
    /**
        Overrides JPanel setVisible() method to request focus. <P>
    **/
    //---------------------------------------------------------------------
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
        if (aFlag)
        {
            setCurrentFocus(groupList);
        }
    }
    
    //---------------------------------------------------------------------
    /**
      * Activates this bean.
      */
     public void activate()
     {
         super.activate();
         groupList.addFocusListener(this);
     }

     //--------------------------------------------------------------------------
     /**
      * Deactivates this bean.
      */
     public void deactivate()
     {
         super.deactivate();
         groupList.removeFocusListener(this);
     }

    //---------------------------------------------------------------------
    /**
        Initializes the class.
    **/
    //---------------------------------------------------------------------
    protected void initialize() 
    {
        GridBagConstraints gbc = new GridBagConstraints();
        setName("RxSelectBean");
        setLayout(new GridBagLayout());

        // Initialize the group header
        ReasonCodeGroupHeader reasonCodeGroupHeader = new ReasonCodeGroupHeader();
        reasonCodeGroupHeader.setName("reasonCodeGroupHeader");
        gbc.gridx = 0; 
        gbc.gridy = 0;
        gbc.gridwidth = 1; 
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        add(reasonCodeGroupHeader, gbc);

        // Initialize the group list
        groupList = new JList();
        groupList.setName("groupList");
        groupList.setCellRenderer(new ReasonCodeGroupRenderer());
        groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane jsp = new JScrollPane(groupList);
        gbc.gridx = 0; 
        gbc.gridy = 1;
        gbc.gridwidth = 1; 
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        add(jsp, gbc);
    }

    //------------------------------------------------------------------------
    /**
        Updates the bean model, populated with data from the bean. <P>
    **/
    //------------------------------------------------------------------------
    public void updateModel()
    {
        beanModel.setSelectedGroup((ReasonCodeGroupBeanModel)groupList.getSelectedValue());
    }

    //------------------------------------------------------------------------
    /**
        Sets the model for the current settings of this bean. <p>
        @param model the model for the current values of this bean
    **/
    //------------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set SelectGroupBean model to null");
        }
        if (model instanceof SelectGroupBeanModel)
        {
            beanModel = (SelectGroupBeanModel)model;
            updateBean();
        }
    }

    //---------------------------------------------------------------------
    /**
        Updates the bean, populated with data from the model. <P>
    **/
    //---------------------------------------------------------------------
    public void updateBean()
    {
        Vector groups = beanModel.getGroups();
        DefaultListModel listModel = new DefaultListModel();
        int n = groups.size();
        for (int i = 0; i < n; i++)
        {
            listModel.addElement(groups.elementAt(i));
        }
        groupList.setModel(listModel);
        groupList.setSelectedValue(beanModel.getSelectedGroup(), true);
    }

    //---------------------------------------------------------------------
    /**
        Returns default display string. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: SelectGroupBean (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
        Returns the revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }

    //---------------------------------------------------------------------
    /**
        Displays the bean in it's own frame. <p>
        @param args command line arguments
    **/
    //---------------------------------------------------------------------
    public static void main(String[] args)
    {
        java.awt.Frame frame = new java.awt.Frame();
        SelectGroupBean bean = new SelectGroupBean();

        frame.add("Center", bean);
        frame.setSize(640, 480);
        frame.setVisible(true);
    }
}
