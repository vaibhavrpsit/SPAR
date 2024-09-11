package max.retail.stores.enterpriseconnection.gstin;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import max.retail.stores.gstinCentralJob.MAXCentralGSTINDataTransferTransaction;
import max.retail.stores.gstinCentralJob.gstin.InvoiceIfc;
import max.retail.stores.gstinCentralJob.gstin.InvoiceResp;
import max.retail.stores.ws.gstin.MAXInvoiceDetailsIfc;
import max.retail.stores.ws.gstin.MAXInvoiceDetailsService;
import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.common.jaxws.connector.JAXWSConnector;
import oracle.retail.stores.enterpriseconnection.manager.EnterpriseDataOperation;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

public class MAXGSTINDataTransfer extends EnterpriseDataOperation
{
	protected static final Logger logger = Logger.getLogger(MAXGSTINDataTransfer.class);

	JAXWSConnector connector = null;

	protected JAXWSConnector getWSConnector() throws DataException {
		if (connector == null || !connector.isInitialized()) {
			try {
				connector = (JAXWSConnector) BeanLocator.getServiceBean("service_EInvoiceDataTransferWS");
				connector.openConnector();

			} catch (Exception e) {
				logRemoteExceptions(e, "error getting WebService connector.", null);
			}
		}

		return connector;
	}

	@Override
	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc dataAction)
			throws DataException {
		if (dataTransaction instanceof MAXCentralGSTINDataTransferTransaction) {
			String invRespDetails=null;
			ArrayList<InvoiceResp> invoiceRespList= new ArrayList<InvoiceResp>();

			try {
				getWSConnector();
				MAXInvoiceDetailsService service = (MAXInvoiceDetailsService) connector.getServiceClass();
				MAXInvoiceDetailsIfc invoiceDetail = service.getMAXInvoiceDetailsPort();

				Gson gson = new Gson(); 
				ArrayList input = (ArrayList) dataAction.getDataObject();		
				invRespDetails = invoiceDetail.saveInvoiceDetails(gson.toJson(input));

				ArrayList list = gson.fromJson(invRespDetails, ArrayList.class);
				for(int i=0;i<list.size(); i++){
					InvoiceResp status = gson.fromJson(gson.toJson(list.get(i)), InvoiceResp.class);
					invoiceRespList.add(status);
				}
			} catch (Exception exception) {
				logger.error(exception);
			}
			dataTransaction.setResult(invoiceRespList);
		}
	}

}
