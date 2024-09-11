/* =============================================================================
* Copyright (c) 2003, 2014, Oracle and/or its affiliates. All rights reserved.
 * =============================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/TenderCargo.java /main/29 2014/07/01 13:33:27 blarsen Exp $
 * =============================================================================
 * NOTES
 *
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   06/05/14 - XbranchMerge
 *                         blarsen_bug18854403-ajb-call-ref-trans-cancel-bad-invoiceid
 *                         from rgbustores_14.0x_generic_branch
 *    blarsen   06/03/14 - Refactor: Moving call referral fields into their new
 *                         class.
 *    asinton   02/20/14 - added flag to suppress gift card activations in the
 *                         sale complete tour when a call referral was
 *                         performed
 *    asinton   02/10/14 - reworked flow for gift card activation error
 *                         scenarios
 *    blarsen   02/04/14 - AJB requires original auth response for call
 *                         referrals. Adding this to appropriate
 *                         shuttles/cargos.
 *    abondala  09/04/13 - initialize collections
 *    asinton   08/02/12 - Call referral refactor
 *    asinton   07/02/12 - carry call referral authorization details from
 *                         Mobile POS to call referral site.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    sgu       10/17/11 - prompt for card swipe or manual entry once card
 *                         tender buttons are clicked
 *    rrkohli   08/29/11 - fix for 'Forward Port: APPLICATION CRASH-WHEN
 *                         TENDERING WITH FOREIGN CURRENCY'
 *    asinton   08/12/11 - added boolean attribute tenderCanceled
 *    cgreene   08/09/11 - formatting and removed deprecated code
 *    blarsen   07/19/11 - Moved tendersNeedReversal() method to ReversalCargo
 *                         (new).
 *    blarsen   07/12/11 - Added helper method tendersNeedReversal().
 *    cgreene   07/12/11 - update generics
 *    icole     06/10/11 - Correct merge problem
 *    rrkohli   05/06/11 - pos ui quickwin
 *    jkoppolu  04/01/11 - Added method isStoreCreditUsed which is used to
 *                         preveent the reuse of store credit in a transaction.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *    rkar      11/17/08 - View refresh to 081112.2142 label
 *    rkar      11/12/08 - Adds/changes for POS-RM integration
 *    rkar      11/07/08 - Additions/changes for POS-RM integration
 *    cgreene   11/06/08 - add isCollected to tenders for printing just
 *                         collected tenders
 *    abondala  11/06/08 - updated files related to reason codes
 *    abondala  11/05/08 - updated files related to reason codes
 *
 * =============================================================================
 | $Log:
 |  10   360Commerce 1.9         5/29/2008 11:18:36 AM  Alan N. Sinton  CR
 |       31655: Code to allow refund of monies to multiple gift cards.  Code
 |       changes reviewed by Dan Baker.
 |  9    360Commerce 1.8         5/7/2008 12:05:43 PM   Jack G. Swan    Changed
 |        to support putting returned deposits from Layaway and Order
 |       delete/cancel transactions onto new and existing giftcards.  This
 |       code was reviewed by Brett Larson.
 |  8    360Commerce 1.7         4/27/2008 5:28:27 PM   Charles D. Baker CR
 |       31482 - Updated the journalResponse method of GetResponseSite to
 |       intelligently journal entries with the appropriate journal type
 |       (Trans or Not Trans). Code Review by Tony Zgarba.
 |  7    360Commerce 1.6         4/25/2007 8:52:45 AM   Anda D. Cadar   I18N
 |       merge
 |
 |  6    360Commerce 1.5         8/2/2006 4:59:53 PM    Brendan W. Farrell
 |       Create a change tender during a depletion of a gift card.
 |  5    360Commerce 1.4         2/15/2006 5:13:02 AM   Akhilashwar K. Gupta
 |       Modified to Fix CR 8235
 |  4    360Commerce 1.3         1/22/2006 11:45:03 AM  Ron W. Haight   removed
 |        references to com.ibm.math.BigDecimal
 |  3    360Commerce 1.2         3/31/2005 4:30:22 PM   Robert Pearse
 |  2    360Commerce 1.1         3/10/2005 10:25:53 AM  Robert Pearse
 |  1    360Commerce 1.0         2/11/2005 12:14:48 PM  Robert Pearse
 | $
 | Revision 1.22  2006/02/14 22:03:55  Akhilashwar
 | @scr 8235 Added new attribute giftCard
 |
 | Revision 1.21.2.1  2004/11/01 22:03:55  bwf
 | @scr 7388 Fixed quantity of traveler's check being used as change amount.
 |
 | Revision 1.21  2004/09/27 22:33:09  bwf
 | @scr 7244 Merged 2 versions of abstractfinancialcargo.
 |
 | Revision 1.20  2004/08/19 21:55:41  blj
 | @scr 6855 - Removed old code and fixed some flow issues with gift card credit.
 |
 | Revision 1.19  2004/08/16 18:30:45  lzhao
 | @scr 5421: remove unused import.
 |
 | Revision 1.18  2004/08/12 20:46:35  bwf
 | @scr 6567, 6069 No longer have to swipe debit or credit for return if original
 |                             transaction tendered with one debit or credit.
 |
 | Revision 1.17  2004/07/22 22:38:41  bwf
 | @scr 3676 Add tender display to ingenico.
 |
 | Revision 1.16  2004/07/16 22:12:05  epd
 | @scr 4268 Changing flows to add gift card credit
 |
 | Revision 1.15  2004/06/19 17:33:33  bwf
 | @scr 5205 These are the overhaul changes to the Change Due Options
 |                   screen and max change calculations.
 |
 | Revision 1.14  2004/04/28 15:46:37  blj
 | @scr 4603 - Fix gift card change due defects.
 |
 | Revision 1.13  2004/04/13 16:30:07  bwf
 | @scr 4263 Decomposition of store credit.
 |
 | Revision 1.12  2004/04/08 19:30:59  bwf
 | @scr 4263 Decomposition of Debit and Credit.
 |
 | Revision 1.11  2004/03/26 04:20:19  crain
 | @scr 4105 Foreign Currency
 |
 | Revision 1.10  2004/03/25 14:20:06  crain
 | @scr 4105 Foreign Currency
 |
 | Revision 1.9  2004/03/22 15:51:03  crain
 | @scr 4105 Foreign Currency
 |
 | Revision 1.8  2004/03/16 18:30:41  cdb
 | @scr 0 Removed tabs from all java source code.
 |
 | Revision 1.7  2004/02/27 16:39:40  bjosserand
 | @scr 0 Mail Bank Check
 |
 | Revision 1.6  2004/02/27 01:13:01  bjosserand
 | @scr 0 Mail Bank Check
 |
 | Revision 1.5  2004/02/26 19:47:04  bjosserand
 | @scr 0 Mail Bank Check
 |
 | Revision 1.4  2004/02/20 17:01:09  bjosserand
 | @scr 0 Mail Bank Check
 | Revision 1.3 2004/02/12 16:48:22 mcs Forcing head revision
 |
 | Revision 1.2 2004/02/11 21:22:51 rhafernik @scr 0 Log4J conversion and code cleanup
 |
 | Revision 1.1.1.1 2004/02/11 01:04:12 cschellenger updating to pvcs 360store-current
 |
 |
 |
 | Rev 1.13 Feb 06 2004 16:54:58 bjosserand Mail Bank Check.
 |
 | Rev 1.12 Feb 01 2004 13:37:40 bjosserand Mail Bank Check.
 |
 | Rev 1.11 Jan 30 2004 15:13:26 blj gift card refund issue implementation
 |
 | Rev 1.9 Jan 29 2004 07:23:14 blj added gift card number get/set to handle gift card refunds
 |
 | Rev 1.8 Jan 28 2004 13:16:58 bjosserand Mail Bank Check.
 |
 | Rev 1.7 Jan 06 2004 10:16:30 rsachdeva Refactoring Resolution for POS SCR-3551: Tender using Canadian
 | Cash/Canadian Travelers Check/Canadian Check
 |
 | Rev 1.6 Dec 05 2003 13:43:00 rsachdeva Screen ID Resolution for POS SCR-3551: Tender using Canadian Cash
 |
 | Rev 1.5 Dec 04 2003 10:15:38 epd updates for Debit
 |
 | Rev 1.4 Nov 14 2003 16:42:46 epd removed lots of dead code
 |
 | Rev 1.3 Nov 07 2003 17:04:48 bwf Added micr count. Resolution for 3429: Check/ECheck Tender
 |
 | Rev 1.2 Nov 07 2003 16:54:42 cdb Reverted to use classes from extendyourstore for AbstractFinancialCargo
 | and RetailTransactionCargoIfc. Resolution for 3430: Sale Service Refactoring
 |
 | Rev 1.1 Nov 05 2003 23:47:52 cdb Modified to use _360commerce classes. Resolution for 3430: Sale Service
 | Refactoring
 |
 | Rev 1.0 Nov 04 2003 11:17:54 epd Initial revision.
 |
 | Rev 1.0 Oct 23 2003 17:29:52 epd Initial revision.
 |
 | Rev 1.0 Oct 17 2003 13:06:48 epd Initial revision.
 |
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.manager.rm.RPIFinalResultIfc;
import oracle.retail.stores.domain.manager.rm.RPIRequestIfc;
import oracle.retail.stores.domain.manager.rm.RPIResponseIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.tour.application.tourcam.ObjectRestoreException;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamSnapshot;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.RetailTransactionCargoIfc;
import oracle.retail.stores.pos.services.tender.authorization.CallReferralData;
import oracle.retail.stores.pos.services.tender.tdo.TenderTDOConstants;
import oracle.retail.stores.pos.ui.beans.CreditReferralBeanModel;

import org.apache.log4j.Logger;

/**
 * Data and methods common to the sites in Tender Service.
 *
 */
