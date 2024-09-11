/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ItemNotFoundPriceCodeBeanModel.java /main/16 2011/12/05 12:16:30 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    sgu       06/08/10 - enhance ItemNotFoundPriceCodeBean to display
 *                         external order quantity and description
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    1    360Commerce 1.0         7/28/2006 5:34:16 PM   Brett J. Larsen
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.math.BigDecimal;

import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This bean model is used by ItemNotFoundPriceCode bean.
 * 
 * @version $Revision: /main/16 $
 */
public class ItemNotFoundPriceCodeBeanModel extends ItemInfoBeanModel
{
    private static final long serialVersionUID = -8956279949123597177L;

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/16 $";

    /**
     * This member holds the department identifier.
     */
    protected String departmentName = "";

    /**
     * This member indicates if the item is taxable.
     */
    protected boolean taxable = true;

    /**
     * This member holds the unit of measure.
     */
    protected String unitOfMeasure = new String("None");

    /**
     * This member holds the unit of measure selected value.
     */
    protected String UOMSelectedValue = null;

    /**
     * This member holds the unit of measure default value.
     */
    protected String UOMDefaultValue = "None";

    /**
     * This member contains the item quantity.
     */
    protected BigDecimal quantity = BigDecimalConstants.ONE_AMOUNT;

    /**
     * This member contains the default item quantity.
     */
    protected BigDecimal defaultQuantity = BigDecimalConstants.ONE_AMOUNT;

    /**
     * This member contains the flag indicating if the item quantity can be
     * modified.
     */
    protected boolean quantityModifiable = true;

    /**
     * This member contains the default item quantity.
     */
    protected String defaultUOM = "units";

    /**
     * This member contains the reason the item was not found.
     */
    protected String reason = "";

    /**
     * This member holds the unit of measure array.
     */
    protected String[] unitOfMeasureStrings = new String[1];

    /**
     * This member holds the department array.
     */
    protected String[] departmentStrings = new String[1];

    /**
     * This item serial number.
     */
    protected String itemSerial = null;

    /**
     * This item manufacturer.
     */
    protected String manufacturer = null;

    /**
     * This member holds PriceCode.
     */
    String priceCodeField = null;

    /**
     * This member contains the default item description
     */
    protected String defaultItemDescription = "";

    /**
     * Returns the item serial number.
     * 
     * @return The item serial number.
     */
    public String getItemSerial()
    {
        return (itemSerial);
    }

    /**
     * Sets the item serial number.
     * 
     * @param value the item serial number.
     */
    public void setItemSerial(String value)
    {
        itemSerial = value;
    }

