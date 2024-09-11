/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ServiceSelectBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:58 mszekely Exp $
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
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.alert.AlertEntryIfc;
import oracle.retail.stores.domain.alert.AlertListIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
   The ServiceSelect bean presents the functionality of the Service screen.
   @deprecated as of release 5.0.0
**/
//----------------------------------------------------------------------------
public class ServiceSelectBean extends POSJTableHandlerBean
{
    /** revision number supplied by Team Connection   **/
    public static final String revisionNumber = "$KW=@(#); $Ver=010905:4; $EKW;";
    /** Class name   **/
    public static final String           CLASSNAME = "ServiceSelectBean";

    /** Maximum columns constant. */
    public static final int              MAX_COLUMNS = 4;
    /** Default rows constant. */
    public static final int              DEFAULT_ROWS = 16;
    /** Column 0 width constant. */
    public static final int              column0Width = 75;
    /** Column 1 width constant. */
    public static final int              column1Width = 75;
    /** Column 2 width constant. */
    public static final int              column2Width = 75;
    /** Column 3 width constant. */
    public static final int              column3Width = 300;
    /** The service alert pane. */
    protected JScrollPane                  serviceAlertPane = null;
    /** The bean model. */
    protected ServiceSelectBeanModel       beanModel  = null;
    /** The table model. */
    protected ServiceTableModel            tableModel = null;
    /** The list of service alerts. */
    protected JTable                       table      = null;
    /** The array of alert entries. */
    protected AlertEntryIfc[]              alertEntry = null;

    //---------------------------------------------------------------------
    /**
       Constructor
    */
    //---------------------------------------------------------------------
    public ServiceSelectBean()
    {
        setName("ServiceSelectBean");
        setLayout(new BorderLayout());

        uiFactory.configureUIComponent(this, UI_PREFIX);

        tableModel = new ServiceTableModel(DEFAULT_ROWS, MAX_COLUMNS);

        table = new JTable(tableModel);
        table.setShowGrid(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().setSelectionInterval(0,0);

        // important to disable auto column creation in order to set their width
        table.setAutoCreateColumnsFromModel(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(column0Width);
        table.getColumnModel().getColumn(1).setPreferredWidth(column1Width);
        table.getColumnModel().getColumn(2).setPreferredWidth(column2Width);
        table.getColumnModel().getColumn(3).setPreferredWidth(column3Width);
        table.setAutoResizeMode(table.AUTO_RESIZE_ALL_COLUMNS);

        // this will remove any gaps between cells
        table.setIntercellSpacing(new Dimension(0,0));

        //create the table header
        JLabel[] labels    = new JLabel[MAX_COLUMNS];
        String[] labelText = {"Type", "Date",
                              "Time", "Description/Summary"};

        TableColumn[] column = new TableColumn[MAX_COLUMNS];

        for (int cnt = 0; cnt < MAX_COLUMNS; cnt++)
        {
            labels[cnt] = uiFactory.createLabel(labelText[cnt], null, "Table.header.label");
            labels[cnt].setName(labelText[cnt]);

            column[cnt] = table.getColumnModel().getColumn(cnt);
            column[cnt].setHeaderRenderer(new JComponentCellRenderer());
            column[cnt].setHeaderValue(labels[cnt]);
        }

        //create a scroll pane and add the table to it
        serviceAlertPane = new JScrollPane(table);
        add(serviceAlertPane, "Center");
    }

    //---------------------------------------------------------------------
    /**
        Activate this screen.
     */
    //---------------------------------------------------------------------
    public void activate()
    {
        disableKeys( new int[]{KeyEvent.VK_ENTER} );
        super.activate();

        AlertEntryIfc[] entries = null;

        if ( beanModel != null )
        {
            entries = beanModel.getAlertList().getEntries();
        }

        // update the model first
        updateBean();
    }

    //--------------------------------------------------------------------------
    /**
        Override JPanel set Visible to request focus.
        @param aFlag indicates if the component should be visible or not.
    **/
    //--------------------------------------------------------------------------
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);

        if (visible)
        {
            table.changeSelection(0,0,false,false);
            table.scrollRectToVisible(table.getCellRect(0,1,false));
            table.requestFocus();
        }
    }

