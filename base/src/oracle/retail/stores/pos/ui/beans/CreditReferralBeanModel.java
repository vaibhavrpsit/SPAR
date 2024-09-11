/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CreditReferralBeanModel.java /rgbustores_13.4x_generic_branch/3 2011/09/30 11:17:45 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   09/29/11 - Added Card Type to the Call Referral Screen, made it
 *                         depended upon credit tender only.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   4    360Commerce 1.3         4/25/2007 8:51:31 AM   Anda D. Cadar   I18N
 *        merge
 *   3    360Commerce 1.2         3/31/2005 4:27:32 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:20:27 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:14 PM  Robert Pearse   
 *
 *  Revision 1.4  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Domain imports
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc.TenderType;

/**
 * Data access to the CreditReferralBean
 */
@SuppressWarnings("serial")
public class CreditReferralBeanModel extends POSBaseBeanModel
{
    /** Authorization response */
    protected String fieldAuthResponse = "";
    /** Call referral list */
    protected String[] callReferralList = new String[0];
    /** merchant number */
    protected String merchantNumber = "";
    /** Charge amount */
    protected CurrencyIfc fieldChargeAmount = null;
    /** Approval code */
    protected String fieldApprovalCode = "";
    /** card sub type */
    protected String cardType = "";
    /** credit card types */
    protected String[] creditCardTypes = new String[0];
    /** tender type */
    protected TenderType tenderType;

    /**
     * Get the value of the AuthResponse field
     * @return the value of AuthResponse
     */
    public String getAuthResponse()
    {
        return fieldAuthResponse;
    }

    /**
     * Gets the call referral list.
     * @return the call referral list.
     */
    public String[] getCallReferralList()
    {
        return this.callReferralList;
    }

    /**
     * Sets the call referral list.
     * @param callReferralList
     */
    public void setCallReferralList(String[] callReferralList)
    {
        this.callReferralList = callReferralList;
    }

    /**
     * Gets the merchant number
     * @return the merchantNumber
     */
    public String getMerchantNumber()
    {
        return merchantNumber;
    }

    /**
     * Sets the merchant number
     * @param merchantNumber the merchantNumber to set
     */
    public void setMerchantNumber(String merchantNumber)
    {
        this.merchantNumber = merchantNumber;
    }

    /**
     * Get the value of the ChargeAmount field
     * @return the value of ChargeAmount
     */
    public CurrencyIfc getChargeAmount()
    {
        return fieldChargeAmount;
    }

    /**
     * Get the value of the ApprovalCode field
     * @return the value of ApprovalCode
     */
    public String getApprovalCode()
    {
        return fieldApprovalCode;
    }

    /**
     * Sets the AuthResponse field
     * @param authResponse value to be set for AuthResponse
     */
    public void setAuthResponse(String authResponse)
    {
        fieldAuthResponse = authResponse;
    }

    /**
     * Sets the ChargeAmount field
     * @param chargeAmount value to be set for ChargeAmount
     */
    public void setChargeAmount(CurrencyIfc chargeAmount)
    {
        fieldChargeAmount = chargeAmount;
    }

    /**
     * Sets the ApprovalCode field
     * @param approvalCode value to be set for ApprovalCode
     */
    public void setApprovalCode(String approvalCode)
    {
        fieldApprovalCode = approvalCode;
    }

    /**
     * Sets the card sub type value.
     * @param cardType
     */
    public void setCardType(String cardType)
    {
        this.cardType = cardType;
    }

    /**
     * Returns the cardType value.
     * @return the cardType value.
     */
    public String getCardType()
    {
        return this.cardType;
    }

    /**
     * Sets the list of credit card types.
     * @param creditCardTypes
     */
    public void setCreditCardTypes(String[] creditCardTypes)
    {
        this.creditCardTypes = creditCardTypes;
    }

    /**
     * Returns the list of credit card types.
     * @return
     */
    public String[] getCreditCardTypes()
    {
        return this.creditCardTypes;
    }

    /**
     * Sets the tender type for this referral bean model
     * @param tenderType
     */
    public void setTenderType(TenderType tenderType)
    {
        this.tenderType = tenderType;
    }

    /**
     * Gets the tender type for this referral bean model.
     * @return the tender type for this referral bean model.
     */
    public TenderType getTenderType()
    {
        return this.tenderType;
    }

    /**
     * Converts to a string representing the data in this Object
     * @returns string representing the data in this Object
     */
    public String toString()
    {
        StringBuilder buff = new StringBuilder();

        buff.append("Class: CheckReferralBeanModel Revision: " + revisionNumber + "\n");
        buff.append("AuthResponse [" + fieldAuthResponse + "]\n");
        if(callReferralList != null)
        {
            for(int i = 0; i < callReferralList.length; i++)
            {
                buff.append("CallReferralList[");
                buff.append(i);
                buff.append("] = ");
                buff.append(callReferralList[i]);
                buff.append("]\n");
            }
        }
        buff.append("MerchantNumber [" + merchantNumber + "]\n");
        buff.append("ChargeAmount   [" + fieldChargeAmount + "]\n");
        buff.append("ApprovalCode   [" + fieldApprovalCode + "]\n");
        buff.append("CardType       [" + cardType + "]\n");

        if(creditCardTypes != null)
        {
            for(int i = 0; i < creditCardTypes.length; i++)
            {
                buff.append("CreditCardTypes[");
                buff.append(i);
                buff.append("] = ");
                buff.append(creditCardTypes[i]);
                buff.append("]\n");
            }
        }
        return(buff.toString());
    }
}
