/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/RetailParameter.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:43 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   10/22/10 - update to use java.lang.Comparable
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *   4    360Commerce 1.3         4/1/2008 9:18:26 AM    Anil Rathore
 *        Updated to ignore the case when comparing Reasons and Parameters
 *        name.
 *   3    360Commerce 1.2         3/31/2005 4:29:41 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:24:46 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:13:46 PM  Robert Pearse
 *
 *  Revision 1.5  2004/07/20 18:41:52  cdb
 *  @scr 6127 Updated to use validation in validator rather than aisles.
 *
 *  Revision 1.4  2004/04/08 22:14:54  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *  Revision 1.3  2004/03/16 17:15:18  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:27  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Oct 01 2003 13:47:40   lzhao
 * They were sorted by parameter names defined in application.xml rather than the parameter name value in parameterText properties file.
 * Resolution for 3094: List of parameters not in alphabetical order in Tender parameter group
 *
 *    Rev 1.0   Aug 29 2003 16:11:52   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jun 21 2002 18:26:50   baa
 * externalize parameter names,
 * start formatting currency base on locale
 * Resolution for POS SCR-1624: Localization Support
//this will need to move into the domain
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.Vector;

/**
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class RetailParameter extends DataInputBeanModel implements Comparable<Object>
{
    private static final long serialVersionUID = -2924938138012583628L;

    /** revision number */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** The value displayed for a modifiable parameter. */
    public static final String MODIFIABLE = "Yes";

    /** The value displayed for a nonmodifiable parameter. */
    public static final String NONMODIFIABLE = "No";

    /** The value displayed on the parameter list. */
    protected String parameterNameContent = "";

    protected String parameterName = "";

    protected String parameterLevel = ParametersCommon.STORE;

    protected String parameterGroup = "";

    protected String value = "";

    /**
     * The choices from which the user can pick for modifiability - Yes or No.
     */
    protected static Vector<String> modifiableChoices = new Vector<String>(2);

    /** The selected modifiable value. */
    protected String modifiableValue = MODIFIABLE;

    /**
     * Flag indicates whether the original fields of the parameter have been
     * modified.
     */
    private boolean modified = false;

    static
    {
        modifiableChoices.addElement(MODIFIABLE);
        modifiableChoices.addElement(NONMODIFIABLE);
    }

    public RetailParameter()
    {
        setSelectionChoices("modifiableChoiceList", modifiableChoices);
    }

    /**
     * Returns the value of the ParameterName field.
     * 
     * @return the value of ParameterName
     */
    public String getParameterName()
    {
        return getValueAsString("parameterNameField");
    }

    /**
     * Returns the value of the ParameterGroup field.
     * 
     * @return the value of ParameterGroup
     */
    public String getParameterGroup()
    {
        return getValueAsString("parameterGroupField");
    }

    /**
     * Returns the value of the ParameterNameContent field.
     * 
     * @return the value of ParameterNameContent
     */
    public String getParameterNameContent()
    {
        return parameterNameContent;
    }

    /**
     * Returns the value of the ParameterLevel field.
     * 
     * @return the value of ParameterLevel
     */
    public String getParameterLevel()
    {
        return parameterLevel;
    }

    /**
     * Returns whether this parameter is modifiable.
     *
     * @return true if modifiable; false otherwise
     */
    public boolean getModifiable()
    {
        return (MODIFIABLE.equals(getModifiableValue()));
    }

    public boolean getModified()
    {
        return modified;
    }

    /**
     * Returns the value of the selection.
     * 
     * @return the value of the selection
     */
    public String getModifiableValue()
    {
        return (String) getSelectionValue("modifiableChoiceList");
    }

    /**
     * Returns the modifiableChoices.
     * 
     * @return the modifiableChoices
     */
    public Vector<String> getModifiableChoices()
    {
        return modifiableChoices;
    }

    /**
     * Returns the value of the Value field.
     * 
     * @return the value of Value
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Sets the ParameterName field.
     * 
     * @param parameterName the value to be set for the parameter name
     */
    public void setParameterName(String parameterName)
    {
        setValue("parameterNameField", parameterName);
    }

    /**
     * Sets the ParameterGroup field.
     *
     * @param parameterGroup the value to be set for parameter group
     */
    public void setParameterGroup(String parameterGroup)
    {
        setValue("parameterGroupField", parameterGroup);
    }

    /**
     * Sets the parameterNameContent field.
     *
     * @param the value to be set for parameterNameContent
     */
    public void setParameterNameContent(String parameterNameContent)
    {
        this.parameterNameContent = parameterNameContent;
    }

    /**
     * Sets the ParameterLevel field.
     *
     * @param parameterLevel the value to be set for parameter level
     */
    public void setParameterLevel(String parameterLevel)
    {
        this.parameterLevel = parameterLevel;
    }

    public void setModified(boolean mod)
    {
        modified = mod;
    }

    /**
     * Sets whether this parameter is modifiable.
     *
     * @param modifiable true if the parameter should be modifiable, false
     *            otherwise.
     */
    public void setModifiable(boolean modifiable)
    {
        String value = MODIFIABLE;

        if (!modifiable)
        {
            value = NONMODIFIABLE;
        }
        setSelectionValue("modifiableChoiceList", value);
        modified = true;
    }

    /**
     * Sets whether this parameter is modifiable based on the String returned
     * from the UI.
     *
     * @param modifiable MODIFIABLE is this parameter should be modifiable,
     *            NONMODIFIABLE otherwise
     */
    public void setModifiableValue(String modifiable)
    {
        setSelectionValue("modifiableChoiceList", value);
        modified = true;
    }

    /**
     * Sets the Value field.
     * 
     * @param value the value to be set for Value
     */
    public void setValue(String value)
    {
        this.value = value;
        modified = true;
    }

    /**
     * Compare current object with the object passed in.
     *
     * @param object the object to be compared with current object, the type of
     *            the object can be RetailParameter or ReasonCodeGroupBeanModel
     */
    public int compareTo(Object object)
    {
        int result = -1;

        if (object instanceof RetailParameter)
        {
            RetailParameter param = (RetailParameter) object;
            if (param.getParameterNameContent() != null)
            {
                result = parameterNameContent.compareToIgnoreCase(param.getParameterNameContent());
            }
        }
        else if (object instanceof ReasonCodeGroupBeanModel)
        {
            ReasonCodeGroupBeanModel param = (ReasonCodeGroupBeanModel) object;
            if (param.getParameterNameContent() != null)
            {
                result = parameterNameContent.compareToIgnoreCase(param.getParameterNameContent());
            }
        }
        return result;
    }

    /**
     * Converts to a string representing the data in this Object.
     *
     * @returns string representing the data in this Object
     */
    public String toString()
    {
        StringBuffer buff = new StringBuffer();

        buff.append("Class: " + getClass().getName() + " Revision: " + revisionNumber + "\n");
        buff.append("Name [" + parameterName + "]\n");
        buff.append("Group [" + parameterGroup + "]\n");
        buff.append("Level [" + parameterLevel + "]\n");
        buff.append("Value [" + value + "]\n");
        buff.append("ModifiableValue [" + getModifiableValue() + "]\n");

        return (buff.toString());
    }
}
