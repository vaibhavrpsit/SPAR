/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/factory/IXRetailFactory.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:07 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  06/21/10 - BillPay Changes
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    jswan     08/13/09 - Added the till reconcile amount and count to the
 *                         till, workstation and store tender history tables
 *                         and to code that reads from and writes to them.
 *
 * ===========================================================================
 * $Log:
 *    7    360Commerce 1.6         5/20/2008 5:12:41 PM   Leona R. Slepetis Add
 *          POSLog support for postvoided till functions (loan, payroll,
 *         pickup, payin, payout). Reviewed by R. Ojha.
 *    6    360Commerce 1.5         4/12/2008 5:44:57 PM   Christian Greene
 *         Upgrade StringBuffer to StringBuilder
 *    5    360Commerce 1.4         4/27/2006 7:29:44 PM   Brett J. Larsen CR
 *         17307 - remove inventory functionality - stage 2
 *    4    360Commerce 1.3         12/13/2005 4:43:48 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:28:35 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:34 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:43 PM  Robert Pearse
 *
 *   Revision 1.8.2.1  2004/12/08 00:06:31  mwright
 *   Added logger for transaction re-entry
 *
 *   Revision 1.8  2004/08/10 07:17:10  mwright
 *   Merge (3) with top of tree
 *
 *   Revision 1.7.2.2  2004/08/09 12:44:00  mwright
 *   Added instant credit enrollment transaction logger
 *
 *   Revision 1.7.2.1  2004/07/29 00:52:38  mwright
 *   Added enter/exit training mode and redeem transaction loggers
 *
 *   Revision 1.7  2004/06/24 09:15:10  mwright
 *   POSLog v2.1 (second) merge with top of tree
 *
 *   Revision 1.6.2.1  2004/06/10 10:47:56  mwright
 *   Updated to use schema types in commerce services
 *
 *   Revision 1.6  2004/05/06 03:34:19  mwright
 *   POSLog v2.1 merge with top of tree
 *
 *   Revision 1.3.2.3  2004/04/13 07:24:54  mwright
 *   Removed tabs
 *
 *   Revision 1.3.2.2  2004/03/18 02:24:18  mwright
 *   Implemented use of schema type factory
 *
 *   Revision 1.3.2.1  2004/03/17 04:13:49  mwright
 *   Initial revision for POSLog v2.1
 *
 *   Revision 1.3  2004/02/12 17:13:41  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:29  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:36:12   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Jul 25 2003 10:00:18   jgs
 * Deprecate methods for class which have been moved to commerce services.
 * Resolution for 1653: Modify XML PosLog to support old Domain export processing.
 *
 *    Rev 1.1   Jan 22 2003 09:54:40   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.factory;
