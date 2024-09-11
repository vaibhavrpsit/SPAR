/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/authorization/AuthorizationCargo.java /main/5 2014/07/01 13:33:27 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   06/05/14 - XbranchMerge
 *                         blarsen_bug18854403-ajb-call-ref-trans-cancel-bad-invoiceid
 *                         from rgbustores_14.0x_generic_branch
 *    blarsen   06/03/14 - Refactor: Moving call referral fields into their new
 *                         class.
 *    blarsen   02/04/14 - AJB requires original auth response for call
 *                         referrals. Adding this to appropriate
 *                         shuttles/cargos.
 *    blarsen   02/04/14 - Adding missing getters/setters so shuttles/etc are
 *                         not forced to access the data fields directly.
 *    asinton   11/14/13 - added code to carry the transaction archive name to
 *                         reversal and authorization services for transaction
 *                         archival to support potential reversal of pending
 *                         authorizations in the case of application crash
 *    asinton   08/02/12 - Call referral refactor
 *    asinton   07/02/12 - carry call referral authorization details from
 *                         Mobile POS to call referral site.
 *    blarsen   08/12/11 - Added callReferralApprovedAmount. This must be saved
 *                         since it is editable by the operator.
 *    cgreene   07/28/11 - added support for manager override for card decline
 *    cgreene   05/27/11 - move auth response objects into domain
 *    asinton   03/25/11 - Moved APF request and response objects to common
 *                         module.
 *    asinton   03/21/11 - new tender authorization service
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.authorization;

import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.ui.beans.CreditReferralBeanModel;

/**
 * Cargo for the Authorization service.  This cargo maintains the request and response
 * lists for authorization.
 *
 * @since 13.4
 */
public class AuthorizationCargo extends AbstractFinancialCargo
{
    private static final long serialVersionUID = -6947857149521681677L;

    /** List of Request objects */
    protected List<AuthorizeTransferRequestIfc> requestList;

    /** List of Response objects */
    protected List<AuthorizeTransferResponseIfc> responseList;

    /** current index of requests */
    protected int currentIndex = -1;

    /** CreditReferralBeanModel for use by MobilePOS */
    protected CreditReferralBeanModel creditReferralBeanModel;

    /** Call Referral Data for use by MobilePOS */
    protected CallReferralData callReferralData = new CallReferralData();

    /** transaction archive name */
    protected String transactionArchiveName;


    /**
     * Returns the current request object to be processed.
     * @return
     */
    public AuthorizeTransferRequestIfc getCurrentRequest()
    {
        AuthorizeTransferRequestIfc currentReqeust = null;
        if(requestList != null && requestList.size() > currentIndex)
        {
            currentReqeust = requestList.get(currentIndex);
        }
        return currentReqeust;
    }

    /**
     * Returns the current response object.
     * @return the current response object.
     */
    public AuthorizeTransferResponseIfc getCurrentResponse()
    {
        AuthorizeTransferResponseIfc currentResponse = null;
        if(getResponseList().size() > currentIndex)
        {
            currentResponse = getResponseList().get(currentIndex);
        }
        return currentResponse;
    }


    /**
     * Sets the list of response objects in this cargo.
     * @param list
     */
    public void setResponseList(List<AuthorizeTransferResponseIfc> list)
    {
        this.responseList = list;
    }

    /**
     * Returns the list of response objects.
     * @return the list of response objects.
     */
    public List<AuthorizeTransferResponseIfc> getResponseList()
    {
        if(responseList == null)
        {
            responseList = new ArrayList<AuthorizeTransferResponseIfc>();
        }
        return responseList;
    }

    /**
     * Adds a response object to the list.
     * @param response
     */
    public void addResponse(AuthorizeTransferResponseIfc response)
    {
        getResponseList().add(response);
    }

    /**
     * Sets the list of request objects in this cargo.
     * @param list
     */
    public void setRequestList(List<AuthorizeTransferRequestIfc> list)
    {
        this.requestList = list;
    }

    /**
     * Gets the list of request objects in this cargo.
     * @return the requestlist
     */
    public List<AuthorizeTransferRequestIfc> getRequestList()
    {
        return this.requestList;
    }


    /**
     * Increments the current index.  This method should only be called by the
     * EvaluateRequestListSite.
     */
    public void incrementCurrentIndex()
    {
        currentIndex++;
    }

    /**
     * Returns the creditReferralBeanModel value.
     * @return the creditReferralBeanModel
     */
    public CreditReferralBeanModel getCreditReferralBeanModel()
    {
        return creditReferralBeanModel;
    }

    /**
     * Sets the creditReferralBeanModel value.
     * @param creditReferralBeanModel the creditReferralBeanModel to set
     */
    public void setCreditReferralBeanModel(CreditReferralBeanModel creditReferralBeanModel)
    {
        this.creditReferralBeanModel = creditReferralBeanModel;
    }


    /**
     * Returns the <code>transactionArchiveName</code> value.
     * @return the transactionArchiveName
     */
    public String getTransactionArchiveName()
    {
        return transactionArchiveName;
    }

    /**
     * Sets the <code>transactionArchiveName</code> value.
     * @param transactionArchiveName the transactionArchiveName to set
     */
    public void setTransactionArchiveName(String transactionArchiveName)
    {
        this.transactionArchiveName = transactionArchiveName;
    }

    /**
     * Returns the current index to the request/response lists.
     * @return the currentIndex
     */
    public int getCurrentIndex()
    {
        return currentIndex;
    }

    /**
     * Sets the current index to the request/response lists.
     * @param currentIndex
     */
    public void setCurrentIndex(int currentIndex)
    {
        this.currentIndex = currentIndex;
    }

    /**
     * Returns the  callReferralData
     * @return the callReferralData
     */
    public CallReferralData getCallReferralData()
    {
        return callReferralData;
    }

    /**
     * Sets the  callReferralData
     * @param callReferralData
     */
    public void setCallReferralData(CallReferralData callReferralData)
    {
        this.callReferralData = callReferralData;
    }

}
