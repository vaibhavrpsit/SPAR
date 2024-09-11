/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ReturnItemInfoBeanModel.java /main/19 2013/03/26 07:35:53 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     03/26/13 - Fixed issue with Refund Tenders for Non Retrieved
 *                         Transactions.
 *    jswan     11/15/12 - Modified to support parameter controlled return
 *                         tenders.
 *    rabhawsa  03/05/12 - RM i18n POS Returns changes
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    abhayg    08/26/10 - Serial Number needs to be displayed on the Return
 *                         Item Info screen For Serialized Item
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     06/01/10 - Checked in for refresh to latest lable.
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *   8    360Commerce 1.7         2/25/2008 12:56:25 AM  Manikandan Chellapan
 *        CR#30505 Service Alert Screens are not timing out
 *   7    360Commerce 1.6         4/25/2007 8:51:29 AM   Anda D. Cadar   I18N
 *        merge
 *   6    360Commerce 1.5         5/4/2006 5:11:52 PM    Brendan W. Farrell
 *        Remove inventory.
 *   5    360Commerce 1.4         4/27/2006 7:07:09 PM   Brett J. Larsen CR
 *        17307 - inventory functionality removal - stage 2
 *   4    360Commerce 1.3         1/22/2006 11:45:28 AM  Ron W. Haight
 *        removed references to com.ibm.math.BigDecimal
 *   3    360Commerce 1.2         3/31/2005 4:29:44 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:24:51 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:13:53 PM  Robert Pearse   
 *
 *  Revision 1.13.2.1  2004/11/03 22:19:42  mweis
 *  @scr 7012 Use proper Inventory constants for default values.
 *
 *  Revision 1.13  2004/10/05 20:48:36  cdb
 *  @scr 7246 Removed references to location id and item id in inventory state. Modified returns
 *  so that selection is first in list when default location or state is invalid.
 *
 *  Revision 1.12  2004/08/27 20:27:29  mweis
 *  @scr 7012 First iteration on Inventory w.r.t. Returns.
 *
 *  Revision 1.11  2004/08/26 16:23:01  mweis
 *  @scr 7012 Clump all "Inventory" related params into a hidden group.  Make necessary code changes to honor this new group.
 *
 *  Revision 1.10  2004/07/28 17:09:59  bvanschyndel
 *  @scr 6568 Moved inventory state DB query from the Bean to the Site for returns
 *
 *  Revision 1.9  2004/07/28 16:59:15  bvanschyndel
 *  @scr 0 Moved inventory state DB query from the Bean to the Site for returns
 *
 *  Revision 1.8  2004/06/29 22:03:30  aachinfiev
 *  Merge the changes for inventory & POS integration
 *
 *  Revision 1.7  2004/06/02 21:55:59  mweis
 *  @scr 3098 Returns of a non-UOM item allows a fractional (decimal) quantity.
 *
 *  Revision 1.6  2004/05/13 19:38:40  jdeleau
 *  @scr 4862 Support timeout for all screens in the return item flow.
 *
 *  Revision 1.5  2004/04/09 22:09:58  mweis
 *  @scr 4206 JavaDoc updates.
 *
 *  Revision 1.4  2004/03/16 17:15:18  build
 *  Forcing head revision
 *
 *  Revision 1.3  2004/03/10 20:50:14  epd
 *  @scr 3561 Item size now just displays as label if item from retrieved transaction
 *
 *  Revision 1.2  2004/02/11 20:56:27  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Dec 17 2003 11:23:00   baa
 * return enhancements
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 * 
 *    Rev 1.0   Aug 29 2003 16:11:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:57:16   msg
 * Initial revision.
 * 
 *    Rev 1.1   25 Apr 2002 18:52:34   pdd
 * Removed unnecessary BigDecimal instantiations.
 * Resolution for POS SCR-1610: Remove inefficient instantiations of BigDecimal
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.pos.ui.timer.TimerModelIfc;

/**
 * This class is used with the ReturnItemInfoBean class.
 * 
 * @version $Revision: /main/19 $
 */
public class ReturnItemInfoBeanModel extends ReasonBeanModel
{
    /** serialVersionUID */
    private static final long serialVersionUID = -103924057026340870L;

