/* ===========================================================================
* Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/enrollment/CanceledEnrollmentRoad.java /main/13 2013/11/05 16:53:21 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   11/04/13 - fixed deprecated import of class Util
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         1/25/2006 4:10:51 PM   Brett J. Larsen merge
 *      7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 * 3    360Commerce 1.2         3/31/2005 4:27:19 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:19:57 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:09:46 PM  Robert Pearse
 *:
 * 4    .v700     1.2.1.0     11/4/2005 11:44:44     Jason L. DeLeau 4202: Fix
 *      extensibility issues for instant credit service
 * 3    360Commerce1.2         3/31/2005 15:27:19     Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:19:57     Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:09:46     Robert Pearse
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
 *    Rev 1.3   Nov 24 2003 19:35:38   nrao
 * Code Review Changes.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit.enrollment;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.transaction.InstantCreditTransaction;
import oracle.retail.stores.domain.transaction.InstantCreditTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.instantcredit.InstantCreditCargo;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * @version $Revision: /main/13 $
 */
@SuppressWarnings("serial")
public class CanceledEnrollmentRoad extends PosLaneActionAdapter
{
    /** revision number supplied by version control **/
    public static final String revisionNumber = "$Revision: /main/13 $";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        InstantCreditCargo cargo = (InstantCreditCargo) bus.getCargo();
        JournalManagerIfc journal =
            (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);
        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);

        TransactionIfc transaction = cargo.getTransaction();
        if (transaction != null)
        {
            // save canceled transaction
            if(!cargo.isTransactionSaved() &&
               (cargo.getTransaction().getTransactionType() == TransactionIfc.TYPE_INSTANT_CREDIT_ENROLLMENT))
            {
                boolean error = false;
                try
                {
                    // if instantcredit transaction
                    InstantCreditTransactionIfc icTrans = null;
                    if (transaction instanceof InstantCreditTransaction)
                    {
                           icTrans = (InstantCreditTransactionIfc) transaction;
                           // if sales associate id captured, then set transaction with the value
                        if (icTrans.getInstantCredit() != null)
                        {
                            transaction.setSalesAssociate(icTrans.getInstantCredit()
                                                            .getInstantCreditSalesAssociate());
                        }
                        // if sales associate id not in transaction, then obtain from cargo
                        else if (cargo.getEmployeeID() != null)
                        {
                            EmployeeIfc emp = DomainGateway.getFactory().getEmployeeInstance();
                            emp.setEmployeeID(cargo.getEmployeeID());
                            transaction.setSalesAssociate(emp);
                        }
                    }

                    transaction.setTransactionStatus(TransactionIfc.STATUS_CANCELED);
                    transaction.setTimestampEnd();
                    RegisterIfc register = cargo.getRegister();
                    register.addNumberCancelledTransactions(1);
                    register.setCurrentUniqueID(register.getPreviousUniqueID());
                    TillIfc till = register.getCurrentTill();
                    utility.saveTransaction(transaction, till, register);
                }
                catch (Exception e)
                {
                    error = true;
                    logger.error(
                                 "" + Util.getSimpleClassName(getClass()) + ": Caught a exception while saving transaction.  Exception Class: " + e.getClass().getName() + "  Exception Message: " + ((e.getMessage() != null) ? e.getMessage() : "No Exception Message Available") + "");
                    if (logger.isDebugEnabled()) logger.debug(
                                 Util.throwableToString(e));
                }

                // write hard totals
                error = writeHardTotals(bus, error);

                if (!error)
                {
                    cargo.setTransactionSaved(true);
                }
            }

            // journal the transaction
            journalTransaction(bus, cargo, journal);
        }
    }

    /**
     * Write Hard Totals
     * 
     * @param bus BusIfc
     * @param utility UtilityManager
     * @param error boolean
     * @return boolean
     */
    protected boolean writeHardTotals(BusIfc bus, boolean error)
    {
        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        try
        {
            utility.writeHardTotals();
        }
        catch (Exception e)
        {
            error = true;
            logger.error(
                         "" + Util.getSimpleClassName(getClass()) + ": Caught a exception while saving transaction hard totals.  Exception Class: " + e.getClass().getName() + "  Exception Message: " + ((e.getMessage() != null) ? e.getMessage() : "No Exception Message Available") + "");
            if (logger.isDebugEnabled()) logger.debug(
                         Util.throwableToString(e));
        }
        return error;
    }

    /**
     * Journal the transaction
     * 
     * @param bus BusIfc
     * @param cargo InstantCreditCargo
     * @param journal JournalManagerIfc
     */
    protected void journalTransaction(BusIfc bus, InstantCreditCargo cargo, JournalManagerIfc journal)
    {
        if (journal != null)
        {
        	StringBuffer jr = new StringBuffer();
        	jr.append(Util.EOL + I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANSACTION_CANCELED_LABEL, null));
        	//StringBuffer jr = new StringBuffer("\n     ** Transaction Canceled **     ");

            String transactionID = cargo.getTransaction().getTransactionID();
            // Adding transaction tax total for cancel journal
            journal.journal(cargo.getOperator().getLoginID(),
                            transactionID, jr.toString());
            if (logger.isInfoEnabled()) logger.info(
                        "Transaction " + transactionID + " Canceled");
        }
        else
        {
            logger.error(
                         "" + Util.getSimpleClassName(getClass()) + ": No JournalManager available.");
        }
    }
}
