/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SaleBean.java /main/31 2014/06/30 12:35:32 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   06/30/14 - Add isInStorePriceDuringPickup check for re-price.
 *    yiqzhao   06/24/14 - enable disable pricing button based on the selected
 *                         items.
 *    jswan     06/16/14 - Modified to support display of extended item
 *                         recommended items on the Sale Item Screen.
 *    icole     05/21/14 - Changes to prevent NPE due to line item list being
 *                         null.
 *    yiqzhao   05/02/14 - Disable Delete button for pickup items while doing
 *                         order pickup.
 *    vtemker   02/28/14 - Refresh list contents when scrolling - fix display
 *                         issue with overlapping item links
 *    abhinavs  05/16/13 - Fix to enable or disable pricing sub menus based on
 *                         the line items
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    cgreene   11/27/12 - add mouse listener and item images
 *    yiqzhao   08/02/12 - Make the entered item automatic selected.
 *    sgu       07/03/12 - replace item disposition code to use delivery
 *                         instead of ship
 *    yiqzhao   06/29/12 - manager Pricing button
 *    icole     06/20/12 - Forward Port: Item entered using Item Lookup is
 *                         selected when displayed rather than just
 *                         highlighted.
 *    yiqzhao   04/30/12 - merge checkIfExternalOrderItemExists and
 *                         checkIfShippingChargeItemExists to
 *                         checkIfUnDeletableItemExists
 *    yiqzhao   04/26/12 - Disable Clear button when a Shipping Charge Line
 *                         Item is selected.
 *    rrkohli   05/06/11 - pos ui quickwin
 *    kelesika  10/04/10 - Addressing disabled Enter btn
 *    kelesika  10/04/10 - Addressing disabled Enter btn on Sell Item screen
 *    blarsen   08/17/10 - Fixed various warnings.
 *    blarsen   08/17/10 - Adding check to prevent enablement of scanner data
 *                         at inappropriate times. Data manager status changes
 *                         perform an asynchronous refresh/update which caused
 *                         lost scans. The scan data was recieved prematurely
 *                         and the NEXT letter was sent when *not* at the show
 *                         sale screen site.
 *    abondala  08/09/10 - disable delete button for external order items
 *    abondala  06/22/10 - Disable Clear Button If ExternalOrder item exists in
 *                         the Transaction
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:29:48 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:24:58 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:14:00 PM  Robert Pearse
 *
 *  Revision 1.5  2004/07/19 21:42:00  cdb
 *  @scr 3511 Removed assertion error.
 *
 *  Revision 1.4  2004/05/11 14:33:00  jlemieux
 *  @scr
 *  270 Fixed by adding a veto mechanism to the lifting of the GlassComponent. In particular, the GlassComponent lift is now vetoed when the scanner's scan queue contains 1 or more items and we are on a multiscan screen in POS. This effectively makes POS "prefer" to drain scan queues rather than service user input, which is what we want.
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
 *    Rev 1.3   28 Jan 2004 20:32:28   baa
 * set focus index
 *
 *    Rev 1.2   26 Jan 2004 00:56:24   baa
 * allow for multiple selections
 *
 *    Rev 1.1   Dec 30 2003 16:59:12   baa
 * cleanup for return feature
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 *
 *    Rev 1.0   Aug 29 2003 16:11:54   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Nov 21 2002 10:19:50   mpb
 * SCR #2857
 * In updateBean()  and updateModel(), verify the class of the object before casting it.
 * Resolution for kbpos SCR-2857: ClassCastException during receipt printing
 *
 *    Rev 1.3   Aug 14 2002 18:18:30   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   05 Jun 2002 22:02:46   baa
 * support for  opendrawerfortrainingmode parameter
 * Resolution for POS SCR-1645: Training Mode Enhancements
 *
 *    Rev 1.1   14 May 2002 18:30:08   baa
 * training mode enhancements
 * Resolution for POS SCR-1645: Training Mode Enhancements
 *
 *    Rev 1.0   Apr 29 2002 14:57:22   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:57:18   msg
 * Initial revision.
 *
 *    Rev 1.5   Feb 27 2002 21:25:54   mpm
 * Continuing work on internationalization
 * Resolution for POS SCR-228: Merge VABC, Pier 1 changes
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.4   Feb 25 2002 10:51:16   mpm
 * Internationalization
 * Resolution for POS SCR-228: Merge VABC, Pier 1 changes
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.3   30 Jan 2002 16:42:52   baa
 * ui fixes
 * Resolution for POS SCR-965: Add Customer screen UI defects
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.ListCellRenderer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;

