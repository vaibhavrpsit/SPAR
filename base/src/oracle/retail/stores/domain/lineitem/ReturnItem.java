/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/lineitem/ReturnItem.java /main/22 2013/09/05 10:36:14 abondala Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    yiqzha 11/14/14 - Use isNonRetrievedReceiptedItem for mpos and pos.
 *    tkshar 11/03/14 - added isEmpty check to hasNonRetrievedOriginalReceiptId
 *    abonda 09/04/13 - initialize collections
 *    arabal 04/16/13 - added methods to check if the item
 *                      hasNonRetrievedOriginalReceiptId
 *    jswan  11/15/12 - Modified to support parameter controlled return
 *                      tenders.
 *    rabhaw 03/29/12 - Added method to check if item condition entered.
 *    rabhaw 03/01/12 - RM i18n changes added localizedItemConditionCode
 *    abonda 09/28/11 - add house account option in RM application
 *    cgreen 07/07/11 - convert entryMethod to an enum
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/28/10 - updating deprecated names
 *    abonda 01/03/10 - update header date
 *    cgreen 06/22/09 - remove unnecassary creation of BigDecimal
 *    cgreen 06/03/09 - add method hasOriginalTransactionID
 *    rkar   11/17/08 - View refresh to 081112.2142 label
 *    mdecam 11/07/08 - I18N - updated toString()
 *    mdecam 11/07/08 - I18N - Fixed Clone Method
 *    rkar   11/04/08 - Added code for POS-RM integration
 *    ranojh 10/31/08 - Refreshed View and Merged changes with Reason Codes
 *    ranojh 10/29/08 - Fixed ReturnItem
 *    ranojh 10/29/08 - Changes for Return, UOM and Department Reason Codes
 *    mdecam 10/23/08 - ReasonCode - Added new methods to the interfaces and
 *                      method stubs to the respective classes.
 *
 * ===========================================================================
 *
 *    Rev 1.2   Feb 05 2004 11:13:04   bwf
 * Fixed equals method so that unit test will pass.
 *
 *    Rev 1.1   Jan 23 2004 16:27:24   baa
 * continue return development
 *
 *    Rev 1.0   Aug 29 2003 15:38:04   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Feb 15 2003 14:52:16   mpm
 * Merged 5.1 changes.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.1   Dec 23 2002 12:37:04   crain
 * Added reasonCodeString attribute with get and set methods
 * Resolution for 1869: No reason Code Printed on Return Reciepts
 *
 *    Rev 1.0   Jun 03 2002 16:58:40   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.domain.lineitem;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.stock.ItemSizeIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.tender.TenderLineItemConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.common.utility.Util;

/**
 * This class is used for return item data.
 * 
 * @version $Revision: /main/22 $
 */
public class ReturnItem implements ReturnItemIfc
{
    /**
     * revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /main/22 $";

    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -4930662299132414851L;

    /**
     * entry method
     */
    protected EntryMethod entryMethod = EntryMethod.Manual;

    protected boolean fromGiftReceipt = false;

    protected boolean fromRetrievedTransaction = false;

    protected boolean haveReceipt = false;

    protected ItemSizeIfc itemSize;

    protected ItemTaxIfc itemTax;

    protected CurrencyIfc newLowerPrice;

    /**
     * The line number of the original line item. Used to find the original line
     * item in memory and the database.
     */
    protected int originalLineNumber = -1;

    /**
     * The business date on which the item was originally purchased.
     */
    protected EYSDate originalTransactionBusinessDate = null;

    /**
     * The transaction id of the transaction on which the item was originally
     * purchased.
     */
    protected TransactionIDIfc originalTransactionID = null;

    /**
     * price lookup item
     */
    protected PLUItemIfc pluItem = null;

    /**
     * price
     */
    protected CurrencyIfc price = null;

    /** The return quantity of this item */
    protected BigDecimal quantity;

    /** The quantity purchased **/
    protected BigDecimal quantityPurchased = null;

