/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EmailListBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:46 mszekely Exp $
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
 *   3    360Commerce 1.2         3/31/2005 4:27:55 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:21:15 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:47 PM  Robert Pearse   
 *
 *  Revision 1.4  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;
// java imports
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.emessage.EMessageIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
   The EmailList bean presents the functionality of the Service screen.
   @version $KW=@(#); $Ver=pos_4.5.0:33; $EKW;
   @deprecated as of release 5.0.0
**/
//----------------------------------------------------------------------------
public class EmailListBean extends POSJTableHandlerBean
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";

    /**
        constant for class name
    **/
    public static final String CLASSNAME = "EmailListBean";

    /**
        constant for number of table columns
    **/
    public static final int MAX_COLUMNS = 4;

    /**
        constant for default number of table rows
    **/
    public static final int DEFAULT_ROWS = 16;

    // values required to ensure JTable fits in POS screen
    public static final int column0Width = 150;
    public static final int column1Width = 150;
    public static final int column2Width = 150;
    public static final int column3Width = 150;

    private EmailListBeanModel beanModel = null;
    private JTable table = null;
    private ServiceTableModel tableModel = null;
    private TableColumn column = null;
    private JScrollPane eMessagePane = null;

    private EMessageIfc[] eMessages = null;

    private boolean dirtyModel = false;

    //---------------------------------------------------------------------
    /**
       Constructor
    */
    //---------------------------------------------------------------------
    public EmailListBean()
    {
        initialize();
    }

    //---------------------------------------------------------------------
    /**
     * Initialize the class.
     */
    //---------------------------------------------------------------------
    protected void initialize()
    {
        setName("EmailListBean");
        setLayout(new BorderLayout());
        uiFactory.configureUIComponent(this, UI_PREFIX);

        //initialize EmailListTable
        // 1) setup the table model with 2 columns
        tableModel = new ServiceTableModel(DEFAULT_ROWS, MAX_COLUMNS);

        // 2) create the actual JTable
        table = new JTable(tableModel);
        table.setShowGrid(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // this will remove any gaps between cells
        table.setIntercellSpacing(new Dimension(0,0));

        eMessagePane = new JScrollPane(table);
        add(eMessagePane, "Center");
    }

     //---------------------------------------------------------------------
    /**
        Activate this screen.
     */
    //---------------------------------------------------------------------
    public void activate()
    {
        super.activate();
        EMessageIfc[] eMessages = null;

        if ( beanModel != null )
        {
            eMessages = beanModel.getEMessages();
        }

        // update the model first
        updateBean();

        // important to disable auto column creation in order to set their width
        table.setAutoCreateColumnsFromModel(false);

        // Jerry Rightmer contributed code.
        table.getColumnModel().getColumn(0).setPreferredWidth(column0Width);
        table.getColumnModel().getColumn(1).setPreferredWidth(column1Width);
        table.getColumnModel().getColumn(2).setPreferredWidth(column2Width);
        table.getColumnModel().getColumn(3).setPreferredWidth(column3Width);
        table.setAutoResizeMode(table.AUTO_RESIZE_ALL_COLUMNS);
        // end Jerry's.

        table.revalidate();

        setupHeader(table);

        KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        table.unregisterKeyboardAction(enterKey);

        // clear the last selection and then scroll back to the first row
        table.clearSelection();
        table.getSelectionModel().setSelectionInterval(0,0);
        table.scrollRectToVisible(table.getCellRect(0,1,false));
        table.setVisible(true);
    }

    //---------------------------------------------------------------------
    /**
        Deactivate this screen.
    **/
    //---------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
    }


    //---------------------------------------------------------------------
    /**
       Converts an Array's contents into a ServiceTableModel
       @param eMessages an array containing items from the EMessageIfc
       @return tableModel model to be used by the JTable
     */
    //---------------------------------------------------------------------
    private static ServiceTableModel array2Model(EMessageIfc[] eMessages)
    {
        int eMessageSize = 0;
        if(eMessages!=null)
        {
            eMessageSize = eMessages.length;
    }
        // create a new table model with a default of 16 rows
        // and columns = 4
        String[] columnName =
            { "Order #", "Customer", "Email Date", "Email Status" };
        ServiceTableModel tableModel = null;
        tableModel = new ServiceTableModel(columnName, eMessageSize);
        for (int i = 0; i < eMessageSize; i++)
        {
            tableModel.setValueAt(eMessages[i].getOrderID(), i, 0);

            tableModel.setValueAt(eMessages[i].getCustomerName(), i, 1);

            tableModel.setValueAt(getDateString(
                                  eMessages[i].getTimestampSent()), i, 2);

            tableModel.setValueAt(getStatusString(
                              eMessages[i].getMessageStatus()), i, 3);
        }

        return tableModel;
    }

    //---------------------------------------------------------------------
    /**
       Formats an EYSDate value for the JTable display
       @param date an EYSDate value
       @return String to be used by the JTable
     */
    //---------------------------------------------------------------------
    private static String getDateString(EYSDate date)
    {
            if ((date.getMonth() < 10) &&
                    (date.getDay() < 10))
            {
                return date.toFormattedString("M/d/yyyy");
            }
            else if (date.getMonth() < 10)
            {
                return date.toFormattedString("M/dd/yyyy");
            }
            else if (date.getDay() < 10)
            {
                return date.toFormattedString("MM/d/yyyy");
            }
            else
            {
                return date.toFormattedString("MM/dd/yyyy");
            }
    }

    //---------------------------------------------------------------------
    /**
       Formats the int status value for the JTable display
       @param status an EYSDate value
       @return String to be used by the JTable
     */
    //---------------------------------------------------------------------
    private static String getStatusString(int status)
    {
        return EMessageIfc.MESSAGE_STATUS_DESCRIPTORS[status];
    }

    //---------------------------------------------------------------------
    /**
       Converts a ServiceTableModel contents into a
       EMessage array.
       @param tableModel to be used by the JTable
       @return EMessageIfc[] array containing EMessage items
    */
    //---------------------------------------------------------------------
    private EMessageIfc[] model2Array(ServiceTableModel tableModel)
    {
        int rowCount = tableModel.getRowCount();
        eMessages = new EMessageIfc[rowCount];

        for(int i = 0; i < rowCount; i++)
        {
            eMessages[i] = DomainGateway.getFactory().getEMessageInstance();
            eMessages[i].setOrderID((String)tableModel.getValueAt(i,0));
            eMessages[i].setCustomerName((String)tableModel.getValueAt(i,1));
            eMessages[i].setTimestampSent((EYSDate)tableModel.getValueAt(i,2));
            Integer status = new Integer((String)tableModel.getValueAt(i,3));
            eMessages[i].setMessageStatus(status.intValue());
        }

        return eMessages;
    }

    //---------------------------------------------------------------------
    /**
        This method is used to setup the JTable headers.
        @param table reference to the JTable
    **/
    //---------------------------------------------------------------------
    private void setupHeader(JTable table)
    {

        JLabel[] labels = new JLabel[MAX_COLUMNS];
        String[] labelText = { "Order #", "Customer", "Email Date", "Email Status" };
        TableColumn[] column = new TableColumn[MAX_COLUMNS];

        for (int cnt = 0; cnt < MAX_COLUMNS; cnt++)
        {
            labels[cnt] =
                uiFactory.createLabel(labelText[cnt], null, "Table.header.label");

            column[cnt] = table.getColumnModel().getColumn(cnt);
            column[cnt].setHeaderRenderer(new JComponentCellRenderer());
            column[cnt].setHeaderValue(labels[cnt]);
        }

        table.getTableHeader().revalidate();
        // End modify JTable header
    }

    //---------------------------------------------------------------------
    /**
     * Gets the model property (java.lang.Object) value.
     * @return The model property value.
     * @see #setModel
     * @see oracle.retail.stores.pos.ui.beans.EmailListBeanModel
     */
    //---------------------------------------------------------------------
    public void updateModel()
    {
        EMessageIfc[] eMessages = beanModel.getEMessages();
        beanModel.setSelectedMessage(eMessages[table.getSelectedRow()]);
    }

    //---------------------------------------------------------------------
    /**
       Sets the model property (UIModelIfc) value.
       @param model The new value for the property.
       @see #updateModel
       @see oracle.retail.stores.pos.ui.beans.EmailListBeanModel
     */
    //---------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set EmailListBean " +
                "model to null");
        }
        else
        {
            if (model instanceof EmailListBeanModel)
            {
                beanModel  = (EmailListBeanModel)model;
                tableModel = array2Model(beanModel.getEMessages());

                dirtyModel = true;
            }
        }
    }

    //---------------------------------------------------------------------
    /**
     * Update the model if it's been changed
     */
    //---------------------------------------------------------------------
    public void updateBean()
    {
        if (dirtyModel)
        {
            // reset the table model
            ServiceTableModel tableModel = null;

            // convert the emessage entry array to
            // a ServiceTableModel
            tableModel = array2Model(beanModel.getEMessages());

            table.setModel(tableModel);

            dirtyModel = false;
        }
    }
    //---------------------------------------------------------------------
    // Note: These next methods are empty implementations
    // for the TableBeanIfc. We are required to use the
    // TableBeanIfc for this particular screen.
    //---------------------------------------------------------------------
    //---------------------------------------------------------------------
    /**
     * This method is called everytime the length of the input area
     * on the QuarryTopPanel changes.
     * The highlight heuristics for this is as follows.
     * <OL>
     * <LI> If the length>0, then the clearAction is Enabled AND Highlight is OFF
     * <LI> If the length==0 AND the table is NOT EMPTY,
     *      then the clearAction is Enabled AND Highlight is ON
     * <LI> If the length==0 AND the table is EMPTY,
     *      then the clearAction is Disabled AND Highlight is OFF
     * </OL>
     * This method assumes that the postconditions for the regular clear action
     * has already been met.  For example, if the current input area is non-null, the
     * clear action is enabled, if the input area is null, the clear action is disabled.
     * @param length the new length of the string
     */
    //---------------------------------------------------------------------
    public void inputAreaLengthChanged(int length)
    {
    }

    //---------------------------------------------------------------------
    /**
        Adds an item to the tableBean's model.
        @param index row to add the item to
        @param item the item to add
    **/
    //---------------------------------------------------------------------
    public void addItem(int index,Object item)
    {
    }

    //---------------------------------------------------------------------
    /**
        Deletes an item from the tableBean's model.
        Highlight and selection heuristics are defferred to the implementation.
        @param index row to delete
    **/
    //---------------------------------------------------------------------
    public void deleteItem(int index)
    {
    }

    //---------------------------------------------------------------------
    /**
        Modifies the item at the given index.
        Highlight and selection heuristics are defferred to the implementation.
        @param index index of item to modify
        @param item the new item to replace the index with.
    **/
    //---------------------------------------------------------------------
    public void modifyItem(int index,Object item)
    {
    }

    //---------------------------------------------------------------------
    /**
        Returns the currently selected row in the tableBean.
        @return currently selected row.
    **/
    //---------------------------------------------------------------------
    public int getSelectedRow()
    {
        return table.getSelectedRow();
    }

    //---------------------------------------------------------------------
    /**
        Highlights the current row (if any). Otherwise has no effect.
        If the argument is set to true, then the selected row is highlighted.
        If the argument is false then no row is highlighted.
        @param highlighted Whether or not to highlight the current row.
    **/
    //---------------------------------------------------------------------
    public void setHighlight(boolean highlighted)
    {
    }
    // End of methods for the TableBeanIfc

    //---------------------------------------------------------------------
    /**
    Method to default display string function. <P>
    @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        // result string
        String strResult = new String("Class: " + CLASSNAME + " (Revision " +
                                      getRevisionNumber() + ")" + hashCode());
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
        java.awt.Frame frame = new java.awt.Frame();
        EmailListBean aEmailListBean = new EmailListBean();

        JTable table = aEmailListBean.table;
        ServiceTableModel tableModel = (ServiceTableModel)table.getModel();

        EMessageIfc[] eMessages = new EMessageIfc[3];

        eMessages[0] = DomainGateway.getFactory().getEMessageInstance();
        eMessages[0].setOrderID("0321654");
        eMessages[0].setCustomerName("Smith, John");
        eMessages[0].setTimestampSent(new EYSDate(2001, 4, 16));
    eMessages[0].setMessageStatus(0);

        eMessages[1] = DomainGateway.getFactory().getEMessageInstance();
        eMessages[1].setOrderID("0321654");
        eMessages[1].setCustomerName("Smith, John");
        eMessages[1].setTimestampSent(new EYSDate(2001, 3, 18));
    eMessages[1].setMessageStatus(1);

        eMessages[2] = DomainGateway.getFactory().getEMessageInstance();
        eMessages[2].setOrderID("0654321");
        eMessages[2].setCustomerName("Smith, John");
        eMessages[2].setTimestampSent(new EYSDate(2001, 4, 10));
    eMessages[2].setMessageStatus(1);

        tableModel = array2Model(eMessages);
        table.setModel(tableModel);

        frame.add("Center", aEmailListBean);
        frame.setSize(aEmailListBean.getSize());

        aEmailListBean.activate();
        frame.setVisible(true);
    }
}
