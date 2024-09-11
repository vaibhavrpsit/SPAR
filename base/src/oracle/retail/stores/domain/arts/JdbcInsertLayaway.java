/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcInsertLayaway.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:03 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    mchellap  11/27/08 - Calling insertLayawayStatus to save the transaction
 *                         status
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:11:08 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:37 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:38 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:55 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:28:11    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:37     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:38     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:55     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:37  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:47  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:14  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:24  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:30:48   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:36:26   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:46:44   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:06:52   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:58:54   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:40   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This operation inserts into the layaway table from the LayawayIfc object.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 * @see oracle.retail.stores.domain.financial.LayawayIfc
 * @see oracle.retail.stores.domain.arts.JdbcSaveLayaway
 */
public class JdbcInsertLayaway extends JdbcSaveLayaway implements ARTSDatabaseIfc
{
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcInsertLayaway.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Class constructor.
     */
    public JdbcInsertLayaway()
    {
        setName("JdbcInsertLayaway");
    }

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcInsertLayaway.execute()");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // Navigate the input object to obtain values that will be inserted
        // into the database.
        LayawayIfc item = (LayawayIfc) action.getDataObject();
        insertLayaway(connection, item);
        // Insert the layaway transaction status
        insertLayawayStatus(connection, item);

        if (logger.isDebugEnabled()) logger.debug( "JdbcInsertLayaway.execute()");
    }

    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return(revisionNumber);
    }

    /**
       Returns the string representation of this object.
       @return String representation of object
     */
    @Override
    public String toString()
    {
        return(Util.classToStringHeader("JdbcInsertLayaway",
                                        getRevisionNumber(),
                                        hashCode()).toString());
    }
}

