/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved. 
 * Rev 2.6		May 14, 2024		Kamlesh Pant		Store Credit OTP: 
 * Rev 2.5		June 29, 2020   	Nitika Arora        Changes for Whatsapp Integration Functionality  
 * Rev 2.4		6th June 2019		Vidhya Kommareddi	POS REQ: Print Last Day for Exchange on receipt.
 * Rev 2.3		6th June 2019		Vidhya Kommareddi	POS REQ: Block suspend after N suspends
 * Rev 2.2		22nd May 2019		Vidhya Kommareddi	POS does not have to calculate expiry date of Gift Card anymore. QC will be doing it on their end.
 * Rev 2.1		11 Apr, 2019  		Mohan Yadav		 	Kerala cess changes 
 * Rev 2.0 		15/10/2018			Jyoti Yadav			LS Edge Phase 2
 * Rev 1.10 	10/08/2018			Jyoti Yadav			Quoting PAN CR
 * Rev 1.9  	July 28,2018 		Nitika Arora       	Changes for ADSR(Till Reconcile) Functionality 
 * Rev 1.8 		18/04/2018			Kritica Goel   	    Layaway calculation change
 * Rev 1.7      Apr 06,2018  		Ashish Yadav		Allowing adding items during retrieve transaction (Non furniture Items) suspended from MPOS 
 * Upgraded to ORPOS 14.0.1 from Lifestyle ORPOS 12.0.9IN: AAKASH GUPTA(EYLLP):Aug-17-2015
 * Rev 1.6 26/01/2018	Anoop Seth   	    GC Redemption Cross OU changes
 * Rev 1.5 24/12/2017	Shilpa Rawal  	    GC_eGV_CN Redemption Cross OU changes
 * Rev 1.4 04/01/2017 	Kritica Agarwal		GST Changes
 * Rev 1.3 01/08/2016 	Akhilesh Kumar		Mcoupon capillary integration
 * Rev 1.2 29/06/2013 	Karandeep Singh		Change for Dept Check
 * Rev 1.1  5/June/2013	Karandeep Singh     TIC Preview Sale requirement.
 * Rev 1.0  23/May/2013	Geetika Chugh     	VAT Extra requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;

import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import max.retail.stores.domain.utility.MAXConfigParameters;
import max.retail.stores.domain.utility.MAXConfigParametersIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

import org.apache.log4j.Logger;

/**
 * @author Geetika Chugh
 *
 */

public class MAXJDBCConfigParameter extends JdbcDataOperation implements MAXARTSDatabaseIfc{


	// This id is used to tell
	// the compiler not to generate a
	// new serialVersionUID.
	//
	static final long serialVersionUID = -6210806128663386042L;

	/**
	 * revision number of this class
	 */
	public static final String revisionNumber = "$Revision: 1.3 $";
	/**
	 * The logger to which log messages will be sent.
	 */
	private static Logger logger =
			Logger.getLogger(
					max.retail.stores.domain.arts.MAXJDBCConfigParameter.class);



	//---------------------------------------------------------------------
	/**
	 * Class constructor.
	 */
	//---------------------------------------------------------------------
	public MAXJDBCConfigParameter()
	{
		super();
		setName("MAXjdbcConfigParameter");
	}

	//---------------------------------------------------------------------
	/**
	 * Set all data members should be set to their initial state.
	 * <P>
	 *
	 * @exception DataException
	 */
	//---------------------------------------------------------------------
	@Override
	public void initialize() throws DataException
	{ // begin initialize()
		// no action taken here
	} // end initialize()

	//---------------------------------------------------------------------
	/**
	 * This method is used to execute a specific operation for a specific
	 * transaction against a specific datastore. <B>Pre-Condition</B>
	 * <UL>
	 * <LI>The DataTransactionIfc contains any application-specific data
	 * elements.
	 * <LI>The DataConnectionIfc is valid.
	 * <LI>The DataActionIfc contains the necessary DataObjects.
	 * </UL>
	 * <B>Post-Condition</B>
	 * <UL>
	 * <LI>The appropriate data operations have been executed by the
	 * DataConnection using the input data provided by the DataTransactionIfc
	 * and the DataActionIfc.
	 * <LI>Any results have been posted to the DataTransactionIfc.
	 * </UL>
	 *
	 * @param dt
	 *          The DataTransactionIfc that provides a place to post results.
	 * @param dc
	 *          The DataConnection that provides a connection to the datastore.
	 * @param da
	 *          The DataActionIfc that provides specific input data for this
	 *          operation.
	 * @exception DataException
	 *              is thrown if the operation cannot be completed.
	 */
	//---------------------------------------------------------------------
	@Override
	public void execute(
			DataTransactionIfc transaction,
			DataConnectionIfc connection,
			DataActionIfc action)
					throws DataException
	{
		JdbcDataConnection dataConnection = (JdbcDataConnection)connection;
		MAXConfigParametersIfc configParam = selectConfigParam(dataConnection);
		transaction.setResult(configParam);
	}


