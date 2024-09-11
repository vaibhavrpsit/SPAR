/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/BooleanComboModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:55 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:27:17 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:19:50 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:09:36 PM  Robert Pearse   
 *
 *  Revision 1.5  2004/09/23 00:07:11  kmcbride
 *  @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *  Revision 1.4  2004/03/16 17:15:22  build
 *  Forcing head revision
 *
 *  Revision 1.3  2004/03/16 17:15:16  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:27  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Dec 01 2003 14:01:30   baa
 * yes/no combo box issues
 * Resolution for 3468: Drop down boxes display incorrect data on Customer Details during Customer Search
 * 
 *    Rev 1.0.1.0   Dec 01 2003 12:00:00   baa
 * Address issues with updating yes/no combo boxes
 * Resolution for 3468: Drop down boxes display incorrect data on Customer Details during Customer Search
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.Properties;

import javax.swing.ComboBoxModel;

//-------------------------------------------------------------------------
/**
   This class manipulates the model for True/False combo boxes

**/
//-------------------------------------------------------------------------
public class BooleanComboModel extends javax.swing.DefaultListModel implements ComboBoxModel
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -1326812820729269887L;

    static public final String PTY_TRUE_VALUE = "TrueValue";
    static public final String PTY_FALSE_VALUE = "FalseValue";
    static public final String PTY_DEFAULT_VALUE = "DefaultValue";
    static public String DEFAULT_TRUE_VALUE = "Yes";
    static public String DEFAULT_FALSE_VALUE = "No";
    static public String DEFAULT_DEFAULT_VALUE = DEFAULT_TRUE_VALUE;
    // Not initialized because extending class overrides
    protected String trueValue = null;
    protected String falseValue = null;
    protected String defaultValue = null;
    protected String selectedValue = null;
    /** Properties **/
    protected Properties props = null;

    //---------------------------------------------------------------------
    /**
    * StateComboModel constructor. This initializes the model.
    */
    //---------------------------------------------------------------------
    public BooleanComboModel()
    {
        super();
        initialize();
    }

    //---------------------------------------------------------------------
    /**
    * Returns value of property "Common.defaultValue".
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
    * Returns value of property "Common.falseValue".
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
    * Returns value of property "Common.trueValue".
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

    //---------------------------------------------------------------------
    /**
    * Returns the selected item.
    * @return java.lang.Object
    */
    //---------------------------------------------------------------------
    public Object getSelectedItem()
    {
        if (selectedValue == null)
        {
            selectedValue = getFalseValue();
        }
        return selectedValue;
    }

    //---------------------------------------------------------------------
    /**
    * Returns the locale appropiate values for true and false. <P>
    * <B>Pre-Condition</B>
    * <UL>None.
    * <LI>
    * </UL>
    * @return java.lang.String[] An array of True/False values.
    */
    //---------------------------------------------------------------------
    public String[] getValues()
    {
        String[] booleanValues = {getTrueValue(),getFalseValue()};
        return booleanValues;
    }

    //---------------------------------------------------------------------
    /**
    *   Initialize the model
    */
    //---------------------------------------------------------------------
    protected void initialize()
    {
        trueValue = DEFAULT_TRUE_VALUE;
        falseValue = DEFAULT_FALSE_VALUE;
        defaultValue = DEFAULT_DEFAULT_VALUE;
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
            selectedValue = (String) item;
        }
    }

    //---------------------------------------------------------------------
    /**
     *  Set the properties to be used by this bean
        @param props the propeties object
     */
    //---------------------------------------------------------------------
    public void setProps(Properties props)
    {
        // Get rid of what is already here.
        clear();
        
        this.props = props;

        String[] values = getValues();

        for(int i = 0; i < values.length; i++)
        {
            addElement(values[i]);
        }
    }

    //---------------------------------------------------------------------
    /**
    * This returns true if the input string indicates a positive answer, 
    * false if it indicates a negative answer.  
    * @return boolean The boolean representation of the string value
    * @param value java.lang.String The string to convert to boolean
    */
    //---------------------------------------------------------------------
    public boolean valueOf(String value) throws IllegalArgumentException
    {
        if (value.equals(getTrueValue()))
        {
            return true;
        }

        if (!value.equals(getFalseValue()))
        {
            throw new IllegalArgumentException("Unexpected value in BooleanComboModel");
        }

        return false;
    }

    //---------------------------------------------------------------------
    /**
    * This method returns the corresponding string for the boolean. <P>
    * @return java.lang.String Returns the corresponding string for the boolean
    * @param value boolean The boolean to translate into a striing
    */
    //---------------------------------------------------------------------
    public String valueOf(boolean value)
    {
        
        if (value)
        {
            return getTrueValue();
        }

        return getFalseValue();
    }
}
