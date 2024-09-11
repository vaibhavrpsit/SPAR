/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/max/retail/stores/domain/transaction/MAXTillAdjustmentTransactionIfc.java /main/32 2014/06/17 15:26:38 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * Rev 1.0	Aug 26,2016		Nitesh Kumar	changes for code merging 
 * ===========================================================================
 */
package max.retail.stores.domain.transaction;

import oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;

public abstract interface MAXTillAdjustmentTransactionIfc extends TillAdjustmentTransactionIfc {
	public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

	/**
	 * Sets all reasoncodes
	 * <p>
	 * 
	 * @param reasonCodeList
	 *            is the list of till Pay In/Pay Out/Payroll Pay Out reason
	 *            codes
	 */
	public void setTillAdjustmentReasonCodes(CodeListIfc reasonCodeList);

	// -----------------------------------------------------------------------
	/**
	 *
	 * @return Return the Pay In/Pay Out/Payroll Pay Out reason codes
	 */
	public CodeListIfc getTillAdjustmentReasonCodes();

	// -----------------------------------------------------------------------
	/**
	 * Sets all approvalcodes
	 * <p>
	 * 
	 * @param approvalCodeList
	 *            is the list of till Pay Out/Payroll Pay Out approval codes
	 */
	public void setTillAdjustmentApprovalCodes(CodeListIfc approvalCodeList);

	// -----------------------------------------------------------------------
	/**
	 *
	 * @return Return the Pay Out/Payroll Pay Out Approval Codes
	 */
	public CodeListIfc getTillAdjustmentApprovalCodes();
	// ----------------------------------------------------------------------------

}