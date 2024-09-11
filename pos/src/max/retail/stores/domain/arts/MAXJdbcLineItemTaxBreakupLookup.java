/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/max/retail/stores/domain/arts/MAXJdbcLineItemTaxBreakupLookup.java /main/32 2014/06/17 15:26:38 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * Rev 1.0	Aug 26,2016		Nitesh Kumar	changes for code merging 
 * ===========================================================================
 */
package max.retail.stores.domain.arts;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import max.retail.stores.domain.lineitem.MAXLineItemTaxBreakUpDetail;
import max.retail.stores.domain.lineitem.MAXLineItemTaxBreakUpDetailIfc;
import max.retail.stores.domain.tax.MAXTaxAssignment;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

public class MAXJdbcLineItemTaxBreakupLookup extends JdbcDataOperation implements ARTSDatabaseIfc {
	private static Logger logger = Logger.getLogger(MAXJdbcLineItemTaxBreakupLookup.class);

	public MAXJdbcLineItemTaxBreakupLookup() {
		super();
		setName("JdbcLineItemTaxBreakupLookup");
	}

	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcLineItemTaxBreakupLookup.execute");

		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
		ReturnItemIfc item = (ReturnItemIfc) action.getDataObject();

		MAXLineItemTaxBreakUpDetailIfc[] lineItemTaxBreakUpDetail = selectSaleReturnLineItemTaxBreakupInformation(
				connection, item);

		dataTransaction.setResult(lineItemTaxBreakUpDetail);

		if (logger.isDebugEnabled())
			logger.debug("JdbcLineItemTaxBreakupLookup.execute");
	}

	public MAXLineItemTaxBreakUpDetailIfc[] selectSaleReturnLineItemTaxBreakupInformation(
			JdbcDataConnection dataConnection, ReturnItemIfc item) throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.selectSaleReturnLineItemTaxBreakupInformation()");

		BigDecimal returnQuantity = item.getItemQuantity();

		SQLSelectStatement sql = new SQLSelectStatement();

		sql.addTable("TR_LTM_TX_BRKUP");

		sql.addColumn("TX_BRKUP_TX_CD");
		sql.addColumn("TX_BRKUP_TX_CD_DSCR");
		sql.addColumn("TX_BRKUP_TX_RT");
		sql.addColumn("QU_ITM_LM_RTN_SLS");
		sql.addColumn("TX_BRKUP_TXBL_AMT");
		sql.addColumn("TX_BRKUP_TX_AMT");

		sql.addColumn("TX_FCT");
		sql.addColumn("TXBL_FCT");

		sql.addQualifier("AI_TRN = " + item.getOriginalTransactionID().getFormattedTransactionSequenceNumber());
		sql.addQualifier("ID_WS = " + getStringValue(item.getOriginalTransactionID().getFormattedWorkstationID()));
		sql.addQualifier("ID_STR_RT = " + getStringValue(item.getOriginalTransactionID().getFormattedStoreID()));
		sql.addQualifier("DC_DY_BSN = " + getStringValue(item.getOriginalTransactionBusinessDate().asISODate()));
		sql.addQualifier("AI_LN_ITM = " + item.getOriginalLineNumber());

		ArrayList lineItemTaxBreakUpList = new ArrayList();
		try {
			dataConnection.execute(sql.getSQLString());
			ResultSet rs = (ResultSet) dataConnection.getResult();

			while (rs.next()) {
				MAXLineItemTaxBreakUpDetailIfc lineItemTaxBreakUp = new MAXLineItemTaxBreakUpDetail();
				MAXTaxAssignment taxAssignment = new MAXTaxAssignment();
				int index = 0;
				index++;
				taxAssignment.setTaxCode(getSafeString(rs, index));
				index++;
				taxAssignment.setTaxCodeDescription(getSafeString(rs, index));

				index++;
				BigDecimal rate = getBigDecimal(rs, index);
				taxAssignment.setTaxRate(rate);

				index++;
				BigDecimal quantity = getBigDecimal(rs, index);

				index++;
				lineItemTaxBreakUp.setTaxableAmount(
						getCurrencyFromDecimal(rs, index).multiply(returnQuantity).divide(quantity).negate());
				index++;
				lineItemTaxBreakUp.setTaxAmount(
						getCurrencyFromDecimal(rs, index).multiply(returnQuantity).divide(quantity).negate());

				index++;
				BigDecimal txfct = getBigDecimal(rs, index, 10);
				index++;
				BigDecimal txblfct = getBigDecimal(rs, index, 10);
				taxAssignment.setTaxAmountFactor(txfct);
				taxAssignment.setTaxableAmountFactor(txblfct);

				lineItemTaxBreakUp.setTaxAssignment(taxAssignment);
				lineItemTaxBreakUpList.add(lineItemTaxBreakUp);
			}
			rs.close();
		} catch (SQLException se) {
			throw new DataException(1, "selectSaleReturnLineItemTaxBreakupInformation", se);
		} catch (DataException de) {
			throw de;
		} catch (Exception e) {
			throw new DataException(0, "selectSaleReturnLineItemTaxBreakupInformation", e);
		}

		MAXLineItemTaxBreakUpDetailIfc[] lineItemTaxBreakUpDetail = new MAXLineItemTaxBreakUpDetail[lineItemTaxBreakUpList
				.size()];
		for (int i = 0; i < lineItemTaxBreakUpDetail.length; i++) {
			lineItemTaxBreakUpDetail[i] = ((MAXLineItemTaxBreakUpDetailIfc) lineItemTaxBreakUpList.get(i));
		}
		return lineItemTaxBreakUpDetail;
	}

	protected String getStringValue(String value) {
		if (value != null) {
			value = new String("'" + value + "'");
		}
		return value;
	}
}