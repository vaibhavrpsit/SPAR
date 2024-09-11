 /* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/ItemCargo.java /main/28 2013/03/05 14:03:16 yiqzhao Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    yiqzha 02/26/13 - Check for order item after adding related item(s).
 *    jswan  09/25/12 - Modified to support retrieval of the list of Service
 *                      (non-merchandise) items.
 *    cgreen 05/08/12 - implement force use of entered serial number instead of
 *                      checking with SIM
 *    cgreen 03/09/12 - add support for journalling queues by current register
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    cgreen 02/15/11 - move constants into interfaces and refactor
 *    acadar 06/10/10 - refreshed to tip
 *    acadar 06/10/10 - use default locale for currency display
 *    acadar 06/09/10 - XbranchMerge acadar_tech30 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    sgu    06/08/10 - fix tab
 *    sgu    06/08/10 - rename mandatoryPrice to externalPrice to be consistent
 *    sgu    06/08/10 - add item # & desc to the screen prompt. fix unknow item
 *                      screen to disable price and quantity for external item
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 03/26/10 - added line feed to end of journal when updating tax
 *    abonda 01/03/10 - update header date
 *    asinto 05/12/09 - Removed setRetailTransactionIfc and
 *                      getRetailTransactionIfc as they duplicated the
 *                      functionality of setTransaction and getTransaction,
 *                      respectively.
 *    jswan  04/14/09 - Modified to fix conflict between multi quantity items
 *                      and items that have been marked for Pickup or Delivery.
 *    nkgaut 04/08/09 - EJ Fixes for extra space and reason codes
 *    nkgaut 04/02/09 - Fix for missing space in EJ for Tax Reason
 *    vcheng 01/06/09 - EJ defect fixes
 *    vcheng 12/17/08 - ej defect fixes
 *    aphula 11/22/08 - Checking files after code review by Naga
 *    aphula 11/13/08 - Check in all the files for Pickup Delivery Order
 *                      functionality
 *    acadar 11/03/08 - localization of reason codes for discounts and merging
 *                      to tip
 *    acadar 11/02/08 - updated as per code review
 *    acadar 10/30/08 - cleanup
 *    acadar 10/28/08 - localization for item tax reason codes
 *
 * ===========================================================================

 * $Log:
 *  9    360Commerce 1.8         5/21/2007 9:16:21 AM   Anda D. Cadar   EJ
 *       changes
 *  8    360Commerce 1.7         4/25/2007 8:52:23 AM   Anda D. Cadar   I18N
 *       merge
 *
 *  7    360Commerce 1.6         3/29/2007 2:08:35 PM   Michael Boyd    CR
 *       26172 - v8x merge to trunk
 *
 *       6    .v8x      1.4.1.0     1/12/2007 4:25:34 PM   Brett J. Larsen CR
 *       23450
 *       - ejournal label for tax-modifier reason was inconsistent with
 *       receipt and, more importantly, the ejournal label for retrieved
 *       transactions - made everything consistent (w/ QA's approval)
 *  6    360Commerce 1.5         1/12/2007 4:50:28 PM   Brett J. Larsen Merge
 *       from ItemCargo.java, Revision 1.4.1.0
 *  5    360Commerce 1.4         7/28/2006 5:44:03 PM   Brett J. Larsen 4530:
 *       default reason code fix
 *       v7x->360Commerce merge
 *  4    360Commerce 1.3         1/22/2006 11:45:11 AM  Ron W. Haight   removed
 *        references to com.ibm.math.BigDecimal
 *  3    360Commerce 1.2         3/31/2005 4:28:30 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:22:22 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:11:36 PM  Robert Pearse
 * $

 *  5    .v7x      1.3.1.0     6/23/2006 5:00:24 AM   Dinesh Gautam   CR 4530:
 *       Fix for reason code

 * Revision 1.22.2.1  2004/10/28 17:19:05  jdeleau
 * @scr 7540 Make sure subtotal prints out tax items for a kit.
 *
 * Revision 1.22  2004/09/27 22:32:03  bwf
 * @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 * Revision 1.21  2004/09/01 14:31:03  rsachdeva
 * @scr 6791 Transaction Level Send Javadoc
 *
 * Revision 1.20  2004/08/27 15:07:43  rsachdeva
 * @scr 6791 Deprecate Customer Present
 *
 * Revision 1.19  2004/08/09 16:28:04  rsachdeva
 * @scr 6719 Send Level In Progress
 *
 * Revision 1.18  2004/08/09 16:13:53  rsachdeva
 * @scr 6791 Send Level In Progress
 *
 * Revision 1.17  2004/07/27 00:07:46  jdeleau
 * @scr 6251 Make sure when tax is toggled off, that the original tax is charged when its toggled back on
 *
 * Revision 1.16  2004/07/12 21:41:59  jdeleau
 * @scr 6132 Remove commented out code
 *
 * Revision 1.15  2004/07/12 21:40:42  jdeleau
 * @scr 6132 Fix e-journal entries for tax overrides
 *
 * Revision 1.14  2004/06/11 19:10:35  lzhao
 * @scr 4670: add customer present feature
 *
 * Revision 1.13  2004/06/09 19:45:14  lzhao
 * @scr 4670: add customer present dialog and the flow.
 *
 * Revision 1.12  2004/06/04 20:23:45  lzhao
 * @scr 4670: add Change send functionality.
 *
 * Revision 1.11  2004/06/03 14:47:43  epd
 * @scr 5368 Update to use of DataTransactionFactory
 *
 * Revision 1.10  2004/05/05 22:18:53  dcobb
 * @scr 4389 Tax Override multiitem select non-taxable & taxable items, then turn off tax on these items: tax is not turned off
 *
 * Revision 1.9  2004/04/20 13:17:05  tmorris
 * @scr 4332 -Sorted imports
 *
 * Revision 1.8  2004/04/12 18:52:57  pkillick
 * @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 * Revision 1.7  2004/03/16 18:30:46  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.6  2004/03/11 14:32:10  baa
 * @scr 3561 Add itemScanned get/set methods to PLUItemCargoIfc and add support for changing type of quantity based on the uom
 *
 * Revision 1.5  2004/03/05 00:41:52  bjosserand
 * @scr 3954 Tax Override
 * Revision 1.4 2004/02/12 21:36:28 epd @scr 0 These files comprise all new/modified files
 * that make up the refactored send service
 *
 * Revision 1.3 2004/02/12 16:51:02 mcs Forcing head revision
 *
 * Revision 1.2 2004/02/11 21:39:28 rhafernik @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1 2004/02/11 01:04:17 cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.0 Aug 29 2003 16:01:36 CSchellenger Initial revision.
 *
 * Rev 1.6 Apr 15 2003 14:08:50 bwf Add discount for itemdiscount service. Resolution for 2103: Remove uses of
 * deprecated items in POS.
 *
 * Rev 1.5 Feb 16 2003 10:43:28 mpm Merged 5.1 changes. Resolution for POS SCR-2053: Merge 5.1 changes into 6.0
 *
 * Rev 1.4 Feb 12 2003 18:19:00 crain Refactored the methods that retrieve reason code lists Resolution for 1907:
 * Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 *
 * Rev 1.3 Feb 11 2003 15:50:04 crain Get the code list map from utility manager Resolution for 1907: Remove deprecated
 * calls to AbstractFinancialCargo.getCodeListMap()
 *
 * Rev 1.2 Jan 16 2003 10:43:18 sfl Make sure item tax override amount is displayed with two digits after decimal point
 * in EJ. Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 * Rev 1.1 Aug 21 2002 11:21:24 DCobb Added Alterations service. Resolution for POS SCR-1753: POS 5.5 Alterations
 * Package
 *
 * Rev 1.0 Apr 29 2002 15:16:54 msg Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.item.AdvItemSearchResults;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.ItemSerialCargoIfc;

import org.apache.log4j.Logger;

/**
 * Cargo class for item package operations, primarily sale/return items.
 *
 */
