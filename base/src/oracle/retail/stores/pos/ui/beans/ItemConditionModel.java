/* ===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ItemConditionModel.java /main/1 2012/03/14 00:09:11 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  03/05/12 - RM i18n POS Return changes
 *    rabhawsa  03/05/12 - RM i18n POS return changes added for item condition
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.Locale;
import java.util.Vector;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.manager.ifc.ModelIfc;

public class ItemConditionModel implements ModelIfc
{
    
    /**
     * Generated SerialVersionUID
     */
    private static final long serialVersionUID = 2632037108816726105L;

    // --------------------------------------------------------------------------
    /**
     * Revision Number supplied by TeamConnection.
     */
    // --------------------------------------------------------------------------
    protected static final String revisionNumber = "$Revision: /main/1 $";

    // --------------------------------------------------------------------------
    /**
     * Indicates if field is selected.
     */
    // --------------------------------------------------------------------------
    protected boolean fieldSelected = false;

    // --------------------------------------------------------------------------
    /**
     * Item Condition field is selected.
     */
    // --------------------------------------------------------------------------
    protected String fieldSelectedItemCondition = new String();

    // --------------------------------------------------------------------------
    /**
     * Item Condition field Reference Key
     */
    // --------------------------------------------------------------------------
    protected String fieldSelectedItemConditionKey = new String();

    // --------------------------------------------------------------------------
    /**
     * Container of Item Condition codes.
     */
    // --------------------------------------------------------------------------
    protected Vector fieldItemConditionCodes = new Vector();

    // --------------------------------------------------------------------------
    /**
     * Container for Item Condition code keys;
     */
    // --------------------------------------------------------------------------
    protected Vector fieldItemConditionCodeKeys = new Vector();

    // --------------------------------------------------------------------------
    /**
     * Index of selected Item Condition code.
     */
    // --------------------------------------------------------------------------
    protected int fieldSelectedIndex = 0;

    // --------------------------------------------------------------------------
    /**
     * Indicates default Item Condition code description.
     */
    // --------------------------------------------------------------------------
    protected String defaultValue = "";

    // --------------------------------------------------------------------------
    /**
     * Constructor
     */
    // --------------------------------------------------------------------------
    public ItemConditionModel()
    {
       
    }

    // --------------------------------------------------------------------------
    /**
     * Gets the itemConditionCodes property (Vector) value.
     * 
     * @return Vector
     * @see #setItemConditionCodes(Vector)
     */
    // --------------------------------------------------------------------------
    public Vector getItemConditionCodes()
    {
        return fieldItemConditionCodes;
    }

    // --------------------------------------------------------------------------
    /**
     * Gets the itemConditionCodeKeys property (Vector) value.
     * 
     * @return Vector
     * @see #setItemConditionCodeKeys(Vector)
     */
    // --------------------------------------------------------------------------
    public Vector getItemConditionCodeKeys()
    {
        return fieldItemConditionCodeKeys;
    }

    // --------------------------------------------------------------------------
    /**
     * Gets the selectedItemCondition property (java.lang.String) value.
     * 
     * @return String
     * @see #setSelectedItemConditionCode(String)
     **/
    // --------------------------------------------------------------------------
    public String getSelectedItemCondition()
    {
        return fieldSelectedItemCondition;
    }

    // --------------------------------------------------------------------------
    /**
     * Gets the selectedItemConditionKey property (java.lang.String) value.
     * 
     * @return String
     **/
    // --------------------------------------------------------------------------
    public String getSelectedItemConditionKey()
    {
        return fieldSelectedItemConditionKey;
    }

    // --------------------------------------------------------------------------
    /**
     * Gets the index of the selected Item Condition property.
     * 
     * @return int
     * @see #setSelectedIndex(int)
     **/
    // --------------------------------------------------------------------------
    public int getSelectedIndex()
    {
        return fieldSelectedIndex;
    }

    // --------------------------------------------------------------------------
    /**
     * Gets the index of the default Item Condition property.
     * 
     * @return int
     **/
    // --------------------------------------------------------------------------
    public int getDefaultIndex()
    {
        return fieldItemConditionCodes.indexOf(defaultValue);
    }

    // --------------------------------------------------------------------------
    /**
     * Indicates default Item Condition code description.
     * 
     * @return The default value
     */
    // --------------------------------------------------------------------------
    public String getDefaultValue()
    {
        return defaultValue;
    }

    // --------------------------------------------------------------------------
    /**
     * Gets the selected property (boolean) value.
     * 
     * @return boolean
     * @see #setSelected(boolean)
     */
    // --------------------------------------------------------------------------
    public boolean isSelected()
    {
        return fieldSelected;
    }

    // --------------------------------------------------------------------------
    /**
     * Sets the itemConditionCodes property (Vector) value.
     * 
     * @param itemConditionCodes The new value for the property.
     * @see #getItemConditionCodes()
     */
    // --------------------------------------------------------------------------
    public void setItemConditionCodes(Vector itemConditionCodes)
    {
        fieldItemConditionCodes = itemConditionCodes;
    }

    // --------------------------------------------------------------------------
    /**
     * Sets the itemConditionCodeKeys property (Vector) value.
     * 
     * @param itemConditionCodeKeys The new value for the property.
     * @see #getItemConditionCodeKeys()
     */
    // --------------------------------------------------------------------------
    public void setItemConditionCodeKeys(Vector itemConditionCodeKeys)
    {
        fieldItemConditionCodeKeys = itemConditionCodeKeys;
    }

    // --------------------------------------------------------------------------
    /**
     * Sets the selected property (boolean) value.
     * 
     * @param selected whether selected or not
     * @see #isSelected()
     */
    // --------------------------------------------------------------------------
    public void setSelected(boolean selected)
    {
        fieldSelected = selected;
    }

    
    // --------------------------------------------------------------------------
    /**
     * Sets the fieldSelectedIndex property and uses it to set the
     * fieldSelectedItemCondition property.
     * 
     * @param selectedItemConditionIndex index of selected Item Condition
     * @see #getSelectedItemCondition()
     * @see #getSelectedIndex()
     */
    // --------------------------------------------------------------------------
    public void setSelectedItemConditionCode(int selectedItemConditionIndex)
    {
        fieldSelectedIndex = selectedItemConditionIndex;
        if (fieldSelectedIndex >= 0 && fieldSelectedIndex < fieldItemConditionCodes.size())
        {
            fieldSelectedItemCondition = (String)fieldItemConditionCodes.elementAt(fieldSelectedIndex);
            if (!fieldItemConditionCodeKeys.isEmpty())
            {
                fieldSelectedItemConditionKey = (String)fieldItemConditionCodeKeys.elementAt(fieldSelectedIndex);
            }
        }
    }

    // --------------------------------------------------------------------------
    /**
     * Sets the fieldSelectedItemCondition property and uses it to set the
     * fieldSelectedIndex property.
     * 
     * @param selectedItemCondition Item Condition for selection
     * @see #getSelectedItemCondition()
     * @see #getSelectedIndex()
     */
    // --------------------------------------------------------------------------
    public void setSelectedItemConditionCode(String selectedItemCondition)
    {
        String oldfieldSelectedItemConditionKey = fieldSelectedItemConditionKey;
        fieldSelectedItemCondition = selectedItemCondition;
        fieldSelectedIndex = fieldItemConditionCodes.indexOf(selectedItemCondition);

        if (!fieldItemConditionCodeKeys.isEmpty() && fieldSelectedIndex > -1)
        {
            fieldSelectedItemConditionKey = (String)fieldItemConditionCodeKeys.elementAt(fieldSelectedIndex);
        }
        else
            try
            {
                Integer.parseInt(selectedItemCondition);
                fieldSelectedItemConditionKey = selectedItemCondition;
                if (fieldItemConditionCodeKeys.contains(selectedItemCondition))
                {
                    fieldSelectedIndex = fieldItemConditionCodeKeys.indexOf(fieldSelectedItemConditionKey);
                    fieldSelectedItemCondition = (String)fieldItemConditionCodes.elementAt(fieldSelectedIndex);
                }
            }
            catch (NumberFormatException nfe)
            {
                // Revert to last value - if possible
                fieldSelectedItemConditionKey = oldfieldSelectedItemConditionKey;
            }
    }

    // --------------------------------------------------------------------------
    /**
     * Clears the selected Item Condition code when an invalid code has been entered.
     **/
    // --------------------------------------------------------------------------
    public void clearSelectedItemCondition()
    {
        fieldSelected = false;
        fieldSelectedItemCondition = new String();
        fieldSelectedItemConditionKey = new String();
        fieldSelectedIndex = 0;
    }

    // --------------------------------------------------------------------------
    /**
     * Indicates default Item Condition code description.
     * 
     * @param newValue The new value
     */
    // --------------------------------------------------------------------------
    public void setDefaultValue(String newValue)
    {
        defaultValue = newValue;
    }

    // --------------------------------------------------------------------------
    /**
     * Converts to a string representing the data in this Object
     * 
     * @return String representing the data in this Object.
     **/
    // --------------------------------------------------------------------------
    public String toString()
    {
        StringBuffer buff = new StringBuffer();

        buff.append("Class: ItemConditionModel Revision: " + revisionNumber + "\n");
        buff.append("fieldItemConditionCodes [" + fieldItemConditionCodes + "]\n");
        buff.append("fieldSelectedIndex [" + fieldSelectedIndex + "]\n");
        buff.append("fieldSelectedItemCondition [" + fieldSelectedItemCondition + "]\n");
        buff.append("fieldSelectedItemConditionKey [" + fieldSelectedItemConditionKey + "]\n");
        buff.append("fieldSelected [" + fieldSelected + "]\n");
        return (buff.toString());
    }

    /**
     * This method injects a CodeList into the Model
     * 
     * @param list
     * @param selectedItemConditionCode
     * @param locale
     */
    public void inject(CodeListIfc list, String selectedItemConditionCode, Locale locale)
    {
        if (list != null)
        {
            setItemConditionCodes(list.getTextEntries(locale));
            setItemConditionCodeKeys(list.getKeyEntries());
            setDefaultValue(list.getDefaultOrEmptyString(locale));
            if (selectedItemConditionCode == null || selectedItemConditionCode.length() == 0
                    || (CodeConstantsIfc.CODE_UNDEFINED.equals(selectedItemConditionCode)))
            {
                selectedItemConditionCode = list.getDefaultCodeString();
            }
            // Check if there is a previous selection
            if (!Util.isEmpty(selectedItemConditionCode) && !CodeConstantsIfc.CODE_UNDEFINED.equals(selectedItemConditionCode))
            {
                CodeEntryIfc codeEntry = list.findListEntryByCode(selectedItemConditionCode);
                if (codeEntry != null)
                {
                    String selectedItemCondition = codeEntry.getText(locale);
                    setSelectedItemConditionCode(selectedItemCondition);
                }

            }
            setSelected(true);
        }
    }

}
