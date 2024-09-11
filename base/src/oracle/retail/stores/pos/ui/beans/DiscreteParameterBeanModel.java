/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DiscreteParameterBeanModel.java /rgbustores_13.4x_generic_branch/2 2011/09/06 16:39:37 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/06/11 - simplified toString method
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:27:46 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:21:00 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:37 PM  Robert Pearse   
 *
 *  Revision 1.5  2004/04/27 17:24:31  cdb
 *  @scr 4166 Removed unintentional null pointer exception potential.
 *
 *  Revision 1.4  2004/03/16 17:15:22  build
 *  Forcing head revision
 *
 *  Revision 1.3  2004/03/16 17:15:17  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:26  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:10:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Sep 03 2002 16:05:02   baa
 * externalize domain  constants and parameter values
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Jun 21 2002 18:26:36   baa
 * externalize parameter names,
 * start formatting currency base on locale
 * Resolution for POS SCR-1624: Localization Support
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.lang.reflect.Field;
import java.util.Vector;

import oracle.retail.stores.foundation.manager.parameter.Parameter;
import oracle.retail.stores.foundation.tour.dtd.ParamSourceScriptIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This class packages a retail parameter and its fields that the user may
 * change.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
public class DiscreteParameterBeanModel extends RetailParameter
{
    private static final long serialVersionUID = 640717031273618404L;

    /** revision number **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";

    /** The choices from which the user can pick. **/
    protected Vector valueChoices = null;

    /** The new value. **/
    protected String newValue = null;

    /**
     * Class Constructor
     */
    public DiscreteParameterBeanModel()
    {
    }

    /**
     * Class constructor that builds itself based on the parameter provided.
     * 
     * @param param parameter object
     */
    public DiscreteParameterBeanModel(Parameter param)
    {
        String paramType = param.getType();
        // Set up special values for booleans
        if (ParamSourceScriptIfc.VAL_PRIM_TYPE_BOOLEAN.equals(paramType))
        {
            Vector<String> values = new Vector<String>(2);
            values.addElement("Y");
            values.addElement("N");
            setSelectionChoices("editValueField", values);
            setSelectionValue("editValueField", values.elementAt(0));
        }
    }

    /**
     * Returns the new value.
     * 
     * @return the new value
     */
    public String getNewValue()
    {
        return newValue;
    }

    /**
     * Returns the value choices
     * 
     * @return the value choices
     */
    public Vector getValueChoices()
    {
        return valueChoices;
    }

    /**
     * Sets the new value.
     * 
     * @param value the new value.
     */
    public void setNewValue(String value)
    {
        newValue = value;
    }

    /**
     * Sets the valueChoices.
     * 
     * @param valueChoices the Vector of Strings from which the user can choose
     */
    public void setValueChoices(Vector valueChoices)
    {
        this.valueChoices = valueChoices;

        // By default, select the first item
        if (newValue == null || !valueChoices.contains(newValue))
        {
            setNewValue((String)valueChoices.firstElement());
        }
    }

    /**
     * Returns the revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * Returns the string representation of the object.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        // result string
        StringBuilder strResult = new StringBuilder("Class:  " + getClass().getName() + "(Revision " + getRevisionNumber() + ")@" + hashCode());
        strResult.append("[valueChoices=");
        strResult.append(valueChoices);
        strResult.append(",newValue=");
        strResult.append(newValue);
        strResult.append("]");
        return strResult.toString();
    }
}