    /** Item number object */
    protected String itemNumber = new String();

    /** Item Description object */
    protected String itemDescription = new String();

    /** Restocking fee flag **/
    protected String restockingFee = "N";

    /** Gift card serial number. **/
    protected String giftCardSerialNumber = null;

    /** Gift card balance. **/
    protected BigDecimal giftCardBalance = BigDecimal.ZERO;

    /** UnitOfMeasure flag; default: "Units" */
    protected boolean isUOM = false;

    /** Unit price */
    protected CurrencyIfc price = null;

    /** Indicates if the price data field should be enabled */
    protected boolean priceEnabled = true;

    /** Store Number where the item was purchased */
    protected String storeNumber = new String();

    /** Indicates if the store number data field should be enabled */
    protected boolean storeNumberEnabled = true;

    /** Sales Associate that sold the item */
    protected String salesAssociate = new String();

    /** Indicates if the sales associate data field should be enabled */
    protected boolean salesAssociateEnabled = true;

    /** Receipt or web number */
    protected String receiptNumber = new String();

    /** Indicates if the receipt number data field should be enabled */
    protected boolean receiptNumberEnabled = true;

    /** UnitOfMeasure object */
    protected String unitOfMeasure = "(none)";

    /** Indicates if the unit of measure data field should be enabled */
    protected boolean unitOfMeasureEnabled = true;

    /** Quantity */
    protected BigDecimal quantity = BigDecimalConstants.ONE_AMOUNT;

    /** Indicates if the quantity field should be enabled or not */
    protected boolean quantityEnabled = true;

    /** Item Size **/
    protected String itemSize = null;

    /** isSizeRequired Flag **/
    protected boolean itemSizeRequired = false;

    /** Indicates if the item size field should be enabled or not */
    protected boolean itemSizeEnabled = true;

    /**
     * TimerModel used for automatic logoff after a timeout
     */
    protected TimerModelIfc timerModel = null;

    /**
     * ItemConditionModel for Item Condition 
     */
    protected ItemConditionModel itemConditionModel = new ItemConditionModel();
    
    /** Serial number **/
    protected String serialNumber = null;

    /** This boolean indicates if the serial number field is required **/
    protected boolean serialNumberRequired = false;

    /** Indicates if the serialNumber field should be enabled or not */
    protected boolean serialNumberEnabled = true;

    /**
     * This array of tender descriptors provides a selection list for the
     * originalTransactionTenderComboBox
     */
    protected String[] tenderDescriptors = null;
    
    /**
     * Index of selected tender code.
     */
    protected int tenderSelectedIndex = -1;
    
    /**
     * Gets the itemDescription property (java.lang.String) value.
     * 
     * @return The itemDescription property value.
     * @see #setItemDescription(String)
     */
    public String getItemDescription()
    {
        return itemDescription;
    }

    /**
     * Gets the itemNumber property (java.lang.String) value.
     * 
     * @return The itemNumber property value.
     * @see #setItemNumber(String)
     */
    public String getItemNumber()
    {
        return itemNumber;
    }

    /**
     * Gets the price property (CurrencyIfc) value.
     * 
     * @return The price property value.
     * @see #setPrice(CurrencyIfc)
     */
    public CurrencyIfc getPrice()
    {
        return price;
    }

    /**
     * Gets the receiptNumber property (java.lang.String) value.
     * 
     * @return The receiptNumber property value.
     * @see #setReceiptNumber(String)
     */
    public String getReceiptNumber()
    {
        return receiptNumber;
    }

    /**
     * Gets the salesAssociate property (java.lang.String) value.
     * 
     * @return The salesAssociate property value.
     * @see #setSalesAssociate(String)
     */
    public String getSalesAssociate()
    {
        return salesAssociate;
    }

    /**
     * Gets the storeNumber property (java.lang.String) value.
     * 
     * @return The storeNumber property value.
     * @see #setStoreNumber(String)
     */
    public String getStoreNumber()
    {
        return storeNumber;
    }

