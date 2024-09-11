package max.retail.stores.gstinJob;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import max.retail.stores.gstinJob.utility.gstin.GSTINInvoiceIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

public class MAXGSTINAutomationTransaction extends DataTransaction implements DataTransactionIfc {

	
	private static final long serialVersionUID = -1088213848545075441L;

	public static String operationName = "GSTINAutomation";

	public static String transactionName = "MAXGSTINAutomationTransaction";

	private static Logger logger = Logger.getLogger(MAXGSTINAutomationTransaction.class.getName());

	public MAXGSTINAutomationTransaction() {
		super(transactionName);
	}
	public MAXGSTINAutomationTransaction(String name)
	{
		super(name);
	}


	public HashMap getGstinCongiguration(HashMap map) throws DataException {
		HashMap output = new HashMap();
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction da = new DataAction();
		da.setDataOperationName(operationName);
		da.setDataObject(map);
		dataActions[0] = da;
		setDataActions(dataActions);
		output = (HashMap) getDataManager().execute(this);
		return output;
	}





	public ArrayList<GSTINInvoiceIfc> getInvoiceData(HashMap map) throws DataException {
		ArrayList<GSTINInvoiceIfc> output = new ArrayList<GSTINInvoiceIfc>();
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction da = new DataAction();
		da.setDataOperationName(operationName);
		da.setDataObject(map);
		dataActions[0] = da;
		setDataActions(dataActions);
		output = (ArrayList<GSTINInvoiceIfc>) getDataManager().execute(this);
		return output;
	}



	public boolean updateGstinDetails(HashMap updateInvoice) throws DataException {
		boolean output = false;
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction da = new DataAction();
		da.setDataOperationName(operationName);
		da.setDataObject(updateInvoice);
		dataActions[0] = da;
		setDataActions(dataActions);
		output = (boolean) getDataManager().execute(this);
		return output;
	}

	public boolean updateInvoiceError(HashMap updateInvoice) throws DataException {
		boolean output = false;
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction da = new DataAction();
		da.setDataOperationName(operationName);
		da.setDataObject(updateInvoice);
		dataActions[0] = da;
		setDataActions(dataActions);
		output = (boolean) getDataManager().execute(this);
		return output;
	}	


	public ArrayList<GSTINInvoiceIfc> getFailedInvoiceData(HashMap invoiceInput) throws DataException  {
		ArrayList<GSTINInvoiceIfc> output = new ArrayList<GSTINInvoiceIfc>();
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction da = new DataAction();
		da.setDataOperationName(operationName);
		da.setDataObject(invoiceInput);
		dataActions[0] = da;
		setDataActions(dataActions);
		output = (ArrayList<GSTINInvoiceIfc>) getDataManager().execute(this);
		return output;
	}




}

