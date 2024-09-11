/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SuspendListBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:50 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:17 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:36 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Sep 16 2003 17:53:28   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * 
 *    Rev 1.0   Aug 29 2003 16:12:42   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:18:58   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:54:58   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:57:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:32:20   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 21 2001 11:35:10   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:16:20   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Java imports
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;

// POS imports

//----------------------------------------------------------------------------
/**
   This class displays a list of suspended transactions. <p>
   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
*/
//----------------------------------------------------------------------------
public class SuspendListBean extends CycleRootPanel
{
    /**
        revision number supplied by source-code-control system
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        Builds each line item
    **/
    protected SuspendListItemRenderer transactionItemRenderer = null;
    /**
        Header bean
    **/
    protected SuspendListHeaderBean transactionHeaderBean = null;
    /**
        JList for transaction line itmes
    **/
    protected JList transactionList = null;
    /**
        Scroll pane for the list
    **/
    protected JScrollPane transactionPane = null;
    /**
        Bean model data from the Business logic
    **/
    protected TransactionListBeanModel beanModel = null;
    /**
        List model for the transaction line items.
    **/
    protected DefaultListModel transModel = null;

    //---------------------------------------------------------------------
    /**
     * Constructor
     */
    //---------------------------------------------------------------------
    public SuspendListBean()
    {
        super();
        initialize();
    }

    //---------------------------------------------------------------------
    /**
     * This method sets the focus to the proper component
     */
    //---------------------------------------------------------------------
    public void activate()
    {
        if (getTransactionList().getSelectedIndex() == -1)
        {
            getTransactionList().setSelectedIndex(0);
        }
        getTransactionList().addFocusListener(this);
    }
    
