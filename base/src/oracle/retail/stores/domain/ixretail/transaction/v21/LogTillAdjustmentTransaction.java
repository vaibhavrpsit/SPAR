/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/transaction/v21/LogTillAdjustmentTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:09 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rrkohli   09/23/10 - fix to add address field in POSLog
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    ranojha   02/22/09 - Fixed unittests using tenderDescriptor element in
 *                         POSLog
 *    ranojha   02/20/09 - Incorporated code review comments
 *    ranojha   02/18/09 - Fixed import and export logic for TenderDescriptor
 *                         in till Pickup POSLog
 *    ohorne    11/03/08 - Localization of Till-related Reason Codes
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         5/20/2008 5:12:41 PM   Leona R. Slepetis Add
 *          POSLog support for postvoided till functions (loan, payroll,
 *         pickup, payin, payout). Reviewed by R. Ojha.
 *    4    360Commerce 1.3         6/26/2007 11:13:58 AM  Ashok.Mondal    I18N
 *         changes to export and import POSLog.
 *    3    360Commerce 1.2         3/31/2005 4:28:57 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:17 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:27 PM  Robert Pearse   
 *
 *   Revision 1.6.2.7  2004/11/30 04:00:10  mwright
 *   Added back numeric reason code
 *
 *   Revision 1.6.2.6  2004/11/29 23:40:03  mwright
 *   Undo last changes until requirements are updated
 *   The address and employee ID fields are commented out, so we can add them back when the requirements are updated
 *
 *   Revision 1.6.2.5  2004/11/29 05:09:14  mwright
 *   Add back the address and employee ID elements, because they are used by the POS, i.e. non-null in the table
 *
 *   Revision 1.6.2.4  2004/11/22 23:06:40  kll
 *   @scr 7716, 7717: updates for various till adjustment transactions
 *
 *   Revision 1.6.2.3  2004/11/22 17:42:08  kll
 *   @scr 7716, 7717: retract changes to schema
 *
 *   Revision 1.7  2004/11/11 11:11:57  mwright
 *   Log reason descrption rather than code
 *
 *   Revision 1.6  2004/09/30 18:43:58  kmcbride
 *   @scr 7266: Adding attributes to the till adjustment for POSLog generation.
 *
 *   Revision 1.5  2004/08/10 07:17:09  mwright
 *   Merge (3) with top of tree
 *
 *   Revision 1.4.2.3  2004/08/06 02:33:19  mwright
 *   Fixed bug where condition for payroll payout was inverted
 *
 *   Revision 1.4.2.2  2004/08/01 23:46:02  mwright
 *   Removed TO-DO tags on completed tasks
 *
 *   Revision 1.4.2.1  2004/07/29 01:42:54  mwright
 *   Added payroll payout transaction
 *
 *   Revision 1.4  2004/06/30 08:13:26  mwright
 *   Removed transaction type from adjustment element
 *   Use one of 4 different elements in schema type record, instead of single till adjustment element
 *
 *   Revision 1.3  2004/06/24 09:15:09  mwright
 *   POSLog v2.1 (second) merge with top of tree
 *
 *   Revision 1.2.2.2  2004/06/15 06:38:01  mwright
 *   Removed TO DO reminding of test case
 *
 *   Revision 1.2.2.1  2004/06/10 10:55:23  mwright
 *   Updated to use schema types in commerce services
 *
 *   Revision 1.2  2004/05/06 03:21:07  mwright
 *   Initial revision for POSLog v2.1 merge with top of tree
 *
 *   Revision 1.1.2.2  2004/05/05 02:34:24  mwright
 *   Changed session settlement element to a financial count element
 *
 *   Revision 1.1.2.1  2004/04/27 22:14:27  mwright
 *   Initial revision for v2.1
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.transaction.v21;

import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.FinancialCountElement360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogTotalsIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.TillAdjustment360Ifc;
import oracle.retail.stores.commerceservices.xmltosql.v21.XmlToSqlUtilityMethods;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.TenderDescriptor360Ifc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.financial.LogFinancialCountIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogTillAdjustmentTransactionIfc;
import oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;


