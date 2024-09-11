/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/TenderBean.java /main/19 2014/02/24 10:51:48 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     02/24/14 - Modified to prevent exit from refund options when
 *                         refunding failed gift card activation.
 *    rahravin  08/29/13 - Enable delete in splitting a tender
 *    rrkohli   05/06/11 - methods commented for POS UI quickwin
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   03/23/10 - Call getTotalsBean once then refer to local variable
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  8    360Commerce 1.7         6/12/2007 8:48:26 PM   Anda D. Cadar   SCR
 *       27207: Receipt changes -  proper alignment for amounts
 *  7    360Commerce 1.6         4/25/2007 8:51:26 AM   Anda D. Cadar   I18N
 *       merge
 *  6    360Commerce 1.5         5/12/2006 5:25:36 PM   Charles D. Baker
 *       Merging with v1_0_0_53 of Returns Managament
 *  5    360Commerce 1.4         2/10/2006 1:21:56 PM   Brett J. Larsen Merge
 *       from TenderBean.java, Revision 1.3.1.0
 *  4    360Commerce 1.3         1/25/2006 4:11:50 PM   Brett J. Larsen merge
 *       7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *  3    360Commerce 1.2         3/31/2005 4:30:22 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:25:53 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:14:48 PM  Robert Pearse
 *:
 *  4    .v700     1.2.1.0     11/28/2005 10:27:24    Deepanshu       CR 4011:
 *       Update the Tender amount and Balance due in the summary section of the
 *       work panel
 *  3    360Commerce1.2         3/31/2005 15:30:22     Robert Pearse
 *  2    360Commerce1.1         3/10/2005 10:25:53     Robert Pearse
 *  1    360Commerce1.0         2/11/2005 12:14:48     Robert Pearse
 *
 * Revision 1.3  2004/03/16 17:15:18  build
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 20:56:27  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 * updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Oct 21 2003 10:28:44   epd
 * Added functionality for Delete Tender functionality from Tender Options screens
 *
 *    Rev 1.0   Aug 29 2003 16:12:48   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Aug 14 2002 18:19:00   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:55:08   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:58:06   msg
 * Initial revision.
 *
 *    Rev 1.5   13 Mar 2002 23:46:18   baa
 * fix painting problems
 * Resolution for POS SCR-1343: Split tender using Cash causes half of Tender Options to flash
 *
 *    Rev 1.4   Mar 01 2002 10:02:58   mpm
 * Internationalization of tender-related screens
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.3   30 Jan 2002 16:42:54   baa
 * ui fixes
 * Resolution for POS SCR-965: Add Customer screen UI defects
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Properties;
import java.util.Vector;

import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.tender.AuthorizableTenderIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.behavior.ClearActionListener;
import oracle.retail.stores.pos.ui.behavior.EnableButtonListener;
import oracle.retail.stores.pos.ui.behavior.GlobalButtonListener;

/**
 * This bean uses the TenderBeanModel
 *
 * @see oracle.retail.stores.pos.ui.beans.TenderBeanModel
 */
