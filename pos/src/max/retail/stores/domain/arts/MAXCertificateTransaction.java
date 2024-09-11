/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved. 
 *	Rev 2.1		May 14, 2024			Kamlesh Pant		Store Credit OTP:
 *  Rev 2.0     May 15, 2023            Kumar Vaibhav       CN lock chnages
 *  Rev 1.0     Dec 19, 2016	        Ashish Yadav		Initial Changes for StoreCredit FES
 *
 ********************************************************************************/
package max.retail.stores.domain.arts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import max.retail.stores.domain.tender.MAXTenderStoreCredit;
import max.retail.stores.domain.tender.MAXTenderStoreCreditIfc;
import max.retail.stores.domain.utility.MAXStoreCreditIfc;
import max.retail.stores.storecredit.temp.MAXStoreCreditDetails;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.CertificateTransaction;
import oracle.retail.stores.domain.tender.TenderCertificateIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.GiftCertificateDocumentIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;

import org.apache.log4j.Logger;

public class MAXCertificateTransaction extends CertificateTransaction{

	private static final long serialVersionUID = 2459156163824058051L;
	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXCertificateTransaction.class);

	/**
	 * MAX version of super
	 * CertificateTransaction#readCertificate(TenderCertificateIfc certificate).
	 *
	 * @author Aakash Gupta
	 */
	@Override
	public TenderCertificateIfc readCertificate(TenderCertificateIfc certificate) throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXCertificateTransaction.readCertificate");

		// Change for Rev 1.0:Starts
		//super.readCertificate(certificate);

		if (certificate instanceof TenderStoreCreditIfc) {
			try {
				logger.info("Communicating ORCO 14 custom webservice for store credit: "
						+ ((TenderStoreCreditIfc) certificate).getStoreCreditID());
				updateStoreCreditDetails(certificate);
				/*if (!updateStoreCreditDetails(certificate))
					super.readCertificate(certificate);*/
			} catch (Exception exception) {
				logger.error("Error!!! occured while communicating ORCO 14 custom webservice for store credit: "
						+ ((TenderStoreCreditIfc) certificate).getStoreCreditID() + ". Error details:\n "
						+ exception.toString() + "\n. Using ORACLE's code version to find store credit details.");
				throw new DataException("ERROR establishing connection with Central Office while using Store Credit");
				
				//super.readCertificate(certificate);

			}
		} else
			super.readCertificate(certificate);

		// Change for Rev 1.0:Ends

		return certificate;
	}
	@SuppressWarnings({ "unchecked", "deprecation" })
	public boolean updateStoreCreditDetails(TenderCertificateIfc certificate) throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXCertificateTransaction.updateStoreCreditDetails");
		DataAction[] dataActions = new DataAction[1];
		DataAction da = new DataAction();
		da.setDataOperationName("LookupStoreCreditFromCO");
		da.setDataObject(certificate);
		dataActions[0] = da;
		setDataActions(dataActions);
		HashMap<String, Object> storeCreditMap = new HashMap<>();
		try {
			storeCreditMap = (HashMap<String, Object>) (getDataManager().execute(this));
		} catch (DataException exception) {
			logger.error("ERROR establishing connection with Central Office while using Store Credit");
			throw new DataException("ERROR establishing connection with Central Office while using Store Credit");
			// return false;
		}
		MAXTenderStoreCreditIfc tenderStoreCredit = (MAXTenderStoreCreditIfc) certificate;
		if (!(Boolean) storeCreditMap.get("IsRollOutComplete")) {
			Boolean isValid = (Boolean) storeCreditMap.get("IsValid");
			if (isValid != null && isValid) {
				// set values in store credit
				String status = (String) storeCreditMap.get("Status");
				tenderStoreCredit.getStoreCredit().setStatus(status);
				if (status.equalsIgnoreCase("REDEEM")) {
					String redemptionTransaction = (String) storeCreditMap.get("RedemptionTransaction");
					tenderStoreCredit.setRedeemTransactionID(redemptionTransaction);

					String redemptionDate = (String) storeCreditMap.get("RedemptionDate");
					tenderStoreCredit.setRedeemDate(getEYSDate(redemptionDate));
					tenderStoreCredit.setStoreCreditStatus(status);
				}

				Double amount = (Double) storeCreditMap.get("BalanceAmount");
				CurrencyIfc storeCreditBalance = DomainGateway.getBaseCurrencyInstance(new BigDecimal(amount));
				tenderStoreCredit.setAmount(storeCreditBalance);

				String expirationDate = (String) storeCreditMap.get("ExpirationDate");
				tenderStoreCredit.setExpirationDate(getEYSDate(expirationDate));
				
				
				//Added by Vaibhav LS Credit note code merging start Rev 2.0
				Boolean storeCreditLock = (Boolean) storeCreditMap.get("IsStoreCreditLock");
				tenderStoreCredit.setStoreCreditLock(storeCreditLock);
				((MAXStoreCreditIfc) tenderStoreCredit.getStoreCredit()).setStoreCreditLock(storeCreditLock);
				
				logger.debug("Store credit status karni -"+tenderStoreCredit.isStoreCreditLock());
				//end Rev 2.0
				
				//changes for Rev 2.1 starts
				String mobileNumber=(String) storeCreditMap.get("mobile");
				tenderStoreCredit.setSCmobileNumber(mobileNumber);
				((MAXStoreCreditIfc) tenderStoreCredit.getStoreCredit()).setSCmobileNumber(mobileNumber);

				logger.debug("Store credit mobile -"+tenderStoreCredit.getSCmobileNumber());
				//changes for Rev 2.1 ends

			} else if (isValid != null) {
				String status = (String) storeCreditMap.get("Status");
				if (status != null && !status.equalsIgnoreCase("NO_DATA"))
					tenderStoreCredit.setStoreCreditStatus(status);
				if(status.equals("NO_DATA") || status ==null)
					tenderStoreCredit.setStoreCreditStatus("NO_DATA");
			}
			return true;
		} else {
			updateProperty();
			return false;
		}
	}

	/**
	 * Triggers the store credit details in ORCO 12.0.9IN if the property
	 * <code>IS_ROLLOUT_COMPLETE</code> from
	 * <code>Client/pos/config/storecredit.properties</code> is set to "N".
	 *
	 * @author AakashGupta
	 * @param storeCreditTender
	 * @return Integer
	 */
	public Integer triggerStoreCreditInOldCO(MAXTenderStoreCreditIfc storeCreditTender) {
		if (logger.isDebugEnabled())
			logger.debug("MAXCertificateTransaction.updateStoreCreditinOldCO");
		Integer updateStatus = 2;

		if (isRollOutComplete())
			updateStatus = 2;

		else if (storeCreditTender.getStoreCredit() instanceof MAXStoreCreditIfc) {
			MAXStoreCreditDetails storeCreditDetails = new MAXStoreCreditDetails();
			MAXStoreCreditIfc storeCredit = (MAXStoreCreditIfc) storeCreditTender.getStoreCredit();
			String status = storeCreditTender.getAmountTender().getDoubleValue() > 0.0 ? "REDEEM" : "ISSUED";
			storeCreditDetails.setStoreCreditID(storeCredit.getStoreCreditID());
			storeCreditDetails.setValid(true);
			storeCreditDetails.setAmount(storeCredit.getAmount().getDoubleValue());
			storeCreditDetails.setStatus(status);
			if (storeCredit.getExpirationDate() != null)
				storeCreditDetails.setExpiratonDate(storeCredit.getExpirationDate().toFormattedString("dd-MM-yyyy"));
			storeCreditDetails.setCurrencyID(storeCredit.getCurrencyID());
			storeCreditDetails.setFirstName(storeCredit.getFirstName());
			storeCreditDetails.setLastName(storeCredit.getLastName());
			storeCreditDetails.setIdType(storeCredit.getPersonalIDType(Locale.getDefault()));
			String storeCreditDetailsXML = getStoreCreditAsXML(storeCreditDetails);

			DataAction[] dataActions = new DataAction[1];
			DataAction da = new DataAction();
			da.setDataOperationName("UpdateStoreCreditInOldCO");
			da.setDataObject(storeCreditDetailsXML);
			dataActions[0] = da;
			setDataActions(dataActions);
			try {
				updateStatus = (Integer) getDataManager().execute(this);
			} catch (DataException exception) {
				logger.error(
						"ERROR estabilishing connection with Central Office while updating Store Credit in Old CO");
				updateStatus = 0;
			}
		}
		return updateStatus;
	}

	/**
	 * Reads the property <code>IS_ROLLOUT_COMPLETE</code> from
	 * <code>Client/pos/config/storecredit.properties</code>.</br>
	 * Returns true if the value is 'Y' or "",false for 'N'
	 *
	 * @author Aakash Gupta
	 * @return boolean
	 */
	public boolean isRollOutComplete() {
		Properties storeCreditProperty = new Properties();
		String isRollOutComplete = "";
		InputStream is;
		
		try {
			is = new FileInputStream(new File("../config/storecredit.properties"));
			storeCreditProperty.load(is);
			isRollOutComplete = storeCreditProperty.getProperty("IS_ROLLOUT_COMPLETE", "Y").trim();
		}
		catch (FileNotFoundException e) {
			logger.error("ERROR!!! reading storecredit.properties from Server/config/");
		}catch (IOException exception) {
			logger.error("ERROR!!! reading storecredit.properties from Server/config/");
		}
		 
		return isRollOutComplete.equalsIgnoreCase("Y") || isRollOutComplete.equals("") ? true : false;
	}

	/**
	 * Updates the property <code>IS_ROLLOUT_COMPLETE</code> at
	 * <code>Client/pos/config/storecredit.properties</code> to 'Y' if the value
	 * for the same property received from ORCO 14.1 is 'Y' as well.
	 *
	 * @author Aakash Gupta
	 *
	 */
	public void updateProperty() {
		Properties storeCreditProperty = new Properties();
		String comment = "\n-------ROLLOUT COMPLETE-------";
		try {
			InputStream is = new FileInputStream(new File("../config/storecredit.properties"));
			OutputStream os = new FileOutputStream(new File("../config/storecredit.properties"));
			storeCreditProperty.load(is);
			storeCreditProperty.setProperty("IS_ROLLOUT_COMPLETE", "Y");
			storeCreditProperty.store(os, comment);
		} catch (IOException exception) {
			logger.error("ERROR!!! reading/updating storecredit.properties from Server/config/");
		}
	}

	/**
	 * Helper method to convert String date to EYS date.
	 *
	 * @author Aakash Gupta
	 *
	 */
	public EYSDate getEYSDate(String inputDate) {
		DateFormat dateFormat = null;
		if (inputDate.length() > 10)// If time too is included in the date.
			dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		else
			dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		EYSDate eysDate = new EYSDate();
		try {
			Date date = dateFormat.parse(inputDate);
			eysDate = new EYSDate(date);
		} catch (ParseException exception) {
			exception.printStackTrace();
			logger.error(
					"MAXCertificateTransaction.getEYSDate(String):"
					+ " ERROR!!! parsing store credit redemption/expiration date to EYSDate");
		}
		return eysDate;
	}

	/**
	 * Marshals the <code>MAXStoreCreditDetails</code> into an XML.
	 *
	 * @author Aakash Gupta
	 * @param storeCreditDetails
	 * @return String
	 */
	public String getStoreCreditAsXML(MAXStoreCreditDetails storeCreditDetails) {
		StringWriter stringWriter = new StringWriter();
		try {
			JAXBContext contextObj = JAXBContext.newInstance(MAXStoreCreditDetails.class);
			Marshaller marshaller = contextObj.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(storeCreditDetails, stringWriter);
		} catch (JAXBException exception) {
			exception.printStackTrace();
			logger.error(
					"MAXCertificateTransaction.getStoreCreditAsXML(MAXStoreCreditDetails): ERROR!!! marshalling store credit details.");
		}
		return stringWriter.toString();
	}
	
	//Added by Vaibhav LS Credit note code merging start Rev 2.0
		public boolean updateStoreCreditLockStatus(String storeCreditId,String lockFlag) {
			Boolean updateResult=false;
			HashMap<String,Object> storeCreditDetails= new HashMap<String,Object>();
			storeCreditDetails.put("StoreCreditId",storeCreditId);
			storeCreditDetails.put("lockFlag",lockFlag);		
			DataActionIfc[] dataActions = new DataActionIfc[1];
			DataAction dataAction = new DataAction();
			dataAction.setDataOperationName("updateStoreCreditLockStatus");
			dataAction.setDataObject(storeCreditDetails);
			dataActions[0] = dataAction;
			setDataActions(dataActions);

			try {
				updateResult=(Boolean)getDataManager().execute(this);
			} catch (DataException e) {
					logger.error(
							"ERROR estabilishing connection with Central Office while updating Store Credit in CO");
				}
			return updateResult;
		}
		//END Rev 2.0
	public TenderCertificateIfc readCertificatefromlocal(TenderCertificateIfc certificate) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "CertificateTransaction.readCertificate");

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();

        if (certificate instanceof TenderStoreCreditIfc)
        {
            da.setDataOperationName("LookupStoreCredit");
        }
        else if (certificate instanceof TenderGiftCertificateIfc)
        {
            da.setDataOperationName("LookupCertificate");
        }
        else
        {
            if (logger.isDebugEnabled()) logger.debug(
                    "Unknown Certificate: " + certificate);
        }

        da.setDataObject(certificate);
        dataActions[0] = da;
        setDataActions(dataActions);
        MAXTenderStoreCredit document = 
            (MAXTenderStoreCredit)getDataManager().execute(this);

        if (certificate instanceof TenderStoreCreditIfc) 
        {
            ((MAXTenderStoreCreditIfc) certificate).setStoreCredit(document.getStoreCredit());
        }
        else if (certificate instanceof TenderGiftCertificateIfc)
        {
            ((TenderGiftCertificateIfc) certificate).
                setDocument((GiftCertificateDocumentIfc)document);
        }

        if (logger.isDebugEnabled()) logger.debug(
                    "CertificateTransaction.readCertificate");

        return certificate;
    }
}