//--------------------------------------------------------------------------
/**
    This class creates the TLog in IXRetail format for the Retail Transaction
    View for a till adjustment transaction.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogTillAdjustmentTransaction
extends LogTenderControlTransaction
implements LogTillAdjustmentTransactionIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------------
    /**
        Constructs LogTillAdjustmentTransaction object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogTillAdjustmentTransaction()
    {
    }

    //---------------------------------------------------------------------
    /**
       Populate the schema type element with transaction-specific data.
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createBaseElements()
    throws XMLConversionException
    {
        super.createBaseElements();     // sets up the element infrastructure, particularly creates tenderControlTransactionElement

        int transactionType = transaction.getTransactionType();

        if (transactionType != TransactionConstantsIfc.TYPE_VOID)
        {
            addBaseElements(transaction);
        }        
    }

    
    //----------------------------------------------------------------------
    /**
        
        @param transaction
        @throws XMLConversionException
    **/
    //----------------------------------------------------------------------
    protected void addBaseElements(TransactionIfc transaction)
        throws XMLConversionException
    {
        int transactionType = transaction.getTransactionType();
        if (transactionType != TransactionConstantsIfc.TYPE_PAYROLL_PAYOUT_TILL)
        {
            createBaseElementsForTillAdjustment(transactionType, transaction);
        }
        else
        {
            createBaseElementsForPayrollPayout(transaction);
        }
    }
    
    //--------------------------------------------------------------------
    /**
       Populate the schema type element with transaction-specific data.
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createBaseElementsForTillAdjustment(int transactionType, TransactionIfc transaction)
    throws XMLConversionException
    {
        
        // set reference to TillAdjustmentTransactionIfc
        TillAdjustmentTransactionIfc tillAdjustmentTransaction = (TillAdjustmentTransactionIfc) transaction;

        TillAdjustment360Ifc tillAdjustmentElement = getSchemaTypesFactory().getTillAdjustment360Instance();
        
        POSLogTotalsIfc adjustmentElement = getSchemaTypesFactory().getPOSLogTotalsInstance();
        POSLogTotalsIfc expectedElement   = getSchemaTypesFactory().getPOSLogTotalsInstance();
        
        adjustmentElement.setAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(tillAdjustmentTransaction.getAdjustmentAmount())));
        adjustmentElement.setCount(Integer.toString(tillAdjustmentTransaction.getAdjustmentCount()));
        
        expectedElement.setAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(tillAdjustmentTransaction.getExpectedAmount())));
        expectedElement.setCount(Integer.toString(tillAdjustmentTransaction.getExpectedCount()));
        
        //tillAdjustmentElement.setTenderType(tillAdjustmentTransaction.getTenderType());
        tillAdjustmentElement.setCurrencyID(tillAdjustmentTransaction.getCurrencyID()); //I18N
        tillAdjustmentElement.setAdjustment(adjustmentElement);
        tillAdjustmentElement.setExpected(expectedElement);
        if (transactionType == TransactionConstantsIfc.TYPE_PAYOUT_TILL
                || transactionType == TransactionConstantsIfc.TYPE_PAYIN_TILL
                || transactionType == TransactionConstantsIfc.TYPE_PAYROLL_PAYOUT_TILL)
        {
            tillAdjustmentElement.setReasonCodeNumeric(tillAdjustmentTransaction.getReason().getCode());
        }
        
        tillAdjustmentElement.setCountType(FinancialCountIfc.COUNT_TYPE_DESCRIPTORS[tillAdjustmentTransaction.getCountType()]);

        FinancialCountIfc financialCount = tillAdjustmentTransaction.getTenderCount();
        if (financialCount != null)
        {
            FinancialCountElement360Ifc tenderCount = getSchemaTypesFactory().getFinancialCountElement360Instance();
            
            LogFinancialCountIfc logCount = IXRetailGateway.getFactory().getLogFinancialCountInstance();

            logCount.createElement(financialCount, null, tenderCount, null);
            tillAdjustmentElement.setTenderCount(tenderCount);
        }
        TenderDescriptor360Ifc tenderDescriptorElement = getSchemaTypesFactory().getTenderDescriptor360Instance();
        String tenderCode = DomainGateway.getFactory().getTenderTypeMapInstance().getCode(tillAdjustmentTransaction.getTender().getTenderType());
        tenderDescriptorElement.setTenderType(tenderCode);
        tenderDescriptorElement.setTenderSubType(tillAdjustmentTransaction.getTender().getTenderSubType());
        tenderDescriptorElement.setCountryCode(tillAdjustmentTransaction.getTender().getCountryCode());
        tenderDescriptorElement.setCurrencyID(tillAdjustmentTransaction.getTender().getCurrencyID());

        tillAdjustmentElement.setTender(tenderDescriptorElement);
        
        // new columns in table:
        if (transactionType == TransactionConstantsIfc.TYPE_PAYOUT_TILL
                || transactionType == TransactionConstantsIfc.TYPE_PAYROLL_PAYOUT_TILL)
        {
            tillAdjustmentElement.setPayeeName(tillAdjustmentTransaction.getPayeeName());
            tillAdjustmentElement.setAddressLine1(tillAdjustmentTransaction.getAddressLine(0));
            tillAdjustmentElement.setAddressLine2(tillAdjustmentTransaction.getAddressLine(1));
            tillAdjustmentElement.setAddressLine3(tillAdjustmentTransaction.getAddressLine(2));
            tillAdjustmentElement.setComments(tillAdjustmentTransaction.getComments());
            
            if (tillAdjustmentTransaction.getApproval() != null)
            {    
                String approval = XmlToSqlUtilityMethods.lookupTillApproval(tillAdjustmentTransaction.getApproval().getCode());
                tillAdjustmentElement.setApprovalCode(approval);
            }
        }
        
        switch (transactionType)
        {
            case TransactionConstantsIfc.TYPE_PAYIN_TILL :
                tenderControlTransactionElement.setPaidIn360(tillAdjustmentElement);
                break;
            
            case TransactionConstantsIfc.TYPE_PAYOUT_TILL :
                tenderControlTransactionElement.setPaidOut360(tillAdjustmentElement);
                break;
                
            case TransactionConstantsIfc.TYPE_PICKUP_TILL :
                tenderControlTransactionElement.setPickup360(tillAdjustmentElement);
                break;
                
            case TransactionConstantsIfc.TYPE_LOAN_TILL :
                tenderControlTransactionElement.setLoan360(tillAdjustmentElement);
                break;

            default :
                throw new XMLConversionException("Unknown type of Till Adjustment Transaction: " + Integer.toString(transactionType));
        }
    }

    //---------------------------------------------------------------------
    /**
       Populate the schema type element with transaction-specific data.
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createBaseElementsForPayrollPayout(TransactionIfc transaction)
    throws XMLConversionException
    {
        TillAdjustmentTransactionIfc tillAdjustmentTransaction = (TillAdjustmentTransactionIfc) transaction;
        POSLogTotalsIfc expectedElement   = getSchemaTypesFactory().getPOSLogTotalsInstance();
        
        TillAdjustment360Ifc tillAdjustmentElement = getSchemaTypesFactory().getTillAdjustment360Instance();
        POSLogTotalsIfc adjustmentElement          = getSchemaTypesFactory().getPOSLogTotalsInstance();

        adjustmentElement.setAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(tillAdjustmentTransaction.getAdjustmentAmount())));
        tillAdjustmentElement.setAdjustment(adjustmentElement);
        // if expected is mandatory, make it same as adjustment...
        tillAdjustmentElement.setExpected(expectedElement);
        
        tillAdjustmentElement.setReasonCodeNumeric(tillAdjustmentTransaction.getReason().getCode());
        
        tillAdjustmentElement.setCountType(FinancialCountIfc.COUNT_TYPE_DESCRIPTORS[tillAdjustmentTransaction.getCountType()]);
        tillAdjustmentElement.setTenderType(tillAdjustmentTransaction.getTenderType());
        tillAdjustmentElement.setCurrencyID(tillAdjustmentTransaction.getCurrencyID()); //I18N

        // new columns in table:
        tillAdjustmentElement.setPayeeName(tillAdjustmentTransaction.getPayeeName());
        tillAdjustmentElement.setAddressLine1(tillAdjustmentTransaction.getAddressLine(0));
        tillAdjustmentElement.setAddressLine2(tillAdjustmentTransaction.getAddressLine(1));
        tillAdjustmentElement.setAddressLine3(tillAdjustmentTransaction.getAddressLine(2));

        tillAdjustmentElement.setComments(tillAdjustmentTransaction.getComments());

        if (tillAdjustmentTransaction.getApproval() != null)
        {
            String approval = XmlToSqlUtilityMethods.lookupTillApproval(tillAdjustmentTransaction.getApproval().getCode());
            tillAdjustmentElement.setApprovalCode(approval);            
        }

        tillAdjustmentElement.setEmployeeID(tillAdjustmentTransaction.getEmployeeID());
        
        tenderControlTransactionElement.setPayrollPayout360(tillAdjustmentElement);
    }    
    
}

/*
  
 Payroll payout trx:
 
INSERT INTO tr_fn_acnt ( 
ty_trn_fn_acnt,     '40'            always 40 for payroll payouts
ty_tnd,             'Cash' 

ai_trn,             19
dc_dy_bsn,          '2004-02-01'
id_ws,              '129'
id_str_rt           '04241'



INSERT INTO tr_rcv_fnd ( 
rc_rcv_dsb,         '1' 
mo_rcv_fnd,         -95.00

dc_dy_bsn,          '2004-02-01'
ai_trn,             19
id_str_rt           '04241'
id_ws,              '129' 




*/
