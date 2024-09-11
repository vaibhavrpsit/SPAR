/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * Copyright (c) 2015 Lifestyle.    All Rights Reserved.  */

package max.retail.stores.pos.services.sale.validate;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author mohd.arif
 *
 */
public class MAXCapillaryCRM 
{
	private String CRM_URL = "";
	
	private String requestAction = "";
	
	private String requestMethod = "";
	
	private String requestMessage = "";
	
	private String connResponseCode = "";
	
	private String connResponseMessage = "";
	
	private String responseCode = "";
	
	private String responseMessage = "";
	
	private String itemStatus = "";
	
	private String itemStatusCode = "";
	
	private String itemStatusMsg = "";
	
	private boolean responseSuccess = false;
	
	private HashMap transData;

	private ArrayList failedRequestMessages = new ArrayList();

	
	public MAXCapillaryCRM()
	{
		failedRequestMessages = new ArrayList();
	}

	/**
	 * @return the cRM_URL
	 */
	public String getCRM_URL() {
		return CRM_URL;
	}

	/**
	 * @param cRM_URL the cRM_URL to set
	 */
	public void setCRM_URL(String cRM_URL) {
		CRM_URL = cRM_URL;
	}

	/**
	 * @return the requestAction
	 */
	public String getRequestAction() {
		return requestAction;
	}

	/**
	 * @param requestAction the requestAction to set
	 */
	public void setRequestAction(String requestAction) {
		this.requestAction = requestAction;
	}

	/**
	 * @return the requestMethod
	 */
	public String getRequestMethod() {
		return requestMethod;
	}

	/**
	 * @param requestMethod the requestMethod to set
	 */
	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	/**
	 * @return the requestMessage
	 */
	public String getRequestMessage() {
		return requestMessage;
	}

	/**
	 * @param requestMessage the requestMessage to set
	 */
	public void setRequestMessage(String requestMessage) {
		this.requestMessage = requestMessage;
	}

	/**
	 * @return the transData
	 */
	public HashMap getTransData() {
		return transData;
	}

	/**
	 * @param transData the transData to set
	 */
	public void setTransData(HashMap transData) {
		this.transData = transData;
	}

	/**
	 * @return the connResponseCode
	 */
	public String getConnResponseCode() {
		return connResponseCode;
	}

	/**
	 * @param connResponseCode the connResponseCode to set
	 */
	public void setConnResponseCode(String connResponseCode) {
		this.connResponseCode = connResponseCode;
	}

	/**
	 * @return the connResponseMessage
	 */
	public String getConnResponseMessage() {
		return connResponseMessage;
	}

	/**
	 * @param connResponseMessage the connResponseMessage to set
	 */
	public void setConnResponseMessage(String connResponseMessage) {
		this.connResponseMessage = connResponseMessage;
	}

	/**
	 * @return the responseCode
	 */
	public String getResponseCode() {
		return responseCode;
	}

	/**
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	/**
	 * @return the responseMessage
	 */
	public String getResponseMessage() {
		return responseMessage;
	}

	/**
	 * @param responseMessage the responseMessage to set
	 */
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	/**
	 * @return the responseSuccess
	 */
	public boolean isResponseSuccess() {
		return responseSuccess;
	}

	/**
	 * @param responseSuccess the responseSuccess to set
	 */
	public void setResponseSuccess(boolean responseSuccess) {
		this.responseSuccess = responseSuccess;
	}

	/**
	 * @return the itemStatus
	 */
	public String getItemStatus() {
		return itemStatus;
	}

	/**
	 * @param itemStatus the itemStatus to set
	 */
	public void setItemStatus(String itemStatus) {
		this.itemStatus = itemStatus;
	}

	/**
	 * @return the itemStatusCode
	 */
	public String getItemStatusCode() {
		return itemStatusCode;
	}

	/**
	 * @param itemStatusCode the itemStatusCode to set
	 */
	public void setItemStatusCode(String itemStatusCode) {
		this.itemStatusCode = itemStatusCode;
	}

	/**
	 * @return the itemStatusMsg
	 */
	public String getItemStatusMsg() {
		return itemStatusMsg;
	}

	/**
	 * @param itemStatusMsg the itemStatusMsg to set
	 */
	public void setItemStatusMsg(String itemStatusMsg) {
		this.itemStatusMsg = itemStatusMsg;
	}
	
	/**
	 * @return the failedRequestMessages
	 */
	public ArrayList getFailedRequestMessages() {
		return failedRequestMessages;
	}

	/**
	 * @param failedRequestMessages the failedRequestMessages to set
	 */
	public void setFailedRequestMessages(ArrayList failedRequestMessages) {
		this.failedRequestMessages = failedRequestMessages;
	}
	
	
	
}
