/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DecimalParameterBeanModel.java /main/13 2011/12/05 12:16:31 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   4    360Commerce 1.3         1/22/2006 11:45:23 AM  Ron W. Haight
 *        removed references to com.ibm.math.BigDecimal
 *   3    360Commerce 1.2         3/31/2005 4:27:42 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:20:52 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:31 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 16:10:06   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jun 21 2002 18:26:32   baa
 * externalize parameter names,
 * start formatting currency base on locale
 * Resolution for POS SCR-1624: Localization Support
 *
 *    Rev 1.0   Apr 29 2002 14:53:12   msg
 * Initial revision.
 *
 *    Rev 1.1   25 Apr 2002 18:52:18   pdd
 * Removed unnecessary BigDecimal instantiations.
 * Resolution for POS SCR-1610: Remove inefficient instantiations of BigDecimal
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import oracle.retail.stores.foundation.manager.ifc.parameter.ParameterValidatorIfc;
import oracle.retail.stores.foundation.manager.parameter.FloatRangeValidator;
import oracle.retail.stores.foundation.manager.parameter.Parameter;
import oracle.retail.stores.foundation.utility.Util;
import java.math.BigDecimal;

//----------------------------------------------------------------------------
/**
    This class packages a decimal-valued retail parameter and its fields that a
    user may change for decimal valued parameters.
**/
//----------------------------------------------------------------------------
public class DecimalParameterBeanModel extends RetailParameter
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/13 $";

    //---------------------------------------------------------------------
    /**
        Class constructor
    **/
    //---------------------------------------------------------------------
    public DecimalParameterBeanModel()
    {
        setValue("parameterMinField", BigDecimal.ZERO);
        setValue("parameterMaxField", new BigDecimal(99999.99));
        setValue("editValueField", new BigDecimal(-1.0));
    }

    //---------------------------------------------------------------------
    /**
        Class constructor uses the validation restrictions from the
        provided parameter. <p>
        @param param the Parameter and its associated validator
    **/
    //---------------------------------------------------------------------
    public DecimalParameterBeanModel(Parameter param)
    {
        this();

        ParameterValidatorIfc validator = param.getValidator();

        if (validator instanceof FloatRangeValidator)
        {
            FloatRangeValidator floatidator = (FloatRangeValidator)validator;
            setValue("parameterMinField", new BigDecimal(floatidator.getDoubleMin()));
            setValue("parameterMaxField", new BigDecimal(floatidator.getDoubleMax()));
        }
    }

    public BigDecimal getMinValue()
    {
        return getValueAsDecimal("parameterMinField");
    }

    public BigDecimal getMaxValue()
    {
        return getValueAsDecimal("parameterMaxField");
    }

    public BigDecimal getNewValue()
    {
        return getValueAsDecimal("editValueField");
    }

    public void setMinValue(BigDecimal value)
    {
        setValue("parameterMinField", value);
    }

    public void setMaxValue(BigDecimal value)
    {
        setValue("parameterMaxField", value);
    }

    public void setNewValue(BigDecimal value)
    {
        setValue("editValueField", value);
    }

    //-------------------------------------------------------------------------
    /**
        Converts to a string representing the data in this Object. <p>
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
        buff.append("New Value [" + getNewValue() + "]\n");
        buff.append("ModifiableValue [" + getModifiableValue() + "]\n");

        return(buff.toString());
    }
}