	// rev 1.0 change starts
	//    this method retreives the values of  parameters added in DB
	protected MAXConfigParametersIfc selectConfigParam(DataConnectionIfc connection) throws DataException{

		MAXConfigParametersIfc configParam = new MAXConfigParameters();
		SQLSelectStatement sql = new SQLSelectStatement();
		sql.addTable(ALIAS_CONFIG_PARAMETER, TABLE_CONFIG_PARAMETER);
		/*
		 * Add columns and their values
		 */
		/*sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_FLAG_VAT_EXTRA);
		sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_EOSS_MONTHS);
		sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_LIMIT_VAT_EXTRA);
		sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_DISCOUNT_LIMIT_VAT_EXTRA);
		//change for Rev 1.1 : Start
		sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_TIC_DISC_START_DATE);
		sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_TIC_DISC_END_DATE);
		//change for Rev 1.1 : End
*/		/** Change for Rev 1.2 : Start */
		//sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_DEPT_VAT_EX);
		/** Change for Rev 1.2 : End */
		/** Change for Rev 1.3 : Start */
		//sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_ISSUE_COUPN_CAPILLARY);
		/** Change for Rev 1.3 : End */
		/** Change for Rev 1.4 : Start */
		//sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_GST_ENABLED);
		//sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_TAX_THRESHOLD);
		/** Change for Rev 1.4 : End */		
		
		/** Change for Rev 1.5 : Start */
		//sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_ORGANIZATION_UNIT_ID);
		/** Change for Rev 1.5 : End */	
		
		/** Change for Rev 1.6 : Start */
		//sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_SCLP_BIN_RANGE_ID);
		/** Change for Rev 1.6 : End */			
		
		//Changes starts for Rev 1.7(Ashish)
		/*sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_FURNITURE_NONFURNITURE_CHECK);
		sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_FURNITURE_REGISTER_NUMBER);*/
		//Changes ends for Rev 1.7(Ashish)
		
		//Change for Rev 1.8 : Starts
	//	sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_LAYAWAY_TAX_CALCULATION);
		//Change for Rev 1.8 : Ends
		/** Change for Rev 1.9 : Start */
		//sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_DSR_ENABLE_FLAG);
		//sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_DSR_DAYS_VALIDITY);
		/** Change for Rev 1.9 : Ends */
		/*Change for Rev 1.10: Start*/
		//sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_QUOTING_PAN_THRESHOLD);
		/*Change for Rev 1.10: End*/
		/*Change for Rev 2.0: Start*/
		/*sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_EDGE_DISC_START_DATE);
		sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_EDGE_DISC_END_DATE);
		sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_EDGE_ITEM_VALUES);
		sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_EDGE_NAME_VALUES);
		*/
		/*Change for Rev 2.0: End*/
		
		//sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_MAX_QOCN_ISSUE_AMT);
		//sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_CESS_ENABLE);  	 // rev 2.1 changes  here
		//Rev 2.2 start --end --commented out below line
		//sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + GC_EXP_EXTN_DAYS);  	 // gc expire extended days changes  here
		
		//Rev 2.3 start 
		//sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_MAX_NUM_OF_SUSPENDS);  	 
		//Rev 2.3 end
		//Rev 2.4 start
		//sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_RETURN_DAYS);  	
		//Changes starts for rev 1.1 (Ashish : Easy Exchange)
	//	sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_EASY_EXCHANGE_ENABLE_FLAG);
		//Changes ends for rev 1.1 (Ashish : Easy Exchange)
		//Changes starts for rev 1.1 (Ashish : Youth Card)
		//sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_YOUTH_CARD_ENABLE_FLAG);
		//sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_IS_PROMOTIONAL_FLAG);
		/*sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_YOUTH_CARD_DIS_PERC);
		sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_YOUTH_CARD_INITIAL_AMT);
		sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_YOUTH_CARD_EXPIRY);
		sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_YOUTH_CARD_ITEM);
		sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_YOUTH_CARD_THRESHOLD_AMOUNT);
				//Changes ends for rev 1.1 (Ashish : Youth Card)
		//Rev 2.4 end
*/		//Changes starts for Rev 1.1 (Ashish : EReceipt)
		/*sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_ERECEIPT_CONF);
		sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_ERECEIPT_TRAN_TYPE);
		//Changes ends for Rev 1.1 (Ashish : EReceipt)
		//Change for Rev 2.5 : Starts
		sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_VIRTUAL_STORE_ID);
		//Change for Rev 2.5 : Ends
		sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + ENABLE_GSTIN_INVOICE);		*/

		sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_SBI_POINT_CON);
		sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_SBI_POINT_MIN);
		sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_SBI_LOYALTY_CON_RATE);
		sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_PAYTMQR_STATUS_RETRY_COUNT);
			sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_SPCL_EMP_DISC);
		sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_FL_EMP_OTP);
			sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_CASH_LIMIT_PARAMETER);
		//Rev 2.6 Starts
		sql.addColumn(ALIAS_CONFIG_PARAMETER + "." + FIELD_SC_OTP_RETRIES);
		//Rev 2.6 Ends
		try
		{
			connection.execute(sql.getSQLString());
			ResultSet rs = (ResultSet) connection.getResult();

			while (rs.next())
			{

				//configParam.setCalculateVatExtra(getBooleanFromString(rs, 1));
				//configParam.setEossMonths(getSafeString(rs, 2));
				//configParam.setVatLimit(rs.getInt(3));
				//configParam.setDiscountLimit(rs.getInt(4));
				//change for Rev 1.1 : Start
				//configParam.setPreviewSaleStartDate(new EYSDate(rs.getDate(FIELD_TIC_DISC_START_DATE)));
				//configParam.setPreviewSaleEndDate(new EYSDate(rs.getDate(FIELD_TIC_DISC_END_DATE)));
				//change for Rev 1.1 : End
				/** Change for Rev 1.2 : Start */
				//configParam.setVatExtraDepts(getSafeString(rs, 7));
				/** Change for Rev 1.2 : End */
				
				/** Change for Rev 1.3 : Start */
				//configParam.setIssueCouponEnable(false);
				//int couponIssue=rs.getInt(8);
			
				/*
				 * if(couponIssue==1){ configParam.setIssueCouponEnable(true); }else{
				 * configParam.setIssueCouponEnable(false); }
				 */
				/** Change for Rev 1.3 : End */
				/** Change for Rev 1.4 : Start */
				//String gstEnabled =getSafeString(rs, 9);
				//configParam.setGSTEnable(gstEnabled.equalsIgnoreCase("Y") ? true : false);
				//configParam.setThresholdTax(rs.getString(FIELD_TAX_THRESHOLD));
				/** Change for Rev 1.4 : End */
								
				/** Change for Rev 1.5 : Start */
				//configParam.setOraganizationUnit(rs.getString(FIELD_ORGANIZATION_UNIT_ID));
				/** Change for Rev 1.5 : End */
								
				/** Change for Rev 1.6 : Start */
				//configParam.setBinRange(rs.getString(FIELD_SCLP_BIN_RANGE_ID));
				/** Change for Rev 1.6 : End */
								
				//Changes starts for rev 1.7 (Ashish)
				/*String furnonfurEnable =rs.getString(FIELD_FURNITURE_NONFURNITURE_CHECK);
				configParam.setFurnitureNonFurnitureEnable(furnonfurEnable.equalsIgnoreCase("Y") ? true : false);
				configParam.setFurnitureRegisterNumber(rs.getInt(FIELD_FURNITURE_REGISTER_NUMBER));*/
				//Changes ends for rev 1.7 (Ashish)
				/** Change for Rev 1.8 : Start */
			//	configParam.setLayawayCalculationChange(rs.getString(FIELD_LAYAWAY_TAX_CALCULATION).equalsIgnoreCase("Y") ? true : false);
				/** Change for Rev 1.8 : End */
				/* Change for Rev 1.9 : Start */
				//configParam.setDsrEnableFlag(rs.getString(FIELD_DSR_ENABLE_FLAG).equalsIgnoreCase("Y") ? true : false);
				//configParam.setDsrDays(rs.getString(FIELD_DSR_DAYS_VALIDITY));
				
				/* Change for Rev 1.9 : End */
				/*Change for Rev 1.10: Start*/
				//configParam.setPANThreshold(rs.getString(FIELD_QUOTING_PAN_THRESHOLD));
				/*Change for Rev 1.10: End*/
				/*Change for Rev 2.0: Start*/
				/*configParam.setEdgePreviewSaleStartDate(new EYSDate(rs.getDate(FIELD_EDGE_DISC_START_DATE)));
				configParam.setEdgePreviewSaleEndDate(new EYSDate(rs.getDate(FIELD_EDGE_DISC_END_DATE)));
				configParam.setEdgeItemValues(rs.getString(FIELD_EDGE_ITEM_VALUES));
				configParam.setEdgeName(rs.getString(FIELD_EDGE_NAME_VALUES));*/
				/*Change for Rev 2.0: End*/
				
				//configParam.setMAXQOCNIssueamount(rs.getString(FIELD_MAX_QOCN_ISSUE_AMT));
				 // rev 2.1 changes start here
				boolean cessEnable=false;
				//String cess  = rs.getString(FIELD_CESS_ENABLE);
				/*if(cess !=null && cess.equalsIgnoreCase("Y")){
					cessEnable = true;
				}*/
				//configParam.setCessEnable(cessEnable);
					 // rev 2.1 changes end here
				//Rev 2.2 start -end --commented out below line
				//configParam.setGcExpExtnDays(rs.getString(GC_EXP_EXTN_DAYS));
				//Rev 2.3 start 
				//configParam.setMaximumNumOfSuspends(rs.getString(FIELD_MAX_NUM_OF_SUSPENDS));
				//Rev 2.3 end
				//Rev 2.4 start 
			//	configParam.setReturnDays(rs.getString(FIELD_RETURN_DAYS));
				//Rev 2.4 end
				//Changes starts for rev 1.1 (Ashish : Easy Exchange)
			//	configParam.setEnableEasyExchange(rs.getString(FIELD_EASY_EXCHANGE_ENABLE_FLAG));
				//Changes starts for rev 1.1 (Ashish : Easy Exchange)
				//Changes starts for rev 1.1 (Ashish : Easy Exchange)
				/*configParam.setEnableYouthCard(rs.getString(FIELD_YOUTH_CARD_ENABLE_FLAG));
				configParam.setPromotionalYouthCard(rs.getString(FIELD_IS_PROMOTIONAL_FLAG));
				configParam.setYouthCardDiscPerc(rs.getInt(FIELD_YOUTH_CARD_DIS_PERC));
				configParam.setYouthCardInitialAmount(rs.getInt(FIELD_YOUTH_CARD_INITIAL_AMT));
				configParam.setYouthCardExpiry(rs.getString(FIELD_YOUTH_CARD_EXPIRY));
				configParam.setYouthCardItem(rs.getString(FIELD_YOUTH_CARD_ITEM));
configParam.setYouthThresholdAmount(rs.getString(FIELD_YOUTH_CARD_THRESHOLD_AMOUNT));
				//Changes starts for rev 1.1 (Ashish : Easy Exchange)
				//Changes starts for Rev 1.1 (Ashish : Ereceipt)
				configParam.seteReceiptConf(rs.getString(FIELD_ERECEIPT_CONF));
				configParam.seteReceiptTrantype(rs.getString(FIELD_ERECEIPT_TRAN_TYPE));
				//Changes ends for Rev 1.1 (Ashish : Ereceipt)
				//Change for Rev 2.5 : Starts
				configParam.setStoreId(rs.getString(FIELD_VIRTUAL_STORE_ID));
				//Change for Rev 2.5 : Ends   ENABLE_GSTIN_INVOICE
				configParam.setGstInvoice(rs.getString(ENABLE_GSTIN_INVOICE));*/
				
				String sbiPointconEnable =rs.getString(FIELD_SBI_POINT_CON);
				configParam.setSbiPointConversion(sbiPointconEnable.equalsIgnoreCase("Y") ? true : false);
				configParam.setSbiMinPoint(rs.getInt(FIELD_SBI_POINT_MIN));
				configParam.setSbiPointConversionRate(rs.getInt(FIELD_SBI_LOYALTY_CON_RATE));
				configParam.setPaytmQRStatusCheckRetryCount(rs.getInt(FIELD_PAYTMQR_STATUS_RETRY_COUNT));
				String spclEmpDisc = rs.getString("SPCL_EMP_DISC");
				configParam.setSpclEmpDisc(spclEmpDisc.equalsIgnoreCase("Y") ? true : false);
					//changes by shyvanshu mehra...
				
				configParam.setCashLimitParameter(rs.getBigDecimal(FIELD_CASH_LIMIT_PARAMETER));
				configParam.setEmpOtpEnableCheck(rs.getString(FIELD_FL_EMP_OTP).equalsIgnoreCase("Y") ? true : false);
				
				//Rev 2.6 start
				configParam.setScOtpRetries(rs.getInt(FIELD_SC_OTP_RETRIES));
				//Rev 2.6 end
			}

			rs.close();

		}catch (DataException de) {
			logger.warn("" + de + "");
			throw de;
		} catch (SQLException se) {
			logger.warn("" + se + "");
			throw new DataException(DataException.SQL_ERROR, "transaction table", se);
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "transaction table", e);
		}


		return configParam;
	}
	//rev 1.0 change ends

}
