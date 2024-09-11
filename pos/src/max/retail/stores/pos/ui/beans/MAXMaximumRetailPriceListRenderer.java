/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2016-2017 MAXHyperMarkets, Inc.    All Rights Reserved.
  Rev 1.0	Nadia Arora		20 Feb,2017		MMRP Changes
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import javax.swing.JLabel;

import max.retail.stores.domain.lineitem.MAXMaximumRetailPriceChangeIfc;
import oracle.retail.stores.pos.ui.beans.AbstractListRenderer;

public class MAXMaximumRetailPriceListRenderer  extends AbstractListRenderer {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -3719922440578331246L;

	public static final int MAXIMUM_RETAIL_PRICE = 0;

	// Change for MAX: Rev 1.0
	public static final int RETAIL_SELLING_PRICE = 1;

	public static String YES = "Y";

	public static String NO = "N";

	// Change for MAX: Rev 1.0
	public static final int MAX_FIELDS = 2;

	public static final int[] ITEM_WEIGHTS = { 50, 50 };

	/**
	 * Added the new field to display RSP input
	 */
	public MAXMaximumRetailPriceListRenderer() {
		super();
		setName("MAXMaximumRetailPriceListRenderer");
		firstLineWeights = ITEM_WEIGHTS;
		fieldCount = MAX_FIELDS;
		lineBreak = MAX_FIELDS;
		initialize();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 *  */
	@Override
	public Object createPrototype() {

		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * * .Object)
	 */
	@Override
	public void setData(Object data) {
		MAXMaximumRetailPriceChangeIfc item = (MAXMaximumRetailPriceChangeIfc) data;
		labels[MAXIMUM_RETAIL_PRICE].setText(item.getMaximumRetailPrice().toGroupFormattedString(getLocale()));

		// Change for MAX: Rev 1.0
		if (item instanceof MAXMaximumRetailPriceChangeIfc && ((MAXMaximumRetailPriceChangeIfc) item).getRetailSellingPrice() != null) {
			labels[RETAIL_SELLING_PRICE].setText(((MAXMaximumRetailPriceChangeIfc) item).getRetailSellingPrice().toGroupFormattedString(getLocale()));
		}
		// Change for MAX: Rev 1.0 Ends
	}

	/*
	 * (non-Javadoc)
	 *
	 **/
	@Override
	protected void setPropertyFields() {
		// TODO Auto-generated method stub

	}

	// ---------------------------------------------------------------------

	// Initializes the RSP and MRP components.
	@Override
	protected void initOptions() {

		labels[MAXIMUM_RETAIL_PRICE].setHorizontalAlignment(JLabel.LEFT);
		// Change for MAX: Rev 1.0
		labels[RETAIL_SELLING_PRICE].setHorizontalAlignment(JLabel.RIGHT);
	}
}