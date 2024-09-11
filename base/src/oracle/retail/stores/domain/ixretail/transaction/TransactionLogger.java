/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/transaction/TransactionLogger.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:10 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  07/08/10 - Add billpay transaction type
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         5/20/2008 5:12:41 PM   Leona R. Slepetis Add
 *          POSLog support for postvoided till functions (loan, payroll,
 *         pickup, payin, payout). Reviewed by R. Ojha.
 *    4    360Commerce 1.3         4/27/2006 7:29:46 PM   Brett J. Larsen CR
 *         17307 - remove inventory functionality - stage 2
 *    3    360Commerce 1.2         3/31/2005 4:30:35 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:23 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:15 PM  Robert Pearse
 *
 *   Revision 1.8.2.1  2004/12/08 00:06:31  mwright
 *   Added logger for transaction re-entry
 *
 *   Revision 1.8  2004/09/23 00:30:54  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.7  2004/08/10 07:17:10  mwright
 *   Merge (3) with top of tree
 *
 *   Revision 1.6.6.2  2004/08/09 12:38:50  mwright
 *   Case for instant credit enrollment transaction
 *
 *   Revision 1.6.6.1  2004/07/29 01:20:45  mwright
 *   Added cases for payroll payout, redeem transaction and enter/exit training mode
 *
 *   Revision 1.6  2004/04/09 16:55:48  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:43  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:53  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:48  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:26:32  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:36:52   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jan 22 2003 10:00:12   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.0   Sep 05 2002 11:13:02   msg
 * Initial revision.
 *
 *    Rev 1.6   May 26 2002 17:34:50   mpm
 * Added support for inventory-return transactions.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.5   May 26 2002 13:54:36   mpm
 * Added support for receiving transactions.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.4   May 26 2002 12:19:06   mpm
 * Added support for inventory transfer transactions.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.3   May 26 2002 09:10:42   mpm
 * Added inventory counts to tlog.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.2   May 23 2002 14:09:16   mpm
 * Added support for bank-deposit transaction.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.1   May 07 2002 18:05:30   mpm
 * Completed till suspend, resume.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   May 06 2002 19:41:10   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.transaction;
// XML imports
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

