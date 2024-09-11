/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2016-2017 MAXHyperMarkets, Inc.    All Rights Reserved.
  Rev 1.0	Nadia Arora		20 Feb,2017		MMRP Changes
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import max.retail.stores.domain.lineitem.MAXMaximumRetailPriceChangeIfc;
import oracle.retail.stores.foundation.manager.ui.UIModelIfc;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.beans.ListBean;

public class MAXMaximumRetailPriceListBean  extends ListBean {
	
	private static final long serialVersionUID = 1L;
    public static final String revisionNumber = "$Revision: 1.1 $";

    /** class name */
    public static final String CLASSNAME = "MaximumRetailPriceListBean";

    //---------------------------------------------------------------------
    /**
     *    Default constructor.
    **/
    //---------------------------------------------------------------------
    public MAXMaximumRetailPriceListBean()
    {
        super();
    }



    //------------------------------------------------------------------------
    /**
     *  Sets the information to be shown by this bean.
     *  @param model the model to be shown.  The runtime type should be
     *  MaximumRetailPriceListBeanModel
     */
    //------------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if(model == null)
        {
            throw new NullPointerException("Attempt to set MaximumRetailPriceListBean " +
                    "model to null");
        }
        if (model instanceof MAXMaximumRetailPriceListBeanModel)
        {
            beanModel = (MAXMaximumRetailPriceListBeanModel)model;
            updateBean();
            }
        }


    //------------------------------------------------------------------------
    /**
     Updates the model for the current settings of this bean. <p>
     **/
    //------------------------------------------------------------------------
    public void updateModel()
    {
        int index = list.getSelectedRow();
        if(beanModel instanceof MAXMaximumRetailPriceListBeanModel)
        {
        	MAXMaximumRetailPriceListBeanModel model = (MAXMaximumRetailPriceListBeanModel)beanModel;
        			model.setSelectedItem(((MAXMaximumRetailPriceChangeIfc)list.getModel().getElementAt(index)));
        }
    }


    //---------------------------------------------------------------------
    /**
        Updates the model if It's been changed
    **/
    //---------------------------------------------------------------------
    protected void updateBean()
    {
      if(beanModel instanceof MAXMaximumRetailPriceListBeanModel)
      {
        MAXMaximumRetailPriceListBeanModel model = (MAXMaximumRetailPriceListBeanModel)beanModel;
        POSListModel posListModel = new POSListModel(model.getItemMaximumRetailPriceList());
        getList().setModel(posListModel);

        MAXMaximumRetailPriceChangeIfc itemMaximumRetailPrice = model.getSelectedItem();
        if (itemMaximumRetailPrice != null)
        {
           list.setSelectedValue(itemMaximumRetailPrice, true);
        }
        }
    }

    //--------------------------------------------------------------------------
    /**
     *  Sets the visibility of this bean.
     *  @param aFlag true if visible, false otherwise
     */
    //--------------------------------------------------------------------------
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);

        // if visible is true, set the selection and request focus
        if (aFlag)
        {
            if(list.getSelectedIndex() == -1 &&  list.getModel().getSize() > 0 &&
               selectionMode != NO_SELECTION_MODE)
            {
                list.setSelectedIndex(0);
            }

            list.setFocusable(true);
            list.setFocusTraversalKeysEnabled(false);
            setCurrentFocus(list);
         }
    }
}