/* =============================================================================
* Copyright (c) 2003, 2014, Oracle and/or its affiliates. All rights reserved.
 * =============================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/validate/TenderLaunchShuttle.java /main/20 2014/07/01 13:33:26 blarsen Exp $
 * =============================================================================
 * NOTES
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
 *    blarsen   02/04/14 - AJB requires original auth response for call
 *                         referrals. Adding this to appropriate
 *                         shuttles/cargos.
 *    mjwallac  01/07/14 - fix dereferencing of null objects.
 *    asinton   08/02/12 - Call referral refactor
 *    asinton   07/02/12 - carry call referral authorization details from
 *                         Mobile POS to call referral site.
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    icole     03/06/12 - Refactor to remove CPOIPaymentUtility and attempt to
 *                         have more generic code, rather than heavily Pincomm.
 *    cgreene   06/07/11 - update to first pass of removing pospal project
 *    rrkohli   05/06/11 - Added Code For Pos UI update Quick win
 *    icole     04/28/11 - Payment updates
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    rkar      11/12/08 - Adds/changes for POS-RM integration
 *    rkar      11/07/08 - Additions/changes for POS-RM integration
 *
 * =============================================================================
     $Log:
      4    360Commerce 1.3         6/5/2008 11:43:28 AM   Maisa De Camargo CR
           31945 - Fixed problem when getting data from the MSR Device. Code
           Reviewed by Deepti Sharma.
      3    360Commerce 1.2         3/31/2005 4:30:25 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:25:59 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:14:54 PM  Robert Pearse
     $
     Revision 1.5  2004/09/23 00:07:16  kmcbride
     @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents

     Revision 1.4  2004/04/09 16:56:00  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.3  2004/02/12 16:48:21  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:22:50  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.4   Feb 01 2004 13:49:10   bjosserand
 * Mail Bank Check.
 *
 *    Rev 1.3   Nov 19 2003 16:21:16   epd
 * Refactoring updates
 *
 *    Rev 1.2   08 Nov 2003 01:27:18   baa
 * cleanup -sale refactoring
 *
 *    Rev 1.1   Nov 07 2003 07:47:30   baa
 * integration with subservices
 * Resolution for 3430: Sale Service Refactoring
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.validate;

import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.manager.rm.RPIRequestIfc;
import oracle.retail.stores.domain.manager.rm.RPIResponseIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.ado.store.StoreADO;
import oracle.retail.stores.pos.ado.store.StoreFactory;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.TransactionPrototypeEnum;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.services.tender.authorization.CallReferralData;
import oracle.retail.stores.pos.ui.beans.CreditReferralBeanModel;

import org.apache.log4j.Logger;

/**
 * This shuttle copies information from the cargo used in the Sale service to
 * the cargo used in the Tender service.
 *
 */
