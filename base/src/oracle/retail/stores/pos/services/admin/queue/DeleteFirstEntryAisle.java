/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/queue/DeleteFirstEntryAisle.java /main/16 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/30/12 - get journalmanager from bus
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    04/05/10 - use default locale for currency and date/time
 *                         display
 *    abondala  01/03/10 - update header date
 *    nganesh   03/12/09 - Externalized Delete first transaction entry
 *
 * ===========================================================================
 * $Log:
 3    360Commerce 1.2         3/31/2005 4:27:43 PM   Robert Pearse
 2    360Commerce 1.1         3/10/2005 10:20:53 AM  Robert Pearse
 1    360Commerce 1.0         2/11/2005 12:10:33 PM  Robert Pearse
 *
 Revision 1.4  2004/02/12 16:48:52  mcs
 Forcing head revision
 *
 Revision 1.3  2004/02/11 23:22:58  bwf
 @scr 0 Organize imports.
 *
 Revision 1.2  2004/02/11 21:35:20  rhafernik
 @scr 0 Log4J conversion and code cleanup
 *
 Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Oct 02 2003 10:06:32   bwf
 * Removed deprecation because flow has been reinstated.  Also removed unused imports.
 *
 *    Rev 1.1   Sep 25 2003 12:25:16   bwf
 * Deprecated.
 * Resolution for 3334: Feature Enhancement:  Queue Exception Handling
 *
 *    Rev 1.0   Aug 29 2003 15:53:14   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:38:28   msg
 * Initial revision.
 *
 *    Rev 1.2   20 Mar 2002 14:23:54   epd
 * corrected E-journal message
 * Resolution for POS SCR-820: Delete 1st Queue EJ Entry has text errors not to spec
 *
 *    Rev 1.1   Mar 18 2002 23:06:16   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:20:26   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:12:32   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:11:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.queue;

import java.text.DateFormat;
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.TransactionWriteDataTransaction;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.DataManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionQueueIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * This Aisle performs all the steps required to delete the first entry in the
 * queue.
 * 
 * @version $Revision: /main/16 $
 */
@SuppressWarnings("serial")
public class DeleteFirstEntryAisle extends PosLaneActionAdapter
{
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/16 $";

    /**
     * format of date and time
     */
    protected static String DATE_TIME_FORMAT = "MM/dd/yy HH:mm";

    /**
     * This Aisle performs all the steps required to delete the first entry in
     * the queue.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void traverse(BusIfc bus)
    {
		// Initialize report string.
		StringBuilder report = new StringBuilder();
		report.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
				JournalConstantsIfc.DELETE_1ST_QUEUE_LABEL, null));
		report.append(Util.EOL);
		report.append(Util.EOL);

		// Get the data manager and dialog model
		DataManagerIfc dm = (DataManagerIfc) Gateway.getDispatcher()
				.getManager(DataManagerIfc.TYPE);
		DialogBeanModel model = new DialogBeanModel();

		// Get and test the queue names
		String[] qNames = dm.getTransactionQueues();
		if (qNames == null) {
			// Report the error
			logger.error("ClearQueueAisle: There is no queue to clear.");
			model.setResourceID("DeleteQueueError");
			model.setType(DialogScreensIfc.ERROR);
		} else if (qNames.length > 1) {
			// Report the error
			logger
					.error(bus.getServiceName()
							+ " ClearQueueAisle: There is more than 1 queue; don't know which one to delete from.");
			model.setResourceID("DeleteQueueError");
			model.setType(DialogScreensIfc.ERROR);
		} else {
			try {
				// Get the first transaction and remove it from the queue.
				DataTransactionQueueIfc queue = dm
						.getTransactionQueue(qNames[0]);
				DataTransactionIfc dti = queue.getHeadTransaction();

				// Build the journal report.
				if (dti != null) {
					report.append(getFormattedJournalLine(dti));
					queue.removeHeadTransaction();
				} else {
					report.append(I18NHelper.getString(
							I18NConstantsIfc.EJOURNAL_TYPE,
							JournalConstantsIfc.QUEUE_IS_EMPTY_LABEL, null));

				}

				// Set up the dialog model
				model.setResourceID("AcknowledgeDeleteEntry");
				model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
			} catch (DataException de) {
				// Report the error
				logger
						.error("ClearQueueAisle: " + de.getErrorCodeString()
								+ "");
				model.setResourceID("DeleteQueueError");
				model.setType(DialogScreensIfc.ERROR);
			}
		}

		// display dialog
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);

		// Journal the entry.
		JournalManagerIfc jmi = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        if (jmi != null)
        {
            // Fix this when interface has been fixed.
            jmi.journal("", null, report.toString());
        }
        else
        {
            logger.warn("No journal manager found!");
        }
    }

	private String getFormattedJournalLine(DataTransactionIfc dTran)
	{
       StringBuilder buff = new StringBuilder();

       UtilityManagerIfc utilityObj = (UtilityManagerIfc)Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
   	   Locale journalLocale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);

   	    String transactionId = null;
   	    String transactionType = null;
   	    String transactionDate = null;
        if (dTran instanceof TransactionWriteDataTransaction)
        {
            TransactionWriteDataTransaction transaction = (TransactionWriteDataTransaction)dTran;
            TransactionIfc tran = transaction.getARTSTransaction().getPosTransaction();

            transactionId = tran.getTransactionID();
            transactionType = utilityObj.retrieveCommonText(
                    TransactionIfc.TYPE_DESCRIPTORS[tran.getTransactionType()],
                    TransactionIfc.TYPE_DESCRIPTORS[tran.getTransactionType()], journalLocale);

            transactionDate = tran.getTimestampBegin()
            .toFormattedString(DateFormat.SHORT, DateFormat.SHORT, LocaleMap.getLocale(LocaleMap.DEFAULT));

        }
        else
        {
            EYSDate timeStamp = DomainGateway.getFactory().getEYSDateInstance();

            transactionId = "";

            transactionType = dTran.getTransactionName();

            transactionDate = timeStamp.toFormattedString(DATE_TIME_FORMAT);
        }
        Object[] transDataArgs = new Object[] { transactionId };
        buff.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,JournalConstantsIfc.TRANSACTION_NUMBER_LABEL, transDataArgs,journalLocale));
        buff.append("\n");


        Object[] dataArgs = new Object[] { transactionType };
        buff.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,JournalConstantsIfc.TYPE_LABEL, dataArgs,journalLocale));
        buff.append("\n");


        dataArgs = new Object[] { transactionDate };
        buff.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,JournalConstantsIfc.CLEAR_QUEUE_DATETIME, dataArgs,journalLocale));


        return buff.toString();
	}
}
