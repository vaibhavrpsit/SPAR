/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
*	
*	Rev 1.1     Dec 22, 2016		Ashish Yadav			Credit Card FES
*	Rev 1.0     Oct 19, 2016		Ashish Yadav			Initial Draft Food Totals requirement.
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */ 
package max.retail.stores.pos.ui.beans;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.Properties;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.tender.AuthorizableTenderIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.AbstractListRenderer;
import oracle.retail.stores.pos.ui.beans.AbstractTotalsBean;
import oracle.retail.stores.pos.ui.beans.TenderBean;
import oracle.retail.stores.pos.ui.beans.TenderBeanModel;
import oracle.retail.stores.pos.ui.beans.TenderTotalsBean;
import oracle.retail.stores.pos.ui.behavior.ClearActionListener;
import oracle.retail.stores.pos.ui.behavior.EnableButtonListener;
import oracle.retail.stores.pos.ui.behavior.GlobalButtonListener;

/**
 * This bean uses the TenderBeanModel
 *
 * @see oracle.retail.stores.pos.ui.beans.TenderBeanModel
 */
public class MAXTenderBean extends TenderBean
    implements PropertyChangeListener, ClearActionListener, DocumentListener
{
    private static final long serialVersionUID = -7137517720772418075L;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/19 $";
// Changes start for code merging
protected JLabel foodTotalLabel = null;
	private String FOOD_TOTAL = "Food Total: ";
	private String NON_FOOD_TOTAL = "Non Food Total: ";
	private String EASY_BUY_TOTAL = "Easy Buy Total: ";
	// Changes start for rev 1.1 (Ashish : Credit Card)
	protected TransactionTotalsIfc totals;
	// Changes start for rev 1.1 (Ashish : Credit Card)

	JPanel p = null;
// changes ends for code merging
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
// Changes start for code merging
    public MAXTenderBean()
    {
    	super();
        setName("MAXTenderBean");
    }
// Changes ends for code merging

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
    // Changes start for code merging
        if(beanModel instanceof MAXTenderBeanModel)
        {
            MAXTenderBeanModel model = (MAXTenderBeanModel)beanModel;
	    // changes ends for code merging

            if (tenderToDelete != null)
            {
                model.setTenderToDelete(tenderToDelete);
                model.setIndexOfTenderToDelete(indexOfTenderToDelete);
                tenderToDelete = null;
                indexOfTenderToDelete = -1;
            }
        }
    }

 // Changes start for rev 1.1 (Ashish : Credit Card)
    protected void updateBean()
    {
    // Changes start for code merging
        if(beanModel != null && beanModel instanceof MAXTenderBeanModel)
        {
            MAXTenderBeanModel model = (MAXTenderBeanModel)beanModel;
	    // Changes end for code merging
            tenderLineItems = model.getTenderLineItems();

            // Set lineItem lists
            POSListModel listModel = new POSListModel(tenderLineItems);
            getList().setModel(listModel);

            // Set totals
            totals = model.getTransactionTotals();
            if(totals != null)
            {
                getTotalsBean().getModel().setTotals(totals);
                // Set tendered and balance due for Summary section view.
                if (tenderLineItems.size() > 0)
                {
                	if(p != null){
                		remove(p);
                	}
                	super.configureScrollPane();
                    TenderLineItemIfc[] items = new TenderLineItemIfc[tenderLineItems.size()];
                    tenderLineItems.toArray(items);
                    int numTenderItems = 0;
                    CurrencyIfc tenderAmount = null;
                    CurrencyIfc balAmount = null;
                    CurrencyIfc grandTotal;
                    if (items != null)
                    {
                        numTenderItems = items.length;
                    }
                    for (int i = 0; i < numTenderItems; i++)
                    {
                    	if (items[i].getAmountTender().signum() != CurrencyIfc.NEGATIVE)
                        {
                            if (tenderAmount != null)
                            {
                                tenderAmount = tenderAmount.add(items[i].getAmountTender());
                            }
                            else
                            {
                                tenderAmount = items[i].getAmountTender();
                            }
                        }
                    }
                    if (tenderAmount != null)
                    {
                        getTotalsBean().getModel().setTendered(tenderAmount.toFormattedString(getLocale()));
                        grandTotal = getTotals().getGrandTotal();
                        if (grandTotal != null)
                        {
                            balAmount = grandTotal.subtract(tenderAmount);
                            getTotalsBean().getModel().setBalanceDue(balAmount.toFormattedString(getLocale()));
                        }
                    }
                }
                else
                {
                	this.configureScrollPane();
                	configureFoodTotals(model.getFoodTotal(),model.getNonFoodTotal(),model.getEasyBuyTotal());
                }
                totalsBean.updateBean();
            }
            
            // Always scroll to the last tender item entered
            int lastItemIndex = listModel.size()-1;

            list.setSelectedIndex(lastItemIndex);
            list.ensureIndexIsVisible(lastItemIndex);

            //After bean has been updated, set "clear" button appropriately
            manageClearButton();
        }
    }
    protected AbstractTotalsBean getTotalsBean()
    {
        if(totalsBean == null)
        {
            // Initialize the totals panel
            setTotalsBean(null);
        }
        return totalsBean;
    }
    public void setTotalsBean(String propValue)
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
        //add(totalsBean, BorderLayout.SOUTH);
    }
    public TransactionTotalsIfc getTotals()
    {
        return totals;
    }
 // Changes start for rev 1.1 (Ashish : Credit Card)

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
		    // Changes start for code merging
		    tenderToDelete = (TenderLineItemIfc)list.getSelectedValue();
                           indexOfTenderToDelete = list.getSelectedIndex();
			   UISubsystem.getInstance().mail(new Letter(CLEAR_ACTION), true);
		    // Changes ends for code merging
                        globalButtonListener.enableButton(CLEAR_ACTION, false);
                        //return;
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
     
     // Changes start for code merging
      protected void configureScrollPane(){
    	if(scrollPane != null)
    		remove(scrollPane);
    }
    //Adding the Panel to display Food and Non Food Totals
    protected void configureFoodTotals(BigDecimal foodTotal,BigDecimal nonFoodTotal,BigDecimal easyBuyTotal){
    
    	if(p != null)
    		remove(p);
    	p = new JPanel();
    	foodTotalLabel = new JLabel("<html><b>"+FOOD_TOTAL+foodTotal+"<br><br>"+NON_FOOD_TOTAL+nonFoodTotal+"<br><br>"+EASY_BUY_TOTAL+easyBuyTotal+"</b></html>");
    	foodTotalLabel.setFont(new Font("Helvetica", Font.PLAIN, 20));
    	uiFactory.configureUIComponent(p, "JPanel");
    	p.setLayout(new GridBagLayout());
    	p.add(foodTotalLabel);   
    	p.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    	add(p,BorderLayout.CENTER);
    }
     // Changes ends for code merging
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        MAXTenderBean bean = new MAXTenderBean();
        bean.setLabelText("Type,Number,Amount");
        bean.setLabelWeights("60,20,20");

        MAXTenderBeanModel model = new MAXTenderBeanModel();
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
