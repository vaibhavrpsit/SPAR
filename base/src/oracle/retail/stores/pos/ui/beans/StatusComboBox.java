/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/StatusComboBox.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:59 mszekely Exp $
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

/**
import javax.swing.JComboBox;
import javax.swing.ComboBoxModel;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
*/

//----------------------------------------------------------------------------
/**
   This class is used with the OrderStatusComboBoxModel. Valid order status
   values include New, Printed, Partial, Filled, Completed, Canceled. <p>
   @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
   @deprecated as of release 6.0 replaced by ValidatingComboBox
*/
//----------------------------------------------------------------------------

public class StatusComboBox extends ValidatingComboBox
{
    
    //---------------------------------------------------------------------
    /**
     * Constructor.  Pass the combo model to the superclass, ValidatingComboBox.
     * 
     */
    //---------------------------------------------------------------------
    public StatusComboBox ()
    {
        super(new StatusComboModel());
        setEditable(false);
    } 
   //---------------------------------------------------------------------
   /**
     * Returns the default OrderStatus for the combo model. 
     * The dataModel is a field in JComboBox<P>
     * @return java.lang.String
     */
    //---------------------------------------------------------------------
    public String getDefaultValue() 
    {
        return ((StatusComboModel)dataModel).getDefaultValue();
    }
    //---------------------------------------------------------------------
    /**
     * Sets the default OrderStatus for the combo model. <P>
     * @param String Default Value 
     */
    //---------------------------------------------------------------------
    public void setDefaultValue(String defVal) 
    {
        ((StatusComboModel)dataModel).setDefaultValue(defVal);
    }
    //---------------------------------------------------------------------
    /**
    * Override ValidatingComboBox implementation of isInputValid method.
    * Due to the fact that there's no way to Clear the ComboBox
    * the invalid value will be considered the Empty String
    * @return boolean true if Valid,  false if Invalid
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
    }       // end method isInputValid
} ///:~ end class StatusComboBox
