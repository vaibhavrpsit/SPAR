/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ListFromListParameterBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:44 mszekely Exp $
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
 *    4    360Commerce 1.3         12/13/2005 4:42:45 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:28:52 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:07 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:20 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:11:08   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:48:38   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:56:04   msg
 * Initial revision.
 * 
 *    Rev 1.2   08 Feb 2002 17:34:58   KAC
 * Now has oldValues for use in canceling deletions.
 * Added getPotentialDeletes()
 * Resolution for POS SCR-1176: Update "list from list" parameter editing
 * 
 *    Rev 1.1   01 Feb 2002 07:44:52   KAC
 * Fixed problem where inherited parameters weren't being 
 * saved, because the modified flag hadn't been set.
 * Resolution for POS SCR-672: Create List Parameter Editor
 * 
 *    Rev 1.0   30 Jan 2002 10:12:20   KAC
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import oracle.retail.stores.foundation.utility.Util;

//------------------------------------------------------------------------------
/**
    This class packages a retail parameter and its fields that the
    user may change.
    @author  $KW=@(#); $Own=Builder; $EKW;
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class ListFromListParameterBeanModel
extends RetailParameter //DiscreteParameterBeanModel
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** The proposed new values which may or may not change based on
        the user's deletion confirmation. **/
    protected Serializable[] newValues = null;

    /** The values that existed before the user did an add or delete
        and confirmation. **/
    protected Serializable[] oldValues = null;

    /** The new values. **/
    protected Serializable[] potentialValues = null;


    //--------------------------------------------------------------------------
    /**
        Class Constructor
    **/
    //--------------------------------------------------------------------------

    public ListFromListParameterBeanModel()
    {
    }


    //--------------------------------------------------------------------------
    /**
        Returns the new values. <p>
        @return the new values
    **/
    //--------------------------------------------------------------------------

    public Serializable[] getNewValues()
    {
        return newValues;
    }


    //--------------------------------------------------------------------------
    /**
        Returns the old values. <p>
        @return the old values
    **/
    //--------------------------------------------------------------------------

    public Serializable[] getOldValues()
    {
        return oldValues;
    }


    //--------------------------------------------------------------------------
    /**
        Returns the potential values. <p>
        @return the potential values
    **/
    //--------------------------------------------------------------------------

    public Serializable[] getPotentialValues()
    {
        return potentialValues;
    }


    //--------------------------------------------------------------------------
    /**
        Sets the new value.
        @param value the new value.
    **/
    //--------------------------------------------------------------------------

    public void setNewValues(Serializable[] value)
    {
        // If the new value is different than the old, record it
        if (!Util.isObjectEqual(value, newValues))
        {
            newValues = value;
            setModified(true);
        }
    }


    //--------------------------------------------------------------------------
    /**
        Sets the old value.
        @param value the old value.
    **/
    //--------------------------------------------------------------------------

    public void setOldValues(Serializable[] value)
    {
        oldValues = value;
    }


    //--------------------------------------------------------------------------
    /**
        Sets the potential value.
        @param value the potential value.
    **/
    //--------------------------------------------------------------------------

    public void setPotentialValues(Serializable[] value)
    {
        potentialValues = value;
    }


    //--------------------------------------------------------------------------
    /**
        Return the string representation of the parameter values to be deleted
        @return the string representation of the parameter values to be deleted
    **/
    //--------------------------------------------------------------------------

    public String getPotentialDeletes()
    {
        StringBuffer buf = new StringBuffer();
        ArrayList oldValueList = new ArrayList(11);
        ArrayList newValueList = new ArrayList(11);
        if (oldValues != null && oldValues.length > 0)
        {            
            oldValueList.addAll(Arrays.asList(oldValues));
        }
        if (newValues != null && newValues.length > 0)
        {
            
            newValueList.addAll(Arrays.asList(newValues));
        }
        // Find the values that will be deleted
        for (int i = 0; i < oldValues.length; i++)
        {
            if (oldValueList != null && newValueList != null)
            {
                Object oldValueElement = oldValueList.get(i);
                if (!newValueList.contains(oldValueElement))
                {
                    
                    // Put commas between multiple elements
                    if (buf.length() > 0)
                    {
                        buf.append(", ");
                    }
                    Object deletingOldValueElement = oldValueList.get(oldValueList.indexOf(oldValueElement));
                    buf.append(deletingOldValueElement.toString());
                }
            }
        }       // for
        return buf.toString();
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