public class TenderCargo extends AbstractFinancialCargo implements RetailTransactionCargoIfc, Serializable
{
    /** Serial Version UID */
    private static final long serialVersionUID = 3594843217203564921L;

    /** The logger to which log messages will be sent. */
    protected static final Logger logger = Logger.getLogger(TenderCargo.class);

    /**
     * The transaction to which tender line items are added
     */
    protected TenderableTransactionIfc transaction = null;

    /**
     * This flag is used to indicate that update was selected without modifying anything.
     */
    protected boolean updateWithoutModify = false;

    /**
     * This flag is used to indicate that an invalid postal code was entered
     */
    protected boolean postalCodeInvalid = false;

    /** Indicates transaction in progress */
    protected boolean transactionInProgress = false;

    /**
     * the customer
     */
    protected CustomerIfc customer;
    /**
     * Keeps track of the number of times micr has been used.
     */
    protected int micrCounter = 0;

    /**
     * The CurrencyIfc representation of the amount entered on GetTender
     */
    protected CurrencyIfc m_currentAmount = null;

    /**
     * This array contains a list of SaleReturnTransacions on which returns have been completed.
     */
    protected SaleReturnTransactionIfc[] originalReturnTransactions;

    /**
     * Contains credit/debit swipe information if swipe occurred before tender service
     */
    protected MSRModel preTenderMSRModel;

