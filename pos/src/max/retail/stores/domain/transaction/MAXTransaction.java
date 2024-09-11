/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/max/retail/stores/domain/transaction/MAXTransaction.java /main/32 2014/06/17 15:26:38 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * Rev 1.0	Aug 26,2016		Nitesh Kumar	changes for code merging 
 * ===========================================================================
 */
package max.retail.stores.domain.transaction;

import java.util.List;

import oracle.retail.stores.domain.transaction.Transaction;

public class MAXTransaction extends Transaction { // Begin class Transaction()
	// This id is used to tell
	// the compiler not to generate a
	// new serialVersionUID.
	//
	static final long serialVersionUID = 7871388092505703643L;

	/**
	 * revision number
	 **/
	public static String revisionNumber = "$Revision: /rgbustores_12.0.9in_branch/1 $";

	/**
	 * String containing rounding parameters
	 */

	protected String rounding;

	/**
	 * List containing the rounding denominations.The list is expected to be
	 * sorted when set.
	 */
	protected List roundingDenominations;
	
	/*
	 * protected String submitinvresponse;
	 * 
	 * 
	 * 
	 * public String getSubmitinvresponse() { return submitinvresponse; }
	 * 
	 * public void setSubmitinvresponse(String submitinvresponse) {
	 * this.submitinvresponse = submitinvresponse; }
	 */

	/**
	 * @param rounding
	 *            The rounding to set.
	 */
	public void setRounding(String rounding) {
		this.rounding = rounding;
	}

	/**
	 * @return rounding The boolean for rounding enabled/disabled
	 */
	public String getRounding() {
		return rounding;
	}

	/**
	 * @return Returns the roundingDenominations
	 */
	public List getRoundingDenominations() {
		return roundingDenominations;
	}

	/**
	 * @param roundingDenominations
	 *            The roundingDenominations to set.
	 */
	public void setRoundingDenominations(List roundingDenominations) {
		this.roundingDenominations = roundingDenominations;
	}
} // end class Transaction
