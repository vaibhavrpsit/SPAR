/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/max/retail/stores/domain/lineitem/MAXItemTaxIfc.java /main/32 2014/06/17 15:26:38 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * Rev 1.0	Aug 26,2016		Nitesh Kumar	changes for code merging 
 * ===========================================================================
 */
package max.retail.stores.domain.lineitem;

import oracle.retail.stores.domain.lineitem.ItemTaxIfc;

public interface MAXItemTaxIfc extends ItemTaxIfc { // Begin interface
													// ItemTaxIfc
	/**
	 * revision number supplied by source-code control system
	 **/
	public static String revisionNumber = "$Revision: /rgbustores_12.0.9in_branch/1 $";

	public MAXLineItemTaxBreakUpDetailIfc[] getLineItemTaxBreakUpDetail();

	public void setLineItemTaxBreakUpDetail(MAXLineItemTaxBreakUpDetailIfc[] itemTaxBreakUpDetail);

}