    /** The quantity purchased **/
    protected BigDecimal quantityReturnable = null;

    /**
     * restocking fee
     */
    protected CurrencyIfc restockingFee = null;

    /**
     * sales associate
     */
    protected EmployeeIfc salesAssociate = null;

    /**
     * serial number
     */
    protected String serialNumber = null;

    protected int startingSublineNumber = 0;

    /**
     * store
     */
    protected StoreIfc store = null;

    /* multi quantity price adjust sub line number (for matching up pairs) */
    protected int subLineNumber = -1;

    /**
     * tax rate
     */
    protected double taxRate = 0;

    /**
     * The list of tender type code strings, used for RM integration for return
     * items from multiple sale transactions
     */
    protected HashMap<String, String> tenderList = new HashMap<String, String>(0);

    /** The localized Reason Code **/
    protected LocalizedCodeIfc localizedReasonCode = null;
    
    /** The localized Item Condition Code **/
    protected LocalizedCodeIfc localizedItemConditionCode = null;
    
    /** The tender type entered by the operator during a non retrieved return; used
     *  to determine the return tender types the application allows.
     */
    protected int userSuppliedTenderType = TenderLineItemConstantsIfc.TENDER_TYPE_UNKNOWN; 

    /** The receipt ID enter by an operator during a non retrieved return.  This can be
     *  a POS transaction ID, an order ID, or a transaction ID from a legacy POS. 
     */
    protected String nonRetrievedOriginalReceiptId = null;
    
