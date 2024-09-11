/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/LineItemsModel.java /main/18 2014/06/22 09:20:29 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     06/16/14 - Modified to support display of extended item
 *                         recommended items on the Sale Item Screen.
 *    icole     05/21/14 - Deprecated beanHasBeenUpdated and get and set as
 *                         there is no reference to getBeanHasBeenUpdated on to
 *                         setBeanHasBeenUpdated so of no value.
 *    icole     05/21/14 - Changes to prevent NPE due to line item list being
 *                         null.
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         4/4/2008 3:12:27 AM    Sujay Beesnalli
 *         Forward porting CR# 30354 from v12x. Added flags to determine
 *         highlighting of rows.
 *    5    360Commerce 1.4         3/26/2008 4:40:48 AM   VIVEKANAND KINI This
 *         was changed for some for CR30354 for GAP. This CR was deferred and
 *         not included in forward porting. CR 30238 reused this change, and
 *         has been included with this fix. It has been verified, it does not
 *         have any effect on other parts POS code. 
 *    4    360Commerce 1.3         2/25/2008 12:56:25 AM  Manikandan Chellapan
 *         CR#30505 Service Alert Screens are not timing out
 *    3    360Commerce 1.2         3/31/2005 4:28:51 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:06 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:19 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/03/15 21:55:15  jdeleau
 *   @scr 4040 Automatic logoff after timeout
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:11:06   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:50:50   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:56:00   msg
 * Initial revision.
 * 
 *    Rev 1.3   Jan 19 2002 10:30:54   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.2   30 Oct 2001 12:38:52   pjf
 * Added get/setHighlightedRow() for single item selection from the KitComponentsBean.
 * Resolution for POS SCR-8: Item Kits
 *
 *    Rev 1.1   27 Oct 2001 10:24:52   mpm
 * Merged Pier 1, Virginia ABC changes.
 *
 *    Rev 1.0   Sep 21 2001 11:36:08   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:17:10   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.List;

import oracle.retail.stores.common.item.ExtendedItemData;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.pos.ui.timer.TimerModelIfc;

/**
 * This is the bean model used by the SaleBean.
 * 
 * @version $Revision: /main/18 $
 * @see oracle.retail.stores.pos.ui.beans.SaleBean
 */
public class LineItemsModel extends ListBeanModel
{
    private static final long serialVersionUID = 7100900646815105653L;

    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/18 $";

    /** Transaction totals model */
    protected TotalsBeanModel totalsBeanModel = null;

    /**
     * Indicates that the current item should be at the top of the list.
     */
    protected boolean moveHighlightToTop = false;

    /**
     * Index of the modified item. The value -1 indicates that the itemToDisplay
     * has not been modified; i.e., it is a new item.
     */
    protected int itemModifiedIndex = -1;

    /**
     * Indicates that this model has already updated the screen and should not
     * be used to update it again. this can happen in the cancel processing of
     * the Sale Bean.
     * @deprecated as of 14.1. No callers to getBeanHasBeenUpdated.
     */
    protected boolean beanHasBeenUpdated = false;

    /** The array of row indices to delete. */
    protected int[] rowsToDelete = null;

    /**
     * TimerModel used for automatic logoff after a timeout
     */
    protected TimerModelIfc timerModel = null;

    protected boolean highlightItem = true;

    protected boolean displayTransDetailScreen = false;

    /** List of {@link ExtendedItemData} recommended items. */
    protected List<ExtendedItemData> recommendedItems = null;

    /** The item ID of a recommended that user has selected to view */
    protected String selectedRecommendedItemID = null;
    
    /**
     * LineItemsModel constructor comment.
     */
    public LineItemsModel()
    {
        super();
    }

    /**
     * Returns the line items in an array. This is the same a {@link #getListArray()}
     * but cast into {@link AbstractTransactionLineItemIfc}.
     * 
     * @return a non-null array of {@link AbstractTransactionLineItemIfc}.
     */
    public AbstractTransactionLineItemIfc[] getLineItems()
    {
        Object[] listArray = getListArray();
        AbstractTransactionLineItemIfc[] lineItems = new AbstractTransactionLineItemIfc[listArray.length];
        System.arraycopy(listArray, 0, lineItems, 0, listArray.length);
        return lineItems;
    }

