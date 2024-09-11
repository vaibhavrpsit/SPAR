/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/startofday/StoreStatusUpdateSite.java /main/13 2014/07/23 15:44:29 rhaight Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rhaight   07/03/14 - store offline open revisions
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    atirkey   01/12/09 - Removed condition to enable logging of store open
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce1.3         3/30/2007 4:49:51 AM   Michael Boyd    CR
 *         26172 - v8x merge to trunk
 *
 *         4    .v8x      1.2.1.0     3/2/2007 1:04:19 PM    Maisa De Camargo
 *         Added
 *         code to check if the store is already opened before performing a
 *         "Open Store" Transaction.
 *    3    360Commerce1.2         4/1/2005 3:00:14 AM    Robert Pearse   
 *    2    360Commerce1.1         3/10/2005 9:55:37 PM   Robert Pearse   
 *    1    360Commerce1.0         2/11/2005 11:44:32 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/03 23:15:06  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:53  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:57:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   May 23 2003 06:53:40   jgs
 * Modified to delay the end of transaction journal entry.
 * Resolution for 2543: Modify EJournal to put entries into a JMS Queue on the store server.
 * 
 *    Rev 1.2   Sep 03 2002 16:03:42   baa
 * externalize domain  constants and parameter values
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   May 09 2002 08:37:24   mpm
 * Modified to support new store open/close transaction.
 * Resolution for POS SCR-1630: Make changes to support TLog facility.
 *
 *    Rev 1.0   Apr 29 2002 15:29:28   msg
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.startofday;

import java.util.Hashtable;

import oracle.retail.stores.commerceservices.audit.AuditLoggerConstants;
import oracle.retail.stores.commerceservices.audit.AuditLoggerServiceIfc;
import oracle.retail.stores.commerceservices.audit.AuditLoggingUtils;
import oracle.retail.stores.commerceservices.audit.event.AuditLogEventEnum;
import oracle.retail.stores.commerceservices.audit.event.StoreOpsEvent;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.StoreDataTransaction;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.StoreOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CheckTrainingReentryMode;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * This site sets the store status to open, sets the business date. The arrive()
 * can mail either a success or failure letter.
 * 
 * @version $Revision: /main/13 $
 */
