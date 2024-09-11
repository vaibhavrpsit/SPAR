package max.retail.stores.domain.arts;


import java.util.HashMap;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.data.DataAction;
import com.extendyourstore.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;
import max.retail.stores.domain.manageroverride.MAXManagerOverride;

public class MAXSaveManagerOverrideTransaction  extends DataTransaction implements DataTransactionIfc {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8320695555048820901L;

	public static String transactionName = "MAXSaveManagerOverrideTransaction";
	
	private static Logger logger = Logger.getLogger(MAXSaveManagerOverrideTransaction.class.getName());

	public MAXSaveManagerOverrideTransaction() {
		super(transactionName);
		//System.out.println("Going inside MAXSaveManagerOverrideTransaction");

	}
	
	/**
	 * MAX Changes for Manager Override Report Requirement - Start
	 * 
	 * @param transaction
	 * @throws oracle.retail.stores.foundation.manager.data.DataException 
	 * @throws oracle.retail.stores.foundation.manager.data.DataException 
	 */
	@SuppressWarnings("deprecation")
	public void saveManagerOverride(MAXManagerOverride managerOverride) throws oracle.retail.stores.foundation.manager.data.DataException, Exception
	{
		if(!Util.isEmpty(managerOverride.getManagerId()))
		{
			
			DataActionIfc[] dataActions = new DataActionIfc[1];
			DataAction dataAction = new DataAction();
			dataAction.setDataOperationName("WriteManagerOverride");				
			dataAction.setDataObject(managerOverride);
			dataActions[0] = dataAction;
			setDataActions(dataActions);
			//System.out.println("Again  going");
			 getDataManager().execute(this);
			
		}
		

	}
}
