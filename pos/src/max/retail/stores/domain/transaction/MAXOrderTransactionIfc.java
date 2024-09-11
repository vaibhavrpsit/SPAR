/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.1	13/Aug/2013	  	Prateek, Changes done for Special Order CR -Food Total & suggested Tender type
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.transaction;

import java.math.BigDecimal;

import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSTime;

public interface MAXOrderTransactionIfc extends OrderTransactionIfc {

	public EYSDate getExpectedDeliveryDate();

	public void setExpectedDeliveryDate(EYSDate expectedDeliveryDate);

	public EYSTime getExpectedDeliveryTime();

	public void setExpectedDeliveryTime(EYSTime expectedDeliveryTime);

	/** MAX Rev 1.1 Change : Start **/
	public BigDecimal getFoodTotals();

	public void setFoodTotals(BigDecimal foodTotals);

	public String getSuggestedTender();

	public void setSuggestedTender(String suggestedTender);
	/** MAX Rev 1.1 Change : End **/
}