    /**
     * Sets the itemDescription property (java.lang.String) value.
     * 
     * @param value The new value for the property.
     * @see #getItemDescription()
     */
    public void setItemDescription(String value)
    {
        itemDescription = value;
    }

    /**
     * Sets the itemNumber property (java.lang.String) value.
     * 
     * @param value The new value for the property.
     * @see #getItemNumber()
     */
    public void setItemNumber(String value)
    {
        itemNumber = value;
    }

    /**
     * Sets the price property CurrencyIfc value.
     * 
     * @param value The new value for the property.
     * @see #getPrice()
     */
    public void setPrice(CurrencyIfc value)
    {
        price = value;
    }

    /**
     * Sets the receiptNumber property (java.lang.String) value.
     * 
     * @param value The new value for the property.
     * @see #getReceiptNumber()
     */
    public void setReceiptNumber(String value)
    {
        receiptNumber = value;
    }

    /**
     * Sets the salesAssociate property (java.lang.String) value.
     * 
     * @param value The new value for the property.
     * @see #getSalesAssociate()
     */
    public void setSalesAssociate(String value)
    {
        salesAssociate = value;
    }

    /**
     * Sets the storeNumber property (java.lang.String) value.
     * 
     * @param value The new value for the property.
     * @see #getStoreNumber()
     */
    public void setStoreNumber(String value)
    {
        storeNumber = value;
    }

    /**
     * Returns whether this item is a unit of measure (allows fractional
     * pieces). Most things in this world are not.
     * 
     * @return Whether this item is a unit of measure.
     * @see #getUnitOfMeasure()
     */
    public boolean isUOM()
    {
        return isUOM;
    }

    /**
     * Sets whether this item is a unit of measure (allows fractional pieces).
     * 
     * @param isUOM whether this item is a unit of measure.
     */
    public void setUOM(boolean isUOM)
    {
        this.isUOM = isUOM;
    }

    /**
     * Retrieves unitOfMeasure text.
     * 
     * @return The text describing what sort of unit of measure this item is.
     * @see #isUOM()
     */
    public String getUnitOfMeasure()
    {
        return (unitOfMeasure);
    }

    /**
     * Sets unit of measure text.
     * 
     * @param value The new unit of measure text.
     */
    public void setUnitOfMeasure(String value)
    {
        unitOfMeasure = value;
    }

    /**
     * Retrieves item quantity.
     * 
     * @return long quantity
     */
    public BigDecimal getQuantity()
    {
        // If we have a unit of measure item, flesh its quantity to 2 decimal
        // places.
        if (isUOM())
        {
            int scale = quantity.scale(); // how many decimal digits there are.
            if (scale == 0 || scale == 1)
            {
                quantity = quantity.multiply(BigDecimalConstants.ONE_AMOUNT); // "1.00"
            }
        }

        return (quantity);
    }

    /**
     * Sets item quantity.
     * 
     * @param value The new item's quantity.
     */
    public void setQuantity(BigDecimal value)
    {
        quantity = value;
    }

    /**
     * Retrieves EnableOnlyQuantityReason.
     * 
     * @return boolean
     */
    public boolean getEnableOnlyQuantityReason()
    {
        return quantityEnabled && !priceEnabled && !storeNumberEnabled && !salesAssociateEnabled
                && !receiptNumberEnabled && !unitOfMeasureEnabled && !itemSizeEnabled && !serialNumberEnabled;
    }

    /**
     * Sets EnableOnlyQuantityReason.
     * 
     * @param value Whether this property is <code>true</code> or
     *            <code>false</code>.
     */
    public void setEnableOnlyQuantityReason(boolean value)
    {
        if (value)
        {
            quantityEnabled = value;
            priceEnabled = !value;
            storeNumberEnabled = !value;
            salesAssociateEnabled = !value;
            receiptNumberEnabled = !value;
            unitOfMeasureEnabled = !value;
            itemSizeEnabled = !value;
            serialNumberEnabled = !value;
        }
    }

    // ***************************************************************************
    // Gift Card related methods

    /**
     * Gets the giftCardSerialNumber property (java.lang.String) value.
     * 
     * @return The giftCardSerialNumber property value.
     * @see #setGiftCardSerialNumber(String)
     */
    public String getGiftCardSerialNumber()
    {
        return giftCardSerialNumber;
    }

