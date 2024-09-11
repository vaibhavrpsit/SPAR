/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ItemNotFoundBeanModel.java /main/15 2011/12/05 12:16:30 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    sgu       06/08/10 - enhance ItemNotFoundPriceCodeBean to display
 *                         external order quantity and description
 *    sgu       06/08/10 - fix tab
 *    sgu       06/08/10 - add item # & desc to the screen prompt. fix unknow
 *                         item screen to disable price and quantity for
 *                         external item
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   7    360Commerce 1.6         3/25/2008 6:21:36 AM   Vikram Gopinath CR
 *        #30683, porting changes from v12x. Save the correct pos department
 *        id for an unknown item.
 *   6    360Commerce 1.5         7/30/2007 11:41:05 AM  Alan N. Sinton  CR
 *        27943 - Fixed screen hang with ItemNotFound.
 *   5    360Commerce 1.4         1/22/2006 11:45:26 AM  Ron W. Haight
 *        removed references to com.ibm.math.BigDecimal
 *   4    360Commerce 1.3         12/13/2005 4:42:45 PM  Barry A. Pape
 *        Base-lining of 7.1_LA
 *   3    360Commerce 1.2         3/31/2005 4:28:32 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:22:29 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:11:39 PM  Robert Pearse
 *
 *  Revision 1.4  2004/03/16 17:15:17  build
 *  Forcing head revision
 *
 *  Revision 1.3  2004/03/04 19:59:11  rsachdeva
 *  @scr 3906 Quantity and Unit of Measure
 *
 *  Revision 1.2  2004/02/11 20:56:26  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:11:00   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 14:52:48   msg
 * Initial revision.
 *
 *    Rev 1.1   25 Apr 2002 18:52:32   pdd
 * Removed unnecessary BigDecimal instantiations.
 * Resolution for POS SCR-1610: Remove inefficient instantiations of BigDecimal
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.math.BigDecimal;

import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.foundation.utility.Util;

/**
 * Class description.
 */
public class ItemNotFoundBeanModel extends ItemInfoBeanModel
{
    private static final long serialVersionUID = 2428303801944967337L;

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * This member holds the department name.
     */
    protected String departmentName = "";

    /**
     * This member holds the department identifier.
     */
    protected String departmentID = "";

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
    protected String defaultUOM = "Units";

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
     * This member holds the department ids.
     */
    protected String[] departmentIDs = new String[1];

    /**
     * This item serial number.
     */
    protected String itemSerial = null;

    /**
     * This item manufacturer.
     */
    protected String manufacturer = null;

    /**
     * This member contains the default item price
     */
    protected BigDecimal defaultPrice = BigDecimalConstants.ZERO_AMOUNT;

    /**
     * This member contains the flag indicating if the item price can be
     * modified.
     */
    protected boolean priceModifiable = true;

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
     * Returns the department id.
     * 
     * @return The department id.
     */
    public String getDepartmentID()
    {
        return (departmentID);
    }

    /**
     * Sets the department id.
     * 
     * @param value the department id.
     */
    public void setDepartmentID(String value)
    {
        departmentID = value;
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
     * Sets Department IDs.
     * 
     * @param deptIDs This member contains the array of strings.
     */
    public void setDepartmentIDs(String[] deptIDs)
    {
        departmentIDs = deptIDs;
    }

    /**
     * Returns Department IDs.
     * 
     * @return String array
     */
    public String[] getDepartmentIDs()
    {
        return (departmentIDs);
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
     * @return the default item price
     */
    public BigDecimal getDefaultPrice()
    {
        return defaultPrice;
    }

    /**
     * Set the default item price
     * 
     * @param defaultPrice
     */
    public void setDefaultPrice(BigDecimal defaultPrice)
    {
        this.defaultPrice = defaultPrice;
    }

    /**
     * @return the flag indicating if the item price is modifiable
     */
    public boolean isPriceModifiable()
    {
        return priceModifiable;
    }

    /**
     * Set the flag indicating if the item price is modifiable
     * 
     * @param priceModifiable
     */
    public void setPriceModifiable(boolean priceModifiable)
    {
        this.priceModifiable = priceModifiable;
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
     * Returns default display string.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        // build result string
        StringBuffer strResult = new StringBuffer("Class:  ItemNotFoundBeanModel (Revision ");
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
     * ItemNotFoundBeanModel main method.
     * 
     * @param String args[] command-line parameters
     */
    public static void main(String args[])
    {
        // instantiate class
        ItemNotFoundBeanModel c = new ItemNotFoundBeanModel();
        // output toString()
        System.out.println(c.toString());
    }

}
