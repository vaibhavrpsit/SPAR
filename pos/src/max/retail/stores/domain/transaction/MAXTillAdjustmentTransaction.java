/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/TillAdjustmentTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:47 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    cgreen 03/17/09 - formatting
 *    glwang 02/26/09 - get count type from TillAdjustmentTransaction instead
 *                      of FinancialCount
 *    ohorne 11/06/08 - TillPayment Tender Type now persisted as Code instead
 *                      of DisplayName. Deprecated
 *                      TillAdjustmentTransaction.tenderType, which has been
 *                      replaced with a TenderDescriptorIfc attribute.
 *    mdecam 11/07/08 - I18N - updated toString()
 *    mdecam 11/07/08 - I18N - Fixed Clone Method
 *    ohorne 10/29/08 - Localization of Till related Reason Codes
 *
 * ===========================================================================

     $Log:
      6    360Commerce 1.5         4/12/2008 5:44:57 PM   Christian Greene
           Upgrade StringBuffer to StringBuilder
      5    360Commerce 1.4         6/26/2007 11:13:58 AM  Ashok.Mondal    I18N
           changes to export and import POSLog.
      4    360Commerce 1.3         4/25/2007 10:00:18 AM  Anda D. Cadar   I18N
           merge
      3    360Commerce 1.2         3/31/2005 4:30:29 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:26:10 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:15:03 PM  Robert Pearse
     $
     Revision 1.7  2004/09/23 00:30:51  kmcbride
     @scr 7211: Inserting serialVersionUIDs in these Serializable classes

     Revision 1.6  2004/07/22 04:56:32  khassen
     @scr 6296/6297/6298 - Updating pay in, pay out, payroll pay out:
     Adding database fields, print and reprint receipt functionality to reflect
     persistence of additional data in transaction.

     Revision 1.5  2004/07/08 23:34:31  jdeleau
     @scr 6086 payroll till pay out on post void was crashing the system.  In fact it
     was not implemented at all.  Now its implemented just as normal till pay out.

     Revision 1.4  2004/02/16 18:12:58  dcobb
     @scr 3381 Feature Enhancement:  Till Pickup and Loan
     Add to/from register to TillAdjustmentTransactionTest.

     Revision 1.3  2004/02/12 17:14:42  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:28:51  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:34  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.5   Jan 28 2004 16:36:34   DCobb
 * Added From/To register ID's for operation without a safe.
 * Resolution for 3381: Feature Enhancement:  Till Pickup and Loan
 *
 *    Rev 1.4   Dec 01 2003 13:46:18   bwf
 * Updated for echeck declines.
 *
 *    Rev 1.3   25 Nov 2003 22:51:00   baa
 * implement new methods on interface
 *
 *    Rev 1.2   25 Nov 2003 22:36:34   baa
 * address build break
 *
 *    Rev 1.1   Sep 15 2003 14:02:14   bwf
 * Put amount in toString method.
 * Resolution for 3334: Feature Enhancement:  Queue Exception Handling
 *
 *    Rev 1.0   Aug 29 2003 15:41:08   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Feb 15 2003 14:52:30   mpm
 * Merged 5.1 changes.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.0   Jun 03 2002 17:06:24   msg
 * Initial revision.
 * ===========================================================================
 */
package max.retail.stores.domain.transaction;

import oracle.retail.stores.domain.transaction.TillAdjustmentTransaction;
import oracle.retail.stores.domain.utility.CodeListIfc;

public class MAXTillAdjustmentTransaction extends TillAdjustmentTransaction implements MAXTillAdjustmentTransactionIfc {
	/**
	 * reason codes of till Pay In/Pay Out/Payroll Pay Out
	 */
	protected CodeListIfc reasonCodes = null;

	/**
	 * approval codes of till Pay Out/Payroll Pay Out
	 */
	protected CodeListIfc approvalCodes = null;

	/**
	 * @param reasonCodeList
	 *            is the list of till Pay In/Pay Out/Payroll Pay Out reason
	 *            codes
	 */
	public void setTillAdjustmentReasonCodes(CodeListIfc reasonCodeList) {
		this.reasonCodes = reasonCodeList;
	}

	/**
	 * @return Return the Pay In/Pay Out/Payroll Pay Out reason codes
	 */
	public CodeListIfc getTillAdjustmentReasonCodes() {
		return reasonCodes;
	}

	/**
	 * @param approvalCodeList
	 *            is the list of till Pay Out/Payroll Pay Out approval codes
	 */
	public void setTillAdjustmentApprovalCodes(CodeListIfc approvalCodeList) {
		this.approvalCodes = approvalCodeList;
	}

	/**
	 * @return Return the Pay Out/Payroll Pay Out Approval Codes
	 */
	public CodeListIfc getTillAdjustmentApprovalCodes() {
		return approvalCodes;
	}
}