/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  
Rev 1.1   Hitesh Dua		27/July/2017	EJ's  are getting saved with VAT as tax type In EJ VAT Taxable amount is wrong
Rev 1.0   Rahul		16/April/2014	Changes done for Business Date Mismatch Alert Prompt
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.arts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import oracle.retail.stores.common.data.JdbcUtilities;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.journal.JournalableIfc;
import oracle.retail.stores.foundation.manager.journal.JournalEntry;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;
import oracle.retail.stores.persistence.utility.DatabaseBlobHelperFactory;
import oracle.retail.stores.persistence.utility.DatabaseBlobHelperIfc;
import oracle.retail.stores.common.utility.Util;

//--------------------------------------------------------------------------
/**
 * This operation inserts a EJournal entry into the database.
 * <P>
 * 
 * @version $Revision: 7$
 **/
// --------------------------------------------------------------------------
public class MAXJdbcSaveJournalEntry extends JdbcDataOperation implements ARTSDatabaseIfc, CodeConstantsIfc {
	/**
	 * The logger to which log messages will be sent.
	 **/
	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXJdbcSaveJournalEntry.class);

	private static final String[] journalTypes = { "UNKNOWN", // JournalableIfc.ENTRY_TYPE_UNKNOWN
			"TRANSACTION", // JournalableIfc.ENTRY_TYPE_START
			"TRANSACTION", // JournalableIfc.ENTRY_TYPE_END
			"TRANSACTION", // JournalableIfc.ENTRY_TYPE_TRANS
			"NOT TRANS", // JournalableIfc.ENTRY_TYPE_NOTTRANS
			"TRANSACTION" // JournalableIfc.ENTRY_TYPE_CUST
	};

	private static final int COMPLETION_FLAG = 1;

	// ---------------------------------------------------------------------
	/**
	 * Class constructor.
	 * <P>
	 **/
	JournalManagerIfc jmi = (JournalManagerIfc) Gateway.getDispatcher().getManager(JournalManagerIfc.TYPE);

	// ---------------------------------------------------------------------
	public MAXJdbcSaveJournalEntry() {
		super();
		setName("MAXJdbcSaveJournalEntry");
	}

	// ---------------------------------------------------------------------
	/**
	 * Executes the SQL statements against the database.
	 * <P>
	 * 
	 * @param dataTransaction
	 *            The data transaction
	 * @param dataConnection
	 *            The connection to the data source
	 * @param action
	 *            The information passed by the valet
	 * @exception DataException
	 *                upon error
	 **/
	// ---------------------------------------------------------------------
	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {

		if (logger.isDebugEnabled())
			logger.debug("JdbcSaveJournalEntry.execute");

		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
		JournalEntry journalEntry = (JournalEntry) action.getDataObject();
		generateSoftCopyRTF(connection, journalEntry);
		saveJournalEntry(connection, journalEntry);

		if (logger.isDebugEnabled())
			logger.debug("JdbcSaveJournalEntry.execute");
	}

	// ---------------------------------------------------------------------
	/**
	 * Saves the ejournal entry to the database.
	 * <p>
	 * 
	 * @param dataConnection
	 *            The connection to the data source
	 * @param journalEntry
	 *            The journal entry to save
	 * @exception DataException
	 *                upon error
	 **/
	// ---------------------------------------------------------------------
	public void saveJournalEntry(JdbcDataConnection dataConnection, JournalEntry journalEntry) throws DataException {

		SQLInsertStatement sql = new SQLInsertStatement();

		sql.setTable(TABLE_EJOURNAL);

		String storeID = journalEntry.getStoreNumber();
		if (storeID == null || storeID.equals("")) {
			storeID = " ";
		}
		sql.addColumn(FIELD_RETAIL_STORE_ID, inQuotes(storeID));

		String workstationId = journalEntry.getWorkstationID();
		if (workstationId == null || workstationId.equals("")) {
			workstationId = " ";
		}
		sql.addColumn(FIELD_WORKSTATION_ID, inQuotes(workstationId));

		sql.addColumn(FIELD_OPERATOR_ID, makeSafeString(journalEntry.getUser()));
		sql.addColumn(FIELD_EMPLOYEE_ID, makeSafeString(journalEntry.getSalesAssociateID()));

		EYSDate businessDate = new EYSDate(new Date(journalEntry.getBusinessDayDate()));
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(businessDate));

		int journalType = journalEntry.getEntryType();

		String transSeqNo = extractTransactionNumber(journalEntry);
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, transSeqNo);

		EYSDate bDate = new EYSDate(new Date(journalEntry.getTimeStamp()));
		sql.addColumn(FIELD_EJOURNAL_ENTRY_BEGIN_TIMESTAMP, dateToSQLTimestampFunction(bDate));

		EYSDate eDate = new EYSDate(new Date(journalEntry.getEndTimeStamp()));
		sql.addColumn(FIELD_EJOURNAL_ENTRY_END_TIMESTAMP, dateToSQLTimestampFunction(eDate));

		sql.addColumn(FIELD_EJOURNAL_JOURNAL_TYPE, makeSafeString(journalTypes[journalType]));

		sql.addColumn(FIELD_EJOURNAL_FLAG_NORMAL_COMPLETION, COMPLETION_FLAG);

		try {
			dataConnection.execute(sql.getSQLString());

			// resave the blob because 1st time doesn't work past 4000 bytes.
			HashMap map = new HashMap();
			map.put(FIELD_RETAIL_STORE_ID, storeID);
			map.put(FIELD_WORKSTATION_ID, workstationId);
			SimpleDateFormat format = new SimpleDateFormat(JdbcUtilities.YYYYMMDD_DATE_FORMAT_STRING);
			String dateSQL = format.format(businessDate.dateValue());
			map.put(FIELD_BUSINESS_DAY_DATE, dateSQL);
			map.put(FIELD_TRANSACTION_SEQUENCE_NUMBER, new Integer(transSeqNo));
			map.put(FIELD_EJOURNAL_ENTRY_BEGIN_TIMESTAMP, bDate.dateValue());
			/*DatabaseBlobHelper helper = (DatabaseBlobHelper) DatabaseBlobHelperFactory.getInstance()
					.getDatabaseBlobHelper(dataConnection.getConnection());
*/
			DatabaseBlobHelperFactory factory = DatabaseBlobHelperFactory.getInstance();
			DatabaseBlobHelperIfc helper = factory.getDatabaseBlobHelper(dataConnection.getConnection());
			
			if (helper != null) {
				//changes for rev 1.1
				helper.updateClob(dataConnection.getConnection(), TABLE_EJOURNAL, FIELD_EJOURNAL_JOURNAL_ENTRY,
						journalEntryText(journalEntry.getText()), map);
			}
		} catch (DataException de) {
			logger.error("" + de + "");
			throw de;
		} catch (SQLException se) {
			throw new DataException(DataException.SQL_ERROR, "saveJournalEntry", se);
		}

		catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "saveJournalEntry", e);
		}

	}

	//added for rev 1.1
	private String journalEntryText(String text) {

		String[] splitText=text.split("\n");
		String jText="";
		for(String str:splitText){
			
				jText=jText.concat(str)+"\n";
		}
		
		return jText;
	}

	/**
	 * Extract the transaction number from the journal entry and the sequence
	 * number format.
	 * <p>
	 *
	 * @param tn
	 *            The sequence number
	 * @param journalEntry
	 *            The journal entry to save
	 * @return
	 */
	private String extractTransactionNumber(JournalEntry je) {
		// caution: this method assumes an order in the sequence number
		int journalType = je.getEntryType();
		if (journalType == JournalableIfc.ENTRY_TYPE_NOTTRANS || (je.getTransaction() == null)
				|| (journalType == JournalableIfc.ENTRY_TYPE_TRANS) || (journalType == JournalableIfc.ENTRY_TYPE_CUST)) {
			return "0";
		} 
			String returnValue = je.getTransaction();
			 if (!Util.isEmpty(returnValue))
			{
			int a = Integer.parseInt(DomainGateway.getProperty("TransactionIDStoreIDLength"));
			int b = Integer.parseInt(DomainGateway.getProperty("TransactionIDWorkstationIDLength"));
			int c = Integer.parseInt(DomainGateway.getProperty("TransactionIDSequenceNumberLength"));
				int begin = (a + b);
				int end = (a + b + c);
				if(returnValue.length() >= end){
					return returnValue.substring(begin, end);
				}
			// Save only the transaction number portion of the "transaction ID"
			// in the
			// transaction_sequence_number field. But if this journal entry is
			// not part of a
			// transaction (ENTRY_TYPE_NOTTRANS), save 0 as the transaction
			// number.
				logger.warn("Unable to determine transaction sequence number from journal.");
		}
			return "0";
	}

	public void generateSoftCopyRTF(JdbcDataConnection dataConnection, JournalEntry journalEntry) {
		try {
			String strTxnID = journalEntry.getTransaction();
			// Retrieving data from properties file
			int journalType = journalEntry.getEntryType();
			EYSDate businessDate = new EYSDate(new Date(journalEntry.getBusinessDayDate()));			 
			if (journalType == JournalableIfc.ENTRY_TYPE_END) {
				String strReceiptDirectory = new String(
						Gateway.getProperty("application", "EJournalSoftCopyDirectory", "true"));
				File ReceiptDirectory = new File(strReceiptDirectory);
				if (!ReceiptDirectory.exists()) {
					ReceiptDirectory.mkdir();
				}
				String strFileExtension = ".pdf";
				SimpleDateFormat format = new SimpleDateFormat(JdbcUtilities.YYYYMMDD_DATE_FORMAT_STRING);
				String fileNameDate = format.format(businessDate.dateValue());
				String strPath = strReceiptDirectory.concat(strTxnID).concat("_").concat(fileNameDate).concat(strFileExtension);

				try {
					Document d = new Document();
					PdfWriter writer = null;
					try {
						File strFile = new File(strPath);
						if (strFile.exists()) {
							writer = PdfWriter.getInstance(d,
									new FileOutputStream(strPath + "_" + journalEntry.getTimestamp()));

						} else {
							writer = PdfWriter.getInstance(d, new FileOutputStream(strPath));
						}
					} catch (DocumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					d.open();

					try {
						Paragraph paragraph = new Paragraph();
						paragraph.add(journalEntry.getText());
						paragraph.setAlignment(Paragraph.ALIGN_JUSTIFIED);
						d.add(paragraph);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					d.close();
				} catch (IOException de) {
					String message = "Unable to open the File--- :";
					logger.error(message, de);
				}

			}
		} catch (Exception de) {
			String message = "There is Some Error in File Path:";
			logger.error(message, de);

		}

	}
}
