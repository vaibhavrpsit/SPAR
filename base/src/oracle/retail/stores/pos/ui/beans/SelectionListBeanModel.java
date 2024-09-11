/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SelectionListBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:48 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   10/22/10 - add generics for string
 *    cgreene   05/27/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.Vector;

/**
 * This class packages a list of Objects from which to choose, together with a
 * label and the currently selected object in the list.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class SelectionListBeanModel extends POSBaseBeanModel
{
    private static final long serialVersionUID = -7782083104026003471L;

    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** The label for the list. **/
    protected String labelText = "";

    /** The choices from which the user can pick. **/
    protected Vector<String> choices = null;

    /** The value of the selected item in the choices. **/
    protected Object selectionValue = null;

    /**
     * Class Constructor
     */
    public SelectionListBeanModel()
    {
    }

    /**
     * Returns the value of the selected item.
     * 
     * @return the value of the selected item
     */
    public Object getSelectionValue()
    {
        return selectionValue;
    }

    /**
     * Returns the label text.
     * 
     * @return the label text
     */
    public String getLabelText()
    {
        return labelText;
    }

    /**
     * Returns the choices.
     * 
     * @return the choices
     */
    public Vector<String> getChoices()
    {
        return choices;
    }

    /**
     * Sets the selected item.
     * 
     * @param value the value of the selected item.
     */
    public void setSelectionValue(Object value)
    {
        selectionValue = value;
    }

    /**
     * Sets the label text.
     * 
     * @param text the label text. If not "", then this text will be displayed
     *            instead of the the text in the xml config file.
     */
    public void setLabelText(String text)
    {
        this.labelText = text;
    }

    /**
     * Sets the choices.
     * 
     * @param choices the Vector of Strings from which the user can choose
     */
    public void setChoices(Vector<String> choices)
    {
        this.choices = choices;
    }
}
