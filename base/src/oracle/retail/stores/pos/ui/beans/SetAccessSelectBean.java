/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SetAccessSelectBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:54 mszekely Exp $
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
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:29:57 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:25:14 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:14:11 PM  Robert Pearse   
 *
 *  Revision 1.5  2004/08/19 13:42:23  kll
 *  @scr 6826: access constant in a static fashion
 *
 *  Revision 1.4  2004/07/24 00:17:57  jdeleau
 *  @scr 3750 Add multi-select to the JTable that will allow non-contiguous
 *  selections via a spacebar.
 *
 *  Revision 1.3  2004/03/16 17:15:18  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:27  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Dec 18 2003 15:00:56   rrn
 * Change table.selectionMode to MULTIPLE_INTERVAL_SELECTION and 
 * modify actionPerformed( ) to handle variable number of selections.
 * Resolution for 3477: Access list should be multi-select enabled.
 * 
 *    Rev 1.1   Sep 16 2003 17:53:14   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * 
 *    Rev 1.0   Aug 29 2003 16:12:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Mar 20 2003 15:42:30   bwf
 * Set correct bundle keys for yes and no
 * Resolution for 1866: I18n Database  support
 * 
 *    Rev 1.1   Aug 14 2002 18:18:42   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:52:28   msg
 * Initial revision.
 * 
 *    Rev 1.1   28 Mar 2002 16:51:06   baa
 * translate yes/no labels, disable enter and left/right keys
 * Resolution for POS SCR-58: Enter is disabled in Set Access, but still moves the cursor up and down the list
 *
 *    Rev 1.0   Mar 18 2002 11:53:44   msg
 * Initial revision.
 *
 *    Rev 1.4   Mar 08 2002 09:02:12   mpm
 * Externalized text for role UI screens.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.behavior.YesNoActionListener;