    //---------------------------------------------------------------------
    /**
     * Updates the model value.
     */
    //---------------------------------------------------------------------
    public void updateModel()
    {
        AlertListIfc    alertList  = beanModel.getAlertList();
        AlertEntryIfc[] entries    = alertList.getEntries();

        beanModel.setSelectedEntry( entries[table.getSelectedRow()] );
    }

    //---------------------------------------------------------------------
    /**
       Sets the model property (java.lang.Object) value.
       @param model The new value for the property.
       @see #getModel
       @see oracle.retail.stores.pos.ui.beans.ServiceSelectBeanModel
     */
    //---------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if (model instanceof ServiceSelectBeanModel)
        {
            beanModel  =  (ServiceSelectBeanModel)model;
            tableModel = array2Model(beanModel.getAlertList().getEntries());
        }
    }

    //---------------------------------------------------------------------
    /**
     * Update the table with the data from the beanModel.
     */
    //---------------------------------------------------------------------
    protected void updateBean()
    {
        table.setModel(
                array2Model(
                    beanModel.getAlertList().getEntries() ) );
    }

    //---------------------------------------------------------------------
    /**
       Converts an Array's contents into a ServiceTableModel
       @param aletEntry an array containing items from the AlertEntryIfc
       @return tableModel model to be used by the JTable
     */
    //---------------------------------------------------------------------
    static ServiceTableModel array2Model(AlertEntryIfc[] alertEntry)
    {
        int alertSize = alertEntry.length;

        // create a new table model with a default of 16 rows
        // and columns = 4
        String[] columnName = {"Type", "Date", "Time",
                                "Description/Summary"};

        ServiceTableModel tableModel =
                          new ServiceTableModel(columnName, alertSize);

        // loop through the Service Alert arrays and
        // retrieve the type, date, time and summary values
        for (int i = 0; i < alertSize; i++)
        {
            // retrieve the Alert Type and
            // add them to the first table column
            tableModel.setValueAt(
                AlertEntryIfc.ALERT_TYPE_DESCRIPTORS[alertEntry[i].getAlertType()], i, 0);

            // retrieve the Alert Date values and
            // add them to the second table column
            tableModel.setValueAt(getDateString(
                                    alertEntry[i].getTimeIssued()), i, 1);

            // retrieve the Alert Date values and
            // add them to the second table column
            tableModel.setValueAt(getTimeString(
                                    alertEntry[i].getTimeIssued()), i, 2);

            // retrieve the Alert Date values and
            // add them to the second table column
            tableModel.setValueAt(alertEntry[i].getSummary(), i, 3);
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
    protected static String getDateString(EYSDate date)
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
       Formats an EYSDate value for the JTable display as Time
       @param date an EYSDate value
       @return String to be used by the JTable
     */
    //---------------------------------------------------------------------
    protected static String getTimeString(EYSDate date)
    {
        if (date.getHour() < 10)
        {
            return date.toFormattedString("h:mm a");
        }
        else
        {
            return date.toFormattedString("hh:mm a");
        }
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
        Method to default display string function. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        // result string
        String strResult = new String("Class: " + CLASSNAME + " (Revision " +
                                      getRevisionNumber() +
                                      ")" +
                                      hashCode());
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
        ServiceSelectBean aServiceSelectBean
                                = new ServiceSelectBean();

        JTable table = aServiceSelectBean.table;
        ServiceTableModel tableModel = (ServiceTableModel)table.getModel();

        AlertEntryIfc[] alertEntry = new AlertEntryIfc[5];

        for (int i = 0; i < 5; i++)
        {
            alertEntry[i] = DomainGateway.getFactory().getAlertEntryInstance();
            if (i%2 > 0)
            {
                alertEntry[i].setAlertType(AlertEntryIfc.ALERT_TYPE_EMAIL);
                alertEntry[i].setTimeIssued(new EYSDate(2001, 4, 23, 6, 33, 0));
            }
            else
            {
                alertEntry[i].setAlertType(AlertEntryIfc.ALERT_TYPE_ORDER_PICKUP);
                alertEntry[i].setTimeIssued(new EYSDate(2001, 4, 26, 11, 57, 0));
            }
            alertEntry[i].setSummary("Summary text");
        }

        tableModel = array2Model(alertEntry);
        table.setModel(tableModel);

        frame.add("Center", aServiceSelectBean);
        frame.setSize(aServiceSelectBean.getSize());

        aServiceSelectBean.activate();
        frame.setVisible(true);
    }
}