    /**
     * Indicates whether or not a card was swiped ahead
     */
    protected Boolean swipeAhead = false;

    /**
     * Determines which letter to use to go to the start site in the new sub tour.
     */
    protected String subTourLetter = null;

    /**
     * Flag to indicate if tender canceled.
     */
    protected boolean tenderCanceled = false;

    /**
     * Flag to indicate a refund should be applied to the original card tender
     */
    protected boolean refundToOriginalCard = false;

    ////////////////////////////////////////////////////////////////
    // ADO modifications
    ////////////////////////////////////////////////////////////////

    /** Array of original return transaction ADOs. */
    protected RetailTransactionADOIfc[] originalReturnTxnADOs;

    /** Container for tender information as entered from UI or device. */
    protected HashMap<String,Object> tenderAttributes = new HashMap<String,Object>(0);

    /** Stores the current tender during validation failures for overrides */
    protected TenderADOIfc tender;

    /** Stores the current tender for line display **/
    protected TenderADOIfc lineDisplayTender = null;

    /** gift card number * */
    protected String giftCardNumber = "";

    /** gift card **/
    protected GiftCardIfc giftCard = null;

    /** This flag indicates whether the item that was entered was scanned or typed. * */
    protected boolean itemScanned = false;

    /** This flag indicates whether a customer was 'linked' from the find or add customer service. * */
    protected boolean FindOrAddOrUpdateLinked = false;

    /** This field holds the customer ID type * */
    protected String idType;

    /** This is the phone type (used with customer capture) * */
    protected int phoneType;

    /**
     * The item number last entered
     */
    protected BigDecimal itemQuantity = BigDecimalConstants.ONE_AMOUNT;

    /** first time entering change due **/
    protected boolean firstTimeChangeDue = true;

    /** Set and read in RefunLimitActionSite */
    protected String refundTenderLetter = null;

    /** Used for Next on RefundOptions screen. **/
    protected String refundOptionsLetter = null;

    /** Used for next of refund optinso screen. **/
    protected TenderADOIfc nextTender = null;

    /** Used to determine whether or not cash was pressed or not */
    protected boolean cashOnlyOption = false;

    /** Used to determine whether or not a giftcard is being used for refund. */
    protected boolean giftCardCreditIssue = false;

    protected LocalizedCodeIfc localizedPersonalIDCode = DomainGateway.getFactory().getLocalizedCode();

    protected CodeListIfc personalIDTypes;

    /** Authorization Transaction Type. */
    protected int authorizationTransactionType = -1;

    /** Used for saving returnResponse */
    protected RPIResponseIfc 	returnResponse;

    /** Used for saving return request */
    protected RPIRequestIfc  	returnRequest;

