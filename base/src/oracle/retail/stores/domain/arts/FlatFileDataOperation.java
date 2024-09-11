/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/FlatFileDataOperation.java /main/11 2011/12/05 12:16:32 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  5    360Commerce 1.4         7/3/2007 7:10:13 AM    Manikandan Chellapan
 *       CR#27329 BuildFF cleanup bulk check in
 *  4    360Commerce 1.3         1/22/2006 11:41:14 AM  Ron W. Haight   Removed
 *        references to com.ibm.math.BigDecimal
 *  3    360Commerce 1.2         3/31/2005 4:28:12 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:21:43 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:11:07 PM  Robert Pearse   
 * $
 * Revision 1.5  2004/09/23 00:30:50  kmcbride
 * @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 * Revision 1.4  2004/02/19 23:36:46  jriggins
 * @scr 3782 this commit mainly deals with the database modifications needed for Enter New Password feature in Operator ID
 * Revision 1.3 2004/02/12 17:13:13 mcs
 * Forcing head revision
 * 
 * Revision 1.2 2004/02/11 23:25:23 bwf @scr 0 Organize imports.
 * 
 * Revision 1.1.1.1 2004/02/11 01:04:26 cschellenger updating to pvcs
 * 360store-current
 * 
 * 
 * 
 * Rev 1.0 Aug 29 2003 15:30:08 CSchellenger Initial revision.
 * 
 * Rev 1.0 Jun 03 2002 16:34:52 msg Initial revision.
 * 
 * Rev 1.3 25 Apr 2002 10:27:40 pdd Removed unnecessary BigDecimal
 * instantiations. Resolution for POS SCR-1610: Remove inefficient
 * instantiations of BigDecimal
 * 
 * Rev 1.2 Apr 02 2002 18:58:22 mpm Corrected instantiation, cloning of
 * BigDecimal. Resolution for Domain SCR-46: Correct initialization of
 * BigDecimal objects
 * 
 * Rev 1.1 Mar 18 2002 22:45:14 msg - updated copyright
 * 
 * Rev 1.0 Mar 18 2002 12:05:04 msg Initial revision.
 * 
 * Rev 1.1 Feb 05 2002 16:33:24 mpm Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 * 
 * Rev 1.0 Sep 20 2001 15:58:44 msg Initial revision.
 * 
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

// java imports
import java.text.ParseException;

import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.flatfile.FlatFileException;
import oracle.retail.stores.foundation.manager.ifc.data.DataOperationIfc;
import oracle.retail.stores.foundation.utility.Util;
import java.math.BigDecimal;

//-------------------------------------------------------------------------
/**
 * This class provides methods commonly used by data operations.
 * <P>
 * 
 * @version $Revision: /main/11 $
 * @deprecated as of release 12.0.0, flatfile operations were replaced by offline
 * database jdbc operations.
 */
//-------------------------------------------------------------------------
public abstract class FlatFileDataOperation
    implements DataOperationIfc, FlatFileDataIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 3750054467219187619L;

    /** The DataOperation name * */
    protected String name = null;

    //---------------------------------------------------------------------
    /**
     * Class constructor.
     * <P>
     */
    //---------------------------------------------------------------------
    public FlatFileDataOperation()
    {
    }

    //---------------------------------------------------------------------
    /**
     * Set all data members should be set to their initial state.
     * <P>
     * 
     * @exception DataException
     */
    //---------------------------------------------------------------------
    public void initialize() throws DataException
    { // begin initialize()
        // do nothing
    } // end initialize()

    //---------------------------------------------------------------------
    /**
     * Set the name.
     * <P>
     * 
     * @param name
     *          The name to assign the operation.
     */
    //---------------------------------------------------------------------
    public void setName(String name)
    {
        this.name = name;
    }

    //---------------------------------------------------------------------
    /**
     * Return the name of the operation.
     * <P>
     */
    //---------------------------------------------------------------------
    public String getName()
    {
        return name;
    }

    public DataException translateToDataException(FlatFileException eff)
    {

        DataException edata = new DataException(1);

        return edata;
    }

    //----------------------------------------------------------------------
    /**
     * Converts a boolean to a "t" or "f".
     * <p>
     * 
     * @return "t" if the boolean is true; "f" otherwise.
     */
    //----------------------------------------------------------------------
    protected String getBooleanString(boolean value)
    {
        String rc = "f";

        if (value)
        {
            rc = "t";
        }

        return (rc);
    }

    //----------------------------------------------------------------------
    /**
     * Returns the appropriate boolean value for the given string.
     * <p>
     * 
     * @return the appropriate boolean value for the given string.
     */
    //----------------------------------------------------------------------
    protected boolean getBoolean(String value)
    {
        boolean rc = true;

        if (value.equalsIgnoreCase("f"))
        {
            rc = false;
        }

        return (rc);
    }

    //----------------------------------------------------------------------
    /**
     * Returns the appropriate integer value for the given string.
     * <p>
     * 
     * @return the appropriate integer value for the given string.
     */
    //----------------------------------------------------------------------
    protected int getInteger(String value)
    {
        int rc = 0;

        try
        {
            rc = Integer.valueOf(value).intValue();
        }
        catch (java.lang.NumberFormatException nfe)
        {
            // the default value
        }

        return (rc);
    }

    //----------------------------------------------------------------------
    /**
     * Returns the appropriate BigDecimal value for the given string.
     * <p>
     * 
     * @return the appropriate BigDecimal value for the given string.
     */
    //----------------------------------------------------------------------
    protected BigDecimal getBigDecimal(String value)
    {
        BigDecimal rc;

        try
        {
            rc = new BigDecimal(value);
        }
        catch (java.lang.NumberFormatException nfe)
        {
            rc = BigDecimal.ZERO;
        }

        return (rc);
    }

    /**
     * Creates an EYSDate instance from a string given in "YYYY-MM-DD" format
     * 
     * @param date
     *          string in "YYYY-MM-DD" format
     * @return EYSDate matching the format
     * @throws ParseException
     *           if unable to parse the date in "YYYY-MM-DD" format
     */
    protected EYSDate getEYSDateFromString(String date) throws ParseException
    {
        EYSDate dateVal = null;

        try
        {
            if (date != null && date.trim().length() > 0)
            {
                String[] ymd = date.split("-");
                dateVal =
                    new EYSDate(
                        Integer.parseInt(ymd[0]),
                        Integer.parseInt(ymd[1]),
                        Integer.parseInt(ymd[2]));
            }
        }
        catch (Throwable t)
        {
            throw new ParseException(
                "Unable to parse date: "
                    + date
                    + ". Expecting format 'YYYY-MM-DD'.",
                0);
        }

        return dateVal;
    }

    /**
     * Returns boolean from flat file, where boolean data is stored as a string (1
     * or 0 strings as true or false, respectively).
     * 
     * This method has been created even though
     * FlatFileDataOperation.getBooleanString() exists because this one looks
     * for "0" or "1" (like <code>oracle.retail.stores.domain.arts.JdbcUtilities</code>
     * does) instead of "t" or "f".
     * 
     * @param flagString
     *          string to interpret
     * @return boolean <code>true</code> if flagString equals "1" and <code>false</code>
     *         otherwise
     */
    public boolean getBooleanFromString(String flagString)
    {
        boolean rc = false;

        // convert flag to boolean
        if (flagString.equals("1"))
        {
            rc = true;
        }

        return rc;
    }

} // End FlatFileOperation
