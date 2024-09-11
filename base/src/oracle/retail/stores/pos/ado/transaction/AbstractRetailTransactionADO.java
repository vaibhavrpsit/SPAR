/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/transaction/AbstractRetailTransactionADO.java /main/62 2014/03/03 13:35:53 jswan Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED (MM/DD/YY)
*    yiqzhao 10/17/14 - Change the modifier of method calculateRefundOptionsRow to
*                      public. It will be called from POS service.
*    crain  10/07/14 - Bug 19771786: update training mode with the value of
*                      the gift certificate retrieved from the transaction
*    asinto 09/11/14 - Removed entryMethod argument from updateCustomerForRedeemedStoreCredit
*                      because it is not used.
*    crain  08/21/14 - Bug 18689467: tendered with 2 debit cards, during receipted-return of 
*                      transaction debit button needs to be enabled on refund option tender screen.
*    jswan  02/28/14 - Modified to fix issue with post voiding transactions
*                      with Gift/Mall Certificate tenders.
*    blarse 01/29/14 - Some payment systems may not require tokens (or provide
*                      tokens) for post voids. Using new properties to support
*                      those payment systems (AJB EMV).
*    mjwall 01/09/14 - fix null dereferences
*    cgreen 10/25/13 - remove currency type deprecations and use currency code
*                      instead of description
*    abonda 09/04/13 - initialize collections
*    mkutia 06/10/13 - Order and Layaway transaction Cash Rounding
*    jswan  06/05/13 - Modified to prevent zero amount change tender from being
*                      written to the database.
*    mkutia 05/31/13 - Matching the journalling of the change due amount with
*                      that from the sale receipt
*    jswan  05/20/13 - Refactored the location of cash change adjustment
*                      calculation in the tender tour to handle cancel order
*                      refunds.
*    jswan  03/26/13 - Removed tender change from consideration as an original
*                      tender.
*    mkutia 02/12/13 - added methods to retrieve the Rounded Change amount and
*                      Change Given for currency rounding
*    yiqzha 01/10/13 - Add business name for store credit and store credit
*                      tender line tables.
*    subrde 12/28/12 - Rename CouponsAccepted to NonStoreCouponsAccepted
*    tkshar 12/10/12 - commons-lang update 3.1
*    jswan  11/15/12 - Modified to support parameter controlled return tenders.
*    jswan  10/25/12 - Modified to support returns by order.
*    jswan  03/21/12 - Modified to support centralized gift certificate and
*                      store credit.
*    cgreen 12/05/11 - updated from deprecated packages and used more
*                      bigdecimal constants
*    blarse 10/20/11 - Disallowing Debit tender if in reentry mode.
*    blarse 10/07/11 - Remove ReturnWithDebitAllowsCashTender parameter. The
*                      Cash refund tender option should always be present for
*                      debit case.
*    asinto 10/04/11 - prevent post voiding of transactions with authorized
*                      tenders that lack necessary data for reversing.
*    sgu    09/08/11 - add house account as a refund tender
*    asinto 09/06/11 - remove ability to post void transactions with issue,
*                      reload, redeem of gift cards.
*    cgreen 09/02/11 - set re-entry mode when initializing transation
*    mkutia 08/12/11 - If Check max overtender limit dialog has been shown then
*                      donot show the max cash change overtender dialog
*    mkutia 08/10/11 - Undid incorrect changes for bug 11838309
*    blarse 07/28/11 - AuthorizationThreshold parameter removed as part of 13.4
*                      Advanced Payment Foundation. Changed House Account to
*                      use HouseCardsAccepted rather than CardTypes parameter.
*    jswan  07/28/11 - Implement ReturnWithDebitAllowsCashTender parameter
*                      functionality.
*    cgreen 07/21/11 - remove DebitBinFileLookup and DebitCardsAccepted
*                      parameters for APF
*    jswan  07/21/11 - Modified to support credit tender refund - tour flow,
*                      simulated authorizer and Pincomm.
*    sgu    07/11/11 - check for check overtender only if not in transaction
*                      reentry mode
*    asinto 06/18/11 - Added constant for GiftCardsAccepted
*    ohorne 06/08/11 - added houseAccountEnabled check in getEnabledTenderOptions()
*    asinto 05/09/11 - Removed restrictions for voiding of gift cards
*    jkoppo 04/01/11 - Added new method isStoreCreditUsed, which is used to
*                      prevent the use of same store credit mutiple times in a
*                      single transaction
*    mchell 03/24/11 - XbranchMerge mchellap_bug-11838309 from main
*    mchell 03/24/11 - BUG#11838309 Over tendering allowed for check
*    cgreen 02/11/11 - Used LinkedHashMaps to ensure order and added generics
*                      tempaltes.
*    npoola 09/24/10 - changed the parameter name from TrainingModeOpenDrawer
*                      to OpenDrawerInTrainingMode
*    jswan  08/06/10 - Fixed issue with return tender.
*    cgreen 05/26/10 - convert to oracle packaging
*    abhayg 05/07/10 - Fixed Transaction status issue
*    acadar 04/16/10 - merge from 13.2x
*    acadar 04/16/10 - XbranchMerge acadar_bug-9561577 from
*                      rgbustores_13.2x_generic_branch
*    acadar 04/07/10 - add check for gift certificate number, because Mall
*                      Certificate as POs do not have gift certificate numbers
*    jswan  03/25/10 - Merge Result.
*    jswan  03/25/10 - Fixed issue with partially canceled PDO generating
*                      incorrect 'Total Tender' and 'Change Due' in transaction
*                      EJ.
*    cgreen 03/23/10 - switch Zero comparison to using signum
*    dwfung 03/10/10 - Handling Training Mode for Gift Card Inquiry Requests
*    cgreen 02/02/10 - reduce number of method calls when determine balance due
*                      status
*    cgreen 01/20/10 - added null check to return tenders
*    cgreen 01/20/10 - added isInMode(RegisterMode.REENTRY) when getting
*                      pending auth tenders
*    abonda 01/03/10 - update header date
*    asinto 12/03/09 - Changes to support credit card authorizations on returns
*                      and voids.
*    jswan  11/18/09 - Revised method header comments.
*    jswan  11/18/09 - Forward to fix use of gift cerificate more than once in
*                      a transaction and making change to gift certificate
*                      which already been redeemed.
*    jswan  11/17/09 - XbranchMerge shagoyal_bug-8553074 from
*                      rgbustores_13.0x_branch
*    asinto 06/11/09 - Improved comments and javadocs.
*    asinto 06/11/09 - Changed calls to getTransactionTotals to
*                      getTenderTransactionTotals in getTransactionGrandTotal
*                      and lessThanOrEqualToMaxReturnCash. For
*                      SaleReturnTransactions this will not have any affect but
*                      for OrderTransacstion the getTenderTransactionTotals
*                      will return the 'live' transaction totals instance.
*    asinto 04/21/09 - Moved implementation for getDepositAmount and
*                      updateOrderStatus from AbstractRetailTransactionADO to
*                      OrderTransactionADO.
*    aphula 04/14/09 - Fixed issue if Special Order is done by Purchase Order
*    asinto 03/30/09 - Chenged method name to be more generic per review
*                      comments.
*    asinto 03/30/09 - Prevent post void of transaction with any gift card
*                      tender.
*    asinto 03/18/09 - Modified to show correct dialog.
*    asinto 03/09/09 - Added gift card as refund tender not post voidable.
*    asinto 03/08/09 - Changes to disallow post void of gift card issue,
*                      reload, and redeem.
*    mahisi 02/23/09 - Fixed variation issue in total among a sale transaction
*                      and order transaction
*    aratho 02/14/09 - Updated to pass Personal Id information to printing
*                      tour.
*    jswan  01/29/09 - Modified to correct issues with printing store credit.
*    mdecam 01/23/09 - Updated getVoidAuthPendingTenderLineItems method to
*                      retrieve only the tenderLineItems that haven't being
*                      voided yet for reversible groups.
*    nkgaut 12/04/08 - Forward ported the change for credit card authorisation
*                      on refund transactions
*    abonda 11/06/08 - updated files related to reason codes
*    abonda 11/05/08 - updated files related to the reason codes CheckIDTypes
*                      and MailBankCheckIDTypes

* ===========================================================================
 | $Log:
 |  19   360Commerce 1.18        6/15/2008 5:47:57 PM   Jack G. Swan
 |       Modified to allow gift card authorisation on refund transactions.
 |  18   360Commerce 1.17        6/12/2008 5:05:53 PM   Charles D. Baker CR
 |       30240 - Updated comments per Jack's code review.
 |  17   360Commerce 1.16        6/12/2008 4:32:39 PM   Charles D. Baker CR
 |       32040 - Updated to avoid clearing tax exempt status unless a) we're
 |       removing a tax exempt tender and b) there are not remaining tenders
 |       that are tax exempt purchase orders. Code review by Jack Swan.
 |  16   360Commerce 1.15        6/12/2008 4:12:17 PM   Maisa De Camargo CR
 |       32031 - Applied a non-invasive fix for Post Void of Transactions
 |       tendered with GC (ISD).
 |       The ISD messageType for GC is based on the existence of a GC. (FA for
 |        a new card, F for existent card). Code Reviewed by Jack Swan.
 |  15   360Commerce 1.14        5/7/2008 10:26:52 AM   Mathews Kochummen get
 |       consistent order of tender line items in postvoid receipt. reviewed
 |       by anda
 |  14   360Commerce 1.13        5/4/2008 11:20:34 AM   Brett J. Larsen CR
 |       31549 - post voids allowed for transaction tendered with gift card
 |       when gift card has since been used
 |  13   360Commerce 1.12        5/2/2008 4:56:32 PM    Christian Greene 31553
 |       Add voidCheck for when voiding the issue of redeemed gift certs
 |  12   360Commerce 1.11        4/30/2008 1:51:19 PM   Maisa De Camargo CR
 |       31328 - Added a new scenario to the Refund Options Screen.
 |       The scenarios are described in the ORPOS_Tender.doc. In order to
 |       maintain the priority, I have shifted the values of the
 |       refundOptionsRow. Code Reviewed by Jack Swan.
 |  11   360Commerce 1.10        3/31/2008 1:50:22 PM   Mathews Kochummen
 |       forward port from v12x to trunk
 |  10   360Commerce 1.9         7/18/2007 8:43:35 AM   Alan N. Sinton  CR
 |       27651 - Made Post Void EJournal entries VAT compliant.
 |  9    360Commerce 1.8         5/11/2007 4:35:13 PM   Peter J. Fierro
 |       Deprecate DomainInterfaceManager/Technician, refactor DomainGateway
 |       to not read/write xml type lists.
 |  8    360Commerce 1.7         4/25/2007 3:54:09 PM   Ashok.Mondal    CR
 |       10571 :V7.2.2 merge to trunk.
 |  7    360Commerce 1.6         4/24/2007 1:16:09 PM   Charles D. Baker CR
 |       26556 - I18N Code Merge.
 |  6    360Commerce 1.5         2/20/2006 11:51:47 AM  Brett J. Larsen CR 8450
 |        - max over change dialog not being presented at appropriate times -
 |       fixed logic which did not consider cashOnlyForChangeDue value
 |  5    360Commerce 1.4         1/25/2006 4:10:47 PM   Brett J. Larsen merge
 |       7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 |  4    360Commerce 1.3         12/13/2005 4:42:33 PM  Barry A. Pape
 |       Base-lining of 7.1_LA
 |  3    360Commerce 1.2         3/31/2005 4:27:07 PM   Robert Pearse
 |  2    360Commerce 1.1         3/10/2005 10:19:27 AM  Robert Pearse
 |  1    360Commerce 1.0         2/11/2005 12:09:20 PM  Robert Pearse
 | $
 |
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.CertificateTransaction;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.TransactionReadDataTransaction;
import oracle.retail.stores.domain.customer.CaptureCustomerIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.customer.IRSCustomerIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.financial.PaymentHistoryInfoIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.returns.ReturnTenderDataElementIfc;
import oracle.retail.stores.domain.stock.GiftCertificateItemIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderCertificateIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificate;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderLineItemConstantsIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditConstantsIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.tender.TenderTypeMap;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.transaction.AbstractTenderableTransaction;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.RedeemTransactionIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.CardTypeCodesIfc;
import oracle.retail.stores.domain.utility.CurrencyRoundingCalculatorIfc;
import oracle.retail.stores.domain.utility.StoreCreditIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderGroupFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalConstants;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.JournalableADOIfc;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.lineitem.LineItemADOIfc;
import oracle.retail.stores.pos.ado.lineitem.LineItemConstants;
import oracle.retail.stores.pos.ado.lineitem.LineItemFactory;
import oracle.retail.stores.pos.ado.lineitem.LineItemTypeEnum;
import oracle.retail.stores.pos.ado.lineitem.TenderLineItemCategoryEnum;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.ado.store.RegisterMode;
import oracle.retail.stores.pos.ado.tender.AbstractTenderADO;
import oracle.retail.stores.pos.ado.tender.AuthorizableADOIfc;
import oracle.retail.stores.pos.ado.tender.ReversibleTenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderCheckADO;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.tender.group.AuthorizableTenderGroupADOIfc;
import oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc;
import oracle.retail.stores.pos.ado.tender.group.TenderGroupCashADO;
import oracle.retail.stores.pos.ado.tender.group.TenderGroupCheckADO;
import oracle.retail.stores.pos.ado.tender.group.TenderGroupCreditADO;
import oracle.retail.stores.pos.ado.tender.group.TenderGroupDebitADO;
import oracle.retail.stores.pos.ado.tender.group.TenderGroupGiftCardADO;
import oracle.retail.stores.pos.ado.utility.AuthorizationException;
import oracle.retail.stores.pos.ado.utility.TenderUtilityIfc;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.services.tender.tdo.RefundOptionsTDO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Provides functionality common to all transactions
 */