    //---------------------------------------------------------------------
    /**
     * This method sets the focus to the proper component
     */
    //---------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
        getTransactionList().removeFocusListener(this);
    }

    //---------------------------------------------------------------------
    /**
     * This method sets the focus to the proper component
     */
    //---------------------------------------------------------------------
    public void setVisible(boolean aFlag)
    {
        setCurrentFocus(getTransactionList());
    }

    //---------------------------------------------------------------------
    /**
     * Return the SuspendListItemRenderer property value.
     * @return SuspendListItemRenderer
     */
    //---------------------------------------------------------------------
    protected SuspendListItemRenderer getSuspendListItemRenderer()
    {
        return transactionItemRenderer;
    }

    //---------------------------------------------------------------------
    /**
     * Return the HeaderBean property value.
     * @return SuspendListHeaderBean
     */
    //---------------------------------------------------------------------
    protected SuspendListHeaderBean getHeaderBean()
    {
        return transactionHeaderBean;
    }

    //---------------------------------------------------------------------
    /**
     * Return the ListModel property value.
     * @return DefaultListModel
     */
    //---------------------------------------------------------------------
    protected DefaultListModel getTransModel()
    {
        return transModel;
    }

    //---------------------------------------------------------------------
    /**
     * Return the TransactionList property value.
     * @return JList
     */
    //---------------------------------------------------------------------
    protected JList getTransactionList()
    {
        return transactionList;
    }

    //---------------------------------------------------------------------
    /**
     * Return the TransactionPane property value.
     * @return JScrollPane
     */
    //---------------------------------------------------------------------
    protected JScrollPane getTransactionPane()
    {
        return transactionPane;
    }

    //---------------------------------------------------------------------
    /**
     * This updates model from the bean.  This bean uses the TransactionListBeanModel.
     * @return java.lang.Object
     * @see TransactionListBeanModel
     */
    //---------------------------------------------------------------------
    public void updateModel()
    {
        beanModel.setSelectedRow(transactionList.getSelectedIndex());
    }

    //---------------------------------------------------------------------
    /**
     * This method sets the model in the bean. This bean uses the TransactionListBeanModel.
     * @param beanModel java.lang.Object
     * @see TransactionListBeanModel
     */
    //---------------------------------------------------------------------
    public void setModel(UIModelIfc obj)
    {
        if (obj instanceof TransactionListBeanModel)
        {
            beanModel = (TransactionListBeanModel) obj;
            updateBean();
        }
    }

    //---------------------------------------------------------------------
    /**
     * Update the model if It's been changed
     */
    //---------------------------------------------------------------------
    protected void updateBean()
    {
        // set the transaction summary
        getTransactionList().setModel(array2Model(beanModel.getTransactionSummary()));
    }

    //---------------------------------------------------------------------
    /**
     *  Set the properties to be used by this bean
        @param props the propeties object
     */
    //---------------------------------------------------------------------
    public void setProps(Properties props)
    {
        if (transactionHeaderBean != null)
        {
            transactionHeaderBean.setProps(props);
        }
    }

    //---------------------------------------------------------------------
    /**
       Converts a array contents into a DefaultListModel
       @return DefaultListModel<SaleReturnLineItem>
    */
    //---------------------------------------------------------------------
    static DefaultListModel array2Model(TransactionSummaryIfc[] trans)
    {
        DefaultListModel listModel = new DefaultListModel();

        for (int i = 0; i < trans.length; i++)
        {
            listModel.addElement(trans[i]);
        }
        return listModel;
    }

    //---------------------------------------------------------------------
    /**
     * Initializes connections
     */
    //---------------------------------------------------------------------
    protected void initConnections()
    {
        getTransactionList().setModel(getTransModel());
        getTransactionPane().setViewportView(getTransactionList());
    }

    //---------------------------------------------------------------------
    /**
     * Initialize the class.
     */
    //---------------------------------------------------------------------
    protected void initialize()
    {
        transactionItemRenderer = new SuspendListItemRenderer();
        transactionItemRenderer.setName("SuspendListItemRenderer");
        transactionItemRenderer.setBounds(95, 358, 488, 44);

        transactionHeaderBean = new SuspendListHeaderBean();
        transactionHeaderBean.setName("SuspendListHeaderBean");

        transModel = new DefaultListModel();

        transactionList = new JList();
        transactionList.setName("TransactionList");
        transactionList.setCellRenderer(getSuspendListItemRenderer());
        transactionList.setPrototypeCellValue(getSuspendListItemRenderer().createPrototype());
        transactionList.setBounds(0, 0, 160, 120);
        transactionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        transactionPane = new JScrollPane();
        transactionPane.setName("TransactionPane");

        GridBagConstraints constraintsHeaderBean = new GridBagConstraints();
        GridBagConstraints constraintsTransactionPane = new GridBagConstraints();
        setName("SuspendListBean");
        setLayout(new java.awt.GridBagLayout());
        setBackground(new Color(204,204,204));
        setSize(512, 262);

        constraintsHeaderBean.gridx = 0; constraintsHeaderBean.gridy = 0;
        constraintsHeaderBean.gridwidth = 1; constraintsHeaderBean.gridheight = 1;
        constraintsHeaderBean.fill = GridBagConstraints.HORIZONTAL;
        constraintsHeaderBean.anchor = GridBagConstraints.CENTER;
        constraintsHeaderBean.weightx = 1.0;
        constraintsHeaderBean.weighty = 0.0;
        add(getHeaderBean(), constraintsHeaderBean);

        constraintsTransactionPane.gridx = 0; constraintsTransactionPane.gridy = 3;
        constraintsTransactionPane.gridwidth = 1; constraintsTransactionPane.gridheight = 1;
        constraintsTransactionPane.fill = GridBagConstraints.BOTH;
        constraintsTransactionPane.anchor = GridBagConstraints.CENTER;
        constraintsTransactionPane.weightx = 0.0;
        constraintsTransactionPane.weighty = 1.0;
        add(getTransactionPane(), constraintsTransactionPane);
        initConnections();
    }

    //---------------------------------------------------------------------
    /**
        The following method is required by the TableBeanIfc; it returns
        the current row.
     */
    //---------------------------------------------------------------------
    public int getSelectedRow()
    {
        return transactionList.getSelectedIndex();
    }

    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object.
        <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  SuspendListBean (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

    //---------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    //---------------------------------------------------------------------
    public static void main(java.lang.String[] args)
    {
        // Create bean
        SuspendListBean aSuspendListBean;
        aSuspendListBean = new SuspendListBean();

        // Create model
        TransactionSummaryIfc[] tsi = new TransactionSummaryIfc[3];
        tsi[0] = (TransactionSummaryIfc)aSuspendListBean.getSuspendListItemRenderer().createPrototype();
        tsi[1] = (TransactionSummaryIfc)aSuspendListBean.getSuspendListItemRenderer().createPrototype();
        tsi[2] = (TransactionSummaryIfc)aSuspendListBean.getSuspendListItemRenderer().createPrototype();
        TransactionListBeanModel tlbm = new TransactionListBeanModel();
        tlbm.setTransactionSummary(tsi);
        aSuspendListBean.setModel(tlbm);
        aSuspendListBean.activate();

        // Display bean
        java.awt.Frame frame = new java.awt.Frame();
        frame.add("Center", aSuspendListBean);
        frame.setSize(aSuspendListBean.getSize());
        frame.setVisible(true);
    }
}
