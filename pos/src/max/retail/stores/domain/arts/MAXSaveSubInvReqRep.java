package max.retail.stores.domain.arts;


import java.util.HashMap;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.data.DataAction;
import com.extendyourstore.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;
import max.retail.stores.domain.SubInvReqRep.MAXSubInvReqRep;

/**
 * @author kajal
 *
 */
public class MAXSaveSubInvReqRep extends DataTransaction implements DataTransactionIfc {

	
	private static final long serialVersionUID = -8320695555048820901L;

	public static String transactionName = "MAXSaveSubInvReqRep";
	
	private static Logger logger = Logger.getLogger(MAXSaveSubInvReqRep.class.getName());

	public MAXSaveSubInvReqRep() {
		super(transactionName);
		//System.out.println("Going inside MAXSaveSaveSubInvReqRepTransaction");

	}
	
	
	@SuppressWarnings("deprecation")
	public void saveSubInvReqRep(MAXSubInvReqRep mgo) throws oracle.retail.stores.foundation.manager.data.DataException, Exception
	{
		
			DataActionIfc[] dataActions = new DataActionIfc[1];
			DataAction dataAction = new DataAction();
			dataAction.setDataOperationName("SubInvReqRep");				
			dataAction.setDataObject(mgo);
			dataActions[0] = dataAction;
			setDataActions(dataActions);
			//System.out.println("Again  going");
			 getDataManager().execute(this);
			
		}
		


}
