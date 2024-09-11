/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
*  
*		Rev 1.1  11/July/2013   Prateek			 Changes done for Offline Scenarios where till is reconcile from POS and BO both
*       Rev 1.0  27/May/2013	Tanmaya Kamal	 Initial Draft: Coupon Till History
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.JdbcSaveTill;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.Till;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderLineItemConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;

import org.apache.log4j.Logger;

public abstract class MAXJdbcSaveTill extends JdbcSaveTill implements MAXARTSDatabaseIfc {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8314525702172372351L;
	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXJdbcSaveTill.class);

	public boolean insertTillTenderHistory(JdbcDataConnection dataConnection, FinancialCountTenderItemIfc tenderItem,
			TillIfc till, RegisterIfc register, ReconcilableCountIfc loans, ReconcilableCountIfc pickups,
			ReconcilableCountIfc payIns, ReconcilableCountIfc payOuts) throws DataException {
		boolean returnCode = true;
		String tenderType = tenderTypeMap.getCode(tenderItem.getTenderType());
		String tenderSubType = tenderItem.getTenderSubType();
		String tenderDesc = tenderItem.getDescription();
		TenderDescriptorIfc tenderDescriptor = tenderItem.getTenderDescriptor();

		SQLInsertStatement sql = new SQLInsertStatement();
		isUpdateStatement = false;

		/*
		 * Define table
		 */
		sql.setTable(TABLE_TILL_TENDER_HISTORY);
		/*
		 * Add columns and their values
		 */
		sql.addColumn(FIELD_TENDER_REPOSITORY_ID, getTillID(till));
		sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(register));
		sql.addColumn(FIELD_TILL_START_DATE_TIMESTAMP, getStartTimestamp(till));
		sql.addColumn(FIELD_TENDER_TYPE_CODE, inQuotes(tenderType));
		sql.addColumn(FIELD_TENDER_SUBTYPE, inQuotes(emptyStringToSpaceString(tenderSubType)));
		sql.addColumn(FIELD_CURRENCY_ISSUING_COUNTRY_CODE, inQuotes(tenderItem.getCurrencyCode()));
		sql.addColumn(FIELD_CURRENCY_ID, tenderItem.getCurrencyID());
		sql.addColumn(FIELD_TILL_TENDER_DEPOSIT_TOTAL_AMOUNT, getTenderDepositAmount(till, tenderDesc));
		sql.addColumn(FIELD_TILL_TENDER_LOAN_MEDIA_TOTAL_AMOUNT, getTenderLoanAmount(loans, tenderDescriptor));

		sql.addColumn(FIELD_TILL_TENDER_OVER_TOTAL_AMOUNT, getTenderOverAmount(till, tenderDescriptor));
		sql.addColumn(FIELD_TILL_TENDER_PICKUP_MEDIA_TOTAL_AMOUNT, getTenderPickupAmount(pickups, tenderDescriptor));
		sql.addColumn(FIELD_TILL_TENDER_SHORT_TOTAL_AMOUNT, getTenderShortAmount(till, tenderDescriptor));
		sql.addColumn(FIELD_TILL_TOTAL_BEGINNING_TENDER_MEDIA_UNIT_COUNT,
				getTenderBeginningCount(till, tenderDescriptor));
		sql.addColumn(FIELD_TILL_TOTAL_TENDER_DEPOSIT_MEDIA_UNIT_COUNT, getTenderDepositCount(till, tenderDesc));
		sql.addColumn(FIELD_TILL_TOTAL_TENDER_LOAN_MEDIA_UNIT_COUNT, getTenderLoanCount(loans, tenderDescriptor));
		sql.addColumn(FIELD_TILL_TOTAL_TENDER_MEDIA_COUNT, getTenderCount(till, tenderDescriptor));
		sql.addColumn(FIELD_TILL_TOTAL_TENDER_MEDIA_OVER_COUNT, getTenderOverCount(till, tenderDescriptor));
		sql.addColumn(FIELD_TILL_TOTAL_TENDER_MEDIA_SHORT_COUNT, getTenderShortCount(till, tenderDescriptor));
		sql.addColumn(FIELD_TILL_TOTAL_TENDER_PICKUP_MEDIA_UNIT_COUNT, getTenderPickupCount(pickups, tenderDescriptor));
		sql.addColumn(FIELD_TILL_TOTAL_TENDER_REFUND_MEDIA_UNIT_COUNT, getTenderRefundCount(till, tenderDescriptor));
		sql.addColumn(FIELD_TILL_TENDER_REFUND_TOTAL_AMOUNT, getTenderRefundAmount(till, tenderDescriptor));
		sql.addColumn(FIELD_TILL_TENDER_TOTAL_AMOUNT, getTenderTotalAmount(till, tenderDescriptor));
		sql.addColumn(FIELD_TILL_TENDER_OPEN_AMOUNT, getTenderOpenAmount(till, tenderDescriptor));
		sql.addColumn(FIELD_TILL_TENDER_CLOSE_AMOUNT, getTenderCloseAmount(till, tenderDescriptor));
		sql.addColumn(FIELD_TILL_TENDER_MEDIA_CLOSE_COUNT, getTenderCloseCount(till, tenderDescriptor));
		sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_IN_MEDIA_TOTAL_AMOUNT, getTillPayInAmount(payIns, tenderDescriptor));
		sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_OUT_MEDIA_TOTAL_AMOUNT, getTillPayOutAmount(payOuts, tenderDescriptor));
		sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_IN_MEDIA_UNIT_COUNT, getTillPayInCount(payIns, tenderDescriptor));
		sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_OUT_MEDIA_UNIT_COUNT, getTillPayOutCount(payOuts, tenderDescriptor));
		sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
		if (tenderDescriptor.getTenderType() == TenderLineItemConstantsIfc.TENDER_TYPE_STORE_CREDIT)
			sql.addColumn(MAX_IN_AMT, getTenderStoreCreditAmount(till, tenderDescriptor));
		else
			sql.addColumn(MAX_IN_AMT, getTenderShortAmount(till, tenderDescriptor));
		// sql.addColumn(MAX_IN_AMT, getTenderShortAmount(till,
		// tenderDescriptor));
		sql.addColumn(MAX_OUT_AMT, getTenderRefundAmount(till, tenderDescriptor));

		dataConnection.execute(sql.getSQLString());

		if (0 >= dataConnection.getUpdateCount()) {
			returnCode = false;
		}

		return (returnCode);
	}

	public boolean updateTillTenderHistory(JdbcDataConnection dataConnection, FinancialCountTenderItemIfc tenderItem,
			TillIfc till, RegisterIfc register, ReconcilableCountIfc loans, ReconcilableCountIfc pickups,
			ReconcilableCountIfc payIns, ReconcilableCountIfc payOuts) throws DataException {
		boolean returnCode = true;
		String tenderType = tenderTypeMap.getCode(tenderItem.getTenderType());
		String tenderSubType = tenderItem.getTenderSubType();
		String tenderDesc = tenderItem.getDescription();
		TenderDescriptorIfc tenderDescriptor = tenderItem.getTenderDescriptor();
		/** MAX Rev 1.1 Change : Start **/
		boolean isReconcile = isTillReconcileEarlier(dataConnection, till);
		/** MAX Rev 1.1 Change : End **/

		int i = 0;
		SQLUpdateStatement sql = new SQLUpdateStatement();
		isUpdateStatement = true;

		/*
		 * Define table
		 */
		sql.setTable(TABLE_TILL_TENDER_HISTORY);
		/*
		 * Add columns and their values
		 */
		sql.addColumn(FIELD_TILL_TENDER_DEPOSIT_TOTAL_AMOUNT, getTenderDepositAmount(till, tenderDesc));
		sql.addColumn(FIELD_TILL_TENDER_LOAN_MEDIA_TOTAL_AMOUNT, getTenderLoanAmount(loans, tenderDescriptor));
		/** MAX Rev 1.1 Change : Start **/
		if (!isReconcile)
			sql.addColumn(FIELD_TILL_TENDER_OVER_TOTAL_AMOUNT, getTenderOverAmount(till, tenderDescriptor));
		/** MAX Rev 1.1 Change : End **/
		sql.addColumn(FIELD_TILL_TENDER_PICKUP_MEDIA_TOTAL_AMOUNT, getTenderPickupAmount(pickups, tenderDescriptor));
		sql.addColumn(FIELD_TILL_TENDER_SHORT_TOTAL_AMOUNT, getTenderShortAmount(till, tenderDescriptor));
		sql.addColumn(FIELD_TILL_TOTAL_BEGINNING_TENDER_MEDIA_UNIT_COUNT,
				getTenderBeginningCount(till, tenderDescriptor));
		sql.addColumn(FIELD_TILL_TOTAL_TENDER_DEPOSIT_MEDIA_UNIT_COUNT, getTenderDepositCount(till, tenderDesc));
		sql.addColumn(FIELD_TILL_TOTAL_TENDER_LOAN_MEDIA_UNIT_COUNT, getTenderLoanCount(loans, tenderDescriptor));
		sql.addColumn(FIELD_TILL_TOTAL_TENDER_MEDIA_COUNT, getTenderCount(till, tenderDescriptor));
		sql.addColumn(FIELD_TILL_TOTAL_TENDER_MEDIA_OVER_COUNT, getTenderOverCount(till, tenderDescriptor));
		sql.addColumn(FIELD_TILL_TOTAL_TENDER_MEDIA_SHORT_COUNT, getTenderShortCount(till, tenderDescriptor));
		sql.addColumn(FIELD_TILL_TOTAL_TENDER_PICKUP_MEDIA_UNIT_COUNT, getTenderPickupCount(pickups, tenderDescriptor));
		sql.addColumn(FIELD_TILL_TOTAL_TENDER_REFUND_MEDIA_UNIT_COUNT, getTenderRefundCount(till, tenderDescriptor));
		sql.addColumn(FIELD_TILL_TENDER_REFUND_TOTAL_AMOUNT, getTenderRefundAmount(till, tenderDescriptor));
		sql.addColumn(FIELD_TILL_TENDER_TOTAL_AMOUNT, getTenderTotalAmount(till, tenderDescriptor));
		sql.addColumn(FIELD_TILL_TENDER_OPEN_AMOUNT, getTenderOpenAmount(till, tenderDescriptor));
		/** MAX Rev 1.1 Change : Start **/
		if (!isReconcile)
			sql.addColumn(FIELD_TILL_TENDER_CLOSE_AMOUNT, getTenderCloseAmount(till, tenderDescriptor));
		if (!isReconcile)
			sql.addColumn(FIELD_TILL_TENDER_MEDIA_CLOSE_COUNT, getTenderCloseCount(till, tenderDescriptor));
		/** MAX Rev 1.1 Change : End **/
		sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_IN_MEDIA_TOTAL_AMOUNT, getTillPayInAmount(payIns, tenderDescriptor));
		sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_OUT_MEDIA_TOTAL_AMOUNT, getTillPayOutAmount(payOuts, tenderDescriptor));
		sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_IN_MEDIA_UNIT_COUNT, getTillPayInCount(payIns, tenderDescriptor));
		sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_OUT_MEDIA_UNIT_COUNT, getTillPayOutCount(payOuts, tenderDescriptor));
		sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

		/*
		 * Add Qualifiers
		 */
		sql.addQualifier(FIELD_TENDER_REPOSITORY_ID + " = " + getTillID(till));
		sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(register));
		sql.addQualifier(FIELD_TILL_START_DATE_TIMESTAMP + " = " + getStartTimestamp(till));
		sql.addQualifier(FIELD_TENDER_TYPE_CODE + " = " + inQuotes(tenderType));
		sql.addQualifier(FIELD_TENDER_SUBTYPE + " = " + inQuotes(emptyStringToSpaceString(tenderSubType)));
		sql.addQualifier(FIELD_CURRENCY_ISSUING_COUNTRY_CODE + " = " + inQuotes(tenderItem.getCurrencyCode()));
		// sql.addQualifier(FIELD_CURRENCY_ID + " = " +
		// tenderItem.getCurrencyID());
		if (till.getStatus() != Till.STATUS_RECONCILED) {
			if (tenderDescriptor.getTenderType() == TenderLineItemConstantsIfc.TENDER_TYPE_STORE_CREDIT)
				sql.addColumn(MAX_IN_AMT, getTenderStoreCreditAmount(till, tenderDescriptor));
			else
				sql.addColumn(MAX_IN_AMT, getTenderShortAmount(till, tenderDescriptor));
		}

		sql.addColumn(MAX_OUT_AMT, getTenderRefundAmount(till, tenderDescriptor));
		dataConnection.execute(sql.getSQLString());

		if (0 >= dataConnection.getUpdateCount()) {
			returnCode = false;
		}

		return (returnCode);
	}

	/** MAX Rev 1.1 Change : Start **/
	private boolean isTillReconcileEarlier(JdbcDataConnection dataConnection, TillIfc till) {
		logger.info("Patched class is being used.");

		boolean isReconciled = false;
		SQLSelectStatement sql = new SQLSelectStatement();
		sql.addTable(TABLE_TILL);
		sql.addColumn(FIELD_TILL_RECONCILE_FROM_BO);
		sql.addQualifier(FIELD_TENDER_REPOSITORY_ID, getTillID(till.getTillID()));

		ResultSet rs = null;

		int i = 0;
		try {
			dataConnection.execute(sql.getSQLString());
			rs = (ResultSet) dataConnection.getResult();
			while (rs.next()) {
				i = rs.getInt(1);
			}
		} catch (DataException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
		isReconciled = (i == 0) ? false : true;
		return isReconciled;
	}

	protected String getTillID(String tillID) {
		return ("'" + tillID + "'");
	}

	/** MAX Rev 1.1 Change : End **/
	// ---------------------------------------------------------------------
	/**
	 * Updates a record in the till table.
	 * <P>
	 * 
	 * @param dataConnection
	 *            connection to the db
	 * @param till
	 *            the till information
	 * @param register
	 *            the register associated with the till
	 * @return true if successful
	 * @exception DataException
	 *                upon error
	 **/
	// ---------------------------------------------------------------------
	public boolean updateTill(JdbcDataConnection dataConnection, TillIfc till, RegisterIfc register)
			throws DataException {
		boolean returnCode = false;
		SQLUpdateStatement sql = new SQLUpdateStatement();
		isUpdateStatement = true;

		/*
		 * Define the table
		 */
		sql.setTable(TABLE_TILL);

		/*
		 * Add columns and their values
		 */
		sql.addColumn(FIELD_TILL_SIGNON_OPERATOR, makeSafeString(till.getSignOnOperator().getEmployeeID()));
		if (till.getSignOffOperator() != null) {
			sql.addColumn(FIELD_TILL_SIGNOFF_OPERATOR, makeSafeString(till.getSignOffOperator().getEmployeeID()));
		}
		sql.addColumn(FIELD_TILL_STATUS_CODE, getStatusCode(till));
		sql.addColumn(FIELD_TILL_STATUS_DATE_TIME_STAMP, dateToSQLTimestampString(new Date()));
		sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(register));
		sql.addColumn(FIELD_TILL_START_DATE_TIMESTAMP, getStartTimestamp(till));
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDay(till.getBusinessDate()));
		sql.addColumn(FIELD_WORKSTATION_ACCOUNTABILITY, "'" + till.getRegisterAccountability() + "'");
		sql.addColumn(FIELD_TILL_TYPE, "'" + till.getTillType() + "'");

		if (till.getStatus() == AbstractFinancialEntityIfc.STATUS_RECONCILED) {
			sql.addColumn(FIELD_ALTER_TILL_RECONCILE, 0);
			sql.addColumn(FIELD_TILL_RECONCILE_FROM_BO, 0);
		}
		/*
		 * Add Qualifier(s)
		 */
		sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(register));
		sql.addQualifier(FIELD_TENDER_REPOSITORY_ID + " = " + getTillID(till));

		dataConnection.execute(sql.getSQLString());

		if (0 < dataConnection.getUpdateCount()) {
			returnCode = true;
		}

		return (returnCode);
	}

	protected String getTenderStoreCreditAmount(TillIfc till, TenderDescriptorIfc tenderDesc) {
		FinancialCountIfc expectedCount = till.getTotals().getCombinedCount().getExpected();
		FinancialCountTenderItemIfc expectedTender = expectedCount.getSummaryTenderItemByDescriptor(tenderDesc);

		FinancialCountIfc enteredCount = till.getTotals().getCombinedCount().getEntered();
		FinancialCountTenderItemIfc enteredTender = enteredCount.getSummaryTenderItemByDescriptor(tenderDesc);

		CurrencyIfc zero = DomainGateway.getBaseCurrencyInstance();
		CurrencyIfc amount;

		if (expectedTender != null && enteredTender != null) {
			amount = enteredTender.getAmountTotal().subtract(expectedTender.getAmountTotal());
		} else if (enteredTender == null) {
			if (expectedTender != null) {
				amount = expectedTender.getAmountIn().negate();
			} else // both are null
			{
				amount = zero;
			}
		} else // expectedTender == null
		{
			amount = enteredTender.getAmountTotal();
		}

		if (amount.compareTo(zero) == CurrencyIfc.LESS_THAN) {
			amount = amount.negate();
		} else {
			amount = zero;
		}

		String value = amount.getStringValue();

		if (isUpdateStatement) {
			value = FIELD_TILL_TENDER_SHORT_TOTAL_AMOUNT + " + " + safeSQLCast(value);
		}

		return (value);
	}
}
