/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/StateComboBox.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;


//-------------------------------------------------------------------------
/**
   This class provides a specialized combo box for dispalying States.

   @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
   @deprecated as of release 5.5 replaced by ValidatingComboBox
*/
//-------------------------------------------------------------------------
public class StateComboBox extends ValidatingComboBox
{
    
    /** revision number supplied by version control */
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";
    
    //---------------------------------------------------------------------
    /**
     * Constructor
     * 
     */
    //---------------------------------------------------------------------
    public StateComboBox ()
    {
        super(new StateComboModel());
        setEditable(false);
    } 
    //---------------------------------------------------------------------
    /**
     * Returns the default state for the combo model. <P>
     * @return java.lang.String
     */
    //---------------------------------------------------------------------
    public String getDefaultValue() 
    {
        return ((StateComboModel)dataModel).getDefaultValue();
    }
    //---------------------------------------------------------------------
    /**
     * Sets the default state for the combo model. <P>
     * @param String Default Value 
     */
    //---------------------------------------------------------------------
    public void setDefaultValue(String defVal) 
    {
        ((StateComboModel)dataModel).setDefaultValue(defVal);
    }
  
    //---------------------------------------------------------------------
    /**
     * Gets the country combo box.
     * @retun CountryComboBox 
     */
    //---------------------------------------------------------------------
    public CountryComboBox getCountryComboBox() 
    {
        return ((StateComboModel)dataModel).getCountryComboBox();
    }
    //---------------------------------------------------------------------
    /**
     * Sets the country combo box.
     * @param ccb CountryComboBox 
     */
    //---------------------------------------------------------------------
    public void setCountryComboBox(CountryComboBox ccb) 
    {
        ((StateComboModel)dataModel).setCountryComboBox(ccb);
    }
    
    //---------------------------------------------------------------------
    /**
     * Gets the postal code extension text field.
     * @return NumericTextField
     */
    //---------------------------------------------------------------------
    public NumericTextField getExtPostalCodeField() 
    {
        return ((StateComboModel)dataModel).getExtPostalCodeField();
    }
    //---------------------------------------------------------------------
    /**
     * Sets the postal code extension text field.
     * @param ntf NumericTextField
     */
    //---------------------------------------------------------------------

    public void setExtPostalCodeField(NumericTextField ntf) 
    {
        ((StateComboModel)dataModel).setExtPostalCodeField(ntf);
    }

    //---------------------------------------------------------------------
    /**
    * Checks if the item selected is a canadian province
    * @return boolean
    */
    //---------------------------------------------------------------------
    public boolean isInCanada() 
    {
        return ((StateComboModel)dataModel).isInCanada();
    }
    //---------------------------------------------------------------------
    /**
    * Due to the fact that there's no way to Clear the ComboBox
    * the invalid value will be considered the Empty String
    * @return boolean true if Valid false if Invalid
    */
    //---------------------------------------------------------------------
    public boolean isInputValid()
    {
        if (!emptyAllowed && 
            (getSelectedIndex() == -1 || getSelectedItem().equals("")))
        {
            return(false);
        }
        else
        {
            return(true);
        }
    }
} 
