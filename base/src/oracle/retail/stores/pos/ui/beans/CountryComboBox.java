/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CountryComboBox.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:59 mszekely Exp $
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
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:27:31 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:20:25 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:13 PM  Robert Pearse   
 *
 *  Revision 1.5  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

/*
@deprecated as of release 5.5 replaced by ValidatingComboBox
*/
public class CountryComboBox extends ValidatingComboBox
{
    
    //---------------------------------------------------------------------
    /**
     * Constructor
     * 
     */
    //---------------------------------------------------------------------
    public CountryComboBox ()
    {
        super(new CountryComboModel());
        setEditable(false);
    } 
    //---------------------------------------------------------------------
    /**
     * Returns the default Country for the combo model. <P>
     * @return java.lang.String
     */
    //---------------------------------------------------------------------
    public String getDefaultValue() 
    {
        return ((CountryComboModel)dataModel).getDefaultValue();
    }
    //---------------------------------------------------------------------
    /**
     * Sets the default Country for the combo model. <P>
     * @param String Default Value 
     */
    //---------------------------------------------------------------------
    public void setDefaultValue(String defVal) 
    {
        ((CountryComboModel)dataModel).setDefaultValue(defVal);
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
