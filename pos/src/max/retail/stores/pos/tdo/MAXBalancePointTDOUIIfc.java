/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *     Copyright (c) 2010 Lifestyle India Pvt Ltd.    All Rights Reserved.
 *		
 * Rev 1.0  Jan 19, 2011 6:35:06 PM Vaishali.Kumari
 * Initial revision.
 * Resolution for FES_LMG_India_Customer_Loyalty_v1.3
 * Created to display balance point 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.tdo;

import java.util.HashMap;

import max.retail.stores.domain.customer.MAXCustomerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public interface MAXBalancePointTDOUIIfc {
	/**
	 * Build a UI bean model. Use the HashMap to pass in attributes needed to
	 * construct the bean model
	 * 
	 * @return a fully populated bean model
	 */
	public POSBaseBeanModel buildBeanModel(HashMap attributeMap);

	/**
	 * Formats the pole display for line 1
	 * 
	 * @param customer
	 *            contains the info to be displayed
	 * @return The display String
	 */
	public String formatPoleDisplayLine1(MAXCustomerIfc customer);

	/**
	 * Formats the pole display for line 2
	 * 
	 * @param customer
	 *            contains the info to be displayed
	 * @return the display String
	 */
	public String formatPoleDisplayLine2(MAXCustomerIfc customer);
}