// foundation imports
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.SchemaTypesFactory;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.SchemaTypesFactoryIfc;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.customer.LogCustomer;
import oracle.retail.stores.domain.ixretail.customer.LogCustomerIfc;
import oracle.retail.stores.domain.ixretail.customer.LogIRSCustomerIfc;
import oracle.retail.stores.domain.ixretail.discount.LogDiscountLineItem;
import oracle.retail.stores.domain.ixretail.discount.LogDiscountLineItemIfc;
import oracle.retail.stores.domain.ixretail.financial.LogDrawer;
import oracle.retail.stores.domain.ixretail.financial.LogDrawerIfc;
import oracle.retail.stores.domain.ixretail.financial.LogFinancialCount;
import oracle.retail.stores.domain.ixretail.financial.LogFinancialCountIfc;
import oracle.retail.stores.domain.ixretail.financial.LogFinancialCountTenderItem;
import oracle.retail.stores.domain.ixretail.financial.LogFinancialCountTenderItemIfc;
import oracle.retail.stores.domain.ixretail.financial.LogFinancialTotals;
import oracle.retail.stores.domain.ixretail.financial.LogFinancialTotalsIfc;
import oracle.retail.stores.domain.ixretail.financial.LogLayaway;
import oracle.retail.stores.domain.ixretail.financial.LogLayawayIfc;
import oracle.retail.stores.domain.ixretail.financial.LogPaymentHistoryIfc;
import oracle.retail.stores.domain.ixretail.financial.LogPaymentLineItem;
import oracle.retail.stores.domain.ixretail.financial.LogPaymentLineItemIfc;
import oracle.retail.stores.domain.ixretail.financial.LogRegister;
import oracle.retail.stores.domain.ixretail.financial.LogRegisterIfc;
import oracle.retail.stores.domain.ixretail.financial.LogStoreSafe;
import oracle.retail.stores.domain.ixretail.financial.LogStoreSafeIfc;
import oracle.retail.stores.domain.ixretail.financial.LogTill;
import oracle.retail.stores.domain.ixretail.financial.LogTillIfc;
import oracle.retail.stores.domain.ixretail.financial.v21.LogBillPayment;
import oracle.retail.stores.domain.ixretail.financial.v21.LogBillPaymentIfc;
import oracle.retail.stores.domain.ixretail.lineitem.LogLineItem;
import oracle.retail.stores.domain.ixretail.lineitem.LogLineItemIfc;
import oracle.retail.stores.domain.ixretail.lineitem.LogOrderItem;
import oracle.retail.stores.domain.ixretail.lineitem.LogOrderItemIfc;
import oracle.retail.stores.domain.ixretail.lineitem.LogSaleReturnLineItem;
import oracle.retail.stores.domain.ixretail.lineitem.LogSaleReturnLineItemIfc;
import oracle.retail.stores.domain.ixretail.log.BatchTotal;
import oracle.retail.stores.domain.ixretail.log.BatchTotalIfc;
import oracle.retail.stores.domain.ixretail.log.Log;
import oracle.retail.stores.domain.ixretail.log.LogBatchTotal;
import oracle.retail.stores.domain.ixretail.log.LogBatchTotalIfc;
import oracle.retail.stores.domain.ixretail.log.LogIfc;
import oracle.retail.stores.domain.ixretail.log.POSLogWriter;
import oracle.retail.stores.domain.ixretail.log.POSLogWriterIfc;
import oracle.retail.stores.domain.ixretail.tender.LogTenderLineItem;
import oracle.retail.stores.domain.ixretail.tender.LogTenderLineItemIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogBankDepositTransaction;
import oracle.retail.stores.domain.ixretail.transaction.LogBankDepositTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogInstantCreditTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogNoSaleTransaction;
import oracle.retail.stores.domain.ixretail.transaction.LogNoSaleTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogPaymentTransaction;
import oracle.retail.stores.domain.ixretail.transaction.LogPaymentTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogRedeemTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogRegisterOpenCloseTransaction;
import oracle.retail.stores.domain.ixretail.transaction.LogRegisterOpenCloseTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogRetailTransaction;
import oracle.retail.stores.domain.ixretail.transaction.LogRetailTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogStoreOpenCloseTransaction;
import oracle.retail.stores.domain.ixretail.transaction.LogStoreOpenCloseTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogTillAdjustmentTransaction;
import oracle.retail.stores.domain.ixretail.transaction.LogTillAdjustmentTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogTillOpenCloseTransaction;
import oracle.retail.stores.domain.ixretail.transaction.LogTillOpenCloseTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogTrainingModeTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogTransaction;
import oracle.retail.stores.domain.ixretail.transaction.LogTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogVoidControlTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogVoidTransaction;
import oracle.retail.stores.domain.ixretail.transaction.LogVoidTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.TransactionLogger;
import oracle.retail.stores.domain.ixretail.transaction.TransactionLoggerIfc;
import oracle.retail.stores.domain.ixretail.transaction.v21.LogVoidControlTransaction;
import oracle.retail.stores.domain.ixretail.utility.LogAddress;
import oracle.retail.stores.domain.ixretail.utility.LogAddressIfc;
import oracle.retail.stores.domain.ixretail.utility.LogGiftCard;
import oracle.retail.stores.domain.ixretail.utility.LogGiftCardIfc;
import oracle.retail.stores.domain.ixretail.utility.LogPhone;
import oracle.retail.stores.domain.ixretail.utility.LogPhoneIfc;
import oracle.retail.stores.domain.ixretail.utility.LogQuantity;
import oracle.retail.stores.domain.ixretail.utility.LogQuantityIfc;
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
     This is the base IXRetail object factory.  It simply returns instances
     of classes.  It is employed through the {@link IXRetailGateway#getFactory()}
     method.  For instance, to invoke an instance of Log, the developer
     would code {@code IXRetailGateway.getFactory().getLogInstance()}. <P>
     @see oracle.retail.stores.domain.ixretail.IXRetailGateway
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class IXRetailFactory
implements IXRetailFactoryIfc
{                                       // begin class IXRetailFactory
    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------------
    /**
        Constructs IXRetailFactory object. <P>
    **/
    //----------------------------------------------------------------------------
    public IXRetailFactory()
    {
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogCustomerIfc class. <P>
        @return LogCustomerIfc instance
    **/
    //---------------------------------------------------------------------
    public LogCustomerIfc getLogCustomerInstance()
    {
        return(new LogCustomer());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogIRSCustomerIfc class. <P>
        @return LogIRSCustomerIfc instance
    **/
    //---------------------------------------------------------------------
    public LogIRSCustomerIfc getLogIRSCustomerInstance()
    {
        return null;   // not supported for v1.0
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogDiscountLineItemIfc class. <P>
        @return LogDiscountLineItemIfc instance
    **/
    //---------------------------------------------------------------------
    public LogDiscountLineItemIfc getLogDiscountLineItemInstance()
    {
        return(new LogDiscountLineItem());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogDrawerIfc class. <P>
        @return LogDrawerIfc instance
    **/
    //---------------------------------------------------------------------
    public LogDrawerIfc getLogDrawerInstance()
    {
        return(new LogDrawer());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogFinancialCountIfc class. <P>
        @return LogFinancialCountIfc instance
    **/
    //---------------------------------------------------------------------
    public LogFinancialCountIfc getLogFinancialCountInstance()
    {
        return(new LogFinancialCount());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogFinancialCountTenderItemIfc class. <P>
        @return LogFinancialCountTenderItemIfc instance
    **/
    //---------------------------------------------------------------------
    public LogFinancialCountTenderItemIfc getLogFinancialCountTenderItemInstance()
    {
        return(new LogFinancialCountTenderItem());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogFinancialTotalsIfc class. <P>
        @return LogFinancialTotalsIfc instance
    **/
    //---------------------------------------------------------------------
    public LogFinancialTotalsIfc getLogFinancialTotalsInstance()
    {
        return(new LogFinancialTotals());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogLayawayIfc class. <P>
        @return LogLayawayIfc instance
    **/
    //---------------------------------------------------------------------
    public LogLayawayIfc getLogLayawayInstance()
    {
        return(new LogLayaway());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of PaymentHistoryIfc class. <P>
        @return LogPaymentHistoryIfc instance
    **/
    //---------------------------------------------------------------------
    public LogPaymentHistoryIfc getLogPaymentHistoryInstance()
    {
        return null;   // not supported for v1.0
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogPaymentLineItemIfc class. <P>
        @return LogPaymentLineItemIfc instance
    **/
    //---------------------------------------------------------------------
    public LogPaymentLineItemIfc getLogPaymentLineItemInstance()
    {
        return(new LogPaymentLineItem());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogRegisterIfc class. <P>
        @return LogRegisterIfc instance
    **/
    //---------------------------------------------------------------------
    public LogRegisterIfc getLogRegisterInstance()
    {
        return(new LogRegister());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogStoreSafeIfc class. <P>
        @return LogStoreSafeIfc instance
    **/
    //---------------------------------------------------------------------
    public LogStoreSafeIfc getLogStoreSafeInstance()
    {
        return(new LogStoreSafe());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogTillIfc class. <P>
        @return LogTillIfc instance
    **/
    //---------------------------------------------------------------------
    public LogTillIfc getLogTillInstance()
    {
        return(new LogTill());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogLineItemIfc class. <P>
        @return LogLineItemIfc instance
    **/
    //---------------------------------------------------------------------
    public LogLineItemIfc getLogLineItemInstance()
    {
        return(new LogLineItem());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogOrderItemIfc class. <P>
        @return LogOrderItemIfc instance
    **/
    //---------------------------------------------------------------------
    public LogOrderItemIfc getLogOrderItemInstance()
    {
        return(new LogOrderItem());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogSaleReturnLineItemIfc class. <P>
        @return LogSaleReturnLineItemIfc instance
    **/
    //---------------------------------------------------------------------
    public LogSaleReturnLineItemIfc getLogSaleReturnLineItemInstance()
    {
        return(new LogSaleReturnLineItem());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of BatchTotalIfc class. <P>
        @return BatchTotalIfc instance
    **/
    //---------------------------------------------------------------------
    public BatchTotalIfc getBatchTotalInstance()
    {
        return(new BatchTotal());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogIfc class. <P>
        @return LogIfc instance
    **/
    //---------------------------------------------------------------------
    public LogIfc getLogInstance()
    {
        return(new Log());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogBatchTotalIfc class. <P>
        @return LogBatchTotalIfc instance
    **/
    //---------------------------------------------------------------------
    public LogBatchTotalIfc getLogBatchTotalInstance()
    {
        return(new LogBatchTotal());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogTenderLineItemIfc class. <P>
        @return LogTenderLineItemIfc instance
    **/
    //---------------------------------------------------------------------
    public LogTenderLineItemIfc getLogTenderLineItemInstance()
    {
        return(new LogTenderLineItem());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogBankDepositTransactionIfc class. <P>
        @return LogBankDepositTransactionIfc instance
    **/
    //---------------------------------------------------------------------
    public LogBankDepositTransactionIfc getLogBankDepositTransactionInstance()
    {
        return(new LogBankDepositTransaction());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogNoSaleTransactionIfc class. <P>
        @return LogNoSaleTransactionIfc instance
    **/
    //---------------------------------------------------------------------
    public LogNoSaleTransactionIfc getLogNoSaleTransactionInstance()
    {
        return(new LogNoSaleTransaction());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogPaymentTransactionIfc class. <P>
        @return LogPaymentTransactionIfc instance
    **/
    //---------------------------------------------------------------------
    public LogPaymentTransactionIfc getLogPaymentTransactionInstance()
    {
        return(new LogPaymentTransaction());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogRegisterOpenCloseTransactionIfc class. <P>
        @return LogRegisterOpenCloseTransactionIfc instance
    **/
    //---------------------------------------------------------------------
    public LogRegisterOpenCloseTransactionIfc getLogRegisterOpenCloseTransactionInstance()
    {
        return(new LogRegisterOpenCloseTransaction());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogRegisterOpenCloseTransactionIfc class. <P>
        @return LogRegisterOpenCloseTransactionIfc instance
    **/
    //---------------------------------------------------------------------
    public LogRegisterOpenCloseTransactionIfc getLogRegisterOpenTransactionInstance()
    {
        return null;        // unused in v1.0, should throw exception
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogRegisterOpenCloseTransactionIfc class. <P>
        @return LogRegisterOpenCloseTransactionIfc instance
    **/
    //---------------------------------------------------------------------
    public LogRegisterOpenCloseTransactionIfc getLogRegisterCloseTransactionInstance()
    {
        return null;        // unused in v1.0, should throw exception
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogRetailTransactionIfc class. <P>
        @return LogRetailTransactionIfc instance
    **/
    //---------------------------------------------------------------------
    public LogRetailTransactionIfc getLogRetailTransactionInstance()
    {
        return(new LogRetailTransaction());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogStoreOpenCloseTransactionIfc class. <P>
        @return LogStoreOpenCloseTransactionIfc instance
    **/
    //---------------------------------------------------------------------
    public LogStoreOpenCloseTransactionIfc getLogStoreOpenCloseTransactionInstance()
    {
        return(new LogStoreOpenCloseTransaction());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogStoreOpenCloseTransactionIfc class. <P>
        @return LogStoreOpenCloseTransactionIfc instance
    **/
    //---------------------------------------------------------------------
    public LogStoreOpenCloseTransactionIfc getLogStoreOpenTransactionInstance()
    {
        return null;        // unused in v1.0, should throw exception...
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogStoreOpenCloseTransactionIfc class. <P>
        @return LogStoreOpenCloseTransactionIfc instance
    **/
    //---------------------------------------------------------------------
    public LogStoreOpenCloseTransactionIfc getLogStoreCloseTransactionInstance()
    {
        return null;        // unused in v1.0, should throw exception...
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogTillAdjustmentTransactionIfc class. <P>
        @return LogTillAdjustmentTransactionIfc instance
    **/
    //---------------------------------------------------------------------
    public LogTillAdjustmentTransactionIfc getLogTillAdjustmentTransactionInstance()
    {
        return(new LogTillAdjustmentTransaction());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogTillOpenCloseTransactionIfc class. <P>
        @return LogTillOpenCloseTransactionIfc instance
    **/
    //---------------------------------------------------------------------
    public LogTillOpenCloseTransactionIfc getLogTillOpenCloseTransactionInstance()
    {
        return(new LogTillOpenCloseTransaction());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogTransactionIfc class. <P>
        @return LogTransactionIfc instance
    **/
    //---------------------------------------------------------------------
    public LogTransactionIfc getLogTransactionInstance()
    {
        return(new LogTransaction());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogVoidTransactionIfc class. <P>
        @return LogVoidTransactionIfc instance
    **/
    //---------------------------------------------------------------------
    public LogVoidTransactionIfc getLogVoidTransactionInstance()
    {
        return(new LogVoidTransaction());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogVoidControlTransactionIfc class. <P>
        @return LogVoidControlTransactionIfc instance
    **/
    //---------------------------------------------------------------------
    public LogVoidControlTransactionIfc getLogVoidControlTransactionInstance()
    {
        return new LogVoidControlTransaction();
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of TransactionLoggerIfc class. <P>
        @return TransactionLoggerIfc instance
    **/
    //---------------------------------------------------------------------
    public TransactionLoggerIfc getTransactionLoggerInstance()
    {
        return(new TransactionLogger());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogAddressIfc class. <P>
        @return LogAddressIfc instance
    **/
    //---------------------------------------------------------------------
    public LogAddressIfc getLogAddressInstance()
    {
        return(new LogAddress());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogGiftCardIfc class. <P>
        @return LogGiftCardIfc instance
    **/
    //---------------------------------------------------------------------
    public LogGiftCardIfc getLogGiftCardInstance()
    {
        return(new LogGiftCard());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogPhoneIfc class. <P>
        @return LogPhoneIfc instance
    **/
    //---------------------------------------------------------------------
    public LogPhoneIfc getLogPhoneInstance()
    {
        return(new LogPhone());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogQuantityIfc class. <P>
        @return LogQuantityIfc instance
    **/
    //---------------------------------------------------------------------
    public LogQuantityIfc getLogQuantityInstance()
    {
        return(new LogQuantity());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of POSLogWriterIfc class. <P>
        @return POSLogWriterIfc instance
    **/
    //---------------------------------------------------------------------
    public POSLogWriterIfc getPOSLogWriterInstance()
    {
        return(new POSLogWriter());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogTrainingModeTransaction class. <P>
        @return instance of LogTrainingModeTransactionIfc
    **/
    //---------------------------------------------------------------------
    public LogTrainingModeTransactionIfc getLogTrainingModeTransactionInstance()
    {
        return null;        // not yet implemented in v1.0
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogRedeemTransaction class. <P>
        @return instance of LogRedeemTransactionIfc
    **/
    //---------------------------------------------------------------------
    public LogRedeemTransactionIfc getLogRedeemTransactionInstance()
    {
        return null;        // not yet implemented in v1.0
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogInstantCreditTransaction class. <P>
        @return instance of LogInstantCreditTransactionIfc
    **/
    //---------------------------------------------------------------------
    public LogInstantCreditTransactionIfc getLogInstantCreditTransactionInstance()
    {
        return null;        // not yet implemented in v1.0
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogReEntryTransaction class. <P>
        @return instance of logger, implementing only LogTransactionIfc
    **/
    //---------------------------------------------------------------------
    public LogTransactionIfc getLogReEntryTransactionInstance()
    {
        return new LogTransaction();    // v1.0 handled this in default case
    }


    //---------------------------------------------------------------------
    /**
        Returns instance of SchemaTypesFactory class. <P>
        @return SchemaTypesFactoryIfc instance
    **/
    //---------------------------------------------------------------------
    public SchemaTypesFactoryIfc getSchemaTypesFactoryInstance()
    {
        return new SchemaTypesFactory();
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogBillPaymentIfc class. <P>
        @return SchemaTypesFactoryIfc instance
    **/
    //---------------------------------------------------------------------
    public LogBillPaymentIfc getLogBillPaymentInstance()
    {
          return new LogBillPayment();
    }



    //----------------------------------------------------------------------------
    /**
        Returns default display string. <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // build result string
        StringBuilder strResult =
          Util.classToStringHeader("IXRetailFactory",
                                    revisionNumber,
                                    hashCode());
        // pass back result
        return(strResult.toString());
    }                                   // end toString()


}                                       // end class IXRetailFactory
