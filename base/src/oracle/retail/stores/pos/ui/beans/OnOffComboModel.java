/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/OnOffComboModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:44 mszekely Exp $
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

import javax.swing.ComboBoxModel;

//-------------------------------------------------------------------------
/**
 * This class contains the states needed for either a ListBox or a ComboBox

   @version $KW=@(#); $Ver=pos_4.5.0:8; $EKW;
*/
//-------------------------------------------------------------------------
public class OnOffComboModel extends BooleanComboModel implements ComboBoxModel
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 6663189520000085983L;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:8; $EKW;";
    /** Constant for true */
    static public final String PTY_TRUE_VALUE = "OnOffComboModel.trueValue";
    /** Constant for false */
    static public final String PTY_FALSE_VALUE = "OnOffComboModel.falseValue";
    /** Constant for default */
    static public final String PTY_DEFAULT_VALUE = PTY_TRUE_VALUE;
    /** Constant for on */
    static public final String DEFAULT_TRUE_VALUE = "On";
    /** Constant for off */
    static public final String DEFAULT_FALSE_VALUE = "Off";
    /** Constant for default */
    static public final String DEFAULT_DEFAULT_VALUE = DEFAULT_TRUE_VALUE;

    //---------------------------------------------------------------------
    /**
       Constructor.
    */
    //---------------------------------------------------------------------
    public OnOffComboModel()
    {
        super();
        trueValue = DEFAULT_TRUE_VALUE;
        falseValue = DEFAULT_FALSE_VALUE;
        defaultValue = DEFAULT_DEFAULT_VALUE;
        selectedValue = defaultValue;
        removeAllElements();
        add(0, trueValue);
        add(1, falseValue);
    }

    //---------------------------------------------------------------------
    /**
    * Returns value of property "BooleanComboModel.defaultValue".
    * @return java.lang.String value
    */
    //---------------------------------------------------------------------
    public String getDefaultValue()
    {
        if (props != null)
        {
            defaultValue = props.getProperty(PTY_DEFAULT_VALUE, DEFAULT_DEFAULT_VALUE);
        }

        return defaultValue;
    }

    //---------------------------------------------------------------------
    /**
    * Returns value of property "BooleanComboModel.falseValue".
    * @return java.lang.String value
    */
    //---------------------------------------------------------------------
    public String getFalseValue()
    {
        if (props != null)
        {
            falseValue = props.getProperty(PTY_FALSE_VALUE, DEFAULT_FALSE_VALUE);
        }

        return falseValue;
    }

    //---------------------------------------------------------------------
    /**
    * Returns value of property "BooleanComboModel.trueValue".
    * @return java.lang.String value
    */
    //---------------------------------------------------------------------
    public String getTrueValue()
    {
        if (props != null)
        {
            trueValue = props.getProperty(PTY_TRUE_VALUE, DEFAULT_TRUE_VALUE);
        }

        return trueValue;
    }

}
