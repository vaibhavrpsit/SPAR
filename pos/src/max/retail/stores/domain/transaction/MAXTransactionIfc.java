/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/max/retail/stores/domain/transaction/MAXTransactionIfc.java /main/32 2014/06/17 15:26:38 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * Rev 1.0	Aug 26,2016		Nitesh Kumar	changes for code merging 
 * ===========================================================================
 */
package max.retail.stores.domain.transaction;

import java.util.List;

import oracle.retail.stores.domain.transaction.TransactionIfc;

public interface MAXTransactionIfc extends TransactionIfc { // begin class
															// TransactionIfc
	/**
	 * revision number
	 **/
	public static final String revisionNumber = "$Revision: /rgbustores_12.0.9in_branch/1 $";

	/**
	 * @param rounding
	 *            The rounding to set.
	 */
	public void setRounding(String rounding);

	/**
	 * @param roundingDenominations
	 *            The roundingDenominations to set.
	 */
	public void setRoundingDenominations(List roundingDenominations);

	/**
	 * @return rounding The boolean for rounding enabled/disabled
	 */
	public String getRounding();

	/**
	 * @return Returns the roundingDenominations
	 */
	public List getRoundingDenominations();
	
	public String getSubmitinvresponse();
	
	public void setSubmitinvresponse(String submitinvresponse);

}
