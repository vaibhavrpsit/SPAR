/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncommon/AbstractReturnItemCargo.java /main/17 2012/03/14 00:09:15 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  03/05/12 - RM i18n POS return changes added localized item
 *                         condition codes
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    jswan     07/07/10 - Code review changes and fixes for Cancel button in
 *                         External Order integration.
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    sgu       06/08/10 - add item # & desc to the screen prompt. fix unknow
 *                         item screen to disable price and quantity for
 *                         external item
 *    cgreene   05/26/10 - convert to oracle packaging
 *    jswan     05/14/10 - ExternalOrder mods checkin for refresh to tip.
 *    jswan     05/12/10 - Modify cargos for external order items return.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  5    360Commerce 1.4         5/27/2008 7:37:28 PM   Anil Rathore    Updated
 *        to display ITEM_NOT_FOUND dialog. Changes reviewed by Dan.
 *  4    360Commerce 1.3         4/25/2007 8:52:15 AM   Anda D. Cadar   I18N
 *       merge
 *
 *  3    360Commerce 1.2         3/31/2005 4:27:07 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:19:27 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:09:20 PM  Robert Pearse
 * $
 * Revision 1.10  2004/03/26 05:39:05  baa
 * @scr 3561 Returns - modify flow to support entering price code for not found gift receipt
 *
 * Revision 1.9  2004/03/15 15:16:51  baa
 * @scr 3561 refactor/clean item size code, search by tender changes
 *
 * Revision 1.8  2004/02/23 13:54:52  baa
 * @scr 3561 Return Enhancements to support item size
 *
 * Revision 1.7  2004/02/18 20:36:20  baa
 * @scr 3561 Returns changes to support size
 *
 * Revision 1.6  2004/02/17 20:40:28  baa
 * @scr 3561 returns
 * Revision 1.5 2004/02/13 14:02:48 baa @scr 3561 returns
 * enhancements
 *
 * Revision 1.4 2004/02/12 20:41:40 baa @scr 0 fixjavadoc
 *
 * Revision 1.3 2004/02/12 16:51:45 mcs Forcing head revision
 *
 * Revision 1.2 2004/02/11 21:52:30 rhafernik @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1 2004/02/11 01:04:20 cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.3 Jan 23 2004 16:10:04 baa continue returns developement
 *
 * Rev 1.2 Jan 07 2004 10:49:34 rsachdeva getAccessFunctionID removed - as ReturnOptionsCargo now
 * extends ReturnItemCargo Resolution for POS SCR-3626: Manager Override ARG for Return buttons
 * states Override Restocking Fee.
 *
 * Rev 1.1 Dec 17 2003 11:20:00 baa return enhancements Resolution for 3561: Feature Enhacement:
 * Return Search by Tender
 *
 * Rev 1.0 Aug 29 2003 16:05:44 CSchellenger Initial revision.
 *
 * Rev 1.3 Jul 01 2003 11:26:10 baa apply security check when declining restocking fee Resolution
 * for 2745: No override restocking fee access, user still can decline restoking fee
 *
 * Rev 1.2 Feb 16 2003 10:43:30 mpm Merged 5.1 changes. Resolution for POS SCR-2053: Merge 5.1
 * changes into 6.0
 *
 * Rev 1.1 Jan 13 2003 15:04:42 RSachdeva Replaced AbstractFinancialCargo.getCodeListMap() by
 * UtilityManagerIfc.getCodeListMap() Resolution for POS SCR-1907: Remove deprecated calls to
 * AbstractFinancialCargo.getCodeListMap()
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncommon;

import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.common.ItemSizeCargoIfc;
import oracle.retail.stores.pos.ui.beans.ReturnItemInfoBeanModel;

/**
 * This class provides some of the functionality required by the ReturnItemCargoIfc.
 * 
 * @version $Revision: /main/17 $
 */
