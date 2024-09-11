/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CountryComboModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:59 mszekely Exp $
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
 *  Revision 1.6  2004/09/23 00:07:11  kmcbride
 *  @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *  Revision 1.5  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;

//---------------------------------------------------------------------
/**
 * This class contains the Countrys needed for either a ListBox or a ComboBox
 @deprecated as of release 5.5 replaced by ComboBoxModel
 */
//---------------------------------------------------------------------
public class CountryComboModel extends DefaultListModel implements ComboBoxModel
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -801044696805291575L;

    /**
        selectedValue - the value selected by the user, default null
    **/
    protected String selectedValue = null;
    /**
        defaultValue - the default value, set to "USA"
    **/
   protected String defaultValue = "USA";

    //---------------------------------------------------------------------
    /**
     * CountryComboModel constructor. This initializes the model.
     */
    //---------------------------------------------------------------------
    public CountryComboModel() 
    {
        super();
        initialize(); 
          selectedValue=getDefaultValue();
    }
    //---------------------------------------------------------------------
    /**
     * Returns the default Country for this combo model. <P>
     * @return java.lang.String
     */
    //---------------------------------------------------------------------
    public String getDefaultValue() 
    {
        String[] countries=getCountrysArray();
        return countries[0];
    }
    //---------------------------------------------------------------------
    /**
     * Sets the default Country for this combo model. <P>
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
     * Returns the Country  as an array of Strings. <P>
     * <B>Pre-Condition</B>
     * <UL>None.
     * <LI>
     * </UL>
     * @return java.lang.String[] An array of 2 letter Country abbreviations.
     */
    //---------------------------------------------------------------------
    public String[] getCountrysArray()
    {
        String[] Countrys = {"USA", "Canada"};
        return Countrys;
    }
    //---------------------------------------------------------------------
    /**
    **/
    //---------------------------------------------------------------------
    protected void initialize() 
    {
        String[] Countrys=getCountrysArray();
        for(int i=0;i<Countrys.length;i++)
        {
            addElement(Countrys[i]);
        }
    }
    //---------------------------------------------------------------------
    /**
     * Sets the selected item.
     * @param item java.lang.Object
     */
    //---------------------------------------------------------------------
    public void setSelectedItem(Object item)
    {
        if (!contains(item))
        {
            addElement(item);
        }
        if (!item.equals(selectedValue))
        {
            selectedValue = (String) item;
            fireContentsChanged(this, -1, -1);
        }
    }
}