    /** Used for saving return final result */
    protected RPIFinalResultIfc returnResult;

    /** Saving refund tender override approval, used for spliting refund tenders. */
    protected boolean refundTenderOverrideApproval = false;

    /** The sales associate */
    protected EmployeeIfc employee;

    /** CreditReferralBeanModel for use by MobilePOS */
    protected CreditReferralBeanModel creditReferralBeanModel;

    //////////////////////////////////////////////////////////////////
    // End ADO modifications
    //////////////////////////////////////////////////////////////////

    /** call referral data */
    protected CallReferralData callReferralData = new CallReferralData();

    /** flag to indicate no signature capture */
    protected boolean noSignatureCapture = false;

    /**
     * Indicates that gift card activations have been canceled.
     */
    protected boolean giftCardActivationsCanceled = false;

    /** flag to indicate that gift card activation should be suppressed */
    protected boolean suppressGiftCardActivation = false;
    
    /** flag to indicate if capture customer is required */
    protected boolean captureCustomerRequired = false;

    /**
     * This is the constructor. It initializes the current tender amount and the alternate tender amount.
     */
    public TenderCargo()
    {
    }

    /**
     * Links the CustomerIfc to this transaction. 1. We should find a way around using reflection utility here. 2. The
     * customer IU implies this link may not happen, but it always will, whether the user selects 'Done' or 'Link'.
     *
     * @param customer
     *            is the CustomerIfc object to link to this transaction
     */
    public void linkCustomerToTransaction(CustomerIfc customer)
    {
        transaction.linkCustomer(customer);
    }

    /**
     * Saves the transaction in the cargo
     *
     * @param trans
     *            TenderableTransactionIfc being tendered
     */
    public void setTransaction(TenderableTransactionIfc trans)
    {
        transaction = trans;
    }

    /**
     * Sets the updateFailure flag
     *
     * @param failure value that indicates whether CustomerIfc DB update failed
     */
    public void setUpdateWithoutModify(boolean failure)
    {
        updateWithoutModify = failure;
    }

    /**
     * Retrieves the current transaction
     *
     * @return the TenderableTransactionIfc that is being tendered
     */
    public TenderableTransactionIfc getTenderableTransaction()
    {
        return transaction;
    }

    /**
     * Retrieves the current transaction getTenderableTransaction() is the one in the interface, but for now, we'll
     * allow the use of this one as well.
     *
     * @return the TenderableTransactionIfc that is being tendered
     */
    public TenderableTransactionIfc getTransaction()
    {
        return getTenderableTransaction();
    }