public class TenderBean extends ListBean
    implements PropertyChangeListener, ClearActionListener, DocumentListener
{
    private static final long serialVersionUID = -7137517720772418075L;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/19 $";

    // Tender Totals Bean
    protected AbstractTotalsBean totalsBean;

    // Current selected row
    protected int selectedRow = -1;

    // Row is highlighted flag
    protected boolean highlighted = false;

    // Transaction Totals
   // protected TransactionTotalsIfc totals; commented for pos ui quickwin 

    // List of Tender line items
    protected Vector<TenderLineItemIfc> tenderLineItems;

    /**
     * The action to send when clearing.
     */
    public static final String CLEAR_ACTION = "Clear";

    /**
     * The global enable button listener
     */
    protected EnableButtonListener globalButtonListener = null;

    /**
     * array of rows to delete Object array of TenderLineItemIfc objects
     */
    protected TenderLineItemIfc tenderToDelete = null;

    /**
     * The selected tender index
     */
    protected int indexOfTenderToDelete = -1;

    /**
     * Constructor
     */
    public TenderBean()
    {
        setName("TenderBean");
    }

    /**
     * Activate this screen.
     */
/*    @Override  commented for pos ui quickwin 
    public void activate()
    {
        super.activate();
        getTotalsBean().activate();
    }*/

    /**
     * Sets the totals bean.
     *
     * @param propValue the class name of the totals bean
     */
    /*public void setTotalsBean(String propValue) //commented for pos ui quickwin 
    {
        if(propValue != null)
        {
            if(totalsBean == null ||
               !totalsBean.getClass().getName().equals(propValue))
            {
                totalsBean =
                    (AbstractTotalsBean)UIUtilities.getNamedClass(propValue);
            }
        }
        if(totalsBean == null)
        {
            totalsBean = new TenderTotalsBean();
        }
        add(totalsBean, BorderLayout.SOUTH);
    }*/

    /**
     * Gets the totals bean.
     */
/*    protected AbstractTotalsBean getTotalsBean() //commented for pos ui quickwin 
    {
        if (totalsBean == null)
        {
            // Initialize the totals panel
            setTotalsBean(null);
        }
        return totalsBean;
    }*/

    /**
     *  Configures the list.
     */
    @Override
    protected void configureList()
    {
        super.configureList();
        list.addPropertyChangeListener(this);
    }

    /**
     * Set the properties to be used by this bean
     *
     * @param props the propeties object
     */
    @Override
    public void setProps(Properties props)
    {
        super.setProps(props);

        // getTotalsBean().setProps(props); commented for pos ui quickwin 
    }

    /**
     * Updates the model
     *
     * @see oracle.retail.stores.pos.ui.beans.TenderBeanModel
     */
    @Override
    public void updateModel()
    {
        if(beanModel instanceof TenderBeanModel)
        {
            TenderBeanModel model = (TenderBeanModel)beanModel;

            if (tenderToDelete != null)
            {
                model.setTenderToDelete(tenderToDelete);
                model.setIndexOfTenderToDelete(indexOfTenderToDelete);
                tenderToDelete = null;
                indexOfTenderToDelete = -1;
            }
        }
    }

    /**
     * Update the bean if the model has changed
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void updateBean()
    {
        if(beanModel != null && beanModel instanceof TenderBeanModel)
        {
            TenderBeanModel model = (TenderBeanModel)beanModel;
            tenderLineItems = model.getTenderLineItems();

            // Set lineItem lists
            POSListModel listModel = new POSListModel(tenderLineItems);
            getList().setModel(listModel);

            // Set totals
          /*  totals = model.getTransactionTotals(); commented for pos ui quickwin 
            if(totals != null)
            {
                AbstractTotalsBean totalsBean = getTotalsBean();
                totalsBean.getModel().setTotals(totals);
                // Set tendered and balance due for Summary section view.
                if (tenderLineItems.size() > 0)
                {
                    TenderLineItemIfc[] items = new TenderLineItemIfc[tenderLineItems.size()];
                    tenderLineItems.toArray(items);
                    CurrencyIfc tempAmount = null;
                    CurrencyIfc tenderAmount = null;
                    CurrencyIfc balAmount = null;
                    CurrencyIfc grandTotal;

                    for (int i = 0; i < items.length; i++)
                    {
                        tempAmount = items[i].getAmountTender();
                    	if (tempAmount != null && tempAmount.signum() != CurrencyIfc.NEGATIVE)
                        {
                            if (tenderAmount != null)
                            {
                                tenderAmount = tenderAmount.add(tempAmount);
                            }
                            else
                            {
                                tenderAmount = tempAmount;
                            }
                        }
                    }
                    if (tenderAmount != null)
                    {
                        totalsBean.getModel().setTendered(tenderAmount.toFormattedString());
                        grandTotal = getTotals().getGrandTotal();
                        if (grandTotal != null)
                        {
                            balAmount = grandTotal.subtract(tenderAmount);
                            totalsBean.getModel().setBalanceDue(balAmount.toFormattedString());
                        }
                    }
                }
                totalsBean.updateBean();
            }*/


            // Always scroll to the last tender item entered
            int lastItemIndex = listModel.size()-1;

            list.setSelectedIndex(lastItemIndex);
            list.ensureIndexIsVisible(lastItemIndex);

            //After bean has been updated, set "clear" button appropriately
            manageClearButton();
        }
    }

    /**
     * Gets the transaction totals
     *
     * @return TransactionTotalsIfc totals
     */
   /* public TransactionTotalsIfc getTotals() commented for pos ui quickwin 
    {
        return totals;
    }*/

    /**
     * De-activates the bean
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
    }

    /**
     * Adds a TenderLineItem to the bean. There is never a selected row.
     *
     * @param index int the index to add
     * @param item java.lang.Object The TenderLineItem to add
     */
    public void addItem(int index, Object item)
    {
        // Make sure it's a valid item
        if (!(item instanceof TenderLineItemIfc))
        {
            throw new ClassCastException("Trying to add an item to TenderBean that is not type TenderLineItemIfc");
        }

        // This updates the transaction visually
        //TenderLineItemIfc tenderItem = (TenderLineItemIfc) item;
        ((POSListModel)list.getModel()).add(index, item);
        setSelectedRow(-1);
    }

    /**
     *
     * @param index int
     */
    public void deleteItem(int index)
    {
        // this updates the transaction visually
        ((POSListModel)list.getModel()).remove(index);
        setSelectedRow(-1);
    }

    /**
     *
     * @param index int
     * @param item java.lang.Object
     */
    public void modifyItem(int index, Object item)
    {
        if (!(item instanceof TenderLineItemIfc))
        {
            throw new ClassCastException("Trying to add an item to TenderBean that is not type TenderLineItemIfc");
        }

        TenderLineItemIfc tenderItem = (TenderLineItemIfc) item;
        // this updates the transaction visually
        ((POSListModel)list.getModel()).setElementAt(tenderItem, index);
        setSelectedRow(-1);
    }

    /**
     * Method to handle events for the PropertyChangeListener interface.
     * @param evt java.beans.PropertyChangeEvent
     */
    public void propertyChange(PropertyChangeEvent evt)
    {
       /* if ((evt.getSource() == list) && (evt.getPropertyName().equals("model")))
        {
            //tenderItemsModel = (POSListModel)list.getModel();
            if(totals != null)
            {
                getTotalsBean().getModel().setTotals(totals);
            }
        }*/
    }

    /**
     * This gets called whenever the tenderItemsModel is changed
     */
    public void tenderItemsModel_ContentsChanged()
    {
        //DefaultListModel tenderItemsModel = tenderItemsModel();
        /*int n = tenderItemsModel.size();
        Vector tenderLineItems = new Vector();

        for (int i = 0; i < n; i++)
        {
            TenderLineItemIfc item = (TenderLineItemIfc)tenderItemsModel.elementAt(i);
            tenderLineItems.insertElementAt(item, i);
        }*/
    }

    /**
     * This method sets the selected row. If the internal variable, highlighted,
     * is true, it also makes sure the row is highlighted. If the internal
     * variable, highlighted, is false, it simply sets the internal selectedRow
     * variable to the index.
     * <p>
     * If <code>index &lt; 0</code> or <code>index &gt; getSaleItemList().getSize()</code>,
     * then the internal selectedRow variable is set to -1.
     *
     * @param index The index of the selected row.
     */
    public void setSelectedRow(int index)
    {
        if (highlighted && 0 < index && index < list.getModel().getSize())
        {
            list.ensureIndexIsVisible(index);
            list.setSelectedIndex(index);
        }
        else
        {
            index=-1;
            list.clearSelection();
        }
        selectedRow = index;
    }

    //---------------------------------------------------------------------
    //  Class to handle Key events for the list
    //---------------------------------------------------------------------
    protected class ListKeyListener implements KeyListener
    {
        public void keyPressed(KeyEvent evt)
        {
            int rowCnt = list.getModel().getSize();

            if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            {
            }
            else if (rowCnt >= 2)
            {
                switch (evt.getKeyCode())
                {
                    // no default behaviour!
                    case KeyEvent.VK_PAGE_UP:
                        pageList(rowCnt , 1);
                        break;
                    // no default behaviour!
                    case KeyEvent.VK_PAGE_DOWN:
                        pageList(rowCnt , -1);
                        break;
                    default:
                        break;
                    }
            }
        }

        public void keyReleased(KeyEvent evt)
        {
        }
        public void keyTyped(KeyEvent evt)
        {
        }
    }

    /**
     * Handles the page up page down functionality for the list
     */
    protected void pageList(int listCnt, int direction)
    {
        int blockInc = list.getScrollableBlockIncrement(list.getVisibleRect(), SwingConstants.VERTICAL, direction);

        int visIndex;
        visIndex = (direction == -1) ? list.getMaxSelectionIndex() : list.getMinSelectionIndex();

        if (visIndex >= 0)
        {
            Point curPnt = list.indexToLocation(visIndex);

            curPnt.y = (direction == -1) ? curPnt.y + blockInc : curPnt.y - blockInc;

            int newIndex = list.locationToIndex(curPnt);

            if (newIndex < 0) // invalid
            {
                newIndex = (direction == -1) ? listCnt - 1 : 0;
            }

            list.ensureIndexIsVisible(newIndex);
            list.setSelectedIndex(newIndex);
        }
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

    /**
     * Adds (actually sets) the enable button listener on the bean.
     *
     * @param listener the global listener button
     */
    public void addGlobalButtonListener(GlobalButtonListener listener)
    {
        globalButtonListener = listener;
    }

    /**
     * Removes the enable button listener from the bean.
     *
     * @param listener the global button listener
     */
    public void removeGlobalButtonListener(GlobalButtonListener listener)
    {
        // line item can't be deleted by removing the listener. 
    }

    /**
     * Determines if the response field has text and sets the "Clear" button
     * appropriately.
     *
     * @Param evt the document event
     */
    public void checkAndEnableButtons(DocumentEvent evt)
    {
        manageClearButton();
    }

    /**
     * Determines if the clear button should be enabled.
     */
    public void manageClearButton()
    {
        if(beanModel instanceof TenderBeanModel)
        {
            TenderBeanModel model = (TenderBeanModel)beanModel;

            if (model.isManageClearButton() && globalButtonListener != null)
            {
                if (list.getModel().getSize() >= 0 && !isAuthorizedTender())
                {
                    globalButtonListener.enableButton(CLEAR_ACTION, true);
                }
                else
                {
                    globalButtonListener.enableButton(CLEAR_ACTION, false);
                }
            }
        }
    }

    /**
     * This Function will check to see if the selected tender in the list is an
     * authorized tender.
     *
     * @return true if the selected tender is authorized
     */
    public boolean isAuthorizedTender()
    {
        int selection = -1;
        boolean delete = true;

        if(list != null) // make sure we arent null before we access the object.
        {
            selection = list.getSelectedIndex();
        }

        // check for a valid selection
        if(selection != -1)
        {
            if (list.getModel().getElementAt(selection) instanceof AuthorizableTenderIfc)
            {
                AuthorizableTenderIfc authTender = (AuthorizableTenderIfc)list.getModel().getElementAt(selection);

                // If it is an approved Tender, we will not allow deletion of the tender
                if(authTender.getAuthorizationStatus() == AuthorizableTenderIfc.AUTHORIZATION_STATUS_APPROVED)
                {
                    delete = true;
                }
                else    // Its not an authorized tender, or the tender is not authorized yet
                {       // Delete all selected rows from the list, then mail the letter to
                        // the business logic.
                    delete = false;
                }
            }
            else
            {
                delete = false;
            }
        }
        return delete;
    }

    /**
     * This event is called when the user presses "clear".  We can not allow
     * the deletion of a approved tender (i.e. debit, credit, etc.)
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent evt)
    {
        if (evt.getActionCommand().equalsIgnoreCase(CLEAR_ACTION))
        {
            int selection = list.getSelectedIndex();

            // check for a valid selection
            if(selection != -1)
            {
                if (list.getModel().getElementAt(selection) instanceof AuthorizableTenderIfc)
                {
                    AuthorizableTenderIfc authTender =
                                (AuthorizableTenderIfc)list.getModel().getElementAt(selection);

                    // If it is an approved Tender, we will not allow deletion of the tender
                    if(authTender.getAuthorizationStatus() == AuthorizableTenderIfc.AUTHORIZATION_STATUS_APPROVED)
                    {
                        globalButtonListener.enableButton(CLEAR_ACTION, false);
                        return;
                    }

                    // Its not an authorizable tender, so
                    // Delete all selected rows from the list, then mail the letter to
                    // the business logic.
                    tenderToDelete = (TenderLineItemIfc)list.getSelectedValue();
                    indexOfTenderToDelete = list.getSelectedIndex();
                    //deleteItem(list.getSelectedRow());
                    UISubsystem.getInstance().mail(new Letter(CLEAR_ACTION), true);
                    globalButtonListener.enableButton(CLEAR_ACTION, false);
                }
                else    // Its not an authorizable tender, so
                {       // Delete all selected rows from the list, then mail the letter to
                        // the business logic.
                    tenderToDelete = (TenderLineItemIfc)list.getSelectedValue();
                    indexOfTenderToDelete = list.getSelectedIndex();
                    //deleteItem(list.getSelectedRow());
                    UISubsystem.getInstance().mail(new Letter(CLEAR_ACTION), true);
                    globalButtonListener.enableButton(CLEAR_ACTION, false);
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ListBean#toString()
     */
    @Override
    public String toString()
    {
        String strResult = new String("Class: TenderBean (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }

    /**
     * Retrieves the Team Connection revision number.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        TenderBean bean = new TenderBean();
        bean.setLabelText("Type,Number,Amount");
        bean.setLabelWeights("60,20,20");

        TenderBeanModel model = new TenderBeanModel();
        Vector<Object> items = new Vector<Object>(3);
        AbstractListRenderer r = (AbstractListRenderer)bean.getRenderer();
        items.add(r.createPrototype());
        items.add(r.createPrototype());
        items.add(r.createPrototype());
        model.setTenderLineItems(items);
        bean.setModel(model);
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}
