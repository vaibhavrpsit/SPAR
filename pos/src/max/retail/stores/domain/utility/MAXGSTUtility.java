/********************************************************************************
*   
*	Copyright (c) 2015  Lifestyle India pvt Ltd    All Rights Reserved.
*	
*	Rev 1.1		16-Oct-2018		Jyoti Yadav			LS Edge Phase 2
*	Rev	1.0 	05-Jan-2017		Kritica.Agarwal		GST Changes	
*
********************************************************************************/
package max.retail.stores.domain.utility;

import org.apache.log4j.Logger;

import max.retail.stores.domain.arts.MAXConfigParameterTransaction;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;

public class MAXGSTUtility  {
	private static boolean gstEnabled;
	/*Change for Rev 1.1: Start*/
	private static MAXConfigParametersIfc configParameters;
	private static Logger logger = Logger.getLogger(MAXGSTUtility.class);
	/*Change for Rev 1.1: End*/
	public static boolean isGSTEnabled(){
		
		return gstEnabled;
	}
	
	public static void setGSTEnabled(boolean gstEnable){
		gstEnabled=gstEnable;
	}
	
	/*Change for Rev 1.1: Start*/
	public static MAXConfigParametersIfc getConfigparameter() {
		MAXConfigParameterTransaction configTransaction = new MAXConfigParameterTransaction();
		//MAXConfigParametersIfc configParameters = null;
		if(configParameters == null){
			configTransaction = (MAXConfigParameterTransaction) DataTransactionFactory
					.create(MAXDataTransactionKeys.CONFIG_PARAMETER_TRANSACTION);
			//System.out.println("GSTutility"+configTransaction);
			try {
				configParameters = configTransaction.selectConfigParameters();
				return configParameters;
			} catch (DataException e1) {
				e1.printStackTrace();
				return null;
			}	
		}else{
			return configParameters;
		}		
	}
	
	public static boolean edgePreviewSaleEnabled(EYSDate bussDate,MAXConfigParametersIfc config){
		boolean previewPeriod = false;
		//EYSDate bussDate = cargo.getRegister().getBusinessDate();
		if (bussDate != null) {
			logger.info(">> findEdgePreviewSaleCustomerType >  bussDate:" + bussDate.dateValue());
		}if (config != null && config.getEdgePreviewSaleStartDate() != null
				&& config.getEdgePreviewSaleEndDate() != null && bussDate != null) {
			logger.info(">> findEdgePreviewSaleCustomerType >  getEdgePreviewSaleStartDate:"
					+ config.getEdgePreviewSaleStartDate().dateValue());
			logger.info(">> findEdgePreviewSaleCustomerType >  getEdgePreviewSaleEndDate:"
					+ config.getEdgePreviewSaleEndDate().dateValue());
			if ((config.getEdgePreviewSaleStartDate().dateValue().before(bussDate.dateValue())
					|| config.getEdgePreviewSaleStartDate().dateValue().equals(bussDate.dateValue()))
					&& (config.getEdgePreviewSaleEndDate().dateValue().after(bussDate.dateValue())
							|| config.getEdgePreviewSaleEndDate().dateValue().equals(bussDate.dateValue()))) {
				previewPeriod = true;
			}
		}
		logger.info(">> Preview Period:" + previewPeriod);
		return previewPeriod;
	}
	/*Change for Rev 1.1: End*/
}
