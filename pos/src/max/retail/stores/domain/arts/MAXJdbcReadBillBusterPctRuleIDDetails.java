package max.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

public class MAXJdbcReadBillBusterPctRuleIDDetails extends JdbcDataOperation implements ARTSDatabaseIfc {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4010587150513273362L;


	public MAXJdbcReadBillBusterPctRuleIDDetails() {
		super();
		setName("MAXJdbcReadBillBusterPctRuleIDDetails");
		//System.out.println("MAXJdbcReadMallCrtfTransactionRead 37");
	}


	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcReadBillBusterPctRuleIDDetails.execute");
		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
		String output = null;
		TenderableTransactionIfc trans = null;
		HashMap hm  =(HashMap) action.getDataObject();
		String ruleID = (String) hm.get("ruleid");
		trans = (TenderableTransactionIfc) hm.get("retailTransactionIfc");
		//trans = (TenderableTransactionIfc) action.getDataObject();
		output = getItemId(connection, trans,ruleID);
		dataTransaction.setResult(output);
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcReadBillBusterPctRuleIDDetails.execute");
	}

	
	public static String getItemId(JdbcDataConnection dataConnection,TenderableTransactionIfc trans,String ruleID) {
		  
		  
		  DataAction dataAction = new DataAction(); //String output = null;
		  dataAction.setDataObject(trans);
		//  DataConnectionIfc dataConnection = new DataConnection();
		  DataActionIfc[] dataActions = new DataActionIfc[1];
		  dataActions[0] = dataAction; 
		  //JdbcDataConnection connection =  (JdbcDataConnection) dataConnection;
		//  String ruleID = trans.getTenderLineItemsVector().get(0).get
		  SQLSelectStatement sql = new SQLSelectStatement();
		  String balc = trans.getTenderTransactionTotals().getBalanceDue().toString(); 
		  String itemId=null; 
		  String sqls = "SELECT RPRD.ID_RU_PRDV,RPRD.ID_STR_RT,RPRD.LU_CBRK_PRDV_TRN,RPRD.SC_RU_PRDV,"+
				  		"RPRD.FL_DL_ADVN_APLY,RPRD.DE_RU_PRDV,RPRD.RC_RU_PRDV,RPRD.CD_SCP_PRDV,RPRD.CD_MTH_PRDV,"
				  		+"RPRD.CD_BAS_PRDV,RPRD.QU_LM_APLY,RPRD.ITM_PRC_CTGY_SRC,RPRD.ITM_PRC_CTGY_TGT,RPRD.MO_TH_SRC,"
				  		+"RPRD.MO_LM_SRC,RPRD.MO_TH_TGT,RPRD.MO_LM_TGT,RPRD.QU_AN_SRC,RPRD.QU_AN_TGT,RPRD.CD_TY_TH_PRDV,"
				  		+"RPRD.DP_LDG_STK_MDFR,RPRD.FL_DL_DST,RPRD.FL_ALW_RPT_SRC,RPRD.ID_TY_DISC,RPRD.ID_PRM,RPRD.ID_PRM_CMP,"
				  		+"RPRD.ID_PRM_CMP_DTL,RPRD.NM_RU_PRDV,IPRD.MO_UN_ITM_PRDV_SLS,IPRD.PE_UN_ITM_PRDV_SLS,"
				  		+"IPRD.PNT_PRC_UN_ITM_PRDV_SLS,IPDRE.MO_TH,IPDRE.QU_TH,IPDRE.ID_ITM,RPRD.DC_RU_PRDV_EF,"
				  		+"RPRD.DC_RU_PRDV_EP,RPRD.ID_PRCGP,RPRD.RULE_QTY FROM RU_PRDV RPRD	JOIN CO_EL_PRDV_ITM IPDRE "
				  		+"ON IPDRE.ID_RU_PRDV = RPRD.ID_RU_PRDV AND IPDRE.ID_STR_RT = RPRD.ID_STR_RT JOIN CO_PRDV_ITM IPRD "
				  		+ "ON IPRD.ID_RU_PRDV = RPRD.ID_RU_PRDV AND IPRD.ID_STR_RT = RPRD.ID_STR_RT"
				  		+ " WHERE RPRD.DC_RU_PRDV_EF <= SYSDATE AND RPRD.DC_RU_PRDV_EP >= SYSDATE AND "
				  		+ "RPRD.SC_RU_PRDV != 'Expried' AND CD_BAS_PRDV = 2 AND DE_RU_PRDV = 'Buy$NorMoreGetYatZ%offTiered_BillBuster'"
				  		+ "And MO_TH_SRC <'" + balc + "'And RPRD.id_ru_prdv='"+ruleID+"' order by MO_TH_SRC desc"; 
		  try { 
			  	dataConnection.execute(sqls); 
			  	ResultSet rs = (ResultSet) dataConnection.getResult(); 
			  	try {
					while (rs.next()) { 
						itemId =(rs.getString(34)).toString(); 
						//System.out.println("=============" + itemId); 
						//map.put(item, amxAdvancePriceingRule); } rs.close(); 
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
		  }
			  	catch(DataException de) 
			  	{ 
			  		logger.warn("" + de + ""); 
			  	} 
			  	
			  	return itemId; 
		  }
		 
}