    /**
     * ReturnItem constructor comment.
     */
    public ReturnItem()
    {
        price = DomainGateway.getBaseCurrencyInstance();
        localizedReasonCode = DomainGateway.getFactory().getLocalizedCode();
        localizedItemConditionCode = DomainGateway.getFactory().getLocalizedCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone()
    {
        ReturnItem newItem = new ReturnItem();
        setCloneAttributes(newItem);
        return newItem;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        boolean isEqual = false;
        try
        {
            ReturnItem c = (ReturnItem)obj; // downcast the input object

            // compare all the attributes of ReturnItem
            if ((Util.isObjectEqual(pluItem, c.getPLUItem())) && (Util.isObjectEqual(price, c.getPrice()))
                    && (taxRate == c.getTaxRate()) 
                    && (Util.isObjectEqual(store, c.getStore()))
                    && (Util.isObjectEqual(salesAssociate, c.getSalesAssociate()))
                    && (Util.isObjectEqual(originalTransactionID, c.getOriginalTransactionID()))
                    && (Util.isObjectEqual(originalTransactionBusinessDate, c.getOriginalTransactionBusinessDate()))
                    && (originalLineNumber == c.getOriginalLineNumber())
                    && (Util.isObjectEqual(localizedReasonCode, c.getReason()))
                    && (Util.isObjectEqual(localizedItemConditionCode, c.getItemCondition()))
                    && (Util.isObjectEqual(restockingFee, c.getRestockingFee()))
                    && (Util.isObjectEqual(serialNumber, c.getSerialNumber())) 
                    && (entryMethod == c.getEntryMethod())
                    && (fromGiftReceipt == c.fromGiftReceipt) 
                    && (haveReceipt == c.haveReceipt)
                    && (fromRetrievedTransaction == c.fromRetrievedTransaction) 
                    && (subLineNumber == c.subLineNumber)
                    && (userSuppliedTenderType == c.userSuppliedTenderType)
                    && (Util.isObjectEqual(nonRetrievedOriginalReceiptId, c.getNonRetrievedOriginalReceiptId())) 
                    && (Util.isObjectEqual(newLowerPrice, c.newLowerPrice)))
            {
                isEqual = true;
            }
            else
            {
                isEqual = false;
            }
        }
        catch (Exception e)
        {
            isEqual = false;
        }
        return (isEqual);
    }

    /**
     * Returns entry method.
     * 
     * @return entry method
     */
    public EntryMethod getEntryMethod()
    {
        return (entryMethod);
    }

    /**
     * @return Gets the return quantity of this item.
     */
    public BigDecimal getItemQuantity()
    {
        return quantity;
    }

    /**
     * ItemSize of this return item. Used to take the size typed into the UI at
     * the "enter item return information" screen and use it later when you
     * create the SaleReturnLineItem
     * 
     * @return Returns the itemSize.
     */
    public ItemSizeIfc getItemSize()
    {
        return itemSize;
    }

    /**
     * For price adjustment, holds the item tax for the item with the lower
     * price. This is necessary because item taxes are calculated and set by the
     * transaction class during transaction totalling, not by the line item
     * during line item totalling.
     */
    public ItemTaxIfc getItemTax()
    {
        return itemTax;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.ReturnItemIfc#getLocalizedReasonCode(java.util.Locale)
     */
    public String getLocalizedReasonCode(Locale lcl)
    {
        Locale bestMatch = LocaleMap.getBestMatch(lcl);
        return localizedReasonCode.getText(bestMatch);
    }

   /*
    * (non-Javadoc)
    * @see oracle.retail.stores.domain.lineitem.ReturnItemIfc#getLocalizedItemConditionCode(java.util.Locale)
    */
    public String getLocalizedItemConditionCode(Locale lcl)
    {
        Locale bestMatch = LocaleMap.getBestMatch(lcl);
        return localizedItemConditionCode.getText(bestMatch);
    }
    
    /**
     * For price adjustment, holds the new lower price. This value is used to
     * create a temporary line item that represents the difference between the
     * old and new prices rather than 2 separate line items. Used in receipt and
     * EJ.
     */
    public CurrencyIfc getNewLowerPrice()
    {
        return newLowerPrice;
    }

    /**
     * Returns the original line item line number.
     * 
     * @return int
     */
    public int getOriginalLineNumber()
    {
        return (originalLineNumber);
    }

    /**
     * Returns the original Transaction Business Date.
     * 
     * @return Date
     */
    public EYSDate getOriginalTransactionBusinessDate()
    {
        return (originalTransactionBusinessDate);
    }

    /**
     * Returns the original transaction id.
     * 
     * @return TransactionIDIfc
     */
    public TransactionIDIfc getOriginalTransactionID()
    {
        return (originalTransactionID);
    }

    /**
     * Returns PLU item.
     * 
     * @return PLU item
     */
    public PLUItemIfc getPLUItem()
    {
        return (pluItem);
    }

    /**
     * Returns price.
     * 
     * @return price
     */
    public CurrencyIfc getPrice()
    {
        return (price);
    }

    /**
     * Get the number of items the customer originally purchaed.
     * 
     * @return Number of items purchased
     * @see oracle.retail.stores.domain.lineitem.ReturnItemIfc#getQuantityPurchased()
     */
    public BigDecimal getQuantityPurchased()
    {
        return this.quantityPurchased;
    }

    /**
     * Get the quantity of this item that may be returned
     * 
     * @return
     * @see oracle.retail.stores.domain.lineitem.ReturnItemIfc#getQuantityReturnable()
     */
    public BigDecimal getQuantityReturnable()
    {
        return this.quantityReturnable;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.ReturnItemIfc#getReason()
     */
    public LocalizedCodeIfc getReason()
    {
        return localizedReasonCode;
    }
    
    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.ReturnItemIfc#getItemCondition()
     */
    public LocalizedCodeIfc getItemCondition()
    {
        return localizedItemConditionCode;
    }

    /**
     * Returns reason code.
     * 
     * @return reason code
     * @deprecated as of 13.1 Use {@link ReturnItem#getReason().getCode()}
     */
    public int getReasonCode()
    {
        if (localizedReasonCode != null)
            return Integer.parseInt(localizedReasonCode.getCode());
        
        return 0;
    }

    /**
     * Returns reason code as String.
     * 
     * @return reason code
     * @deprecated as of 13.1 Use {@link #getReason()}
     */
    public String getReasonCodeAsString()
    {
        if (localizedReasonCode != null)
            return localizedReasonCode.getCode();
        
        return null;
    }

    /**
     * Retrieves the restocking fee.
     * 
     * @return restockingFee as CurrencyIfc
     */
    public CurrencyIfc getRestockingFee()
    {
        return (restockingFee);
    }

    /**
     * Returns the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

    /**
     * Returns sales associate.
     * 
     * @return sales associate
     */
    public EmployeeIfc getSalesAssociate()
    {
        return (salesAssociate);
    }

    /**
     * Retrieves the serial number.
     * 
     * @return serialNumber as String
     */
    public String getSerialNumber()
    {
        return (serialNumber);
    }

    public int getStartingSublineNumber()
    {
        return startingSublineNumber;
    }

    /**
     * Returns store reference.
     * 
     * @return store reference
     */
    public StoreIfc getStore()
    {
        return (store);
    }

    public int getSubLineNumber()
    {
        return subLineNumber;
    }

    /**
     * Returns tax rate.
     * 
     * @return tax rate
     */
    public double getTaxRate()
    {
        return (taxRate);
    }

    /**
     * Get original tender type code list
     */
    public HashMap<String, String> getTenderList()
    {
        return tenderList;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.ReturnItemIfc#hasOriginalTransactionID()
     */
    public boolean hasOriginalTransactionID()
    {
        return (getOriginalTransactionID() != null);
    }
    

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.ReturnItemIfc#hasNonRetrievedOriginalReceiptId()
     * @deprecated
     */
    public boolean hasNonRetrievedOriginalReceiptId()
    {
        return (getNonRetrievedOriginalReceiptId() != null && !getNonRetrievedOriginalReceiptId().isEmpty());
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.ReturnItemIfc#isNonRetrievedReceiptedTransaction()
     */
    public boolean isNonRetrievedReceiptedItem()
    {
        return haveReceipt && !fromRetrievedTransaction;
    }

    /*
     * (non-Javadoc)
     * @see
     * oracle.retail.stores.domain.lineitem.ReturnItemIfc#hasItemCondition()
     */
    public boolean hasItemCondition()
    {
        return !getItemCondition().getCode().equals(CodeConstantsIfc.CODE_UNDEFINED);

    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.ReturnItemIfc#haveReceipt()
     */
    public boolean haveReceipt()
    {
        return haveReceipt;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.ReturnItemIfc#isFromGiftReceipt()
     */
    public boolean isFromGiftReceipt()
    {
        return fromGiftReceipt;
    }

    public boolean isFromRetrievedTransaction()
    {
        return this.fromRetrievedTransaction;
    }

    public boolean isItemTaxRetrieved()
    {
        return isFromRetrievedTransaction() && getItemTax() != null
                && getItemTax().getTaxInformationContainer() != null;
    }

    /**
     * For purpose of return and price adjustment things like "original price"
     * are not part of what constitutes "equal". Created this instead.
     */
    public boolean matchingOriginalTransactions(ReturnItem rItem)
    {
        boolean retVal = false;

        if ((fromGiftReceipt == rItem.fromGiftReceipt) && (haveReceipt == rItem.haveReceipt)
                && (fromRetrievedTransaction == rItem.fromRetrievedTransaction)
                && (subLineNumber == rItem.subLineNumber)
                && (Util.isObjectEqual(originalTransactionID, rItem.getOriginalTransactionID()))
                && (Util.isObjectEqual(originalTransactionBusinessDate, rItem.getOriginalTransactionBusinessDate()))
                && (originalLineNumber == rItem.getOriginalLineNumber()))
        {
            retVal = true;
        }

        return retVal;

    }

    /**
     * Sets entry method.
     * 
     * @param value entry method
     */
    public void setEntryMethod(EntryMethod value)
    {
        entryMethod = value;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.ReturnItemIfc#setFromGiftReceipt(boolean)
     */
    public void setFromGiftReceipt(boolean value)
    {
        fromGiftReceipt = value;
    }

    public void setFromRetrievedTransaction(boolean fromRetrievedTransaction)
    {
        this.fromRetrievedTransaction = fromRetrievedTransaction;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.ReturnItemIfc#setHaveReceipt(boolean)
     */
    public void setHaveReceipt(boolean value)
    {
        haveReceipt = value;
    }

    /**
     * @param quantity The return quantity of this item
     */
    public void setItemQuantity(BigDecimal quantity)
    {
        this.quantity = quantity;
    }

    /**
     * ItemSize of this return item
     * 
     * @param itemSize The itemSize to set.
     */
    public void setItemSize(ItemSizeIfc itemSize)
    {
        this.itemSize = itemSize;
    }

    /**
     * For price adjustment, holds the item tax for the item with the lower
     * price. This is necessary because item taxes are calculated and set by the
     * transaction class during transaction totalling, not by the line item
     * during line item totalling.
     */
    public void setItemTax(ItemTaxIfc value)
    {
        itemTax = value;
    }

    /**
     * For price adjustment, holds the new lower price. This value is used to
     * create a temporary line item that represents the difference between the
     * old and new prices rather than 2 separate line items. Used in receipt and
     * EJ.
     */
    public void setNewLowerPrice(CurrencyIfc value)
    {
        newLowerPrice = value;
    }

    /**
     * Sets the original line item line number.
     * 
     * @param int line number
     */
    public void setOriginalLineNumber(int value)
    {
        originalLineNumber = value;
    }

    /**
     * Sets the original Transaction Business Date.
     * 
     * @param value original transaction business date
     */
    public void setOriginalTransactionBusinessDate(EYSDate value)
    {
        originalTransactionBusinessDate = value;
    }

    /**
     * Sets the original transaction id.
     * 
     * @param value reason code
     */
    public void setOriginalTransactionID(TransactionIDIfc value)
    {
        originalTransactionID = value;
    }

    /**
     * Sets PLU item.
     * 
     * @param value PLU item
     */
    public void setPLUItem(PLUItemIfc value)
    {
        pluItem = value;
    }

    /**
     * Sets price.
     * 
     * @param value price
     */
    public void setPrice(CurrencyIfc value)
    {
        price = value;
    }

    /**
     * Set the number of items the customer originally purchased
     * 
     * @param quantity Number of items purchased
     * @see oracle.retail.stores.domain.lineitem.ReturnItemIfc#setQuantityPurchased(com.ibm.math.BigDecimal)
     */
    public void setQuantityPurchased(BigDecimal quantity)
    {
        this.quantityPurchased = quantity;
    }

    /**
     * Get the quantity of this item that may be returned.
     * 
     * @param quantity
     * @see oracle.retail.stores.domain.lineitem.ReturnItemIfc#setQuantityReturnable(com.ibm.math.BigDecimal)
     */
    public void setQuantityReturnable(BigDecimal quantity)
    {
        this.quantityReturnable = quantity;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.ReturnItemIfc#setReason(oracle.retail.stores.common.utility.LocalizedCodeIfc)
     */
    public void setReason(LocalizedCodeIfc reason)
    {
        this.localizedReasonCode = reason;
    }
    
    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.ReturnItemIfc#setItemCondition(oracle.retail.stores.common.utility.LocalizedCodeIfc)
     */
    public void setItemCondition(LocalizedCodeIfc itemCondition)
    {
        this.localizedItemConditionCode = itemCondition;
    }

    /**
     * Sets reason code.
     * 
     * @param value reason code
     * @deprecated as of 13.1 Use {@link #setReason(LocalizedCodeIfc).setCode()}
     */
    public void setReasonCode(int value)
    {
        if (localizedReasonCode == null)
        {
            localizedReasonCode = DomainGateway.getFactory().getLocalizedCode();
        }
        localizedReasonCode.setCode(String.valueOf(value));
    }

    /**
     * Sets reason code as a string.
     * 
     * @param value reason code
     * @deprecated as of 13.1 Use {@link #setReason(LocalizedCodeIfc)}
     */
    public void setReasonCodeAsString(String value)
    {
        if (localizedReasonCode == null)
        {
            localizedReasonCode = DomainGateway.getFactory().getLocalizedCode();
        }
        localizedReasonCode.setCode(value);
    }

    /**
     * Sets the restocking fee.
     * 
     * @param value as CurrencyIfc
     */
    public void setRestockingFee(CurrencyIfc value)
    {
        restockingFee = value;
    }

    /**
     * Sets salesAssociate attribute.
     * 
     * @param emp salesAssociate
     */
    public void setSalesAssociate(EmployeeIfc emp)
    {
        salesAssociate = emp;
    }

    /**
     * Sets the serial number.
     * 
     * @param value as String
     */
    public void setSerialNumber(String value)
    {
        serialNumber = value;
    }

    public void setStartingSublineNumber(int num)
    {
        startingSublineNumber = num;
    }

    /**
     * Sets store reference.
     * 
     * @param value store reference
     */
    public void setStore(StoreIfc value)
    {
        store = value;
    }

    public void setSubLineNumber(int num)
    {
        subLineNumber = num;
    }

    /**
     * Sets tax rate.
     * 
     * @param value tax rate
     */
    public void setTaxRate(double value)
    {
        taxRate = value;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.ReturnItemIfc#setTenderList(java.util.List)
     */
    public void setTenderList(HashMap<String, String> tenderList)
    {
        this.tenderList = tenderList;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.ReturnItemIfc#getUserSuppliedTenderType()
     */
    public int getUserSuppliedTenderType()
    {
        return userSuppliedTenderType;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.ReturnItemIfc#setUserSuppliedTenderType(int)
     */
    public void setUserSuppliedTenderType(int userSuppliedTenderType)
    {
        this.userSuppliedTenderType = userSuppliedTenderType;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.ReturnItemIfc#getNonRetrievedOriginalReceiptId()
     */
    public String getNonRetrievedOriginalReceiptId()
    {
        return nonRetrievedOriginalReceiptId;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.ReturnItemIfc#setNonRetrievedOriginalReceiptId(java.lang.String)
     */
    public void setNonRetrievedOriginalReceiptId(
            String nonRetrievedOriginalReceiptId)
    {
        this.nonRetrievedOriginalReceiptId = nonRetrievedOriginalReceiptId;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        // build result string
        StringBuilder strResult = new StringBuilder("Class:  ReturnItem (Revision " + getRevisionNumber() + ") @"
                + hashCode());
        strResult.append("\n");

        // add attributes to string
        if (pluItem == null)
        {
            strResult.append("pluItem:                            [null]\n");
        }
        else
        {
            strResult.append(pluItem.toString());
        }
        if (price == null)
        {
            strResult.append("price:                              [null]\n");
        }
        else
        {
            strResult.append("price:                              [" + price + "]\n");
        }
        strResult.append("taxRate:                            [" + taxRate + "]\n");
        if (store == null)
        {
            strResult.append("store:                              [null]\n");
        }
        else
        {
            strResult.append(store.toString());
        }
        if (salesAssociate == null)
        {
            strResult.append("salesAssociate:                     [null]\n");
        }
        else
        {
            strResult.append(salesAssociate.toString());
        }
        if (originalTransactionID == null)
        {
            strResult.append("originalTransactionID:              [null]\n");
        }
        else
        {
            strResult.append("originalTransactionID:\n");
            strResult.append(originalTransactionID.toString());
        }
        if (originalTransactionBusinessDate == null)
        {
            strResult.append("originalTransactionBusinessDate:    [null]\n");
        }
        else
        {
            strResult.append("originalTransactionBusinessDate:\n");
            strResult.append(originalTransactionBusinessDate);
        }
        strResult.append("reason:                             [" + localizedReasonCode + "]\n");
        strResult.append("localizedItemConditionCode:         [" + localizedItemConditionCode + "]\n");
        strResult.append("restockingFee:                      [" + restockingFee + "]\n");
        strResult.append("originalLineNumber:                 [" + originalLineNumber + "]\n");
        strResult.append("entryMethod:                        [" + entryMethod + "]\n");
        strResult.append("userSuppliedTenderType              [" + userSuppliedTenderType + "]\n");
        strResult.append("nonRetrievedOriginalReceiptId       [" + nonRetrievedOriginalReceiptId + "]\n");
        strResult.append(Util.formatToStringEntry("fromGiftReceipt", fromGiftReceipt)).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("haveReceipt", haveReceipt)).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("fromRetrievedTransaction", fromRetrievedTransaction)).append(
                Util.EOL);
        strResult.append(Util.formatToStringEntry("subLineNumber", subLineNumber)).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("startingSublineNumber", startingSublineNumber)).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("newLowerPrice", newLowerPrice)).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("itemTax", itemTax)).append(Util.EOL);
        // pass back result
        return (strResult.toString());
    }

    /**
     * Sets attributes in clone.
     * 
     * @param newClass new instance of class
     */
    protected void setCloneAttributes(ReturnItemIfc newClass)
    {
        // if item not null, set it
        if (pluItem != null)
        {
            newClass.setPLUItem((PLUItemIfc)pluItem.clone());
        }
        // set tax rate
        newClass.setTaxRate(getTaxRate());
        newClass.setPrice((CurrencyIfc)price.clone());
        // if store not null, set it
        if (store != null)
        {
            newClass.setStore((StoreIfc)store.clone());
        }
        // if sales associate not null, set it
        if (salesAssociate != null)
        {
            newClass.setSalesAssociate((EmployeeIfc)salesAssociate.clone());
        }
        // set transaction ID
        if (originalTransactionID != null)
        {
            newClass.setOriginalTransactionID((TransactionIDIfc)originalTransactionID.clone());
        }
        if (originalTransactionBusinessDate != null)
        {
            newClass.setOriginalTransactionBusinessDate((EYSDate)originalTransactionBusinessDate.clone());
        }
        // set the restocking fee
        if (restockingFee != null)
        {
            newClass.setRestockingFee((CurrencyIfc)restockingFee.clone());
        }

        if (quantity != null)
        {
            newClass.setItemQuantity(quantity);
        }
        if (quantityPurchased != null)
        {
            newClass.setQuantityPurchased(quantityPurchased);
        }
        if (quantityReturnable != null)
        {
            newClass.setQuantityReturnable(quantityReturnable);
        }
        if (tenderList != null)
        {
            newClass.setTenderList(new HashMap<String, String>(tenderList));
        }

        newClass.setOriginalLineNumber(getOriginalLineNumber());
        newClass.setEntryMethod(getEntryMethod());
        newClass.setFromGiftReceipt(fromGiftReceipt);
        newClass.setHaveReceipt(haveReceipt);
        newClass.setFromRetrievedTransaction(fromRetrievedTransaction);
        newClass.setSubLineNumber(subLineNumber);
        newClass.setSerialNumber(serialNumber);
        newClass.setStartingSublineNumber(startingSublineNumber);
        newClass.setUserSuppliedTenderType(userSuppliedTenderType);
        newClass.setNonRetrievedOriginalReceiptId(nonRetrievedOriginalReceiptId);
        
        if (newLowerPrice != null)
            newClass.setNewLowerPrice((CurrencyIfc)newLowerPrice.clone());
        if (itemTax != null)
            newClass.setItemTax((ItemTax)itemTax.clone());
        if (localizedReasonCode != null)
        {
            newClass.setReason((LocalizedCodeIfc)localizedReasonCode.clone());
        }
        if (localizedItemConditionCode != null)
        {
            newClass.setItemCondition((LocalizedCodeIfc)localizedItemConditionCode.clone());
        }
    }

    
}
