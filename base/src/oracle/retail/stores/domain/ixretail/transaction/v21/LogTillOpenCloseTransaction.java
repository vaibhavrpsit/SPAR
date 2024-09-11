/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/transaction/v21/LogTillOpenCloseTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:09 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:57 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:17 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:27 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/08/10 07:17:09  mwright
 *   Merge (3) with top of tree
 *
 *   Revision 1.3.2.3  2004/08/06 02:35:09  mwright
 *   Set till status in element
 *   Set till ID from till object, because transaction may not have the till ID set
 *
 *   Revision 1.3.2.2  2004/08/01 23:45:48  mwright
 *   Removed TO-DO tags on completed tasks
 *
 *   Revision 1.3.2.1  2004/07/29 01:44:04  mwright
 *   Added suspend and resume transactions
 *
 *   Revision 1.3  2004/06/24 09:15:09  mwright
 *   POSLog v2.1 (second) merge with top of tree
 *
 *   Revision 1.2.2.2  2004/06/23 00:41:18  mwright
 *   Ensure that non-null open time is sent in XML - till.getOpentime() seems to return null, so we send the transaction start time instead. This is not the right value for a close transaction.
 *
 *   Revision 1.2.2.1  2004/06/10 10:55:23  mwright
 *   Updated to use schema types in commerce services
 *
 *   Revision 1.2  2004/05/06 03:21:07  mwright
 *   Initial revision for POSLog v2.1 merge with top of tree
 *
 *   Revision 1.1.2.1  2004/04/26 22:20:47  mwright
 *   Initial revision for v2.1
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.transaction.v21;

import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TillOpenCloseTransactionIfc;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;

