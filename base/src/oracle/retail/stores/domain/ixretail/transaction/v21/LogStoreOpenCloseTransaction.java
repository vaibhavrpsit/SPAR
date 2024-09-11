/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/transaction/v21/LogStoreOpenCloseTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:09 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:56 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:16 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:26 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/08/10 07:17:09  mwright
 *   Merge (3) with top of tree
 *
 *   Revision 1.3.2.1  2004/07/09 04:12:51  mwright
 *   Get open time from store status object
 *
 *   Revision 1.3  2004/06/24 09:15:09  mwright
 *   POSLog v2.1 (second) merge with top of tree
 *
 *   Revision 1.2.2.2  2004/06/23 00:39:41  mwright
 *   No code change, just cleared up misleading comment about end date stamp
 *
 *   Revision 1.2.2.1  2004/06/10 10:55:23  mwright
 *   Updated to use schema types in commerce services
 *
 *   Revision 1.2  2004/05/06 03:21:07  mwright
 *   Initial revision for POSLog v2.1 merge with top of tree
 *
 *   Revision 1.1.2.2  2004/04/28 11:32:01  mwright
 *   removed dual evaluation of getStatus() call
 *
 *   Revision 1.1.2.1  2004/04/26 22:10:33  mwright
 *   Initial revision for v2.1 - Renamed from LogStoreOpenTransaction
 *   Extended to perform close operations as well
 *   Now uses 360-specific StoreSOD360 element instead of non-extensible ixretail element
 *
 *   Revision 1.2.2.4  2004/04/19 07:40:13  mwright
 *   Changed FinancialCountElement to FinancialCountTenderItemElement
 *
 *   Revision 1.2.2.3  2004/04/13 07:32:16  mwright
 *   Removed tabs
 *
 *   Revision 1.2.2.2  2004/03/21 14:00:19  mwright
 *   Implemented schema type objects to store data prior to building XML
 *
 *   Revision 1.2.2.1  2004/03/17 00:20:41  mwright
 *   Initial revision for POSLog v2.1
 *
 *   Revision 1.1  2004/03/15 09:41:21  mwright
 *   Initial revision for POSLog v2.1
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.transaction.v21;

import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

import oracle.retail.stores.domain.transaction.StoreOpenCloseTransactionIfc;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.ixretail.transaction.LogStoreOpenCloseTransactionIfc;
import oracle.retail.stores.domain.ixretail.financial.LogFinancialTotalsIfc;

import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.FinancialCountElement360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogTCSettleIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.StoreSOD360Ifc;

//--------------------------------------------------------------------------
/**
    This class creates the TLog in IXRetail format for a store open transaction.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogStoreOpenCloseTransaction
extends LogControlTransaction
implements LogStoreOpenCloseTransactionIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";


    protected void createBaseElements()
    throws XMLConversionException
    {
        super.createBaseElements();
        
        StoreOpenCloseTransactionIfc socTransaction     = (StoreOpenCloseTransactionIfc) transaction;
        StoreStatusIfc               storeStatus        = socTransaction.getStoreStatus();
        int                          statusCode         = storeStatus.getStatus();
        
        String                  status            = AbstractFinancialEntityIfc.STATUS_DESCRIPTORS[statusCode];
        FinancialCountIfc       startingSafeCount = socTransaction.getStartingSafeCount(); 
        FinancialCountIfc       endingSafeCount   = socTransaction.getEndingSafeCount(); 
        
        StoreSOD360Ifc storeSod = getSchemaTypesFactory().getStoreSOD360Instance();
        storeSod.setStatus(status);
   
        StoreOpenCloseTransactionIfc openTrx = (StoreOpenCloseTransactionIfc)transaction;
        EYSDate openTime = openTrx.getStoreStatus().getOpenTime();
        if (statusCode == AbstractFinancialEntityIfc.STATUS_OPEN)
        {
            storeSod.setStartDateTimeStamp(dateValue(openTime));
            
            if (startingSafeCount != null)
            {
                FinancialCountElement360Ifc financialCount = getSchemaTypesFactory().getFinancialCountElement360Instance();
                if (createFinancialCountElement(startingSafeCount, financialCount))
                {
                    storeSod.setStartingSafeCount(financialCount);
                }
                
            }
            storeSod.setSignOnOperator(storeStatus.getSignOnOperator().getEmployeeID());
            controlTransactionElement.setStoreSOD360(storeSod);        // this makes the control transaction a store open transaction
        }
        else
        {        
            // the end timestamp is mandatory for store close, but we need the start timestamp for the row being reconstructed.
            // we send the start timestamp in the end element:
            if (openTime == null)
            {
                openTime = transaction.getTimestampBegin();     // TODO Seems getOpentime() returns null on a close
            }
            storeSod.setEndDateTimeStamp(dateValue(openTime));
            if (endingSafeCount != null)
            {
                FinancialCountElement360Ifc financialCount = getSchemaTypesFactory().getFinancialCountElement360Instance();
                if (createFinancialCountElement(endingSafeCount, financialCount))
                {
                    storeSod.setEndingSafeCount(financialCount);
                }
                
            }
            controlTransactionElement.setStoreEOD360(storeSod);        // this makes the control transaction a store close transaction
            storeSod.setSignOffOperator(storeStatus.getSignOffOperator().getEmployeeID());
            
            // add financial count element
            FinancialTotalsIfc totals = socTransaction.getEndOfDayTotals();
            
            if (totals != null)
            {
                LogFinancialTotalsIfc logTotals = IXRetailGateway.getFactory().getLogFinancialTotalsInstance();
                POSLogTCSettleIfc settleElement = getSchemaTypesFactory().getPOSLogTCSettleInstance();
                
                logTotals.createElement(totals, null, settleElement);
                storeSod.setSessionSettle(settleElement);
            }
        }
    }
    
    

}