    /**
     * Retrieves the transaction type (Sale, Return, etc...)
     *
     * @return the int representation of the transaction type.
     */
    public int getTransType()
    {
        return transaction.getTransactionType();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.RetailTransactionCargoIfc#getRetailTransaction()
     */
    @Override
    public RetailTransactionIfc getRetailTransaction()
    {
        return (RetailTransactionIfc)getTenderableTransaction();
    }

    /**
     * Retrieves the till ID. May return empty string if the current register
     * has not been set or the current till does not exist.
     *
     * @return String till ID
     */
    public String getTillID()
    {
        String id = "";
        RegisterIfc register = getRegister();

        // Make sure the register exists
        if (register != null)
        {
            TillIfc till = register.getCurrentTill();

            // Make sure the till exists
            if (till != null)
            {
                String tillID = till.getTillID();

                // Make sure the ID exists
                if (tillID != null)
                {
                    id = tillID;
                }
            }
        }

        return id;
    }

    /**
     * Retrieve the array of transactions on which items have been returned.
     *
     * @return SaleReturnTransactionIfc[]
     */
    public SaleReturnTransactionIfc[] getOriginalReturnTransactions()
    {
        return originalReturnTransactions;
    }

    /**
     * Set the array of transactions on which items have been returned.
     *
     * @param value
     */
    public void setOriginalReturnTransactions(SaleReturnTransactionIfc[] value)
    {
        originalReturnTransactions = value;
    }

    /**
     * Retrieves the current working amount currency value
     *
     * @return the CurrencyIfc value of the working amount
     */
    public CurrencyIfc getCurrentAmount()
    {
        return m_currentAmount;
    }
    /**
     * Sets the currency for the tender amount
     * @param value
     */
    public void setCurrentAmount(CurrencyIfc value)
    {
        m_currentAmount = value;
    }

    /**
     * Returns the customer.
     *
     * @return the customer
     */
    public CustomerIfc getCustomer()
    {
        return (customer);
    }

    /**
     * Sets the msr model containing data for swipe before tender service
     *
     * @param model
     *            an MSRModel containing data for swipe before tender service
     */
    public void setPreTenderMSRModel(MSRModel model)
    {
        preTenderMSRModel = model;
    }

    /**
     * Gets the msr model containing data for swipe before tender service
     *
     * @return an MSRModel containing data for swipe before tender service
     */
    public MSRModel getPreTenderMSRModel()
    {
        return preTenderMSRModel;
    }

    /**
     * Gets the sub Tour letter.
     *
     * @return subTourLetter String
     */
    public String getSubTourLetter()
    {
        return subTourLetter;
    }

    /**
     * Sets the sub tour letter.
     *
     * @param subTourLetter The subTourLetter to set.
     */
    public void setSubTourLetter(String subTourLetter)
    {
        this.subTourLetter = subTourLetter;
    }

    /**
     * Returns the tenderCanceled value.
     * @return the tenderCanceled
     */
    public boolean isTenderCanceled()
    {
        return tenderCanceled;
    }

    /**
     * Sets the tenderCanceled value.
     * @param tenderCanceled the tenderCanceled to set
     */
    public void setTenderCanceled(boolean tenderCanceled)
    {
        this.tenderCanceled = tenderCanceled;
    }

    /**
     * Create a SnapshotIfc which can subsequently be used to restore the cargo to its current state.
     *
     * @return an object which stores the current state of the cargo.
     * @see oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc
     */
    public SnapshotIfc makeSnapshot()
    {
        BusIfc bus = null;
        if (this.getTenderAttributes().get(TenderTDOConstants.BUS) != null)
        {
            bus = (BusIfc) this.getTenderAttributes().get(TenderTDOConstants.BUS);
            this.getTenderAttributes().remove(TenderTDOConstants.BUS);
        }
        TourCamSnapshot snapShot = new TourCamSnapshot(this);
        if (bus != null)
        {
            this.getTenderAttributes().put(TenderTDOConstants.BUS, bus);
        }
        return snapShot;
    }

    /**
     * Reset the cargo data using the snapshot passed in.
     *
     * @param snapshot
     *            is the SnapshotIfc which contains the desired state of the cargo.
     * @exception ObjectRestoreException
     *                is thrown when the cargo cannot be restored with this snapshot
     */
    public void restoreSnapshot(SnapshotIfc snapshot) throws ObjectRestoreException
    {
    }

    //////////////////////////////////////////////////////////////////
    // ADO modifications
    //////////////////////////////////////////////////////////////////

    /**
     * @return Array of original return transaction ADOs
     */
    public RetailTransactionADOIfc[] getOriginalReturnTxnADOs()
    {
        return originalReturnTxnADOs;
    }

    /**
     * @param originalReturnTxnADOs
     */
    public void setOriginalReturnTxnADOs(RetailTransactionADOIfc[] originalReturnTxnADOs)
    {
        this.originalReturnTxnADOs = originalReturnTxnADOs;
    }

    /**
     * Return the tender attributes map
     *
     * @return Container for tender information as entered from UI or device.
     */
    public HashMap<String,Object> getTenderAttributes()
    {
        return tenderAttributes;
    }

    /**
     * @param tenderAttributes
     */
    public void setTenderAttributes(HashMap<String,Object> tenderAttributes)
    {
        this.tenderAttributes = tenderAttributes;
    }

    /**
     * Clear out the tender attributes
     */
    public void resetTenderAttributes()
    {
        tenderAttributes = new HashMap<String,Object>(1);
    }

    /**
     * @return The current tender during validation failures for overrides
     */
    public TenderADOIfc getTenderADO()
    {
        return tender;
    }

    /**
     * @param tender
     */
    public void setTenderADO(TenderADOIfc tender)
    {
        this.tender = tender;
    }

    /**
     * This method gets the line display tender.
     *
     * @return Returns the lineDisplayTender.
     */
    public TenderADOIfc getLineDisplayTender()
    {
        return lineDisplayTender;
    }

    /**
     * This method gets the lineDisplayTender.
     *
     * @param lineDisplayTender The lineDisplayTender to set.
     */
    public void setLineDisplayTender(TenderADOIfc lineDisplayTender)
    {
        this.lineDisplayTender = lineDisplayTender;
    }

    /**
     * This class gets the micr counter.
     *
     * @return micrCounter
     */
    public int getMicrCounter()
    {
        return micrCounter;
    }

    /**
     * Increments MICR counter.
     */
    public void incrementMicrCounter()
    {
        micrCounter++;
    }

    /**
        Sets the transaction in progress status
        @param value boolean representing transaction in progress status
    **/
    public void setTransactionInProgress(boolean value)
    {
        transactionInProgress = value;
    }

    /**
        Gets the transaction in progress status
        @return a boolean representing transaction in progress status
    **/
    public boolean isTransactionInProgress()
    {
       return transactionInProgress;
    }

    /**
     * Returns the updateFailure flag. Used to determine if a CustomerIfc DB update failure has occured
     *
     * @return the boolean flag
     */
    public boolean isPostalCodeInvalid()
    {
        return postalCodeInvalid;
    }

    /**
     * Determines whether a return transaction included a receipt. Layaway delete and special order cancel always
     * include a receipt.
     *
     * @return boolean indicating whether the return included a receipt
     */
    public boolean isReturnWithReceipt()
    {
        boolean hasReceipt = false;

        // if layaway delete or special order cancel then is return with receipt
        if (getTransType() == TransactionIfc.TYPE_LAYAWAY_DELETE
            || getTransType() == TransactionIfc.TYPE_ORDER_CANCEL
            || ((getTransType() == TransactionIfc.TYPE_ORDER_COMPLETE
                || getTransType() == TransactionIfc.TYPE_ORDER_PARTIAL)
                && transaction.getTenderTransactionTotals().getGrandTotal().signum() == CurrencyIfc.NEGATIVE))
        {
            hasReceipt = true;
        }
        else // return, check line items
            {
            if (getTransType() == TransactionIfc.TYPE_RETURN)
            {
                SaleReturnTransactionIfc saleReturnTrans = (SaleReturnTransactionIfc) transaction;
                Vector<AbstractTransactionLineItemIfc> lineItems = saleReturnTrans.getLineItemsVector();

                // Look for a receipt on each return item
                for (int i = 0; i < lineItems.size(); i++)
                {
                    SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc) lineItems.get(i);
                    ReturnItemIfc item = lineItem.getReturnItem();

                    // Verify there was a return item
                    if (item != null)
                    {
                        TransactionIDIfc originalID = item.getOriginalTransactionID();

                        // If the original ID is non-empty, something
                        // was entered for the receipt number
                        if (originalID != null && !originalID.getTransactionIDString().equals(""))
                        {
                            hasReceipt = true;
                            i = lineItems.size();
                        }
                    }
                }
            }
        }
        return hasReceipt;
    }

