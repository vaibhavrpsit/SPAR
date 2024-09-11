/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/StringParameterBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:51 mszekely Exp $
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
 *   3    360Commerce 1.2         3/31/2005 4:30:14 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:25:38 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:14:33 PM  Robert Pearse   
 *
 *  Revision 1.4  2004/07/20 20:29:24  cdb
 *  @scr 6127 Updated behavior of StringLengthValidator.
 *
 *  Revision 1.3  2004/07/20 18:41:52  cdb
 *  @scr 6127 Updated to use validation in validator rather than aisles.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.foundation.manager.ifc.parameter.ParameterValidatorIfc;
import oracle.retail.stores.foundation.manager.parameter.StringLengthValidator;
import oracle.retail.stores.foundation.manager.parameter.Parameter;

//import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
    This class packages a retail parameter and its fields that the
    user may change.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class StringParameterBeanModel extends RetailParameter
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    protected String newValue = null;

    //---------------------------------------------------------------------
    /**
        Class Constructor
    **/
    //---------------------------------------------------------------------
    public StringParameterBeanModel()
    {
        setValue("parameterMinField", "-1");
        setValue("parameterMaxField", "-1");
    }

    //---------------------------------------------------------------------
    /**
     Class constructor uses the validation restrictions from the
     provided parameter. <p>
     @param param the Parameter and its associated validator
     **/
    //---------------------------------------------------------------------
    public StringParameterBeanModel(Parameter param)
    {
        this();

        ParameterValidatorIfc validator = param.getValidator();

        if (validator instanceof StringLengthValidator)
        {
            StringLengthValidator stringValidator = (StringLengthValidator)validator;
            setValue("parameterMinField", Integer.toString(stringValidator.getMinimum()));
            setValue("parameterMaxField", Integer.toString(stringValidator.getMaximum()));
        }
    }
    
    public String getNewValue()
    {
        return newValue;
    }

    public void setNewValue(String value)
    {
        newValue = value;
    }

    public int getMinValue()
    {
        return getValueAsInt("parameterMinField");
    }

    public int getMaxValue()
    {
        return getValueAsInt("parameterMaxField");
    }

    public void setMinValue(String value)
    {
        setValue("parameterMinField", value);
    }

    public void setMaxValue(String value)
    {
        setValue("parameterMaxField", value);
    }

    //-------------------------------------------------------------------------
    /**
        Returns a string representation of this object. <p>
        @returns string representing the data in this Object
    **/
    //-------------------------------------------------------------------------
    public String toString()
    {
        StringBuffer buff = new StringBuffer();

        buff.append("Class: " + getClass().getName() + " Revision: " +
                    revisionNumber + "\n");
        buff.append("Name [" + parameterName + "]\n");
        buff.append("Group [" + parameterGroup + "]\n");
        buff.append("Value [" + value + "]\n");
        buff.append("New Value [" + newValue + "]\n");
        buff.append("Modifiable [" + getModifiable() + "]\n");

        return(buff.toString());
    }
}
