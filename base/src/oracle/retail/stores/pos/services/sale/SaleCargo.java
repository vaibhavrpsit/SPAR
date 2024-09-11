/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/SaleCargo.java /main/43 2014/07/17 15:09:42 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    asinto 10/22/14 - Refactor to restore Fulfillment main option flow.
 *    yiqzha 07/17/14 - Move same original transaction check to utility class
 *                      and make regular transaction and order transaction call
 *                      the same method.
 *    vinees 07/03/14 - Removing isCustomerEnabled method, Customer should
 *                      always be enabled in Sales Screen
 *    mchell 07/01/14 - Added a flag to skip return authorization for mpos
 *                      returns
 *    blarse 06/05/14 - XbranchMerge
 *                      blarsen_bug18854403-ajb-call-ref-trans-cancel-bad-invoiceid
 *                      from rgbustores_14.0x_generic_branch
 *    blarse 06/03/14 - Refactor: Moving call referral fields into their new
 *                      class.
 *    jswan  06/16/14 - Modified to support display of extended item
 *                      recommended items on the Sale Item Screen.
 *    asinto 03/21/14 - added suppressGiftCardDeactivation query parameter to
 *                      cancelSaleTransaction API to force cancel transaction
 *                      to succeed when offline to gift card server
 *    asinto 02/20/14 - added flag to suppress gift card activations in the
 *                      sale complete tour when a call referral was performed
 *    asinto 02/10/14 - reworked flow for gift card activation error scenarios
 *    blarse 02/04/14 - AJB requires original auth response for call referrals.
 *                      Adding this to appropriate shuttles/cargos.
 *    rabhaw 07/30/13 - pluitem for different size should be identified by
 *                      size.
 *    yiqzha 05/10/13 - Avoid PickupShipDialog display again if pick or
 *                      shipping is already selected. It happens when a
 *                      serialized item entered.
 *    jswan  04/25/13 - Modified to prevent NullPointerException in
 *                      completeItemNotFound() method.
 *    yiqzha 04/16/13 - Fix the issue when enter quantity with item id in sell
 *                      item screen.
 *    yiqzha 02/26/13 - Check for order item after adding related item(s).
 *    rgour  10/29/12 - Enhancements in Suspended Transactions
 *    blarse 08/28/12 - Merge project Echo (MPOS) into trunk.
 *    asinto 08/02/12 - Call referral refactor
 *    asinto 07/02/12 - carry call referral authorization details from Mobile
 *    icole  04/19/12 - Forward port mukothan_bug-13013218, corrects problem of
 *                      a single item transaction capable of being price
 *                      adjusted and returned resulting in refund greater than
 *                      original amount.
 *    yiqzha 04/16/12 - refactor store send from transaction totals
 *    cgreen 03/16/12 - split transaction-methods out of utilitymanager
 *    cgreen 03/09/12 - add support for journalling queues by current register
 *    asinto 02/27/12 - refactored the flow so that items added from scan sheet
 *                      doesn't allow for a hang or mismatched letter.
 *    asinto 02/13/12 - prompt for serial numbers when entering tender if items
 *                      are missing this data
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    tkshar 10/10/11 - Added skipUOMCheckFlag
 *    cgreen 08/15/11 - fixed missing return statement
 *    cgreen 08/09/11 - formatting and removed deprecated code
 *    kelesi 12/03/10 - Multiple gift card reversals
 *    nkgaut 09/20/10 - refractored code to use a single class for checking cash
 *                      in drawer
 *    sgu    06/09/10 - merge to tip
 *    sgu    06/08/10 - add item # & desc to the screen prompt. fix unknow item
 *                      screen to disable price and quantity for external item
 *    acadar 06/08/10 - changes for signature capture, disable txn send, and
 *                      discounts
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abhayg 03/05/10 - To Fix Bug-9382583
 *    abonda 01/03/10 - update header date
 *    rkar   11/12/08 - Adds/changes for POS-RM integration
 *    rkar   11/07/08 - Additions/changes for POS-RM integration
 *    akandr 10/29/08 - EJ externalization changes
 *    nkgaut 09/18/08 - Added getter/setter for cash drawer warning boolean
 *                      variable

 * ===========================================================================

     $Log:
      11   360Commerce 1.10        5/27/2008 7:37:28 PM   Anil Rathore
           Updated to display ITEM_NOT_FOUND dialog. Changes reviewed by Dan.
      10   360Commerce 1.9         3/25/2008 6:21:36 AM   Vikram Gopinath CR
           #30683, porting changes from v12x. Save the correct pos department
           id for an unknown item.
      9    360Commerce 1.8         3/10/2008 3:51:48 PM   Sandy Gu
           Specify store id for non receipted return item query.
      8    360Commerce 1.7         9/20/2007 12:09:12 PM  Rohit Sachdeva
           28813: Initial Bulk Migration for Java 5 Source/Binary
           Compatibility of All Products
      7    360Commerce 1.6         6/4/2007 6:01:32 PM    Alan N. Sinton  CR
           26486 - Changes per review comments.
      6    360Commerce 1.5         5/14/2007 2:32:57 PM   Alan N. Sinton  CR
           26486 - EJournal enhancements for VAT.
      5    360Commerce 1.4         1/25/2006 4:11:44 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      4    360Commerce 1.3         1/22/2006 11:45:02 AM  Ron W. Haight
           removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:29:48 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:24:58 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:14:00 PM  Robert Pearse
     $:
      4    .v700     1.2.1.0     10/31/2005 11:50:32    Deepanshu       CR
           6092: Set the Sales Associate
      3    360Commerce1.2         3/31/2005 15:29:48     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:24:58     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:14:00     Robert Pearse
     $
     Revision 1.26  2004/08/09 22:43:41  rsachdeva
     @scr 6791 Assign Send Item for Transaction Level Send

     Revision 1.25  2004/07/29 19:10:14  rsachdeva
     @scr 5442 Item Not Found Cancel for Returns

     Revision 1.24  2004/07/28 15:04:02  rsachdeva
     @scr 4865 Transaction Sales Associate

     Revision 1.23  2004/07/27 22:29:28  jdeleau
     @scr 6485 Make sure the undo button on the sell item screen does
     not force the operator to re-enter the users zip of phone.

     Revision 1.22  2004/07/14 15:40:19  jdeleau
     @scr 5025 Persist the item selection on the sale screen across services, such that
     when it returns to the sale screen the same items are selected, if possible.

     Revision 1.21  2004/06/11 12:37:56  mkp1
     @scr 2775 More Tax - Returns

     Revision 1.20  2004/06/07 23:06:43  bwf
     @scr 5421 Removed unused imports.

     Revision 1.19  2004/06/07 20:19:24  mkp1
     @scr 2775 PLU now checks whether item is taxable before retrieving tax rules

     Revision 1.18  2004/06/03 14:47:45  epd
     @scr 5368 Update to use of DataTransactionFactory

     Revision 1.17  2004/06/02 19:06:51  lzhao
     @scr 4670: add ability to delete send items, modify shipping and display shipping method.

     Revision 1.16  2004/04/21 19:22:15  rsachdeva
     @scr 3906 Comment Added

     Revision 1.15  2004/04/20 13:05:35  tmorris
     @scr 4332 -Sorted imports

     Revision 1.14  2004/04/14 15:17:10  pkillick
     @scr 4332 -Replaced direct instantiation(new) with Factory call.

     Revision 1.13  2004/04/08 22:04:15  bjosserand
     @scr 4093 Transaction Reentry

     Revision 1.12  2004/04/07 17:50:55  tfritz
     @scr 3884 - Training Mode rework

     Revision 1.11  2004/03/31 21:39:57  mweis
     @scr 4206 JavaDoc cleanup.

     Revision 1.10  2004/03/22 17:26:43  blj
     @scr 3872 - added redeem security, receipt printing and saving redeem transactions.

     Revision 1.9  2004/03/17 16:00:15  epd
     @scr 3561 Bug fixing and refactoring

     Revision 1.8  2004/03/16 18:30:42  cdb
     @scr 0 Removed tabs from all java source code.

     Revision 1.7  2004/03/15 21:55:15  jdeleau
     @scr 4040 Automatic logoff after timeout

     Revision 1.6  2004/03/15 20:03:44  rsachdeva
     @scr  3906 Sale Item Size

     Revision 1.5  2004/03/11 20:03:23  blj
     @scr 3871 - added/updated shuttles to/from redeem, to/from tender, to/from completesale.
     also updated sites cargo for new redeem transaction.

     Revision 1.4  2004/02/20 15:51:43  baa
     @scr 3561  size enhancements

     Revision 1.3  2004/02/12 16:48:17  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:22:50  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.8   Nov 26 2003 09:12:32   lzhao
 * remove tendering.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.7   13 Nov 2003 23:56:56   baa
 * allow for not found items
 *
 *    Rev 1.6   Nov 13 2003 11:10:18   baa
 * sale refactoring
 * Resolution for 3430: Sale Service Refactoring
 *
 *    Rev 1.5   08 Nov 2003 01:16:38   baa
 * cleanup -sale refactoring
 *
 *    Rev 1.4   Nov 06 2003 00:52:06   cdb
 * Updated to use new Ifc's to ease ADO transition.
 * Resolution for 3430: Sale Service Refactoring
 *
 *    Rev 1.2   Nov 04 2003 19:08:04   cdb
 * Updated to remove irrelevant deprecation warnings.
 * Resolution for 3430: Sale Service Refactoring
 *
 *    Rev 1.1   Nov 04 2003 17:05:36   cdb
 * Made protected constant NO_SELECTION public.
 * Resolution for 3430: Sale Service Refactoring
 *
 *    Rev 1.0   Nov 03 2003 14:49:16   baa
 * Initial revision.

 * ===================================================
 */
