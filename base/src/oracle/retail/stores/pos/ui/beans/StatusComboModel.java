/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/StatusComboModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:58 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;

import oracle.retail.stores.domain.order.OrderConstantsIfc;
//---------------------------------------------------------------------
/**
 * This class contains the valid order status values for the StatusComboBox in
 * the Status Search screen.  
 * Valid values are: New, Printed, Partial, Filled, Completed, Canceled <p>
 * @version  $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
 * @deprecated as of release 6.0 replaced by ComboBoxModel
 */             
//---------------------------------------------------------------------
public class StatusComboModel extends DefaultListModel implements ComboBoxModel
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -139617143463546237L;

    protected String selectedStatusValue = null;
    protected String defaultStatusValue = null;

    //---------------------------------------------------------------------
    /**
     * StatusComboModel constructor. This initializes the model.
     */
    //---------------------------------------------------------------------
    public StatusComboModel() 
    {
        super();
        initialize(); 
    }
    //---------------------------------------------------------------------
    /**
     * Returns the default order status for this combo model. <P>
     * @return java.lang.String
     */
    //---------------------------------------------------------------------
    public String getDefaultValue() 
    {
        if (defaultStatusValue == null)
        {
            String[] orderStatus = getOrderStatusArray();
            return orderStatus[0];            
        }
        else
        {
            return(defaultStatusValue); 
        }
    }
    //---------------------------------------------------------------------
    /**
     * Sets the default order status for this combo model. <P>
     * @param String Default Value 
     */
    //---------------------------------------------------------------------
    public void setDefaultValue(String defVal) 
    {
        defaultStatusValue = defVal;
    }
    //---------------------------------------------------------------------
    /**
     * Returns the selected item.
     * @return java.lang.Object
     */
    //---------------------------------------------------------------------
    public Object getSelectedItem() 
    {
        return selectedStatusValue;
    }
    //---------------------------------------------------------------------
    /**
     * Returns the 6 valid order status codes as a String array. <P>
     * <B>Pre-Condition</B>
     * <UL>None.
     * <LI>
     * </UL>
     * @return java.lang.String[] An array of order status codes.
     */
    //---------------------------------------------------------------------
    public String[] getOrderStatusArray()
    {
        String[] orderStatusArray = OrderConstantsIfc.ORDER_STATUS_DESCRIPTORS;
        return orderStatusArray;
    }
    //---------------------------------------------------------------------
    /**
    **/
    //---------------------------------------------------------------------
    private void initialize() 
    {
        String[] orderStatusList = getOrderStatusArray();
        for(int i = 0; i < orderStatusList.length; i++)
        {
            addElement(orderStatusList[i]);
        }
        selectedStatusValue = getDefaultValue();  
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
        if (!item.equals(selectedStatusValue))
        {
            selectedStatusValue = (String) item;
            fireContentsChanged(this, -1, -1);
        }
    }   // end method setSelectedItem
}  ///:~ end class StatusComboModel
