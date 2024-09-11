package max.retail.stores.domain.arts;

import java.util.HashMap;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.data.DataAction;
import com.extendyourstore.foundation.manager.data.DataException;

import max.retail.stores.domain.liquidationreport.MAXLiquidationReport;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * @author Kumar Vaibhav
 *
 */
public class MAXSaveLiquidationTransaction extends DataTransaction implements DataTransactionIfc {
	
	
	

	public static String transactionName = "MAXSaveLiquidationTransaction";
	
	private static Logger logger = Logger.getLogger(MAXSaveLiquidationTransaction.class.getName());

	public MAXSaveLiquidationTransaction() {
		super();
		//System.out.println("Going inside MAXSaveLiquidationTransaction");

	}
	
	/**
	 * MAX Changes for liquidationReport  Requirement - Start
	 * 
	 * @param transaction
	 * @throws oracle.retail.stores.foundation.manager.data.DataException 
	 * @throws oracle.retail.stores.foundation.manager.data.DataException 
	 */
	@SuppressWarnings("deprecation")
	public void saveLiquidationItem(MAXLiquidationReport liquidationReport) throws oracle.retail.stores.foundation.manager.data.DataException, Exception
	{
		
			
			DataActionIfc[] dataActions = new DataActionIfc[1];
			DataAction dataAction = new DataAction();
			dataAction.setDataOperationName("WriteLiquidationItem");				
			dataAction.setDataObject(liquidationReport);
			dataActions[0] = dataAction;
			setDataActions(dataActions);
			getDataManager().execute(this);
			
		
		

	}



}
