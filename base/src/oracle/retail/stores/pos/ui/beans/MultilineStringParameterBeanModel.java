/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/MultilineStringParameterBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:43 mszekely Exp $
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
 *    4    360Commerce 1.3         8/10/2006 2:40:22 PM   Brett J. Larsen CR
 *         10543 - adding support for min/max length limits for multiline
 *         string parameters (e.g. gift footer)
 *
 *         v7x -> 360commerce merge
 *    3    360Commerce 1.2         3/31/2005 4:29:06 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:38 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:43 PM  Robert Pearse   
 *
 *
 *    4    .v7x      1.2.1.0     6/30/2006 2:31:18 PM   Michael Wisbauer Added
 *         String validator to multi-sting bean and xml files
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:11:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:52:08   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:56:12   msg
 * Initial revision.
 * 
 *    Rev 1.1   01 Feb 2002 07:44:52   KAC
 * Fixed problem where inherited parameters weren't being 
 * saved, because the modified flag hadn't been set.
 * Resolution for POS SCR-672: Create List Parameter Editor
 * 
 *    Rev 1.0   31 Jan 2002 13:43:22   KAC
 * Initial revision.
 * Resolution for POS SCR-672: Create List Parameter Editor
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import java.io.Serializable;
import java.lang.reflect.Field;

import oracle.retail.stores.foundation.manager.ifc.parameter.ParameterValidatorIfc;
import oracle.retail.stores.foundation.manager.parameter.Parameter;
import oracle.retail.stores.foundation.manager.parameter.StringLengthValidator;
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
    This class packages a retail parameter and its fields that the
    user may change.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------


public class MultilineStringParameterBeanModel
extends RetailParameter
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** The new values. **/
    protected Serializable[] allLines = null;


    //--------------------------------------------------------------------------
    /**
        Class Constructor
    **/
    //--------------------------------------------------------------------------
    public MultilineStringParameterBeanModel()
    {
        setValue("parameterMinField", "-1");
        setValue("parameterMaxField", "-1");
    	
    }

    //--------------------------------------------------------------------------
    /**
        Class Constructor
        @param param the parameter whose values we'll edit
    **/
    //--------------------------------------------------------------------------
    public MultilineStringParameterBeanModel(Parameter param)
    {
        this();

        ParameterValidatorIfc validator = param.getValidator();

        if (validator instanceof StringLengthValidator)
        {
            StringLengthValidator stringValidator = (StringLengthValidator)validator;
            setValue("parameterMinField", Integer.toString(stringValidator.getMinimum()));
            setValue("parameterMaxField", Integer.toString(stringValidator.getMaximum()));
        }        
        allLines = param.getValues();
    }

    //--------------------------------------------------------------------------
    /**
        Returns the new values. <p>
        @return the new values
    **/
    //--------------------------------------------------------------------------

    public Serializable[] getAllLines()
    {
        return allLines;
    }


    //--------------------------------------------------------------------------
    /**
        Sets the new value.
        @param value the new value.
    **/
    //--------------------------------------------------------------------------

    public void setAllLines(Serializable[] value)
    {
        // If the new value is different than the old, record it
        if (!Util.isObjectEqual(value, allLines))
        {
            allLines = value;
            setModified(true);
        }
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
    
    //--------------------------------------------------------------------------
    /**
        Returns the revision number. <P>
        @return String representation of revision number
    **/
    //--------------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

    //--------------------------------------------------------------------------
    /**
        Returns the string representation of the object. <P>
        @return String representation of object
    **/
    //--------------------------------------------------------------------------
    public String toString()
    {
        // verbose flag
        boolean bVerbose = true;
        // result string
        String strResult = "Class:  " + getClass().getName() +
            "(Revision " + getRevisionNumber() + ")@" + hashCode();
        // if verbose mode, do inspection gig
        if (bVerbose)
        {   // begin verbose mode
            // theClass will ascend through the inheritance hierarchy
            Class theClass = getClass();
            // fieldType contains the type of the field currently being examined
            Class fieldType = null;
            // fieldName contains the name of the field currently being examined
            String fieldName = "";
            // fieldValue contains value of the field currently being examined
            Object fieldValue = null;

            // Ascend through the class hierarchy, capturing field information
            while (theClass != null)
            {   // begin loop through fields
                // fields contains all noninherited field information
                Field[] fields = theClass.getDeclaredFields();

                // Go through each field, capturing information
                for (int i = 0; i < fields.length; i++)
                {
                    fieldType = fields[i].getType();
                    fieldName = fields[i].getName();

                    // get the field's value, if possible
                    try
                    {
                        fieldValue = fields[i].get(this);
                    }
                    // if the value can't be gotten, say so
                    catch (IllegalAccessException ex)
                    {
                        fieldValue = "*no access*";
                    }

                    // If it is a "simple" field, use the value
                    //if (Util.isSimpleClass(fieldType))
                    {
                        strResult += "\n\t" + fieldName +
                            ":\t" +  fieldValue;
                    }   // if simple
                }   // for each field
                theClass = theClass.getSuperclass();
            }   // end loop through fields
        }   // end verbose mode

        return(strResult);
    }
}
