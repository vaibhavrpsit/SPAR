package max.retail.stores.gstinCentralJob;


import java.util.ArrayList;
import java.util.HashMap;

import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

public class MAXCentralGSTINDataTransferTransaction extends DataTransaction
implements DataTransactionIfc{

	private static final long serialVersionUID = 6345168077674591501L;

	/**
       revision number of this class
	 **/
	public static final String revisionNumber = "$Revision: 4$";
	/**
       The transactionName name links this transaction to a command within the
       DataScript.
	 **/
	public static String findForLoginName = "MAXCentralGSTINDataTransferTransaction";

	public MAXCentralGSTINDataTransferTransaction()
	{
		super(findForLoginName);
	}

	//---------------------------------------------------------------------
	/**
       DataCommand constructor.  Initializes dataOperations and
       dataConnectionPool.
	 **/
	//---------------------------------------------------------------------
	public MAXCentralGSTINDataTransferTransaction(String name)
	{
		super(name);
	}


	public ArrayList gstinData(ArrayList outputData) throws DataException {
		ArrayList output = new ArrayList();
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction da = new DataAction();
		da.setDataOperationName("GSTINCentralDataTransfer");
		da.setDataObject(outputData);
		dataActions[0] = da;
		setDataActions(dataActions);
		output = (ArrayList) getDataManager().execute(this);
		return output;
	}

	public String getRevisionNumber()
	{
		return(revisionNumber);
	}

	//---------------------------------------------------------------------
	/**
       Returns the string representation of this object.
       <P>
       @return String representation of object
	 **/
	//---------------------------------------------------------------------
	@Override
	public String toString()
	{
		String strResult = new String("Class: MAXCentralGSTINDataTransferTransaction (Revision "
				+ getRevisionNumber() + ") @"
				+ hashCode());


		return(strResult);
	}
}