    /**
     * Returns the department name.
     * 
     * @return The department name.
     */
    public String getDepartmentName()
    {
        return (departmentName);
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
     * Retrieves This member indicates if the item is taxable..
     * 
     * @return This member indicates if the item is taxable.
     */
    public boolean getTaxable()
    {
        return (taxable);
    }

    /**
     * Sets This member indicates if the item is taxable..
     * 
     * @param value This member indicates if the item is taxable.
     */
    public void setTaxable(boolean value)
    {
        taxable = value;
    }

    /**
     * Gets the item description.
     * 
     * @return the item description
     */
    public String getUnitOfMeasure()
    {
        return unitOfMeasure;
    }

    /**
     * Sets the item description.
     * 
     * @param number the item description
     */
    public void setUnitOfMeasure(String unit)
    {
        unitOfMeasure = unit;
    }

    /**
     * Returns the default state for this combo model.
     * 
     * @return java.lang.String
     */
    public String getDefaultValue()
    {
        return UOMDefaultValue;
    }

    /**
     * Sets the default state for this combo model.
     * 
     * @param String Default Value
     */
    public void setDefaultValue(String defVal)
    {
        UOMDefaultValue = defVal;
    }

    /**
     * Returns the selected item.
     * 
     * @return java.lang.Object
     */
    public Object getSelectedItem()
    {
        return UOMSelectedValue;
    }

    /**
     * Sets the selected item.
     * 
     * @param item java.lang.Object
     */
    public void setSelectedItem(Object item)
    {
    }

    /**
     * Retrieves This member contains the item quantity..
     * 
     * @return This member contains the item quantity.
     */
    public BigDecimal getQuantity()
    {
        if (quantity.scale() == 0)
        {
            quantity = quantity.multiply(BigDecimalConstants.ONE_AMOUNT);
        }
        else if (quantity.scale() == 1)
        {
            quantity = quantity.multiply(BigDecimalConstants.ONE_AMOUNT);
        }

        return (quantity);
    }

    /**
     * Sets This member contains the item quantity..
     * 
     * @param value This member contains the item quantity.
     */
    public void setQuantity(BigDecimal value)
    {
        quantity = value;
    }

    /**
     * Get the default quantity
     * 
     * @return BigDecimal default quantity
     */
    public BigDecimal getDefaultQuantity()
    {
        return this.defaultQuantity;
    }

    /**
     * Sets default quantity
     * 
     * @param defaultQuantity default quantity
     */
    public void setDefaultQuantity(BigDecimal defaultQuantity)
    {
        this.defaultQuantity = defaultQuantity;
    }

    /**
     * @return the flag indicating if the item quantity is modifiable
     */
    public boolean isQuantityModifiable()
    {
        return quantityModifiable;
    }

    /**
     * Set the flag indicating if the item quantity is modifiable
     * 
     * @param quantityModifiable
     */
    public void setQuantityModifiable(boolean quantityModifiable)
    {
        this.quantityModifiable = quantityModifiable;
    }

    /**
     * Retrieves This member contains the reason the item was not found..
     * 
     * @return This member contains the reason the item was not found.
     */
    public String getReason()
    {
        return (reason);
    }

    /**
     * Sets This member contains the reason the item was not found..
     * 
     * @param value This member contains the reason the item was not found.
     */
    public void setReason(String value)
    {
        reason = value;
    }

    /**
     * Sets Unit of Measure Strings.
     * 
     * @param value This member contains the array of strings.
     */
    public void setUnitOfMeasureStrings(String[] uom)
    {
        unitOfMeasureStrings = uom;
    }

    /**
     * Returns Unit of Measure Strings.
     * 
     * @return String array
     */
    public String[] getUnitOfMeasureStrings()
    {
        return (unitOfMeasureStrings);
    }

    /**
     * Gets default unit of measure
     * 
     * @return String default unit of measure
     */
    public String getDefaultUOM()
    {
        return this.defaultUOM;
    }

    /**
     * Sets default unit of measure
     * 
     * @param defaultUOM default unit of measure
     */
    public void setDefaultUOM(String defaultUOM)
    {
        this.defaultUOM = defaultUOM;
    }

    /**
     * Sets Department Strings.
     * 
     * @param value This member contains the array of strings.
     */
    public void setDepartmentStrings(String[] depts)
    {
        departmentStrings = depts;
    }

    /**
     * Returns Department Strings.
     * 
     * @return String array
     */
    public String[] getDepartmentStrings()
    {
        return (departmentStrings);
    }

    /**
     * Returns manufacturer.
     * 
     * @return String manufacturer
     */
    public String getManufacturer()
    {
        return manufacturer;
    }

    /**
     * Sets manufacturer.
     * 
     * @param String manufacturer
     */
    public void setManufacturer(String manufacturer)
    {
        this.manufacturer = manufacturer;
    }

    /**
     * Returns default display string.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        // build result string
        StringBuffer strResult = new StringBuffer("Class:  ItemNotFoundPriceCodeBeanModel (Revision ");
        strResult.append(getRevisionNumber()).append(") @");
        strResult.append(hashCode()).append("\n");

        // add attributes to string
        strResult.append("departmentName:                     [" + departmentName + "]");
        strResult.append("taxable:                            [" + taxable + "]");
        strResult.append("unitOfMeasure:                      [" + unitOfMeasure + "]");
        strResult.append("reason:                             [" + reason + "]");
        strResult.append("serial number:                      [" + itemSerial + "]");
        // pass back result
        return (strResult.toString());
    }

    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * Sets PriceCode.
     * 
     * @param String priceCode
     */
    public void setPriceCode(String priceCode)
    {
        priceCodeField = priceCode;
    }

    /**
     * Returns PriceCode.
     * 
     * @return String priceCodeField
     */
    public String getPriceCode()
    {
        return priceCodeField;
    }

    /**
     * Returns the default item description
     * 
     * @return the default item descripiton
     */
    public String getDefaultItemDescription()
    {
        return defaultItemDescription;
    }

    /**
     * Sets the default item description
     * 
     * @param defaultItemDescription the default item description
     */
    public void setDefaultItemDescription(String defaultItemDescription)
    {
        this.defaultItemDescription = defaultItemDescription;
    }

    /**
     * ItemNotFoundPriceCodeBeanModel main method.
     * 
     * @param String args[] command-line parameters
     */
    public static void main(String args[])
    {
        // instantiate class
        ItemNotFoundPriceCodeBeanModel c = new ItemNotFoundPriceCodeBeanModel();
        // output toString()
        System.out.println(c.toString());
    }

}
