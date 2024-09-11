/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ISODateParameterBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:53 mszekely Exp $
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
 *   3    360Commerce 1.2         3/31/2005 4:28:28 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:22:19 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:11:33 PM  Robert Pearse   
 *
 *  Revision 1.1  2004/03/19 21:02:56  mweis
 *  @scr 4113 Enable ISO_DATE datetype
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.domain.utility.EYSDate;

//----------------------------------------------------------------------------
/**
    This class packages a retail parameter and its fields that the
    user may change.
**/
//----------------------------------------------------------------------------
public class ISODateParameterBeanModel extends RetailParameter
{
    /** The revision number. **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** The original value. **/
    protected EYSDate oldValue = null;
    /** The value being proposed to replace the original value. **/
    protected EYSDate newValue = null;
    
    //---------------------------------------------------------------------
    /**
        Class constructor.
    **/
    //---------------------------------------------------------------------
    public ISODateParameterBeanModel()
    {
    }

    /**
     * Returns the ISO date as 'YYYY-MM-DD'.
     * Overrides the parent method.
     * @return The ISO date as 'YYYY-MM-DD'.
     */
    public String getValue()
    {
        return oldValue.asISODate();
    }

    /**
     * Sets the original value.
     * Overrides the parent method to capture this value as an EYSDate.
     * @param value The original value's new value, in the form of 'YYYY-MM-DD'.
     */
    public void setValue(String value)
    {
        // Note: 'value' is expected to be 'YYYY-MM-DD'.
        setOldValue(EYSDate.getEYSDateFromISO(value));
        super.setValue(value);        
    }

    /**
     * Returns the original "old" value.
     * @return The original "old" value.
     */
    public EYSDate getOldValue()
    {
        return oldValue;
    }
    
    /**
     * Sets the original "old" value.  <p>
     * Note: Typically one should call {@link #setValue(String)}
     *       instead of this method.
     * @param value The original's new value.
     */
    public void setOldValue(EYSDate value)
    {
        oldValue = value;
    }

    /**
     * Returns the newly proposed value.
     * @return The newly proposed value.
     */
    public EYSDate getNewValue()
    {
        return newValue;
    }

    /**
     * Sets the newly proposed value.
     * @param value The new value.
     */
    public void setNewValue(EYSDate value)
    {
        newValue = value;
    }

    //-------------------------------------------------------------------------
    /**
        Returns a string representation of this object. <p>
        @return String representing the data in this Object
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