//----------------------------------------------------------------------------
/**
    The SetAccessSelect bean presents the functionality of the SetAccess screen.
   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class SetAccessSelectBean extends    POSJTableHandlerBean
                                 implements YesNoActionListener, FocusListener       
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        title column width
    **/
    public static final int TITLE_COLUMN_WIDTH = 353;

    /**
        access column width
    **/
    public static final int ACCESS_COLUMN_WIDTH = 148;

    /**
        bean model
    **/
    protected SetAccessSelectBeanModel beanModel = null;

    /**
        role
    **/
    protected RoleIfc[] role = null;

    /**
        selected role
    **/
    protected RoleIfc selectedRole = null;

    /**
        table
    **/
    protected MultiSelectJTable table = null;

    /**
        table rows
    **/
    protected int rows = 0;

    /**
        table model
    **/
    protected RoleTableModel tableModel = null;

    /**
        column
    **/
    protected TableColumn column = null;

    /**
        role function title
    **/
    protected String[] roleFunctionTitle = null;

    /**
        role function access
    **/
    protected String[] roleFunctionAccess = null;

    /**
        role function pane
    **/
    protected JScrollPane roleFunctionPane = null;
    /**
        function label
    **/
    protected JLabel functionLabel = null;
    /**
        access label
    **/
    protected JLabel accessLabel = null;
    /**
     *  Yes/no  label labels
     */
    protected String noLabel  = "No";
    protected String yesLabel = "Yes";
    /**
        function label tag
    **/
    public static final String FUNCTION_LABEL = "FunctionLabel";
    /**
        access label tag
    **/
    public static final String ACCESS_LABEL = "AccessLabel";

    /**
     *  Yes/no  label tags
     */
    public static final String YES_LABEL = "Yes";
    public static final String NO_LABEL  = "No";
    //---------------------------------------------------------------------
    /**
        SetAccessSelectBean Constructor
    **/
    //---------------------------------------------------------------------
    public SetAccessSelectBean()
    {
        super();
        initialize();
    }

    //---------------------------------------------------------------------
    /**
        Initialize the class.
    **/
    //---------------------------------------------------------------------
    protected void initialize()
    {
        setName("SetAccessSelectBean");
        setLayout(new BorderLayout());
        uiFactory.configureUIComponent(this, UI_PREFIX);

        // steps to create a JTable:
        // 1) setup the table model with 2 columns
        tableModel = new RoleTableModel(rows, 2);

        // 2) create the actual JTable
        table = new MultiSelectJTable(tableModel);
        table.setShowGrid(false);

        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // add the new table into a JScrollPane
        roleFunctionPane = new JScrollPane(table);
        roleFunctionPane.setVerticalScrollBar(new EYSScrollBar());
        roleFunctionPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(roleFunctionPane, "Center");

        // important to disable auto column creation in order to set their width
        table.setAutoCreateColumnsFromModel(false);

        // Allows for better scaleing
        table.getColumnModel().getColumn(0).setPreferredWidth(TITLE_COLUMN_WIDTH);
        table.getColumnModel().getColumn(1).setPreferredWidth(ACCESS_COLUMN_WIDTH);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        table.revalidate();

        setupHeader(table);

        // clear the last selection and then scroll back to the first row
        table.clearSelection();
        table.getSelectionModel().setSelectionInterval(0,0);
        table.scrollRectToVisible(table.getCellRect(0,1,false));

    }

    //---------------------------------------------------------------------
    /**
        The UI Framework calls this method before displaying the screen.
    **/
    //---------------------------------------------------------------------
    public void activate()
    {
        disableKeys( new int[]{KeyEvent.VK_ENTER,KeyEvent.VK_LEFT,KeyEvent.VK_RIGHT});
        super.activate();
        table.activate();
        table.addFocusListener(this);
    }

    //---------------------------------------------------------------------
    /**
        The UI Framework calls this method after removing the screen from
        the display.
    **/
    //---------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
        table.removeFocusListener(this);
    }

    //---------------------------------------------------------------------
    /**
        Updates the model property from the components.
    **/
    //---------------------------------------------------------------------
    public void updateModel()
    {
        // retrieve the role function titles
        beanModel.setRoleFunctionTitle(model2TitleArray(tableModel));
        // retrieve the role function access values
        beanModel.setRoleFunctionAccess(model2AccessArray(tableModel));
    }

    //---------------------------------------------------------------------
    /**
       Sets the model property value.
       @param model the value for the model property.
     */
    //---------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set SetAccessSelectBean " +
                "model to null");
        }
        if (model instanceof SetAccessSelectBeanModel)
        {
            beanModel = (SetAccessSelectBeanModel)model;
            rows = beanModel.getFunctionsArraySize();
            tableModel = arrays2Model(beanModel.getRoleFunctionTitle(),
                                      beanModel.getRoleFunctionAccess());
            table.setModel(tableModel);
        }
    }

    //---------------------------------------------------------------------
    /**
        Converts an Array's contents into a RoleTableModel
        @param roleFunctionTitle an array containing title items
        @param roleFunctionAccess an array containing access items
        @return DefaultListModel list model to be used by the JTable
    **/
    //---------------------------------------------------------------------
    protected RoleTableModel arrays2Model(String[] roleFunctionTitle,
                                            String[] roleFunctionAccess)
    {
        int roleFunctionSize = roleFunctionTitle.length;

        String functionColumnName = retrieveText(FUNCTION_LABEL,
                                                 "Function");
        String accessColumnName = retrieveText(ACCESS_LABEL,
                                               "Access");

        // create a new table model with rows = number of role Functions
        // and columns = 2
        String[] columnName =
        {
            functionColumnName,
            accessColumnName
        };

        RoleTableModel tableModel = null;
        tableModel = new RoleTableModel(columnName, roleFunctionSize);

        // loop through the RoleFunction array and
        // retrieve the titles and access values
        for (int i = 0; i < roleFunctionSize; i++)
        {
            // retrieve the function titles and
            // add them to the first table column
            tableModel.setValueAt(roleFunctionTitle[i], i, 0) ;

            // retrieve the function access values and
            // add them to the second table column
            tableModel.setValueAt(roleFunctionAccess[i], i, 1);
        }
        return tableModel;
    }

    //---------------------------------------------------------------------
    /**
       Converts a RoleTableModel contents into a
       role function Titles array.
       @param tableModel model to be used by the JTable
       @return String[] array containing function title items
    **/
    //---------------------------------------------------------------------
    protected String[] model2TitleArray(RoleTableModel tableModel)
    {
        int rowCount = tableModel.getRowCount();
        roleFunctionTitle = new String[rowCount];

        for(int i = 0; i < rowCount; i++)
        {
            roleFunctionTitle[i] = (String)tableModel.getValueAt(i,0);
        }
        return roleFunctionTitle;
    }

    //---------------------------------------------------------------------
    /**
       Converts an RoleTableModel contents into a
       role function Access array.
       @param tableModel model to be used by the JTable
       @return String[] array containing function access values
    **/
    //---------------------------------------------------------------------
    protected String[] model2AccessArray(RoleTableModel tableModel)
    {
        int rowCount = tableModel.getRowCount();
        roleFunctionAccess = new String[rowCount];

        for(int i = 0; i < rowCount; i++)
        {
            roleFunctionAccess[i] = (String)tableModel.getValueAt(i,1);
        }
        return roleFunctionAccess;
    }

    //---------------------------------------------------------------------
    /**
        This is a method used to setup the column headers for the role table.
        @param table to be used by the JTable
    **/
    //---------------------------------------------------------------------
    protected void setupHeader(JTable table)
    {
        //To modify the JTable header:
        functionLabel = uiFactory.createLabel("Function", null, "Table.label");

        accessLabel = uiFactory.createLabel("Access", null, "Table.label");

        table.getColumnModel().getColumn(0).setHeaderRenderer(new JComponentCellRenderer());
        table.getColumnModel().getColumn(1).setHeaderRenderer(new JComponentCellRenderer());

        table.getColumnModel().getColumn(0).setHeaderValue(functionLabel);
        table.getColumnModel().getColumn(1).setHeaderValue(accessLabel);
        table.getTableHeader().revalidate();
        // End modify JTable header
    }

    //---------------------------------------------------------------------
    /**
        The framework calls this method through a connection when the user
        presses the Yes/No key on the local navigation bar.
        @return java.awt.event.ActionListener a default button listener
    **/
    //---------------------------------------------------------------------
    public void actionPerformed(ActionEvent evt)
    {
    
        int [] selectedRows = table.getSelectedRows();
        
        for(int i=0; i<selectedRows.length; i++)
        {
            if(table.getValueAt(selectedRows[i], 1).equals(noLabel))
            {
                table.setValueAt(yesLabel, selectedRows[i], 1);
                tableModel.setValueAt(yesLabel, selectedRows[i], 1);
            }
            else
            {
                table.setValueAt(noLabel, selectedRows[i], 1);
                tableModel.setValueAt(noLabel, selectedRows[i], 1);
            }
            
        }

        setCurrentFocus(table);
    }

    //--------------------------------------------------------------------------
    /**
        Override JPanel set Visible to request focus.
        @param aFlag indicates if the component should be visible or not.
    **/
    //--------------------------------------------------------------------------
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);

        if (aFlag)
        {
            table.changeSelection(0,0,false,false);
            setCurrentFocus(table);
        }
    }

    //---------------------------------------------------------------------
    /**
       Updates property-based fields.
    **/
    //---------------------------------------------------------------------
    protected void updatePropertyFields()
    {                                   // begin updatePropertyFields()
        functionLabel.setText(retrieveText(FUNCTION_LABEL, functionLabel));
        accessLabel.setText(retrieveText(ACCESS_LABEL, accessLabel));
        noLabel= retrieveText(NO_LABEL, noLabel);
        yesLabel= retrieveText(YES_LABEL, yesLabel);
    }                                   // end updatePropertyFields()

    //---------------------------------------------------------------------------
    /**
     *  Empty method for FocusListener. 
     */    
    public void focusLost(FocusEvent e)
    {
    }
    
    //---------------------------------------------------------------------------
    /**
     *  Empty method for FocusListener.
     */    
    public void focusGained(FocusEvent e)
    {       
    }

    //---------------------------------------------------------------------
    /**
        Method to default display string function. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        // result string
        String strResult = new String("Class: SetAccessSelectBean (Revision "
            + getRevisionNumber() + ")" + hashCode());

        // pass back result
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
        Retrieves the Team Connection revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }

    //---------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    //---------------------------------------------------------------------
    public static void main(java.lang.String[] args)
    {
        String Function[] = {"f1", "f2", "f3"};
        String Access[] = {"a1", "a2", "a3"};

        java.awt.Frame frame = new java.awt.Frame();
        SetAccessSelectBean aSetAccessSelectBean;
        aSetAccessSelectBean = new SetAccessSelectBean();

        aSetAccessSelectBean.arrays2Model(Function, Access);
        frame.add("Center", aSetAccessSelectBean);
        frame.setSize(aSetAccessSelectBean.getSize());
        frame.setVisible(true);
    }
}