    /**
     * This method returns a boolean true if already added StoreCredit in the
     * TenderLineItemIfc is being used again.
     *
     * @return boolean storeCreditUsed
     */
    public boolean isStoreCreditUsed(String storeCreditNumber, String storeCreditAmount)
    {
        return currentTransactionADO.isStoreCreditUsed(storeCreditNumber, storeCreditAmount);
    }

    /**
     * Returns the updateFailure flag. Used to determine if a CustomerIfc DB
     * update failure has occured
     *
     * @return the boolean flag
     */
    public boolean isUpdateWithoutModify()
    {
        return updateWithoutModify;
    }

    /**
     * Resets MICR counter to zero.
     */
    public void resetMicrCounter()
    {
        micrCounter = 0;
    }
    /**
     * Get giftcard number.
     *
     * @return gift card number
     */
    public String getGiftCardNumber()
    {
        return giftCardNumber;
    }

    /**
     * Set gift card number.
     *
     * @param value
     */
    public void setGiftCardNumber(String value)
    {
        giftCardNumber = value;
    }

    //  ----------------------------------------------------------------------
    /**
     * Sets the itemScanned flag.
     *
     * @param value
     *            boolean
     */
    public void setItemScanned(boolean value)
    {
        itemScanned = value;
    }

    /**
     * Returns the itemScanned flag.
     *
     * @return boolean
     */
    public boolean isItemScanned()
    {
        return itemScanned;
    }

    /**
     * Gets the item quantity value.
     *
     * @return long value
     */
    public BigDecimal getItemQuantity()
    {
        return itemQuantity;
    }

    /**
     * Sets the Item quantity value.
     * <P>
     * @param value item quantity
     */
    public void setItemQuantity(BigDecimal value)
    {
        itemQuantity = value;
    }

    //////////////////////////////////////////////////////////////////
    // End ADO modifications
    //////////////////////////////////////////////////////////////////

    /**
     * @return whether a customer was 'linked' from the find or add customer service
     */
    public boolean isFindOrAddOrUpdateLinked()
    {
        return FindOrAddOrUpdateLinked;
    }

    /**
     * @param b
     */
    public void setFindOrAddOrUpdateLinked(boolean b)
    {
        FindOrAddOrUpdateLinked = b;
    }

    /**
     * @param ifc
     */
    public void setCustomer(CustomerIfc ifc)
    {
        customer = ifc;
    }

    /**
     * @return Returns the idType.
     */
    public String getIdType()
    {
        return idType;
    }

    /**
     * @param idType
     *            The idType to set.
     */
    public void setIdType(String idType)
    {
        this.idType = idType;
    }