//--------------------------------------------------------------------------
/**
    This class handles the logging for all kinds of transactions. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class TransactionLogger
implements TransactionLoggerIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -1583251441997470952L;

    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.ixretail.transaction.TransactionLogger.class);
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------------
    /**
        Constructs TransactionLogger object. <P>
    **/
    //----------------------------------------------------------------------------
    public TransactionLogger()
    {                                   // begin TransactionLogger()
    }                                   // end TransactionLogger()

    //---------------------------------------------------------------------
    /**
       Creates a transaction element. <P>
       @param transaction transaction object
       @param doc parent document241
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createTransactionElement(TransactionIfc transaction,
                                            Document doc)
    throws XMLConversionException
    {                                   // begin createTransactionElement()
        int type = transaction.getTransactionType();
        LogTransactionIfc logTransaction = null;
        if (type == TransactionIfc.TYPE_VOID)
        {
            int origType = ((VoidTransactionIfc)transaction).getOriginalTransaction().getTransactionType();
            if (origType == TransactionIfc.TYPE_LOAN_TILL || origType == TransactionIfc.TYPE_PICKUP_TILL ||
                origType == TransactionIfc.TYPE_PAYIN_TILL || origType == TransactionIfc.TYPE_PAYOUT_TILL ||
                origType == TransactionIfc.TYPE_PAYROLL_PAYOUT_TILL)
            {
                type = TransactionIfc.TYPE_TILL_VOID;
            }
        }

        logTransaction = getTransactionLogger(type);
        Element transactionElement =
          logTransaction.createTransactionElement(transaction,
                                                  doc);

        return(transactionElement);

    }                                   // end createTransactionElement()

    //---------------------------------------------------------------------
    /**
       Selects transaction logger to log transaction. <P>
       @param transactionType transaction type
       @return transaction logger
    **/
    //---------------------------------------------------------------------
    protected LogTransactionIfc getTransactionLogger(int transactionType)
    {                                   // begin getTransactionLogger()
        LogTransactionIfc logTransaction = null;
        // process transaction based on type
        switch(transactionType)
        {
            case TransactionIfc.TYPE_SALE:
            case TransactionIfc.TYPE_RETURN:
            case TransactionIfc.TYPE_LAYAWAY_INITIATE:
            case TransactionIfc.TYPE_LAYAWAY_COMPLETE:
            case TransactionIfc.TYPE_LAYAWAY_DELETE:
            case TransactionIfc.TYPE_ORDER_CANCEL:
            case TransactionIfc.TYPE_ORDER_COMPLETE:
            case TransactionIfc.TYPE_ORDER_INITIATE:
            case TransactionIfc.TYPE_ORDER_PARTIAL:
            case TransactionIfc.TYPE_BILL_PAY:
                 logTransaction =
                   IXRetailGateway.getFactory().getLogRetailTransactionInstance();
                 break;
            case TransactionIfc.TYPE_HOUSE_PAYMENT:
            case TransactionIfc.TYPE_LAYAWAY_PAYMENT:
                 logTransaction =
                   IXRetailGateway.getFactory().getLogPaymentTransactionInstance();
                 break;
            case TransactionIfc.TYPE_VOID:
                 logTransaction =
                   IXRetailGateway.getFactory().getLogVoidTransactionInstance();
                 break;
            case TransactionIfc.TYPE_NO_SALE:
                 logTransaction =
                   IXRetailGateway.getFactory().getLogNoSaleTransactionInstance();
                 break;
            case TransactionIfc.TYPE_OPEN_STORE:
            case TransactionIfc.TYPE_CLOSE_STORE:
                 logTransaction =
                   IXRetailGateway.getFactory().getLogStoreOpenCloseTransactionInstance();
                 break;
            case TransactionIfc.TYPE_OPEN_REGISTER:
            case TransactionIfc.TYPE_CLOSE_REGISTER:
                 logTransaction =
                   IXRetailGateway.getFactory().getLogRegisterOpenCloseTransactionInstance();
                 break;
            case TransactionIfc.TYPE_OPEN_TILL:
            case TransactionIfc.TYPE_CLOSE_TILL:
            case TransactionIfc.TYPE_SUSPEND_TILL:
            case TransactionIfc.TYPE_RESUME_TILL:
                 logTransaction =
                   IXRetailGateway.getFactory().getLogTillOpenCloseTransactionInstance();
                 break;
            case TransactionIfc.TYPE_PICKUP_TILL:
            case TransactionIfc.TYPE_LOAN_TILL:
            case TransactionIfc.TYPE_PAYIN_TILL:
            case TransactionIfc.TYPE_PAYOUT_TILL:
            case TransactionIfc.TYPE_PAYROLL_PAYOUT_TILL:
                 logTransaction =
                   IXRetailGateway.getFactory().getLogTillAdjustmentTransactionInstance();
                 break;
            case TransactionIfc.TYPE_BANK_DEPOSIT_STORE:
                logTransaction =
                  IXRetailGateway.getFactory().getLogBankDepositTransactionInstance();
                break;
            case TransactionIfc.TYPE_ENTER_TRAINING_MODE:
            case TransactionIfc.TYPE_EXIT_TRAINING_MODE:
                logTransaction = IXRetailGateway.getFactory().getLogTrainingModeTransactionInstance();
                break;
            case TransactionIfc.TYPE_REDEEM:
                logTransaction = IXRetailGateway.getFactory().getLogRedeemTransactionInstance();
                break;
            case TransactionIfc.TYPE_INSTANT_CREDIT_ENROLLMENT:
                logTransaction = IXRetailGateway.getFactory().getLogInstantCreditTransactionInstance();
                break;

            case TransactionIfc.TYPE_ENTER_TRANSACTION_REENTRY:
            case TransactionIfc.TYPE_EXIT_TRANSACTION_REENTRY:
                logTransaction = IXRetailGateway.getFactory().getLogReEntryTransactionInstance();  // v1.0 returns default...
                break;
            case TransactionIfc.TYPE_TILL_VOID:
                logTransaction =
                  IXRetailGateway.getFactory().getLogVoidControlTransactionInstance();
                break;

            default:
                logTransaction =
                  IXRetailGateway.getFactory().getLogTransactionInstance();
                break;
        }

        return(logTransaction);
    }                                   // end getTransactionLogger()


}