public class ItemCargo extends AbstractFinancialCargo
    implements ItemSerialCargoIfc, CodeConstantsIfc
{
    private static final long serialVersionUID = 7948937969724457284L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(ItemCargo.class);;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/28 $";

    public static final String JOURNAL_PREFIX = "JournalEntry";

    /**
     * line item
     */
    protected SaleReturnLineItemIfc item;

    /**
     * line items
     */
    protected SaleReturnLineItemIfc[] items;

    /**
     * kit header line item
     */
    protected KitHeaderLineItemIfc kitHeader;

    /**
     * line item index
     */
    protected int index;

    /**
     * line item indices
     */
    protected int[] indices;

    /**
     * PLU Item
     */
    protected PLUItemIfc pluItem = null;

    /**
     * Item Quantity
     */
    protected BigDecimal itemQuantity = BigDecimalConstants.ONE_AMOUNT;

    /**
     * Gift Card Item
     */
    protected GiftCardIfc giftCard = null;

    /**
     * Flag to determine whether to add the PLU Item to the current item
     */
    protected boolean addPLUItem = false;

    /**
     * Serial number of item to be added
     */
    protected String itemSerial = null;

    /**
     * transaction type - sale or return
     */
    protected RetailTransactionIfc transaction;

    /**
     * the list of service items
     * @deprecated in 14.0; see serviceItems
     */
    protected PLUItemIfc[] serviceItemList;

    /**
     * The list of service items.
     */
    protected AdvItemSearchResults serviceItems;
    
    /**
     * Security override Return Letter
     */
    protected String securityOverrideReturnLetter;

    /**
     * Flag to determine whether a service item was added thru inquiry/services
     */
    protected boolean serviceItemFlag = false;

    /**
     * Flag to indicate whether the item is an alteration item.
     */
    protected boolean alterationItemFlag = false;

    /**
     * discount amount reason code list
     */
    protected CodeListIfc localizedDiscountAmountReasons = null;

    /**
     * price override code list
     */
    protected CodeListIfc localizedPriceOverrideReasons = null;

    /**
     * manual discount percent reason code list
     */
    protected CodeListIfc localizedDiscountPercentReasons = null;

    /**
     * discount type
     */
    protected int discountType = 0;

    /**
     * Customer to be linked
     */
    protected CustomerIfc customer;

    /**
     * This flag indicates whether the item that was entered was scanned or
     * typed.
     */
    protected boolean itemScanned = false;

    /**
     * This flag indicates whether the item is going to update send information.
     */
    protected boolean itemUpdate = false;

    /**
     * send level in progress.If false, it implies Item level send is in
     * progress, otherwise transaction level send is in progress.
     */
    protected boolean transactionLevelSendInProgress = false;

    /**
     * Indicates that pickup or delivery was executed
     */
    protected boolean pickupOrDeliveryExecuted = false;
    
    List<SaleReturnLineItemIfc> orderLineItems = new ArrayList<SaleReturnLineItemIfc>();

    /**
     * Constructs ItemCargo object.
     */
    public ItemCargo()
    {
    }

    /**
     * Resets ItemCargo object.
     */
    public void resetCargo()
    {
        item = DomainGateway.getFactory().getSaleReturnLineItemInstance();
    }

    /**
     * Updates item with tax changes.
     *
     * @param newTax new item tax settings
     */
    public void updateItemTax(ItemTaxIfc newTax)
    {
        int taxMode = newTax.getTaxMode();
        LocalizedCodeIfc reasonCode = newTax.getReason();
        CurrencyIfc overrideAmount = newTax.getOverrideAmount();
        double overrideRate = newTax.getOverrideRate();
        if (items != null)
        {
            // update tax based on tax mode
            for (int i = 0; i < items.length; i++)
            {
                if (items[i].isTaxChanged())
                {
                    ItemPriceIfc ip = items[i].getItemPrice();
                    //ItemPriceIfc ip = item.getItemPrice();
                    switch (taxMode)
                    { // begin evaluate new tax mode
                        case TaxIfc.TAX_MODE_OVERRIDE_AMOUNT :
                            if (logger.isInfoEnabled())
                                logger.info("Overriding item tax amount ...");
                            // override tax amount on item
                            ip.overrideTaxAmount(overrideAmount, reasonCode);
                            break;
                        case TaxIfc.TAX_MODE_OVERRIDE_RATE :
                            if (logger.isInfoEnabled())
                                logger.info("Overriding item tax rate ...");
                            // override tax rate on item
                            ip.overrideTaxRate(overrideRate, reasonCode);
                            break;
                        case TaxIfc.TAX_MODE_TOGGLE_ON :
                            if (logger.isInfoEnabled())
                                logger.info("Setting item tax toggle on ...");
                            ip.toggleTax(true, reasonCode);
                            break;
                        case TaxIfc.TAX_MODE_TOGGLE_OFF :
                            if (logger.isInfoEnabled())
                                logger.info("Setting item tax toggle off ...");
                            ip.toggleTax(false, reasonCode);
                            break;
                        case TaxIfc.TAX_MODE_STANDARD :
                        default :
                            if (logger.isInfoEnabled())
                                logger.info("Resetting standard tax ...");
                            // reset standard
                            ip.clearTaxOverride();
                            break;
                    } // end evaluate new tax mode
                    // Set the original tax code
                    items[i].getItemPrice().getItemTax().setOriginalTaxMode(newTax.getOriginalTaxMode());
                }
            }
        }
    } // end updateItemTax()

    /**
     * Retrieves line item.
     *
     * @return line item
     */
    public SaleReturnLineItemIfc getItem()
    {
        return item;
    }

    /**
     * Retrieves list of line items .
     *
     * @return list of line items
     */
    public SaleReturnLineItemIfc[] getItems()
    {
        return items;
    }

    /**
     * Sets line item.
     *
     * @param value line item
     */
    public void setItem(SaleReturnLineItemIfc value)
    {
        item = value;
    }

    /**
     * Sets list of line items.
     *
     * @param values of list of line item
     */
    public void setItems(SaleReturnLineItemIfc[] values)
    {
        items = values;
    }

    /**
     * Sets the kit header line item to modify.
     *
     * @param item The line item of the kit header
     */
    public void setKitHeader(KitHeaderLineItemIfc item)
    {
        kitHeader = item;
    }

    /**
     * Returns the kit header line item to modify.
     *
     * @return line item
     */
    public KitHeaderLineItemIfc getKitHeader()
    {
        return kitHeader;
    }

    /**
     * Retrieves transaction identifier.
     *
     * @return transaction identifier
     */
    public String getTransactionID()
    {
        String id = "";
        if (transaction != null)
        {
            id = transaction.getTransactionID();
        }
        return (id);
    }

    /**
     * Sets transaction.
     *
     * @param transaction the retail transaction
     */
    public void setTransaction(RetailTransactionIfc transaction)
    {
        this.transaction = transaction;
    }

    /**
     * Returns the current retail transaction.
     *
     * @return transaction the retail transaction
     */
    public RetailTransactionIfc getTransaction()
    {
        return transaction;
    }

    /**
     * Retrieves plu item.
     *
     * @return plu item
     */
    public PLUItemIfc getPLUItem()
    {
        return (pluItem);
    }

    /**
     * Sets plu item.
     *
     * @param item plu item
     */
    public void setPLUItem(PLUItemIfc item)
    {
        pluItem = item;
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
     *
     * @param value The item quantity.
     */
    public void setItemQuantity(BigDecimal value)
    {
        itemQuantity = value;
    }

    /**
     * Retrieves the gift card.
     *
     * @return GiftCardIfc
     */
    public GiftCardIfc getGiftCard()
    {
        return (giftCard);
    }

    /**
     * Sets the gift card.
     *
     * @param gftCard The gift card
     */
    public void setGiftCard(GiftCardIfc gftCard)
    {
        giftCard = gftCard;
    }

    /**
     * Returns whether to add the PLU Item to the current item.
     *
     * @return True, if the PLU Item should be added to the current item. False
     *         otherwise.
     */
    public boolean getAddPLUItem()
    {
        return (addPLUItem);
    }

    /**
     * Sets whether to add the PLU Item to the current item.
     *
     * @param value true or false
     */
    public void setAddPLUItem(boolean value)
    {
        addPLUItem = value;
    }

    /**
     * Returns whether the item added is a service item added thru
     * inquiry/services.
     *
     * @return True, if the item added was a service item added thru
     *         inquiry/services. False otherwise.
     */
    public boolean getServiceItemFlag()
    {
        return (serviceItemFlag);
    }

    /**
     * Sets whether the item added was a service item added thru
     * inquiry/services.
     *
     * @param value true or false
     */
    public void setServiceItemFlag(boolean value)
    {
        serviceItemFlag = value;
    }

    /**
     * Gets the alteration item flag.
     *
     * @return the alteration item flag
     */
    public boolean getAlterationItemFlag()
    {
        return alterationItemFlag;
    }

    /**
     * Sets whether the item added was an alteration item.
     *
     * @param value true or false
     */
    public void setAlterationItemFlag(boolean value)
    {
        alterationItemFlag = value;
    }

    /**
     * Returns serial number of item to be added.
     *
     * @return Serial number of item to be added.
     */
    public String getItemSerial()
    {
        return itemSerial;
    }

    /**
     * Sets serial number of item to be added.
     *
     * @param value Serial number of item to be added
     */
    public void setItemSerial(String value)
    {
        itemSerial = value;
    }

    /**
     * Retrieves line-item index.
     *
     * @return line-item index
     */
    public int getIndex()
    {
        return (index);
    }

    /**
     * Sets line-item index.
     *
     * @param value line-item index
     */
    public void setIndex(int value)
    {
        index = value;
    }

    /**
     * Retrieves line-item indices.
     *
     * @return line-item indices
     */
    public int[] getIndices()
    {
        return (indices);
    }

    /**
     * Sets line-item indices.
     *
     * @param values  The line-item indices
     */
    public void setIndices(int[] values)
    {
        indices = values;
    }

    /**
     * Retrieves cashier.
     *
     * @return cashier
     */
    public EmployeeIfc getCashier()
    {
        return (getOperator());
    }

    /**
     * Retrieves line-item discount-by-amount reason code list.
     *
     * @return line-item discount-by-amount reason code list
     */
    public CodeListIfc getLocalizedDiscountAmountReasons()
    {
        return localizedDiscountAmountReasons;
    }

    /**
     * Sets  line-item discount-by-amount reason code list.
     * @param line-item discount-by-amount reason code list
     */
    public void setLocalizedDiscountAmountReasons(CodeListIfc list)
    {
        localizedDiscountAmountReasons = list;
    }

    /**
     * Sets the price overrride code list
     * @param CodeListIfc codeList
     */
    public void setLocalizedPriceOverrideReasons(CodeListIfc codeList)
    {
        localizedPriceOverrideReasons = codeList;
    }

    /**
     * Gets the price overrride code list
     * @return CodeListIfc codeList
     */
    public CodeListIfc getLocalizedPriceOverrideReasons()
    {
        return localizedPriceOverrideReasons;
    }

    /**
     * Retrieves line-item discount-by-percent reason code list.
     *
     * @return line-item discount-by-percent reason code list
     */
    public CodeListIfc getLocalizedDiscountPercentReasons()
    {
        return localizedDiscountPercentReasons;
    }

    /**
     * Sets the line-item discount-by-percent reason code list.
     *
     * @return line-item discount-by-percent reason code list
     */
    public void setLocalizedDiscountPercentReasons(CodeListIfc list)
    {
        localizedDiscountPercentReasons = list;
    }

    /**
     * Sets the list of service items.
     * @param items The list of service items.
     * @deprecated in 14.0; see setServiceItems()
     */
    public void setServiceItemList(PLUItemIfc[] items)
    {
        serviceItemList = items;
    }

    /**
     * Returns the list of service items.
     * @return the list of service items.
     * @deprecated in 14.0; see getServiceItems()
     */
    public PLUItemIfc[] getServiceItemList()
    {
        return (serviceItemList);
    }

    /**
     * @return the serviceItems
     */
    public AdvItemSearchResults getServiceItems()
    {
        return serviceItems;
    }

    /**
     * @param serviceItems the serviceItems to set
     */
    public void setServiceItems(AdvItemSearchResults serviceItems)
    {
        this.serviceItems = serviceItems;
    }

    /**
     * Gets the transaction type.
     *
     * @return transaction type value
     */
    public int getTransactionType()
    {
        int type = TransactionIfc.TYPE_UNKNOWN;

        if (transaction != null)
        {
            type = transaction.getTransactionType();
        }
        return (type);
    }

    /**
     * The securityOverrideReturnLetter returned by this cargo is to indicate
     * where the security override will return
     *
     * @param value The security override return letter
     */
    public void setSecurityOverrideReturnLetter(String value)
    {
        securityOverrideReturnLetter = value;
    }

    /**
     * The securityOverrideReturnLetter returned by this cargo is to indicate
     * where the security override will return
     *
     * @return the String value
     */
    public String getSecurityOverrideReturnLetter()
    {
        return securityOverrideReturnLetter;
    }

    /**
     * Returns discountType
     *
     * @return discountType discount type
     */
    public int getDiscountType()
    {
        return this.discountType;
    }

    /**
     * Sets discountType.
     *
     * @param discountType the discount type
     */
    public void setDiscountType(int discountType)
    {
        this.discountType = discountType;
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
     * Sets the itemUpdate flag.
     *
     * @param value boolean
     */
    public void setItemUpdate(boolean value)
    {
        itemUpdate = value;
    }

    /**
     * Returns the itemUpdate flag.
     *
     * @return boolean
     */
    public boolean isItemUpdate()
    {
        return itemUpdate;
    }

    /**
     * Method to default display string function.
     *
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        String strResult = new String("Class:  ItemCargo (Revision " + getRevisionNumber() + ") @" + hashCode());
        return (strResult);
    }

    /**
     * Retrieves the revision number.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }

    /**
     * @return Returns the customer.
     */
    public CustomerIfc getCustomer()
    {
        return customer;
    }

    /**
     * @param customer The customer to set.
     */
    public void setCustomer(CustomerIfc customer)
    {
        this.customer = customer;
    }

    /**
     * Checks if send at transaction level is in progress
     *
     * @return boolean true if transaction level send is in progress
     */
    public boolean isTransactionLevelSendInProgress()
    {
        return transactionLevelSendInProgress;
    }

    /**
     * Sets true if transaction level send is in progress. The send at
     * transaction level is not assigned yet - it is in progress.
     *
     * @param transactionLevelSendInProgress true for transaction level send in
     *            progress
     */
    public void setTransactionLevelSendlInProgress(boolean transactionLevelSendInProgress)
    {
        this.transactionLevelSendInProgress = transactionLevelSendInProgress;
    }

    /**
     * @return Returns the pickupOrDeliveryExecuted.
     */
    public boolean isPickupOrDeliveryExecuted()
    {
        return pickupOrDeliveryExecuted;
    }

    /**
     * @param pickupOrDeliveryExecuted The pickupOrDeliveryExecuted to set.
     */
    public void setPickupOrDeliveryExecuted(boolean pickupOrDeliveryExecuted)
    {
        this.pickupOrDeliveryExecuted = pickupOrDeliveryExecuted;
    }

    /**
     * @return the external price to use for this item
     */
    public CurrencyIfc getExternalPrice()
    {
        return null;
    }

    public List<SaleReturnLineItemIfc> getOrderLineItems() {
        return orderLineItems;
    }
    
    public void addOrderLineItem(SaleReturnLineItemIfc lineItem) {
        orderLineItems.add(lineItem);
    }
}
