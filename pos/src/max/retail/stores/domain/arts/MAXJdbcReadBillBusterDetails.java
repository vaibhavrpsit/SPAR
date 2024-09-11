package max.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import oracle.retail.stores.common.data.JdbcUtilities;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

public class MAXJdbcReadBillBusterDetails extends JdbcDataOperation implements ARTSDatabaseIfc {
	
	/**
	 *  
	 */
	private static final long serialVersionUID = 4605746138074257057L;

	public MAXJdbcReadBillBusterDetails() {
		super();
		setName("MAXJdbcReadBillBusterDetails");
		//System.out.println("MAXJdbcReadMallCrtfTransactionRead 37");
	}

	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcReadBillBusterDetails.execute");
		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
		String output = null;
		TenderableTransactionIfc trans = null;
		trans = (TenderableTransactionIfc) action.getDataObject();
		output = readBillBusterAmtOff(connection, trans);
		dataTransaction.setResult(output);
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcReadBillBusterDetails.execute");
	}
	
	public String readBillBusterAmtOff(JdbcDataConnection dataConnection, TenderableTransactionIfc trans)
			throws DataException {
		//System.out.println("MAXJdbcReadMallCrtfTransactionRead 63");
		SQLSelectStatement sql = new SQLSelectStatement();
		String itemId =null;
		 HashMap<String, String> map = new HashMap<>();
		//System.out.println("hgffgfghghg");
		//SimpleDateFormat format = new SimpleDateFormat(JdbcUtilities.YYYYMMDD_DATE_FORMAT_STRING);
		String balc = trans.getTenderTransactionTotals().getBalanceDue().toString();
		//String Mob_number = trans.getCustomerInfo().getPhoneNumber().getPhoneNumber();
		 //DATE=trans.getBusinessDay().toString();
		//LocaleRequestor sqlLocale;
		//PLUItemIfc pluItem;
		//applyAdvancedPricingRules(dataConnection, pluItem, sqlLocale);
		String sqls = "SELECT RPRD.ID_RU_PRDV,RPRD.ID_STR_RT,RPRD.LU_CBRK_PRDV_TRN,RPRD.SC_RU_PRDV,"
				+ "RPRD.FL_DL_ADVN_APLY,RPRD.DE_RU_PRDV,RPRD.RC_RU_PRDV,RPRD.CD_SCP_PRDV,RPRD.CD_MTH_PRDV,"
				+ "RPRD.CD_BAS_PRDV,RPRD.QU_LM_APLY,RPRD.ITM_PRC_CTGY_SRC,RPRD.ITM_PRC_CTGY_TGT,RPRD.MO_TH_SRC,"
				+ "RPRD.MO_LM_SRC,RPRD.MO_TH_TGT,RPRD.MO_LM_TGT,RPRD.QU_AN_SRC,RPRD.QU_AN_TGT,RPRD.CD_TY_TH_PRDV,"
				+ "RPRD.DP_LDG_STK_MDFR,RPRD.FL_DL_DST,RPRD.FL_ALW_RPT_SRC,RPRD.ID_TY_DISC,RPRD.ID_PRM,RPRD.ID_PRM_CMP,"
				+ "RPRD.ID_PRM_CMP_DTL,RPRD.NM_RU_PRDV,IPRD.MO_UN_ITM_PRDV_SLS,IPRD.PE_UN_ITM_PRDV_SLS,"
				+ "IPRD.PNT_PRC_UN_ITM_PRDV_SLS,IPDRE.MO_TH,IPDRE.QU_TH,IPDRE.ID_ITM,RPRD.DC_RU_PRDV_EF,"
				+ "RPRD.DC_RU_PRDV_EP,RPRD.ID_PRCGP,RPRD.RULE_QTY FROM RU_PRDV RPRD	JOIN CO_EL_PRDV_ITM IPDRE "
				+ "ON IPDRE.ID_RU_PRDV = RPRD.ID_RU_PRDV AND IPDRE.ID_STR_RT = RPRD.ID_STR_RT JOIN CO_PRDV_ITM IPRD "
				+ "ON IPRD.ID_RU_PRDV = RPRD.ID_RU_PRDV AND IPRD.ID_STR_RT = RPRD.ID_STR_RT"
				+ " WHERE RPRD.DC_RU_PRDV_EF <= SYSDATE AND RPRD.DC_RU_PRDV_EP >= SYSDATE "
				+ "AND RPRD.SC_RU_PRDV != 'Expried' AND CD_BAS_PRDV = 2 AND DE_RU_PRDV = 'Buy$NorMoreGetYatZ$offTiered_BillBuster' And MO_TH_SRC <'" + balc + "' order by MO_TH_SRC desc";
		try {
			dataConnection.execute(sqls);
			ResultSet rs = (ResultSet) dataConnection.getResult();
			while (rs.next()) {
				itemId = (rs.getString(34)).toString();
				break;
				//System.out.println("=============" + itemId);
				//map.put(item, amxAdvancePriceingRule);
			}
			rs.close();
		} catch (DataException de) {
			logger.warn("" + de + "");
			throw de;
		} catch (SQLException se) {
			logger.warn("" + se + "");
			throw new DataException(DataException.SQL_ERROR, "transaction table", se);
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "transaction table", e);
		}
		return itemId;
	}
	
}
