
package max.retail.stores.pos.ui.beans;

import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//----------------------------------------------------------------------------
/**
 * This is the model used to pass customer information
 * 
 * @version $Revision: 4$
 **/
// ----------------------------------------------------------------------------
public class MAXCouponBeanModel extends POSBaseBeanModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 759404759459378785L;
	protected String couponList[];
	protected int selectedIndex = 0;

	public String[] getCouponList() {
		return couponList;
	}

	public void setCouponList(String value[]) {
		couponList = value;
	}

	public void setSelectedCouponIndex(int index) {
		selectedIndex = index;
	}

	public int getSelectedCouponIndex() {
		return selectedIndex;
	}
	
	public String getSelectedCoupon()
	{
		return couponList[selectedIndex];
	}
}