public class TenderLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -8942316912525520108L;
    /** The logger to which log messages will be sent. */
    protected static final Logger logger = Logger.getLogger(TenderLaunchShuttle.class);
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/20 $";
    /**
     * transaction
     */
    protected RetailTransactionIfc transaction = null;
    /**
     * This array contains a list of SaleReturnTransacions on which returns have
     * been completed.
     */
    protected SaleReturnTransactionIfc[] originalReturnTransactions = null;

    /**
     * Carry return response to Tender service
     */
    protected RPIResponseIfc returnResponse;

    /**
     * Carry return request to Tender service
     */
    protected RPIRequestIfc  returnRequest;

    protected EmployeeIfc employee = null;

    /** CreditReferralBeanModel for use by MobilePOS */
    protected CreditReferralBeanModel creditReferralBeanModel;


    /** call referral data */
    private CallReferralData callReferralData = new CallReferralData();

    /** flag to indicate no signature capture */
    protected boolean noSignatureCapture;

    /** flag to indicate that gift card activation should be suppressed */
    private boolean suppressGiftCardActivation;

    /**
     * Loads cargo from Sale service.
     *
     * @param bus Service Bus
     */
    @Override
    public void load(BusIfc bus)
    {
        super.load(bus);
        SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();
        transaction = cargo.getTransaction();
        originalReturnTransactions = cargo.getOriginalReturnTransactions();
        returnResponse = cargo.getReturnResponse();
        returnRequest  = cargo.getReturnRequest();
        employee = cargo.getEmployee();
        creditReferralBeanModel = cargo.getCreditReferralBeanModel();
        callReferralData = cargo.getCallReferralData();
        noSignatureCapture = cargo.isNoSignatureCapture();
        suppressGiftCardActivation = cargo.isSuppressGiftCardActivation();
    }

    /**
     * Loads data into tender service.
     *
     * @param bus Service Bus
     */
    @Override
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        TenderCargo cargo = (TenderCargo) bus.getCargo();

        ////////////////////////////////////
        // Construct ADO's
        ////////////////////////////////////

        // create a register
        StoreFactory storeFactory = StoreFactory.getInstance();
        RegisterADO registerADO = storeFactory.getRegisterADOInstance();
        registerADO.fromLegacy(cargo.getRegister());

        // create the store
        StoreADO storeADO = storeFactory.getStoreADOInstance();
        storeADO.fromLegacy(cargo.getStoreStatus());

        // put store in register
        registerADO.setStoreADO(storeADO);

        // Create/convert/set in cargo ADO transaction
        TransactionPrototypeEnum txnType =
            TransactionPrototypeEnum.makeEnumFromTransactionType(transaction.getTransactionType());
        RetailTransactionADOIfc txnADO = null;
        try
        {
            txnADO = txnType.getTransactionADOInstance();
        }
        catch (ADOException e1)
        {
            // Exception getting ADO instance, log exception and return
            logger.error("Exception while getting ADO Instance " + e1.getMessage());
            return;
        }
        ((ADO) txnADO).fromLegacy(transaction);
        cargo.setCurrentTransactionADO(txnADO);

        // Create/convert/set in cargo original return ADO transactions
        if (originalReturnTransactions != null)
        {
            RetailTransactionADOIfc[] originalReturnTxnADOs =
                new RetailTransactionADOIfc[originalReturnTransactions.length];
            for (int i = 0; i < originalReturnTransactions.length; i++)
            {
                TransactionPrototypeEnum returnTxnType =
                    TransactionPrototypeEnum.makeEnumFromTransactionType(
                        originalReturnTransactions[i].getTransactionType());
                RetailTransactionADOIfc returnTxnADO = null;
                try
                {
                    returnTxnADO = returnTxnType.getTransactionADOInstance();
                }
                catch (ADOException e2)
                {
                    // Exception getting ADO instance, log exception and return
                    logger.error("Exception while getting ADO Instance " + e2.getMessage());
                    return;
                }
                ((ADO) returnTxnADO).fromLegacy(originalReturnTransactions[i]);
                originalReturnTxnADOs[i] = returnTxnADO;
            }
            cargo.setOriginalReturnTxnADOs(originalReturnTxnADOs);
        }

        ///////////////////////////////////
        // End ADO
        ///////////////////////////////////

        cargo.setTransaction(transaction);
        //cargo.setResourceID("TenderSecurityError");
        cargo.setOriginalReturnTransactions(originalReturnTransactions);

        cargo.setReturnResponse(returnResponse);
        cargo.setReturnRequest(returnRequest);

        cargo.setCreditReferralBeanModel(creditReferralBeanModel);

        // MPOS: copy in call referral data captured from previous authorization attempt
        cargo.setCallReferralData(callReferralData);

        // MPOS: carry the noSignatureCapture value from MPOS Server
        cargo.setNoSignatureCapture(noSignatureCapture);

        // MPOS: flag to suppress gift card activation when done manually via voice authorization
        cargo.setSuppressGiftCardActivation(suppressGiftCardActivation);
    }

    /**
     * Returns the revision number of the class.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

}
