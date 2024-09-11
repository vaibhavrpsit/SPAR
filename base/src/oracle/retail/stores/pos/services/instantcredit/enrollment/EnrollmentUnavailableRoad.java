/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/enrollment/EnrollmentUnavailableRoad.java /rgbustores_13.4x_generic_branch/4 2011/08/23 15:32:26 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       08/23/11 - check nullpointer for approval status
 *    sgu       08/16/11 - check null for approval status
 *    sgu       05/16/11 - move instant credit approval status to its own class
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    acadar    11/18/09 - added check for null instant credit
 *    kulu      01/27/09 - minor modification based on review
 *    kulu      01/26/09 - Guard against null
 *    kulu      01/26/09 - Minor correction
 *    kulu      01/26/09 - Fix the bug that House Account enroll response data
 *                         don't have padding translation at enroll franking
 *                         slip
 *
 * ===========================================================================
 * $Log:
 * 6    360Commerce 1.5         12/18/2007 5:47:48 PM  Alan N. Sinton  CR
 *      29661: Changes per code review.
 * 5    360Commerce 1.4         11/27/2007 12:32:24 PM Alan N. Sinton  CR
 *      29661: Encrypting, masking and hashing account numbers for House
 *      Account.
 * 4    360Commerce 1.3         6/9/2006 3:22:03 PM    Brett J. Larsen CR 18490
 *       - UDM - instant credit auth code changed to varchar from int
 * 3    360Commerce 1.2         3/31/2005 4:28:00 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:21:23 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:10:53 PM  Robert Pearse
 *
 *Revision 1.5  2004/04/05 23:03:00  jdeleau
 *@scr 4218 JavaDoc fixes associated with RegisterReports changes
 *
 *Revision 1.4  2004/04/02 23:07:51  jdeleau
 *@scr 4218 Register Reports - House Account and initial changes to
 *the way SummaryReports are built.
 *
 *Revision 1.3  2004/02/12 16:50:42  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:51:22  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Nov 24 2003 19:40:44   nrao
 * Code Review Changes.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit.enrollment;

// foundation imports
import oracle.retail.stores.domain.transaction.InstantCreditTransactionIfc;
import oracle.retail.stores.domain.utility.InstantCreditApprovalStatus;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.services.instantcredit.InstantCreditCargo;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

//--------------------------------------------------------------------------
/**
    This road is traveled when discount by amt is selected.
    @version $Revision: /rgbustores_13.4x_generic_branch/4 $
**/
//--------------------------------------------------------------------------
public class EnrollmentUnavailableRoad extends LaneActionAdapter
{
    /**
       revision number supplied by version control
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/4 $";

    //----------------------------------------------------------------------
    /**
     * Sets transaction values
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        InstantCreditCargo instantCreditCargo = (InstantCreditCargo) bus.getCargo();
        UtilityManagerIfc utility = (UtilityManager)bus.getManager(UtilityManagerIfc.TYPE);

        if ( instantCreditCargo.getTransaction() instanceof InstantCreditTransactionIfc)
        {
            InstantCreditTransactionIfc instantCreditTransactionIfc = (InstantCreditTransactionIfc) instantCreditCargo.getTransaction();
            if(instantCreditTransactionIfc.getInstantCredit() != null)
            {
                if(!InstantCreditApprovalStatus.CALL_CENTER.equals(instantCreditTransactionIfc.getInstantCredit().getApprovalStatus()))
                {
                    instantCreditTransactionIfc.getInstantCredit().setApprovalStatus(InstantCreditApprovalStatus.ENROLL_BY_PHONE);
                }
            }

        }

        // journal enrollment
       // StringBuffer sb = new StringBuffer("Instant Credit Enroll");
        StringBuffer sb = new StringBuffer();
        Object[] dataArgs = new Object[2];
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.INSTANT_CREDIT_ENROLL_LABEL, null));
        if(instantCreditCargo.getInstantCredit() != null &&
                !instantCreditCargo.isTransactionSaved() &&
                instantCreditCargo.getInstantCredit().getEncipheredCardData() != null)
        {
        	dataArgs[0] = instantCreditCargo.getInstantCredit()
					.getEncipheredCardData().getTruncatedAcctNumber();
			sb.append(Util.EOL)
					.append(
							I18NHelper.getString(
									I18NConstantsIfc.EJOURNAL_TYPE,
									JournalConstantsIfc.ACCOUNT_NUMBER_LABEL,
									dataArgs)).append(Util.EOL);
        }
        InstantCreditApprovalStatus approvalStatus = instantCreditCargo.getApprovalStatus();
        if (approvalStatus != null)
        {
            dataArgs[0] = utility.retrieveCommonText(approvalStatus.getResourceKey(), approvalStatus.getResourceKey(), LocaleConstantsIfc.JOURNAL);
        }
		sb.append(Util.EOL).append(
				I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
						JournalConstantsIfc.RESPONSE_LABEL, dataArgs));

        JournalManagerIfc jm = (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);
        jm.journal(sb.toString());
    }
}
