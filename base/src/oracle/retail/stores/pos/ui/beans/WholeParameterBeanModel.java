/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/WholeParameterBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:44 mszekely Exp $
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

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.parameter.ParameterValidatorIfc;
import oracle.retail.stores.foundation.manager.parameter.IntegerRangeValidator;
import oracle.retail.stores.foundation.manager.parameter.Parameter;

//----------------------------------------------------------------------------
/**
    This class packages a retail parameter and its fields that the
    user may change.
    @author  $KW=@(#); $Own=Builder; $EKW;
    @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
**/
//----------------------------------------------------------------------------
public class WholeParameterBeanModel extends RetailParameter
{
    /** revision number **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";

    protected long minValue = 0;
    protected long maxValue = 999;
    protected long newValue = -1;

    //---------------------------------------------------------------------
    /**
        Default constructor
    **/
    //---------------------------------------------------------------------
    public WholeParameterBeanModel()
    {
    }

    //---------------------------------------------------------------------
    /**
        Class constructor that uses the validation restrictions from the
        provided parameter. <p>
        @param param the Parameter and its associated validator
    **/
    //---------------------------------------------------------------------
    public WholeParameterBeanModel(Parameter param)
    {
        ParameterValidatorIfc validator = param.getValidator();

        if (validator instanceof IntegerRangeValidator)
        {
            IntegerRangeValidator intidator = (IntegerRangeValidator)validator;
            minValue = intidator.getMin();
            maxValue = intidator.getMax();
        }
    }

    public long getMinValue()
    {
        return minValue;
    }

    public long getMaxValue()
    {
        return maxValue;
    }

    public long getNewValue()
    {
        return newValue;
    }

    public void setMinValue(long value)
    {
        minValue = value;
    }

    public void setMaxValue(long value)
    {
        maxValue = value;
    }

    public void setNewValue(long value)
    {
        newValue = value;
    }

    //-------------------------------------------------------------------------
    /**
        Returns a string representing the data in this Object. <p>
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