    /**
     * Sets the saleLineItems property value.
     * 
     * @param items The new value for the property.
     */
    public void setLineItems(AbstractTransactionLineItemIfc[] items)
    {
        setListModel(items);
    }

    /**
     * Gets the TotalsBeanModel property value.
     * 
     * @return The saleLineItems property value.
     */
    public TotalsBeanModel getTotalsBeanModel()
    {
        return totalsBeanModel;
    }

    /**
     * Sets the TotalsBeanModel property value.
     * 
     * @param model The new value for the property.
     */
    public void setTotalsBeanModel(TotalsBeanModel model)
    {
        totalsBeanModel = model;
    }

    /**
     * Gets the moveHighlightToTop boolean.
     * 
     * @return boolean.
     */
    public boolean getMoveHighlightToTop()
    {
        return (moveHighlightToTop);
    }

    /**
     * Sets the moveCurrentToTop boolean.
     * 
     * @param value boolean.
     */
    public void setMoveHighlightToTop(boolean value)
    {
        moveHighlightToTop = value;
    }

    /**
     * Gets the beanHasBeenUpdated boolean.
     * 
     * @return boolean.
     * @deprecated as of 14.1. No callers to getBeanHasBeenUpdated.
     */
    public boolean getBeanHasBeenUpdated()
    {
        return (beanHasBeenUpdated);
    }

    /**
     * Sets the beanHasBeenUpdated boolean.
     * 
     * @param value boolean.
     * @deprecated as of 14.1. No callers to getBeanHasBeenUpdated.
     */
    public void setBeanHasBeenUpdated(boolean value)
    {
        beanHasBeenUpdated = value;
    }

    /**
     * Sets highlighted row.
     * 
     * @param row - the row currently highlighted on the bean.
     */
    public void setHighlightedRow(int row)
    {
        setSelectedRow(row);
    }

    /**
     * Returns highlighted row.
     * 
     * @return int highlighted row - the row currently highlighted on the bean.
     */
    public int getHighlightedRow()
    {
        return getSelectedRow();
    }

    /**
     * Retrieves rows to delete.
     * 
     * @return an array of indexes associated with selected line items.
     */
    public int[] getRowsToDelete()
    {
        return rowsToDelete;
    }

    /**
     * Sets rows to delete.
     * 
     * @param rows an array of indexes associated with selected line items.
     */
    public void setRowsToDelete(int[] rows)
    {
        rowsToDelete = rows;
    }

    /**
     * Returns the itemModifiedIndex.
     * 
     * @return The itemModifiedIndex
     */
    public int getItemModifiedIndex()
    {
        return itemModifiedIndex;
    }

    /**
     * Sets the itemModifiedIndex.
     * 
     * @param index The itemModifiedIndex
     */
    public void setItemModifiedIndex(int index)
    {
        itemModifiedIndex = index;
    }

    public boolean isHighlightItem()
    {
        return highlightItem;
    }

    public void setHighlightItem(boolean highlightItem)
    {
        this.highlightItem = highlightItem;
    }

    public boolean isDisplayTransDetailScreen()
    {
        return displayTransDetailScreen;
    }

    public void setDisplayTransDetailScreen(boolean displayTransDetailScreen)
    {
        this.displayTransDetailScreen = displayTransDetailScreen;
    }

    /**
     * Gets the <code>recommendedItems</code> value.
     * @return the recommendedItems
     */
    public List<ExtendedItemData> getRecommendedItems()
    {
        return recommendedItems;
    }

    /**
     * Sets the <code>recommendedItems</code> value.
     * @param recommendedItems the recommendedItems to set
     */
    public void setRecommendedItems(List<ExtendedItemData> recommendedItems)
    {
        this.recommendedItems = recommendedItems;
    }

    /**
     * Gets the <code>selectedRecommendedItemID</code> value.
     * @return the selectedRecommendedItemID
     */
    public String getSelectedRecommendedItemID()
    {
        return selectedRecommendedItemID;
    }

    /**
     * Sets the <code>selectedRecommendedItemID</code> value.
     * @param selectedRecommendedItemID the selectedRecommendedItemID to set
     */
    public void setSelectedRecommendedItemID(String selectedRecommendedItemID)
    {
        this.selectedRecommendedItemID = selectedRecommendedItemID;
    }

}