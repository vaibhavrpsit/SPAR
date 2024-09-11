/* *****************************************************************************************
 *Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	Rev 1.2 	May 17, 2024		Kamlesh Pant		Store Credit OTP:
 *	Rev 1.1     Jan 05,2018 		Shilpa Rawal :		GC_eGV_CN Redemption Cross OU
 *
 * Initial Draft:		Aakash Gupta :		Dec 5th,2015
 * *******************************************************************************************/

package max.retail.stores.enterpriseconnection.storecredit;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import max.retail.stores.domain.arts.MAXCertificateTransaction;
import max.retail.stores.domain.tender.MAXTenderStoreCreditIfc;
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
public class MAXEnterpriseReadStoreCredit extends EnterpriseDataOperation {

	protected static Logger log = Logger.getLogger(MAXEnterpriseReadStoreCredit.class);
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
	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc dataAction)
			throws DataException {
		if (dataTransaction instanceof MAXCertificateTransaction) {
			getWSConnector();
			MAXTenderStoreCreditIfc tenderStoreCredit = (MAXTenderStoreCreditIfc) dataAction.getDataObject();
			String storeCreditNumber = tenderStoreCredit.getStoreCreditID();
			Boolean trainingModeFlag = tenderStoreCredit.isTrainingMode();
			MAXStoreCreditWSService storeCreditService = (MAXStoreCreditWSService) connector.getServiceClass();
			MAXStoreCreditWSIfc storeCreditWS = storeCreditService.getMAXStoreCreditWSPort();
			String storeCreditDetailsXML = storeCreditWS.getStoreCreditDetails(storeCreditNumber, trainingModeFlag);
			if (storeCreditDetailsXML != null) {
				HashMap<String, Object> storeCreditMap = generateMapFromDetails(storeCreditDetailsXML);
				dataTransaction.setResult(storeCreditMap);
			}
		}
	}

	/**
	 * Unmarshalls the store credit details xml and creates a HashMap from the
	 * values.
	 *
	 * @author Aakash Gupta
	 * @param storeCreditDetailsXML
	 * @return HashMap<String, Object>
	 */
	public HashMap<String, Object> generateMapFromDetails(String storeCreditDetailsXML) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		HashMap<String, Object> storeCreditMap = new HashMap<String, Object>();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(storeCreditDetailsXML));
			Document document = builder.parse(is);
			Element rootNode = document.getDocumentElement();
			
			//Rev 1.2 Changes Starts
			String issuingOrganizationUnit,id,expirationDate,redemptionTransaction,redemptionDate,status,mobileNumber;
		//Rev 1.2 Changes ends
		
			Boolean isValid,isRollOutComplete,storeCreditStatus;
			 isRollOutComplete = new Boolean(
					rootNode.getElementsByTagName("IS_ROLLOUT_COMPLETE").item(0).getTextContent());
			storeCreditMap.put("IsRollOutComplete", isRollOutComplete);
			
			storeCreditStatus = new Boolean(rootNode.getElementsByTagName("Store_Credit_Lock").item(0).getTextContent());
			storeCreditMap.put("IsStoreCreditLock", storeCreditStatus);
			
			log.debug("Store Credit status karni - "+storeCreditStatus);
			//System.out.println("before isRollOutComplete "+isRollOutComplete);
			
			if (!isRollOutComplete) {
				 isValid = new Boolean(rootNode.getElementsByTagName("IS_VALID").item(0).getTextContent());
				storeCreditMap.put("IsValid", isValid);

				 status = rootNode.getElementsByTagName("STATUS").item(0).getTextContent();
				storeCreditMap.put("Status", status);

				if (isValid) {
					 id = rootNode.getElementsByTagName("ID").item(0).getTextContent();
					storeCreditMap.put("ID", id);

					Double balanceAmount = new Double(
							rootNode.getElementsByTagName("BALANCE_AMOUNT").item(0).getTextContent());
					storeCreditMap.put("BalanceAmount", balanceAmount);

					 expirationDate = rootNode.getElementsByTagName("EXPIRATION_DATE").item(0).getTextContent();
					storeCreditMap.put("ExpirationDate", expirationDate);

					 redemptionTransaction = rootNode.getElementsByTagName("REDEMPTION_TRANSACTION").item(0)
							.getTextContent();
					storeCreditMap.put("RedemptionTransaction", redemptionTransaction);

					 redemptionDate = rootNode.getElementsByTagName("REDEMPTION_DATE").item(0).getTextContent();
					storeCreditMap.put("RedemptionDate", redemptionDate);
					
					// Changes for Rev 1.2 Starts
					mobileNumber = rootNode.getElementsByTagName("mobile").item(0).getTextContent();
					storeCreditMap.put("mobile", mobileNumber);
					// Changes for Rev 1.2 ends
					
					// Changes for Rev 1.1 Starts	
					
					if(
							//rootNode.getElementsByTagName("Issuing_OU_ID")!=null && 
							rootNode.getElementsByTagName("Issuing_OU_ID").getLength()>0){
					 issuingOrganizationUnit = rootNode.getElementsByTagName("Issuing_OU_ID").item(0).getTextContent();
					storeCreditMap.put("issuingOrganizationUnit", issuingOrganizationUnit);
					}
					// Changes for Rev 1.1 ends
				}
			}
		} catch (ParserConfigurationException exception) {
			exception.printStackTrace();
		}
		catch (IOException exception) {
			exception.printStackTrace();
		}
		catch (SAXException exception) {
			exception.printStackTrace();
		}
		return storeCreditMap;
	}

}
