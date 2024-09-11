/* *****************************************************************************************
 * Copyright (c) 2015   Lifestyle India Pvt. Ltd.All Rights Reserved.
 *
 * Initial Draft:		Aakash Gupta :		Dec 5th,2015
 * *******************************************************************************************/
package max.retail.stores.enterpriseconnection.storecredit;

import java.util.HashMap;

import org.apache.log4j.Logger;

import max.retail.stores.ws.storecredit.MAXStoreCreditWSIfc;
import max.retail.stores.ws.storecredit.MAXStoreCreditWSService;
import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.common.jaxws.connector.JAXWSConnector;
import oracle.retail.stores.enterpriseconnection.manager.EnterpriseDataOperation;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * Enterprise class that interacts with Store Credit Web Service hosted at CO.
 *
 * @author Aakash Gupta
 */
public class MAXEnterpriseUpdateSCLockStatus extends EnterpriseDataOperation {

	protected static Logger log = Logger.getLogger(MAXEnterpriseUpdateSCLockStatus.class);
	JAXWSConnector connector = null;

	public JAXWSConnector getWSConnector() throws DataException {
		if (connector == null || !connector.isInitialized()) {
			try {
				connector = (JAXWSConnector) BeanLocator.getServiceBean("service_StoreCreditWS");
				connector.openConnector();
			} catch (Exception e) {
				logRemoteExceptions(e, "error getting WebService connector.", null);
			}
		}
		return connector;
	}

	@Override
	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnectionIfc,
			DataActionIfc dataAction) throws DataException {
		log.info("MAXEnterpriseUpdateSCLockStatus.execute():Contacting ad-hoc Store Credit Webservice");
		getWSConnector();
		boolean persistanceStatus = false;
		HashMap<String, Object> storeCreditDetails = (HashMap<String, Object>) dataAction.getDataObject();
		String StoreCreditId = (String) storeCreditDetails.get("StoreCreditId");
		String lockFlag = (String) storeCreditDetails.get("lockFlag");		
		MAXStoreCreditWSService storeCreditService = (MAXStoreCreditWSService) connector.getServiceClass();
		MAXStoreCreditWSIfc storeCredit = storeCreditService.getMAXStoreCreditWSPort();
		persistanceStatus = storeCredit.updateStoreCreditLockStatus(StoreCreditId,lockFlag);
		dataTransaction.setResult(persistanceStatus);
	}
}
