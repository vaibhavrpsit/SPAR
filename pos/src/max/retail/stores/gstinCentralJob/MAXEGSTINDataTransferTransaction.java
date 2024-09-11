package max.retail.stores.gstinCentralJob;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import max.retail.stores.domain.gstin.MAXGSTINValidationResponseIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

public class MAXEGSTINDataTransferTransaction extends DataTransaction implements DataTransactionIfc {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1088213848545075441L;

	public static String operationName = "GSTINDataTransfer";

	public static String transactionName = "MAXEGSTINDataTransferTransaction";

	private static Logger logger = Logger.getLogger(MAXEGSTINDataTransferTransaction.class.getName());

	public MAXEGSTINDataTransferTransaction() {
		super(transactionName);
	}
	public MAXEGSTINDataTransferTransaction(String name)
	{
		super(name);
	}


	public ArrayList getGSTINDetails(HashMap map) throws DataException {
		ArrayList output = new ArrayList();
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction da = new DataAction();
		da.setDataOperationName(operationName);
		da.setDataObject(map);
		dataActions[0] = da;
		setDataActions(dataActions);
		output = (ArrayList) getDataManager().execute(this);
		//dataTransfer(output);
		return output;
	}

	public boolean updateGSTINTransferStatus(HashMap map) throws DataException {
		HashMap output = new HashMap();
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction da = new DataAction();
		da.setDataOperationName(operationName);
		da.setDataObject(map);
		dataActions[0] = da;
		setDataActions(dataActions);
		output = (HashMap) getDataManager().execute(this);
		return true;
	}
	
	public MAXGSTINValidationResponseIfc getStoreGSTIN(HashMap map) throws DataException {
		
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction da = new DataAction();
		da.setDataOperationName(operationName);
		da.setDataObject(map);
		dataActions[0] = da;
		setDataActions(dataActions);
		MAXGSTINValidationResponseIfc output = (MAXGSTINValidationResponseIfc) getDataManager().execute(this);
		return output;
	}
	
	public boolean insertGSTIN(HashMap map) throws DataException {
		HashMap output = new HashMap();
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction da = new DataAction();
		da.setDataOperationName(operationName);
		da.setDataObject(map);
		dataActions[0] = da;
		setDataActions(dataActions);
		output = (HashMap) getDataManager().execute(this);
		boolean flag = (boolean) output.get("Status");
		return flag;
	}

}

