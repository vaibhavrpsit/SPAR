/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ItemListBean.java /main/25 2012/11/29 14:38:20 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   11/27/12 - Refactored loadImage into superclass
 *    cgreene   08/16/11 - check for null when loading image
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mchellap  04/27/09 - Fixed item inquiry wildcard search
 *    cgreene   04/17/09 - fix item image painting in lists
 *    cgreene   04/10/09 - fix possible npe when checking for blob by adding
 *                         hasImageBlob to ItemImageIfc
 *    cgreene   03/30/09 - removed item name column from item image table
 *    cgreene   03/19/09 - refactoring changes
 *    tzgarba   02/25/09 - Removed test class dependencies from shipping
 *                         source.
 *    ddbaker   12/31/08 - Work to repair SelectItem screen.
 *    atirkey   12/02/08 - Item Image CR
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:32 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:28 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:38 PM  Robert Pearse
 *
 *   Revision 1.6  2004/03/26 21:18:19  cdb
 *   @scr 4204 Removing Tabs.
 *
 *   Revision 1.5  2004/03/19 15:07:28  aschenk
 *   @scr 4084 - Item number is now carried over to Inventory option screen if one was selected on previous screen.
 *
 *   Revision 1.4  2004/03/18 22:47:41  aschenk
 *   @scr 4079 and 4080 - Items were cleared after a help or cancelled cancel for an item inquiry.
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Sep 16 2003 17:52:38   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:10:56   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Aug 14 2002 18:17:50   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:53:24   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:35:04   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:55:40   msg
 * Initial revision.
 *
 *    Rev 1.7   Mar 05 2002 19:34:32   mpm
 * Text externalization for inquiry UI artifacts.
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.POSListModel;

/**
 * The itemList bean presents the functionality of the itemList screen.
 *
 * @version $Revision: /main/25 $
 */
public class ItemListBean extends ListBean
{
    private static final long serialVersionUID = 8375202782682948385L;

    /** revision number */
    public static final String revisionNumber = "$Revision: /main/25 $";

    /** class name */
    public static final String CLASSNAME = "ItemListBean";

    protected ItemListBeanModel beanModel = new ItemListBeanModel();

    /**
     * Default constructor.
     */
    public ItemListBean()
    {
        super();
    }

    /**
     * Sets the information to be shown by this bean.
     *
     * @param model the model to be shown. The runtime type should be
     *            ItemInfoBeanModel
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set ItemListBean model to null");
        }
        if (model instanceof ItemListBeanModel)
        {
            beanModel = (ItemListBeanModel)model;
            updateBean();
        }
    }

    /**
     * Updates the model for the current settings of this bean.
     */
    @Override
    public void updateModel()
    {
        int index = list.getSelectedRow();
        beanModel.setSelectedItem((PLUItemIfc)list.getModel().getElementAt(index));
    }

    /**
     * Updates the model if It's been changed
     */
    @Override
    protected void updateBean()
    {
        // mark all the images as loading if they have not been loaded yet
        PLUItemIfc[] items = beanModel.getItemList();
        for (int i = 0; i < items.length; i++)
        {
            loadImage(items[i].getItemImage(), i);
        }

        POSListModel posListModel = new POSListModel(items);
        getList().setModel(posListModel);

        Object item = beanModel.getSelectedItem();
        if (item != null)
        {
            list.setSelectedValue(item, true);
        }
    }

    /**
     * Sets the visibility of this bean.
     *
     * @param aFlag true if visible, false otherwise
     */
    @Override
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);

        // if visible is true, set the selection and request focus
        if (aFlag)
        {
            if (list.getSelectedIndex() == -1 && list.getModel().getSize() > 0 && selectionMode != NO_SELECTION_MODE)
            {
                list.setSelectedIndex(0);
            }

            list.setFocusable(true);
            list.setFocusTraversalKeysEnabled(false);
            setCurrentFocus(list);
        }
    }
}
