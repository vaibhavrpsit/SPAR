/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/TransactionListBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:45 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:35 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:23 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:15 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Sep 16 2003 17:53:32   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * 
 *    Rev 1.0   Aug 29 2003 16:12:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:19:06   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:56:06   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:52:38   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:32:30   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 21 2001 11:34:54   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:16:14   msg
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

import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;

//----------------------------------------------------------------------------
/**
   This class displays a list of transaction history interface objects. <p>
   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
*/
//----------------------------------------------------------------------------
public class TransactionListBean extends CycleRootPanel 
{
    /**
        revision number supplied by source-code-control system
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        Builds each line item
    **/
    protected TransactionItemRenderer transactionItemRenderer = null;
    /**
        Header bean
    **/
    protected TransactionHeaderBean transactionHeaderBean = null;
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
    public TransactionListBean() 
    {
        super();
        initialize();
    }

    //---------------------------------------------------------------------
    /**
     * Initialize the class.
     */
    //---------------------------------------------------------------------
    protected void initialize() 
    {
        GridBagConstraints constraintsHeaderBean = new GridBagConstraints();
        GridBagConstraints constraintsTransactionPane = new GridBagConstraints();
        setName("TransactionListBean");
        setLayout(new java.awt.GridBagLayout());
        setBackground(new Color(204,204,204));
        setSize(512, 262);

        constraintsHeaderBean.gridx = 0; constraintsHeaderBean.gridy = 0;
        constraintsHeaderBean.gridwidth = 1; constraintsHeaderBean.gridheight = 1;
        constraintsHeaderBean.fill = GridBagConstraints.HORIZONTAL;
        constraintsHeaderBean.anchor = GridBagConstraints.CENTER;
        constraintsHeaderBean.weightx = 1.0;
        constraintsHeaderBean.weighty = 0.0;
        transactionHeaderBean = new TransactionHeaderBean();
        transactionHeaderBean.setName("TransactionHeaderBean");
        add(transactionHeaderBean, constraintsHeaderBean);

        constraintsTransactionPane.gridx = 0; constraintsTransactionPane.gridy = 3;
        constraintsTransactionPane.gridwidth = 1; constraintsTransactionPane.gridheight = 1;
        constraintsTransactionPane.fill = GridBagConstraints.BOTH;
        constraintsTransactionPane.anchor = GridBagConstraints.CENTER;
        constraintsTransactionPane.weightx = 0.0;
        constraintsTransactionPane.weighty = 1.0;
        transactionPane = new JScrollPane();
        transactionPane.setName("TransactionPane");
        add(transactionPane, constraintsTransactionPane);
        initConnections();
    }
    
    //---------------------------------------------------------------------
    /**
     * Initializes connections
     */
    //---------------------------------------------------------------------
    protected void initConnections() 
    {
        transModel = new DefaultListModel();
        
        transactionList = new JList();
        transactionList.setName("TransactionList");
        transactionItemRenderer = new TransactionItemRenderer();
        transactionItemRenderer.setName("TransactionItemRenderer");
        transactionItemRenderer.setBounds(95, 358, 488, 44);
        transactionList.setCellRenderer(transactionItemRenderer);
        transactionList.setPrototypeCellValue(transactionItemRenderer.createPrototype());
        transactionList.setBounds(0, 0, 160, 120);

        transactionList.setModel(transModel);
        transactionPane.setViewportView(transactionList);
    }

    //---------------------------------------------------------------------
    /**
     * This method sets the model in the bean. This bean uses the TransactionListBeanModel.
     * @param beanModel java.lang.Object
     * @see TransactionListBeanModel
     */
    //---------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if (model instanceof TransactionListBeanModel)
        {
            beanModel = (TransactionListBeanModel)model;
            transactionList.setModel(array2Model(beanModel.getTransactionSummary()));
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
        DefaultListModel listModel=new DefaultListModel();
        for(int i=0; i < trans.length; i++)
        {
            listModel.addElement(trans[i]);
        }
        return listModel;
    }

    //---------------------------------------------------------------------
    /**
     * Updates the model with the selected row.
     * @return java.lang.Object
     * @see TransactionListBeanModel
     */
    //---------------------------------------------------------------------
    public void updateModel()
    {
        // The user does not change the list and the cargo in the site
        // code already has it.  Sending it back to the business logic
        // overhead.
        beanModel.setTransactionSummary(null);
        
        beanModel.setSelectedRow(transactionList.getSelectedIndex());
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
            if(transactionList.getSelectedIndex() == -1)
            {
                transactionList.setSelectedIndex(0);
            }
            setCurrentFocus(transactionList);
        }
    }
    
    //--------------------------------------------------------------------------
    /**
     * activate any settings made by this bean to external entities
     */
    //--------------------------------------------------------------------------
    public void activate()
    {
        super.activate();
        transactionList.addFocusListener(this);
    }
     
    //--------------------------------------------------------------------------
    /**
     * deactivate any settings made by this bean to external entities
     */
    //--------------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
        transactionList.removeFocusListener(this);
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
            transactionItemRenderer.setProps(props);
        }
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
        String strResult = new String("Class:  TransactionListBean (Revision " +
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
        TransactionListBean aTransactionListBean;
        aTransactionListBean = new TransactionListBean();

        // Create model
        TransactionSummaryIfc[] tsi = new TransactionSummaryIfc[3];
        TransactionListBeanModel tlbm = new TransactionListBeanModel();
        tlbm.setTransactionSummary(tsi);
        aTransactionListBean.setModel(tlbm);
        aTransactionListBean.activate();

        // Display bean
        java.awt.Frame frame = new java.awt.Frame();
        frame.add("Center", aTransactionListBean);
        frame.setSize(aTransactionListBean.getSize());
        frame.setVisible(true);
    }
}
