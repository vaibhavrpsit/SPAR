/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillloan/CheckCountLoanSite.java /main/11 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:24 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:05 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:53 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/30 18:16:03  dcobb
 *   @scr 4098 Open drawer before detail count screens.
 *   Loan changed to open drawer before detail count screens.
 *
 *   Revision 1.3  2004/02/12 16:49:59  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:54  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Feb 10 2004 13:08:50   DCobb
 * Open the cash drawer after a successful count.
 * Resolution for 3381: Feature Enhancement:  Till Pickup and Loan
 * 
 *    Rev 1.0   Aug 29 2003 15:57:46   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:27:58   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:29:04   msg
 * Initial revision.
 * 
 *    Rev 1.1   29 Oct 2001 16:15:00   epd
 * Updated files to remove reference to Till related parameters.  This information, formerly contained in parameters, now resides as register settings obtained from the RegisterIfc class.  
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * 
 *    Rev 1.0   Sep 21 2001 11:18:24   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:14:32   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillloan;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

/**
 * Checks the Loan Cash Count parameter and mails the letter "CountTypeNone",
 * "CountTypeSummary" or "CountTypeDetail" according to the Cash Loan Count
 * type.
 * 
 * @version $Revision: /main/11 $
 */
public class CheckCountLoanSite extends PosSiteActionAdapter implements SiteActionIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 5572771969549547361L;

    /** The revision number for this class */
    public static final String revisionNumber = "$Revision: /main/11 $";

    /**
     * Set the current till in the cargo. Create the till loan transaction and
     * set in the cargo. Check the loan cash count system setting and mail the
     * letter "CountTypeNone", "CountTypeSummary" or "CountTypeDetail"
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TillLoanCargo cargo = (TillLoanCargo) bus.getCargo();
        RegisterIfc register = cargo.getRegister();

        // Set current till (used by poscount service)
        TillIfc t = DomainGateway.getFactory().getTillInstance();
        t.setTillID(register.getCurrentTillID());
        cargo.setTillID(register.getCurrentTillID());
        t.addCashier(cargo.getOperator());
        cargo.setTill(t);
        
        // Create the Till Loan Transaction
        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        TillAdjustmentTransactionIfc transaction =
          DomainGateway.getFactory().getTillAdjustmentTransactionInstance();
        transaction.setTransactionType(TransactionIfc.TYPE_LOAN_TILL);
        utility.initializeTransaction(transaction, -1);
        cargo.setTransaction(transaction);

        // Determine the letter according to the loan count setting       
        String letterName = null;
        int tillCountCashLoan = register.getTillCountCashLoan();

        switch (tillCountCashLoan)
        {
            case FinancialCountIfc.COUNT_TYPE_NONE:
            {
                cargo.setLoanCountType(FinancialCountIfc.COUNT_TYPE_NONE);
                letterName = TillLetterIfc.COUNT_TYPE_NONE;
                break;
            }
            case FinancialCountIfc.COUNT_TYPE_DETAIL:
            {
                cargo.setLoanCountType(FinancialCountIfc.COUNT_TYPE_DETAIL);
                letterName = TillLetterIfc.COUNT_TYPE_DETAIL;
                break;
            }
            case FinancialCountIfc.COUNT_TYPE_SUMMARY:
            default:
            {
                cargo.setLoanCountType(FinancialCountIfc.COUNT_TYPE_SUMMARY);
                letterName = TillLetterIfc.COUNT_TYPE_SUMMARY;
            }
        }      

        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }
}