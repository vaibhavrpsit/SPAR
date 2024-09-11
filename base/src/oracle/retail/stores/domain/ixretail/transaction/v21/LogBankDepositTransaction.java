/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/transaction/v21/LogBankDepositTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:09 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:53 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:10 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:21 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/06/24 09:15:09  mwright
 *   POSLog v2.1 (second) merge with top of tree
 *
 *   Revision 1.2.2.1  2004/06/10 10:55:23  mwright
 *   Updated to use schema types in commerce services
 *
 *   Revision 1.2  2004/05/06 03:21:07  mwright
 *   Initial revision for POSLog v2.1 merge with top of tree
 *
 *   Revision 1.1.2.2  2004/05/05 02:29:26  mwright
 *   Added amount to deposit element
 *
 *   Revision 1.1.2.1  2004/04/27 22:14:27  mwright
 *   Initial revision for v2.1
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.transaction.v21;

import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
import oracle.retail.stores.domain.transaction.BankDepositTransactionIfc;
import oracle.retail.stores.domain.financial.FinancialCountIfc;

import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.transaction.LogBankDepositTransactionIfc;
import oracle.retail.stores.domain.ixretail.financial.LogFinancialCountIfc;

import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogTCDepositIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogTCTenderTotalIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogOperatorIDIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.FinancialCountElement360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.FinancialCountTenderItemElement360Ifc;

public class LogBankDepositTransaction
extends LogTenderControlTransaction
implements LogBankDepositTransactionIfc
{
 /**
    revision number supplied by source-code-control system
 **/
 public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

 
 protected void createBaseElements()
 throws XMLConversionException
 {
     super.createBaseElements();     // creates tenderControlTransactionElement
     
     POSLogTCDepositIfc depositElement = getSchemaTypesFactory().getPOSLogTCDepositInstance();
     tenderControlTransactionElement.setDeposit(depositElement);        // this makes the tender control trx a bank deposit

     POSLogOperatorIDIfc operator = getSchemaTypesFactory().getPOSLogOperatorIDInstance();
     operator.setOperatorID(transaction.getCashier().getEmployeeID());
     
     depositElement.setBank(ELEMENT_VALUE_NOT_SUPPORTED);
     depositElement.setAccount(ELEMENT_VALUE_NOT_SUPPORTED);
     depositElement.setBagID(ELEMENT_VALUE_NOT_SUPPORTED);
     depositElement.setDescription(ELEMENT_VALUE_NOT_SUPPORTED);
     
     // it might be nice to accumulate all the deposits and put the result in here...watch the currencies though
     depositElement.setAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(null)));
     
     depositElement.setDepositor(operator);     // this is redundant, should be removed in next version of schema
     // the deposit details are added below:
     
     FinancialCountIfc depositCount = ((BankDepositTransactionIfc)transaction).getDepositCount();
     if (depositCount != null)
     {
         FinancialCountElement360Ifc financialCountElement = getSchemaTypesFactory().getFinancialCountElement360Instance();
         LogFinancialCountIfc        logDepositCount       = IXRetailGateway.getFactory().getLogFinancialCountInstance();
         
         logDepositCount.createElement(depositCount, null, financialCountElement, null);
         
         // We need to convert the FinancialCountTenderItemElement360Ifc[] that the logger produces into a list of POSLogTCTenderTotalIfc elements
         FinancialCountTenderItemElement360Ifc[] tenderItemElements = financialCountElement.getTenderItems();
         if (tenderItemElements != null)
         {
             for (int i = 0; i < tenderItemElements.length; i++)
             {
                 FinancialCountTenderItemElement360Ifc tenderElement = tenderItemElements[i];
                 POSLogTCTenderTotalIfc depositDetail = getSchemaTypesFactory().getPOSLogTCTenderTotalInstance();
                 depositDetail.setTenderTotal(tenderElement.getIncoming());  // "amount and number in" in v1.0
                 depositDetail.setTenderType(tenderElement.getTenderType());
                 depositDetail.setSubTenderType(tenderElement.getSubTenderType());
                 depositDetail.setDenomination(tenderElement.getDenomination());
                 depositElement.addDepositDetail(depositDetail);
             }
         }
     }
     
     
 }
 
 
 
}
