/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *
 *	Rev 1.0		Feb 16, 2017		Nadia Arora		fix : In ADV search, search the item with item desc 
 	and if we click on item detail application comming to the main screen
 *
 ********************************************************************************/

package max.retail.stores.pos.ui.beans;

import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.beans.ItemListBean;
import oracle.retail.stores.pos.ui.behavior.EnableButtonListener;
import oracle.retail.stores.pos.ui.behavior.LocalButtonListener;

public class MAXItemListBean extends ItemListBean {

	private static final long serialVersionUID = -8547298514356935644L;
	/** The local enable button listener * */
	protected EnableButtonListener localButtonListener = null;

	@Override
	protected void updateBean() {
		// ListBeanModel model = (ListBeanModel)beanModel;

		POSListModel posListModel = new POSListModel(beanModel.getItemList());
		getList().setModel(posListModel);
		PLUItemIfc item = beanModel.getSelectedItem();
		if (item != null) {
			list.setSelectedValue(item, true);
		}
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
	//Change for Rev 1.0:ends
}