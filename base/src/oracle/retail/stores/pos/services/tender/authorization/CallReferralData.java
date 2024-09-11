/* ===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/authorization/CallReferralData.java /main/1 2014/07/01 13:33:27 blarsen Exp $
 * ===========================================================================
 * NOTES
 *
 * This class contains call referral data that is primarily used by MPOS.
 *
 * This is required because a call referral on MPOS takes two distinct webservice calls.
 * This is the data from the original auth request* which is required in the second
 * auth request that contains the auth code acquired by the operator via a call to the bank.
 *
 *   * which "failed" with a call referral response
 *
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   06/04/14 - Added call referral traceNumber & entryMethod to fix
 *                         American Eagle SRs 18854403 & 18854433.
 *    blarsen   06/04/14 - Original version.  Refactor.  Moved many call referral
 *                         related fields from many cargos/shuttles.  Should
 *                         greatly simplify change in the event more fields
 *                         are needed for call referral processing.
 *
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.tender.authorization;

import java.io.Serializable;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc.TenderType;
import oracle.retail.stores.domain.utility.EntryMethod;

public class CallReferralData implements Serializable
{
    private static final long serialVersionUID = 704130021912362682L;

    /** Call referral approval code */
    private String approvalCode;
    /** Call referral approved amount */
    private CurrencyIfc approvedAmount;
    /** call referral account number token */
    private String accountNumberToken;
    /** call referral tender type */
    private TenderType tenderType;
    /** call referral masked account number */
    private String maskedAccountNumber;
    /** call referral tender sub type */
    private String tenderSubType;
    /** call referral giftcard account number */
    private String giftcardAccountNumber;
    /** original auth response
     * Note that some payment systems (AJB) require the entire
     * original auth response for call referrals */
    private Object originalAuthResponse;

    /** Call referral trace number */
    private String traceNumber;

    /** call referral entry method */
    private EntryMethod entryMethod;

    public CallReferralData()
    {
    }



    /**
     * Gets the approvalCode value.
     * @return the approvalCode
     */
    public String getApprovalCode()
    {
        return approvalCode;
    }

    /**
     * Sets the approvalCode value.
     * @param approvalCode the approvalCode to set
     */
    public void setApprovalCode(String approvalCode)
    {
        this.approvalCode = approvalCode;
    }

    /**
     * Gets the approvedAmount value.
     * @return the approvedAmount
     */
    public CurrencyIfc getApprovedAmount()
    {
        return approvedAmount;
    }

    /**
     * Sets the approvedAmount value.
     * @param approvedAmount the approvedAmount to set
     */
    public void setApprovedAmount(CurrencyIfc approvedAmount)
    {
        this.approvedAmount = approvedAmount;
    }

    /**
     * @return the accountNumberToken
     */
    public String getAccountNumberToken()
    {
        return accountNumberToken;
    }

    /**
     * @param accountNumberToken the accountNumberToken to set
     */
    public void setAccountNumberToken(String accountNumberToken)
    {
        this.accountNumberToken = accountNumberToken;
    }

    /**
     * @return the tenderType
     */
    public TenderType getTenderType()
    {
        return tenderType;
    }

    /**
     * @param tenderType the tenderType to set
     */
    public void setTenderType(TenderType tenderType)
    {
        this.tenderType = tenderType;
    }

    /**
     * @return the call referral masked account number
     */
    public String getMaskedAccountNumber()
    {
        return this.maskedAccountNumber;
    }

    /**
     * @param giftcardNumber the masked account number
     */
    public void setMaskedAccountNumber(String maskedAccountNumber)
    {
        this.maskedAccountNumber = maskedAccountNumber;
    }

    /**
     * @return the call referral tender sub type
     */
    public String getTenderSubType()
    {
        return this.tenderSubType;
    }

    /**
     * @param subType the call referral tender sub type
     */
    public void setTenderSubType(String tendersubType)
    {
        this.tenderSubType = tendersubType;
    }

    /**
     * @return the call referral giftcard account number
     */
    public String getGiftcardAccountNumber()
    {
        return this.giftcardAccountNumber;
    }

    /**
     * @param giftcardNumber the giftcard account number
     */
    public void setGiftcardAccountNumber(String giftcardAccountNumber)
    {
        this.giftcardAccountNumber = giftcardAccountNumber;
    }

    /**
     * @return the original raw auth response
     */
    public Object getOriginalAuthResponse()
    {
        return originalAuthResponse;
    }

    /**
     * @param origAuthResponse the original raw auth response
     */
    public void setOriginalAuthResponse(Object originalAuthResponse)
    {
        this.originalAuthResponse = originalAuthResponse;
    }

    /**
     * @return the trace number
     */
    public String getTraceNumber()
    {
        return traceNumber;
    }

    /**
     * @param traceNumber from the original auth response
     */
    public void setTraceNumber(String traceNumber)
    {
        this.traceNumber = traceNumber;
    }

    /**
     * @return the entry method
     */
    public EntryMethod getEntryMethod()
    {
        return entryMethod;
    }

    /**
     * @param entryMethod the entry method from the original auth response
     */
    public void setEntryMethod(EntryMethod entryMethod)
    {
        this.entryMethod = entryMethod;
    }

    /**
     * Returns default display string.
     * <P>
     *
     * @return String representation of object
     **/
    public String toString()
    {
        StringBuffer strResult = new StringBuffer();
        strResult.append("Class:  CallReferralData\n");

        strResult.append("ApprovalCode: " + getApprovalCode() + "\n");
        strResult.append("ApprovedAmount: " + getApprovedAmount() + "\n");
        strResult.append("AccountNumberToken: " + getAccountNumberToken() + "\n");
        strResult.append("TenderType: " + getTenderType() + "\n");
        strResult.append("MaskedAccountNumber: " + getMaskedAccountNumber() + "\n");
        strResult.append("TenderSubType: " + getTenderSubType() + "\n");
        strResult.append("GiftcardAccountNumber: " + getGiftcardAccountNumber() + "\n");
        strResult.append("OriginalAuthResponse: " + getOriginalAuthResponse() + "\n");
        strResult.append("TraceNumber: " + getTraceNumber() + "\n");
        strResult.append("EntryMethod: " + getEntryMethod() + "\n");
        return (strResult.toString());
    }

}