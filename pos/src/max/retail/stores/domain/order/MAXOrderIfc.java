/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	12/se/2016	  	Nitesh 			Code Merging
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.order;

import java.util.Vector;

import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSTime;

public interface MAXOrderIfc extends OrderIfc {

	public void setExpectedDeliveryDate(EYSDate expectedDeliveryDate);

	public EYSTime getExpectedDeliveryTime();

	public void setExpectedDeliveryTime(EYSTime expectedDeliveryTime);

	public boolean isHasShippingCharge();

	// fix for bug 6615
	public void setOriginalTransaction(TransactionIfc transaction);

	public TransactionIfc getOriginalTransaction();

	// end
	public void setHasShippingCharge(boolean hasShippingCharge);

	public boolean isAlterOrder();

	public void setAlterOrder(boolean alterOrder);

	public Vector getDeletedItems();

	public void setDeletedItems(Vector deletedItems);

	/** MAX Rev 1.1 Change : Start **/
	public String getSuggestedTender();

	public void setSuggestedTender(String suggestedTender);

	/** MAX Rev 1.1 Change : End **/

	public boolean getTrainingMode();

	// ----------------------------------------------------------------------------
	/**
	 * Retrieves training mode flag.
	 * <P>
	 * 
	 * @return training mode flag
	 **/
	// ----------------------------------------------------------------------------
	public String getTrainingModeFlag(); // end setTrainingModeFlag()
	// ----------------------------------------------------------------------------

	/**
	 * Sets training mode flag.
	 * <P>
	 * 
	 * @param value
	 *            training mode flag
	 * @deprecated use setTrainingModeFlag instead
	 **/
	// ----------------------------------------------------------------------------
	public void setTrainingMode(boolean value); // end setTrainingMode()
	// ----------------------------------------------------------------------------

	/**
	 * Sets training mode flag.
	 * <P>
	 * 
	 * @param value
	 *            training mode flag
	 **/
	// ----------------------------------------------------------------------------
	public void setTrainingModeFlag(String value);
}
