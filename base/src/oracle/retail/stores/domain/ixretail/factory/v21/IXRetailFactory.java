/* ===========================================================================
* Copyright (c) 2005, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/factory/v21/IXRetailFactory.java /rgbustores_13.4x_generic_branch/2 2011/05/11 16:05:19 mszekely Exp $
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
 6    360Commerce 1.5         4/12/2008 5:44:57 PM   Christian Greene Upgrade
 *    StringBuffer to StringBuilder
 5    360Commerce 1.4         4/27/2006 7:29:45 PM   Brett J. Larsen CR 17307 -
 *     remove inventory functionality - stage 2
 4    360Commerce 1.3         12/13/2005 4:43:48 PM  Barry A. Pape
 *    Base-lining of 7.1_LA
 3    360Commerce 1.2         3/31/2005 4:28:35 PM   Robert Pearse   
 2    360Commerce 1.1         3/10/2005 10:22:34 AM  Robert Pearse   
 1    360Commerce 1.0         2/11/2005 12:11:43 PM  Robert Pearse   
 *
Revision 1.6.2.1  2004/12/08 00:06:31  mwright
Added logger for transaction re-entry
 *
Revision 1.6  2004/08/10 07:17:11  mwright
Merge (3) with top of tree
 *
Revision 1.5.6.2  2004/08/09 12:44:00  mwright
Added instant credit enrollment transaction logger
 *
Revision 1.5.6.1  2004/07/29 00:52:38  mwright
Added enter/exit training mode and redeem transaction loggers
 *
Revision 1.5  2004/05/06 03:33:06  mwright
POSLog v2.1 merge with top of tree
 *
Revision 1.2.2.7  2004/05/05 23:34:21  mwright
Added batch total logger
 *
Revision 1.2.2.6  2004/05/05 02:25:57  mwright
Added factory methods for till adjustment and inventory-related loggers
 *
Revision 1.2.2.5  2004/04/26 07:13:45  mwright
Added factory methods for LogTillOpenClose(), LogFinancialTotals(), LogVoidTransaction(), LogPaymentTransaction(), LogBankDepositTransaction()
 *
Revision 1.2.2.4  2004/04/19 07:05:19  mwright
Added factory calls for new loggers
 *
Revision 1.2.2.3  2004/04/13 06:03:27  mwright
Added factory methods for new v2.1 loggers
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.factory.v21;

import oracle.retail.stores.domain.ixretail.customer.LogCustomerIfc;
import oracle.retail.stores.domain.ixretail.customer.LogIRSCustomerIfc;
import oracle.retail.stores.domain.ixretail.customer.v21.LogCustomer;
import oracle.retail.stores.domain.ixretail.customer.v21.LogIRSCustomer;
import oracle.retail.stores.domain.ixretail.discount.LogDiscountLineItemIfc;
import oracle.retail.stores.domain.ixretail.discount.v21.LogDiscountLineItem;
import oracle.retail.stores.domain.ixretail.factory.IXRetailFactoryIfc;
import oracle.retail.stores.domain.ixretail.financial.LogDrawerIfc;
import oracle.retail.stores.domain.ixretail.financial.LogFinancialCountIfc;
import oracle.retail.stores.domain.ixretail.financial.LogFinancialCountTenderItemIfc;
import oracle.retail.stores.domain.ixretail.financial.LogFinancialTotalsIfc;
import oracle.retail.stores.domain.ixretail.financial.LogLayawayIfc;
import oracle.retail.stores.domain.ixretail.financial.LogPaymentHistoryIfc;
import oracle.retail.stores.domain.ixretail.financial.LogPaymentLineItemIfc;
import oracle.retail.stores.domain.ixretail.financial.LogRegisterIfc;
import oracle.retail.stores.domain.ixretail.financial.v21.LogDrawer;
import oracle.retail.stores.domain.ixretail.financial.v21.LogFinancialCount;
import oracle.retail.stores.domain.ixretail.financial.v21.LogFinancialCountTenderItem;
import oracle.retail.stores.domain.ixretail.financial.v21.LogFinancialTotals;
import oracle.retail.stores.domain.ixretail.financial.v21.LogLayaway;
import oracle.retail.stores.domain.ixretail.financial.v21.LogPaymentHistory;
import oracle.retail.stores.domain.ixretail.financial.v21.LogPaymentLineItem;
import oracle.retail.stores.domain.ixretail.financial.v21.LogRegister;
import oracle.retail.stores.domain.ixretail.lineitem.LogLineItemIfc;
import oracle.retail.stores.domain.ixretail.lineitem.LogOrderItemIfc;
import oracle.retail.stores.domain.ixretail.lineitem.LogSaleReturnLineItemIfc;
import oracle.retail.stores.domain.ixretail.lineitem.v21.LogLineItem;
import oracle.retail.stores.domain.ixretail.lineitem.v21.LogOrderItem;
import oracle.retail.stores.domain.ixretail.lineitem.v21.LogSaleReturnLineItem;
import oracle.retail.stores.domain.ixretail.log.LogBatchTotalIfc;
import oracle.retail.stores.domain.ixretail.log.LogIfc;
import oracle.retail.stores.domain.ixretail.log.v21.Log;
import oracle.retail.stores.domain.ixretail.log.v21.LogBatchTotal;
import oracle.retail.stores.domain.ixretail.tender.LogTenderLineItemIfc;
import oracle.retail.stores.domain.ixretail.tender.v21.LogTenderLineItem;
import oracle.retail.stores.domain.ixretail.transaction.LogBankDepositTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogInstantCreditTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogNoSaleTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogPaymentTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogRedeemTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogRegisterOpenCloseTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogRetailTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogStoreOpenCloseTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogTillAdjustmentTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogTillOpenCloseTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogTrainingModeTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogVoidTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.v21.LogBankDepositTransaction;
import oracle.retail.stores.domain.ixretail.transaction.v21.LogInstantCreditTransaction;
import oracle.retail.stores.domain.ixretail.transaction.v21.LogNoSaleTransaction;
import oracle.retail.stores.domain.ixretail.transaction.v21.LogPaymentTransaction;
import oracle.retail.stores.domain.ixretail.transaction.v21.LogReEntryModeTransaction;
import oracle.retail.stores.domain.ixretail.transaction.v21.LogRedeemTransaction;
import oracle.retail.stores.domain.ixretail.transaction.v21.LogRegisterOpenCloseTransaction;
import oracle.retail.stores.domain.ixretail.transaction.v21.LogRetailTransaction;
import oracle.retail.stores.domain.ixretail.transaction.v21.LogStoreOpenCloseTransaction;
import oracle.retail.stores.domain.ixretail.transaction.v21.LogTillAdjustmentTransaction;
import oracle.retail.stores.domain.ixretail.transaction.v21.LogTillOpenCloseTransaction;
import oracle.retail.stores.domain.ixretail.transaction.v21.LogTrainingModeTransaction;
import oracle.retail.stores.domain.ixretail.transaction.v21.LogTransaction;
import oracle.retail.stores.domain.ixretail.transaction.v21.LogVoidTransaction;
import oracle.retail.stores.domain.ixretail.utility.LogAddressIfc;
import oracle.retail.stores.domain.ixretail.utility.LogGiftCardIfc;
import oracle.retail.stores.domain.ixretail.utility.LogPhoneIfc;
import oracle.retail.stores.domain.ixretail.utility.v21.LogAddress;
import oracle.retail.stores.domain.ixretail.utility.v21.LogGiftCard;
import oracle.retail.stores.domain.ixretail.utility.v21.LogPhone;
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
     This is the base IXRetail object factory for V21.  It simply returns instances
     of classes.  It is employed through the {@link oracle.retail.stores.domain.ixretail.IXRetailGateway#getFactory()}
     method.  For instance, to invoke an instance of Log, the developer
     would code {@code IXRetailGateway.getFactory().getLogInstance()}. <P>
     @see oracle.retail.stores.domain.ixretail.IXRetailGateway
     @version $Revision: /rgbustores_13.4x_generic_branch/2 $
**/
//----------------------------------------------------------------------------
public class IXRetailFactory
extends oracle.retail.stores.domain.ixretail.factory.IXRetailFactory
implements IXRetailFactoryIfc
{                                       // begin class IXRetailFactory
    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

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
        Returns instance of LogIfc class. <P>
        @return LogIfc instance
    **/
    //---------------------------------------------------------------------
    public LogIfc getLogInstance()
    {
        return new Log();
    }

    
    //---------------------------------------------------------------------
    /**
        Returns instance of LogNoSaleTransactionIfc class. <P>
        @return LogNoSaleTransactionIfc instance
    **/
    //---------------------------------------------------------------------
    public LogNoSaleTransactionIfc getLogNoSaleTransactionInstance()
    {
        return new LogNoSaleTransaction();
    }
    
    //---------------------------------------------------------------------
    /**
        Returns instance of LogStoreOpenCloseTransactionIfc class. <P>
        @return LogStoreOpenCloseTransactionIfc instance
    **/
    //---------------------------------------------------------------------
    public LogStoreOpenCloseTransactionIfc getLogStoreOpenCloseTransactionInstance()
    {
        return (LogStoreOpenCloseTransactionIfc) new LogStoreOpenCloseTransaction();
    }

    
    //---------------------------------------------------------------------
    /**
        Returns instance of LogRegisterOpenCloseTransactionIfc class. <P>
        @return LogRegisterOpenCloseTransactionIfc instance
    **/
    //---------------------------------------------------------------------
    public LogRegisterOpenCloseTransactionIfc getLogRegisterOpenCloseTransactionInstance()
    {
        return new LogRegisterOpenCloseTransaction();
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogTillOpenCloseTransactionIfc class. <P>
        @return LogTillOpenCloseTransactionIfc instance
    **/
    //---------------------------------------------------------------------
    public LogTillOpenCloseTransactionIfc getLogTillOpenCloseTransactionInstance()
    {
        return (LogTillOpenCloseTransactionIfc) new LogTillOpenCloseTransaction();
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogFinancialCountTenderItemIfc class. <P>
        @return LogFinancialCountTenderItemIfc instance
    **/
    //---------------------------------------------------------------------
    public LogFinancialCountTenderItemIfc getLogFinancialCountTenderItemInstance()
    {
        return new LogFinancialCountTenderItem();
    }

    
    //---------------------------------------------------------------------
    /**
        Returns instance of LogRetailTransactionIfc class. <P>
        @return LogRetailTransactionIfc instance
    **/
    //---------------------------------------------------------------------
    public LogRetailTransactionIfc getLogRetailTransactionInstance()
    {
        return new LogRetailTransaction();
    }
    
    //---------------------------------------------------------------------
    /**
        Returns instance of LogLayawayIfc class. <P>
        @return LogLayawayIfc instance
    **/
    //---------------------------------------------------------------------
    public LogLayawayIfc getLogLayawayInstance()
    {
        return new LogLayaway();
    }
    
    //---------------------------------------------------------------------
    /**
        Returns instance of PaymentHistoryIfc class. <P>
        @return LogPaymentHistoryIfc instance
    **/
    //---------------------------------------------------------------------
    public LogPaymentHistoryIfc getLogPaymentHistoryInstance()
    {
        return(new LogPaymentHistory());
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogPaymentLineItemIfc class. <P>
        @return LogPaymentLineItemIfc instance
    **/
    //---------------------------------------------------------------------
    public LogPaymentLineItemIfc getLogPaymentLineItemInstance()
    {
        return new LogPaymentLineItem();
    }
    
    //---------------------------------------------------------------------
    /**
        Returns instance of LogCustomerIfc class. <P>
        @return LogCustomerIfc instance
    **/
    //---------------------------------------------------------------------
    public LogCustomerIfc getLogCustomerInstance()
    {
        return new LogCustomer();
    }
    
    //---------------------------------------------------------------------
    /**
        Returns instance of LogIRSCustomerIfc class. <P>
        @return LogIRSCustomerIfc instance
    **/
    //---------------------------------------------------------------------
    public LogIRSCustomerIfc getLogIRSCustomerInstance()
    {
        return (new LogIRSCustomer());
    }
        
    //---------------------------------------------------------------------
    /**
        Returns instance of LogLineItemIfc class. <P>
        @return LogLineItemIfc instance
    **/
    //---------------------------------------------------------------------
    public LogLineItemIfc getLogLineItemInstance()
    {
        return new LogLineItem();
    }
    
    //---------------------------------------------------------------------
    /**
        Returns instance of LogDiscountLineItemIfc class. <P>
        @return LogDiscountLineItemIfc instance
    **/
    //---------------------------------------------------------------------
    public LogDiscountLineItemIfc getLogDiscountLineItemInstance()
    {
        return new LogDiscountLineItem();
    }
    
    //---------------------------------------------------------------------
    /**
        Returns instance of LogOrderItemIfc class. <P>
        @return LogOrderItemIfc instance
    **/
    //---------------------------------------------------------------------
    public LogOrderItemIfc getLogOrderItemInstance()
    {
        return new LogOrderItem();
    }
    
    //---------------------------------------------------------------------
    /**
        Returns instance of LogSaleReturnLineItemIfc class. <P>
        @return LogSaleReturnLineItemIfc instance
    **/
    //---------------------------------------------------------------------
    public LogSaleReturnLineItemIfc getLogSaleReturnLineItemInstance()
    {
        return new LogSaleReturnLineItem();
    }
    
    //---------------------------------------------------------------------
    /**
        Returns instance of LogTenderLineItemIfc class. <P>
        @return LogTenderLineItemIfc instance
    **/
    //---------------------------------------------------------------------
    public LogTenderLineItemIfc getLogTenderLineItemInstance()
    {
        return new LogTenderLineItem();
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogAddressIfc class. <P>
        @return LogAddressIfc instance
    **/
    //---------------------------------------------------------------------
    public LogAddressIfc getLogAddressInstance()
    {
        return new LogAddress();
    }
    
    //---------------------------------------------------------------------
    /**
        Returns instance of LogGiftCardIfc class. <P>
        @return LogGiftCardIfc instance
    **/
    //---------------------------------------------------------------------
    public LogGiftCardIfc getLogGiftCardInstance()
    {
        return new LogGiftCard();
    }
    
    //---------------------------------------------------------------------
    /**
        Returns instance of LogPhoneIfc class. <P>
        @return LogPhoneIfc instance
    **/
    //---------------------------------------------------------------------
    public LogPhoneIfc getLogPhoneInstance()
    {
        return new LogPhone();
    }

    
    //---------------------------------------------------------------------
    /**
        Returns instance of LogDrawerIfc class. <P>
        @return LogDrawerIfc instance
    **/
    //---------------------------------------------------------------------
    public LogDrawerIfc getLogDrawerInstance()
    {
        return new LogDrawer();
    }
    
    //---------------------------------------------------------------------
    /**
        Returns instance of LogFinancialCountIfc class. <P>
        @return LogFinancialCountIfc instance
    **/
    //---------------------------------------------------------------------
    public LogFinancialCountIfc getLogFinancialCountInstance()
    {
        return new LogFinancialCount();
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogRegisterIfc class. <P>
        @return LogRegisterIfc instance
    **/
    //---------------------------------------------------------------------
    public LogRegisterIfc getLogRegisterInstance()
    {
        return new LogRegister();
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogFinancialTotalsIfc class. <P>
        @return LogFinancialTotalsIfc instance
    **/
    //---------------------------------------------------------------------
    public LogFinancialTotalsIfc getLogFinancialTotalsInstance()
    {
        return new LogFinancialTotals();
    }
   
    //---------------------------------------------------------------------
    /**
        Returns instance of LogVoidTransactionIfc class. <P>
        @return LogVoidTransactionIfc instance
    **/
    //---------------------------------------------------------------------
    public LogVoidTransactionIfc getLogVoidTransactionInstance()
    {
        return new LogVoidTransaction();
    }
    
    //---------------------------------------------------------------------
    /**
        Returns instance of LogPaymentTransactionIfc class. <P>
        @return LogPaymentTransactionIfc instance
    **/
    //---------------------------------------------------------------------
    public LogPaymentTransactionIfc getLogPaymentTransactionInstance()
    {
        return new LogPaymentTransaction();
    }
    
    //---------------------------------------------------------------------
    /**
        Returns instance of LogBankDepositTransactionIfc class. <P>
        @return LogBankDepositTransactionIfc instance
    **/
    //---------------------------------------------------------------------
    public LogBankDepositTransactionIfc getLogBankDepositTransactionInstance()
    {
        return new LogBankDepositTransaction();
    }
    
    //---------------------------------------------------------------------
    /**
        Returns instance of LogTillAdjustmentTransactionIfc class. <P>
        @return LogTillAdjustmentTransactionIfc instance
    **/
    //---------------------------------------------------------------------
    public LogTillAdjustmentTransactionIfc getLogTillAdjustmentTransactionInstance()
    {
        return new LogTillAdjustmentTransaction();
    }
    
    //---------------------------------------------------------------------
    /**
        Returns instance of LogBatchTotalIfc class. <P>
        @return LogBatchTotalIfc instance
    **/
    //---------------------------------------------------------------------
    public LogBatchTotalIfc getLogBatchTotalInstance()
    {
        return new LogBatchTotal();
    }
    

    //---------------------------------------------------------------------
    /**
        Returns instance of LogTrainingModeTransaction class. <P>
        @return instance of LogTrainingModeTransactionIfc
    **/
    //---------------------------------------------------------------------
    public LogTrainingModeTransactionIfc getLogTrainingModeTransactionInstance()
    {
        return new LogTrainingModeTransaction();
    }
    
    
    //---------------------------------------------------------------------
    /**
        Returns instance of LogRedeemTransaction class. <P>
        @return instance of LogRedeemTransactionIfc
    **/
    //---------------------------------------------------------------------
    public LogRedeemTransactionIfc getLogRedeemTransactionInstance()
    {
        return new LogRedeemTransaction();
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogInstantCreditTransaction class. <P>
        @return instance of LogInstantCreditTransactionIfc
    **/
    //---------------------------------------------------------------------
    public LogInstantCreditTransactionIfc getLogInstantCreditTransactionInstance()
    {
        return new LogInstantCreditTransaction(); 
    }

    //---------------------------------------------------------------------
    /**
        Returns instance of LogReEntryTransaction class. <P>
        @return instance of logger, implementing only LogTransactionIfc
    **/
    //---------------------------------------------------------------------
    public LogTransactionIfc getLogReEntryTransactionInstance()
    {
        return new LogReEntryModeTransaction();
    }
    
    //---------------------------------------------------------------------
    /**
        Returns instance of LogTransactionIfc class. <P>
        @return LogTransactionIfc instance
    **/
    //---------------------------------------------------------------------
    public LogTransactionIfc getLogTransactionInstance()
    {
        return new LogTransaction();
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