import oracle.retail.stores.common.item.ExtendedItemData;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.BeanChangeEvent;
import oracle.retail.stores.foundation.manager.gui.BeanChangeListener;
import oracle.retail.stores.foundation.manager.gui.EnableUIEventsVetoer;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.gui.URLLabel;
import oracle.retail.stores.pos.ui.OnlineStatusContainer;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.behavior.ClearActionListener;
import oracle.retail.stores.pos.ui.behavior.EnableButtonListener;
import oracle.retail.stores.pos.ui.behavior.GlobalButtonListener;
import oracle.retail.stores.pos.ui.behavior.LocalButtonListener;

/**
 * The sale bean presents the functionality of the sale screen.
 *
 * @version $Revision: /main/31 $
 */
public class SaleBean extends ListBean
    implements DocumentListener, ClearActionListener, EnableUIEventsVetoer
{

    private static final long serialVersionUID = -9115713935468176035L;

    /** revision number * */
    public static final String revisionNumber = "$Revision: /main/31 $";

    public static final int H_GAP = 1;

    public static final int V_GAP = 1;

    /** Constant for the clear action name * */
    protected static final String CLEAR_ACTION = "Clear";

    /** Constant for the next action name * */
    protected static final String NEXT_ACTION = "Next";

    /** Constant for the pricing action name * */
    protected static final String PRICING_ACTION = "Pricing";

    /** The local enable button listener * */
    protected EnableButtonListener localButtonListener;

    /** The global enable button listener * */
    protected EnableButtonListener globalButtonListener;

    /** Handle for a BeanChangeListener * */
    protected BeanChangeListener beanChangeListener;

    /** A listener for handling mouse motion and click events on description label. */
    protected ItemDescriptionMouseListener descriptionMouseListener;

    /** array of rows to delete * */
    protected int[] rowsToDelete = null;

    private boolean itemIdEntered = true;
    
    /** Indicates if the recommended item has been added to the Sale Bean. */
    private boolean isRecommendedItemBeanDisplayed = false;

    /** Bean for the panel that shows recommended items. */
    protected RecommendedItemsBean recommendedItemsBean = null;

    /**
     * Default constructor.
     */
    public SaleBean()
    {
        super();
        descriptionMouseListener = new ItemDescriptionMouseListener();
        setName("SaleBean");
    }

    /**
     * Configures the list.
     */
    @Override
    protected void configureList()
    {
        super.configureList();
        list.setAutoscrolls(true);
    }

    /**
     * Activate this screen.
     */
    @Override
    public void activate()
    {
        super.activate();
        list.setEnabled(true);
        list.addMouseMotionListener(descriptionMouseListener);
        list.addMouseListener(descriptionMouseListener);
        getScrollPane().getViewport().addChangeListener(descriptionMouseListener);
    }

    /**
     * Deactivate this screen.
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
        list.setEnabled(false);
        list.removeMouseMotionListener(descriptionMouseListener);
        list.removeMouseListener(descriptionMouseListener);
        getScrollPane().getViewport().removeChangeListener(descriptionMouseListener);
    }

    /**
     * Updates the model if It's been changed
     */
    @SuppressWarnings("unchecked")
    @Override
    public void updateBean()
    {
        // verify that the beanModel is of the expected type
        if (beanModel instanceof LineItemsModel)
        { // Begin bean model is expected type
            LineItemsModel model = (LineItemsModel)beanModel;

            // mark all the images as loading if they have not been loaded yet
            AbstractTransactionLineItemIfc[] items = model.getLineItems();
            for (int i = 0; i < items.length; i++)
            {
                if (items[i] instanceof SaleReturnLineItemIfc)
                {
                    SaleReturnLineItemIfc item = (SaleReturnLineItemIfc)items[i];
                    if (item.getPLUItem() != null && item.getPLUItem().getItemImage() != null)
                    {
                        loadImage(item.getPLUItem().getItemImage(), i);
                    }
                }
            }

            StatusBeanModel statusBeanModel = model.getStatusBeanModel();

            if (statusBeanModel != null)
            {
                OnlineStatusContainer onlineStatusContainer = statusBeanModel.getStatusContainer();

                if (onlineStatusContainer != null)
                {
                    Hashtable<Integer, Boolean> hashtable = onlineStatusContainer.getStatusHash();
                    Object object = hashtable.get(new Integer(POSUIManagerIfc.TRAINING_MODE_STATUS));
                    if (object != null)
                    {
                        Boolean trainingMode = (Boolean)object;
                        setApplicationBackground(trainingMode.booleanValue());
                    }
                }
            }

            POSListModel listModel = model.getListModel();
            getList().setModel(listModel);

            // If there is a line item ...
            if (listModel != null && listModel.getSize() > 0)
            {
                list.clearSelection();

                // Check for highlight position
                if (model.getMoveHighlightToTop())
                {
                    list.setSelectedIndex(0);
                }
                else
                {
                    if (model.getSelectedRows() != null && (model.getSelectedRows().length > 0))
                    {
                        list.setSelectedIndices(model.getSelectedRows());
                    }
                    else
                    {
                        list.setSelectedIndex(model.getSelectedRow());
                    }

                }
            }
            else
            {
                getList().setSelectedIndex(EYSList.NO_SELECTION);
            }

            // Tell the Scanner Session that we're ready to handle scanner data
            if (isStatusChangeUpdate())
            {
                logger
                        .debug("Update/refresh requested.  But, update is for data manager status change.  "
                                + "Status change is asynchronous.  Sale may not be ready for scanner data.  So, not calling notifyReadyForData()");
            }
            else
            {
                notifyReadyForData();
            }

            // After bean has been updated, set "clear" button appropriately
            manageClearButton();
            managePriceButton();
            manageRecommendedItemsPanel(model.getRecommendedItems());
        } // End bean model is expected type

    }

    /**
     * Is the update caused by a change to the data manager status?
     * <p>
     * Typically the refresh/update is caused by the tour's return to the
     * ShowSaleScreenSite.
     * <p>
     * However, status changes can occur asynchronously and during the middle of
     * the sale tour.
     * <p>
     * You do not want to enable scanner event data in the middle of a tour
     * flow.
     * <p>
     * When scanner data is received, the prompt and response bean receives a
     * scanner event and sends a NEXT letter. This letter is only appropriate
     * when in the ShowSaleScreenSite.
     *
     * @return true if the update is caused by a data manager status change
     */
    private boolean isStatusChangeUpdate()
    {
        boolean isStatusChange = false;

        LineItemsModel model = (LineItemsModel)beanModel;
        StatusBeanModel statusBeanModel = model.getStatusBeanModel();
        if (statusBeanModel != null)
        {
            OnlineStatusContainer onlineStatusContainer = statusBeanModel.getStatusContainer();

            if (onlineStatusContainer != null)
            {
                Hashtable<Integer, Boolean> hashtable = onlineStatusContainer.getStatusHash();
                Object object = hashtable.get(POSUIManagerIfc.DATA_MANAGER_STATUS);

                if (object != null)
                {
                    isStatusChange = true;
                }
            }
        }
        return isStatusChange;
    }

    /**
     * Ordinarily the method would update the LineItemsModel for return to the
     * business logic. However, in this bean the happens in the actionPerfomed()
     * method in response to the NEXT and CLEAR actions.
     */
    public void updateModel()
    {
        if (beanModel instanceof LineItemsModel)
        {
            LineItemsModel model = (LineItemsModel)beanModel;

            if (rowsToDelete != null)
            {
                model.setRowsToDelete(rowsToDelete);
                rowsToDelete = null;
            }
            else 
            {
                model.setSelectedRow(list.getSelectedRow());
                model.setSelectedRows(list.getAllSelectedRows());
            }
            
            if (recommendedItemsBean != null)
            {
                model.setSelectedRecommendedItemID(recommendedItemsBean.getSelectedRecommendedItemID());
            }
        }
    }

    /**
     * Deletes items from the model.
     *
     * @param index The array of indices of items to delete
     */
    public void deleteItems(int[] index)
    {
        int newIndex = 0;
        @SuppressWarnings("rawtypes")
        DefaultListModel model = (DefaultListModel)list.getModel();
        for (int i = index.length - 1; i > -1; i--)
        {
            model.remove(index[i]);
            newIndex = index[i];
        }
        // Adjust index based on the current cursor position.
        int row = list.getSelectedRow();

        if (newIndex < row)
        {
            newIndex = row - 1;
        }
        else if (newIndex > row)
        {
            newIndex = row;
        }
        int size = model.getSize();

        if (newIndex >= size)
        {
            newIndex = size - 1;
        }

        // if selected items remain in the list, set a new selected row
        if (list.isSelectionEmpty() == false)
        {
            list.setSelectedIndex(newIndex);
        }
        else
        {
            list.setSelectedIndex(EYSList.NO_SELECTION);
        }
    }

    /**
     * Modifies an item in the model.
     *
     * @param index The index to modify.
     * @param item the item to replace with.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void modifyItem(int index, Object item)
    {
        ((DefaultListModel)list.getModel()).setElementAt(item, index);
    }

    /**
     * Implemented for the DocumentListener interface.
     *
     * @Param evt the cocument event
     */
    public void changedUpdate(DocumentEvent evt)
    {
        checkAndEnableButtons(evt);
    }

    /**
     * Implemented for the DocumentListener interface.
     *
     * @Param evt the document event
     */
    public void insertUpdate(DocumentEvent evt)
    {
        checkAndEnableButtons(evt);
    }

    /**
     * Implemented for the DocumentListener interface.
     *
     * @Param evt the document event
     */
    public void removeUpdate(DocumentEvent evt)
    {
        checkAndEnableButtons(evt);
    }

    /**
     * Determines if the response field has text and sets the "Next" and "Clear"
     * buttons appropriately.
     *
     * @Param evt the document event
     */
    public void checkAndEnableButtons(DocumentEvent evt)
    {
        if (evt.getDocument().getLength() > 0)
        {
            globalButtonListener.enableButton(NEXT_ACTION, true);
            globalButtonListener.enableButton(CLEAR_ACTION, true);
            itemIdEntered = true;
        }
        else
        {
            itemIdEntered = false;
            globalButtonListener.enableButton(NEXT_ACTION, false);
            if (list.getModel().getSize() > 0)
            {
                globalButtonListener.enableButton(CLEAR_ACTION, true);
            }
            else
            {
                globalButtonListener.enableButton(CLEAR_ACTION, false);
            }
        }
    }

    /**
     * Determines if the "Clear" button should be enabled.
     */
    public void manageClearButton()
    {
        if (globalButtonListener != null)
        {
            if (list.getModel().getSize() > 0)
            {
                if (!checkIfUnDeletableItemExists(list))
                {
                    globalButtonListener.enableButton(CLEAR_ACTION, true);
                }
                else
                {
                    globalButtonListener.enableButton(CLEAR_ACTION, false);
                }
            }
            else
            {
                globalButtonListener.enableButton(CLEAR_ACTION, false);
            }
        }
    }

    /**
     * If the list of recommended items has members, add the recommended item bean
     * to this bean.  If not remove it.
     * @param recommendedItems
     */
    protected void manageRecommendedItemsPanel(List<ExtendedItemData> recommendedItems)
    {
        if (recommendedItems != null && recommendedItems.size() > 0)
        {
            if (recommendedItemsBean == null)
            {
                recommendedItemsBean = new RecommendedItemsBean();
            }
            recommendedItemsBean.setExtendedItemDataList(recommendedItems);
            if (!isRecommendedItemBeanDisplayed)
            {
                add(recommendedItemsBean, BorderLayout.SOUTH);
                isRecommendedItemBeanDisplayed = true;
            }
            recommendedItemsBean.initButtons();
        }
        else if (recommendedItemsBean != null && isRecommendedItemBeanDisplayed)
        {
            remove(recommendedItemsBean);
            recommendedItemsBean.removeAll();
            recommendedItemsBean.resetSessionVariables();
            isRecommendedItemBeanDisplayed = false;
        }
    }

    /**
     * Determines if the "Pricing" button should be enabled.
     */
    public void managePriceButton()
    {
        if (localButtonListener != null)
        {
            if (list.getModel().getSize() > 0)
            {
                if (!checkIfPriceUnchangableItemExists(list))
                {
                    localButtonListener.enableButton(PRICING_ACTION, true);
                }
                else
                {
                    localButtonListener.enableButton(PRICING_ACTION, false);
                }
            }
            else
            {
                localButtonListener.enableButton(PRICING_ACTION, false);
            }
        }
    }
    
    /**
     * If the Item is from ExternalOrder or Shipping Charge, disable the Clear button.
     *
     * If multiple items are selected and at least one of them is from
     * ExternalOrder or ShippingCharge, disable the Clear button.
     */
    protected boolean checkIfUnDeletableItemExists(EYSList multiList)
    {
        // get the selection
        int listLength = multiList.getSelectedIndices().length;
        int[] selectedIndices = multiList.getSelectedIndices();
        int currentItem = multiList.getSelectedRow();
        int selection = list.getSelectedIndex();

        try
        {
            // check for disabling selection
            if (selection != -1)
            {
                if (listLength <= 1)
                {
                    // single item selected
                    if (multiList.getModel().getElementAt(currentItem) instanceof SaleReturnLineItemIfc)
                    {
                        SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)multiList.getModel().getElementAt(
                                currentItem);
                        if (srli.isFromExternalOrder() || srli.isShippingCharge())
                        {
                            return true;
                        }
                        if (srli.isPickupCancelLineItem())
                        {
                            return true;
                        }
                    }
                }

                // does list contain any External Orders
                // Same item can exist as an external order as well as local
                // item with different prices
                for (int i = 0; i < selectedIndices.length; i++)
                {
                    if (multiList.getModel().getElementAt(selectedIndices[i]) instanceof SaleReturnLineItemIfc)
                    {
                        SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)multiList.getModel().getElementAt(
                                selectedIndices[i]);
                        if (srli.isFromExternalOrder() || srli.isShippingCharge())
                        {
                            return true;
                        }
                        if (srli.isPickupCancelLineItem())
                        {
                            return true;
                        }                        
                    }
                }

                if (currentItem != -1)
                {
                    if (multiList.getModel().getElementAt(currentItem) instanceof SaleReturnLineItemIfc)
                    {
                        SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)multiList.getModel().getElementAt(
                                currentItem);
                        if (srli.isFromExternalOrder() || srli.isShippingCharge())
                        {
                            return true;
                        }
                        if (srli.isPickupCancelLineItem())
                        {
                            return true;
                        }                        
                    }
                }
            }
            else if (currentItem != -1 && listLength != -1)
            {
                // no selection but is current item external order?
                Object obj = multiList.getModel().getElementAt(currentItem);
                if (obj instanceof SaleReturnLineItemIfc)
                {
                    SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)multiList.getModel().getElementAt(currentItem);
                    if (srli.isFromExternalOrder() || srli.isShippingCharge())
                    {
                        return true;
                    }
                    if (srli.isPickupCancelLineItem())
                    {
                        return true;
                    }
                }
            }
        }
        catch (Exception ex)
        {
            // when an item is deleted from the list, this may throw exception
            // which can be ignored.
        }

        return false;
    }

    /**
     * If the Item is from ExternalOrder or Shipping Charge, disable the Clear button.
     *
     * If multiple items are selected and at least one of them is from
     * ExternalOrder or ShippingCharge, disable the Clear button.
     */
    protected boolean checkIfPriceUnchangableItemExists(EYSList multiList)
    {
        // get the selection
        int listLength = multiList.getSelectedIndices().length;
        int[] selectedIndices = multiList.getSelectedIndices();
        int currentItem = multiList.getSelectedRow();
        int selection = list.getSelectedIndex();

        try
        {
            // check for disabling selection
            if (selection != -1)
            {
                if (listLength <= 1)
                {
                    // single item selected
                    if (multiList.getModel().getElementAt(currentItem) instanceof SaleReturnLineItemIfc)
                    {
                        SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)multiList.getModel().getElementAt(
                                currentItem);
                        if (isPriceUnchangableItem(srli))
                        {
                            return true;
                        }
                    }
                }

                // does list contain any External Orders
                // Same item can exist as an external order as well as local
                // item with different prices
                for (int i = 0; i < selectedIndices.length; i++)
                {
                    if (multiList.getModel().getElementAt(selectedIndices[i]) instanceof SaleReturnLineItemIfc)
                    {
                        SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)multiList.getModel().getElementAt(
                                selectedIndices[i]);
                        if (isPriceUnchangableItem(srli))
                        {
                            return true;
                        }
                    }
                }

                if (currentItem != -1)
                {
                    if (multiList.getModel().getElementAt(currentItem) instanceof SaleReturnLineItemIfc)
                    {
                        SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)multiList.getModel().getElementAt(
                                currentItem);
                        if (isPriceUnchangableItem(srli))
                        {
                            return true;
                        }
                    }
                }
            }
            else if (currentItem != -1 && listLength != -1)
            {
                // no selection but is current item external order?
                Object obj = multiList.getModel().getElementAt(currentItem);
                if (obj instanceof SaleReturnLineItemIfc)
                {
                    SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)multiList.getModel().getElementAt(currentItem);
                    if (isPriceUnchangableItem(srli))
                    {
                        return true;
                    }
                }
            }
        }
        catch (Exception ex)
        {
            // when an item is deleted from the list, this may throw exception
            // which can be ignored.
        }

        return false;
    }

    /**
     * Returns a boolean flag indicating if a line item's price can be changed
     * @param srli the line item
     * @return the boolean flag
     */
    protected boolean isPriceUnchangableItem(SaleReturnLineItemIfc srli)
    {
        OrderItemStatusIfc orderItemStatus = srli.getOrderItemStatus();
        boolean isDeliveryItem = srli.getOrderItemStatus().getItemDispositionCode()==OrderConstantsIfc.ORDER_ITEM_DISPOSITION_DELIVERY;
    	 if (srli.isFromExternalOrder() || srli.isShippingCharge() ||
             	(orderItemStatus.isCrossChannelItem() && isDeliveryItem))
         {
             return true;
         }
    	 else if (srli.isPickupCancelLineItem() && !srli.isInStorePriceDuringPickup())
    	 {
    	     return true;
    	 }
    	 return false;
    }

    /**
     * This event is called when the user presses "clear".
     *
     * @param evt the action event
     */
    @Override
    public void actionPerformed(ActionEvent evt)
    {
        // Delete all selected rows from the list, then mail the letter to
        // the business logic.
        if (evt.getActionCommand().equalsIgnoreCase(CLEAR_ACTION))
        {
            rowsToDelete = list.getAllSelectedRows();
            deleteItems(rowsToDelete);
            UISubsystem.getInstance().mail(new Letter(CLEAR_ACTION), true);
            globalButtonListener.enableButton(CLEAR_ACTION, false);
        }
    }

    /**
     * Adds (actually sets) the enable button listener on the bean.
     *
     * @param listener
     */
    public void addGlobalButtonListener(GlobalButtonListener listener)
    {
        globalButtonListener = listener;
    }

    /**
     * Removes the enable button listener from the bean.
     *
     * @param listener
     */
    public void removeGlobalButtonListener(GlobalButtonListener listener)
    {
        globalButtonListener = null;
    }

    /**
     * Gets the enable button listener from the bean.
     *
     * @return listener
     */
    public EnableButtonListener getGlobalButtonListener()
    {
        return globalButtonListener;
    }

    /**
     * Adds (actually sets) the enable button listener on the bean.
     *
     * @param listener
     */
    public void addLocalButtonListener(LocalButtonListener listener)
    {
        localButtonListener = listener;
    }

    /**
     * Removes the enable button listener from the bean.
     *
     * @param listener
     */
    public void removeLocalButtonListener(LocalButtonListener listener)
    {
        localButtonListener = null;
    }

    /**
     * Gets the enable button listener from the bean.
     *
     * @return listener
     */
    public EnableButtonListener getLocalButtonListener()
    {
        return localButtonListener;
    }

    /**
     * Adds (actually sets) a BeanChangeListener.
     *
     * @param listener BeanChangeListener
     */
    public void addBeanChangeListener(BeanChangeListener listener)
    {
        beanChangeListener = listener;
    }

    /**
     * Removes (actually unsets) a BeanChangeListener.
     *
     * @param listener BeanChangeListener
     */
    public void removeBeanChangeListener(BeanChangeListener listener)
    {
        if (listener != null && listener == beanChangeListener)
        {
            beanChangeListener = null;
        }
    }

    /**
     * Adds a list selection listener to the internal list.
     *
     * @param l the listener
     */
    public void addListSelectionListener(ListSelectionListener l)
    {
        list.addListSelectionListener(l);
    }

    /**
     * Removes a list selection listener to the internal list.
     *
     * @param l the listener
     */
    public void removeListSelectionListener(ListSelectionListener l)
    {
        list.removeListSelectionListener(l);
    }

    /**
     * Notify bean change listener that bean has changed... and we're ready for
     * data.
     */
    protected void notifyReadyForData()
    {
        if (beanChangeListener != null)
        {
            beanChangeListener.beanChanged(new BeanChangeEvent(this));
        }
    }

    /**
     * Override JPanel set Visible to request focus.
     *
     * @param aFlag indicates if the component should be visible or not.
     */
    @Override
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);

        if (aFlag)
        {
            if (list.getModel().getSize() > 0 && globalButtonListener != null)
            {
                if (!checkIfUnDeletableItemExists(list))
                {
                    globalButtonListener.enableButton(CLEAR_ACTION, true);
                }
                else
                {
                    globalButtonListener.enableButton(CLEAR_ACTION, false);
                }

                if ((beanModel.getPromptAndResponseModel() != null)
                        && (beanModel.getPromptAndResponseModel().getResponseText() != null) && (itemIdEntered))
                {
                    globalButtonListener.enableButton(NEXT_ACTION, true);
                }
            }
        }
    }


    /**
     * Set the properties to be used by this bean
     *
     * @param props the propeties object
     */
    public void setProps(Properties props)
    {
        super.setProps(props);

       // getTotalsBean().setProps(props); commented for pos ui quickwin
        if (renderer != null && renderer instanceof AbstractListRenderer)
        {
            ((AbstractListRenderer)renderer).setProps(props);
        }
    }

    /**
     * Returns <tt>true</tt> to veto the enabling of UI events if the
     * underlying scanner reports items in its scan queue; <tt>false</tt> if
     * there is no attached scanner, or if the scanner is attached but its scan
     * queue is empty.
     *
     * @return <tt>true</tt> to veto the enabling of UI events if the
     *         underlying scanner reports items in its scan queue;
     *         <tt>false</tt> otherwise
     */
    public boolean vetoEnableOfUIEvents()
    {
        if (beanChangeListener != null)
        {
            // we expect beanChangeListener to be an instance of EnableUIEventsVetoer.
            // (Specifically, it should be a ScannerSessionAdapter)
            // If it's not, then we are in trouble because we are attempting to
            // delegate the decision of whether to veto the enabling of UI events
            // all the way down to the scanner
            if (beanChangeListener instanceof EnableUIEventsVetoer)
            {
                // delegate the decision of whether to veto the enabling of UI
                // events to the object listening to this bean (should be the
                // ScannerSessionAdapter)
                return ((EnableUIEventsVetoer) beanChangeListener).vetoEnableOfUIEvents();
            }

            final String msg = "We expect beanChangeListener to also be an instanceof EnableUIEventsVetoer. "
                    + "If it is not, then we cannot query the attached scanner for the number of items in the scan queue. "
                    + "See Foundation SCR 270 for the details of why that's a bad thing. In short, we must lock the "
                    + "UI down while the scanner is allowed to drain its scan queue, otherwise the user may preempt the "
                    + "scanner and navigate to a screen where scans no longer make sense and we'll ignore the remaining "
                    + "scans in the scan queue.";

            // for good measure, we log this important message
            logger.error(msg);

            // and then conjure up a failed Assertion to aid people in detecting
            // this problem.
            assert beanChangeListener instanceof EnableUIEventsVetoer : msg;
        }
        return false;
    }
    
    // -------------------------------------------------------------------------
    /**
     * Inner class to handle mouse events that allow abilities on {@link URLLabel} in
     * the list's renderer.
     *
     * @author cgreene
     * @since 14.0
     * @see SaleLineItemRenderer
     */
    protected class ItemDescriptionMouseListener extends MouseAdapter implements MouseMotionListener, ChangeListener
    {
        /**
         * Refresh the List of items when scrolling
         */
        @Override
        public void stateChanged(ChangeEvent e)
        {
            repaint();
        }
        
        /* (non-Javadoc)
         * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
         */
        public void mouseClicked(MouseEvent e)
        {
            Component c = getComponentInRendererAt(e);
            if (c instanceof URLLabel && (((URLLabel)c).isMouseOnText(e)))
            {

                LineItemsModel model = (LineItemsModel)beanModel;
                Point p = e.getPoint();
                int idx = list.locationToIndex(p);
                model.setItemModifiedIndex(idx);
                ((URLLabel)c).mouseClicked(e);
            }
        }

        /*
         * (non-Javadoc)
         * @see
         * java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent
         * )
         */
        @Override
        public void mouseMoved(MouseEvent e)
        {
            Component c = getComponentInRendererAt(e);
            if (c instanceof URLLabel)
            {
                URLLabel label = (URLLabel)c;
                if (label.isMouseOnText(e))
                {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
            }
            else
            {
                setCursor(Cursor.getDefaultCursor());
            }
        }

        /*
         * (non-Javadoc)
         * @see
         * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent
         * )
         */
        @Override
        public void mouseDragged(MouseEvent e)
        {
            // do nothing
        }
        
        /**
         * @param e
         * @return
         */
        private Component getComponentInRendererAt(MouseEvent e)
        {
            Point p = e.getPoint();
            int idx = list.locationToIndex(p);
            if (idx > -1)
            {
                @SuppressWarnings("rawtypes")
                ListCellRenderer r = list.getCellRenderer();
                Object value = list.getModel().getElementAt(idx);
                boolean isSelected = list.getSelectionModel().isSelectedIndex(idx);
                boolean cellHasFocus = (idx == list.getHighlightRow());
                @SuppressWarnings("unchecked")
                Component c = r.getListCellRendererComponent(list, value, idx, isSelected, cellHasFocus);
                if (c instanceof SaleLineItemRenderer)
                {
                    SaleLineItemRenderer renderer = (SaleLineItemRenderer)c;
                    Rectangle b = list.getCellBounds(idx, idx);
                    renderer.setBounds(b);
                    p.translate(-b.x, -b.y);
                    return renderer.getComponentAt(p);
                }
            }
            return null;
        }
    }

    /**
     * Starts the part when it is run as an application
     *
     * @param args command line parameters
     */
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        SaleBean bean = new SaleBean();
        bean.setLabelText("Description/Item #,Qty,Price,Discount,Ext Price,Tax");
        bean.setLabelWeights("40,13,13,13,13,8");
        bean.setRenderer("oracle.retail.stores.pos.ui.beans.SaleLineItemRenderer");

        LineItemsModel model = new LineItemsModel();
        AbstractTransactionLineItemIfc[] items = new AbstractTransactionLineItemIfc[3];
        AbstractListRenderer r = (AbstractListRenderer)bean.getRenderer();
        items[0] = (AbstractTransactionLineItemIfc)r.createPrototype();
        items[1] = (AbstractTransactionLineItemIfc)r.createPrototype();
        items[2] = (AbstractTransactionLineItemIfc)r.createPrototype();
        model.setLineItems(items);
        bean.setModel(model);
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }

}
