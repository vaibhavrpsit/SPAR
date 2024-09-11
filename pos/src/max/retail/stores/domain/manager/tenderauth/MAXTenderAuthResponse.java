/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.manager.tenderauth;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import oracle.retail.stores.domain.utility.EYSDate;

//-------------------------------------------------------------------------
/**
 * TenderAuthResponse object
 * 
 * @version $Revision: 4$
 **/
// -------------------------------------------------------------------------
public class MAXTenderAuthResponse implements MAXTenderAuthConstantsIfc, Serializable {
	// This id is used to tell
	// the compiler not to generate a
	// new serialVersionUID.
	//
	static final long serialVersionUID = 8658583427571750251L;

	protected int actionCode = NO_ACTION;

	// -----String Formats--------//
	protected String responseCode = ""; // Valid values defined in
										// TenderAuthConstantsIfc
	protected String approvalCode = ""; // Alphanumeric - undefined length
	protected String referenceCode = ""; // Alphanumeric - undefined length
	protected String authorizationDate = ""; // 8 Digit numeric string
												// (MMddyyyy)
	protected String authorizationTime = ""; // 6 Digit numeric string (HHmmss)
	protected String responseText = ""; // Alphanumeric - undefined length
	protected String referralPhone = ""; // 10 Digit numeric separated by dashes
											// = "123-456-7890"
	protected String settlementData = ""; // Alphanumeric - undefined length

	// ---------------------------------------------------------------------
	/**
	 * Default constructor.
	 **/
	// ---------------------------------------------------------------------
	public MAXTenderAuthResponse() {
	}

	// ---------------------------------------------------------------------
	/**
	 * Constructor sets the responseCode value.
	 * 
	 * @param newResponseCode
	 *            String
	 **/
	// ---------------------------------------------------------------------
	public MAXTenderAuthResponse(String newResponseCode) {
		responseCode = newResponseCode;
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the responseCode.
	 * <P>
	 * 
	 * @return String responseCode
	 **/
	// ---------------------------------------------------------------------
	public String getResponseCode() {
		return responseCode;
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets the responseCode value.
	 * 
	 * @param newResponseCode
	 *            String
	 **/
	// ---------------------------------------------------------------------
	public void setResponseCode(String newResponseCode) {
		responseCode = newResponseCode;
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the approvalCode.
	 * <P>
	 * 
	 * @return String approvalCode
	 **/
	// ---------------------------------------------------------------------
	public String getApprovalCode() {
		return approvalCode;
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets the approvalCode value.
	 * 
	 * @param newApprovalCode
	 *            String
	 **/
	// ---------------------------------------------------------------------
	public void setApprovalCode(String newApprovalCode) {
		approvalCode = newApprovalCode.trim(); // result may no chars
		if (approvalCode.length() == 0) {
			approvalCode = null;
		}
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the referenceCode.
	 * <P>
	 * 
	 * @return String referenceCode
	 **/
	// ---------------------------------------------------------------------
	public String getReferenceCode() {
		return referenceCode;
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets the referenceCode value.
	 * 
	 * @param newReferenceCode
	 *            String
	 **/
	// ---------------------------------------------------------------------
	public void setReferenceCode(String newReferenceCode) {
		referenceCode = newReferenceCode.trim(); // result may no chars
		if (referenceCode.length() == 0) {
			referenceCode = null;
		}
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the authorizationDate.
	 * <P>
	 * 
	 * @return String authorizationDate
	 **/
	// ---------------------------------------------------------------------
	public String getAuthorizationDate() {
		return authorizationDate;
	}

	// -------------------------------------------------------------------------
	/**
	 * Sets the authorization date.
	 * 
	 * @param newAuthorizationDate
	 *            The authorization date.
	 **/
	// -------------------------------------------------------------------------
	public void setAuthorizationDate(String newAuthorizationDate) {
		authorizationDate = newAuthorizationDate;
	}

	// -------------------------------------------------------------------------
	/**
	 * Sets the authorization time.
	 * 
	 * @param newAuthorizationTime
	 *            The authorization time.
	 **/
	// -------------------------------------------------------------------------
	public void setAuthorizationTime(String newAuthorizationTime) {
		authorizationTime = newAuthorizationTime;
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the authorizationTime.
	 * <P>
	 * 
	 * @return String authorizationTime
	 **/
	// ---------------------------------------------------------------------
	public String getAuthorizationTime() {
		return authorizationTime;
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the responseText.
	 * <P>
	 * 
	 * @return String responseText
	 **/
	// ---------------------------------------------------------------------
	public String getResponseText() {
		return responseText;
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets the responseText value.
	 * 
	 * @param newResponseText
	 *            String
	 **/
	// ---------------------------------------------------------------------
	public void setResponseText(String newResponseText) {
		responseText = newResponseText;
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the actionCode.
	 * <P>
	 * 
	 * @return String actionCode
	 **/
	// ---------------------------------------------------------------------
	public int getActionCode() {
		return actionCode;
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets the actionCode value.
	 * 
	 * @param newActionCode
	 *            String
	 **/
	// ---------------------------------------------------------------------
	public void setActionCode(int newActionCode) {
		actionCode = newActionCode;
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the referralPhone.
	 * <P>
	 * 
	 * @return String referralPhone
	 **/
	// ---------------------------------------------------------------------
	public String getReferralPhone() {
		return referralPhone;
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets the referralPhone value.
	 * 
	 * @param newReferralPhone
	 *            String
	 **/
	// ---------------------------------------------------------------------
	public void setReferralPhone(String newReferralPhone) {
		referralPhone = newReferralPhone;
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the settlementData.
	 * <P>
	 * 
	 * @return String settlementData
	 **/
	// ---------------------------------------------------------------------
	public String getSettlementData() {
		return settlementData;
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets the settlementData value.
	 * 
	 * @param newSettlementData
	 *            String
	 **/
	// ---------------------------------------------------------------------
	public void setSettlementData(String newSettlementData) {
		settlementData = newSettlementData;
	}

	/**
	 * Covert the string to an EYSDate
	 * 
	 * @param date
	 * @return
	 */
	public EYSDate getAuthorizationDateTime() {
		String sDateTime = authorizationDate + authorizationTime;
		EYSDate retDate = null;
		try {
			SimpleDateFormat format = new SimpleDateFormat("MMddyyyyHHmmss");
			retDate = new EYSDate(format.parse(sDateTime));
		} catch (Exception e) {
			// any exception causes a return of null
		}

		return retDate;
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns attribute descriptor String
	 * <P>
	 * 
	 * @return String formatted attribute descriptor
	 **/
	// ---------------------------------------------------------------------
	public String toString() {
		String values = "\n" + "ResponseCode:                  [" + responseCode + "]\n"
				+ "ResponseText:                  [" + responseText + "]\n" + "ApprovalCode:                  ["
				+ approvalCode + "]\n" + "ReferenceCode:                 [" + referenceCode + "]\n"
				+ "AuthorizationDate:             [" + authorizationDate + "]\n" + "AuthorizationTime:             ["
				+ authorizationTime + "]\n" + "ActionCode:                    [" + actionCode + "]\n"
				+ "ReferralPhone:                 [" + referralPhone + "]\n" + "SettlementData:                ["
				+ settlementData + "]\n";

		return values;
	}
}
