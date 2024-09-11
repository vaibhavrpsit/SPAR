package max.retail.stores.gstinCentralJob;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.utility.MAXCodeConstantsIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.gate.Gateway;

public class GSTINInvoiceDataTransferJob  extends QuartzJobBean {

	HashMap inputData = new HashMap();
	HashMap outputData = new HashMap();
	HashMap outputBDData = new HashMap();
	protected static final Logger logger = Logger.getLogger(GSTINInvoiceDataTransferJob.class);
	/* 
	 * @Override by Mohan.Yadav
	 * @see org.springframework.scheduling.quartz.QuartzJobBean#executeInternal(org.quartz.JobExecutionContext)
	 */
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {

		String storeId = Gateway.getProperty("application", "StoreID", "");
		inputData.put(MAXCodeConstantsIfc.STORE_ID, storeId);
		inputData.put(MAXCodeConstantsIfc.GSTIN_DATA_TRANFER_ID, 1);
		MAXEGSTINDataTransferTransaction dsrTransaction = (MAXEGSTINDataTransferTransaction) DataTransactionFactory.create(MAXDataTransactionKeys.GSTIN_DATA_TRANSFER_TRANSACTION);
		try {
			ArrayList outputData = dsrTransaction.getGSTINDetails(inputData);

			if(outputData !=null && outputData.size()>0){
				try {
					MAXCentralGSTINDataTransferTransaction centralEReceiptDataTransferTransaction = (MAXCentralGSTINDataTransferTransaction) DataTransactionFactory.create(MAXDataTransactionKeys.GSTIN_DATA_BO_CO_TRANSFER_TRANSACTION);


					ArrayList transferData  = centralEReceiptDataTransferTransaction.gstinData(outputData);
					HashMap saveData = new HashMap();
					saveData.put(MAXCodeConstantsIfc.GSTIN_DATA_TRANFER_ID, 2);
					saveData.put(MAXCodeConstantsIfc.GSTIN_DATA_TRANFER_DETAILS, transferData);
					logger.info(transferData);
					if (transferData.size()>0) {
						dsrTransaction.updateGSTINTransferStatus(saveData);
					}
				} catch (DataException e) {
					logger.error(e.getMessage());
				}
			}


		} catch (DataException e) {
			logger.error(e.getMessage());
		}


	}

}
