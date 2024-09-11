/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved 
 * Rev 1.2 	May 14, 2024			Kamlesh Pant	   Store Credit OTP:
 * Rev 1.1  30 Mar,2017             Nitika             Update the fetch query according to version 14.
 * Rev 1.0  08 Nov, 2016            Nadia              MAX-StoreCredi_Return requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import max.retail.stores.domain.utility.MAXStoreCreditIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.JdbcLookupStoreCredit;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.StoreCreditIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

//-------------------------------------------------------------------------
/**
 * This operation searches for a store credit that has been used for tendering
 * some previous transaction.
 * <P>
 * 
 * @version $Revision: /rgbustores_12.0.9in_branch/1 $
 * 
 **/
// -------------------------------------------------------------------------
public class MAXJdbcLookupStoreCredit extends JdbcLookupStoreCredit

{
	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXJdbcLookupStoreCredit.class);

	// Changes start for Rev 1.0 (Nadia: Store Credit)
	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcLookupStoreCredit.execute()");

		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
		TenderStoreCreditIfc tenderStoreCredit = (TenderStoreCreditIfc) action.getDataObject();

		lookupStoreCredit(connection, tenderStoreCredit);

		dataTransaction.setResult(tenderStoreCredit);

		if (logger.isDebugEnabled())
			logger.debug("JdbcLookupStoreCredit.execute()");
	}
	/*Changes for Rev 1.1 starts */
		public void lookupStoreCredit(JdbcDataConnection dataConnection,
			            TenderStoreCreditIfc tenderStoreCredit)
			throws DataException
			{
					if (logger.isDebugEnabled()) 
						logger.debug( "JdbcLookupStoreCredit.lookupStoreCredit()");
					
					SQLSelectStatement sql = new SQLSelectStatement();
					// Table
					sql.setTable(TABLE_STORE_CREDIT);
					
					// Add columns
					sql.addColumn(FIELD_STORE_CREDIT_ID);
					sql.addColumn(FIELD_STORE_CREDIT_BALANCE);
					sql.addColumn(FIELD_STORE_CREDIT_EXPIRATION_DATE);
					sql.addColumn(FIELD_STORE_CREDIT_STATUS);
					sql.addColumn(FIELD_STORE_CREDIT_PREVIOUS_STATUS);
					sql.addColumn(FIELD_STORE_CREDIT_FIRST_NAME);
					sql.addColumn(FIELD_STORE_CREDIT_LAST_NAME);
					sql.addColumn(FIELD_STORE_CREDIT_BUSINESS_NAME);
					sql.addColumn(FIELD_STORE_CREDIT_TRAINING_FLAG);
					sql.addColumn(FIELD_CURRENCY_ISO_CODE);
					sql.addColumn(FIELD_STORE_CREDIT_ISSUE_DATE);
					sql.addColumn(FIELD_STORE_CREDIT_REDEEM_DATE);
					sql.addColumn(FIELD_STORE_CREDIT_VOID_DATE);
					
					//Change for Rev 1.1: Start	
					sql.addColumn("CUST_MOBILE_NUM");
					//Change for Rev 1.1: Ends	
					// Qualifiers
					sql.addQualifier(FIELD_STORE_CREDIT_ID
					+ " = " + makeSafeString(new String(tenderStoreCredit.getNumber())));
					
					sql.addQualifier(FIELD_STORE_CREDIT_TRAINING_FLAG
					+ " = " + makeStringFromBoolean(tenderStoreCredit.isTrainingMode()));
					
					try
					{
					MAXStoreCreditIfc storeCredit = null;
					dataConnection.execute(sql.getSQLString());
					ResultSet rs = (ResultSet)dataConnection.getResult();
					
					
					while ( rs.next() )
					{
					int index = 0;
					String documentID  = getSafeString(rs, ++index);
					String sAmount     = getSafeString(rs, ++index);
					EYSDate expDate    = dateToEYSDate(rs, ++index);
					String status      = getSafeString(rs, ++index);
					String prevStatus  = getSafeString(rs, ++index);
					String fName       = getSafeString(rs, ++index);
					String lName       = getSafeString(rs, ++index);
					String businessName = getSafeString(rs, ++index);
					boolean isTrainingMode = getBooleanFromString(rs, ++index);
					String isoCode     = getSafeString(rs, ++index);
					EYSDate issueDate  = timestampToEYSDate(rs, ++index);
					EYSDate redeemDate = timestampToEYSDate(rs, ++index);
					EYSDate voidDate   = timestampToEYSDate(rs, ++index);
					//Change for Rev 1.1: Start
					String mobilenumber=getSafeString(rs, ++index);
					//Change for Rev 1.1: Ends
					CurrencyIfc amount = DomainGateway.getCurrencyInstance(isoCode, sAmount);
					storeCredit = (MAXStoreCreditIfc)DomainGateway.getFactory().getStoreCreditInstance();
					storeCredit.setDocumentID(documentID);
					storeCredit.setAmount(amount);
					storeCredit.setExpirationDate(expDate);
					storeCredit.setStatus(status);
					storeCredit.setPreviousStatus(prevStatus);
					storeCredit.setLastName(lName);
					storeCredit.setFirstName(fName);
					storeCredit.setBusinessName(businessName);
					storeCredit.setTrainingMode(isTrainingMode);
					storeCredit.setIssueDate(issueDate);
					storeCredit.setRedeemDate(redeemDate);
					storeCredit.setVoidDate(voidDate);
					storeCredit.setSCmobileNumber(mobilenumber);
					}
					rs.close();
					
					if (storeCredit == null)
					{
					throw new DataException(DataException.NO_DATA, "No Store credit available for " + tenderStoreCredit.getNumber());
					}
					
					tenderStoreCredit.setStoreCredit(storeCredit);
					}
					catch (DataException de) {
						logger.warn("" + de + "");
						throw de;
					} catch (SQLException se) {
						dataConnection.logSQLException(se, "lookupStoreCredit");
						throw new DataException(DataException.SQL_ERROR, "lookupStoreCredit", se);
					} catch (Exception e) {
						throw new DataException(DataException.UNKNOWN, "lookupStoreCredit", e);
					}					
					if (logger.isDebugEnabled()) 
						logger.debug( "JdbcLookupStoreCredit.lookupStoreCredit()");
					}
		/*Changes for Rev 1.1 ends */
	
	// Changes end for Rev 1.0 (Nadia: Store Credit)
	/*public void lookupStoreCredit(JdbcDataConnection dataConnection, TenderStoreCreditIfc storeCredit)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcLookupStoreCredit.lookupStoreCredit()");

		SQLSelectStatement sql = new SQLSelectStatement();
		// Table
		sql.setTable(TABLE_STORE_CREDIT);

		// add column
		sql.addColumn(FIELD_STORE_CREDIT_ID);
		sql.addColumn(FIELD_STORE_CREDIT_EXPIRATION_DATE);
		sql.addColumn(FIELD_STORE_CREDIT_BALANCE);

		// Qualifiers
		sql.addQualifier(FIELD_STORE_CREDIT_ID + " = " + makeSafeString(storeCredit.getStoreCredit().getDocumentID().toString()));

		if (((TenderAlternateCurrencyIfc) storeCredit).getAlternateCurrencyTendered() != null) {

			sql.addQualifier(FIELD_STORE_CREDIT_FOREIGN_FACE_VALUE_AMOUNT + " = "
					+ ((TenderAlternateCurrencyIfc) storeCredit).getAlternateCurrencyTendered());
		}
		
		 * else { sql.addQualifier(FIELD_STORE_CREDIT_BALANCE + " = " +
		 * storeCredit.getAmountTender()); }
		 

		if (!storeCredit.isTrainingMode()) {
			sql.addQualifier(FIELD_STORE_CREDIT_STATUS + " = " + makeSafeString(TenderCertificateIfc.ISSUED));
		}

		sql.addQualifier(
				FIELD_STORE_CREDIT_TRAINING_FLAG + " = " + makeStringFromBoolean(storeCredit.isTrainingMode()));

		try {
			dataConnection.execute(sql.getSQLString());
			ResultSet rs = (ResultSet) dataConnection.getResult();

			if (rs.next()) {
				EYSDate exp_Date = getEYSDateFromString(rs, 2);
				storeCredit.setExpirationDate(exp_Date);
				storeCredit.setAmount(DomainGateway.getBaseCurrencyInstance(rs.getString(3)));
			}

			else if (!rs.next()) {
				throw new DataException(DataException.NO_DATA, "lookupStoreCredit");
			}

			rs.close();
		} catch (DataException de) {
			logger.warn("" + de + "");
			throw de;
		} catch (SQLException se) {
			dataConnection.logSQLException(se, "lookupStoreCredit");
			throw new DataException(DataException.SQL_ERROR, "lookupStoreCredit", se);
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "lookupStoreCredit", e);
		}

		if (logger.isDebugEnabled())
			logger.debug("JdbcLookupStoreCredit.lookupStoreCredit()");
	}
*/
	// ----------------------------------------------------------------------
	/**
	 * Adds a abs function to the column name or value.
	 * <p>
	 * 
	 * @param str
	 *            the column name
	 * @return abs function with the string
	 **/
	// ----------------------------------------------------------------------
	protected String addAbsFunction(String str) {
		return "abs(" + str + ")";
	}

	// -------------------------------------------------------------------
	/**
	 * Returns a string representation of this object.
	 * <P>
	 * 
	 * @param none
	 * @return String representation of object
	 **/
	// ----------------------------------------------------------------------
	public String toString() {

		// result string
		String strResult = new String(
				"Class:  JdbcLookupStoreCredit (Revision " + getRevisionNumber() + ")" + hashCode());

		// pass back result
		return (strResult);
	}

	// ----------------------------------------------------------------------
	/**
	 * Returns the revision number of the class.
	 * <P>
	 * 
	 * @param none
	 * @return String representation of revision number
	 **/
	// ----------------------------------------------------------------------
	public String getRevisionNumber() {

		// return string
		return (revisionNumber);
	}

}
