/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DefaultComboBoxModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:58 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:27:43 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:20:52 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:31 PM  Robert Pearse   
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

//------------------------------------------------------------------------------ 
/**
    The default model for a ComboBox.
    @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
**/
//------------------------------------------------------------------------------ 
public class DefaultComboBoxModel extends DefaultListModel implements ComboBoxModel
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 230734994336169505L;

      /**  Revision number  **/
    public static final String revisionNumber = "$KW=@(#); $Ver; $EKW;";
      /**  Selected Value  **/
    protected Object selectedValue = null;
      /**  Default value  **/
    protected Object defaultValue = null;
        
    //---------------------------------------------------------------------
    /**
        Constructs DefaultComboBoxModel object.
    **/
    //---------------------------------------------------------------------
    public DefaultComboBoxModel()
    {
        super();                      
    }
    //---------------------------------------------------------------------
    /**
     * Returns the default element for this combo model.
     * @return java.lang.String
     */
    //---------------------------------------------------------------------
    public Object getDefaultValue() 
    {
        return defaultValue;
    }
    //---------------------------------------------------------------------
    /**
     * Sets the default element for this combo model.
     * @param String Default Value 
     */
    //---------------------------------------------------------------------
    public void setDefaultValue(Object defVal) 
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
     * Sets the selected item.
     * @param item java.lang.Object
     */
    //---------------------------------------------------------------------
    public void setSelectedItem(Object item)
    {
        if (item != null)
        {
              if (!contains(item))
              {
                  addElement(item);
              }
              if (!item.equals(selectedValue))
              {
                  selectedValue = item;
                  fireContentsChanged(this, -1, -1);
              }                  
        }
    }
}