    /**
     * Attempts to create a CurrencyIfc object from a passed in amount
     *
     * @param amountString
     *            String representing amount
     * @return a CurrencyIfc instance representing the proper amount
     */
    public CurrencyIfc parseAmount(String amountString)
    {
        CurrencyIfc amount = null;
        try
        {
            amount = DomainGateway.getBaseCurrencyInstance(amountString);
        }
        catch (Exception e)
        {
            logger.error("Attempted to parse amount string", e);
        }
        return amount;
    }

    /**
     * @return Returns the phoneType.
     */
    public int getPhoneType()
    {
        return phoneType;
    }

    /**
     * @param phoneType
     *            The phoneType to set.
     */
    public void setPhoneType(int phoneType)
    {
        this.phoneType = phoneType;
    }

    /**
     * This gets the first time change due.
     *
     * @return Returns the firstTimeChangeDue.
     */
    public boolean isFirstTimeChangeDue()
    {
        return firstTimeChangeDue;
    }

    /**
     * This sets the first time change due.
     *
     * @param firstTimeChangeDue The firstTimeChangeDue to set.
     */
    public void setFirstTimeChangeDue(boolean firstTimeChangeDue)
    {
        this.firstTimeChangeDue = firstTimeChangeDue;
    }

    /**
     * @return Returns the refundTenderLetter.
     */
    public String getRefundTenderLetter()
    {
        return refundTenderLetter;
    }

    /**
     * @param refundTenderLetter The refundTenderLetter to set.
     */
    public void setRefundTenderLetter(String refundTenderLetter)
    {
        this.refundTenderLetter = refundTenderLetter;
    }

    /**
     * This method gets the next tender.
     *
     * @return Returns the nextTender.
     */
    public TenderADOIfc getNextTender()
    {
        return nextTender;
    }

    /**
     * This method sets the next tender.
     *
     * @param nextTender The nextTender to set.
     */
    public void setNextTender(TenderADOIfc nextTender)
    {
        this.nextTender = nextTender;
    }

    /**
     * This method checks to see if cash was the only option and therefore not
     * pressed.
     *
     * @return Returns the cashOnlyOption.
     */
    public boolean isCashOnlyOption()
    {
        return cashOnlyOption;
    }

    /**
     * This method sets whethere cash is the only option and therefore not
     * pressed.
     *
     * @param cashOnlyOption The cashOnlyOption to set.
     */
    public void setCashOnlyOption(boolean cashOnlyOption)
    {
        this.cashOnlyOption = cashOnlyOption;
    }

    /**
     * set gift card reference
     *
     * @param value GiftCardIfc
     */
    public void setGiftCard(GiftCardIfc value)
    {
        giftCard = value;
    }

    /**
     * get gift card reference
     *
     * @return GiftCardIfc
     */
    public GiftCardIfc getGiftCard()
    {
        return giftCard;
    }

    /**
     * @return Returns the giftCardCreditIssue.
     */
    public boolean isGiftCardCreditIssue()
    {
        return giftCardCreditIssue;
    }

    /**
     * @param giftCardCreditIssue The giftCardCreditIssue to set.
     */
    public void setGiftCardCreditIssue(boolean giftCardCredit)
    {
        this.giftCardCreditIssue = giftCardCredit;
    }

    /**
     * Returns the Authorization Transaction Type.
     *
     * @return the authorizationTransactionType
     */
    public int getAuthorizationTransactionType()
    {
        return authorizationTransactionType;
    }

    /**
     * @return
     */
    public RPIResponseIfc getReturnResponse()
    {
        return returnResponse;
    }

    /**
     * set return response
     *
     * @param return response
     */
    public void setReturnResponse(RPIResponseIfc returnResponse)
    {
        this.returnResponse = returnResponse;
    }

    public RPIRequestIfc getReturnRequest()
    {
        return returnRequest;
    }

    /**
     * set return request
     *
     * @param returnRequest
     */
    public void setReturnRequest(RPIRequestIfc returnRequest)
    {
        this.returnRequest = returnRequest;
    }

    /**
     * Getter
     *
     * @return return result (Returns Management)
     */
    public RPIFinalResultIfc getReturnResult()
    {
        return returnResult;
    }

    /**
     * Setter
     *
     * @param returnResult return result (Returns Management)
     */
    public void setReturnResult(RPIFinalResultIfc returnResult)
    {
        this.returnResult = returnResult;
    }

    /**
     * is gotten manager approval for refund tender override.
     *
     * @return boolean
     */
    public boolean isRefundTenderOverrideApproval()
    {
        return refundTenderOverrideApproval;
    }