import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.transaction.LogTillOpenCloseTransactionIfc;
import oracle.retail.stores.domain.ixretail.financial.LogFinancialCountIfc;
import oracle.retail.stores.domain.ixretail.financial.LogFinancialTotalsIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.TillSOD360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.TillMovement360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogTCSettleIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.ReconcilableCountElement360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.FinancialCountElement360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.TaxTotalElement360Ifc;
//--------------------------------------------------------------------------
/**
    This class creates the TLog in IXRetail format for a till open transaction.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogTillOpenCloseTransaction
extends LogControlTransaction
implements LogTillOpenCloseTransactionIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    

    protected void createBaseElements()
    throws XMLConversionException
    {
        // get till-open transaction
        TillOpenCloseTransactionIfc tocTransaction = (TillOpenCloseTransactionIfc) transaction;
        TillIfc till = tocTransaction.getTill();

        super.createBaseElements();

        int transactionType = transaction.getTransactionType();
        

        // TODO This appears to be null, may lead to comparison failure in imported table:
        EYSDate openTime = till.getOpenTime();
        if (openTime == null)
        {
            openTime = transaction.getTimestampBegin();
        }
        

        EYSDate statusDate = till.getLastStatusChangeTime();
        if (statusDate == null)
        {
            statusDate = new EYSDate(1980, 1, 1, 0, 0, 0);      // can't set it in domain, seems to be generated at object write, stored in table
        }
        
        RegisterIfc register       = tocTransaction.getRegister();
        EmployeeIfc signOnOperator = till.getSignOnOperator();
        
        switch (transactionType)
        {
            case TransactionIfc.TYPE_OPEN_TILL:
                TillSOD360Ifc tillSod = getSchemaTypesFactory().getTillSOD360Instance();
                tillSod.setStartDateTimeStamp(dateValue(openTime));
                tillSod.setStatus(AbstractFinancialEntityIfc.STATUS_DESCRIPTORS[till.getStatus()]);
                tillSod.setTillType(AbstractStatusEntityIfc.TILL_TYPE_DESCRIPTORS[till.getTillType()]);
                
                if (register != null) 
                {
                    tillSod.setAccountability(AbstractStatusEntityIfc.ACCOUNTABILITY_DESCRIPTORS[register.getAccountability()]);
                    tillSod.setRegisterStatus(Integer.toString(register.getStatus()));
                    tillSod.setLastUniqueID(register.getCurrentUniqueID());
                }
            
                tillSod.setSignOnOperator(signOnOperator.getEmployeeID());
                ReconcilableCountElement360Ifc reconElement = getSchemaTypesFactory().getReconcilableCountElement360Instance();
                ReconcilableCountIfc count = till.getTotals().getStartingFloatCount();
                LogFinancialCountIfc countLogger = IXRetailGateway.getFactory().getLogFinancialCountInstance();
                boolean something = false;
                if (count.getEntered() != null)
                {
                    FinancialCountElement360Ifc enteredElement = getSchemaTypesFactory().getFinancialCountElement360Instance();
                    countLogger.createElement(count.getEntered(), null, enteredElement, null);
                    reconElement.setEntered(enteredElement);
                    something = true;
                }
                if (count.getExpected() != null)
                {
                    FinancialCountElement360Ifc expectedElement = getSchemaTypesFactory().getFinancialCountElement360Instance();
                    countLogger.createElement(count.getExpected(), null, expectedElement, null);
                    reconElement.setExpected(expectedElement);
                    something = true;
                }
                if (something)
                {
                    tillSod.setStartingFloatCount(reconElement);
                }
                tillSod.setTillID(till.getTillID());
                controlTransactionElement.setTillSOD360(tillSod);       // this makes the control transaction a 360-specific till open transaction
                break;
            
            case TransactionIfc.TYPE_CLOSE_TILL:
                TillSOD360Ifc tillEod = getSchemaTypesFactory().getTillSOD360Instance();
                tillEod.setStartDateTimeStamp(dateValue(openTime));
                tillEod.setStatus(AbstractFinancialEntityIfc.STATUS_DESCRIPTORS[till.getStatus()]);
                tillEod.setTillType(AbstractStatusEntityIfc.TILL_TYPE_DESCRIPTORS[till.getTillType()]);
                
                if (register != null) 
                {
                    tillEod.setAccountability(AbstractStatusEntityIfc.ACCOUNTABILITY_DESCRIPTORS[register.getAccountability()]);
                    tillEod.setRegisterStatus(Integer.toString(register.getStatus()));
                    tillEod.setLastUniqueID(register.getCurrentUniqueID());
                }
                
                EYSDate closeTime = till.getCloseTime();
                if (closeTime == null)  // i.e. open trx, seems we always get null here even for a close TODO 
                {
                    closeTime = transaction.getTimestampBegin();    // just because it is mandatory in the schema for till close
                }
                tillEod.setEndDateTimeStamp(dateValue(closeTime));
                tillEod.setSignOffOperator(till.getSignOffOperator().getEmployeeID());
                
                POSLogTCSettleIfc totalsElement = getSchemaTypesFactory().getPOSLogTCSettleInstance();
                LogFinancialTotalsIfc totalsLogger = IXRetailGateway.getFactory().getLogFinancialTotalsInstance();
                totalsLogger.createElement(till.getTotals(), null, totalsElement);
                tillEod.setSessionSettle(totalsElement);
                addTillID(tillEod, till.getTillID());
                tillEod.setTillID(till.getTillID());
                controlTransactionElement.setTillEOD360(tillEod);       // this makes the control transaction a 360-specific till close transaction
                break;
            
            case TransactionIfc.TYPE_SUSPEND_TILL:
                TillMovement360Ifc tillSusp = getSchemaTypesFactory().getTillMovement360Instance();
                tillSusp.setStartDateTimeStamp(dateValue(openTime));
                tillSusp.setStatusDateTimeStamp(dateValue(statusDate));
                tillSusp.setStatus(AbstractFinancialEntityIfc.STATUS_DESCRIPTORS[till.getStatus()]);
                tillSusp.setTillType(AbstractStatusEntityIfc.TILL_TYPE_DESCRIPTORS[till.getTillType()]);
                if (register != null)   // TODO seems it is null, need to check jdbc?
                {
                    tillSusp.setAccountability(AbstractStatusEntityIfc.ACCOUNTABILITY_DESCRIPTORS[register.getAccountability()]);
                    tillSusp.setRegisterStatus(Integer.toString(register.getStatus()));
                    tillSusp.setLastUniqueID(register.getCurrentUniqueID());
                }
                tillSusp.setSignOnOperator(signOnOperator.getEmployeeID());
                tillSusp.setTillID(till.getTillID());
                controlTransactionElement.setTillSuspend360(tillSusp);
                break;
            
            case TransactionIfc.TYPE_RESUME_TILL:
                TillMovement360Ifc tillRes = getSchemaTypesFactory().getTillMovement360Instance();
                tillRes.setStartDateTimeStamp(dateValue(openTime));
                tillRes.setStatusDateTimeStamp(dateValue(statusDate));
                tillRes.setStatus(AbstractFinancialEntityIfc.STATUS_DESCRIPTORS[till.getStatus()]);
                tillRes.setTillType(AbstractStatusEntityIfc.TILL_TYPE_DESCRIPTORS[till.getTillType()]);
                if (register != null) 
                {
                    tillRes.setAccountability(AbstractStatusEntityIfc.ACCOUNTABILITY_DESCRIPTORS[register.getAccountability()]);
                    tillRes.setRegisterStatus(Integer.toString(register.getStatus()));
                    tillRes.setLastUniqueID(register.getCurrentUniqueID());
                }
                tillRes.setSignOnOperator(signOnOperator.getEmployeeID());
                tillRes.setTillID(till.getTillID());
                controlTransactionElement.setTillResume360(tillRes);
                break;
        }
    
    }

    /**
     * The tax totals element need the till ID, but that is not in the tax totals object in the financial totals
     */
    protected void addTillID(TillSOD360Ifc tillEod, String tillID)
    {
        POSLogTCSettleIfc settle = tillEod.getSessionSettle();
        if (settle != null)
        {
            TaxTotalElement360Ifc[] totals = settle.getTaxTotals();
            if (totals != null)
            {
                for (int i = 0; i < totals.length; i++)
                {
                    totals[i].setTillID(tillID);
                }
            }
        }
    }

}