@SuppressWarnings("serial")
public class StoreStatusUpdateSite extends PosSiteActionAdapter
{
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    /**
     * Updates store status in database.
     * 
     * @param bus the bus arriving at this site
     */
    public void arrive(BusIfc bus)
    {
        String letterName = CommonLetterIfc.SUCCESS;

        // get cargo reference
        StartOfDayCargo cargo = (StartOfDayCargo) bus.getCargo();

        // clone store status (reset cargo if update succeeds)
        StoreStatusIfc originalStoreStatus = (StoreStatusIfc) cargo.getStoreStatus().clone();
        
        // for AuditLogging
        AuditLoggerServiceIfc auditService = AuditLoggingUtils.getAuditLogger();
		StoreOpsEvent ev = (StoreOpsEvent)AuditLoggingUtils.createLogEvent(StoreOpsEvent.class, AuditLogEventEnum.START_OF_DAY);
		
        if (isStoreAlreadyOpened(originalStoreStatus))
        {
        	letterName = "OpenStoreError";
        }
        else
        {
	        StoreStatusIfc ss = (StoreStatusIfc) cargo.getStoreStatus().clone();
	        ss.setStatus(AbstractFinancialEntityIfc.STATUS_OPEN);
	        ss.setBusinessDate(cargo.getInputBusinessDate());
	
	        // set operator to this register's operator
	        ss.setSignOnOperator(cargo.getOperator());
	        ss.setOpenTime();
	        cargo.setStoreStatus(ss);
	
	        try
	        {                               // begin update store status try block
	            // initialize store open/close transaction
	            TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
	            StoreOpenCloseTransactionIfc transaction =
	              DomainGateway.getFactory().getStoreOpenCloseTransactionInstance();
	            transaction.setTransactionType(TransactionIfc.TYPE_OPEN_STORE);
	            utility.initializeTransaction(transaction, -1);
	            transaction.setTimestampEnd();
	            transaction.setTransactionStatus(TransactionIfc.STATUS_COMPLETED);
	            
	            // set the offline processing flag
	            transaction.setStoreOpenMode(cargo.getStoreOpenMode());
	            
	            // set store totals and count
	            transaction.setStoreStatus(ss);
	            
	                transaction.setStartingSafeCount
	                  (cargo.getSafeTotals().getStartingSafeCount().getEntered());
	            
	
	            // The end of day processing adds more information to the journal
	            // after the transaction has been completed.  Setting this flag
	            // to false allows a follow on site to finish up the journalling
	            // for this transaction.
	            utility.saveTransaction(transaction, false);
	            
	            // for Auditlogging
	    		ev.setUserId(transaction.getCashier().getLoginID());
	    		ev.setStoreId(transaction.getFormattedStoreID());
	            ev.setBusinessDate(transaction.getBusinessDay().dateValue());
	            ev.setStoreStatus(AuditLoggerConstants.STORE_STATUS_OPEN);
	            if(transaction.getTransactionIdentifier()!=null)
	            {
	            	ev.setRegisterID(transaction.getTransactionIdentifier().getWorkstationID());
	            }
	            if (cargo.getSafeCountType() == FinancialCountIfc.COUNT_TYPE_NONE)
	            {
		            ev.setOperatingFundAmount(AuditLoggerConstants.OPERATING_FUND_AMOUNT_EXPECTED);
	            }
	            else 
	            {
	            	ev.setOperatingFundAmount(cargo.getSafeTotals().getStartingSafeCount().getEntered().getAmount().getStringValue());
            		if(cargo.getSafeCountType() == FinancialCountIfc.COUNT_TYPE_DETAIL)
            		{
            			Hashtable denominations = new Hashtable();
            			FinancialCountTenderItemIfc[] fcti = cargo.getSafeTotals().getStartingSafeCount().getEntered().getTenderItems();
            			for(int i=0;i<fcti.length;i++)
            			{
            				denominations.put(fcti[i].getDescription(),String.valueOf(fcti[i].getNumberItemsIn()));
            			}
                        // Remove from hashmap the entry for total because
            			// the total amount is logged as a seperate field 
                        denominations.remove(DomainGateway.getFactory()
                                     .getTenderTypeMapInstance()
                                     .getDescriptor(TenderLineItemIfc.TENDER_TYPE_CASH));
            			ev.addDenomination(denominations,null);
            		}
	            }
            	ev.setEventOriginator("LookupStoreStatusSite.arrive");
            		
				if (!CheckTrainingReentryMode.isTrainingRetryOn(cargo
						.getRegister())) 
				{
					auditService.logStatusSuccess(ev);
				}
            
	            // set the status in the cargo to the record that was found
	            cargo.setCommitFlag(true);
	            cargo.setTransaction(transaction);
	
	        }                               // end update store status try block
	        // catch problems on the update
	        catch (DataException e)
	        {
	        	// for Auditlogging
	            if(CheckTrainingReentryMode.isTrainingRetryOn(cargo.getRegister()))
	            {
	            	auditService.logStatusFailure(ev);
	            }
	        	
	        	// set error code
	            cargo.setDataExceptionErrorCode(e.getErrorCode());
	            // check for REFERENTIAL_INTEGRITY exception, which indicates store
	            // was already opened
	            if (e.getErrorCode() == DataException.REFERENTIAL_INTEGRITY_ERROR)
	            {
	                letterName = "OpenStoreError";
	                // undo cargo updates
	                cargo.setStoreStatus(originalStoreStatus);
	                cargo.setCommitFlag(false);
	            }
	            // if another error, handle it
	            else
	            {
	                // set error code
	                cargo.setDataExceptionErrorCode(e.getErrorCode());
	                logger.error( "" + e + "");
	                letterName = CommonLetterIfc.FAILURE;
	            }
	        }
        }
        
        // mail appropriate letter
        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }

    /**
     * Queries the database to double check if the store is already opened
     */
    boolean isStoreAlreadyOpened (StoreStatusIfc originalStoreStatus)
    {
    	boolean isStoreAlreadyOpened = false;
    	
    	StoreDataTransaction dt = null;
    	StoreStatusIfc retrievedStoreStatus = null;
    	dt = (StoreDataTransaction) DataTransactionFactory.create(DataTransactionKeys.STORE_DATA_TRANSACTION);
    	try {
			retrievedStoreStatus = dt.readStoreStatus(originalStoreStatus.getStore().getStoreID());
			isStoreAlreadyOpened = (retrievedStoreStatus.getStatus() == AbstractStatusEntityIfc.STATUS_OPEN);
		} catch (DataException e) {
			// We may be offline, return the original Store Status
			isStoreAlreadyOpened = (originalStoreStatus.getStatus() == AbstractStatusEntityIfc.STATUS_OPEN);
		}
		return isStoreAlreadyOpened;
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