public abstract class AbstractRetailTransactionADO extends ADO
    implements RetailTransactionADOIfc, JournalableADOIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 7396026165585726321L;

    /** class logger * */
    protected static final Logger logger = Logger.getLogger(AbstractRetailTransactionADO.class);

    /** the performance logger */
    protected static final Logger perf = Logger.getLogger("PERF." + AbstractRetailTransactionADO.class.getName());


    /** Constant for application property group */
    protected static final String APPLICATION_PROPERTY_GROUP = "application";

    /** Is token required for Credit Post Voids? */
    protected static final String POS_TOKEN_REQUIRED_FOR_POST_VOID_CREDIT = "TokenRequiredForPostVoidCredit";

    /** Is token required for Debit Post Voids? */
    protected static final String POS_TOKEN_REQUIRED_FOR_POST_VOID_DEBIT = "TokenRequiredForPostVoidDebit";


    /**
    * This map of tender groups contains all the tender line items for this transaction.
    */
    protected Map<TenderTypeEnum, TenderGroupADOIfc> tenderGroupMap = new LinkedHashMap<TenderTypeEnum, TenderGroupADOIfc>();

    /** our RDO transaction */
    protected TransactionIfc transactionRDO;

    /** our RDO IRS Customer */
    protected IRSCustomerIfc adoIRSCustomer = null;

    /** the modes that the Register is in at time of transaction creation **/
    protected RegisterMode[] modes;

    /** store credit refund only parameter **/
    protected static final String storeCreditRefundOnly = "StoreCreditRefundOnly";

    /** this determines if cash is the only option for change due **/
    protected boolean cashOnlyForChangeDue = false;

    /** this int refers to the tender requirements table for refund options
     *  It is used to determine which message to display on the refund options screen and
     *  what buttons to enable.
     */
    protected int refundOptionsRow = 0;

    /**
     * Transactions, when created by the factory, must be initialized. All initialization should occur here. The
     * customer info and operator come from cargo and the register, which acts as a transaction factory, should pass
     * itself in.
     *
     * @param customerInfoRDO
     *            The customer info object for this transaction
     * @param operatorRDO
     *            The current cashier/operator of the system
     * @param registerADO
     *            The register
     */
    public void initialize(CustomerInfoIfc customerInfoRDO, EmployeeIfc operatorRDO, RegisterADO registerADO)
    {
        TransactionIfc txnRDO = instantiateTransactionRDO();
        intializeTransactionRDO(txnRDO, customerInfoRDO, operatorRDO, registerADO);
    }

    /**
     * Each concrete transaction will have its own concrete RDO transaction type. It is the responsibility of the ADO
     * transactions to create the appropriate RDO transaction type
     *
     * @return A concrete RDO transaction.
     */
    protected abstract TransactionIfc instantiateTransactionRDO();

    /**
     * New transactions must be initialized. The following logic is common to all transaction types.
     *
     * @param txnRDO
     *            The transaction to be initialized.
     * @param customerInfoRDO
     *            The customer info for this transaction.
     * @param operatorRDO
     *            The cashier/operator for this transaction.
     * @param registerADO
     *            The ADO register.
     */
    protected void intializeTransactionRDO(
        TransactionIfc txnRDO,
        CustomerInfoIfc customerInfoRDO,
        EmployeeIfc operatorRDO,
        RegisterADO registerADO)
    {
        txnRDO.setCustomerInfo(customerInfoRDO);
        txnRDO.setCashier(operatorRDO);
        txnRDO.setBusinessDay(registerADO.getStoreADO().getBusinessDate());
        // get register RDO for additional fields
        RegisterIfc registerRDO = (RegisterIfc) registerADO.toLegacy();
        txnRDO.setWorkstation(registerRDO.getWorkstation());
        txnRDO.setTillID(registerRDO.getCurrentTillID());
        txnRDO.setTrainingMode(registerADO.isInMode(RegisterMode.TRAINING));
        txnRDO.setTransactionSequenceNumber(registerRDO.getNextTransactionSequenceNumber());
        txnRDO.setReentryMode(registerRDO.getWorkstation().isTransReentryMode());

        // finish up
        txnRDO.buildTransactionID();
        txnRDO.setTimestampBegin();
        txnRDO.setTransactionStatus(TransactionIfc.STATUS_IN_PROGRESS);

        setRegisterModes(registerADO.getRegisterModes());

        // journal the txn creation
        RegisterJournalIfc journal = getJournalFactory().getRegisterJournal();
        journal.setParameterManager(getParameterManager());
        journal.journal(this, JournalFamilyEnum.TRANSACTION, JournalActionEnum.CREATE);

    }

    /**
     * Given a line item type, return an array of RDO line items.
     *
     * @param type
     *            The desired type to retrieve
     * @return an array of items that match the desired type.
     */
    protected LineItemADOIfc[] getLineItemsForType(LineItemTypeEnum type)
    {
        SaleReturnLineItemIfc[] items =
            ((RetailTransactionIfc) transactionRDO).getProductGroupLineItems(type.toString());

        LineItemADOIfc[] adoItems = null;
        if (items != null)
        {
            adoItems = new LineItemADOIfc[items.length];
            // HashMap for creating ADO line item
            HashMap<String, LineItemTypeEnum> lineItemAttributes = new HashMap<String, LineItemTypeEnum>(1);
            lineItemAttributes.put(LineItemConstants.LINE_ITEM_TYPE, type);

            for (int i = 0; i < items.length; i++)
            {
                // create ADO line item
                LineItemADOIfc lineItemADO = LineItemFactory.getInstance().createLineItem(lineItemAttributes);
                ((ADO) lineItemADO).fromLegacy(items[i]);
                adoItems[i] = lineItemADO;
            }
        }
        else
        {
            adoItems = new LineItemADOIfc[0];
        }

        return adoItems;
    }

    /**
     * Returns the Transaction ID String.
     * @return String
     */
    public String getTransactionID()
    {
        return transactionRDO.getTransactionID();
    }

    /**
     * Returns the transaction type string
     * @return TransactionPrototypeEnum
     */
    public TransactionPrototypeEnum getTransactionType()
    {
        return TransactionPrototypeEnum.makeEnumFromTransactionType(transactionRDO.getTransactionType());
    }

    /**
     * This method returns a boolean flag indicating whether or not this transaction contains Send items. Defaulted to
     * false here. Transactions that contain line items must override this method.
     *
     * @return boolean
     */
    public boolean containsSendItems()
    {
        return false;
    }

    /**
     * This method returns a boolean flag indicating whether or not
     * this transaction contains return items.
     *
     * @return boolean
     */
    public boolean containsReturnItems()
    {
        return false;
    }

    /**
     * This method returns a boolean flag indicating whether or not a customer is present. Default to true here.
     * Transactions that contain line items must override this method.
     *
     * @return boolean
     * @see oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc#isCustomerPresent()
     */
    public boolean isCustomerPresent()
    {
        boolean returnBool = true;
        if (transactionRDO instanceof SaleReturnTransactionIfc)
        {
            returnBool = ((SaleReturnTransactionIfc) transactionRDO).isCustomerPresentDuringSend();
        }
        return returnBool;
    }

    /**
     * A transaction carries with it the modes that the Register is in at time of transaction creation
     *
     * @param modes
     */
    protected void setRegisterModes(RegisterMode[] modes)
    {
        this.modes = modes;
    }

    /**
     * Default behavior for a transaction is to assume not voidable. All transactions that are voidable need to
     * override this.
     *
     * @return boolean
     * @see oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc#isVoidable(String)
     */
    public boolean isVoidable(String currentTillID) throws VoidException
    {
        throw new VoidException("Invalid Transaction Type", VoidErrorCodeEnum.INVALID_TRANSACTION);
    }

    /**
     * Some transactions require specific behavior when voided to reverse certain actions (such as contacting a 3rd
     * party authorizer for a reversal). Each transaction type should be responsible for handling its own specific
     * logic. It returns a HashMap of Tender Groups containing inversed tenders
     *
     * You can use this method to make any modifications to the original transaction before the
     * void transaction is saved. To make additional DB updates after the void transaction is
     * saved, @see #updateForVoid()
     *
     * @return Inverse tenders from the current transaction.
     * @throws AuthorizationException
     */
    protected LinkedHashMap<TenderTypeEnum,TenderGroupADOIfc> processVoid() throws AuthorizationException
    {
        // new voided group map
        LinkedHashMap<TenderTypeEnum,TenderGroupADOIfc> voidGroupMap = new LinkedHashMap<TenderTypeEnum, TenderGroupADOIfc>();
        // iterate through original tenders
        Iterator<TenderGroupADOIfc> iter = tenderGroupMap.values().iterator();
        while (iter.hasNext())
        {
            TenderGroupADOIfc tenderGroup = iter.next();
            // Get inverse group
            TenderGroupADOIfc voidGroup = tenderGroup.processVoid();

            // if group already exists in map, we must combine them.
            // This could happen if a tender returns a voided group map of a different type.
            if (voidGroupMap.get(voidGroup.getGroupType()) != null)
            {
                voidGroupMap.get(voidGroup.getGroupType()).combineGroups(voidGroup);
            }
            else // otherwise create add as new group
            {
                voidGroupMap.put(voidGroup.getGroupType(), voidGroup);
            }
        }
        return voidGroupMap;
    }

    /**
     * Performs a database lookup to make sure transaction was not already voided
     *
     * @throws VoidException
     */
    protected void voidCheckForPreviousVoid() throws VoidException
    {
        TransactionReadDataTransaction trans = null;

        trans = (TransactionReadDataTransaction) DataTransactionFactory.create(DataTransactionKeys.TRANSACTION_READ_DATA_TRANSACTION);
        try
        {
            if (trans.isTransactionVoided(transactionRDO))
            {
                throw new VoidException("Txn already voided", VoidErrorCodeEnum.PREVIOUSLY_VOIDED);
            }
        }
        catch (DataException e)
        {
            logger.warn("A database problem occurred.", e);
            throw new VoidException("Database Problem", VoidErrorCodeEnum.NOT_FOUND);
        }
    }

    /**
     * Make sure any issued tenders have not been expended. Checks store credit,
     * gift cards and gift cert redeems.
     *
     * @throws VoidException
     */
    protected void voidCheckForIssuedTenderModifications() throws VoidException
    {
        // 1) Make sure issued store credits have not been used
        voidCheckForRedeemedStoreCredit();

        // 2) Make sure gift certs issued by this transaction have not been used
        voidCheckForTenderedGiftCertificate();
    }

    /**
     * Void logic. Make sure any issued store credits have not been redeemed.
     *
     * @throws VoidException
     */
    protected void voidCheckForRedeemedStoreCredit() throws VoidException
    {
        if (transactionRDO instanceof TenderableTransactionIfc)
        {
            TenderLineItemIfc[] tli = ((TenderableTransactionIfc) transactionRDO).getTenderLineItems();
            TenderStoreCreditIfc tscr = null;
            for (int i = 0; i < tli.length; i++)
            {
                if ((tli[i] instanceof TenderStoreCreditIfc))
                {
                    tscr = (TenderStoreCreditIfc) tli[i];
                    if (tscr.isRedeemed() || tscr.getPostVoided())
                    {
                        throw new VoidException("Invalid Transaction", VoidErrorCodeEnum.TRANSACTION_MODIFIED);
                    }
                }
            }
        }
    }

    /**
     * Void logic. Make sure any issued gift certificate have not been used.
     *
     * @throws VoidException
     */
    protected void voidCheckForTenderedGiftCertificate() throws VoidException
    {
        if (transactionRDO instanceof SaleReturnTransactionIfc)
        {
            @SuppressWarnings("unchecked")
            Vector<AbstractTransactionLineItemIfc> lines = ((SaleReturnTransactionIfc) transactionRDO).getItemContainerProxy().getLineItemsVector();
            for (AbstractTransactionLineItemIfc lineItem : lines)
            {
                if (lineItem instanceof SaleReturnLineItemIfc)
                {
                    SaleReturnLineItemIfc saleLine = (SaleReturnLineItemIfc)lineItem;
                    if (saleLine.isSaleLineItem() && saleLine.getPLUItem() instanceof GiftCertificateItemIfc)
                    {
                        // create search tender
                        GiftCertificateItemIfc giftCert = (GiftCertificateItemIfc)saleLine.getPLUItem();
                        TenderGiftCertificateIfc tenderGiftCert = new TenderGiftCertificate(giftCert.getPrice(), giftCert.getItemID());
                        tenderGiftCert.setTrainingMode(giftCert.isTrainingMode());
                        tenderGiftCert.getDocument().setIssuingStoreID(transactionRDO.getFormattedStoreID());

                        // perform data lookup to see if redeemed
                        CertificateTransaction trans = (CertificateTransaction)DataTransactionFactory.create(DataTransactionKeys.CERTIFICATE_TRANSACTION);
                        try
                        {
                            tenderGiftCert = (TenderGiftCertificateIfc)trans.readCertificate((TenderCertificateIfc)tenderGiftCert);
                            if (tenderGiftCert.isRedeemed() || tenderGiftCert.getPostVoided())
                            {
                                throw new VoidException("Invalid Transaction", VoidErrorCodeEnum.TRANSACTION_MODIFIED);
                            }
                        }
                        catch (DataException e)
                        {
                            logger.warn("A database problem occurred.", e);
                            throw new VoidException("Database Problem", VoidErrorCodeEnum.NOT_FOUND);
                        }
                    }
                }
            }
        }

        if (transactionRDO instanceof TenderableTransactionIfc)
        {
            TenderLineItemIfc[] tli = ((TenderableTransactionIfc) transactionRDO).getTenderLineItems();
            TenderGiftCertificateIfc tgfc = null;
            for (int i = 0; i < tli.length; i++)
            {
                if ((tli[i] instanceof TenderGiftCertificateIfc))
                {
                    tgfc = (TenderGiftCertificateIfc) tli[i];
                    if (tgfc.getPostVoided())
                    {
                        throw new VoidException("Invalid Transaction", VoidErrorCodeEnum.TRANSACTION_MODIFIED);
                    }
                }
            }
        }
    }

    /**
     * Cannot void transactions where authorized tender is missing account information, specifically
     * the token.
     * @throws VoidException
     * @since 13.4
     */
    protected void voidCheckAuthorizedTenderMissingToken() throws VoidException
    {

        boolean isTokenRequiredForPostVoidCredit = Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP, POS_TOKEN_REQUIRED_FOR_POST_VOID_CREDIT, true);
        boolean isTokenRequiredForPostVoidDebit = Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP, POS_TOKEN_REQUIRED_FOR_POST_VOID_DEBIT, true);

        if(transactionRDO instanceof TenderableTransactionIfc)
        {
            Collection<TenderLineItemIfc> creditTenders = ((TenderableTransactionIfc)transactionRDO).getTenderLineItems(TenderLineItemIfc.TENDER_TYPE_CHARGE);
            for(TenderLineItemIfc creditLineItem : creditTenders)
            {
                if(creditLineItem instanceof TenderChargeIfc &&
                        isTokenRequiredForPostVoidCredit &&
                        StringUtils.isEmpty(((TenderChargeIfc)creditLineItem).getAccountNumberToken()))
                {
                    throw new VoidException("Authorized Tender Not Voidable", VoidErrorCodeEnum.AUTH_TENDER_NOT_VOIDABLE);
                }
            }

        }
        Collection<TenderLineItemIfc> debitTenders = ((TenderableTransactionIfc)transactionRDO).getTenderLineItems(TenderLineItemIfc.TENDER_TYPE_DEBIT);
        for(TenderLineItemIfc debitLineItem : debitTenders)
        {
            if(debitLineItem instanceof TenderChargeIfc &&
                    isTokenRequiredForPostVoidDebit &&
                    StringUtils.isEmpty(((TenderChargeIfc)debitLineItem).getAccountNumberToken()))
            {
                throw new VoidException("Authorized Tender Not Voidable", VoidErrorCodeEnum.AUTH_TENDER_NOT_VOIDABLE);
            }
        }
    }

    /**
     * Requirements change in 13.1 disallows Post Void of transactions containing any
     * action with a gift card except sale transactions using gift card tender.
     *
     * @throws VoidException
     * @since 13.1
     */
    protected void voidCheckForGiftCardOperation() throws VoidException
    {
        // check if any line items are gift card reloads or issues
        if (transactionRDO instanceof RetailTransactionIfc)
        {
            RetailTransactionIfc retailTransaction = (RetailTransactionIfc)transactionRDO;
            // check the sale return line items
            Iterator<AbstractTransactionLineItemIfc> items = retailTransaction.getLineItemsIterator();
            while(items.hasNext())
            {
                SaleReturnLineItemIfc srLineItem = (SaleReturnLineItemIfc) items.next();
                if (srLineItem.isGiftCardIssue() || srLineItem.isGiftCardReload())
                {
                    throw new VoidException("Gift Card Reload or Issue Not Voidable", VoidErrorCodeEnum.GIFT_CARD_VOID_INVALID);
                }
            }

            // check the tender line items
            TenderLineItemIfc[] tenders = retailTransaction.getTenderLineItems();
            for (int i = 0; i < tenders.length; i++)
            {
                TenderLineItemIfc tender = tenders[i];
                // throw exception if any refund tender is gift card
                if(tender.getAmountTender().signum() == CurrencyIfc.NEGATIVE &&
                        tender.getTypeCode() == TenderLineItemIfc.TENDER_TYPE_GIFT_CARD)
                {
                    throw new VoidException("Gift Card Tender Is Not Voidable", VoidErrorCodeEnum.GIFT_CARD_VOID_INVALID);
                }
            }
        }
    }

    /**
     * Makes sure no line items have been returned agains this transaction
     *
     * @throws VoidException
     */
    protected void voidCheckForReturnItems() throws VoidException
    {
        AbstractTransactionLineItemIfc[] lineItems = ((RetailTransactionIfc) transactionRDO).getLineItems();
        SaleReturnLineItemIfc lineItem = null;

        if (lineItems != null)
        {
            for (int i = 0; i < lineItems.length; i++)
            {
                lineItem = (SaleReturnLineItemIfc) lineItems[i];
                if ((lineItem.getQuantityReturnedDecimal().compareTo(BigDecimal.ZERO) != 0))
                {
                    throw new VoidException("Transaction Modified", VoidErrorCodeEnum.TRANSACTION_MODIFIED);
                }
                if (lineItem.getOrderItemStatus() != null &&
                        lineItem.getOrderItemStatus().getQuantityReturned().compareTo(BigDecimal.ZERO) != 0)
                {
                    throw new VoidException("Transaction Modified", VoidErrorCodeEnum.TRANSACTION_MODIFIED);
                }
            }
        }
    }

    /**
     * Voidable transactions must check that the transaction has not been suspended.
     *
     * @throws VoidException
     */
    protected void voidCheckForSuspendedTransaction() throws VoidException
    {
        if (transactionRDO.getTransactionStatus() == TransactionIfc.STATUS_SUSPENDED)
        {
            throw new VoidException("Transaction is Suspended", VoidErrorCodeEnum.INVALID_TRANSACTION);
        }
    }

    /**
     * Voidable transactions must check that the transaction is being voided on the same till that the transaction was
     * wrung on.
     *
     * @param currentTillID
     *            The current active Till ID.
     * @throws VoidException
     *             Thrown when the Till ID's do not match.
     */
    protected void voidCheckForSameTill(String currentTillID) throws VoidException
    {
        if (!currentTillID.equals(transactionRDO.getTillID()))
        {
            throw new VoidException("Different Till", VoidErrorCodeEnum.DIFFERENT_TILL);
        }
    }

    /**
     * Checks to see if a void is allowed based on the fact that 1) Debits exist in the transaction 2) The appropriate
     * parameter allows void w/ debit.
     *
     * @throws VoidException
     */
    protected void voidCheckDebitAllowed() throws VoidException
    {
        // only check the paramet
        TenderGroupADOIfc debitGroup = getTenderGroup(TenderTypeEnum.DEBIT);
        if (debitGroup.getTenderCount() > 0)
        {
            UtilityIfc util = getUtility();

            if (util.getParameterValue("AllowPostVoidOnDebitTransaction", "Y").equals("N"))
            {
                throw new VoidException("Debit not allowed for void", VoidErrorCodeEnum.INVALID_TRANSACTION);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see oracle.retail.stores.ado.transaction.RetailTransactionADOIfc#removeTender(oracle.retail.stores.ado.tender.TenderADOIfc)
     */
    public void removeTender(TenderADOIfc tender)
    {
        // get the appropriate group
        TenderGroupADOIfc tenderGroup = getTenderGroup(tender.getTenderType());

        // remove tender from group
        tenderGroup.removeTender(tender);

        // also remove tender from RDO transaction
        ((TenderableTransactionIfc) transactionRDO).removeTenderLineItem((TenderLineItemIfc) ((ADO) tender).toLegacy());

        // If we're removing a tender that causes a transaction to be tax exempt
        // (like a purchase order from a tax exempt agency),
        // and this leaves us with no such tax exempt tenders,
        // then we clear the tax exmptions.
        // Otherwise, we don't want to clear the tax exempt status of the transaction.
        if (tender.getTenderAttributes().get(TenderConstants.TAXABLE_STATUS) != null &&
            tender.getTenderAttributes().get(TenderConstants.TAXABLE_STATUS).equals(TenderConstants.TAX_EXEMPT)
            && !containsTaxExemptPurchaseOrders())
        {
            clearTaxExempt();
        }
    }

    /**
     * Determines if the current tenders include any tax exempt purchase orders.
     *
     * @return True if the current tenders include any tax exempt purchase orders
     */
    public boolean containsTaxExemptPurchaseOrders()
    {
        boolean containsTaxExemptPurchaseOrders = false;

        TenderGroupADOIfc tenderGroupPurchaseOrder = getTenderGroup(TenderTypeEnum.PURCHASE_ORDER);
        TenderADOIfc[] tenders = tenderGroupPurchaseOrder.getTenders();
        HashMap<String,Object> tenderAttributes = null;
        for (int x = 0; !containsTaxExemptPurchaseOrders && x < tenders.length; x++)
        {
            tenderAttributes = tenders[x].getTenderAttributes();
            containsTaxExemptPurchaseOrders = TenderConstants.TAX_EXEMPT.equals(tenderAttributes.get(TenderConstants.TAXABLE_STATUS));
        }
        return containsTaxExemptPurchaseOrders;
    }

    /**
     * Attempt to add a tender. Invokes validation.
     *
     * @param tender
     *            The tender to be added
     * @throws TenderException
     *             Thrown when a validation error occurs.
     * @see oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc#addTender(TenderADOIfc)
     */
    public void addTender(TenderADOIfc tender) throws TenderException
    {
        TenderGroupADOIfc tenderGroup = getTenderGroup(tender.getTenderType());

        // add tender to group
        tenderGroup.addTender(tender);

        // also add tender to RDO
         ((TenderableTransactionIfc) transactionRDO).addTender((TenderLineItemIfc) ((ADO) tender).toLegacy());
    }

    /**
     * Attempt to add a tender. Invokes validation.
     *
     * @param tender
     *            The tender to be added
     */
    public void addValidTender(TenderADOIfc tender)
    {
        TenderGroupADOIfc tenderGroup = getTenderGroup(tender.getTenderType());

        // add tender to group with no validation
        tenderGroup.addTenderNoValidation(tender);

        // also add tender to RDO
         ((TenderableTransactionIfc) transactionRDO).addTender((TenderLineItemIfc) ((ADO) tender).toLegacy());
    }

    /**
     * For internal use only. Adds a tender bypassing validation logic.
     *
     * @param tender
     *            The tender to be added.
     * @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#addTenderNoValidation(TenderADOIfc)
     */
    protected void addTenderNoValidation(TenderADOIfc tender)
    {
        TenderGroupADOIfc tenderGroup = getTenderGroup(tender.getTenderType());

        tenderGroup.addTenderNoValidation(tender);
    }

    /*
     * (non-Javadoc)
     *
     * @see oracle.retail.stores.ado.transaction.RetailTransactionADOIfc#getTenderLineItems(oracle.retail.stores.ado.transaction.TenderLineItemCategoryEnum)
     */
    public TenderADOIfc[] getTenderLineItems(TenderLineItemCategoryEnum category)
    {
        TenderADOIfc[] result = null;
        if (category == TenderLineItemCategoryEnum.ALL)
        {
            result = getAllTenderLineItems();
        }
        else if (category == TenderLineItemCategoryEnum.AUTH_PENDING)
        {
            result = getAuthPendingTenderLineItems();
        }
        else if (category == TenderLineItemCategoryEnum.REVERSAL_PENDING)
        {
            result = getReversalPendingTenderLineItems();
        }
        else if (category == TenderLineItemCategoryEnum.VOID_AUTH_PENDING)
        {
            result = getVoidAuthPendingTenderLineItems();
        }
        else if (category == TenderLineItemCategoryEnum.FORCED_CASH_CHANGE)
        {
            result = getForcedCashChangeTenderLineItem();
        }
        else if (category == TenderLineItemCategoryEnum.POSITIVE_TENDERS)
        {
            result = getPositiveTenderLineItems();
        }
        else if (category == TenderLineItemCategoryEnum.NEGATIVE_TENDERS)
        {
            result = getNegativeTenderLineItems();
        }
        return result;
    }

    /**
     * Returns an array of all tenders on this transaction
     *
     * @return array of tenders
     */
    protected TenderADOIfc[] getAllTenderLineItems()
    {
        List<TenderADOIfc> tenderList = new ArrayList<TenderADOIfc>(10);
        // iterate through all groups and add every tender to the result
        Iterator<TenderGroupADOIfc> iter = tenderGroupMap.values().iterator();
        while (iter.hasNext())
        {
            TenderGroupADOIfc group = iter.next();
            List<TenderADOIfc> groupTenders = Arrays.asList(group.getTenders());
            tenderList.addAll(groupTenders);
        }
        // convert list to array and return
        TenderADOIfc[] result = new TenderADOIfc[tenderList.size()];
        result = tenderList.toArray(result);
        return result;
    }


    /**
     * Retrieve only positive (incoming) tenders.
     * Those are tenders that are given to the store by the customer
     * @return array of tenders
     */
    protected TenderADOIfc[] getPositiveTenderLineItems()
    {
        // get All the tenders and weed out the negative tenders
        TenderADOIfc[] allTenders = getAllTenderLineItems();

        List<TenderADOIfc> positiveTenders = new LinkedList<TenderADOIfc>();
        for (int i = 0; i < allTenders.length; i++)
        {
            if (allTenders[i].getAmount().signum() == CurrencyIfc.POSITIVE)
            {
                positiveTenders.add(allTenders[i]);
            }
        }

        // convert list to array and return
        TenderADOIfc[] result = new TenderADOIfc[positiveTenders.size()];
        result = positiveTenders.toArray(result);
        return result;
    }

    /**
     * Retrieve only negative (outgoing) tenders.
     * Those are tenders that are given to the customer by the store
     * @return array of tenders
     */
    protected TenderADOIfc[] getNegativeTenderLineItems()
    {
        // get All the tenders and weed out the positive tenders
        TenderADOIfc[] allTenders = getAllTenderLineItems();

        List<TenderADOIfc> negativeTenders = new LinkedList<TenderADOIfc>();
        for (int i = 0; i < allTenders.length; i++)
        {
            if (allTenders[i].getAmount().signum() == CurrencyIfc.NEGATIVE)
            {
                negativeTenders.add(allTenders[i]);
            }
        }

        // convert list to array and return
        TenderADOIfc[] result = new TenderADOIfc[negativeTenders.size()];
        result = negativeTenders.toArray(result);
        return result;
    }

    /**
     * Returns an array of all tenders still requiring authorization.
     *
     * @return array of tenders
     */
    protected TenderADOIfc[] getAuthPendingTenderLineItems()
    {
        List<TenderADOIfc> tenderList = new ArrayList<TenderADOIfc>(10);

        // If in training mode or re-entry mode, simply return an empty array
        RegisterADO registerADO = getContext().getRegisterADO();
        if (registerADO.isInMode(RegisterMode.TRAINING) || registerADO.isInMode(RegisterMode.REENTRY))
        {
            // convert list to array and return
            TenderADOIfc[] result = new TenderADOIfc[tenderList.size()];
            result = tenderList.toArray(result);
            return result;
        }

        // iterate through all groups and add every tender to the result
        Iterator<TenderGroupADOIfc> iter = tenderGroupMap.values().iterator();
        // the allowable tenders are orderable
        // giftcard, credit, check, and then debit
        TenderGroupADOIfc debitGroup = null;
        TenderGroupADOIfc checkGroup = null;
        TenderGroupADOIfc creditGroup = null;

        while (iter.hasNext())
        {
            TenderGroupADOIfc group = iter.next();
            if (group instanceof TenderGroupDebitADO)
            {
                debitGroup = group;
            }
            else if (group instanceof TenderGroupCheckADO)
            {
                checkGroup = group;
            }
            else if (group instanceof TenderGroupCreditADO)
            {
                creditGroup = group;
            }
            else if (group instanceof AuthorizableTenderGroupADOIfc)
            {
                pullAuthPendingTendersFromGroup(tenderList, group);
            }
        }
        // add credit
        if (creditGroup != null)
        {
            pullAuthPendingTendersFromGroup(tenderList, creditGroup);
        }
        // add check
        if(checkGroup != null)
        {
            pullAuthPendingTendersFromGroup(tenderList, checkGroup);
        }
        // now add the debit group so it is LAST!
        if (debitGroup != null)
        {
            pullAuthPendingTendersFromGroup(tenderList, debitGroup);
        }

        // convert list to array and return
        TenderADOIfc[] result = new TenderADOIfc[tenderList.size()];
        result = tenderList.toArray(result);
        return result;
    }

    /**
     * Pulls all the tenders from a named from and adds them to a supplied list.
     *
     * @param tenderList
     * @param group
     */
    protected void pullAuthPendingTendersFromGroup(List<TenderADOIfc> tenderList, TenderGroupADOIfc group)
    {
        // iterate through tenders in group and only get tenders
        // needing authorization
        if (!(evaluateTenderState() == TenderStateEnum.REFUND_DUE) ||
                (evaluateTenderState() == TenderStateEnum.REFUND_DUE &&
                        (group instanceof TenderGroupDebitADO || group instanceof TenderGroupCreditADO)))
        {
            tenderList.addAll(group.pullAuthPendingTendersFromGroup(tenderList, group));
        }


    }

    /**
     * Returns an array of all tenders still requiring authorization.
     *
     * @return array of tenders
     */
    protected TenderADOIfc[] getReversalPendingTenderLineItems()
    {
        List<TenderADOIfc> tenderList = new ArrayList<TenderADOIfc>(10);

        // If in training mode, simply return an empty array
        RegisterADO registerADO = getContext().getRegisterADO();
        if (registerADO.isInMode(RegisterMode.TRAINING))
        {
            // convert list to array and return
            TenderADOIfc[] result = new TenderADOIfc[tenderList.size()];
            result = tenderList.toArray(result);
            return result;
        }

        // iterate through all groups and add every tender to the result
        Iterator<TenderGroupADOIfc> iter = tenderGroupMap.values().iterator();
        while (iter.hasNext())
        {
            TenderGroupADOIfc group = iter.next();
            // The group must contain reversible tenders to be included
            if (group instanceof AuthorizableTenderGroupADOIfc
                && ((AuthorizableTenderGroupADOIfc) group).isReversible())
            {
                // iterate through tenders in group and only get tenders
                // needing authorization
                TenderADOIfc[] tenders = group.getTenders();
                for (int i = 0; i < tenders.length; i++)
                {
                    // only add authorized tenders that have not been reversed
                    if (((AuthorizableADOIfc) tenders[i]).isAuthorized()
                        && !((ReversibleTenderADOIfc) tenders[i]).isReversed())
                    {
                        tenderList.add(tenders[i]);
                    }
                }
            }
        }

        // convert list to array and return
        TenderADOIfc[] result = new TenderADOIfc[tenderList.size()];
        result = tenderList.toArray(result);
        return result;
    }

    /**
     * Returns an array of all tenders still requiring authorization.
     *
     * @return array of tenders
     */
    protected TenderADOIfc[] getVoidAuthPendingTenderLineItems()
    {
        List<AbstractTenderADO> tenderList = new ArrayList<AbstractTenderADO>();

        // If in training mode, simply return an empty array
        RegisterADO registerADO = getContext().getRegisterADO();
        if (registerADO.isInMode(RegisterMode.TRAINING))
        {
            // convert list to array and return
            TenderADOIfc[] result = new TenderADOIfc[tenderList.size()];
            result = tenderList.toArray(result);
            return result;
        }

        // iterate through all groups and add every tender to the result
        Iterator<TenderGroupADOIfc> iter = tenderGroupMap.values().iterator();
        while (iter.hasNext())
        {
            TenderGroupADOIfc group = iter.next();
            // The group must contain authorizable tenders to be included
            if (group instanceof AuthorizableTenderGroupADOIfc)
            {
                // iterate through tenders in group and only get tenders
                // needing authorization
                if (((AuthorizableTenderGroupADOIfc) group).isReversible())
            	{
            		tenderList.addAll(((AuthorizableTenderGroupADOIfc)group).getVoidAuthPendingTenderLineItems());
            	}
            	else
            	{
            		tenderList.addAll(group.pullAuthPendingTendersFromGroup(tenderList, group));
            	}
            }
        }

        // convert list to array and return
        TenderADOIfc[] result = new TenderADOIfc[tenderList.size()];
        result = tenderList.toArray(result);
        return result;
    }

    /**
     * Retrieves any existing forced cash change tender only.
     * @return array of tenders
     */
    protected TenderADOIfc[] getForcedCashChangeTenderLineItem()
    {
        TenderGroupCashADO cashGroup = (TenderGroupCashADO)tenderGroupMap.get(TenderTypeEnum.CASH);

        TenderADOIfc[] result = null;
        if (cashGroup != null)
        {
            // find cash tender with negative value
            TenderADOIfc[] cashTenders = cashGroup.getTenders();
            for (int i = 0; i < cashTenders.length; i++)
            {
                if (cashTenders[i].getAmount().signum() == CurrencyIfc.NEGATIVE)
                {
                    result = new TenderADOIfc[1];
                    result[0] = cashTenders[i];
                    break;
                }
            }
        }

        if (result == null)
        {
            result = new TenderADOIfc[0];
        }
        return result;
    }

    /**
     * This method removes all tenders that are either not authorized or are unauthorizable.
     */
    public void deleteNonAuthorizedTenders()
    {
        TenderADOIfc[] tenders = getAllTenderLineItems();
        // if there are any tenders left
        if (tenders != null)
        {
            // go through all the tenders
            for (int i = 0; i < tenders.length; i++)
            {
                // if an authorizable tender
                if (tenders[i] instanceof AuthorizableADOIfc)
                {
                    // if not already authorized
                    if (!((AuthorizableADOIfc) tenders[i]).isAuthorized())
                    {
                        // remove tender
                        removeTender(tenders[i]);
                    }
                }
                else
                {
                    // remove non-authorizable tenders
                    removeTender(tenders[i]);
                }
            }
        }
    }


    /**
     * This method validates the tender limits.
     *
     * @param tenderAttributes
     * @throws TenderException
     * @see oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc#validateTenderLimits(java.util.HashMap)
     */
    public void validateTenderLimits(HashMap tenderAttributes) throws TenderException
    {
        assert(tenderAttributes.get(TenderConstants.TENDER_TYPE) != null);

        // validate the tender limits and overtender
        TenderGroupADOIfc group =
            getTenderGroup((TenderTypeEnum) tenderAttributes.get(TenderConstants.TENDER_TYPE));

        group.validateOvertender(tenderAttributes, getBalanceDue(), getOvertenderLimit());

        // Skip tender limit validation if we're in transaction reentry mode
        if (!(transactionRDO.getWorkstation().isTransReentryMode()))
        {
            group.validateLimits(tenderAttributes, getBalanceDue());
        }
    }

    /**
     * This method validates the refund limits.
     *
     * @param tenderAttributes
     * @param hasReceipt
     * @param retrieved
     * @throws TenderException
     * @see oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc#validateRefundLimits(java.util.HashMap, boolean, boolean)
     */
    public void validateRefundLimits(HashMap tenderAttributes, boolean hasReceipt, boolean retrieved) throws TenderException
    {
        assert(tenderAttributes.get(TenderConstants.TENDER_TYPE) != null);

        // overtendering in a return is not allowed
        CurrencyIfc tenderAmount =
            DomainGateway.getBaseCurrencyInstance((String) tenderAttributes.get(TenderConstants.AMOUNT)).abs();
        if (tenderAmount.compareTo(getBalanceDue().abs()) == CurrencyIfc.GREATER_THAN)
        {
            throw new TenderException("Overtender not allowed in a return", TenderErrorCodeEnum.OVERTENDER_ILLEGAL);
        }

        // Skip tender limit validation if we're in transaction reentry mode
        if (!(transactionRDO.getWorkstation().isTransReentryMode()))
        {
            // validate specific limits for this tender
            TenderGroupADOIfc group = getTenderGroup((TenderTypeEnum) tenderAttributes.get(TenderConstants.TENDER_TYPE));
            group.validateRefundLimits(tenderAttributes, hasReceipt, retrieved);
        }
    }

    /**
     * This method validates the change limits.
     *
     * @param tenderAttributes
     * @param hasReceipt
     * @param retrieved
     * @throws TenderException
     * @see oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc#validateChangeLimits(java.util.HashMap, boolean, boolean)
     */
    public void validateChangeLimits(HashMap tenderAttributes, boolean hasReceipt, boolean retrieved) throws TenderException
    {
        // Skip tender limit validation if we're in transaction reentry mode
        if (!(transactionRDO.getWorkstation().isTransReentryMode()))
        {
            // validate specific limits for this tender
            TenderGroupADOIfc group = getTenderGroup((TenderTypeEnum) tenderAttributes.get(TenderConstants.TENDER_TYPE));
            TenderGroupCheckADO checkGroup = (TenderGroupCheckADO)tenderGroupMap.get(TenderTypeEnum.CHECK);
            if(group instanceof TenderGroupCashADO)
            {
                if (checkGroup == null || (checkGroup != null && !checkGroup.isMaxOvertenderLimitViolated()))
                {
                    String strAmount = (String)tenderAttributes.get(TenderConstants.AMOUNT);
                    CurrencyIfc tenderAmount = DomainGateway.getBaseCurrencyInstance(strAmount);
                    if (tenderAmount.signum() == CurrencyIfc.NEGATIVE)
                    {
                        tenderAmount = ((TenderableTransactionIfc) transactionRDO).getTenderTransactionTotals().getBalanceDue().abs();
                    }
                    ((TenderGroupCashADO)group).validateChangeLimits(tenderAmount, calculateMaxAllowableCashChange(), cashOnlyForChangeDue);
                }
            }
            else
            {
                validateRefundLimits(tenderAttributes, hasReceipt, retrieved);
            }
        }
    }

    /**
     * This method checks to see if the negative cash should be treated as a
     * refund or as change.
     *
     * @return boolean true = is a cash refund
     */
    protected boolean isCashRefund()
    {
        boolean result = false;
        // if we are in a return, redeem, order cancel, or layaway delete then a negative cash is a refund and not change
        if (transactionRDO.getTransactionType() != TransactionIfc.TYPE_RETURN &&
            transactionRDO.getTransactionType() != TransactionIfc.TYPE_REDEEM &&
            transactionRDO.getTransactionType() != TransactionIfc.TYPE_ORDER_CANCEL &&
            transactionRDO.getTransactionType() != TransactionIfc.TYPE_LAYAWAY_DELETE)
        {
            result = true;
        }
        return result;
    }

    /**
     * Calculates the total tender for this transaction without accounting
     * for forced cash change tenders (negative cash), which are accounted for
     * instead in the change due.
     *
     * @return Currency object
     */
    protected CurrencyIfc getTenderTotal()
    {
        CurrencyIfc result = ((TenderableTransactionIfc) transactionRDO).getTransactionTotals().getAmountTender();

        if (isCashRefund())
        {
            result = result.subtract(((TenderableTransactionIfc) transactionRDO).getNegativeCashTotal());
        }

        return result;
    }

    /**
     * Returns the total tender for a particular tender type
     *
     * @return Currency object
     */
    public CurrencyIfc getTenderTotal(TenderTypeEnum type)
    {
        // Until we model some kind of financials for a transaction,
        // we will defer to the RDO for this information. Note that
        // this requires that the RDO must be current.
        return getTenderGroup(type).getTenderTotal();
    }

    /**
     * Returns the balance for this transaction
     *
     * @return Currency object
     */
    public CurrencyIfc getBalanceDue()
    {
        // Until we model some kind of financials for a transaction,
        // we will defer to the RDO for this information. Note that
        // this requires that the RDO must be current.
        return ((TenderableTransactionIfc) transactionRDO).getTenderTransactionTotals().getBalanceDue();
    }


    /**
     * Returns the Rounded CashChange amount for this transaction
     *
     * @return Currency object
     */
    public CurrencyIfc getCashChangeRoundingAdjustment()
    {
        return ((TenderableTransactionIfc) transactionRDO).getTenderTransactionTotals().getCashChangeRoundingAdjustment();
    }

    /**
     * Calculates the change given on a transaction considering the Rounded Cash Change amount
     *
     * @return changeGiven as CurrencyIfc
     */
    public CurrencyIfc getChangeGiven()
    {
        CurrencyIfc changeGiven = DomainGateway.getBaseCurrencyInstance();

        if (transactionRDO instanceof AbstractTenderableTransaction)
        {
            changeGiven =  ((AbstractTenderableTransaction)transactionRDO).calculateChangeGiven();
        }
        return changeGiven;
    }


    /**
     * Returns the amount of tender in the transaction.
     *
     * @return Currency object
     */
    public CurrencyIfc getAmountTender()
    {
        // Until we model some kind of financials for a transaction,
        // we will defer to the RDO for this information. Note that
        // this requires that the RDO must be current.
        return ((TenderableTransactionIfc)transactionRDO).getTenderTransactionTotals().getAmountTender();
    }

    /**
     * Add the cash change amounts from all gift card tenders and return that value
     * @return Currency object
     */
    public CurrencyIfc getForcedCashChangeAmount()
    {
        CurrencyIfc result = DomainGateway.getBaseCurrencyInstance();
        TenderGroupGiftCardADO giftCardGroup = (TenderGroupGiftCardADO)tenderGroupMap.get(TenderTypeEnum.GIFT_CARD);
        if (giftCardGroup != null)
        {
            result = giftCardGroup.getCashChangeTotal();
        }
        return result;
    }

    /**
     * This method calculates the total cash change amount.  Cash change
     * consists of any balance due plus depleted gift card balance amounts.
     * This value is always returned as a positive value.
     * @return Currency object
     */
    public CurrencyIfc getTotalCashChangeAmount()
    {
        CurrencyIfc cashChange = DomainGateway.getBaseCurrencyInstance();//null; - CR 4792

        // cash is a refund during a return and redeem, not change
        if (isCashRefund())
        {
            TenderADOIfc[] negTenders = getNegativeTenderLineItems();
            if (negTenders.length > 0)
            {
                for (int i = 0; i < negTenders.length; i++)
                {
                    if (negTenders[i].getTenderType().equals(TenderTypeEnum.CASH))
                    {
                        // there can only be one negative cash change.  there can be more than one negative refund cash
                        // but this is only used in change.
                        cashChange = cashChange.add(negTenders[i].getAmount().abs());
                        //break;
                    }
                }
            }
        }
        if (cashChange.signum() == CurrencyIfc.ZERO)
        {
            cashChange = getForcedCashChangeAmount().abs();
        }
        return cashChange;
    }

    /**
     * Returns the grand total for this transaction
     *
     * @return Currency object
     */
    protected CurrencyIfc getTransactionGrandTotal()
    {
        // Until we model some kind of financials for a transaction,
        // we will defer to the RDO for this information. Note that
        // this requires that the RDO must be current.
        return ((TenderableTransactionIfc) transactionRDO).getTenderTransactionTotals().getGrandTotal();
    }

    /**
     * Evaluate the tender state.
     *
     * @return TenderStateEnum result
     */
    public TenderStateEnum evaluateTenderState()
    {
        // recalculate transaction total if needed based on dirty flag
        // set by tender.
        recalculateTransactionTotal();

        TenderStateEnum result = null;
        CurrencyIfc balanceDue = getBalanceDue();
        CurrencyIfc transactionGrandTotal = getTransactionGrandTotal();
        // 1) if the balance is positive, tenders are due
        if (balanceDue.signum() == CurrencyIfc.POSITIVE)
        {
            result = TenderStateEnum.TENDER_OPTIONS;
        }
        // 2) if the balance is negative, the forced cash change is zero, and
        //    the grand total of the transaction is positive, then change is due
        else if (balanceDue.signum() == CurrencyIfc.NEGATIVE &&
                 getForcedCashChangeAmount().signum() == CurrencyIfc.ZERO &&
                 transactionGrandTotal.signum() == CurrencyIfc.POSITIVE)
        {
            result = TenderStateEnum.CHANGE_DUE;
        }
        // 3) If the balance is negative, forced cash change is positive, and
        //    grand total is positive, then change due
        else if (balanceDue.signum() == CurrencyIfc.NEGATIVE &&
                 getForcedCashChangeAmount().signum() == CurrencyIfc.POSITIVE &&
                 transactionGrandTotal.signum() == CurrencyIfc.POSITIVE)
        {
            result = TenderStateEnum.CHANGE_DUE;
        }
        // 3) If the balance is zero, forced cash change is positive, and
        //    grand total is positive, then paid up
        else if (balanceDue.signum() == CurrencyIfc.ZERO &&
                 getForcedCashChangeAmount().signum() == CurrencyIfc.POSITIVE &&
                 transactionGrandTotal.signum() == CurrencyIfc.POSITIVE)
        {
            result = TenderStateEnum.PAID_UP;
        }
        // 4) if the balance is negative, then refund tenders are due.
        else if (balanceDue.signum() == CurrencyIfc.NEGATIVE)
        {
            result = TenderStateEnum.REFUND_OPTIONS;
        }
        // 5) if the balance is negative, and the transaction is a return or resume
        else if (balanceDue.signum() == CurrencyIfc.ZERO &&
                (transactionRDO.getTransactionType() == TransactionConstantsIfc.TYPE_REDEEM ||
                 transactionRDO.getTransactionType() == TransactionConstantsIfc.TYPE_RETURN))
        {
            result = TenderStateEnum.REFUND_DUE;
        }
        else if (transactionGrandTotal.signum() == CurrencyIfc.NEGATIVE &&
                 transactionRDO instanceof OrderTransactionIfc)
        {
            result = TenderStateEnum.REFUND_DUE;
        }
        // 6) All paid up
        else
        {
            result = TenderStateEnum.PAID_UP;
        }

        return result;
    }

    /**
     * Encase this call so that it can be overriden for unit tests.
     *
     * @return Tender group factory
     * @throws ADOException
     */
    protected TenderGroupFactoryIfc getGroupFactory() throws ADOException
    {
        return (TenderGroupFactoryIfc) ADOFactoryComplex.getFactory("factory.tender.group");
    }

    /**
     * Retrieves a tender group corresponding to a given tender type
     *
     * @param tenderType
     *            Type of group to return
     * @return The desired tender group
     */
    protected TenderGroupADOIfc getTenderGroup(TenderTypeEnum tenderType)
    {
        // Add ADO tender to ADO tender group.
        TenderGroupADOIfc tenderGroup = tenderGroupMap.get(tenderType);
        if (tenderGroup == null)
        {
            TenderGroupFactoryIfc groupFactory = null;
            try
            {
                groupFactory = getGroupFactory();
            }
            catch (ADOException e)
            {
                logger.error("Could not get Tender Group factory.", e);
                throw new RuntimeException("Could not get Tender Group factory.", e);
            }
            tenderGroup = groupFactory.createTenderGroup(tenderType);
            tenderGroupMap.put(tenderGroup.getGroupType(), tenderGroup);
        }
        return tenderGroup;
    }

    /**
     * Extracts Currency Part from the Parameter Values for ChecksAccepted and TravelersChecksAccepted.
     *
     * @param checkList
     *            Array of Parameter Values
     * @return An array of Parameter Values with currency part extracted
     */
    protected String[] extractCheckCurrencyPart(String[] checkList)
    {
        String[] currencyChecks = new String[checkList.length];
        for (int i = 0; i < checkList.length; i++)
        {
            if (checkList[i].indexOf("CHK") != -1) // not None
            {
                String extractedCurrency = checkList[i].substring(0, checkList[i].indexOf("CHK"));
                currencyChecks[i] = extractedCurrency;
            }
            else
            {
                currencyChecks[i] = checkList[i];
            }
        }
        return currencyChecks;
    }

    /**
     * Returns an array of alternate currencies
     *
     * @param baseCurrency
     * @param list
     * @return ArrayList an array of alternate currencies
     */
    protected ArrayList<String> getAltCurrenciesAccepted(String baseCurrency, String[] list)
    {
        ArrayList<String> altCurrencies = new ArrayList<String>();
        if (list != null)
        {
            for (int i = 0; i < list.length; i++)
            {
                if (!baseCurrency.equals(list[i]))
                {
                    altCurrencies.add(list[i]);
                }
            }
        }
        return altCurrencies;
    }
    /**
     * This method checks to see if all tenders used in the original
     * transaction are of debit type
     * @param type Tender Type
     * @return true if all the tenders are debit transaction
     */

    protected boolean isAllReturnTenderDebit()
    {
        boolean isDebitTender = true;
        if (transactionRDO instanceof SaleReturnTransactionIfc)
        {
            ReturnTenderDataElementIfc[] tenders = ((SaleReturnTransactionIfc)transactionRDO).getReturnTenderElements();
            if (tenders != null)
            {
                for (int i = 0; i < tenders.length; i++)
                {
                    // if tender type is debit
                    if (tenders[i].getTenderType() != TenderLineItemIfc.TENDER_TYPE_DEBIT)
                    {
                        isDebitTender = false;
                        break;
                    }
                }
            }
        }

        return isDebitTender;
    }

    /**
     * Specific transaction types that need additional logic should override this and call 'super()' to
     * get the base list.
     *
     * @see oracle.retail.stores.ado.transaction.RetailTransactionADOIfc#getEnabledTenderOptions()
     */
    public TenderTypeEnum[] getEnabledTenderOptions()
    {
        // temporary list. initialize to a size that can hold
        // all tender types
        ArrayList<TenderTypeEnum> tenderList = new ArrayList<TenderTypeEnum>(14);

        // local String constants
        final String NONE = "None";

        // create utility object
        UtilityIfc util = getUtility();

        ///////
        // Cash
        String[] cashAccepted = util.getParameterValueList("CashAccepted");
        if (cashAccepted == null)
        {
            // initialize to take base currency
            cashAccepted = new String[1];
            cashAccepted[0] = NONE;
        }
        // if our base cash description exists as one of the cash accepted options, add to list
        String isoCode = DomainGateway.getBaseCurrencyInstance().getCurrencyCode();
        if (util.isStringListed(isoCode, cashAccepted))
        {
            tenderList.add(TenderTypeEnum.CASH);
        }

        ////////
        // Check
        String[] checksAccepted = util.getParameterValueList("ChecksAccepted");
        if (checksAccepted == null)
        {
            checksAccepted = new String[1];
            checksAccepted[0] = NONE;
        }
        else
        {
            checksAccepted = extractCheckCurrencyPart(checksAccepted);
        }
        // Add Check to the list if the base currency is listed in the check currencies
        if (util.isStringListed(isoCode, checksAccepted))
        {
            tenderList.add(TenderTypeEnum.CHECK);
        }

        /////////
        // Non-Store Coupon
        if (util.getParameterValue("NonStoreCouponsAccepted", "Y").equalsIgnoreCase("Y"))
        {
            tenderList.add(TenderTypeEnum.COUPON);
        }

        /////////
        // Credit
        if (util.getParameterValue(ParameterConstantsIfc.TENDER_CreditDebitCardsAccepted, "Y").equalsIgnoreCase("Y"))
        {
            tenderList.add(TenderTypeEnum.CREDIT);

            // do not accept debit if in reentry mode
            WorkstationIfc ws = transactionRDO.getWorkstation();
            boolean transactionReentry = ws.isTransReentryMode();
            if (!transactionReentry)
            {
                tenderList.add(TenderTypeEnum.DEBIT);
            }
        }

        /////////
        // House Account
        if (util.getParameterValue(ParameterConstantsIfc.TENDER_HouseCardsAccepted, "Y").equalsIgnoreCase("Y"))
        {
            tenderList.add(TenderTypeEnum.HOUSE_ACCOUNT);
        }

        ////////////
        // Gift Card
        if (util.getParameterValue(ParameterConstantsIfc.TENDER_GiftCardsAccepted, "Y").equalsIgnoreCase("Y"))
        {
            tenderList.add(TenderTypeEnum.GIFT_CARD);
        }

        ////////
        // Gift Cert
        String[] gcAccepted = util.getParameterValueList("GiftCertificatesAccepted");
        if (gcAccepted == null)
        {
            gcAccepted = new String[1];
            gcAccepted[0] = NONE;
        }
        // Add gift certificate to the list if the base currency is listed in the gift certificate currencies
        if (util.isStringListed(isoCode, gcAccepted))
        {
            tenderList.add(TenderTypeEnum.GIFT_CERT);
        }

        /////////////////
        // Purchase Order
        if (util.getParameterValue("PurchaseOrdersAccepted", "Y").equalsIgnoreCase("Y"))
        {
            tenderList.add(TenderTypeEnum.PURCHASE_ORDER);
        }

        String[] scAccepted = util.getParameterValueList("StoreCreditsAccepted");
        if (scAccepted == null)
        {
            scAccepted = new String[1];
            scAccepted[0] = NONE;
        }

        // Add store credit to the list if the base currency is listed in the store credit currencies
        if (util.isStringListed(isoCode, scAccepted))
        {
            tenderList.add(TenderTypeEnum.STORE_CREDIT);
        }

        ///////////////
        // Mall Certificate
        if (util.getParameterValue("MallCertificateAccepted", "Y").equalsIgnoreCase("Y"))
        {
            tenderList.add(TenderTypeEnum.MALL_CERT);
        }

        ///////////////
        // Money Order
        if (util.getParameterValue("MoneyOrderAccepted", "Y").equalsIgnoreCase("Y"))
        {
            tenderList.add(TenderTypeEnum.MONEY_ORDER);
        }

        /////////////////
        // Traveler Check
        String[] travChecksAccepted = util.getParameterValueList("TravelersChecksAccepted");
        if (travChecksAccepted == null)
        {
            travChecksAccepted = new String[1];
            travChecksAccepted[0] = NONE;
        }
        else
        {
            travChecksAccepted = extractCheckCurrencyPart(travChecksAccepted);
        }
        // Add Traveler Check to the list if the base currency is listed in the check currencies
        if (util.isStringListed(isoCode, travChecksAccepted))
        {
            tenderList.add(TenderTypeEnum.TRAVELERS_CHECK);
        }

        // Enable the button for the "Alternate" currency if Cash or Traveler's checks or Checks
        // are accepted in more than one currency and there are alternate currencies
        // available.
        // Note that "Alternate" is not the label for the button, just the action
        CurrencyTypeIfc[] altCurrencies = DomainGateway.getAlternateCurrencyTypes();
        String baseCurrency = DomainGateway.getBaseCurrencyInstance().getCurrencyCode();
        ArrayList<String> parmAltCash = getAltCurrenciesAccepted(baseCurrency, cashAccepted);
        ArrayList<String> parmAltTC = getAltCurrenciesAccepted(baseCurrency, travChecksAccepted);
        ArrayList<String> parmAltCheck = getAltCurrenciesAccepted(baseCurrency, checksAccepted);

        if ((altCurrencies != null) && (altCurrencies.length > 0)) // are there alternate currencies to use
        {
            String firstAltCurr = altCurrencies[0].getCurrencyCode();

            // If the first alternate currency (from domain) appears in one of the
            // accepted tender parameters
            if (util.isStringListed(firstAltCurr, parmAltCash.toArray())
                || util.isStringListed(firstAltCurr, parmAltTC.toArray())
                || util.isStringListed(firstAltCurr, parmAltCheck.toArray()))
            {
                tenderList.add(TenderTypeEnum.ALTERNATE);
            }
        }

        // convert list to array
        TenderTypeEnum[] tenderTypeArray = new TenderTypeEnum[tenderList.size()];
        tenderTypeArray = tenderList.toArray(tenderTypeArray);
        return tenderTypeArray;
    }

    /** ***************************************************************************************** */
    //-------------------------------------------------------------------------------------------
    /*
     * The Following Method are used to determine which buttons should be enabled for refund options screen
     */
    //-------------------------------------------------------------------------------------------
    /** ****************************************************************************************** */

    /**
     * This method checks to see if there was more than one orignal transaction id for the return items.
     *
     * @return boolean true if has only one original transaction otherwith false
     */
    protected boolean hasOnlyOneOriginalTransaction()
    {
        boolean oneOriginal = true;
        AbstractTransactionLineItemIfc[] items = ((SaleReturnTransactionIfc) transactionRDO).getLineItems();
        String origTransNumber = null;
        boolean nonRetrievedItems = false;
        // if there is only one item, assume only one trans id
        if (items.length > 1)
        {
            for (int i = 0; i < items.length; i++)
            {
                if (items[i] instanceof SaleReturnLineItemIfc)
                {
                    ReturnItemIfc item = ((SaleReturnLineItemIfc) items[i]).getReturnItem();
                    if (item != null)
                    {
                        // if this is the first item, get the trans id
                        if (origTransNumber == null)
                        {
                            if (item.getOriginalTransactionID() != null)
                            {
                                // there were non retrieved items and
                                // now a retrieved item then assume multiple
                                // transactions
                                if (nonRetrievedItems)
                                {
                                    oneOriginal = false;
                                    break;
                                }

                                origTransNumber = item.getOriginalTransactionID().getTransactionIDString();
                            }
                            // if already had non retrieved items
                            else
                            {
                                nonRetrievedItems = true;
                            }
                        }
                        // else check if there is a different trans id
                        else if (item.getOriginalTransactionID() != null)
                        {
                            if (!origTransNumber.equals(item.getOriginalTransactionID().getTransactionIDString())
                                || nonRetrievedItems)
                            {
                                oneOriginal = false;
                                break;
                            }
                        }
                        // else know that there was a return item with transaction id
                        // and now there is a non retrieved item.
                        // assume multiple transactions
                        else
                        {
                            oneOriginal = false;
                            break;
                        }
                    }
                }
            }
        }
        return oneOriginal;
    }

    /**
     * This method determines if there was only one type of tender used in the original transaction.
     *
     * @return True if there was only one type of tender used in the original transaction
     */
    protected boolean hasOnlyOneOriginalTender()
    {
        boolean oneTender = true;
        // do we have a positve tender
        boolean positiveTender = false;
        if (transactionRDO instanceof SaleReturnTransactionIfc)
        {
            ReturnTenderDataElementIfc[] tenders =
                ((SaleReturnTransactionIfc) transactionRDO).getReturnTenderElements();

            if (tenders != null)
            {
                for (int i = 0; i < tenders.length; i++)
                {
                    if (tenders[i].getTenderAmount().signum() == CurrencyIfc.POSITIVE)
                    {
                        // did we find a positve tender already?
                        if (!positiveTender)
                        {
                            // this is the first tender
                            positiveTender = true;
                        }
                        else
                        {
                            // then we have more than one tender
                            oneTender = false;
                            break;
                        }
                    }
                }
            }
        }
        return oneTender;
    }

    /**
     * This method determines if the transaction contains a retrieved original transaction.
     *
     * @return true if transaction contains a retrieved original transaction.
     */
    protected boolean hasBeenRetrieved()
    {

        boolean retrieved = false;

        AbstractTransactionLineItemIfc[] items = ((SaleReturnTransactionIfc) transactionRDO).getLineItems();
        if (items != null && items.length > 0)
        {
            for (int i = 0; i < items.length; i++)
            {
                if (items[i] instanceof SaleReturnLineItemIfc)
                {
                    ReturnItemIfc item = ((SaleReturnLineItemIfc) items[i]).getReturnItem();
                    if (item != null && item.isFromRetrievedTransaction())
                    {
                        retrieved = true;
                        break;
                    }
                }
            }
        }
        return retrieved;
    }

    /**
     * This method checks to see if a regular receipt was supplied by the customer.
     * See the hasGiftReceipt() method for testing for gift receipt
     * @return true if a regular receipt was supplied by the customer.
     */
    protected boolean hasReceipt()
    {
        boolean hasReceipt = false;

        AbstractTransactionLineItemIfc[] items = ((SaleReturnTransactionIfc) transactionRDO).getLineItems();
        if (items != null && items.length > 0)
        {
            for (int i = 0; i < items.length; i++)
            {
                if (items[i] instanceof SaleReturnLineItemIfc)
                {
                    ReturnItemIfc item = ((SaleReturnLineItemIfc) items[i]).getReturnItem();
                    if (item != null)
                    {
                        if (item.haveReceipt())
                        {
                            hasReceipt = true;
                            break;
                        }
                    }
                }
            }
        }
        return hasReceipt;
    }

    /**
     * This method checks to see if a gift receipt was supplied by the customer.
     * See the hasReceipt() method for testing for regular receipt
     * @return true if a gift receipt was supplied by the customer.
     */
    protected boolean hasGiftReceipt()
    {
        boolean hasReceipt = false;

        AbstractTransactionLineItemIfc[] items = ((SaleReturnTransactionIfc) transactionRDO).getLineItems();
        if (items != null && items.length > 0)
        {
            for (int i = 0; i < items.length; i++)
            {
                if (items[i] instanceof SaleReturnLineItemIfc)
                {
                    ReturnItemIfc item = ((SaleReturnLineItemIfc) items[i]).getReturnItem();
                    if (item != null)
                    {
                        if (item.isFromGiftReceipt())
                        {
                            hasReceipt = true;
                            break;
                        }
                    }
                }
            }
        }
        return hasReceipt;
    }

    /**
     * This method checks to see if the type selected was a tender used in the orignal transaction
     *
     * @param type
     *            Tender Type
     * @return true if the type selected was a tender used in the orignal transaction
     */
    protected boolean isReturnTender(int type)
    {
        boolean hasOnlyOneTender = false;
        if (transactionRDO instanceof SaleReturnTransactionIfc)
        {
            ReturnTenderDataElementIfc[] tenders =
                ((SaleReturnTransactionIfc) transactionRDO).getReturnTenderElements();
            if (tenders != null)
            {
                for (int i = 0; i < tenders.length; i++)
                {
                 // if tender type is equal to the parameter type irrelevant of whether it is a positive/negative tender
                    if (tenders[i].getTenderType() == type)
                    {
                        hasOnlyOneTender = true;
                        break;
                    }
                }
            }
        }

        return hasOnlyOneTender;
    }

    /**
     * This method checks to see if the card type selected was a tender used in the orignal transaction
     * @param type the card type
     * @return true if the card type was a tender used in the original transaction
     */
    protected boolean hasCardType(String type)
    {
        boolean hasCardType = false;
        if (transactionRDO instanceof SaleReturnTransactionIfc)
        {
            ReturnTenderDataElementIfc[] tenders =
                ((SaleReturnTransactionIfc) transactionRDO).getReturnTenderElements();
            if (tenders != null)
            {
                for (int i = 0; i < tenders.length; i++)
                {
                    // if tender type is equal to the parameter type irrelevant of whether it is a positive/negative tender
                    if (type.equals(tenders[i].getCardType()))
                    {
                        hasCardType = true;
                        break;
                    }
                }
            }
        }

        return hasCardType;
    }

    /**
     * This method checks if the amount due to the customer is less than or equal to the maximum return cash with or
     * without receipt.
     *
     * @return boolean true if less than or equal to max return cash, else return false
     */
    protected boolean lessThanOrEqualToMaxReturnCash()
    {
        boolean lessThan = false;
        UtilityIfc util = getUtility();
        String maxCashAllowed = null;
        if (hasReceipt())
        {
            maxCashAllowed = util.getParameterValue("MaximumCashRefund", "100.00");
        }
        else
        {
            maxCashAllowed = util.getParameterValue("MaximumCashRefundWithoutReceipt", "100.00");
        }
        CurrencyIfc balanceDue =
            ((TenderableTransactionIfc) transactionRDO).getTenderTransactionTotals().getBalanceDue().abs();
        CurrencyIfc maxCash = DomainGateway.getBaseCurrencyInstance(maxCashAllowed);
        if (balanceDue.compareTo(maxCash) == CurrencyIfc.LESS_THAN
            || balanceDue.compareTo(maxCash) == CurrencyIfc.EQUALS)
        {
            lessThan = true;
        }

        return lessThan;
    }

    /**
     * Given the internal state of this transaction return an array of tenders which are valid for accepting as a
     * refund tenders.
     *
     * @return @see oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc#getEnabledRefundOptions()
     * @deprecated in 14.0 @use oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc#getEnabledRefundTenderTypes()
     */
    public TenderTypeEnum[] getEnabledRefundOptions()
    {
        calculateRefundOptionsRow();

        // temporary list. initialize to a size that can hold
        // all tender types
        ArrayList<TenderTypeEnum> tenderList = new ArrayList<TenderTypeEnum>(6);
        UtilityIfc util = getUtility();

        // if store credit refund only paramter set
        if (util.getParameterValue(storeCreditRefundOnly, "N").equalsIgnoreCase("Y"))
        {
            tenderList.add(TenderTypeEnum.STORE_CREDIT);
        }
        // if return items from two or more transactions
        else if (!hasOnlyOneOriginalTransaction())
        {
            tenderList.add(TenderTypeEnum.CASH);
            tenderList.add(TenderTypeEnum.MAIL_CHECK);
            tenderList.add(TenderTypeEnum.CREDIT);
            tenderList.add(TenderTypeEnum.GIFT_CARD);
            tenderList.add(TenderTypeEnum.STORE_CREDIT);
        }
        // if return from one transaction
        // and retrieved
        // and original transaction was paid from with one tender or multiple
        // tenders
        // and it was debit
        else if (hasBeenRetrieved()
                && (hasOnlyOneOriginalTender() && isReturnTender(TenderLineItemIfc.TENDER_TYPE_DEBIT))
                || (!hasOnlyOneOriginalTender() && isAllReturnTenderDebit()))
        {
            tenderList.add(TenderTypeEnum.CASH);
            tenderList.add(TenderTypeEnum.DEBIT);
            tenderList.add(TenderTypeEnum.CREDIT);
            tenderList.add(TenderTypeEnum.GIFT_CARD);
            tenderList.add(TenderTypeEnum.STORE_CREDIT);
            tenderList.add(TenderTypeEnum.MAIL_CHECK);
            refundOptionsRow = 14;
        }
        // if one transaction and has been retrieved and multiple tenders and if one of them is debit include debit as refund
        else if (hasBeenRetrieved() && !hasOnlyOneOriginalTender() && lessThanOrEqualToMaxReturnCash())
        {
            tenderList.add(TenderTypeEnum.CASH);
            tenderList.add(TenderTypeEnum.MAIL_CHECK);
            tenderList.add(TenderTypeEnum.CREDIT);
            tenderList.add(TenderTypeEnum.GIFT_CARD);
            tenderList.add(TenderTypeEnum.STORE_CREDIT);
            if (isReturnTender(TenderLineItemIfc.TENDER_TYPE_DEBIT))
            {
                tenderList.add(TenderTypeEnum.DEBIT);
            }
            refundOptionsRow = 2;
        }
        // if return items from one transaction
        // return has multiple tenders
        // has been retrieved with receipt
        // and amount is greater than max cash for refund with no receipt
        // and if one of them is debit include debit as refund
        else if (hasBeenRetrieved() && !hasOnlyOneOriginalTender() && hasReceipt() && !lessThanOrEqualToMaxReturnCash())
        {
            tenderList.add(TenderTypeEnum.CREDIT);
            tenderList.add(TenderTypeEnum.GIFT_CARD);
            tenderList.add(TenderTypeEnum.STORE_CREDIT);
            tenderList.add(TenderTypeEnum.MAIL_CHECK);
            if (isReturnTender(TenderLineItemIfc.TENDER_TYPE_DEBIT))
            {
                tenderList.add(TenderTypeEnum.DEBIT);
            }
            refundOptionsRow = 3;
        }
        // if return items from one transaction
        // and has been retrieved
        // and the original transaction was paid from with one tender, housecard
        else if (
            hasBeenRetrieved() && hasOnlyOneOriginalTender() && hasCardType(CardTypeCodesIfc.HOUSE_CARD))
        {
            tenderList.add(TenderTypeEnum.CREDIT);
            tenderList.add(TenderTypeEnum.GIFT_CARD);
            tenderList.add(TenderTypeEnum.STORE_CREDIT);
            tenderList.add(TenderTypeEnum.MAIL_CHECK);
            tenderList.add(TenderTypeEnum.HOUSE_ACCOUNT);
        }
        // if return items from one transaction
        // and has been retrieved
        // and the original transaction was paid from with one tender, credit
        else if (
            hasBeenRetrieved() && hasOnlyOneOriginalTender() && isReturnTender(TenderLineItemIfc.TENDER_TYPE_CHARGE))
        {
            tenderList.add(TenderTypeEnum.CREDIT);
            tenderList.add(TenderTypeEnum.GIFT_CARD);
            tenderList.add(TenderTypeEnum.STORE_CREDIT);
            tenderList.add(TenderTypeEnum.MAIL_CHECK);
        }
        // if return from one transaction
        // if has been retireved
        // and has only one tender, not credit
        // and less than or equal to max return cash
        // and no gift receipt
        else if (hasBeenRetrieved() &&
                 hasOnlyOneOriginalTender() &&
                 !isReturnTender(TenderLineItemIfc.TENDER_TYPE_CHARGE) &&
                 lessThanOrEqualToMaxReturnCash() &&
                 !hasGiftReceipt())
        {
            tenderList.add(TenderTypeEnum.CASH);
        }
        // if return from one transaction
        // if doesnt have receipt (any type) and has not been retrieved
        // and less than or equal to max return cash
        else if (!hasReceipt() &&
                 !hasGiftReceipt() &&
                 !hasBeenRetrieved() &&
                 lessThanOrEqualToMaxReturnCash())
        {
            tenderList.add(TenderTypeEnum.CASH);
        }
        // if return from one transaction
        // and is from a gift receipt
        // and is less than or equal to max return cash
        else if (hasGiftReceipt() && lessThanOrEqualToMaxReturnCash())
        {
            tenderList.add(TenderTypeEnum.CASH);
        }
        // if return from one transaction
        // and gift receipt
        else if (hasGiftReceipt())
        {
            tenderList.add(TenderTypeEnum.GIFT_CARD);
            tenderList.add(TenderTypeEnum.STORE_CREDIT);
        }
        // if return from one transaction
        // and has no receipt and not been retrieved
        // and amount is greater than max cash for refund with no receipt
        else if (!hasReceipt() &&
                 !hasGiftReceipt() &&
                 !hasBeenRetrieved() &&
                 !lessThanOrEqualToMaxReturnCash())
        {
            tenderList.add(TenderTypeEnum.MAIL_CHECK);
            tenderList.add(TenderTypeEnum.STORE_CREDIT);
        }
        // if return from one transaction
        // and has receipt but not retrieved
        else if ((hasReceipt() || hasGiftReceipt()) &&
                 !hasBeenRetrieved())
        {
            tenderList.add(TenderTypeEnum.CASH);
            tenderList.add(TenderTypeEnum.MAIL_CHECK);
            tenderList.add(TenderTypeEnum.CREDIT);
            tenderList.add(TenderTypeEnum.GIFT_CARD);
            tenderList.add(TenderTypeEnum.STORE_CREDIT);
        }
        // if return from one transaction
        // and retrieved
        // and orig was paid for with one tender,
        // cash, check, travelers check, money order,
        // purchase order, or mall certificate
        else if (
            hasBeenRetrieved()
                && hasOnlyOneOriginalTender()
                && (isReturnTender(TenderLineItemIfc.TENDER_TYPE_CASH)
                    || isReturnTender(TenderLineItemIfc.TENDER_TYPE_CHECK)
                    || isReturnTender(TenderLineItemIfc.TENDER_TYPE_TRAVELERS_CHECK)
                    || isReturnTender(TenderLineItemIfc.TENDER_TYPE_MONEY_ORDER)
                    || isReturnTender(TenderLineItemIfc.TENDER_TYPE_PURCHASE_ORDER)
                    || isReturnTender(TenderLineItemIfc.TENDER_TYPE_MALL_GIFT_CERTIFICATE)))
        {
            tenderList.add(TenderTypeEnum.CASH);
            tenderList.add(TenderTypeEnum.MAIL_CHECK);
            tenderList.add(TenderTypeEnum.GIFT_CARD);
            tenderList.add(TenderTypeEnum.STORE_CREDIT);
        }
        // if return from one transaction
        // and retrieved
        // and original transaction was paid with one tender
        // and that tender was gift card or gift certificate
        else if (
            hasBeenRetrieved()
                && hasOnlyOneOriginalTender()
                && (isReturnTender(TenderLineItemIfc.TENDER_TYPE_GIFT_CARD) ||
                    isReturnTender(TenderLineItemIfc.TENDER_TYPE_GIFT_CERTIFICATE)))
        {
            tenderList.add(TenderTypeEnum.GIFT_CARD);
            tenderList.add(TenderTypeEnum.STORE_CREDIT);
        }
        // if return from one transaction
        // and retrieved
        // and original transaction was paid with one tender
        // and it was store credit
        else if (
            hasBeenRetrieved()
                && hasOnlyOneOriginalTender()
                && isReturnTender(TenderLineItemIfc.TENDER_TYPE_STORE_CREDIT))
        {
            tenderList.add(TenderTypeEnum.STORE_CREDIT);
        }
        // If all else fails, store credit and mail check are the safest refund options
        else
        {
            StringBuilder logOptions = new StringBuilder();
            logOptions.append("\nStoreCreditRefundOnly = ").append(Boolean.toString(util.getParameterValue(storeCreditRefundOnly, "N").equalsIgnoreCase("Y")))
                      .append("\nHasOnlyOneOriginalTransaction = ").append(Boolean.toString(hasOnlyOneOriginalTransaction()))
                      .append("\nHasBeenRetrieved = ").append(Boolean.toString(hasBeenRetrieved()))
                      .append("\nHasOnlyOneOriginalTender = ").append(Boolean.toString(hasOnlyOneOriginalTender()))
                      .append("\nLessThanOrEqualToMaxReturnCash = ").append(Boolean.toString(lessThanOrEqualToMaxReturnCash()))
                      .append("\nHasGiftReceipt = ").append(Boolean.toString(hasGiftReceipt()))
                      .append("\nHasReceipt = ").append(Boolean.toString(hasReceipt()))
                      .append("\nReturnTenders = ");
            if (transactionRDO instanceof SaleReturnTransactionIfc)
            {
                ReturnTenderDataElementIfc[] tenders =
                    ((SaleReturnTransactionIfc) transactionRDO).getReturnTenderElements();
                if (tenders != null)
                {
                    for (int i = 0; i < tenders.length; i++)
                    {
                        logOptions.append(TenderLineItemIfc.TENDER_LINEDISPLAY_DESC[tenders[i].getTenderType()]);
                    }
                }
            }

            logger.error("We failed in getRefundOptions by falling through all scenarios given the following parameters: " + logOptions.toString());
            assert(false) : "We failed in getRefundOptions by falling through all scenarios";
            tenderList.add(TenderTypeEnum.STORE_CREDIT);
            tenderList.add(TenderTypeEnum.MAIL_CHECK);
        }

        WorkstationIfc ws = transactionRDO.getWorkstation();
        boolean transactionReentry = ws.isTransReentryMode();
        if (transactionReentry)
        { // if we're in trans reentry mode, clear the list and add all tender types
            tenderList.clear();

            tenderList.add(TenderTypeEnum.CASH);
            tenderList.add(TenderTypeEnum.CREDIT);
            tenderList.add(TenderTypeEnum.GIFT_CARD);
            tenderList.add(TenderTypeEnum.STORE_CREDIT);
            tenderList.add(TenderTypeEnum.MAIL_CHECK);
        }

        // convert list to array
        TenderTypeEnum[] tenderTypeArray = new TenderTypeEnum[tenderList.size()];
        tenderTypeArray = tenderList.toArray(tenderTypeArray);
        return tenderTypeArray;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc#isHouseCardsAccepted()
     */
    public boolean isHouseCardsAccepted()
    {
        boolean houseCardsAccepted = false;
        try
        {
            houseCardsAccepted = getParameterManager().getBooleanValue("HouseCardsAccepted");
        }
        catch (ParameterException e)
        {
            logger.error("Could not retrieve HouseCardsAccepted from the ParameterManager.", e);
        }

        return houseCardsAccepted;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc#getEnabledRefundTenderTypes()
     */
    public List<Integer> getEnabledRefundTenderTypes()
    {
        calculateRefundOptionsRow();

        TreeSet<Integer> TenderTypesSet = new TreeSet<Integer>();

        // if we're in trans reentry mode, clear the list and add all tender types
        WorkstationIfc ws = transactionRDO.getWorkstation();
        boolean transactionReentry = ws.isTransReentryMode();
        if (transactionReentry)
        {
            TenderTypesSet.add(TenderLineItemConstantsIfc.TENDER_TYPE_CASH);
            TenderTypesSet.add(TenderLineItemConstantsIfc.TENDER_TYPE_CHARGE);
            TenderTypesSet.add(TenderLineItemConstantsIfc.TENDER_TYPE_GIFT_CARD);
            TenderTypesSet.add(TenderLineItemConstantsIfc.TENDER_TYPE_STORE_CREDIT);
            TenderTypesSet.add(TenderLineItemConstantsIfc.TENDER_TYPE_MAIL_BANK_CHECK);
        }
        else if (transactionRDO instanceof SaleReturnTransactionIfc)
        {
            ReturnTenderDataElementIfc[] originalTenders =
                ((SaleReturnTransactionIfc)transactionRDO).getReturnTenderElements();
            AbstractTransactionLineItemIfc[] lineItems =
                ((SaleReturnTransactionIfc)transactionRDO).getItemContainerProxy().getLineItems();

            if (originalTenders != null)
            {
                for(ReturnTenderDataElementIfc originalTender: originalTenders)
                {
                    // Only include positive (i.e. sale) tenders
                    if (originalTender.getTenderAmount().signum() > 0)
                    {
                        TenderTypesSet.addAll(getRefundTenderTypes(originalTender.getTenderType()));
                    }
                }
            }

            for(AbstractTransactionLineItemIfc lineItem: lineItems)
            {
                if (lineItem instanceof SaleReturnLineItemIfc)
                {
                    SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)lineItem;
                    if (srli.getReturnItem() != null &&
                        srli.getReturnItem().getUserSuppliedTenderType() !=
                            TenderLineItemConstantsIfc.TENDER_TYPE_UNKNOWN)
                    {
                        TenderTypesSet.addAll(getRefundTenderTypes(srli.getReturnItem().getUserSuppliedTenderType()));
                    }
                }
            }

            if (TenderTypesSet.isEmpty())
            {
                TenderTypesSet.addAll(getNonRetrievedRefundTenderTypes());
            }
        }

        List<Integer> refundTenderTypes = new ArrayList<Integer>();
        refundTenderTypes.addAll(TenderTypesSet);
        return refundTenderTypes;
    }

    /*
     * Look up the Refund Tenders paramater based on the tender type and
     * return a List of integer tender types.
     */
    private TreeSet<Integer> getRefundTenderTypes(int tenderType)
    {
        TenderTypeMapIfc map = TenderTypeMap.getTenderTypeMap();
        String parameterName = "RefundTenderFor" +
            map.getDescriptor(tenderType)+ "Payment";
        TreeSet<Integer> acceptedTenderTypes = new TreeSet<Integer>();

        try
        {
            String[] tendersDescriptors = getParameterManager().getStringValues(parameterName);
            for(String descriptor: tendersDescriptors)
            {
                acceptedTenderTypes.add(map.getTypeFromDescriptor(descriptor));
            }
        }
        catch (ParameterException e)
        {
            logger.error("Could not retrieve " + parameterName + " from the ParameterManager.", e);
        }

        return acceptedTenderTypes;
    }

    /*
     * Look up the Non Retrieved Refund Tender Types paramater and
     * return a List of integer tender types.
     */
    private TreeSet<Integer> getNonRetrievedRefundTenderTypes()
    {
        TenderTypeMapIfc map = TenderTypeMap.getTenderTypeMap();
        TreeSet<Integer> acceptedTenderTypes = new TreeSet<Integer>();

        try
        {
            String[] tendersDescriptors = getParameterManager().getStringValues("RefundTenderForNonRetrievedTrans");
            for(String descriptor: tendersDescriptors)
            {
                acceptedTenderTypes.add(map.getTypeFromDescriptor(descriptor));
            }
        }
        catch (ParameterException e)
        {
            logger.error("Could not retrieve RefundTenderForNonRetrievedTrans from the ParameterManager.", e);
        }

        return acceptedTenderTypes;
    }

    /*
     * Sets the refund row, i.e. Propmt associated with the refund options screen.
     */
    public void calculateRefundOptionsRow()
    {
        refundOptionsRow = RefundOptionsTDO.ENTER_AMOUNT_AND_CHOOSE;

        // if return items from one transaction
        // and has been retrieved
        // and the original transaction was paid from with one tender, housecard
        if (hasBeenRetrieved() && hasOnlyOneOriginalTender() && hasCardType(CardTypeCodesIfc.HOUSE_CARD))
        {
            refundOptionsRow = RefundOptionsTDO.NEXT_FOR_HOUSE_ACCOUNT_REFUND;
        }
        // if return items from one transaction
        // and has been retrieved
        // and the original transaction was paid from with one tender, credit
        else if (hasBeenRetrieved() && hasOnlyOneOriginalTender() && isReturnTender(TenderLineItemIfc.TENDER_TYPE_CHARGE))
        {
            refundOptionsRow = RefundOptionsTDO.NEXT_FOR_CREDIT_REFUND;
        }
        // if return items from one transaction
        // return has multiple tenders
        // has been retrieved with receipt
        // and amount is greater than max cash for refund with no receipt
        else if (lessThanOrEqualToMaxReturnCash())
        {
            refundOptionsRow = RefundOptionsTDO.NEXT_FOR_CASH_REFUND;
        }
    }

    /**
     * Given the internal state of this transaction return an array of tenders which are valid for change.
     *
     * @return @see oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc#getEnabledChangeOptions()
     */
    public TenderTypeEnum[] getEnabledChangeOptions()
    {
        TenderUtilityIfc tenderUtil = getTenderUtility();
        return tenderUtil.getEnabledChangeOptions(this);
    }

    /**
     * This method returns a boolean true if cash is the only change due option.
     *
     * @return boolean cashOnlyForChangeDue
     */
    public boolean isCashOnlyOptionForChangeDue()
    {
        return cashOnlyForChangeDue;
    }

    /**
     * This method returns a boolean true if the Gift Certificate had already
     * been added to the TenderLineItemIfc array. This indicates that operatator
     * is trying to use the same gift certificate twice in a single transaction.
     *
     * @param tenderAttributes gift certificate attributes
     * @return boolean giftCertificateUsed
     */
    public boolean isGiftCertificateUsed(HashMap tenderAttributes)
    {
        boolean giftCertificateUsed  = false;
        String giftCertificateNumber = (String)tenderAttributes.get(TenderConstants.NUMBER);

    	TenderLineItemIfc[] tenders = ((TenderableTransactionIfc) transactionRDO).getTenderLineItems();
    	TenderLineItemIfc tli;
        TenderGiftCertificateIfc giftcert = null;
        for (int i = 0; i < tenders.length; i++)
        {
            tli = tenders[i];
            if (tli instanceof TenderGiftCertificateIfc)
            {
                giftcert = (TenderGiftCertificateIfc)tli;
                if (giftcert.getGiftCertificateNumber()!= null && giftcert.getGiftCertificateNumber().equals(giftCertificateNumber))
                {
    				giftCertificateUsed = true;
    				break;
    			}
            }
    	}

    	return giftCertificateUsed;
    }

    /**
     * This method sets the cash only option.
     *
     * @param cashOnlyOption
     */
    public void setCashOnlyOptionForChangeDue(boolean cashOnlyOption)
    {
        cashOnlyForChangeDue = cashOnlyOption;
    }

    /**
     * This method calculates the maximum allowable cash change amount
     * based on the following formula:
     * MaxCashChange = the greatest applicable 'Max Cash Change' limit +
     *                 the total of cash or cash equivalent tenders in the transaction
     *
     * @return Currency object
     */
    protected CurrencyIfc calculateMaxAllowableCashChange()
    {
        TenderUtilityIfc tenderUtil = getTenderUtility();
        return tenderUtil.calculateMaxAllowableCashChange(tenderGroupMap);
    }

    /**
     * Accumulates the overtender limits for each individual tender
     * and returns the transaction overtender limit.
     *
     * @return Currency object
     */
    protected CurrencyIfc getOvertenderLimit()
    {
        CurrencyIfc result = DomainGateway.getBaseCurrencyInstance();

        // get the overtender limit for each group
        Iterator<TenderGroupADOIfc> groupIter = tenderGroupMap.values().iterator();
        while (groupIter.hasNext())
        {
            TenderGroupADOIfc group = groupIter.next();
            result = result.add(group.getOvertenderLimit());
        }
        return result;
    }

    /**
     * You can use this method to make any modifications to the DB after the
     * void transaction is saved. To make additional updates to the original transaction
     * before the void transaction is saved.
     *
     * @see #processVoid()
     * @see oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc#updateForVoid()
     */
    public void updateForVoid() throws DataException
    {
        // Most transactions do not need this, so it is defined here
        // as blank. This must be overridden by transactions requiring
        // a database update when voided.
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.journal.JournalableADOIfc#getJournalMemento()
     */
    public Map<String,Object> getJournalMemento()
    {
        HashMap<String,Object> memento = new HashMap<String,Object>(10);
        memento.put(JournalConstants.BALANCE, getBalanceDue());
        memento.put(JournalConstants.TRANSACTION_RDO_TYPE, new Integer(transactionRDO.getTransactionType()));

        // This is an end of release fix up for a defect in the journal amounts of tender and
        // change due for a partially cancled PDO pickup.  The methods getTenderTotal() and
        // getTotalCashChangeAmount() used by ORPOS to determine application flow, so I am
        // reluctant to modify them at this stage of the game.
        if (transactionRDO.getTransactionType() == TransactionConstantsIfc.TYPE_ORDER_PARTIAL ||
            transactionRDO.getTransactionType() == TransactionConstantsIfc.TYPE_ORDER_COMPLETE)
        {
            memento.put(JournalConstants.TOTAL_TENDER, ((TenderableTransactionIfc)transactionRDO).getCollectedTenderTotalAmount());
        }
        else
        {
            memento.put(JournalConstants.TOTAL_TENDER, getTenderTotal());
            memento.put(JournalConstants.ROUNDING_ADJUSTMENT, getCashChangeRoundingAdjustment());
            memento.put(JournalConstants.CHANGE_GIVEN, getChangeGiven());
        }
        memento.put(JournalConstants.TOTAL_CHANGE, ((TenderableTransactionIfc)transactionRDO).calculateChangeDue().abs());

        journalIssuedStoreCredits(memento);

        journalRedeemedStoreCredits(memento);

        journalIRSCustomer(memento);

        return memento;
    }

    /**
     * This method journals redeemed store credits.
     * @param memento
     */
    public void journalRedeemedStoreCredits(HashMap<String,Object> memento)
    {
    }

    /**
     * This method journals issued store credits.
     * @param memento
     */
    public void journalIssuedStoreCredits(HashMap<String,Object> memento)
    {
        // add issued store credits.
        if (getTenderGroup(TenderTypeEnum.STORE_CREDIT).getTenderCount() > 0)
        {
            TenderGroupADOIfc group = tenderGroupMap.get(TenderTypeEnum.STORE_CREDIT);
            TenderADOIfc[] storeCredits = group.getTenders();
            List<TenderADOIfc> issuedStoreCredits = new LinkedList<TenderADOIfc>();
            for (int i = 0; i < storeCredits.length; i++)
            {
                TenderStoreCreditIfc storeCredit = (TenderStoreCreditIfc) ((ADO) storeCredits[i]).toLegacy();
                if (storeCredit.getState() == TenderStoreCreditIfc.ISSUE)
                {
                    issuedStoreCredits.add(storeCredits[i]);
                }
            }
            memento.put(JournalConstants.ISSUED_STORE_CREDITS, issuedStoreCredits);
        }
    }

    /**
     * This method journals issued store credits.
     * @param memento
     */
    public void journalIRSCustomer(HashMap<String,Object> memento)
    {
        if (((TenderableTransactionIfc) transactionRDO).getIRSCustomer() != null)
        {
            memento.put(JournalConstants.IRS_CUSTOMER,
                        ((TenderableTransactionIfc) transactionRDO).getIRSCustomer());
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.transaction.RetailTransactionADOIfc#override(oracle.retail.stores.domain.employee.EmployeeIfc, int)
     */
    public boolean overrideFunction(EmployeeIfc overrideEmployee, int function, Object data)
    {
        boolean result = false;
        switch (function)
        {
            case RoleFunctionIfc.TENDER_LIMIT :
                assert(data instanceof TenderTypeEnum);
                TenderGroupADOIfc group = tenderGroupMap.get(data);
                result = group.override(overrideEmployee, function);
                break;
            default :
                throw new RuntimeException("Default condition should never occur");
        }
        return result;
    }


    /**
     * Check to see if the register is in training mode.
     * @return True if in training mode
     */
    public boolean isInTrainingMode()
    {
        RegisterADO registerADO = getContext().getRegisterADO();
        return registerADO.isInMode(RegisterMode.TRAINING);
    }

    /**
     * Check to see if we should open the drawer.
     *
     * @return True of drawer should open
     * @see oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc#openDrawer()
     */
    public boolean openDrawer()
    {
        boolean result = false;

        UtilityIfc util = getUtility();

        boolean dontOpenForTraining = false;
        // Training Mode - Determine whether the drawer should be popped
        if (isInTrainingMode())
        {
            boolean openDrawerForTraining = util.getParameterValue("OpenDrawerInTrainingMode", "N").equalsIgnoreCase("Y");
            if (!openDrawerForTraining)
            {
                dontOpenForTraining = true;
            }
        }

        // if we are in training mode and the parameter is not set then never open drawer
        if (!dontOpenForTraining)
        {
            TenderADOIfc[] tendersInTrans = getAllTenderLineItems();

            // Added for 3488 : Code checks open drawer parameter list in application.xml
            // and compares it to tenders in transaction. Determines if drawer needs to open.
            String[] openDrawerParm = util.getParameterValueList("OpenDrawerForTender");

            if (openDrawerParm == null)
            {
                result = false;
            }

            for (int i = 0; i < tendersInTrans.length; i++)
            {
                TenderTypeEnum tenderType = tendersInTrans[i].getTenderType();

                if (util.isStringListed(tenderType.toString(), openDrawerParm))
                {
                    result = true;
                    break;
                }

            }
            if (result)
            {
                // For transactions with a positive grand total, always open the drawer.
                // For transactions with a negative grand total, only open the drawer
                // if a cash tender exists in the transaction
                if (getTransactionGrandTotal().signum() == CurrencyIfc.NEGATIVE
                                && getTenderGroup(TenderTypeEnum.CASH).getTenderCount() == 0)
                {
                    result = false;
                }
            }
        }
        return result;
    }

    /**
     * This method calls the RDO linkCustomer.
     *
     * @param customer
     *            CustomerIfc
     */
    public void linkCustomer(CustomerIfc customer)
    {
        // link customer
        if (transactionRDO instanceof TenderableTransactionIfc)
        {
            TenderableTransactionIfc ttTrans = (TenderableTransactionIfc) transactionRDO;
            ttTrans.linkCustomer(customer);
        }
    }

    /**
     * This method calls the RDO getCustomer.
     *
     * @return CustomerIfc
     */
    public CustomerIfc getCustomer()
    {
        // get customer
        TenderableTransactionIfc ttTrans = (TenderableTransactionIfc) transactionRDO;
        return ttTrans.getCustomer();
    }

    /**
     * This method determines whether or not a Business customer is linked to the transaction.
     *
     * @return linked Boolean
     */
    public boolean isBusinessCustomerLinked() throws TenderException
    {
        boolean linked = false;
        if (transactionRDO instanceof TenderableTransactionIfc)
        {
            TenderableTransactionIfc saleReturnTrans = (TenderableTransactionIfc) transactionRDO;

            // get customer
            CustomerIfc customer = saleReturnTrans.getCustomer();

            // is there a business customer
            if (customer != null)
            {
                if (customer.isBusinessCustomer())
                {
                    // there is a business customer
                    linked = true;
                }
            }
        }
        if (!linked)
        {
            throw new TenderException("Business Customer is not linked.", TenderErrorCodeEnum.NO_CUSTOMER_LINKED);
        }
        return linked;
    }

    /**
     * This method saves Declined echecks.
     *
     * @param check
     * @see oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc#saveDeclineECheck(oracle.retail.stores.pos.ado.tender.TenderADOIfc)
     */
    public void saveDeclineECheck(TenderADOIfc check)
    {
        if (transactionRDO instanceof TenderableTransactionIfc)
        {
            ((TenderableTransactionIfc) transactionRDO).addECheckDeclinedItems(
                (TenderLineItemIfc) ((TenderCheckADO) check).toLegacy());
        }
    }

    /**
     * This method will check for any tenders with the dirty flag set to true. If it finds any, it will recalculate the
     * transaction totals and break.
     *
     */
    public void recalculateTransactionTotal()
    {

        try
        {
            TenderADOIfc[] tenders = getTenderLineItems(TenderLineItemCategoryEnum.ALL);
            int length = tenders.length;
            for (int i = 0; i < length; i++)
            {
                if (tenders[i].isDirtyFlag())
                {
                    ((TenderableTransactionIfc) transactionRDO).updateTenderTotals();
                    break;
                }
            }
        }
        catch (Exception e)
        {
            logger.error("A problem was encountered.", e);
            throw new RuntimeException("A problem was encountered.", e);
        }

    }

    /**
     * Set the transaction as exempted from tax.
     *
     */
    public void setTaxExempt(String certificateNumber, int reasonCode)
    {
        if (transactionRDO instanceof SaleReturnTransactionIfc)
        {
            ((SaleReturnTransactionIfc) transactionRDO).setTaxExempt(certificateNumber, reasonCode);
        }
    }

    /**
     * Recalculate the OrderTransaction Object.
     *
     * Empty implementation provided here. OrderTransactionADO overrides this method to
     * provide the implementation.
     *
     * @see oracle.retail.stores.pos.ado.transaction.OrderTransactionADO#updateOrderStatus()
     */
    public void updateOrderStatus()
    {
    }

    /**
     * Returns the deposit amount.
     *
     * This implementation returns a depositAmount of zero value.  OrderTransactionADO
     * overrides this method to provide the implementation.
     *
     * @see oracle.retail.stores.pos.ado.transaction.OrderTransactionADO#getDepositAmount()
     *
     * @return Currency deposit amount.
     */
    public CurrencyIfc getDepositAmount()
    {
        CurrencyIfc depositAmount = DomainGateway.getBaseCurrencyInstance();
        return depositAmount;
    }

    /**
     * Clear tax exempt.
     */
    public void clearTaxExempt()
    {
        if (transactionRDO instanceof SaleReturnTransactionIfc)
        {
            ((SaleReturnTransactionIfc) transactionRDO).clearTaxExempt();
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc#getTenderStoreCreditIfcLineItems()
     */
    public TenderStoreCreditIfc getTenderStoreCreditIfcLineItem()
    {
        // Loop through all tenders and find the store credit tender to be issued
        TenderLineItemIfc[] tenders = ((TenderableTransactionIfc) transactionRDO).getTenderLineItems();
        TenderLineItemIfc tli;
        TenderStoreCreditIfc tscRedeem = null;
        for (int i = 0; i < tenders.length; i++)
        {
            tli = tenders[i];
            if (tli instanceof TenderStoreCreditIfc)
            {
                tscRedeem = (TenderStoreCreditIfc) tli;
            }
        }
        return tscRedeem;
    }

    /**
     * Set the transaction as non-taxable.
     */
    public void setNonTaxable()
    {
        if (transactionRDO instanceof SaleReturnTransactionIfc)
        {
            ((SaleReturnTransactionIfc) transactionRDO).setNonTaxable();
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc#issueStoreCreditAmount(java.lang.String)
     */
    public CurrencyIfc issueStoreCreditAmount(String amount)
    {
        // if store credit selected from refund screen, use amount entered by user
    	TenderStateEnum state = evaluateTenderState();
        if (state == TenderStateEnum.REFUND_OPTIONS ||
        	state == TenderStateEnum.REDEEM_REFUND_OPTIONS ||
            state == TenderStateEnum.CHANGE_DUE)
        {
            return DomainGateway.getBaseCurrencyInstance(amount);
        }

        // otherwise use balance due amount
        return getBalanceDue().abs();
    }

    //----------------------------------------------------------------------
    /**
        This method returns true if already added StoreCredit
        in the TenderLineItemIfc is being used again.
        @return boolean storeCreditUsed
    **/
    //----------------------------------------------------------------------
    public boolean isStoreCreditUsed(String storeCreditNumber, String storeCreditAmount)
    {
        boolean storeCreditUsed = false;
        TenderLineItemIfc[] tenders = ((TenderableTransactionIfc)
                                       transactionRDO).getTenderLineItems();
        TenderLineItemIfc tli;
        TenderStoreCreditIfc storeCredit = null;
        for (int i = 0; i < tenders.length; i++)
        {
            tli = tenders[i];
            if (tli instanceof TenderStoreCreditIfc)
            {
               storeCredit = (TenderStoreCreditIfc)tli;
               if (storeCredit.getStoreCreditID().equals(storeCreditNumber))
               {
                    if(storeCredit.getAmountTender().equals(DomainGateway.getBaseCurrencyInstance(storeCreditAmount)))
                    {
                        storeCreditUsed = true;
	                    break;
                    }
               }
            }
        }
       return storeCreditUsed;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc#unusedStoreCreditReissued()
     */
    public TenderStoreCreditIfc unusedStoreCreditReissued(StoreCreditIfc storeCredit, CurrencyIfc tenderAmount)
    {
        RegisterADO registerADO = getContext().getRegisterADO();
        // set training mode
        storeCredit.setTrainingMode(registerADO.isInMode(RegisterMode.TRAINING));

        TenderStoreCreditIfc tscIssue = DomainGateway.getFactory().getTenderStoreCreditInstance();
        tscIssue.setState(TenderStoreCreditConstantsIfc.ISSUE);
        tscIssue.setCollected(false);

        // unused store credit is re-issued
        if (evaluateTenderState() == TenderStateEnum.CHANGE_DUE &&
            transactionRDO instanceof TenderableTransactionIfc)
        {
            TenderStoreCreditIfc tscRedeem = getTenderStoreCreditIfcLineItem();
            tscIssue.setStoreCredit(storeCredit);
            tscIssue.setAmount(getBalanceDue().abs());
            tscIssue.setAmountTender(tscIssue.getAmount().negate());
            if (tscRedeem != null)
            {
                if (tscRedeem.getExpirationDate() != null)
                {
                    tscIssue.setExpirationDate(tscRedeem.getExpirationDate());
                }
            }


            return tscIssue;
        }

        // new store credit is issued
        tscIssue.setStoreCredit(storeCredit);
        tscIssue.setAmount(tenderAmount);
        tscIssue.setAmountTender(tscIssue.getAmount().negate());
        tscIssue.getStoreCredit().setStatus(StoreCreditIfc.ISSUED);
        return tscIssue;
    }

    /**
     * Attempt to add a redeem tender.
     *
     * @param tender
     *            The redeem tender to be added
     * @throws TenderException
     *             Thrown when a validation error occurs.
     */
    public void addRedeemTender(TenderADOIfc tender) throws TenderException
    {

    }

    /**
     * This method updates the customer information for the
     * redeemed store credit.  The customer information isnt
     * captured until the tender service.  The redeemed store
     * credit has already been created and added by this time
     * so we must go back and update the customer information.
     *
     * @param cust
     * @param entryMethod
     * @param idType
     * @deprecated As of 14.1, please use {@link RetailTransactionADOIfc#updateCustomerForRedeemedStoreCredit(CustomerIfc, LocalizedCodeIfc)} instead.
     */
    public void updateCustomerForRedeemedStoreCredit(CustomerIfc cust, String entryMethod, LocalizedCodeIfc idType)
    {
        updateCustomerForRedeemedStoreCredit(cust, idType);
    }

    /**
     * This method updates the customer information for the
     * redeemed store credit.  The customer information isnt
     * captured until the tender service.  The redeemed store
     * credit has already been created and added by this time
     * so we must go back and update the customer information.
     *
     * @param cust
     * @param idType
     */
    @Override
    public void updateCustomerForRedeemedStoreCredit(CustomerIfc cust, LocalizedCodeIfc idType)
    {
        if (transactionRDO instanceof RedeemTransactionIfc)
        {
            TenderLineItemIfc redeemTender = ((RedeemTransactionIfc)transactionRDO).getRedeemTender();
            if (redeemTender.getTypeCode() == TenderLineItemIfc.TENDER_TYPE_STORE_CREDIT)
            {
                TenderStoreCreditIfc tsc = (TenderStoreCreditIfc) redeemTender;
                StoreCreditIfc sc = tsc.getStoreCredit();

                // add id type to store credit tender
                if (idType != null)
                {
                    tsc.setPersonalIDType(idType);
                    if (sc != null)
                    {
                        sc.setPersonalIDType(idType);
                    }
                }

                // add first name, last name to store credit tender
                if (cust != null)
                {
                    tsc.setFirstName(cust.getFirstName());
                    tsc.setLastName(cust.getLastName());
                    tsc.setBusinessName(cust.getCompanyName());
                    if (sc != null)
                    {
                        sc.setFirstName(cust.getFirstName());
                        sc.setLastName(cust.getLastName());
                        sc.setBusinessName(cust.getCompanyName());
                    }
                }
            }
        }
    }

    /**
     * This method calls the RDO setCaptureCustomer.
     *
     * @param customer the capture customer info
     */
    public void setCaptureCustomer(CaptureCustomerIfc customer)
    {
        transactionRDO.setCaptureCustomer(customer);
    }

    /**
     * This method calls the RDO getCaptureCustomer.
     *
     * @return CaptureCustomerIfc
     */
    public CaptureCustomerIfc getCaptureCustomer()
    {
        return transactionRDO.getCaptureCustomer();
    }

    /**
     * This method returns the refund options row.
     *
     * @return int which corresponds to the requirement row of which options to
     *         present for refund options.
     */
    public int getRefundOptionsRow()
    {
        return refundOptionsRow;
    }

    /**
     * This method returns true if PAT Customer has not been collected and PAT
     * Cash Tender criteria are met
     *
     * @return true if PAT Customer has not been collected and PAT Cash Tender
     *         criteria are met
     */
    public boolean capturePATCustomer()
    {
        boolean isPATCashTransaction = isPATCashTransaction();
        RegisterADO registerADO = getContext().getRegisterADO();
        if (!isPATCashTransaction)
        {
            ((TenderableTransactionIfc) transactionRDO).setIRSCustomer(null);
        }
        else if (!registerADO.isInMode(RegisterMode.TRAINING))
        {
            ((TenderableTransactionIfc) transactionRDO).setIRSCustomer(adoIRSCustomer);
        }
        return ((adoIRSCustomer == null) && isPATCashTransaction);
    }

    /**
     * This method preserves the IRS Customer to include in the RDO
     * if required.
     *
     * @param irsCustomer the capture customer info
     */
    public void setIRSCustomer(IRSCustomerIfc irsCustomer)
    {
        if (irsCustomer == null || isPATCashTransaction())
        {
            RegisterADO registerADO = getContext().getRegisterADO();
            if (!registerADO.isInMode(RegisterMode.TRAINING))
            {
                ((TenderableTransactionIfc) transactionRDO).setIRSCustomer(irsCustomer);
            }
            adoIRSCustomer = irsCustomer;
        }
    }

    /**
     * This method retrieves the IRS Customer.
     *
     * @return IRSCustomerIfc
     */
    public IRSCustomerIfc getIRSCustomer()
    {
        return ((TenderableTransactionIfc) transactionRDO).getIRSCustomer();
    }

    /**
     * This method returns true if PAT Cash Tender criteria are met. This method
     * should be overridden in transactions that may be PAT Cash related
     * <p>
     * Override this method in transaction ADOs that are required to collect PAT
     * Cash customer information.
     *
     * @return true if PAT Cash Tender criteria are met
     */
    protected boolean isPATCashTransaction()
    {
        return false;
    }

    /**
     * Returns a hashmap of tenders that qualify as PAT Cash. The key
     * is generated by #getHistoryKey(TenderADOIfc). This will sum any
     * tenders with identical keys.
     *
     * @return Map of tenders keyed by type and country
     */
    protected HashMap<String,PaymentHistoryInfoIfc> getPATCashTenders()
    {
        if (perf.isInfoEnabled())
        {
            perf.info("Entering getPATCashTenders");
        }
        HashMap<String, PaymentHistoryInfoIfc> newTenders = new HashMap<String, PaymentHistoryInfoIfc>(0);
        PaymentHistoryInfoIfc tenderBucket = null;
        TenderADOIfc[] tenders = getPositiveTenderLineItems();
        for (int x = 0; x < tenders.length; x++)
        {
            if (tenders[x].isPATCash())
            {
                String key = getHistoryKey(tenders[x]);
                if (newTenders.containsKey(key))
                {
                    tenderBucket = newTenders.get(key);
                    tenderBucket.setTenderAmount(tenderBucket.getTenderAmount().add(
                        tenders[x].getAmount()));
                }
                else
                {
                    tenderBucket = DomainGateway.getFactory().getPaymentHistoryInfoInstance();
                    tenderBucket.setTenderAmount(tenders[x].getAmount());
                    tenderBucket.setTenderType(tenders[x].getTenderType().toString());
                    String country = tenders[x].getAmount().getCountryCode();
                    if (tenders[x].getTenderAttributes().get(TenderConstants.ALTERNATE_AMOUNT) != null)
                    {
                        country = ((TenderAlternateCurrencyIfc)tenders[x].toLegacy()).getAlternateCurrencyTendered().getCountryCode();
                    }
                    tenderBucket.setCountryCode(country);
                    newTenders.put(key, tenderBucket);
                }
            }
        }
        if (perf.isInfoEnabled())
        {
            perf.info("Exiting getPATCashTenders");
        }
        return newTenders;
    }

    /**
     * Adds total amount tendered from payment history - assumes patCashTotal is
     * zero.
     *
     * @param originalPaymentHistory The history to be added from
     * @param patCashTotal Amount to add to
     * @return totaled pat cash payments
     */
    protected CurrencyIfc totalPaymentHistory(HashMap<String,PaymentHistoryInfoIfc> originalPaymentHistory,
                                              CurrencyIfc patCashTotal)
    {
        if (perf.isDebugEnabled())
        {
            perf.debug("Entering totalPaymentHistory");
        }
        if (originalPaymentHistory != null)
        {
            Iterator<String> i = originalPaymentHistory.keySet().iterator();
            while (i.hasNext())
            {
                PaymentHistoryInfoIfc tenderBucket = originalPaymentHistory.get(i.next());
                patCashTotal = patCashTotal.add(tenderBucket.getTenderAmount());
            }
        }
        if (perf.isDebugEnabled())
        {
            perf.debug("Exiting totalPaymentHistory");
        }
        return patCashTotal;
    }

    /**
     * Generates a tender key from a payment history info objecct.
     *
     * @param tenderBucket payment history info object
     * @return a generated key
     */
    protected String getHistoryKey(PaymentHistoryInfoIfc tenderBucket)
    {
        String key = tenderBucket.getTenderType();
        if (!Util.isEmpty(tenderBucket.getCountryCode()))
        {
            key = key + "_" + tenderBucket.getCountryCode();
        }
        return key;
    }

    /**
     * Generates a tender key from a tender ADO object.
     *
     * @param tenderADO Tender ADO
     * @return a generated key
     */
    protected String getHistoryKey(TenderADOIfc tenderADO)
    {
        String key = tenderADO.getTenderType().toString();

        if (tenderADO.getTenderAttributes().get(TenderConstants.ALTERNATE_AMOUNT) != null)
        {
            key = key + "_" + ((TenderAlternateCurrencyIfc)tenderADO.toLegacy()).getAlternateCurrencyTendered().getCountryCode();
        }
        else if (!Util.isEmpty(tenderADO.getAmount().getCountryCode()))
        {
            key = key + "_" + tenderADO.getAmount().getCountryCode();
        }

        return key;
    }

    /**
     * Generates a hash of the payment history. Keys are generatd by
     * {@link #getHistoryKey(PaymentHistoryInfoIfc)}
     *
     * @param paymentHistoryInfoCollection Payment History to Hash
     * @return Hash of the payment history
     */
    protected LinkedHashMap<String,PaymentHistoryInfoIfc> getPaymentHistoryHash(List<PaymentHistoryInfoIfc> paymentHistoryInfoCollection)
    {
        LinkedHashMap<String,PaymentHistoryInfoIfc> originalPaymentHistory = new LinkedHashMap<String, PaymentHistoryInfoIfc>(paymentHistoryInfoCollection.size());
        Iterator<PaymentHistoryInfoIfc> iter = paymentHistoryInfoCollection.iterator();
        while (iter.hasNext())
        {
            // Create ADO tender from RDO tender
            PaymentHistoryInfoIfc tenderBucket = iter.next();

            // We want to preserve the original payment history of the
            // transaction in a way that we don't inadvertently override our
            // copy by interacting with what exists in the transaction.
            originalPaymentHistory.put(getHistoryKey(tenderBucket), (PaymentHistoryInfoIfc)tenderBucket.clone());
        }
        return originalPaymentHistory;
    }

    /**
     * This method Restores the original payment history to the layaway RDO
     *
     * @param originalPaymentHistory Hash to restore from
     * @param internalPaymentHistoryInfoCollection Payment History contained in
     *            the RDO
     */
    protected void restorePaymentHistory(HashMap<String,PaymentHistoryInfoIfc> originalPaymentHistory, List<PaymentHistoryInfoIfc> internalPaymentHistoryInfoCollection)
    {
        if (originalPaymentHistory == null)
        {
            originalPaymentHistory = new HashMap<String, PaymentHistoryInfoIfc>(2);
        }
        internalPaymentHistoryInfoCollection.clear();

        PaymentHistoryInfoIfc nextHistory = null;
        for (Iterator<PaymentHistoryInfoIfc> i = originalPaymentHistory.values().iterator(); i.hasNext();)
        {
            // We want to restore the original payment history to the transaction without
            // inadvertently overriding our copy of the original payment history
            nextHistory = (PaymentHistoryInfoIfc)i.next().clone();
            internalPaymentHistoryInfoCollection.add(nextHistory);
        }
    }

    /**
     * This method adds tenders from this transaction the original payment
     * history of the layaway RDO
     *
     * @param originalPaymentHistory Hash of original payment history to include
     *            in RDO
     * @param internalPaymentHistoryInfoCollection Payment History contained in
     *            the RDO
     * @param newTenders Tenders to be added
     */
    protected void updatePaymentHistory(HashMap<String,PaymentHistoryInfoIfc> originalPaymentHistory,
            List<PaymentHistoryInfoIfc> internalPaymentHistoryInfoCollection,
            HashMap<String,PaymentHistoryInfoIfc> newTenders)
    {
        if (perf.isDebugEnabled())
        {
            perf.debug("Entering updatePaymentHistory");
        }
        internalPaymentHistoryInfoCollection.clear();

        PaymentHistoryInfoIfc nextHistory = null;
        for (Iterator<String> i = originalPaymentHistory.keySet().iterator(); i.hasNext();)
        {
            String key = i.next();

            // We want to update the original payment history to the transaction without
            // inadvertently overriding our copy of the original payment history
            nextHistory = (PaymentHistoryInfoIfc)originalPaymentHistory.get(key).clone();
            if (newTenders.containsKey(key))
            {
                nextHistory.setTenderAmount(nextHistory.getTenderAmount()
                    .add(newTenders.get(key).getTenderAmount()));
                newTenders.remove(key);
            }
            internalPaymentHistoryInfoCollection.add(nextHistory);
        }
        for (Iterator<PaymentHistoryInfoIfc> i = newTenders.values().iterator(); i.hasNext();)
        {
            internalPaymentHistoryInfoCollection.add(i.next());
        }
        if (perf.isDebugEnabled())
        {
            perf.debug("Exiting updatePaymentHistory");
        }
    }

    /**
     * This method decrements the RDO's payment history with the current
     * payments for the sake of a void
     *
     * @param originalPaymentHistory Hash of original payment history to include
     *            in RDO
     * @param internalPaymentHistoryInfoCollection Payment History contained in
     *            the RDO
     */
    protected void updatePaymentHistoryForVoid(HashMap<String,PaymentHistoryInfoIfc> originalPaymentHistory, List<PaymentHistoryInfoIfc> internalPaymentHistoryInfoCollection)
    {
        if (originalPaymentHistory != null &&
            originalPaymentHistory.size() > 0)
        {
            if (perf.isDebugEnabled())
            {
                perf.debug("Entering updatePaymentHistoryForVoid");
            }
            HashMap<String,PaymentHistoryInfoIfc> voidTenders = getPATCashTenders();

            internalPaymentHistoryInfoCollection.clear();

            PaymentHistoryInfoIfc nextHistory = null;
            for (Iterator<String> i = originalPaymentHistory.keySet().iterator(); i.hasNext();)
            {
                String key = i.next();

                // We want to restore the original payment history to the transaction without
                // inadvertently overriding our copy of the original payment history
                nextHistory = (PaymentHistoryInfoIfc)originalPaymentHistory.get(key).clone();
                if (voidTenders.containsKey(key))
                {
                    nextHistory.setTenderAmount(nextHistory.getTenderAmount()
                        .subtract((voidTenders.get(key)).getTenderAmount()));
                }
                internalPaymentHistoryInfoCollection.add(nextHistory);
            }
            if (perf.isDebugEnabled())
            {
                perf.debug("Exiting updatePaymentHistoryForVoid");
            }
        }
    }

    /**
    This method is used to determine if the transaction is taxable or not
    @return boolean
    **/

    public boolean isTaxableTransaction()
    {
    	if (transactionRDO instanceof AbstractTenderableTransaction)
    	{
    		return ((AbstractTenderableTransaction)transactionRDO).isTaxableTransaction();
    	}
    	return false;
    }

    /**
     * @return if it is in transaction reentry mode
     */
    public boolean isTransReentryMode()
    {
        return transactionRDO.getWorkstation().isTransReentryMode();
    }
    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc#adjustCashAmountReturnedToCustomer()
     */
    public void adjustCashAmountReturnedToCustomer()
    {
        CurrencyIfc cashChangeRoundingAdjustment = null;
        try
        {
            ArrayList<TenderLineItemIfc> changeTenderLineItems = new ArrayList<TenderLineItemIfc>();
            CurrencyIfc amountToAdjust = DomainGateway.getBaseCurrencyInstance();
            Vector<TenderLineItemIfc> lineItems = ((TenderableTransactionIfc)transactionRDO).getTenderLineItemsVector();
            for(TenderLineItemIfc lineItem: lineItems)
            {
                if (lineItem.getTypeCode() == TenderLineItemIfc.TENDER_TYPE_CASH &&
                    lineItem.getAmountTender().signum() == CurrencyIfc.NEGATIVE)
                {
                    changeTenderLineItems.add(lineItem);
                    amountToAdjust = amountToAdjust.add(lineItem.getAmountTender());
                }
            }

            if (changeTenderLineItems.size() > 0)
            {
                String roundingType = getParameterManager().getStringValue(ParameterConstantsIfc.TENDER_RoundingType);
                String roundingCurrency = getParameterManager().getStringValue(ParameterConstantsIfc.TENDER_RoundingCurrency);

                CurrencyRoundingCalculatorIfc calculator = DomainGateway.getFactory().getCurrencyRoundingCalculatorInstance();
                cashChangeRoundingAdjustment = calculator.calculateCashChangeRoundingAdjustment(amountToAdjust, roundingType, roundingCurrency);
                if (transactionRDO instanceof OrderTransactionIfc)
                {
                    ((OrderTransactionIfc)transactionRDO).getTenderTransactionTotals().setCashChangeRoundingAdjustment(cashChangeRoundingAdjustment);
                }
                else if (transactionRDO instanceof LayawayTransactionIfc)
                {
                    ((LayawayTransactionIfc)transactionRDO).getTenderTransactionTotals().setCashChangeRoundingAdjustment(cashChangeRoundingAdjustment);
                }
                else
                {
                    ((TenderableTransactionIfc)transactionRDO).getTransactionTotals().setCashChangeRoundingAdjustment(cashChangeRoundingAdjustment);
                }

                if (cashChangeRoundingAdjustment.signum() != 0)
                {
                    TenderLineItemIfc changeTenderLineItem = null;
                    for(TenderLineItemIfc lineItem: changeTenderLineItems)
                    {
                        ((TenderableTransactionIfc)transactionRDO).removeTenderLineItem(lineItem);
                        changeTenderLineItem = lineItem;
                    }
                    if (changeTenderLineItem != null)
                    {
                        changeTenderLineItem.setAmountTender(amountToAdjust.add(cashChangeRoundingAdjustment));
                        if (changeTenderLineItem.getAmountTender().signum() != 0)
                        {
                            ((TenderableTransactionIfc)transactionRDO).addTender(changeTenderLineItem);
                        }
                    }
                }
            }
        }
        catch (ParameterException pe)
        {
            logger.warn("ParameterManager is not avialable; currency rounding for cash change cannot be calculated.", pe);
        }
    }
}
