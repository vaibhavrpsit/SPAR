/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/TelephoneTypeComboBox.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:58 mszekely Exp $
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

//----------------------------------------------------------------------------
/**
 * Contains the visual presentation for Telephone Type List
*/
//----------------------------------------------------------------------------
public class TelephoneTypeComboBox extends ValidatingComboBox
{
    //---------------------------------------------------------------------
    /**
     * Constructor
     */
    //---------------------------------------------------------------------
    public TelephoneTypeComboBox()
    {
        super(new TelephoneTypeComboModel());
        setEditable(false);
    } 
    //---------------------------------------------------------------------
    /**
     * Returns the default Telephone Type for the combo model. <P>
     * @return String Default Value
     */
    //---------------------------------------------------------------------
    public String getDefaultValue() 
    {
        return ((TelephoneTypeComboModel)dataModel).getDefaultValue();
    }
} 
