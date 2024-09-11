/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CustomerHistoryListBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:44 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:36 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:38 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:22 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:09:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:17:04   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:56:50   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:54:48   msg
 * Initial revision.
 * 
 *    Rev 1.2   Jan 19 2002 10:29:34   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.1   05 Nov 2001 17:37:44   baa
 * Code Review changes. Customer, Customer history Inquiry Options
 * Resolution for POS SCR-244: Code Review  changes
 *
 *    Rev 1.0   19 Oct 2001 15:34:06   baa
 * Initial revision.
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
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
   This class displays a list of transaction history interface objects. <p>
   @version $KW=@(#); $Ver=pos_4.5.0:33; $EKW;
*/
//----------------------------------------------------------------------------
public class CustomerHistoryListBean extends TransactionListBean
{
    /**
        revision number supplied by source-code-control system
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:33; $EKW;";
    /**
        Builds each line item
    **/
    protected CustomerHistoryListRenderer customerHistoryListRenderer = null;
    /**
        Header bean
    **/
    protected CustomerHistoryListHeaderBean customerHistoryListHeaderBean = null;


    //---------------------------------------------------------------------
    /**
     * Constructor
     */
    //---------------------------------------------------------------------
    public CustomerHistoryListBean()
    {
        super();

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
        setName("CustomerHistoryListBean");
        setLayout(new java.awt.GridBagLayout());
        setBackground(new Color(204,204,204));


        constraintsHeaderBean.gridx = 0; constraintsHeaderBean.gridy = 0;
        constraintsHeaderBean.gridwidth = 1; constraintsHeaderBean.gridheight = 1;
        constraintsHeaderBean.fill = GridBagConstraints.HORIZONTAL;
        constraintsHeaderBean.anchor = GridBagConstraints.CENTER;
        constraintsHeaderBean.weightx = 1.0;
        constraintsHeaderBean.weighty = 0.0;
        customerHistoryListHeaderBean = new CustomerHistoryListHeaderBean();
        customerHistoryListHeaderBean.setName("CustomerHistoryListHeaderBean");
        add(customerHistoryListHeaderBean, constraintsHeaderBean);

        constraintsTransactionPane.gridx = 0; constraintsTransactionPane.gridy = 1;
        constraintsTransactionPane.gridwidth = 1; constraintsTransactionPane.gridheight = 1;
        constraintsTransactionPane.fill = GridBagConstraints.BOTH;
        constraintsTransactionPane.anchor = GridBagConstraints.CENTER;
        constraintsTransactionPane.weightx = 1.0;
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
        customerHistoryListRenderer = new CustomerHistoryListRenderer();
        customerHistoryListRenderer.setName("CustomerHistoryItemRenderer");
        transactionList.setCellRenderer(customerHistoryListRenderer);
        transactionList.setModel(transModel);
        transactionPane.setViewportView(transactionList);
    }




    //---------------------------------------------------------------------
    /**
     *  Set the properties to be used by this bean
        @param props the propeties object
     */
    //---------------------------------------------------------------------
    public void setProps(Properties props)
    {
        if (customerHistoryListHeaderBean != null)
        {
            customerHistoryListHeaderBean.setProps(props);
            customerHistoryListRenderer.setProps(props);
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
        String strResult = new String("Class:  CustomerHistoryListBean (Revision " +
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
        return(Util.parseRevisionNumber(revisionNumber));
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
        CustomerHistoryListBean aTransactionListBean;
        aTransactionListBean = new CustomerHistoryListBean();

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
