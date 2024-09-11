/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/StateComboModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:55 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Color;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;

//---------------------------------------------------------------------
/**
 * This class contains the states needed for either a ListBox or a ComboBox
 @deprecated as of release 5.5 replaced by ComboBoxModel
 */
//---------------------------------------------------------------------
public class StateComboModel extends DefaultListModel implements ComboBoxModel
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 4843690379911961337L;

    /** Selected value */
    protected String selectedValue = null;
    /** Default value */
    protected String defaultValue = "TX";
    /** Used to set the country based on the state selected */
    protected CountryComboBox c_ComboBox = null;
    /** Used to enable/disable the postal code extension based on the 
        state selected. */
    protected NumericTextField extPostalCodeField = null;
    /** The number of US State entries */
    protected static final int US_STATES = 51;
    /** The array of US State And Canadian Provence Text Values */
    protected static String[] states = {"AK", "AL", "AZ", "AR", "CA", "CO", "CT", "DC", 
                           "DE", "FL", "GA", "HI", "IA", "ID", "IL", "IN", 
                           "KS", "KY", "LA", "MA", "MD", "ME", "MI", "MN", 
                           "MO", "MS", "MT", "NC", "ND", "NE", "NH", "NJ", 
                           "NM", "NV", "NY", "OH", "OK", "OR", "PA", "RI", 
                           "SC", "SD", "TN", "TX", "UT", "VA", "VT", "WA", 
                           "WV", "WI", "WY", "AB", "BC", "MB", "NB", "NF", 
                           "NT", "NS", "ON", "PE", "QC", "SK", "YK", "" };


    //---------------------------------------------------------------------
    /**
     * StateComboModel constructor. This initializes the model.
     */
    //---------------------------------------------------------------------
    public StateComboModel() 
    {
        super();
        initialize(); 
    }
    //---------------------------------------------------------------------
    /**
     * Returns the default state for this combo model. <P>
     * @return java.lang.String
     */
    //---------------------------------------------------------------------
    public String getDefaultValue() 
    {
        return states[0];
    }
    //---------------------------------------------------------------------
    /**
     * Sets the default state for this combo model. <P>
     * @param String Default Value 
     */
    //---------------------------------------------------------------------
    public void setDefaultValue(String defVal) 
    {
        defaultValue = defVal;
    }
    //---------------------------------------------------------------------
    /**
     * Returns the selected item.
     * @return java.lang.Object
     */
    //---------------------------------------------------------------------
    public Object getSelectedItem() 
    {
        return selectedValue;
    }
    //---------------------------------------------------------------------
    /**
     * Returns the 50 state 2 letter abbreviations as an array of Strings. <P>
     * <B>Pre-Condition</B>
     * <UL>None.
     * <LI>
     * </UL>
     * @return java.lang.String[] An array of 2 letter state abbreviations.
     */
    //---------------------------------------------------------------------
    public String[] getStatesArray()
    {
        return states;
    }
    //---------------------------------------------------------------------
    /**
        Initialize the combo box.
    **/
    //---------------------------------------------------------------------
    protected void initialize() 
    {
        for(int i=0;i<states.length;i++)
        {
            addElement(states[i]);
        }
          selectedValue=getDefaultValue();
    }

    //---------------------------------------------------------------------
    /**
     * Checks if the item selected in the state combo box
     * is a canadian province
     * @return boolean
     */
    //---------------------------------------------------------------------
    public boolean isInCanada()
    {
        int i;
        
        for (i=0; i<=states.length-1;i++)
        {
          if (states[i].equals(selectedValue))
              break;
        }
              
        if (i >= US_STATES)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    //---------------------------------------------------------------------
    /**
     * Sets the country combo box.
     * @param ccb CountryComboBox 
     */
    //---------------------------------------------------------------------
    public void setCountryComboBox(CountryComboBox ccb)
    {
        c_ComboBox = ccb;
    }

    //---------------------------------------------------------------------
    /**
     * Gets the country combo box.
     * @return CountryComboBox
     */
    //---------------------------------------------------------------------
    public CountryComboBox getCountryComboBox()
    {
        return c_ComboBox;
    }
    
    //---------------------------------------------------------------------
    /**
     * Sets the postal code extension text field.
     * @param ePostalCodeField NumericTextField
     */
    //---------------------------------------------------------------------
    public void setExtPostalCodeField (NumericTextField ePostalCodeField)
    {
        extPostalCodeField = ePostalCodeField;
    }

    //---------------------------------------------------------------------
    /**
     * Gets the postal code extension text field.
     * @return NumericTextField
     */
    //---------------------------------------------------------------------
    public NumericTextField getExtPostalCodeField ()
    {
        return extPostalCodeField;
    }

    //---------------------------------------------------------------------
    /**
     * Sets the selected item.
     * @param item java.lang.Object
     */
    //---------------------------------------------------------------------
    public void setSelectedItem(Object item)
    {

        int lastState = states.length-2;
        
        //Replace the "" selection with the last state/province on the list
        if(item.equals(""))
        {
            item= states[lastState];
        }

        // Add the selected element
        if (!contains(item))
        {
            addElement(item);
        }
        if (!item.equals(selectedValue))
        {
            selectedValue = (String) item;
            fireContentsChanged(this, -1, -1);
        }

        //Based on the selected state, display the correct country in 
        // the country field; also, disable the postal code extension field
        if (c_ComboBox != null)
        {
           c_ComboBox.setEnabled(false);
           if (isInCanada())
            {
                c_ComboBox.setSelectedItem("Canada");
                 
                extPostalCodeField.setBackground(extPostalCodeField.getParent().getBackground());
                extPostalCodeField.setText("");
                extPostalCodeField.setEnabled(false);
            }
            else
            {
                c_ComboBox.setSelectedItem("USA");
                
                extPostalCodeField.setBackground(Color.white);
                extPostalCodeField.setEnabled(true);
            }
        }
    }
}