    /**
     * Gets the gift card balance.
     * 
     * @return the gift card balance
     */
    public BigDecimal getGiftCardBalance()
    {
        return giftCardBalance;
    }

    /**
     * Sets GiftCardSerialNumber.
     * 
     * @param serialNumber The serial number.
     */
    public void setGiftCardSerialNumber(String serialNumber)
    {
        giftCardSerialNumber = serialNumber;
    }

    /**
     * Sets the gift card balance.
     * 
     * @param balance The gift card balance.
     */
    public void setGiftCardBalance(BigDecimal balance)
    {
        this.giftCardBalance = balance;
    }

    /**
     * Returns the item size
     * 
     * @return String the size
     */
    public String getItemSize()
    {
        return itemSize;
    }

    /**
     * Sets the item size
     * 
     * @param value the size
     */
    public void setItemSize(String value)
    {
        itemSize = value;
    }

    /**
     * Gets the item size required flag
     * 
     * @return boolean the flag
     */
    public boolean isItemSizeRequired()
    {
        return itemSizeRequired;
    }

    /**
     * Sets the item size required flag
     * 
     * @param b the flag
     */
    public void setItemSizeRequired(boolean b)
    {
        itemSizeRequired = b;
    }

    /**
     * Retrieves the restocking fee.
     * 
     * @return restockingFee as String
     */
    public String getRestockingFee()
    {
        return (restockingFee);
    }

    /**
     * Sets the restocking fee.
     * 
     * @param value as String
     */
    public void setRestockingFee(String value)
    {
        restockingFee = value;
    }

    /**
     * @return Returns the priceEnabled.
     */
    public boolean isPriceEnabled()
    {
        if (giftCardSerialNumber != null)
        {
            return false;
        }

        return priceEnabled;
    }

    /**
     * @param priceEnabled The priceEnabled to set.
     */
    public void setPriceEnabled(boolean priceEnabled)
    {
        this.priceEnabled = priceEnabled;
    }

    /**
     * @return Returns the storeNumberEnabled.
     */
    public boolean isStoreNumberEnabled()
    {
        return storeNumberEnabled;
    }

    /**
     * @param storeNumberEnabled The storeNumberEnabled to set.
     */
    public void setStoreNumberEnabled(boolean storeNumberEnabled)
    {
        this.storeNumberEnabled = storeNumberEnabled;
    }

    /**
     * @return Returns the salesAssociateEnabled.
     */
    public boolean isSalesAssociateEnabled()
    {
        return salesAssociateEnabled;
    }

    /**
     * @param salesAssociateEnabled The salesAssociateEnabled to set.
     */
    public void setSalesAssociateEnabled(boolean salesAssociateEnabled)
    {
        this.salesAssociateEnabled = salesAssociateEnabled;
    }

    /**
     * @return Returns the receiptNumberEnabled.
     */
    public boolean isReceiptNumberEnabled()
    {
        return receiptNumberEnabled;
    }

    /**
     * @param receiptNumberEnabled The receiptNumberEnabled to set.
     */
    public void setReceiptNumberEnabled(boolean receiptNumberEnabled)
    {
        this.receiptNumberEnabled = receiptNumberEnabled;
    }

    /**
     * @return Returns the unitOfMeasureEnabled.
     */
    public boolean isUnitOfMeasureEnabled()
    {
        return unitOfMeasureEnabled;
    }

    /**
     * @param unitOfMeasureEnabled The unitOfMeasureEnabled to set.
     */
    public void setUnitOfMeasureEnabled(boolean unitOfMeasureEnabled)
    {
        this.unitOfMeasureEnabled = unitOfMeasureEnabled;
    }

    /**
     * @return Returns the quantityEnabled.
     */
    public boolean isQuantityEnabled()
    {
        if (giftCardSerialNumber != null)
        {
            return false;
        }

        return quantityEnabled;
    }

    /**
     * @param quantityEnabled The quantityEnabled to set.
     */
    public void setQuantityEnabled(boolean quantityEnabled)
    {
        this.quantityEnabled = quantityEnabled;
    }