abstract public class AbstractReturnItemCargo
    extends AbstractFindTransactionCargo
    implements ReturnItemCargoIfc,ItemSizeCargoIfc, ReturnExternalOrderItemsCargoIfc
{
    /** serialVersionUID */
    private static final long serialVersionUID = 6479070912615011277L;

    /**
     * default employee for the transaction - set in Launch Shuttle
     */
    protected EmployeeIfc salesAssociate;

    /**
     * Sales associate ID for the lookup
     */
    protected String salesAssociateID;

    /**
     * Department name for unknown items
     */
    protected String departmentName;

    /**
     * The error code generated by the attempt to lookup a item
     */
    protected int dataExceptionErrorCode;

    /**
     * item serial number.
     */
    protected String itemSerial = null;
    /**
     * Model containing return item info for kit headers This is necessary because in release 5.0,
     * kit header items are not processed through the UI. Even though the a return kit header is
     * not visible to the user, it must be maintained with the transaction to properly track
     * inventory.
     */
    protected ReturnItemInfoBeanModel returnItemInfo = null;
    /**
     * this flag is set when the validation failed in Return Item Info screen
     */
    protected boolean validationFailed = false;
    /**
     * This flag indicates whether the item that was entered was scanned or typed.
     */
    protected boolean itemScanned = false;

    /**
     * Captures item size if required
     */
    protected String sizeCode;

    /**
     * maximum item number length.
     */
    protected int maxPLUItemIDLength = Integer.MAX_VALUE;

    /**
     * The Code List for CODE_LIST_RETURN_REASON_CODES
     */
    protected CodeListIfc localizedReasonCodes = null;
    
    /**
     * The code List for the Item Condition
     */
    protected CodeListIfc localizedItemConditionCodes = null;

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
     * @param itemNumber The maximum item number length
     */
    public void setMaxPLUItemIDLength(int maxPLUItemIDLength)
    {
        this.maxPLUItemIDLength = maxPLUItemIDLength;
    }

    /**
     * Class Constructor.
     * <p>
     * Initializes the reason code list for item returns.
     */
    public AbstractReturnItemCargo()
    {
    }

    /**
     * Method to retrieve the localizedReasonCodes
     * 
     * @return the localizedReasonCodes
     */
    public CodeListIfc getLocalizedReasonCodes()
    {
        return localizedReasonCodes;
    }

    /**
     * Method to retrieve the localizedItemConditionCodes
     * 
     * @return the localizedItemConditionCodes
     */
    public CodeListIfc getLocalizedItemConditionCodes()
    {
        return localizedItemConditionCodes;
    }
    
    /**
     * Method sets the localizedReasonCodes
     * 
     * @param localizedReasonCode the localizedReasonCode to set
     */
    public void setLocalizedReasonCodes(CodeListIfc localizedReasonCodes)
    {
        this.localizedReasonCodes = localizedReasonCodes;
    }

    /**
     * Method sets the localizedItemConditionCodes
     * 
     * @param localizedItemConditionCode the localizedItemConditionCode to set
     */
    public void setLocalizedItemConditionCodes(CodeListIfc localizedItemConditionCodes)
    {
        this.localizedItemConditionCodes = localizedItemConditionCodes;
    }
    
    /**
     * Sets the salesAssociate.
     * 
     * @param value the employee.
     */
    public void setSalesAssociate(EmployeeIfc value)
    {
        salesAssociate = value;
    }

    /**
     * Gets the salesAssociate.
     * 
     * @return the employee.
     */
    public EmployeeIfc getSalesAssociate()
    {
        return (salesAssociate);
    }

    /**
     * Sets the Sales Associate ID.
     * 
     * @param value the Sales Associate ID.
     */
    public void setSalesAssociateID(String value)
    {
        salesAssociateID = value;
    }

    /**
     * Gets the Sales Associate ID.
     * 
     * @return the Sales Associate ID.
     */
    public String getSalesAssociateID()
    {
        return (salesAssociateID);
    }

    /**
     * Sets the department name.
     * 
     * @param value the department name.
     */
    public void setDepartmentName(String value)
    {
        departmentName = value;
    }

    /**
     * Gets the department name.
     * 
     * @return the department name.
     */
    public String getDepartmentName()
    {
        return (departmentName);
    }

    /**
     * No clean up required.
     * <P>
     */
    public void completeItemNotFound()
    {
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
     * @param value integer value
     */
    public void setDataExceptionErrorCode(int value)
    {
        dataExceptionErrorCode = value;
    }

    /**
     * Gets the sales associate for the item selected.
     * 
     * @return EmployeeIfc
     */
    public EmployeeIfc getSaleLineItemSalesAssociate()
    {
        return null;
    }

    /**
     * Gets the PLUItem from the return array.
     * 
     * @return PLUItem.
     */
    public PLUItemIfc getReturnPLUItem()
    {
        return null;
    }

    /**
     * Returns the price.
     * 
     * @return CurrencyIfc
     */
    public abstract CurrencyIfc getPrice();

    /**
     * Sets the price.
     * 
     * @param price of the return item
     */
    public abstract void setPrice(CurrencyIfc price);

    /**
     * Gets the ItemTax object for the return item.
     * 
     * @return ItemTax.
     */
    public ItemTaxIfc getItemTax()
    {
        return null; // external tax mgr
    }

    /**
     * Retrieves item serial number.
     * 
     * @return String
     */
    public String getItemSerial()
    {
        return itemSerial;
    }

    /**
     * Sets the item serial number.
     * 
     * @param sn serial number
     */
    public void setItemSerial(String sn)
    {
        itemSerial = sn;
    }

    /**
     * Sets the return item info for a kit header.
     * 
     * @param value returnItemInfoBean model
     */
    public void setReturnItemInfo(ReturnItemInfoBeanModel value)
    {
        returnItemInfo = value;
    }

    /**
     * Returns return item info for a kit header.
     * 
     * @return ReturnItemInfoBeanModel
     */
    public ReturnItemInfoBeanModel getReturnItemInfo()
    {
        return returnItemInfo;
    }

    /**
     * Sets the validation failed flag.
     * 
     * @param value the validation failed flag.
     */
    public void setValidationFailed(boolean value)
    {
        validationFailed = value;
    }

    /**
     * Gets the validation failed flag.
     * 
     * @return the validation failed flag.
     */
    public boolean getValidationFailed()
    {
        return (validationFailed);
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
     * @return Returns the itemSize.
     */
    public String getItemSizeCode()
    {
        return sizeCode;
    }

    /**
     * @param code The itemSize to set.
     */
    public void setItemSizeCode(String code)
    {
        sizeCode = code;
    }

    /**
     * Returns the external item price.
     * 
     * @return the CurrencyIfc value
     */
    public CurrencyIfc getItemPrice()
    {
        CurrencyIfc itemPrice = null;
        if (getPLUItem() != null)
        {
            itemPrice = getPLUItem().getPrice(new EYSDate(), -1);
        }
        else if (getCurrentExternalOrderItemReturnStatusElement() != null)
        {
            itemPrice = getCurrentExternalOrderItemReturnStatusElement().getExternalOrderItem().getPrice();
        }

        return itemPrice;
    }

    /**
     * Return the item description
     * 
     * @return the String value
     */
    public String getItemDescription()
    {
        String description = null;
        if (getPLUItem() != null)
        {
            description = getPLUItem().getDescription(LocaleMap.getLocale(LocaleMap.DEFAULT));
        }
        else if (getCurrentExternalOrderItemReturnStatusElement() != null)
        {
            description = getCurrentExternalOrderItemReturnStatusElement().getExternalOrderItem().getDescription();
        }

        return description;
    }

    /**
     * Gets the item quantity for the current item.
     * 
     * @return the item quantity
     */
    public BigDecimal getItemQuantity()
    {
        BigDecimal quantity = null;
        if (getReturnItem() != null)
        {
            quantity = getReturnItem().getItemQuantity();
        }
        else if (getCurrentExternalOrderItemReturnStatusElement() != null)
        {
            quantity = getCurrentExternalOrderItemReturnStatusElement().getExternalOrderItem().getQuantity();
        }

        return quantity;
    }

}