    /**
     * set manager approval flag for refund tender override.
     *
     * @param refundTenderOverrideApproval
     */
    public void setRefundTenderOverrideApproval(boolean refundTenderOverrideApproval)
    {
        this.refundTenderOverrideApproval = refundTenderOverrideApproval;
    }

    /**
     * Sets the Authorization Transaction Type.
     *
     * @param authorizationTransactionType the authorizationTransactionType to
     *            set
     */
    public void setAuthorizationTransactionType(int authorizationTransactionType)
    {
        this.authorizationTransactionType = authorizationTransactionType;
    }

    /**
     * @return the personalIDTypes
     */
    public CodeListIfc getPersonalIDTypes()
    {
        return personalIDTypes;
    }

    /**
     * @param personalIDTypes the personalIDTypes to set
     */
    public void setPersonalIDTypes(CodeListIfc personalIDTypes)
    {
        this.personalIDTypes = personalIDTypes;
    }

    public LocalizedCodeIfc getLocalizedPersonalIDCode()
    {
        return localizedPersonalIDCode;
    }

    public void setLocalizedPersonalIDCode(LocalizedCodeIfc localizedPersonalIDCode)
    {
        this.localizedPersonalIDCode = localizedPersonalIDCode;
    }

    public EmployeeIfc getEmployee()
    {
        return employee;
    }

    public void setEmployee(EmployeeIfc employee)
    {
        this.employee = employee;
    }

    /**
     * @return boolean whether or not card swiped ahead
     */
    public boolean isSwipeAhead()
    {
        return swipeAhead;
    }

    /**
     * @param swipe ahead state to set
     */
    public void setSwipeAhead(boolean swipeAhead)
    {
        this.swipeAhead = swipeAhead;
    }

    /**
     * @return boolean whether a refund should be applied to the original card tender
     */
    public boolean refundToOriginalCard()
    {
        return refundToOriginalCard;
    }

    /**
     * Set a boolean whether a refund should be applied to the original card tender
     * @param refundToOriginalCard
     */
    public void setRefundToOriginalCard(
            boolean refundToOriginalCard)
    {
        this.refundToOriginalCard = refundToOriginalCard;
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
     * @return the callReferralData
     */
    public CallReferralData getCallReferralData()
    {
        return callReferralData;
    }

    /**
     * @param callReferralData the callReferralDatan to set
     */
    public void setCallReferralData(CallReferralData callReferralData)
    {
        this.callReferralData = callReferralData;
    }


    /**
     * Returns the <code>noSignatureCapture</code> value.
     * @return the <code>noSignatureCapture</code> value
     */
    public boolean isNoSignatureCapture()
    {
        return this.noSignatureCapture;
    }

    /**
     * Sets the <code>noSignatureCapture</code> value.
     * @param noSignatureCapture the new <code>noSignatureCapture</code> value
     */
    public void setNoSignatureCapture(boolean noSignatureCapture)
    {
        this.noSignatureCapture = noSignatureCapture;
    }

    /**
     * Sets the flag to indicate that the transaction has had some gift card activations canceled.
     * @param giftCardActivationsCanceled flag to indicate that the transaction has some canceled gift card activations.
     */
    public void setGiftCardActivationsCanceled(boolean giftCardActivationsCanceled)
    {
        this.giftCardActivationsCanceled = giftCardActivationsCanceled;
    }

    /**
     * Returns the flag to indicate that the transaction has had some gift card activations canceled.
     * @return indicates if gift card activations have been canceled.
     */
    public boolean isGiftCardActivationsCanceled()
    {
        return this.giftCardActivationsCanceled;
    }

    /**
     * Returns the <code>suppressGiftCardActivation</code> value.
     * @return the suppressGiftCardActivation
     */
    public boolean isSuppressGiftCardActivation()
    {
        return this.suppressGiftCardActivation;
    }

    /**
     * Sets the <code>suppressGiftCardActivation</code> value.
     * @param suppressGiftCardActivation the suppressGiftCardActivation to set
     */
    public void setSuppressGiftCardActivation(boolean suppressGiftCardActivation)
    {
        this.suppressGiftCardActivation = suppressGiftCardActivation;
    }

    /**
     * Returns the <code>captureCustomerRequired</code> value.
     * @return the captureCustomerRequired
     */
	public boolean isCaptureCustomerRequired() 
	{
		return captureCustomerRequired;
	}

    /**
     * Sets the <code>captureCustomerRequired</code> value.
     * @param captureCustomerRequired the captureCustomerRequired to set
     */
	public void setCaptureCustomerRequired(boolean captureCustomerRequired) 
	{
		this.captureCustomerRequired = captureCustomerRequired;
	}
    
    
}
