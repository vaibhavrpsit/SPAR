/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/max/retail/stores/domain/financial/MAXShippingMethodIfc.java /main/32 2014/06/17 15:26:38 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * Rev 1.0	Aug 26,2016		Nitesh Kumar	changes for code merging 
 * ===========================================================================
 */
package max.retail.stores.domain.financial;

import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSTime;

public interface MAXShippingMethodIfc extends ShippingMethodIfc {

	public EYSDate getExpectedDeliveryDate();

	public void setExpectedDeliveryDate(EYSDate expectedDeliveryDate);

	public EYSTime getExpectedDeliveryTime();

	public void setExpectedDeliveryTime(EYSTime expectedDeliveryTime);

}
