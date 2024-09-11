/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcFFDataOperation.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:06 mszekely Exp $
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
 *   4    360Commerce 1.3         7/3/2007 7:10:13 AM    Manikandan Chellapan
 *        CR#27329 BuildFF cleanup bulk check in
 *   3    360Commerce 1.2         3/31/2005 4:28:37 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:22:38 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:11:54 PM  Robert Pearse   
 *
 *  Revision 1.5  2004/09/23 00:30:50  kmcbride
 *  @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *  Revision 1.4  2004/04/01 00:11:34  cdb
 *  @scr 4206 Corrected some header foul ups caused by Eclipse auto formatting.
 *
 *
 * Revision 1.2 2004/02/12 17:13:13 mcs Forcing
 * head revision
 * 
 * Revision 1.1.1.1 2004/02/11 01:04:26 cschellenger updating to pvcs
 * 360store-current
 * 
 * 
 * 
 * Rev 1.1 Nov 11 2003 16:33:06 dcobb Added properties to configure the name
 * and location of files. Resolution for 3362: New Feature: 4690 (V3R3) OS
 * Support
 * 
 * Rev 1.0 Aug 29 2003 15:30:44 CSchellenger Initial revision.
 * 
 * Rev 1.0 Jun 03 2002 16:36:18 msg Initial revision.
 * 
 * Rev 1.0 Mar 18 2002 12:05:30 msg Initial revision.
 * 
 * Rev 1.0 Feb 25 2002 17:12:16 mpm Initial revision. Resolution for Domain
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;
// foundation imports
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.ifc.data.DataOperationIfc;
import oracle.retail.stores.foundation.utility.FilePath;
import oracle.retail.stores.foundation.utility.Util;

//-------------------------------------------------------------------------
/**
 * This class provides methods commonly used by data operations.
 * <P>
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 * @deprecated as of release 12.0.0
 */
//-------------------------------------------------------------------------
public abstract class JdbcFFDataOperation
    extends JdbcDataOperation
    implements DataOperationIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -8126572166466984455L;

    public static final String APPLICATION_PROPERTY_GROUP_NAME = "application";
    public static final String FF_PATH_NAME = "FlatFilePath";

    /**
     * path to flat files
     */
    protected String filePath = null;

    //---------------------------------------------------------------------
    /**
     * Class constructor.
     * <P>
     */
    //---------------------------------------------------------------------
    public JdbcFFDataOperation()
    {
    }

    //---------------------------------------------------------------------
    /**
     * Sets attribute for path to flat files.
     * <P>
     * 
     * @param value
     *          path to flat file
     */
    //---------------------------------------------------------------------
    public void setFilePath(String value)
    { // begin setFilePath()
        filePath = value;
    } // end setFilePath()

    //---------------------------------------------------------------------
    /**
     * Returns attribute for path to flat files.
     * <P>
     * 
     * @return path to flat file
     */
    //---------------------------------------------------------------------
    public String getFilePath()
    { // begin getFilePath()
        if (Util.isEmpty(filePath))
        {
            filePath =
                FilePath.getPathFromProperties(
                    APPLICATION_PROPERTY_GROUP_NAME,
                    FF_PATH_NAME,
                    null);
        }
        return (filePath);
    } // end getFilePath()

    //---------------------------------------------------------------------
    /**
     * Gets full file name for data file.
     * <P>
     * 
     * @param value
     *          file name without path
     * @return full file name (with path) for data file
     */
    //---------------------------------------------------------------------
    protected String getFullFileName(String value)
    { // begin getFullFileName()
        String returnString = value;
        if (!Util.isEmpty(getFilePath()))
        {
            StringBuffer buf = new StringBuffer(getFilePath());
            buf.append(System.getProperty("file.separator")).append(value);
            returnString = buf.toString();
        }
        return (returnString);
    } // end getFullFileName()

    /**
     * Turns an EYSDate representation into YYYY-MM-DD format
     * 
     * @param date the particular date. Can be null
     * @return The YYYY-MM-DD format for the EYSDate or the empty string ("")
     *         if <code>date</code> is <code>null</code>
     */
    public String makeDateString(EYSDate date)
    {
        StringBuffer dateString = new StringBuffer("");

        if (date != null)
        {
            dateString
                .append(date.getYear())
                .append("-")
                .append(date.getMonth())
                .append("-")
                .append(date.getDay());
        }

        return dateString.toString();
    }
}