    /**
     * @return Returns the itemSizeEnabled.
     */
    public boolean isItemSizeEnabled()
    {
        return itemSizeEnabled;
    }

    /**
     * @param itemSizeEnabled The itemSizeEnabled to set.
     */
    public void setItemSizeEnabled(boolean itemSizeEnabled)
    {
        this.itemSizeEnabled = itemSizeEnabled;
    }

    /**
     * @return Returns the serialNumberEnabled.
     */
    public boolean isSerialNumberEnabled()
    {
        return serialNumberEnabled;
    }

    /**
     * @param serialNumberEnabled The serialNumberEnabled to set.
     */
    public void setSerialNumberEnabled(boolean serialNumberEnabled)
    {
        this.serialNumberEnabled = serialNumberEnabled;
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        // result string
        String strResult = new String("Class:  GetReturnItemInformationAisle ");

        strResult += "\n";

        // add attributes to string
        strResult += "itemNumber:                                 [" + itemNumber + "]\n";
        strResult += "itemDescription:                            [" + itemDescription + "]\n";
        strResult += "price:                                      [" + price + "]\n";
        strResult += "storeNumber:                                [" + storeNumber + "]\n";
        strResult += "salesAssociate:                             [" + salesAssociate + "]\n";
        strResult += "receiptNumber:                              [" + receiptNumber + "]\n";
        strResult += "unitOfMeasure:                              [" + unitOfMeasure + "]\n";
        strResult += "quantity:                                   [" + quantity + "]\n";
        strResult += "restockingFee:                              [" + restockingFee + "]\n";
        strResult += "serialNumber:                               [" + serialNumber + "]\n";
        strResult += "serialNumberRequired:                       [" + serialNumberRequired + "]\n";

        // pass back result
        return (strResult);
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

    /**
     * Sets the serial number.
     * 
     * @param value as String
     */
    public void setSerialNumber(String value)
    {
        serialNumber = value;
    }

    /**
     * Retrieves serialNumberRequired.
     * 
     * @return serialNumberRequired as boolean
     */
    public boolean getSerialNumberRequired()
    {
        return (serialNumberRequired);
    }

    /**
     * Sets serialNumberRequired.
     * 
     * @param value as boolean
     */
    public void setSerialNumberRequired(boolean value)
    {
        serialNumberRequired = value;
    }
    /**
     * Gets the Item Condition Model
     * @return the itemConditionModel
     */
    public ItemConditionModel getItemConditionModel()
    {
        return itemConditionModel;
    }

    /**
     * Sets the Item Condition Model
     * @param itemConditionModel the itemConditionModel to set
     */
    public void setItemConditionModel(ItemConditionModel itemConditionModel)
    {
        this.itemConditionModel = itemConditionModel;
    }

    /**
     * @return the tenderCodes
     */
    public String[] getTenderDescriptors()
    {
        return tenderDescriptors;
    }

    /**
     * @param tenderCodes the tenderCodes to set
     */
    public void setTenderDescriptors(String[] tenderDescriptors)
    {
        this.tenderDescriptors = tenderDescriptors;
    }

    /**
     * @return the tenderSelectedIndex
     */
    public int getTenderSelectedIndex()
    {
        return tenderSelectedIndex;
    }

    /**
     * @param tenderSelectedIndex the tenderSelectedIndex to set
     */
    public void setTenderSelectedIndex(int tenderSelectedIndex)
    {
        this.tenderSelectedIndex = tenderSelectedIndex;
    }

    /**
     * @return Returns the isItemFromRetrievedTransaction.
     * @deprecated in 13.3
     */
    /*
     * TODO remove comment public boolean isItemFromRetrievedTransaction() {
     * return isItemFromRetrievedTransaction; }
     */
    /**
     * @param isItemFromRetrievedTransaction The isItemFromRetrievedTransaction
     *            to set.
     * @deprecated in 13.3
     */
    /*
     * TODO remove comment public void setItemFromRetrievedTransaction(boolean
     * isItemFromRetrievedTransaction) { this.isItemFromRetrievedTransaction =
     * isItemFromRetrievedTransaction; }
     */
}
