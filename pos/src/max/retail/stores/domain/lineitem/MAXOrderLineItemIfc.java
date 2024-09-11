/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/max/retail/stores/domain/arts/MAXOrderLineItemIfc.java /main/32 2014/06/17 15:26:38 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * Rev 1.0	Aug 26,2016		Nitesh Kumar	changes for code merging 
 * ===========================================================================
 */
package max.retail.stores.domain.lineitem;

import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;

public abstract interface MAXOrderLineItemIfc extends OrderLineItemIfc {
	public static final String revisionNumber = "$Revision: /main/31 $";

	public static final int ORDER_ITEM_STATUS_CANCELED = 3;

}