package oracle.retail.stores.pos.services.sale;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.externalorder.LegalDocumentIfc;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.manager.rm.RPIFinalResultIfc;
import oracle.retail.stores.domain.manager.rm.RPIRequestIfc;
import oracle.retail.stores.domain.manager.rm.RPIResponseIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.appmanager.ManagerException;
import oracle.retail.stores.pos.appmanager.ManagerFactory;
import oracle.retail.stores.pos.appmanager.send.SendManager;
import oracle.retail.stores.pos.appmanager.send.SendManagerIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargoIfc;
import oracle.retail.stores.pos.services.common.ItemSizeCargoIfc;
import oracle.retail.stores.pos.services.common.PLUCargoIfc;
import oracle.retail.stores.pos.services.common.TimedCargoIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;
import oracle.retail.stores.pos.services.tender.authorization.CallReferralData;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CreditReferralBeanModel;
import oracle.retail.stores.pos.utility.TransactionUtility;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * The cargo needed by the POS service.
 *
 */
public class SaleCargo extends AbstractFinancialCargo implements AbstractFinancialCargoIfc,
        SaleCargoIfc, ItemSizeCargoIfc, TimedCargoIfc, PLUCargoIfc
{
    private static final long serialVersionUID = 3123716742890548638L;
    /**
     * The operator ID prompt text tag for this service
     */
    public static final String operatorIdPromptTag = "SaleAssociateIdPrompt";
    /**
     * The operator ID prompt text for this service
     */
    public static final String operatorIdPromptText = "Enter sales associate ID.";
    /**
     * The sales associate ID
     */
    protected String employeeID;

    /**
     * The flag to retrive Suspended Transaction On Sell Screen
     */
    protected boolean retrieveSuspendedTransactionOnSaleScreen = false;

    /**
     * The sales associate
     */
    protected EmployeeIfc employee;
    /**
     * The customer transaction
     */
    protected RetailTransactionIfc transaction;
    /**
     * The current item index
     */
    protected int[] indices = new int[] { -1 };
    /**
     * The item number last entered
     */
    protected String pluItemID;
    /**
     * The department name (for ITEM_NOT_FOUND)
     */
    protected String departmentName;
    /**
     * The department id (for ITEM_NOT_FOUND)
     */
    protected String departmentID;
    /**
     * The item number last entered
     */
    protected BigDecimal itemQuantity = BigDecimalConstants.ONE_AMOUNT;
    /**
     * The item
     */
    protected PLUItemIfc pluItem;
    /**
     * The line item
     */
    protected SaleReturnLineItemIfc lineItem;
    /**
     * Selected line items
     */
    protected SaleReturnLineItemIfc[] lineItems;
    /**
     * Splitted line items from best deal calculation
     */
    protected SaleReturnLineItemIfc[] splittedLineItems;
    /**
     * The item serial number (if item classification isSerializable)
     */
    protected String itemSerialNumber;
    /**
     * The error code (if any)generated by employee lookup
     */
    protected int dataExceptionErrorCode;
    /**
     * Till status; this is the result of evaluating the logged on operater, the
     * till and accountability. It stored here to make the job of the till
     * signals easier. It may not reflect the status of the till once the user
     * gets logged on.
     */
    // Status: Invalid, Closed, Open, Reconciled, Suspended
    protected int tillStatus = AbstractFinancialEntityIfc.STATUS_INVALID;
    /**
     * Not opening the cash drawer, use this to make sure the user has time to
     * press Enter before leaving.
     */
    protected boolean waitForNext = false;
    /**
     * Stores the letter to be used to exit the service.
     */
    protected LetterIfc exitLetter = null;
    /**
     * This vector contains a list of SaleReturnTransacions on which returns
     * have been completed.
     */
    protected List<SaleReturnTransactionIfc> originalReturnTransactions = null;
    /**
     * indicates whether operator password is required
     */
    protected boolean passwordRequired = false;
    /**
     * Static index value indicating no selected row
     */
    public static final int NO_SELECTION = -1;
    /**
     * Index of the modified item. The value -1 indicates that the itemToDisplay
     * has not been modified; i.e., it is a new item.
     */
    protected int itemModifiedIndex = NO_SELECTION;

    /**
     * This flag indicates whether the item that was entered was scanned or
     * typed.
     */
    protected boolean itemScanned = false;
    /**
     * This flag indicates whether refresh is needed or not.
     */
    protected boolean refreshNeeded = false;
    /**
     * This flag indicates whether this is the first sale or not.
     */
    protected boolean firstSale = true;
    /**
     * This send index for deleting.
     */
    protected int sendIndex = -1;

    /**
     * This is the item size
     */
    protected String sizeCode;

    /**
     * Show whether or not this transaction has timed out
     */
    protected boolean timeout = false;

    /**
     * The geoCode for taxes
     */
    protected String geoCode;
    /**
     * The store ID
     */
    protected String storeID;
    /**
     * sales associate set using modify transaction sales associate
     */
    protected boolean salesAssociateAlreadySet = false;
    /**
     * Can the prompt for customer phone/zip be skipped?
     */
    protected boolean skipCustomerPrompt;

    /**
     * maximum item number length.
     */
    protected int maxPLUItemIDLength = Integer.MAX_VALUE;

    /**
     * boolean variable to indicate if need to iterate over the collection of
     * legal documents
     */
    private boolean beginIterationOverLegalDocuments = true;

    /**
     * Current legal document that requires signature contract
     */
    private LegalDocumentIfc legalDocument;

    /**
     * index of next legal document
     */
    private int nextLegalDocumentRecord = 0;

    /**
     * return request
     */
    protected RPIRequestIfc returnRequest;

    /**
     * return response
     */
    protected RPIResponseIfc returnResponse;

    /**
     * return result
     */
    protected RPIFinalResultIfc returnResult;

    /**
     * boolean to indicate if there are gift cards subject to reversal
     */
    protected boolean isReverseGiftCard = false;

    /**
     * reversal count
     */
    protected int reverseCount = 0;

    /**
     * skipUOMCheckFlag
     */
    protected boolean skipUOMCheckFlag = false;

    /**
     * Serialized Item Index
     */
    protected int serializedItemIndex = 0;

    /**
     * Selected Scan Sheet ItemID
     */
    private String selectedScanSheetItemID;

    /**
     * CreditReferralBeanModel for use by MobilePOS
     */
    protected CreditReferralBeanModel creditReferralBeanModel;

    /** call referral data */
    protected CallReferralData callReferralData = new CallReferralData();

    protected List<SaleReturnLineItemIfc> orderLineItems = new ArrayList<SaleReturnLineItemIfc>();

    /** no signature capture flag */
    protected boolean noSignatureCapture = false;

    /**
     * Indicates that pickup or delivery was executed
     */
    protected boolean pickupOrDeliveryExecuted = false;

    /**
     * Indicates that gift card activations have been canceled.
     */
    protected boolean giftCardActivationsCanceled = false;

    /** flag to indicate that gift card activation should be suppressed */
    protected boolean suppressGiftCardActivation = false;

    /** flag to indicate that gift card deactivation should be suppressed */
    private boolean suppressGiftCardDeactivation = false;

    /** The selected recommended item ID */
    private String selectedRecommendedItemId = null;

    /**
     * Can the return authorization skipped?
     */
    protected boolean skipReturnAuthorization = false;

    /**
     * From fulfillment flag.  Default is false.
     */
    protected boolean fromFulfillment = false;

    /**
     * Exit to fullfillment flag. Default is false.
     */
    protected boolean exitToFulfillment = false;

    /**
     * Returns the maximum item number length.
     *
     * @return The maximum item number length
     */
    public int getMaxPLUItemIDLength()
    {
        return maxPLUItemIDLength;
    }

    /**
     * Sets the maximum item number length.
     *
     * @param maxPLUItemIDLength The maximum item number length
     */
    public void setMaxPLUItemIDLength(int maxPLUItemIDLength)
    {
        this.maxPLUItemIDLength = maxPLUItemIDLength;
    }

    /**
     * Returns the item's Size code
     *
     * @return The item's Size code
     */
    public String getItemSizeCode()
    {
        return sizeCode;
    }

    /**
     * Sets the item's Size code
     *
     * @param code item's Size code
     */
    public void setItemSizeCode(String code)
    {
        sizeCode = code;
    }

    /**
     * Returns the sales associate ID.
     *
     * @return The sales associate ID
     */
    public String getEmployeeID()
    {
        return employeeID;
    }

    /**
     * Sets the sales associate ID.
     *
     * @param employeeID The sales associate ID
     */
    public void setEmployeeID(String employeeID)
    {
        this.employeeID = employeeID;
    }

    /**
     * Returns the sales associate.
     *
     * @return The sales associate
     */
    public EmployeeIfc getEmployee()
    {
        return employee;
    }

    /**
     * Sets the sales associate.
     *
     * @param employee The sales associate
     */
    public void setEmployee(EmployeeIfc employee)
    {
        this.employee = employee;
    }

    /**
     * Sets the transaction.
     *
     * @param transaction The transaction
     */
    public void setTransaction(SaleReturnTransactionIfc transaction)
    {
        this.transaction = transaction;
    }

    /**
     * Returns the transaction.
     *
     * @return The transaction
     */
    public SaleReturnTransactionIfc getTransaction()
    {
        return (SaleReturnTransactionIfc)transaction;
    }

    /**
     * Sets the transaction.
     *
     * @param transaction a RetailTransactionIfc
     */
    public void setRetailTransactionIfc(RetailTransactionIfc transaction)
    {
        this.transaction = transaction;
    }

    /**
     * Returns the transaction.
     *
     * @return RetailTransactionIfc transaction
     */
    public RetailTransactionIfc getRetailTransaction()
    {
        return transaction;
    }

    /**
     * Returns the transaction.
     *
     * @return TenderableTransactionIfc transaction
     */
    public TenderableTransactionIfc getTenderableTransaction()
    {
        return transaction;
    }

    /**
     * Returns the current item index.
     *
     * @return The current item index
     */
    public int getIndex()
    {
        int returnValue = -1;
        if (this.indices != null && this.indices.length > 0)
        {
            returnValue = this.indices[0];
        }
        return returnValue;
    }

    /**
     * Get the list of selected indices
     *
     * @return indices
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#getIndices()
     */
    public int[] getIndices()
    {
        return this.indices;
    }

    /**
     * Sets the current item index.
     *
     * @param index The current item index
     */
    public void setIndex(int index)
    {
        this.indices = new int[] { index };
    }

    /**
     * Set the list of selected indices
     *
     * @param indices
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#setIndices(int[])
     */
    public void setIndices(int[] indices)
    {
        this.indices = indices;
    }

    /**
     * Returns the itemModifiedIndex.
     *
     * @return The itemModifiedIndex
     */
    public int getItemModifiedIndex()
    {
        return itemModifiedIndex;
    }

    /**
     * Sets the itemModifiedIndex.
     *
     * @param index The itemModifiedIndex
     */
    public void setItemModifiedIndex(int index)
    {
        itemModifiedIndex = index;
    }

    /**
     * Sets the current item index.
     *
     * @param index The current item index
     */
    public void setIndex(Integer index)
    {
        setIndex(index.intValue());
    }

    /**
     * Returns the item number last entered.
     *
     * @return The item number last entered
     */
    public String getPLUItemID()
    {
        return pluItemID;
    }

    /**
     * Sets the item number last entered.
     *
     * @param itemNumber The item number last entered
     */
    public void setPLUItemID(String itemNumber)
    {
        pluItemID = itemNumber;
    }

    /**
     * Returns the item serial number last entered.
     *
     * @return The item serial number last entered
     */
    public String getItemSerial()
    {
        return itemSerialNumber;
    }

    /**
     * Sets the item serial number last entered.
     *
     * @param newItemSerialNumber The item serial number last entered
     */
    public void setItemSerial(String newItemSerialNumber)
    {
        itemSerialNumber = newItemSerialNumber;
    }

    /**
     * Returns the department name for items not found.
     *
     * @return The department name
     */
    public String getDepartmentName()
    {
        return (departmentName);
    }

    /**
     * Sets the department name for items not found.
     *
     * @param dept The department name
     */
    public void setDepartmentName(String dept)
    {
        departmentName = dept;
    }

    /**
     * Returns the department id for items not found.
     *
     * @return The department id
     */
    public String getDepartmentID()
    {
        return (departmentID);
    }

    /**
     * Sets the department id for items not found.
     *
     * @param deptID The department id
     */
    public void setDepartmentID(String deptID)
    {
        departmentID = deptID;
    }

    /**
     * Returns the current PLU item.
     *
     * @return The current PLU item
     */
    public PLUItemIfc getPLUItem()
    {
        return pluItem;
    }

    /**
     * Sets the current PLU item.
     *
     * @param item The current PLU item
     */
    public void setPLUItem(PLUItemIfc item)
    {
        pluItem = item;
    }

    /**
     * Returns the retrieveSuspendedTransactionOnSaleScreen value.
     *
     * @return retrieveSuspendedTransactionOnSaleScreen boolean
     */
    public boolean isRetrieveSuspendedTransactionOnSaleScreen()
    {
        return retrieveSuspendedTransactionOnSaleScreen;
    }

    /**
     * Sets the retrieveSuspendedTransactionOnSaleScreen.
     *
     * @param retrieveSuspendedTransactionOnSaleScreen boolean
     */
    public void setRetrieveSuspendedTransactionOnSaleScreen(boolean retrieveSuspendedTransactionOnSaleScreen)
    {
        this.retrieveSuspendedTransactionOnSaleScreen = retrieveSuspendedTransactionOnSaleScreen;
    }


    /**
     * Returns the current line item.
     *
     * @return The current line item
     */
    public SaleReturnLineItemIfc getLineItem()
    {
        return lineItem;
    }

    /**
     * Sets the current line item.
     *
     * @param item The current line item
     */
    public void setLineItem(SaleReturnLineItemIfc item)
    {
        lineItem = item;
    }

    /**
     * From a whole line items list in the cargo, a subset of highlighted ones
     * are put into an array lineItems.
     *
     * @param items Line items to set
     */
    public void setLineItems(SaleReturnLineItemIfc[] items)
    {
        lineItems = items;
    }

    /**
     * Returns the highlighted line items
     *
     * @return The highlighted items
     */
    public SaleReturnLineItemIfc[] getLineItems()
    {
        return lineItems;
    }

    /**
     * From all the splitted line items in the cargo, which come from quantity*itemID entered from sale item screen.
     * The item id is the source or target for best deal.
     *
     * @param splitted items Line items to set
     */
    public void setSplittedLineItems(SaleReturnLineItemIfc[] lineItems)
    {
        splittedLineItems = lineItems;
    }

    /**
     * Returns the splitted line items
     *
     * @return The highlighted items
     */
    public SaleReturnLineItemIfc[] getSplittedLineItems()
    {
        return splittedLineItems;
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
     * Sets the items quantity value
     *
     * @param value The BigDecimal representation of the items quantity
     */
    public void setItemQuantity(BigDecimal value)
    {
        itemQuantity = value;
    }

    /**
     * Returns the error code returned with a DataException.
     *
     * @return the integer value
     */
    public int getDataExceptionErrorCode()
    {
        return dataExceptionErrorCode;
    }

    /**
     * Sets the error code returned with a DataException.
     *
     * @param value the integer value
     */
    public void setDataExceptionErrorCode(int value)
    {
        dataExceptionErrorCode = value;
    }

    /**
     * Returns the till status.
     *
     * @return the integer value
     */
    public int getTillStatus()
    {
        return tillStatus;
    }

    /**
     * Sets the till status.
     *
     * @param value the integer value
     */
    public void setTillStatus(int value)
    {
        tillStatus = value;
    }

    /**
     * Gets the prompt Enter ID prompt text for this service.
     *
     * @return the prompt text.
     */
    public String getOperatorIdPromptText()
    { // begin setOperator()
        UtilityManagerIfc utility = (UtilityManagerIfc)Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        String promptText = utility.retrieveText(POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                BundleConstantsIfc.POS_BUNDLE_NAME, operatorIdPromptTag, operatorIdPromptText,
                LocaleConstantsIfc.USER_INTERFACE);
        return promptText;
    } // end setOperator()

    /**
     * Returns the waitForNext flag
     *
     * @return the waitForNext boolean flag
     */
    public boolean isWaitForNext()
    {
        return waitForNext;
    }

    /**
     * Sets the waitForNext flag
     *
     * @param value boolean value for the flag
     */
    public void setWaitForNext(boolean value)
    {
        waitForNext = value;
    }

    /**
     * Returns the letter to exit the service with.
     *
     * @return exit letter
     */
    public LetterIfc getExitLetter()
    {
        return exitLetter;
    }

    /**
     * Sets the letter to exit the service with.
     *
     * @param letter the exit letter
     */
    public void setExitLetter(LetterIfc letter)
    {
        exitLetter = letter;
    }

    /**
     * Retrieves the till ID
     *
     * @return String till ID
     */
    public String getTillID()
    {
        return getRegister().getCurrentTill().getTillID();
    }

    /**
     * Retrieve the array of transactions on which items have been returned.
     * This cargo does not track this data.
     *
     * @return SaleReturnTransactionIfc[]
     */
    public SaleReturnTransactionIfc[] getOriginalReturnTransactions()
    {
        if (originalReturnTransactions != null)
        {
            return originalReturnTransactions.toArray(new SaleReturnTransactionIfc[originalReturnTransactions.size()]);
        }
        return null;
    }

    /**
     * Sets the array of transactions on which items have been returned. This
     * cargo does not track this data.
     *
     * @param origTxns retrieved return transactions
     */
    public void setOriginalReturnTransactions(SaleReturnTransactionIfc[] origTxns)
    {
        originalReturnTransactions = new ArrayList<SaleReturnTransactionIfc>();
        for (int i = 0; (origTxns != null) && (i < origTxns.length); i++)
        {
            originalReturnTransactions.add(origTxns[i]);
        }
    }

    /**
     * Retrieve the array of transactions on which items have been returned.
     * This cargo does not track this data.
     */
    public void resetOriginalReturnTransactions()
    {
        originalReturnTransactions = null;
    }

    /**
     * Add a transaction to the vector of transactions on which items have been
     * returned. This cargo does not track this data.
     *
     * @param transaction SaleReturnTransactionIfc
     */
    public void addOriginalReturnTransaction(SaleReturnTransactionIfc transaction)
    {
        // check to see if an array already exist; if not make one.
        if (originalReturnTransactions == null)
        {
            originalReturnTransactions = new ArrayList<SaleReturnTransactionIfc>();
        }
        else
        {
            // Check to see if this transaction is already in the array.
            // if so, remove the current reference.
            int size = originalReturnTransactions.size();
            for (int i = 0; i < size; i++)
            {
                SaleReturnTransactionIfc temp = originalReturnTransactions.get(i);
                if (TransactionUtility.isOfSameOriginalReturnTransaction(temp, transaction))
                {
                    originalReturnTransactions.remove(i);
                    // Stop the loop.
                    i = size;
                }
            }
        }
        originalReturnTransactions.add(transaction);
    }

    /**
     * Add a transaction to the vector of transactions on which items have been returned
     * if it's not already in the list.
     * @param transaction SaleReturnTransactionIfc
     */
    public void addOriginalPriceAdjReturnTransaction(SaleReturnTransactionIfc transaction)
    {
        boolean addTransaction = true;
        // check to see if an array already exist; if not make one.
        if (originalReturnTransactions == null)
        {
            originalReturnTransactions = new ArrayList<SaleReturnTransactionIfc>();
        }
        else
        {
            // Check to see if this transaction is already in the array.
            // if so, don't add.
            int size = originalReturnTransactions.size();
            for (int i = 0; i < size; i++)
            {
                SaleReturnTransactionIfc temp = originalReturnTransactions.get(i);
                if (TransactionUtility.isOfSameOriginalReturnTransaction(temp, transaction))
                {
                    addTransaction = false;
                    // Stop the loop.
                    i = size;
                }
            }
        }
        if(addTransaction)
        {
            originalReturnTransactions.add(transaction);
        }
    }

    /**
     * Add the item created by the cashier to the transaction and journal the
     * item number, description, price, quantity, sales associate, and tax mode.
     * <P>
     */
    public void completeItemNotFound(BusIfc bus)
    {
        pluItem.setDepartmentID(getDepartmentID());

        SaleReturnLineItemIfc item = ((SaleReturnTransactionIfc)transaction).addPLUItem(pluItem, itemQuantity);
        item.setItemSerial(itemSerialNumber);
        // setting size in item
        if (sizeCode != null)
        {
            item.setItemSizeCode(sizeCode);
            // resetting sizecode for checking before setting
            sizeCode = null;
        }
        if (transaction != null && ((SaleReturnTransactionIfc)transaction).isTransactionLevelSendAssigned())
        {
            SendManagerIfc sendMgr = null;
            try
            {
                sendMgr = (SendManagerIfc)ManagerFactory.create(SendManagerIfc.MANAGER_NAME);
            }
            catch (ManagerException e)
            {
                // default to product version
                sendMgr = new SendManager();
            }
            // this check is being done to make sure
            // this is a valid send item
            if (sendMgr.checkValidSendItem(item))
            {
                item.setItemSendFlag(true);
                // this value is always 1 since multiple sends are not allowed
                item.setSendLabelCount(1);
            }

        }
        // Make a journal entry
        JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        JournalFormatterManagerIfc formatter = (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager(
                JournalFormatterManagerIfc.TYPE);
        if (journal != null)
        {

            StringBuffer sb = new StringBuffer();
            sb.append(formatter.toJournalString(item, null, null));

            Object dataArgs[] = { pluItem.getDepartmentID() };
            String deptNumber = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.DEPARTMENT_NUMBER, dataArgs);

            String itemNotFound = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.ITEM_NOT_FOUND, dataArgs);

            sb.append(Util.EOL).append(deptNumber).append(Util.EOL).append(itemNotFound).append(Util.EOL);

            // The employee is not always set at this point.
            EmployeeIfc employee = getEmployee();
            if (employee == null)
            {
                employee = getOperator();
            }

            journal.journal(employee.getLoginID(), transaction.getTransactionID(), sb.toString());
        }
    }

    /**
     * Initializes a transaction and sets it in this cargo.
     *
     * @param bus The bus
     */
    public void initializeTransaction(BusIfc bus)
    {
        SaleReturnTransactionIfc transaction = DomainGateway.getFactory().getSaleReturnTransactionInstance();
        transaction.setCashier(getOperator());
        if (bus.getCargo() instanceof GiftCardCargo)
        {
            transaction.setSalesAssociate(getSalesAssociate());
        }
        else
        {
            transaction.setSalesAssociate(getEmployee());
        }

        boolean transReentry = register.getWorkstation().isTransReentryMode();
        ((SaleReturnTransaction)transaction).setReentryMode(transReentry);

        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        utility.initializeTransaction(transaction);
        setTransaction(transaction);
    }

    /**
     * Returns the passwordRequired flag
     *
     * @return the passwordRequired flag
     */
    public boolean isPasswordRequired()
    {
        return (passwordRequired);
    }

    /**
     * Sets the passwordRequired flag
     *
     * @param value the value for the flag
     */
    public void setPasswordRequired(boolean value)
    {
        passwordRequired = value;
    }

    /**
     * Sets the itemScanned flag.
     *
     * @param value boolean
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
     * Sets the refreshNeeded flag.
     *
     * @param value boolean
     */
    public void setRefreshNeeded(boolean value)
    {
        refreshNeeded = value;
    }

    /**
     * Returns the refreshNeeded flag.
     *
     * @return boolean
     */
    public boolean isRefreshNeeded()
    {
        return refreshNeeded;
    }

    /**
     * Sets the firstSale flag.
     *
     * @param value boolean
     */
    public void setFirstSale(boolean value)
    {
        firstSale = value;
    }

    /**
     * Returns the firstSale flag.
     *
     * @return boolean
     */
    public boolean isFirstSale()
    {
        return firstSale;
    }

    /**
     * Returns the send index.
     *
     * @return the integer value
     */
    public int getSendIndex()
    {
        return sendIndex;
    }

    /**
     * Sets the send index.
     *
     * @param value the integer value
     */
    public void setSendIndex(int value)
    {
        sendIndex = value;
    }

    /**
     * Returns a string representation of this object.
     *
     * @return String representation of object
     */
    public String toString()
    {
        StringBuilder strResult = new StringBuilder("Class:  SaleCargo (Revision " + getRevisionNumber() + ") @" + hashCode());

        /***********************************************************************
         * Display the value of each attribute of the class. For each object *
         * display the word "null" if the reference is null. *
         ***********************************************************************/

        strResult.append("\nemployeeID             = [").append(employeeID).append("]");

        if (employee == null)
            strResult.append("\nemployee               = [null]");
        else
            strResult.append("\nemployee               = [").append(employee).append("]");

        if (indices != null)
        {
            for (int i = 0; i < indices.length; i++)
            {
                strResult.append("\nindices                  = [").append(indices[i]).append("]");
            }
        }
        else
        {
            strResult.append("\nindices                  = [null]");
        }
        strResult.append("\npluItemID              = [").append(pluItemID).append("]");
        strResult.append("\ndepartmentName         = [").append(departmentName).append("]");

        if (itemQuantity == null)
            strResult.append("\nitemQuantity           = [null]");
        else
            strResult.append("\nitemQuantity           = [").append(itemQuantity).append("]");

        if (pluItem == null)
            strResult.append("\npluItem                = [null]");
        else
            strResult.append("\npluItem                = [").append(pluItem).append("]");

        strResult.append("\ndataExceptionErrorCode = [").append(dataExceptionErrorCode).append("]");
        strResult.append("\ntillStatus             = [").append(tillStatus).append("]");
        strResult.append("\nwaitForNext            = [").append(waitForNext).append("]");

        if (exitLetter == null)
            strResult.append("\nexitLetter             = [null]");
        else
            strResult.append("\nexitLetter             = [").append(exitLetter).append("]");

        strResult.append("\npasswordRequired       = [").append(String.valueOf(passwordRequired)).append("]");

        if (originalReturnTransactions == null)
            strResult.append("\noriginalReturnTransactions = [null]");
        else
        {
            for (SaleReturnTransactionIfc srt : originalReturnTransactions)
            {
                strResult.append("\noriginalReturnTransaction    = ").append(srt);
            }
        }

        return strResult.toString();
    }

    /**
     * Set whether or not a timeout has occurred
     *
     * @param aValue
     * @see oracle.retail.stores.pos.services.common.TimedCargoIfc#setTimeout(boolean)
     */
    public void setTimeout(boolean aValue)
    {
        this.timeout = aValue;
    }

    /**
     * Tell whether or not a timeout has occurred
     *
     * @return
     * @see oracle.retail.stores.pos.services.common.TimedCargoIfc#isTimeout()
     */
    public boolean isTimeout()
    {
        return this.timeout;
    }

    /**
     * Get the GeoCode used for taxes
     *
     * @return geoCode
     */
    public String getGeoCode()
    {
        // if the geoCode is null try getting from the store
        if (geoCode == null && getStoreStatus() != null && getStoreStatus().getStore() != null)
        {
            geoCode = getStoreStatus().getStore().getGeoCode();
        }
        return geoCode;
    }

    /**
     * Set the geoCode used for taxes
     *
     * @param value the geoCode
     */
    public void setGeoCode(String value)
    {
        geoCode = value;
    }

    /**
     * Get the store ID
     *
     * @return store ID
     */
    public String getStoreID()
    {
        // if the geoCode is null try getting from the store
        if (storeID == null && getStoreStatus() != null && getStoreStatus().getStore() != null)
        {
            storeID = getStoreStatus().getStore().getStoreID();
        }
        return storeID;
    }

    /**
     * Set the store ID
     *
     * @param value the store ID
     */
    public void setStoreID(String value)
    {
        storeID = value;
    }

    /**
     * This is to keep track if sales associate set using transaction options
     *
     * @param boolean value true if being set first time
     */
    public void setAlreadySetTransactionSalesAssociate(boolean value)
    {
        salesAssociateAlreadySet = value;
    }

    /**
     * Already set sales associate using transaction options return true is
     * sales associate is already set
     */
    public boolean isAlreadySetTransactionSalesAssociate()
    {
        return salesAssociateAlreadySet;
    }

    /**
     * Set whether the customer info prompt can be skipped
     *
     * @param value
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#setCanSkipCustomerPrompt(boolean)
     */
    public void setCanSkipCustomerPrompt(boolean value)
    {
        this.skipCustomerPrompt = value;
    }

    /**
     * Get whether the customer info prompt can be skipped
     *
     * @return
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#getCanSkipCustomerPrompt()
     */
    public boolean getCanSkipCustomerPrompt()
    {
        return this.skipCustomerPrompt;
    }

    /**
     * Returns the enableCancel
     *
     * @return boolean
     */
    public boolean isEnableCancelItemNotFoundFromReturns()
    {
        // for PLUCargoIfc
        return false;
    }

    /**
     * Set enable cancel
     *
     * @param enableCancel The enableCancel to set.
     */
    public void setEnableCancelItemNotFoundFromReturns(boolean enableCancel)
    {
        // for PLUCargoIfc
    }

    /**
     * Returns the flag indicating if the plu item is from an external order
     *
     * @return the boolean flag
     */
    public boolean isExternalOrder()
    {
        return false;
    }

    /**
     * Returns the external item price.
     *
     * @return the CurrencyIfc value
     */
    public CurrencyIfc getItemPrice()
    {
        PLUItemIfc plu = getPLUItem();
        CurrencyIfc price = null;
        if (plu != null)
        {
            price = plu.getPrice();
        }

        return price;
    }

    /**
     * Return the item description
     *
     * @return the String value
     */
    public String getItemDescription()
    {
        PLUItemIfc plu = getPLUItem();
        String desc = null;
        if (plu != null)
        {
            desc = plu.getDescription(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));
        }

        return desc;
    }

    /**
     * Set return response
     *
     * @param returnResponse The Return Response
     */
    public void setReturnResponse(RPIResponseIfc returnResponse)
    {
        this.returnResponse = returnResponse;
    }

    /**
     * Get return response
     *
     * @return RPIResponseIfc
     */
    public RPIResponseIfc getReturnResponse()
    {
        return returnResponse;
    }

    /**
     * Get return request
     *
     * @return returnRequest The Return Request
     */
    public RPIRequestIfc getReturnRequest()
    {
        return returnRequest;
    }

    /**
     * Setter
     *
     * @param returnRequest The Return Request
     */
    public void setReturnRequest(RPIRequestIfc returnRequest)
    {
        this.returnRequest = returnRequest;
    }

    /**
     * Get return result
     *
     * @return returnResult The Return Result
     */
    public RPIFinalResultIfc getReturnResult()
    {
        return returnResult;
    }

    /**
     * Set return result
     *
     * @param returnResult RPIFinalResultIfc
     */
    public void setReturnResult(RPIFinalResultIfc returnResult)
    {
        this.returnResult = returnResult;
    }

    /**
     * @return the beginIterationOverLegalDocuments
     */
    public boolean isBeginIterationOverLegalDocuments()
    {
        return beginIterationOverLegalDocuments;
    }

    /**
     * @param beginIterationOverLegalDocuments the
     *            beginIterationOverLegalDocuments to set
     */
    public void setBeginIterationOverLegalDocuments(boolean beginIterationOverLegalDocuments)
    {
        this.beginIterationOverLegalDocuments = beginIterationOverLegalDocuments;
    }

    /**
     * @return the legalDocument
     */
    public LegalDocumentIfc getLegalDocument()
    {
        return legalDocument;
    }

    /**
     * @param legalDocument the legalDocument to set
     */
    public void setLegalDocument(LegalDocumentIfc legalDocument)
    {
        this.legalDocument = legalDocument;
    }

    /**
     * @return the nextLegalDocumentRecord
     */
    public int getNextLegalDocumentRecord()
    {
        return nextLegalDocumentRecord;
    }

    /**
     * @param nextLegalDocumentRecord the nextLegalDocumentRecord to set
     */
    public void setNextLegalDocumentRecord(int nextLegalDocumentRecord)
    {
        this.nextLegalDocumentRecord = nextLegalDocumentRecord;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#isReverseGiftCard()
     */
    public boolean isReverseGiftCard()
    {
        return isReverseGiftCard;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#setReverseGiftCard
     * (boolean)
     */
    public void setReverseGiftCard(boolean value)
    {
        isReverseGiftCard = value;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#setReverseCount(int)
     */
    public void setReverseCount(int value)
    {
        reverseCount = value;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#getReverseCount()
     */
    public int getReverseCount()
    {
        return reverseCount;
    }

    @Override
    public boolean isSkipUOMCheck()
    {
        return skipUOMCheckFlag;
    }

    @Override
    public void skipUOMCheck(boolean skipUOMCheckFlag)
    {
        this.skipUOMCheckFlag = skipUOMCheckFlag;

    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#getSerializedItemIndex()
     */
    @Override
    public int getSerializedItemIndex()
    {
        return serializedItemIndex;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#setSerializedItemIndex(int)
     */
    @Override
    public void setSerializedItemIndex(int index)
    {
        this.serializedItemIndex = index;
    }

    /**
     * Sets the selected scan sheet item.
     * @param itemID
     */
    public void setSelectedScanSheetItemID(String itemID)
    {
        this.selectedScanSheetItemID = itemID;
    }

    /**
     * Gets the selected scan sheet item.
     * @return the selected scan sheet item.
     */
    public String getSelectedScanSheetItemID()
    {
        return this.selectedScanSheetItemID;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#getCreditReferralBeanModel()
     */
    @Override
    public CreditReferralBeanModel getCreditReferralBeanModel()
    {
        return creditReferralBeanModel;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#setCreditReferralBeanModel(oracle.retail.stores.pos.ui.beans.CreditReferralBeanModel)
     */
    @Override
    public void setCreditReferralBeanModel(CreditReferralBeanModel creditReferralBeanModel)
    {
        this.creditReferralBeanModel = creditReferralBeanModel;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#getCallReferralData()
     */
    @Override
    public CallReferralData getCallReferralData()
    {
        return callReferralData;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#setCallReferralData(oracle.retail.stores.pos.services.tender.authorization.CallReferralData)
     */
    @Override
    public void setCallReferralData(CallReferralData callReferralData)
    {
        this.callReferralData = callReferralData;
    }

    public List<SaleReturnLineItemIfc> getOrderLineItems() {
        return orderLineItems;
    }

    public void addOrderLineItem(SaleReturnLineItemIfc lineItem) {
        orderLineItems.add(lineItem);
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#isNoSignatureCapture()
     */
    @Override
    public boolean isNoSignatureCapture()
    {
        return this.noSignatureCapture;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#setNoSignatureCapture(boolean)
     */
    @Override
    public void setNoSignatureCapture(boolean noSignatureCapture)
    {
        this.noSignatureCapture = noSignatureCapture;
    }

    /**
     * gets teh pickup or delivery executed boolean
     * @return boolean
     */
    public boolean isPickupOrDeliveryExecuted()
    {
        return pickupOrDeliveryExecuted;
    }

    /**
     * Sets the pickup or delivery executed boolean
     * @param pickupOrDeliveryExecuted
     */
    public void setPickupOrDeliveryExecuted(boolean pickupOrDeliveryExecuted)
    {
        this.pickupOrDeliveryExecuted = pickupOrDeliveryExecuted;
    }

    /*
     * (non-Javadoc)
     * @see
     * oracle.retail.stores.pos.services.common.PLUCargoIfc#getPLUItemForSizePrompt
     * ()
     */
    public PLUItemIfc getPLUItemForSizePrompt()
    {
        return pluItem;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#setGiftCardActivationsCanceled(boolean)
     */
    @Override
    public void setGiftCardActivationsCanceled(boolean giftCardActivationsCanceled)
    {
        this.giftCardActivationsCanceled = giftCardActivationsCanceled;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#isGiftCardActivationsCanceled()
     */
    @Override
    public boolean isGiftCardActivationsCanceled()
    {
        return this.giftCardActivationsCanceled;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#isSuppressGiftCardActivation()
     */
    @Override
    public boolean isSuppressGiftCardActivation()
    {
        return suppressGiftCardActivation;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#setSuppressGiftCardActivation(boolean)
     */
    @Override
    public void setSuppressGiftCardActivation(boolean suppressGiftCardActivation)
    {
        this.suppressGiftCardActivation = suppressGiftCardActivation;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#isSuppressGiftCardDeactivation()
     */
    @Override
    public boolean isSuppressGiftCardDeactivation()
    {
        return suppressGiftCardDeactivation;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#setSuppressGiftCardDeactivation(boolean)
     */
    @Override
    public void setSuppressGiftCardDeactivation(boolean suppressGiftCardDeactivation)
    {
        this.suppressGiftCardDeactivation = suppressGiftCardDeactivation;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#getSelectedRecommendedItemId()
     */
    @Override
    public String getSelectedRecommendedItemId()
    {
        return this.selectedRecommendedItemId;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#setSelectedRecommendedItemId(java.lang.String)
     */
    @Override
    public void setSelectedRecommendedItemId(String itemId)
    {
        this.selectedRecommendedItemId = itemId;
    }

    /**
     * @return the skipReturnAuthorization
     */
    public boolean isSkipReturnAuthorization()
    {
        return skipReturnAuthorization;
    }

    /**
     * @param skipReturnAuthorization the skipReturnAuthorization to set
     */
    public void skipReturnAuthorization(boolean skipReturnAuthorization)
    {
        this.skipReturnAuthorization = skipReturnAuthorization;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#setFromFulfillment(boolean)
     */
    @Override
    public void setFromFulfillment(boolean value)
    {
        this.fromFulfillment = value;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#isFromFulfillment()
     */
    @Override
    public boolean isFromFulfillment()
    {
        return this.fromFulfillment;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#setExitToFulfillment(boolean)
     */
    @Override
    public void setExitToFulfillment(boolean value)
    {
        this.exitToFulfillment = value;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.services.sale.SaleCargoIfc#isExitToFulfillment()
     */
    @Override
    public boolean isExitToFulfillment()
    {
        return this.exitToFulfillment;
    }

}

