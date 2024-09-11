/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/max/retail/stores/domain/financial/MAXShippingMethod.java /main/32 2014/06/17 15:26:38 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * Rev 1.0	Aug 26,2016		Nitesh Kumar	changes for code merging 
 * ===========================================================================
 */
package max.retail.stores.domain.financial;

import oracle.retail.stores.domain.shipping.ShippingMethod;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSTime;

@SuppressWarnings("deprecation")
public class MAXShippingMethod extends ShippingMethod implements MAXShippingMethodIfc {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6271672225066645426L;

	protected EYSDate expectedDeliveryDate = null;

	protected EYSTime expectedDeliveryTime = null;

	public EYSDate getExpectedDeliveryDate() {
		return expectedDeliveryDate;
	}

	public void setExpectedDeliveryDate(EYSDate expectedDeliveryDate) {
		this.expectedDeliveryDate = expectedDeliveryDate;
	}

	public EYSTime getExpectedDeliveryTime() {
		return expectedDeliveryTime;
	}

	public void setExpectedDeliveryTime(EYSTime expectedDeliveryTime) {
		this.expectedDeliveryTime = expectedDeliveryTime;
	}
}
